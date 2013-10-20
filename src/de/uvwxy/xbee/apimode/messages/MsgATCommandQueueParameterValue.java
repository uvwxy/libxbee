package de.uvwxy.xbee.apimode.messages;

import de.uvwxy.xbee.commands.ATCommand;

/**
 * As "(Note that frames are identical to the “AT Command” API type except for the API identifier.)" we only have to
 * change the info, title, and the id.
 * 
 * @author paul
 * 
 */
public class MsgATCommandQueueParameterValue extends MsgATCommand {

	private static final String INFO = "This API type allows module parameters to be queried or set. In contrast to the “AT Command” API type, new parameter values are queued and not applied until either the “AT Command” (0x08) API	type or the AC (Apply Changes) command is issued. Register queries (reading parameter values) are returned immediately.";
	private static final String TITLE = "At Command - Queue Parameter Value";
	private static final int ID = MessageID.MSG_AT_COMMAND_QUEUE_PARAMETER_VALUE.value;

	public MsgATCommandQueueParameterValue(byte[] data) {
		super(TITLE, INFO, ID, data);
	}

	public MsgATCommandQueueParameterValue(int frameID, ATCommand atc, byte[] parameters) {
		super(TITLE, INFO, ID, getData(frameID, atc, parameters));
	}

}
