package org.hhu.surface;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import org.hhu.surface.R;
import org.hhu.tools.VolPlayer;
import org.hhu.tools.VolRecorder;

public class Monitor extends Activity implements OnGestureListener{
	private final int MSG_HELL = 1003;
	private final int MSG_QUIT = 1004;
	private final int QUIT_INTERVAL = 2500;//ms
	private MjpegView backgroundView = null;
	protected VolPlayer mPlayer;
	protected VolRecorder mRecorder;
    private Context mContext;
    private GestureDetector detector;
    public TextView textView;
    private MyHandler myHandler;
    private boolean mQuitFlag = false;
    
    private int Value_X = 90;
    private int Value_Y = 90;
    
//    private String CAM_VIDEO_URL = "http://video.ngrok.aichimantou.com:8080/?action=stream";
    private String CAM_VIDEO_URL = "http://192.168.1.154:8080/?action=stream";
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	mContext = this;
    	this.requestWindowFeature(Window.FEATURE_NO_TITLE);//Hide title
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        initSetting();
     	openVideo();
        myHandler = new MyHandler();
        MyThread m = new MyThread(); 
        new Thread(m).start();
    }
    
    private void initSetting() {
    	//Create gesture detector
        detector = new GestureDetector(this,this);
        textView = (TextView) findViewById(R.id.mySurfaceView1);
        textView.setBackgroundColor(Color.argb(0, 0, 255, 0));//0~255 alpha value
        textView.setTextColor(Color.argb(172, 0, 255, 0));
        backgroundView = (MjpegView) findViewById(R.id.mySurfaceView1);
    }
    
    private void openVideo() {
    	Toast.makeText(mContext, "++Video in++", Toast.LENGTH_SHORT).show();
    	backgroundView.setSource(CAM_VIDEO_URL);// Init camera
    }
    
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
                closePlayer();
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
    
	@Override
	public boolean onDown(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		return false;
	}
	@Override
	public void onLongPress(MotionEvent arg0) {
		showPopupWindow();
		openRecorder();
		openPlayer();
		Toast.makeText(mContext, "++ Start recorder ++", Toast.LENGTH_SHORT).show();
	}
	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		if (distanceX >= 10) {
			Value_X = Value_X + 2;
			textView.setText("horizontal angle: " + Value_X);
		}
		else if (distanceX <= -10){
			Value_X = Value_X - 2;
			textView.setText("horizontal angle: " + Value_X);
		}
		else if (distanceY >= 10) {
			Value_Y = Value_Y + 2;
			textView.setText("vertical angle: " + Value_Y);
		}
		else if (distanceY <= -10){
			Value_Y = Value_Y - 2;
			textView.setText("vertical angle: " + Value_Y);
		}
		return false;
	}
	@Override
	public void onShowPress(MotionEvent arg0) {
		
	}
	@Override
	public boolean onSingleTapUp(MotionEvent arg0) {
		return false;
	}
	
	@Override
    protected void onResume() {
        backgroundView.resumePlayback();
        super.onResume();
    }
	
	public void onBackPressed() {
        if (mQuitFlag) {
        	finish();
        } else {
            mQuitFlag = true;
            Toast.makeText(mContext, "++Please press again to exit++", Toast.LENGTH_SHORT).show();
            Message msg = new Message();
            msg.what = MSG_QUIT;
            myHandler.sendMessageDelayed(msg, QUIT_INTERVAL);
        }
    }
	
	class MyHandler extends Handler {
		String data;
		int i = 0;
	    public MyHandler() { 
	    } 

	    public MyHandler(Looper L) { 
	        super(L); 
	    } 

	    // 子类必须重写此方法，接受数据 
	    @Override 
	    public void handleMessage(Message msg) { 
	    	switch(msg.what) {
	    	case MSG_HELL:
	    		data = "" + i;
	    		Log.d("MyHandler", "handleMessage。。。。。。"); 
	            super.handleMessage(msg); 
	            Monitor.this.textView.setText(data);
	            i++;
	    		break;
	    	case MSG_QUIT:
	             mQuitFlag = false;
	             break;
	    	default :
	             break;
	    	}
	    } 
	} 
	class MyThread implements Runnable { 
	    public void run() { 
	        try { 
	            Thread.sleep(1000); 
	            Log.d("thread。。。。。。。", "mThread。。。。。。。。"); 
	            for(int i = 0; i < 10; i++){
	            	Message msg = new Message(); 
	                msg.what = MSG_HELL;
	                Monitor.this.myHandler.sendMessage(msg); // 向Handler发送消息，更新UI
	                Thread.sleep(1000);
	            }
	        } catch (InterruptedException e) { 
	            e.printStackTrace(); 
	        } 

	        
	    } 
	}
}




