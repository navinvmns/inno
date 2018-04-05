package com.ClientServiceTool.utility;

import org.owasp.encoder.Encode;

import com.ClientServiceTool.constants.LoggerConstants;
import com.ClientServiceTool.logger.ClientServiceLoggerFactory;

/**
 * @author choudamk
 * @version 1.0
 * @since 12/06/2017
 */
public class JavaEncoderUtility {

	private final static ClientServiceLoggerFactory log = ClientServiceLoggerFactory
			.getLogger(JavaEncoderUtility.class);

	/**
	 * @author choudamk
	 * @since 12/06/2017
	 * @param input
	 * @return Encoded text, empty string if anything goes wrong
	 */
	public static String encodeForHtml(String input) {
		if (isEmpty(input)) {
			return LoggerConstants.EMPTY;
		}
		try {
			return Encode.forHtml(input);
		} catch (Exception ex) {
			log.error("Encoding for HTML error, will return empty string: {}", ex.getMessage());
			return LoggerConstants.EMPTY;
		}
	}

	/**
	 * @author choudamk
	 * @since 12/06/2017
	 * @param input
	 * @return Encoded text, empty string if anything goes wrong
	 */
	public String encodeForJavascript(String input) {
		if (isEmpty(input)) {
			return LoggerConstants.EMPTY;
		}
		try {
			return Encode.forJavaScript(input);
		} catch (Exception ex) {
			log.error("Encoding for Javascript error, will return empty string: {}", ex.getMessage());
			return LoggerConstants.EMPTY;
		}
	}

	/**
	 * @author choudamk
	 * @since 12/06/2017
	 * @param input
	 * @return
	 */
	public static boolean isEmpty(String input) {
		if (input == null || input.isEmpty()) {
			return true;
		}
		return false;

	}
}
