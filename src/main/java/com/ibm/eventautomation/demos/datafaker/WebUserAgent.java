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


public class WebUserAgent extends AbstractProvider<BaseProviders> {

    private static final String[] DESKTOP_OS = {
        "Windows 10", "Windows 11", "macOS 13.0", "macOS 14.0", "macOS 15.0",
        "Ubuntu 22.04", "Ubuntu 24.04", "Fedora 38", "Debian 12"
    };

    private static final String[] MOBILE_OS = {
        "Android 13", "Android 14", "Android 15",
        "iOS 16.0", "iOS 17.0", "iOS 18.0"
    };

    private static final String[] TABLET_OS = {
        "Android 13", "Android 14", "iPadOS 16.0", "iPadOS 17.0", "iPadOS 18.0"
    };

    private static final String[] DESKTOP_BROWSERS = {
        "Chrome", "Firefox", "Safari", "Edge", "Opera", "Brave"
    };

    private static final String[] MOBILE_BROWSERS = {
        "Chrome Mobile", "Safari Mobile", "Samsung Internet", "Firefox Mobile",
        "Opera Mobile"
    };

    private static final String[] DESKTOP_RESOLUTIONS = {
        "1920x1080", "2560x1440", "3840x2160", "1680x1050", "1366x768",
        "1440x900", "2560x1600", "3440x1440", "1600x900", "1280x720"
    };

    private static final String[] MOBILE_RESOLUTIONS = {
        "390x844", "393x851", "412x915", "360x800", "414x896",
        "375x812", "428x926", "393x873", "412x892", "360x780"
    };

    private static final String[] TABLET_RESOLUTIONS = {
        "1024x768", "2048x1536", "1280x800", "2560x1600", "2224x1668",
        "2388x1668", "2732x2048", "1200x1920", "1600x2560"
    };

    private static final String[] CHROME_VERSIONS = {
        "119.0.0.0", "120.0.0.0", "121.0.0.0", "122.0.0.0", "123.0.0.0"
    };

    private static final String[] FIREFOX_VERSIONS = {
        "120.0", "121.0", "122.0", "123.0", "124.0"
    };

    private static final String[] SAFARI_VERSIONS = {
        "16.6", "17.0", "17.1", "17.2", "17.3", "17.4"
    };

    private static final String[] EDGE_VERSIONS = {
        "119.0.0.0", "120.0.0.0", "121.0.0.0", "122.0.0.0"
    };

    public WebUserAgent(BaseProviders faker) {
        super(faker);
    }

    public String deviceType() {
        int random = faker.random().nextInt(100);
        if (random < 50) {
            return "Desktop";
        }
        else if (random < 90) {
            return "Mobile";
        }
        else {
            return "Tablet";
        }
    }

    public String operatingSystem(String deviceType) {
        switch (deviceType) {
            case "Mobile":
                return MOBILE_OS[faker.random().nextInt(MOBILE_OS.length)];
            case "Tablet":
                return TABLET_OS[faker.random().nextInt(TABLET_OS.length)];
            case "Desktop":
            default:
                return DESKTOP_OS[faker.random().nextInt(DESKTOP_OS.length)];
        }
    }

    public String operatingSystem() {
        return operatingSystem(deviceType());
    }

    public String browserName(String deviceType) {
        if ("Mobile".equals(deviceType) || "Tablet".equals(deviceType)) {
            return MOBILE_BROWSERS[faker.random().nextInt(MOBILE_BROWSERS.length)];
        }
        else {
            return DESKTOP_BROWSERS[faker.random().nextInt(DESKTOP_BROWSERS.length)];
        }
    }

    public String browserName() {
        return browserName(deviceType());
    }

    public String browserVersion(String browserName) {
        if (browserName.contains("Chrome")) {
            return CHROME_VERSIONS[faker.random().nextInt(CHROME_VERSIONS.length)];
        }
        else if (browserName.contains("Firefox")) {
            return FIREFOX_VERSIONS[faker.random().nextInt(FIREFOX_VERSIONS.length)];
        }
        else if (browserName.contains("Safari")) {
            return SAFARI_VERSIONS[faker.random().nextInt(SAFARI_VERSIONS.length)];
        }
        else if (browserName.contains("Edge")) {
            return EDGE_VERSIONS[faker.random().nextInt(EDGE_VERSIONS.length)];
        }
        else {
            return CHROME_VERSIONS[faker.random().nextInt(CHROME_VERSIONS.length)];
        }
    }

