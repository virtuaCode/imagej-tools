import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Font;
import java.math.BigDecimal;

import ij.ImagePlus;
import ij.gui.DialogListener;
import ij.gui.GenericDialog;
import ij.gui.Line;
import ij.gui.Overlay;
import ij.gui.TextRoi;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

public class Grid_Ruler implements PlugInFilter, DialogListener {

	double size;
	int highlight;
	ImagePlus image;

	
	@Override
	public void run(ImageProcessor ip) {
		if(!showDialog())
			return;
		
		int width = image.getWidth();
		int height = image.getHeight();
		
		Overlay overlay = new Overlay();
		
		float x = 0;
		int xc = 0;
		while (x < width) {
			Line line = new Line(x, 0, x, height);
			line.setStrokeColor(xc % highlight == 0 ? Color.YELLOW : new Color(200, 200, 0));
			overlay.add(line);
			
			if (xc % highlight == 0) {
				Font font = new Font("Arial", Font.PLAIN, 12);
				
				String label = new BigDecimal(x).stripTrailingZeros().toPlainString();
				
				TextRoi tr1 = new TextRoi(x - label.length() * 8, 1 , label, font);
				tr1.setStrokeColor(Color.YELLOW);
				overlay.add(tr1);
			}
			
			x += size;
			xc += 1;
		}
		
		float y = 0;
		int yc = 0; 
		while (y < height) {
			Line line = new Line(0, y, width, y);
			line.setStrokeColor(yc % highlight == 0 ? Color.YELLOW : new Color(200, 200, 0));
			overlay.add(line);
			
			if (yc % highlight == 0) {
				Font font = new Font("Arial", Font.PLAIN, 12);
				
				String label = new BigDecimal(y).stripTrailingZeros().toPlainString();
				
				TextRoi tr1 = new TextRoi(1, y - 12, label, font);
				tr1.setStrokeColor(Color.YELLOW);
				overlay.add(tr1);
			}
			y += size;
			yc += 1;
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
		
		gd.addNumericField("Width:", 10, 0);
		gd.addNumericField("Highlight:", 10, 0);
		gd.addDialogListener(this);
		gd.showDialog();
		
		if (gd.wasCanceled())
			return false;
		
		return true;
	}

	@Override
	public boolean dialogItemChanged(GenericDialog gd, AWTEvent e) {
		size =  gd.getNextNumber();
		highlight = (int) gd.getNextNumber();
		
		return !gd.invalidNumber();
	}
	
}
