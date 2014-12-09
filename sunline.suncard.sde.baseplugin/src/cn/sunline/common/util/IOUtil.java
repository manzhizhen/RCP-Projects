package cn.sunline.common.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.math.BigInteger;
import java.net.Socket;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.InterruptibleChannel;
import java.nio.charset.Charset;
import java.security.MessageDigest;

/**
 * I/OÁ÷¹¤¾ß
 * 
 */
public class IOUtil {
	/**
	 * The size of any transfer buffer, byte or chracter orientated, used by the transfer utility methods.
	 */
	public static final int DEFAULT_TRANSFER_BUF_SIZE = (int) (32 * 1024);

	/**
	 * Flushes an <code>OutputStream</code> ignoring any errors.
	 * 
	 * @param outputStream
	 *            the stream to flush, which can be null
	 */
	public static void flushIgnoringErrors(OutputStream outputStream) {
		if (outputStream == null)
			return;
		try {
			outputStream.flush();
		} catch (IOException ignoreEx) {
		}
	}

	/**
	 * Closes an input stream ignoring any errors.
	 * 
	 * @param is
	 *            the stream to close, which may be null
	 */
	public static void closeIgnoringErrors(InputStream is) {
		if (is == null)
			return;
		try {
			is.close();
		} catch (java.io.IOException ignoreEx) {
		}
	}

	/**
	 * Closes an output stream ignoring any errors.
	 * 
	 * @param os
	 *            the stream to close, which may be null
	 */
	public static void closeIgnoringErrors(OutputStream os) {
		if (os == null)
			return;
		try {
			os.close();
		} catch (IOException ignoreEx) {
		}
	}

	/**
	 * Closes a reader ignoring any errors.
	 * 
	 * @param reader
	 *            the reader to close, which may be null
	 */
	public static void closeIgnoringErrors(Reader reader) {
		if (reader == null)
			return;
		try {
			reader.close();
		} catch (IOException ignoreEx) {
		}
	}

	/**
	 * Closes a writer ignoring any errors.
	 * 
	 * @param writer
	 *            the writer to close, can be null
	 */
	public static void closeIgnoringErrors(Writer writer) {
		if (writer == null)
			return;
		try {
			writer.close();
		} catch (IOException ignoreEx) {
		}
	}

	/**
	 * Closes a <code>Channel</code> ignoring any errors.
	 * 
	 * @param channel
	 *            the channel to close, can be null
	 */
	public static void closeIgnoringErrors(InterruptibleChannel channel) {
		if (channel == null)
			return;
		try {
			channel.close();
		} catch (IOException ignoreEx) {
		}
	}

	/**
	 * Obtains the <code>String</code> representation of the specified <code>ByteBuffer</code> assuming the UTF-16 encoding.
	 * <p>
	 * On exit, the buffer position is as it was on exit.
	 * 
	 * @param buffer
	 *            the byte buffer whose string value is to be obtained.
	 */
	public static CharSequence byteBufferString(ByteBuffer buffer) {
		return byteBufferString(buffer, "UTF-16");
	}

	/**
	 * Obtains the <code>String</code> representation of the specified <code>ByteBuffer</code> using the specified character encoding.
	 * <p>
	 * On exit, the buffer position is as it was on exit.
	 * 
	 * @param buffer
	 *            the byte buffer whose string value is to be obtained.
	 * @param encoding
	 *            the assumed encoding of the buffer
	 */
	public static CharSequence byteBufferString(ByteBuffer buffer, String encoding) {
		buffer.rewind();

		try {
			Charset charset = Charset.forName(encoding);
			return charset.decode(buffer);
		} finally {
			buffer.rewind();
		}
	}

	/**
	 * Shuts down the input of a <code>Socket</code> ignoring any errors.
	 * 
	 * @param socket
	 *            the socket whose input is to be shutdown, can be null
	 */
	public static void shutdownInputIgnoringErrors(Socket socket) {
		if (socket == null)
			return;
		try {
			socket.shutdownInput();
		} catch (IOException ignoreEx) {
		}
	}

	/**
	 * Shuts down the output of a <code>Socket</code> ignoring any errors.
	 * 
	 * @param socket
	 *            the socket whose output is to be shutdown, can be null
	 */
	public static void shutdownOutputIgnoringErrors(Socket socket) {
		if (socket == null)
			return;
		try {
			socket.shutdownOutput();
		} catch (IOException ignoreEx) {
		}
	}

