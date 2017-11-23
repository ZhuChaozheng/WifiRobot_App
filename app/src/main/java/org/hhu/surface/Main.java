package org.hhu.surface;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

import org.hhu.streaming.SocketClient;
import org.hhu.streaming.VolPlayer;
import org.hhu.streaming.VolRecorder;
import org.hhu.surface.Settings;
import org.hhu.surface.MyOrientationListener.OnOrientationListener;
import org.hhu.tool.Constant;
import org.hhu.tool.FrameData;
import org.hhu.tool.GPSData;
import org.hhu.tool.Utils;
import org.hhu.tool.Constant.CommandArray;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomControls;

public class Main extends Activity implements SeekBar.OnSeekBarChangeListener,android.view.GestureDetector.OnGestureListener
{
    private final int MSG_ID_ERR_CONN = 1001;
    private final int MSG_ID_ERR_RECEIVE = 1003;
    private final int MSG_ID_CON_READ = 1004;
    private final int MSG_ID_CON_SUCCESS = 1005;    
    private final int MSG_ID_START_CHECK = 1006;
    private final int MSG_ID_ERR_INIT_READ = 1007;
    private final int MSG_ID_CLEAR_QUIT_FLAG = 1008;
    
    private final int MSG_ID_LOOP_START = 1010;
    private final int MSG_ID_HEART_BREAK_RECEIVE = 1011;
    private final int MSG_ID_HEART_BREAK_SEND = 1012;
    private final int MSG_ID_LOOP_END = 1013;
    
    private final int MSG_PING_COMING=1010;
    
    private final int MSG_RECEIVE_ERROR = 1014;
	private final int MSG_QUIT = 1015;
	private final int MSG_SUCCESS = 1016;
	private final int MSG_OK = 1017;
    
    private final int STATUS_INIT = 0x2001;
    private final int STATUS_CONNECTED = 0x2003;
    private final int WARNING_ICON_OFF_DURATION_MSEC = 600;
    private final int WARNING_ICON_ON_DURATION_MSEC = 800;    
    
    private final int WIFI_STATE_UNKNOW = 0x3000;
    private final int WIFI_STATE_DISABLED = 0x3001;
    private final int WIFI_STATE_NOT_CONNECTED = 0x3002;
    private final int WIFI_STATE_CONNECTED = 0x3002;
    
    private final int MAX_SPEED_VALUE = 100;
    private final int INIT_SPEED_VALUE = 50;
    
    private final byte COMMAND_PERFIX = -1;
    private final int HEART_BREAK_CHECK_INTERVAL = 8000;//ms
    private final int QUIT_BUTTON_PRESS_INTERVAL = 2500;//ms
    private final int HEART_BREAK_SEND_INTERVAL = 2500;//ms
    
    private boolean m4test = true;

    private String CAMERA_VIDEO_URL = "http://192.168.2.1:8080/?action=stream";
    private String CAMERA_VIDEO_URL_TEST = "";
    private String ROUTER_CONTROL_URL = "192.168.2.1";
    private String ROUTER_CONTROL_URL_TEST = "192.168.43.150";
    private int ROUTER_CONTROL_PORT = 2001;
    private int ROUTER_CONTROL_PORT_TEST = 2001;
    private final String WIFI_SSID_PERFIX = "robot";
    
    private RelativeLayout relativeLayout;
    private ImageButton ForWard;
    private ImageButton BackWard;
    private ImageButton TurnLeft;
    private ImageButton TurnRight;
    private ImageButton TakePicture;
    
    private ImageButton mAnimIndicator;
    
    private ImageButton reset;
    
    private boolean bAnimationEnabled = true;
    private Drawable mWarningIcon;
    private boolean bReaddyToSendCmd = false;
    private TextView mLogText;
    private TextView mLogHorizontal;
    private TextView mLogVertical;
    private TextView mLogLeft;
    private TextView mLogRight;
  
    private Drawable buttonLenon;
    private Drawable buttonLenoff;
    
    private GestureDetector detector;
    private Vibrator vibrator;
    private SeekBar mSeekBar;
    private SeekBar mSeekBar1;

    private int mSeekBarValue = -1;
    private int mMessage = 0;
    private int Value_X = 90;
    private int Value_Y = 90;
    private int Value = 0;
    
    String result = "";
    private final int PING_WAIT_TIME=2500;
    
    private ImageButton buttonCus1;
    private ImageButton buttonAudio;
    private boolean bAudon = false;
    private int mWifiStatus = STATUS_INIT;

    private Thread mThreadClient = null;
    private Thread mThreadReceive = null;
    private boolean mThreadFlag = true;

    private boolean mQuitFlag = false;
    private boolean bHeartBreakFlag = false;
    private int mHeartBreakCounter = 0;
    private int mLastCounter = 0;
    
    private Context mContext;
    private MyHandler mHandler = null; 
    SocketClient mtcpSocket;
    MjpegView backgroundView = null;
    RockView rockView = null;
    protected VolPlayer     m_player;
    protected VolRecorder   m_recorder;
    
    
    private byte[] COM_REQ = {(byte)0xC8};
    private byte[] COMM_STOP = {(byte) 0xFF, 0x01, 0x01, 0x00, 0x00, 0x00, (byte) 0x5A, (byte) 0x88};
    private byte[] COMM_FORWARD = {(byte) 0xFF, 0x01, 0x01, 0x00, 0x01, 0x00, (byte) 0x6B, (byte) 0xBB};
    private byte[] COMM_BACKWARD = {(byte) 0xFF, 0x01, 0x01, 0x00, 0x02, 0x00, (byte) 0x38, (byte) 0xEE};
    private byte[] COMM_LEFT={(byte) 0xFF, 0x01, 0x01, 0x00, 0x03, 0x00, (byte) 0x09, (byte) 0xDD};
    private byte[] COMM_RIGHT={(byte) 0xFF, 0x01, 0x01, 0x00, 0x04, 0x00, (byte) 0x9E, (byte) 0x44};
    private byte[] COMM_GEAR_CONTROL = {(byte) 0xFF, 0x01, 0x01, 0x00, 0x05, 0x00, 0x00, 0x00};
    private byte[] COMM_SPEED_CONTROL = {(byte) 0xFF, 0x01, 0x01, 0x00, 0x0D, 0x00, 0x00, 0x00};
    private byte[] COMM_LEN_ON = {(byte) 0xFF, 0x03, 0x00, 0x00, 0x10, (byte) 0xFE};
    private byte[] COMM_LEN_OFF = {(byte) 0xFF, 0x03, 0x01, 0x00, 0x10, (byte) 0xFE};

