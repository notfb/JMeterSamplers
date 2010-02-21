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

package org.apache.jmeter.visualizers;

import org.apache.jmeter.samplers.SampleResult;

/**
 * @author Khor Soon Hin Created 2001/08/12
 * @version $Revision: 493775 $ Last updated: $Date: 2007-01-07 18:09:09 +0100 (Sun, 07 Jan 2007) $
 */

public interface GraphAccumListener {
	public void updateGui(SampleResult s);

	public void updateGui();
}