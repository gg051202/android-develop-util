package a26c.com.android_frame_test.socket;

import android.util.Log;

import com.a26c.android.frame.util.AndroidScheduler;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.apache.mina.transport.socket.SocketSessionConfig;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.greenrobot.eventbus.EventBus;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class SocketManager {


    private static SocketManager mInstance;

    private NioSocketConnector mConnection;

    private IoSession mSession;

    /**
     * 手动取消，无需自动重连
     */
    private boolean needToAutoConnect = true;

    private SocketManager() {
        mConnection = new NioSocketConnector();
        mConnection.setDefaultRemoteAddress(new InetSocketAddress("47.94.217.160", 3000));
        mConnection.setConnectTimeoutMillis(3000);//超时
        mConnection.getFilterChain().addFirst("reconnection", new ReconnectFilterAdapter());
        mConnection.getFilterChain().addLast("codec",
                new ProtocolCodecFilter(new DataEncoder(), new DataDecoder()));
        mConnection.setHandler(new CallbackHandler());//添加回调

        //设置多长时间没有进行读写操作进入空闲状态，会调用sessionIdle方法，单位（秒）
        SocketSessionConfig sessionConfig = mConnection.getSessionConfig();
        sessionConfig.setReadBufferSize(10240);
        sessionConfig.setReaderIdleTime(60 * 5);
        sessionConfig.setWriterIdleTime(60 * 5);
        sessionConfig.setBothIdleTime(60 * 5);


    }

    public static SocketManager getInstance() {
        if (mInstance == null) {
            synchronized (SocketManager.class) {
                if (mInstance == null) {
                    mInstance = new SocketManager();
                }
            }
        }
        return mInstance;
    }


    /**
     * 发送消息的方法
     */
    public static void sendMsg(final Object msg) {
        System.out.println("正在发送消息:" + msg);
        if (getInstance().mSession != null && !getInstance().mSession.isClosing()) {
            getInstance().mSession.write(msg);
        } else {
            getInstance().connnect(new OnConnectSuccessListener() {
                @Override
                public void success() {
                    getInstance().mSession.write(msg);
                }
            });
        }
    }


    /**
     * 与服务器连接
     */
    public void connnect(OnConnectSuccessListener listener) {

        System.out.println("开始尝试连接...");
        if (mSession != null && !mSession.isClosing()) {
            System.out.println("已连接，无需重复连接");
            return;
        }
        this.onConnectSuccessListener = listener;
        needToAutoConnect = true;

        Observable.interval(0, 5, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .map(new Func1<Long, Boolean>() {
                    @Override
                    public Boolean call(Long aLong) {
                        try {
                            ConnectFuture future = mConnection.connect();
                            future.awaitUninterruptibly();
                            mSession = future.getSession();
                            return mSession != null;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return false;

                    }
                })
                .observeOn(AndroidScheduler.mainThread())
                .subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted() {

                    }

                    /**
                     * 连接socket成功的回调,失败会一直重连，无需回调
                     */
                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(Boolean isConnect) {
                        System.out.println("连接socket服务器结果：" + isConnect);
                        if (isConnect) {
                            if (onConnectSuccessListener != null) {
                                onConnectSuccessListener.success();
                            }
                            unsubscribe();
                        }

                    }
                });

    }

    /**
     * 断开连接
     */
    public void disContect() {
        Log.i("tag","断开连接...");
        if (mSession == null || mSession.isClosing()) {
            return;
        }
        needToAutoConnect = false;
        if (mSession != null) {
            mSession.closeOnFlush();
        }
        mSession = null;
        Log.i("tag", "连接已断开");
    }

    /**
     * 接受到服务器返回的socket消息，全局接受ReceiveSocketMessageEventBus通知
     */
    private static class CallbackHandler extends IoHandlerAdapter {

        @Override
        public void messageReceived(IoSession session, Object message) throws Exception {
            Log.i("tag", "接收到服务器端消息：" + message.toString());
            EventBus.getDefault().post(new ReceiveSocketMessageEventBus(message.toString()));
        }


    }

    /**
     * 如果掉线了，自动重连
     */
    private class ReconnectFilterAdapter extends IoFilterAdapter {
        @Override
        public void sessionClosed(NextFilter nextFilter, IoSession session) throws Exception {
            if (needToAutoConnect) {
                SocketManager.this.connnect(null);
            }
        }
    }

    static class DataDecoder extends CumulativeProtocolDecoder {

        @Override
        protected boolean doDecode(IoSession session, IoBuffer ioBuffer, ProtocolDecoderOutput out) throws Exception {

            ioBuffer.mark();
            String result = "";
            short totalSize, cmd;
            IoBuffer buffer = IoBuffer.allocate(100).setAutoExpand(true);

            while (ioBuffer.hasRemaining()) {
                //读取长度
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
                    result = ioBuffer.getString(Charset.forName("utf-8").newDecoder());
                    result = Util.unicodeToUTF_8(result);
                    System.out.println("result:" + result);
                }
            }
            out.write(result);
            return false;
        }

    }

    static class DataEncoder extends ProtocolEncoderAdapter {

        @Override
        public void encode(IoSession session, Object message,
                           ProtocolEncoderOutput out) throws Exception {
            String value = (String) message;


            int totalSize = 4 + value.getBytes().length;
            IoBuffer buffer = IoBuffer.allocate(totalSize).setAutoExpand(true);

            buffer.put(Util.Short2BytesLH((short) totalSize));
            buffer.put(Util.Short2BytesLH((short) 1000));
            buffer.putString(value, Charset.forName("UTF-8").newEncoder());
            buffer.flip();
            out.write(buffer);
            out.flush();

        }

    }


    public interface OnConnectSuccessListener {
        void success();
    }

    private OnConnectSuccessListener onConnectSuccessListener;

}
