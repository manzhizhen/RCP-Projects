package cn.sunline.common.util;

import java.io.ByteArrayOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

/**
 * 对string和stream进行Base64编码.
 * <p>
 * 适用于大容量内容进行编码
 * <p>
 * An implementation of Base64 content transfer encoding encoder, as specified by RFC 1521, Section 5.2, Base64 Content-Transfer-Encoding. Refer to <A
 * href="Base64 Content-Transfer-Encoding" alt="RFC 1521 for Base64 Encoding">RFC 1521</A> for further information about the algorithm.
 * </p>
 * 
 * 
 */
public class Base64Encoder extends FilterOutputStream {
	/** The Base64 alphabet. */
	private static final char base64Alphabet[] = new char[] { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q',
			'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r',
			's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/' };

	/** Whether or not to include line breaks in encoded output. */
	private boolean outputLineBreaks = true;

	/** The total number of input bytes encoded. */
	private int inputCharCount;

	/** The remaining bits, not yet encoded. */
	private int remainingBits;

	/**
	 * Creates new Base64Encoder with the specified target output stream.
	 * 
	 * @param os
	 *            the underlying <code>OutputStream</code> where Base64 encoded bytes will be written.
	 */
	public Base64Encoder(OutputStream os) {
		super(os);
	}

	/**
	 * Whether this encoder outputs line breaks (Line Feed, 0x0D) every 76th output character (according to the Base64 encoding specification).
	 * 
	 * @return true if this encoder is to output line breaks
	 */
	public boolean isOutputtingLineBreaks() {
		return outputLineBreaks;
	}

	/**
	 * Whether this encoder outputs line breaks (Line Feed, 0x0D) every 76th output character (according to the Base64 encoding specification).
	 * 
	 * @param newValue
	 *            true if this encoder is to output line breaks
	 */
	public void setOutputtingLineBreaks(boolean newValue) {
		outputLineBreaks = newValue;
	}

	/**
	 * Encodes the specified byte and writes the encoded value to the underlying output stream.
	 * 
	 * @param b
	 *            the byte to encode
	 * @throws IOException
	 *             if an I/O error occurs.
	 */
	public void write(int b) throws IOException {
		// Remove sign-bit on any negative input values or
		// values >
		b = b & 0xff;

		int lookupIdx;
		if (inputCharCount % 3 == 0) {
			// First byte of a new 24-bit input set (4 encoded chars set)
			lookupIdx = b >> 2;
			remainingBits = b & 0x03; // Keep low-order last 2 bits
		} else if (inputCharCount % 3 == 1) {
			// Second byte of a 24-bit input set (4 encoded chars set)
			// Use the remaining 2 bits from last input byte
			lookupIdx = (remainingBits << 4) | (b >> 4);
			remainingBits = b & 0x0f; // Keep low-order last 4 bits
		} else // inputCharCount % 3 == 2
		{
			// Third byte of a 24-bit input set (4 encoded chars set)
			// Use the remaining 4 bits from last input byte, coupled
			// with the 8 input bits means we write to encoded chars
			// this time.
			lookupIdx = (remainingBits << 2) | (b >> 6);
			out.write(base64Alphabet[lookupIdx]);

			lookupIdx = b & 0x3f; // Keep low-order last 6 bits
			remainingBits = 0;
		}
		out.write(base64Alphabet[lookupIdx]);
		inputCharCount++; // Increment number of encoded chars written out

		// ------------------------------------------------------------------------
		// As per RFC 1521, output encoded characters should be broken into lines
		// of 76 characters or 57 (== 76 * 6/8ths) input characters.
		// ------------------------------------------------------------------------
		if (inputCharCount % 57 == 0 && outputLineBreaks) {
			out.write('\n');
		}
	}

	/**
	 * Encodes the specified byte array, from the specified offset and number of bytes, and writes the encoded bytes to the underlying output stream.
	 * 
	 * <p>
	 * Simple calls <code>write(int)</code> for each byte of the array in the range specified.
	 * 
	 * @param b
	 *            the byte array to encode
	 * @param off
	 *            the offset within the array to begin encoding
	 * @param len
	 *            the number of bytes, from the offset specified, to encode
	 * @throws IOException
	 *             if an I/O error occurs.
	 * @see #write(int)
	 */
	public void write(byte b[], int off, int len) throws IOException {
		for (int n = 0; n < len; n++) {
			write(b[off + n]);
		}
	}

	/**
	 * Encodes the specified byte buffer.
	 * 
	 * <p>
	 * Simple calls <code>write(int)</code> for each byte of the buffer, from the current position to the buffer's limit.
	 * 
	 * @param byteBuffer
	 *            the buffer to encode.
	 * @throws IOException
	 *             if an I/O error occurs.
	 * @see #write(int)
	 */
	public void write(ByteBuffer byteBuffer) throws IOException {
		while (byteBuffer.hasRemaining()) {
			write(byteBuffer.get());
		}
	}

