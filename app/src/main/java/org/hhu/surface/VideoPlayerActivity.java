package org.hhu.surface;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.DrawableRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.GestureDetector.OnGestureListener;
import android.view.SurfaceHolder.Callback;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.content.Context;
import android.content.res.Configuration;

import com.baidu.location.LocationClient;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.ksyun.media.player.IMediaPlayer;
import com.ksyun.media.player.KSYMediaPlayer;


import net.qyvideo.qianyiplayer.util.QosThread;

import org.hhu.tools.VolPlayer;
import org.hhu.tools.VolRecorder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


//Main.java import

import java.io.BufferedInputStream;
import java.util.Arrays;

import org.hhu.streaming.SocketClient;
import org.hhu.tool.Constant;
import org.hhu.tool.GPSData;
import org.hhu.tool.Utils;
import org.hhu.utils.APIUtils;
import org.hhu.utils.GPSDataUtils;
import org.hhu.utils.HexUtils;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Looper;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.ZoomControls;

import android.content.ServiceConnection;





/**
 * @author dexter
 * 2018/11/17.
 */
public class VideoPlayerActivity extends Activity implements OnGestureListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = "VideoPlayerActivity";

    private static final int RECORD_REQUEST_CODE  = 101;
    private static final int STORAGE_REQUEST_CODE = 102;
    private static final int AUDIO_REQUEST_CODE   = 103;

    private MediaProjectionManager projectionManager;
    private MediaProjection mediaProjection;
    private RecordService recordService;

    private boolean isRecording = false;

    public static final int UPDATE_SEEKBAR = 0;
    public static final int HIDDEN_SEEKBAR = 1;
    public static final int UPDATE_QOS  = 2;

    private KSYMediaPlayer ksyMediaPlayer;
    private QosThread mQosThread;

    private Surface mSurface = null;
//    private SurfaceView backgroundView = null;
    private SurfaceHolder mSurfaceHolder = null;

//    private Handler mHandler;

    // UI
    private LinearLayout mPlayerPanel;
    private ImageView mPlayerStartBtn;
    private SeekBar mPlayerSeekbar;
    private TextView mPlayerPosition;
    private TextView mLoadText;
    private TextView mCpu;
    private TextView mMemInfo;
    private TextView mVideoResolution;
    private TextView mVideoBitrate;
    private TextView mFrameRate;
    private TextView mVideoBufferTime;
    private TextView mAudioBufferTime;
    private TextView mServerIp;
    private TextView mSdkVersion;
    private TextView mDNSTime;
    private TextView mHttpConnectionTime;

    protected VolPlayer     mPlayer;
	protected VolRecorder   mRecorder;
//	private GestureDetector detector;
	
    private Button mPlayerScaleVideo;

    private boolean mPlayerPanelShow = false;
    private boolean mPause = false;
    private  boolean lightStatus = false; //大灯开关状态

    private long mStartTime = 0;
    private long mPauseStartTime = 0;
    private long mPausedTime = 0;

    private int mVideoWidth = 0;
    private int mVideoHeight = 0;

    private int mVideoScaleIndex = 0;
    private boolean useHwCodec = false;

    private String mDataSource;





    //Main.java

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

    private String CAMERA_VIDEO_URL = null;
    private String CAMERA_VIDEO_URL_TEST = "";
    private String ROUTER_CONTROL_IP = "192.168.2.1";
    private String ROUTER_CONTROL_URL_TEST = "192.168.43.150";
    private String LEFT_MOTOR_SPEED = "50";
    private String RIGHT_MOTOR_SPEED = "50";
    private int ROUTER_CONTROL_PORT = 2001;
    private int ROUTER_CONTROL_PORT_TEST = 2001;
    private final String WIFI_SSID_PERFIX = "robot";

    private RelativeLayout relativeLayout;
    private ImageView btnSwitchLight;
    private ImageView ivSignal;
    private ImageView btnScreenRecord;

    private ImageView btnScreenShot;

    private ImageView reset;

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
    private final int PING_WAIT_TIME = 2500;

    private ImageView buttonCus1;
    private ImageView buttonAudio;
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



    /**
     * 通信协议指令集
     */
    private byte[] COM_REQ = {(byte)0xC8};
    private byte[] COMM_STOP = {(byte) 0xFF, 0x01, 0x01, 0x00, 0x00, 0x00, (byte) 0x5A, (byte) 0x88};
    private byte[] COMM_FORWARD = {(byte) 0xFF, 0x01, 0x01, 0x00, 0x01, 0x00, (byte) 0x6B, (byte) 0xBB};
    private byte[] COMM_BACKWARD = {(byte) 0xFF, 0x01, 0x01, 0x00, 0x02, 0x00, (byte) 0x38, (byte) 0xEE};
    private byte[] COMM_LEFT={(byte) 0xFF, 0x01, 0x01, 0x00, 0x03, 0x00, (byte) 0x09, (byte) 0xDD};
    private byte[] COMM_RIGHT={(byte) 0xFF, 0x01, 0x01, 0x00, 0x04, 0x00, (byte) 0x9E, (byte) 0x44};
    private byte[] COMM_GEAR_CONTROL_1 = {(byte) 0xFF, 0x01, 0x01, 0x00, 0x05, 0x00, 0x00, 0x00};
    private byte[] COMM_GEAR_CONTROL_2 = {(byte) 0xFF, 0x01, 0x01, 0x00, 0x06, 0x00, 0x00, 0x00};
    private byte[] COMM_SPEED_CONTROL = {(byte) 0xFF, 0x01, 0x01, 0x00, 0x0D, 0x00, 0x00, 0x00};

    private byte[] COMM_LEN_ON = {(byte) 0xFF, 0x01, 0x01, 0x00, (byte) 0x0F, 0x00, (byte) 0x64, (byte) 0x98};
    private byte[] COMM_LEN_OFF = {(byte) 0xFF, 0x01, 0x01, 0x00, 0x10, (byte) 0x00, 0x29, (byte) 0x8B};

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



    private OnClickListener mOnBtnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                // 录屏
                case R.id.btnScreenRecord:
                    if (recordService.isRunning()) {
                        recordService.stopRecord();
                        Toast.makeText(mContext, "录屏已保存至 " + recordService.getsaveDirectory(), Toast.LENGTH_LONG).show();
                    } else {
                        Intent captureIntent = projectionManager.createScreenCaptureIntent();
                        startActivityForResult(captureIntent, RECORD_REQUEST_CODE);
                    }
                    break;
