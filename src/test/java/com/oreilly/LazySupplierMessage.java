package com.oreilly;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class LazySupplierMessage {

	private String getErrorMessage() {
		System.out.println("Inside getErrorMessage");
		return "Here's an error message";
	}


	// see AssertionsDemo for this same example, with other comments
	// lambda becomes a supplier
	// if the creation of the error message is expensive because it has to be constructed or something, then this is useful
	@Test
	void showError() {
		boolean x = false;
		Assertions.assertTrue(x, () -> getErrorMessage());

		// alternative to the lambda function
//		Assertions.assertTrue(x, this::getErrorMessage);
	}
}
