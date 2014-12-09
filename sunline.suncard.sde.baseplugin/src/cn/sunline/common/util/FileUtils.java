package cn.sunline.common.util;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.jar.JarFile;

/**
 * This class also encapsulates methods which allow Files to be referred to using abstract path names which are translated to native system file paths
 * at runtime as well as copying files or setting their last modification time.
 * 
 */
public class FileUtils {
	private static final int DELETE_RETRY_SLEEP_MILLIS = 10;
	private static final int EXPAND_SPACE = 50;
	private static final FileUtils PRIMARY_INSTANCE = new FileUtils();

	// get some non-crypto-grade randomness from various places.
	private static Random rand = new Random(System.currentTimeMillis() + Runtime.getRuntime().freeMemory());

	static final int BUF_SIZE = 8192;

	/**
	 * The granularity of timestamps under FAT.
	 */
	public static final long FAT_FILE_TIMESTAMP_GRANULARITY = 2000;

	/**
	 * The granularity of timestamps under Unix.
	 */
	public static final long UNIX_FILE_TIMESTAMP_GRANULARITY = 1000;

	/**
	 * The granularity of timestamps under the NT File System. NTFS has a granularity of 100 nanoseconds, which is less than 1 millisecond, so we
	 * round this up to 1 millisecond.
	 */
	public static final long NTFS_FILE_TIMESTAMP_GRANULARITY = 1;

	private static final boolean ON_NETWARE = isFamily("netware");
	private static final boolean ON_DOS = isFamily("dos");
	private static final boolean ON_WIN9X = isFamily("win9x");
	private static final boolean ON_WINDOWS = isFamily("windows");

	/**
	 * OS family that can be tested for. {@value}
	 */
	public static final String FAMILY_WINDOWS = "windows";
	/**
	 * OS family that can be tested for. {@value}
	 */
	public static final String FAMILY_9X = "win9x";
	/**
	 * OS family that can be tested for. {@value}
	 */
	public static final String FAMILY_NT = "winnt";
	/**
	 * OS family that can be tested for. {@value}
	 */
	public static final String FAMILY_OS2 = "os/2";
	/**
	 * OS family that can be tested for. {@value}
	 */
	public static final String FAMILY_NETWARE = "netware";
	/**
	 * OS family that can be tested for. {@value}
	 */
	public static final String FAMILY_DOS = "dos";
	/**
	 * OS family that can be tested for. {@value}
	 */
	public static final String FAMILY_MAC = "mac";
	/**
	 * OS family that can be tested for. {@value}
	 */
	public static final String FAMILY_TANDEM = "tandem";
	/**
	 * OS family that can be tested for. {@value}
	 */
	public static final String FAMILY_UNIX = "unix";
	/**
	 * OS family that can be tested for. {@value}
	 */
	public static final String FAMILY_VMS = "openvms";
	/**
	 * OS family that can be tested for. {@value}
	 */
	public static final String FAMILY_ZOS = "z/os";
	/** OS family that can be tested for. {@value} */
	public static final String FAMILY_OS400 = "os/400";
	/**
	 * OpenJDK is reported to call MacOS X "Darwin"
	 * 
	 * @see https://issues.apache.org/bugzilla/show_bug.cgi?id=44889
	 * @see https://issues.apache.org/jira/browse/HADOOP-3318
	 */
	private static final String DARWIN = "darwin";

	/**
	 * A one item cache for fromUri. fromUri is called for each element when parseing ant build files. It is a costly operation. This just caches the
	 * result of the last call.
	 */
	private Object cacheFromUriLock = new Object();
	private String cacheFromUriRequest = null;
	private String cacheFromUriResponse = null;

	/**
	 * Method to retrieve The FileUtils, which is shared by all users of this method.
	 * 
	 * @return an instance of FileUtils.
	 * @since Ant 1.6.3
	 */
	public static FileUtils getFileUtils() {
		return PRIMARY_INSTANCE;
	}

	/**
	 * Empty constructor.
	 */
	protected FileUtils() {
	}

	/**
	 * Get the URL for a file taking into account # characters.
	 * 
	 * @param file
	 *            the file whose URL representation is required.
	 * @return The FileURL value.
	 * @throws MalformedURLException
	 *             if the URL representation cannot be formed.
	 */
	public URL getFileURL(File file) throws MalformedURLException {
		return new URL(toURI(file.getAbsolutePath()));
	}