	/**
	 * Automatically transfers data from the specified <code>URL</code> to the <code>OutputStream</code> until End-Of-File (EOF) is encountered on the
	 * input stream.
	 * <p>
	 * Note that the input and output streams are not closed by this method.
	 * 
	 * @param inputResource
	 *            the input resource from which the data will be read
	 * @param os
	 *            the output stream where the data will be written.
	 * @exception IOException
	 *                thrown if an error occurs during the transfer.
	 */
	public static void transfer(URL inputResource, OutputStream os) throws IOException {
		transfer(inputResource, os, DEFAULT_TRANSFER_BUF_SIZE);
	}

	/**
	 * Automatically transfers data from the specified <code>URL</code> to the <code>OutputStream</code> until End-Of-File (EOF) is encountered on the
	 * input stream.
	 * <p>
	 * Note that the input and output streams are not closed by this method.
	 * 
	 * @param inputResource
	 *            the input resource from which the data will be read
	 * @param os
	 *            the output stream where the data will be written.
	 * @param bufferSize
	 *            the transfer buffer size of the buffer to use during the transfer for efficiency.
	 * @exception IOException
	 *                thrown if an error occurs during the transfer.
	 */
	public static void transfer(URL inputResource, OutputStream os, int bufferSize) throws IOException {
		InputStream resourceIS = null;
		try {
			resourceIS = inputResource.openStream();
			transfer(resourceIS, os, bufferSize);
		} finally {
			closeIgnoringErrors(resourceIS);
		}
	}

	/**
	 * Automatically transfers data from the specified <code>InputStream</code> to the <code>OutputStream</code> until End-Of-File (EOF) is
	 * encountered on the input stream.
	 * <p>
	 * Note that the input and output streams are not closed by this method.
	 * 
	 * @param is
	 *            the input stream from which the data will be read
	 * @param os
	 *            the output stream where the data will be written.
	 * @param bufferSize
	 *            the transfer buffer size of the buffer to use during the transfer for efficiency.
	 * @exception IOException
	 *                thrown if an error occurs during the transfer.
	 */
	public static void transfer(InputStream is, OutputStream os, int bufferSize) throws IOException {
		byte transferBuf[] = new byte[bufferSize];
		int readCount;
		while ((readCount = is.read(transferBuf)) != -1) {
			os.write(transferBuf, 0, readCount);
		}
	}

	/**
	 * Automatically transfers data from the specified <code>URL</code> to the <code>OutputStream</code> until End-Of-File (EOF) is encountered on the
	 * input stream and closes both streams regardless of any successful or erroneous outcome.
	 * 
	 * @param sourceURL
	 *            the URL from which the data will be read
	 * @param os
	 *            the output stream where the data will be written.
	 * @exception IOException
	 *                thrown if an error occurs during the transfer.
	 * @see #transfer(java.io.InputStream, java.io.OutputStream, int)
	 */
	public static void transferAndClose(URL sourceURL, OutputStream os) throws IOException {
		transferAndClose(sourceURL, os, DEFAULT_TRANSFER_BUF_SIZE);
	}

	/**
	 * Automatically transfers data from the specified <code>URL</code> to the <code>OutputStream</code> until End-Of-File (EOF) is encountered on the
	 * input stream and closes both streams regardless of any successful or erroneous outcome.
	 * 
	 * @param sourceURL
	 *            the URL from which the data will be read
	 * @param os
	 *            the output stream where the data will be written.
	 * @param bufferSize
	 *            the transfer buffer size of the buffer to use during the transfer for efficiency.
	 * @exception IOException
	 *                thrown if an error occurs during the transfer.
	 * @see #transfer(java.io.InputStream, java.io.OutputStream, int)
	 */
	public static void transferAndClose(URL sourceURL, OutputStream os, int bufferSize) throws IOException {
		transferAndClose(sourceURL.openStream(), os, bufferSize);
	}

