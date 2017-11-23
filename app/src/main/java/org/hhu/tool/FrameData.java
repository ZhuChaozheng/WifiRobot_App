package org.hhu.tool;

public class FrameData {
	public byte frameHead;
	public byte addressBit;
	public byte lengthLow;
	public byte lengthHigh;
	public byte commandBit;
	public byte[] dataField;
	public byte crcLow;
	public byte crcHigh;
	public void jiaoyanshuju(byte[] recMsg, int i) {
		if (recMsg[i] == (byte)0xff) {
			i ++;
			if (recMsg[i] == 0x02) {
				i ++;
				jiaoyanshuju(recMsg, i);
			}
				
			
		}
	}
}
