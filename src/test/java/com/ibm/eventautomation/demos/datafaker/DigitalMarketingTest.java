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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

public class DigitalMarketingTest {

    private LoosehangerFaker faker;
    private DigitalMarketing digitalMarketing;

    @BeforeEach
    void setUp() {
        faker = new LoosehangerFaker();
        digitalMarketing = faker.digitalMarketing();
    }

    @Test
    void testUtmSource() {
        int google = 0, facebook = 0, email = 0, other = 0;
        int iterations = 1000;

        for (int i = 0; i < iterations; i++) {
            String source = digitalMarketing.utmSource();
            assertNotNull(source);
            assertFalse(source.isEmpty());

            switch (source) {
                case "google": google++; break;
                case "facebook": facebook++; break;
                case "email": email++; break;
                default: other++; break;
            }
        }

        assertTrue(google > 250 && google < 350, "Google should be ~30%");
        assertTrue(facebook > 150 && facebook < 250, "Facebook should be ~20%");
        assertTrue(email > 50 && email < 150, "Email should be ~10%");
        assertTrue(other < 400, "Other should be <40% but was " + (other / 10));
    }

    @Test
    void testUtmMedium() {
        String medium = digitalMarketing.utmMedium();
        assertNotNull(medium);
        assertFalse(medium.isEmpty());
    }

    @Test
    void testUtmMediumBySource() {
        String googleMedium = digitalMarketing.utmMedium("google");
        assertTrue(googleMedium.equals("cpc") || googleMedium.equals("organic_search"));

        String facebookMedium = digitalMarketing.utmMedium("facebook");
        assertTrue(facebookMedium.equals("paid_social") || facebookMedium.equals("organic_social"));

        String emailMedium = digitalMarketing.utmMedium("email");
        assertEquals("email", emailMedium);

        String affiliateMedium = digitalMarketing.utmMedium("affiliate");
        assertEquals("affiliate", affiliateMedium);
    }

    @Test
    void testUtmCampaign() {
        String campaign = digitalMarketing.utmCampaign();
        assertNotNull(campaign);
        assertTrue(campaign.contains("_"));
    }

    @Test
    void testUtmContent() {
        String content = digitalMarketing.utmContent();
        assertNotNull(content);
        assertFalse(content.isEmpty());
    }

    @Test
    void testUtmTerm() {
        String term = digitalMarketing.utmTerm();
        assertNotNull(term);
        assertFalse(term.isEmpty());
    }

    @Test
    void testGclid() {
        String gclid = digitalMarketing.gclid();
        assertNotNull(gclid);
        assertTrue(gclid.length() > 20);
        assertTrue(gclid.startsWith("Cj0KCQiA") ||
                   gclid.startsWith("EAIaIQob") ||
                   gclid.startsWith("CjwKCAjw"));
    }

    @Test
    void testFbclid() {
        String fbclid = digitalMarketing.fbclid();
        assertNotNull(fbclid);
        assertTrue(fbclid.length() > 20);
        assertTrue(fbclid.startsWith("IwAR") ||
                   fbclid.startsWith("IwZX") ||
                   fbclid.startsWith("IwY"));
    }

    @Test
    void testMsclkid() {
        String msclkid = digitalMarketing.msclkid();
        assertNotNull(msclkid);
        assertEquals(22, msclkid.length());
        assertTrue(msclkid.matches("[A-Za-z0-9]{22}"));
    }

    @Test
    void testTtclid() {
        String ttclid = digitalMarketing.ttclid();
        assertNotNull(ttclid);
        assertEquals(28, ttclid.length());
        assertTrue(ttclid.matches("[A-Za-z0-9]{28}"));
    }

