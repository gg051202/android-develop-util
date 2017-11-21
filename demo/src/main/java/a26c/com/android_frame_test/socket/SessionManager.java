package a26c.com.android_frame_test.socket;

import org.apache.mina.core.session.IoSession;

/**
 * Description:
 * User: chenzheng
 * Date: 2016/12/9 0009
 * Time: 17:50
 */
public class SessionManager {

    private static SessionManager mInstance=null;

    private IoSession mSession;
    public static SessionManager getInstance(){
        if(mInstance==null){
            synchronized (SessionManager.class){
                if(mInstance==null){
                    mInstance = new SessionManager();
                }
            }
        }
        return mInstance;
    }

    private SessionManager(){}

    public void setSeesion(IoSession session){
        this.mSession = session;
    }

    public void writeToServer(Object msg){
        System.out.println("发送消息:"+  msg);
        if(mSession!=null){
            mSession.write(msg);
        }
    }

}
