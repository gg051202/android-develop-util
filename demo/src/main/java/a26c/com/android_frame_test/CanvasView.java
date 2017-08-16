package a26c.com.android_frame_test;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

/**
 * Created by guilinlin on 2017/8/15 09:52.
 * email 973635949@qq.com
 */

public class CanvasView extends SurfaceView implements SurfaceHolder.Callback {

    private Paint paint;
    private LinearGradient shader;

    public CanvasView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);
        paint = new Paint();
        paint.setDither(true);
        paint.setAntiAlias(true);
        shader = new LinearGradient(0, 0, 0, 500, 0xff000000, 0x00000000, Shader.TileMode.CLAMP);
        setLayerType(View.LAYER_TYPE_SOFTWARE, paint);

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Canvas canvas = holder.lockCanvas();
        canvas.drawColor(0xffffffff);
        paint.setShader(shader);
        Path path = new Path();
        path.moveTo(0, 500);
        path.lineTo(100, 0);
        path.lineTo(200, 0);
        path.lineTo(200, 400);
        path.lineTo(300, 400);
        path.lineTo(200, 500);


        canvas.drawPath(path, paint);
        holder.unlockCanvasAndPost(canvas);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}
