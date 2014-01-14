package de.uvwxy.xbee.apimode.messages;

import com.google.common.io.BaseEncoding;

public class MsgTXRequest64 extends APIMessage {
	private static final String TITLE = "A TX Request message will cause the module to send RF Data as an RF Packet.";
	private static final String INFO = "TX (Transmit) Request (64bit Address)";
	private static int ID = MessageID.MSG_TX_REQUEST_64.value;
	public static final byte ADDRESS_LEN16 = 2;
	public static final byte ADDRESS_LEN64 = 8;
	public static final byte OPTIONS_DISABLE_ACK = 0x01;
	public static final byte OPTIONS_SEND_WITH_BROADCAST_PAN_ID = 0x04;

	private byte addressLength = ADDRESS_LEN64;

	// Passthrough constructor for TXR16
	protected MsgTXRequest64(String title, String info, int id, byte[] data, byte addrLen) {
		super(title, info, id, data);
		addressLength = addrLen;

		if ((id == MessageID.MSG_TX_REQUEST_16.value && addressLength != ADDRESS_LEN16)
				&& (id == MessageID.MSG_TX_REQUEST_64.value && addressLength != ADDRESS_LEN64))
			throw new RuntimeException("Address length was wrong (16bits vs 64bits) (it was " + addressLength + ")"
					+ "\nType was " + MessageID.get(id));
	}

	public MsgTXRequest64(byte[] data) {
		super(TITLE, INFO, ID, data);
		addressLength = MsgTXRequest64.ADDRESS_LEN64;
	}

	public MsgTXRequest64(int frameID, byte[] destionationAddress, byte options, byte[] rfData) {
		this(TITLE, INFO, ID, getData(frameID, destionationAddress, options, rfData), (byte) destionationAddress.length);
	}

	protected static byte[] getData(int frameID, byte[] destionationAddress, byte options, byte[] rfData) {
		if (rfData.length > 100)
			throw new RuntimeException("RF Data exceeds 100 bytes");

		byte[] ret = new byte[1 + destionationAddress.length + 1 + rfData.length];
		ret[0] = (byte) frameID;
		System.arraycopy(destionationAddress, 0, ret, 1, destionationAddress.length);
		ret[1 + destionationAddress.length] = options;
		System.arraycopy(rfData, 0, ret, 1 + destionationAddress.length + 1, rfData.length);
		return ret;
	}

	public int getFrameID() {
		return super.getFrameID();
	}

	public byte[] getDestAddress() {
		return data(1, addressLength);
	}

	public byte getOptions() {
		return data(1 + addressLength);
	}

	public byte[] getRFData() {
		return dataTailFrom(1 + addressLength + 1);
	}

	@Override
	public String getMessage() {
		return "TXRequest to " + getHumanReadableAddress(getDestAddress()) + " with options "
				+ Integer.toHexString(getOptions()) + "\nRFDATA:" + getRFHexString();
	}

	public String getRFHexString() {
		return BaseEncoding.base16().encode(getRFData());
	}
}
