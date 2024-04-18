/**
 * Copyright 2023 IBM Corp. All Rights Reserved.
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
package com.ibm.eventautomation.demos.loosehangerjeans;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.source.SourceConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatagenSourceConnector extends SourceConnector {

    protected static final String VERSION = "0.0.5";

    private final Logger log = LoggerFactory.getLogger(DatagenSourceConnector.class);

    private Map<String, String> configProps = null;

    @Override
    public ConfigDef config() {
        return DatagenSourceConfig.CONFIG_DEF;
    }

    @Override
    public Class<? extends Task> taskClass() {
        return DatagenSourceTask.class;
    }

    @Override
    public List<Map<String, String>> taskConfigs(int maxTasks) {
        if (maxTasks > 1) {
            log.error("Only one task is supported. Ignoring tasks.max which is set to {}", maxTasks);
        }

        List<Map<String, String>> taskConfigs = new ArrayList<>(1);
        taskConfigs.add(configProps);
        return taskConfigs;
    }


    @Override
    public void start(Map<String, String> props) {
        log.info("Starting connector {}", props);
        configProps = props;
    }

    @Override
    public void stop() {
        log.info("Stopping connector");
    }


    @Override
    public String version() {
        return VERSION;
    }
}
