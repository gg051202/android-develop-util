package a26c.com.android_frame_test.util;

public interface OnCheckPermissionListener {
        /**
         * 申请权限成功，所有权限全部允许才算申请成功
         */
        void success();

        /**
         * 申请权限失败
         */
        boolean fail();
    }