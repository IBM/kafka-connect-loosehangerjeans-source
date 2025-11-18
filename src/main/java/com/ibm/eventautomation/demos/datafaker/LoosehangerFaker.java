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

import java.util.Locale;
import java.util.Random;

import net.datafaker.Faker;
import net.datafaker.service.RandomService;

public class LoosehangerFaker extends Faker {

    public LoosehangerFaker() {
        super();
    }

    public LoosehangerFaker(Locale locale) {
        super(locale);
    }

    public LoosehangerFaker(Random random) {
        super(random);
    }

    public LoosehangerFaker(Locale locale, Random random) {
        super(locale, random);
    }

    public LoosehangerFaker(Locale locale, RandomService randomService) {
        super(locale, randomService);
    }



    public WebUserAgent webUserAgent() {
        return getProvider(WebUserAgent.class, WebUserAgent::new);
    }
    public DigitalMarketing digitalMarketing() {
        return getProvider(DigitalMarketing.class, DigitalMarketing::new);
    }
}
