package org.hhu.constant;

import android.util.Log;

public class Constant {
	public static final String PREF_KEY_ROUTER_URL = "pref_key_router_url";
	public static final String PREF_KEY_CAMERA_URL = "pref_key_camera_url";
	
	public static final String PREF_KEY_TEST_MODE_ENABLED = "pref_key_test_enabled";
	public static final String PREF_KEY_ROUTER_URL_TEST = "pref_key_router_url_test";
	public static final String PREF_KEY_CAMERA_URL_TEST = "pref_key_camera_url_test";
	
	public static final String PREF_KEY_LEN_ON = "pref_key_len_on";
	public static final String PREF_KEY_LEN_OFF = "pref_key_len_off";
	
	public static final String DEFAULT_VALUE_CAMERA_URL = "http://192.168.43.150:8080/?action=stream";
	public static final String DEFAULT_VALUE_ROUTER_URL = "192.168.43.150:2001";
	public static final String DEFAULT_VALUE_CAMERA_URL_TEST = "";
	public static final String DEFAULT_VALUE_ROUTER_URL_TEST = "192.168.43.150:2001";
	
	public static final String DEFAULT_VALUE_LEN_ON = "FF040100FF";
	public static final String DEFAULT_VALUE_LEN_OFF = "FF040000FF";
	
	
	public static final int COMMAND_LENGTH = 5;
	public static final int COMMAND_RADIOX = 16;
	public static final int MIN_COMMAND_REC_INTERVAL = 1000;//ms
	
    public static final String ACTION_TAKE_PICTURE_DONE = "hanry.take_picture_done";
    public static final String EXTRA_RES = "res";
    public static final String EXTRA_PATH = "path";
   
    public final static int CAM_RES_OK = 6;
    public final static int CAM_RES_FAIL_FILE_WRITE_ERROR = 7;
    public final static int CAM_RES_FAIL_FILE_NAME_ERROR = 8;
    public final static int CAM_RES_FAIL_NO_SPACE_LEFT = 9;
    public final static int CAM_RES_FAIL_BITMAP_ERROR = 10;
    public final static int CAM_RES_FAIL_UNKNOW = 20;
    
	public static class CommandArray {
		
		public byte mCmd1 = 0;
		public byte mCmd2 = 0;
		public byte mCmd3 = 0;
		public CommandArray (int cmd1, int cmd2, int cmd3) {
			mCmd1 = (byte)cmd1;
			mCmd2 = (byte)cmd2;
			mCmd3 = (byte)cmd3;
		}
		
		public CommandArray (String cmdLine) {
	        int icmd1 = -1;
			int icmd2 = -1;
			int icmd3 = -1;
			
			if (cmdLine != null 
	    			&& (cmdLine.startsWith("FF") || cmdLine.startsWith("ff"))
	    			&& (cmdLine.endsWith("FF") || cmdLine.endsWith("ff"))
	    			&& cmdLine.length() == COMMAND_LENGTH*2 ) {
	    		String cmd1 = cmdLine.substring(2, 4);
	    		String cmd2 = cmdLine.substring(4, 6);
	    		String cmd3 = cmdLine.substring(6, 8);
	    		
	    		try {
	    			icmd1 = Integer.parseInt(cmd1, COMMAND_RADIOX);
	    			icmd2 = Integer.parseInt(cmd2, COMMAND_RADIOX);
	    			icmd3 = Integer.parseInt(cmd3, COMMAND_RADIOX);
	    		} catch (Exception e) {
	    			icmd1 = icmd2 = icmd3 = -1;
	    		}
	    		
	    		if (icmd1 >= 0 && icmd2 >= 0 && icmd3 >= 0) {
    				mCmd1 = (byte)icmd1;
    				mCmd2 = (byte)icmd2;
    				mCmd3 = (byte)icmd3;
	    	
	    		} else {
	    			Log.i("Constant", "uncorrect command:" + cmdLine 
	    					+ " cmd1=" + icmd1
	    					+ " cmd2=" + icmd2
	    					+ " cmd3=" + icmd3);
	    		}
	    	} else {
	    		Log.i("Constant", "error format command:" + cmdLine 
    					+ " cmd1=" + icmd1
    					+ " cmd2=" + icmd2
    					+ " cmd3=" + icmd3);
	    	}
		}
		
		public boolean isValid() {
			if (mCmd1 != 0 || mCmd2 != 0 || mCmd3 != 0) {
    			return true;
    		} else {
    			return false;
    		}
		}
	}

}
