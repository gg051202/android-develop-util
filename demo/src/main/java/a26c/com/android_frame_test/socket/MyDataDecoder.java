
package a26c.com.android_frame_test.socket;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import java.nio.charset.Charset;

public class MyDataDecoder extends CumulativeProtocolDecoder {
    @Override
    protected boolean doDecode(IoSession session, IoBuffer ioBuffer, ProtocolDecoderOutput out) throws Exception {

        ioBuffer.mark();
        String strTemp = "";
        short totalSize, cmd;
        IoBuffer buffer = IoBuffer.allocate(100).setAutoExpand(true);

        while (ioBuffer.hasRemaining()) {
            //读取包头
            if (ioBuffer.limit() >= 2) {
                byte b1 = ioBuffer.get();
                byte b2 = ioBuffer.get();
                totalSize = Util.byteToShort(new byte[]{b1, b2});
                System.out.println("totalSize:" + totalSize);
            } else {
                ioBuffer.reset();
                return true;
            }
            //读取CMD
            if (ioBuffer.limit() >= 2) {
                byte b1 = ioBuffer.get();
                byte b2 = ioBuffer.get();
                cmd = Util.byteToShort(new byte[]{b1, b2});
                System.out.println("cmd:" + cmd);
            } else {
                ioBuffer.reset();
                return true;
            }
            if (totalSize - 4 > 0) {
                buffer.flip();
                strTemp = ioBuffer.getString(Charset.forName("utf-8").newDecoder());
                strTemp = Util.unicodeToUTF_8(strTemp);
                System.out.println("result:" + strTemp);
            }
        }
        out.write(strTemp);
        return false;
    }

}