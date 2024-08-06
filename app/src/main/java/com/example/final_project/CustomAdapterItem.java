package com.example.final_project;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.final_project.Item;
import com.example.final_project.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class CustomAdapterItem extends ArrayAdapter {
    Context context;
    int layoutItem;
    ArrayList<Item> lstData = new ArrayList<>();

    public CustomAdapterItem(@NonNull Context context, int resource, ArrayList<Item> lstData) {
        super(context, resource, lstData);
        this.context = context;
        this.layoutItem = resource;
        this.lstData = lstData;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Item item = lstData.get(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(layoutItem, null);
            }
            TextView txtName = (TextView) convertView.findViewById(R.id.txtName);
            txtName.setText("Name: " + item.getName());

            TextView txtCategory = (TextView) convertView.findViewById(R.id.txtCategory);
            txtCategory.setText("Category: " + item.getCategory());

            TextView txtPrice = (TextView) convertView.findViewById(R.id.txtPrice);
            txtPrice.setText("Price: " + item.getPrice());

            ImageView imgView = convertView.findViewById(R.id.imgItem);
            int resourceId = context.getResources().getIdentifier(item.getPhoto(), "drawable", context.getPackageName());
            if (resourceId != 0) {
                imgView.setImageResource(resourceId);
            } else {
                // Nếu không tìm thấy ảnh, có thể hiển thị một ảnh mặc định hoặc thực hiện xử lý khác tùy thuộc vào yêu cầu của bạn.
                imgView.setImageResource(R.drawable.cow); // Đặt ảnh mặc định
            }
        return convertView;
    }

}