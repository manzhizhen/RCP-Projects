/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.sunline.common.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Miscellaneous utility methods for URL.
 * 
 */
public final class UrlUtil {
	/** Constant for the file URL protocol. */
	static final String PROTOCOL_FILE = "file";

	static final String PROTOCOL_JAR = "jar";
	static final String PROTOCOL_WSJAR = "wsjar";

	/** Constant for the resource path separator. */
	static final String RESOURCE_PATH_SEPARATOR = "/";

	/**
	 * Private constructor. Prevents instances from being created.
	 */
	private UrlUtil() {
		// to prevent instantiation...
	}

	/**
	 * Constructs a URL from a base path and a file name. The file name can be absolute, relative or a full URL. If necessary the base path URL is
	 * applied.
	 * 
	 * @param basePath
	 *            the base path URL (can be <b>null</b>)
	 * @param file
	 *            the file name
	 * @return the resulting URL
	 * @throws MalformedURLException
	 *             if URLs are invalid
	 */
	public static URL getURL(String basePath, String file) throws MalformedURLException {
		File f = new File(file);
		if (f.isAbsolute()) // already absolute?
		{
			return f.toURL();
		}

		try {
			if (basePath == null) {
				return new URL(file);
			} else {
				URL base = new URL(basePath);
				return new URL(base, file);
			}
		} catch (MalformedURLException uex) {
			return constructFile(basePath, file).toURL();
		}
	}

	/**
	 * Helper method for constructing a file object from a base path and a file name. This method is called if the base path passed to
	 * <code>getURL()</code> does not seem to be a valid URL.
	 * 
	 * @param basePath
	 *            the base path
	 * @param fileName
	 *            the file name
	 * @return the resulting file
	 */
	static File constructFile(String basePath, String fileName) {
		File file = null;

		File absolute = null;
		if (fileName != null) {
			absolute = new File(fileName);
		}

		if (StringUtil.isEmpty(basePath) || (absolute != null && absolute.isAbsolute())) {
			file = new File(fileName);
		} else {
			StringBuffer fName = new StringBuffer();
			fName.append(basePath);

			// My best friend. Paranoia.
			if (!basePath.endsWith(File.separator)) {
				fName.append(File.separator);
			}

			//
			// We have a relative path, and we have
			// two possible forms here. If we have the
			// "./" form then just strip that off first
			// before continuing.
			//
			if (fileName.startsWith("." + File.separator)) {
				fName.append(fileName.substring(2));
			} else {
				fName.append(fileName);
			}

			file = new File(fName.toString());
		}

		return file;
	}

	/**
	 * Return the location of the specified resource by searching the current classpath and the system classpath.
	 * 
	 * @param name
	 *            the name of the resource
	 * 
	 * @return the location of the resource URL
	 */
	public static Collection locate(String name) {
		return locate(null, name);
	}

	/**
	 * Return the location of the specified resource by searching the current classpath and the system classpath.
	 * 
	 * @param base
	 *            the base path of the resource
	 * @param name
	 *            the name of the resource
	 * 
	 * @return the location of the resource
	 */
	public static Collection locate(String base, String name) {
		Collection ret = new HashSet();
		if (name == null) {
			// undefined, always return null
			return ret;
		}

		URL url = null;

		// attempt to create an URL directly
		try {
			if (base == null) {
				url = new URL(name);
			} else {
				URL baseURL = new URL(base);
				url = new URL(baseURL, name);

				// check if the file exists
				InputStream in = null;
				try {
					in = url.openStream();
				} finally {
					if (in != null) {
						in.close();
					}
				}
			}
			ret.add(url);
		} catch (IOException e) {
			url = null;
		}

		// attempt to load from an absolute path
		File file = new File(name);
		if (file.isAbsolute() && file.exists()) // already absolute?
		{
			try {
				url = file.toURL();
				ret.add(url);
			} catch (MalformedURLException e) {
			}
		}

		// attempt to load from the base directory
		try {
			file = constructFile(base, name);
			if (file != null && file.exists()) {
				url = file.toURL();
			}

			if (url != null) {
				ret.add(url);
			}
		} catch (MalformedURLException e) {
		}

		// attempt to load from classpath
		Collection clsUrls = locateFromClasspath(name);
		if (clsUrls != null)
			ret.addAll(clsUrls);
		return ret;
	}

	/**
	 * Tries to find a resource with the given name in the classpath.
	 * 
	 * @param resourceName
	 *            the name of the resource
	 * @return the URL to the found resource or <b>null</b> if the resource cannot be found
	 */
	public static Collection locateFromClasspath(String resourceName) {
		Collection ret = new HashSet();
		Enumeration urls;
		// attempt to load from the context classpath
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		if (loader != null) {
			try {
				urls = loader.getResources(resourceName);
				if (urls != null) {
					while (urls.hasMoreElements()) {
						ret.add(urls.nextElement());
					}
				}
			} catch (IOException e) {
			}

		}

		// attempt to load from the system classpath
		try {
			urls = ClassLoader.getSystemResources(resourceName);
			if (urls != null) {
				while (urls.hasMoreElements()) {
					ret.add(urls.nextElement());
				}
			}
		} catch (IOException e) {
		}

		return ret;
	}

	/**
	 * Return the path without the file name, for example http://xyz.net/foo/bar.xml results in http://xyz.net/foo/
	 * 
	 * @param url
	 *            the URL from which to extract the path
	 * @return the path component of the passed in URL
	 */
	static String getBasePath(URL url) {
		if (url == null) {
			return null;
		}

		String s = url.toString();

		if (s.endsWith("/") || StringUtil.isEmpty(url.getPath())) {
			return s;
		} else {
			return s.substring(0, s.lastIndexOf("/") + 1);
		}
	}

