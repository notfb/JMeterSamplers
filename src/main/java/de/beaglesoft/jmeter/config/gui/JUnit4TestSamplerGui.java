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

import de.beaglesoft.jmeter.sampler.JUnit4Sampler;
import org.apache.jmeter.gui.util.VerticalPanel;
import org.apache.jmeter.protocol.java.control.gui.ClassFilter;
import org.apache.jmeter.samplers.gui.AbstractSamplerGui;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.gui.JLabeledTextField;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.jorphan.reflect.ClassFinder;
import org.apache.jorphan.util.JOrphanUtils;
import org.apache.log.Logger;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.ObjectStreamException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * The <code>JUnit4TestSamplerGui</code> class provides the user interface for
 * the {@link JUnit4Sampler}.
 * <p/>
 * <p>
 * Changed to run JUnit 4 tests - largely based on JMeter JUnitSampler.
 * </p>
 * <p/>
 * TODO: thread-safe annotations ...
 *
 * @author Fabian Bieker
 */
public class JUnit4TestSamplerGui extends AbstractSamplerGui implements
        ChangeListener, ActionListener {

    // TODO: make configurable
    private static final String UNIT_TEST_SUBSTR = "Test";

    private static final boolean SEARCH_INNER_CLASSES = false;

    private static final long serialVersionUID = 1L;

    private static final Logger log = LoggingManager.getLoggerForClass();

    /**
     * The name of the classnameCombo JComboBox
     */
    private static final String CLASSNAMECOMBO = "classnamecombo"; //$NON-NLS-1$
    private static final String METHODCOMBO = "methodcombo"; //$NON-NLS-1$
    private static final String PREFIX = "test"; //$NON-NLS-1$

    // Names of JUnit methods
    private static final String ONETIMESETUP = "oneTimeSetUp"; //$NON-NLS-1$
    private static final String ONETIMETEARDOWN = "oneTimeTearDown"; //$NON-NLS-1$
    private static final String SUITE = "suite"; //$NON-NLS-1$

    private static final String[] SPATHS;

    static {
        String paths[];
        final String ucp = JMeterUtils.getProperty("user.classpath");
        if (ucp != null) {
            final String parts[] = ucp.split(File.pathSeparator);
            paths = new String[parts.length + 1];
            paths[0] = JMeterUtils.getJMeterHome() + "/lib/junit/"; //$NON-NLS-1$
            System.arraycopy(parts, 0, paths, 1, parts.length);
        } else {
            paths = new String[]{JMeterUtils.getJMeterHome() + "/lib/junit/" //$NON-NLS-1$
            };
        }
        SPATHS = paths;
    }

    private final JLabel methodLabel =
            new JLabel(
                    JMeterUtils.getResString("junit_test_method") + "(not used atm)"); //$NON-NLS-1$

    private final JLabeledTextField successMsg =
            new JLabeledTextField(JMeterUtils.getResString("junit_success_msg")); //$NON-NLS-1$

    private final JLabeledTextField failureMsg =
            new JLabeledTextField(JMeterUtils.getResString("junit_failure_msg")); //$NON-NLS-1$

    private final JLabeledTextField errorMsg =
            new JLabeledTextField(JMeterUtils.getResString("junit_error_msg")); //$NON-NLS-1$

    private final JLabeledTextField successCode =
            new JLabeledTextField(
                    JMeterUtils.getResString("junit_success_code")); //$NON-NLS-1$

    private final JLabeledTextField failureCode =
            new JLabeledTextField(
                    JMeterUtils.getResString("junit_failure_code")); //$NON-NLS-1$

    private final JLabeledTextField errorCode =
            new JLabeledTextField(JMeterUtils.getResString("junit_error_code")); //$NON-NLS-1$

    private final JLabeledTextField filterpkg =
            new JLabeledTextField(JMeterUtils.getResString("junit_pkg_filter")); //$NON-NLS-1$

    private final JCheckBox appendError =
            new JCheckBox(JMeterUtils.getResString("junit_append_error")); //$NON-NLS-1$
    private final JCheckBox appendExc =
            new JCheckBox(JMeterUtils.getResString("junit_append_exception")); //$NON-NLS-1$

    /**
     * A combo box allowing the user to choose a test class.
     */
    private JComboBox classnameCombo;
    private JComboBox methodName;
    private transient Object testclass = null;
    private List<String> methodList = null;

    private final AtomicReference<ClassFilter> filter =
            new AtomicReference<ClassFilter>();

    private List<Class<?>> classList = null;

    /**
     * Constructor for JUnitTestSamplerGui
     */
    public JUnit4TestSamplerGui() {
        super();
        filter.set(new ClassFilter());
        init();
    }

    public String getLabelResource() {
        // TODO: add own key ...
        return "junit4_request"; //$NON-NLS-1$
    }

    /**
     * Initialize the GUI components and layout.
     */
    private void init() {
        setLayout(new BorderLayout(0, 5));
        setBorder(makeBorder());

        add(makeTitlePanel(), BorderLayout.NORTH);

        add(createClassPanel(), BorderLayout.CENTER);

        initGui();
    }

    private JPanel createClassPanel() {
        methodList = new ArrayList<String>();

        try {

            // find classes ending with containing Test
            // Note: misses some classes when dependencies (.jars etc.)
            // are not found by JMeter
            classList = findClasses();
            if (log.isInfoEnabled()) {
                log.debug("classlist is " + classList);
            }

        } catch (final IOException e) {
            log.error("Exception getting interfaces.", e);
        }

        final JLabel label =
                new JLabel(JMeterUtils.getResString("protocol_java_classname")); //$NON-NLS-1$

        classnameCombo = new JComboBox(classList.toArray());
        classnameCombo.addActionListener(this);
        classnameCombo.setName(CLASSNAMECOMBO);
        classnameCombo.setEditable(false);
        label.setLabelFor(classnameCombo);

        final ClassFilter fi = filter.get();
        if (fi != null && fi.size() > 0) {
            methodName = new JComboBox(fi.filterArray(methodList));
        } else {
            methodName = new JComboBox(methodList.toArray());
        }
        methodName.addActionListener(this);
        methodName.setName(METHODCOMBO);
        methodLabel.setLabelFor(methodName);

        final VerticalPanel panel = new VerticalPanel();
        panel.add(filterpkg);
        panel.add(label);
        filterpkg.addChangeListener(this);

        if (classnameCombo != null) {
            panel.add(classnameCombo);
        }
        panel.add(methodLabel);
        if (methodName != null) {
            panel.add(methodName);
        }
        panel.add(successMsg);
        panel.add(successCode);
        panel.add(failureMsg);
        panel.add(failureCode);
        panel.add(errorMsg);
        panel.add(errorCode);
        panel.add(appendError);
        panel.add(appendExc);

        // apply class filter to gui to ensure it is always used
        // FIXME: wrong place to call it ...
        // filterVisableClasses();

        return panel;
    }

    private void initGui() {
        appendError.setSelected(false);
        appendExc.setSelected(false);
        filterpkg.setText(""); //$NON-NLS-1$
        successCode.setText(JMeterUtils.getResString("junit_success_default_code")); //$NON-NLS-1$
        successMsg.setText(JMeterUtils.getResString("junit_success_default_msg")); //$NON-NLS-1$
        failureCode.setText(JMeterUtils.getResString("junit_failure_default_code")); //$NON-NLS-1$
        failureMsg.setText(JMeterUtils.getResString("junit_failure_default_msg")); //$NON-NLS-1$
        errorMsg.setText(JMeterUtils.getResString("junit_error_default_msg")); //$NON-NLS-1$
        errorCode.setText(JMeterUtils.getResString("junit_error_default_code")); //$NON-NLS-1$
    }

    // just to avoid the unchecked warning on a small scope ...

    @SuppressWarnings("unchecked")
    private List<Class<?>> findClasses() throws IOException {
        return ClassFinder.findClassesThatExtend(SPATHS,
                new Class[]{Object.class}, SEARCH_INNER_CLASSES,
                UNIT_TEST_SUBSTR, null);
    }

    @Override
    public void clearGui() {
        super.clearGui();
        initGui();
    }

    /**
     * Implements JMeterGuiComponent.createTestElement()
     */
    public TestElement createTestElement() {
        final JUnit4Sampler sampler = new JUnit4Sampler();
        modifyTestElement(sampler);
        return sampler;
    }

    /**
     * Implements JMeterGuiComponent.modifyTestElement(TestElement)
     */
    public void modifyTestElement(final TestElement el) {
        final JUnit4Sampler sampler = (JUnit4Sampler) el;
        configureTestElement(sampler);
        if (classnameCombo.getSelectedItem() != null
                && classnameCombo.getSelectedItem() instanceof String) {
            sampler.setClassname((String) classnameCombo.getSelectedItem());
        }
        if (methodName.getSelectedItem() != null) {
            final Object mobj = methodName.getSelectedItem();
            sampler.setMethod((String) mobj);
        }
        sampler.setFilterString(filterpkg.getText());
        sampler.setSuccess(successMsg.getText());
        sampler.setSuccessCode(successCode.getText());
        sampler.setError(errorMsg.getText());
        sampler.setErrorCode(errorCode.getText());
        sampler.setFailure(failureMsg.getText());
        sampler.setFailureCode(failureCode.getText());
        sampler.setAppendError(appendError.isSelected());
        sampler.setAppendException(appendExc.isSelected());
    }

    @Override
    public void configure(final TestElement el) {
        super.configure(el);
        final JUnit4Sampler sampler = (JUnit4Sampler) el;
        classnameCombo.setSelectedItem(sampler.getClassname());
        instantiateClass();
        methodName.setSelectedItem(sampler.getMethod());
        filterpkg.setText(sampler.getFilterString());
        if (sampler.getSuccessCode().length() > 0) {
            successCode.setText(sampler.getSuccessCode());
        } else {
            successCode.setText(JMeterUtils.getResString("junit_success_default_code")); //$NON-NLS-1$
        }
        if (sampler.getSuccess().length() > 0) {
            successMsg.setText(sampler.getSuccess());
        } else {
            successMsg.setText(JMeterUtils.getResString("junit_success_default_msg")); //$NON-NLS-1$
        }
        if (sampler.getFailureCode().length() > 0) {
            failureCode.setText(sampler.getFailureCode());
        } else {
            failureCode.setText(JMeterUtils.getResString("junit_failure_default_code")); //$NON-NLS-1$
        }
        if (sampler.getFailure().length() > 0) {
            failureMsg.setText(sampler.getFailure());
        } else {
            failureMsg.setText(JMeterUtils.getResString("junit_failure_default_msg")); //$NON-NLS-1$
        }
        if (sampler.getError().length() > 0) {
            errorMsg.setText(sampler.getError());
        } else {
            errorMsg.setText(JMeterUtils.getResString("junit_error_default_msg")); //$NON-NLS-1$
        }
        if (sampler.getErrorCode().length() > 0) {
            errorCode.setText(sampler.getErrorCode());
        } else {
            errorCode.setText(JMeterUtils.getResString("junit_error_default_code")); //$NON-NLS-1$
        }
        appendError.setSelected(sampler.getAppendError());
        appendExc.setSelected(sampler.getAppendException());
    }

    public void instantiateClass() {
        final String className = (String) classnameCombo.getSelectedItem();
        if (className != null) {
            testclass = JUnit4Sampler.getClassInstance(className);
            if (testclass == null) {
                clearMethodCombo();
            }
            configureMethodCombo();
        }
    }

    public void showErrorDialog() {
        JOptionPane.showConfirmDialog(
                this,
                JMeterUtils.getResString("junit_constructor_error"), //$NON-NLS-1$
                "Warning", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.ERROR_MESSAGE);
    }

    public void configureMethodCombo() {
        if (testclass != null) {
            clearMethodCombo();
            final String[] names =
                    getMethodNames(getMethods(testclass, methodList));
            for (final String name2 : names) {
                methodName.addItem(name2);
                methodList.add(name2);
            }
            methodName.repaint();
        }
    }

    public void clearMethodCombo() {
        methodName.removeAllItems();
        methodList.clear();
    }

    public Method[] getMethods(final Object obj, final List<String> list) {
        final Method[] meths = obj.getClass().getMethods();
        final List<Method> newList = new ArrayList<Method>();
        for (final Method meth : meths) {
            if (meth.getName().startsWith(PREFIX)
                    || meth.getName().equals(ONETIMESETUP)
                    || meth.getName().equals(ONETIMETEARDOWN)
                    || meth.getName().equals(SUITE)) {
                newList.add(meth);
            }
        }
        if (list.size() > 0) {
            final Method[] rmeth = new Method[list.size()];
            return list.toArray(rmeth);
        }
        return new Method[0];
    }

    public String[] getMethodNames(final Method[] meths) {
        final String[] names = new String[meths.length];
        for (int idx = 0; idx < meths.length; idx++) {
            names[idx] = meths[idx].getName();
        }
        return names;
    }

    /**
     * Handle action events for this component. This method currently handles
     * events for the classname combo box.
     *
     * @param evt the ActionEvent to be handled
     */
    public void actionPerformed(final ActionEvent evt) {
        if (evt.getSource() == classnameCombo) {
            instantiateClass();
        }
    }

    /**
     * the current implementation checks to see if the source of the event is
     * the filterpkg field.
     */
    public void stateChanged(final ChangeEvent event) {
        if (event.getSource() == filterpkg) {
            filterVisableClasses();
        }
    }

    // FIXME: NOT Thread-safe!

    private void filterVisableClasses() {
        final ClassFilter fi = filter.get();
        fi.setPackges(JOrphanUtils.split(filterpkg.getText(), ",")); //$NON-NLS-1$
        classnameCombo.removeAllItems();
        // change the classname drop down
        final Object[] clist = fi.filterArray(classList);
        for (final Object element : clist) {
            classnameCombo.addItem(element);
        }
    }

    private Object writeReplace() throws ObjectStreamException {
        filter.set(null);
        return this;
    }

    private Object readResolve() throws ObjectStreamException {
        final ClassFilter fi = new ClassFilter();
        fi.setPackges(JOrphanUtils.split(filterpkg.getText(), ","));
        filter.set(fi);
		return this;
	}

}
