package common.libTest.commons.compress;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.io.FileUtils;

public class UsageZipArchiveStream {

	public static void main(String[] args) {
		/*File src = new File("C:\\test2\\simpleChat");
		File destFile = new File("C:\\test2\\simpleChat2.zip");

		zip(src, destFile);*/

		File srcFile = new File("C:\\test2\\simpleChat2.zip");
		File dest = new File("C:\\test2\\simpleChat2");

		unzip(srcFile, dest);
	}

	public static void zip(File src, File destFile) {
		if (destFile.exists()) {
			destFile.delete();
		}

		try {
			OutputStream os = new FileOutputStream(destFile);
			ZipArchiveOutputStream zaos = new ZipArchiveOutputStream(os);

			List<File> fileList = (List<File>) FileUtils.listFiles(src, null, true);

			for (File file : fileList) {
				String sEntryName = getEntryName(src, file);

				ZipArchiveEntry entry = new ZipArchiveEntry(sEntryName);
				zaos.putArchiveEntry(entry);

				copy(file, zaos);

				zaos.closeArchiveEntry();
			}

			zaos.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String getEntryName(File src, File file) throws IOException {
		int nIdx = src.getAbsolutePath().length() + 1;
		String sPath = file.getCanonicalPath();

		return sPath.substring(nIdx);
	}

	private static void copy(File file, ZipArchiveOutputStream zaos) {
		try {
			InputStream is = new BufferedInputStream(new FileInputStream(file));
			byte[] buffer = new byte[8*1024];
			int bytesRead;
			while ((bytesRead = is.read(buffer)) >= 0) {
				zaos.write(buffer, 0, bytesRead);
			}
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void unzip(File srcFile, File dest) {
		try {
			if ( !dest.exists() ) {
				dest.mkdirs();
			}

			InputStream is = new BufferedInputStream(new FileInputStream(srcFile));
			ZipArchiveInputStream zais = new ZipArchiveInputStream(is);

			ZipArchiveEntry entry = (ZipArchiveEntry) zais.getNextEntry();
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

				entry = (ZipArchiveEntry) zais.getNextEntry();
			}
			zais.close();
			is.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
