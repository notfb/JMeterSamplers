/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.jmeter.protocol.jms.sampler;

import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

import javax.jms.*;

/**
 * Receiver of pseudo-synchronous reply messages.
 * 
 */
public class Receiver implements Runnable {
	private boolean active;

	private QueueSession session;

	private QueueReceiver consumer;

	private QueueConnection conn;

	// private static Receiver receiver;
	private static final Logger log = LoggingManager.getLoggerForClass();

	private Receiver(QueueConnectionFactory factory, Queue receiveQueue) throws JMSException {
		conn = factory.createQueueConnection();
		session = conn.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
		consumer = session.createReceiver(receiveQueue);
		log.debug("Receiver - ctor. Starting connection now");
		conn.start();
		log.info("Receiver - ctor. Connection to messaging system established");
	}

	public static synchronized Receiver createReceiver(QueueConnectionFactory factory, Queue receiveQueue)
			throws JMSException {
		// if (receiver == null) {
		Receiver receiver = new Receiver(factory, receiveQueue);
		Thread thread = new Thread(receiver);
		thread.start();
		// }
		return receiver;
	}

	public void run() {
		activate();
		Message reply;

		while (isActive()) {
			reply = null;
			try {
				reply = consumer.receive(5000);
				if (reply != null) {

					if (log.isDebugEnabled()) {
						log.debug("Received message, correlation id:" + reply.getJMSCorrelationID());
					}

					if (reply.getJMSCorrelationID() == null) {
						log.warn("Received message with correlation id null. Discarding message ...");
					} else {
						MessageAdmin.getAdmin().putReply(reply.getJMSCorrelationID(), reply);
					}
				}

			} catch (JMSException e1) {
				log.error("Error handling receive",e1);
			}
		}
		// not active anymore
		if (session != null) {
			try {
				session.close();
				try {
					if (conn != null) {
						conn.close();
					}
				} catch (JMSException e) {
					log.error("Error closing connection",e);
				}
			} catch (JMSException e) {
				log.error("Error closing session",e);
			}
		}
	}

	public synchronized void activate() {
		active = true;
	}

	public synchronized void deactivate() {
		active = false;
	}

	private synchronized boolean isActive() {
		return active;
	}

}