	/**
	 * Automatically transfers data from the specified <code>InputStream</code> to the <code>OutputStream</code> until End-Of-File (EOF) is
	 * encountered on the input stream and closes both streams regardless of any successful or erroneous outcome.
	 * 
	 * @param is
	 *            the input stream from which the data will be read
	 * @param os
	 *            the output stream where the data will be written.
	 * @param bufferSize
	 *            the transfer buffer size of the buffer to use during the transfer for efficiency.
	 * @exception IOException
	 *                thrown if an error occurs during the transfer.
	 * @see #transfer(java.io.InputStream, java.io.OutputStream, int)
	 */
	public static void transferAndClose(InputStream is, OutputStream os, int bufferSize) throws IOException {
		try {
			transfer(is, os, bufferSize);
		} finally {
			closeIgnoringErrors(is);
			closeIgnoringErrors(os);
		}
	}

	/**
	 * Automatically transfers data from the specified <code>InputStream</code> to the <code>OutputStream</code> until End-Of-File (EOF) is
	 * encountered on the input stream.
	 * <p>
	 * Note that the input and output streams are not closed by this method.
	 * 
	 * @param is
	 *            the input stream from which the data will be read
	 * @param os
	 *            the output stream where the data will be written.
	 * @exception IOException
	 *                thrown if an error occurs during the transfer.
	 */
	public static void transfer(InputStream is, OutputStream os) throws IOException {
		transfer(is, os, DEFAULT_TRANSFER_BUF_SIZE);
	}

	/**
	 * Automatically transfers data from the specified <code>InputStream</code> to the <code>OutputStream</code> until End-Of-File (EOF) is
	 * encountered on the input stream and closes both streams regardless of any successful or erroneous outcome.
	 * <p>
	 * Note that the input and output streams are not closed by this method.
	 * 
	 * @param is
	 *            the input stream from which the data will be read
	 * @param os
	 *            the output stream where the data will be written.
	 * @exception IOException
	 *                thrown if an error occurs during the transfer.
	 * @see #transfer(java.io.InputStream, java.io.OutputStream)
	 * @see #transferAndClose(java.io.InputStream, java.io.OutputStream, int)
	 */
	public static void transferAndClose(InputStream is, OutputStream os) throws IOException {
		try {
			transfer(is, os);
		} finally {
			closeIgnoringErrors(is);
			closeIgnoringErrors(os);
		}
	}

	/**
	 * Automatically transfers data from the specified <code>ByteBuffer</code> to the <code>OutputStream</code>.
	 * 
	 * @param buffer
	 *            the buffer containing the data to be transferred
	 * @param os
	 *            the output stream to receive the data
	 * @exception IOException
	 *                thrown if an error occurs during the transfer.
	 */
	public static void transfer(ByteBuffer buffer, OutputStream os) throws IOException {
		while (buffer.hasRemaining()) {
			os.write(buffer.get());
		}
	}

	/**
	 * Automatically transfers data from the specified <code>ByteBuffer</code> to the <code>OutputStream</code> and closes the output stream
	 * regardless of any successful or erroneous outcome.
	 * 
	 * @param buffer
	 *            the buffer containing the data to be transferred
	 * @param os
	 *            the output stream to receive the data
	 * @exception IOException
	 *                thrown if an error occurs during the transfer.
	 * @see #transfer(java.nio.ByteBuffer, java.io.OutputStream)
	 */
	public static void transferAndClose(ByteBuffer buffer, OutputStream os) throws IOException {
		try {
			while (buffer.hasRemaining()) {
				os.write(buffer.get());
			}
		} finally {
			closeIgnoringErrors(os);
		}
	}

	/**
	 * Automatically transfers data from the specified <code>Reader</code> to the <code>Writer</code> until End-Of-File (EOF) is encountered.
	 * <p>
	 * Note that the Reader and Writer are not closed by this method.
	 * 
	 * @param reader
	 *            the reader from which the data will be read
	 * @param writer
	 *            the writer where the data will be written.
	 * @param bufferSize
	 *            the transfer buffer size of the buffer to use during the transfer for efficiency.
	 * @exception IOException
	 *                thrown if an error occurs during the transfer.
	 */
	public static void transfer(Reader reader, Writer writer, int bufferSize) throws IOException {
		char transferBuf[] = new char[bufferSize];
		int readCount;
		while ((readCount = reader.read(transferBuf)) != -1) {
			writer.write(transferBuf, 0, readCount);
		}
	}

