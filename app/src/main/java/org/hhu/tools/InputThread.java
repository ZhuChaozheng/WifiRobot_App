package org.hhu.tools;

import android.util.Log;

public class InputThread implements Runnable {
	SocketClient minputSocket;
	private boolean loopFlag = true;
	@Override
	public void run() {
		minputSocket = new SocketClient("192.168.1.101", 13456);
			try {
				byte[] reBytes = new byte[100];
				while(loopFlag) {
					reBytes = receiveCommand();
					Thread.sleep(500);
					for(byte a : reBytes)
					Log.d("msgInput", "" + a);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	}
	
	private byte[] receiveCommand() {
		
		return(minputSocket.receiveMsg());
		
	}

}
