package com.oreilly;

import org.junit.jupiter.api.*;

import java.lang.reflect.Method;
import java.util.Optional;

// OR:
// @TestInstance(TestInstance.Lifecycle.PER_METHOD)
// @AfterAll method 'void com.oreilly.LifeCycleTest.afterAll()' must be static unless the test class is annotated with @TestInstance(Lifecycle.PER_CLASS)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LifeCycleTest {
    public LifeCycleTest() {
        System.out.println("Constructor");
    }

    @BeforeAll
    static void init() {
        System.out.println("BeforeAll");
    }

    @BeforeEach
    void beforeEach() {
        System.out.println("BeforeEach");
    }

    // todo: note the display name instead of the long function names
    @Test @DisplayName("A test with arguments")
    protected void test1(TestInfo info, TestReporter reporter) {
        reporter.publishEntry("test1", info.getDisplayName());
        Optional<Class<?>> testClass = info.getTestClass();
        Optional<Method> testMethod = info.getTestMethod();
        if (testClass.isPresent() && testMethod.isPresent()) {
            System.out.println(testMethod.get() +
                    " in " + testClass.get());
        }
    }

    @Test
    public void test2() {
        System.out.println("test2");
    }

    @Test
    void test3() {
        System.out.println("test3");
    }

    @AfterEach
    void afterEach() {
        System.out.println("AfterEach");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("AfterAll");
    }
}