    private byte[] COMM_SELF_CHECK = {(byte) 0xFF, (byte)0xEE, (byte)0xEE, 0x00, (byte) 0xFF};
    private byte[] COMM_SELF_CHECK_ALL = {(byte) 0xFF, (byte)0xEE, (byte)0xE0, 0x00, (byte) 0xFF};

    private byte[] COMM_HEART_BREAK = {(byte) 0xFF, (byte) 0xEE, (byte)0xE1, 0x00, (byte) 0xFF};
    /**
	 * 地图控件
	 */
	private MapView mMapView = null;
	/**
	 * 地图实例
	 */
	private BaiduMap mBaiduMap;
	/**
	 * 定位的客户端
	 */
	private LocationClient mLocationClient;
	/**
	 * 定位的监听器
	 */
	/**
	 * 当前定位的模式
	 */
	private com.baidu.mapapi.map.MyLocationConfiguration.LocationMode mCurrentMode = com.baidu.mapapi.map.MyLocationConfiguration.LocationMode.NORMAL;
	/***
	 * 是否是第一次定位
	 */
	private volatile boolean isFristLocation = true;

	/**
	 * 最新一次的经纬度
	 */
	private double mCurrentLantitude;
	private double mCurrentLongitude;
	/**
	 * 当前的精度
	 */
	private float mCurrentAccracy;
	/**
	 * 方向传感器的监听器
	 */
	private MyOrientationListener myOrientationListener;
	/**
	 * 方向传感器X方向的值
	 */
	private int mXDirection;
	
	private boolean flag = false;
 
	private GPSData gpsData;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mContext = this;
        initSettings();
        mHandler = new MyHandler();
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//Hide title
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        initMap();
        
        buttonCus1= (ImageButton)findViewById(R.id.ButtonCus);
        buttonCus1.setOnClickListener(buttonCus1ClickListener);
        
        buttonAudio= (ImageButton)findViewById(R.id.btnAudio);
        buttonAudio.setOnClickListener(buttonAudioClickListener);
        
        TakePicture = (ImageButton)findViewById(R.id.ButtonTakePic);
        TakePicture.setOnClickListener(buttonTakePicClickListener);
        
        mAnimIndicator = (ImageButton)findViewById(R.id.btnIndicator1);
        mAnimIndicator.setOnClickListener(buttonIndicatorListener);//buttonTakePicClickListener
        
        reset = (ImageButton)findViewById(R.id.reset);
        reset.setOnClickListener(buttonResetClickListener);
        
        mWarningIcon = getResources().getDrawable(R.drawable.sym_indicator1);
      
        buttonLenon = getResources().getDrawable(R.drawable.sym_light);
        buttonLenoff = getResources().getDrawable(R.drawable.sym_light_off);
        
        backgroundView = (MjpegView) findViewById (R.id.mySurfaceView1);
        rockView = (RockView) findViewById(R.id.view2);
        
        mLogText = (TextView)findViewById(R.id.logTextView);
        mLogLeft = (TextView)findViewById(R.id.log_left);
        mLogRight = (TextView)findViewById(R.id.log_right);
        mLogVertical = (TextView)findViewById(R.id.log_vertical);
        mLogHorizontal = (TextView)findViewById(R.id.log_horizontal);
        
        if (null != mLogText) {
            mLogText.setBackgroundColor(Color.argb(0, 0, 255, 0));//0~255 alpha value
            mLogText.setTextColor(Color.argb(172, 0, 255, 0));
        }

        //Create gesture detector
        detector = new GestureDetector(this,this); 
        
        mSeekBar = (SeekBar) findViewById(R.id.gear1);
        mSeekBar.setMax(MAX_SPEED_VALUE);
        mSeekBar.setProgress(INIT_SPEED_VALUE);
        mSeekBar.setOnSeekBarChangeListener(this);
        mSeekBar1 = (SeekBar) findViewById(R.id.gear2);
        mSeekBar1.setMax(MAX_SPEED_VALUE);
        mSeekBar1.setProgress(INIT_SPEED_VALUE);
        mSeekBar1.setOnSeekBarChangeListener(this);
        
        buttonAudio.setKeepScreenOn(true);
        
        data2Location(gpsData);
      		
