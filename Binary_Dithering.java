import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;

public class Binary_Dithering implements PlugInFilter {
	ImagePlus imp;

	public int setup(String arg, ImagePlus imp) {
		this.imp = imp;
		return DOES_RGB + DOES_8G;
	}

	public void run(ImageProcessor ip) {
		int height = ip.getHeight();
		int width = ip.getWidth();

		ImageProcessor bp = new ByteProcessor(width, height);
		ImagePlus newImage = new ImagePlus(imp.getTitle(), bp);

		int[][] pixels = ip.getIntArray();
		int[][] grayPixels = bp.getIntArray();

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int argb = pixels[x][y];
				int r = (argb >> 16) & 255;
				int g = (argb >> 8) & 255;
				int b = argb & 255;

				int cc = (2 * r + 3 * g + b) / 6;

				grayPixels[x][y] = cc;
			}
		}

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {

				int cc = clamp(grayPixels[x][y]);
				int rc = (cc < 128 ? 0 : 255);
				int err = cc - rc;
				grayPixels[x][y] = rc;

				if (x + 1 < width)
					grayPixels[x + 1][y] += (err * 7) >> 4;
				if (y + 1 == height)
					continue;
				if (x > 0)
					grayPixels[x - 1][y + 1] += (err * 3) >> 4;
				grayPixels[x][y + 1] += (err * 5) >> 4;
				if (x + 1 < width)
					grayPixels[x + 1][y + 1] += (err * 1) >> 4;
			}
		}

		bp.setIntArray(grayPixels);
		newImage.setProcessor(null, bp);

		newImage.show();
	}

	int clamp(int value) {
		return value < 0 ? 0 : value > 255 ? 255 : value;
	}
}
