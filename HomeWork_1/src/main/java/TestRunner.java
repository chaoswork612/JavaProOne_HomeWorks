import annotations.*;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

public class TestRunner extends ClassLoader {
    public static void main(String[] args) throws ReflectiveOperationException {
        TestRunner.runTests(FirstTestClass.class);
    }

    public static void runTests(Class<?> c) throws ReflectiveOperationException {
        Object testObject = c.getDeclaredConstructor().newInstance();
        var methods = c.getDeclaredMethods();
        AtomicInteger beforeSuiteCount = new AtomicInteger();
        AtomicInteger afterSuiteCount = new AtomicInteger();
        AtomicReference<Method> beforeSuiteMethod = new AtomicReference<>();
        AtomicReference<Method> afterSuiteMethod = new AtomicReference<>();
        AtomicReference<Method> beforeTestMethod = new AtomicReference<>();
        AtomicReference<Method> afterTestMethod = new AtomicReference<>();
        List<Method> testMethods = new ArrayList<>();
        Arrays.stream(methods).forEach(method -> {
            if (method.isAnnotationPresent(BeforeSuite.class)) {
                beforeSuiteCount.getAndIncrement();
                if (Modifier.isStatic(method.getModifiers())) {
                    throw new RuntimeException(String.format("%s is applied to a non-static method: %s\n",
                            method.getDeclaredAnnotation(BeforeSuite.class).annotationType().getName(),
                            method.getName())
                    );
                } else if (beforeSuiteCount.get() > 1) {
                    throw new RuntimeException(String.format("%s annotation applied %s times\n",
                            method.getDeclaredAnnotation(BeforeSuite.class).annotationType().getName(),
                            beforeSuiteCount.get())
                    );
                }
                beforeSuiteMethod.set(method);
            } else if (method.isAnnotationPresent(AfterSuite.class)) {
                afterSuiteCount.getAndIncrement();
                if (!Modifier.isStatic(method.getModifiers())) {
                    throw new RuntimeException(String.format("%s is applied to a non-static method: %s\n",
                            method.getDeclaredAnnotation(AfterSuite.class), method.getName())
                    );
                } else if (afterSuiteCount.get() > 1) {
                    throw new RuntimeException(String.format("%s annotation applied %s times\n",
                            method.getDeclaredAnnotation(AfterSuite.class).annotationType().getName(),
                            afterSuiteCount.get())
                    );
                }
                afterSuiteMethod.set(method);
            } else if (method.isAnnotationPresent(Test.class)) {
                if (Modifier.isStatic(method.getModifiers())) {
                    throw new RuntimeException(String.format("%s is applied to a static method: %s\n",
                            method.getDeclaredAnnotation(Test.class), method.getName())
                    );
                }
                Test testAnnotation = method.getAnnotation(Test.class);
                int priority = testAnnotation.priority();
                if (priority < 1 || priority > 10) throw new RuntimeException("Priority must be between 1 and 10");
                testMethods.add(method);
            } else if (method.isAnnotationPresent(BeforeTest.class)) {
                if (Modifier.isStatic(method.getModifiers())) {
                    throw new RuntimeException(String.format("%s is applied to a static method: %s\n",
                            method.getDeclaredAnnotation(BeforeTest.class), method.getName())
                    );
                }
                beforeTestMethod.set(method);
            } else if (method.isAnnotationPresent(AfterTest.class)) {
                if (Modifier.isStatic(method.getModifiers())) {
                    throw new RuntimeException(String.format("%s is applied to a static method: %s\n",
                            method.getDeclaredAnnotation(AfterTest.class), method.getName())
                    );
                }
                afterTestMethod.set(method);
            }
        });

        if (beforeSuiteMethod.get() != null) {
            try {
                beforeSuiteMethod.get().invoke(testObject);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }

        if (!testMethods.isEmpty()) {
            testMethods.stream().sorted((m1, m2) -> {
                int p1 = m1.getAnnotation(Test.class).priority();
                int p2 = m2.getAnnotation(Test.class).priority();
                return Integer.compare(p2, p1);
            }).toList().forEach(testMethod -> {
                if (beforeTestMethod.get() != null) {
                    try {
                        beforeTestMethod.get().invoke(testObject);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                }
                CsvSource csvSource = testMethod.getAnnotation(CsvSource.class);
                if (csvSource != null) {
                    String[] values = csvSource.value().split(",");
                    Class<?>[] parameterTypes = testMethod.getParameterTypes();
                    List<Serializable> parameters = new ArrayList<>();
                    IntStream.range(0, values.length).forEach(i -> parameters.add(convertStringToType(values[i].trim(), parameterTypes[i])));
                    try {
                        testMethod.invoke(testObject, parameters.toArray());
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    try {
                        testMethod.invoke(testObject);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                }
                if (afterTestMethod.get() != null) {
                    try {
                        afterTestMethod.get().invoke(testObject);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }

        if (afterSuiteMethod.get() != null) {
            try {
                afterSuiteMethod.get().invoke(testObject);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static Serializable convertStringToType(String value, Class<?> type) {
        if (type == int.class || type == Integer.class) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Cannot convert '" + value + "' to int", e);
            }
        } else if (type == String.class) {
            return value;
        } else if (type == boolean.class || type == Boolean.class) {
            try {
                return Boolean.parseBoolean(value);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Cannot convert '" + value + "' to boolean", e);
            }
        }
        throw new IllegalArgumentException("Unsupported type: " + type);
    }
}
