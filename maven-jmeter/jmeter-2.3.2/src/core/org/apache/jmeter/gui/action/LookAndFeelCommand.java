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

package org.apache.jmeter.gui.action;

import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.Set;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.jmeter.gui.GuiPackage;
import org.apache.jmeter.util.JMeterUtils;

/**
 * Implements the Look and Feel menu item.
 */
public class LookAndFeelCommand implements Command {

	private static final Set commands = new HashSet();

	static {
		UIManager.LookAndFeelInfo[] lfs = UIManager.getInstalledLookAndFeels();
		for (int i = 0; i < lfs.length; i++) {
			commands.add(ActionNames.LAF_PREFIX + lfs[i].getClassName());
		}

		try {
			String defaultUI = JMeterUtils.getPropDefault("jmeter.laf", UIManager
					.getCrossPlatformLookAndFeelClassName());
			UIManager.setLookAndFeel(defaultUI);
		} catch (IllegalAccessException e) {
		} catch (ClassNotFoundException e) {
        } catch (InstantiationException e) {
        } catch (UnsupportedLookAndFeelException e) {
        }
	}

	public LookAndFeelCommand() {
	}

	public void doAction(ActionEvent ev) {
		try {
			String className = ev.getActionCommand().substring(4).replace('/', '.');
			UIManager.setLookAndFeel(className);
			SwingUtilities.updateComponentTreeUI(GuiPackage.getInstance().getMainFrame());
		} catch (javax.swing.UnsupportedLookAndFeelException e) {
			JMeterUtils.reportErrorToUser("Look and Feel unavailable:" + e.toString());
		} catch (InstantiationException e) {
			JMeterUtils.reportErrorToUser("Look and Feel unavailable:" + e.toString());
		} catch (ClassNotFoundException e) {
			JMeterUtils.reportErrorToUser("Look and Feel unavailable:" + e.toString());
		} catch (IllegalAccessException e) {
			JMeterUtils.reportErrorToUser("Look and Feel unavailable:" + e.toString());
		}
	}

	public Set getActionNames() {
		return commands;
	}
}
