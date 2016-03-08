package org.hhu.streaming;

import java.io.*;
import java.net.*;

public class SocketClient {
	static Socket client = null;

	public SocketClient(String site, int port) {
		try {
			while(client==null){
			client = new Socket(site, port);
			// System.out.println("Client is created! site:"+site+" port:"+port);
			
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
	
	public static void main(String[] args) throws Exception {

	}

}