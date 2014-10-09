package res;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import component.Playback;

/**
 * Manages the icons used in the media player.
 * 
 * @see http://www.flaticon.com/packs/computer-and-media-1
 *
 */

public class MediaIcon {
	private static final int pixel = 20;
	private static String path = System.getProperty("user.dir");
	
	private static ImageIcon play = new ImageIcon(path + "/res/play.png");
	private static ImageIcon pause = new ImageIcon(path + "/res/pause.png");
	private static ImageIcon stop = new ImageIcon(path + "/res/stop.png");
	private static ImageIcon fastforward = new ImageIcon(path + "/res/fastforward.png");
	private static ImageIcon rewind = new ImageIcon(path + "/res/rewind.png");
	private static ImageIcon mute = new ImageIcon(path + "/res/mute.png");
	private static ImageIcon unmute = new ImageIcon(path + "/res/unmute.png");
	private static ImageIcon maxVolume = new ImageIcon(path + "/res/maxvolume.png");
	private static ImageIcon open = new ImageIcon(path + "/res/open.png");
	private static ImageIcon download = new ImageIcon(path + "/res/download.png");
	private static ImageIcon fullscreen = new ImageIcon(path + "/res/fullscreen.png");
	
	public static Icon getIcon(Playback option) {
		ImageIcon icon = null;
		
		switch(option) {
			case PLAY:
				icon = play;
				break;
			case PAUSE:
				icon = pause;
				break;
			case STOP:
				icon = stop;
				break;
			case FASTFORWARD:
				icon = fastforward;
				break;
			case REWIND:
				icon = rewind;
				break;
			case MUTE:
				icon = mute;
				break;
			case UNMUTE:
				icon = unmute;
				break;
			case MAXVOLUME:
				icon = maxVolume;
				break;
			case OPEN:
				icon = open;
				break;
			case DOWNLOAD:
				icon = download;
				break;
			case FULLSCREEN:
				icon = fullscreen;
				break;
		}
		
		BufferedImage bi = resizeImage(icon);
		icon.setImage(bi);
		return icon;
	}
	
	/**
	 * Resizes a given image to desired size
	 * 
	 * @param icon
	 * @return BufferedImage
	 * @see http://www.javalobby.org/articles/ultimate-image/#11
	 */
	private static BufferedImage resizeImage(ImageIcon icon) {
		BufferedImage bi = new BufferedImage(pixel, pixel, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = bi.createGraphics();
		
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	    g.drawImage(icon.getImage(), 0, 0, pixel, pixel, null);
	    g.dispose();
	    
		return bi;
	}
}
