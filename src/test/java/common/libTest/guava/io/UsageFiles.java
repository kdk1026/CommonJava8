package common.libTest.guava.io;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

public class UsageFiles {

	public static List<File> listFiles(File srcFile) {
		return (List<File>) Files.fileTreeTraverser().preOrderTraversal(srcFile).toList();
	}
	
	public static File copyFile(File srcFile, File destFile) {
		try {
			Files.copy(srcFile, destFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return destFile;
	}
	
	public static File writeStringToFile(File destFile, String srcStr) {
		try {
			Files.write(srcStr, destFile, Charsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return destFile;
	}

	public static String readFileToString(File srcFile) {
		String destStr = "";
		try {
			destStr = Files.toString(srcFile, Charsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return destStr;
	}
	
}
