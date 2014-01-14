package de.uvwxy.xbee.apimode.messages;

/**
 * Same as TXRequest64 but only two address bytes
 * 
 * @author paul
 * 
 */
public class MsgTXRequest16 extends MsgTXRequest64 {
	private static final String TITLE = "A TX Request message will cause the module to send RF Data as an RF Packet.";
	private static final String INFO = "TX (Transmit) Request (16bit Address)";
	private static byte ID = (byte) MessageID.MSG_TX_REQUEST_16.value;

	public MsgTXRequest16(byte[] data) {
		super(TITLE, INFO, ID, data, MsgTXRequest64.ADDRESS_LEN16);
	}

	public MsgTXRequest16(byte frameID, byte[] destionationAddress, byte options, byte[] rfData) {
		super(TITLE, INFO, ID, getData(frameID, destionationAddress, options, rfData),
				(byte) destionationAddress.length);
	}
}
