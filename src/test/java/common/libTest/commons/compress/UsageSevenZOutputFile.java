package common.libTest.commons.compress;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;
import org.apache.commons.compress.archivers.sevenz.SevenZOutputFile;
import org.apache.commons.io.FileUtils;

public class UsageSevenZOutputFile {

	public static void main(String[] args) {
		/*File src = new File("C:\\test2\\simpleChat");
		File destFile = new File("C:\\test2\\simpleChat2.7z");

		compress(src, destFile);*/

		File srcFile = new File("C:\\test2\\simpleChat2.7z");
		File dest = new File("C:\\test2\\simpleChat2");

		uncompress(srcFile, dest);
	}

	public static void compress(File src, File destFile) {
		if (destFile.exists()) {
			destFile.delete();
		}

		try {
			SevenZOutputFile sevenZOutput = new SevenZOutputFile(destFile);

			List<File> fileList = (List<File>) FileUtils.listFiles(src, null, true);
			for (File file : fileList) {
				String sEntryName = getEntryName(src, file);
				File entryFile = new File(sEntryName);

				SevenZArchiveEntry entry = sevenZOutput.createArchiveEntry(entryFile, sEntryName);
				sevenZOutput.putArchiveEntry(entry);

				copy(file, sevenZOutput);

				sevenZOutput.closeArchiveEntry();
			}
			sevenZOutput.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String getEntryName(File src, File file) throws IOException {
		int nIdx = src.getAbsolutePath().length() + 1;
		String sPath = file.getCanonicalPath();

		return sPath.substring(nIdx);
	}

	private static void copy(File file, SevenZOutputFile sevenZOutput) {
		try {
			InputStream is = new BufferedInputStream(new FileInputStream(file));
			byte[] buffer = new byte[8*1024];
			int bytesRead;
			while ((bytesRead = is.read(buffer)) >= 0) {
				sevenZOutput.write(buffer, 0, bytesRead);
			}
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void uncompress(File srcFile, File dest) {
		try {
			if ( !dest.exists() ) {
				dest.mkdirs();
			}

			SevenZFile sevenZFile = new SevenZFile(srcFile);

			SevenZArchiveEntry entry = sevenZFile.getNextEntry();
			while ( entry != null ) {
				String sName = entry.getName();
				File outFile = new File(dest, sName);

				if ( !outFile.getParentFile().exists() ) {
					outFile.getParentFile().mkdirs();
				}

				try {
					outFile.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}

				entry = sevenZFile.getNextEntry();
			}
			sevenZFile.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
