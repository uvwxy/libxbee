package de.uvwxy.usb;

import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.util.Log;

import com.ftdi.j2xx.D2xxManager;
import com.ftdi.j2xx.D2xxManager.D2xxException;
import com.ftdi.j2xx.FT_Device;

import de.uvwxy.opensource.UnboundedFifoByteBuffer;

public abstract class UARTDevice {
	private D2xxManager mD2xxManager = null;
	private Context ctx;

	private FT_Device mFTDevice = null;
	private boolean uart_configured;

	protected UnboundedFifoByteBuffer fifo = new UnboundedFifoByteBuffer(1024 * 2);
	private CopyBuffer fifoThread = new CopyBuffer();

	public UARTDevice(Context ctx) {
		this.ctx = ctx;
	}

	public int populateDevices() {
		if (initMD2xxManager()) {
			return mD2xxManager.createDeviceInfoList(ctx);
		} else {
			return -1;
		}
	}

	public boolean bindToFirst() {
		return bindTo(0);
	}

	public boolean bindTo(int index) {
		if (ctx != null && initMD2xxManager()) {
			try {
				mFTDevice = mD2xxManager.openByIndex(ctx, index);
			} catch (Exception e) {
				return false;
			}
			if (mFTDevice != null) {
				return true;
			}
		}
		return false;
	}

	public boolean bindTo(Intent usbConnectedIntent) {
		return bindTo(getUSBDevice(usbConnectedIntent));
	}

	public boolean bindTo(UsbDevice usb) {
		if (ctx != null && initMD2xxManager()) {
			populateDevices();
			mFTDevice = mD2xxManager.openByUsbDevice(ctx, usb);
			if (mFTDevice != null) {
				return true;
			}
		}
		return false;
	}

	private boolean initMD2xxManager() {
		if (mD2xxManager == null) {
			try {
				mD2xxManager = D2xxManager.getInstance(ctx);
			} catch (D2xxException e) {
				return false;
			}
		}
		return true;
	}

