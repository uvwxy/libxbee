package de.uvwxy.xbee.apimode.messages;

import de.uvwxy.xbee.commands.ATCommand;

public class MsgATCommand extends APIMessage {
	private static String INFO = "The “AT Command” API type allows for module parameters to be queried or set. When using this command ID, new parameter values are applied immediately. This includes any register set with the “AT Command - Queue Parameter Value” (0x09) API type.";
	private static String TITLE = "AT Command";
	private static int ID = MessageID.MSG_AT_COMMAND.value;

	public MsgATCommand(byte[] data) {
		super(TITLE, INFO, ID, data);
	}

	public MsgATCommand(int frameID, ATCommand atc, byte[] parameters) {
		this(TITLE, INFO, ID, getData(frameID, atc, parameters));
	}

	/**
	 * Constructor to create a packet. Uses the wrapper constructor after creating a byte[] data array.
	 * 
	 * @param title
	 * @param info
	 * @param id
	 * @param data
	 */
	protected MsgATCommand(String title, String info, int id, byte[] data) {
		super(title, info, id, data);
	}

	/**
	 * Function used by the non wrapper constructor to create a byte array with data.
	 * 
	 * @param frameID
	 * @param atc
	 * @param parameters
	 * @return
	 */
	protected static byte[] getData(int frameID, ATCommand atc, byte[] parameters) {
		byte[] bAtCommand = atc.value.getBytes();
		// byte[] bParameters = parameters != null ? parameters.getBytes() : null;

		byte[] data = new byte[1 + bAtCommand.length + (parameters != null ? parameters.length : 0)];
		data[0] = (byte) frameID;
		data[1] = bAtCommand[0];
		data[2] = bAtCommand[1];

		if (parameters != null) {
			System.arraycopy(parameters, 0, data, 3, parameters.length);
		}
		// for (int i = 0; i < bParameters.length; i++) {
		// data[3 + i] = bParameters[i];
		// }

		return data;
	}

	public int getFrameID() {
		return super.getFrameID();
	}

	public ATCommand getATCommand() {
		return super.getATCommand();
	}

	public boolean hasParameters() {
		return data().length > 3;
	}

	public String getParameterValue() {
		if (hasParameters()) {
			return new String(dataTailFrom(3));
		}
		return "No Params";
	}

	@Override
	public String getMessage() {
		return "Frame ID: " + getFrameID() + "\nCommand: " + getATCommand().value + "\nParameters: "
				+ getParameterValue();
	}

}