    public String screenResolution(String deviceType) {
        switch (deviceType) {
            case "Mobile":
                return MOBILE_RESOLUTIONS[faker.random().nextInt(MOBILE_RESOLUTIONS.length)];
            case "Tablet":
                return TABLET_RESOLUTIONS[faker.random().nextInt(TABLET_RESOLUTIONS.length)];
            case "Desktop":
            default:
                return DESKTOP_RESOLUTIONS[faker.random().nextInt(DESKTOP_RESOLUTIONS.length)];
        }
    }

    public String screenResolution() {
        return screenResolution(deviceType());
    }

    public String userAgentString() {
        String deviceType = deviceType();
        String os = operatingSystem(deviceType);
        String browser = browserName(deviceType);
        String version = browserVersion(browser);

        return buildUserAgentString(deviceType, os, browser, version);
    }

    public String userAgentString(String deviceType, String os, String browser) {
        String version = browserVersion(browser);
        return buildUserAgentString(deviceType, os, browser, version);
    }

    private String buildUserAgentString(String deviceType, String os, String browser, String version) {
        StringBuilder ua = new StringBuilder("Mozilla/5.0 ");

        if ("Desktop".equals(deviceType)) {
            if (os.startsWith("Windows")) {
                ua.append("(Windows NT 10.0; Win64; x64) ");
            }
            else if (os.startsWith("macOS")) {
                ua.append("(Macintosh; Intel Mac OS X 10_15_7) ");
            }
            else if (os.contains("Ubuntu") || os.contains("Debian") || os.contains("Fedora")) {
                ua.append("(X11; Linux x86_64) ");
            }
        }
        else if ("Mobile".equals(deviceType)) {
            if (os.startsWith("Android")) {
                ua.append("(Linux; Android ").append(os.split(" ")[1])
                  .append("; SM-G991B) ");
            }
            else if (os.startsWith("iOS")) {
                ua.append("(iPhone; CPU iPhone OS ")
                  .append(os.split(" ")[1].replace(".", "_"))
                  .append(" like Mac OS X) ");
            }
        }
        else if ("Tablet".equals(deviceType)) {
            if (os.startsWith("Android")) {
                ua.append("(Linux; Android ").append(os.split(" ")[1])
                  .append("; SM-T870) ");
            }
            else if (os.startsWith("iPadOS")) {
                ua.append("(iPad; CPU OS ")
                  .append(os.split(" ")[1].replace(".", "_"))
                  .append(" like Mac OS X) ");
            }
        }

        if (browser.contains("Chrome")) {
            ua.append("AppleWebKit/537.36 (KHTML, like Gecko) ");
            if (browser.equals("Chrome Mobile")) {
                ua.append("Chrome/").append(version).append(" Mobile Safari/537.36");
            }
            else {
                ua.append("Chrome/").append(version).append(" Safari/537.36");
            }
        }
        else if (browser.contains("Firefox")) {
            ua.append("Gecko/20100101 Firefox/").append(version);
        }
        else if (browser.contains("Safari")) {
            ua.append("AppleWebKit/605.1.15 (KHTML, like Gecko) ");
            if (browser.equals("Safari Mobile")) {
                ua.append("Version/").append(version).append(" Mobile/15E148 Safari/604.1");
            }
            else {
                ua.append("Version/").append(version).append(" Safari/605.1.15");
            }
        }
        else if (browser.contains("Edge")) {
            ua.append("AppleWebKit/537.36 (KHTML, like Gecko) ");
            ua.append("Chrome/").append(version).append(" Safari/537.36 Edg/").append(version);
        }
        else if (browser.contains("Brave")) {
            ua.append("AppleWebKit/537.36 (KHTML, like Gecko) ");
            ua.append("Chrome/").append(version).append(" Safari/537.36");
        }
        else if (browser.contains("Samsung")) {
            ua.append("AppleWebKit/537.36 (KHTML, like Gecko) ");
            ua.append("SamsungBrowser/23.0 Chrome/115.0.0.0 Mobile Safari/537.36");
        }
        else if (browser.contains("Opera")) {
            ua.append("AppleWebKit/537.36 (KHTML, like Gecko) ");
            ua.append("Chrome/").append(version).append(" Safari/537.36 OPR/105.0.0.0");
        }

        return ua.toString().trim();
    }

    public String webKitVersion() {
        String[] versions = {
            "534.30",
            "537.11", "537.17", "537.22", "537.36",
            "600.1.4",
            "603.1.30",
            "604.1", "604.5.6",
            "605.1", "605.1.15", "605.2.8",
            "606.1", "606.2.11"
        };
        return versions[faker.random().nextInt(versions.length)];
    }

    public boolean javaScriptEnabled() {
        return faker.random().nextInt(100) < 99;
    }

    public boolean cookiesEnabled() {
        return faker.random().nextInt(100) < 98;
    }

    public boolean doNotTrack() {
        return faker.random().nextInt(100) < 25;
    }
}
