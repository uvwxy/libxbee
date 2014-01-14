package de.uvwxy.xbee.apimode.messages;

import java.util.HashMap;

import android.annotation.SuppressLint;
import de.uvwxy.xbee.commands.ATCommand;

@SuppressLint("UseSparseArrays")
public class MsgATCommandResponse extends APIMessage {

	private static String INFO = "The â€œAT Commandâ€� API type allows for module parameters to be queried or set. When using this command ID, new parameter values are applied immediately. This includes any register set with the â€œAT Command - Queue Parameter Valueâ€� (0x09) API type.";
	private static String TITLE = "AT Command";
	private static final int ID = MessageID.MSG_AT_COMMAND_RESPONSE.value;

	public MsgATCommandResponse(byte[] data) {
		super(TITLE, INFO, ID, data);
	}

	public int getFrameID() {
		return super.getFrameID();
	}

	public ATCommand getATCommand() {
		return super.getATCommand();
	}

	public Status getStatus() {
		return Status.get(data(3));
	}

	public boolean hasParameters() {
		return data().length > 4;
	}

	public String getParameterValue() {
		if (hasParameters()) {
			String ret = "";
			for (byte b : dataTailFrom(4)) {
				ret += String.format("%02X ", (b & 0xff));
				// Integer.toHexString(b & 0xff);

			}
			return ret;
		}
		return "No Params";
	}

	public enum Status {
		STATUS_OK(0), STATUS_ERROR(1), STATUS_UNKNOWN(2);
		private final int value;

		Status(int value) {
			this.value = value;
		}

		public static Status get(int i) {
			Status s = (Status) reverseStatus.get(i);
			return (s == null) ? Status.STATUS_UNKNOWN : s;
		}

		private static final HashMap<Integer, Status> reverseStatus = new HashMap<Integer, Status>();
		static {
			for (Status s : Status.values()) {
				reverseStatus.put(s.value, s);
			}
		}
	}

	@Override
	public String getMessage() {
		return "Frame ID: " + getFrameID() + "\nCommand: " + getATCommand().value + "\nParameters: "
				+ getParameterValue() + "\nStatus: " + getStatus();
	}

}
