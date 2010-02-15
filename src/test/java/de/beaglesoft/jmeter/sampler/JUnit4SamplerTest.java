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
package de.beaglesoft.jmeter.sampler;

import org.apache.jmeter.samplers.Entry;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Fabian Bieker
 */
public class JUnit4SamplerTest {

    private static final String CHECK_STRING = "CHECK_STRING";

    /**
     * @author Fabian Bieker (fbi)
     *         <p/>
     *         simple testcases to use for unit testing JUnit4Sampler
     */
    public static class SimpleTest {

        static final Logger log = LoggingManager.getLoggerForClass();

        boolean testRan = false;

        @Before
        public void setUp() throws Exception {
            log.debug("setUp()");
        }

        @After
        public void tearDown() throws Exception {
            log.debug("tearDown()");
        }

        @Test
        public void test() {
            log.debug("test()");
            testRan = true;
        }
    }

    /**
     * simple test case that fails ...
     *
     * @author Fabian Bieker (fbi)
     */
    public static class SimpleTestFailure extends SimpleTest {

        @Test
        public void testFails() throws Exception {
            log.debug("testFails()");
            fail("this should fail!");
        }
    }

    public static class SimpleTestError extends SimpleTest {

        @Test
        public void testError() throws Exception {
            log.debug("testError()");
            throw new Error("should throw this for testing ...");
        }
    }

    private JUnit4Sampler s;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        s = new JUnit4Sampler();
    }

    /**
     * Test method for
     * {@link de.beaglesoft.jmeter.sampler.JUnit4Sampler#getClassInstance(java.lang.String)}
     * .
     */
    @Test
    public void testGetClassInstance() {
        final Object obj =
                JUnit4Sampler.getClassInstance(SimpleTest.class.getName());
        assertNotNull(obj);
        assertTrue(obj instanceof SimpleTest);

        assertNull(JUnit4Sampler.getClassInstance(null));
        assertNull(JUnit4Sampler.getClassInstance(""));
        assertNull(JUnit4Sampler.getClassInstance("INVALID CLASS NAME"));

        // no public constructor
        assertNull(JUnit4Sampler.getClassInstance("de.beaglesoft.jmeter.ReflectionUtil"));
    }

    /**
     * Test method for
     * {@link de.beaglesoft.jmeter.sampler.JUnit4Sampler#sample(org.apache.jmeter.samplers.Entry)}
     * .
     */
    @Test
    public void testSampleSimple() {
        s.setClassname(SimpleTest.class.getName());
        assertEquals(SimpleTest.class.getName(), s.getClassname());

        SampleResult res = s.sample(new Entry());
        assertNotNull(res);
        assertTrue(res.isSuccessful());

        s.setClassname(SimpleTestFailure.class.getName());
        assertEquals(SimpleTestFailure.class.getName(), s.getClassname());

        res = s.sample(new Entry());
        assertNotNull(res);
        assertFalse(res.isSuccessful());
    }

    /**
     * Test method for
     * {@link de.beaglesoft.jmeter.sampler.JUnit4Sampler#getFailure()}. Test
     * method for
     * {@link de.beaglesoft.jmeter.sampler.JUnit4Sampler#setFailure(java.lang.String)}
     * .
     */
    @Test
    public void testSetGetFailure() {
        s.setFailure(CHECK_STRING);
        assertEquals(CHECK_STRING, s.getFailure());

        s.setClassname(SimpleTestFailure.class.getName());
        SampleResult res = s.sample(new Entry());
        assertNotNull(res);
        assertEquals(CHECK_STRING, res.getResponseMessage());

        s.setClassname(SimpleTest.class.getName());
        res = s.sample(new Entry());
        assertNotNull(res);
        assertNotSame(CHECK_STRING, res.getResponseMessage());

        // s.setClassname( SimpleTestError.class.getName());
        // res = s.sample( new Entry());
        // assertNotNull( res);
        // assertNotSame( CHECK_STRING, res.getResponseMessage());

    }

    @Test
    public void testSetGetSuccess() {
        s.setSuccess(CHECK_STRING);
        assertEquals(CHECK_STRING, s.getSuccess());

        s.setClassname(SimpleTest.class.getName());
        SampleResult res = s.sample(new Entry());
        assertNotNull(res);
        assertEquals(CHECK_STRING, res.getResponseMessage());

        s.setClassname(SimpleTestFailure.class.getName());
        res = s.sample(new Entry());
        assertNotNull(res);
        assertNotSame(CHECK_STRING, res.getResponseMessage());

        // s.setClassname( SimpleTestError.class.getName());
        // res = s.sample( new Entry());
        // assertNotNull( res);
        // assertNotSame( CHECK_STRING, res.getResponseMessage());

    }

    @Test
    public void testSetGetFailureCode() throws Exception {
        s.setFailureCode(CHECK_STRING);
        assertEquals(CHECK_STRING, s.getFailureCode());

        s.setClassname(SimpleTestFailure.class.getName());
        SampleResult res = s.sample(new Entry());
        assertNotNull(res);
        assertEquals(CHECK_STRING, res.getResponseCode());

        s.setClassname(SimpleTest.class.getName());
        res = s.sample(new Entry());
        assertNotNull(res);
        assertNotSame(CHECK_STRING, res.getResponseCode());

        // s.setClassname( SimpleTestError.class.getName());
        // res = s.sample( new Entry());
        // assertNotNull( res);
        // assertNotSame( CHECK_STRING, res.getResponseCode());
    }

    /**
     * Test method for
     * {@link de.beaglesoft.jmeter.sampler.JUnit4Sampler#getAppendError()} .
     * Test method for
     * {@link de.beaglesoft.jmeter.sampler.JUnit4Sampler#setAppendError(boolean)}
     * .
     */
    @Test
    public void testSetGetAppendError() {
        s.setSuccess(CHECK_STRING);
        s.setClassname(SimpleTestFailure.class.getName());

        s.setAppendError(false);
        assertFalse(s.getAppendError());

        SampleResult res = s.sample(new Entry());
        assertNotNull(res);
        assertFalse(res.getResponseDataAsString().contains("Failure: msg:"));

        s.setAppendError(true);
        assertTrue(s.getAppendError());

        res = s.sample(new Entry());
        assertNotNull(res);
        System.out.println(res.getResponseDataAsString());
        assertTrue(res.getResponseDataAsString().contains("Failure: msg:"));
    }

    /**
     * Test method for
     * {@link de.beaglesoft.jmeter.sampler.JUnit4Sampler#getAppendException()} .
     * Test method for
     * {@link de.beaglesoft.jmeter.sampler.JUnit4Sampler#setAppendException(boolean)}
     * .
     */
    @Test
    public void testSetGetAppendException() {
        s.setSuccess(CHECK_STRING);
        s.setClassname(SimpleTestFailure.class.getName());
        s.setAppendError(true);
        assertTrue(s.getAppendError());

        s.setAppendException(false);
        assertFalse(s.getAppendException());

        SampleResult res = s.sample(new Entry());
        assertNotNull(res);
        assertFalse(res.getResponseDataAsString().toLowerCase().contains(
                "stacktrace:"));

        s.setAppendException(true);
        assertTrue(s.getAppendException());

        res = s.sample(new Entry());
        assertNotNull(res);
        assertTrue(res.getResponseDataAsString().toLowerCase().contains(
                "stacktrace:"));
	}

}
