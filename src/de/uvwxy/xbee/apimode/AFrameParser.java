package de.uvwxy.xbee.apimode;

import android.util.Log;
import de.uvwxy.opensource.UnboundedFifoByteBuffer;

public abstract class AFrameParser {
	public static final byte XBEE_FRAME_DELIMITER = 0x7E;
	public static final byte XBEE_ESCAPE_BYTE = 0x7D;
	public static final byte XBEE_ESCAPE_BYTE_XOR_VALUE = 0x20;

	protected Frame mCurrentFrame = null;
	private ReadNext mCurrentState = ReadNext.HEADER;

	private enum ReadNext {
		HEADER, LENGTH_MSB, LENGTH_LSB, CMD_ID, CMD_DATA, CHECKSUM
	};

	/**
	 * Call this function to advance in parsing a frame from the fifo buffer.
	 * 
	 * @param fifo
	 * @return null if parsing incomplete, else valid Frame.
	 */
	public Frame readFrameDataFromFiFO(UnboundedFifoByteBuffer fifo) {
		readNextField: for (;;) {
			switch (mCurrentState) {
			case HEADER:
//				Log.i("FRAMEPARSER", "Reading HEADER");
				if (readStartDelimiter(fifo)) {
					mCurrentState = ReadNext.LENGTH_MSB;
					mCurrentFrame = new Frame();
					continue readNextField;
				} else {
					if (fifo.size() == 0) {
						try {
//							Log.d("PARSER", "FIFO Sleep induced..");
							Thread.sleep(250);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
				break;
			case LENGTH_MSB:
				Log.i("FRAMEPARSER", "Reading L_MSB");
				if (readLengthMSB(fifo)) {
					mCurrentState = ReadNext.LENGTH_LSB;
					continue readNextField;
				}
				break;
			case LENGTH_LSB:
				Log.i("FRAMEPARSER", "Reading L_LSB");
				if (readLengthLSB(fifo)) {
					mCurrentState = ReadNext.CMD_ID;
					continue readNextField;
				}
				break;
			case CMD_ID:
				Log.i("FRAMEPARSER", "Reading CMD_ID");
				if (readCmdID(fifo)) {
					mCurrentState = ReadNext.CMD_DATA;
					continue readNextField;
				}
				break;
			case CMD_DATA:
				Log.i("FRAMEPARSER", "Reading DATA");
				if (readCmdData(fifo)) {
					mCurrentState = ReadNext.CHECKSUM;
					continue readNextField;
				}
				break;
			case CHECKSUM:
				Log.i("FRAMEPARSER", "Reading CHECKSUM");
				if (readChecksum(fifo)) {
					Frame ret = mCurrentFrame;
					mCurrentFrame = null;
					mCurrentState = ReadNext.HEADER;
					return ret;
				}

				break;

			default:
				mCurrentState = ReadNext.HEADER;
				return null;
			}
			break;
		}

		return null;
	}

	/**
	 * @param fifo
	 */
	private boolean readStartDelimiter(UnboundedFifoByteBuffer fifo) {
		// read until start delimiter
		byte head = ~XBEE_FRAME_DELIMITER;
		while (head != XBEE_FRAME_DELIMITER) {
			try {
				// access fifo directly as no escaping need if
				// FrameParserEscaped is used
				head = fifo.remove();
				Log.i("FRAMEPARSER", "read byte " + Integer.toHexString(head));
			} catch (IllegalStateException e) {
				return false;
			}
		}
		return true;
	}

	private boolean readLengthMSB(UnboundedFifoByteBuffer fifo) {
		try {
			mCurrentFrame.setLengthMSB(readNextByte(fifo));
		} catch (IllegalStateException e) {
			return false;
		}
		return true;
	}

	private boolean readLengthLSB(UnboundedFifoByteBuffer fifo) {
		try {
			mCurrentFrame.setLengthLSB(readNextByte(fifo));
			mCurrentFrame.setFrameData(new FrameData(mCurrentFrame.getFrameDataLen()));
		} catch (IllegalStateException e) {
			return false;
		}
		return true;
	}

	private boolean readCmdID(UnboundedFifoByteBuffer fifo) {
		try {
			mCurrentFrame.getFrameData().setCmdID(readNextByte(fifo) & 0xFF);
		} catch (IllegalStateException e) {
			return false;
		}
		return true;
	}

	private boolean readCmdData(UnboundedFifoByteBuffer fifo) {

		while (true) {
			try {
				// put the next byte to cmd data
				if (mCurrentFrame.getFrameData().putByteToCmdData(readNextByte(fifo))) {
					// we have written the last byte, data is complete
					return true;
				}
				// still bytes to write into fifo, continue
			} catch (IllegalStateException e) {
				// fifo was empty, cancel
				return false;
			}
		}
	}

	private boolean readChecksum(UnboundedFifoByteBuffer fifo) {
		try {
			mCurrentFrame.setChecksum(readNextByte(fifo));
		} catch (IllegalStateException e) {
			return false;
		}
		return true;
	}

	protected abstract byte readNextByte(UnboundedFifoByteBuffer fifo);
}
