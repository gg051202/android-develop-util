package a26c.com.android_frame_test.socket;

import android.util.Log;

import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.SocketSessionConfig;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.greenrobot.eventbus.EventBus;

import java.net.InetSocketAddress;

public class ConnectionManager {


    private NioSocketConnector mConnection;
    private IoSession mSession;

    public ConnectionManager() {

        mConnection = new NioSocketConnector();


        mConnection.setConnectTimeoutMillis(3000);//超时
        mConnection.getFilterChain().addFirst("reconnection", new MyIoFilterAdapter());
        mConnection.getFilterChain().addLast("codec",
                new ProtocolCodecFilter(new MyDataEncoder(),new MyDataDecoder()));
        mConnection.setHandler(new DefaultHandler());//添加回调


        //设置多长时间没有进行读写操作进入空闲状态，会调用sessionIdle方法，单位（秒）
        SocketSessionConfig sessionConfig = mConnection.getSessionConfig();
        sessionConfig.setReadBufferSize(10240);
        sessionConfig.setReaderIdleTime(60 * 5);
        sessionConfig.setWriterIdleTime(60 * 5);
        sessionConfig.setBothIdleTime(60 * 5);
    }

    /**
     * 与服务器连接
     */
    public boolean connnect() {
        Log.i("tag", "准备连接");
        try {
            ConnectFuture future = mConnection.connect(new InetSocketAddress("47.94.217.160", 3000));
            future.awaitUninterruptibly();
            mSession = future.getSession();

            SessionManager.getInstance().setSeesion(mSession);

        } catch (Exception e) {
            e.printStackTrace();
            Log.i("tag", "连接失败");
            return false;
        }

        return mSession != null;
    }

    /**
     * 断开连接
     */
    public void disContect() {
        if (mConnection != null) {
            mConnection.dispose();
        }
        if (mSession != null) {
            mSession.closeOnFlush();
        }
        mConnection = null;
        mSession = null;
        Log.i("tag", "断开连接");
    }

    private static class DefaultHandler extends IoHandlerAdapter {

        @Override
        public void messageReceived(IoSession session, Object message) throws Exception {
            Log.i("tag", "接收到服务器端消息：" + message.toString());
            EventBus.getDefault().post(new ReceiveSocketMessageEventBus(message.toString()));
        }
    }

    private class MyIoFilterAdapter extends IoFilterAdapter {
        @Override
        public void sessionClosed(NextFilter nextFilter, IoSession session) throws Exception {
            Log.i("", "连接关闭，每隔5秒进行重新连接");
            for (; ; ) {
                if (mConnection == null) {
                    break;
                }
                if (ConnectionManager.this.connnect()) {
                    Log.i("", "断线重连[" + mConnection.getDefaultRemoteAddress().getHostName() + ":" +
                            mConnection.getDefaultRemoteAddress().getPort() + "]成功");
                    break;
                }
                Thread.sleep(5000);
            }
        }

    }
}
