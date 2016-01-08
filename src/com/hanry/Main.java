package com.hanry;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
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
import android.os.Message;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hanry.Constant;
import com.hanry.R;
import com.hanry.WifiCarSettings;
import com.hanry.Constant.CommandArray;

public class Main extends Activity implements SeekBar.OnSeekBarChangeListener
{
    private final int MSG_ID_ERR_CONN = 1001;
    //private final int MSG_ID_ERR_SEND = 1002;
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
    
    private final int STATUS_INIT = 0x2001;
    //private final int STATUS_CONNECTING = 0x2002;
    private final int STATUS_CONNECTED = 0x2003;
    private final int WARNING_ICON_OFF_DURATION_MSEC = 600;
    private final int WARNING_ICON_ON_DURATION_MSEC = 800;    
    
    private final int WIFI_STATE_UNKNOW = 0x3000;
    private final int WIFI_STATE_DISABLED = 0x3001;
    private final int WIFI_STATE_NOT_CONNECTED = 0x3002;
    private final int WIFI_STATE_CONNECTED = 0x3003;
    
    private final int MIN_GEAR_STEP = 5;
    private final int MAX_GEAR_VALUE_X = 180;
    private final int MAX_GEAR_VALUE_Y = 100;
    private final int INIT_GEAR_VALUE_X = 90;
    private final int INIT_GEAR_VALUE_Y = 40;
    
    private final int MIN_SPEED_STEP = 0;
    private final int MAX_SPEED_VALUE = 100;
    private final int INIT_SPEED_VALUE = 50;
    
    private final int MIN_SCROLL_DISTANCE = 200;
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
    
    private ImageButton ForWard;
    private ImageButton BackWard;
    private ImageButton TurnLeft;
    private ImageButton TurnRight;
    private ImageButton TakePicture;
    
    private ImageView mAnimIndicator;
    private boolean bAnimationEnabled = true;
    private Drawable mWarningIcon;
    private boolean bReaddyToSendCmd = false;
    private TextView mLogText;
  
    private Drawable ForWardon;
    private Drawable ForWardoff;
    private Drawable BackWardon;
    private Drawable BackWardoff;
    private Drawable TurnLefton;
    private Drawable TurnLeftoff;
    private Drawable TurnRighton;
    private Drawable TurnRightoff;
    private Drawable buttonLenon;
    private Drawable buttonLenoff;
    
    private GestureDetector detector;
    private Vibrator vibrator;
    private SeekBar mSeekBar;
    private SeekBar mSeekBar1;
    private SeekBar mSeekBar2;
    private SeekBar mSeekBar3;
    private int mSeekBarValue = -1;
    private int mMessage = 0;
    private int Value_X = 90;
    private int Value_Y = 90;
    private int Value = 0;
    
    String result = "";
    private final int PING_WAIT_TIME=2500;
    
    private ImageButton buttonCus1;
    private ImageButton buttonLen;
    private boolean bLenon = false;
    private int mWifiStatus = STATUS_INIT;

    private Thread mThreadClient = null;
    private boolean mThreadFlag = false;

    private boolean mQuitFlag = false;
    private boolean bHeartBreakFlag = false;
    private int mHeartBreakCounter = 0;
    private int mLastCounter = 0;
    
    private Context mContext;
    SocketClient mtcpSocket;
    MjpegView backgroundView = null;
    /*
    private byte[] COMM_FORWARD = {(byte) 0xFF, (byte)0x00, (byte)0x01, (byte)0x00, (byte) 0xFF};
    private byte[] COMM_BACKWARD = {(byte) 0xFF, 0x00, 0x02, 0x00, (byte) 0xFF};
    private byte[] COMM_STOP = {(byte) 0xFF, 0x00, 0x00, 0x00, (byte) 0xFF};
    private byte[] COMM_LEFT = {(byte) 0xFF, 0x00, 0x03, 0x00, (byte) 0xFF};
    private byte[] COMM_RIGHT = {(byte) 0xFF, 0x00, 0x04, 0x00, (byte) 0xFF};
    */
    /*
     * 
     * 修改了几个方向命令作为通信测试，后期将继续完善
     */
    /*
    private byte[] COMM_STOP = {(byte) 0xFF, 0x00, 0x00, 0x00, 0x0F};
    private byte[] COMM_FORWARD = {(byte) 0xFF, 0x00, 0x01, 0x00, 0x10};
    private byte[] COMM_BACKWARD = {(byte) 0xFF, 0x00, 0x02, 0x00, 0x10};
    private byte[] COMM_LEFT={(byte) 0xFF, 0x00, 0x03, 0x00, 0x10};
    private byte[] COMM_RIGHT={(byte) 0xFF, 0x00, 0x04, 0x00, 0x10};
    private byte[] COMM_GEAR_CONTROL = {(byte) 0xFF, 0x01, 0x01, 0x00, (byte) 0xFF};
    private byte[] COMM_LEN_ON = {(byte) 0xFF, 0x03, 0x00, 0x00, 0x10};
    private byte[] COMM_LEN_OFF = {(byte) 0xFF, 0x03, 0x01, 0x00, 0x10};
    
       */
    
