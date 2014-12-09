package cn.sunline.common.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 常用编码/还原密码工具类.
 * 
 * @author liangbl
 * 
 */
public class PasswordUtil {

	private static final String DEFAULT_CHARACTER_ENCODING = "UTF-8";

	/**
	 * 将密码摘要.
	 * <p>
	 * 信息摘要是安全的单向哈希函数，它接收任意大小的数据，并输出固定长度的哈希值.
	 * <p>
	 * SUN提供的常用的算法名称有：MD2 MD5 SHA-1 SHA-256 SHA-384 SHA-512
	 * <p>
	 * 注：不可还原密码
	 * 
	 * @param password
	 *            密码明文
	 * @param encodingAlgorithm
	 *            信息摘要算法名称
	 * @param characterEncoding
	 *            密码的字符集名称
	 * @return 摘要字节数组
	 */
	public static byte[] digest(String password, String encodingAlgorithm, String characterEncoding) {
		if (password == null) {
			return null;
		}
		try {
			MessageDigest messageDigest = MessageDigest.getInstance(encodingAlgorithm);

			if (!StringUtil.isBlank(characterEncoding)) {
				messageDigest.update(password.getBytes(characterEncoding));
			} else {
				messageDigest.update(password.getBytes());
			}

			return messageDigest.digest();
		} catch (final NoSuchAlgorithmException e) {
			throw new SecurityException(e);
		} catch (final UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 常见编码算法：信息摘要.
	 * 
	 * @see #digest(String password, String encodingAlgorithm, String characterEncoding)
	 * @param password
	 *            待加密密码
	 * @param encodingAlgorithm
	 *            信息摘要算法名称
	 * @return　将密码摘要后的字节数组转为字符表示的16进制字符串
	 */
	public static String encodeByDigestHex(String password, String encodingAlgorithm) {
		String retv;
		if (password == null) {
			return null;
		}
		final byte[] digest = digest(password, encodingAlgorithm, DEFAULT_CHARACTER_ENCODING);
		retv = StringUtil.toHex(digest);
		return retv;
	}

	/**
	 * 常见编码算法：信息摘要,并用Base64编码.
	 * <p>
	 * Base64编码后保证结果为ASCII字符,可用于Internet传输(如：邮件)，也便于保存到数据库。
	 * 
	 * @see #digest(String password, String encodingAlgorithm, String characterEncoding)
	 * @param password
	 *            待加密密码
	 * @param encodingAlgorithm
	 *            信息摘要算法名称
	 * @return 将密码摘要后的字节串转为Base64编码后的字符串
	 */
	public static String encodeByDigestBase64(String password, String encodingAlgorithm) {
		String retv;
		if (password == null) {
			return null;
		}
		final byte[] digest = digest(password, encodingAlgorithm, DEFAULT_CHARACTER_ENCODING);
		retv = Base64Encoder.encode(digest, false);
		return retv;
	}

	/**
	 * 常见编码算法：DES加密,并用Base64编码.
	 * 
	 * @param password
	 * @param encodedKeySpec
	 *            8bytes长度的DES KEY
	 * @return DES加密结果用Base64编码后的字符串
	 */
	public static String encodeByDesBase64(String password, byte[] encodedKeySpec) {
		String retv;
		if (password == null) {
			return null;
		}
		byte[] digest;
		try {
			digest = CryptoUtil.encrypt("DES/ECB/PKCS5Padding", CryptoUtil.generateSymmetricKey("DES", encodedKeySpec), password
					.getBytes(DEFAULT_CHARACTER_ENCODING));
		} catch (UnsupportedEncodingException e) {
			throw new SecurityException(e);
		}
		retv = Base64Encoder.encode(digest, false);
		return retv;
	}

	/**
	 * 常见编码算法：DES加密,并用Base64编码.
	 * 
	 * @param password
	 * @param keyBase64
	 *            经过Base64编码后的8bytes长度的DES KEY
	 * @return DES加密结果用Base64编码后的字符串
	 */
	public static String encodeByDesBase64(String password, String keyBase64) {
		return encodeByDesBase64(password, Base64Decoder.decode(keyBase64));
	}

	/**
	 * 常见还原算法：DES加密,并用Base64编码.
	 * 
	 * @param password
	 * @param encodedKeySpec
	 *            8bytes长度的DES KEY
	 * @return 明文密码字符串
	 */
	public static String decodeByDesBase64(String password, byte[] encodedKeySpec) {
		String retv;
		if (password == null) {
			return null;
		}
		byte[] digest;
		digest = CryptoUtil.decrypt("DES/ECB/PKCS5Padding", CryptoUtil.generateSymmetricKey("DES", encodedKeySpec), Base64Decoder.decode(password));
		retv = new String(digest);
		return retv;
	}

	/**
	 * 常见还原算法：DES加密,并用Base64编码.
	 * 
	 * @param password
	 * @param encodedKeySpec
	 *            经过Base64编码后的8bytes长度的DES KEY
	 * @return 明文密码字符串
	 */
	public static String decodeByDesBase64(String password, String keyBase64) {
		return decodeByDesBase64(password, Base64Decoder.decode(keyBase64));
	}
}
