import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;

public class Gaussian_Blur implements PlugInFilter {
	ImagePlus imp;
	int[] mask = new int[] { 1, 4, 6, 4, 1 };

	int iterations = 1;

	public int setup(String arg, ImagePlus imp) {
		this.imp = imp;
		return DOES_8G;
	}

	public void run(ImageProcessor ip) {
		boolean next = showDialog();

		if (!next)
			return;

		ByteProcessor bp = ip.duplicate().convertToByteProcessor();

		int height = bp.getHeight();
		int width = bp.getWidth();

		int[][] pixels = bp.getIntArray();

		ByteProcessor newBp = new ByteProcessor(width, height);

		int[][] tempPixels;

		int half = mask.length / 2;

		for (int i = 0; i < iterations; i++) {
			tempPixels = new int[width][height];
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					int sum = 0;
					int sumFactor = 0;
					for (int xm = -half; xm <= half; xm++) {
						for (int ym = -half; ym <= half; ym++) {
							if (x + xm >= 0 && x + xm < width && y + ym >= 0 && y + ym < height) {
								int factor = gaussianAt(xm + half, ym + half);
								sumFactor += factor;
								sum += pixels[x + xm][y + ym] * factor;
							}
						}
					}
					tempPixels[x][y] = sum / sumFactor;
				}
			}
			pixels = tempPixels;
		}

		newBp.setIntArray(pixels);
		ImagePlus newImp = new ImagePlus(imp.getTitle() + " (Gaussian)", newBp);

		newImp.show();
	}

	private int gaussianAt(int x, int y) {
		return mask[x] * mask[y];
	}

	private boolean showDialog() {
		GenericDialog gd = new GenericDialog("Gaussian");
		gd.addNumericField("Iterations:", iterations, 0);
		gd.showDialog();
		if (gd.wasCanceled())
			return false;
		iterations = (int) gd.getNextNumber();

		if (iterations < 1 || iterations > 20)
			return false;

		return true;
	}
}
