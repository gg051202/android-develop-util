package a26c.com.android_frame_test.socket;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

public class MyDataEncoder extends ProtocolEncoderAdapter {


    private final CharsetEncoder charsetEncoder;

    public MyDataEncoder() {
        this.charsetEncoder = Charset.forName("UTF-8").newEncoder();
    }


    @Override
    public void encode(IoSession session, Object message,
                       ProtocolEncoderOutput out) throws Exception {
        String value = (String) message;


        int totalSize = 4 + value.getBytes().length;
        IoBuffer buffer = IoBuffer.allocate(totalSize).setAutoExpand(true);

        buffer.put(Util.Short2BytesLH((short) totalSize));
        buffer.put(Util.Short2BytesLH((short) 1000));
        buffer.putString(value, charsetEncoder);
        buffer.flip();
        out.write(buffer);
        out.flush();

    }

}