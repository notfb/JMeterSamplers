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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import org.apache.jmeter.samplers.AbstractSampler;
import org.apache.jmeter.samplers.Entry;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

/**
 * 
 * This is a basic implementation that runs a single test method of a JUnit test
 * case. The current implementation will use the string constructor first. If
 * the test class does not declare a string constructor, the sampler will try
 * empty constructor.
 * 
 * <p>
 * Changed to run JUnit 4 tests - largely based on JMeter JUnitSampler.
 * </p>
 * 
 * TODO: fix concurrency + thread-safe annotations ... <br/>
 * 
 * TODO: add resource strings (sampler name etc.) <br/>
 * jmeter-trunk$ find ./ -iname '*resources*'
 * 
 * TODO: run just one test method ... <br/>
 * 
 * TODO: optimize <br/>
 * 
 * @author Fabian Bieker (BeagleSoft GmbH)
 */
public class JUnit4Sampler extends AbstractSampler {

	private static final long serialVersionUID = 1L;

	/**
	 * Property key representing the classname of the JavaSamplerClient to user.
	 */
	public static final String CLASSNAME = "junit4sampler.classname";
	public static final String METHOD = "junit4sampler.method";
	public static final String ERROR = "junit4sampler.error";
	public static final String ERRORCODE = "junit4sampler.error.code";
	public static final String FAILURE = "junit4sampler.failure";
	public static final String FAILURECODE = "junit4sampler.failure.code";
	public static final String SUCCESS = "junit4sampler.success";
	public static final String SUCCESSCODE = "junit4sampler.success.code";
	public static final String FILTER = "junit4sampler.pkg.filter";
	public static final String APPEND_ERROR = "junit4sampler.append.error";
	public static final String APPEND_EXCEPTION =
			"junit4sampler.append.exception";

	protected transient TestCase TEST_INSTANCE = null;

	/**
	 * Logging
	 */
	private static final Logger log = LoggingManager.getLoggerForClass();

	public JUnit4Sampler() {
		// nothing to do here ...
	}

	/**
	 * Sets the Classname attribute of the JavaConfig object
	 * 
	 * @param classname the new Classname value
	 */
	public void setClassname(final String classname) {
		setProperty(CLASSNAME, classname);
	}

	/**
	 * Gets the Classname attribute of the JavaConfig object
	 * 
	 * @return the Classname value
	 */
	public String getClassname() {
		return getPropertyAsString(CLASSNAME);
	}

	/**
	 * Return the name of the method to test
	 */
	public String getMethod() {
		return getPropertyAsString(METHOD);
	}

	/**
	 * Method should add the JUnit testXXX method to the list at the end, since
	 * the sequence matters.
	 * 
	 * @param methodName
	 */
	public void setMethod(final String methodName) {
		setProperty(METHOD, methodName);
	}

	/**
	 * get the success message
	 */
	public String getSuccess() {
		return getPropertyAsString(SUCCESS);
	}

	/**
	 * set the success message
	 * 
	 * @param success
	 */
	public void setSuccess(final String success) {
		setProperty(SUCCESS, success);
	}

	/**
	 * get the success code defined by the user
	 */
	public String getSuccessCode() {
		return getPropertyAsString(SUCCESSCODE);
	}

	/**
	 * set the succes code. the success code should be unique.
	 * 
	 * @param code
	 */
	public void setSuccessCode(final String code) {
		setProperty(SUCCESSCODE, code);
	}

	/**
	 * get the failure message
	 */
	public String getFailure() {
		return getPropertyAsString(FAILURE);
	}

	/**
	 * set the failure message
	 * 
	 * @param fail
	 */
	public void setFailure(final String fail) {
		setProperty(FAILURE, fail);
	}

	/**
	 * The failure code is used by other components
	 */
	public String getFailureCode() {
		return getPropertyAsString(FAILURECODE);
	}

	/**
	 * Provide some unique code to denote a type of failure
	 * 
	 * @param code
	 */
	public void setFailureCode(final String code) {
		setProperty(FAILURECODE, code);
	}

	/**
	 * return the descriptive error for the test
	 */
	public String getError() {
		return getPropertyAsString(ERROR);
	}

	/**
	 * provide a descriptive error for the test method. For a description of the
	 * difference between failure and error, please refer to the following url
	 * http://junit.sourceforge.net/doc/faq/faq.htm#tests_9
	 * 
	 * @param error
	 */
	public void setError(final String error) {
		setProperty(ERROR, error);
	}

	/**
	 * return the error code for the test method. it should be an unique error
	 * code.
	 */
	public String getErrorCode() {
		return getPropertyAsString(ERRORCODE);
	}

	/**
	 * provide an unique error code for when the test does not pass the assert
	 * test.
	 * 
	 * @param code
	 */
	public void setErrorCode(final String code) {
		setProperty(ERRORCODE, code);
	}

	/**
	 * return the comma separated string for the filter
	 */
	public String getFilterString() {
		return getPropertyAsString(FILTER);
	}

	/**
	 * set the filter string in comman separated format
	 * 
	 * @param text
	 */
	public void setFilterString(final String text) {
		setProperty(FILTER, text);
	}

	/**
	 * If append error is not set, by default it is set to false, which means
	 * users have to explicitly set the sampler to append the assert errors.
	 * Because of how junit works, there should only be one error
	 */
	public boolean getAppendError() {
		return getPropertyAsBoolean(APPEND_ERROR, false);
	}