	/**
	 * Extract the file name from the specified URL.
	 * 
	 * @param url
	 *            the URL from which to extract the file name
	 * @return the extracted file name
	 */
	static String getFileName(URL url) {
		if (url == null) {
			return null;
		}

		String path = url.getPath();

		if (path.endsWith("/") || StringUtil.isEmpty(path)) {
			return null;
		} else {
			return path.substring(path.lastIndexOf("/") + 1);
		}
	}

	/**
	 * Tries to convert the specified base path and file name into a file object. This method is called e.g. by the save() methods of file based
	 * configurations. The parameter strings can be relative files, absolute files and URLs as well. This implementation checks first whether the
	 * passed in file name is absolute. If this is the case, it is returned. Otherwise further checks are performed whether the base path and file
	 * name can be combined to a valid URL or a valid file name. <em>Note:</em> The test if the passed in file name is absolute is performed using
	 * <code>java.io.File.isAbsolute()</code>. If the file name starts with a slash, this method will return <b>true</b> on Unix, but <b>false</b> on
	 * Windows. So to ensure correct behavior for relative file names on all platforms you should never let relative paths start with a slash. E.g. in
	 * a configuration definition file do not use something like that:
	 * 
	 * <pre>
	 * &lt;properties fileName="/subdir/my.properties"/&gt;
	 * </pre>
	 * 
	 * Under Windows this path would be resolved relative to the configuration definition file. Under Unix this would be treated as an absolute path
	 * name.
	 * 
	 * @param basePath
	 *            the base path
	 * @param fileName
	 *            the file name
	 * @return the file object (<b>null</b> if no file can be obtained)
	 */
	public static File getFile(String basePath, String fileName) {
		// Check if the file name is absolute
		File f = new File(fileName);
		if (f.isAbsolute()) {
			return f;
		}

		// Check if URLs are involved
		URL url;
		try {
			url = new URL(new URL(basePath), fileName);
		} catch (MalformedURLException mex1) {
			try {
				url = new URL(fileName);
			} catch (MalformedURLException mex2) {
				url = null;
			}
		}

		if (url != null) {
			return fileFromURL(url);
		}

		return constructFile(basePath, fileName);
	}

	/**
	 * Tries to convert the specified URL to a file object. If this fails, <b>null</b> is returned.
	 * 
	 * @param url
	 *            the URL
	 * @return the resulting file object
	 * @throws UnsupportedEncodingException
	 */
	public static File fileFromURL(URL url) {
		if (PROTOCOL_FILE.equals(url.getProtocol())) {
			try {
				return fileFromURL(URLDecoder.decode(url.getPath(), "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException(e);
			}
		} else {
			return null;
		}
	}

	public static File fileFromURL(String fileUrl) {
		fileUrl = (fileUrl.startsWith("file:")) ? fileUrl.substring(5) : fileUrl;
		return new File(fileUrl);
	}

	/**
	 * 包含文件系统和classpath,及classpath中的jar文件
	 * 
	 * @param basePath
	 * @param recursive
	 * @return
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public static Collection listFiles(String basePath, boolean recursive) throws IOException, URISyntaxException {
		Collection ret = new HashSet();
		Collection urls = locate(basePath);
		System.out.println(basePath + " basePath Found files=" + urls);
		for (Iterator itr = urls.iterator(); itr.hasNext();) {
			URL url = (URL) itr.next();

			if (PROTOCOL_FILE.equals(url.getProtocol())) {// path
				ret.addAll(listDirFiles(url, recursive));
			} else {
				String filePath = url.getPath();
				int jarindex = filePath.indexOf(".jar!");
				if (jarindex != -1) {// jar file:includes protocol:jar and wsjar ...
					String jarFileName = filePath.substring(0, jarindex + 4);
					String jarPath = filePath.substring(jarindex + 6);
					JarFile jarfile = new JarFile(fileFromURL(jarFileName));
					Enumeration jarentries = jarfile.entries();
					while (jarentries.hasMoreElements()) {
						JarEntry jarentry = (JarEntry) jarentries.nextElement();
						if (jarentry.getName().startsWith(jarPath) && !jarentry.isDirectory()) {
							String fileUriName = jarFileName + "!/" + jarentry.getName();
							ret.add(new URL(url.getProtocol(), "", fileUriName));
						}
					}
					jarfile.close();
				}
			}
		}
		return ret;
	}

	/**
	 * 列表目录下的文件
	 * <p>
	 * 不能列表jar文件中的目上录
	 * 
	 * @param dirUrl
	 *            指定根目录
	 * @param recursive
	 *            是否包括下级子目录
	 * @return Collections of URL
	 * @throws MalformedURLException
	 */
	public static Collection listDirFiles(URL dirUrl, boolean recursive) throws MalformedURLException {
		Collection ret = new HashSet();
		if (dirUrl == null || !PROTOCOL_FILE.equals(dirUrl.getProtocol()))
			return ret;
		File dir = fileFromURL(dirUrl);
		if (dir.isFile()) {
			ret.add(dirUrl);
		} else if (dir.isDirectory()) {
			File[] files = dir.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory() && recursive) {
					Collection subfiles = listDirFiles(files[i].toURL(), recursive);
					ret.addAll(subfiles);
				} else {
					ret.add(files[i].toURL());
				}
			}
		}
		return ret;
	}
}
