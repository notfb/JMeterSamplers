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
package de.beaglesoft.jmeter;

import org.junit.Assert;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Utility class for unit testing
 *
 * @author Fabian Bieker
 */
public class ReflectionUtil {

    private ReflectionUtil() {
        // hide constructor ...
    }

    public static void setPrivateField(final Object obj, final String name,
                                       final Object value) {
        Assert.assertNotNull(obj);
        Assert.assertNotNull(name);

        try {
            final Field field = obj.getClass().getDeclaredField(name);
            Assert.assertNotNull("field '" + name + "' not found", field);
            field.setAccessible(true);
            field.set(obj, value);
        } catch (final IllegalAccessException ex) {
            Assert.fail("IllegalAccessException accessing " + name);
        } catch (final SecurityException e) {
            Assert.fail("SecurityException accessing " + name);
        } catch (final NoSuchFieldException e) {
            Assert.assertNotNull("field '" + name + "' not found");
        }
    }

    public static Object getPrivateField(final Object obj, final String name) {
        Assert.assertNotNull(obj);
        Assert.assertNotNull(name);

        try {
            final Field field = obj.getClass().getDeclaredField(name);
            Assert.assertNotNull("field '" + name + "' not found", field);
            field.setAccessible(true);
            return field.get(obj);
        } catch (final IllegalAccessException ex) {
            Assert.fail("IllegalAccessException accessing " + name);
        } catch (final SecurityException e) {
            Assert.fail("SecurityException accessing " + name);
        } catch (final NoSuchFieldException e) {
            Assert.fail("field '" + name + "' not found");
        }
        return null;
    }

    // TODO: refactor - for loop success, access by name ...

    public static Object invokePrivateMethod(final Object obj,
                                             final String name, final Object[] params) {
        Assert.assertNotNull(obj);
        Assert.assertNotNull(name);
        Assert.assertNotNull(params);

        // Go and find the private method...
        final Method methods[] = obj.getClass().getDeclaredMethods();
        for (Method method : methods) {
            if (name.equals(method.getName())) {
                try {
                    method.setAccessible(true);
                    return method.invoke(obj, params);
                } catch (final IllegalAccessException ex) {
                    Assert.fail("IllegalAccessException accessing " + name);
                } catch (final InvocationTargetException ite) {
                    Assert.fail("InvocationTargetException accessing " + name);
                }
            }
        }
        Assert.fail("Method '" + name + "' not found");
        return null;
    }

}
