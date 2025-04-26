package io.appium.android.apis.wcustom;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import io.appium.android.apis.R;

public class ResourceSearchActivity extends Activity {

    private EditText packageNameInput; // 输入包名
    private EditText resourceInput; // 输入资源名称或 ID
    private TextView resourceNameView; // 显示资源名称
    private TextView resourceValueView; // 显示资源值
    private ImageView resourceImageView; // 显示图片或颜色示例

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resource_display);

        // 初始化视图
        packageNameInput = findViewById(R.id.package_name_input);
        resourceInput = findViewById(R.id.resource_id_input); // 合并了资源 ID 和名称的输入框
        resourceNameView = findViewById(R.id.resource_name);
        resourceValueView = findViewById(R.id.resource_value);
        resourceImageView = findViewById(R.id.resource_image);
        Button loadResourceButton = findViewById(R.id.load_resource_button);

        // 按钮点击事件
        loadResourceButton.setOnClickListener(v -> loadResource());
    }

    /**
     * 加载并显示资源
     */
    private void loadResource() {
        String packageName = packageNameInput.getText().toString().trim();
        String resourceInputText = resourceInput.getText().toString().trim();

        if (packageName.isEmpty() || resourceInputText.isEmpty()) {
            Toast.makeText(this, "请输入包名和资源！", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // 初始化 ResourceHelper
            if (!ResourceHelper.initialize(this, packageName)) {
                Toast.makeText(this, "无法加载目标资源包", Toast.LENGTH_SHORT).show();
                return;
            }
            // 隐藏输入法
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(packageNameInput.getWindowToken(), 0);
            }
            // 自动解析资源输入
            int resourceId = parseResource(packageName, resourceInputText);

            // 使用 ResourceHelper 加载并显示内容
            String resourceContent = ResourceHelper.getById(resourceId, this); // 传入上下文

            if (resourceContent != null) {
                // 显示资源内容
                resourceNameView.setText(String.format("资源名称: %s", resourceInputText));
                resourceValueView.setText(resourceContent);
                handleResourceDisplay(resourceId, resourceContent);
            } else {
                Toast.makeText(this, "无法识别资源内容", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "资源加载失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 自动解析资源输入，判断是资源名称还是资源 ID，返回资源 ID
     *
     * @param packageName       包名
     * @param resourceInputText 用户输入的资源名称或 ID
     * @return 资源 ID
     */
    private int parseResource(String packageName, String resourceInputText) {
        try {
            Resources resources = ResourceHelper.getResources();

            // 检查是否为资源 ID
            if (isResourceIdInput(resourceInputText)) {
                return parseResourceId(resourceInputText);
            } else {
                // 否则，认为是资源名称，通过名称解析资源 ID
                int resourceId = resources.getIdentifier(resourceInputText, null, packageName);
                if (resourceId == 0) {
                    throw new Resources.NotFoundException("资源未找到: " + resourceInputText);
                }
                return resourceId;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "资源 ID 格式无效: " + resourceInputText, Toast.LENGTH_SHORT).show();
            throw e; // 保证调用方捕获异常
        } catch (Resources.NotFoundException e) {
            Toast.makeText(this, "资源未找到: " + resourceInputText, Toast.LENGTH_SHORT).show();
            throw e;
        }
    }

    /**
     * 判断输入是否为资源 ID
     */
    private boolean isResourceIdInput(String input) {
        // 检测是否为纯数字或以 0x 开头的 16 进制内容
        return input.matches("\\d+") || input.startsWith("0x") || input.startsWith("0X");
    }

    /**
     * 解析资源 ID (支持十进制和 16 进制)
     */
    private int parseResourceId(String resourceIdString) throws NumberFormatException {
        if (resourceIdString.startsWith("0x") || resourceIdString.startsWith("0X")) {
            return Integer.parseInt(resourceIdString.substring(2), 16); // 16 进制
        } else {
            return Integer.parseInt(resourceIdString); // 十进制
        }
    }

    /**
     * 根据资源内容，显示图片、颜色或文本内容
     */
    private void handleResourceDisplay(int resourceId, String resourceContent) {
        resourceImageView.setVisibility(View.GONE); // 默认隐藏图片视图

        // 如果是颜色资源，动态设置背景颜色
        if (resourceContent.startsWith("Color:")) {
            int colorValue = ResourceHelper.getResources().getColor(resourceId, null);
            animateColorTransition(resourceImageView, colorValue, 1000); // 1 秒颜色过渡
            resourceImageView.setVisibility(View.VISIBLE);
        }

        // 如果是图片资源，显示图片
        if (resourceContent.contains("图片资源") || resourceContent.contains("Drawable")) {
            resourceImageView.setImageDrawable(ResourceHelper.getResources().getDrawable(resourceId, this.getTheme()));
            resourceImageView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 动态颜色过渡效果
     */
    private void animateColorTransition(View view, int toColor, int duration) {
        int fromColor = ((Integer) view.getTag() != null) ? (Integer) view.getTag() : 0xFFFFFFFF; // 默认为白色
        ObjectAnimator colorAnimator = ObjectAnimator.ofObject(view, "backgroundColor", new ArgbEvaluator(), fromColor, toColor);
        colorAnimator.setDuration(duration);
        colorAnimator.start();
        view.setTag(toColor);
    }
}