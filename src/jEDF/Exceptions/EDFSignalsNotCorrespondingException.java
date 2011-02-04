package jEDF.Exceptions;

/**
 *
 * <p>Titre : jEDF</p>
 * <p>Description : Java European Data Format Viewer and Analyser</p>
 * <p>Author : Nizar Kerkeni</p>
 * <p>Copyright : Copyright (c) 2003-2006</p>
 * <p>Version : 2.0</p>
 */

public class EDFSignalsNotCorrespondingException extends Exception {
    private int numSignal = 0;

    /**
     * @param numSignal The numSignal to set.
     */
    public void setNumSignal(int numSignal) {
        this.numSignal = numSignal;
    }

    /**
     * @return Returns the numSignal.
     */
    public int getNumSignal() {
        return numSignal;
    }
}
