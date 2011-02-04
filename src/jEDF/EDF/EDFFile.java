package jEDF.EDF;

import java.io.*;
import java.util.*;

/**
 *
 * <p>Titre : jEDF</p>
 * <p>Description : Java European Data Format Viewer and Analyser</p>
 * <p>Author : Nizar Kerkeni</p>
 * <p>Copyright : Copyright (c) 2003-2006</p>
 * <p>Version : 2.0</p>
 */

public class EDFFile {
    /*8 ascii : version of this data format (0)
     80 ascii : local patient identification
     80 ascii : local recording identification
     8 ascii : startdate of recording (dd.mm.yy)
     8 ascii : starttime of recording (hh.mm.ss)
     8 ascii : number of bytes in header record
     44 ascii : reserved
     8 ascii : number of data records (-1 if unknown)
     8 ascii : duration of a data record, in seconds
     4 ascii : number of signals (ns) in data record
     */

    private String version = null;
    private String patientID = null;
    private String recordID = null;
    private Calendar startTime = null;
    private long nbRecords = 0;
    private long recordDuration = 0;
    private int nbSignals = 0;


    private RandomAccessFile raf = null;
    private EDFSignal[] signals = null;
    private long totalNumberOfSamplesPerRecord = 0;
    private long[] signalDataOffsetInRecord = null;
    private File file = null;

    private byte[] unprocessedHeader = new byte[256];

    /**
     * @param file The file containing EDF data.
     * @throws IOException if the specified file cannot be accessed or read
     */
    public EDFFile(File file)
            throws IOException {
        this.file = file;
        raf = new RandomAccessFile(file, "r");
        EDFHeader edfHeader = new EDFHeader(raf);

        version = edfHeader.getVersion();
        patientID = edfHeader.getPatientID();
        recordID = edfHeader.getRecordID();
        startTime = edfHeader.getStartCalendar();
        nbRecords = edfHeader.getNbDataRecords();
        recordDuration = edfHeader.getDuration();
        nbSignals = edfHeader.getNbSignals();

        unprocessedHeader = edfHeader.toBytes();

        signals = new EDFSignal[nbSignals];

        signalDataOffsetInRecord = new long[nbSignals];
        signalDataOffsetInRecord[0] = 0;

        for (int i = 0; i < signals.length; i++) {
            signals[i] = new EDFSignal(this, i);
            totalNumberOfSamplesPerRecord += signals[i].getNbSamples();

            if (i > 0) {
                signalDataOffsetInRecord[i] = signalDataOffsetInRecord[i - 1] +
                                              signals[i - 1].getNbSamples() * 2;
            }
        }
    }

    /**
     * @return return the start position of data, i.e. the total
     * length of the headers
     */
    public long getDataStartPositionInFile() {
        return 256 * (nbSignals + 1);
    }

    /**
     * @param numSignal the number of the signal to get the offset from
     * @return the start position of this signal data relative to
     * the beginning of a record
     */
    public long getSignalDataOffsetInRecord(int numSignal) {
        return signalDataOffsetInRecord[numSignal];
    }


    /**
     * @return this file name
     */
    public String getFileName() {
        return file.getName();
    }


    /**
     * close this edf file
     */
    public void close() {
        try {
            if (raf != null) {
                raf.close();
                raf = null;
            }
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /**
     * @return an array containing the labes of each signal
     */
    public String[] getSignalsLabel() {
        String[] result = new String[signals.length];

        for (int i = 0; i < result.length; i++) {
            result[i] = signals[i].getLabel();
        }

        return result;
    }

    /**
     * @return the File corresponding to this EDFFile
     */
    public File getFile() {
        return new File(file.getAbsolutePath());
    }


    /**
     *
     * @return true if this EDFFile contains only hypnogram data
     */
    public boolean isItAHypnogramRecord() {
        boolean result = true;

        result = result && (nbSignals == 1);
        result = result && (signals[0].isEmbeddedHypnogram());

        return result;
    }

    /**
     * @return Returns the totalNumberOfSamplesPerRecord.
     */
    public int getTotalNumberOfSamplesPerRecord() {
        return (int) totalNumberOfSamplesPerRecord;
    }

    /**
     * @return Returns the nbRecords.
     */
    public int getNbRecords() {
        return (int) nbRecords;
    }

    /**
     * @return Returns the nbSignals.
     */
    public int getNbSignals() {
        return nbSignals;
    }

    /**
     * @return Returns the patientID.
     */
    public String getPatientID() {
        return patientID;
    }

    /**
     * @return Returns the recordDuration.
     */
    public int getRecordDuration() {
        return (int) recordDuration;
    }

    /**
     * @return Returns the recordID.
     */
    public String getRecordID() {
        return recordID;
    }

    /**
     * @return Returns the startTime.
     */
    public Calendar getStartTime() {
        return (Calendar) startTime.clone();
    }

    /**
     * @return Returns the version.
     */
    public String getVersion() {
        return version;
    }

    /**
     * @param numSignal the number of the signal to get
     * @return the wanted signal
     */
    public EDFSignal getSignal(int numSignal) {
        return signals[numSignal];
    }

    /**
     * @return Returns the unprocessedHeader.
     */
    public byte[] getUnprocessedHeader() {
        return unprocessedHeader;
    }

    /**
     *
     * @return Returns the total duration of this EDF file, in seconds
     */
    public int getTotalDuration() {
        return getNbRecords() * getRecordDuration();
    }

    /**
     * @return Returns the RandomAccessFile.
     */
    public RandomAccessFile getRandomAccessFile() {
        return raf;
    }
}
