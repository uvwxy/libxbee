package de.uvwxy.xbee.apimode;

import android.util.Log;

public class FrameData {
	private int cmdID;
	private byte[] cmdData;
	private int dataPtr = 0;

	public FrameData(int dataLength) {
		dataPtr = 0;
		// -1 as cmd id is included in data length
		cmdData = new byte[dataLength - 1];
	}

	public FrameData(int cmdID, byte[] cmdData) {
		this.cmdID = cmdID;
		this.cmdData = cmdData;
	}

	public int getCmdID() {
		return cmdID;
	}

	public byte[] getCmdData() {
		return cmdData;
	}

	public void setCmdID(int cmdID) {
		Log.i("XBEE", "Setting CMD ID " + cmdID);
		this.cmdID = cmdID;
	}

	public void setCmdData(byte[] cmdData) {
		this.cmdData = cmdData;
	}

	/**
	 * This function adds the next byte to the cmd data byte array.
	 * 
	 * @param b
	 *            the byte to write into the array
	 * @return If the last byte is written true is returned, else false.
	 */
	public boolean putByteToCmdData(byte b) {
		if (cmdData == null || dataPtr == cmdData.length) {
			return false;
		}

		cmdData[dataPtr] = b;

		dataPtr++;

		if (dataPtr == cmdData.length)
			return true;

		return false;
	}
}