	/**
	 * Automatically transfers data from the specified <code>Reader</code> to the <code>Writer</code> until End-Of-File (EOF) is encountered and
	 * closes both the reader and writer regardless of any successful or erroneous outcome.
	 * 
	 * @param reader
	 *            the reader from which the data will be read
	 * @param writer
	 *            the writer where the data will be written.
	 * @param bufferSize
	 *            the transfer buffer size of the buffer to use during the transfer for efficiency.
	 * @exception IOException
	 *                thrown if an error occurs during the transfer.
	 * @see #transfer(java.io.Reader, java.io.Writer, int)
	 */
	public static void transferAndClose(Reader reader, Writer writer, int bufferSize) throws IOException {
		try {
			transfer(reader, writer, bufferSize);
		} finally {
			closeIgnoringErrors(reader);
			closeIgnoringErrors(writer);
		}
	}

	/**
	 * Automatically transfers data from the specified <code>Reader</code> to the <code>Writer</code> until End-Of-File (EOF) is encountered.
	 * <p>
	 * Note that the Reader and Writer are not closed by this method.
	 * 
	 * @param reader
	 *            the reader from which the data will be read
	 * @param writer
	 *            the writer where the data will be written.
	 * @exception IOException
	 *                thrown if an error occurs during the transfer.
	 */
	public static void transfer(Reader reader, Writer writer) throws IOException {
		transfer(reader, writer, DEFAULT_TRANSFER_BUF_SIZE);
	}

	/**
	 * Automatically transfers data from the specified <code>Reader</code> to the <code>Writer</code> until End-Of-File (EOF) is encountered and
	 * closes both the reader and writer regardless of any successful or erroneous outcome.
	 * 
	 * @param reader
	 *            the reader from which the data will be read
	 * @param writer
	 *            the writer where the data will be written.
	 * @exception IOException
	 *                thrown if an error occurs during the transfer.
	 * @see #transfer(java.io.Reader, java.io.Writer, int)
	 */
	public static void transferAndClose(Reader reader, Writer writer) throws IOException {
		try {
			transfer(reader, writer);
		} finally {
			closeIgnoringErrors(reader);
			closeIgnoringErrors(writer);
		}
	}

	/**
	 * Automatically transfers data from the specified source <code>{@link File}</code> to the <code>OutputStream</code> until End-Of-File (EOF) is
	 * encountered on the input stream and closes both streams regardless of any successful or erroneous outcome.
	 * 
	 * @param sourceFile
	 *            the source file from which data will be read
	 * @param destinationFile
	 *            the destination file where the data will be written. If the file exists prior to this operation it will be overwritten.
	 * @exception IOException
	 *                thrown if an error occurs during the transfer.
	 * @see #transfer(java.io.InputStream, java.io.OutputStream, int)
	 */
	public static void transfer(File sourceFile, File destinationFile) throws IOException {
		transfer(sourceFile, destinationFile, DEFAULT_TRANSFER_BUF_SIZE);
	}

	/**
	 * Automatically transfers data from the specified source <code>{@link File}</code> to the <code>OutputStream</code> until End-Of-File (EOF) is
	 * encountered on the input stream and closes both streams regardless of any successful or erroneous outcome.
	 * 
	 * @param sourceFile
	 *            the source file from which data will be read
	 * @param destinationFile
	 *            the destination file where the data will be written.
	 * @param bufferSize
	 *            the size of th transfer buffer to use during the transfer for efficiency.
	 * @exception IOException
	 *                thrown if an error occurs during the transfer.
	 * @see #transfer(java.io.InputStream, java.io.OutputStream, int)
	 */
	public static void transfer(File sourceFile, File destinationFile, int bufferSize) throws IOException {
		transferAndClose(new FileInputStream(sourceFile), new FileOutputStream(destinationFile), bufferSize);
	}

	/**
	 * Reads and returns a line from the specified <code>Reader</code>. A line is considered to be terminated by any one of a line feed ('\n'), a
	 * carriage return ('\r'), or a carriage return followed immediately by a linefeed.
	 * 
	 * @param reader
	 *            the reader from which the line will be read.
	 * @return a line of text, without the line termination characters, or null if End-of-file reached.
	 * @exception IOException
	 *                thrown if an error occurs during the read operation.
	 */
	public static String readLine(Reader reader) throws IOException {
		int ch = reader.read();
		if (ch == '\n')
			return "";

		StringBuffer lineBuf = new StringBuffer();
		while (ch != -1 && ((char) ch) != '\n') {
			lineBuf.append((char) ch);
			ch = reader.read();
		}

		if (lineBuf.length() > 0 && lineBuf.charAt(lineBuf.length() - 1) == '\r')
			lineBuf.deleteCharAt(lineBuf.length() - 1);

		return (lineBuf.length() == 0 ? null : lineBuf.toString());
	}

