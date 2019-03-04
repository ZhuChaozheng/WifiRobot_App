package org.hhu.surface;

import java.net.URL;

import org.hhu.tool.Constant;
import org.hhu.tool.Constant.CommandArray;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class Settings extends PreferenceActivity implements OnSharedPreferenceChangeListener {  
	
	private EditTextPreference mPrefRouterUrl;
	private EditTextPreference mPrefCameraUrl;
	private EditTextPreference mPrefRouterUrlTest;
	private EditTextPreference mPrefCameraUrlTest;

    private EditTextPreference mPrefLeftMotorSpeed;
    private EditTextPreference mPrefRightMotorSpeed;
	
	private EditTextPreference mPrefLenOn;
	private EditTextPreference mPrefLenOff;

    private Context mContext;


    @Override
	public void onCreate(Bundle savedInstanceState) {  
		super.onCreate(savedInstanceState);
        mContext = this.getApplicationContext();
		// ��ĵ�ֵ�����Զ����浽SharePreferences  
		addPreferencesFromResource(R.xml.wifi_car_settings);


        Preference button = findPreference(getString(R.string.myCoolButton));
        button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent setIntent = new Intent();
                setIntent.setClass(mContext, OfflineMapActivity.class);
                startActivity(setIntent);
                return true;
            }
        });


		mPrefRouterUrl = (EditTextPreference)findPreference(Constant.PREF_KEY_ROUTER_URL);
		mPrefCameraUrl = (EditTextPreference)findPreference(Constant.PREF_KEY_CAMERA_URL);
		mPrefRouterUrlTest = (EditTextPreference)findPreference(Constant.PREF_KEY_ROUTER_URL_TEST);
		mPrefCameraUrlTest = (EditTextPreference)findPreference(Constant.PREF_KEY_CAMERA_URL_TEST);

        mPrefLeftMotorSpeed = (EditTextPreference)findPreference(Constant.PREF_KEY_LEFT_MOTOR_SPEED);
        mPrefRightMotorSpeed = (EditTextPreference)findPreference(Constant.PREF_KEY_RIGHT_MOTOR_SPEED);
		
		mPrefLenOn = (EditTextPreference)findPreference(Constant.PREF_KEY_LEN_ON);
		mPrefLenOff = (EditTextPreference)findPreference(Constant.PREF_KEY_LEN_OFF);
		 
		initValue();
	}  
	 
	 void initValue(){
		 SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);

		 String CameraUrl = settings.getString(Constant.PREF_KEY_CAMERA_URL, Constant.DEFAULT_VALUE_CAMERA_URL);
		 mPrefCameraUrl.setSummary(CameraUrl);
		 
		 String RouterUrl = settings.getString(Constant.PREF_KEY_ROUTER_URL, Constant.DEFAULT_VALUE_ROUTER_URL);
		 mPrefRouterUrl.setSummary(RouterUrl);
		 
		 
		 String testCameraUrl = settings.getString(Constant.PREF_KEY_CAMERA_URL_TEST, Constant.DEFAULT_VALUE_CAMERA_URL_TEST);
		 mPrefCameraUrlTest.setSummary(testCameraUrl);
		 
		 String testRouterUrl = settings.getString(Constant.PREF_KEY_ROUTER_URL_TEST, Constant.DEFAULT_VALUE_ROUTER_URL_TEST);
		 mPrefRouterUrlTest.setSummary(testRouterUrl);


		 String leftMotorSpeed = settings.getString(Constant.PREF_KEY_LEFT_MOTOR_SPEED, Constant.DEFAULT_PREF_KEY_LEFT_MOTOR_SPEED);
		 mPrefLeftMotorSpeed.setSummary(leftMotorSpeed);

         String rightMotorSpeed = settings.getString(Constant.PREF_KEY_RIGHT_MOTOR_SPEED, Constant.DEFAULT_PREF_KEY_RIGHT_MOTOR_SPEED);
         mPrefRightMotorSpeed.setSummary(rightMotorSpeed);


		 String lenon = settings.getString(Constant.PREF_KEY_LEN_ON, Constant.DEFAULT_VALUE_LEN_ON);
		 mPrefLenOn.setSummary(lenon);
		 
		 String lenoff = settings.getString(Constant.PREF_KEY_LEN_OFF, Constant.DEFAULT_VALUE_LEN_OFF);
		 mPrefLenOff.setSummary(lenoff);
	 }
	 
    @Override
	protected void onResume() {
	    super.onResume();
	    getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener( this );
	}
	
	@Override
	protected void onPause() {
	    super.onPause();
	    getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener( this );
	}
	
 
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,String key) {
		Preference pref = findPreference(key);
	    if (pref instanceof EditTextPreference) {
	        EditTextPreference etp = (EditTextPreference) pref;
	        if (etp == mPrefLenOn || etp == mPrefLenOff) {      	
	        	String comm = etp.getText();
	        	CommandArray cmd = new CommandArray(comm);
        		if (cmd.isValid() ) {
        		} else {
        			Toast.makeText(this, "�����ʽ��������������", Toast.LENGTH_SHORT).show();
        		}
	        } 
	        etp.setSummary(etp.getText());
	    }
	}


    private View.OnClickListener buttonMap1ClickListener = new View.OnClickListener() {
        public void onClick(View arg0) {
            Intent setIntent = new Intent();
            setIntent.setClass(mContext, OfflineMapActivity.class);
            startActivity(setIntent);
        }
    };

}
