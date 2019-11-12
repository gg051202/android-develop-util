package a26c.com.android_frame_test.util;

public class PermissionData {
    private String permissionName;
    /**
     * 该权限是否拥有
     */
    private boolean isGranted;
    /**
     * 申请权限的结果，true表示用户通过，false表示用户未通过
     */
    private boolean result;

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public boolean isGranted() {
        return isGranted;
    }

    public void setGranted(boolean granted) {
        isGranted = granted;
    }

    public String getPermissionName() {
        return permissionName;
    }

    public void setPermissionName(String permissionName) {
        this.permissionName = permissionName;
    }
}