	/**
	 * Reads and returns a line from the specified <code>InputStream</code>. A line is considered to be terminated by any one of a line feed ('\n'), a
	 * carriage return ('\r'), or a carriage return followed immediately by a linefeed.
	 * 
	 * @param is
	 *            the stream from which the line will be read.
	 * @return a line of text, without the line termination characters, or null if End-of-file reached. The default system character encoding is
	 *         assumed for the input stream.
	 * @exception IOException
	 *                thrown if an error occurs during the read operation.
	 */
	public static String readLine(InputStream is) throws IOException {
		return readLine(is, new ByteArrayOutputStream());
	}

	/**
	 * Reads and returns a line from the specified <code>InputStream</code>. A line is considered to be terminated by any one of a line feed ('\n'), a
	 * carriage return ('\r'), or a carriage return followed immediately by a linefeed.
	 * 
	 * @param is
	 *            the stream from which the line will be read.
	 * @param baos
	 *            an existing buffer that will be reset and reused for efficiency
	 * @return a line of text, without the line termination characters, or null if End-of-file reached. The default system character encoding is
	 *         assumed for the input stream.
	 * @exception IOException
	 *                thrown if an error occurs during the read operation.
	 */
	public static String readLine(InputStream is, ByteArrayOutputStream baos) throws IOException {
		return readLine(is, "US-ASCII", baos);
	}

	/**
	 * Reads and returns a line from the specified <code>InputStream</code>. A line is considered to be terminated by any one of a line feed ('\n'), a
	 * carriage return ('\r'), or a carriage return followed immediately by a linefeed.
	 * 
	 * @param is
	 *            the stream from which the line will be read.
	 * @param encoding
	 *            the character encoding of the byte stream.
	 * @return a line of text, without the line termination characters, or null if End-of-file reached.
	 * @exception IOException
	 *                thrown if an error occurs during the read operation.
	 * @see java.lang.String for the supported character encodings.
	 */
	public static String readLine(InputStream is, String encoding) throws IOException {
		return readLine(is, encoding, new ByteArrayOutputStream());
	}

	/**
	 * Reads and returns a line from the specified <code>InputStream</code>. A line is considered to be terminated by any one of a line feed ('\n'), a
	 * carriage return ('\r'), or a carriage return followed immediately by a linefeed.
	 * 
	 * @param is
	 *            the stream from which the line will be read.
	 * @param encoding
	 *            the character encoding of the byte stream.
	 * @param baos
	 *            an existing buffer that will be reset and reused for efficiency
	 * @return a line of text, without the line termination characters, or null if End-of-file reached.
	 * @exception IOException
	 *                thrown if an error occurs during the read operation.
	 * @see java.lang.String for the supported character encodings.
	 */
	public static String readLine(InputStream is, String encoding, ByteArrayOutputStream baos) throws IOException {
		String line;
		int byteRead = is.read();

		while (byteRead != -1 && byteRead != (byte) '\n') {
			baos.write(byteRead);
			byteRead = is.read();
		}

//		if (byteRead == -1)
//			System.out.println("********* EOF read from IOUtil");
		if (baos.size() == 0)
			line = (byteRead == -1 ? null : ""); // EOF or \n as first byte read
		else {
			byte byteBuf[] = baos.toByteArray();
			line = new String(byteBuf, 0, (byteBuf[byteBuf.length - 1] == '\r' ? byteBuf.length - 1 : byteBuf.length), encoding);
		}

		return line;
	}

	/**
	 * Flushes the specified stream, converting any <code>java.io.IOException</code> thrown to a <code>org.beanplanet.io.IOException</code>.
	 * 
	 * @param os
	 *            the stream to be flushed
	 * @throws RuntimeException
	 *             throw if an error occurs flushing the stream.
	 */
	public static void flushWithRuntimeError(OutputStream os) throws RuntimeException {
		try {
			os.flush();
		} catch (java.io.IOException ioEx) {
			throw new RuntimeException("Error flushing output stream: ", ioEx);
		}
	}

