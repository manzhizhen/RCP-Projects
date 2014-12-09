package cn.sunline.suncard.sde.workflow.file;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;

public class FileDeal {
	// byte数组的大小
	public final static int BUFFER_SIZE = 0x0088ffff;
	
	public static Log logger = LogManager.getLogger(FileDeal.class.getName());

	public boolean copyFile(String src, String des) {
		FileInputStream FIS = null;
		FileOutputStream FOS = null;

		try {
			FIS = new FileInputStream(src);
			FOS = new FileOutputStream(des);
			byte[] bt = new byte[BUFFER_SIZE];
			int readNum = 0;
			while ((readNum = FIS.read(bt)) != -1) {
				FOS.write(bt, 0, bt.length);
			}

			return true;

		} catch (Exception e) {
			return false;

		} finally {
			try {
				if (FIS != null && FOS != null) {
					FIS.close();
					FOS.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 通过文件路径获得文件名字（不包括小数点和扩展名）
	 * 
	 * @param filePath
	 * @return
	 */
	public static String getFileName(String filePath) {

		try {
			File file = new File(filePath);
			if (file.isFile()) {
				String fileName = file.getName();
				return fileName.substring(0, fileName.lastIndexOf("."));
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
			return null;
		}

		return null;
	}

	/**
	 * 移动文件到目标 路径，如果目标文件存在，会覆盖。
	 * 
	 * @param startFile
	 * @param targetFile
	 * @throws IOException
	 */
	public static void moveFileToPath(String startFile, String targetFile)
			throws IOException {
		logger.info("public static void moveFileToPath(String startFile, String targetFile) ...");
		
		logger.info("起始文件：" + startFile);
		logger.info("目标文件：" + targetFile);		
		
		BufferedInputStream bi = null;
		BufferedOutputStream bo = null;

		try {
			bi = new BufferedInputStream(new FileInputStream(startFile));
			bo = new BufferedOutputStream(new FileOutputStream(targetFile));
			byte[] bytes = new byte[BUFFER_SIZE];
			int count;
			while ((count = bi.read(bytes, 0, BUFFER_SIZE)) >= 0) {
				bo.write(bytes, 0, count);
			}

		} finally {
			if (bi != null) {
				bi.close();
			}

			if (bo != null) {
				bo.close();
			}
		}

		new File(startFile).delete();
	}

	/**
	 * 复制文件到目标 路径，如果目标文件存在，会覆盖。
	 * 
	 * @param startFile
	 * @param targetFile
	 * @throws IOException
	 */
	public static void copyFileToPath(String startFile, String targetFile)
			throws IOException {
		logger.info("public static void copyFileToPath(String startFile, String targetFile) ...");
		
		logger.info("起始文件：" + startFile);
		logger.info("目标文件：" + targetFile);		
		
		BufferedInputStream bi = null;
		BufferedOutputStream bo = null;

		try {
			bi = new BufferedInputStream(new FileInputStream(startFile));
			bo = new BufferedOutputStream(new FileOutputStream(targetFile));
			byte[] bytes = new byte[BUFFER_SIZE];
			int count;
			while ((count = bi.read(bytes, 0, BUFFER_SIZE)) >= 0) {
				bo.write(bytes, 0, count);
			}

		} finally {
			if (bi != null) {
				bi.close();
			}

			if (bo != null) {
				bo.close();
			}
		}
	}

	/**
	 * 拷贝一个目录下的所有文件到另一个目录下
	 * 
	 * @param SrcDirectoryPath
	 * @param DesDirectoryPath
	 * @return
	 */
	public static boolean copyFolderToPath(String SrcDirectoryPath,
			String DesDirectoryPath) {
		logger.info("public static boolean copyFolderToPath(String SrcDirectoryPath, String DesDirectoryPath) ...");
		
		logger.info("起始路径：" + SrcDirectoryPath);
		logger.info("目标路径：" + DesDirectoryPath);	
		
		try {
			// 創建不存在的目錄
			File F0 = new File(DesDirectoryPath);
			if (!F0.exists()) {
				if (!F0.mkdir()) {
					return false;
				}
			}
			
			File F = new File(SrcDirectoryPath);
			File[] allFile = F.listFiles(); // 取得當前目錄下面的所有文件，將其放在文件數組中
			int totalNum = allFile.length; // 取得當前文件夾中有多少文件（包括文件夾）
			String srcName = "";
			String desName = "";
			int currentFile = 0;
			
			// 一個一個的拷貝文件
			for (currentFile = 0; currentFile < totalNum; currentFile++) {
				if (!allFile[currentFile].isDirectory()) {
					// 如果是文件是采用處理文件的方式
					srcName = allFile[currentFile].toString();
					desName = DesDirectoryPath + "\\"
							+ allFile[currentFile].getName();
	
					copyFileToPath(srcName, desName);
				}
				// 如果是文件夾就采用遞歸處理
				else {
					// 利用遞歸讀取文件夾中的子文件下的內容，再讀子文件夾下面的子文件夾下面的內容...
					if (copyFolderToPath(allFile[currentFile].getPath()
							.toString(), DesDirectoryPath + "\\"
							+ allFile[currentFile].getName().toString())) {
						// System.out.println("D Copy Successfully!");
					} else {
						System.out.println("SubDirectory Copy Error!");
					}
				}
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	/**
	 * 删除文件或文件夹
	 * 
	 * @param filePath
	 */
	public static void deleteFile(String filePath) {
		logger.info("public static void deleteFile(String filePath) ...");
		
		logger.info("要删除的文件路径：" + filePath);
		
		File file = new File(filePath);
		if (!file.exists()) {
			return;
		}

		if (file.isFile()) {
			file.delete();
			return;
		}

		if (!filePath.endsWith(File.separator)) {
			filePath = filePath + File.separator;
		}

		File[] files = file.listFiles();
		for (File childFile : files) {
			if (childFile.isFile()) {
				childFile.delete();
			} else {
				deleteFile(childFile.getAbsolutePath());
			}
		}

		file.delete();
	}

	public static void main(String[] args) {
		// deleteFile(new File("d:/4444").getAbsolutePath());

		copyFolderToPath("d:/3333", "d:/4444");
	}
}
