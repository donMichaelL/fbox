package org.fbox.network.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestLogging {

	private static final Logger LOGGER = LoggerFactory.getLogger(TestLogging.class);
	// private static final Logger LOGGER = LoggerFactory.getLogger(this.getClass);
	
	public static void doSomething() {
		LOGGER.info("doSomething"); // Problem with log4j
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		doSomething();

	}

}
