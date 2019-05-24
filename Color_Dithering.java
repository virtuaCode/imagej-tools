import java.awt.image.ColorModel;

import ij.IJ;
import ij.ImagePlus;
import ij.Undo;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ImageConverter;
import ij.process.ImageProcessor;

public class Color_Dithering implements PlugInFilter {

    int colorCount = 2;

    ImagePlus imp;

    public int setup(String arg, ImagePlus imp) {
        this.imp = imp;
        return DOES_RGB;
    }

    public void run(ImageProcessor ip) {
        if (!showDialog())
            return;

        imp = IJ.getImage();
        Undo.setup(Undo.TRANSFORM, imp);

        ImagePlus newImage = new ImagePlus(imp.getTitle() + " (" + colorCount + " Colors)", imp.getProcessor());

        ImageProcessor cp = newImage.getProcessor().convertToColorProcessor();
        int height = cp.getHeight();
        int width = cp.getWidth();
        int[][] pixels = cp.getIntArray();
        byte[] bytePixels = new byte[width * height];

        ImageConverter ic = new ImageConverter(newImage);

        ic.convertRGBtoIndexedColor(colorCount);

        ImageProcessor indexedIp = newImage.getProcessor();

        ColorModel cm = indexedIp.getColorModel();

        int[] redErrors = new int[height * width];
        int[] greenErrors = new int[height * width];
        int[] blueErrors = new int[height * width];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int argb = pixels[x][y];
                int r = (argb >> 16) & 255;
                int g = (argb >> 8) & 255;
                int b = argb & 255;

                redErrors[y * width + x] = r;
                greenErrors[y * width + x] = g;
                blueErrors[y * width + x] = b;
            }
        }

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int cindex = y * width + x;

                int[] color = new int[]{ 
                    clamp(redErrors[cindex]), 
                    clamp(greenErrors[cindex]), 
                    clamp(blueErrors[cindex])
                };

                int[] indexedColor = closestColor(color, cm, colorCount);

                int red = indexedColor[0];
                int green = indexedColor[1];
                int blue = indexedColor[2];

                int redError = color[0] - red;
                int greenError = color[1] - green;
                int blueError = color[2] - blue;

                bytePixels[cindex] = (byte) indexedColor[3];

                redErrors[cindex] = red;
                greenErrors[cindex] = green;
                blueErrors[cindex] = blue;

                if (x + 1 < width) {
                    redErrors[cindex + 1] += (redError * 7) >> 4;
                    greenErrors[cindex + 1] += (greenError * 7) >> 4;
                    blueErrors[cindex + 1] += (blueError * 7) >> 4;
                }

                if (y + 1 == height) {
                    continue;
                }

                if (x > 0) {
                    redErrors[cindex + width - 1] += (redError * 3) >> 4;
                    greenErrors[cindex + width - 1] += (greenError * 3) >> 4;
                    blueErrors[cindex + width - 1] += (blueError * 3) >> 4;
                }

                redErrors[cindex + width] += (redError * 5) >> 4;
                greenErrors[cindex + width] += (greenError * 5) >> 4;
                blueErrors[cindex + width] += (blueError * 5) >> 4;

                if (x + 1 < width) {
                    redErrors[cindex + width + 1] += (redError) >> 4;
                    greenErrors[cindex + width + 1] += (greenError) >> 4;
                    blueErrors[cindex + width + 1] += (blueError) >> 4;
                }
            }
        }

        ByteProcessor bp = new ByteProcessor(width, height, bytePixels, cm);
        newImage.setProcessor(null, bp);
        newImage.show();
    }

    private int clamp(int n) {
        return n > 255 ? 255 : (n < 0 ? 0 : n);
    }

    private boolean showDialog() {
        GenericDialog gd = new GenericDialog("Color Dithering");
        gd.addNumericField("Colour count:", colorCount, 0);
        gd.showDialog();
        if (gd.wasCanceled())
            return false;
        colorCount = (int) gd.getNextNumber();

        if (colorCount < 2 || colorCount > 256)
            return false;

        return true;
    }

    private int[] closestColor(int[] color, ColorModel cm, int size) {
        int[] minColor = null;
        int index = -1;
        int colorDistance = -1;

        for (int i = 0; i < size; i++) {
            int r = cm.getRed(i);
            int g = cm.getGreen(i);
            int b = cm.getBlue(i);

            int[] cmColor = new int[] { r, g, b };
            int d = distance(color, cmColor);

            if (colorDistance == -1 || colorDistance > d) {
                minColor = cmColor;
                colorDistance = d;
                index = i;
            }
        }

        return new int[] { minColor[0], minColor[1], minColor[2], index };
    }

    private int distance(int[] color1, int[] color2) {
        int redDiff = color1[0] - color2[0];
        int greenDiff = color1[1] - color2[1];
        int blueDiff = color1[2] - color2[2];

        return redDiff * redDiff + greenDiff * greenDiff + blueDiff * blueDiff;
    }
}
