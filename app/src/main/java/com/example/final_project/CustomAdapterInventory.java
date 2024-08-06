package com.example.final_project;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class CustomAdapterInventory extends BaseAdapter {
    private Context context;
    private int layoutResourceId;
    private ArrayList<Item> data;

    public CustomAdapterInventory(Context context, int layoutResourceId, ArrayList<Item> data) {
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;

        if (row == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new ViewHolder();
            holder.itemNameTextView = row.findViewById(R.id.txtNameItemInventory);
            holder.itemQuantityTextView = row.findViewById(R.id.txtSlItemInventory);
            holder.imgItemInventory  =row.findViewById(R.id.imgItemInventory) ;

            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        // Lấy dữ liệu từ ArrayList tại vị trí position
        Item itemData = data.get(position);
        // Hiển thị dữ liệu lên TextView
        holder.itemNameTextView.setText(itemData.getName());
        holder.itemQuantityTextView.setText("x" + String.valueOf(itemData.getQuantity()));
        //
        holder.imgItemInventory.setImageResource(itemData.getIdPhoto());
        //
        return row;
    }

    // Tạo một lớp ViewHolder để giữ các view đã tìm thấy
    static class ViewHolder {
        TextView itemNameTextView;
        TextView itemQuantityTextView;
        ImageView imgItemInventory ;
    }
    //read photo from assets

}
