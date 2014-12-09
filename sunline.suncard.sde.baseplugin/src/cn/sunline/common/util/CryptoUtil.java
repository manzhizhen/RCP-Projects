package cn.sunline.common.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.spec.EncodedKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * 加解密工具类.
 * <p>
 * 支持多种对称和非对称加密算法,支持对流进行加解密.
 * <p>
 * 提供生成密钥(对称密钥，公钥，私钥)，及对密钥进行格式化反格式化的工具.
 * <p>
 * 调用JVM的加密机进行加解密.可执行此程序(main)列出所有的算法提供者名称及支持的加密算法
 * 
 * <p>
 * 加密算法(algorithm)定义
 * <p>
 * 格式(3项中后两项可用缺省值)：algorithm/mode/padding
 * <ul>
 * <li>DES/CBC/PKCS5Padding
 * <li>DES/ECB/PKCS5Padding (SunJCE default)
 * </ul>
 * Algorithm
 * <ul>
 * <li>AES: Advanced Encryption Standard as specified by NIST in a draft FIPS. Based on the Rijndael algorithm by Joan Daemen and Vincent Rijmen, AES
 * is a 128-bit block cipher supporting keys of 128, 192, and 256 bits.
 * <li>Blowfish: The block cipher designed by Bruce Schneier.
 * <li>DES: The Digital Encryption Standard as described in FIPS PUB 46-2.
 * <li>DESede: Triple DES Encryption (DES-EDE).
 * <li>PBEWith<digest>And<encryption> or PBEWith<prf>And<encryption>: The password-based encryption algorithm (PKCS #5), using the specified message
 * digest (<digest>) or pseudo-random function (<prf>) and encryption algorithm (<encryption>). Examples:
 * <ul>
 * <li>PBEWithMD5AndDES: The password-based encryption algorithm as defined in: RSA Laboratories, "PKCS #5: Password-Based Encryption Standard,"
 * version 1.5, Nov 1993. Note that this algorithm implies CBC as the cipher mode and PKCS5Padding as the padding scheme and cannot be used with any
 * other cipher modes or padding schemes.
 * <li>PBEWithHmacSHA1AndDESede: The password-based encryption algorithm as defined in: RSA Laboratories, "PKCS #5: Password-Based Cryptography
 * Standard," version 2.0, March 1999.
 * </ul>
 * <li>RC2, RC4, and RC5: Variable-key-size encryption algorithms developed by Ron Rivest for RSA Data Security, Inc.
 * <li>RSA: The RSA encryption algorithm as defined in PKCS #1.
 * </ul>
 * Mode
 * <ul>
 * <li>NONE: No mode.
 * <li>CBC: Cipher Block Chaining Mode, as defined in FIPS PUB 81.
 * <li>CFB: Cipher Feedback Mode, as defined in FIPS PUB 81.
 * <li>ECB: Electronic Codebook Mode, as defined in: The National Institute of Standards and Technology (NIST) Federal Information Processing Standard
 * (FIPS) PUB 81, "DES Modes of Operation," U.S. Department of Commerce, Dec 1980.
 * <li>OFB: Output Feedback Mode, as defined in FIPS PUB 81.
 * <li>PCBC: Propagating Cipher Block Chaining, as defined by Kerberos V4.
 * </ul>
 * Padding
 * <ul>
 * <li>NoPadding: No padding.
 * <li>OAEPWith<digest>And<mgf>Padding: Optimal Asymmetric Encryption Padding scheme defined in PKCS #1, where <digest> should be replaced by the
 * message digest and <mgf> by the mask generation function. Example: OAEPWithMD5AndMGF1Padding.
 * <li>PKCS5Padding: The padding scheme described in: RSA Laboratories, "PKCS #5: Password-Based Encryption Standard," version 1.5, November 1993.
 * <li>SSL3Padding: The padding scheme defined in the SSL Protocol Version 3.0, November 18, 1996, section 5.2.3.2 (CBC block cipher):
 * </ul>
 * 
 * @author liangbl@sunline.cn
 */
public class CryptoUtil {
	/**
	 * The default symmetric algorithm used to generated keys when no algorithm is explicilty specified.
	 */
	public static final String DEFAULT_SYMMETRIC_ALGORITHM = "Blowfish";

	/**
	 * The default asymmetric algorithm used to generated keys when no algorithm is explicilty specified.
	 */
	public static final String DEFAULT_ASYMMETRIC_ALGORITHM = "RSA/ECB/PKCS1Padding";

	/**
	 * The default size of asymmetric keys generated when no key size is explicilty specified.
	 */
	public static final int DEFAULT_GENERATED_ASYMMETRIC_KEY_SIZE = 1024;

	/**
	 * The default size of symmetric keys generated when no key size is explicilty specified.
	 */
	public static final int DEFAULT_GENERATED_SYMMETRIC_KEY_SIZE = 128;

	// private static final KeyPair embeddedKeyPair;
	private static final SecretKey embeddedSecretKey;

	// The default key sizes to use, per algorithm, if not provided by clients
	private static Map defaultKeySizeMap = new HashMap();

	static {
		embeddedSecretKey = generateSymmetricKey(DEFAULT_SYMMETRIC_ALGORITHM, Base64Decoder.decode("9GzWorXLpJZDEkyPgvZi6Q==".getBytes()));

		// Uppercase algorithms to default key sizes map
		defaultKeySizeMap.put("BLOWFISH", new Integer(128));
		defaultKeySizeMap.put("DES", new Integer(56));// =8*n
		defaultKeySizeMap.put("DESEDE", new Integer(168));// keysize: must be equal to 112 or 168
		defaultKeySizeMap.put("RC2", new Integer(128));
		defaultKeySizeMap.put("RC4", new Integer(128));
		// 非对称算法
		defaultKeySizeMap.put("RC5", new Integer(128));
		defaultKeySizeMap.put("RSA", new Integer(2048));
	}

	/**
	 * 取缺省的对称密钥大小
	 * 
	 * @param algorithm
	 * @return
	 */
	public static int getDefaultSymmetricKeySize(String algorithm) {
		Integer keySize = (Integer) defaultKeySizeMap.get(getKeyTypeFromAlgorithm(algorithm).toUpperCase());
		return keySize != null ? keySize.intValue() : DEFAULT_GENERATED_SYMMETRIC_KEY_SIZE;
	}

	/**
	 * 取非对称密钥大小
	 * 
	 * @param algorithm
	 * @return
	 */
	public static int getDefaultASymmetricKeySize(String algorithm) {
		Integer keySize = (Integer) defaultKeySizeMap.get(algorithm.toUpperCase());
		return keySize != null ? keySize.intValue() : DEFAULT_GENERATED_ASYMMETRIC_KEY_SIZE;
	}

	/**
	 * Generates a public/private keypair using the specified algorithm and key size.
	 * 
	 * @param algorithm
	 *            the algorithm used to generated the keys, such as "DSA" or "RSA"
	 * @param providerName
	 *            the name of the cryptography provider to use, such as "BC" for Bouncy Castle, which may be null
	 * @param keySize
	 *            the size of the keys to generated, such as 512 or 1024
	 * @return the key pair generated
	 * @throws SecurityException
	 *             if an error occurs generating the key pair
	 * @see #DEFAULT_ASYMMETRIC_ALGORITHM
	 */
	public static KeyPair generateKeyPair(String algorithm, String providerName, int keySize) throws SecurityException {
		try {
			KeyPairGenerator keyGen = providerName != null ? KeyPairGenerator.getInstance(getKeyTypeFromAlgorithm(algorithm), providerName)
					: KeyPairGenerator.getInstance(getKeyTypeFromAlgorithm(algorithm));
			keyGen.initialize(keySize);
			KeyPair keypair = keyGen.genKeyPair();
			return keypair;
		} catch (Throwable th) {
			throw new SecurityException("An unexpected error occured encrypting the data: ", th);
		}
	}

	/**
	 * Generates a public/private keypair using the specified algorithm and key size.
	 * 
	 * @param algorithm
	 *            the algorithm used to generated the keys, such as "DSA" or "RSA"
	 * @param keySize
	 *            the size of the keys to generated, such as 512 or 1024
	 * @return the key pair generated
	 * @throws SecurityException
	 *             if an error occurs generating the key pair
	 * @see #DEFAULT_ASYMMETRIC_ALGORITHM
	 */
	public static KeyPair generateKeyPair(String algorithm, int keySize) throws SecurityException {
		return generateKeyPair(algorithm, null, keySize);
	}

	/**
	 * Generates a public/private keypair using the specified algorithm and default key size.
	 * 
	 * @param algorithm
	 *            the algorithm that was used to originally generate the key pair. If null, the default algorithm employed by this utility will be
	 *            assumed ({@link #DEFAULT_ASYMMETRIC_ALGORITHM}).
	 * @param providerName
	 *            the name of the cryptography provider to use, such as "BC" for Bouncy Castle, which may be null
	 * @return the key pair generated
	 * @throws SecurityException
	 *             if an error occurs generating the key pair
	 * @see #DEFAULT_ASYMMETRIC_ALGORITHM
	 * @see #getDefaultASymmetricKeySize(String)
	 */
	public static KeyPair generateKeyPair(String algorithm, String providerName) throws SecurityException {
		return generateKeyPair(algorithm, providerName, getDefaultASymmetricKeySize(algorithm));
	}

	/**
	 * Generates a public/private keypair using the specified algorithm and default key size.
	 * 
	 * @param algorithm
	 *            the algorithm used to generated the keys, such as "DSA" or "RSA"
	 * @return the key pair generated
	 * @throws SecurityException
	 *             if an error occurs generating the key pair
	 * @see #DEFAULT_ASYMMETRIC_ALGORITHM
	 * @see #getDefaultASymmetricKeySize(String)
	 */
	public static KeyPair generateKeyPair(String algorithm) throws SecurityException {
		return generateKeyPair(algorithm, getDefaultASymmetricKeySize(algorithm));
	}

	/**
	 * Generates a public/private keypair using a default algorithm and with the specified key size.
	 * 
	 * @param keySize
	 *            the size of the keys to generated, such as 512 or 1024
	 * @return the key pair generated
	 * @throws SecurityException
	 *             if an error occurs generating the key pair
	 * @see #DEFAULT_ASYMMETRIC_ALGORITHM
	 */
	public static KeyPair generateKeyPair(int keySize) throws SecurityException {
		return generateKeyPair(DEFAULT_ASYMMETRIC_ALGORITHM, keySize);
	}

	/**
	 * Generates a public/private keypair using defaults for the algorithm and sizes of the keys generated.
	 * 
	 * @return the key pair generated
	 * @throws SecurityException
	 *             if an error occurs generating the key pair
	 * @see #DEFAULT_ASYMMETRIC_ALGORITHM
	 * @see #getDefaultASymmetricKeySize(String)
	 */
	public static KeyPair generateKeyPair() throws SecurityException {
		return generateKeyPair(DEFAULT_ASYMMETRIC_ALGORITHM, getDefaultASymmetricKeySize(DEFAULT_ASYMMETRIC_ALGORITHM));
	}

	/**
	 * Generates a secret key using the specified algorithm and key size.
	 * 
	 * @param algorithm
	 *            the algorithm used to generated the key, such as "DES" or "Blowfish"
	 * @param keySize
	 *            the size of the key to generated, such as 512 or 1024
	 * @return the key generated
	 * @throws SecurityException
	 *             if an error occurs generating the key
	 * @see #DEFAULT_SYMMETRIC_ALGORITHM
	 */
	public static SecretKey generateSymmetricKey(String algorithm, int keySize) throws SecurityException {
		return generateSymmetricKey(algorithm, null, keySize);
	}

	/**
	 * Generates a secret key using the specified algorithm, provider and key size.
	 * 
	 * @param algorithm
	 *            the algorithm used to generated the key, such as "DES" or "Blowfish"
	 * @param providerName
	 *            the name of the cryptography provider to use, such as "BC" for Bouncy Castle, which may be null
	 * @param keySize
	 *            the size of the key to generated, such as 512 or 1024
	 * @return the key generated
	 * @throws SecurityException
	 *             if an error occurs generating the key
	 * @see #DEFAULT_SYMMETRIC_ALGORITHM
	 */
	public static SecretKey generateSymmetricKey(String algorithm, String providerName, int keySize) throws SecurityException {
		try {
			KeyGenerator keyGen = providerName != null ? KeyGenerator.getInstance(getKeyTypeFromAlgorithm(algorithm), providerName) : KeyGenerator
					.getInstance(getKeyTypeFromAlgorithm(algorithm));
			keyGen.init(keySize);
			SecretKey key = keyGen.generateKey();
			return key;
		} catch (Throwable th) {
			throw new SecurityException("An unexpected error occured generating the secret key: ", th);
		}
	}

	/**
	 * Generates a secret key using the specified algorithm, provider and a default key size.
	 * 
	 * @param algorithm
	 *            the algorithm used to generated the key, such as "DES" or "Blowfish"
	 * @param providerName
	 *            the name of the cryptography provider to use, such as "BC" for Bouncy Castle, which may be null
	 * @return the key generated
	 * @throws SecurityException
	 *             if an error occurs generating the key
	 * @see #DEFAULT_SYMMETRIC_ALGORITHM
	 * @see #getDefaultSymmetricKeySize(String)
	 */
	public static SecretKey generateSymmetricKey(String algorithm, String providerName) throws SecurityException {
		return generateSymmetricKey(algorithm, providerName, getDefaultSymmetricKeySize(algorithm));
	}

	/**
	 * Generates a secret key using the specified algorithm and default provider and default key size.
	 * 
	 * @param algorithm
	 *            the algorithm used to generated the key, such as "DES" or "Blowfish"
	 * @return the key generated
	 * @throws SecurityException
	 *             if an error occurs generating the key
	 * @see #DEFAULT_SYMMETRIC_ALGORITHM
	 * @see #getDefaultSymmetricKeySize(String)
	 */
	public static SecretKey generateSymmetricKey(String algorithm) throws SecurityException {
		return generateSymmetricKey(algorithm, null, getDefaultSymmetricKeySize(algorithm));
	}

	/**
	 * Generates a secret key using a default algorithm, default provider and the specified key size.
	 * 
	 * @param keySize
	 *            the size of the key to generated, such as 512 or 1024
	 * @return the key generated
	 * @throws SecurityException
	 *             if an error occurs generating the key
	 * @see #DEFAULT_SYMMETRIC_ALGORITHM
	 */
	public static SecretKey generateSymmetricKey(int keySize) throws SecurityException {
		return generateSymmetricKey(DEFAULT_SYMMETRIC_ALGORITHM, null, keySize);
	}

	/**
	 * Generates a secret key using a default algorithm, default provider and a default key size.
	 * 
	 * @return the key generated
	 * @throws SecurityException
	 *             if an error occurs generating the key
	 * @see #DEFAULT_SYMMETRIC_ALGORITHM
	 * @see #getDefaultASymmetricKeySize(String)
	 */
	public static SecretKey generateSymmetricKey() throws SecurityException {
		return generateSymmetricKey(DEFAULT_SYMMETRIC_ALGORITHM, null, getDefaultSymmetricKeySize(DEFAULT_SYMMETRIC_ALGORITHM));
	}

	/**
	 * Reconstructs a secret key from its encoded, persisted representations which was originally generated using the specified algorithm.
	 * 
	 * @param algorithm
	 *            the algorithm that was used to originally generate the key pair
	 * @param encodedKeySpec
	 *            the encoded secret key specification (as that returned by a call to <code>Key.getEncoded</code>)
	 * @return the reconstructed secret key
	 * @throws SecurityException
	 *             if an error occurs generating the key
	 */
	public static SecretKey generateSymmetricKey(String algorithm, byte encodedKeySpec[]) throws SecurityException {
		try {
			// SecretKey secretKey = new SecretKeySpec(encodedKeySpec, algorithm);
			SecretKey secretKey = new SecretKeySpec(encodedKeySpec, getKeyTypeFromAlgorithm(algorithm));
			return secretKey;
		} catch (Throwable th) {
			throw new SecurityException("An unexpected error occured reconstructing the symmetric key from its encoded specification: ", th);
		}
	}

	/**
	 * Generates the encoded, persistable, representation of the public and private keys from the specified key pair.
	 * 
	 * @param keyPair
	 *            the key pair whose keys are to be encoded.
	 * @return an array of encodings, of size 2, with the first array element representing the encoded public key and the second the encoded private
	 *         key.
	 */
	public static byte[][] generateEncodedPublicPrivateKeySpecs(KeyPair keyPair) {
		try {
			byte publicKeyEncoding[] = keyPair.getPublic().getEncoded();
			byte privateKeyEncoding[] = keyPair.getPrivate().getEncoded();
			return new byte[][] { publicKeyEncoding, privateKeyEncoding };
		} catch (Throwable th) {
			throw new SecurityException("An unexpected error occured generating the encoded public and private key specifications: ", th);
		}
	}

	/**
	 * Generates the encoded, persistable, representation of the public and private keys from the specified key pair, in Base64 encoded format with
	 * standard PEM headers and footer delimiters.
	 * 
	 * <p>
	 * For example, with header/footer delimiters, a private key might be returned as:
	 * </p>
	 * 
	 * <pre>
	 * -----BEGIN PRIVATE KEY-----
	 * MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAKAMsHzpziR5n+UhalvyX1w1cC2w
	 * oCtqqQAX1lamrofMwy0qTz+W2g4g6DMiAD0PwmqaTvJ7UsxW9jmTAebSe6oKOYOQxUTXI+WG3c9B
	 * hx2hQj36ClhFRF5Zc0oY8y75RzEH63qUKsekQjXbjRnzGcQ9K2i+USemJPWjW63tuxixAgMBAAEC
	 * gYAZaQpYOrKs3daCBWUihf+X3zAZQPKdEgkU57Py+/G3w821DQOZ//RMy/Kxs5NAHpFqZWdlXikO
	 * IjxjdbCWmhJh81Gvnwz1BArGaYwrQO0uTH0qbEN9SyBcuBAD8nNfVpgqKdK7Ye4lyewdRB8C+T5J
	 * Uc89MCR4kQt09j0mzUw1MQJBANvDJcR64tOQDNPu0ehF6fRZhwSYD+LDv0Qih4owqL9R00hXuPDm
	 * PRyn25TMN91WTeFTirmJspWHHgy63f45jdUCQQC6cOGbEbggXmvFZYKGfW0ChJrgJFMx99RW6kLn
	 * Jw9E3rylAgZD8MXfK1yCb1HtL5TEMqE9DkxPCQPbkIYQjGFtAkAjxcRkE0zQ+2XbKcjpclf++oPL
	 * 76TGWO7NfIFrsTgGzJ8D66OjMxdHjttjgUqmsOHEiADQ6uUzCeeOUuzH8T5xAkEAjudg9ZQiVqUo
	 * 4/fHkUBoIsrzTyRopF86YZhTyYuV14sGe0/O75qIgGNjGBMtb6jN1YidMAhakyXs0Am5yMthDQJA
	 * MVW2SBPHqRfHe7wTJxOxqNM/2dMwQD0eKPUHaviios+n33Dnfqk7uPFFYVKmxeBq5E3Gsq7qv9sl
	 * O67Ms20ZBg==
	 * -----END PRIVATE KEY-----
	 * </pre>
	 * 
	 * @param keyPair
	 *            the key pair whose keys are to be encoded.
	 * @return an array of encodings, of size 2, with the first array element representing the encoded public key and the second the encoded private
	 *         key in PEM format.
	 * @throws SecurityException
	 *             if an error occurs during encoding
	 * @see #encodePublicOrPrivateKeyToPEMString(Key, boolean)
	 * @see #decodePEMStringsToKeyPair(String, String)
	 */
	public static String[] encodeKeyPairToPEMStrings(KeyPair keyPair, boolean includeHeadersAndFooters) throws SecurityException {
		return new String[] { encodePublicOrPrivateKeyToPEMString(keyPair.getPublic(), includeHeadersAndFooters),
				encodePublicOrPrivateKeyToPEMString(keyPair.getPrivate(), includeHeadersAndFooters) };
	}

	/**
	 * Generates the encoded, persistable, representation of the public and private keys from the specified key pair, in Base64 encoded format with or
	 * without standard PEM header/footer delimiters.
	 * 
	 * <p>
	 * For example, with header/footer delimiters, a private key might be returned as:
	 * </p>
	 * 
	 * <pre>
	 * -----BEGIN PRIVATE KEY-----
	 * MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAKAMsHzpziR5n+UhalvyX1w1cC2w
	 * oCtqqQAX1lamrofMwy0qTz+W2g4g6DMiAD0PwmqaTvJ7UsxW9jmTAebSe6oKOYOQxUTXI+WG3c9B
	 * hx2hQj36ClhFRF5Zc0oY8y75RzEH63qUKsekQjXbjRnzGcQ9K2i+USemJPWjW63tuxixAgMBAAEC
	 * gYAZaQpYOrKs3daCBWUihf+X3zAZQPKdEgkU57Py+/G3w821DQOZ//RMy/Kxs5NAHpFqZWdlXikO
	 * IjxjdbCWmhJh81Gvnwz1BArGaYwrQO0uTH0qbEN9SyBcuBAD8nNfVpgqKdK7Ye4lyewdRB8C+T5J
	 * Uc89MCR4kQt09j0mzUw1MQJBANvDJcR64tOQDNPu0ehF6fRZhwSYD+LDv0Qih4owqL9R00hXuPDm
	 * PRyn25TMN91WTeFTirmJspWHHgy63f45jdUCQQC6cOGbEbggXmvFZYKGfW0ChJrgJFMx99RW6kLn
	 * Jw9E3rylAgZD8MXfK1yCb1HtL5TEMqE9DkxPCQPbkIYQjGFtAkAjxcRkE0zQ+2XbKcjpclf++oPL
	 * 76TGWO7NfIFrsTgGzJ8D66OjMxdHjttjgUqmsOHEiADQ6uUzCeeOUuzH8T5xAkEAjudg9ZQiVqUo
	 * 4/fHkUBoIsrzTyRopF86YZhTyYuV14sGe0/O75qIgGNjGBMtb6jN1YidMAhakyXs0Am5yMthDQJA
	 * MVW2SBPHqRfHe7wTJxOxqNM/2dMwQD0eKPUHaviios+n33Dnfqk7uPFFYVKmxeBq5E3Gsq7qv9sl
	 * O67Ms20ZBg==
	 * -----END PRIVATE KEY-----
	 * </pre>
	 * 
	 * @param keyPair
	 *            the key pair whose keys are to be encoded.
	 * @return an array of encodings, of size 2, with the first array element representing the encoded public key and the second the encoded private
	 *         key in PEM format.
	 * @throws SecurityException
	 *             if an error occurs during encoding
	 * @see #encodePublicOrPrivateKeyToPEMString(Key, boolean)
	 * @see #decodePEMStringsToKeyPair(String, String)
	 */
	public static String[] encodeKeyPairToPEMStrings(KeyPair keyPair) throws SecurityException {
		return encodeKeyPairToPEMStrings(keyPair, true);
	}

	/**
	 * Generates the encoded, persistable, representation of a public / private key, in PEM format.
	 * 
	 * <p>
	 * The PEM format represents DER-encoded data in a printable format. Traditionally, PEM encoding simply base64-encodes DER-encoded data and adds a
	 * simple header and footer.
	 * </p>
	 * 
	 * <p>
	 * For example, with header/footer delimiters, a private key might be returned as:
	 * </p>
	 * 
	 * <pre>
	 * -----BEGIN PRIVATE KEY-----
	 * MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAKAMsHzpziR5n+UhalvyX1w1cC2w
	 * oCtqqQAX1lamrofMwy0qTz+W2g4g6DMiAD0PwmqaTvJ7UsxW9jmTAebSe6oKOYOQxUTXI+WG3c9B
	 * hx2hQj36ClhFRF5Zc0oY8y75RzEH63qUKsekQjXbjRnzGcQ9K2i+USemJPWjW63tuxixAgMBAAEC
	 * gYAZaQpYOrKs3daCBWUihf+X3zAZQPKdEgkU57Py+/G3w821DQOZ//RMy/Kxs5NAHpFqZWdlXikO
	 * IjxjdbCWmhJh81Gvnwz1BArGaYwrQO0uTH0qbEN9SyBcuBAD8nNfVpgqKdK7Ye4lyewdRB8C+T5J
	 * Uc89MCR4kQt09j0mzUw1MQJBANvDJcR64tOQDNPu0ehF6fRZhwSYD+LDv0Qih4owqL9R00hXuPDm
	 * PRyn25TMN91WTeFTirmJspWHHgy63f45jdUCQQC6cOGbEbggXmvFZYKGfW0ChJrgJFMx99RW6kLn
	 * Jw9E3rylAgZD8MXfK1yCb1HtL5TEMqE9DkxPCQPbkIYQjGFtAkAjxcRkE0zQ+2XbKcjpclf++oPL
	 * 76TGWO7NfIFrsTgGzJ8D66OjMxdHjttjgUqmsOHEiADQ6uUzCeeOUuzH8T5xAkEAjudg9ZQiVqUo
	 * 4/fHkUBoIsrzTyRopF86YZhTyYuV14sGe0/O75qIgGNjGBMtb6jN1YidMAhakyXs0Am5yMthDQJA
	 * MVW2SBPHqRfHe7wTJxOxqNM/2dMwQD0eKPUHaviios+n33Dnfqk7uPFFYVKmxeBq5E3Gsq7qv9sl
	 * O67Ms20ZBg==
	 * -----END PRIVATE KEY-----
	 * </pre>
	 * 
	 * @param key
	 *            the key to be encoded; must be either a public or private key.
	 * @param includeHeadersAndFooter
	 *            true if a PEM header and footer is to be used to delimited the encoding, false if not.
	 * @return the encoded key in PEM format.
	 * @throws SecurityException
	 *             if an error occurs during encoding
	 * @see #decodePublicKeyPEMString(String)
	 * @see #decodePrivateKeyPEMString(String)
	 */
	public static String encodePublicOrPrivateKeyToPEMString(Key key, boolean includeHeadersAndFooter) throws SecurityException {
		// Assert.isTrue((key instanceof PublicKey) || (key instanceof PrivateKey), "key specified must either be a public or private key");
		if (!(key instanceof PublicKey) || (key instanceof PrivateKey)) {
			throw new SecurityException("key specified must either be a public or private key");
		}

		try {
			StringBuffer sBuf = new StringBuffer();
			byte keyEncoding[] = key.getEncoded();
			if (includeHeadersAndFooter) {
				sBuf.append("-----BEGIN ").append(key instanceof PublicKey ? "PUBLIC" : "PRIVATE").append(" KEY-----\n");
			}
			sBuf.append(Base64Encoder.encode(keyEncoding)).append("\n");
			if (includeHeadersAndFooter) {
				sBuf.append("-----END ").append(key instanceof PublicKey ? "PUBLIC" : "PRIVATE").append(" KEY-----\n");
			}
			String encodedKeyStr = sBuf.toString();
			return encodedKeyStr;
		} catch (Throwable th) {
			throw new SecurityException("An unexpected error occured generating the encoded key specification: ", th);
		}
	}

	/**
	 * Decodes a PEM encoded public key specification and returns the <code>{@link PublicKey}</code>.
	 * 
	 * <p>
	 * The PEM format represents DER-encoded data in a printable format. Traditionally, PEM encoding simply base64-encodes DER-encoded data and adds a
	 * simple header and footer.
	 * </p>
	 * 
	 * <p>
	 * For example, with header/footer delimiters, a PEM encoded public key might be:
	 * </p>
	 * 
	 * <pre>
	 * -----BEGIN PUBLIC KEY-----
	 * MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAKAMsHzpziR5n+UhalvyX1w1cC2w
	 * oCtqqQAX1lamrofMwy0qTz+W2g4g6DMiAD0PwmqaTvJ7UsxW9jmTAebSe6oKOYOQxUTXI+WG3c9B
	 * hx2hQj36ClhFRF5Zc0oY8y75RzEH63qUKsekQjXbjRnzGcQ9K2i+USemJPWjW63tuxixAgMBAAEC
	 * gYAZaQpYOrKs3daCBWUihf+X3zAZQPKdEgkU57Py+/G3w821DQOZ//RMy/Kxs5NAHpFqZWdlXikO
	 * IjxjdbCWmhJh81Gvnwz1BArGaYwrQO0uTH0qbEN9SyBcuBAD8nNfVpgqKdK7Ye4lyewdRB8C+T5J
	 * Uc89MCR4kQt09j0mzUw1MQJBANvDJcR64tOQDNPu0ehF6fRZhwSYD+LDv0Qih4owqL9R00hXuPDm
	 * PRyn25TMN91WTeFTirmJspWHHgy63f45jdUCQQC6cOGbEbggXmvFZYKGfW0ChJrgJFMx99RW6kLn
	 * Jw9E3rylAgZD8MXfK1yCb1HtL5TEMqE9DkxPCQPbkIYQjGFtAkAjxcRkE0zQ+2XbKcjpclf++oPL
	 * 76TGWO7NfIFrsTgGzJ8D66OjMxdHjttjgUqmsOHEiADQ6uUzCeeOUuzH8T5xAkEAjudg9ZQiVqUo
	 * 4/fHkUBoIsrzTyRopF86YZhTyYuV14sGe0/O75qIgGNjGBMtb6jN1YidMAhakyXs0Am5yMthDQJA
	 * MVW2SBPHqRfHe7wTJxOxqNM/2dMwQD0eKPUHaviios+n33Dnfqk7uPFFYVKmxeBq5E3Gsq7qv9sl
	 * O67Ms20ZBg==
	 * -----END PUBLIC KEY-----
	 * </pre>
	 * 
	 * @param keyPEMSpec
	 *            the encoded key PEM specification, which may be delimited by a PEM header/footer.
	 * @return the reconsituted key
	 * @throws SecurityException
	 *             if an error occurs decoding the key specification
	 * @see #encodePublicOrPrivateKeyToPEMString(Key, boolean)
	 * @see #encodeKeyPairToPEMStrings(KeyPair, boolean)
	 */
	public static PublicKey decodePublicKeyPEMString(String keyPEMSpec) throws SecurityException {
		return decodePublicKeyPEMString(DEFAULT_ASYMMETRIC_ALGORITHM, null, keyPEMSpec);
	}

	/**
	 * Decodes a PEM encoded public key specification using the specified algorithm (which was originally used to generate the key) and returns the
	 * <code>{@link PublicKey}</code>.
	 * 
	 * <p>
	 * The PEM format represents DER-encoded data in a printable format. Traditionally, PEM encoding simply base64-encodes DER-encoded data and adds a
	 * simple header and footer.
	 * </p>
	 * 
	 * <p>
	 * For example, with header/footer delimiters, a PEM encoded public key might be:
	 * </p>
	 * 
	 * <pre>
	 * -----BEGIN PUBLIC KEY-----
	 * MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAKAMsHzpziR5n+UhalvyX1w1cC2w
	 * oCtqqQAX1lamrofMwy0qTz+W2g4g6DMiAD0PwmqaTvJ7UsxW9jmTAebSe6oKOYOQxUTXI+WG3c9B
	 * hx2hQj36ClhFRF5Zc0oY8y75RzEH63qUKsekQjXbjRnzGcQ9K2i+USemJPWjW63tuxixAgMBAAEC
	 * gYAZaQpYOrKs3daCBWUihf+X3zAZQPKdEgkU57Py+/G3w821DQOZ//RMy/Kxs5NAHpFqZWdlXikO
	 * IjxjdbCWmhJh81Gvnwz1BArGaYwrQO0uTH0qbEN9SyBcuBAD8nNfVpgqKdK7Ye4lyewdRB8C+T5J
	 * Uc89MCR4kQt09j0mzUw1MQJBANvDJcR64tOQDNPu0ehF6fRZhwSYD+LDv0Qih4owqL9R00hXuPDm
	 * PRyn25TMN91WTeFTirmJspWHHgy63f45jdUCQQC6cOGbEbggXmvFZYKGfW0ChJrgJFMx99RW6kLn
	 * Jw9E3rylAgZD8MXfK1yCb1HtL5TEMqE9DkxPCQPbkIYQjGFtAkAjxcRkE0zQ+2XbKcjpclf++oPL
	 * 76TGWO7NfIFrsTgGzJ8D66OjMxdHjttjgUqmsOHEiADQ6uUzCeeOUuzH8T5xAkEAjudg9ZQiVqUo
	 * 4/fHkUBoIsrzTyRopF86YZhTyYuV14sGe0/O75qIgGNjGBMtb6jN1YidMAhakyXs0Am5yMthDQJA
	 * MVW2SBPHqRfHe7wTJxOxqNM/2dMwQD0eKPUHaviios+n33Dnfqk7uPFFYVKmxeBq5E3Gsq7qv9sl
	 * O67Ms20ZBg==
	 * -----END PUBLIC KEY-----
	 * </pre>
	 * 
	 * @param algorithm
	 *            the algorithm that was used to originally generate the key pair. If null, the default algorithm employed by this utility will be
	 *            assumed ({@link #DEFAULT_ASYMMETRIC_ALGORITHM}).
	 * @param keyPEMSpec
	 *            the encoded key PEM specification, which may be delimited by a PEM header/footer.
	 * @return the reconsituted key
	 * @throws SecurityException
	 *             if an error occurs decoding the key specification
	 * @see #encodePublicOrPrivateKeyToPEMString(Key, boolean)
	 * @see #encodeKeyPairToPEMStrings(KeyPair, boolean)
	 */
	public static PublicKey decodePublicKeyPEMString(String algorithm, String keyPEMSpec) throws SecurityException {
		return decodePublicKeyPEMString(algorithm, null, keyPEMSpec);
	}

	/**
	 * Decodes a PEM encoded public key specification using the specified algorithm (which was originally used to generate the key) and provider and
	 * returns the <code>{@link PublicKey}</code>.
	 * 
	 * <p>
	 * The PEM format represents DER-encoded data in a printable format. Traditionally, PEM encoding simply base64-encodes DER-encoded data and adds a
	 * simple header and footer.
	 * </p>
	 * 
	 * <p>
	 * For example, with header/footer delimiters, a PEM encoded public key might be:
	 * </p>
	 * 
	 * <pre>
	 * -----BEGIN PUBLIC KEY-----
	 * MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAKAMsHzpziR5n+UhalvyX1w1cC2w
	 * oCtqqQAX1lamrofMwy0qTz+W2g4g6DMiAD0PwmqaTvJ7UsxW9jmTAebSe6oKOYOQxUTXI+WG3c9B
	 * hx2hQj36ClhFRF5Zc0oY8y75RzEH63qUKsekQjXbjRnzGcQ9K2i+USemJPWjW63tuxixAgMBAAEC
	 * gYAZaQpYOrKs3daCBWUihf+X3zAZQPKdEgkU57Py+/G3w821DQOZ//RMy/Kxs5NAHpFqZWdlXikO
	 * IjxjdbCWmhJh81Gvnwz1BArGaYwrQO0uTH0qbEN9SyBcuBAD8nNfVpgqKdK7Ye4lyewdRB8C+T5J
	 * Uc89MCR4kQt09j0mzUw1MQJBANvDJcR64tOQDNPu0ehF6fRZhwSYD+LDv0Qih4owqL9R00hXuPDm
	 * PRyn25TMN91WTeFTirmJspWHHgy63f45jdUCQQC6cOGbEbggXmvFZYKGfW0ChJrgJFMx99RW6kLn
	 * Jw9E3rylAgZD8MXfK1yCb1HtL5TEMqE9DkxPCQPbkIYQjGFtAkAjxcRkE0zQ+2XbKcjpclf++oPL
	 * 76TGWO7NfIFrsTgGzJ8D66OjMxdHjttjgUqmsOHEiADQ6uUzCeeOUuzH8T5xAkEAjudg9ZQiVqUo
	 * 4/fHkUBoIsrzTyRopF86YZhTyYuV14sGe0/O75qIgGNjGBMtb6jN1YidMAhakyXs0Am5yMthDQJA
	 * MVW2SBPHqRfHe7wTJxOxqNM/2dMwQD0eKPUHaviios+n33Dnfqk7uPFFYVKmxeBq5E3Gsq7qv9sl
	 * O67Ms20ZBg==
	 * -----END PUBLIC KEY-----
	 * </pre>
	 * 
	 * @param algorithm
	 *            the algorithm that was used to originally generate the key pair. If null, the default algorithm employed by this utility will be
	 *            assumed ({@link #DEFAULT_ASYMMETRIC_ALGORITHM}).
	 * @param providerName
	 *            the name of the cryptography provider to use, such as "BC" for Bouncy Castle, which may be null
	 * @param keyPEMSpec
	 *            the encoded key PEM specification, which may be delimited by a PEM header/footer.
	 * @return the reconsituted key
	 * @throws SecurityException
	 *             if an error occurs decoding the key specification
	 * @see #encodePublicOrPrivateKeyToPEMString(Key, boolean)
	 * @see #encodeKeyPairToPEMStrings(KeyPair, boolean)
	 */
	public static PublicKey decodePublicKeyPEMString(String algorithm, String providerName, String keyPEMSpec) throws SecurityException {
		String lines[] = StringUtil.splitIntoLines(keyPEMSpec);
		if (lines.length > 2) {
			if (lines[0].indexOf("--BEGIN ") > 0 && lines[0].indexOf(" KEY--") > 0 && lines[lines.length - 1].indexOf("--END ") > 0
					&& lines[lines.length - 1].indexOf(" KEY--") > 0) {
				keyPEMSpec = keyPEMSpec.substring(keyPEMSpec.indexOf(lines[1]), keyPEMSpec.length() - lines[lines.length - 1].length() - 1);
			}
		}
		return generatePublicKey(algorithm, providerName, new X509EncodedKeySpec(Base64Decoder.decode(keyPEMSpec)));
	}

	/**
	 * Decodes a PEM encoded public key specification and returns the <code>{@link PrivateKey}</code>.
	 * 
	 * <p>
	 * The PEM format represents DER-encoded data in a printable format. Traditionally, PEM encoding simply base64-encodes DER-encoded data and adds a
	 * simple header and footer.
	 * </p>
	 * 
	 * <p>
	 * For example, with header/footer delimiters, a PEM encoded private key might be:
	 * </p>
	 * 
	 * <pre>
	 * -----BEGIN PRIVATE KEY-----
	 * MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAKAMsHzpziR5n+UhalvyX1w1cC2w
	 * oCtqqQAX1lamrofMwy0qTz+W2g4g6DMiAD0PwmqaTvJ7UsxW9jmTAebSe6oKOYOQxUTXI+WG3c9B
	 * hx2hQj36ClhFRF5Zc0oY8y75RzEH63qUKsekQjXbjRnzGcQ9K2i+USemJPWjW63tuxixAgMBAAEC
	 * gYAZaQpYOrKs3daCBWUihf+X3zAZQPKdEgkU57Py+/G3w821DQOZ//RMy/Kxs5NAHpFqZWdlXikO
	 * IjxjdbCWmhJh81Gvnwz1BArGaYwrQO0uTH0qbEN9SyBcuBAD8nNfVpgqKdK7Ye4lyewdRB8C+T5J
	 * Uc89MCR4kQt09j0mzUw1MQJBANvDJcR64tOQDNPu0ehF6fRZhwSYD+LDv0Qih4owqL9R00hXuPDm
	 * PRyn25TMN91WTeFTirmJspWHHgy63f45jdUCQQC6cOGbEbggXmvFZYKGfW0ChJrgJFMx99RW6kLn
	 * Jw9E3rylAgZD8MXfK1yCb1HtL5TEMqE9DkxPCQPbkIYQjGFtAkAjxcRkE0zQ+2XbKcjpclf++oPL
	 * 76TGWO7NfIFrsTgGzJ8D66OjMxdHjttjgUqmsOHEiADQ6uUzCeeOUuzH8T5xAkEAjudg9ZQiVqUo
	 * 4/fHkUBoIsrzTyRopF86YZhTyYuV14sGe0/O75qIgGNjGBMtb6jN1YidMAhakyXs0Am5yMthDQJA
	 * MVW2SBPHqRfHe7wTJxOxqNM/2dMwQD0eKPUHaviios+n33Dnfqk7uPFFYVKmxeBq5E3Gsq7qv9sl
	 * O67Ms20ZBg==
	 * -----END PRIVATE KEY-----
	 * </pre>
	 * 
	 * @param keyPEMSpec
	 *            the encoded key PEM specification, which may be delimited by a PEM header/footer.
	 * @return the reconsituted key
	 * @throws SecurityException
	 *             if an error occurs decoding the key specification
	 * @see #encodePublicOrPrivateKeyToPEMString(Key, boolean)
	 * @see #encodeKeyPairToPEMStrings(KeyPair, boolean)
	 */
	public static PrivateKey decodePrivateKeyPEMString(String keyPEMSpec) throws SecurityException {
		return decodePrivateKeyPEMString(DEFAULT_ASYMMETRIC_ALGORITHM, null, keyPEMSpec);
	}

	/**
	 * Decodes a PEM encoded private key specification using the specified algorithm (which was originally used to generate the key) and returns the
	 * <code>{@link PrivateKey}</code>.
	 * 
	 * <p>
	 * The PEM format represents DER-encoded data in a printable format. Traditionally, PEM encoding simply base64-encodes DER-encoded data and adds a
	 * simple header and footer.
	 * </p>
	 * 
	 * <p>
	 * For example, with header/footer delimiters, a PEM encoded private key might be:
	 * </p>
	 * 
	 * <pre>
	 * -----BEGIN PRIVATE KEY-----
	 * MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAKAMsHzpziR5n+UhalvyX1w1cC2w
	 * oCtqqQAX1lamrofMwy0qTz+W2g4g6DMiAD0PwmqaTvJ7UsxW9jmTAebSe6oKOYOQxUTXI+WG3c9B
	 * hx2hQj36ClhFRF5Zc0oY8y75RzEH63qUKsekQjXbjRnzGcQ9K2i+USemJPWjW63tuxixAgMBAAEC
	 * gYAZaQpYOrKs3daCBWUihf+X3zAZQPKdEgkU57Py+/G3w821DQOZ//RMy/Kxs5NAHpFqZWdlXikO
	 * IjxjdbCWmhJh81Gvnwz1BArGaYwrQO0uTH0qbEN9SyBcuBAD8nNfVpgqKdK7Ye4lyewdRB8C+T5J
	 * Uc89MCR4kQt09j0mzUw1MQJBANvDJcR64tOQDNPu0ehF6fRZhwSYD+LDv0Qih4owqL9R00hXuPDm
	 * PRyn25TMN91WTeFTirmJspWHHgy63f45jdUCQQC6cOGbEbggXmvFZYKGfW0ChJrgJFMx99RW6kLn
	 * Jw9E3rylAgZD8MXfK1yCb1HtL5TEMqE9DkxPCQPbkIYQjGFtAkAjxcRkE0zQ+2XbKcjpclf++oPL
	 * 76TGWO7NfIFrsTgGzJ8D66OjMxdHjttjgUqmsOHEiADQ6uUzCeeOUuzH8T5xAkEAjudg9ZQiVqUo
	 * 4/fHkUBoIsrzTyRopF86YZhTyYuV14sGe0/O75qIgGNjGBMtb6jN1YidMAhakyXs0Am5yMthDQJA
	 * MVW2SBPHqRfHe7wTJxOxqNM/2dMwQD0eKPUHaviios+n33Dnfqk7uPFFYVKmxeBq5E3Gsq7qv9sl
	 * O67Ms20ZBg==
	 * -----END PRIVATE KEY-----
	 * </pre>
	 * 
	 * @param algorithm
	 *            the algorithm that was used to originally generate the key pair. If null, the default algorithm employed by this utility will be
	 *            assumed ({@link #DEFAULT_ASYMMETRIC_ALGORITHM}).
	 * @param keyPEMSpec
	 *            the encoded key PEM specification, which may be delimited by a PEM header/footer.
	 * @return the reconsituted key
	 * @throws SecurityException
	 *             if an error occurs decoding the key specification
	 * @see #encodePublicOrPrivateKeyToPEMString(Key, boolean)
	 * @see #encodeKeyPairToPEMStrings(KeyPair, boolean)
	 */
	public static PrivateKey decodePrivateKeyPEMString(String algorithm, String keyPEMSpec) throws SecurityException {
		return decodePrivateKeyPEMString(algorithm, null, keyPEMSpec);
	}

	/**
	 * Decodes a PEM encoded private key specification using the specified algorithm (which was originally used to generate the key) and provider and
	 * returns the <code>{@link PrivateKey}</code>.
	 * 
	 * <p>
	 * The PEM format represents DER-encoded data in a printable format. Traditionally, PEM encoding simply base64-encodes DER-encoded data and adds a
	 * simple header and footer.
	 * </p>
	 * 
	 * <p>
	 * For example, with header/footer delimiters, a PEM encoded private key might be:
	 * </p>
	 * 
	 * <pre>
	 * -----BEGIN PRIVATE KEY-----
	 * MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAKAMsHzpziR5n+UhalvyX1w1cC2w
	 * oCtqqQAX1lamrofMwy0qTz+W2g4g6DMiAD0PwmqaTvJ7UsxW9jmTAebSe6oKOYOQxUTXI+WG3c9B
	 * hx2hQj36ClhFRF5Zc0oY8y75RzEH63qUKsekQjXbjRnzGcQ9K2i+USemJPWjW63tuxixAgMBAAEC
	 * gYAZaQpYOrKs3daCBWUihf+X3zAZQPKdEgkU57Py+/G3w821DQOZ//RMy/Kxs5NAHpFqZWdlXikO
	 * IjxjdbCWmhJh81Gvnwz1BArGaYwrQO0uTH0qbEN9SyBcuBAD8nNfVpgqKdK7Ye4lyewdRB8C+T5J
	 * Uc89MCR4kQt09j0mzUw1MQJBANvDJcR64tOQDNPu0ehF6fRZhwSYD+LDv0Qih4owqL9R00hXuPDm
	 * PRyn25TMN91WTeFTirmJspWHHgy63f45jdUCQQC6cOGbEbggXmvFZYKGfW0ChJrgJFMx99RW6kLn
	 * Jw9E3rylAgZD8MXfK1yCb1HtL5TEMqE9DkxPCQPbkIYQjGFtAkAjxcRkE0zQ+2XbKcjpclf++oPL
	 * 76TGWO7NfIFrsTgGzJ8D66OjMxdHjttjgUqmsOHEiADQ6uUzCeeOUuzH8T5xAkEAjudg9ZQiVqUo
	 * 4/fHkUBoIsrzTyRopF86YZhTyYuV14sGe0/O75qIgGNjGBMtb6jN1YidMAhakyXs0Am5yMthDQJA
	 * MVW2SBPHqRfHe7wTJxOxqNM/2dMwQD0eKPUHaviios+n33Dnfqk7uPFFYVKmxeBq5E3Gsq7qv9sl
	 * O67Ms20ZBg==
	 * -----END PRIVATE KEY-----
	 * </pre>
	 * 
	 * @param algorithm
	 *            the algorithm that was used to originally generate the key pair. If null, the default algorithm employed by this utility will be
	 *            assumed ({@link #DEFAULT_ASYMMETRIC_ALGORITHM}).
	 * @param providerName
	 *            the name of the cryptography provider to use, such as "BC" for Bouncy Castle, which may be null
	 * @param keyPEMSpec
	 *            the encoded key PEM specification, which may be delimited by a PEM header/footer.
	 * @return the reconsituted key
	 * @throws SecurityException
	 *             if an error occurs decoding the key specification
	 * @see #encodePublicOrPrivateKeyToPEMString(Key, boolean)
	 * @see #encodeKeyPairToPEMStrings(KeyPair, boolean)
	 */
	public static PrivateKey decodePrivateKeyPEMString(String algorithm, String providerName, String keyPEMSpec) throws SecurityException {
		String lines[] = StringUtil.splitIntoLines(keyPEMSpec);
		if (lines.length > 2) {
			if (lines[0].indexOf("--BEGIN ") > 0 && lines[0].indexOf(" KEY--") > 0 && lines[lines.length - 1].indexOf("--END ") > 0
					&& lines[lines.length - 1].indexOf(" KEY--") > 0) {
				keyPEMSpec = keyPEMSpec.substring(keyPEMSpec.indexOf(lines[1]), keyPEMSpec.length() - lines[lines.length - 1].length() - 1);
			}
		}
		return generatePrivateKey(algorithm, providerName, new PKCS8EncodedKeySpec(Base64Decoder.decode(keyPEMSpec)));
	}

	/**
	 * Decodes PEM encoded public and private key specifications and produces the <code>{@link KeyPair}</code> of the two.
	 * 
	 * <p>
	 * The PEM format represents DER-encoded data in a printable format. Traditionally, PEM encoding simply base64-encodes DER-encoded data and adds a
	 * simple header and footer.
	 * </p>
	 * 
	 * <p>
	 * For example, with header/footer delimiters, a PEM encoded private key might be:
	 * </p>
	 * 
	 * <pre>
	 * -----BEGIN PRIVATE KEY-----
	 * MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAKAMsHzpziR5n+UhalvyX1w1cC2w
	 * oCtqqQAX1lamrofMwy0qTz+W2g4g6DMiAD0PwmqaTvJ7UsxW9jmTAebSe6oKOYOQxUTXI+WG3c9B
	 * hx2hQj36ClhFRF5Zc0oY8y75RzEH63qUKsekQjXbjRnzGcQ9K2i+USemJPWjW63tuxixAgMBAAEC
	 * gYAZaQpYOrKs3daCBWUihf+X3zAZQPKdEgkU57Py+/G3w821DQOZ//RMy/Kxs5NAHpFqZWdlXikO
	 * IjxjdbCWmhJh81Gvnwz1BArGaYwrQO0uTH0qbEN9SyBcuBAD8nNfVpgqKdK7Ye4lyewdRB8C+T5J
	 * Uc89MCR4kQt09j0mzUw1MQJBANvDJcR64tOQDNPu0ehF6fRZhwSYD+LDv0Qih4owqL9R00hXuPDm
	 * PRyn25TMN91WTeFTirmJspWHHgy63f45jdUCQQC6cOGbEbggXmvFZYKGfW0ChJrgJFMx99RW6kLn
	 * Jw9E3rylAgZD8MXfK1yCb1HtL5TEMqE9DkxPCQPbkIYQjGFtAkAjxcRkE0zQ+2XbKcjpclf++oPL
	 * 76TGWO7NfIFrsTgGzJ8D66OjMxdHjttjgUqmsOHEiADQ6uUzCeeOUuzH8T5xAkEAjudg9ZQiVqUo
	 * 4/fHkUBoIsrzTyRopF86YZhTyYuV14sGe0/O75qIgGNjGBMtb6jN1YidMAhakyXs0Am5yMthDQJA
	 * MVW2SBPHqRfHe7wTJxOxqNM/2dMwQD0eKPUHaviios+n33Dnfqk7uPFFYVKmxeBq5E3Gsq7qv9sl
	 * O67Ms20ZBg==
	 * -----END PRIVATE KEY-----
	 * </pre>
	 * 
	 * @param publicKeyPEMSpec
	 *            the encoded public key PEM specification, which may be delimited by a PEM header/footer.
	 * @param privateKeyPEMSpec
	 *            the encoded private key PEM specification, which may be delimited by a PEM header/footer.
	 * @return a key pair, consisting of the public and private keys decoded from their PEM encoded formats
	 * @throws SecurityException
	 *             if an error occurs decoding the key specifications
	 * @see #encodePublicOrPrivateKeyToPEMString(Key, boolean)
	 * @see #encodeKeyPairToPEMStrings(KeyPair, boolean)
	 */
	public static KeyPair decodePEMStringsToKeyPair(String publicKeyPEMSpec, String privateKeyPEMSpec) throws SecurityException {
		return decodePEMStringsToKeyPair(DEFAULT_ASYMMETRIC_ALGORITHM, null, publicKeyPEMSpec, privateKeyPEMSpec);
	}

	/**
	 * Decodes PEM encoded public and private key specifications and produces the <code>{@link KeyPair}</code> of the two.
	 * 
	 * <p>
	 * The PEM format represents DER-encoded data in a printable format. Traditionally, PEM encoding simply base64-encodes DER-encoded data and adds a
	 * simple header and footer.
	 * </p>
	 * 
	 * <p>
	 * For example, with header/footer delimiters, a PEM encoded private key might be:
	 * </p>
	 * 
	 * <pre>
	 * -----BEGIN PRIVATE KEY-----
	 * MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAKAMsHzpziR5n+UhalvyX1w1cC2w
	 * oCtqqQAX1lamrofMwy0qTz+W2g4g6DMiAD0PwmqaTvJ7UsxW9jmTAebSe6oKOYOQxUTXI+WG3c9B
	 * hx2hQj36ClhFRF5Zc0oY8y75RzEH63qUKsekQjXbjRnzGcQ9K2i+USemJPWjW63tuxixAgMBAAEC
	 * gYAZaQpYOrKs3daCBWUihf+X3zAZQPKdEgkU57Py+/G3w821DQOZ//RMy/Kxs5NAHpFqZWdlXikO
	 * IjxjdbCWmhJh81Gvnwz1BArGaYwrQO0uTH0qbEN9SyBcuBAD8nNfVpgqKdK7Ye4lyewdRB8C+T5J
	 * Uc89MCR4kQt09j0mzUw1MQJBANvDJcR64tOQDNPu0ehF6fRZhwSYD+LDv0Qih4owqL9R00hXuPDm
	 * PRyn25TMN91WTeFTirmJspWHHgy63f45jdUCQQC6cOGbEbggXmvFZYKGfW0ChJrgJFMx99RW6kLn
	 * Jw9E3rylAgZD8MXfK1yCb1HtL5TEMqE9DkxPCQPbkIYQjGFtAkAjxcRkE0zQ+2XbKcjpclf++oPL
	 * 76TGWO7NfIFrsTgGzJ8D66OjMxdHjttjgUqmsOHEiADQ6uUzCeeOUuzH8T5xAkEAjudg9ZQiVqUo
	 * 4/fHkUBoIsrzTyRopF86YZhTyYuV14sGe0/O75qIgGNjGBMtb6jN1YidMAhakyXs0Am5yMthDQJA
	 * MVW2SBPHqRfHe7wTJxOxqNM/2dMwQD0eKPUHaviios+n33Dnfqk7uPFFYVKmxeBq5E3Gsq7qv9sl
	 * O67Ms20ZBg==
	 * -----END PRIVATE KEY-----
	 * </pre>
	 * 
	 * @param algorithm
	 *            the algorithm that was used to originally generate the key pair. If null, the default algorithm employed by this utility will be
	 *            assumed ({@link #DEFAULT_ASYMMETRIC_ALGORITHM}).
	 * @param publicKeyPEMSpec
	 *            the encoded public key PEM specification, which may be delimited by a PEM header/footer.
	 * @param privateKeyPEMSpec
	 *            the encoded private key PEM specification, which may be delimited by a PEM header/footer.
	 * @return a key pair, consisting of the public and private keys decoded from their PEM encoded formats
	 * @throws SecurityException
	 *             if an error occurs decoding the key specifications
	 * @see #encodePublicOrPrivateKeyToPEMString(Key, boolean)
	 * @see #encodeKeyPairToPEMStrings(KeyPair, boolean)
	 */
	public static KeyPair decodePEMStringsToKeyPair(String algorithm, String publicKeyPEMSpec, String privateKeyPEMSpec) throws SecurityException {
		return decodePEMStringsToKeyPair(algorithm, null, publicKeyPEMSpec, privateKeyPEMSpec);
	}

	/**
	 * Decodes PEM encoded public and private key specifications and produces the <code>{@link KeyPair}</code> of the two.
	 * 
	 * <p>
	 * The PEM format represents DER-encoded data in a printable format. Traditionally, PEM encoding simply base64-encodes DER-encoded data and adds a
	 * simple header and footer.
	 * </p>
	 * 
	 * <p>
	 * For example, with header/footer delimiters, a PEM encoded private key might be:
	 * </p>
	 * 
	 * <pre>
	 * -----BEGIN PRIVATE KEY-----
	 * MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAKAMsHzpziR5n+UhalvyX1w1cC2w
	 * oCtqqQAX1lamrofMwy0qTz+W2g4g6DMiAD0PwmqaTvJ7UsxW9jmTAebSe6oKOYOQxUTXI+WG3c9B
	 * hx2hQj36ClhFRF5Zc0oY8y75RzEH63qUKsekQjXbjRnzGcQ9K2i+USemJPWjW63tuxixAgMBAAEC
	 * gYAZaQpYOrKs3daCBWUihf+X3zAZQPKdEgkU57Py+/G3w821DQOZ//RMy/Kxs5NAHpFqZWdlXikO
	 * IjxjdbCWmhJh81Gvnwz1BArGaYwrQO0uTH0qbEN9SyBcuBAD8nNfVpgqKdK7Ye4lyewdRB8C+T5J
	 * Uc89MCR4kQt09j0mzUw1MQJBANvDJcR64tOQDNPu0ehF6fRZhwSYD+LDv0Qih4owqL9R00hXuPDm
	 * PRyn25TMN91WTeFTirmJspWHHgy63f45jdUCQQC6cOGbEbggXmvFZYKGfW0ChJrgJFMx99RW6kLn
	 * Jw9E3rylAgZD8MXfK1yCb1HtL5TEMqE9DkxPCQPbkIYQjGFtAkAjxcRkE0zQ+2XbKcjpclf++oPL
	 * 76TGWO7NfIFrsTgGzJ8D66OjMxdHjttjgUqmsOHEiADQ6uUzCeeOUuzH8T5xAkEAjudg9ZQiVqUo
	 * 4/fHkUBoIsrzTyRopF86YZhTyYuV14sGe0/O75qIgGNjGBMtb6jN1YidMAhakyXs0Am5yMthDQJA
	 * MVW2SBPHqRfHe7wTJxOxqNM/2dMwQD0eKPUHaviios+n33Dnfqk7uPFFYVKmxeBq5E3Gsq7qv9sl
	 * O67Ms20ZBg==
	 * -----END PRIVATE KEY-----
	 * </pre>
	 * 
	 * @param algorithm
	 *            the algorithm that was used to originally generate the key pair. If null, the default algorithm employed by this utility will be
	 *            assumed ({@link #DEFAULT_ASYMMETRIC_ALGORITHM}).
	 * @param providerName
	 *            the name of the cryptography provider to use, such as "BC" for Bouncy Castle, which may be null
	 * @param publicKeyPEMSpec
	 *            the encoded public key PEM specification, which may be delimited by a PEM header/footer.
	 * @param privateKeyPEMSpec
	 *            the encoded private key PEM specification, which may be delimited by a PEM header/footer.
	 * @return a key pair, consisting of the public and private keys decoded from their PEM encoded formats
	 * @throws SecurityException
	 *             if an error occurs decoding the key specifications
	 * @see #encodePublicOrPrivateKeyToPEMString(Key, boolean)
	 * @see #encodeKeyPairToPEMStrings(KeyPair, boolean)
	 */
	public static KeyPair decodePEMStringsToKeyPair(String algorithm, String providerName, String publicKeyPEMSpec, String privateKeyPEMSpec)
			throws SecurityException {
		return new KeyPair(decodePublicKeyPEMString(algorithm, providerName, publicKeyPEMSpec), decodePrivateKeyPEMString(algorithm, providerName,
				privateKeyPEMSpec));
	}

	/**
	 * Decodes a PEM encoded public or private key specification using the specified algorithm (which was originally used to generate the key) and
	 * returns the <code>{@link Key}</code>.
	 * 
	 * <p>
	 * The PEM format represents DER-encoded data in a printable format. Traditionally, PEM encoding simply base64-encodes DER-encoded data and adds a
	 * simple header and footer.
	 * </p>
	 * 
	 * <p>
	 * For example, with header/footer delimiters, a PEM encoded public key might be:
	 * </p>
	 * 
	 * <pre>
	 * -----BEGIN PUBLIC KEY-----
	 * MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAKAMsHzpziR5n+UhalvyX1w1cC2w
	 * oCtqqQAX1lamrofMwy0qTz+W2g4g6DMiAD0PwmqaTvJ7UsxW9jmTAebSe6oKOYOQxUTXI+WG3c9B
	 * hx2hQj36ClhFRF5Zc0oY8y75RzEH63qUKsekQjXbjRnzGcQ9K2i+USemJPWjW63tuxixAgMBAAEC
	 * gYAZaQpYOrKs3daCBWUihf+X3zAZQPKdEgkU57Py+/G3w821DQOZ//RMy/Kxs5NAHpFqZWdlXikO
	 * IjxjdbCWmhJh81Gvnwz1BArGaYwrQO0uTH0qbEN9SyBcuBAD8nNfVpgqKdK7Ye4lyewdRB8C+T5J
	 * Uc89MCR4kQt09j0mzUw1MQJBANvDJcR64tOQDNPu0ehF6fRZhwSYD+LDv0Qih4owqL9R00hXuPDm
	 * PRyn25TMN91WTeFTirmJspWHHgy63f45jdUCQQC6cOGbEbggXmvFZYKGfW0ChJrgJFMx99RW6kLn
	 * Jw9E3rylAgZD8MXfK1yCb1HtL5TEMqE9DkxPCQPbkIYQjGFtAkAjxcRkE0zQ+2XbKcjpclf++oPL
	 * 76TGWO7NfIFrsTgGzJ8D66OjMxdHjttjgUqmsOHEiADQ6uUzCeeOUuzH8T5xAkEAjudg9ZQiVqUo
	 * 4/fHkUBoIsrzTyRopF86YZhTyYuV14sGe0/O75qIgGNjGBMtb6jN1YidMAhakyXs0Am5yMthDQJA
	 * MVW2SBPHqRfHe7wTJxOxqNM/2dMwQD0eKPUHaviios+n33Dnfqk7uPFFYVKmxeBq5E3Gsq7qv9sl
	 * O67Ms20ZBg==
	 * -----END PUBLIC KEY-----
	 * </pre>
	 * 
	 * @param algorithm
	 *            the algorithm that was used to originally generate the key pair. If null, the default algorithm employed by this utility will be
	 *            assumed ({@link #DEFAULT_ASYMMETRIC_ALGORITHM}).
	 * @param providerName
	 *            the name of the cryptography provider to use, such as "BC" for Bouncy Castle, which may be null
	 * @param keyPEMSpec
	 *            a string containing a PEM encoded public or private key; an attempt will be made to decode a public then a private key from the
	 *            resource. The resource must contain a valid PEM encoded key with or without PEM key headers.
	 * @return the reconsituted key
	 * @throws SecurityException
	 *             if an error occurs decoding the key specification
	 * @see #encodePublicOrPrivateKeyToPEMString(Key, boolean)
	 * @see #encodeKeyPairToPEMStrings(KeyPair, boolean)
	 */
	public static Key decodePublicOrPrvateKeyPEMString(String algorithm, String providerName, String keyPEMSpec) throws SecurityException {
		Key key = null;
		try {
			key = decodePublicKeyPEMString(algorithm, providerName, keyPEMSpec);
		} catch (SecurityException ignoreIfNotPublicKeyEx) {
		}

		if (key == null) {
			try {
				key = decodePrivateKeyPEMString(algorithm, providerName, keyPEMSpec);
			} catch (SecurityException ignoreIfNotPublicKeyEx) {
			}
		}

		if (key == null) {
			throw new SecurityException("The specified PEM encoded key [" + key
					+ "] does not appear to contain a valid public or private PEM encoded key.");
		}

		return key;
	}

	/**
	 * Reconstructs a public/private key pair from their encoded, persisted representations which were originally generated using the specified
	 * algorithm.
	 * 
	 * @param algorithm
	 *            the algorithm that was used to originally generate the key pair. If null, the default algorithm employed by this utility will be
	 *            assumed ({@link #DEFAULT_ASYMMETRIC_ALGORITHM}).
	 * @param providerName
	 *            the name of the cryptography provider to use, such as "BC" for Bouncy Castle, which may be null
	 * @param publicKeySpec
	 *            the public key specification (as that returned by a call to <code>PublicKey.getEncoded</code>)
	 * @param privateKeySpec
	 *            the private key specification (as that returned by a call to <code>PrivateKey.getEncoded</code>)
	 * @return a key pair that comprises the reconstructed public and private keys.
	 * @throws SecurityException
	 *             if an error occurs generating the key pair
	 * @see #DEFAULT_ASYMMETRIC_ALGORITHM
	 */
	public static KeyPair generateKeyPair(String algorithm, String providerName, EncodedKeySpec publicKeySpec, EncodedKeySpec privateKeySpec)
			throws SecurityException {
		try {
			if (algorithm == null) {
				algorithm = DEFAULT_ASYMMETRIC_ALGORITHM;
			}
			KeyFactory keyFactory = providerName != null ? KeyFactory.getInstance(getKeyTypeFromAlgorithm(algorithm), providerName) : KeyFactory
					.getInstance(getKeyTypeFromAlgorithm(algorithm));
			PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
			PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);
			return new KeyPair(publicKey, privateKey);
		} catch (Throwable th) {
			throw new SecurityException(
					"An unexpected error occured reconstructing the public/private keys from their encoded specification [algorithm=" + algorithm
							+ ", " + "providerName=" + providerName + "]: ", th);
		}
	}

	/**
	 * Reconstructs a public key from its encoded key specification. The key was originally generated using the specified key generation algorithm,
	 * <code>{@link #DEFAULT_ASYMMETRIC_ALGORITHM}</code>.
	 * 
	 * @param providerName
	 *            the name of the cryptography provider to use, such as "BC" for Bouncy Castle, which may be null
	 * @param publicKeySpec
	 *            the public key specification (as that returned by a call to <code>PublicKey.getEncoded</code>)
	 * @return the public key, reconstructed from its encoded key specification.
	 * @throws SecurityException
	 *             if an error occurs generating the key
	 * @see #DEFAULT_ASYMMETRIC_ALGORITHM
	 */
	public static PublicKey generatePublicKey(String providerName, EncodedKeySpec publicKeySpec) throws SecurityException {
		return generatePublicKey(DEFAULT_ASYMMETRIC_ALGORITHM, providerName, publicKeySpec);
	}

	/**
	 * Reconstructs a public key from its encoded key specification. The key was originally generated using the specified key generation algorithm,
	 * <code>DEFAULT_ASYMMETRIC_ALGORITHM</code>.
	 * 
	 * <p>
	 * The default provider will be used to reconstruct the key.
	 * </p>
	 * 
	 * @param publicKeySpec
	 *            the public key specification (as that returned by a call to <code>PublicKey.getEncoded</code>)
	 * @return the public key, reconstructed from its encoded key specification.
	 * @throws SecurityException
	 *             if an error occurs generating the key
	 * @see #DEFAULT_ASYMMETRIC_ALGORITHM
	 */
	public static PublicKey generatePublicKey(EncodedKeySpec publicKeySpec) throws SecurityException {
		return generatePublicKey(DEFAULT_ASYMMETRIC_ALGORITHM, null, publicKeySpec);
	}

	/**
	 * Reconstructs a public key from its encoded key specification. The key was originally generated using the specified key generation algorithm and
	 * the specified preferred provider will be used in the reconstruction.
	 * 
	 * @param algorithm
	 *            the algorithm that was used to originally generate the key
	 * @param providerName
	 *            the name of the cryptography provider to use, such as "BC" for Bouncy Castle, which may be null
	 * @param publicKeySpec
	 *            the public key specification (as that returned by a call to <code>PublicKey.getEncoded</code>)
	 * @return the public key, reconstructed from its encoded key specification.
	 * @throws SecurityException
	 *             if an error occurs generating the key
	 */
	public static PublicKey generatePublicKey(String algorithm, String providerName, EncodedKeySpec publicKeySpec) throws SecurityException {
		try {
			if (algorithm == null) {
				algorithm = DEFAULT_ASYMMETRIC_ALGORITHM;
			}
			KeyFactory keyFactory = providerName != null ? KeyFactory.getInstance(getKeyTypeFromAlgorithm(algorithm), providerName) : KeyFactory
					.getInstance(getKeyTypeFromAlgorithm(algorithm));
			PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
			return publicKey;
		} catch (Throwable th) {
			throw new SecurityException(
					"An unexpected error occured reconstructing the public key from its encoded specification " + "[algorithm=" + algorithm
							+ ", providerName=" + providerName + ", encodedKeyBase64=" + Base64Encoder.encode(publicKeySpec.getEncoded()) + "]: ", th);
		}
	}

	/**
	 * Reconstructs a private key from its encoded key specification. The key was originally generated using the specified key generation algorithm,
	 * <code>DEFAULT_ASYMMETRIC_ALGORITHM</code>.
	 * 
	 * @param providerName
	 *            the name of the cryptography provider to use, such as "BC" for Bouncy Castle, which may be null
	 * @param privateKeySpec
	 *            the private key specification (as that returned by a call to <code>PrivateKey.getEncoded</code>)
	 * @return the public key, reconstructed from its encoded key specification.
	 * @throws SecurityException
	 *             if an error occurs generating the key
	 * @see #DEFAULT_ASYMMETRIC_ALGORITHM
	 */
	public static PrivateKey generatePrivateKey(String providerName, EncodedKeySpec privateKeySpec) throws SecurityException {
		return generatePrivateKey(DEFAULT_ASYMMETRIC_ALGORITHM, providerName, privateKeySpec);
	}

	/**
	 * Reconstructs a private key from its encoded key specification. The key was originally generated using the specified key generation algorithm,
	 * <code>DEFAULT_ASYMMETRIC_ALGORITHM</code>.
	 * 
	 * <p>
	 * The default provider will be used to reconstruct the key.
	 * </p>
	 * 
	 * @param privateKeySpec
	 *            the private key specification (as that returned by a call to <code>PrivateKey.getEncoded</code>)
	 * @return the private key, reconstructed from its encoded key specification.
	 * @throws SecurityException
	 *             if an error occurs generating the key
	 * @see #DEFAULT_ASYMMETRIC_ALGORITHM
	 */
	public static PrivateKey generatePrivateKey(EncodedKeySpec privateKeySpec) throws SecurityException {
		return generatePrivateKey(DEFAULT_ASYMMETRIC_ALGORITHM, null, privateKeySpec);
	}

	/**
	 * Reconstructs a private key from its encoded key specification. The key was originally generated using the specified key generation algorithm
	 * and the specified preferred provider will be used in the reconstruction.
	 * 
	 * @param algorithm
	 *            the algorithm that was used to originally generate the key, or null if the default algorithm, employed by this utility, is to be
	 *            used.
	 * @param providerName
	 *            the name of the cryptography provider to use, such as "BC" for Bouncy Castle, which may be null
	 * @param privateKeySpec
	 *            the private key specification (as that returned by a call to <code>PrivateKey.getEncoded</code>)
	 * @return the private key, reconstructed from its encoded key specification.
	 * @throws SecurityException
	 *             if an error occurs generating the key
	 */
	public static PrivateKey generatePrivateKey(String algorithm, String providerName, EncodedKeySpec privateKeySpec) throws SecurityException {
		try {
			if (algorithm == null) {
				algorithm = DEFAULT_ASYMMETRIC_ALGORITHM;
			}
			KeyFactory keyFactory = providerName != null ? KeyFactory.getInstance(getKeyTypeFromAlgorithm(algorithm), providerName) : KeyFactory
					.getInstance(getKeyTypeFromAlgorithm(algorithm));
			PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);
			return privateKey;
		} catch (Throwable th) {
			throw new SecurityException("An unexpected error occured reconstructing the private key from its encoded specification " + "[algorithm="
					+ algorithm + ", providerName=" + providerName + ", encodedKeyBase64=" + Base64Encoder.encode(privateKeySpec.getEncoded())
					+ "]: ", th);
		}
	}

	/**
	 * Reconstructs a public/private key pair from their encoded, persisted representations which were originally generated using the default
	 * algorithm employed by this utility ({@link #DEFAULT_ASYMMETRIC_ALGORITHM}) and the default provider.
	 * 
	 * @param publicKeySpec
	 *            the public key specification (as that returned by a call to <code>PublicKey.getEncoded</code>)
	 * @param privateKeySpec
	 *            the private key specification (as that returned by a call to <code>PrivateKey.getEncoded</code>)
	 * @return a key pair that comprises the reconstructed public and private keys.
	 * @throws SecurityException
	 *             if an error occurs generating the key pair
	 */
	public static KeyPair generateKeyPair(EncodedKeySpec publicKeySpec, EncodedKeySpec privateKeySpec) throws SecurityException {
		return generateKeyPair(DEFAULT_ASYMMETRIC_ALGORITHM, null, publicKeySpec, privateKeySpec);
	}

	/**
	 * Reconstructs a public/private key pair from their encoded, persisted representations which were originally generated using the specified
	 * algorithm.
	 * 
	 * @param algorithm
	 *            the algorithm that was used to originally generate the key pair. If null, the default algorithm employed by this utility will be
	 *            assumed ({@link #DEFAULT_ASYMMETRIC_ALGORITHM}).
	 * @param publicKeySpec
	 *            the public key specification (as that returned by a call to <code>PublicKey.getEncoded</code>)
	 * @param privateKeySpec
	 *            the private key specification (as that returned by a call to <code>PrivateKey.getEncoded</code>)
	 * @return a key pair that comprises the reconstructed public and private keys.
	 * @throws SecurityException
	 *             if an error occurs generating the key pair
	 */
	public static KeyPair generateKeyPair(String algorithm, EncodedKeySpec publicKeySpec, EncodedKeySpec privateKeySpec) throws SecurityException {
		return generateKeyPair(algorithm, null, publicKeySpec, privateKeySpec);
	}

	/**
	 * Encrypts binary content using a specified algorithm and key.
	 * 
	 * @param algorithm
	 *            the name of the encryption algorithm to use
	 * @param key
	 *            the key to use
	 * @param data
	 *            the binary content to be encrypted
	 * @return the encrypted cypher of the specified data
	 * @throws SecurityException
	 *             if an unexpected error occurs during encryption
	 * @see #decrypt(String, Key, byte[])
	 */
	public static byte[] encrypt(String algorithm, Key key, byte[] data) throws SecurityException {
		return encrypt(algorithm, null, data, key);
	}

	/**
	 * Decrypts binary content using a specified algorithm and key.
	 * 
	 * @param algorithm
	 *            the name of the decryption algorithm to use
	 * @param key
	 *            the key to use
	 * @param data
	 *            the binary content to be decrypted
	 * @return the decrypted cypher of the specified data
	 * @throws SecurityException
	 *             if an unexpected error occurs during decryption
	 * @see #encrypt(String, Key, byte[])
	 */
	public static byte[] decrypt(String algorithm, Key key, byte[] data) throws SecurityException {
		return decrypt(algorithm, null, data, key);
	}

	/**
	 * Encrypts binary content using a specified algorithm and key.
	 * 
	 * @param algorithm
	 *            the name of the asymmetric encryption algorithm to use.
	 * @param providerName
	 *            the name of the cryptography provider to use, such as "BC" for Bouncy Castle, which may be null
	 * @param data
	 *            the binary content to be encrypted
	 * @param key
	 *            the key to use
	 * @return the encrypted cypher of the specified data
	 * @throws SecurityException
	 *             if an unexpected error occurs during encryption
	 * @see #decrypt(String, String, byte[], Key)
	 */
	public static byte[] encrypt(String algorithm, String providerName, byte[] data, Key key) throws SecurityException {
		try {
			// Get the cipher object for the specified algorithm
			Cipher cipher = providerName != null ? Cipher.getInstance(algorithm, providerName) : Cipher.getInstance(algorithm);

			// Now encrypt the data using the public key
			cipher.init(Cipher.ENCRYPT_MODE, key);

			// AlgorithmParameterSpec aps = new IvParameterSpec(theIVp);
			// cipher.init(Cipher.ENCRYPT_MODE, key, aps);
			return encrypt(cipher, data, key);
		} catch (Throwable th) {
			throw new SecurityException("An unexpected error occured encrypting the data: ", th);
		}
	}

	public static byte[] encrypt(Cipher cipher, byte[] data, Key key) throws SecurityException {
		try {
			return cipher.doFinal(data);
			// ByteArrayOutputStream outputBAOS = new ByteArrayOutputStream(data.length * 2);
			// crypt(cipher, key, new ByteArrayInputStream(data), outputBAOS, IOUtil.DEFAULT_TRANSFER_BUF_SIZE);
			// return outputBAOS.toByteArray();
		} catch (Throwable th) {
			throw new SecurityException("An unexpected error occured encrypting the data: ", th);
		}
	}

	/**
	 * Decrypts binary content using a specified algorithm and key.
	 * 
	 * @param algorithm
	 *            the name of the asymmetric encryption algorithm to use
	 * @param providerName
	 *            the name of the cryptography provider to use, such as "BC" for Bouncy Castle, which may be null
	 * @param key
	 *            the key to use
	 * @return the encrypted cypher of the specified data
	 * @throws SecurityException
	 *             if an unexpected error occurs during decryption
	 * @see #encrypt(String, String, byte[], Key)
	 */
	public static byte[] decrypt(String algorithm, String providerName, byte[] data, Key key) throws SecurityException {
		try {
			// Get the cipher object for the specified algorithm
			Cipher cipher = providerName != null ? Cipher.getInstance(algorithm, providerName) : Cipher.getInstance(algorithm);

			// Now encrypt the data using the public key
			cipher.init(Cipher.DECRYPT_MODE, key);
			return decrypt(cipher, data, key);
		} catch (Throwable th) {
			throw new SecurityException("An unexpected error occured decrypting the data: ", th);
		}
	}

	public static byte[] decrypt(Cipher cipher, byte[] data, Key key) throws SecurityException {
		try {
			return cipher.doFinal(data);
			// ByteArrayOutputStream outputBAOS = new ByteArrayOutputStream(data.length * 2);
			// crypt(cipher, key, new ByteArrayInputStream(data), outputBAOS, IOUtil.DEFAULT_TRANSFER_BUF_SIZE);
			// return outputBAOS.toByteArray();
		} catch (Throwable th) {
			throw new SecurityException("An unexpected error occured decrypting the data: ", th);
		}
	}

	/**
	 * Encrypts binary content using a specified algorithm and key.
	 * 
	 * <p>
	 * A default transfer buffer size of <code>{@link IOUtil#DEFAULT_TRANSFER_BUF_SIZE}</code> is used for the input stream block transfer size.
	 * </p>
	 * 
	 * @param algorithm
	 *            the name of the encryption algorithm to use.
	 * @param key
	 *            the key to use
	 * @param file
	 *            the file containing the binary content to be encrypted
	 * @return the encrypted cypher of the specified data
	 * @throws SecurityException
	 *             if an unexpected error occurs during decryption
	 * @see #decrypt(String, Key, File)
	 */
	public static byte[] encrypt(String algorithm, Key key, File file) throws SecurityException {
		return encrypt(algorithm, null, key, file, IOUtil.DEFAULT_TRANSFER_BUF_SIZE);
	}

	/**
	 * Decrypts binary content using a specified algorithm and key.
	 * 
	 * <p>
	 * A default transfer buffer size of <code>{@link IOUtil#DEFAULT_TRANSFER_BUF_SIZE}</code> is used for the input stream block transfer size.
	 * </p>
	 * 
	 * @param algorithm
	 *            the name of the decryption algorithm to use.
	 * @param key
	 *            the key to use
	 * @param file
	 *            the file containing the binary content to be decrypted
	 * @return the decrypted data
	 * @throws SecurityException
	 *             if an unexpected error occurs during decryption
	 * @see #encrypt(String, Key, File)
	 */
	public static byte[] decrypt(String algorithm, Key key, File file) throws SecurityException {
		return decrypt(algorithm, null, key, file, IOUtil.DEFAULT_TRANSFER_BUF_SIZE);
	}

	/**
	 * Encrypts binary content using a specified algorithm and key.
	 * 
	 * @param algorithm
	 *            the name of the encryption algorithm to use.
	 * @param key
	 *            the key to use
	 * @param file
	 *            the file containing the binary content to be encrypted
	 * @param bufferSize
	 *            the input transfer buffer size of the buffer to use during the transfer for efficiency.
	 * @return the encrypted cypher of the specified data
	 * @throws SecurityException
	 *             if an unexpected error occurs during encryption
	 * @see #decrypt(String, Key, File, int)
	 */
	public static byte[] encrypt(String algorithm, Key key, File file, int bufferSize) throws SecurityException {
		return encrypt(algorithm, null, key, file, bufferSize);
	}

	/**
	 * Decrypts binary content using a specified algorithm and key.
	 * 
	 * @param algorithm
	 *            the name of the decryption algorithm to use.
	 * @param key
	 *            the key to use
	 * @param file
	 *            the file containing the binary content to be decrypted
	 * @param bufferSize
	 *            the input transfer buffer size of the buffer to use during the transfer for efficiency.
	 * @return the encrypted cypher of the specified data
	 * @throws SecurityException
	 *             if an unexpected error occurs during decryption
	 * @see #encrypt(String, Key, File, int)
	 */
	public static byte[] decrypt(String algorithm, Key key, File file, int bufferSize) throws SecurityException {
		return decrypt(algorithm, null, key, file, bufferSize);
	}

	/**
	 * Encrypts binary content using a specified algorithm and key.
	 * 
	 * <p>
	 * A default transfer buffer size of <code>{@link IOUtil#DEFAULT_TRANSFER_BUF_SIZE}</code> is used for the input stream block transfer size.
	 * </p>
	 * 
	 * @param algorithm
	 *            the name of the encryption algorithm to use.
	 * @param providerName
	 *            the name of the cryptography provider to use, such as "BC" for Bouncy Castle, which may be null
	 * @param key
	 *            the key to use
	 * @param file
	 *            the file containing the binary content to be encrypted
	 * @return the encrypted cypher of the specified data
	 * @throws SecurityException
	 *             if an unexpected error occurs during encryption
	 * @see #decrypt(String, String, Key, File)
	 */
	public static byte[] encrypt(String algorithm, String providerName, Key key, File file) throws SecurityException {
		return encrypt(algorithm, providerName, key, file, IOUtil.DEFAULT_TRANSFER_BUF_SIZE);
	}

	/**
	 * Decrypts binary content using a specified algorithm and key.
	 * 
	 * <p>
	 * A default transfer buffer size of <code>{@link IOUtil#DEFAULT_TRANSFER_BUF_SIZE}</code> is used for the input stream block transfer size.
	 * </p>
	 * 
	 * @param algorithm
	 *            the name of the decryption algorithm to use.
	 * @param providerName
	 *            the name of the cryptography provider to use, such as "BC" for Bouncy Castle, which may be null
	 * @param key
	 *            the key to use
	 * @param file
	 *            the file containing the binary content to be decrypted
	 * @return the decrypted data
	 * @throws SecurityException
	 *             if an unexpected error occurs during decryption
	 * @see #encrypt(String, String, Key, File)
	 */
	public static byte[] decrypt(String algorithm, String providerName, Key key, File file) throws SecurityException {
		return decrypt(algorithm, providerName, key, file, IOUtil.DEFAULT_TRANSFER_BUF_SIZE);
	}

	/**
	 * Encrypts binary content using a specified algorithm and key.
	 * 
	 * @param algorithm
	 *            the name of the encryption algorithm to use.
	 * @param providerName
	 *            the name of the cryptography provider to use, such as "BC" for Bouncy Castle, which may be null
	 * @param key
	 *            the key to use
	 * @param file
	 *            the file containing the binary content to be encrypted
	 * @param bufferSize
	 *            the input transfer buffer size of the buffer to use during the transfer for efficiency.
	 * @return the encrypted cypher of the specified data
	 * @throws SecurityException
	 *             if an unexpected error occurs during encryption
	 * @see #decrypt(String, String, Key, File, int)
	 */
	public static byte[] encrypt(String algorithm, String providerName, Key key, File file, int bufferSize) throws SecurityException {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			ByteArrayOutputStream baos = new ByteArrayOutputStream((int) file.length());

			encrypt(algorithm, providerName, key, fis, baos, bufferSize);

			return baos.toByteArray();
		} catch (Throwable th) {
			throw new SecurityException("An unexpected error occured encrypting the data: ", th);
		} finally {
			IOUtil.closeIgnoringErrors(fis);
		}
	}

	/**
	 * Decrypts binary content using a specified algorithm and key.
	 * 
	 * @param algorithm
	 *            the name of the decryption algorithm to use.
	 * @param providerName
	 *            the name of the cryptography provider to use, such as "BC" for Bouncy Castle, which may be null
	 * @param key
	 *            the key to use
	 * @param file
	 *            the file containing the binary content to be decrypted
	 * @param bufferSize
	 *            the input transfer buffer size of the buffer to use during the transfer for efficiency.
	 * @return the encrypted cypher of the specified data
	 * @throws SecurityException
	 *             if an unexpected error occurs during decryption
	 * @see #encrypt(String, String, Key, File, int)
	 */
	public static byte[] decrypt(String algorithm, String providerName, Key key, File file, int bufferSize) throws SecurityException {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			ByteArrayOutputStream baos = new ByteArrayOutputStream((int) file.length());

			decrypt(algorithm, providerName, key, fis, baos, bufferSize);

			return baos.toByteArray();
		} catch (Throwable th) {
			throw new SecurityException("An unexpected error occured decrypting the data: ", th);
		} finally {
			IOUtil.closeIgnoringErrors(fis);
		}
	}

	/**
	 * Encrypts binary content using a default algorithm, applicable for the type of key specified: asymmetric or symmetric.
	 * 
	 * <p>
	 * A default transfer buffer size of <code>{@link IOUtil#DEFAULT_TRANSFER_BUF_SIZE}</code> is used for the input stream block transfer size.
	 * </p>
	 * 
	 * @param key
	 *            the key to use
	 * @param inputFile
	 *            the file containing the binary content to be encrypted
	 * @param outputFile
	 *            the detination file to contain the output cipher text
	 * @throws SecurityException
	 *             if an unexpected error occurs during encryption
	 * @see #decrypt(Key, File, File)
	 */
	public static void encrypt(Key key, File inputFile, File outputFile) throws SecurityException {
		encrypt(null, null, key, inputFile, outputFile, IOUtil.DEFAULT_TRANSFER_BUF_SIZE);
	}

	/**
	 * Decrypts binary content using a default algorithm, applicable for the type of key specified: asymmetric or symmetric.
	 * 
	 * <p>
	 * A default transfer buffer size of <code>{@link IOUtil#DEFAULT_TRANSFER_BUF_SIZE}</code> is used for the input stream block transfer size.
	 * </p>
	 * 
	 * @param key
	 *            the key to use
	 * @param inputFile
	 *            the file containing the binary content to be decrypted
	 * @param outputFile
	 *            the detination file to contain the decrypted content
	 * @throws SecurityException
	 *             if an unexpected error occurs during decryption
	 * @see #encrypt(Key, File, File)
	 */
	public static void decrypt(Key key, File inputFile, File outputFile) throws SecurityException {
		decrypt(null, null, key, inputFile, outputFile, IOUtil.DEFAULT_TRANSFER_BUF_SIZE);
	}

	/**
	 * Encrypts binary content using a default algorithm, applicable for the type of key specified: asymmetric or symmetric.
	 * 
	 * @param key
	 *            the key to use
	 * @param inputFile
	 *            the file containing the binary content to be encrypted
	 * @param outputFile
	 *            the detination file to contain the output cipher text
	 * @param bufferSize
	 *            the input transfer buffer size of the buffer to use during the transfer for efficiency.
	 * @throws SecurityException
	 *             if an unexpected error occurs during encryption
	 * @see #decrypt(Key, File, File, int)
	 */
	public static void encrypt(Key key, File inputFile, File outputFile, int bufferSize) throws SecurityException {
		encrypt(null, null, key, inputFile, outputFile, bufferSize);
	}

	/**
	 * Decrypts binary content using a default algorithm, applicable for the type of key specified: asymmetric or symmetric.
	 * 
	 * @param key
	 *            the key to use
	 * @param inputFile
	 *            the file containing the binary content to be decrypted
	 * @param outputFile
	 *            the detination file to contain the decrypted content
	 * @param bufferSize
	 *            the input transfer buffer size of the buffer to use during the transfer for efficiency.
	 * @throws SecurityException
	 *             if an unexpected error occurs during decryption
	 * @see #encrypt(Key, File, File, int)
	 */
	public static void decrypt(Key key, File inputFile, File outputFile, int bufferSize) throws SecurityException {
		decrypt(null, null, key, inputFile, outputFile, bufferSize);
	}

	/**
	 * Encrypts binary content using a specified algorithm and key.
	 * 
	 * <p>
	 * A default transfer buffer size of <code>{@link IOUtil#DEFAULT_TRANSFER_BUF_SIZE}</code> is used for the input stream block transfer size.
	 * </p>
	 * 
	 * @param algorithm
	 *            the name of the encryption algorithm to use.
	 * @param key
	 *            the key to use
	 * @param inputFile
	 *            the file containing the binary content to be encrypted
	 * @param outputFile
	 *            the detination file to contain the output cipher text
	 * @throws SecurityException
	 *             if an unexpected error occurs during encryption
	 * @see #decrypt(String, Key, File, File)
	 */
	public static void encrypt(String algorithm, Key key, File inputFile, File outputFile) throws SecurityException {
		encrypt(algorithm, null, key, inputFile, outputFile, IOUtil.DEFAULT_TRANSFER_BUF_SIZE);
	}

	/**
	 * Decrypts binary content using a default algorithm, applicable for the type of key specified: asymmetric or symmetric.
	 * 
	 * <p>
	 * A default transfer buffer size of <code>{@link IOUtil#DEFAULT_TRANSFER_BUF_SIZE}</code> is used for the input stream block transfer size.
	 * </p>
	 * 
	 * @param algorithm
	 *            the name of the decryption algorithm to use.
	 * @param key
	 *            the key to use
	 * @param inputFile
	 *            the file containing the binary content to be decrypted
	 * @param outputFile
	 *            the detination file to contain the decrypted content
	 * @throws SecurityException
	 *             if an unexpected error occurs during decryption
	 * @see #encrypt(String, Key, File, File)
	 */
	public static void decrypt(String algorithm, Key key, File inputFile, File outputFile) throws SecurityException {
		decrypt(algorithm, null, key, inputFile, outputFile, IOUtil.DEFAULT_TRANSFER_BUF_SIZE);
	}

	/**
	 * Encrypts binary content using a specified algorithm and key.
	 * 
	 * @param algorithm
	 *            the name of the encryption algorithm to use.
	 * @param key
	 *            the key to use
	 * @param inputFile
	 *            the file containing the binary content to be encrypted
	 * @param outputFile
	 *            the detination file to contain the output cipher text
	 * @param bufferSize
	 *            the input transfer buffer size of the buffer to use during the transfer for efficiency.
	 * @throws SecurityException
	 *             if an unexpected error occurs during encryption
	 * @see #decrypt(String, Key, File, File, int)
	 */
	public static void encrypt(String algorithm, Key key, File inputFile, File outputFile, int bufferSize) throws SecurityException {
		encrypt(algorithm, null, key, inputFile, outputFile, bufferSize);
	}

	/**
	 * Decrypts binary content using a default algorithm, applicable for the type of key specified: asymmetric or symmetric.
	 * 
	 * @param algorithm
	 *            the name of the decryption algorithm to use.
	 * @param key
	 *            the key to use
	 * @param inputFile
	 *            the file containing the binary content to be decrypted
	 * @param outputFile
	 *            the detination file to contain the decrypted content
	 * @param bufferSize
	 *            the input transfer buffer size of the buffer to use during the transfer for efficiency.
	 * @throws SecurityException
	 *             if an unexpected error occurs during decryption
	 * @see #encrypt(String, Key, File, File, int)
	 */
	public static void decrypt(String algorithm, Key key, File inputFile, File outputFile, int bufferSize) throws SecurityException {
		decrypt(algorithm, null, key, inputFile, outputFile, bufferSize);
	}

	/**
	 * Encrypts binary content using a specified algorithm and key.
	 * 
	 * <p>
	 * A default transfer buffer size of <code>{@link IOUtil#DEFAULT_TRANSFER_BUF_SIZE}</code> is used for the input stream block transfer size.
	 * </p>
	 * 
	 * @param algorithm
	 *            the name of the encryption algorithm to use.
	 * @param providerName
	 *            the name of the cryptography provider to use, such as "BC" for Bouncy Castle, which may be null
	 * @param key
	 *            the key to use
	 * @param inputFile
	 *            the file containing the binary content to be encrypted
	 * @param outputFile
	 *            the detination file to contain the output cipher text
	 * @throws SecurityException
	 *             if an unexpected error occurs during encryption
	 * @see #decrypt(String, String, Key, File, File)
	 */
	public static void encrypt(String algorithm, String providerName, Key key, File inputFile, File outputFile) throws SecurityException {
		encrypt(algorithm, providerName, key, inputFile, outputFile, IOUtil.DEFAULT_TRANSFER_BUF_SIZE);
	}

	/**
	 * Decrypts binary content using a specified algorithm and key.
	 * 
	 * <p>
	 * A default transfer buffer size of <code>{@link IOUtil#DEFAULT_TRANSFER_BUF_SIZE}</code> is used for the input stream block transfer size.
	 * </p>
	 * 
	 * @param algorithm
	 *            the name of the decryption algorithm to use.
	 * @param providerName
	 *            the name of the cryptography provider to use, such as "BC" for Bouncy Castle, which may be null
	 * @param key
	 *            the key to use
	 * @param inputFile
	 *            the file containing the binary content to be decrypted
	 * @param outputFile
	 *            the detination file to contain the output cipher text
	 * @throws SecurityException
	 *             if an unexpected error occurs during decryption
	 * @see #encrypt(String, String, Key, File, File)
	 */
	public static void decrypt(String algorithm, String providerName, Key key, File inputFile, File outputFile) throws SecurityException {
		decrypt(algorithm, providerName, key, inputFile, outputFile, IOUtil.DEFAULT_TRANSFER_BUF_SIZE);
	}

	/**
	 * Encrypts binary content using a specified algorithm and key.
	 * 
	 * @param algorithm
	 *            the name of the encryption algorithm to use.
	 * @param providerName
	 *            the name of the cryptography provider to use, such as "BC" for Bouncy Castle, which may be null
	 * @param key
	 *            the key to use
	 * @param inputFile
	 *            the file containing the binary content to be encrypted
	 * @param outputFile
	 *            the detination file to contain the output cipher text
	 * @param bufferSize
	 *            the input transfer buffer size of the buffer to use during the transfer for efficiency.
	 * @throws SecurityException
	 *             if an unexpected error occurs during encryption
	 * @see #decrypt(String, String, Key, File, File, int)
	 */
	public static void encrypt(String algorithm, String providerName, Key key, File inputFile, File outputFile, int bufferSize)
			throws SecurityException {
		FileInputStream fis = null;
		FileOutputStream fos = null;
		try {
			fis = new FileInputStream(inputFile);
			fos = new FileOutputStream(outputFile);

			encrypt(algorithm, providerName, key, fis, fos, bufferSize);
		} catch (Throwable th) {
			throw new SecurityException("An unexpected error occured encrypting the data: ", th);
		} finally {
			IOUtil.closeIgnoringErrors(fis);
			IOUtil.closeIgnoringErrors(fos);
		}
	}

	/**
	 * Decrypts binary content using a specified algorithm and key.
	 * 
	 * @param algorithm
	 *            the name of the decryption algorithm to use.
	 * @param providerName
	 *            the name of the cryptography provider to use, such as "BC" for Bouncy Castle, which may be null
	 * @param key
	 *            the key to use
	 * @param inputFile
	 *            the file containing the binary content to be decrypted
	 * @param outputFile
	 *            the detination file to contain the output cipher text
	 * @param bufferSize
	 *            the input transfer buffer size of the buffer to use during the transfer for efficiency.
	 * @throws SecurityException
	 *             if an unexpected error occurs during decryption
	 * @see #encrypt(String, String, Key, File, File, int)
	 */
	public static void decrypt(String algorithm, String providerName, Key key, File inputFile, File outputFile, int bufferSize)
			throws SecurityException {
		FileInputStream fis = null;
		FileOutputStream fos = null;
		try {
			fis = new FileInputStream(inputFile);
			fos = new FileOutputStream(outputFile);

			decrypt(algorithm, providerName, key, fis, fos, bufferSize);
		} catch (Throwable th) {
			throw new SecurityException("An unexpected error occured decrypting the data: ", th);
		} finally {
			IOUtil.closeIgnoringErrors(fis);
			IOUtil.closeIgnoringErrors(fos);
		}
	}

	/**
	 * Encrypts binary input stream content to the specified outputStream using the default algorithm employed by this utility and the specified key.
	 * The streams are <u>not</u> closed by this operation.
	 * 
	 * @param key
	 *            the key to use
	 * @param inputStream
	 *            the stream containing the binary content to be encrypted
	 * @param outputStream
	 *            the destination output stream to contain the cypher text
	 * @throws SecurityException
	 *             if an unexpected error occurs during encryption
	 * @see #DEFAULT_ASYMMETRIC_ALGORITHM
	 * @see #DEFAULT_SYMMETRIC_ALGORITHM
	 * @see #decrypt(Key, InputStream inputStream, OutputStream outputStream, int)
	 */
	public static void encrypt(Key key, InputStream inputStream, OutputStream outputStream, int transferBufferSize) throws SecurityException {
		encrypt(null, null, key, inputStream, outputStream, transferBufferSize);
	}

	/**
	 * Encrypts binary input stream content to the specified outputStream using the default algorithm employed by this utility and the specified key.
	 * The streams are <u>not</u> closed by this operation.
	 * 
	 * @param key
	 *            the key to use
	 * @param inputStream
	 *            the stream containing the binary content to be encrypted
	 * @param outputStream
	 *            the destination output stream to contain the decrypted data
	 * @throws SecurityException
	 *             if an unexpected error occurs during decryption
	 * @see #DEFAULT_ASYMMETRIC_ALGORITHM
	 * @see #DEFAULT_SYMMETRIC_ALGORITHM
	 * @see #encrypt(Key, InputStream inputStream, OutputStream outputStream, int)
	 */
	public static void decrypt(Key key, InputStream inputStream, OutputStream outputStream, int transferBufferSize) throws SecurityException {
		decrypt(null, null, key, inputStream, outputStream, transferBufferSize);
	}

	/**
	 * Encrypts binary input stream content to the specified outputStream using the specified algorithm and specified key. The streams are <u>not</u>
	 * closed by this operation.
	 * 
	 * @param algorithm
	 *            the name of the encryption algorithm to use, which may be null. If null, the default symmetric or asymmetric cryptography algorithm
	 *            is used, depending on whether the key specified is a symmetric or asymmetric key.
	 * @param key
	 *            the key to use
	 * @param inputStream
	 *            the stream containing the binary content to be encrypted
	 * @param outputStream
	 *            the destination output stream to contain the cypher text
	 * @throws SecurityException
	 *             if an unexpected error occurs during encryption
	 * @see #DEFAULT_ASYMMETRIC_ALGORITHM
	 * @see #DEFAULT_SYMMETRIC_ALGORITHM
	 * @see #decrypt(String, Key, InputStream inputStream, OutputStream outputStream, int)
	 */
	public static void encrypt(String algorithm, Key key, InputStream inputStream, OutputStream outputStream, int transferBufferSize)
			throws SecurityException {
		encrypt(algorithm, null, key, inputStream, outputStream, transferBufferSize);
	}

	/**
	 * Decrypts binary input stream content to the specified outputStream using the specified algorithm and specified key. The streams are <u>not</u>
	 * closed by this operation.
	 * 
	 * @param algorithm
	 *            the name of the decryption algorithm to use, which may be null. If null, the default symmetric or asymmetric cryptography algorithm
	 *            is used, depending on whether the key specified is a symmetric or asymmetric key.
	 * @param key
	 *            the key to use
	 * @param inputStream
	 *            the stream containing the binary content to be decrypted
	 * @param outputStream
	 *            the destination output stream to contain the decrypted data
	 * @throws SecurityException
	 *             if an unexpected error occurs during decryption
	 * @see #DEFAULT_ASYMMETRIC_ALGORITHM
	 * @see #DEFAULT_SYMMETRIC_ALGORITHM
	 * @see #encrypt(String, Key, InputStream inputStream, OutputStream outputStream, int)
	 */
	public static void decrypt(String algorithm, Key key, InputStream inputStream, OutputStream outputStream, int transferBufferSize)
			throws SecurityException {
		decrypt(algorithm, null, key, inputStream, outputStream, transferBufferSize);
	}

	/**
	 * Encrypts binary input stream content to the specified outputStream using the specified algorithm and the specified key. The streams are
	 * <u>not</u> closed by this operation.
	 * 
	 * @param algorithm
	 *            the name of the encryption algorithm to use, which may be null. If null, the default symmetric or asymmetric cryptography algorithm
	 *            is used, depending on whether the key specified is a symmetric or asymmetric key.
	 * @param providerName
	 *            the name of the cryptography provider to use, such as "BC" for Bouncy Castle, which may be null
	 * @param key
	 *            the key to use
	 * @param inputStream
	 *            the stream containing the binary content to be encrypted
	 * @param outputStream
	 *            the destination output stream to contain the cypher text
	 * @param bufferSize
	 *            the input transfer buffer size of the buffer to use during the transfer for efficiency.
	 * @return the encrypted cypher of the specified data
	 * @throws SecurityException
	 *             if an unexpected error occurs during encryption
	 * @see #DEFAULT_ASYMMETRIC_ALGORITHM
	 * @see #DEFAULT_SYMMETRIC_ALGORITHM
	 * @see #decrypt(String, String, Key,InputStream, OutputStream, int)
	 */
	public static void encrypt(String algorithm, String providerName, Key key, InputStream inputStream, OutputStream outputStream, int bufferSize)
			throws SecurityException {
		boolean isSymmetricAlgorithm = key instanceof SecretKey;
		if (algorithm == null) {
			algorithm = (isSymmetricAlgorithm ? DEFAULT_SYMMETRIC_ALGORITHM : DEFAULT_ASYMMETRIC_ALGORITHM);
		}

		try {
			// Get the cipher object for the specified algorithm
			Cipher cipher = providerName != null ? Cipher.getInstance(algorithm, providerName) : Cipher.getInstance(algorithm);
			// Now encrypt the data using the public key
			cipher.init(Cipher.ENCRYPT_MODE, key);
			crypt(cipher, key, inputStream, outputStream, bufferSize);
		} catch (java.security.GeneralSecurityException secEx) {
			throw new SecurityException(secEx);
		}
	}

	/**
	 * Decrypts binary input stream content to the specified outputStream using the specified algorithm and the specified key. The streams are
	 * <u>not</u> closed by this operation.
	 * 
	 * @param algorithm
	 *            the name of the encryption algorithm to use, which may be null. If null, the default symmetric or asymmetric cryptography algorithm
	 *            is used, depending on whether the key specified is a symmetric or asymmetric key.
	 * @param providerName
	 *            the name of the cryptography provider to use, such as "BC" for Bouncy Castle, which may be null
	 * @param key
	 *            the key to use
	 * @param inputStream
	 *            the stream containing the binary content to be decrypted
	 * @param outputStream
	 *            the destination output stream to contain the decrypted data
	 * @param bufferSize
	 *            the input transfer buffer size of the buffer to use during the transfer for efficiency.
	 * @return the encrypted cypher of the specified data
	 * @throws SecurityException
	 *             if an unexpected error occurs during decryption
	 * @see #DEFAULT_ASYMMETRIC_ALGORITHM
	 * @see #DEFAULT_SYMMETRIC_ALGORITHM
	 * @see #encrypt(String, String, Key,InputStream, OutputStream, int)
	 */
	public static void decrypt(String algorithm, String providerName, Key key, InputStream inputStream, OutputStream outputStream, int bufferSize)
			throws SecurityException {
		boolean isSymmetricAlgorithm = key instanceof SecretKey;
		if (algorithm == null) {
			algorithm = (isSymmetricAlgorithm ? DEFAULT_SYMMETRIC_ALGORITHM : DEFAULT_ASYMMETRIC_ALGORITHM);
		}

		try {
			// Get the cipher object for the specified algorithm
			Cipher cipher = providerName != null ? Cipher.getInstance(algorithm, providerName) : Cipher.getInstance(algorithm);
			// Now encrypt the data using the public key
			cipher.init(Cipher.DECRYPT_MODE, key);
			crypt(cipher, key, inputStream, outputStream, bufferSize);
		} catch (java.security.GeneralSecurityException secEx) {
			throw new SecurityException(secEx);
		}
	}

	/**
	 * Encrypts/Decrypts binary input stream content to the specified outputStream using the specified and configured cipher. The streams are
	 * <u>not</u> closed by this operation.
	 * 
	 * @param cipher
	 *            the cipher to use in the encrypt/decrypt operation; the cipher must have been configured and <u>initialised</u> prior to calling
	 *            this method.
	 * @param key
	 *            the key to use
	 * @param inputStream
	 *            the stream containing the binary content to be encrypted or decrypted
	 * @param outputStream
	 *            the destination output stream to contain the encrypted or decrypted data
	 * @param bufferSize
	 *            the input transfer buffer size of the buffer to use during the transfer for efficiency.
	 * @throws SecurityException
	 *             if an unexpected error occurs during decryption
	 * @see #DEFAULT_ASYMMETRIC_ALGORITHM
	 * @see #DEFAULT_SYMMETRIC_ALGORITHM
	 */
	public static void crypt(Cipher cipher, Key key, InputStream inputStream, final OutputStream outputStream, int bufferSize)
			throws SecurityException {
		boolean isSymmetricAlgorithm = key instanceof SecretKey;
		int blockSize = cipher.getBlockSize();

		// System.out.println("*** alg="+cipher.getAlgorithm()+" blksize="+cipher.getBlockSize()+" provider="+cipher.getProvider());

		try {

			if (isSymmetricAlgorithm || (blockSize == 8) || (blockSize == 0)) {
				CipherOutputStream cos = new CipherOutputStream(new OutputStream() {
					public void close() throws IOException {
						// Don't close the underlying stream - just flush it
						outputStream.flush();
					}

					public void write(byte b[], int off, int len) throws IOException {
						outputStream.write(b, off, len);
					}

					public void write(int b) throws IOException {
						outputStream.write(b);
					}

				}, cipher);
				IOUtil.transfer(inputStream, cos, bufferSize);
				cos.close();
			} else {
				// byte cipherOutputBuf[] = new byte[blockSize];
				byte inputBuf[] = new byte[blockSize];
				int readCount;
				while ((readCount = inputStream.read(inputBuf)) != -1) {
					// try
					// {
					byte cipherText[] = cipher.doFinal(inputBuf, 0, readCount);
					outputStream.write(cipherText);
					// }
					// catch (ShortBufferException shortEx)
					// {
					// // Readjust output buffer size.
					// cipherOutputBuf = new byte[cipher.getOutputSize(inputBuf.length)];
					// int cipherTextLen = cipher.update(inputBuf, 0, readCount, cipherOutputBuf);
					// outputStream.write(cipherOutputBuf, 0, cipherTextLen);
					// }
				}

				// try
				// {
				// int cipherTextLen = cipher.doFinal(cipherOutputBuf, 0);
				// outputStream.write(cipherOutputBuf, 0, cipherTextLen);
				// outputStream.flush();
				// }
				// catch (ShortBufferException shortEx)
				// {
				// // Readjust output buffer size.
				// cipherOutputBuf = new byte[cipher.getOutputSize(inputBuf.length)];
				// int cipherTextLen = cipher.doFinal(cipherOutputBuf, 0);
				// outputStream.write(cipherOutputBuf, 0, cipherTextLen);
				// outputStream.flush();
				// }
			}
		} catch (Throwable th) {
			throw new SecurityException("An unexpected error occured encrypting the data: ", th);
		}
	}

	/**
	 * Encrypts binary content using a default, embedded, symmetric key and algorithm.
	 * 
	 * @param data
	 *            the binary content to be encrypted
	 * @return the encrypted cypher of the specified data
	 * @throws SecurityException
	 *             if an unexpected error occurs during encryption
	 * @see #decrypt(byte[])
	 */
	public static byte[] encrypt(byte[] data) throws SecurityException {
		return encrypt(DEFAULT_SYMMETRIC_ALGORITHM, embeddedSecretKey, data);
	}

	/**
	 * Decrypts binary content using a default, embedded, symmetric key and algorithm.
	 * 
	 * @param encryptedData
	 *            the binary content to be decrypted
	 * @return the decrypted cypher of the specified data
	 * @throws SecurityException
	 *             if an unexpected error occurs during decryption
	 * @see #encrypt(byte[])
	 */
	public static byte[] decrypt(byte[] encryptedData) throws SecurityException {
		return decrypt(DEFAULT_SYMMETRIC_ALGORITHM, embeddedSecretKey, encryptedData);
	}

	/**
	 * Encrypts string content using a default, embedded, symmetric key and algorithm.
	 * 
	 * @param data
	 *            the binary content to be encrypted
	 * @return the encrypted cypher of the specified data
	 * @throws SecurityException
	 *             if an unexpected error occurs during encryption
	 * @see #decrypt(String)
	 */
	public static byte[] encrypt(String data) throws SecurityException {
		return encrypt(data.getBytes());
	}

	/**
	 * Decrypts string content using a default, embedded, symmetric key and algorithm.
	 * 
	 * @param data
	 *            the binary content to be decrypted
	 * @return the decrypted data
	 * @throws SecurityException
	 *             if an unexpected error occurs during decryption
	 * @see #encrypt(String)
	 */
	public static byte[] decrypt(String data) throws SecurityException {
		return decrypt(data.getBytes());
	}

	/**
	 * Encrypts string content using a default, embedded, symmetric key and algorithm.
	 * 
	 * @param data
	 *            the binary content to be encrypted
	 * @param charSetName
	 *            the character set encoding of the specified data
	 * @return the encrypted cypher of the specified data
	 * @throws SecurityException
	 *             if an unexpected error occurs during encryption
	 */
	public static byte[] encrypt(String data, String charSetName) throws SecurityException {
		try {
			return encrypt(data.getBytes(charSetName));
		} catch (UnsupportedEncodingException ex) {
			throw new SecurityException("Unable to encrypt data: ", ex);
		}
	}

	/**
	 * Decrypts binary content using a specified algorithm and key, expecting a valid string as the decrypted result
	 * 
	 * <p>
	 * The system default chaacter encoding is assumed for the resulting decrypted characters.
	 * 
	 * @param encryptedData
	 *            the binary content to be encrypted
	 * @return the decrypted data as a string
	 * @throws SecurityException
	 *             if an unexpected error occurs during encryption
	 */
	public static String decryptToString(byte[] encryptedData) throws SecurityException {
		return new String(decrypt(encryptedData));
	}

	/**
	 * Decrypts binary content using a specified algorithm and key, expecting a valid string as the decrypted result
	 * 
	 * @param encryptedData
	 *            the binary content to be encrypted
	 * @param charSetName
	 *            the character set encoding of the specified data
	 * @return the decrypted data as a string
	 * @throws SecurityException
	 *             if an unexpected error occurs during encryption
	 */
	public static String decryptToString(byte[] encryptedData, String charSetName) throws SecurityException {
		try {
			return new String(decrypt(encryptedData), charSetName);
		} catch (UnsupportedEncodingException ex) {
			throw new SecurityException("Unable to decrypt data as a string [characterSetName=" + charSetName + "]: ", ex);
		}
	}

	/**
	 * Utility method to return all of the cryptograhic service providers registered in the system.
	 * 
	 * @return a list of the cryptograhic service providers
	 */
	public static String[] getServiceTypes() {
		Set result = new HashSet();

		// All all providers
		Provider[] providers = Security.getProviders();
		for (int i = 0; i < providers.length; i++) {
			// Get services provided by each provider
			Set keys = providers[i].keySet();
			for (Iterator it = keys.iterator(); it.hasNext();) {
				String key = (String) it.next();
				key = key.split(" ")[0];

				if (key.startsWith("Alg.Alias.")) {
					// Strip the alias
					key = key.substring(10);
				}
				int ix = key.indexOf('.');
				result.add(key.substring(0, ix));
			}
		}
		return (String[]) result.toArray(new String[result.size()]);
	}

	/**
	 * A utility method which returns a list of the cryptograhic implementations provided by the specified service provider.
	 * 
	 * @param serviceType
	 *            the cryptograhic service for which all imlplementations are to be returned
	 * @return a list of the cryptograhic implementations supported by the specified service provider
	 */
	public static String[] getCryptoImpls(String serviceType) {
		Set result = new HashSet();

		// All all providers
		Provider[] providers = Security.getProviders();
		for (int i = 0; i < providers.length; i++) {
			// Get services provided by each provider
			Set keys = providers[i].keySet();
			for (Iterator it = keys.iterator(); it.hasNext();) {
				String key = (String) it.next();
				key = key.split(" ")[0];

				if (key.startsWith(serviceType + ".")) {
					result.add(key.substring(serviceType.length() + 1));
				} else if (key.startsWith("Alg.Alias." + serviceType + ".")) {
					// This is an alias
					result.add(key.substring(serviceType.length() + 11));
				}
			}
		}
		return (String[]) result.toArray(new String[result.size()]);
	}

	/**
	 * Attempts to locate an return the public/private keypair from the specified keystore using the specified keystore location, keystore type,
	 * keystore password, keypair alias and keypair alias password.
	 * 
	 * @param keystoreFile
	 *            the location of the keystore to load
	 * @param aliasName
	 *            the alias of the keypair entry in the store
	 * @param aliasEntryPassword
	 *            the password for th entry
	 * @return the keypair, or null if the given alias does not exist or does not identify a key entry.
	 * @throws SecurityException
	 *             if an error occurs locating the key entry
	 * @see #getKeyPairFromStore(InputStream, String, String, String, String)
	 * @see KeyStore#getKey(String, char[]) for further information
	 */
	public KeyPair getKeyPairFromStore(File keystoreFile, String keystoreType, String keyStorePassword, String aliasName, String aliasEntryPassword)
			throws SecurityException {
		if (!keystoreFile.exists() || !keystoreFile.canRead()) {
			throw new SecurityException("Unable to read keystore file: [" + keystoreFile.getAbsolutePath()
					+ "]: the file does not exist or is not readable.");
		}

		InputStream keystoreIS = null;
		try {
			keystoreIS = new FileInputStream(keystoreFile);
			return getKeyPairFromStore(keystoreIS, keystoreType, keyStorePassword, aliasName, aliasEntryPassword);
		} catch (Exception ex) {
			throw new SecurityException("Unable to locate keypair in keystore [keystore=" + keystoreFile + ", alias=" + aliasName + "]: ", ex);
		} finally {
			IOUtil.closeIgnoringErrors(keystoreIS);
		}
	}

	/**
	 * Attempts to locate an return the public/private keypair from the specified keystore using the specified keystore location, keystore type,
	 * keystore password, keypair alias and keypair alias password.
	 * 
	 * @param keystoreURL
	 *            the location of the keystore to load
	 * @param aliasName
	 *            the alias of the keypair entry in the store
	 * @param aliasEntryPassword
	 *            the password for th entry
	 * @return the keypair, or null if the given alias does not exist or does not identify a key entry.
	 * @throws SecurityException
	 *             if an error occurs locating the key entry
	 * @see #getKeyPairFromStore(InputStream, String, String, String, String)
	 * @see KeyStore#getKey(String, char[]) for further information
	 */
	public KeyPair getKeyPairFromStore(URL keystoreURL, String keystoreType, String keyStorePassword, String aliasName, String aliasEntryPassword)
			throws SecurityException {
		InputStream keystoreIS = null;
		try {
			keystoreIS = keystoreURL.openStream();
			return getKeyPairFromStore(keystoreIS, keystoreType, keyStorePassword, aliasName, aliasEntryPassword);
		} catch (Exception ex) {
			throw new SecurityException("Unable to locate keypair in keystore [keystore=" + keystoreURL.toExternalForm() + ", alias=" + aliasName
					+ "]: ", ex);
		} finally {
			IOUtil.closeIgnoringErrors(keystoreIS);
		}
	}

	/**
	 * Attempts to locate an return the public/private keypair from the specified keystore using the specified keystore location, keystore type,
	 * keystore password, keypair alias and keypair alias password.
	 * 
	 * @param keystoreIS
	 *            the stream containing the keystore to load; it is up to th caller to close the stream after invoking this method.
	 * @param aliasName
	 *            the alias of the keypair entry in the store
	 * @param aliasEntryPassword
	 *            the password for th entry
	 * @return the keypair, or null if the given alias does not exist or does not identify a key entry.
	 * @throws SecurityException
	 *             if an error occurs locating the key entry
	 * @see #getKeyPairFromStore(KeyStore, String, String)
	 * @see KeyStore#getKey(String, char[]) for further information
	 */
	public KeyPair getKeyPairFromStore(InputStream keystoreIS, String keystoreType, String keyStorePassword, String aliasName,
			String aliasEntryPassword) throws SecurityException {
		try {
			KeyStore keystore = KeyStore.getInstance(keystoreType);
			keystore.load(keystoreIS, keyStorePassword.toCharArray());
			KeyPair keyPair = getKeyPairFromStore(keystore, aliasName, aliasEntryPassword);
			return keyPair;
		} catch (Exception ex) {
			throw new SecurityException("Unable to locate keypair in keystore [alias=" + aliasName + "]: ", ex);
		}
	}

	/**
	 * Attempts to locate an return the public/private keypair from the specified keystore using the specified alias and alias password.
	 * 
	 * @param keystore
	 *            the keystore containing the public/private key
	 * @param aliasEntryPassword
	 *            the password for th entry
	 * @return the keypair, or null if the given alias does not exist or does not identify a key entry.
	 * @throws SecurityException
	 *             if an error occurs locating the key entry
	 * @see KeyStore#getKey(String, char[]) for further information
	 */
	public KeyPair getKeyPairFromStore(KeyStore keystore, String aliasName, String aliasEntryPassword) throws SecurityException {
		try {
			// Get private key
			Key key = keystore.getKey(aliasName, aliasEntryPassword.toCharArray());
			if (key instanceof PrivateKey) {
				// Get certificate of public key
				Certificate cert = keystore.getCertificate(aliasName);

				// Get public key
				PublicKey publicKey = cert.getPublicKey();

				// Return a key pair
				return new KeyPair(publicKey, (PrivateKey) key);
			}
		} catch (GeneralSecurityException ex) {
			throw new SecurityException("Unable to locate keypair in keystore [keystore=" + keystore + ", alias=" + aliasName + "]: ", ex);
		}

		return null;
	}

	private static String getKeyTypeFromAlgorithm(String algorithm) {
		// Transformation is of the form algorithm/mode/padding
		int modePos = algorithm.indexOf('/');
		return modePos > 0 ? algorithm.substring(0, modePos) : algorithm;
	}

	public static void main(String args[]) {
		Provider[] pd = Security.getProviders();
		System.out.println("\n\nCryptographic Providers:");
		for (int i = 1; i <= (pd.length); i++) {
			System.out.println("Provider[" + i + "]=" + pd[i - 1].getName());
		}

		System.out.println("\n\nService types:");
		String serviceTypes[] = getServiceTypes();
		for (int n = 0; n < serviceTypes.length; n++) {
			System.out.println(serviceTypes[n]);
		}

		System.out.println("\n\nCryptographic implementations:");
		String cryptoImpls[] = getCryptoImpls("Cipher");
		for (int n = 0; n < cryptoImpls.length; n++) {
			System.out.println(cryptoImpls[n]);
		}

		System.out.println("\n\ntest:Generating key pair [algorithm=" + DEFAULT_ASYMMETRIC_ALGORITHM + ", key size=" + DEFAULT_GENERATED_ASYMMETRIC_KEY_SIZE
				+ "] ...");
		KeyPair keyPair = CryptoUtil.generateKeyPair();
		System.out.println("Encoding keys ...");
		byte[][] encodedKeys = generateEncodedPublicPrivateKeySpecs(keyPair);
		String publicKeyBase64 = Base64Encoder.encode(encodedKeys[0]);
		String privateKeyBase64 = Base64Encoder.encode(encodedKeys[1]);
		System.out.println("Public Key Base64 representation:\n_____STARTS BELOW____________________________________\n" + publicKeyBase64
				+ "\n_____ENDS ABOVE  ____________________________________");
		System.out.println("Private Key Base64 representation:\n_____STARTS BELOW____________________________________\n" + privateKeyBase64
				+ "\n_____ENDS ABOVE  ____________________________________");

		System.out.println("\n\ntest:Generating secret key [algorithm=" + DEFAULT_SYMMETRIC_ALGORITHM + ", key size=" + DEFAULT_GENERATED_ASYMMETRIC_KEY_SIZE
				+ "] ...");
		SecretKey key = generateSymmetricKey();
		System.out.println("Encoding secret key ...");
		byte[] keySpec = key.getEncoded();
		String secretKeyBase64 = Base64Encoder.encode(keySpec);
		System.out.println("Secret Key Base64 representation:\n_____STARTS BELOW____________________________________\n" + secretKeyBase64
				+ "\n_____ENDS ABOVE  ____________________________________");

		String originalText = "Hello World!";
		byte encrypted[] = encrypt(originalText.getBytes());

		System.out.println("Encrypted text (base64) = =" + Base64Encoder.encode(encrypted) + " [original base64="
				+ Base64Encoder.encode(originalText.getBytes()) + ", encrypted len=" + encrypted.length + "]");

		byte decrypted[] = decrypt(encrypted);
		System.out.println("Original =" + originalText + "\nDecrypted =" + (new String(decrypted)));

	}
}