//                // 截图
//                case R.id.btnScreenShot:
//                    Bitmap bitmap = ksyMediaPlayer.getScreenShot();
//                    String ScreenShotDir = saveImage(bitmap);
//                    Toast.makeText(mContext, "截屏已保存至" + ScreenShotDir, Toast.LENGTH_LONG).show();
//                    break;
                // 大灯
                case R.id.buttonLight:
                    if (!lightStatus) {
                        sendCommand(COMM_LEN_ON);
                        lightStatus = true;
                        btnSwitchLight.setImageDrawable(getDrawable(R.drawable.my_light_on));
                    } else if (lightStatus) {
                        sendCommand(COMM_LEN_OFF);
                        lightStatus = false;
                        btnSwitchLight.setImageDrawable(getDrawable(R.drawable.my_light_off));
                    }
                    break;
                // 设置
                case R.id.ButtonCus:
                    Intent setIntent = new Intent();
                    setIntent.setClass(mContext, Settings.class);
                    startActivity(setIntent);
            }
        }
    };

    private BaiduMap.OnMapClickListener listener = new BaiduMap.OnMapClickListener() {
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





    private IMediaPlayer.OnPreparedListener mOnPreparedListener = new IMediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(IMediaPlayer mp) {

            mVideoWidth = ksyMediaPlayer.getVideoWidth();
            mVideoHeight = ksyMediaPlayer.getVideoHeight();

            // Set Video Scaling Mode
            ksyMediaPlayer.setVideoScalingMode(KSYMediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);

            //start player
            ksyMediaPlayer.start();

            //set progress
            setVideoProgress(0);
            
        }
    };
