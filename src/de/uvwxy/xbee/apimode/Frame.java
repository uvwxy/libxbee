package de.uvwxy.xbee.apimode;

import de.uvwxy.xbee.apimode.messages.APIMessage;
import de.uvwxy.xbee.apimode.messages.MessageID;
import android.util.Log;

public class Frame {
	private byte mLengthMSB;
	private byte mLengthLSB;
	private FrameData mFrameData = null;;
	private byte mChecksum;

	protected Frame() {

	}

	public Frame(APIMessage apm) {
		mFrameData = new FrameData(apm.getCmdID(), apm.getCmdData());
		setMSBLSB();
		setChecksum();
	}

	public byte getLengthMSB() {
		return mLengthMSB;
	}

	public byte getLengthLSB() {
		return mLengthLSB;
	}

	public FrameData getFrameData() {
		return mFrameData;
	}

	public byte getChecksum() {
		return mChecksum;
	}

	public void setLengthMSB(byte mLengthMSB) {
		this.mLengthMSB = mLengthMSB;
	}

	public void setLengthLSB(byte mLengthLSB) {
		this.mLengthLSB = mLengthLSB;
	}

	public void setFrameData(FrameData mFrameData) {
		this.mFrameData = mFrameData;
	}

	public void setChecksum(byte mChecksum) {
		this.mChecksum = mChecksum;
	}

	public int getFrameDataLen() {
		return ((mLengthMSB & 0xFF) << 8) | (mLengthLSB & 0xFF);
	}

	protected void setMSBLSB() {
		int len = mFrameData.getCmdData().length + 1;
		setLengthLSB((byte) (len & 0xff));
		setLengthMSB((byte) ((len >> 8) & 0xff));
	}

	protected void setChecksum() {
		int res = 0;
		res += mFrameData.getCmdID();
		byte[] cmdData = mFrameData.getCmdData();
		for (int i = 0; i < cmdData.length; i++) {
			res += (cmdData[i] & 0xFF);
		}
		mChecksum = (byte) (0xFF - res);
	}

	public boolean checksumIsValid() {
		int res = 0;
		res += mFrameData.getCmdID();
		byte[] cmdData = mFrameData.getCmdData();
		for (int i = 0; i < cmdData.length; i++) {
			res += (cmdData[i] & 0xFF);
		}
		res += (mChecksum & 0xFF);
		Log.i("FRAMEPARSER", "Checksum is valid = " + ((res & 0xff) == 0xff));
		return ((res & 0xFF) == 0xFF);
	}

	public byte[] getRawData() {
		byte[] api_data = mFrameData.getCmdData();
		byte[] ret = new byte[1 + 1 + 1 + 1 + api_data.length + 1];

		ret[0] = AFrameParser.XBEE_FRAME_DELIMITER;
		ret[1] = mLengthMSB;
		ret[2] = mLengthLSB;
		ret[3] = (byte) (mFrameData.getCmdID());

		System.arraycopy(api_data, 0, ret, 4, api_data.length);

		ret[ret.length - 1] = mChecksum;
		return ret;
	}
	
	public APIMessage getAPIMessage(){
		return APIMessage.createMessage(getFrameData());
	}

	@Override
	public String toString() {
		String ret = "Frame length: " + getFrameDataLen();
		ret += "\nFrame type: " + MessageID.get(getFrameData().getCmdID());
		ret += "\nFrame data: " + new String(getFrameData().getCmdData());
		// TODO: implement: parsing of frame data to object
		return ret;
	}
}