	public static UsbDevice getUSBDevice(Intent intent) {
		return (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
	}

	protected boolean setConfig(int baud, byte dataBits, byte stopBits, byte parity, short flowControl) {
		if (mFTDevice != null && mFTDevice.isOpen() == false) {
			return false;
		}

		// configure our port
		// reset to UART mode for 232 devices
		mFTDevice.setBitMode((byte) 0, D2xxManager.FT_BITMODE_RESET);

		mFTDevice.setBaudRate(baud);

		switch (dataBits) {
		case 7:
			dataBits = D2xxManager.FT_DATA_BITS_7;
			break;
		case 8:
			dataBits = D2xxManager.FT_DATA_BITS_8;
			break;
		default:
			dataBits = D2xxManager.FT_DATA_BITS_8;
			break;
		}

		switch (stopBits) {
		case 1:
			stopBits = D2xxManager.FT_STOP_BITS_1;
			break;
		case 2:
			stopBits = D2xxManager.FT_STOP_BITS_2;
			break;
		default:
			stopBits = D2xxManager.FT_STOP_BITS_1;
			break;
		}

		switch (parity) {
		case 0:
			parity = D2xxManager.FT_PARITY_NONE;
			break;
		case 1:
			parity = D2xxManager.FT_PARITY_ODD;
			break;
		case 2:
			parity = D2xxManager.FT_PARITY_EVEN;
			break;
		case 3:
			parity = D2xxManager.FT_PARITY_MARK;
			break;
		case 4:
			parity = D2xxManager.FT_PARITY_SPACE;
			break;
		default:
			parity = D2xxManager.FT_PARITY_NONE;
			break;
		}

		mFTDevice.setDataCharacteristics(dataBits, stopBits, parity);

		short flowCtrlSetting;
		switch (flowControl) {
		case 0:
			flowCtrlSetting = D2xxManager.FT_FLOW_NONE;
			break;
		case 1:
			flowCtrlSetting = D2xxManager.FT_FLOW_RTS_CTS;
			break;
		case 2:
			flowCtrlSetting = D2xxManager.FT_FLOW_DTR_DSR;
			break;
		case 3:
			flowCtrlSetting = D2xxManager.FT_FLOW_XON_XOFF;
			break;
		default:
			flowCtrlSetting = D2xxManager.FT_FLOW_NONE;
			break;
		}

		// TODO : flow ctrl: XOFF/XOM
		mFTDevice.setFlowControl(flowCtrlSetting, (byte) 0x0b, (byte) 0x0d);
		uart_configured = true;
		return true;
	}

	public void setVidPid(int vid, int pid) {
		if (initMD2xxManager()) {
			mD2xxManager.setVIDPID(vid, pid);
		}
	}

	protected FT_Device getFTDevice() {
		return mFTDevice;
	}

	protected void setFTDevice(FT_Device mFTDevice) {
		this.mFTDevice = mFTDevice;
	}

	public boolean isOpen() {
		if (mFTDevice != null) {
			return mFTDevice.isOpen();
		}
		return false;
	}

	protected int write(byte[] data) {
		if (mFTDevice != null && mFTDevice.isOpen() && uart_configured) {

			mFTDevice.setLatencyTimer((byte) 16);
//			mFTDevice.purge((byte) (D2xxManager.FT_PURGE_TX | D2xxManager.FT_PURGE_RX));
			return mFTDevice.write(data);
		}
		return -1;
	}

	public int write(String s) {
		return write(s.getBytes());
	}

	public String read() {

		return "null";
	}

	public void enableRead() {
		// Discards any data form the specified driver buffer and also flushes data from the device.
		mFTDevice.purge((byte) (D2xxManager.FT_PURGE_TX));
		// Restarts the driver's IN thread following a successful call to stopInTask() Remarks: This function is used to
		// restart the driver's IN task (read) after it has been stopped by a call to stopInTask().
		mFTDevice.restartInTask();
		Thread t = new Thread(fifoThread);
		t.start();
	}

	public void disableRead() {
		// This method stops the driver's IN thread and prevents USB IN requests being issued to the device. No data
		// will be received from the device if the IN thread is stopped. Remarks: This function is used to put the
		// driver's IN task (read) into a wait state. It can be used in situations where data is being received
		// continuously, so that the device can be purged without more data being received. It is used together with
		// restartInTask() which sets the IN task running again.
		mFTDevice.stopInTask();
		fifoThread.setRunning(false);

	}

	private class CopyBuffer implements Runnable {
		private static final int THREAD_BYTE_COPY_BUFFER_SIZE = 1024;

		private boolean running = true;

		protected boolean isRunning() {
			return running;
		}

		protected void setRunning(boolean running) {
			this.running = running;
		}

		@Override
		public void run() {
			byte[] lCOPY_BUFFER = new byte[THREAD_BYTE_COPY_BUFFER_SIZE];
			int x = 0;
			while (mFTDevice != null && isRunning()) {
				int availableBytes = mFTDevice.getQueueStatus();

				if (availableBytes > 0) {
//					Log.i("COPYBUFFER", "Read");
					// read no more than bytes in lCOPY_BUFFER
					if (availableBytes > THREAD_BYTE_COPY_BUFFER_SIZE) {
						availableBytes = THREAD_BYTE_COPY_BUFFER_SIZE;
					}

					// read from device
					int readBytes = mFTDevice.read(lCOPY_BUFFER, availableBytes);

					// copy everything into fifo buffer
					for (int i = 0; i < readBytes; i++) {
						fifo.add(lCOPY_BUFFER[i]);
					}
					x = 0;
				} else {
					//					if (x < 10){
					//						Log.i("COPYBUFFER", "Sleep");
					//						x++;
					//					}

					if (!mFTDevice.isOpen()) {
						Log.i("COPYBUFFER", "Device is closed");
						running = false;
					}
					//					try {
					//						// prevent 100% cpu
					//						
					//						Thread.sleep(1000/60);
					//		
					//					} catch (InterruptedException e) {
					//						e.printStackTrace();
					//					}
				}

			}
		}
	};

	public void close(){
		mFTDevice.close();
	}
}
