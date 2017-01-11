package com.a26c.android.frame.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

public class FrameAppManager {
    private static Stack<Activity> activityStack;
    private static FrameAppManager instance;

    private FrameAppManager() {
    }

    /**
     * 单一实例
     */
    public static FrameAppManager getAppManager() {
        if (instance == null) {
            instance = new FrameAppManager();
        }
        return instance;
    }

    /**
     * 添加Activity到堆栈
     */
    public void addActivity(Activity activity) {
        if (activityStack == null) {
            activityStack = new Stack<Activity>();
        }
        activityStack.add(activity);
    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    public Activity currentActivity() {
        Activity activity = activityStack.lastElement();
        return activity;
    }

    /**
     * 结束当前Activity（堆栈中最后一个压入的）
     */
    public void finishActivity() {
        Activity activity = activityStack.lastElement();
        finishActivity(activity);
    }

    /**
     * 移除当前ACTIVITY堆栈
     */
    public void removeActivity(Activity activity) {
        if (activityStack.contains(activity)) {
            activityStack.remove(activity);
            activity = null;
        }
    }

    /**
     * 结束指定的Activity
     */
    public void finishActivity(Activity activity) {
        if (activity != null) {
            activity.finish();
            activity = null;
            Iterator<Activity> iter = activityStack.iterator();
            while (iter.hasNext()) {
                Activity str = iter.next();
                if (str.equals(activity)) {
                    iter.remove();
                }
            }
        }
    }

    /**
     * 结束指定类名的Activity
     */
    public void finishActivity(Class<?> cls) {
        for (Activity activity : activityStack) {
            if (activity.getClass().equals(cls)) {
                finishActivity(activity);
            }
        }
    }

    /**
     * 结束所有Activity
     */
    public void finishAllActivity() {
        if (activityStack == null)
            return;
        try {
            for (int i = 0, size = activityStack.size(); i < size; i++) {
                if (null != activityStack.get(i)) {
                    activityStack.get(i).finish();
                }
            }
            activityStack.clear();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获得所有IdeaCodeActivity
     */
    public List<Activity> getAllActivity() {
        ArrayList<Activity> listActivity = new ArrayList<Activity>();
        for (Activity activity : activityStack) {
            listActivity.add(activity);
        }
        return listActivity;
    }

    /**
     * 检查某个Activity是否在运行
     */
    public boolean checkActivity(Class<?> cls) {

        for (Activity activity : activityStack) {
            if (activity.getClass().equals(cls)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 根据Activity名称返回指定的Activity
     *
     * @param name
     */
    public Activity getActivityByName(String name) {
        for (Activity ia : activityStack) {
            if (ia.getClass().getName().indexOf(name) >= 0) {
                return ia;
            }
        }
        return null;
    }

    /**
     * 退出应用程序
     */
    @SuppressWarnings("deprecation")
    public void AppExit(Context context) {
        try {
            finishAllActivity();
            ActivityManager activityMgr = (ActivityManager) context
                    .getSystemService(Context.ACTIVITY_SERVICE);
            activityMgr.restartPackage(context.getPackageName());
        } catch (Exception e) {
        }
    }
}
