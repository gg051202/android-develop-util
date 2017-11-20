package a26c.com.android_frame_test.socket;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

/**
 * 编码器将数据直接发出去(不做处理)
 */
public class MyDataEncoder extends ProtocolEncoderAdapter {

    @Override
    public void encode(IoSession session, Object message,
                       ProtocolEncoderOutput out) throws Exception {
        String value = (String) message;
        int totalSize = 4 + value.getBytes().length;
        byte[] buffer = new byte[totalSize];
        buffer = copy(buffer, 0, unsignedShortToByte2(totalSize));
        buffer = copy(buffer, 2, unsignedShortToByte2(1000));
        buffer = copy(buffer, 4, value.getBytes());

        out.write(buffer);
        out.flush();

    }


    public byte[] copy(byte[] buffer, int start, byte[] value) {

        int i = 0;
        for (byte b : value) {
            buffer[start + i++] = b;
        }
        return buffer;
    }


    /**
     * short整数转换为2字节的byte数组
     *
     * @param s short整数
     * @return byte数组
     */
    public static byte[] unsignedShortToByte2(int s) {
        byte[] targets = new byte[2];
        targets[0] = (byte) (s >> 8 & 0xFF);
        targets[1] = (byte) (s & 0xFF);
        return targets;
    }
}