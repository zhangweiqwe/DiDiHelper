package cn.zr;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

/**
 * Created by zhongxiang.huang on 2017/6/23.
 */

public class SuspensionWindow implements View.OnTouchListener {

    private static final String TAG = "SuspensionWindow";


    private WindowManager.LayoutParams layoutParams;
    private WindowManager windowManager;

    private View view;

    private int width, height;

    public SuspensionWindow(final Context context) {
        width = height = DensityUtils.dp2px(context, 48);

        Log.d(TAG, "SuspensionWindow(Context context)");

        ImageView imageView = new ImageView(context);
        imageView.setBackgroundResource(R.mipmap.ic_launcher_round);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, MainActivity.class));
            }
        });
        view = imageView;
        view.setOnTouchListener(this);

        layoutParams = new WindowManager.LayoutParams();
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//8.0新特性
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }
        layoutParams.format = PixelFormat.RGBA_8888;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            layoutParams.flags = layoutParams.flags | WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS;
        }
        layoutParams.gravity = Gravity.TOP | Gravity.LEFT;
        layoutParams.width = width;
        layoutParams.height = height;
    }


    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return floatLayoutTouch(motionEvent);
    }


    private float rawX, rawY, x, y;

    private boolean floatLayoutTouch(MotionEvent motionEvent) {

        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x = motionEvent.getX();
                y = motionEvent.getY();
                rawX = motionEvent.getRawX();
                rawY = motionEvent.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                rawX = motionEvent.getRawX();
                rawY = motionEvent.getRawY();
                layoutParams.x = (int) (rawX - x);
                layoutParams.y = (int) (rawY - y);
                windowManager.updateViewLayout(view, layoutParams);
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return false;
    }

    public void show() {
        Log.d(TAG, "show()" + view.getParent() + "" + this.hashCode());

        if (view.getParent() == null) {
            Context context = view.getContext();
            layoutParams.x = context.getResources().getDisplayMetrics().widthPixels;
            layoutParams.y = DensityUtils.dp2px(context, 122);
            windowManager.addView(view, layoutParams);
        }
    }

    public void hide() {
        Log.d(TAG, "hide()" + view.getParent());
        if (view.getParent() != null)
            windowManager.removeView(view);
    }


}

