package common.test;

import static org.junit.Assert.assertTrue;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import org.junit.Test;

public class TestMetadata {

	@Test
	public void test() {

		String sPath = "C:/test/반명함.jpg";
		File file = new File(sPath);

		BufferedImage image = null;
		try {
			image = ImageIO.read(file);

			getRead(image);

		} catch (Exception e) {
			e.printStackTrace();
		}

		assertTrue(true);
	}

	public void getRead(BufferedImage image) {
		int width = image.getWidth();
		int height = image.getHeight();

		System.out.println(width);
		System.out.println(height);

		Color color = null;
		int lightBlue = new Color(212, 228, 241).getRGB();

		System.out.println(lightBlue);
//		int fw = 0;
//		int fh = 0;

		for (int w = 0; w < width; w++) {
			for (int h = 0; h < height; h++) {
				color = new Color(image.getRGB(w, h));

				/*
				if ( color.getRGB() == lightBlue ) {
					System.out.println( color.getRGB() );
					fw = w;
					fh = h;
					System.out.println( w + ", " + h  );
					break;
				}
				*/

				if ( color.getRed() > 235
						&& color.getGreen() < 200 || color.getGreen() > 245
						&& color.getBlue() > 230) {

					image.setRGB(w, h, Color.WHITE.getRGB());
				}
			}

			/*
			if ( fw > 0 && fh > 0 ) {
				break;
			}
			*/
		}

		try {
			ImageIO.write(image, "jpg", new File("C:/test/find.jpg"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
