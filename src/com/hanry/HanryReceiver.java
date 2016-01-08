package com.hanry;

import com.hanry.Constant;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class HanryReceiver extends BroadcastReceiver 
{ 
    @Override 
    public void onReceive(Context context, Intent intent) 
    { 
    	Log.i("MjpegView", "onReceive intent = " + intent);
    	
        if (Constant.ACTION_TAKE_PICTURE_DONE.equals(intent.getAction())) 
        { 
            Bundle bundle = intent.getExtras(); 
            if (bundle != null) 
            { 
                int res = bundle.getInt(Constant.EXTRA_RES); 
                String text = bundle.getString(Constant.EXTRA_PATH);
                
                Log.i("MjpegView", "onReceive intent, res= " + res + " path=" + text);
                
                switch (res) {
                case Constant.CAM_RES_OK:
                	Toast.makeText(context, "≥…π¶±£¥Ê’’∆¨£∫" + text, Toast.LENGTH_LONG).show();
                	break;
                case Constant.CAM_RES_FAIL_BITMAP_ERROR:
                case Constant.CAM_RES_FAIL_FILE_NAME_ERROR:
                case Constant.CAM_RES_FAIL_FILE_WRITE_ERROR:
                case Constant.CAM_RES_FAIL_NO_SPACE_LEFT:
                case Constant.CAM_RES_FAIL_UNKNOW:
                	Toast.makeText(context, "±£¥Ê’’∆¨ ß∞‹£∫Error = " + res, Toast.LENGTH_LONG).show();
                	break;
                default:
                	break;
                }
                 
            } 
        } 
    } 
} 