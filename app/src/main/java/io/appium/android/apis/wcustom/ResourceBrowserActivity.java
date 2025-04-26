package io.appium.android.apis.wcustom;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

import io.appium.android.apis.R;

public class ResourceBrowserActivity extends Activity {

    private static final String TAG = "ResourceBrowser";

    private static final List<String> RESOURCE_TYPES = Arrays.asList(
            "drawable", "mipmap", "string", "color", "dimen", "raw", "layout");

    private EditText packageNameInput;
    private Spinner resourceTypeSpinner;
    private ListView resourceListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resource_browser);

        // 初始化 UI 组件
        packageNameInput = findViewById(R.id.package_name_input);
        resourceTypeSpinner = findViewById(R.id.resource_type_spinner);
        resourceListView = findViewById(R.id.resource_list_view);

        // 初始化 Spinner 数据
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, RESOURCE_TYPES);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        resourceTypeSpinner.setAdapter(adapter);

        // 加载资源按钮点击事件
        findViewById(R.id.load_resource_button).setOnClickListener(v -> loadResources());
    }

    private void loadResources() {
        String targetPackageName = packageNameInput.getText().toString().trim();
        String resourceType = resourceTypeSpinner.getSelectedItem().toString();

        // 校验输入
        if (targetPackageName.isEmpty()) {
            Toast.makeText(this, "请输入目标包名", Toast.LENGTH_SHORT).show();
            return;
        }

        // 隐藏输入法
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(packageNameInput.getWindowToken(), 0);
        }

        // 初始化资源工具类
        if (!ResourceHelper.initialize(this, targetPackageName)) {
            Toast.makeText(this, "目标包名无效", Toast.LENGTH_SHORT).show();
            return;
        }

        // 获取资源列表
        List<String> resourceNames = ResourceHelper.getResourceNames(resourceType);
        if (resourceNames.isEmpty()) {
            Toast.makeText(this, "没有找到对应的资源", Toast.LENGTH_SHORT).show();
            return;
        }

        // 设置资源列表适配器
        ResourceAdapter adapter = new ResourceAdapter(this, resourceNames, resourceType);
        resourceListView.setAdapter(adapter);

        // 列表项点击事件
        resourceListView.setOnItemClickListener((parent, view, position, id) -> {
            String resourceName = resourceNames.get(position);
            Toast.makeText(this, "选中资源名称：" + resourceName, Toast.LENGTH_SHORT).show();
        });
    }
}