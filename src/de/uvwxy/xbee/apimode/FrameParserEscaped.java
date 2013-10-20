package de.uvwxy.xbee.apimode;

import android.util.Log;
import de.uvwxy.opensource.UnboundedFifoByteBuffer;

public class FrameParserEscaped extends AFrameParser {
	protected byte readNextByte(UnboundedFifoByteBuffer fifo) {
		// only read byte if at least two bytes are available. otherwise treat as empty fifo
		if (fifo.size() >= 1 && !(fifo.get() == AFrameParser.XBEE_ESCAPE_BYTE && fifo.size() < 2)) {
			byte b = fifo.remove();
//			Log.i("FRAMEPARSER", "byte " + Integer.toHexString(b));
			if (b == AFrameParser.XBEE_ESCAPE_BYTE) {
				byte c = (byte) (fifo.remove() ^ AFrameParser.XBEE_ESCAPE_BYTE_XOR_VALUE);
//				Log.i("FRAMEPARSER", "byte " + Integer.toHexString(c));
				return c;
			} else {
				return b;
			}
		}
		throw new IllegalStateException("The buffer is not full enough for escaping char");
	}
}
