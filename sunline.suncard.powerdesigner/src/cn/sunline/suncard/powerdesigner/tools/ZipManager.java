/* 文件名：     ZipManager.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2012-12-18
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.tools;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;

import oracle.net.TNSAddress.SOException;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.apache.tools.zip.ZipOutputStream;

import cn.sunline.suncard.powerdesigner.resource.SystemConstants;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;

/**
 * Zip文件管理类
 * 
 * @author Manzhizhen
 * @version 1.0, 2012-12-18
 * @see
 * @since 1.0
 */
public class ZipManager {
	public final static int BUFFER_SIZE = 8 * 1024;

	// private static Log logger = LogManager
	// .getLogger(ZipManager.class.getName());

	/**
	 * 压缩一个文件夹下面的所有文件
	 * 
	 * @param sourceFilePath
	 *            要压缩的文件夹路径
	 * @param targetFilePath
	 *            压缩文件的路径
	 * @param fileName
	 * @throws IOException
	 */
	public static void compressFiles(String sourceFilePath,
			String targetFilePath) throws IOException {
		if (sourceFilePath == null || targetFilePath == null) {
			// logger.error("要压缩的文件夹路径或压缩文件路径为空，压缩文件失败！");
			return;
		}

		File sourceFile = new File(sourceFilePath);
		if (!sourceFile.isDirectory()) {
			// //logger.error("要压缩的文件夹路径不正确，压缩文件失败！");
			return;
		}

		File targetFile = new File(targetFilePath);
		if (!targetFile.isFile()) {
			// logger.error("压缩文件路径不正确，压缩文件失败！");
			return;
		}
		if (!targetFile.exists()) {
			targetFile.createNewFile();
		}

		compressFiles(sourceFile, targetFile);

	}

	/**
	 * 压缩一个文件夹下面的所有文件
	 * 
	 * @param sourceFileFolder
	 *            要压缩的文件夹
	 * @param zipFile
	 *            压缩文件
	 * @param fileName
	 * @throws IOException
	 */
	public static void compressFiles(File sourceFileFolder, File zipFile)
			throws IOException {
		if (sourceFileFolder == null || !sourceFileFolder.isDirectory()) {
			// logger.error("要压缩的文件夹路径为空或错误，压缩文件失败！");
			return;
		}

		if (zipFile == null) {
			// logger.error("压缩文件路径为空或错误，压缩文件失败！");
			return;
		}

		if (!zipFile.exists()) {
			if (!zipFile.getParentFile().exists()) {
				zipFile.getParentFile().mkdirs();
			}

			zipFile.createNewFile();
		}

		FileOutputStream fileOutputStream = new FileOutputStream(zipFile);
		CheckedOutputStream cos = new CheckedOutputStream(fileOutputStream,
				new CRC32()); // 不加CRC32，一样可以生成文件。关于数据如何校验，请高手指点

		ZipOutputStream zipOutputStream = new ZipOutputStream(cos);
		zipOutputStream.setEncoding(SystemConstants.FILE_CODE_GBK);
		compress(zipOutputStream, sourceFileFolder, "");

		zipOutputStream.close();
		// logger.info(zipFile.getAbsoluteFile() + "压缩完成！");

	}

	private static void compress(ZipOutputStream out, File file, String path)
			throws IOException {
		BufferedInputStream bis = null;

		try {
			if (file.isDirectory()) {
				File[] childFiles = file.listFiles();
				// 处理空文件夹
				if (childFiles.length == 0) {
					ZipEntry zipEntry = new ZipEntry(path + "/");
					out.putNextEntry(zipEntry);
				} else {
					path = path.length() == 0 ? "" : path + "/";
					for (int i = 0; i < childFiles.length; i++) {
						compress(out, childFiles[i], path + childFiles[i].getName());
					}
				}

			} else {
				out.putNextEntry(new ZipEntry(path));
				bis = new BufferedInputStream(new FileInputStream(file));

				int size;
				byte[] bytes = new byte[BUFFER_SIZE];
				while ((size = bis.read(bytes)) != -1) {
					out.write(bytes, 0, size);
				}

				out.closeEntry();

			}
		} catch (FileNotFoundException e) {
			throw e;
		} catch (IOException e) {
			throw e;

		} finally {
			if (bis != null) {
				bis.close();
			}
		}
	}

	/**
	 * 解压文件到一个文件夹下
	 * 
	 * @throws Exception
	 * @throws IOException
	 */
	public static void decompressFile(File compressFile, File targetFolder)
			throws IOException, Exception {
		if (compressFile == null || !compressFile.exists()) {
			// logger.error("压缩文件为空或不正确，解压文件失败！");
			return;
		}

		if (targetFolder == null) {
			// logger.error("目标目录为空，解压文件失败！");
			return;
		}

		if (!targetFolder.isDirectory()) {
			targetFolder.mkdirs();
		}

		ZipFile zipFile = new ZipFile(compressFile, SystemConstants.FILE_CODE_GBK);

		decompressFile(zipFile, targetFolder.getAbsolutePath());
		zipFile.close();
		// logger.info("成功解压文件到：" + targetFolder.getAbsolutePath());

	}

	public static void decompressFile(ZipFile zipFile, String unZipRoot)
			throws IOException {
		FileOutputStream fos = null;
		InputStream is = null;
		Enumeration e = zipFile.getEntries();
		int len = 0;
		ZipEntry zipEntry;
		byte[] bytes = new byte[BUFFER_SIZE];

		if (!unZipRoot.endsWith(File.separator)) {
			unZipRoot += File.separator;
		}

		String perUnzipFilePath = "";
		try {
			while (e.hasMoreElements()) {
				zipEntry = (ZipEntry) e.nextElement();
				InputStream fis = zipFile.getInputStream(zipEntry);
				perUnzipFilePath = unZipRoot + zipEntry.getName();

				File perUnzipFile = new File(perUnzipFilePath);
				if (zipEntry.isDirectory()) {
					if (!perUnzipFile.exists()) {
						perUnzipFile.mkdirs();
					}
				} else {
					File parentFile = perUnzipFile.getParentFile();
					if (!parentFile.exists()) {
						parentFile.mkdirs();
					}

					if (!perUnzipFile.exists()) {
						perUnzipFile.createNewFile();
					}
				}

				if (perUnzipFile.isDirectory()) {
					File[] files = perUnzipFile.listFiles();
					for (int i = 0; i < files.length; i++) {
						File file = files[i];
						fos = new FileOutputStream(file);
						is = zipFile.getInputStream(zipEntry);
						while ((len = is.read(bytes)) != -1) {
							fos.write(bytes, 0, len);
						}
					}
				} else {
					fos = new FileOutputStream(perUnzipFile);
					is = zipFile.getInputStream(zipEntry);
					while ((len = is.read(bytes)) != -1) {
						fos.write(bytes, 0, len);
					}
				}
			}
		} catch (ZipException e1) {
			throw e1;
		} catch (FileNotFoundException e1) {
			throw e1;
		} catch (IOException e1) {
			throw e1;
		} finally {
			if (fos != null) {
				fos.close();
			}
			if (is != null) {
				is.close();
			}

		}
	}

}
