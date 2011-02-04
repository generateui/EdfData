package jEDF.EDF;

import jEDF.Exceptions.EDFSignalsNotCorrespondingException;

/**
 *
 * <p>Titre : jEDF</p>
 * <p>Description : Java European Data Format Viewer and Analyser</p>
 * <p>Author : Nizar Kerkeni</p>
 * <p>Copyright : Copyright (c) 2003-2006</p>
 * <p>Version : 2.0</p>
 */

public class EDFBipolarSignal extends EDFSignal
{
    EDFSignal signal = null;

    /**
     *
     * @param signal1 	the first signal in this bipolar signal
     * @param signal2 	the second signal in this bipolar signal
     * @throws EDFSignalsNotCorrespondingException	when the 2 signals do not match (their sampling frequencies aren't the same)
     */
    public EDFBipolarSignal(EDFSignal signal1, EDFSignal signal2)
                    throws EDFSignalsNotCorrespondingException
    {
        super(signal1);

        signal = signal2;
        checkSignalsCorrespondance(signal1, signal2);
    }

    /**
     * read this signal data
     * @param startSecond the second to start the reading from (0..n-1)
     * @param nbSeconds the number of seconds to read (1..n)
     * @return short[] the read data
     */
    public short[] readSeconds(int startSecond, int nbSeconds)
    {
        short[] signal1 = super.readSeconds(startSecond, nbSeconds);
        short[] signal2 = signal.readSeconds(startSecond, nbSeconds);

        for (int i = 0; i < signal1.length; i++)
        {
            signal1[i] -= signal2[i];
        }

        return signal1;
    }

    /**
     * Check if two signals are corresponding.
     * @param signal1 the first signal to be checked
     * @param signal2 against the second signal
     * @throws EDFSignalsNotCorrespondingException if the two signals does not correspond
     */
    private void checkSignalsCorrespondance(EDFSignal signal1, EDFSignal signal2)
                    throws EDFSignalsNotCorrespondingException
    {
        if (signal1.getNbSamples() != signal2.getNbSamples()
                        || signal1.getDigMax() != signal2.getDigMax()
                        || signal1.getDigMin() != signal2.getDigMin()
                        || signal1.getPhysMax() != signal2.getPhysMax()
                        || signal1.getPhysMin() != signal2.getPhysMin())
        {
            throw new EDFSignalsNotCorrespondingException();
        }
    }

    /**
     * @return Returns the signal label.
     */
    public String getLabel()
    {
        return super.getLabel() + "-" + signal.getLabel();
    }

    /* (non-Javadoc)
     * @see EDFSignal#readSecondsAndAPoint(int, int)
     */
    public short[] readSecondsAndAPoint(int startSecond, int nbSeconds)
    {
        short[] signal1 = super.readSecondsAndAPoint(startSecond, nbSeconds);
        short[] signal2 = signal.readSecondsAndAPoint(startSecond, nbSeconds);

        for (int i = 0; i < signal1.length; i++)
        {
            signal1[i] -= signal2[i];
        }

        return signal1;
    }
}
