package io.appium.android.apis.wcustom.suspendedwindow;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.Nullable;

import io.appium.android.apis.R;
import io.appium.android.apis.wcustom.suspendedwindow.service.SuspendwindowService;
import io.appium.android.apis.wcustom.suspendedwindow.utils.Utils;
import io.appium.android.apis.wcustom.suspendedwindow.utils.ViewModleMain;
import io.appium.android.apis.wcustom.suspendedwindow.utils.ItemViewTouchListener;

public class SuspendedwindowActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "SuspendedwindowActivity-apisdebug";

    private View floatRootView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) { // 修改了签名
        super.onCreate(savedInstanceState); // 使用正确的 super 方法调用

        Log.d(TAG, "onCreate: Activity 初始化");
        setContentView(R.layout.spw_activity_main);

        // 启动悬浮窗服务
        startService(new Intent(this, SuspendwindowService.class));
        Log.d(TAG, "onCreate: SuspendwindowService 已启动");

        // 设置监听事件
        findViewById(R.id.bt_01).setOnClickListener(this);
        findViewById(R.id.bt_02).setOnClickListener(this);
        findViewById(R.id.bt_03).setOnClickListener(this);
        findViewById(R.id.bt_04).setOnClickListener(this);
        findViewById(R.id.bt_05).setOnClickListener(this);
        Log.d(TAG, "onCreate: 按钮点击监听已设置");
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        Log.d(TAG, "onClick: 按钮点击，ID = " + id);

        if (id == R.id.bt_02) { // 打开悬浮窗
            Utils.checkSuspendedWindowPermission(this, () -> {
                Log.d(TAG, "onClick: 悬浮窗权限已授予");
                ViewModleMain.getIsShowSuspendWindow().setValue(true); // 打开悬浮窗
                ViewModleMain.logLiveDataState(); // 打印状态
            });
        } else if (id == R.id.bt_03) { // 打开无障碍模式悬浮窗
            Utils.checkAccessibilityPermission(this, () -> {
                Log.d(TAG, "onClick: 无障碍权限已授予");
                ViewModleMain.getIsShowWindow().setValue(true); // 打开无障碍悬浮窗
                ViewModleMain.logLiveDataState(); // 打印 LiveData 状态
            });
        } else if (id == R.id.bt_04) { // 前台模式悬浮窗
            Utils.checkSuspendedWindowPermission(this, () -> {
                Log.d(TAG, "onClick: 前台模式权限已授予");
                ViewModleMain.getIsShowSuspendWindow().setValue(true); // 前台模式悬浮窗
                ViewModleMain.logLiveDataState(); // 打印状态
            });
        } else if (id == R.id.bt_05) { // 关闭所有悬浮窗
            Log.d(TAG, "onClick: 请求关闭所有悬浮窗");
            ViewModleMain.getIsShowSuspendWindow().setValue(false); // 关闭悬浮窗
            ViewModleMain.getIsShowWindow().setValue(false); // 重置无障碍悬浮窗状态
            ViewModleMain.logLiveDataState(); // 打印状态
        } else if (id == R.id.bt_01) { // 单独显示悬浮窗
            showCurrentWindow();
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void showCurrentWindow() {
        if (floatRootView != null) {
            Log.d(TAG, "showCurrentWindow: 悬浮窗已经显示，避免重复添加");
            Toast.makeText(this, "悬浮窗已显示", Toast.LENGTH_SHORT).show();
            return;
        }

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

        floatRootView = LayoutInflater.from(this).inflate(R.layout.spw_activity_float_item, null);
        floatRootView.setOnTouchListener(new ItemViewTouchListener(layoutParams, getWindowManager()));
        getWindowManager().addView(floatRootView, layoutParams);

        Log.d(TAG, "showCurrentWindow: 悬浮窗已显示");
    }
}