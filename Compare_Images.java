import java.awt.AWTEvent;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.DialogListener;
import ij.gui.GenericDialog;
import ij.plugin.ImageCalculator;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

public class Compare_Images implements PlugInFilter, DialogListener {

	final String COMPOSITE = "Composite";
	final String[] OPTIONS = new String[] { "UNION", "INTERSECT", "MINUS", "DISJUNCTIVE UNION" };
	String imageBname;
	int selectedOptionIndex = 0;
	ImagePlus imageA;

	public void run(ImageProcessor ip) {
		if (!SelectImageDialogs.selectImageDialogWithOptions(imageA, "Compare Images", imageBname, this, OPTIONS))
			return;

		ImagePlus imageB = WindowManager.getImage(imageBname);

		ImageCalculator ic = new ImageCalculator();

		ImagePlus imageAorB = ic.run("OR create", imageA, imageB);
		imageAorB.setTitle("AorB");
		imageAorB.show();

		ImagePlus imageAxorA = ic.run("XOR create", imageA, imageA);
		imageAxorA.setTitle("AxorA");
		imageAxorA.show();

		ImagePlus imageAxorB = ic.run("XOR create", imageA, imageB);
		imageAxorB.setTitle("AxorB");
		imageAxorB.show();

		ImagePlus imageAandB = ic.run("AND create", imageA, imageB);
		imageAandB.setTitle("AandB");
		imageAandB.show();

		String[] channels;
		
		switch (selectedOptionIndex) {
		case 0:
			channels = new String[] { imageAxorA.getTitle(), imageAorB.getTitle(), imageAxorA.getTitle() };
			break;

		case 1:
			channels = new String[] { imageAxorB.getTitle(), imageAorB.getTitle(), imageAxorB.getTitle() };
			break;

		case 2:
			channels = new String[] { imageAandB.getTitle(), imageAorB.getTitle(), imageAandB.getTitle() };
			break;

		case 3:
			channels = new String[] { imageAandB.getTitle(), imageAorB.getTitle(), imageAandB.getTitle() };
			break;

		default:

			imageAxorA.close();
			imageAorB.close();
			imageAxorB.close();
			imageAandB.close();
			return;
		}

		IJ.run("Merge Channels...", String.format("c1=%s c2=%s c3=%s create keep", (Object[]) channels));

		ImagePlus impComp = SelectImageDialogs.findImageByTitle(COMPOSITE);

		if (impComp != null) {
			impComp.setTitle(imageA.getTitle() + " " + OPTIONS[selectedOptionIndex] + " " + imageB.getTitle());
		}

		imageAxorA.close();
		imageAorB.close();
		imageAxorB.close();
		imageAandB.close();
	}

	public int setup(String arg, ImagePlus imp) {
		imageA = imp;
		return DOES_8G;
	}

	@Override
	public boolean dialogItemChanged(GenericDialog gd, AWTEvent e) {
		imageBname = gd.getNextChoice();
		String option = gd.getNextChoice();
		for (int i = 0; i < OPTIONS.length; i++) {
			if (OPTIONS[i].equals(option)) {
				selectedOptionIndex = i;
				break;
			};
		}

		return !gd.invalidNumber(); // input is valid if all numeric input is ok
	}
}