	/**
	 * Flushes the specified writer, converting any <code>java.io.IOException</code> thrown to a <code>org.beanplanet.io.IOException</code>.
	 * 
	 * @param writer
	 *            the writer to be flushed
	 * @throws RuntimeException
	 *             throw if an error occurs flushing the writer.
	 */
	public static void flushWithRuntimeError(Writer writer) throws RuntimeException{
		try {
			writer.flush();
		} catch (java.io.IOException ioEx) {
			throw new RuntimeException("Error flushing writer: ", ioEx);
		}
	}

	/**
	 * Calculates a message digest of the specified byte stream.
	 * 
	 * @param is
	 *            the stream to digest.
	 * @param messageDigestAlgorthmName
	 *            the message digest algorithm required (such as "SHA", "MD5", and so on).
	 * @param providerName
	 *            the message digest algorithm provider (such as "BC" for BouncyCastle), which may be null; in which case the default, if any, will be
	 *            used.
	 * @param transferBufSize
	 *            the I/O transfer buffer size to use in the digest; large buffers improve performance but use more memory.
	 * @return the digest hash value calculated over the byte stream.
	 * @throws IOException
	 *             thrown if an error occurs performing the message digest.
	 */
	public static byte[] hashByteStream(InputStream is, String messageDigestAlgorthmName, String providerName, int transferBufSize)
			throws IOException {
		try {
			MessageDigest md = providerName != null ? MessageDigest.getInstance(messageDigestAlgorthmName, providerName) : MessageDigest
					.getInstance(messageDigestAlgorthmName);
			byte tempBuf[] = new byte[transferBufSize];
			int readCount = 0;
			while ((readCount = is.read(tempBuf)) >= 0) {
				md.update(tempBuf, 0, readCount);
			}
			return md.digest();
		} catch (Throwable th) {
			throw new IOException("Unable to perform message digest (\"" + messageDigestAlgorthmName + "\" provided by \"" + providerName + "\"): "
					+ th.getMessage());
		}
	}

	/**
	 * Calculates a message digest of the specified byte stream. The default transfer buffer size will be used for I/O transfers.
	 * 
	 * @param is
	 *            the stream to digest.
	 * @param messageDigestAlgorthmName
	 *            the message digest algorithm required (such as "SHA", "MD5", and so on).
	 * @param providerName
	 *            the message digest algorithm provider (such as "BC" for BouncyCastle), which may be null; in which case the default, if any, will be
	 *            used.
	 * @return the digest hash value calculated over the byte stream.
	 * @throws IOException
	 *             thrown if an error occurs performing the message digest.
	 * @see #DEFAULT_TRANSFER_BUF_SIZE the default transfer buffer size.
	 */
	public static byte[] hashByteStream(InputStream is, String messageDigestAlgorthmName, String providerName) throws IOException {
		return hashByteStream(is, messageDigestAlgorthmName, providerName, DEFAULT_TRANSFER_BUF_SIZE);
	}

	/**
	 * Calculates a message digest of the specified byte stream. The default provider of the specified digest algorithm will be used.
	 * 
	 * @param is
	 *            the stream to digest.
	 * @param messageDigestAlgorthmName
	 *            the message digest algorithm required (such as "SHA", "MD5", and so on).
	 * @param transferBufSize
	 *            the I/O transfer buffer size to use in the digest; large buffers improve performance but use more memory.
	 * @return the digest hash value calculated over the byte stream.
	 * @throws IOException
	 *             thrown if an error occurs performing the message digest.
	 */
	public static byte[] hashByteStream(InputStream is, String messageDigestAlgorthmName, int transferBufSize) throws IOException {
		return hashByteStream(is, messageDigestAlgorthmName, null, transferBufSize);
	}

	/**
	 * Calculates a message digest of the specified byte stream. The default provider of the specified digest algorithm will be used and the default
	 * transfer buffer size will be used for I/O transfers
	 * 
	 * @param is
	 *            the stream to digest.
	 * @param messageDigestAlgorthmName
	 *            the message digest algorithm required (such as "SHA", "MD5", and so on).
	 * @return the digest hash value calculated over the byte stream.
	 * @throws IOException
	 *             thrown if an error occurs performing the message digest.
	 * @see #DEFAULT_TRANSFER_BUF_SIZE the default transfer buffer size.
	 */
	public static byte[] hashByteStream(InputStream is, String messageDigestAlgorthmName) throws IOException {
		return hashByteStream(is, messageDigestAlgorthmName, null, DEFAULT_TRANSFER_BUF_SIZE);
	}

