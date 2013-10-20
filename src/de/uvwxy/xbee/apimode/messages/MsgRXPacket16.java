package de.uvwxy.xbee.apimode.messages;

public class MsgRXPacket16 extends MsgRXPacket64 {
	private static final String TITLE = "When the module receives an RF packet, it is sent out the UART using this message type.";
	private static final String INFO = "RX (Receive) Packet";
	private static final int ID = MessageID.MSG_RX_PACKET_16.value;

	protected MsgRXPacket16(byte[] data) {
		super(TITLE, INFO, ID, data, MsgTXRequest64.ADDRESS_LEN16);
	}

}