/*
    private IMediaPlayer.OnBufferingUpdateListener mOnBufferingUpdateListener = new IMediaPlayer.OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(IMediaPlayer mp, int percent) {
            long duration = ksyMediaPlayer.getDuration();
            long progress = duration * percent/100;
            mPlayerSeekbar.setSecondaryProgress((int)progress);
        }
    };

    private IMediaPlayer.OnVideoSizeChangedListener mOnVideoSizeChangeListener = new IMediaPlayer.OnVideoSizeChangedListener() {
        @Override
        public void onVideoSizeChanged(IMediaPlayer mp, int width, int height, int sarNum, int sarDen) {
            if(mVideoWidth > 0 && mVideoHeight > 0) {
                if(width != mVideoWidth || height != mVideoHeight) {
                    mVideoWidth = mp.getVideoWidth();
                    mVideoHeight = mp.getVideoHeight();

                    if(ksyMediaPlayer != null)
                        ksyMediaPlayer.setVideoScalingMode(KSYMediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
                }
            }
        }
    };

    private IMediaPlayer.OnSeekCompleteListener mOnSeekCompletedListener = new IMediaPlayer.OnSeekCompleteListener() {
        @Override
        public void onSeekComplete(IMediaPlayer mp) {
            Log.e(TAG, "onSeekComplete...............");
        }
    };

    private IMediaPlayer.OnCompletionListener mOnCompletionListener = new IMediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(IMediaPlayer mp) {
            Toast.makeText(mContext, "OnCompletionListener, play complete.", Toast.LENGTH_LONG).show();
            videoPlayEnd();
        }
    };

    private IMediaPlayer.OnErrorListener mOnErrorListener = new IMediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(IMediaPlayer mp, int what, int extra) {
            switch (what)
            {
                case KSYMediaPlayer.MEDIA_ERROR_UNKNOWN:
                    Log.e(TAG, "OnErrorListener, Error Unknown:" + what + ",extra:" + extra);
                    break;
                default:
                    Log.e(TAG, "OnErrorListener, Error:" + what + ",extra:" + extra);
            }

            videoPlayEnd();

            return false;
        }
    };

    public IMediaPlayer.OnInfoListener mOnInfoListener = new IMediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(IMediaPlayer iMediaPlayer, int i, int i1) {
            switch (i) {
                case KSYMediaPlayer.MEDIA_INFO_BUFFERING_START:
                    Log.d(TAG, "Buffering Start.");
                    break;
                case KSYMediaPlayer.MEDIA_INFO_BUFFERING_END:
                    Log.d(TAG, "Buffering End.");
                    break;
                case KSYMediaPlayer.MEDIA_INFO_AUDIO_RENDERING_START:
                    Toast.makeText(mContext, "Audio Rendering Start", Toast.LENGTH_SHORT).show();
                    break;
                case KSYMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                    Toast.makeText(mContext, "Video Rendering Start", Toast.LENGTH_SHORT).show();
                    break;
                case KSYMediaPlayer.MEDIA_INFO_SUGGEST_RELOAD:
                    // Player find a new stream(video or audio), and we could reload the video.
                    if(ksyMediaPlayer != null)
                        ksyMediaPlayer.reload(mDataSource, false);
                    break;
            }
            return false;
        }
    };
*/
    private View.OnClickListener mVideoScaleButton = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int mode = mVideoScaleIndex;
            mVideoScaleIndex = (mVideoScaleIndex == 1) ? 0: 1;
            if(ksyMediaPlayer != null) {
                if(mode == 1)
                    ksyMediaPlayer.setVideoScalingMode(KSYMediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
                else
                    ksyMediaPlayer.setVideoScalingMode(KSYMediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT);
            }
        }
    };

    public void start() {
        Bundle savedInstanceState = new Bundle();
        savedInstanceState.putString("url", "rtmp://live.hkstv.hk.lxdns.com/live/hks");
        onCreate(savedInstanceState);
        buttonResetClickListener.onClick(null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);







        //Main.java

        projectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);

        /**
         * 权限检查
         */
        if (ContextCompat.checkSelfPermission(VideoPlayerActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_REQUEST_CODE);
        }

        if (ContextCompat.checkSelfPermission(VideoPlayerActivity.this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.RECORD_AUDIO}, AUDIO_REQUEST_CODE);
        }

        Intent intent = new Intent(this, RecordService.class);
        bindService(intent, connection, BIND_AUTO_CREATE);




        mContext = this.getApplicationContext();
        initSettings();
        mHandler = new MyHandler();
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//Hide title
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.qianyi_player);
        initMap();

        buttonCus1= findViewById(R.id.ButtonCus);
        buttonCus1.setOnClickListener(mOnBtnClickListener);

        buttonAudio= findViewById(R.id.btnAudio);
        buttonAudio.setOnClickListener(buttonAudioClickListener);

        btnScreenRecord = findViewById(R.id.btnScreenRecord);
        btnScreenRecord.setOnClickListener(mOnBtnClickListener);

        btnScreenShot = findViewById(R.id.btnScreenShot);
        btnScreenShot.setOnClickListener(mOnBtnClickListener);//buttonTakePicClickListener

        reset = findViewById(R.id.reset);
        reset.setOnClickListener(buttonResetClickListener);

        btnSwitchLight = findViewById(R.id.buttonLight);
        btnSwitchLight.setOnClickListener(mOnBtnClickListener);

        ivSignal = findViewById(R.id.viewSignal);


        mWarningIcon = getResources().getDrawable(R.drawable.sym_indicator1);

        buttonLenon = getResources().getDrawable(R.drawable.sym_light);
        buttonLenoff = getResources().getDrawable(R.drawable.sym_light_off);

        backgroundView = findViewById(R.id.player_surface);
        rockView = findViewById(R.id.view2);

        mLogText = findViewById(R.id.logTextView);
        mLogLeft = findViewById(R.id.log_left);
        mLogRight = findViewById(R.id.log_right);
        mLogVertical = findViewById(R.id.log_vertical);
        mLogHorizontal = findViewById(R.id.log_horizontal);

        if (null != mLogText) {
            mLogText.setBackgroundColor(Color.argb(0, 0, 255, 0));//0~255 alpha value
            mLogText.setTextColor(Color.argb(172, 0, 255, 0));
        }

        //Create gesture detector
        detector = new GestureDetector(this,this);

