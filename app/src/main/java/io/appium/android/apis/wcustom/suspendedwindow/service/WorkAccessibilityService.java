package io.appium.android.apis.wcustom.suspendedwindow.service;

import android.accessibilityservice.AccessibilityService;
import android.graphics.PixelFormat;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;

import androidx.lifecycle.Observer;

import io.appium.android.apis.R;
import io.appium.android.apis.wcustom.suspendedwindow.utils.ViewModleMain;

public class WorkAccessibilityService extends AccessibilityService {

    private static final String TAG = "WorkAccessibilityService-apisdebug";

    private WindowManager windowManager;
    private View floatRootView;

    private final Observer<Boolean> isShowWindowObserver = isShow -> {
        Log.d(TAG, "isShowWindowObserver: 悬浮窗状态改变，显示: " + isShow);
        if (isShow != null && isShow) {
            showWindow();
        } else {
            removeWindow();
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        ViewModleMain.getIsShowWindow().observeForever(isShowWindowObserver);
        Log.d(TAG, "onCreate: 开始观察 isShowWindow");
    }

    private void showWindow() {
        if (windowManager == null) {
            windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        }

        if (floatRootView == null) {
            floatRootView = LayoutInflater.from(this).inflate(R.layout.spw_activity_float_item, null);

            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.type = WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY;
            layoutParams.format = PixelFormat.TRANSPARENT;
            layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

            try {
                windowManager.addView(floatRootView, layoutParams);
                Log.d(TAG, "showWindow: 悬浮窗已添加");
            } catch (Exception e) {
                Log.e(TAG, "showWindow: 添加悬浮窗失败，原因: " + e.getMessage(), e);
            }
        }
    }

    private void removeWindow() {
        if (floatRootView != null && windowManager != null) {
            try {
                windowManager.removeView(floatRootView);
                floatRootView = null;
                Log.d(TAG, "removeWindow: 悬浮窗已移除");
            } catch (Exception e) {
                Log.e(TAG, "removeWindow: 移除悬浮窗失败，原因: " + e.getMessage(), e);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        removeWindow();
        ViewModleMain.getIsShowWindow().removeObserver(isShowWindowObserver);
        Log.d(TAG, "onDestroy: 观察者已移除");
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        // 不处理事件
    }

    @Override
    public void onInterrupt() {
        // 无需处理中断
    }
}