	/**
	 * Closes the Base64 encoding stream and underlying output stream.
	 * 
	 * <p>
	 * This method <b>MUST</b> be called in order to ensure that any remaining quadruples are written to the output stream.
	 * 
	 * @see #write(int)
	 * @throws IOException
	 *             if an I/O error occurs.
	 */
	public void close() throws IOException {
		// -----------------------------------------------------------------
		// Flush any remaining elements of the last 24-bit quantum. Base64
		// output must be an integral number of 24 bit quantums with padding
		// of the last quantum to 24 bits, if necessary.
		// Possible cases are that the final quantum of encoding:
		//
		// 1) Was an integral multiple of 24 bits - in which case we need
		// take no further action since all encoded characters have been
		// written out.
		// 2) Is exactly 8 bits - in which case we need to pad with 16-bits
		// which produces 2 encoded characters (12 bits) and two padding
		// "=" characters.
		// 3) Is exactly 16 bits - in which case we need to pad with 8-bits
		// which produces 3 encoded characters (18 bits) and one padding
		// "=" character.
		// -----------------------------------------------------------------
		if (inputCharCount % 3 == 1) // case 2) above?
		{
			out.write(base64Alphabet[remainingBits << 4]);
			out.write('=');
			out.write('=');
		} else if (inputCharCount % 3 == 2) // case 3) above?
		{
			out.write(base64Alphabet[remainingBits << 2]);
			out.write('=');
		}
		super.close();
	}

	/**
	 * Returns the Base64 encoded form of the specified input string(UTF-8字符集).
	 * 
	 * <p>
	 * This method uses the platform's default character encoding to comprise the result base 64 string.
	 * </p>
	 * 
	 * @param inputString
	 *            the string to encode
	 * @return the Base64 encoded form of the input string
	 */
	public static String encode(String inputString) {
		return encode(inputString, "UTF-8", true);
	}

	/**
	 * Returns the Base64 encoded form of the specified input string with line feeds included or excluded from the output.
	 * 
	 * <p>
	 * This method uses the platform's default character encoding to comprise the result base 64 string.
	 * </p>
	 * 
	 * @param inputString
	 *            the string to encode
	 * @param characterEncoding
	 * @param outputLineBreaks
	 *            whether or not line breaks are to be included in the encoded output
	 * @return the Base64 encoded form of the input string
	 */
	public static String encode(String inputString, String characterEncoding, boolean outputLineBreaks) {
		try {
			return encode(inputString.getBytes(characterEncoding), outputLineBreaks);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Returns the Base64 encoded form of the specified bytes.
	 * 
	 * @param bytes
	 *            the bytes to encode
	 * @return a string containing human-readable Base64 encoded form of the bytes.
	 */
	public static String encode(byte[] bytes) {
		return encode(ByteBuffer.wrap(bytes), true);
	}

	/**
	 * Returns the Base64 encoded form of the specified bytes.
	 * 
	 * @param bytes
	 *            the bytes to encode
	 * @param outputLineBreaks
	 *            whether or not line breaks are to be included in the encoded output
	 * @return a string containing human-readable Base64 encoded form of the bytes.
	 */
	public static String encode(byte[] bytes, boolean outputLineBreaks) {
		return encode(ByteBuffer.wrap(bytes), outputLineBreaks);
	}

	/**
	 * Returns the Base64 encoded form of the specified byte buffer.
	 * 
	 * @param byteBuffer
	 *            the byte buffer to encode
	 * @return a string containing human-readable Base64 encoded form of the bytes.
	 */
	public static String encode(ByteBuffer byteBuffer) {
		return encode(byteBuffer, true);
	}

	/**
	 * Returns the Base64 encoded form of the specified byte buffer.
	 * 
	 * @param byteBuffer
	 *            the byte buffer to encode
	 * @param outputLineBreaks
	 *            whether or not line breaks are to be included in the encoded output
	 * @return a string containing human-readable Base64 encoded form of the bytes.
	 */
	public static String encode(ByteBuffer byteBuffer, boolean outputLineBreaks) {
		// Maximum output size will be 137% of input.
		ByteArrayOutputStream baos = new ByteArrayOutputStream((int) (byteBuffer.capacity() * 1.37));
		Base64Encoder encoder = new Base64Encoder(baos);
		encoder.setOutputtingLineBreaks(outputLineBreaks);

		try {
			encoder.write(byteBuffer);
			encoder.close();

			return baos.toString(StringUtil.getDefaultCharacterEncoding());
		} catch (IOException ex) {
			// Should never get here as the I/O streams are in-memory and
			// should not fail.
			throw new RuntimeException("I/O error encountered during Base64 encode operation.", ex);
		}
	}
	
	public static void main(String args[]) {
		if(args.length<1){
			System.out.println("pls input the org-code:");
			return;
		}
		String result = Base64Encoder.encode(args[0]);
		System.out.print(result);
	}
}
