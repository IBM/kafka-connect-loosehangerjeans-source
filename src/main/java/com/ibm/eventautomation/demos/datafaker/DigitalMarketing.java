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
package com.ibm.eventautomation.demos.datafaker;

import net.datafaker.providers.base.AbstractProvider;
import net.datafaker.providers.base.BaseProviders;

import java.time.Year;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;


public class DigitalMarketing extends AbstractProvider<BaseProviders> {

    private static final String[] UTM_SOURCES = {
        "google", "facebook", "twitter", "linkedin", "instagram", "youtube",
        "tiktok", "pinterest", "reddit", "bing", "newsletter", "email",
        "affiliate", "partner", "referral", "direct", "qr_code", "sms"
    };

    private static final String[] UTM_MEDIUMS = {
        "cpc", "ppc", "paid_search", "organic_search", "organic_social",
        "paid_social", "display", "banner", "email", "newsletter",
        "affiliate", "referral", "video", "cpm", "native", "retargeting",
        "social", "push_notification", "sms", "influencer"
    };

    private static final String[] CAMPAIGN_TYPES = {
        "spring_sale", "summer_promo", "fall_campaign", "winter_deals",
        "black_friday", "cyber_monday", "holiday_sale", "new_year",
        "back_to_school", "easter_promo", "mothers_day", "fathers_day",
        "product_launch", "brand_awareness", "lead_gen", "webinar",
        "ebook_download", "free_trial", "demo_request", "newsletter_signup",
        "retargeting", "abandoned_cart", "customer_retention", "upsell",
        "cross_sell", "referral_program", "loyalty_rewards"
    };

    private static final String[] CAMPAIGN_PREFIXES = {
        String.valueOf(Year.now().getValue()) + "_q1",
        String.valueOf(Year.now().getValue()) + "_q2",
        String.valueOf(Year.now().getValue()) + "_q3",
        String.valueOf(Year.now().getValue()) + "_q4",
        "jan", "feb", "mar", "apr", "may", "jun", "jul", "aug", "sep", "oct", "nov", "dec"
    };

    private static final String[] UTM_CONTENTS = {
        "hero_banner", "sidebar_ad", "footer_link", "header_cta", "popup",
        "exit_intent", "inline_text", "button_primary", "button_secondary",
        "image_ad", "video_ad", "carousel_1", "carousel_2", "carousel_3",
        "featured_product", "bestseller", "new_arrival", "clearance",
        "testimonial", "case_study", "blog_post", "landing_page",
        "version_a", "version_b", "control", "variant_1", "variant_2"
    };

    private static final String[] UTM_TERMS = {
        "best", "cheap", "affordable", "premium", "luxury", "discount",
        "sale", "deal", "offer", "coupon", "free", "online", "buy",
        "shop", "order", "near_me", "review", "compare", "vs", "alternative"
    };

    private static final String[] GCLID_PREFIXES = {"Cj0KCQiA", "EAIaIQob", "CjwKCAjw"};
    private static final String[] FBCLID_PREFIXES = {"IwAR", "IwZX", "IwY"};


    public DigitalMarketing(BaseProviders faker) {
        super(faker);
    }

    public String utmSource() {
        int random = faker.random().nextInt(100);
        if (random < 30) return "google";
        else if (random < 50) return "facebook";
        else if (random < 60) return "email";
        else if (random < 70) return "twitter";
        else if (random < 75) return "linkedin";
        else if (random < 80) return "instagram";
        else return UTM_SOURCES[faker.random().nextInt(UTM_SOURCES.length)];
    }

    public String utmMedium() {
        return UTM_MEDIUMS[faker.random().nextInt(UTM_MEDIUMS.length)];
    }

    public String utmMedium(String source) {
        switch (source.toLowerCase()) {
            case "google":
            case "bing":
                return faker.random().nextBoolean() ? "cpc" : "organic_search";
            case "facebook":
            case "instagram":
            case "twitter":
            case "linkedin":
            case "tiktok":
            case "pinterest":
                return faker.random().nextBoolean() ? "paid_social" : "organic_social";
            case "email":
            case "newsletter":
                return "email";
            case "affiliate":
                return "affiliate";
            case "referral":
                return "referral";
            default:
                return utmMedium();
        }
    }

