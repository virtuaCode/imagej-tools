import ij.*;
import ij.process.*;
import ij.gui.*;
import ij.plugin.filter.PlugInFilter;

/**
 * Rotate a transparent version of the selected image on top of it self. 
 * Pixels that are less then 200 (grayscale) become transparent.
 * @author Michael Schmidt
 */
public class Rotate_Animation implements PlugInFilter {

    ImagePlus imp;    

    public void run(ImageProcessor ip) {
        int width = ip.getWidth();
        int height = ip.getHeight();

        ImageProcessor roiIp = (ImageProcessor) ip.duplicate();
        ByteProcessor mask = roiIp.duplicate().convertToByteProcessor();

        mask.threshold(200);

        for (int i = 0; i < width * height; i++) {
            if (mask.get(i) == 255) {
                roiIp.set(i, 0xFF000000);
            }
        }

        ImageRoi imageRoi = new ImageRoi(0, 0, roiIp);
        imageRoi.setZeroTransparent(true);
        
        Overlay overlay = new Overlay(imageRoi);
        imp.setOverlay(overlay);
        imp.setRoi(imageRoi);
        imp.show();

        for (int a=0; a<=360; a++) {
           imageRoi.setAngle(a);
           imp.draw();
           IJ.wait(20);
        }
    }

    @Override
    public int setup(String arg0, ImagePlus imp) {
        this.imp = imp;
        return DOES_ALL;
    }
}