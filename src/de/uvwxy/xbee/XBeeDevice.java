package de.uvwxy.xbee;

import de.uvwxy.opensource.UnboundedFifoByteBuffer;
import de.uvwxy.xbee.apimode.Frame;
import de.uvwxy.xbee.apimode.messages.APIMessage;
import de.uvwxy.xbee.commands.ATCommand;

public interface XBeeDevice {

	UnboundedFifoByteBuffer getFIFO();

	public boolean bindToFirst();

	public boolean init();

	public boolean isOpen();

	public Frame getNextAPIMessage();

	public void setAPIMode2();
	
	public void setCommandMode();

	public void sendCommand(ATCommand atc, String params);

	public void sendAPICommand(ATCommand atc, byte[] params);

	public void sendAPIMessage(APIMessage apm);

	public void close();
}