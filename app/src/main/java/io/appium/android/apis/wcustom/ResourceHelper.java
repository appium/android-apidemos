package io.appium.android.apis.wcustom;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * ResourceHelper 是一个静态工具类，用于加载和操作指定包名的资源。
 */
public class ResourceHelper {
    private static final String TAG = "ResourceHelper";
    private static Resources resources;
    private static String packageName;

    /**
     * 初始化 ResourceHelper，用于绑定到指定包的资源。
     *
     * @param context           应用上下文
     * @param targetPackageName 目标包名
     * @return 初始化成功返回 true，否则返回 false
     */
    public static boolean initialize(Context context, String targetPackageName) {
        Log.d(TAG, "尝试初始化资源，目标包名: " + targetPackageName);
        try {
            resources = context.getPackageManager().getResourcesForApplication(targetPackageName);
            packageName = targetPackageName;
            Log.d(TAG, "资源初始化成功");
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "无法初始化资源，目标包名无效: " + targetPackageName, e);
            return false;
        }
    }

    /**
     * 提供对 Resources 对象的访问。
     *
     * @return 当前绑定上下文的 Resources 对象
     */
    public static Resources getResources() {
        if (resources == null) {
            Log.e(TAG, "资源尚未初始化，请先调用 initialize 方法");
        }
        return resources;
    }

    /**
     * 根据资源 ID 获取对应的资源内容。
     *
     * @param resourceId 资源 ID
     * @param context    上下文
     * @return 资源的内容
     */
    public static String getById(int resourceId, Context context) {
        if (resources == null) {
            Log.e(TAG, "资源尚未初始化，请先调用 initialize 方法");
            return null;
        }

        try {
            String resourceType = resources.getResourceTypeName(resourceId);
            Log.d(TAG, "获取资源内容，资源 ID: " + resourceId + ", 类型: " + resourceType);

            switch (resourceType) {
                case "string":
                    return resources.getString(resourceId);
                case "color":
                    return "Color: #" + Integer.toHexString(resources.getColor(resourceId, null));
                case "dimen":
                    return "Dimen: " + resources.getDimension(resourceId) + "px";
                case "drawable":
                case "mipmap":
                    return "图片资源：可预览";
                case "layout":
                    return "布局资源：" + resources.getResourceEntryName(resourceId);
                case "raw":
                    return "Raw 资源：" + resources.getResourceEntryName(resourceId);
                default:
                    Log.w(TAG, "未知的资源类型：" + resourceType);
                    return "未知资源类型：" + resourceType;
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "资源未找到，资源 ID: " + resourceId, e);
            return null;
        }
    }
    /**
     * 获取指定类型的所有资源名称，基于分段扫描并动态退出机制。
     *
     * @param resourceType 资源类型（如 "drawable", "string", "attr", "color" 等）
     * @return 资源名称列表
     */
    public static List<String> getResourceNames(String resourceType) {
        List<String> resourceNames = new ArrayList<>();
        if (resources == null) {
            Log.e(TAG, "资源尚未初始化，请先调用 initialize 方法");
            return resourceNames;
        }

        Log.d(TAG, "尝试获取资源名称列表，目标类型: " + resourceType);

// 定义分段范围 —— 按类型的可能区间划分
        int[][] resourceSegments = {
                {0x7f010000, 0x7f01FFFF}, // 包含 "style" 或 "attr"
                {0x7f020000, 0x7f02FFFF}, // 包含 "drawable" 或 "attr"
                {0x7f030000, 0x7f03FFFF}, // 包含 "mipmap" 图标
                {0x7f040000, 0x7f04FFFF}, // 包含 "layout" 布局文件
                {0x7f050000, 0x7f05FFFF}, // 包含 "string" 文本资源
                {0x7f060000, 0x7f06FFFF}, // 包含 "color" 颜色资源
                {0x7f070000, 0x7f07FFFF}, // 包含 "dimen" 尺寸资源
                {0x7f080000, 0x7f08FFFF}, // 包含 "id" 视图 ID
                {0x7f090000, 0x7f09FFFF}, // 包含 "raw" 原始资源文件
                {0x7f0A0000, 0x7f0AFFFF}, // 包含 "array" 数组资源
                {0x7f0B0000, 0x7f0BFFFF}, // 包含 "anim" 动画资源
                {0x7f0C0000, 0x7f0CFFFF}, // 包含 "menu" 菜单资源
                {0x7f0D0000, 0x7f0DFFFF}, // 可能包含 "integer" 或 "bool"
                {0x7f0E0000, 0x7f0EFFFF}, // 可能包含 "fraction" 百分比
                {0x7f0F0000, 0x7f0FFFFF}, // 可能包含 "font" 字体文件
                {0x7f100000, 0x7f10FFFF}, // 可能包含 "transition" 动画切换
                {0x7f110000, 0x7f11FFFF}, // 可能包含 "plurals" 复数字符串
                {0x7f120000, 0x7f12FFFF}, // 可能包含 "xml" 自定义 XML 配置
        };

        int notFoundCountThreshold = 3; // 连续3次未找到资源即退出当前段扫描

        for (int[] segment : resourceSegments) {
            int resourceIdStart = segment[0];
            int resourceIdEnd = segment[1];
            int notFoundCount = 0; // 当前段连续未找到资源的计数器

            Log.d(TAG, String.format("开始扫描资源段 [%08X - %08X]", resourceIdStart, resourceIdEnd));

            for (int resourceId = resourceIdStart; resourceId <= resourceIdEnd; resourceId++) {
                try {
                    // 获取资源实际类型
                    String actualResourceType = resources.getResourceTypeName(resourceId);

                    // 如果资源类型匹配目标类型，则记录资源名称
                    if (resourceType.equals(actualResourceType)) {
                        String resourceName = resources.getResourceEntryName(resourceId);
                        resourceNames.add(resourceName);
                        notFoundCount = 0; // 重置连续未找到计数器
                        Log.v(TAG, "找到资源: " + resourceName + " (ID: " + resourceId + ")");
                    } else {
                        // 不是目标类型资源 - 忽略
                        Log.v(TAG, String.format("忽略资源 ID: %08X (类型: %s, 目标类型: %s)",
                                resourceId, actualResourceType, resourceType));
                    }
                } catch (Resources.NotFoundException e) {
                    notFoundCount++; // 增加未找到计数
                    if (notFoundCount >= notFoundCountThreshold) {
                        Log.d(TAG, "连续 " + notFoundCountThreshold + " 次未找到资源，提前退出当前段扫描");
                        break; // 提前跳出当前段扫描
                    }
                }
            }
        }

        if (resourceNames.isEmpty()) {
            Log.w(TAG, "未找到指定类型的资源: " + resourceType);
        } else {
            Log.d(TAG, "资源扫描完成，共找到 " + resourceNames.size() + " 个 " + resourceType + " 类型资源");
        }

        return resourceNames;
    }
    /**
     * 根据资源名称获取资源 ID。
     *
     * @param resourceName 资源名称
     * @param resourceType 资源类型（如 "drawable", "string" 等）
     * @return 资源 ID
     */
    public static int getResourceIdByName(String resourceName, String resourceType) {
        if (resources == null) {
            Log.e(TAG, "资源尚未初始化，请先调用 initialize 方法");
            return 0;
        }

        Log.d(TAG, "尝试获取资源 ID，资源名称: " + resourceName + ", 类型: " + resourceType);
        int resourceId = resources.getIdentifier(resourceName, resourceType, packageName);
        if (resourceId != 0) {
            Log.d(TAG, "获取资源 ID 成功，资源 ID: " + resourceId);
        } else {
            Log.w(TAG, "无法获取资源 ID，检查资源名称是否正确: " + resourceName);
        }
        return resourceId;
    }

    /**
     * 获取资源类型。
     *
     * @param resourceId 资源 ID
     * @return 资源类型（如 "drawable", "string" 等）
     */
    public static String getResourceType(int resourceId) {
        if (resources == null) {
            Log.e(TAG, "资源尚未初始化，请先调用 initialize 方法");
            return null;
        }
        String resourceType = resources.getResourceTypeName(resourceId);
        Log.d(TAG, "获取资源类型，资源 ID: " + resourceId + ", 类型: " + resourceType);
        return resourceType;
    }

    /**
     * 获取资源的名称。
     *
     * @param resourceId 资源 ID
     * @return 资源的名称
     */
    public static String getResourceName(int resourceId) {
        if (resources == null) {
            Log.e(TAG, "资源尚未初始化，请先调用 initialize 方法");
            return null;
        }
        String resourceName = resources.getResourceEntryName(resourceId);
        Log.d(TAG, "获取资源名称，资源 ID: " + resourceId + ", 名称: " + resourceName);
        return resourceName;
    }

    /**
     * 获取 Drawable 资源。
     *
     * @param resourceId 资源 ID
     * @return Drawable 对象
     */
    public static Drawable getDrawable(int resourceId) {
        if (resources == null) {
            Log.e(TAG, "资源尚未初始化，请先调用 initialize 方法");
            return null;
        }

        Log.d(TAG, "尝试获取 Drawable 资源，资源 ID: " + resourceId);
        try {
            Drawable drawable = resources.getDrawable(resourceId, null);
            Log.d(TAG, "获取 Drawable 成功");
            return drawable;
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Drawable 资源未找到，资源 ID: " + resourceId, e);
            return null;
        }
    }
}