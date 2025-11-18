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
package com.ibm.eventautomation.demos.loosehangerjeans.data;

import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;

import com.ibm.eventautomation.demos.datafaker.LoosehangerFaker;
import com.ibm.eventautomation.demos.datafaker.WebUserAgent;

import net.datafaker.providers.base.Internet;



public class UserContext {

    private final String deviceType;
    private final String deviceOperatingSystem;
    private final String browserName;
    private final String browserVersion;
    private final String userAgent;
    private final String ipAddress;
    private final String screenResolution;
    private final boolean javascript;
    private final boolean donottrack;
    private final boolean cookies;

    public UserContext(LoosehangerFaker faker) {
        WebUserAgent webUserAgent = faker.webUserAgent();
        Internet internet = faker.internet();

        deviceType = webUserAgent.deviceType();
        deviceOperatingSystem = webUserAgent.operatingSystem(deviceType);
        browserName = webUserAgent.browserName(deviceType);
        browserVersion = webUserAgent.browserVersion(browserName);
        userAgent = webUserAgent.userAgentString(deviceType, deviceOperatingSystem, browserName);
        ipAddress = internet.ipV4Address();
        screenResolution = webUserAgent.screenResolution(deviceType);
        javascript = webUserAgent.javaScriptEnabled();
        donottrack = webUserAgent.doNotTrack();
        cookies = webUserAgent.cookiesEnabled();
    }

    private static final Schema DEVICE_SCHEMA = SchemaBuilder.struct()
        .name("device")
        .version(1)
        .field("type",       Schema.STRING_SCHEMA)
        .field("os",         Schema.STRING_SCHEMA)
        .field("resolution", Schema.STRING_SCHEMA)
        .build();

    private static final Schema ENABLED_SCHEMA = SchemaBuilder.struct()
        .name("browserenabled")
        .version(1)
        .field("cookies",    Schema.BOOLEAN_SCHEMA)
        .field("javascript", Schema.BOOLEAN_SCHEMA)
        .build();

    private static final Schema BROWSER_SCHEMA = SchemaBuilder.struct()
        .name("browser")
        .version(1)
        .field("name",      Schema.STRING_SCHEMA)
        .field("version",   Schema.STRING_SCHEMA)
        .field("useragent", Schema.STRING_SCHEMA)
        .field("enabled",   ENABLED_SCHEMA)
        .build();

    public static final Schema SCHEMA = SchemaBuilder.struct()
        .name("usercontext")
        .version(1)
        .field("device",     DEVICE_SCHEMA)
        .field("browser",    BROWSER_SCHEMA)
        .field("ipaddress",  Schema.STRING_SCHEMA)
        .field("donottrack", Schema.BOOLEAN_SCHEMA)
        .build();


    public Struct toStruct() {
        final Struct device = new Struct(DEVICE_SCHEMA);
        device.put(DEVICE_SCHEMA.field("type"),       deviceType);
        device.put(DEVICE_SCHEMA.field("os"),         deviceOperatingSystem);
        device.put(DEVICE_SCHEMA.field("resolution"), screenResolution);

        final Struct enabled = new Struct(ENABLED_SCHEMA);
        enabled.put(ENABLED_SCHEMA.field("cookies"),    cookies);
        enabled.put(ENABLED_SCHEMA.field("javascript"), javascript);

        final Struct browser = new Struct(BROWSER_SCHEMA);
        browser.put(BROWSER_SCHEMA.field("name"),      browserName);
        browser.put(BROWSER_SCHEMA.field("version"),   browserVersion);
        browser.put(BROWSER_SCHEMA.field("useragent"), userAgent);
        browser.put(BROWSER_SCHEMA.field("enabled"),   enabled);

        final Struct struct = new Struct(SCHEMA);
        struct.put(SCHEMA.field("device"),     device);
        struct.put(SCHEMA.field("browser"),    browser);
        struct.put(SCHEMA.field("ipaddress"),  ipAddress);
        struct.put(SCHEMA.field("donottrack"), donottrack);
        return struct;
    }


    @Override
    public String toString() {
        return "UserContext[type=" + deviceType + ", os=" + deviceOperatingSystem +
            ", browser=" + browserName + ", userAgent=" + userAgent + ", ipAddress=" + ipAddress + "]";
    }
}