    public String utmCampaign() {
        String prefix = CAMPAIGN_PREFIXES[faker.random().nextInt(CAMPAIGN_PREFIXES.length)];
        String type = CAMPAIGN_TYPES[faker.random().nextInt(CAMPAIGN_TYPES.length)];
        return prefix + "_" + type;
    }

    public String utmContent() {
        return UTM_CONTENTS[faker.random().nextInt(UTM_CONTENTS.length)];
    }

    public String utmTerm() {
        return UTM_TERMS[faker.random().nextInt(UTM_TERMS.length)];
    }

    public String gclid() {
        String prefix = GCLID_PREFIXES[faker.random().nextInt(GCLID_PREFIXES.length)];
        StringBuilder sb = new StringBuilder(prefix);
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_";
        for (int i = 0; i < 20; i++) {
            sb.append(chars.charAt(faker.random().nextInt(chars.length())));
        }
        return sb.toString();
    }

    public String fbclid() {
        String prefix = FBCLID_PREFIXES[faker.random().nextInt(FBCLID_PREFIXES.length)];
        StringBuilder sb = new StringBuilder(prefix);
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_";
        for (int i = 0; i < 24; i++) {
            sb.append(chars.charAt(faker.random().nextInt(chars.length())));
        }
        return sb.toString();
    }

    public String msclkid() {
        StringBuilder sb = new StringBuilder();
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        for (int i = 0; i < 22; i++) {
            sb.append(chars.charAt(faker.random().nextInt(chars.length())));
        }
        return sb.toString();
    }

    public String ttclid() {
        StringBuilder sb = new StringBuilder();
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        for (int i = 0; i < 28; i++) {
            sb.append(chars.charAt(faker.random().nextInt(chars.length())));
        }
        return sb.toString();
    }

    public String clickId(String source) {
        switch (source.toLowerCase()) {
            case "google":
            case "bing":
                return gclid();
            case "facebook":
            case "instagram":
                return fbclid();
            case "microsoft":
                return msclkid();
            case "tiktok":
                return ttclid();
            default:
                return null;
        }
    }

    public Map<String, String> utmParams() {
        Map<String, String> params = new HashMap<>();
        String source = utmSource();
        String medium = utmMedium(source);
        params.put("utm_source", source);
        params.put("utm_medium", medium);
        params.put("utm_campaign", utmCampaign());
        params.put("utm_content", utmContent());

        if (medium.contains("cpc") || medium.contains("ppc") || medium.contains("paid_search")) {
            params.put("utm_term", utmTerm());
        }

        return params;
    }

    public Map<String, String> utmParamsWithClickId() {
        Map<String, String> params = utmParams();
        String source = params.get("utm_source");
        String clickId = clickId(source);

        if (clickId != null) {
            switch (source.toLowerCase()) {
                case "google":
                case "bing":
                    params.put("gclid", clickId);
                    break;
                case "facebook":
                case "instagram":
                    params.put("fbclid", clickId);
                    break;
                case "microsoft":
                    params.put("msclkid", clickId);
                    break;
                case "tiktok":
                    params.put("ttclid", clickId);
                    break;
            }
        }

        return params;
    }

    public String queryString() {
        return mapToQueryString(utmParams());
    }

    public String queryStringWithClickId() {
        return mapToQueryString(utmParamsWithClickId());
    }

    private String mapToQueryString(Map<String, String> params) {
        return params.entrySet().stream()
            .map(e -> e.getKey() + "=" + e.getValue())
            .collect(Collectors.joining("&"));
    }

    public String referrerDomain() {
        String[] domains = {
            "google.com", "facebook.com", "twitter.com", "linkedin.com",
            "instagram.com", "youtube.com", "reddit.com", "pinterest.com",
            "tiktok.com", "bing.com", "yahoo.com", "duckduckgo.com"
        };
        return domains[faker.random().nextInt(domains.length)];
    }

    public String referrerUrl() {
        String domain = referrerDomain();
        String[] paths = {"/search", "/feed", "/discover", "/trending", "/home", ""};
        String path = paths[faker.random().nextInt(paths.length)];
        return "https://" + domain + path;
    }
}

