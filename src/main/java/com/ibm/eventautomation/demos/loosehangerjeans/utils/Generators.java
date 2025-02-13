/**
 * Copyright 2023, 2025 IBM Corp. All Rights Reserved.
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
package com.ibm.eventautomation.demos.loosehangerjeans.utils;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Random;

/**
 * Helper class for generating random data.
 */
public class Generators {

    private final static Random RNG = new Random();


    public static <T> T randomItem(List<T> list) {
        return list.get(RNG.nextInt(list.size()));
    }

    public static double randomPrice(double min, double max) {
        double randomValue = min + (max - min) * RNG.nextDouble();
        return Math.round(randomValue * 100.0) / 100.0;
    }

    public static double randomDouble(double min, double max) {
        double randomValue = min + (max - min) * RNG.nextDouble();
        return Math.round(randomValue * 10.0) / 10.0;
    }

    public static boolean shouldDo(double ratio) {
        return RNG.nextDouble() < ratio;
    }

    public static int randomInt(int min, int max) {
        return RNG.nextInt(min, max + 1);
    }

    public static boolean randomBoolean() {
    	return RNG.nextBoolean();
    }

    /**
     * Generator can simulate a delay in events being produced
     *  to Kafka by putting a timestamp in the message payload
     *  that is earlier than the current time.
     *
     * This helper class will generate a timestamp that is
     *  slightly earlier than now, to simulate an event that was
     *  produced a while ago and has taken a little time to make
     *  it to Kafka.
     *
     * The delay will be randomly generated, allowing for
     *  events to appear to be produced out of sequence.
     *
     * @param maxOffset - maximum delay to introduce in seconds
     */
    public static ZonedDateTime nowWithRandomOffset(int maxOffset) {
        final ZonedDateTime now = ZonedDateTime.now();
        if (maxOffset == 0) {
            return now;
        }
        else {
            return now.minusSeconds(randomInt(0, maxOffset));
        }
    }
}
