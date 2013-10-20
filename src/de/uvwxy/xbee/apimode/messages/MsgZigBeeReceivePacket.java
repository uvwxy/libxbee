package de.uvwxy.xbee.apimode.messages;

import com.google.common.io.BaseEncoding;

public class MsgZigBeeReceivePacket extends APIMessage {
	private static final String TITLE = "When the module receives an RF packet, it is sent out the UART using this message type.";
	private static final String INFO = "ZigBee Recive Packet";
	private static final int ID = MessageID.MSG_ZIGBEE_RECEIVE_PACKET.value;

	protected MsgZigBeeReceivePacket(byte[] data) {
		super(TITLE, INFO, ID, data);

	}

	@Override
	public String getMessage() {
		return "ZigBee Receive Packet from " + BaseEncoding.base16().encode(getSourceAddress()) + " with options "
				+ Integer.toHexString(getOptions()) + "\nRFDATA (" + getRFData().length + "):" + getRFHexString();
	}

	public byte[] getSourceAddress() {
		return data(0, 8); // 64bit
	}

	public byte[] getSourceNetworkAddress() {
		return data(8, 2);
	}

	public byte getOptions() {
		return data(8 + 2);
	}

	public byte[] getRFData() {
		return dataTailFrom(8 + 2 + 1);
	}

	public String getRFHexString() {
		return BaseEncoding.base16().encode(getRFData());
	}

}
