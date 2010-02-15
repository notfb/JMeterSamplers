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
package de.beaglesoft.jmeter.config.gui;

import de.beaglesoft.jmeter.ReflectionUtil;
import de.beaglesoft.jmeter.sampler.JUnit4Sampler;
import org.apache.jmeter.junit.JMeterTestCase;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jorphan.gui.JLabeledTextField;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;
import org.junit.Before;
import org.junit.Test;

import javax.swing.*;

/**
 * @author Fabian Bieker
 */
public class JUnit4TestSamplerGuiTest extends JMeterTestCase {

    protected static final Logger log = LoggingManager.getLoggerForClass();
    private JUnit4Sampler s;
    private JUnit4TestSamplerGui gui;

    /**
     * @throws java.lang.Exception
     */
    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        gui = new JUnit4TestSamplerGui();
    }

    private JUnit4Sampler mkTestElement() {
        final TestElement e = gui.createTestElement();
        assertNotNull(e);
        assertTrue(e instanceof JUnit4Sampler);
        return (JUnit4Sampler) e;
    }

    /**
     * Test method for
     * {@link de.beaglesoft.jmeter.config.gui.JUnit4TestSamplerGui#instantiateClass()}
     * .
     */
    @Test
    public void testInstantiateClass() {

        // fails, but should not throw an exception ...
        gui.instantiateClass();

        // TODO: more testing ...

    }

    /**
     * Test method for
     * {@link de.beaglesoft.jmeter.config.gui.JUnit4TestSamplerGui#createTestElement()}
     * .
     */
    @Test
    public void testCreateTestElement() {
        final TestElement e = gui.createTestElement();
        assertNotNull(e);
        assertTrue(e instanceof JUnit4Sampler);
    }

    /**
     * Test method for
     * {@link de.beaglesoft.jmeter.config.gui.JUnit4TestSamplerGui#modifyTestElement(org.apache.jmeter.testelement.TestElement)}
     * .
     */
    @Test
    public void testModifyTestElement() {

        s = mkTestElement();

        // TODO: verify defaults?

        ((JLabeledTextField) ReflectionUtil.getPrivateField(gui, "successMsg")).setText("successMsgValue");
        ((JLabeledTextField) ReflectionUtil.getPrivateField(gui, "successCode")).setText("21");
        gui.modifyTestElement(s);
        assertEquals("successMsgValue", s.getSuccess());
        assertEquals("21", s.getSuccessCode());

        ((JLabeledTextField) ReflectionUtil.getPrivateField(gui, "errorMsg")).setText("errorMsgValue");
        ((JLabeledTextField) ReflectionUtil.getPrivateField(gui, "errorCode")).setText("22");
        gui.modifyTestElement(s);
        assertEquals("errorMsgValue", s.getError());
        assertEquals("22", s.getErrorCode());

        ((JLabeledTextField) ReflectionUtil.getPrivateField(gui, "failureMsg")).setText("failureMsgValue");
        ((JLabeledTextField) ReflectionUtil.getPrivateField(gui, "failureCode")).setText("23");
        gui.modifyTestElement(s);
        assertEquals("failureMsgValue", s.getFailure());
        assertEquals("23", s.getFailureCode());

        ((JCheckBox) ReflectionUtil.getPrivateField(gui, "appendError")).setSelected(false);
        ((JCheckBox) ReflectionUtil.getPrivateField(gui, "appendExc")).setSelected(false);
        gui.modifyTestElement(s);
        assertFalse(s.getAppendError());
        assertFalse(s.getAppendException());

        ((JCheckBox) ReflectionUtil.getPrivateField(gui, "appendError")).setSelected(true);
        ((JCheckBox) ReflectionUtil.getPrivateField(gui, "appendExc")).setSelected(true);
        gui.modifyTestElement(s);
        assertTrue(s.getAppendError());
        assertTrue(s.getAppendException());

        final JComboBox classComboBox =
                (JComboBox) ReflectionUtil.getPrivateField(gui,
                        "classnameCombo");
        classComboBox.addItem("classNameVal");
        classComboBox.setSelectedItem("classNameVal");
        gui.modifyTestElement(s);
        assertEquals("classNameVal", s.getClassname());

    }

    /**
     * Test method for
     * {@link de.beaglesoft.jmeter.config.gui.JUnit4TestSamplerGui#configure(org.apache.jmeter.testelement.TestElement)}
     * .
     */
    @Test
    public void testConfigureTestElement() {
        final TestElement e = mkTestElement();
        s = (JUnit4Sampler) e;

        final JComboBox classComboBox =
                (JComboBox) ReflectionUtil.getPrivateField(gui,
                        "classnameCombo");

        classComboBox.addItem("classname");

        s.setAppendError(true);
        s.setAppendException(true);
        s.setClassname("classname");
        s.setError("error");
        s.setErrorCode("23");
        s.setSuccess("succ");
        s.setSuccessCode("0");
        s.setFailure("fail");
        s.setFailureCode("42");

        gui.configure(s);

        assertEquals("error",
                ((JLabeledTextField) ReflectionUtil.getPrivateField(gui,
                        "errorMsg")).getText());
        assertEquals("succ",
                ((JLabeledTextField) ReflectionUtil.getPrivateField(gui,
                        "successMsg")).getText());
        assertEquals("fail",
                ((JLabeledTextField) ReflectionUtil.getPrivateField(gui,
                        "failureMsg")).getText());
        assertEquals("23", ((JLabeledTextField) ReflectionUtil.getPrivateField(
                gui, "errorCode")).getText());
        assertEquals("0", ((JLabeledTextField) ReflectionUtil.getPrivateField(
                gui, "successCode")).getText());
        assertEquals("42", ((JLabeledTextField) ReflectionUtil.getPrivateField(
                gui, "failureCode")).getText());

        assertTrue(((JCheckBox) ReflectionUtil.getPrivateField(gui,
                "appendError")).isSelected());
        assertTrue(((JCheckBox) ReflectionUtil.getPrivateField(gui, "appendExc")).isSelected());

        assertEquals("classname", classComboBox.getSelectedItem());

        // TODO: test some more cases ...
    }

