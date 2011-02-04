package jEDF.EDF;

import jEDF.FFT.FastFourierTransform;

import java.io.IOException;

/**
 *
 * <p>Titre : jEDF</p>
 * <p>Description : Java European Data Format Viewer and Analyser</p>
 * <p>Author : Nizar Kerkeni</p>
 * <p>Copyright : Copyright (c) 2003-2006</p>
 * <p>Version 2.0</p>
 */

public class EDFSignal
{

    private String label = null;

    private String transducerType = null;

    private String physDim = null;

    private double physMin = 0.0;

    private double physMax = 0.0;

    private long digMin = 0;

    private long digMax = 0;

    private String prefiltering = null;

    private long nbSamples = 0;

    private double samplingRate = 0;

    private int numSignal = -1;

    private static final int maxHz = 33;

    private EDFSignalData signalData = null;

    private EDFFile edfFile = null;

    private String sep = "\t"; //seperator \t by lines \n by column

    /**
     * @param edfFile
     *            the EDF file to extract the signal from
     * @param numSignal
     *            the signal number
     * @throws IOException
     *             if the EDF file cannot be accessed
     */
    public EDFSignal(EDFFile edfFile, int numSignal) throws IOException
    {
        this.numSignal = numSignal;
        this.edfFile = edfFile;

        signalData = new EDFSignalData(this, edfFile);

        EDFSignalHeader signalHeader = new EDFSignalHeader(edfFile
                        .getRandomAccessFile(), numSignal, edfFile
                        .getNbSignals());

        label = signalHeader.getLabel();
        transducerType = signalHeader.getTransducerType();
        physDim = signalHeader.getPhysicalDimension();
        physMin = signalHeader.getPhysicalMin();
        physMax = signalHeader.getPhysicalMax();
        digMin = signalHeader.getDigitalMin();
        digMax = signalHeader.getDigitalMax();
        prefiltering = signalHeader.getPrefiltering();
        nbSamples = signalHeader.getNbSamples();
    }

    /**
     * @param signal
     *            the signal to create a copy from
     */
    public EDFSignal(EDFSignal signal)
    {
        label = signal.label;
        transducerType = signal.transducerType;
        physDim = signal.physDim;
        physMin = signal.physMin;
        physMax = signal.physMax;
        digMin = signal.digMin;
        digMax = signal.digMax;
        prefiltering = signal.prefiltering;
        nbSamples = signal.nbSamples;
        numSignal = signal.numSignal;
        signalData = signal.signalData;
        edfFile = signal.edfFile;
    }

    /**
     * @return Returns the digital Max.
     */
    public int getDigMax()
    {
        return (int) digMax;
    }

    /**
     * @return Returns the digital Min.
     */
    public int getDigMin()
    {
        return (int) digMin;
    }

    /**
     * @return Returns the label.
     */
    public String getLabel()
    {
        return label;
    }

    /**
     * @return Returns the number of samples per record.
     */
    public int getNbSamples()
    {
        return (int) nbSamples;
    }

    /**
     * @return Returns the signal number.
     */
    public int getNumSignal()
    {
        return numSignal;
    }

    /**
     * @return Returns the physical Dimension.
     */
    public String getPhysDim()
    {
        return physDim;
    }

    /**
     * @return Returns the physical Max.
     */
    public double getPhysMax()
    {
        return physMax;
    }

    /**
     * @return Returns the physical Min.
     */
    public double getPhysMin()
    {
        return physMin;
    }

    /**
     * @return Returns the prefiltering.
     */
    public String getPrefiltering()
    {
        return prefiltering;
    }

    /**
     * @return Returns the signalData.
     */
    public EDFSignalData getSignalData()
    {
        return signalData;
    }

    /**
     * @return Returns the transducer Type.
     */
    public String getTransducerType()
    {
        return transducerType;
    }

    /**
     * @param startSecond
     *            the second to start reading from
     * @param nbSeconds
     *            the number of seconds to read
     * @return the read data
     */
    public short[] readSeconds(int startSecond, int nbSeconds)
    {
        return signalData.readSeconds(startSecond, nbSeconds);
    }

