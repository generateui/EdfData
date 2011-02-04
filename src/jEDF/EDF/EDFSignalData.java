package jEDF.EDF;

import java.io.*;
import java.nio.*;

/**
 *
 * <p>Titre : jEDF</p>
 * <p>Description : Java European Data Format Viewer and Analyser</p>
 * <p>Author : Nizar Kerkeni</p>
 * <p>Copyright : Copyright (c) 2003-2006</p>
 * <p>Version : 2.0</p>
 */

public class EDFSignalData {
    private EDFSignal signal = null;
    private RandomAccessFile raf = null;
    private EDFFile edfFile = null;

    /**
     * @param signal the corresponding signal
     * @param edfFile the corresponding edf file
     */
    public EDFSignalData(EDFSignal signal, EDFFile edfFile) {
        this.signal = signal;
        this.raf = edfFile.getRandomAccessFile();
        this.edfFile = edfFile;
    }

    /**
     * @param startSecond the second to start reading from (0..n-1)
     * @param nbSeconds the number of seconds to read (0..n-1)
     * @return the read data + 1
     */
    public short[] readSecondsAndAPoint(int startSecond, int nbSeconds) {

        long recordDuration = edfFile.getRecordDuration();

        long numRecord = (long) Math.floor((double) startSecond /
                                           (double) recordDuration);

        long samplesPerRecord = signal.getNbSamples();

        long recordPosition = edfFile.getDataStartPositionInFile() + numRecord *
                              edfFile.getTotalNumberOfSamplesPerRecord() * 2 +
                              edfFile.getSignalDataOffsetInRecord(signal.getNumSignal());

        long startingSampleInFirstRecord = Math.round(
                ((double) (startSecond % recordDuration))
                * (double) signal.getNbSamples()
                / (double) recordDuration);

        long nbSamplesToRead = Math.round((double) nbSeconds *
                                          (double) samplesPerRecord
                                          / (double) recordDuration) + 1;

        long nbOfSamplesToReadInFirstRecord = nbSamplesToRead;

        if ((nbOfSamplesToReadInFirstRecord + startingSampleInFirstRecord >
             signal.getNbSamples())) {
            nbOfSamplesToReadInFirstRecord = samplesPerRecord -
                                             startingSampleInFirstRecord;
        }

        return read(numRecord, samplesPerRecord, recordPosition,
                    startingSampleInFirstRecord,
                    nbSamplesToRead, nbOfSamplesToReadInFirstRecord);
    }

    /**
     * @param startSecond the second to start reading from (0..n-1)
     * @param nbSeconds the number of seconds to read (0..n-1)
     * @return the read data
     */
    public short[] readSeconds(int startSecond, int nbSeconds) {

        long recordDuration = edfFile.getRecordDuration();

        long numRecord = (long) Math.floor((double) startSecond /
                                           (double) recordDuration);

        long samplesPerRecord = signal.getNbSamples();

        long recordPosition = edfFile.getDataStartPositionInFile() +
                              numRecord * edfFile.getTotalNumberOfSamplesPerRecord() * 2 +
                              edfFile.getSignalDataOffsetInRecord(signal.getNumSignal());

        long startingSampleInFirstRecord = Math.round(
                ((double) (startSecond % recordDuration))
                * (double) signal.getNbSamples()
                / (double) recordDuration);

        long nbSamplesToRead = Math.round((double) nbSeconds *
                                          (double) samplesPerRecord
                                          / (double) recordDuration);

        long nbOfSamplesToReadInFirstRecord = nbSamplesToRead;

        if ((nbOfSamplesToReadInFirstRecord + startingSampleInFirstRecord >
             signal.getNbSamples())) {
            nbOfSamplesToReadInFirstRecord = samplesPerRecord -
                                             startingSampleInFirstRecord;
        }

        return read(numRecord, samplesPerRecord, recordPosition,
                    startingSampleInFirstRecord,
                    nbSamplesToRead, nbOfSamplesToReadInFirstRecord);
    }

    /**
     * @param startEpoch the epoch to start reading from 0..n-1
     * @param endEpoch the end epoch 0..n-1
     * @return the read data
     */
    public short[] readEpochs(int startEpoch, int endEpoch) {
        return readSeconds(startEpoch * edfFile.getRecordDuration(),
                           (endEpoch - startEpoch) * edfFile.getRecordDuration());
    }

    private short[] read(long numRecord, long samplesPerRecord,
                         long recordPosition,
                         long startingSampleInFirstRecord, long nbSamplesToRead,
                         long nbOfSamplesToReadInFirstRecord) {

        ByteBuffer byteBuffer = ByteBuffer.allocate((int) nbSamplesToRead * 2);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        ShortBuffer shortBuffer = byteBuffer.asShortBuffer();

        short[] result = new short[(int) nbSamplesToRead];

        int nbSamplesToReadAtOnce = 0;

        nbSamplesToReadAtOnce = (int) nbOfSamplesToReadInFirstRecord;

        while (nbSamplesToRead > 0) {
            byte[] DataToRead = new byte[nbSamplesToReadAtOnce * 2];
            try {
                raf.seek(recordPosition + startingSampleInFirstRecord * 2);
                raf.readFully(DataToRead);
                byteBuffer.put(DataToRead);
            }
            catch (EOFException eof) {
                //eof.printStackTrace();// add / remove this line
                break;
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            nbSamplesToRead -= nbSamplesToReadAtOnce;

            numRecord++;

            recordPosition = edfFile.getDataStartPositionInFile() + numRecord *
                             edfFile.getTotalNumberOfSamplesPerRecord() * 2 +
                             edfFile.getSignalDataOffsetInRecord(signal.getNumSignal());

            startingSampleInFirstRecord = 0;

            if (((double) nbSamplesToRead / (double) signal.getNbSamples()) >= 1.0) {
                nbSamplesToReadAtOnce = (int) samplesPerRecord;
            }
            else {
                nbSamplesToReadAtOnce = (int) nbSamplesToRead;
            }
        }

        shortBuffer.get(result);

        return result;
    }
}
