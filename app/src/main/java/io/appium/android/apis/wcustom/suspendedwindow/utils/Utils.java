package io.appium.android.apis.wcustom.suspendedwindow.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import io.appium.android.apis.wcustom.suspendedwindow.service.WorkAccessibilityService;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @功能: 工具类
 * @User Lmy
 * @Creat 4/16/21 8:33 AM
 * @Compony 永远相信美好的事情即将发生
 */
public class Utils {

    public static final int REQUEST_FLOAT_CODE = 1001;
    private static final String TAG = "Utils-apisdebug";

    /**
     * 跳转到设置页面申请打开无障碍辅助功能
     */
    private static void accessibilityToSettingPage(Context context) {
        try {
            // 开启辅助功能页面
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            Intent intent = new Intent(Settings.ACTION_SETTINGS);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            e.printStackTrace();
        }
    }

    /**
     * 判断 Service 是否开启
     */
    public static boolean isServiceRunning(Context context, String serviceName) {
        if (TextUtils.isEmpty(serviceName)) {
            return false;
        }
        ActivityManager myManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningService = myManager.getRunningServices(1000);
        for (ActivityManager.RunningServiceInfo serviceInfo : runningService) {
            if (serviceInfo.service.getClassName().equals(serviceName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断悬浮窗权限
     */
    private static boolean commonROMPermissionCheck(Context context) {
        boolean result = true;
        if (Build.VERSION.SDK_INT >= 23) {
            try {
                Class<?> clazz = Settings.class;
                Method canDrawOverlays = clazz.getDeclaredMethod("canDrawOverlays", Context.class);
                result = (Boolean) canDrawOverlays.invoke(null, context);
            } catch (Exception e) {
                Log.e(TAG, Log.getStackTraceString(e));
            }
        }
        return result;
    }

    /**
     * 检查悬浮窗权限是否开启
     */
    public static void checkSuspendedWindowPermission(Activity context, Runnable block) {
        if (commonROMPermissionCheck(context)) {
            block.run();
        } else {
            Toast.makeText(context, "请开启悬浮窗权限", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            intent.setData(Uri.parse("package:" + context.getPackageName()));
            context.startActivityForResult(intent, REQUEST_FLOAT_CODE);
        }
    }

    /**
     * 检查无障碍服务权限是否开启
     */
    public static void checkAccessibilityPermission(Activity context, Runnable block) {
        if (isServiceRunning(context, WorkAccessibilityService.class.getCanonicalName())) {
            block.run();
        } else {
            accessibilityToSettingPage(context);
        }
    }

    public static boolean isNull(Object obj) {
        return obj == null;
    }
}