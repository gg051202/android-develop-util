package a26c.com.android_frame_test.util;

import android.content.Context;

public class EventHandler {
    private Context mContext;
    public EventHandler(Context context) {
        mContext = context;
    }

    public void onTaskClick(Task task) {
        task.run();
    }
}