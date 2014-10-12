package model;

import javax.swing.DefaultBoundedRangeModel;

/**
 * 
 * The BoundedRangeModel used as the JSlider model for the time bar. This model extends DefaultBoundedRangeModel and also includes a boolean variable 
 * which indicates when it should be active in triggering events. </br>
 * 
 * {@link listener.MediaPlayerListener} </br>
 * {@link panel.PlaybackPanel}
 */

@SuppressWarnings("serial")
public class TimeBoundedRangeModel extends DefaultBoundedRangeModel {

	private boolean active = true;
	
	// Toggle active
	public void setActive(boolean value) {
		active = value;
	}
	
	public boolean getActive() {
		return active;
	}
	
	@Override
	protected void fireStateChanged() {
		super.fireStateChanged();
		
		// After every event. Turn active back to true.
		active = true;
	}

}
