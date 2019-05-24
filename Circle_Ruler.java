import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Font;
import java.math.BigDecimal;

import ij.ImagePlus;
import ij.gui.DialogListener;
import ij.gui.GenericDialog;
import ij.gui.OvalRoi;
import ij.gui.Overlay;
import ij.gui.TextRoi;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

public class Circle_Ruler implements PlugInFilter, DialogListener {

	double size;
	int highlight;
	ImagePlus image;

	@Override
	public void run(ImageProcessor ip) {
		if (!showDialog())
			return;

		int width = image.getWidth();
		int height = image.getHeight();

		Overlay overlay = new Overlay();

		double maxDistance = Math.hypot(width, height) * 2;
		double curDistance = size;
		int count = 1;

		while (curDistance < maxDistance) {
			OvalRoi oval = new OvalRoi(-curDistance, height - curDistance, curDistance * 2, curDistance * 2);
			oval.setStrokeColor(count % highlight == 0 ? Color.GREEN : new Color(0, 200, 0));
			overlay.add(oval);

			if (count % highlight == 0) {
				Font font = new Font("Arial", Font.PLAIN, 12);

				String label = new BigDecimal(curDistance).stripTrailingZeros().toPlainString();

				TextRoi tr1 = new TextRoi(0, height - curDistance + 2, label, font);
				tr1.setStrokeColor(Color.GREEN);
				overlay.add(tr1);

				TextRoi tr2 = new TextRoi(curDistance - label.length() * 8, height - 12, label, font);
				tr2.setStrokeColor(Color.GREEN);
				overlay.add(tr2);
			}

			curDistance += size;
			count += 1;
		}

		image.setOverlay(overlay);
	}

	@Override
	public int setup(String arg0, ImagePlus image) {
		this.image = image;
		return DOES_ALL;
	}

	private boolean showDialog() {
		GenericDialog gd = new GenericDialog("Overlayer");

		gd.addNumericField("Distance:", 50, 0);
		gd.addNumericField("Highlight:", 5, 0);
		gd.addDialogListener(this);
		gd.showDialog();

		if (gd.wasCanceled())
			return false;

		return true;
	}

	@Override
	public boolean dialogItemChanged(GenericDialog gd, AWTEvent e) {
		size = gd.getNextNumber();
		highlight = (int) gd.getNextNumber();

		return !gd.invalidNumber();
	}

}
