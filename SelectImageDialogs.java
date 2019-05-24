import java.util.Vector;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.DialogListener;
import ij.gui.GenericDialog;
import ij.util.StringSorter;

public class SelectImageDialogs {
	public static boolean selectImageDialog(ImagePlus imp, String command, String image2name, DialogListener listener) {
		String[] suitableImages = getSuitableImages(imp); // images that we can blend with the current one
		if (suitableImages == null) {
			String type = imp.getBitDepth() == 24 ? "RGB" : "grayscale";
			IJ.error(command + " Error",
					"No suitable image (" + type + ", " + imp.getWidth() + "x" + imp.getHeight() + ") to blend with");
			return false;
		}
		GenericDialog gd = new GenericDialog(command + "...");
		gd.addMessage("Image 1: " + imp.getTitle());
		gd.addChoice("Image 2:", suitableImages, image2name);
		gd.addDialogListener(listener);
		gd.showDialog(); // user input (or reading from macro) happens here
		if (gd.wasCanceled()) // dialog cancelled?
			return false;
		
		return true;
	}
	
	public static boolean selectImageDialogWithOptions(ImagePlus imp, String command, String image2name, DialogListener listener, String[] options) {
		String[] suitableImages = getSuitableImages(imp); // images that we can blend with the current one
		if (suitableImages == null) {
			String type = imp.getBitDepth() == 24 ? "RGB" : "grayscale";
			IJ.error(command + " Error",
					"No suitable image (" + type + ", " + imp.getWidth() + "x" + imp.getHeight() + ") to blend with");
			return false;
		}
		GenericDialog gd = new GenericDialog(command + "...");
		gd.addMessage("Image 1: " + imp.getTitle());
		gd.addChoice("Image 2:", suitableImages, image2name);
		gd.addChoice("Option:", options, options[0]);
		gd.addDialogListener(listener);
		gd.showDialog(); // user input (or reading from macro) happens here
		if (gd.wasCanceled()) // dialog cancelled?
			return false;
		
		return true;
	}
	
	public static ImagePlus findImageByTitle(String title) {
		int[] fullList = WindowManager.getIDList();// IDs of all open image windows
		
		for (int i = 0; i < fullList.length; i++) { // check images for suitability, make condensed list
			ImagePlus imp = WindowManager.getImage(fullList[i]);
			if (imp.getTitle().equals(title))
				return imp;
		}
		
		return null;
	}
	
	/**
	 * Get a list of open images with the same size and number of channels as the
	 * current ImagePlus (number of channels is 1 for grayscale, 3 for RGB). The
	 * current image is not entered in to the list.
	 * 
	 * @return A sorted list of the names of the images. Duplicate names are listed
	 *         only once.
	 */
	private static String[] getSuitableImages(ImagePlus imp) {
		int width = imp.getWidth(); // determine properties of the current image
		int height = imp.getHeight();
		int channels = imp.getProcessor().getNChannels();
		int thisID = imp.getID();
		int[] fullList = WindowManager.getIDList();// IDs of all open image windows
		Vector<String> suitables = new Vector<String>(fullList.length); // will hold names of suitable images
		for (int i = 0; i < fullList.length; i++) { // check images for suitability, make condensed list
			ImagePlus imp2 = WindowManager.getImage(fullList[i]);
			if (imp2.getWidth() == width && imp2.getHeight() == height && imp2.getProcessor().getNChannels() == channels
					&& fullList[i] != thisID) {
				String name = imp2.getTitle(); // found suitable image
				if (!suitables.contains(name)) // enter only if a new name
					suitables.addElement(name);
			}
		}
		if (suitables.size() == 0)
			return null; // nothing found
		String[] suitableImages = new String[suitables.size()];
		for (int i = 0; i < suitables.size(); i++) // vector to array conversion
			suitableImages[i] = (String) suitables.elementAt(i);
		StringSorter.sort(suitableImages);
		return suitableImages;
	}
}
