package common.libTest.commons.io;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

public class UsageFileUtils {

	public static List<File> listFiles(File srcFile) {
		return (List<File>) FileUtils.listFiles(srcFile, null, false);
	}

	public static List<File> listFiles(File srcFile, String[] extensions) {
		return (List<File>) FileUtils.listFiles(srcFile, extensions, false);
	}

	public static List<File> listFilesRecursive(File srcFile) {
		return (List<File>) FileUtils.listFiles(srcFile, null, true);
	}

	public static List<File> listFilesRecursive(File srcFile, String[] extensions) {
		return (List<File>) FileUtils.listFiles(srcFile, extensions, true);
	}

	public static List<File> listFilesByPrefixFilter(File srcFile, String prefix) {
		IOFileFilter fileFilter = FileFilterUtils.prefixFileFilter(prefix);
		return (List<File>) FileUtils.listFiles(srcFile, fileFilter, null);
	}

	public static List<File> listFilesRecursiveByPrefixFilter(File srcFile, String prefix) {
		IOFileFilter fileFilter = FileFilterUtils.prefixFileFilter(prefix);
		return (List<File>) FileUtils.listFiles(srcFile, fileFilter, TrueFileFilter.INSTANCE);
	}

	public static List<File> listFilesByNameFilter(File srcFile, String fileName) {
		IOFileFilter fileFilter = FileFilterUtils.nameFileFilter(fileName);
		return (List<File>) FileUtils.listFiles(srcFile, fileFilter, null);
	}

	public static List<File> listFilesRecursiveByNameFilter(File srcFile, String fileName) {
		IOFileFilter fileFilter = FileFilterUtils.nameFileFilter(fileName);
		return (List<File>) FileUtils.listFiles(srcFile, fileFilter, TrueFileFilter.INSTANCE);
	}

	public static File getFile(File srcFilePath) {
		return FileUtils.getFile(srcFilePath);
	}

	public static File getFile(File srcFilePath, String fileName) {
		return FileUtils.getFile(srcFilePath, fileName);
	}

	public static File copyFile(File srcFile, File destFile) {
		try {
			FileUtils.copyFile(srcFile, destFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return destFile;
	}

	public static File writeStringToFile(File destFile, String srcStr) {
		try {
			FileUtils.writeStringToFile(destFile, srcStr, "UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return destFile;
	}

	public static String readFileToString(File srcFile) {
		String destStr = "";
		try {
			destStr = FileUtils.readFileToString(srcFile, "UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return destStr;
	}

	public static boolean cleanDirectory(File srcFilePath) {
		boolean isSuccess = false;
		try {
			FileUtils.cleanDirectory(srcFilePath);
			isSuccess = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return isSuccess;
	}

	public static boolean deleteDirectory(File srcFilePath) {
		boolean isSuccess = false;
		try {
			FileUtils.deleteDirectory(srcFilePath);
			isSuccess = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return isSuccess;
	}

}
