package common.libTest.commons.compress;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.io.FileUtils;

public class UsageTarArchiveStream {

	public static void main(String[] args) {
		/*File src = new File("C:\\test2\\simpleChat");
		File destFile = new File("C:\\test2\\simpleChat2.tar");

		archive(src, destFile);*/

		File srcFile = new File("C:\\test2\\simpleChat2.tar");
		File dest = new File("C:\\test2\\simpleChat2");

		unarchive(srcFile, dest);
	}

	public static void archive(File src, File destFile) {
		if (destFile.exists()) {
			destFile.delete();
		}

		try {
			OutputStream os = new FileOutputStream(destFile);
			TarArchiveOutputStream taos = new TarArchiveOutputStream(os);
			taos.setLongFileMode(TarArchiveOutputStream.LONGFILE_POSIX);

			List<File> fileList = (List<File>) FileUtils.listFiles(src, null, true);

			for (File file : fileList) {
				String sEntryName = getEntryName(src, file);

				TarArchiveEntry entry = new TarArchiveEntry(sEntryName);
				taos.putArchiveEntry(entry);

				copy(file, taos);

				taos.closeArchiveEntry();
			}

			taos.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String getEntryName(File src, File file) throws IOException {
		int nIdx = src.getAbsolutePath().length() + 1;
		String sPath = file.getCanonicalPath();

		return sPath.substring(nIdx);
	}

	private static void copy(File file, TarArchiveOutputStream taos) {
		try {
			InputStream is = new BufferedInputStream(new FileInputStream(file));
			byte[] buffer = new byte[8*1024];
			int bytesRead;
			while ((bytesRead = is.read(buffer)) >= 0) {
				taos.write(buffer, 0, bytesRead);
			}
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void unarchive(File srcFile, File dest) {
		try {
			if ( !dest.exists() ) {
				dest.mkdirs();
			}

			InputStream is = new BufferedInputStream(new FileInputStream(srcFile));
			TarArchiveInputStream tais = new TarArchiveInputStream(is);

			TarArchiveEntry entry = (TarArchiveEntry) tais.getNextEntry();
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

				entry = (TarArchiveEntry) tais.getNextEntry();
			}
			tais.close();
			is.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
