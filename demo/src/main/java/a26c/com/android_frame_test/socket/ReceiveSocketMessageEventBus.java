package a26c.com.android_frame_test.socket;

/**
 * Created by guilinlin on 2017/11/20 17:21.
 * email 973635949@qq.com
 */

public class ReceiveSocketMessageEventBus {

    private String message;

    public ReceiveSocketMessageEventBus(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
