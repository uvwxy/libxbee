package de.uvwxy.xbee.apimode.messages;

import java.util.HashMap;

public class MsgModemStatus extends APIMessage {
	private static final String INFO = "RF module status messages are sent from the module in response to specific conditions.";
	private static final String TITLE = "Modem Status";
	private static final int ID = MessageID.MSG_MODEM_STATUS.value;

	public MsgModemStatus(byte[] data) {
		super(TITLE, INFO, ID, data);
	}

	/*
	 * No other constructor as this message is only received
	 */

	public Status getStatus() {
		return Status.get(data(0));
	}

	public enum Status {
		STATUS_HARDWARE_RESET(0), STATUS_WATCHDOG_TIMER_RESET(1), STATUS_ASSOCIATED(2), STATUS_DISASSOCIATED(3), STATUS_SYNCHRONIZATION_LOST(
				4), STATUS_COORDINATOR_REALIGNMENT(5), STATUS_COORDINATOR_STARTED(6), STATUS_UNKNOWN(7);
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
		return "Status: " + getStatus();
	}
}
