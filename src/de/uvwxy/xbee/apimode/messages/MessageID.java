package de.uvwxy.xbee.apimode.messages;

import java.util.HashMap;

import android.util.Log;

public enum MessageID {
	MSG_MODEM_STATUS(0x8A), MSG_AT_COMMAND(0x08), MSG_AT_COMMAND_QUEUE_PARAMETER_VALUE(0x09), MSG_AT_COMMAND_RESPONSE(
			0x88), MSG_TX_REQUEST_64(0x00), MSG_TX_REQUEST_16(0x01), MSG_TX_REQUEST_STATUS(0x89), MSG_RX_PACKET_64(0x80), MSG_RX_PACKET_16(
			0x81), MSG_ZIGBEE_RECEIVE_PACKET(0x90), ZIGBEE_EXPLICIT_RX_INDICATOR(0x91),

	MSG_UNKNOWN(0xBADBAD);

	public final int value;

	MessageID(int value) {
		this.value = value;
	}

	public static MessageID get(int i) {
		
		// bytes starting with 1....? are casted to 1....1 1....? as int
		
		Log.i("XBEE", "Looking up message id " + i);
		MessageID s = (MessageID) reverseStatus.get(i);
		return (s == null) ? MessageID.MSG_UNKNOWN : s;
	}

	private static final HashMap<Integer, MessageID> reverseStatus = new HashMap<Integer, MessageID>();
	static {
		for (MessageID s : MessageID.values()) {
			reverseStatus.put(s.value, s);
		}
	}
}
