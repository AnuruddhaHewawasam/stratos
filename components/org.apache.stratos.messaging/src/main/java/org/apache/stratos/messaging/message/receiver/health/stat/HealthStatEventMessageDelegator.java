/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.stratos.messaging.message.receiver.health.stat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.stratos.messaging.message.processor.MessageProcessorChain;
import org.apache.stratos.messaging.message.processor.health.stat.HealthStatMessageProcessorChain;
import org.apache.stratos.messaging.message.processor.instance.notifier.InstanceNotifierMessageProcessorChain;
import org.apache.stratos.messaging.message.receiver.instance.notifier.InstanceNotifierEventMessageQueue;
import org.apache.stratos.messaging.util.Constants;

import javax.jms.TextMessage;


/**
 * Implements logic for processing health stat event messages based on a given
 * topology process chain.
 */
public class HealthStatEventMessageDelegator implements Runnable {

    private static final Log log = LogFactory.getLog(HealthStatEventMessageDelegator.class);
    private MessageProcessorChain processorChain;
    private boolean terminated;

    public HealthStatEventMessageDelegator() {
        this.processorChain = new HealthStatMessageProcessorChain();
    }

    public HealthStatEventMessageDelegator(MessageProcessorChain processorChain) {
        this.processorChain = processorChain;
    }

    @Override
    public void run() {
        try {
            if (log.isInfoEnabled()) {
                log.info("Health stat event message delegator started");
            }

            while (!terminated) {
                try {
                    TextMessage message = HealthStatEventMessageQueue.getInstance().take();

                    // Retrieve the header
                    //TODO get the type from json since this is sent by CEP
                    String type = message.getStringProperty(Constants.EVENT_CLASS_NAME);

                    // Retrieve the actual message
                    //TODO get the 'message' from full json message
                    String json = message.getText();
                    if (log.isDebugEnabled()) {
                        log.debug(String.format("Instance notifier event message received from queue: %s", type));
                    }

                    // Delegate message to message processor chain
                    if (log.isDebugEnabled()) {
                        log.debug(String.format("Delegating instance notifier event message: %s", type));
                    }
                    processorChain.process(type, json, null);
                } catch (Exception e) {
                    log.error("Failed to retrieve instance notifier event message", e);
                }
            }
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("Instance notifier event message delegator failed", e);
            }
        }
    }

    /**
     * Terminate topology event message delegator thread.
     */
    public void terminate() {
        terminated = true;
    }
}
