package com.oreilly;

import org.apache.commons.validator.routines.ISBNValidator;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings({"ConstantConditions", "ResultOfMethodCallIgnored", "Convert2MethodRef", "NewClassNamingConvention", "ExcessiveLambdaUsage"})
public class AssertionsDemo {
    @Test
    void standardAssertions() {
        assertEquals(2, 2);
        assertEquals(4, 2 + 2, "The optional assertion message is now the last parameter.");
        assertTrue('a' < 'b', () -> "Assertion messages can be lazily evaluated -- "
                + "to avoid constructing complex messages unnecessarily.");
    }

    @Test
    void testWithoutSupplier() {
        assertEquals("this is a string",  // expected
                "this is a string",       // test method
                getErrorMessage());       // error message method called even if no error
    }

    @Test
    void testWithSupplierMethod() {
        assertEquals("this is a string",   // expected
                "this is a string",        // test method
                () -> getErrorMessage());  // error message supplier NOT CALLED if no error
        assertTrue(true, () -> "This is a test");
    }

    @Test
    void testWithSupplierMethodReference() {
        assertEquals("this is a string",  // expected
                "this is a string", // test method
                this::getErrorMessage);  // error message supplier NOT CALLED if no error
    }

    private String getErrorMessage() {
        System.out.println("Inside getErrorMessage");
        return "This should never happen";
    }

    private Book findByIsbn(String isbn) {
        if (isbn.equals("149197317X")) return
                new Book("149197317X", "Modern Java Recipes",
                        "Ken Kousen", LocalDate.parse("2017-08-26"));
        else return null;
    }

    // for example, test lots of different properties on an object
    // the series of lambda functions means they will all run even if one fails
    @Test
    void assertAllBook() {
        Book book = findByIsbn("149197317X");
        // assertAll(String header, Executable...)
        assertAll("MJR",
                () -> assertTrue(ISBNValidator.getInstance().isValidISBN10(book.getIsbn())),
                () -> assertEquals("Modern Java Recipes", book.getTitle()),
                () -> assertEquals("Ken Kousen", book.getAuthor()),
                () -> assertTrue(book.getPublicationDate().isBefore(LocalDate.now())));
    }

    @Test
    void assertAllBookWithDependents() {
        Book book = findByIsbn("149197317X");
        assertAll("MJR",
                // ISBN and author name tests always run
                // Null check on title always runs
                () -> ISBNValidator.getInstance().isValidISBN10(book.getIsbn()),
                () -> {
                    // if this fails, then the rest *inside* this lambda function won't run
                    assertNotNull(book.getTitle());

                    // The rest of the block skipped if null title
                    String[] name = book.getTitle().split(" ");
                    assertEquals(3, name.length);

                    // Skipped if title has other than three words
                    assertAll("title words",
                            () -> assertTrue(name[0].startsWith("M")),
                            () -> assertTrue(name[1].startsWith("J")),
                            () -> assertTrue(name[2].startsWith("R")));
                },
                () -> assertEquals("Ken Kousen", book.getAuthor()));
    }

    private void throwException() {
        String[] strings = "".split(" ");
        if (strings.length != 2)
            throw new IllegalArgumentException("Parsing problem");
    }

    // In JUnit 4, this would be @Test(expected=IllegalArgumentException.class) - drawback is that anything else in this test that
    // throws an IllegalArgumentsException would cause the test the pass even if the method in question didn't run but something else threw this exception
    // assertThrows returns the exception
    // JUnit 5's way of handling an expected error being thrown
    @Test
    void exceptionTesting() {
        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> throwException());
        assertEquals("Parsing problem", ex.getMessage());
    }

    // Junit 4: @Test(expected = IndexOutOfBoundsException.class)
    @Test
    void exceptionWithoutMethodReference() {
        List<String> strings = Arrays.asList("this", "is", "a", "list", "of", "strings");
        IndexOutOfBoundsException ex =
                assertThrows(IndexOutOfBoundsException.class, () -> strings.get(-1));
        System.out.println(ex);
        // assertThat(ex.getMessage(), containsString("-1"));
        assertTrue(ex.getMessage().contains("-1"));
    }

    @Test @DisplayName("Arithmetic exception: / by zero")
    void arithmeticExceptionWithInts() {
        int x = 1;
        int y = 0;
        Exception exception = assertThrows(ArithmeticException.class, () -> System.out.println(x / y));
        assertEquals("/ by zero", exception.getMessage());
    }

    @Test @DisplayName("IEEE 754 spec: Floating point / by zero results in INFINITY")
    void noArithmeticExceptionWithDoubles() {
        double x = 1.0;
        double y = 0.0;
        assertDoesNotThrow(() -> System.out.println(x / y));
    }

    @Test
    void timeoutOkay() {
        assertTimeout(Duration.ofMillis(100), () -> System.out.println("Hello"));
    }

    @Test
    @Disabled("Disable until demo")
    // executable can throw an exception, so the Thread.sleep doesn't need to be wrapped in a try-catch block
    void timeoutExceeded() {
        assertTimeout(Duration.ofMillis(100), () -> Thread.sleep(200));
    }

    @Test
    @Disabled("Disable until demo")
    // Executable called in a different thread
    void timeoutExceededWithPreemption() {
        assertTimeoutPreemptively(Duration.ofMillis(100), () -> Thread.sleep(200));
    }
}