        connectToRouter(m4test);
      	mThreadReceive = new Thread(mRunReceive);
        mThreadReceive.start();
}
    
	private void data2Location(GPSData gpsData) {
		LatLng point;
		if (isFristLocation)
		{
			isFristLocation = false;
			point = new LatLng(32, 118.8); 
		}
		else {
			point = new LatLng(gpsData.longitude, gpsData.latitude);  
		}
  		//构建Marker图标 
  		BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.navi_map_gps_locked);  
  		//构建MarkerOption，用于在地图上添加Marker  
  		OverlayOptions option = new MarkerOptions().position(point).icon(bitmap);
  		//在地图上添加Marker，并显示  
  		mBaiduMap.addOverlay(option);
  		//将地图设置到指定的中心点和缩放
  		mBaiduMap.setMapStatus(
  				MapStatusUpdateFactory.newMapStatus(
  						new MapStatus.Builder().target(point).zoom(20).build()
  				)
  		);
	}
	
	
	OnMapClickListener listener = new OnMapClickListener() {

		@Override
		public void onMapClick(LatLng arg0) {

			// switch video and map scale
			
		}

		@Override
		public boolean onMapPoiClick(MapPoi arg0) {

			// switch video and map scale
			
			return false;
		}  
	};
	
	private void initMap() {
		
    	mMapView = (MapView) findViewById(R.id.id_bmapView);
		// 获得地图的实例
		mBaiduMap = mMapView.getMap();
		
		// 隐藏缩放控件
        int childCount = mMapView.getChildCount();
        View zoom = null;
        for (int i = 0; i < childCount; i++) {
                View child = mMapView.getChildAt(i);
                if (child instanceof ZoomControls) {
                        zoom = child;
                        break;
                }
        }
//        zoom.setVisibility(View.GONE);
        // 隐藏比例尺控件
        int count = mMapView.getChildCount();
        View scale = null;
        for (int i = 0; i < count; i++) {
                View child = mMapView.getChildAt(i);
                if (child instanceof ZoomControls) {
                        scale = child;
                        break;
                }
        }
        //delete baiduMap logo
        mMapView.removeViewAt(1);
	}

	private OnClickListener buttonAudioClickListener = new OnClickListener() {
        public void onClick(View arg0) {            
              if (bAudon) {
                  bAudon = false;
                  //closePlayer();
                  buttonAudio.setImageDrawable(buttonLenoff);
                  buttonAudio.invalidateDrawable(buttonLenon);
              } 
              else  {
                  bAudon = true;
                  //openPlayer();
                  buttonAudio.setImageDrawable(buttonLenon);
                  buttonAudio.invalidateDrawable(buttonLenon);
              }
        }
    };
    
    private OnClickListener buttonResetClickListener = new OnClickListener() {
    	public void onClick(View arg0) {
    		
    		// Initial vertical direction angle 40 
    		COMM_GEAR_CONTROL[4] = 0x05;
    		COMM_GEAR_CONTROL[5] = 0x28;
    		COMM_GEAR_CONTROL = com_protocol(COMM_GEAR_CONTROL);
    		sendCommand(COMM_GEAR_CONTROL);
    		mLogHorizontal.setText("horizontal angle: 90°");
    		// Initial horizontal direction angle 90 
    		COMM_GEAR_CONTROL[4] = 0x06;
    		COMM_GEAR_CONTROL[5] = 0x5A;
    		COMM_GEAR_CONTROL = com_protocol(COMM_GEAR_CONTROL);
    		sendCommand(COMM_GEAR_CONTROL);
    		mLogVertical.setText("vertical angle: 40°");
    		mLogText.setText("Already reset cloud terrace!!");
    		Toast.makeText(mContext, "++Reset cloud terrace++", Toast.LENGTH_SHORT).show();
        }
    };
    
    private OnClickListener buttonIndicatorListener = new OnClickListener() {
    	public void onClick(View arg0) {       
            if (null != backgroundView) {
            	backgroundView.saveBitmap();
            }
        }
    };
    private OnClickListener buttonTakePicClickListener = new OnClickListener() {
    	public void onClick(View arg0) {  
    		String cameraUrl = null;
            if (m4test) {
            	cameraUrl = CAMERA_VIDEO_URL_TEST;
            } else {
            	cameraUrl = CAMERA_VIDEO_URL;
            }
            if (null != cameraUrl && cameraUrl.length() > 4) {
            	Toast.makeText(mContext, "++Video in++", Toast.LENGTH_SHORT).show();
            	backgroundView.setSource(cameraUrl);// Init camera
            }
        }
    };
    private OnClickListener buttonCus1ClickListener = new OnClickListener() {
        public void onClick(View arg0) {       
            Intent setIntent = new Intent();
            setIntent.setClass(mContext, Settings.class);
            startActivity(setIntent);
        }
    };
    
    /*
    private OnLongClickListener buttonCus1ClickListener2 = new OnLongClickListener() {
        public boolean onLongClick(View arg0) {
            mThreadFlag = false;
            try {
                if (null != mThreadClient)
                    mThreadClient.join(); // wait for secondary to finish
            } catch (InterruptedException e) {
                mLogText.setText("shut down robot listening process failed..." +  e.getMessage());
            }
            
            connectToRouter(m4test);
            return false;
        }
    };
    
    private void selfcheck() {
        sendCommand(COMM_SELF_CHECK);
    }
    */
    
    /*
     * 
     * Audio control 
     * 
     */
    
    private void openPlayer() {
    	m_player = new VolPlayer();
        m_player.init();
        m_player.start();
    }
    private void closePlayer() {
        m_player.free();
        m_player = null;
    }
    private void openRecorder() {
        m_recorder = new VolRecorder();
        m_recorder.init();
        m_recorder.start();
    }
    private void closeRecorder() {
        m_recorder.free();
        m_recorder = null;
    }
    
    private void sendCommand(byte[] data) {
//        if ( mWifiStatus != STATUS_CONNECTED || null == mtcpSocket) {
//            mLogText.setText("status abnormal,cannot send command..." +  data.toString());
//            return;
//        }
        
//        if (!bReaddyToSendCmd) {
//        	mLogText.setText("please wait 1 second to send msg ....");
//        	return;
//        }
        	
        try {
            mtcpSocket.sendMsg(data);
        } catch (Exception e) {
            Log.i("Socket", e.getMessage() != null ? e.getMessage().toString() : "sendCommand error!");
        }
    }
    
    private void handleCallback(byte[] command) {
        if (null == command || command.length != Constant.COMMAND_LENGTH) {
            return;
        }
        
        byte cmd1 = command[1];
        byte cmd2 = command[2];
        //byte cmd3 = command[3];
        
        if (command[0] != COMMAND_PERFIX || command[Constant.COMMAND_LENGTH-1] != COMMAND_PERFIX) {
        	return;	
        }
        
        if (cmd1 != 0x03) {
        	Log.i("Socket", "unknow command from router, ignor it! cmd1=" + cmd1);
        	return;
        }
        
        switch (cmd2) {
        case (byte)0x01:
            mLogText.setText("receive robot heart beat!!");
        	handleHeartBreak();
        	break;
        case (byte)0x02:
            handleHeartBreak();
            break;
        default:
        	
            break;
        }
    }
    
    /*
     * 
     * 
     *  Testing network is connect, and poping warning dialog
     *  
     *  
     */
    /*
    private void pingIpAddr(String str) {
    	Process p;
    	Message msg = new Message();  
    	try { 
    		// In 'ping -c 3 -w 100', '-c' setting times is 3, '-w' overtime 100s
	    	p = Runtime.getRuntime().exec("ping -c 3 -w 100 " + str);
	    	// Using message update view window logtext
	        msg.what = MSG_PING_COMING;
	        mHandler.sendMessageDelayed(msg, PING_WAIT_TIME);
	    	int status = p.waitFor();
	    	InputStream input = p.getInputStream();
	    	BufferedReader in = new BufferedReader(new InputStreamReader(input));
	    	StringBuffer buffer = new StringBuffer();
	    	String line = "";
	    	while ((line = in.readLine()) != null){
	    	buffer.append(line);
	    	}
	    	System.out.println("Return ============" + buffer.toString());
	    	if (status == 0) {
	    	result = "success";
	    	} else {
	    	result = "faild";
	    	}
    	} catch (IOException e) {
    		e.printStackTrace();
    	} catch (InterruptedException e) {
    		e.printStackTrace();
    	}
}
*/
    
    private int getWifiStatus () {
        int status = WIFI_STATE_UNKNOW;
        WifiManager mWifiMng = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        switch (mWifiMng.getWifiState()) {
        case WifiManager.WIFI_STATE_DISABLED:
        case WifiManager.WIFI_STATE_DISABLING:    
        case WifiManager.WIFI_STATE_ENABLING:
        case WifiManager.WIFI_STATE_UNKNOWN:
            status = WIFI_STATE_DISABLED;
            break;
        case WifiManager.WIFI_STATE_ENABLED:
            status = WIFI_STATE_NOT_CONNECTED;
            ConnectivityManager conMan = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            State wifiState = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
            if (State.CONNECTED == wifiState) {
                WifiInfo info = mWifiMng.getConnectionInfo();
                if (null != info) {
                    String bSSID = info.getBSSID();
                    String SSID = info.getSSID();
                    Log.i("Socket", "getWifiStatus bssid=" + bSSID + " ssid=" + SSID);
                    if (null != SSID && SSID.length() > 0) {
                        if (SSID.toLowerCase().contains(WIFI_SSID_PERFIX)) {
                            status = WIFI_STATE_CONNECTED;
                        }
                    }
                }
            }
            break;
        default:
            break;
        }
        return status;
    }
    
    private void connectToRouter(boolean isTest) {
    	int status = getWifiStatus();
        if (WIFI_STATE_CONNECTED == status || isTest) {
            mThreadFlag = true;
            mThreadClient = new Thread(mRunRock);
            mThreadClient.start();
        } else if (WIFI_STATE_NOT_CONNECTED == status) {
            mLogText.setText("Failed to initialize the connection,"
            		+ " WiFi is not connected, or robot status exception!!!");
        } else {
            mLogText.setText("Failed to initialize the connection, "
            		+ "WiFi is not open, please manually open and try again!!!");
        }
    }
    
    private void initWifiConnection() {
        mWifiStatus = STATUS_INIT;
        Log.i("Socket", "initWifiConnection");
        try {
            if (mtcpSocket != null) {
                mtcpSocket.closeSocket();
            }
            String clientUrl = ROUTER_CONTROL_URL;
            int clientPort = ROUTER_CONTROL_PORT;
            if (m4test) {
            	clientUrl = ROUTER_CONTROL_URL_TEST;
                clientPort = ROUTER_CONTROL_PORT_TEST;
            }
            mtcpSocket = new SocketClient(clientUrl, clientPort);
            Log.i("Socket", "Wifi Connect created ip=" + clientUrl
            		+ " port=" + clientPort);
            flag = true;
            mWifiStatus = STATUS_CONNECTED;
        } catch (Exception e) {
            Log.d("Socket", "initWifiConnection return exception! ");
        }
        
        Message msg = new Message();
        if (mWifiStatus != STATUS_CONNECTED || null == mtcpSocket) {          
            msg.what = MSG_ID_ERR_CONN;
        } else {
            msg.what = MSG_ID_CON_SUCCESS;
        }
        
        mHandler.sendMessage(msg);
    }
    
    private int appendBuffer (byte[] buffer, int len, byte[] dstBuffer, int dstLen) {
    	int j = 0;
    	int i = dstLen;
    	for (i = dstLen; i < Constant.COMMAND_LENGTH && j < len; i++) {
    		dstBuffer[i] = buffer[j];
    		j++;
    	}
    	return i;
    }
    
    private Runnable mRunReceive = new Runnable() 
    {	
		public void run()
		{
			Message msg = new Message();
			msg.what = MSG_SUCCESS;
			mHandler.sendMessage(msg);
			byte[] frame = new byte[30];
			
			try {
				while(mThreadFlag) {
					if(flag) {
						sendCommand(COM_REQ);
						int j = 0;
						byte[] recMsg = mtcpSocket.receiveMsg();
						
						for (int i = 0; i < recMsg.length; i++) {
							switch(recMsg[i])
							{
								case (byte)0xff:
									frame[j] = recMsg[i];
									j ++;
									break;
								case 0x02:
									frame[j] = recMsg[i];
									j ++;
									break;
								default:
									if (j >= 2) {
										frame[j] = recMsg[i];
										j ++;
									}
									else {
										j = 0;
									}
									
									break;
								
							}
								
							
						}
						// data solve
						int dataFieldLength = ((frame[3] << 8) + frame[2]) & 0xffff;
						byte[] dataField = new byte[5 + dataFieldLength];
						dataField[0] = frame[0];
						dataField[1] = frame[1];
						dataField[2] = frame[2];
						dataField[3] = frame[3];
						dataField[4] = frame[4];
						System.arraycopy(frame, 5, dataField, 5, dataFieldLength);
						byte[] dataField1 = new byte[dataFieldLength];
						System.arraycopy(dataField, 5, dataField1, 0, dataFieldLength);
						Utils utils = new Utils();
						int crc = utils.crc16(dataField);
//						calculate receive CRC data, and comparison
//						int crc2 = (((frame[23] << 8) + frame[24]) & 0xffff);
//						if (crc == crc2 ) {
							gpsData = utils.protocol2value(dataField1);
							Message msgData = new Message();
							msgData.what = MSG_OK;
							Bundle b = new Bundle();
							b.putDouble("langitude", gpsData.longitude);
							b.putDouble("latitude", gpsData.latitude);
							b.putDouble("heading", gpsData.heading);
							b.putDouble("speed", gpsData.speed);
							b.putInt("online", gpsData.online);
							b.putInt("visible", gpsData.visible);
							msgData.setData(b);
							mHandler.sendMessage(msgData);
						}
						
//					}
				}
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
    };
    
    private Runnable mRunRock = new Runnable() 
    {
        public void run()
        {   
            BufferedInputStream is = null;
            try {                
                //Connect server
                initWifiConnection();
                       
                //Get input output stream
              //  is = new BufferedInputStream(mtcpSocket.getInputStream());
            } catch (Exception e) {
                Message msg = new Message();
                msg.what = MSG_ID_ERR_INIT_READ;
                mHandler.sendMessage(msg);
                return;
            }  
            int type = 0;
            for( ; ;) {
            	try{
            		Thread.sleep(50);
            		if(type != rockView.mLogicType) {
            			type = rockView.mLogicType;
                		switch(type) {
                		case 0:
                			sendCommand(COMM_STOP);
                			Log.i("Main", "STOP++");
                			break;
                    	case 1:
                    		sendCommand(COMM_FORWARD);
                    		Log.i("Main", "UP++");
                    		break;
                    	case 2:
                    		sendCommand(COMM_BACKWARD);
                    		Log.i("Main", "BACK++");
                    		break;
                    	case 3:
                    		sendCommand(COMM_LEFT);
                    		Log.i("Main", "LEFT++");
                    		break;
                    	case 4:
                    		sendCommand(COMM_RIGHT);
                    		Log.i("Main", "RIGHT++");
                    		break;
                    	default:
                    			break;
                    	}
            		}
            	} catch(InterruptedException e) {
            		Thread.currentThread().interrupt();
            	}
            }
            /*
            byte[] buffer = new byte[256];
            long lastTicket = System.currentTimeMillis();
            byte[] command = {0,0,0,0,0};
            int commandLength = 0;
            int i = 0;
            while (mThreadFlag)
            {
                try
                {
                    int ret = is.read(buffer);
                    if (ret > 0) {
                    	
	                    printRecBuffer ("receive buffer", buffer, ret);
	                    
	                    if(ret > 0 && ret <= Constant.COMMAND_LENGTH ) { 
	                    	long newTicket = System.currentTimeMillis();
	                    	long ticketInterval = newTicket - lastTicket;
	                    	Log.d("Socket", "time ticket interval =" + ticketInterval);
	                		
	                    	if (ticketInterval < Constant.MIN_COMMAND_REC_INTERVAL) {
	                    		if (commandLength > 0) {
	                    			commandLength = appendBuffer(buffer, ret, command, commandLength);
	                    		} else {
	                    			Log.d("Socket", "not recognized command-1");
	                    		}
	                    	} else {
	                    		if (buffer[0] == COMMAND_PERFIX ) {
	                    			for (i = 0; i < ret; i++) {
	                                    command[i] = buffer[i];
	                                }
	                    			commandLength = ret;
	                    		} else {
	                    			Log.d("Socket", "not recognized command-2");
	                    			commandLength = 0;
	                    		}
	                    	}
	                        
	                    	lastTicket = newTicket;
	                    	printRecBuffer ("print command", command, commandLength);
	                    	
	                    	if (commandLength >= Constant.COMMAND_LENGTH) {
	                    		Message msg = new Message();
	                            msg.what = MSG_ID_CON_READ;
	                            msg.obj = command;
	                            mHandler.sendMessage(msg);
	                            commandLength = 0;
	                    	} 
	                    }
                    }
                } catch (Exception e) {
                    Message msg = new Message();
                    msg.what = MSG_ID_ERR_RECEIVE;
                    mHandler.sendMessage(msg);
                }
            }
            */
        }
    };
    
    void printRecBuffer(String tag, byte[] buffer, int len) {
    	StringBuffer sb = new StringBuffer();
    	sb.append(tag);
    	sb.append(" len = ");
    	sb.append(len);
    	sb.append(" :");
    	for (int i =0 ;i < len; i++) {
    		sb.append(buffer[i]);
    		sb.append(", ");
    	}
    	Log.i("Socket", sb.toString());
    }
     
     private boolean isIconAnimationEnabled () {
         return bAnimationEnabled && bHeartBreakFlag;
     }
     private boolean mIconAnimationState = false;     
    
    
     /**
      * 
      *  Icon animation handler for flashing warning alerts. 
      *  
      */
     private final Handler mAnimationHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (mIconAnimationState) {
                mAnimIndicator.setAlpha(255);
                if (isIconAnimationEnabled()) {
                    mAnimationHandler.sendEmptyMessageDelayed(0, WARNING_ICON_ON_DURATION_MSEC);
                }
            } else {
                mAnimIndicator.setAlpha(0);
                if (isIconAnimationEnabled()) {
                    mAnimationHandler.sendEmptyMessageDelayed(0, WARNING_ICON_OFF_DURATION_MSEC);
                }
            }
            mIconAnimationState = !mIconAnimationState;
            mAnimIndicator.invalidateDrawable(mWarningIcon);
        }
    };
    
    private void startIconAnimation() {
        Log.i("Animation", "startIconAnimation handler : " + mAnimationHandler);
        if (mAnimIndicator != null) {
            mAnimIndicator.setImageDrawable(mWarningIcon);
        }
        if (isIconAnimationEnabled())
            mAnimationHandler.sendEmptyMessageDelayed(0, WARNING_ICON_ON_DURATION_MSEC);
    }
    
    private void handleHeartBreak() {
        Log.i("Main", "handleHeartBreak");
        mHeartBreakCounter++;
        bHeartBreakFlag = true;
    }
    
    private void stopIconAnimation() {
        mAnimationHandler.removeMessages(0);
    }
    
    
    
    /*
     * 
     * input array length is 8, then output after calculate CRC16()
     * return the array length is 8.
     * 
     */
    
    public byte[] com_protocol(byte[] array) {
    	byte[] arrays = Arrays.copyOf(array, array.length - 2);
    	Utils utils = new Utils();
		String crcStr = Integer.toHexString(utils.crc16(arrays));
		while(true) {
			if(crcStr.length() < 4) {
				crcStr = "0" + crcStr;
			}
			else {
				break;
			}
		}
		String strLow = crcStr.substring(2, 4);
		String strHigh = crcStr.substring(0, 2);
		byte crcLow = (byte) Integer.parseInt(strLow, 16);
		byte crcHigh = (byte) Integer.parseInt(strHigh, 16);
		array = Arrays.copyOf(array, array.length);
		array[array.length - 2] = crcLow;
		array[array.length - 1] = crcHigh;
    	return array;
    }
    
    public void onDestroy() {     
        if(null != mtcpSocket) {                
            try {
                mtcpSocket.closeSocket();
            } catch (Exception e) {
                e.printStackTrace();
            }
            mThreadFlag = false;
            mThreadClient.interrupt();
        }
        
        if (null != mHandler) {
        	int i;
        	for (i = MSG_ID_LOOP_START + 1; i < MSG_ID_LOOP_END; i++ ) {
        		mHandler.removeMessages(i);
        	}
        }
        stopIconAnimation();
        super.onDestroy();
    }
   
    @Override
    protected void onResume() {
//        backgroundView.resumePlayback();
        super.onResume();
    }
    
    @Override
    public void onBackPressed() {
        if (mQuitFlag) {
            int pid = android.os.Process.myPid();
            android.os.Process.killProcess(pid);
        } else {
            mQuitFlag = true;
            Toast.makeText(mContext, "++Please press again to exit++", Toast.LENGTH_SHORT).show();
            Message msg = new Message();    
            msg.what = MSG_ID_CLEAR_QUIT_FLAG;
            mHandler.sendMessageDelayed(msg, QUIT_BUTTON_PRESS_INTERVAL);
        }
    }
    
    void initSettings () {
		 SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
		 
		 String CameraUrl = settings.getString(Constant.PREF_KEY_CAMERA_URL, Constant.DEFAULT_VALUE_CAMERA_URL);
		 CAMERA_VIDEO_URL = CameraUrl;
		 CameraUrl = settings.getString(Constant.PREF_KEY_CAMERA_URL_TEST, Constant.DEFAULT_VALUE_CAMERA_URL_TEST);
		 CAMERA_VIDEO_URL_TEST = CameraUrl;		 
		 
		 String RouterUrl = settings.getString(Constant.PREF_KEY_ROUTER_URL, Constant.DEFAULT_VALUE_ROUTER_URL);
		 int index = RouterUrl.indexOf(":");
		 String routerIP = "";
		 String routerPort = "";
		 int port = 0;
		 if (index > 0) {
			 routerIP = RouterUrl.substring(0, index);
			 routerPort = RouterUrl.substring(index+1, RouterUrl.length() );
			 port = Integer.parseInt(routerPort);
		 }
		 
		 ROUTER_CONTROL_URL = routerIP;
		 ROUTER_CONTROL_PORT = port;
		 
		 RouterUrl = settings.getString(Constant.PREF_KEY_ROUTER_URL_TEST, Constant.DEFAULT_VALUE_ROUTER_URL_TEST);
		 index = RouterUrl.indexOf(":");
		 if (index > 0) {
			 routerIP = RouterUrl.substring(0, index);
			 routerPort = RouterUrl.substring(index+1, RouterUrl.length() );
			 port = Integer.parseInt(routerPort);
		 }
		 
		 ROUTER_CONTROL_URL_TEST = routerIP;
		 ROUTER_CONTROL_PORT_TEST = port;
		 
		 m4test =  settings.getBoolean(Constant.PREF_KEY_TEST_MODE_ENABLED, false);
		 
		// initLenControl(Constant.PREF_KEY_LEN_ON, Constant.DEFAULT_VALUE_LEN_ON);
		// initLenControl(Constant.PREF_KEY_LEN_OFF, Constant.DEFAULT_VALUE_LEN_OFF);
    }
    /*
    void initLenControl (String prefKey, String defaultValue) {
   	 	SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);

    	String comm = settings.getString(prefKey, defaultValue);
    	CommandArray cmd = new CommandArray(comm);
		if (cmd.isValid() ) {
			if (Constant.PREF_KEY_LEN_ON.equalsIgnoreCase(prefKey)) {
				COMM_LEN_ON[1] = cmd.mCmd1;
				COMM_LEN_ON[2] = cmd.mCmd2;
				COMM_LEN_ON[3] = cmd.mCmd3;
			} else if (Constant.PREF_KEY_LEN_OFF.equalsIgnoreCase(prefKey)) {
				COMM_LEN_OFF[1] = cmd.mCmd1;
				COMM_LEN_OFF[2] = cmd.mCmd2;
				COMM_LEN_OFF[3] = cmd.mCmd3;	
			} else {
				Log.i("Main", "unknow prefKey:" + prefKey); 
			}
		} else {
			Log.i("Main", "error format of command:" + comm); 
		}
    }
    */
    private void showPopupWindow() {
    	View contentView = LayoutInflater.from(mContext)
				.inflate(R.layout.dialog_record, null);
		final PopupWindow popupWindow = new PopupWindow(
				contentView, LayoutParams.WRAP_CONTENT, 
				LayoutParams.WRAP_CONTENT, true);
		popupWindow.setTouchable(true);
		
		popupWindow.setTouchInterceptor(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                closeRecorder();
                Toast.makeText(mContext, "++ Recorder cancel ++", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
		popupWindow.setBackgroundDrawable(getResources().getDrawable(
                R.drawable.record_bg));
		popupWindow.showAtLocation(findViewById(R.id.root),Gravity.CENTER,0,0);
    }

  //The touch event on the GestureDetector to activity processing
    public boolean onTouchEvent(MotionEvent event){
        return detector.onTouchEvent(event);
    }
    
	public boolean onDown(MotionEvent arg0) {
		return false;
	}

	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		
		return false;
	}

	public void onLongPress(MotionEvent e) {
		//showPopupWindow();
		//openRecorder();
		//Toast.makeText(mContext, "++ Start recorder ++", Toast.LENGTH_SHORT).show();
	}
	

	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
			
		/*
		 * 
		 * In order to prevent the steering engine from 
		 * the super bound, we set the threshold value of
		 * 10 and 170 respectively for the left and right.
		 * 
		 */

		if(Value_X > 170)
		{
			Value_X = 170;
			vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);  
        	long[] pattern = { 10, 200, 10, 200, 10, 200 }; // {Interval time, vibration duration...}
        	vibrator.vibrate(pattern, -1);
        	if(mMessage == 0)
        	{
        		
            	mMessage = 1;
        	}
        	
        	return true;
        }
		else if(Value_X < 10)
		{
			Value_X = 10;
			vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);  
			long[] pattern = { 10, 200, 10, 200, 10, 200 }; 
			vibrator.vibrate(pattern, -1); 
			if(mMessage == 0)
        	{
				
	        	mMessage = 1;
        	}
			return true;
		}
		else if(Value_Y > 170)
		{
			Value_Y = 170;
			vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);  
			long[] pattern = { 10, 200, 10, 200, 10, 200 }; 
			vibrator.vibrate(pattern, -1); 
			if(mMessage == 0)
        	{
				
	        	mMessage = 1;
        	} 
			return true;
		}
		else if(Value_Y < 10)
		{
			Value_Y = 10;
			vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);  
			long[] pattern = { 10, 200, 10, 200, 10, 200 }; 
        	vibrator.vibrate(pattern, -1); 
        	if(mMessage == 0)
        	{
        		
            	mMessage = 1;
        	}
        	return true;
		}
		if (distanceX >= 10) {
			Value_X = Value_X + 2;
			COMM_GEAR_CONTROL[5] = (byte) Value_X;
			mLogText.setText("horizontal angle: " + Value_X);
			COMM_GEAR_CONTROL[4] = 0x05;
			COMM_GEAR_CONTROL = com_protocol(COMM_GEAR_CONTROL);
			sendCommand(COMM_GEAR_CONTROL);
			mLogHorizontal.setText("horizontal angle: " + Value_X + "°");
		}
		else if (distanceX <= -10){
			Value_X = Value_X - 2;
			COMM_GEAR_CONTROL[5] = (byte) Value_X;
			mLogText.setText("horizontal distance: "+ Value_X +" ");
			COMM_GEAR_CONTROL[4] = 0x05;
			COMM_GEAR_CONTROL = com_protocol(COMM_GEAR_CONTROL);
			sendCommand(COMM_GEAR_CONTROL);
			mLogHorizontal.setText("horizontal angle: " + Value_X + "°");
		}
		else if (distanceY <= -10) {
			mLogText.setText("vertical angle: " + Value_Y + " ");
			Value_Y = Value_Y + 2;
			COMM_GEAR_CONTROL[5] = (byte) Value_Y;
			COMM_GEAR_CONTROL[4] = 0x06;
			COMM_GEAR_CONTROL = com_protocol(COMM_GEAR_CONTROL);
			sendCommand(COMM_GEAR_CONTROL);
			mLogVertical.setText("vertical angle: " + Value_Y + "°");
		}
		else if (distanceY >= 10){
			mLogText.setText("vertical angle: " + Value_Y + " ");
			Value_Y = Value_Y - 2;
			COMM_GEAR_CONTROL[5] = (byte) Value_Y;
			COMM_GEAR_CONTROL[4] = 0x06;
			COMM_GEAR_CONTROL = com_protocol(COMM_GEAR_CONTROL);
			sendCommand(COMM_GEAR_CONTROL);
			mLogVertical.setText("vertical angle: " + Value_Y + "°");
		}
        return true;   
	}

	public void onShowPress(MotionEvent e) {

	}

	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		  switch(seekBar.getId()) {
		  case R.id.gear1:
	        	mLogText.setText("change left speed: " + progress);
	        	mLogLeft.setText("speed →: " + progress + "cm/s");
              mSeekBarValue = progress;
              Value = progress;
              COMM_SPEED_CONTROL[4] = 0x0D;
              COMM_SPEED_CONTROL[5] = (byte) Value;
              COMM_SPEED_CONTROL = com_protocol(COMM_SPEED_CONTROL);
              sendCommand(COMM_SPEED_CONTROL);
              break;
	      case R.id.gear2:
	        	mLogText.setText("change right speed: " + progress);
	        	mLogRight.setText("speed →: " + progress + "cm/s");
	            mSeekBarValue = progress;
	            Value = progress;
	            COMM_SPEED_CONTROL[4] = 0x0E;
	            COMM_SPEED_CONTROL[5] = (byte) Value;
	            COMM_SPEED_CONTROL = com_protocol(COMM_SPEED_CONTROL);
	            sendCommand(COMM_SPEED_CONTROL);
	            break;
	        }
	}

	@Override
	public void onStartTrackingTouch(SeekBar arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStopTrackingTouch(SeekBar arg0) {
		// TODO Auto-generated method stub
		
	}
	
	class MyHandler extends Handler {
		String data;
		int i = 0;
	    public MyHandler() { 
	    } 

	    public MyHandler(Looper L) { 
	        super(L); 
	    } 

	    @Override 
	    public void handleMessage(Message msg) { 
	    	switch(msg.what) {
	    	case MSG_RECEIVE_ERROR:
	    		data = "" + i;
	    		Log.d("MyHandler", "handleMessage error"); 
	            i++;
	    		break;
	    	case MSG_QUIT:
	             mQuitFlag = false;
	             break;
	    	case MSG_SUCCESS:
//	    		textView.setText("connected");
	    		break;
	    	case MSG_OK:
	    		Bundle b = msg.getData();
				Double langitude = b.getDouble("langitude");
				Double latitude = b.getDouble("latitude");
				Double heading = b.getDouble("heading");
				Double speed = b.getDouble("speed");
				int online = b.getInt("online");
				int visible = b.getInt("visible");
    			String langitudeData = "langitude: " + langitude;
    			String latitudeData = "latitude: " + latitude;
    			String speedData = "speed: " + speed;
    			String onlineData = "online: " + online;
    			data2Location(gpsData);
    			mLogText.setText(langitudeData + latitudeData + speedData
    					+ onlineData);
	    	default :
	            break;
	    	}
	    	super.handleMessage(msg); 
	    } 
	} 

}

