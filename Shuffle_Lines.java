import ij.*;
import ij.process.*;
import ij.plugin.filter.PlugInFilter;
import java.util.*;
import java.util.List;
import java.util.Arrays;

public class Shuffle_Lines implements PlugInFilter {
	ImagePlus imp; 


	public int setup(String arg, ImagePlus imp) {
		this.imp = imp;
		return DOES_RGB;
	}


	public void run(ImageProcessor ip) {
		int height = ip.getHeight();
		int width = ip.getWidth();

        int[][] pixels = ip.getIntArray();

        List<int[]> pixelList = Arrays.asList(pixels);

        Collections.shuffle(pixelList);

        int[][] newPixels = pixelList.toArray(new int[][]{});
        
        ImageProcessor newIp = new ColorProcessor(width, height);
        newIp.setIntArray(newPixels);
        ImagePlus newImp = new ImagePlus(imp.getTitle() + " (shuffled)", newIp);

        newImp.show();
	}
}