    private byte[] COMM_STOP = {(byte) 0xFF, 0x00, 0x00, 0x00, 0x0F, (byte) 0xFE};
    private byte[] COMM_FORWARD = {(byte) 0xFF, 0x00, 0x01, 0x00, 0x10, (byte) 0xFE};
    private byte[] COMM_BACKWARD = {(byte) 0xFF, 0x00, 0x02, 0x00, 0x10, (byte) 0xFE};
    private byte[] COMM_LEFT={(byte) 0xFF, 0x00, 0x03, 0x00, 0x10, (byte) 0xFE};
    private byte[] COMM_RIGHT={(byte) 0xFF, 0x00, 0x04, 0x00, 0x10, (byte) 0xFE};
    private byte[] COMM_GEAR_CONTROL = {(byte) 0xFF, 0x01, 0x01, 0x00, (byte) 0xFF, (byte) 0xFE};
    private byte[] COMM_SPEED_CONTROL = {(byte) 0xFF, 0x02, 0x01, 0x00, (byte) 0xFF, (byte) 0xFE};
    private byte[] COMM_LEN_ON = {(byte) 0xFF, 0x03, 0x00, 0x00, 0x10, (byte) 0xFE};
    private byte[] COMM_LEN_OFF = {(byte) 0xFF, 0x03, 0x01, 0x00, 0x10, (byte) 0xFE};

    private byte[] COMM_SELF_CHECK = {(byte) 0xFF, (byte)0xEE, (byte)0xEE, 0x00, (byte) 0xFF};
    private byte[] COMM_SELF_CHECK_ALL = {(byte) 0xFF, (byte)0xEE, (byte)0xE0, 0x00, (byte) 0xFF};

