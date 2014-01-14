package de.uvwxy.data;

/**
 * I know this is very close to the exsisting class java.nio.ByteBuffer, but it does not provide any functions that
 * actually return a byte array. Thus it is easier to work with this implementation. Also might be less overhead.
 * 
 * @author paul
 * 
 */
public class ByteArrayWrapper {

	private byte[] data;

	public ByteArrayWrapper(byte[] data) {
		this.data = data;
	}

	/*
	 * Data access below
	 */

	protected final byte[] data() {
		if (data == null) {
			throw new RuntimeException("API Message data is null");
		}

		return data;
	}

	protected final byte data(int pos) {
		// throws data is null message due to data() call in first if clause
		if (data() == null || data.length < pos) {
			String errorMessage = "API Message access to invalid byte in data";
			errorMessage += "\n Access to " + pos + ", data length is " + data.length;
			throw new RuntimeException(errorMessage);
		}
		return data()[pos];
	}

	protected final byte[] data(int pos, int length) {
		// throws data is null message due to data() call in first if clause
		if (data() == null || data.length < pos || data.length < pos + length) {
			String errorMessage = "API Message access to invalid byte in data";
			errorMessage += "\n Access to " + pos + " length " + length + ", data length is " + data.length;
			throw new RuntimeException(errorMessage);
		}

		byte[] ret = new byte[length];
		System.arraycopy(data(), pos, ret, 0, length);
		return ret;
	}

	protected final byte[] dataTailFrom(int pos) {
		return data(pos, data().length - pos);
	}
}
