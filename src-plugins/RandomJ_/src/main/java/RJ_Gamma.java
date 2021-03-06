import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.PlugIn;
import imagescience.image.FloatImage;
import imagescience.image.Image;
import imagescience.random.Randomizer;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Panel;
import java.awt.Point;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class RJ_Gamma implements PlugIn, WindowListener {

	private static String order = "1";

	private static final String[] inserts = { "additive", "multiplicative" };
	private static int insert = 0;

	private static Point pos = new Point(-1,-1);

	public void run(String arg) {

		if (!RJ.libcheck()) return;
		final ImagePlus imp = RJ.imageplus();
		if (imp == null) return;

		RJ.log(RJ.name()+" "+RJ.version()+": Gamma");

		GenericDialog gd = new GenericDialog(RJ.name()+": Gamma");
		gd.addStringField("Order:",order);
		gd.addPanel(new Panel(),GridBagConstraints.EAST,new Insets(0,0,0,0));
		gd.addChoice("Insertion:",inserts,inserts[insert]);

		if (pos.x >= 0 && pos.y >= 0) {
			gd.centerDialog(false);
			gd.setLocation(pos);
		} else gd.centerDialog(true);
		gd.addWindowListener(this);
		gd.showDialog();

		if (gd.wasCanceled()) return;

		order = gd.getNextString();
		insert = gd.getNextChoiceIndex();

		(new RJGamma()).run(imp,order,insert);
	}

	public void windowActivated(final WindowEvent e) { }

	public void windowClosed(final WindowEvent e) {

		pos.x = e.getWindow().getX();
		pos.y = e.getWindow().getY();
	}

	public void windowClosing(final WindowEvent e) { }

	public void windowDeactivated(final WindowEvent e) { }

	public void windowDeiconified(final WindowEvent e) { }

	public void windowIconified(final WindowEvent e) { }

	public void windowOpened(final WindowEvent e) { }

}

class RJGamma {

	void run(final ImagePlus imp, final String order, final int insert) {

		try {
			int orderval;
			try { orderval = Integer.parseInt(order); }
			catch (Exception e) { throw new IllegalArgumentException("Invalid order value"); }
			int insval = Randomizer.ADDITIVE;
			switch (insert) {
				case 0: insval = Randomizer.ADDITIVE; break;
				case 1: insval = Randomizer.MULTIPLICATIVE; break;
			}

			final Image img = Image.wrap(imp);
			final Randomizer ran = new Randomizer();
			ran.messenger.log(RJ_Options.log);
			ran.messenger.status(RJ_Options.pgs);
			ran.progressor.display(RJ_Options.pgs);
			final Image newimg = RJ_Options.floatout ?
				ran.gamma(new FloatImage(img),orderval,insval,false) :
				ran.gamma(img,orderval,insval,true);
			RJ.show(newimg,imp);

		} catch (OutOfMemoryError e) {
			RJ.error("Not enough memory for this operation");

		} catch (IllegalArgumentException e) {
			RJ.error(e.getMessage());

		} catch (Throwable e) {
			RJ.error("An unidentified error occurred while running the plugin");

		}
	}

}