	/**
	 * Determines if the OS on which Ant is executing matches the given OS family.
	 * 
	 * @param family
	 *            the family to check for
	 * @return true if the OS matches
	 * @since 1.5
	 */
	public static boolean isFamily(String family) {
		return isOs(family, null, null, null);
	}

	/**
	 * Determines if the OS on which Ant is executing matches the given OS family, name, architecture and version
	 * 
	 * @param family
	 *            The OS family
	 * @param name
	 *            The OS name
	 * @param arch
	 *            The OS architecture
	 * @param version
	 *            The OS version
	 * @return true if the OS matches
	 * @since 1.7
	 */
	public static boolean isOs(String family, String name, String arch, String version) {
		boolean retValue = false;

		if (family != null || name != null || arch != null || version != null) {
			final String OS_NAME = System.getProperty("os.name").toLowerCase();
			final String OS_ARCH = System.getProperty("os.arch").toLowerCase(Locale.ENGLISH);
			final String OS_VERSION = System.getProperty("os.version").toLowerCase();
			final String PATH_SEP = System.getProperty("path.separator");

			boolean isFamily = true;
			boolean isName = true;
			boolean isArch = true;
			boolean isVersion = true;

			if (family != null) {

				// windows probing logic relies on the word 'windows' in
				// the OS
				boolean isWindows = OS_NAME.indexOf(FAMILY_WINDOWS) > -1;
				boolean is9x = false;
				boolean isNT = false;
				if (isWindows) {
					// there are only four 9x platforms that we look for
					is9x = (OS_NAME.indexOf("95") >= 0 || OS_NAME.indexOf("98") >= 0 || OS_NAME.indexOf("me") >= 0
					// wince isn't really 9x, but crippled enough to
					// be a muchness. Ant doesnt run on CE, anyway.
					|| OS_NAME.indexOf("ce") >= 0);
					isNT = !is9x;
				}
				if (family.equals(FAMILY_WINDOWS)) {
					isFamily = isWindows;
				} else if (family.equals(FAMILY_9X)) {
					isFamily = isWindows && is9x;
				} else if (family.equals(FAMILY_NT)) {
					isFamily = isWindows && isNT;
				} else if (family.equals(FAMILY_OS2)) {
					isFamily = OS_NAME.indexOf(FAMILY_OS2) > -1;
				} else if (family.equals(FAMILY_NETWARE)) {
					isFamily = OS_NAME.indexOf(FAMILY_NETWARE) > -1;
				} else if (family.equals(FAMILY_DOS)) {
					isFamily = PATH_SEP.equals(";") && !isFamily(FAMILY_NETWARE);
				} else if (family.equals(FAMILY_MAC)) {
					isFamily = OS_NAME.indexOf(FAMILY_MAC) > -1 || OS_NAME.indexOf(DARWIN) > -1;
				} else if (family.equals(FAMILY_TANDEM)) {
					isFamily = OS_NAME.indexOf("nonstop_kernel") > -1;
				} else if (family.equals(FAMILY_UNIX)) {
					isFamily = PATH_SEP.equals(":") && !isFamily(FAMILY_VMS)
							&& (!isFamily(FAMILY_MAC) || OS_NAME.endsWith("x") || OS_NAME.indexOf(DARWIN) > -1);
				} else if (family.equals(FAMILY_ZOS)) {
					isFamily = OS_NAME.indexOf(FAMILY_ZOS) > -1 || OS_NAME.indexOf("os/390") > -1;
				} else if (family.equals(FAMILY_OS400)) {
					isFamily = OS_NAME.indexOf(FAMILY_OS400) > -1;
				} else if (family.equals(FAMILY_VMS)) {
					isFamily = OS_NAME.indexOf(FAMILY_VMS) > -1;
				} else {
					throw new RuntimeException("Don\'t know how to detect os family \"" + family + "\"");
				}
			}
			if (name != null) {
				isName = name.equals(OS_NAME);
			}
			if (arch != null) {
				isArch = arch.equals(OS_ARCH);
			}
			if (version != null) {
				isVersion = version.equals(OS_VERSION);
			}
			retValue = isFamily && isName && isArch && isVersion;
		}
		return retValue;
	}

