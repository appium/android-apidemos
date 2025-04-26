package io.appium.android.apis.wcustom;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import io.appium.android.apis.R;

public class ResourceAdapter extends BaseAdapter {

    private final Context context;
    private final List<String> resourceNames;
    private final String resourceType;

    public ResourceAdapter(Context context, List<String> resourceNames, String resourceType) {
        this.context = context;
        this.resourceNames = resourceNames;
        this.resourceType = resourceType;
    }

    @Override
    public int getCount() {
        return resourceNames.size();
    }

    @Override
    public Object getItem(int position) {
        return resourceNames.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_resource, parent, false);
            holder = new ViewHolder();
            holder.resourceName = convertView.findViewById(R.id.resource_name);
            holder.resourcePreview = convertView.findViewById(R.id.resource_preview);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // 设置资源名称
        String resourceName = resourceNames.get(position);
        holder.resourceName.setText(resourceName);

        // 设置资源预览（图像或颜色）
        int resourceId = ResourceHelper.getResourceIdByName(resourceName, resourceType);
        try {
            switch (resourceType) {
                case "drawable":
                case "mipmap":
                    Drawable drawable = ResourceHelper.getDrawable(resourceId);
                    holder.resourcePreview.setImageDrawable(drawable);
                    break;
                case "color":
                    int colorValue = ResourceHelper.getResources().getColor(resourceId, null);
                    holder.resourcePreview.setBackgroundColor(colorValue);
                    holder.resourcePreview.setImageDrawable(null);
                    break;
                default:
                    holder.resourcePreview.setImageDrawable(null);
                    holder.resourcePreview.setBackgroundColor(0x00000000); // 清除背景
                    break;
            }
        } catch (Exception e) {
            holder.resourcePreview.setImageDrawable(null); // 处理加载失败的情况
            holder.resourcePreview.setBackgroundColor(0x00000000); // 设置透明背景
        }

        return convertView;
    }

    private static class ViewHolder {
        TextView resourceName;
        ImageView resourcePreview;
    }
}