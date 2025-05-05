package io.appium.android.apis.wcustom.suspendedwindow.utils;

import android.graphics.PixelFormat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

/**
 * 实现悬浮窗的触摸拖动逻辑。
 */
public class ItemViewTouchListener implements View.OnTouchListener {

    private static final String TAG = "ItemViewTouchListener-apisdebug";

    private final WindowManager.LayoutParams layoutParams;
    private final WindowManager windowManager;
    private int lastX, lastY; // 触摸点（X, Y）
    private int paramX, paramY; // 悬浮窗位置（X, Y）

    /**
     * 构造器，初始化触摸监听器。
     *
     * @param layoutParams 悬浮窗的布局参数
     * @param windowManager 悬浮窗的 WindowManager
     */
    public ItemViewTouchListener(WindowManager.LayoutParams layoutParams, WindowManager windowManager) {
        if (layoutParams == null || windowManager == null) {
            throw new IllegalArgumentException("layoutParams and windowManager cannot be null");
        }
        this.layoutParams = layoutParams;
        this.windowManager = windowManager;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 记录手指初次按下的位置
                lastX = (int) event.getRawX();
                lastY = (int) event.getRawY();
                // 记录悬浮窗的当前坐标
                paramX = layoutParams.x;
                paramY = layoutParams.y;
                Log.d(TAG, "ACTION_DOWN: lastX=" + lastX + ", lastY=" + lastY);
                return true;

            case MotionEvent.ACTION_MOVE:
                // 计算鼠标移动的距离
                int dx = (int) event.getRawX() - lastX;
                int dy = (int) event.getRawY() - lastY;

                // 更新悬浮窗的坐标位置
                layoutParams.x = paramX + dx;
                layoutParams.y = paramY + dy;

                if (windowManager != null) {
                    // 刷新悬浮窗的位置
                    windowManager.updateViewLayout(v, layoutParams);
                    Log.d(TAG, "ACTION_MOVE: Updated (x=" + layoutParams.x + ", y=" + layoutParams.y + ")");
                }
                return true;

            default:
                // 未处理的触摸事件
                return false;
        }
    }
}