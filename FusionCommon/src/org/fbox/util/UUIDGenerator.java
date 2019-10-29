/**
 * A primary key generator that uses the UUID pattern to generate unique primary
 * keys. The class is used statically.
 * <P>
 * Each key is a 32-byte UUID (universally unique identifier) string consisting
 * of 4 segments:
 * <OL>
 * <LI> The current system time in hex format
 * <LI> This machine's IP address
 * <LI> The java.lang.Object hashcode of this class 
 * (for more variance, you could change this to be the hashcode of a passed in seed object)
 * <LI> A random number
 * </OL>
 */

package org.fbox.util;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.SecureRandom;

import org.fbox.common.exception.UUIDGenerateException;

public class UUIDGenerator {

	private static SecureRandom generator;
	private static String midValue;

	static {

		generator = new SecureRandom(); // initialize random generator
		try {
			InetAddress inet = InetAddress.getLocalHost();

			byte[] bytes = inet.getAddress();
			int hash = System.identityHashCode(UUIDGenerator.class);
			BigInteger big = new BigInteger(bytes);

			midValue = big.toString(16) + Integer.toHexString(hash); // initialize midValue
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
	

	/**
	 * Generates a unique key based on the UUID pattern.
	 * 
	 * @return a 32-character key.
	 */
	public static String getUUID(String prefix) throws UUIDGenerateException {
		StringBuffer uuid = new StringBuffer(prefix!=null?prefix:"");
		long currentTime = System.currentTimeMillis();
		int timeLow = (int) currentTime & 0xFFFFFFFF;

		uuid.append(Integer.toHexString(timeLow));

		if (midValue == null) {
			throw new UUIDGenerateException();
		}
		uuid.append(midValue);
		int random = generator.nextInt();
		uuid.append(Integer.toHexString(random));
		return uuid.toString();
	}

}