	/**
	 * Verifies that the specified filename represents an absolute path. Differs from new java.io.File("filename").isAbsolute() in that a path
	 * beginning with a double file separator--signifying a Windows UNC--must at minimum match "\\a\b" to be considered an absolute path.
	 * 
	 * @param filename
	 *            the filename to be checked.
	 * @return true if the filename represents an absolute path.
	 * @throws java.lang.NullPointerException
	 *             if filename is null.
	 * @since Ant 1.6.3
	 */
	public static boolean isAbsolutePath(String filename) {
		int len = filename.length();
		if (len == 0) {
			return false;
		}
		char sep = File.separatorChar;
		filename = filename.replace('/', sep).replace('\\', sep);
		char c = filename.charAt(0);
		if (!(ON_DOS || ON_NETWARE)) {
			return (c == sep);
		}
		if (c == sep) {
			// CheckStyle:MagicNumber OFF
			if (!(ON_DOS && len > 4 && filename.charAt(1) == sep)) {
				return false;
			}
			// CheckStyle:MagicNumber ON
			int nextsep = filename.indexOf(sep, 2);
			return nextsep > 2 && nextsep + 1 < len;
		}
		int colon = filename.indexOf(':');
		return (Character.isLetter(c) && colon == 1 && filename.length() > 2 && filename.charAt(2) == sep) || (ON_NETWARE && colon > 0);
	}

	/**
	 * Dissect the specified absolute path.
	 * 
	 * @param path
	 *            the path to dissect.
	 * @return String[] {root, remaining path}.
	 * @throws java.lang.NullPointerException
	 *             if path is null.
	 * @since Ant 1.7
	 */
	public String[] dissect(String path) {
		char sep = File.separatorChar;
		path = path.replace('/', sep).replace('\\', sep);

		// make sure we are dealing with an absolute path
		if (!isAbsolutePath(path)) {
			throw new RuntimeException(path + " is not an absolute path");
		}
		String root = null;
		int colon = path.indexOf(':');
		if (colon > 0 && (ON_DOS || ON_NETWARE)) {

			int next = colon + 1;
			root = path.substring(0, next);
			char[] ca = path.toCharArray();
			root += sep;
			// remove the initial separator; the root has it.
			next = (ca[next] == sep) ? next + 1 : next;

			StringBuffer sbPath = new StringBuffer();
			// Eliminate consecutive slashes after the drive spec:
			for (int i = next; i < ca.length; i++) {
				if (ca[i] != sep || ca[i - 1] != sep) {
					sbPath.append(ca[i]);
				}
			}
			path = sbPath.toString();
		} else if (path.length() > 1 && path.charAt(1) == sep) {
			// UNC drive
			int nextsep = path.indexOf(sep, 2);
			nextsep = path.indexOf(sep, nextsep + 1);
			root = (nextsep > 2) ? path.substring(0, nextsep + 1) : path;
			path = path.substring(root.length());
		} else {
			root = File.separator;
			path = path.substring(1);
		}
		return new String[] { root, path };
	}

	/**
	 * Interpret the filename as a file relative to the given file unless the filename already represents an absolute filename. Differs from
	 * <code>new File(file, filename)</code> in that the resulting File's path will always be a normalized, absolute pathname. Also, if it is
	 * determined that <code>filename</code> is context-relative, <code>file</code> will be discarded and the reference will be resolved using
	 * available context/state information about the filesystem.
	 * 
	 * @param file
	 *            the "reference" file for relative paths. This instance must be an absolute file and must not contain &quot;./&quot; or
	 *            &quot;../&quot; sequences (same for \ instead of /). If it is null, this call is equivalent to
	 *            <code>new java.io.File(filename).getAbsoluteFile()</code>.
	 * 
	 * @param filename
	 *            a file name.
	 * 
	 * @return an absolute file.
	 * @throws java.lang.NullPointerException
	 *             if filename is null.
	 */
	public File resolveFile(File file, String filename) {
		if (!isAbsolutePath(filename)) {
			char sep = File.separatorChar;
			filename = filename.replace('/', sep).replace('\\', sep);
			if (isContextRelativePath(filename)) {
				file = null;
				// on cygwin, our current directory can be a UNC;
				// assume user.dir is absolute or all hell breaks loose...
				String udir = System.getProperty("user.dir");
				if (filename.charAt(0) == sep && udir.charAt(0) == sep) {
					filename = dissect(udir)[0] + filename.substring(1);
				}
			}
			filename = new File(file, filename).getAbsolutePath();
		}
		return normalize(filename);
	}

