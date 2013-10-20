package de.uvwxy.xbee.apimode;

import de.uvwxy.opensource.UnboundedFifoByteBuffer;

public class FrameParserUnEscaped extends AFrameParser {

	@Override
	protected byte readNextByte(UnboundedFifoByteBuffer fifo) {
		return fifo.remove();
	}

}