    @Test
    void testClickIdBySource() {
        String googleClickId = digitalMarketing.clickId("google");
        assertNotNull(googleClickId);
        assertTrue(googleClickId.startsWith("Cj") || googleClickId.startsWith("EA"));

        String fbClickId = digitalMarketing.clickId("facebook");
        assertNotNull(fbClickId);
        assertTrue(fbClickId.startsWith("Iw"));

        String unknownClickId = digitalMarketing.clickId("unknown");
        assertNull(unknownClickId);
    }

    @Test
    void testFullUtmParams() {
        Map<String, String> params = digitalMarketing.utmParams();
        assertNotNull(params);
        assertTrue(params.containsKey("utm_source"));
        assertTrue(params.containsKey("utm_medium"));
        assertTrue(params.containsKey("utm_campaign"));
        assertTrue(params.containsKey("utm_content"));
        assertTrue(params.size() >= 4);
    }

    @Test
    void testFullUtmParamsWithTerm() {
        boolean foundWithTerm = false;
        for (int i = 0; i < 50; i++) {
            Map<String, String> params = digitalMarketing.utmParams();
            String medium = params.get("utm_medium");
            if (medium.contains("cpc") || medium.contains("ppc") || medium.contains("paid_search")) {
                assertTrue(
                    params.containsKey("utm_term"),
                    "utm_term should be present for paid search campaigns");
                foundWithTerm = true;
                break;
            }
        }
        assertTrue(foundWithTerm, "Should generate at least one paid search campaign in 50 tries");
    }

    @Test
    void testFullUtmParamsWithClickId() {
        Map<String, String> params = digitalMarketing.utmParamsWithClickId();
        assertNotNull(params);
        assertTrue(params.containsKey("utm_source"));
        assertTrue(params.containsKey("utm_medium"));
        assertTrue(params.containsKey("utm_campaign"));

        String source = params.get("utm_source");
        if (source.equals("google") || source.equals("bing")) {
            assertTrue(params.containsKey("gclid"));
        }
        else if (source.equals("facebook") || source.equals("instagram")) {
            assertTrue(params.containsKey("fbclid"));
        }
        else if (source.equals("microsoft")) {
            assertTrue(params.containsKey("msclkid"));
        }
        else if (source.equals("tiktok")) {
            assertTrue(params.containsKey("ttclid"));
        }
    }

    @Test
    void testFullQueryStringWithClickId() {
        String queryString = digitalMarketing.queryStringWithClickId();
        assertNotNull(queryString);
        assertTrue(queryString.contains("utm_source="));
        assertTrue(queryString.length() > 50);
    }

    @Test
    void testReferrerDomain() {
        String domain = digitalMarketing.referrerDomain();
        assertNotNull(domain);
        assertTrue(domain.endsWith(".com"));
    }

    @Test
    void testReferrerUrl() {
        String url = digitalMarketing.referrerUrl();
        assertNotNull(url);
        assertTrue(url.startsWith("https://"));
        assertTrue(url.contains(".com"));
    }

    @Test
    void testConsistentMediumForSource() {
        for (int i = 0; i < 20; i++) {
            Map<String, String> params = digitalMarketing.utmParams();
            String source = params.get("utm_source");
            String medium = params.get("utm_medium");

            if (source.equals("email") || source.equals("newsletter")) {
                assertEquals("email", medium);
            }
            else if (source.equals("affiliate")) {
                assertEquals("affiliate", medium);
            }
            else if (source.equals("referral")) {
                assertEquals("referral", medium);
            }
        }
    }

    @Test
    void testDeterministicGeneration() {
        LoosehangerFaker faker1 = new LoosehangerFaker(new java.util.Random(12345));
        LoosehangerFaker faker2 = new LoosehangerFaker(new java.util.Random(12345));

        assertEquals(faker1.digitalMarketing().utmSource(),
                     faker2.digitalMarketing().utmSource(),
                     "Same seed should produce same UTM source");

        assertEquals(faker1.digitalMarketing().gclid().substring(0, 10),
                     faker2.digitalMarketing().gclid().substring(0, 10),
                     "Same seed should produce similar click IDs");
    }
}
