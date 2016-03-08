package org.hhu.streaming;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

public class VolPlayer extends Thread { 
	private AudioTrack out_trk ;			//定义播放音频对象
	private int        out_buf_size ;		//定义数组能够容纳的字节数
	private byte []    out_bytes ;			//传输字节数组
	private boolean    keep_running ;		//正常传输标志
	private Socket ss;
	private DataInputStream din;
	public void init() {
		try {
			keep_running = true ;
			out_buf_size = AudioTrack.getMinBufferSize( 8000,
					AudioFormat.CHANNEL_OUT_MONO,
					AudioFormat.ENCODING_PCM_16BIT);
			out_trk = new AudioTrack(AudioManager.STREAM_MUSIC, 8000,
					AudioFormat.CHANNEL_OUT_MONO,
					AudioFormat.ENCODING_PCM_16BIT,
					out_buf_size  ,
					AudioTrack.MODE_STREAM);
			out_bytes=new byte[512];
			//1114
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void free()
	{
		keep_running = false ;
		try {
			Thread.sleep(100) ;
		} catch(Exception e) {
			Log.d("sleep exceptions...\n","") ;
		}
	}

	public void run()
	{
		try
		{
			while(ss == null)
			{
				ss=new Socket("192.168.43.150", 15636);
			}
			din=new DataInputStream(ss.getInputStream());
			byte [] bytes_pkg = null ;
			out_trk.play() ;
			int length_p;
			while(keep_running) {
				try {
					length_p = din.read(out_bytes);
					bytes_pkg = out_bytes.clone();
					out_trk.write(bytes_pkg, 0, length_p);
				} catch(Exception e) {
					e.printStackTrace();
				}

		}
		out_trk.stop() ;
		out_trk = null ;
		try {
			din.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}catch(Exception e){}
	}
}


