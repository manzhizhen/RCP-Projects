package cn.sunline.common.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * ���ñ���/��ԭ���빤����.
 * 
 * @author liangbl
 * 
 */
public class PasswordUtil {

	private static final String DEFAULT_CHARACTER_ENCODING = "UTF-8";

	/**
	 * ������ժҪ.
	 * <p>
	 * ��ϢժҪ�ǰ�ȫ�ĵ����ϣ�����������������С�����ݣ�������̶����ȵĹ�ϣֵ.
	 * <p>
	 * SUN�ṩ�ĳ��õ��㷨�����У�MD2 MD5 SHA-1 SHA-256 SHA-384 SHA-512
	 * <p>
	 * ע�����ɻ�ԭ����
	 * 
	 * @param password
	 *            ��������
	 * @param encodingAlgorithm
	 *            ��ϢժҪ�㷨����
	 * @param characterEncoding
	 *            ������ַ�������
	 * @return ժҪ�ֽ�����
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
	 * ���������㷨����ϢժҪ.
	 * 
	 * @see #digest(String password, String encodingAlgorithm, String characterEncoding)
	 * @param password
	 *            ����������
	 * @param encodingAlgorithm
	 *            ��ϢժҪ�㷨����
	 * @return��������ժҪ����ֽ�����תΪ�ַ���ʾ��16�����ַ���
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
	 * ���������㷨����ϢժҪ,����Base64����.
	 * <p>
	 * Base64�����֤���ΪASCII�ַ�,������Internet����(�磺�ʼ�)��Ҳ���ڱ��浽���ݿ⡣
	 * 
	 * @see #digest(String password, String encodingAlgorithm, String characterEncoding)
	 * @param password
	 *            ����������
	 * @param encodingAlgorithm
	 *            ��ϢժҪ�㷨����
	 * @return ������ժҪ����ֽڴ�תΪBase64�������ַ���
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
	 * ���������㷨��DES����,����Base64����.
	 * 
	 * @param password
	 * @param encodedKeySpec
	 *            8bytes���ȵ�DES KEY
	 * @return DES���ܽ����Base64�������ַ���
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
	 * ���������㷨��DES����,����Base64����.
	 * 
	 * @param password
	 * @param keyBase64
	 *            ����Base64������8bytes���ȵ�DES KEY
	 * @return DES���ܽ����Base64�������ַ���
	 */
	public static String encodeByDesBase64(String password, String keyBase64) {
		return encodeByDesBase64(password, Base64Decoder.decode(keyBase64));
	}

	/**
	 * ������ԭ�㷨��DES����,����Base64����.
	 * 
	 * @param password
	 * @param encodedKeySpec
	 *            8bytes���ȵ�DES KEY
	 * @return ���������ַ���
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
	 * ������ԭ�㷨��DES����,����Base64����.
	 * 
	 * @param password
	 * @param encodedKeySpec
	 *            ����Base64������8bytes���ȵ�DES KEY
	 * @return ���������ַ���
	 */
	public static String decodeByDesBase64(String password, String keyBase64) {
		return decodeByDesBase64(password, Base64Decoder.decode(keyBase64));
	}
}
