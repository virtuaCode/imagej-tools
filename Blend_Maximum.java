import java.awt.AWTEvent;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.DialogListener;
import ij.gui.GenericDialog;
import ij.plugin.ImageCalculator;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

public class Blend_Maximum implements PlugInFilter, DialogListener {
	String image2name;
	ImagePlus image1;

	public void run(ImageProcessor ip) {
		if (!SelectImageDialogs.selectImageDialog(image1, "Blend Maximum", image2name, this))
			return;

		ImagePlus image2 = WindowManager.getImage(image2name);

		ImageCalculator ic = new ImageCalculator();

		ImagePlus newImage = ic.run("Max create", image1, image2);

		newImage.show();
	}

	public int setup(String arg, ImagePlus imp) {
		image1 = imp;

		return PlugInFilter.DOES_8G;
	}

	@Override
	public boolean dialogItemChanged(GenericDialog gd, AWTEvent e) {
		image2name = gd.getNextChoice();
		return !gd.invalidNumber(); // input is valid if all numeric input is ok
	}
}
