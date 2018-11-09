package cn.zr;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import cn.zr.activity.MainActivity;
import cn.zr.util.DensityUtils;

/**
 * Created by zhongxiang.huang on 2017/6/23.
 */

public class SuspensionWindow implements View.OnTouchListener {


    private static SuspensionWindow INSTANCE = null;

    public static void showSuspensionWindow(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new SuspensionWindow(context.getApplicationContext());
        }
        INSTANCE.show();
    }

    public static void dismissSuspensionWindow() {
        if (INSTANCE != null) {
            INSTANCE.hide();
        }
    }

    private static final String TAG = "SuspensionWindow";


    private WindowManager.LayoutParams layoutParams;
    private WindowManager windowManager;

    private View view;

    private int width, height;


    private static final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (INSTANCE != null) {
                INSTANCE.textView.setText((CharSequence) msg.obj);
            }
        }
    };

    public static void showMsg(CharSequence charSequence) {
        if (charSequence == null || INSTANCE == null) {
            return;
        }
        Message msg = Message.obtain();
        msg.obj = charSequence;
        handler.sendMessage(msg);
    }

    private TextView textView;

    private SuspensionWindow(final Context context) {
        width = height = DensityUtils.dp2px(context, 46);

        Log.d(TAG, "SuspensionWindow(Context context)");

        textView = new TextView(context);
        textView.setBackgroundColor(0xaa000000);
        textView.setTextColor(0xffffffff);
        textView.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        textView.setText(context.getString(R.string.app_name));
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
        view = textView;
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


    private float rawX0, rawY0, rawX, rawY, x, y;


    private boolean move = false;

    private boolean floatLayoutTouch(MotionEvent motionEvent) {

        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x = motionEvent.getX();
                y = motionEvent.getY();
                rawX0 = motionEvent.getRawX();
                rawY0 = motionEvent.getRawY();
                move = false;
                break;
            case MotionEvent.ACTION_MOVE:
                rawX = motionEvent.getRawX();
                rawY = motionEvent.getRawY();
                layoutParams.x = (int) (rawX - x);
                layoutParams.y = (int) (rawY - y);
                windowManager.updateViewLayout(view, layoutParams);
                if (Math.abs(rawX0 - rawX) > 10 || Math.abs(rawY0 - rawY) > 10) {
                    move = true;
                } else {
                    move = false;
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return move;
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

