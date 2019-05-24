import ij.IJ;
import ij.ImagePlus;
import ij.plugin.PlugIn;

public class Invert_Image implements PlugIn {

	public void run(String arg) {
		ImagePlus imp = IJ.getImage();
		IJ.run(imp, "Invert", "");
	}
}
