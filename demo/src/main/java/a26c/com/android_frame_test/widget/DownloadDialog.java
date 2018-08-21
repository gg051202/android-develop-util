package a26c.com.android_frame_test.widget;

import android.app.Activity;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import a26c.com.android_frame_test.R;

public class DownloadDialog {


    private AlertDialog alertDialog;
    private Activity activity;

    public DownloadDialog(Activity activity) {
        this.activity = activity;
    }

    public void show() {
        if (alertDialog == null) {
            View view = LayoutInflater.from(activity).inflate(R.layout.dialog_download_dialog, null);
            alertDialog = new AlertDialog.Builder(activity, R.style.NoFrameDialogStyle)
                    .setView(view)
                    .show();
            Window window = alertDialog.getWindow();
            if (window != null) {
                window.setGravity(Gravity.CENTER);
            }

        }
        if (!alertDialog.isShowing()) {
            alertDialog.show();
        }

    }

}

