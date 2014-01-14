package de.uvwxy.xbee.apimode.messages;

import android.util.Log;
import de.uvwxy.data.ByteArrayWrapper;
import de.uvwxy.xbee.apimode.FrameData;
import de.uvwxy.xbee.commands.ATCommand;

public abstract class APIMessage extends ByteArrayWrapper {

	private static String TITLE = null;
	private static String INFO = null;

	private int ID;

	public static APIMessage createMessage(FrameData frameData) {
		return frameData == null ? null : createMessage(frameData.getCmdID(), frameData.getCmdData());
	}

	/**
	 * 
	 * @param id
	 * @param data
	 *            the Frame specific data without frame type (id)
	 * @return
	 */
	public static APIMessage createMessage(int id, byte[] data) {

		APIMessage m = null;

		switch (MessageID.get(id)) {
		case MSG_AT_COMMAND:
			Log.i("APIMESSAGE", "MsgATCommand");
			m = new MsgATCommand(data);
			break;
		case MSG_AT_COMMAND_QUEUE_PARAMETER_VALUE:
			Log.i("APIMESSAGE", "MsgATCommandQueueParameterValue");
			m = new MsgATCommandQueueParameterValue(data);
			break;
		case MSG_AT_COMMAND_RESPONSE:
			Log.i("APIMESSAGE", "MsgATCommandResponse");
			m = new MsgATCommandResponse(data);
			break;
		case MSG_MODEM_STATUS:
			Log.i("APIMESSAGE", "");
			m = new MsgModemStatus(data);
			break;
		case MSG_RX_PACKET_16:
			Log.i("APIMESSAGE", "MsgRXPacket16");
			m = new MsgRXPacket16(data);
			break;
		case MSG_RX_PACKET_64:
			Log.i("APIMESSAGE", "MsgRXPacket64");
			m = new MsgRXPacket64(data);
			break;
		case MSG_TX_REQUEST_16:
			Log.i("APIMESSAGE", "MsgTXRequest16");
			m = new MsgTXRequest16(data);
			break;
		case MSG_TX_REQUEST_64:
			Log.i("APIMESSAGE", "MsgTXRequest64");
			m = new MsgTXRequest64(data);
			break;
		case MSG_TX_REQUEST_STATUS:
			Log.i("APIMESSAGE", "MsgTXStatus");
			m = new MsgTXStatus(data);
			break;
		case MSG_ZIGBEE_RECEIVE_PACKET:
			Log.i("APIMESSAGE", "MsgZigBeeReceivePacket");
			m = new MsgZigBeeReceivePacket(data);
		case MSG_UNKNOWN:
			break;
		case ZIGBEE_EXPLICIT_RX_INDICATOR:
			// TODO!
			break;
		default:
			// TODO!
			break;

		}

		return m;

	}

	protected APIMessage(String title, String info, int id, byte[] data) {
		super(data);
		APIMessage.TITLE = title;
		APIMessage.INFO = info;
		this.ID = id;
	}

	/*
	 * Stuff visible to the outside world belongs below here:
	 */

	public int getCmdID() {
		return ID & 0xFF;
	}

	public MessageID getMessageID() {
		return MessageID.get(getCmdID());
	}

	public abstract String getMessage();

	public String getTitle() {
		return TITLE;
	}

	public String getInfo() {
		return INFO;
	}

	/*
	 * Invisible outside this package, but should be made visible by
	 * "implementing class" Methods are final as they are not supposed to be
	 * overwritten
	 */

	protected int getFrameID() {
		return data(0) & 0xFF;
	}

	protected ATCommand getATCommand() {
		return ATCommand.get(new String(data(1, 2)));
	}

	public String getHumanReadableAddress(byte[] addr) {
		String ret = "";
		for (byte b : addr) {
			ret += Integer.toHexString(b);
		}
		ret = ret.replace("0x", ":");
		ret = ret.substring(2, ret.length());
		return ret;
	}

	public byte[] getCmdData() {
		return data();
	}

}