	public void setAppendError(final boolean error) {
		setProperty(APPEND_ERROR, String.valueOf(error));
	}

	/**
	 * If append exception is not set, by default it is set to false. Users have
	 * to explicitly set it to true to see the exceptions in the result tree.
	 */
	public boolean getAppendException() {
		return getPropertyAsBoolean(APPEND_EXCEPTION, false);
	}

	public void setAppendException(final boolean exc) {
		setProperty(APPEND_EXCEPTION, String.valueOf(exc));
	}

	/**
	 * @see org.apache.jmeter.samplers.Sampler#sample(org.apache.jmeter.samplers.Entry)
	 */
	public SampleResult sample(final Entry entry) {
		final SampleResult sresult = new SampleResult();
		sresult.setSuccessful(false);
		sresult.setDataType(SampleResult.TEXT);

		if (getClassname() == null) {
			sresult.setResponseMessage("Can not sample - no classname set!");
			return sresult;
		}

		final Result tr;

		sresult.setSampleLabel(getName());
		sresult.setSamplerData(getClassname() + "." + getMethod());

		// TODO: optimize
		final Object testInstance = getClassInstance(getClassname());
		if (testInstance == null) {
			sresult.setResponseCode("failed to init class instance, check logfile ...");
			return sresult;
		}

		try {
			// final TestSuite ts = new TestSuite();
			// ts.runTest( testCase, tr);
			// TODO: run just one test method ...
			// TODO: use junit timing?
			sresult.sampleStart();
			tr = JUnitCore.runClasses(testInstance.getClass());
		} catch (final RuntimeException e) {
			log.warn("exectution of junit test failed", e);
			sresult.setResponseCode(getErrorCode());
			sresult.setResponseMessage(getError());
			if (getAppendException()) {
				sresult.setResponseData(String.format("%s - %s \n%s",
						e.getClass().getName(), e.getMessage(),
						Arrays.toString(e.getStackTrace())).getBytes());
			}
			return sresult;
		} finally {
			sresult.sampleEnd();
		}

		if (tr == null) {
			log.error("junit4 test result was null!");
			sresult.setResponseCode("-1"); // TODO:
			sresult.setResponseMessage("test result was null");
		} else if (tr.wasSuccessful()) {
			log.debug(String.format(
					"junit4 test successfull! time: %sms testcount: %s",
					tr.getRunTime(), tr.getRunCount()));
			sresult.setSuccessful(true);
			sresult.setResponseMessage(getSuccess());
			sresult.setResponseCode(getSuccessCode());
			sresult.setResponseData(String.format("%s testcount: %s",
					getSuccess(), tr.getRunCount()).getBytes());
		} else {
			// test failed, append failure traces etc.
			log.debug(String.format(
					"junit4 test failed! time: %sms testcount: %s failures %s",
					tr.getRunTime(), tr.getRunCount(), tr.getFailureCount()));

			final StringBuffer buf = new StringBuffer();
			buf.append(getFailure());
			buf.append(" testcount: ").append(tr.getRunCount());
			buf.append(" failures: ").append(tr.getFailureCount());
			buf.append("\n");
			if (getAppendError()) {
				final List<Failure> failures = tr.getFailures();
				for (final Failure f : failures) {
					buf.append("\nFailure: msg: ").append(f.getMessage());
					buf.append(" descr.: ").append(f.getDescription());
					buf.append(" header: ").append(f.getTestHeader());
					if (getAppendException()) {
						buf.append("\nstacktrace: ");
						buf.append(f.getTrace());
						buf.append("\n");
					}
				}
			}

			sresult.setResponseData(buf.toString().getBytes());
			sresult.setResponseMessage(getFailure());
			sresult.setResponseCode(getFailureCode());
		}

		return sresult;
	}

	/**
	 * 
	 * @param clazz
	 * @param method
	 * @return the method or null if an error occurred
	 */
	@SuppressWarnings("unused")
	private Method getMethod(final Class<?> clazz, final String method) {
		if (clazz != null && method != null) {
			// log.info("class " + clazz.getClass().getName() +
			// " method name is " + method);
			try {
				return clazz.getMethod(method, new Class[0]);
			} catch (final NoSuchMethodException e) {
				log.warn(e.getMessage());
			}
		}
		return null;
	}

	public static Object getClassInstance(final String className) {

		if (className == null || "".equals(className.trim())) {
			log.info("getClassInstance called with empty / null string - ignoreing ...");
			return null;
		}

		try {
			final Class<?> clazz = Class.forName(className);
			return clazz.getConstructor(new Class[0]).newInstance(new Object[0]);
		} catch (final RuntimeException e) {
			log.warn("failed to create class instance ", e);
		} catch (final ClassNotFoundException e) {
			log.warn("class not found ", e);
		} catch (final NoSuchMethodException e) {
			log.warn("constuctor not found ", e);
		} catch (final InstantiationException e) {
			log.warn("constructor call failed ", e);
		} catch (final IllegalAccessException e) {
			log.warn("constructor call failed ", e);
		} catch (final InvocationTargetException e) {
			log.warn("constructor call failed ", e);
		}

		// caller handles null
		return null;
	}

}
