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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

public class WebUserAgentTest {

    private LoosehangerFaker faker;
    private WebUserAgent webUserAgent;

    @BeforeEach
    void setUp() {
        faker = new LoosehangerFaker();
        webUserAgent = faker.webUserAgent();
    }

    @Test
    void testDeviceType() {
        int desktop = 0, mobile = 0, tablet = 0;
        int iterations = 1000;

        for (int i = 0; i < iterations; i++) {
            String device = webUserAgent.deviceType();
            switch (device) {
                case "Desktop": desktop++; break;
                case "Mobile": mobile++; break;
                case "Tablet": tablet++; break;
                default: fail("Unexpected device type " + device);
            }
        }

        assertTrue(desktop > 400 && desktop < 600, "Desktop should be ~50%");
        assertTrue(mobile > 300 && mobile < 500, "Mobile should be ~40%");
        assertTrue(tablet > 50 && tablet < 150, "Tablet should be ~10%");
    }

    @Test
    void testOperatingSystem() {
        String os = webUserAgent.operatingSystem();
        assertNotNull(os);
        assertFalse(os.isEmpty());
    }

    @Test
    void testOperatingSystemByDeviceType() {
        String mobileOS = webUserAgent.operatingSystem("Mobile");
        assertTrue(mobileOS.contains("Android") || mobileOS.contains("iOS"));

        String desktopOS = webUserAgent.operatingSystem("Desktop");
        assertTrue(desktopOS.contains("Windows") ||
                   desktopOS.contains("macOS") ||
                   desktopOS.contains("Ubuntu") ||
                   desktopOS.contains("Fedora") ||
                   desktopOS.contains("Debian"));

        String tabletOS = webUserAgent.operatingSystem("Tablet");
        assertTrue(tabletOS.contains("Android") || tabletOS.contains("iPadOS"));
    }

    @Test
    void testBrowserName() {
        String browser = webUserAgent.browserName();
        assertNotNull(browser);
        assertFalse(browser.isEmpty());
    }

    @Test
    void testBrowserNameByDeviceType() {
        String desktopBrowser = webUserAgent.browserName("Desktop");
        assertTrue(desktopBrowser.matches("Chrome|Firefox|Safari|Edge|Opera|Brave"));

        String mobileBrowser = webUserAgent.browserName("Mobile");
        assertTrue(mobileBrowser.matches("Chrome Mobile|Safari Mobile|Samsung Internet|Firefox Mobile|Opera Mobile"));
    }

    @Test
    void testBrowserVersion() {
        String chromeVersion = webUserAgent.browserVersion("Chrome");
        assertNotNull(chromeVersion);
        assertTrue(chromeVersion.matches("\\d+\\.\\d+\\.\\d+\\.\\d+"));

        String firefoxVersion = webUserAgent.browserVersion("Firefox");
        assertNotNull(firefoxVersion);
        assertTrue(firefoxVersion.matches("\\d+\\.\\d+"));

        String safariVersion = webUserAgent.browserVersion("Safari");
        assertNotNull(safariVersion);
        assertTrue(safariVersion.matches("\\d+\\.\\d+"));
    }

    @Test
    void testScreenResolution() {
        String resolution = webUserAgent.screenResolution();
        assertNotNull(resolution);
        assertTrue(resolution.matches("\\d+x\\d+"));
    }

    @Test
    void testScreenResolutionByDeviceType() {
        String desktopRes = webUserAgent.screenResolution("Desktop");
        assertTrue(desktopRes.matches("\\d{4}x\\d{3,4}"));

        String mobileRes = webUserAgent.screenResolution("Mobile");
        assertTrue(mobileRes.matches("\\d{3}x\\d{3}"));
    }

    @Test
    void testUserAgentString() {
        String userAgent = webUserAgent.userAgentString();
        assertNotNull(userAgent);
        assertTrue(userAgent.startsWith("Mozilla/5.0"));
        assertTrue(userAgent.length() > 50);
    }

