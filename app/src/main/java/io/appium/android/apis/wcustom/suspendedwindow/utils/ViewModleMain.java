package io.appium.android.apis.wcustom.suspendedwindow.utils;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

/**
 * 全局管理悬浮窗的状态。
 */
public class ViewModleMain {

    private static final String TAG = "ViewModleMain-apisdebug";

    // 是否显示悬浮窗
    private static final MutableLiveData<Boolean> isShowSuspendWindow = new MutableLiveData<>(false);

    // 是否显示无障碍浮窗
    private static final MutableLiveData<Boolean> isShowWindow = new MutableLiveData<>(false);

    // 悬浮窗可见性
    private static final MutableLiveData<Boolean> isVisible = new MutableLiveData<>(false);

    /**
     * 获取悬浮窗显示状态的 LiveData。
     */
    public static MutableLiveData<Boolean> getIsShowSuspendWindow() {
        return isShowSuspendWindow;
    }

    /**
     * 获取无障碍悬浮窗状态的 LiveData。
     */
    public static MutableLiveData<Boolean> getIsShowWindow() {
        return isShowWindow;
    }

    /**
     * 获取悬浮窗可见性状态的 LiveData。
     */
    public static MutableLiveData<Boolean> getIsVisible() {
        return isVisible;
    }

    /**
     * 打印当前 LiveData 的状态，用于调试。
     */
    public static void logLiveDataState() {
        Log.d(TAG, "Current State of LiveData:");
        Log.d(TAG, "isShowWindow = " + isShowWindow.getValue());
        Log.d(TAG, "isShowSuspendWindow = " + isShowSuspendWindow.getValue());
        Log.d(TAG, "isVisible = " + isVisible.getValue());
    }
}