	/**
	 * On DOS and NetWare, the evaluation of certain file specifications is context-dependent. These are filenames beginning with a single separator
	 * (relative to current root directory) and filenames with a drive specification and no intervening separator (relative to current directory of
	 * the specified root).
	 * 
	 * @param filename
	 *            the filename to evaluate.
	 * @return true if the filename is relative to system context.
	 * @throws java.lang.NullPointerException
	 *             if filename is null.
	 * @since Ant 1.7
	 */
	public static boolean isContextRelativePath(String filename) {
		char sep = File.separatorChar;
		filename = filename.replace('/', sep).replace('\\', sep);
		char c = filename.charAt(0);
		int len = filename.length();
		return (c == sep && (len == 1 || filename.charAt(1) != sep))
				|| (Character.isLetter(c) && len > 1 && filename.indexOf(':') == 1 && (len == 2 || filename.charAt(2) != sep));
	}

	/**
	 * &quot;Normalize&quot; the given absolute path.
	 * 
	 * <p>
	 * This includes:
	 * <ul>
	 * <li>Uppercase the drive letter if there is one.</li>
	 * <li>Remove redundant slashes after the drive spec.</li>
	 * <li>Resolve all ./, .\, ../ and ..\ sequences.</li>
	 * <li>DOS style paths that start with a drive letter will have \ as the separator.</li>
	 * </ul>
	 * Unlike {@link File#getCanonicalPath()} this method specifically does not resolve symbolic links.
	 * 
	 * @param path
	 *            the path to be normalized.
	 * @return the normalized version of the path.
	 * 
	 * @throws java.lang.NullPointerException
	 *             if path is null.
	 */
	public File normalize(final String path) {
		Stack s = new Stack();
		String[] dissect = dissect(path);
		s.push(dissect[0]);

		StringTokenizer tok = new StringTokenizer(dissect[1], File.separator);
		while (tok.hasMoreTokens()) {
			String thisToken = tok.nextToken();
			if (".".equals(thisToken)) {
				continue;
			}
			if ("..".equals(thisToken)) {
				if (s.size() < 2) {
					// Cannot resolve it, so skip it.
					return new File(path);
				}
				s.pop();
			} else { // plain component
				s.push(thisToken);
			}
		}
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < s.size(); i++) {
			if (i > 1) {
				// not before the filesystem root and not after it, since root
				// already contains one
				sb.append(File.separatorChar);
			}
			sb.append(s.elementAt(i));
		}
		return new File(sb.toString());
	}

	/**
	 * Returns a VMS String representation of a <code>File</code> object. This is useful since the JVM by default internally converts VMS paths to
	 * Unix style. The returned String is always an absolute path.
	 * 
	 * @param f
	 *            The <code>File</code> to get the VMS path for.
	 * @return The absolute VMS path to <code>f</code>.
	 */
	public String toVMSPath(File f) {
		// format: "DEVICE:[DIR.SUBDIR]FILE"
		String osPath;
		String path = normalize(f.getAbsolutePath()).getPath();
		String name = f.getName();
		boolean isAbsolute = path.charAt(0) == File.separatorChar;
		// treat directories specified using .DIR syntax as files
		// CheckStyle:MagicNumber OFF
		boolean isDirectory = f.isDirectory() && !name.regionMatches(true, name.length() - 4, ".DIR", 0, 4);
		// CheckStyle:MagicNumber ON
		String device = null;
		StringBuffer directory = null;
		String file = null;

		int index = 0;

		if (isAbsolute) {
			index = path.indexOf(File.separatorChar, 1);
			if (index == -1) {
				return path.substring(1) + ":[000000]";
			}
			device = path.substring(1, index++);
		}
		if (isDirectory) {
			directory = new StringBuffer(path.substring(index).replace(File.separatorChar, '.'));
		} else {
			int dirEnd = path.lastIndexOf(File.separatorChar, path.length());
			if (dirEnd == -1 || dirEnd < index) {
				file = path.substring(index);
			} else {
				directory = new StringBuffer(path.substring(index, dirEnd).replace(File.separatorChar, '.'));
				index = dirEnd + 1;
				if (path.length() > index) {
					file = path.substring(index);
				}
			}
		}
		if (!isAbsolute && directory != null) {
			directory.insert(0, '.');
		}
		osPath = ((device != null) ? device + ":" : "") + ((directory != null) ? "[" + directory + "]" : "") + ((file != null) ? file : "");
		return osPath;
	}

	/**
	 * Read from reader till EOF.
	 * 
	 * @param rdr
	 *            the reader from which to read.
	 * @return the contents read out of the given reader.
	 * 
	 * @throws IOException
	 *             if the contents could not be read out from the reader.
	 */
	public static String readFully(Reader rdr) throws IOException {
		return readFully(rdr, BUF_SIZE);
	}

	/**
	 * Read from reader till EOF.
	 * 
	 * @param rdr
	 *            the reader from which to read.
	 * @param bufferSize
	 *            the buffer size to use when reading.
	 * 
	 * @return the contents read out of the given reader.
	 * 
	 * @throws IOException
	 *             if the contents could not be read out from the reader.
	 */
	public static String readFully(Reader rdr, int bufferSize) throws IOException {
		if (bufferSize <= 0) {
			throw new IllegalArgumentException("Buffer size must be greater " + "than 0");
		}
		final char[] buffer = new char[bufferSize];
		int bufferLength = 0;
		StringBuffer textBuffer = null;
		while (bufferLength != -1) {
			bufferLength = rdr.read(buffer);
			if (bufferLength > 0) {
				textBuffer = (textBuffer == null) ? new StringBuffer() : textBuffer;
				textBuffer.append(new String(buffer, 0, bufferLength));
			}
		}
		return (textBuffer == null) ? null : textBuffer.toString();
	}

	/**
	 * Safe read fully - do not return a null for an empty reader.
	 * 
	 * @param reader
	 *            the input to read from.
	 * @return the string.
	 * @throws IOException
	 *             if unable to read from reader.
	 * @since Ant 1.7.1
	 */
	public static String safeReadFully(Reader reader) throws IOException {
		String ret = readFully(reader);
		return ret == null ? "" : ret;
	}

	/**
	 * This was originally an emulation of File.createNewFile for JDK 1.1, but it is now implemented using that method (Ant 1.6.3 onwards).
	 * 
	 * <p>
	 * This method has historically <strong>not</strong> guaranteed that the operation was atomic. In its current implementation it is.
	 * 
	 * @param f
	 *            the file to be created.
	 * @return true if the file did not exist already.
	 * @throws IOException
	 *             on error.
	 * @since Ant 1.5
	 */
	public boolean createNewFile(File f) throws IOException {
		return f.createNewFile();
	}

	/**
	 * Create a new file, optionally creating parent directories.
	 * 
	 * @param f
	 *            the file to be created.
	 * @param mkdirs
	 *            <code>boolean</code> whether to create parent directories.
	 * @return true if the file did not exist already.
	 * @throws IOException
	 *             on error.
	 * @since Ant 1.6.3
	 */
	public boolean createNewFile(File f, boolean mkdirs) throws IOException {
		File parent = f.getParentFile();
		if (mkdirs && !(parent.exists())) {
			parent.mkdirs();
		}
		return f.createNewFile();
	}

	/**
	 * Removes a leading path from a second path.
	 * 
	 * @param leading
	 *            The leading path, must not be null, must be absolute.
	 * @param path
	 *            The path to remove from, must not be null, must be absolute.
	 * 
	 * @return path's normalized absolute if it doesn't start with leading; path's path with leading's path removed otherwise.
	 * 
	 * @since Ant 1.5
	 */
	public String removeLeadingPath(File leading, File path) {
		String l = normalize(leading.getAbsolutePath()).getAbsolutePath();
		String p = normalize(path.getAbsolutePath()).getAbsolutePath();
		if (l.equals(p)) {
			return "";
		}
		// ensure that l ends with a /
		// so we never think /foo was a parent directory of /foobar
		if (!l.endsWith(File.separator)) {
			l += File.separator;
		}
		return (p.startsWith(l)) ? p.substring(l.length()) : p;
	}

	/**
	 * Learn whether one path "leads" another.
	 * 
	 * @param leading
	 *            The leading path, must not be null, must be absolute.
	 * @param path
	 *            The path to remove from, must not be null, must be absolute.
	 * @return true if path starts with leading; false otherwise.
	 * @since Ant 1.7
	 */
	public boolean isLeadingPath(File leading, File path) {
		String l = normalize(leading.getAbsolutePath()).getAbsolutePath();
		String p = normalize(path.getAbsolutePath()).getAbsolutePath();
		if (l.equals(p)) {
			return true;
		}
		// ensure that l ends with a /
		// so we never think /foo was a parent directory of /foobar
		if (!l.endsWith(File.separator)) {
			l += File.separator;
		}
		return p.startsWith(l);
	}

	/**
	 * Constructs a <code>file:</code> URI that represents the external form of the given pathname.
	 * 
	 * <p>
	 * Will be an absolute URI if the given path is absolute.
	 * </p>
	 * 
	 * <p>
	 * This code encodes non ASCII characters too.
	 * </p>
	 * 
	 * <p>
	 * The coding of the output is the same as what File.toURI().toASCIIString() produces
	 * </p>
	 * 
	 * See <a href="http://www.w3.org/TR/xml11/#dt-sysid">dt-sysid</a> which makes some mention of how characters not supported by URI Reference
	 * syntax should be escaped.
	 * 
	 * @param path
	 *            the path in the local file system.
	 * @return the URI version of the local path.
	 * @since Ant 1.6
	 */
	public String toURI(String path) {
		return new File(path).getAbsoluteFile().toURI().toASCIIString();
	}

	/**
	 * Compares two filenames.
	 * 
	 * <p>
	 * Unlike java.io.File#equals this method will try to compare the absolute paths and &quot;normalize&quot; the filenames before comparing them.
	 * </p>
	 * 
	 * @param f1
	 *            the file whose name is to be compared.
	 * @param f2
	 *            the other file whose name is to be compared.
	 * 
	 * @return true if the file are for the same file.
	 * 
	 * @since Ant 1.5.3
	 */
	public boolean fileNameEquals(File f1, File f2) {
		return normalize(f1.getAbsolutePath()).getAbsolutePath().equals(normalize(f2.getAbsolutePath()).getAbsolutePath());
	}

	/**
	 * test whether a file or directory exists, with an error in the upper/lower case spelling of the name. Using this method is only interesting on
	 * case insensitive file systems (Windows).<br/>
	 * It will return true only if 3 conditions are met : <br/>
	 * <ul>
	 * <li>operating system is case insensitive</li>
	 * <li>file exists</li>
	 * <li>actual name from directory reading is different from the supplied argument</li>
	 * </ul>
	 * <br/>
	 * the purpose is to identify files or directories on case-insensitive filesystems whose case is not what is expected.<br/>
	 * Possibly to rename them afterwards to the desired upper/lowercase combination. <br/>
	 * 
	 * @param localFile
	 *            file to test
	 * @return true if the file exists and the case of the actual file is not the case of the parameter
	 * @since Ant 1.7.1
	 */
	public boolean hasErrorInCase(File localFile) {
		localFile = normalize(localFile.getAbsolutePath());
		if (!localFile.exists()) {
			return false;
		}
		final String localFileName = localFile.getName();
		FilenameFilter ff = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.equalsIgnoreCase(localFileName) && (!name.equals(localFileName));
			}
		};
		String[] names = localFile.getParentFile().list(ff);
		return names != null && names.length == 1;
	}

	/**
	 * Returns true if the source is older than the dest. If the dest file does not exist, then the test returns false; it is implicitly not up do
	 * date.
	 * 
	 * @param source
	 *            source file (should be the older).
	 * @param dest
	 *            dest file (should be the newer).
	 * @param granularity
	 *            an offset added to the source time.
	 * @return true if the source is older than the dest after accounting for granularity.
	 * @since Ant 1.6.3
	 */
	public boolean isUpToDate(File source, File dest, long granularity) {
		// do a check for the destination file existing
		if (!dest.exists()) {
			// if it does not, then the file is not up to date.
			return false;
		}
		long sourceTime = source.lastModified();
		long destTime = dest.lastModified();
		return isUpToDate(sourceTime, destTime, granularity);
	}

	/**
	 * Returns true if the source is older than the dest.
	 * 
	 * @param source
	 *            source file (should be the older).
	 * @param dest
	 *            dest file (should be the newer).
	 * @return true if the source is older than the dest, taking the granularity into account.
	 * @since Ant 1.6.3
	 */
	public boolean isUpToDate(File source, File dest) {
		return isUpToDate(source, dest, 1000);// unix
	}

	/**
	 * Compare two timestamps for being up to date using the specified granularity.
	 * 
	 * @param sourceTime
	 *            timestamp of source file.
	 * @param destTime
	 *            timestamp of dest file.
	 * @param granularity
	 *            os/filesys granularity.
	 * @return true if the dest file is considered up to date.
	 */
	public boolean isUpToDate(long sourceTime, long destTime, long granularity) {
		return destTime != -1 && destTime >= sourceTime + granularity;
	}

	/**
	 * Compare two timestamps for being up to date using the current granularity.
	 * 
	 * @param sourceTime
	 *            timestamp of source file.
	 * @param destTime
	 *            timestamp of dest file.
	 * @return true if the dest file is considered up to date.
	 */
	public boolean isUpToDate(long sourceTime, long destTime) {
		return isUpToDate(sourceTime, destTime, 1000);
	}

	/**
	 * Close a Writer without throwing any exception if something went wrong. Do not attempt to close it if the argument is null.
	 * 
	 * @param device
	 *            output writer, can be null.
	 */
	public static void close(Writer device) {
		if (null != device) {
			try {
				device.close();
			} catch (IOException e) {
				// ignore
			}
		}
	}

	/**
	 * Close a Reader without throwing any exception if something went wrong. Do not attempt to close it if the argument is null.
	 * 
	 * @param device
	 *            Reader, can be null.
	 */
	public static void close(Reader device) {
		if (null != device) {
			try {
				device.close();
			} catch (IOException e) {
				// ignore
			}
		}
	}

	/**
	 * Close a stream without throwing any exception if something went wrong. Do not attempt to close it if the argument is null.
	 * 
	 * @param device
	 *            stream, can be null.
	 */
	public static void close(OutputStream device) {
		if (null != device) {
			try {
				device.close();
			} catch (IOException e) {
				// ignore
			}
		}
	}

	/**
	 * Close a stream without throwing any exception if something went wrong. Do not attempt to close it if the argument is null.
	 * 
	 * @param device
	 *            stream, can be null.
	 */
	public static void close(InputStream device) {
		if (null != device) {
			try {
				device.close();
			} catch (IOException e) {
				// ignore
			}
		}
	}

	/**
	 * Close a Channel without throwing any exception if something went wrong. Do not attempt to close it if the argument is null.
	 * 
	 * @param device
	 *            channel, can be null.
	 * @since Ant 1.8.0
	 */
	public static void close(Channel device) {
		if (null != device) {
			try {
				device.close();
			} catch (IOException e) {
				// ignore
			}
		}
	}

	/**
	 * Closes an URLConnection if its concrete implementation provides a way to close it that Ant knows of.
	 * 
	 * @param conn
	 *            connection, can be null
	 * @since Ant 1.8.0
	 */
	public static void close(URLConnection conn) {
		if (conn != null) {
			try {
				if (conn instanceof JarURLConnection) {
					JarURLConnection juc = (JarURLConnection) conn;
					JarFile jf = juc.getJarFile();
					jf.close();
					jf = null;
				} else if (conn instanceof HttpURLConnection) {
					((HttpURLConnection) conn).disconnect();
				}
			} catch (IOException exc) {
				// ignore
			}
		}
	}

	/**
	 * Delete the file with {@link File#delete()} if the argument is not null. Do nothing on a null argument.
	 * 
	 * @param file
	 *            file to delete.
	 */
	public static void delete(File file) {
		if (file != null) {
			file.delete();
		}
	}

	/**
	 * Accommodate Windows bug encountered in both Sun and IBM JDKs. Others possible. If the delete does not work, call System.gc(), wait a little and
	 * try again.
	 * 
	 * @return whether deletion was successful
	 * @since Ant 1.8.0
	 */
	public boolean tryHardToDelete(File f) {
		if (!f.delete()) {
			System.gc();
			try {
				Thread.sleep(DELETE_RETRY_SLEEP_MILLIS);
			} catch (InterruptedException ex) {
				// Ignore Exception
			}
			return f.delete();
		}
		return true;
	}

	/**
	 * Calculates the relative path between two files.
	 * <p>
	 * Implementation note:<br/>
	 * This function may throw an IOException if an I/O error occurs because its use of the canonical pathname may require filesystem queries.
	 * </p>
	 * 
	 * @param fromFile
	 *            the <code>File</code> to calculate the path from
	 * @param toFile
	 *            the <code>File</code> to calculate the path to
	 * @return the relative path between the files
	 * @throws Exception
	 *             for undocumented reasons
	 * @see File#getCanonicalPath()
	 * 
	 * @since Ant 1.7
	 */
	public static String getRelativePath(File fromFile, File toFile) throws Exception {
		String fromPath = fromFile.getCanonicalPath();
		String toPath = toFile.getCanonicalPath();

		// build the path stack info to compare
		String[] fromPathStack = getPathStack(fromPath);
		String[] toPathStack = getPathStack(toPath);

		if (0 < toPathStack.length && 0 < fromPathStack.length) {
			if (!fromPathStack[0].equals(toPathStack[0])) {
				// not the same device (would be "" on Linux/Unix)

				return getPath(Arrays.asList(toPathStack));
			}
		} else {
			// no comparison possible
			return getPath(Arrays.asList(toPathStack));
		}

		int minLength = Math.min(fromPathStack.length, toPathStack.length);
		int same = 1; // Used outside the for loop

		// get index of parts which are equal
		for (; same < minLength && fromPathStack[same].equals(toPathStack[same]); same++) {
			// Do nothing
		}

		List relativePathStack = new ArrayList();

		// if "from" part is longer, fill it up with ".."
		// to reach path which is equal to both paths
		for (int i = same; i < fromPathStack.length; i++) {
			relativePathStack.add("..");
		}

		// fill it up path with parts which were not equal
		for (int i = same; i < toPathStack.length; i++) {
			relativePathStack.add(toPathStack[i]);
		}

		return getPath(relativePathStack);
	}

	/**
	 * Gets all names of the path as an array of <code>String</code>s.
	 * 
	 * @param path
	 *            to get names from
	 * @return <code>String</code>s, never <code>null</code>
	 * 
	 * @since Ant 1.7
	 */
	public static String[] getPathStack(String path) {
		String normalizedPath = path.replace(File.separatorChar, '/');

		return normalizedPath.split("/");
	}

	/**
	 * Gets path from a <code>List</code> of <code>String</code>s.
	 * 
	 * @param pathStack
	 *            <code>List</code> of <code>String</code>s to be concatenated as a path.
	 * @return <code>String</code>, never <code>null</code>
	 * 
	 * @since Ant 1.7
	 */
	public static String getPath(List pathStack) {
		// can safely use '/' because Windows understands '/' as separator
		return getPath(pathStack, '/');
	}

	/**
	 * Gets path from a <code>List</code> of <code>String</code>s.
	 * 
	 * @param pathStack
	 *            <code>List</code> of <code>String</code>s to be concated as a path.
	 * @param separatorChar
	 *            <code>char</code> to be used as separator between names in path
	 * @return <code>String</code>, never <code>null</code>
	 * 
	 * @since Ant 1.7
	 */
	public static String getPath(final List pathStack, final char separatorChar) {
		final StringBuffer buffer = new StringBuffer();

		final Iterator iter = pathStack.iterator();
		if (iter.hasNext()) {
			buffer.append(iter.next());
		}
		while (iter.hasNext()) {
			buffer.append(separatorChar);
			buffer.append(iter.next());
		}
		return buffer.toString();
	}

	/**
	 * Get the default encoding. This is done by opening an InputStreamReader on a dummy InputStream and getting the encoding. Could use
	 * System.getProperty("file.encoding"), but cannot see where this is documented.
	 * 
	 * @return the default file encoding.
	 */
	public String getDefaultEncoding() {
		InputStreamReader is = new InputStreamReader(new InputStream() {
			public int read() {
				return -1;
			}
		});
		try {
			return is.getEncoding();
		} finally {
			close(is);
		}
	}
}
