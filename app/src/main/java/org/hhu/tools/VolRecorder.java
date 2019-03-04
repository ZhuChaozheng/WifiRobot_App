package org.hhu.tools;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.media.audiofx.AcousticEchoCanceler;
import android.util.Log;

public class VolRecorder extends Thread
{
 
    protected AudioRecord record ;
    protected int         in_buf_size ;
    protected byte []     in_bytes ;
    protected boolean     keep_running ;
    protected Socket      socket;
    protected DataOutputStream dout;
    protected LinkedList<byte[]>  m_in_q ;
    protected ServerSocket ss;
    protected AcousticEchoCanceler canceler;
    protected int audioSession;
    public boolean isDeviceSupport()
    {
    	return AcousticEchoCanceler.isAvailable();
    }
    
    public boolean initAEC(int audioSession)
    {
    	if(canceler != null)
    	{
    		return false;
    	}
    	canceler = AcousticEchoCanceler.create(audioSession);
    	canceler.setEnabled(true);
    	return canceler.getEnabled();
    }
    
    public boolean setAECEnabled(boolean enable)
    {
    	if(canceler == null)
    	{
    		return false;
    	}
    	canceler.setEnabled(enable);
    	return canceler.getEnabled();
    }
    
    public boolean release()
    {
    	if(canceler == null)
    	{
    		return false;
    	}
    	canceler.setEnabled(false);
    	canceler.release();
    	return true;
    }
    
    public void init()
    {
    	in_buf_size = AudioRecord.getMinBufferSize(8000, 
    			AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
    	if (isDeviceSupport()) {
    		record = new AudioRecord( MediaRecorder.AudioSource.VOICE_COMMUNICATION, 8000, 
        			AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, 
        			in_buf_size);
    	}
    	else {
    		record = new AudioRecord(MediaRecorder.AudioSource.MIC, 8000, AudioFormat.CHANNEL_IN_MONO, 
    				AudioFormat.ENCODING_PCM_16BIT, in_buf_size);
    	}
    	audioSession = record.getAudioSessionId();
    	//initAEC(audioSession);
    	AcousticEchoCanceler.create(audioSession);
    	in_bytes = new byte[512];
    	//2048
    	keep_running = true;
    	m_in_q = new LinkedList<byte[]>();
    }
  
    public void run ()
    {
      try
      {
    	 /*
    	 ss = new ServerSocket(15636);
     	 Socket s = ss.accept();
     	 */
    	 while(socket == null)
 		 {
 			socket = new Socket("192.168.43.110", 2223);
    //		socket = new Socket("ngrok.aichimantou.com", 34565);
 		 }
      	 dout=new DataOutputStream(socket.getOutputStream());
           byte [] bytes_pkg ;	
              record.startRecording() ; 
             int length;
             while(keep_running) {
                 length = record.read(in_bytes, 0, 512);
                 dout.write(in_bytes, 0, length);
                 sleep(40);
                 /*
                 bytes_pkg = in_bytes.clone() ;
                 if(m_in_q.size() >= 2)
                 {
                    dout.write(m_in_q.removeFirst() , 0, m_in_q.removeFirst() .length);
                 }
                    m_in_q.add(bytes_pkg) ;
                    */
             }
             record.stop() ;
             record = null ;
             in_bytes = null ;
             dout.close();
      }
      catch(Exception e)
      {
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
}

