package cn.sunline.common.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

/**
 * 对string和stream进行Base64解码.
 * <p>
 * 适用于大容量内容进行编码
 * 
 */
public class Base64Decoder extends FilterInputStream {
	/** The Base64 alphabet. */
	private static final char base64Alphabet[] = new char[] { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q',
			'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r',
			's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/' };

	/**
	 * US-ASCII to Base64 character set conversion table (alphabet translation). That is, a table of the Base64 characters that are from the US-ASCII
	 * character set.
	 */
	private static final int USAsciiToBase64Charset[] = new int[128];
	static {
		for (int n = 0; n < 64; n++) {
			// Specifiy the Base64 character value for this US-ASCII character value
			USAsciiToBase64Charset[base64Alphabet[n]] = n;
		}
	}

	/** The total number of input bytes encoded. */
	private int inputCharCount;

	/** The remaining bits, not yet encoded. */
	private int remainingBits;

	/** Determines if the stream is closed. */
	private boolean isStreamClosed = false;

	/**
	 * Constructs a new Base64 decoder that reads base64 encoded imput from the specified InputStream.
	 * 
	 * @param is
	 *            the underlying <code>InputStream</code> where Base64 decoded bytes will be written.
	 */
	public Base64Decoder(InputStream is) {
		super(is);
	}

	/**
	 * Reads a byte from the underlying input stream, decodes it and returns the decoded byte.
	 * 
	 * @return a decoded byte from the underlying Base64 encoded stream.
	 * @throws IOException
	 *             if an I/O error occurs.
	 */
	public int read() throws IOException {
		if (isStreamClosed)
			return -1;

		// ------------------------------------------------------------------------
		// As per RFC 1521, ignore any characters outside of the Base64 alphabet.
		// Although a very slight performance hit, this meets spec compliance.
		// ------------------------------------------------------------------------
		int b;
		int base64Char = 0;
		while ((b = in.read()) != -1 && ((Character.isWhitespace((char) b)) || b > 127)
				&& !(b == '=' || (base64Char = USAsciiToBase64Charset[b]) != 0 || b == 'A'))
			;

		if (b == -1) {
			isStreamClosed = true;
			return -1;
		} else if (b == '=') {
			isStreamClosed = true;
			// Consume any remaining padding characters
			while (((b = in.read()) != -1) && b == '=' && inputCharCount % 4 != 3) {
				inputCharCount++;
			}
			return -1;
		} else {
			// Must be US-ASCII character that is in the Base64 alphabet at this point.
			base64Char = USAsciiToBase64Charset[b];
			if (inputCharCount % 4 == 0) {
				// First byte of a new 24-bit encoded input set (3 decoded chars set)
				// Keep all 6 bits and request another byte.
				remainingBits = base64Char;
				inputCharCount++;
				return read();
			} else if (inputCharCount % 4 == 1) {
				// Second byte of a 24-bit encoded input set (3 encoded chars set)
				// Use the remaining 6 bits from last input byte plus 2 from this byte
				b = remainingBits << 2 | base64Char >> 4;
				remainingBits = base64Char & 0x0f; // Save lower 4 bits
			} else if (inputCharCount % 4 == 2) {
				// Third byte of a 24-bit encoded input set (3 encoded chars set)
				// Use the remaining 4 bits from last input byte, coupled
				// with 4 from this byte
				b = remainingBits << 4 | base64Char >> 2;
				remainingBits = base64Char & 0x03; // Save lower 2 bits
			} else if (inputCharCount % 4 == 3) {
				b = remainingBits << 6 | base64Char;
				remainingBits = 0; // No bits left over
			}
			inputCharCount++;

			// System.err.println("*** Returning char="+b);
			// System.exit(99);
			return b;
		}
	}

	/**
	 * Decodes the specified byte array, from the specified offset and number of bytes, reading bytes to decode from the underlying input stream.
	 * 
	 * <p>
	 * Simply calls <code>read(int)</code> for each byte of the array in the range specified.
	 * 
	 * @see #read()
	 * @param b
	 *            the byte array to decode
	 * @param off
	 *            the offset within the array to begin decoding
	 * @param len
	 *            the number of bytes, from the offset specified, to decode
	 * @throws IOException
	 *             if an I/O error occurs.
	 * @return the number of bytes decoded or -1 of End-Of-File encountered. This will actually be 3/4 of the bytes actually read from the underlying
	 *         stream as the process of decoding produces 3 decoded octets for every 4 encoded read.
	 */
	public int read(byte b[], int off, int len) throws IOException {
		if (b.length < (off + len - 1)) {
			throw new IOException("Error during Base64 decoding, Base64Decoder::read(byte[],int,int) - " + "the specified buffer array has length="
					+ b.length + " which is less than " + "the requested ofset=" + off + " and length=" + len);
		}

		int readCount;
		for (readCount = 0; readCount < len; readCount++) {
			int byteRead = read();
			if (byteRead == -1) {
				if (readCount == 0)
					return -1; // First byte EOF then EOF
				else
					break; // Second higher byte returns the size read
			}
			b[off + readCount] = (byte) byteRead;
		}

		return readCount;
	}

	/**
	 * Returns the Base64 decoded form of the specified input string.
	 * 
	 * <p>
	 * This method assumes an encoding of "US-ASCII" (Base64) for the input string bytes.
	 * </p>
	 * 
	 * @param inputString
	 *            the string to decode
	 * @return the Base64 decoded form of the input string
	 * @throws org.beanplanet.core.io.IOException
	 *             containing an <code>UnsupportedException</code> if the US-ASCII encoding is not recognised by the machine. The Java VM is required
	 *             to implement this encoding!
	 */
	public static byte[] decode(String inputString) {
		try {
			return decode(inputString.getBytes("US-ASCII"));
		} catch (UnsupportedEncodingException ex) {
			// This should never happen when in a western country - don't really want to declare it so
			// convert it to a runtime exception and rethrow.
			throw new RuntimeException("Unsupported encoding \"US-ASCII\" during Base64 decode operation.", ex);
		}
	}

	/**
	 * Returns the Base64 decoded form of the specified bytes.
	 * 
	 * @param bytes
	 *            the bytes to decode
	 * @return a string containing human-readable Base64 decoded form of the bytes.
	 * @throws org.beanplanet.core.io.IOException
	 *             if an I/O error occurs during the operation.
	 */
	public static byte[] decode(byte[] bytes) {
		ByteArrayInputStream is = new ByteArrayInputStream(bytes);
		ByteArrayOutputStream os = new ByteArrayOutputStream(bytes.length); // Should be up to 1/1.37 times the size
		Base64Decoder encoder = new Base64Decoder(is);

		try {
			IOUtil.transfer(encoder, os);
			encoder.close();

			return os.toByteArray();
		} catch (IOException ex) {
			// Should never get here as the I/O streams are im-memory and
			// should not fail.
			throw new RuntimeException("I/O error encountered during Base64 decode operation.", ex);
		}
	}
}
