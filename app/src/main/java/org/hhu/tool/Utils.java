package org.hhu.tool;
import android.util.Log;

import org.hhu.tool.GPSData;
public class Utils {
	/*
     * CRC check
     */
    
    public int crc16(byte[] data) {
    	int crc = 0, i, j = 0;
    	int count = data.length;
    	while (--count >= 0) {
    		crc = (crc ^ (((int)data[j]) << 8));
    		for (i = 0; i < 8; i ++) {
    			if ((crc & 0x8000) != 0) {
    				crc = ((crc << 1) ^ 0x1021);
    			}
    			else
    			{
    				crc = crc << 1;
    			}
    		}
    		j ++;
    	}
    	return (crc & 0xFFFF);
    }
    
    public GPSData protocol2value(byte[] array) {
    	GPSData gpsdata = new GPSData();
    	gpsdata.setLongitude(
    	        (double) ((((array[0] & 0xff)) +
				((array[1] & 0xff) << 8) +
				(((array[2] & 0xff) << 8) << 8) +
				((((array[3] & 0xff) << 8) << 8) << 8))
				/ Math.pow(10, 7))
		);
//        Log.i("GPSdata", Double.toString(gpsdata.longitude));
    	gpsdata.setLatitude(
    	        (double) ((((array[4] & 0xff)) +
				((array[5] & 0xff) << 8) +
				(((array[6] & 0xff) << 8) << 8) +
				((((array[7] & 0xff) << 8) << 8) << 8))
				/ Math.pow(10, 7))
        );
    	gpsdata.setHeading((double) ((((array[8] & 0xff)) +
				((array[9] & 0xff) << 8) +
				(((array[10] & 0xff) << 8) << 8) +
				((((array[11] & 0xff) << 8) << 8) << 8))
				/ Math.pow(10, 5)));
//		speed unit is km/h
    	gpsdata.setSpeed(
    	        (double) ((((array[12] & 0xff)) +
				((array[13] & 0xff) << 8) +
				(((array[14] & 0xff) << 8) << 8) +
				((((array[15] & 0xff) << 8) << 8) << 8))
				/ (3.6 * Math.pow(10, 7)))
        );
    	gpsdata.setOnline(
    	        array[16] & 0xff
        );
    	gpsdata.setVisible(
    	        array[17] & 0xff
        );
    	return gpsdata;
    }

}
