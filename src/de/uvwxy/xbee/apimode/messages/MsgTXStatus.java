package de.uvwxy.xbee.apimode.messages;

import java.util.HashMap;

public class MsgTXStatus extends APIMessage {
	private static String INFO = "When a TX Request is completed, the module sends a TX Status message. This message will indicate if the packet was transmitted successfully or if there was a failure.";
	private static String TITLE = "TX (Transmit) Status";
	private static int ID = MessageID.MSG_TX_REQUEST_STATUS.value;

	protected MsgTXStatus(byte[] data) {
		super(TITLE, INFO, ID, data);
	}

	@Override
	public int getFrameID() {
		return super.getFrameID();
	}

	public Status getStatus() {
		return Status.get(data(1));
	}

	public enum Status {
		STATUS_SUCCESS(0), STATUS_NO_ACK(1), STATUS_CCA_FAILURE(2), STATUS_PURGED(3), STATUS_UNKNOWN(4);
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
		// TODO Auto-generated method stub
		return "Frame ID: " + getFrameID() + "\nStatus: " + getStatus();
	}

}
