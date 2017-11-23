package org.hhu.streaming;

import java.io.*;
import java.net.*;

import android.util.Log;

public class SocketClient {
	public static Socket client = null;
	private int inLength;
	public static byte[] recData = new byte[1024];
	public SocketClient(String site, int port) {
		try {
			while(client==null){
				client = new Socket(site, port);
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendMsg(byte[] msg) {
		try {
			OutputStream out = client.getOutputStream();
			out.write(msg);
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public byte[] receiveMsg() {
		try {
			
			InputStream in = client.getInputStream();
			inLength = in.read(recData);
			byte[] copyData = new byte[inLength];
			System.arraycopy(recData, 0, copyData, 0, inLength);
			Log.d("socketclient", "" + inLength);
			return copyData;
		}catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}


	public void closeSocket() {
		try {
			client.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public InputStream getInputStream () {
		if (client != null) {
			try {
				return client.getInputStream();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public OutputStream getOutputStream () {
		if (client != null) {
			try {
				return client.getOutputStream();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
}