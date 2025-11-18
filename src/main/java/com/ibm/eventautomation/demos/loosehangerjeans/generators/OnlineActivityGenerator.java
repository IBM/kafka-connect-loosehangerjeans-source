/**
 * Copyright 2025 IBM Corp. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.ibm.eventautomation.demos.loosehangerjeans.generators;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;

import org.apache.kafka.common.config.AbstractConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.eventautomation.demos.datafaker.LoosehangerFaker;
import com.ibm.eventautomation.demos.loosehangerjeans.DatagenSourceConfig;
import com.ibm.eventautomation.demos.loosehangerjeans.data.AbandonedOrder;
import com.ibm.eventautomation.demos.loosehangerjeans.data.Address;
import com.ibm.eventautomation.demos.loosehangerjeans.data.ClickEvent;
import com.ibm.eventautomation.demos.loosehangerjeans.data.ClickEvent.ClickEventType;
import com.ibm.eventautomation.demos.loosehangerjeans.data.Country;
import com.ibm.eventautomation.demos.loosehangerjeans.data.NewCustomer;
import com.ibm.eventautomation.demos.loosehangerjeans.data.OnlineActivityData;
import com.ibm.eventautomation.demos.loosehangerjeans.data.OnlineAddress;
import com.ibm.eventautomation.demos.loosehangerjeans.data.OnlineCustomer;
import com.ibm.eventautomation.demos.loosehangerjeans.data.OnlineOrder;
import com.ibm.eventautomation.demos.loosehangerjeans.data.UserContext;
import com.ibm.eventautomation.demos.loosehangerjeans.utils.Generators;

public class OnlineActivityGenerator {

    private static final Logger log = LoggerFactory.getLogger(OnlineActivityGenerator.class);

    // ------------------------------------------------------------
    // HELPER CLASSES - used to generate data
    // ------------------------------------------------------------
    private final LoosehangerFaker faker = new LoosehangerFaker(DEFAULT_LOCALE);
    private ProductGenerator productGenerator;
    private final Country COUNTRY = new Country(DEFAULT_LOCALE.getCountry(), DEFAULT_LOCALE.getDisplayCountry(DEFAULT_LOCALE));


    // ------------------------------------------------------------
    //  CONFIG - influences the type of online activity to generate
    // ------------------------------------------------------------

    /** formatter for event timestamps */
    private final DateTimeFormatter timestampFormatter;
    /** maximum number of user sessions to support */
    private final int maxSessions;
    /** Base URL to use for web addresses in click tracking events. */
    private final String baseUrl;
    /** Likelihood that a user will already be logged in at the start of a new session. */
    private final double alreadyLoggedInRate;
    /** Proportion of sessions that include a referral from a digital marketing campaign. */
    private final double marketingCampaignRate;
    /** Maximum number of click tracking events to generate for a single user session. */
    private final int maxEvents;
    /** Maximum number of products to include in the order. */
    private final int maxProducts;
    /** Minimum number of emails for the customer who makes the order. */
    private final int minEmails;
    /** Maximum number of emails for the customer who makes the order. */
    private final int maxEmails;
    /** Minimum number of phones in an address for the given order. */
    private final int minPhones;
    /** Maximum number of phones in an address for the given order. */
    private final int maxPhones;
    /** Likelihood that a user will abandon their cart at each step during a session. */
    private final double abandonmentRate;
    /**
     * Ratio of orders that use the same address as shipping and billing address.
     *
     * Between 0.0 and 1.0.
     *
     * Setting this to 0 will mean no events will use the same address as shipping and billing address.
     * Setting this to 1 will mean every event uses the same address as shipping and billing address.
     */
    private final double reuseAddressRatio;
    /** Custom list of cities to be used instead of faker generated */
    private final List<String> cities;
    /**
     * Ratio of orders that have at least one product that runs out-of-stock after the order has been placed.
     * Must be between 0.0 and 1.0.
     *
     * Setting this to 0 will mean that no out-of-stock event is generated.
     * Setting this to 1 will mean that one out-of-stock event will be generated for each new order.
     */
    private final double outOfStockRatio;

    // ------------------------------------------------------------
    // CURRENT STATE - helps to generate series of events over time
    // ------------------------------------------------------------
    /** Current active user sessions that could generate future events. */
    private final Map<String, SessionState> currentSessions = new HashMap<>();
    /** Recent new customers */
    private final ArrayBlockingQueue<OnlineCustomer> recentNewCustomers = new ArrayBlockingQueue<>(3);


    public OnlineActivityGenerator(AbstractConfig config) {
        this.productGenerator = new ProductGenerator(config);

        this.timestampFormatter = DateTimeFormatter.ofPattern(config.getString(DatagenSourceConfig.CONFIG_FORMATS_TIMESTAMPS_LTZ));
        this.maxSessions = config.getInt(DatagenSourceConfig.CONFIG_ONLINEORDERS_SESSIONS_MAX);
        this.maxProducts = config.getInt(DatagenSourceConfig.CONFIG_ONLINEORDERS_PRODUCTS_MAX);
        this.minEmails = config.getInt(DatagenSourceConfig.CONFIG_ONLINEORDERS_CUSTOMER_EMAILS_MIN);
        this.maxEmails = config.getInt(DatagenSourceConfig.CONFIG_ONLINEORDERS_CUSTOMER_EMAILS_MAX);
        this.minPhones = config.getInt(DatagenSourceConfig.CONFIG_ONLINEORDERS_ADDRESS_PHONES_MIN);
        this.maxPhones = config.getInt(DatagenSourceConfig.CONFIG_ONLINEORDERS_ADDRESS_PHONES_MAX);
        this.reuseAddressRatio = config.getDouble(DatagenSourceConfig.CONFIG_ONLINEORDERS_REUSE_ADDRESS_RATIO);
        this.cities = config.getList(DatagenSourceConfig.CONFIG_ONLINEORDERS_CITIES);
        this.baseUrl = config.getString(DatagenSourceConfig.CONFIG_ONLINEORDERS_URL);
        this.maxEvents = config.getInt(DatagenSourceConfig.CONFIG_ONLINEORDERS_CLICKEVENTS_MAX);
        this.abandonmentRate = config.getDouble(DatagenSourceConfig.CONFIG_ONLINEORDERS_ABANDONED_RATIO);
        this.alreadyLoggedInRate = config.getDouble(DatagenSourceConfig.CONFIG_ONLINEORDERS_LOGGEDIN_RATIO);
        this.marketingCampaignRate = config.getDouble(DatagenSourceConfig.CONFIG_ONLINEORDERS_MARKETING_RATIO);
        this.outOfStockRatio = config.getDouble(DatagenSourceConfig.CONFIG_ONLINEORDERS_OUTOFSTOCK_RATIO);
    }


    /**
     * Prepare a new online customer that could be used in future clicktracking events.
     * This allows for correlation with new customer events.
     */
    public NewCustomer registerNewOnlineCustomer() {
        try {
            if (recentNewCustomers.size() < 3) {
                OnlineCustomer newCustomer = OnlineCustomer.create(faker, minEmails, maxEmails);

                if (recentNewCustomers.add(newCustomer)) {
                    ZonedDateTime regTs = ZonedDateTime.now();
                    return new NewCustomer(formatTimestamp(regTs), newCustomer, regTs);
                }
            }
        }
        catch (IllegalStateException ise) {}

        return null;
    }


    /**
     * Start a new user session.
     *
     * @return session id
     */
    public String startNewSession() {
        if (currentSessions.size() >= maxSessions) {
            return null;
        }

        SessionState newSession = null;
        String newSessionId = "";
        do {
            newSession = new SessionState(faker);
            newSessionId = newSession.sessionId;
        } while (currentSessions.containsKey(newSessionId));

        currentSessions.put(newSession.sessionId, newSession);
        return newSession.sessionId;
    }

    /** Is the specified session still active? */
    public boolean hasMore(String sessionid) {
        SessionState session = currentSessions.get(sessionid);
        if (session != null) {
            if (!session.hasMoreEvents) {
                endSession(session);
            }
            return session.hasMoreEvents;
        }
        return false;
    }

    /** Generate and return the next event for an active user session. */
    public OnlineActivityData nextActivity(ZonedDateTime timestamp, String sessionid) {
        SessionState session = currentSessions.get(sessionid);

        if (session == null || !session.hasMoreEvents) {
            // edge case / error handling - shouldn't happen but
            //  check in case the task has requested an event for
            //  a session that has already completed
            return null;
        }

        if (session.currentEventIdx++ == 0) {
            // special case - first event in the session
            //  initialise new set of activity by returning an initial page view
            session.currentEventType = ClickEventType.PAGE_VIEW;
            session.currentPage = createPageUrl(session);
            return Generators.randomBoolean() ?
                createClickTrackingEvent(timestamp, session) :
                createClickTrackingEventWithReferrer(timestamp, session);
        }

        if (session.currentEventType == ClickEventType.CHECKOUT_COMPLETE) {
            // if the last event was a checkout complete, return a corresponding order
            endSession(session);
            return createOnlineOrder(timestamp, session);
        }

        if (session.currentEventIdx > maxEvents) {
            // already emitted maxEvents so we treat this as if the user has
            //  run out of time to complete their order
            return abandonCart(timestamp, session);
        }

        if (!session.currentCart.isEmpty() && Generators.shouldDo(abandonmentRate)) {
            // abandon some non-empty carts
            return abandonCart(timestamp, session);
        }

        // next event type to generate
        session.currentEventType = nextEventType(session);
        session.currentPage = createPageUrl(session);


        if (session.currentEventType == null) {
            log.error("Unexpected event type - abandoning session");
            endSession(session);
            return null;
        }

        switch (session.currentEventType) {
            case ADD_TO_CART: {
                String product = productGenerator.generate().getDescription();
                session.currentCart.add(product);
                return createCartEvent(timestamp, session, product);
            }
            case REMOVE_FROM_CART: {
                String product = session.currentCart.iterator().next();
                session.currentCart.remove(product);
                return createCartEvent(timestamp, session, product);
            }
            case LOGIN: {
                session.currentLoggedInStatus = true;
                session.loggedInUser = login();
                return createClickTrackingEvent(timestamp, session);
            }
            default: {
                return createClickTrackingEvent(timestamp, session);
            }
        }
    }


    private OnlineCustomer login() {
        // use a recently registered new customer if one is available,
        //  otherwise create a new customer
        OnlineCustomer recentCustomer = recentNewCustomers.poll();
        if (recentCustomer != null) {
            return recentCustomer;
        }
        return OnlineCustomer.create(faker, minEmails, maxEmails);
    }


    private OnlineActivityData createClickTrackingEvent(ZonedDateTime timestamp, SessionState session) {
        return new ClickEvent(
            session.currentEventType,
            formatTimestamp(timestamp),
            timestamp,
            session.sessionId,
            session.userContext,
            session.loggedInUser,
            session.currentPage);
    }

    private OnlineActivityData createClickTrackingEventWithReferrer(ZonedDateTime timestamp, SessionState session) {
        try {
            return new ClickEvent(
                session.currentEventType,
                formatTimestamp(timestamp),
                timestamp,
                session.sessionId,
                session.userContext,
                session.loggedInUser,
                session.currentPage,
                new URL(faker.digitalMarketing().referrerUrl()));
        }
        catch (MalformedURLException e) {
            log.error("Generated invalid referrer url", e);
            return createClickTrackingEvent(timestamp, session);
        }
    }

    private OnlineActivityData createOnlineOrder(ZonedDateTime timestamp, SessionState session) {
        Address shippingAddress = Address.create(faker, COUNTRY, minPhones, maxPhones);
        if (cities.size() > 0) {
            // override the faker-generated city name with one provided in config
            String city = Generators.randomItem(cities);
            shippingAddress.setCity(city);
        }

        Address billingAddress = Generators.shouldDo(reuseAddressRatio)
                ? shippingAddress
                : Address.create(faker, COUNTRY, minPhones, maxPhones);

        return new OnlineOrder(
            formatTimestamp(timestamp),
            session.loggedInUser,
            new ArrayList<>(session.currentCart),
            new OnlineAddress(shippingAddress, billingAddress),
            timestamp);
    }

    private OnlineActivityData createAbandonedOrder(ZonedDateTime timestamp, SessionState session) {
        return new AbandonedOrder(
            formatTimestamp(timestamp),
            session.loggedInUser,
            new ArrayList<>(session.currentCart),
            timestamp);
    }

    private OnlineActivityData createCartEvent(ZonedDateTime timestamp, SessionState session, String product) {
        return new ClickEvent(
            session.currentEventType,
            formatTimestamp(timestamp),
            timestamp,
            session.sessionId,
            session.userContext,
            session.loggedInUser,
            session.currentPage,
            product);
    }

    private String createPageUrl(SessionState session) {
        switch (session.currentEventType) {
            case PAGE_VIEW:
                if (Generators.shouldDo(0.4)) {
                    return baseUrl + "/homepage" + session.urlQueryString;
                }
                else {
                    return baseUrl + "/category/" + Generators.randomString("abcdef", 5) + session.urlQueryString;
                }
            case SEARCH:
                return baseUrl + "/search" + session.urlQueryString;
            case PRODUCT_VIEW:
                return baseUrl + "/product/" + Generators.randomString("abcde0123456789", 12) + session.urlQueryString;
            case CART_VIEW:
            case CHECKOUT_START:
                return baseUrl + "/cart" + session.urlQueryString;
            case LOGIN:
                return baseUrl + "/login" + session.urlQueryString;
            default:
                // otherwise assume the event has not changed the page
                //  so keep the previously generated page URL
                return session.currentPage;
        }
    }

    private ClickEventType nextEventType(SessionState session) {
        Map<ClickEventType, Double> transitions = null;
        switch (session.currentEventType) {
            case PAGE_VIEW: transitions = pageViewTransitions; break;
            case SEARCH: transitions = searchTransitions; break;
            case PRODUCT_VIEW: transitions = productViewTransitions; break;
            case ADD_TO_CART: transitions = addToCartTransitions; break;
            case REMOVE_FROM_CART: transitions = removeFromCartTransitions; break;
            case CART_VIEW: transitions = cartViewTransitions; break;
            case LOGIN: transitions = loginTransitions; break;
            case CHECKOUT_START: transitions = checkoutStartTransitions; break;
            default: transitions = null; break;
        }

        if (transitions == null || transitions.isEmpty()) {
            return null;
        }

        // adjust the set of valid next steps based on the current state
        Map<ClickEventType, Double> validTransitions = new EnumMap<>(ClickEventType.class);
        validTransitions.putAll(transitions);
        if (session.currentLoggedInStatus) {
            validTransitions.remove(ClickEventType.LOGIN);
        }
        if (!session.currentLoggedInStatus) {
            validTransitions.remove(ClickEventType.CHECKOUT_START);
        }
        if (session.currentCart.isEmpty()) {
            validTransitions.remove(ClickEventType.CHECKOUT_START);
            validTransitions.remove(ClickEventType.REMOVE_FROM_CART);
        }
        if (session.currentCart.size() >= maxProducts) {
            validTransitions.remove(ClickEventType.ADD_TO_CART);
        }
        if (validTransitions.isEmpty()) {
            return null;
        }

        // select a random next step from the weighted set of valid next steps
        double total = validTransitions.values().stream().mapToDouble(Double::doubleValue).sum();
        double rand = Generators.randomDouble() * total;
        double cumulative = 0.0;
        for (Map.Entry<ClickEventType, Double> entry : validTransitions.entrySet()) {
            cumulative += entry.getValue();
            if (rand <= cumulative) {
                return entry.getKey();
            }
        }

        return validTransitions.keySet().iterator().next();
    }


    private void endSession(SessionState session) {
        session.hasMoreEvents = false;
        currentSessions.remove(session.sessionId);
    }

    private OnlineActivityData abandonCart(ZonedDateTime timestamp, SessionState session) {
        endSession(session);

        return session.currentLoggedInStatus ?
            // if the user is logged in, record the abandoned cart
            createAbandonedOrder(timestamp, session) :
            // don't record anonymous abandoned carts
            null;
    }


    public boolean shouldGenerateOutOfStockEvent() {
        return Generators.shouldDo(outOfStockRatio);
    }


    /** Locale used for the data generation. */
    protected static final Locale DEFAULT_LOCALE = Locale.US;



    /**
     * Return a string representation of the provided timestamp.
     *
     * The format is determined by a format string provided to the generator
     *  constructor.
     */
    private String formatTimestamp(TemporalAccessor timestamp) {
        return timestampFormatter.format(timestamp);
    }



    private static final String VALID_SESSIONID_CHARS = "abcdefghijklmnopqrstuvwxyz0123456789";


    /** Internal helper class for storing a collection of data for a single user session. */
    private class SessionState {
        // data about the session that will be used in multiple click events
        final String sessionId;
        final UserContext userContext;
        final Set<String> currentCart;
        boolean currentLoggedInStatus;
        OnlineCustomer loggedInUser;
        String currentPage;
        String urlQueryString;

        // internal state used by the generator to create an appropriate next event
        int currentEventIdx = 0;
        ClickEventType currentEventType = null;
        boolean hasMoreEvents = true;

        private SessionState(LoosehangerFaker faker) {
            // initialise state for a new user session
            sessionId = "sess_" + Generators.randomString(VALID_SESSIONID_CHARS, 16);
            userContext = new UserContext(faker);
            currentCart = new HashSet<>();
            currentLoggedInStatus = false;
            loggedInUser = null;
            currentPage = baseUrl;
            if (Generators.shouldDo(marketingCampaignRate)) {
                String marketingQuery = Generators.randomBoolean() ?
                    faker.digitalMarketing().queryStringWithClickId() :
                    faker.digitalMarketing().queryString();
                urlQueryString = "?" + marketingQuery;
            }
            else {
                urlQueryString = "";
            }
            currentLoggedInStatus = Generators.shouldDo(alreadyLoggedInRate);
            if (currentLoggedInStatus) {
                loggedInUser = OnlineCustomer.create(faker, minEmails, maxEmails);
            }
        }
    }


    // ------------------------------------------------------------
    // initialize session state machine
    // ------------------------------------------------------------
    private static final Map<ClickEventType, Double> pageViewTransitions = new EnumMap<>(ClickEventType.class);
    private static final Map<ClickEventType, Double> searchTransitions = new EnumMap<>(ClickEventType.class);
    private static final Map<ClickEventType, Double> productViewTransitions = new EnumMap<>(ClickEventType.class);
    private static final Map<ClickEventType, Double> addToCartTransitions = new EnumMap<>(ClickEventType.class);
    private static final Map<ClickEventType, Double> removeFromCartTransitions = new EnumMap<>(ClickEventType.class);
    private static final Map<ClickEventType, Double> cartViewTransitions = new EnumMap<>(ClickEventType.class);
    private static final Map<ClickEventType, Double> loginTransitions = new EnumMap<>(ClickEventType.class);
    private static final Map<ClickEventType, Double> checkoutStartTransitions = new EnumMap<>(ClickEventType.class);
    static {
        pageViewTransitions.put(ClickEventType.PAGE_VIEW, 0.30);
        pageViewTransitions.put(ClickEventType.SEARCH, 0.25);
        pageViewTransitions.put(ClickEventType.PRODUCT_VIEW, 0.30);
        pageViewTransitions.put(ClickEventType.CART_VIEW, 0.10);
        pageViewTransitions.put(ClickEventType.LOGIN, 0.05);

        searchTransitions.put(ClickEventType.PAGE_VIEW, 0.15);
        searchTransitions.put(ClickEventType.PRODUCT_VIEW, 0.50);
        searchTransitions.put(ClickEventType.CART_VIEW, 0.05);
        searchTransitions.put(ClickEventType.SEARCH, 0.25);
        searchTransitions.put(ClickEventType.LOGIN, 0.05);

        productViewTransitions.put(ClickEventType.ADD_TO_CART, 0.50);
        productViewTransitions.put(ClickEventType.PAGE_VIEW, 0.25);
        productViewTransitions.put(ClickEventType.SEARCH, 0.10);
        productViewTransitions.put(ClickEventType.CART_VIEW, 0.10);
        productViewTransitions.put(ClickEventType.LOGIN, 0.05);

        addToCartTransitions.put(ClickEventType.CART_VIEW, 0.50);
        addToCartTransitions.put(ClickEventType.PAGE_VIEW, 0.25);
        addToCartTransitions.put(ClickEventType.SEARCH, 0.20);
        addToCartTransitions.put(ClickEventType.LOGIN, 0.05);

        removeFromCartTransitions.put(ClickEventType.CART_VIEW, 0.75);
        removeFromCartTransitions.put(ClickEventType.PAGE_VIEW, 0.25);

        cartViewTransitions.put(ClickEventType.REMOVE_FROM_CART, 0.10);
        cartViewTransitions.put(ClickEventType.CHECKOUT_START, 0.45);
        cartViewTransitions.put(ClickEventType.PAGE_VIEW, 0.20);
        cartViewTransitions.put(ClickEventType.SEARCH, 0.20);
        cartViewTransitions.put(ClickEventType.LOGIN, 0.05);

        loginTransitions.put(ClickEventType.PAGE_VIEW, 0.55);
        loginTransitions.put(ClickEventType.CART_VIEW, 0.45);

        checkoutStartTransitions.put(ClickEventType.CHECKOUT_COMPLETE, 0.70);
        checkoutStartTransitions.put(ClickEventType.PAGE_VIEW, 0.30);
    }



    // ------------------------------------------------------------

    /**
     * Generates one week's worth of events to create a fake history.
     *  This is intended to be used on the first run of the connector
     *  to create an instant history of events that can be used for
     *  historical aggregations.
     */
    public List<OnlineActivityData> generateHistory(int sessionIntervalSecs, int eventIntervalSecs)
    {
        final List<OnlineActivityData> history = new ArrayList<>();

        final ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime timestamp = ZonedDateTime.now().minusDays(7);

        String historicalSessionId = startNewSession();
        while (timestamp.isBefore(now)) {
            while (hasMore(historicalSessionId) && timestamp.isBefore(now)) {
                timestamp = timestamp.plusSeconds(eventIntervalSecs);
                OnlineActivityData event = nextActivity(timestamp, historicalSessionId);
                if (event != null) {
                    history.add(event);
                }
            }

            timestamp = timestamp.plusSeconds(sessionIntervalSecs);
            historicalSessionId = startNewSession();
        }

        currentSessions.clear();
        return history;
    }
}
