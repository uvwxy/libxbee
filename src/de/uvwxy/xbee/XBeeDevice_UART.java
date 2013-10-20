package de.uvwxy.xbee;

import android.content.Context;
import android.util.Log;

import com.ftdi.j2xx.D2xxManager;

import de.uvwxy.opensource.UnboundedFifoByteBuffer;
import de.uvwxy.usb.UARTDevice;
import de.uvwxy.xbee.apimode.Frame;
import de.uvwxy.xbee.apimode.FrameCallback;
import de.uvwxy.xbee.apimode.FrameParserEscaped;
import de.uvwxy.xbee.apimode.messages.APIMessage;
import de.uvwxy.xbee.apimode.messages.MsgATCommand;
import de.uvwxy.xbee.commands.ATCommand;

public class XBeeDevice_UART extends UARTDevice implements XBeeDevice {

	FrameParserEscaped fpe = new FrameParserEscaped();

	public XBeeDevice_UART(Context ctx) {
		super(ctx);
	}

	private int baud = 9600;

	public void setBaud(int baud) {
		this.baud = baud;
	}

	public boolean init() {
		// original 9600 baud
		boolean ret = setConfig(baud, D2xxManager.FT_DATA_BITS_8, D2xxManager.FT_STOP_BITS_1, D2xxManager.FT_PARITY_NONE, D2xxManager.FT_FLOW_NONE);
		Log.i("XBEE", "Connection setConfig = OK = " + ret);

		enableRead();
		if (poller != null) {
			poller.interrupt();
		}

		poller = new Thread(xbeePoller);
		poller.start();
		return ret;
	}

	@Override
	public UnboundedFifoByteBuffer getFIFO() {
		return fifo;
	}

	public Frame getNextAPIMessage() {

		Frame f = fpe.readFrameDataFromFiFO(fifo);

		return f;
	}

	@Override
	public void setAPIMode2() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

		int bytesWritten = write("+++");

		try {
			Thread.sleep(1111);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if (bytesWritten >= 3) {
			Log.i("XBEE", "Setting device in command mode");

			sendCommand(ATCommand.CMD_AP, "2");
			Log.i("XBEE", "Exiting command mode");

			sendCommand(ATCommand.CMD_CN, "");
		}
	}

	@Override
	public void sendCommand(ATCommand atc, String params) {
		String out = "AT" + atc.value + params;
		int numBytes = write(out + "\r\n");
		Log.i("XBEE", "Written \"" + numBytes + "\"");
	}

	@Override
	public void sendAPICommand(ATCommand atc, byte[] params) {
		MsgATCommand msgatc = new MsgATCommand((byte) 0x52, atc, params);
		sendAPIMessage(msgatc);
	}

	@Override
	public void sendAPIMessage(APIMessage apm) {
		Frame f = new Frame(apm);
		if (!f.checksumIsValid()) {
			Log.e("XBEE", "CHECKSUM FAIL");
		}
		byte[] rawData = f.getRawData();

		for (byte b : rawData) {
			Log.i("XBEE", "Writing " + Integer.toHexString(b));
		}
		write(rawData);
	}

	@Override
	public void setCommandMode() {
		write("+++");
	}

	Thread poller = null;
	private Runnable xbeePoller = new Runnable() {

		@Override
		public void run() {
			boolean running = true;
			while (running) {
				Frame f = getNextAPIMessage();
				if (f != null && frameCallback != null) {
					frameCallback.onFrame(f);
				} else {
					try {
						// prevent 100% cpu
						Thread.sleep(1000 / 60);
					} catch (InterruptedException e) {
						running = false;
						e.printStackTrace();
					}
				}
			}
		}
	};

	private FrameCallback frameCallback;

	public void setFrameCallback(FrameCallback frameCallback) {
		this.frameCallback = frameCallback;
	}

	@Override
	public void close() {
		poller.interrupt();
		disableRead();
		super.close();
	}
}