	/**
	 * Calculates a message digest of the specified byte stream and returns the hexadecimal (Base 16) string of the hashcode.
	 * 
	 * @param is
	 *            the stream to digest.
	 * @param messageDigestAlgorthmName
	 *            the message digest algorithm required (such as "SHA", "MD5", and so on).
	 * @param providerName
	 *            the message digest algorithm provider (such as "BC" for BouncyCastle), which may be null; in which case the default, if any, will be
	 *            used.
	 * @param transferBufSize
	 *            the I/O transfer buffer size to use in the digest; large buffers improve performance but use more memory.
	 * @return the digest hash value calculated over the byte stream.
	 * @throws IOException
	 *             thrown if an error occurs performing the message digest.
	 */
	public static String hashByteStreamToHexadecimal(InputStream is, String messageDigestAlgorthmName, String providerName, int transferBufSize)
			throws IOException {
		return hashToHexadecimalString(hashByteStream(is, messageDigestAlgorthmName, providerName, transferBufSize));
	}

	/**
	 * Calculates a message digest of the specified byte stream and returns the hexadecimal (Base 16) string of the hashcode. The default transfer
	 * buffer size will be used for I/O transfers.
	 * 
	 * @param is
	 *            the stream to digest.
	 * @param messageDigestAlgorthmName
	 *            the message digest algorithm required (such as "SHA", "MD5", and so on).
	 * @param providerName
	 *            the message digest algorithm provider (such as "BC" for BouncyCastle), which may be null; in which case the default, if any, will be
	 *            used.
	 * @return the digest hash value calculated over the byte stream.
	 * @throws IOException
	 *             thrown if an error occurs performing the message digest.
	 * @see #DEFAULT_TRANSFER_BUF_SIZE the default transfer buffer size.
	 */
	public static String hashByteStreamToHexadecimal(InputStream is, String messageDigestAlgorthmName, String providerName) throws IOException {
		return hashByteStreamToHexadecimal(is, messageDigestAlgorthmName, providerName, DEFAULT_TRANSFER_BUF_SIZE);
	}

	/**
	 * Calculates a message digest of the specified byte stream and returns the hexadecimal (Base 16) string of the hashcode. The default provider of
	 * the specified digest algorithm will be used.
	 * 
	 * @param is
	 *            the stream to digest.
	 * @param messageDigestAlgorthmName
	 *            the message digest algorithm required (such as "SHA", "MD5", and so on).
	 * @param transferBufSize
	 *            the I/O transfer buffer size to use in the digest; large buffers improve performance but use more memory.
	 * @return the digest hash value calculated over the byte stream.
	 * @throws IOException
	 *             thrown if an error occurs performing the message digest.
	 */
	public static String hashByteStreamToHexadecimal(InputStream is, String messageDigestAlgorthmName, int transferBufSize) throws IOException {
		return hashByteStreamToHexadecimal(is, messageDigestAlgorthmName, null, transferBufSize);
	}

	/**
	 * Calculates a message digest of the specified byte stream and returns the hexadecimal (Base 16) string of the hashcode. The default provider of
	 * the specified digest algorithm will be used and the default transfer buffer size will be used for I/O transfers
	 * 
	 * @param is
	 *            the stream to digest.
	 * @param messageDigestAlgorthmName
	 *            the message digest algorithm required (such as "SHA", "MD5", and so on).
	 * @return the digest hash value calculated over the byte stream.
	 * @throws IOException
	 *             thrown if an error occurs performing the message digest.
	 * @see #DEFAULT_TRANSFER_BUF_SIZE the default transfer buffer size.
	 */
	public static String hashByteStreamToHexadecimal(InputStream is, String messageDigestAlgorthmName) throws IOException {
		return hashByteStreamToHexadecimal(is, messageDigestAlgorthmName, null, DEFAULT_TRANSFER_BUF_SIZE);
	}

	/**
	 * Converts a hash of bytes to hexadecimal string notation.
	 * 
	 * @param hash
	 *            the hash byte codes to convert.
	 * @return the hexadecimal (base 16) string representation of the hash codes.
	 */
	public static String hashToHexadecimalString(byte hash[]) {
		String encodedStr = new BigInteger(1, hash).toString(16);

		/* this is important, toString leaves out initial 0 */
		if (encodedStr.length() % 2 > 0)
			encodedStr = "0" + encodedStr;

		return encodedStr;
	}
}
