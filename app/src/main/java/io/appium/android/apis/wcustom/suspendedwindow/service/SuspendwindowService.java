package io.appium.android.apis.wcustom.suspendedwindow.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import androidx.lifecycle.Observer;

import io.appium.android.apis.R;
import io.appium.android.apis.wcustom.suspendedwindow.utils.ItemViewTouchListener;
import io.appium.android.apis.wcustom.suspendedwindow.utils.ViewModleMain;

public class SuspendwindowService extends Service {

    private static final String TAG = "SuspendwindowService-apisdebug";

    private WindowManager windowManager;
    private View floatRootView;

    private final Observer<Boolean> isShowSuspendWindowObserver = isShow -> {
        Log.d(TAG, "isShowSuspendWindowObserver: 悬浮窗状态改变，显示: " + isShow);
        if (Boolean.TRUE.equals(isShow)) {
            showWindow();
        } else {
            removeWindow();
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: 服务启动");
        ViewModleMain.getIsShowSuspendWindow().observeForever(isShowSuspendWindowObserver);
        Log.d(TAG, "onCreate: 已订阅 isShowSuspendWindow 变化");
    }

    private void showWindow() {
        if (windowManager == null) {
            windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
            Log.d(TAG, "showWindow: 初始化 WindowManager");
        }

        if (floatRootView == null) {
            floatRootView = LayoutInflater.from(this).inflate(R.layout.spw_activity_float_item, null);

            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            } else {
                layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
            }

            layoutParams.format = PixelFormat.RGBA_8888;
            layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            layoutParams.gravity = Gravity.START | Gravity.TOP;
            layoutParams.x = 200;
            layoutParams.y = 200;

            floatRootView.setOnTouchListener(new ItemViewTouchListener(layoutParams, windowManager));
            Log.d(TAG, "showWindow: 配置悬浮窗属性");

            try {
                windowManager.addView(floatRootView, layoutParams);
                Log.d(TAG, "showWindow: 悬浮窗已成功显示");
            } catch (Exception e) {
                Log.e(TAG, "showWindow: 悬浮窗显示失败，原因: " + e.getMessage(), e);
            }
        }
    }

    private void removeWindow() {
        if (floatRootView != null && windowManager != null) {
            try {
                windowManager.removeView(floatRootView);
                Log.d(TAG, "removeWindow: 悬浮窗已移除");
            } catch (Exception e) {
                Log.e(TAG, "removeWindow: 移除悬浮窗失败，原因: " + e.getMessage(), e);
            } finally {
                floatRootView = null;
            }
        }
    }

    @Override
    public void onDestroy() {
        removeWindow();
        ViewModleMain.getIsShowSuspendWindow().removeObserver(isShowSuspendWindowObserver);
        Log.d(TAG, "onDestroy: 服务销毁，并解除订阅");
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}