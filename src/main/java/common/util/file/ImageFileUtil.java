package common.util.file;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author 김대광
 * <pre>
 * -----------------------------------
 * 개정이력
 * 2019. 5. 4. 김대광	최초작성
 * </pre>
 */
public class ImageFileUtil {

	private ImageFileUtil() {

	}

	private static final Logger logger = LoggerFactory.getLogger(ImageFileUtil.class);

	/**
	 * 이미지 리사이즈
	 * @param sSrcPath
	 * @param sDestPath
	 * @param nWidth
	 * @param nHeight
	 * @param isScale 크키가 큰 폭을 기준으로 동일 비율 처리 여부
	 */
	public static void resize(String sSrcPath, String sDestPath, int nWidth, int nHeight, boolean isScale) {
		if ( StringUtils.isBlank(sSrcPath) ) {
			throw new NullPointerException("sSrcPath is null");
		}

		if ( StringUtils.isBlank(sDestPath) ) {
			throw new NullPointerException("sDestPath is null");
		}

		if ( nWidth < 1 ) {
			throw new IllegalArgumentException("nWidth is less than 1");
		}

		if ( nHeight < 1 ) {
			throw new IllegalArgumentException("nHeight is less than 1");
		}

		File srcFile = new File(sSrcPath);
		File destFile = new File(sDestPath);
		resize(srcFile, destFile, nWidth, nHeight, isScale);
	}

	/**
	 * 이미지 리사이즈
	 * @param srcFile
	 * @param destFile
	 * @param nWidth
	 * @param nHeight
	 * @param isScale
	 */
	public static void resize(File srcFile, File destFile, int nWidth, int nHeight, boolean isScale) {
		if ( srcFile == null ) {
			throw new NullPointerException("srcFile is null");
		}

		if ( destFile == null ) {
			throw new NullPointerException("destFile is null");
		}

		if ( nWidth < 1 ) {
			throw new IllegalArgumentException("nWidth is less than 1");
		}

		if ( nHeight < 1 ) {
			throw new IllegalArgumentException("nHeight is less than 1");
		}

		final String sSrcPath = srcFile.getPath();
		final String sImageFormat = sSrcPath.substring(sSrcPath.lastIndexOf('.')+1);

		BufferedImage bufImg = null;
		Image image = null;
		Image resizedImg = null;

		try {
			// 원본 이미지 가져오기
			image = new ImageIcon(srcFile.getAbsolutePath()).getImage();

			if (image.getWidth(null) < 1 || image.getHeight(null) < 1) {
				throw new IllegalArgumentException("파일이 존재하지 않습니다.");
			}

			bufImg = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_4BYTE_ABGR);

			AffineTransform ax = new AffineTransform();
			ax.setToScale(1, 1);

			Graphics2D g2d = bufImg.createGraphics();
			g2d.drawImage(image, ax, null);

			int nNewWith = nWidth;
			int nNewHeight = nHeight;

			if (isScale) {
				double dScale = getScale(nWidth, nHeight, image.getWidth(null), image.getHeight(null));

				nNewWith = (int) (dScale * image.getWidth(null));
				nNewHeight = (int) (dScale * image.getHeight(null));
			}

			/**
			 * <참고>
			 * Image.SCALE_DEFAULT : 기본 이미지 스케일링 알고리즘 사용
			 * Image.SCALE_FAST    : 이미지 부드러움보다 속도 우선
			 * Image.SCALE_SMOOTH  : 속도보다 이미지 부드러움을 우선
			 * Image.SCALE_AREA_AVERAGING  : 평균 알고리즘 사용
			 */
			resizedImg = bufImg.getScaledInstance(nNewWith, nNewHeight, Image.SCALE_SMOOTH);

			writeImage(resizedImg, sImageFormat, destFile);

			//원본파일 삭제
			Path path = Paths.get(srcFile.getPath());
			Files.delete(path);

		} catch (IOException e) {
			logger.error("", e);

		} finally {
			if (resizedImg != null) {
				resizedImg.flush();
			}
			if (bufImg != null) {
				bufImg.flush();
			}
			if (image != null) {
				image.flush();
			}
		}
	}

	/**
	 * 이미지 저장
	 * @param image
	 * @param sImageFormat
	 * @param destFile
	 */
	private static void writeImage(Image image, String sImageFormat, File destFile) {
		if ( image == null ) {
			throw new NullPointerException("image is null");
		}

		if ( StringUtils.isBlank(sImageFormat) ) {
			throw new NullPointerException("sImageFormat is null");
		}

		if ( destFile == null ) {
			throw new NullPointerException("destFile is null");
		}

		BufferedImage bufImg = null;

		try {
			bufImg = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_3BYTE_BGR);
			Graphics2D g2d = bufImg.createGraphics();
			g2d.drawImage(image, 0, 0, null);
			ImageIO.write(bufImg, sImageFormat, destFile);

		} catch (IOException e) {
			logger.error("", e);

		} finally {
			if (bufImg != null) {
				bufImg.flush();
			}
		}
	}

	/**
	 * <pre>
	 * 리사이즈할 사이즈와 원본 이미지 사이즈로 스케일 비율 구함
	 *  - 크키가 큰 폭을 기준으로 동일 비율 처리
	 * </pre>
	 * @param nResizeWidth
	 * @param nResizeHeight
	 * @param nOrgWidth
	 * @param nOrgHeight
	 * @return
	 */
	private static double getScale(int nResizeWidth, int nResizeHeight, int nOrgWidth, int nOrgHeight) {
		if ( nResizeWidth < 1 ) {
			throw new IllegalArgumentException("nResizeWidth is less than 1");
		}

		if ( nResizeHeight < 1 ) {
			throw new IllegalArgumentException("nResizeHeight is less than 1");
		}

		if ( nOrgWidth < 1 ) {
			throw new IllegalArgumentException("nOrgWidth is less than 1");
		}

		if ( nOrgHeight < 1 ) {
			throw new IllegalArgumentException("nOrgHeight is less than 1");
		}

		double dWidthScale = (double) nResizeWidth / nOrgWidth;
		double dHeightScale = (double) nResizeHeight / (double) nOrgHeight;
		if (dWidthScale > dHeightScale) {
			return dHeightScale;
		} else {
			return dWidthScale;
		}
	}


}