    /**
     * this fonction return the data corresponding to the given number of
     * seconds plus a point, useful if the data are to be displayed like a graph
     *
     * @param startSecond
     *            the second to start reading from
     * @param nbSeconds
     *            the number of seconds to read
     * @return the read data
     */
    public short[] readSecondsAndAPoint(int startSecond, int nbSeconds)
    {
        return signalData.readSecondsAndAPoint(startSecond, nbSeconds);
    }

    /**
     * @return true if this signal appears to contain hypnogram data
     */
    public boolean isEmbeddedHypnogram()
    {
        boolean result = true;

        result = result && getPhysMin() == 0.0;
        result = result && getPhysMax() == 9.0;
        result = result && getDigMin() == 0;
        result = result && getDigMax() == 9;
        result = result && getNbSamples() == 1;

        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return getLabel();
    }

    private double[] getFourierTransformedData(int startSecond, int nbSeconds)
    {
        short[] data = readSeconds(startSecond, nbSeconds);
        return FastFourierTransform.processDataRealFFT(data);
    }

    private double[] buildFrequencyAxis(double[] fftData)
    {
        int dataLength = fftData.length;
        double samplingRate = getSamplingRate();
        double nbRawSamples = (double) dataLength * 2.0;
        double[] frequencyAxis = new double[dataLength];

        for (int i = 0; i < dataLength; i++)
        {
            frequencyAxis[i] = (double) i * samplingRate / nbRawSamples;
        }

        return frequencyAxis;
    }

    private double[] postProcess1Hz(double[] data, double[] frequencyAxis)
    {
        double[] result = new double[maxHz];

        int j;

        for (j = 0; frequencyAxis[j] <= 0.5; j++)
        {
            ; // Elimination of the frequencies < 0.5
        }
        for (int i = 0; i < result.length; i++)
        {
            for (; (j < frequencyAxis.length)
                            && ((int) frequencyAxis[j]) < (i + 1); j++)
            {
                result[i] += data[j];
            }
        }
        return result;
    }

    private double[] postProcessBySleepBands(double[] data,
                    double[] frequencyAxis)
    {
        // delta theta alpha sigma beta
        int[] sleepBands =
        { 4, 8, 12, 16, 33 };

        double[] result = new double[sleepBands.length];

        double[] tempData = postProcess1Hz(data, frequencyAxis);

        int j = 0;

        for (int i = 0; i < sleepBands.length; i++)
        {

            for (; j < (sleepBands[i]); j++)
            {
                result[i] += tempData[j];
            }
        }
        return result;
    }

    public double[] getFourierTransformedDataBy1Hz(int startSecond,
                    int nbSeconds, double startPosition, double endPosition)
    {
        short[] selectedData = getSelectedSignalData(startSecond, nbSeconds,
                        startPosition, endPosition);

        if (selectedData.length == 0)
        {
            return new double[0];
        }

        double[] fftData = FastFourierTransform
                        .processDataRealFFT(selectedData);

        return postProcess1Hz(fftData, buildFrequencyAxis(fftData));
    }

    public short[] getSelectedSignalData(int startSecond, int nbSeconds,
                    double startPosition, double endPosition)
    {
        short[] data = readSeconds(startSecond, nbSeconds);

        startPosition *= data.length;
        endPosition *= data.length;
        int selectedDataLength = (int) endPosition - (int) startPosition;

        short[] selectedData = new short[selectedDataLength];

        for (int i = 0; i < selectedDataLength; i++)
        {
            selectedData[i] = data[i + (int) startPosition];
        }

        return selectedData;
    }

    public double[] getFourierTransformedDataBySleepBands(int startSecond,
                    int nbSeconds, double startPosition, double endPosition)
    {
        short[] selectedData = getSelectedSignalData(startSecond, nbSeconds,
                        startPosition, endPosition);

        if (selectedData.length == 0)
        {
            return new double[0];
        }

        double[] fftData = FastFourierTransform
                        .processDataRealFFT(selectedData);

        return postProcessBySleepBands(fftData, buildFrequencyAxis(fftData));
    }

    /**
     * @return Returns the edfFile.
     */
    public EDFFile getEdfFile()
    {
        return edfFile;
    }

    public double getSamplingRate()
    {
        samplingRate = (double) getNbSamples()
                        / (double) edfFile.getRecordDuration();
        return samplingRate;
    }
}
