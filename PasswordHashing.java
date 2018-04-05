package com.ClientServiceTool.helpers;

import java.io.IOException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import com.ClientServiceTool.logger.ClientServiceLoggerFactory;
import com.ClientServiceTool.utility.LogTemplate;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * 
 * @author choudamk
 * @since 12/08/2017
 *
 */
public class PasswordHashing {

	private final static ClientServiceLoggerFactory logger = ClientServiceLoggerFactory
			.getLogger(PasswordHashing.class);

	private static final String TOKEN_ALGORITHM = "SHA1PRNG";
	private static final String TOKEN_PROVIDER = "SUN";
	private static final String PBEKEYSPEC = "PBKDF2WithHmacSHA384";
	private static final String TOKEN_SPLIT = ":";
	private static final int iterations = 10030;
	private static PasswordHashing instance;

	private PasswordHashing() {
	}

	public static PasswordHashing getInstance() {
		if (instance == null) {
			instance = new PasswordHashing();
		}
		return instance;
	}

	/**
	 * @author choudamk
	 * @since 12/08/2017
	 * @param password
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 * @throws NoSuchProviderException
	 */
	public String generateSecurePassword(String password)
			throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException {
		BASE64Encoder encoder = null;
		String hashPwd = null;
		byte[] saltPwd = null;
		PBEKeySpec pbeSpec = null;
		byte[] hashBytePwd = null;
		try {
			logger.info(LogTemplate.methodStart, "generateSecurePassword");
			if (password == null || password.trim().isEmpty()) {
				logger.info("Password is NULL.");
				return null;
			}
			saltPwd = getSalt();
			pbeSpec = new PBEKeySpec(password.toCharArray(), saltPwd, iterations, 64 * 8);
			hashBytePwd = SecretKeyFactory.getInstance(PBEKEYSPEC).generateSecret(pbeSpec).getEncoded();
			encoder = new BASE64Encoder();
			hashPwd = encoder.encode((toHex(hashBytePwd) + TOKEN_SPLIT + toHex(saltPwd)).getBytes());
			logger.info(LogTemplate.methodEnd, "Password hashing done and returned");
			return hashPwd;
		} catch (NoSuchAlgorithmException e) {
			logger.error("NoSuchAlgorithmException raised, Error msg  : {}", e.getMessage());
			throw new NoSuchAlgorithmException("NoSuchAlgorithmException raised, Error msg  : " + e.getMessage());
		} catch (InvalidKeySpecException e) {
			logger.error("InvalidKeySpecException raised, Error msg  : {}", e.getMessage());
			throw new InvalidKeySpecException("InvalidKeySpecException raised, Error msg  : " + e.getMessage());
		} catch (NoSuchProviderException e) {
			logger.error("NoSuchProviderException raised, Error msg  : {}", e.getMessage());
			throw new NoSuchProviderException("NoSuchProviderException raised, Error msg  : " + e.getMessage());
		} finally {
			encoder = null;
			saltPwd = null;
			hashBytePwd = null;
		}

	}

	/**
	 * @author choudamk
	 * @since 12/08/2017
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchProviderException
	 */
	private byte[] getSalt() throws NoSuchAlgorithmException, NoSuchProviderException {
		SecureRandom sr = SecureRandom.getInstance(TOKEN_ALGORITHM, TOKEN_PROVIDER);
		byte[] salt = new byte[16];
		sr.nextBytes(salt);
		return salt;
	}

	/**
	 * @author choudamk
	 * @since 12/08/2017
	 * @param array
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	private String toHex(byte[] array) throws NoSuchAlgorithmException {
		BigInteger bi = new BigInteger(1, array);
		String hex = bi.toString(16);
		int paddingLength = (array.length * 2) - hex.length();
		if (paddingLength > 0) {
			return String.format("%0" + paddingLength + "d", 0) + hex;
		} else {
			return hex;
		}
	}

	/**
	 * @author choudamk
	 * @since 12/08/2017
	 * @param password
	 * @param strPassword
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 * @throws IOException
	 */
	public boolean loginPwdCheck(String password, String strPassword)
			throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
		BASE64Decoder decoder = null;
		int diff;
		boolean pwdMatched = false;
		byte[] hashPwd = null, saltPwd = null, passwordHash = null;
		try {
			logger.info(LogTemplate.methodStart, "loginPwdCheck");
			decoder = new BASE64Decoder();
			String[] pwdHashCodeAry = new String(decoder.decodeBuffer(strPassword)).split(TOKEN_SPLIT);
			hashPwd = fromHex(pwdHashCodeAry[0]);
			saltPwd = fromHex(pwdHashCodeAry[1]);
			passwordHash = SecretKeyFactory.getInstance(PBEKEYSPEC)
					.generateSecret(new PBEKeySpec(password.toCharArray(), saltPwd, iterations, hashPwd.length * 8))
					.getEncoded();
			diff = hashPwd.length ^ passwordHash.length;
			for (int i = 0; i < hashPwd.length && i < passwordHash.length; i++) {
				diff |= hashPwd[i] ^ passwordHash[i];
			}
			if (diff == 0) {
				pwdMatched = true;
			}
			logger.info("Password matched :{}", pwdMatched);
			logger.info(LogTemplate.methodEnd, "loginPwdCheck");
			return pwdMatched;
		} catch (NoSuchAlgorithmException e) {
			logger.error("NoSuchAlgorithmException raised, Error msg  : {}", e.getMessage());
			throw new NoSuchAlgorithmException("NoSuchAlgorithmException raised, Error msg  : " + e.getMessage());
		} catch (InvalidKeySpecException e) {
			logger.error("InvalidKeySpecException raised, Error msg  : {}", e.getMessage());
			throw new InvalidKeySpecException("InvalidKeySpecException raised, Error msg  : " + e.getMessage());
		} catch (IOException e) {
			logger.error("IOException raised, Error msg  : {}", e.getMessage());
			throw new IOException("IOException raised, Error msg  : " + e.getMessage());
		} catch (NumberFormatException e) {
			logger.error("NumberFormatException raised - Password not matched , Error msg  : {}", e.getMessage());
			throw new NumberFormatException(
					"NumberFormatException raised, Password not matched Error msg  : " + e.getMessage());
		} finally {
			hashPwd = null;
			saltPwd = null;
			passwordHash = null;
			decoder = null;
		}
	}

	/**
	 * @author choudamk
	 * @since 12/08/2017
	 * @param hex
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	private byte[] fromHex(String hex) throws NoSuchAlgorithmException {
		byte[] bytes = new byte[hex.length() / 2];
		for (int i = 0; i < bytes.length; i++) {
			bytes[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
		}
		return bytes;
	}
}