/*
	
	final class MyHandler extends Handler {
		
		 public void handleMessage(Message msg)                                        
	     {  
	         switch (msg.what) {
	         case MSG_ID_ERR_RECEIVE:
	             break;
	         case MSG_ID_CON_READ:
	             byte[] command = (byte[])msg.obj;
	             handleCallback(command);
	             break;
	         case MSG_ID_ERR_INIT_READ:
	             mLogText.setText("Failed to open the monitor!!!");
	             break;
	         case MSG_ID_CON_SUCCESS:
	             mLogText.setText("");

	             Message msgStartCheck = new Message();
	             msgStartCheck.what = MSG_ID_START_CHECK;
	             mHandler.sendMessageDelayed(msgStartCheck, 3000);
	             
	             Message msgHB1 = new Message();
	             msgHB1.what = MSG_ID_HEART_BREAK_RECEIVE;//Start heartbeat packet detection cycle
	             mHandler.sendMessage(msgHB1);
	             
	             Message msgHB2 = new Message();
	             msgHB2.what = MSG_ID_HEART_BREAK_SEND;//Start heartbeat packet cycle
	             //mHandler.sendMessage(msgHB2);
	             
	             break;
	         case MSG_ID_START_CHECK:
	             mLogText.setText("Start self check, please wait a moment!!");
	             bReaddyToSendCmd = true;
	             //selfcheck();
	             break;
	         case MSG_ID_ERR_CONN:
	             mLogText.setText("connect to robot failed!");
	             break;
	         case MSG_ID_CLEAR_QUIT_FLAG:
	             mQuitFlag = false;
	             break;
	         case MSG_ID_HEART_BREAK_RECEIVE:
	             if (mHeartBreakCounter == 0) {
	                 bHeartBreakFlag = false;
	                 
	             } else if (mHeartBreakCounter > 0) {
	                 bHeartBreakFlag = true;
	             } else {
	                 mLogText.setText("Heartbeat package appears abnormal, has been ignored");
	             }
	             Log.i("main", "handle MSG_ID_HEART_BREAK_RECEIVE :flag=" + bHeartBreakFlag);
	             
	             if (mLastCounter == 0 && mHeartBreakCounter > 0) {
	                 startIconAnimation();
	             }
	             mLastCounter = mHeartBreakCounter;
	             mHeartBreakCounter = 0;
	             Message msgHB = new Message();
	             msgHB.what = MSG_ID_HEART_BREAK_RECEIVE;//Start heartbeat packet detection cycle
	             mHandler.sendMessageDelayed (msgHB, HEART_BREAK_CHECK_INTERVAL);
	             break;
	         case MSG_ID_HEART_BREAK_SEND:
	       	  Message msgSB = new Message();
	             msgSB.what = MSG_ID_HEART_BREAK_SEND;//Cycle to send a heartbeat packet to the robot
	             Log.i("main", "handle MSG_ID_HEART_BREAK_SEND");
	             
	             sendCommand(COMM_HEART_BREAK);
	             mHandler.sendMessageDelayed (msgSB, HEART_BREAK_SEND_INTERVAL);
	       	  break;
	         default :
	             break;
	         }
	         super.handleMessage(msg);            

	     }                                 
		 
	}

*/ 
