import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Font;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.DialogListener;
import ij.gui.GenericDialog;
import ij.gui.Overlay;
import ij.gui.PointRoi;
import ij.gui.TextRoi;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

public class Show_Overlay_Plugin implements PlugInFilter, DialogListener {

	String coordinates;
	
	@Override
	public void run(ImageProcessor ip) {
		if(!showDialog())
			return;
		
		IJ.log(coordinates);
		
		String[] pairs = coordinates.split(";");
		
		int[] coords = new int[pairs.length*2];
		
		for (int i = 0; i < pairs.length; i++) {
			String[] nums = pairs[i].split(","); 
			
			coords[i*2] = Integer.parseInt(nums[0]); 
			coords[i*2+1] = Integer.parseInt(nums[1]);
		}
		
		Overlay o = new Overlay();

		
		for (int i = 0; i < coords.length/2; i++) {
			Font font = new Font("Arial", Font.PLAIN, 18);
			TextRoi tr = new TextRoi(coords[i*2], coords[i*2+1], ""+ (i+1), font);
			tr.setStrokeColor(Color.RED);
			
			PointRoi pr = new PointRoi(coords[i*2], coords[i*2+1]);
			pr.setStrokeColor(Color.RED);
			
			o.add(pr);			
			o.add(tr);
		}
		
		
		
		ip.drawOverlay(o);
	}

	@Override
	public int setup(String arg0, ImagePlus arg1) {
		// TODO Auto-generated method stub
		return DOES_ALL;
	}
	
	private boolean showDialog() {
		GenericDialog gd = new GenericDialog("Overlayer");
		
		gd.addStringField("Coordinates: ", "", 100);
		gd.addDialogListener(this);
		gd.showDialog();
		
		if (gd.wasCanceled())
			return false;
		
		return true;
	}

	@Override
	public boolean dialogItemChanged(GenericDialog gd, AWTEvent e) {
		coordinates = gd.getNextString();
		
		return !gd.invalidNumber();
	}
	
}
