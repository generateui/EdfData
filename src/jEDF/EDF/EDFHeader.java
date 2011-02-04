package jEDF.EDF;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Calendar;

/**
 *
 * <p>Titre : jEDF</p>
 * <p>Description : Java European Data Format Viewer and Analyser</p>
 * <p>Author : Nizar Kerkeni</p>
 * <p>Copyright : Copyright (c) 2003-2006</p>
 * <p>Version : 2.0</p>
 */

public class EDFHeader
{
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

    private byte[] version = new byte[8];

    private byte[] patientID = new byte[80];

    private byte[] recordID = new byte[80];

    private byte[] startDate = new byte[8];

    private byte[] startTime = new byte[8];

    private byte[] nbBytesHeader = new byte[8];
    //	44 ascii : reserved
    private byte[] nbDataRecords = new byte[8];

    private byte[] duration = new byte[8];

    private byte[] nbSignals = new byte[4];

    private byte[] unprocessedHeader = new byte[256];

    /**
     *
     * @param raf the RandomAccessFile to read the header from
     * @throws IOException if the header cannot be read
     */
    public EDFHeader(RandomAccessFile raf) throws IOException
    {
        if (raf == null)
        {
            throw new NullPointerException();
        }

        raf.seek(0);
        raf.readFully(version);
        raf.readFully(patientID);

        raf.readFully(recordID);
        raf.readFully(startDate);
        raf.readFully(startTime);
        raf.readFully(nbBytesHeader);

        raf.skipBytes(44);

        raf.readFully(nbDataRecords);
        raf.readFully(duration);
        raf.readFully(nbSignals);

        raf.seek(0);
        raf.readFully(unprocessedHeader);
    }

    /**
     *
     * @return the version of this EDF file
     */
    public String getVersion()
    {
        return new String(version).trim();
    }

    /**
     *
     * @return the patient identification
     */
    public String getPatientID()
    {
        return new String(patientID).trim();
    }

    /**
     *
     * @return the record identification
     */
    public String getRecordID()
    {
        return new String(recordID).trim();
    }

    /**
     *
     * @return the start date of this record
     */
    public String getStartDate()
    {
        return new String(startDate);
    }

    /**
     *
     * @return the start time of this record
     */
    public String getStartTime()
    {
        return new String(startTime);
    }

    /**
     *
     * @return the numbers of data records (corresponding to epochs)
     */
    public long getNbDataRecords()
    {
        return new Long(new String(nbDataRecords).trim()).longValue();
    }

    /**
     *
     * @return the duration, in seconds, of a data record
     */
    public long getDuration()
    {
        return new Long(new String(duration).trim()).longValue();
    }

    /**
     *
     * @return the number of signals
     */
    public int getNbSignals()
    {
        return new Integer(new String(nbSignals).trim()).intValue();
    }

    /**
     *
     * @return the start Calendar
     */
    public Calendar getStartCalendar()
    {
        byte[] temp = new byte[2];

        temp[0] = startDate[6];
        temp[1] = startDate[7];
        int year = new Integer(new String(temp).trim()).intValue();
        year += (year >= 85 && year <= 99) ? 1900 : 2000;

        temp[0] = startDate[3];
        temp[1] = startDate[4];
        int month = new Integer(new String(temp).trim()).intValue();

        temp[0] = startDate[0];
        temp[1] = startDate[1];
        int day = new Integer(new String(temp).trim()).intValue();

        temp[0] = startTime[0];
        temp[1] = startTime[1];
        int hour = new Integer(new String(temp).trim()).intValue();

        temp[0] = startTime[3];
        temp[1] = startTime[4];
        int minute = new Integer(new String(temp).trim()).intValue();

        temp[0] = startTime[6];
        temp[1] = startTime[7];
        int second = new Integer(new String(temp).trim()).intValue();

        Calendar result = Calendar.getInstance();
        result.set(year, month - 1, day, hour, minute, second);

        return result;
    }

    /**
     *
     * @return the header in raw bytes format, as read directly from the EDF file
     */
    public byte[] toBytes()
    {
        return unprocessedHeader;
    }
}