    @RepeatedTest(20)
    void testUserAgentStringFormat() {
        String userAgent = webUserAgent.userAgentString();

        assertTrue(userAgent.startsWith("Mozilla/5.0"), "User agent should start with Mozilla/5.0");
        assertTrue(userAgent.contains("(") && userAgent.contains(")"),
                   "User agent should contain platform information in parentheses");
        assertTrue(userAgent.contains("Chrome") ||
                   userAgent.contains("Firefox") ||
                   userAgent.contains("Safari") ||
                   userAgent.contains("Edge") ||
                   userAgent.contains("Opera"),
                   "User agent should contain browser name");
    }

    @Test
    void testUserAgentStringWithSpecificParameters() {
        String userAgent = webUserAgent.userAgentString("Desktop", "Windows 11", "Chrome");
        assertNotNull(userAgent);
        assertTrue(userAgent.contains("Windows"));
        assertTrue(userAgent.contains("Chrome"));
    }

    @Test
    void testConsistentUserAgentGeneration() {
        String deviceType = "Mobile";
        String os = webUserAgent.operatingSystem(deviceType);
        String browser = webUserAgent.browserName(deviceType);
        String userAgent = webUserAgent.userAgentString(deviceType, os, browser);

        if (os.startsWith("Android")) {
            assertTrue(userAgent.contains("Android"));
        }
        else if (os.startsWith("iOS")) {
            assertTrue(userAgent.contains("iPhone"));
        }

        String browserCheck = browser.replace(" Mobile", "");
        if (browserCheck.equals("Opera")) {
            assertTrue(userAgent.contains("OPR") || userAgent.contains("Opera"),
                       "User agent should contain OPR or Opera for Opera browser");
        }
        else if (browserCheck.equals("Samsung Internet")) {
            assertTrue(userAgent.contains("SamsungBrowser"),
                       "User agent should contain SamsungBrowser for Samsing Internet browser");
        }
        else {
            assertTrue(userAgent.contains(browserCheck),
                       "User agent should contain browser name: " + browserCheck);
        }
    }

    @Test
    void testWebKitVersion() {
        String version = webUserAgent.webKitVersion();
        assertNotNull(version);
        assertTrue(version.matches("\\d{3}\\.\\d+(\\.\\d+)?"), "Unexpected version " + version);
    }

    @Test
    void testJavaScriptEnabled() {
        int enabled = 0;
        for (int i = 0; i < 100; i++) {
            if (webUserAgent.javaScriptEnabled()) {
                enabled++;
            }
        }
        assertTrue(enabled > 95, "JavaScript should be enabled in >95% of cases");
    }

    @Test
    void testCookiesEnabled() {
        int enabled = 0;
        for (int i = 0; i < 100; i++) {
            if (webUserAgent.cookiesEnabled()) {
                enabled++;
            }
        }
        assertTrue(enabled >= 90, "Cookies should be enabled in at least 90% of cases but was enabled in only " + enabled);
    }

    @Test
    void testDoNotTrack() {
        int enabled = 0;
        for (int i = 0; i < 400; i++) {
            if (webUserAgent.doNotTrack()) {
                enabled++;
            }
        }
        assertTrue(enabled > 70 && enabled < 130, "DoNotTrack should be enabled in ~25% of cases");
    }

    @Test
    void testDeterministicGeneration() {
        LoosehangerFaker faker1 = new LoosehangerFaker(new java.util.Random(12345));
        LoosehangerFaker faker2 = new LoosehangerFaker(new java.util.Random(12345));

        assertEquals(faker1.webUserAgent().userAgentString(),
                     faker2.webUserAgent().userAgentString(),
                     "Same seed should produce same user agent");
    }

    @Test
    void testUserAgentVariety() {
        // Test that we get variety in generated user agents
        java.util.Set<String> userAgents = new java.util.HashSet<>();
        for (int i = 0; i < 100; i++) {
            userAgents.add(webUserAgent.userAgentString());
        }

        assertTrue(userAgents.size() > 50,
                   "Should generate at least 50 unique user agents out of 100");
    }
}