//        mSeekBar = (SeekBar) findViewById(R.id.gear1);
//        mSeekBar.setMax(MAX_SPEED_VALUE);
//        mSeekBar.setProgress(INIT_SPEED_VALUE);
//        mSeekBar.setOnSeekBarChangeListener(this);
//        mSeekBar1 = (SeekBar) findViewById(R.id.gear2);
//        mSeekBar1.setMax(MAX_SPEED_VALUE);
//        mSeekBar1.setProgress(INIT_SPEED_VALUE);
//        mSeekBar1.setOnSeekBarChangeListener(this);

        buttonAudio.setKeepScreenOn(true);

        data2Location(gpsData);

        connectToRouter(m4test);
        mThreadReceive = new Thread(mRunReceive);
        mThreadReceive.start();







        //VideoPlayerActivity.java

//        mContext = this.getApplicationContext();
        useHwCodec = true;

//        setContentView(R.layout.qianyi_player);

//        backgroundView = (MjpegView) findViewById(R.id.player_surface);
//        backgroundView.setZOrderMediaOverlay(ture);
        mSurfaceHolder = backgroundView.getHolder();
        mSurfaceHolder.addCallback(mSurfaceCallback);

        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        detector = new GestureDetector(this,this);
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);

        mQosThread = new QosThread(activityManager, mHandler);

        mDataSource = CAMERA_VIDEO_URL;

        ksyMediaPlayer = new KSYMediaPlayer.Builder(mContext).build();
        ksyMediaPlayer.setOnPreparedListener(mOnPreparedListener);
        ksyMediaPlayer.setScreenOnWhilePlaying(true);
        ksyMediaPlayer.setBufferTimeMax(1);

        if (useHwCodec) {
            //硬解264&265
            ksyMediaPlayer.setCodecFlag(KSYMediaPlayer.KSY_USE_MEDIACODEC_ALL);
        }
        try {
            ksyMediaPlayer.setDataSource(mDataSource);
            ksyMediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        mMapView.onPause();//百度地图

        if(ksyMediaPlayer != null)
        {
            ksyMediaPlayer.pause();
            mPause = true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        backgroundView.resumePlayback();

        mMapView.onResume();//百度地图

        initSettings();//回到此Activity时重新获取控制ip
//        backgroundView.setSource(CAMERA_VIDEO_URL);// Init camera

        if(ksyMediaPlayer != null)
        {
            ksyMediaPlayer.start();
            mPause = false;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            videoPlayEnd();
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public int getChangingConfigurations() {
        return super.getChangingConfigurations();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private void scaleVideoView()
    {
        if(ksyMediaPlayer == null || ksyMediaPlayer.getVideoHeight() <= 0 || backgroundView == null)
            return;

        WindowManager wm = this.getWindowManager();
        int sw = wm.getDefaultDisplay().getWidth();
        int sh = wm.getDefaultDisplay().getHeight();
        int videoWidth = mVideoWidth;
        int videoHeight = mVideoHeight;
        int visibleWidth = 0;
        int visibleHeight = 0;

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
        {
            visibleWidth = sw > sh ? sh : sw;
            visibleHeight = (int) Math.ceil(visibleWidth * videoHeight / videoWidth);
        }
        else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {

            if(videoHeight*sw > videoWidth*sh)
            {
                visibleHeight = sh;
                visibleWidth = (int) Math.ceil(videoWidth * visibleHeight / videoHeight);
            }
            else
            {
                visibleWidth = sw;
                visibleHeight = (int) Math.ceil(visibleWidth * videoHeight / videoWidth);
            }
        }

        LayoutParams lp = backgroundView.getLayoutParams();
        lp.width = visibleWidth;
        lp.height = visibleHeight;
        backgroundView.setLayoutParams(lp);

        backgroundView.invalidate();
    }

    //The touch event on the GestureDetector to activity processing
    public boolean onTouchEvent(MotionEvent event){

        return detector.onTouchEvent(event);
    }

    public int setVideoProgress(int currentProgress) {

        if(ksyMediaPlayer == null)
            return -1;

        long time = currentProgress > 0 ? currentProgress : ksyMediaPlayer.getCurrentPosition();
        long length = ksyMediaPlayer.getDuration();

        return (int)time;
    }

    private void videoPlayEnd() {
        if(ksyMediaPlayer != null)
        {
            ksyMediaPlayer.release();
            ksyMediaPlayer = null;
        }

        if(mQosThread != null) {
            mQosThread.stopThread();
            mQosThread = null;
        }

        mHandler = null;

        finish();
    }

/*
 *
 * Audio control
 *
 */
    private void openPlayer() {
    	mPlayer = new VolPlayer();
        mPlayer.init();
        mPlayer.start();
    }

    private void closePlayer() {
        mPlayer.free();
        mPlayer = null;
    }

    private void openRecorder() {
        mRecorder = new VolRecorder();
        mRecorder.init();
        mRecorder.start();
    }

    private void closeRecorder() {
        mRecorder.free();
        mRecorder = null;
    }
//
//    private void showPopupWindow() {
//    	View contentView = LayoutInflater.from(mContext)
//				.inflate(R.layout.dialog_record, null);
//		final PopupWindow popupWindow = new PopupWindow(
//				contentView, LayoutParams.WRAP_CONTENT,
//				LayoutParams.WRAP_CONTENT, true);
//		popupWindow.setTouchable(true);
//		popupWindow.setTouchInterceptor(new OnTouchListener() {
//
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                popupWindow.dismiss();
//                closeRecorder();
//                closePlayer();
//                Toast.makeText(mContext, "++ Recorder cancel ++", Toast.LENGTH_SHORT).show();
//                return false;
//            }
//        });
//		popupWindow.setBackgroundDrawable(getResources().getDrawable(
//                R.drawable.record_bg));
//		popupWindow.showAtLocation(findViewById(R.id.root),Gravity.CENTER,0,0);
//    }

	
	public void onLongPress(MotionEvent arg0) {
		showPopupWindow();
		openRecorder();
		openPlayer();
		Toast.makeText(mContext, "++ Start recorder ++", Toast.LENGTH_SHORT).show();
	}
   
    private final SurfaceHolder.Callback mSurfaceCallback = new Callback() {
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            if(ksyMediaPlayer != null && ksyMediaPlayer.isPlaying())
                ksyMediaPlayer.setVideoScalingMode(KSYMediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            if(ksyMediaPlayer != null)
                ksyMediaPlayer.setDisplay(holder);
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            Log.d(TAG, "surfaceDestroyed");
            if(ksyMediaPlayer != null) {
                ksyMediaPlayer.setDisplay(null);
            }
        }
    };


















    //Main.java

    private void data2Location(GPSData gpsData) {
        LatLng point;
        if (true)
        {
            isFristLocation = false;
            double lon = 118.7904930000;
            double lat = 31.9229410000;
            point = new LatLng(lat, lon);
        }
        else {
            LatLng temp = new LatLng(gpsData.getLongitude(), gpsData.getLatitude());
            Log.d("Origin Location", temp.latitude + " " + temp.longitude);

            // 将GPS设备采集的原始GPS坐标转换成百度坐标
            CoordinateConverter converter  = new CoordinateConverter();
            converter.from(CoordinateConverter.CoordType.COMMON);
            converter.coord(temp);
            point = converter.convert();

//            GPSData temp = GPSDataUtils.wgs84_to_bd09(point1.latitude, point1.longitude);
//            point = new LatLng(temp.getLatitude(), temp.getLongitude());
        }
        //构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.navi_map_gps_locked);
        //构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions().position(point).icon(bitmap);
        //删除之前的标记
        mBaiduMap.clear();
        //在地图上添加Marker，并显示
        mBaiduMap.addOverlay(option);
        //将地图设置到指定的中心点和缩放
        mBaiduMap.setMapStatus(
            MapStatusUpdateFactory.newMapStatus(
                new MapStatus.Builder().target(point).zoom(20).build()
            )
        );

        Log.d("Marker Location", "lat:" + String.valueOf(point.latitude) + " lon:" + String.valueOf(point.longitude));
    }


    private void initMap() {

        mMapView = (MapView) findViewById(R.id.id_bmapView);
        // 获得地图的实例
        mBaiduMap = mMapView.getMap();
        mMapView.setZOrderMediaOverlay(true);

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
//                closePlayer();
                buttonAudio.setImageDrawable(buttonLenoff);
                buttonAudio.invalidateDrawable(buttonLenon);
//                sendRequestWithHttpClient("http://ipaddress:20001/command.jsp?cmdInt=22");
            }
            else  {
                bAudon = true;
//                openPlayer();
                buttonAudio.setImageDrawable(buttonLenon);
                buttonAudio.invalidateDrawable(buttonLenon);
//                sendRequestWithHttpClient("http://ipaddress:20001/command.jsp?cmdInt=21");
            }
        }
    };

    private OnClickListener buttonResetClickListener = new OnClickListener() {
    	public void onClick(View arg0) {

    		// Initial vertical direction angle 40
            COMM_GEAR_CONTROL_1[5] = (byte) 40;
            COMM_GEAR_CONTROL_1[4] = 0x05;
            COMM_GEAR_CONTROL_1 = com_protocol(COMM_GEAR_CONTROL_1);
            sendCommand(COMM_GEAR_CONTROL_1);
            mLogHorizontal.setText("horizontal angle: 90°");

    		// Initial horizontal direction angle 90
            COMM_GEAR_CONTROL_2[5] = (byte) 90;
            COMM_GEAR_CONTROL_2[4] = 0x06;
            COMM_GEAR_CONTROL_2 = com_protocol(COMM_GEAR_CONTROL_2);
            sendCommand(COMM_GEAR_CONTROL_2);
            mLogVertical.setText("vertical angle: 40°");
    		mLogText.setText("Already reset cloud terrace!!");
    		Toast.makeText(mContext, "++Reset cloud terrace++", Toast.LENGTH_SHORT).show();
        }
    };

//    private OnClickListener buttonIndicatorListener = new OnClickListener() {
//        public void onClick(View arg0) {
//            if (null != backgroundView) {
//            	backgroundView.saveBitmap();
//            }
//        }
//    };

//    private OnClickListener buttonTakePicClickListener = new OnClickListener() {
//        public void onClick(View arg0) {
//            String cameraUrl = null;
//            if (m4test) {
//                cameraUrl = CAMERA_VIDEO_URL_TEST;
//            } else {
//                cameraUrl = CAMERA_VIDEO_URL;
//            }
//            if (null != cameraUrl && cameraUrl.length() > 4) {
//                Toast.makeText(mContext, "++Video in++", Toast.LENGTH_SHORT).show();
//                backgroundView.setSource(cameraUrl);// Init camera
//            }
//        }
//    };


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
            Log.i("SEND", HexUtils.encodeHexStr(data));
        } catch (Exception e) {
            Log.i("SEND", e.getMessage() != null ? e.getMessage().toString() : "sendCommand error!");
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
            String clientUrl = ROUTER_CONTROL_IP;
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




//    //用HttpClient发送请求
//    private void sendRequestWithHttpClient(final String url) {
//        new Thread(new Runnable() {
//
//            @Override
//            public void run() {
//                //用HttpClient发送请求，分为五步
//                //第一步：创建HttpClient对象
//                HttpClient httpCient = new DefaultHttpClient();
//                Log.i("URL", url + "  First step succeed");
//                //第二步：创建代表请求的对象,参数是访问的服务器地址
//                HttpGet httpGet = new HttpGet(url);
//                Log.i("URL", "Second step succeed");
//
//                try {
//                    //第三步：执行请求，获取服务器发还的相应对象
//                    HttpResponse httpResponse = httpCient.execute(httpGet);
//                    //第四步：检查相应的状态是否正常：检查状态码的值是200表示正常
//                    if (httpResponse.getStatusLine().getStatusCode() == 200) {
//                        Log.i("URL", "URL get succeed" + url);
//                    }
//                    else {
//                        Log.i("URL", "URL get failed" + url);
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    Log.i("URL", "URL get Exception!" + url);
//                }
//            }
//        }).start();
//    }




    private Runnable mRunReceive = new Runnable()
    {
        public void run()
        {
            Log.d("MyHandler", "msg received");
            Message msg = new Message();
            msg.what = MSG_SUCCESS;
            mHandler.sendMessage(msg);
            byte[] frame = new byte[30];
            Log.d("MyHandler", frame.toString());
                while(mThreadFlag) {
                    Log.d("MyHandler", "looped again");
                    try {
                        if(flag) {
                            int j = 0;
                            byte[] recMsg = mtcpSocket.receiveMsg();
                            Log.i("RECEIVED", HexUtils.encodeHexStr(recMsg));

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
                            Log.d("Origin GPS", HexUtils.encodeHexStr(dataField1));
                            gpsData = utils.protocol2value(dataField1);
                            Message msgData = new Message();
                            msgData.what = MSG_OK;
                            Bundle b = new Bundle();
                            b.putDouble("longitude", gpsData.getLongitude());
                            b.putDouble("latitude", gpsData.getLatitude());
                            b.putDouble("heading", gpsData.getHeading());
                            b.putDouble("speed", gpsData.getSpeed());
                            b.putInt("online", gpsData.getOnline());
                            b.putInt("visible", gpsData.getVisible());
                            msgData.setData(b);
                            mHandler.sendMessage(msgData);
//                    }

                        }
                    }catch (Exception e) {
                        e.printStackTrace();
                        Log.i("GPSdata", "error");
                        Log.d("GPSdata", "error");
                    }
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
                is = new BufferedInputStream(mtcpSocket.getInputStream());
            } catch (Exception e) {
                Message msg = new Message();
                msg.what = MSG_ID_ERR_INIT_READ;
                mHandler.sendMessage(msg);
                return;
            }
            int type = 0;
            while(true) {
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
                                Log.i("Main", "FORWARD++");
                                break;
                            case 2:
                                sendCommand(COMM_BACKWARD);
                                Log.i("Main", "BACKWARD++");
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
                btnScreenShot.setAlpha(255);
                if (isIconAnimationEnabled()) {
                    mAnimationHandler.sendEmptyMessageDelayed(0, WARNING_ICON_ON_DURATION_MSEC);
                }
            } else {
                btnScreenShot.setAlpha(0);
                if (isIconAnimationEnabled()) {
                    mAnimationHandler.sendEmptyMessageDelayed(0, WARNING_ICON_OFF_DURATION_MSEC);
                }
            }
            mIconAnimationState = !mIconAnimationState;
            btnScreenShot.invalidateDrawable(mWarningIcon);
        }
    };

    private void startIconAnimation() {
        Log.i("Animation", "startIconAnimation handler : " + mAnimationHandler);
        if (btnScreenShot != null) {
            btnScreenShot.setImageDrawable(mWarningIcon);
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

        mMapView.onDestroy();//百度地图

        unbindService(connection);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RECORD_REQUEST_CODE && resultCode == RESULT_OK) {
            mediaProjection = projectionManager.getMediaProjection(resultCode, data);
            recordService.setMediaProject(mediaProjection);
            recordService.startRecord();
            Toast.makeText(mContext, "record started", Toast.LENGTH_SHORT);
        }
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, final int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == STORAGE_REQUEST_CODE || requestCode == AUDIO_REQUEST_CODE) {
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
//                        finish();
//                    }
//                }
//            }, 1000);
//        }
//    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            RecordService.RecordBinder binder = (RecordService.RecordBinder) service;
            recordService = binder.getRecordService();
            recordService.setConfig(metrics.widthPixels, metrics.heightPixels, metrics.densityDpi);
//            startBtn.setText(recordService.isRunning() ? R.string.stop_record : R.string.start_record);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {}
    };

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


        String LeftMotorSpeed = settings.getString(Constant.PREF_KEY_LEFT_MOTOR_SPEED, Constant.DEFAULT_PREF_KEY_LEFT_MOTOR_SPEED);
        LEFT_MOTOR_SPEED = LeftMotorSpeed;
        String RightMotorSpeed = settings.getString(Constant.PREF_KEY_RIGHT_MOTOR_SPEED, Constant.DEFAULT_PREF_KEY_RIGHT_MOTOR_SPEED);
        RIGHT_MOTOR_SPEED = RightMotorSpeed;


        String RouterUrl = settings.getString(Constant.PREF_KEY_ROUTER_URL, Constant.DEFAULT_VALUE_ROUTER_URL);

        int index = RouterUrl.indexOf(":");
        String routerIP = "";
        String routerPort = "";
        int port = 0;
        if (index > 0) {
            routerIP = RouterUrl.substring(0, index);
            routerPort = RouterUrl.substring(index+1, RouterUrl.length());
            port = Integer.parseInt(routerPort);
        }

        ROUTER_CONTROL_IP = routerIP;
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


//        sendRequestWithHttpClient("http://" + ROUTER_CONTROL_IP + ":" + ROUTER_CONTROL_PORT + "/gear.jsp?pos=LEFT_SPEED&para=" + LEFT_MOTOR_SPEED);
//        sendRequestWithHttpClient("http://" + ROUTER_CONTROL_IP + ":" + ROUTER_CONTROL_PORT + "/gear.jsp?pos=RIGHT_SPEED&para=" + RIGHT_MOTOR_SPEED);


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

    public boolean onDown(MotionEvent arg0) {
        return false;
    }

    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                           float velocityY) {

        return false;
    }

//    public void onLongPress(MotionEvent e) {
//        //showPopupWindow();
//        //openRecorder();
//        //Toast.makeText(mContext, "++ Start recorder ++", Toast.LENGTH_SHORT).show();
//    }


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
            Value_X = Value_X - 4;
            COMM_GEAR_CONTROL_1[5] = (byte) Value_X;
            COMM_GEAR_CONTROL_1[4] = 0x05;
            COMM_GEAR_CONTROL_1 = com_protocol(COMM_GEAR_CONTROL_1);
            sendCommand(COMM_GEAR_CONTROL_1);
            mLogText.setText("horizontal angle: " + Value_X);
            mLogHorizontal.setText("horizontal angle: " + Value_X + "°");
        }
        else if (distanceX <= -10) {
            Value_X = Value_X + 4;
            COMM_GEAR_CONTROL_1[5] = (byte) Value_X;
            COMM_GEAR_CONTROL_1[4] = 0x05;
            COMM_GEAR_CONTROL_1 = com_protocol(COMM_GEAR_CONTROL_1);
            sendCommand(COMM_GEAR_CONTROL_1);
            mLogText.setText("horizontal distance: "+ Value_X +" ");
            mLogHorizontal.setText("horizontal angle: " + Value_X + "°");
        }
        if (distanceY <= -10) {
            Value_Y = Value_Y + 4;
            COMM_GEAR_CONTROL_2[5] = (byte) Value_Y;
            COMM_GEAR_CONTROL_2[4] = 0x06;
            COMM_GEAR_CONTROL_2 = com_protocol(COMM_GEAR_CONTROL_2);
            sendCommand(COMM_GEAR_CONTROL_2);
            mLogText.setText("vertical angle: " + Value_Y + " ");
            mLogVertical.setText("vertical angle: " + Value_Y + "°");
        }
        else if (distanceY >= 10) {
            Value_Y = Value_Y - 4;
            COMM_GEAR_CONTROL_2[5] = (byte) Value_Y;
            COMM_GEAR_CONTROL_2[4] = 0x06;
            COMM_GEAR_CONTROL_2 = com_protocol(COMM_GEAR_CONTROL_2);
            sendCommand(COMM_GEAR_CONTROL_2);
            mLogText.setText("vertical angle: " + Value_Y + " ");
            mLogVertical.setText("vertical angle: " + Value_Y + "°");
        }
        return true;
    }

    public void onShowPress(MotionEvent e) {

    }

    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

