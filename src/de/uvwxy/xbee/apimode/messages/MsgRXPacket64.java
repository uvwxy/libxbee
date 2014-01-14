package de.uvwxy.xbee.apimode.messages;

import com.google.common.io.BaseEncoding;

public class MsgRXPacket64 extends APIMessage {
	private static final String TITLE = "When the module receives an RF packet, it is sent out the UART using this message type.";
	private static final String INFO = "RX (Receive) Packet";
	private static final int ID = MessageID.MSG_RX_PACKET_64.value;
	public static final byte ADDRESS_LEN16 = 2;
	public static final byte ADDRESS_LEN64 = 8;
	private byte addressLength = ADDRESS_LEN64;

	// Passthrough constructor for RXP16
	protected MsgRXPacket64(String title, String info, int id, byte[] data, byte addrLen) {
		super(title, info, id, data);
		addressLength = addrLen;

		if (addressLength != ADDRESS_LEN16 || addressLength != ADDRESS_LEN64)
			throw new RuntimeException("Address length was neither 16bits nor 64bits");
	}

	protected MsgRXPacket64(byte[] data) {
		super(TITLE, INFO, ID, data);
		addressLength = MsgTXRequest64.ADDRESS_LEN64;
	}

	public byte[] getSourceAddress() {
		return data(0, addressLength);
	}

	public byte getRSSI() {
		return data(addressLength);
	}

	public byte getOptions() {
		return data(addressLength + 1);
	}

	public byte[] getRFData() {
		return dataTailFrom(addressLength + 1 + 1);
	}

	@Override
	public String getMessage() {
		return "RX Packet from " + getHumanReadableAddress(getSourceAddress()) + " with options "
				+ Integer.toHexString(getOptions()) + "\nRFDATA:" + getRFHexString();
	}
	
	public String getRFHexString(){
		return BaseEncoding.base16().encode(getRFData());
	}

}
