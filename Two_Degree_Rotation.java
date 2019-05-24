import ij.IJ;
import ij.ImagePlus;
import ij.plugin.PlugIn;

public class Two_Degree_Rotation implements PlugIn {

	public void run(String arg) {
		ImagePlus imp = IJ.getImage();
		IJ.run(imp, "Rotate...", "angle=-2 grid=1 interpolation=Bilinear");
	}
}
