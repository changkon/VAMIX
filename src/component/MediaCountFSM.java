package component;

/**
 * Provides the logic for FullScreenMediaPlayer to control visibility of playbackPanel. </br>
 * 
 * <b>Note:</b> Although NEXT is not a state, assume calling the method next(MediaCountFSM state) is the input NEXT.</br>
 * 
 * <b>Input:</b> NEXT, RESET</br>
 * <b>Initial:</b> ZERO</br>
 * <b>States:</b> ZERO, ONE, TWO, THREE, NEXT, RESET</br>
 * <b>Accepting:</b> THREE</br>
 * <b>Transition Function</b></br>
 * T(ZERO, NEXT) = ONE			T(ZERO, RESET) = ZERO</br>
 * T(ONE, NEXT) = TWO			T(ONE, RESET) = ZERO</br>
 * T(TWO, NEXT) = THREE			T(TWO, RESET) = ZERO</br>
 * T(THREE, NEXT) = THREE		T(THREE, RESET) = ZERO</br></br>
 * 
 * {@link frame.FullScreenMediaPlayer} </br>
 * {@link panel.PlaybackPanel}
 * @author changkon
 *
 */

public enum MediaCountFSM {
	ZERO, ONE, TWO, THREE, RESET;
	
	public static MediaCountFSM next(MediaCountFSM state) {
		switch (state) {
			case ZERO:
				return ONE;
			case ONE:
				return TWO;
			case TWO:
				return THREE;
			case THREE:
				return THREE;
			default:
				// input is reset.
				return ZERO;
		}
	}
}