//    @Override
//    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//		  switch(seekBar.getId()) {
//		  case R.id.gear1:
//              mLogText.setText("change left speed: " + progress);
//              mLogLeft.setText("speed →: " + progress + "cm/s");
//              mSeekBarValue = progress;
//              Value = progress;
//              sendRequestWithHttpClient("http://" + ROUTER_CONTROL_IP + ":" + ROUTER_CONTROL_PORT + "/gear.jsp?pos=LEFT_SPEED&para=" + Value);
//              break;
//	      case R.id.gear2:
//              mLogText.setText("change right speed: " + progress);
//              mLogRight.setText("speed →: " + progress + "cm/s");
//              mSeekBarValue = progress;
//              Value = progress;
//              sendRequestWithHttpClient("http://" + ROUTER_CONTROL_IP + ":" + ROUTER_CONTROL_PORT + "/gear.jsp?pos=RIGHT_SPEED&para=" + Value);
//              break;
//	        }
//    }


//    @Override
//    public void onStartTrackingTouch(SeekBar arg0) {
//        // TODO Auto-generated method stub
//
//    }
//g
//    @Override
//    public void onStopTrackingTouch(SeekBar arg0) {
//        // TODO Auto-generated method stub
//
//    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
//        if (key.equals(getString(R.string.pref_show_bass_key))) {
//            mVisualizerView.setShowBass(sharedPreferences.getBoolean(key, getResources().getBoolean(R.bool.pref_show_bass_default)));
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
                ivSignal.setImageResource(R.drawable.my_signal_off);
                i++;
                break;
            case MSG_QUIT:
                mQuitFlag = false;
                Log.d("MyHandler", "handleMessage quit");
                ivSignal.setImageResource(R.drawable.my_signal_off);
                break;
            case MSG_SUCCESS:
                Log.d("MyHandler", "handleMessage success");
                ivSignal.setImageResource(R.drawable.my_signal_off);
//	    		textView.setText("connected");
                break;
            case MSG_OK:
                Log.d("MyHandler", "handleMessage OK");
                ivSignal.setImageResource(R.drawable.my_signal_on);
                Bundle b = msg.getData();
                Double longitude = b.getDouble("longitude");
                Double latitude = b.getDouble("latitude");
                Double heading = b.getDouble("heading");
                Double speed = b.getDouble("speed");
                Log.d("GPS", latitude.toString() + " " + longitude);
                int online = b.getInt("online");
                int visible = b.getInt("visible");
                String longitudeData = "longitude: " + longitude;
                String latitudeData = "latitude: " + latitude;
                String speedData = "speed: " + speed;
                String onlineData = "online: " + online;
                data2Location(gpsData);
                mLogText.setText(longitudeData + latitudeData + speedData + onlineData);
            default :
                break;
        }
        super.handleMessage(msg);
    }
}

    public static String saveImage(Bitmap bmp) {
        File appDir = new File(Environment.getExternalStorageDirectory(), "ScreenShot");
        String ScreenShotDir = Environment.getExternalStorageDirectory().toString() + "/ScreenShot";
        Log.i("ScreenShot", ScreenShotDir);
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ScreenShotDir;
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

