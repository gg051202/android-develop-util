package a26c.com.android_frame_test.socket;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

public class CmccSipcDecoder extends CumulativeProtocolDecoder {
    private final CharsetDecoder charsetDecoder;

    public CmccSipcDecoder() {
        this.charsetDecoder = Charset.forName("UTF-8").newDecoder();
    }

    @Override
    protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
        //记录解析到了短信协议中的哪一行(\n)
        int i = 1;
        //记录在当前行中读取到了哪一个字节
        int matchCount = 0;
        String statusLine = "", sender = "", receiver = "", length = "", sms = "";
        IoBuffer buffer = IoBuffer.allocate(100).setAutoExpand(true);
        while (in.hasRemaining()) {
            byte b = in.get();
            buffer.put(b);
            //10==b表示换行：该短信协议解码器使用\n（ASCII的字符10）作为分界点
            if (10 == b && 5 > i) {
                matchCount++;
                if (1 == i) {
                    //limit=position，position=0
                    buffer.flip();
                    statusLine = buffer.getString(matchCount, this.charsetDecoder);
                    //移除本行的最后一个换行符
                    statusLine = statusLine.substring(0, statusLine.length() - 1);
                    //本行读取完毕，所以让matchCount=0
                    matchCount = 0;
                    buffer.clear();
                }
                if (2 == i) {
                    buffer.flip();
                    sender = buffer.getString(matchCount, this.charsetDecoder);
                    sender = sender.substring(0, sender.length() - 1);
                    matchCount = 0;
                    buffer.clear();
                }
                if (3 == i) {
                    buffer.flip();
                    receiver = buffer.getString(matchCount, this.charsetDecoder);
                    receiver = receiver.substring(0, receiver.length() - 1);
                    matchCount = 0;
                    buffer.clear();
                }
                if (4 == i) {
                    buffer.flip();
                    length = buffer.getString(matchCount, this.charsetDecoder);
                    length = length.substring(0, length.length() - 1);
                    matchCount = 0;
                    buffer.clear();
                }
                i++;
            } else if (5 == i) {
                matchCount++;
                if (Long.parseLong(length.split(": ")[1]) == matchCount) {
                    buffer.flip();
                    sms = buffer.getString(matchCount, this.charsetDecoder);
                    i++;
                    break;
                }
            } else {
                matchCount++;
            }
        }
        SmsInfo smsInfo = new SmsInfo();
        smsInfo.setSender(sender.split(": ")[1]);
        smsInfo.setReceiver(receiver.split(": ")[1]);
        smsInfo.setMessage(sms);
        out.write(smsInfo);
        //告诉Mina：本次数据已全部读取完毕，故返回false
        return false;
    }

    public class SmsInfo {
        private String sender;
        private String receiver;
        private String message;

    /*-- 三个属性的setter和getter略 --*/

        public SmsInfo() {
        }

        public SmsInfo(String sender, String receiver, String message) {
            this.sender = sender;
            this.receiver = receiver;
            this.message = message;
        }

        public String getSender() {
            return sender;
        }

        public void setSender(String sender) {
            this.sender = sender;
        }

        public String getReceiver() {
            return receiver;
        }

        public void setReceiver(String receiver) {
            this.receiver = receiver;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}