// FIXME: runs as a junit3 test ...    
//	@Test
//    @Ignore("TODO: Fails on OSX 10.6.2 - java.io.NotSerializableException: com.apple.laf.AquaComboBoxUI")
//	public void testSerialize() throws Exception {
//
//
//		final File tmpFile =
//				File.createTempFile(this.getClass().getSimpleName(), ".tmp");
//		ObjectOutput sout = null;
//		ObjectInput sin = null;
//
//		try {
//
//			final FileOutputStream f = new FileOutputStream(tmpFile);
//			sout = new ObjectOutputStream(f);
//			sout.writeObject(gui);
//			sout.flush();
//
//			final FileInputStream in = new FileInputStream(tmpFile);
//			sin = new ObjectInputStream(in);
//			final JUnit4TestSamplerGui newGui =
//					(JUnit4TestSamplerGui) sin.readObject();
//
//			// should generate equal test elements ...
//			assertEquals(gui.createTestElement(), newGui.createTestElement());
//
//		} finally {
//			assertTrue(tmpFile.delete());
//			if (sout != null) {
//				sout.close();
//			}
//			if (sin != null) {
//				sin.close();
//			}
//		}
//	}

    @Test
    public void testClearGui() {

        // basic test to check if something fails ...
        gui.clearGui();
        s = mkTestElement();
        gui.configure(s);
    }

    // /**
    // * Test method for
    // * {@link
    // de.beaglesoft.jmeter.config.gui.JUnit4TestSamplerGui#configureMethodCombo()}
    // * .
    // */
    // @Test
    // public void testConfigureMethodCombo() {
    // // TODO: impl. in gui / sampler and test ...
    // }
    //
    // /**
    // * Test method for
    // * {@link
    // de.beaglesoft.jmeter.config.gui.JUnit4TestSamplerGui#clearMethodCombo()}
    // * .
    // */
    // @Test
    // public void testClearMethodCombo() {
    // // TODO: impl. in gui / sampler and test ...
    // }
    //
    // /**
    // * Test method for
    // * {@link
    // de.beaglesoft.jmeter.config.gui.JUnit4TestSamplerGui#getMethods(java.lang.Object,
    // java.util.List)}
    // * .
    // */
    // @Test
    // public void testGetMethods() {
    // // TODO: impl test
    // }
    //
    // /**
    // * Test method for
    // * {@link
    // de.beaglesoft.jmeter.config.gui.JUnit4TestSamplerGui#getMethodNames(java.lang.reflect.Method[])}
    // * .
    // */
    // @Test
    // public void testGetMethodNames() {
    // // TODO: impl test
    // }
    //
    // /**
    // * Test method for
    // * {@link
    // de.beaglesoft.jmeter.config.gui.JUnit4TestSamplerGui#clearGui()}.
    // */
    // @Test
    // public void testClearGui() {
    // // TODO: impl test
    // }
    //
    // /**
    // * Test method for
    // * {@link
    // de.beaglesoft.jmeter.config.gui.JUnit4TestSamplerGui#actionPerformed(java.awt.event.ActionEvent)}
    // * .
    // */
    // @Test
    // public void testActionPerformed() {
    // // TODO: impl test
    // }
    //
    // /**
    // * Test method for
    // * {@link
    // de.beaglesoft.jmeter.config.gui.JUnit4TestSamplerGui#stateChanged(javax.swing.event.ChangeEvent)}
    // * .
    // */
    // @Test
    // public void testStateChanged() {
    // // TODO: impl test
    // }

}
