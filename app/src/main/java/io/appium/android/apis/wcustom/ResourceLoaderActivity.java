package io.appium.android.apis.wcustom;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.appium.android.apis.R;

public class ResourceLoaderActivity extends Activity {
    private Spinner resourceTypeSpinner;
    private Button loadResourceButton;
    private TextView resourceInfoText;
    private FrameLayout resourcePreviewArea;

    private CheckBox checkboxUpDown;
    private CheckBox checkboxLeftRight;
    private CheckBox checkboxScaleUp;
    private CheckBox checkboxScaleDown;

    private Spinner interpolatorSpinner;
    private Button startAnimationButton;
    private ImageView animatingView;

    private Interpolator selectedInterpolator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resource_loader);

        // 初始化控件
        resourceTypeSpinner = findViewById(R.id.resource_type_spinner);
        loadResourceButton = findViewById(R.id.load_resource_button);
        resourceInfoText = findViewById(R.id.resource_info_text);
        resourcePreviewArea = findViewById(R.id.resource_preview_area);

        checkboxUpDown = findViewById(R.id.checkbox_up_down);
        checkboxLeftRight = findViewById(R.id.checkbox_left_right);
        checkboxScaleUp = findViewById(R.id.checkbox_scale_up);
        checkboxScaleDown = findViewById(R.id.checkbox_scale_down);

        interpolatorSpinner = findViewById(R.id.interpolator_spinner);
        startAnimationButton = findViewById(R.id.start_interpolator_animation_button);
        animatingView = findViewById(R.id.animating_view);

        // 配置下拉列表和事件
        setupResourceTypeSpinner();
        setupInterpolatorSpinner();

        // 加载资源按钮事件
        loadResourceButton.setOnClickListener(v -> {
            String selectedType = (String) resourceTypeSpinner.getSelectedItem();
            handleResourceLoading(selectedType);
        });

        // 启动动画按钮事件
        startAnimationButton.setOnClickListener(v -> {
            // 如果没有选择插值器，弹出提示
            if (selectedInterpolator == null) {
                resourceInfoText.setText("请先选择插值器！");
                return;
            }

            // 如果资源预览视图是可见的，隐藏它
            if (resourcePreviewArea.getVisibility() == View.VISIBLE) {
                resourcePreviewArea.setVisibility(View.GONE);
            }

            // 检查是否有选中的动画类型
            if (!checkboxUpDown.isChecked() && !checkboxLeftRight.isChecked() &&
                    !checkboxScaleUp.isChecked() && !checkboxScaleDown.isChecked()) {
                resourceInfoText.setText("请至少选择一种动画效果！");
                return;
            }

            // 启动动画
            animatingView.setVisibility(View.VISIBLE);
            startAnimations();
        });
    }

    private void setupResourceTypeSpinner() {
        List<String> resourceOptions = Arrays.asList("Drawable", "String", "Color", "Raw");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, resourceOptions
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        resourceTypeSpinner.setAdapter(adapter);

        resourceTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                resourceInfoText.setText("当前选择资源类型: " + resourceOptions.get(position));
                resourcePreviewArea.setVisibility(View.GONE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                resourceInfoText.setText("未选择任何资源类型");
            }
        });
    }

    private void handleResourceLoading(String selectedType) {
        switch (selectedType) {
            case "Drawable":
                loadDrawableResource();
                break;
            case "String":
                loadStringResource();
                break;
            case "Color":
                loadColorResource();
                break;
            case "Raw":
                loadRawResource();
                break;
        }
    }

    private void loadDrawableResource() {
        Drawable drawable = getResources().getDrawable(R.drawable.sample_image);
        resourceInfoText.setText("Drawable 加载成功：res/drawable/sample_image");

        ImageView imageView = new ImageView(this);
        imageView.setImageDrawable(drawable);
        resourcePreviewArea.removeAllViews();
        resourcePreviewArea.addView(imageView);
        resourcePreviewArea.setVisibility(View.VISIBLE);
    }

    private void loadStringResource() {
        String value = getResources().getString(R.string.sample_string);
        resourceInfoText.setText("String 资源加载成功：" + value);
        resourcePreviewArea.setVisibility(View.GONE);
    }

    private void loadColorResource() {
        int color = getResources().getColor(R.color.sample_color);
        String colorHex = String.format("#%08X", 0xFFFFFFFF & color);
        resourceInfoText.setText("Color 加载成功！值：" + colorHex);

        View colorPreview = new View(this);
        colorPreview.setBackgroundColor(color);
        colorPreview.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        ));
        resourcePreviewArea.removeAllViews();
        resourcePreviewArea.addView(colorPreview);
        resourcePreviewArea.setVisibility(View.VISIBLE);
    }

    private void loadRawResource() {
        resourceInfoText.setText("Raw 文件：res/raw/sample_file.txt");
        resourcePreviewArea.setVisibility(View.GONE);
    }

    private void setupInterpolatorSpinner() {
        try {
            Class<?> interpolatorClass = Class.forName("android.R$interpolator");
            Field[] fields = interpolatorClass.getDeclaredFields();

            List<String> interpolatorNames = new ArrayList<>();
            List<Integer> interpolatorIds = new ArrayList<>();

            for (Field field : fields) {
                interpolatorNames.add(field.getName());
                interpolatorIds.add((Integer) field.get(null));
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    this, android.R.layout.simple_spinner_item, interpolatorNames
            );
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            interpolatorSpinner.setAdapter(adapter);

            interpolatorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    selectedInterpolator = AnimationUtils.loadInterpolator(
                            ResourceLoaderActivity.this, interpolatorIds.get(position));
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    selectedInterpolator = null; // 没有选择插值器
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startAnimations() {
        if (checkboxUpDown.isChecked()) startUpDownAnimation();
        if (checkboxLeftRight.isChecked()) startLeftRightAnimation();
        if (checkboxScaleUp.isChecked()) startScaleUpAnimation();
        if (checkboxScaleDown.isChecked()) startScaleDownAnimation();
    }

    private void startUpDownAnimation() {
        ValueAnimator animator = ValueAnimator.ofFloat(0, 400);
        animator.setDuration(2000);
        animator.setInterpolator(selectedInterpolator);
        animator.addUpdateListener(animation -> animatingView.setTranslationY((float) animation.getAnimatedValue()));
        animator.start();
    }

    private void startLeftRightAnimation() {
        ValueAnimator animator = ValueAnimator.ofFloat(0, 400);
        animator.setDuration(2000);
        animator.setInterpolator(selectedInterpolator);
        animator.addUpdateListener(animation -> animatingView.setTranslationX((float) animation.getAnimatedValue()));
        animator.start();
    }

    private void startScaleUpAnimation() {
        ValueAnimator animator = ValueAnimator.ofFloat(1f, 2f);
        animator.setDuration(2000);
        animator.setInterpolator(selectedInterpolator);
        animator.addUpdateListener(animation -> {
            float value = (float) animation.getAnimatedValue();
            animatingView.setScaleX(value);
            animatingView.setScaleY(value);
        });
        animator.start();
    }

    private void startScaleDownAnimation() {
        ValueAnimator animator = ValueAnimator.ofFloat(2f, 1f);
        animator.setDuration(2000);
        animator.setInterpolator(selectedInterpolator);
        animator.addUpdateListener(animation -> {
            float value = (float) animation.getAnimatedValue();
            animatingView.setScaleX(value);
            animatingView.setScaleY(value);
        });
        animator.start();
    }
}