    private byte[] COMM_HEART_BREAK = {(byte) 0xFF, (byte)0xEE, (byte)0xE1, 0x00, (byte) 0xFF};
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        
        initSettings();
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//隐去标题（应用的名字必须要写在setContentView之前，否则会有异常）
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.main);

        ForWard= (ImageButton)findViewById(R.id.btnForward);
        TurnLeft= (ImageButton)findViewById(R.id.btnLeft);
        TurnRight=(ImageButton)findViewById(R.id.btnRight);
        BackWard= (ImageButton)findViewById(R.id.btnBack);

        buttonCus1= (ImageButton)findViewById(R.id.ButtonCus1);
        buttonCus1.setOnClickListener(buttonCus1ClickListener);
        buttonCus1.setOnLongClickListener(buttonCus1ClickListener2);
        
        buttonLen= (ImageButton)findViewById(R.id.btnLen);
        buttonLen.setOnClickListener(buttonLenClickListener);
        buttonLen.setLongClickable(true);
        
        TakePicture = (ImageButton)findViewById(R.id.ButtonTakePic);
        TakePicture.setOnClickListener(buttonIndicatorListener);
        mAnimIndicator = (ImageView)findViewById(R.id.btnIndicator);
        mAnimIndicator.setOnClickListener(buttonTakePicClickListener);//buttonTakePicClickListener
        mWarningIcon = getResources().getDrawable(R.drawable.sym_indicator1);
        
        ForWardon = getResources().getDrawable(R.drawable.sym_forward_1);
        ForWardoff = getResources().getDrawable(R.drawable.sym_forward);
        
        TurnLefton = getResources().getDrawable(R.drawable.sym_left_1);
        TurnLeftoff = getResources().getDrawable(R.drawable.sym_left);
        
        TurnRighton = getResources().getDrawable(R.drawable.sym_right_1);
        TurnRightoff = getResources().getDrawable(R.drawable.sym_right);
        
        BackWardon = getResources().getDrawable(R.drawable.sym_backward_1);
        BackWardoff = getResources().getDrawable(R.drawable.sym_backward);
        
        buttonLenon = getResources().getDrawable(R.drawable.sym_light);
        buttonLenoff = getResources().getDrawable(R.drawable.sym_light_off);
        
        
        backgroundView = (MjpegView)findViewById(R.id.mySurfaceView1); 
        
        mLogText = (TextView)findViewById(R.id.logTextView);
        if (null != mLogText) {
            mLogText.setBackgroundColor(Color.argb(0, 0, 255, 0));//0~255透明度值
            mLogText.setTextColor(Color.argb(90, 0, 255, 0));
        }
        //创建手势检测器
        
        mSeekBar = (SeekBar)findViewById(R.id.gear1);
        mSeekBar.setMax(MAX_GEAR_VALUE_X);
        mSeekBar.setProgress(INIT_GEAR_VALUE_X);
        mSeekBar.setOnSeekBarChangeListener(this);
        mSeekBar1 = (SeekBar)findViewById(R.id.gear2);
        mSeekBar1.setMax(MAX_GEAR_VALUE_Y);
        mSeekBar1.setProgress(INIT_GEAR_VALUE_Y);
        mSeekBar1.setOnSeekBarChangeListener(this);
        mSeekBar2 = (SeekBar)findViewById(R.id.gear3);
        mSeekBar2.setMax(MAX_SPEED_VALUE);
        mSeekBar2.setProgress(INIT_SPEED_VALUE);
        mSeekBar2.setOnSeekBarChangeListener(this);
        mSeekBar3 = (SeekBar)findViewById(R.id.gear4);
        mSeekBar3.setMax(MAX_SPEED_VALUE);
        mSeekBar3.setProgress(INIT_SPEED_VALUE);
        mSeekBar3.setOnSeekBarChangeListener(this);
        
        buttonLen.setKeepScreenOn(true);
        
        //connect  
        connectToRouter(m4test);
        //connectToRouter(true);
        
        ForWard.setOnTouchListener(new View.OnTouchListener() 
        {
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch(action)
                {
                case MotionEvent.ACTION_DOWN:
                    sendCommand(COMM_FORWARD);
                    ForWard.setImageDrawable(ForWardon);
                    ForWard.invalidateDrawable(ForWardon);
                    break;
                case MotionEvent.ACTION_UP:
                    sendCommand(COMM_STOP);
                    ForWard.setImageDrawable(ForWardoff);
                    ForWard.invalidateDrawable(ForWardoff);
                    break;                 
                }
                
                return false;
            }
        });
        
        BackWard.setOnTouchListener(new View.OnTouchListener() 
        {
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch(action)
                {
                case MotionEvent.ACTION_DOWN:
                    sendCommand(COMM_BACKWARD);
                    BackWard.setImageDrawable(BackWardon);
                    BackWard.invalidateDrawable(BackWardon);
                    break;                    
                case MotionEvent.ACTION_UP:
                    sendCommand(COMM_STOP);
                    BackWard.setImageDrawable(BackWardoff);
                    BackWard.invalidateDrawable(BackWardoff);
                    break;
                }
                return false;
            }
                    
        });
        TurnRight.setOnTouchListener(new View.OnTouchListener() 
        {
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch(action)
                {
                case MotionEvent.ACTION_DOWN:
                    sendCommand(COMM_RIGHT);
                    TurnRight.setImageDrawable(TurnRighton);
                    TurnRight.invalidateDrawable(TurnRighton);
                    break;
                case MotionEvent.ACTION_UP:
                	sendCommand(COMM_STOP);
                    TurnRight.setImageDrawable(TurnRightoff);
                    TurnRight.invalidateDrawable(TurnRightoff);
                    break;
                }
                return false;
            }
        });
        TurnLeft.setOnTouchListener(new View.OnTouchListener() 
        {
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch(action)
                {
                case MotionEvent.ACTION_DOWN:
                	sendCommand(COMM_LEFT);
                    TurnLeft.setImageDrawable(TurnLefton);
                    TurnLeft.invalidateDrawable(TurnLefton);
                    break;
                case MotionEvent.ACTION_UP:     
                	sendCommand(COMM_STOP);
                    TurnLeft.setImageDrawable(TurnLeftoff);
                    TurnLeft.invalidateDrawable(TurnLeftoff);
                    break;
                }
                return false;
            }
        });
    }

    private OnClickListener buttonLenClickListener = new OnClickListener() {
        public void onClick(View arg0) {            
              if (bLenon) {
                  bLenon = false;
                  sendCommand(COMM_LEN_OFF);
                  buttonLen.setImageDrawable(buttonLenoff);
                  buttonLen.invalidateDrawable(buttonLenon);
              } else  {
                  bLenon = true;
                  sendCommand(COMM_LEN_ON);
                  buttonLen.setImageDrawable(buttonLenon);
                  buttonLen.invalidateDrawable(buttonLenon);
              }
            
        }
    };
    
    private OnClickListener buttonTakePicClickListener = new OnClickListener() {
    	public void onClick(View arg0) {       
            if (null != backgroundView) {
            	backgroundView.saveBitmap();
            }
        }
    };
    private OnClickListener buttonIndicatorListener = new OnClickListener() {
    	public void onClick(View arg0) {  
    		String cameraUrl = null;
            if (m4test) {
            	cameraUrl = CAMERA_VIDEO_URL_TEST;
            } else {
            	cameraUrl = CAMERA_VIDEO_URL;
            }
            if (null != cameraUrl && cameraUrl.length() > 4) {
            	Toast.makeText(mContext, "++视频中++", Toast.LENGTH_SHORT).show();
            	backgroundView.setSource(cameraUrl);//初始化Camera
            }

        }
    };
    private OnClickListener buttonCus1ClickListener = new OnClickListener() {
        public void onClick(View arg0) {       
            Intent setIntent = new Intent();
            setIntent.setClass(mContext, WifiCarSettings.class);
            startActivity(setIntent);
        }
    };
    
    private OnLongClickListener buttonCus1ClickListener2 = new OnLongClickListener() {
        public boolean onLongClick(View arg0) {
            mThreadFlag = false;
            try {
                if (null != mThreadClient)
                    mThreadClient.join(); // wait for secondary to finish
            } catch (InterruptedException e) {
                mLogText.setText("关闭路由器监听进程失败。。。" +  e.getMessage());
            }
            
            connectToRouter(m4test);
            return false;
        }
    };
    
    private void selfcheck() {
        sendCommand(COMM_SELF_CHECK);
    }
    
    private void sendCommand(byte[] data) {
        if ( mWifiStatus != STATUS_CONNECTED || null == mtcpSocket) {
            mLogText.setText("状态异常, 无法发送命令 ...." +  data.toString());
            return;
        }
        
        if (!bReaddyToSendCmd) {
        	mLogText.setText("please wait 1 second to send msg ....");
        	return;
        }
        	
        try {
            mtcpSocket.sendMsg(data);
            //Toast.makeText(mContext, "发送成功", 1);
        } catch (Exception e) {
            Log.i("Socket", e.getMessage() != null ? e.getMessage().toString() : "sendCommand error!");
            //Toast.makeText(mContext, "发送消息给路由器失败  ：" + e.getMessage(),
            //        Toast.LENGTH_SHORT).show();
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
            mLogText.setText("收到小车心跳包 ！");
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
     * *测试网络是否连通，并弹出警告对话框
     */
    private void pingIpAddr(String str) {
    	Process p;
    	Message msg = new Message();  
    	try {
    	//ping -c 3 -w 100  中  ，-c 是指ping的次数 3是指ping 3次 ，-w 100  以秒为单位指定超时间隔，是指超时时间为100秒
    	p = Runtime.getRuntime().exec("ping -c 3 -w 100 " + str);
    	//以信息的方式更新视图窗口的logtext
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
        /*
    	int status = getWifiStatus();
        if (WIFI_STATE_CONNECTED == status || isTest) {
            mThreadFlag = true;
            mThreadClient = new Thread(mRunnable);
            mThreadClient.start();
        } else if (WIFI_STATE_NOT_CONNECTED == status) {
            mLogText.setText("初始化连接路由器失败，wifi未连接，或者路由器状态异常！！");
        } else {
            mLogText.setText("初始化连接路由器失败，wifi未开启，请手动开启后重试！");
        }
        */
        mThreadClient = new Thread(mRunnable);
        mThreadClient.start();
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
    
    private Runnable mRunnable = new Runnable() 
    {
        public void run()
        {   
            BufferedInputStream is = null;
            try {                
                //连接服务器
                initWifiConnection();
                       
                //取得输入、输出流
                //mBufferedReaderClient = new BufferedReader(new InputStreamReader(mtcpSocket.getInputStream()));
                
                is = new BufferedInputStream(mtcpSocket.getInputStream());
            } catch (Exception e) {
                Message msg = new Message();
                msg.what = MSG_ID_ERR_INIT_READ;
                mHandler.sendMessage(msg);
                return;
            }            

            byte[] buffer = new byte[256];
            long lastTicket = System.currentTimeMillis();
            byte[] command = {0,0,0,0,0};
            int commandLength = 0;
            int i = 0;
            while (mThreadFlag)
            {
                try
                {
                    //if ( (recvMessageClient = mBufferedReaderClient.readLine()) != null )
                    //int ret = mBufferedReaderClient.read(buffer);
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
    
    Handler mHandler = new Handler()
    {                                        
          public void handleMessage(Message msg)                                        
          {  
           //  Log.i("Main", "handle internal Message, id=" + msg.what);
              
              switch (msg.what) {
              case MSG_ID_ERR_RECEIVE:
                  break;
              case MSG_ID_CON_READ:
                  byte[] command = (byte[])msg.obj;
                  //mLogText.setText("handle response from router: " + command.toString() );
                  handleCallback(command);
                  break;
              case MSG_ID_ERR_INIT_READ:
                  mLogText.setText("打开监听失败！！");
                  break;
              case MSG_ID_CON_SUCCESS:
                  mLogText.setText("成功连接到路由器!");
    
                  Message msgStartCheck = new Message();
                  msgStartCheck.what = MSG_ID_START_CHECK;
                  mHandler.sendMessageDelayed(msgStartCheck, 3000);
                  
                  Message msgHB1 = new Message();
                  msgHB1.what = MSG_ID_HEART_BREAK_RECEIVE;//启动心跳包检测循环
                  mHandler.sendMessage(msgHB1);
                  
                  Message msgHB2 = new Message();
                  msgHB2.what = MSG_ID_HEART_BREAK_SEND;//启动心跳包循环发送
                  //mHandler.sendMessage(msgHB2);
                  
                  break;
              case MSG_ID_START_CHECK:
                  mLogText.setText("开始进行自检，请稍等。。。。!!");
                  bReaddyToSendCmd = true;
                  //selfcheck();
                  break;
              case MSG_ID_ERR_CONN:
                  mLogText.setText("连接路由器失败!");
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
                      mLogText.setText("心跳包出现异常，已经忽略***");
                  }
                  Log.i("main", "handle MSG_ID_HEART_BREAK_RECEIVE :flag=" + bHeartBreakFlag);
                  
                  if (mLastCounter == 0 && mHeartBreakCounter > 0) {
                      startIconAnimation();
                  }
                  mLastCounter = mHeartBreakCounter;
                  mHeartBreakCounter = 0;
                  Message msgHB = new Message();
                  msgHB.what = MSG_ID_HEART_BREAK_RECEIVE;//启动心跳包检测循环
                  mHandler.sendMessageDelayed (msgHB, HEART_BREAK_CHECK_INTERVAL);
                  break;
              case MSG_ID_HEART_BREAK_SEND:
            	  Message msgSB = new Message();
                  msgSB.what = MSG_ID_HEART_BREAK_SEND;//循环向路由器发送心跳包
                  Log.i("main", "handle MSG_ID_HEART_BREAK_SEND");
                  
                  sendCommand(COMM_HEART_BREAK);
                  mHandler.sendMessageDelayed (msgSB, HEART_BREAK_SEND_INTERVAL);
            	  break;
              default :
                  break;
              }
              super.handleMessage(msg);            

          }                                    
     };
     
     private boolean isIconAnimationEnabled () {
         return bAnimationEnabled && bHeartBreakFlag;
     }
     private boolean mIconAnimationState = false;
     /** Icon animation handler for flashing warning alerts. */
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
     * 减缓响应速度，防止跑飞
     */
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUserh) {
        switch(seekBar.getId()){
        case R.id.gear1:
            mLogText.setText("change horizontal angle: " + progress);
            mSeekBarValue = progress;
            Value = progress;
            COMM_GEAR_CONTROL[2] = 0x01;
            COMM_GEAR_CONTROL[3] = (byte) Value;
            if(COMM_GEAR_CONTROL[3]<0)
            	Value = (256 + COMM_GEAR_CONTROL[0] +
          			 COMM_GEAR_CONTROL[1] + COMM_GEAR_CONTROL[2] +
           			 256 + COMM_GEAR_CONTROL[3]  );
            else
            	Value = (256 + COMM_GEAR_CONTROL[0] +
             			 COMM_GEAR_CONTROL[1] + COMM_GEAR_CONTROL[2] +
              			 COMM_GEAR_CONTROL[3] );
            COMM_GEAR_CONTROL[4] = (byte)( Value >> 4);
            sendCommand(COMM_GEAR_CONTROL);
            break;
        case R.id.gear2:
                mLogText.setText("change vertival speed: " + progress);
                mSeekBarValue = progress;
                Value = progress;
                COMM_GEAR_CONTROL[2] = 0x02;
                COMM_GEAR_CONTROL[3] = (byte) Value;
                if(COMM_GEAR_CONTROL[3]<0)
                	Value = (256 + COMM_GEAR_CONTROL[0] +
                			COMM_GEAR_CONTROL[1] + COMM_GEAR_CONTROL[2] +
                			256 + COMM_GEAR_CONTROL[3]  );
                else
                	Value = (256 + COMM_GEAR_CONTROL[0] +
                			COMM_GEAR_CONTROL[1] + COMM_GEAR_CONTROL[2] +
                  			COMM_GEAR_CONTROL[3] );
                COMM_GEAR_CONTROL[4] = (byte)( Value >> 4);
        		sendCommand(COMM_GEAR_CONTROL);
        		break;
        case R.id.gear3:
                mLogText.setText("change left speed: " + progress);
                mSeekBarValue = progress;
                Value = progress;
                COMM_SPEED_CONTROL[2] = 0x01;
                COMM_SPEED_CONTROL[3] = (byte) Value;
                if(COMM_SPEED_CONTROL[3]<0)
                	Value = (256 + COMM_SPEED_CONTROL[0] +
                			COMM_SPEED_CONTROL[1] + COMM_SPEED_CONTROL[2] +
               			 256 + COMM_SPEED_CONTROL[3]  );
                else
                	Value = (256 + COMM_SPEED_CONTROL[0] +
                			COMM_SPEED_CONTROL[1] + COMM_SPEED_CONTROL[2] +
                  			 COMM_SPEED_CONTROL[3] );
                COMM_SPEED_CONTROL[4] = (byte)( Value >> 4);
                sendCommand(COMM_SPEED_CONTROL);
                break;
        case R.id.gear4:
            mLogText.setText("change right speed: " + progress);
            mSeekBarValue = progress;
            Value = progress;
            COMM_SPEED_CONTROL[2] = 0x02;
            COMM_SPEED_CONTROL[3] = (byte) Value;
            if(COMM_SPEED_CONTROL[3]<0)
            	Value = (256 + COMM_SPEED_CONTROL[0] +
            			COMM_SPEED_CONTROL[1] + COMM_SPEED_CONTROL[2] +
           			 256 + COMM_SPEED_CONTROL[3]  );
            else
            	Value = (256 + COMM_SPEED_CONTROL[0] +
            			COMM_SPEED_CONTROL[1] + COMM_SPEED_CONTROL[2] +
              			 COMM_SPEED_CONTROL[3] );
            COMM_SPEED_CONTROL[4] = (byte)( Value >> 4);
            sendCommand(COMM_SPEED_CONTROL);
            break;
        }
    }

    public void onStartTrackingTouch(SeekBar seekBar) {
      
    }

    public void onStopTrackingTouch(SeekBar seekBar) {
   
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
    	/*
    	int status = getWifiStatus();
        
    	if (WIFI_STATE_CONNECTED == status || m4test) {
            String cameraUrl = null;
            if (m4test) {
            	cameraUrl = CAMERA_VIDEO_URL_TEST;
            } else {
            	cameraUrl = CAMERA_VIDEO_URL;
            }
            if (null != cameraUrl && cameraUrl.length() > 4) {
            	backgroundView.setSource(cameraUrl);//初始化Camera
            }
        }
        */
        backgroundView.resumePlayback();
        super.onResume();
    }
    
    @Override
    public void onBackPressed() {
        if (mQuitFlag) {
            finish();
        } else {
            mQuitFlag = true;
            Toast.makeText(mContext, "++请再次按返回键退出应用++", Toast.LENGTH_LONG).show();
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
		 
		 initLenControl(Constant.PREF_KEY_LEN_ON, Constant.DEFAULT_VALUE_LEN_ON);
		 initLenControl(Constant.PREF_KEY_LEN_OFF, Constant.DEFAULT_VALUE_LEN_OFF);
    }
    
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
/*
  //将该activity上的触碰事件交给GestureDetector处理
    public boolean onTouchEvent(MotionEvent me){
        return detector.onTouchEvent(me);
    }
	public boolean onDown(MotionEvent arg0) {
		return false;
	}

	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		
		return false;
	}

	public void onLongPress(MotionEvent e) {
		
	}

	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		float beginX = e1.getX();
		float endX = e2.getX();
		float beginY = e1.getY();
		float endY = e2.getY();
		float distance_X = endX - beginX;
		float distance_Y = endY - beginY;
		//
		//		为了防止舵机超界，我们设置10和170分别为左右的阈值
		//
		if(Value_X > 170)
		{
			Value_X = 170;
			vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);  
        	long[] pattern = { 10, 200, 10, 200, 10, 200 }; // {间隔时间，震动持续时间，间隔时间，震动持续时间，间隔时间，震动持续时间}
        	vibrator.vibrate(pattern, -1);
        	if(mMessage == 0)
        	{
        		AlertDialog.Builder builder  = new Builder(Main.this);
            	builder.setTitle("警告" ) ;
            	builder.setMessage("摄像头角度已经最大了！！" ) ;
            	builder.setPositiveButton("我知道了" ,  null );
            	builder.show();
            	mMessage = 1;
        	}
        	
        	//Toast.makeText(Main.this, "摄像头已经转过头了", Toast.LENGTH_SHORT).show();
        	return true;
        }
		else if(Value_X < 10)
		{
			Value_X = 10;
			vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);  
			long[] pattern = { 10, 200, 10, 200, 10, 200 }; // {间隔时间，震动持续时间，间隔时间，震动持续时间，间隔时间，震动持续时间}
			vibrator.vibrate(pattern, -1); 
			if(mMessage == 0)
        	{
				AlertDialog.Builder builder  = new Builder(Main.this);
	        	builder.setTitle("警告" ) ;
	        	builder.setMessage("摄像头角度已经最大了！！" ) ;
	        	builder.setPositiveButton("我知道了" ,  null );
	        	builder.show();
	        	mMessage = 1;
        	}
			return true;
		}
		else if(Value_Y > 170)
		{
			Value_Y = 170;
			vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);  
			long[] pattern = { 10, 200, 10, 200, 10, 200 }; // {间隔时间，震动持续时间，间隔时间，震动持续时间，间隔时间，震动持续时间}
			vibrator.vibrate(pattern, -1); 
			if(mMessage == 0)
        	{
				AlertDialog.Builder builder  = new Builder(Main.this);
	        	builder.setTitle("警告" ) ;
	        	builder.setMessage("摄像头角度已经最大了！！" ) ;
	        	builder.setPositiveButton("我知道了" ,  null );
	        	builder.show();
	        	mMessage = 1;
        	} 
			return true;
		}
		else if(Value_Y < 10)
		{
			Value_Y = 10;
			vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);  
			long[] pattern = { 10, 200, 10, 200, 10, 200 }; // {间隔时间，震动持续时间，间隔时间，震动持续时间，间隔时间，震动持续时间}
        	vibrator.vibrate(pattern, -1); 
        	if(mMessage == 0)
        	{
        		AlertDialog.Builder builder  = new Builder(Main.this);
            	builder.setTitle("警告" ) ;
            	builder.setMessage("摄像头角度已经最大了！！" ) ;
            	builder.setPositiveButton("我知道了" ,  null );
            	builder.show();
            	mMessage = 1;
        	}
        	return true;
		}
		if(distance_X > 0&&Math.abs(distance_Y) < MIN_SCROLL_DISTANCE
				&&Math.abs(distance_X) > MIN_SCROLL_DISTANCE)
		{
//		
//			 * 设置一个变量用于存储当前的角度值，每次对该角度值进行加减，每次发送5角度
//			 * 给现场机器人，用来调整摄像头的角度
//			
			Value_X = Value_X + 3;
			COMM_GEAR_CONTROL[3] = (byte) Value_X;
			if(COMM_GEAR_CONTROL[3]<0)
				Value = (256 + COMM_GEAR_CONTROL[0] +
						COMM_GEAR_CONTROL[1] + COMM_GEAR_CONTROL[2] +
						256 + COMM_GEAR_CONTROL[3]  );
			else
				Value = (256 + COMM_GEAR_CONTROL[0] +
						COMM_GEAR_CONTROL[1] + COMM_GEAR_CONTROL[2] +
						COMM_GEAR_CONTROL[3] );
			COMM_GEAR_CONTROL[4] = (byte)( Value >> 4);
			sendCommand(COMM_GEAR_CONTROL);
			mLogText.setText("right distance: "+ Value_X +" ");
			Log.i("MyGesture","right distance:"+ Value_X +" ");
		}
		else if(distance_X < 0&&Math.abs(distance_Y) < MIN_SCROLL_DISTANCE
				&&Math.abs(distance_X) > MIN_SCROLL_DISTANCE)
		{
			Value_X = Value_X - 3;
			COMM_GEAR_CONTROL[3] = (byte) Value_X;
			if(COMM_GEAR_CONTROL[3]<0)
				Value = (256 + COMM_GEAR_CONTROL[0] +
						COMM_GEAR_CONTROL[1] + COMM_GEAR_CONTROL[2] +
						256 + COMM_GEAR_CONTROL[3]  );
			else
				Value = (256 + COMM_GEAR_CONTROL[0] +
						COMM_GEAR_CONTROL[1] + COMM_GEAR_CONTROL[2] +
						COMM_GEAR_CONTROL[3] );
			COMM_GEAR_CONTROL[4] = (byte)( Value >> 4);
			sendCommand(COMM_GEAR_CONTROL);
			mLogText.setText("left distance: "+ Value_X +" ");
			Log.i("MyGesture","left distance:"+ Value_X +" ");
		}
		else if(distance_Y > 0&&Math.abs(distance_X) < MIN_SCROLL_DISTANCE
				&&Math.abs(distance_Y) > MIN_SCROLL_DISTANCE)
		{
			Value_Y = Value_Y - 3;
			COMM_GEAR_CONTROL[3] = (byte) Value_Y;
			if(COMM_GEAR_CONTROL[3]<0)
				Value = (256 + COMM_GEAR_CONTROL[0] +
						COMM_GEAR_CONTROL[1] + COMM_GEAR_CONTROL[2] +
						256 + COMM_GEAR_CONTROL[3]  );
			else
				Value = (256 + COMM_GEAR_CONTROL[0] +
						COMM_GEAR_CONTROL[1] + COMM_GEAR_CONTROL[2] +
						COMM_GEAR_CONTROL[3] );
			COMM_GEAR_CONTROL[4] = (byte)( Value >> 4);
			sendCommand(COMM_GEAR_CONTROL);
			mLogText.setText("down distance: "+ Value_Y +" ");
			Log.i("MyGesture","down distance:"+ Value_Y +" ");
		}
		else if(distance_Y < 0&&Math.abs(distance_X) < MIN_SCROLL_DISTANCE
				&&Math.abs(distance_Y) > MIN_SCROLL_DISTANCE)
		{
			Value_Y = Value_Y + 3;
			COMM_GEAR_CONTROL[3] = (byte) Value_Y;
			if(COMM_GEAR_CONTROL[3]<0)
				Value = (256 + COMM_GEAR_CONTROL[0] +
						COMM_GEAR_CONTROL[1] + COMM_GEAR_CONTROL[2] +
						256 + COMM_GEAR_CONTROL[3]  );
			else
				Value = (256 + COMM_GEAR_CONTROL[0] +
						COMM_GEAR_CONTROL[1] + COMM_GEAR_CONTROL[2] +
						COMM_GEAR_CONTROL[3] );
			COMM_GEAR_CONTROL[4] = (byte)( Value >> 4);
			sendCommand(COMM_GEAR_CONTROL);
			mLogText.setText("up distance: "+ Value_Y +" ");
			Log.i("MyGesture","up distance:"+ Value_Y +" ");
		}
		mMessage = 0;
        return true;   
	}

	public void onShowPress(MotionEvent e) {
		
	}

	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}
	*/
}


