package component;

/**
 * Provides the logic for FullScreenMediaPlayer to control visibility of playbackPanel. </br>
 * 
 * <b>Note:</b> The methods next() and reset() are the inputs</br>
 * 
 * <b>Input:</b> NEXT, RESET<br/>
 * <b>Initial:</b> ZERO<br/>
 * <b>States:</b> ZERO, ONE, TWO, THREE<br/>
 * <b>Accepting:</b> THREE<br/>
 * <b>Transition Function</b><br/>
 * T(ZERO, NEXT) = ONE			T(ZERO, RESET) = ZERO<br/>
 * T(ONE, NEXT) = TWO			T(ONE, RESET) = ZERO<br/>
 * T(TWO, NEXT) = THREE			T(TWO, RESET) = ZERO<br/>
 * T(THREE, NEXT) = THREE		T(THREE, RESET) = ZERO<br/><br/>
 * 
 * {@link frame.FullScreenMediaPlayer} <br/>
 * {@link panel.PlaybackPanel}
 * @author changkon
 *
 */

public enum MediaCountFSM {
	ZERO, ONE, TWO, THREE;
	
	public MediaCountFSM next() {

		switch (this) {
			case ZERO:
				return ONE;
			case ONE:
				return TWO;
			case TWO:
				return THREE;
			default:
				// T(THREE, NEXT) = THREE
				return THREE;
		}
	}
	
	public MediaCountFSM reset() {
		return ZERO;
	}
}
