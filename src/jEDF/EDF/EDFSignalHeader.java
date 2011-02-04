package jEDF.EDF;

import java.io.*;

/**
 *
 * <p>Titre : jEDF</p>
 * <p>Description : Java European Data Format Viewer and Analyser</p>
 * <p>Author : Nizar Kerkeni</p>
 * <p>Copyright : Copyright (c) 2003-2006</p>
 * <p>Version : 2.0</p>
 */

public class EDFSignalHeader {

    /*
     * ns * 16 ascii : ns * label (e.g. EEG FpzCz or Body temp)
     ns * 80 ascii : ns * transducer type (e.g. AgAgCl electrode)
     ns * 8 ascii : ns * physical dimension (e.g. uV or degreeC)
     ns * 8 ascii : ns * physical minimum (e.g. -500 or 34)
     ns * 8 ascii : ns * physical maximum (e.g. 500 or 40)
     ns * 8 ascii : ns * digital minimum (e.g. -2048)
     ns * 8 ascii : ns * digital maximum (e.g. 2047)
     ns * 80 ascii : ns * prefiltering (e.g. HP:0.1Hz LP:75Hz)
     ns * 8 ascii : ns * nr of samples in each data record
     ns * 32 ascii : ns * reserved
     */

    private byte[] label = new byte[16];
    private byte[] transducerType = new byte[80];
    private byte[] physDim = new byte[8];
    private byte[] physMin = new byte[8];
    private byte[] physMax = new byte[8];
    private byte[] digMin = new byte[8];
    private byte[] digMax = new byte[8];
    private byte[] prefiltering = new byte[80];
    private byte[] nbSamples = new byte[8];


    /**
     * @param raf the RandomAccessFile to read from
     * @param numSignal the number of the signal
     * @param nbSignals the number of all signals in the EDF file
     * @throws IOException if the file cannot be accessed
     */
    public EDFSignalHeader(RandomAccessFile raf, int numSignal, long nbSignals)
            throws
            IOException {
        // our position in the file
        long pos = 256; // we jump over the global header

        // we search for our signal position
        raf.seek(pos + numSignal * 16);
        raf.readFully(label); // then we read it
        pos += nbSignals * 16; // finally we jump to the next information

        raf.seek(pos + numSignal * 80);
        raf.readFully(transducerType);
        pos += nbSignals * 80;

        raf.seek(pos + numSignal * 8);
        raf.readFully(physDim);
        pos += nbSignals * 8;

        raf.seek(pos + numSignal * 8);
        raf.readFully(physMin);
        pos += nbSignals * 8;

        raf.seek(pos + numSignal * 8);
        raf.readFully(physMax);
        pos += nbSignals * 8;

        raf.seek(pos + numSignal * 8);
        raf.readFully(digMin);
        pos += nbSignals * 8;

        raf.seek(pos + numSignal * 8);
        raf.readFully(digMax);
        pos += nbSignals * 8;

        raf.seek(pos + numSignal * 80);
        raf.readFully(prefiltering);
        pos += nbSignals * 80;

        raf.seek(pos + numSignal * 8);
        raf.readFully(nbSamples);
        pos += nbSignals * 8;
    }

    /**
     * @return the signal label
     */
    public String getLabel() {
        return new String(label).trim();
    }

    /**
     * @return the transducer type
     */
    public String getTransducerType() {
        return new String(transducerType).trim();
    }

    /**
     * @return the physical dimension
     */
    public String getPhysicalDimension() {
        return new String(physDim).trim();
    }

    /**
     * @return the physical min
     */
    public double getPhysicalMin() {
        return new Double(new String(physMin).trim()).doubleValue();
    }

    /**
     * @return the physical max
     */
    public double getPhysicalMax() {
        return new Double(new String(physMax).trim()).doubleValue();
    }

    /**
     * @return the digital min
     */
    public long getDigitalMin() {
        return new Long(new String(digMin).trim()).longValue();
    }

    /**
     * @return the digital max
     */
    public long getDigitalMax() {
        return new Long(new String(digMax).trim()).longValue();
    }

    /**
     * @return the prefiltering
     */
    public String getPrefiltering() {
        return new String(prefiltering).trim();
    }

    /**
     * @return the number of samples per record
     */
    public long getNbSamples() {
        return new Long(new String(nbSamples).trim()).longValue();
    }
}
