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

package org.apache.jmeter.control;

import java.io.Serializable;

import org.apache.jmeter.engine.event.LoopIterationEvent;
import org.apache.jmeter.engine.event.LoopIterationListener;
import org.apache.jmeter.samplers.Sampler;

/**
 * @author Michael Stover Created March 13, 2001
 * @version $Revision: 493775 $ Last updated: $Date: 2007-01-07 18:09:09 +0100 (Sun, 07 Jan 2007) $
 */
public class OnceOnlyController extends GenericController implements Serializable, LoopIterationListener {

	/**
	 * Constructor for the OnceOnlyController object.
	 */
	public OnceOnlyController() {
	}

	/**
	 * @see LoopIterationListener#iterationStart(LoopIterationEvent)
	 */
	public void iterationStart(LoopIterationEvent event) {
		if (event.getIteration() == 1) {
			reInitialize();
		}
	}

	protected Sampler nextIsNull() throws NextIsNullException {
		return null;
	}
}