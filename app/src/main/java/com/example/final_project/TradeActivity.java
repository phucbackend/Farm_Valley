package com.example.final_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class TradeActivity extends AppCompatActivity {
    MediaPlayer backgroundMusic;
    ImageButton close;
    Handler handler;
    Runnable runnable;
    ListView lstData ;
    CustomAdapterInventory customAdapter ;
    ArrayList<Item> itemDataList ;
    FirebaseAuth auth ;
    FirebaseUser firebaseUser ;
    DatabaseReference coinBalance ;
    DatabaseReference expBalance ;
    int userCoinBalance  ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trade);
        //
        auth = FirebaseAuth.getInstance() ;
        firebaseUser = auth.getCurrentUser() ;
        //
        addControls();
        addEvents();
        backgroundMusic = MediaPlayer.create(this, R.raw.shopandtrade_background_music);
        handler = new Handler();
        playMusic();
        //
        if (firebaseUser != null){
            showAllInventory();
        }

    }
    private void showDialogBuyItem(Item selectedItem) {

        final Dialog dialogBuyItem = new Dialog(TradeActivity.this);
        dialogBuyItem.setContentView(R.layout.dialog_sell_item);

        // Khởi tạo các view trong hộp thoại

        TextView txtItem = dialogBuyItem.findViewById(R.id.txtItemSell);
        TextView txtPrice = dialogBuyItem.findViewById(R.id.txtPriceSell);
        Button btnOK = dialogBuyItem.findViewById(R.id.btnOKSell);
        Button btnCancel = dialogBuyItem.findViewById(R.id.btnCancelSell);
        ImageButton btnPlus = dialogBuyItem.findViewById(R.id.btnPlusSell);
        ImageButton btnMinus = dialogBuyItem.findViewById(R.id.btnMinusSell);
        TextView txtQuantity = dialogBuyItem.findViewById(R.id.txtEditNumSell);
        TextView txtTotalPrice = dialogBuyItem.findViewById(R.id.txtTotalPriceSell);
        // Đặt giá trị dựa trên mục đã chọn
        txtItem.setText(selectedItem.getName());
        // Lấy giá của mục hàng được chọn
        int itemPrice = selectedItem.getSellPrice() ;
        txtPrice.setText(String.valueOf(itemPrice));
        // Khởi tạo giá trị cho số lượng và tổng giá
        final int[] soLuong = {1};
        final int[] tongGia = {itemPrice * soLuong[0]};
        // Đặt giá trị ban đầu cho txtEditNum và txtTotalPrice
        txtQuantity.setText(String.valueOf(soLuong[0]));
        txtTotalPrice.setText(String.valueOf(tongGia[0]));

        btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    soLuong[0]++;
                    txtQuantity.setText(String.valueOf(soLuong[0]));
                    tongGia[0] = itemPrice * soLuong[0];
                    txtTotalPrice.setText(String.valueOf(tongGia[0]));

            }
        });

        btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (soLuong[0] > 1) {
                    soLuong[0]--;
                    txtQuantity.setText(String.valueOf(soLuong[0]));
                    tongGia[0] = itemPrice * soLuong[0];
                    txtTotalPrice.setText(String.valueOf(tongGia[0]));
                }
                else
                    Toast.makeText(TradeActivity.this,"Số lượng không được bé hơn 1",Toast.LENGTH_SHORT).show();
            }
        });
        //
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                if (firebaseUser != null){
                    coinBalance = FirebaseDatabase.getInstance().getReference().child("users").child(firebaseUser.getUid()).child("coin") ;
                    coinBalance.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            userCoinBalance = snapshot.getValue(Integer.class);
                            int currentQuantity = selectedItem.getQuantity() ;
                            int sellQuantity = Integer.parseInt(txtQuantity.getText().toString()) ;
                            int totalCost = tongGia[0];
                            if (currentQuantity - sellQuantity >= 0) {
                                String selectedItemName = selectedItem.getId();
                                String selectedItemCategory = selectedItem.getCategory();
                                int selectedItemExp = selectedItem.getExp() ;
                                performPurchase(totalCost , selectedItemName , selectedItemCategory , Integer.parseInt(txtQuantity.getText().toString()) , selectedItemExp);
                                Toast.makeText(TradeActivity.this, "Bán thành công", Toast.LENGTH_LONG).show();
                                dialogBuyItem.dismiss();
                            } else {
                                // Insufficient coins, show a message to the user
                                Toast.makeText(TradeActivity.this, "Không đủ số lượng để bán !", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

                dialogBuyItem.dismiss();

            }
        });
        //
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Đóng hộp thoại khi nhấn nút Cancel
                dialogBuyItem.dismiss();
            }
        });

        Window window = dialogBuyItem.getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(window.getAttributes());
            layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(layoutParams);
        }

        // Hiển thị hộp thoại
        dialogBuyItem.show();
    }

    private void performPurchase(int totalCost , String itemName , String itemCategory , int quantity , int exp) {
        // Update the user's coin balance on Firebase
        if (firebaseUser != null){
            DatabaseReference itemQuantityRef = FirebaseDatabase.getInstance()
                    .getReference().child("users")
                    .child(firebaseUser.getUid()).child("item")
                    .child(itemCategory).child(itemName).child("quantity");
            itemQuantityRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                int currentQuantity = snapshot.getValue(Integer.class) ;
                itemQuantityRef.setValue(currentQuantity - quantity) ;
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            coinBalance.setValue(userCoinBalance + totalCost);
        }
    }

    private void showAllInventory(){
        itemDataList = new ArrayList<>() ;
        customAdapter = new CustomAdapterInventory(this, R.layout.layout_item_inventory, itemDataList);
        lstData.setAdapter(customAdapter);
        //
        DatabaseReference itemRef = FirebaseDatabase.getInstance().getReference().child("users").child(firebaseUser.getUid()).child("item");
        itemRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Xóa dữ liệu cũ
                itemDataList.clear();
                // Đọc dữ liệu từ Firebase và thêm vào danh sách
                for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
                    for (DataSnapshot itemSnapshot : categorySnapshot.getChildren()) {
                        String itemName = itemSnapshot.getKey();
                        DataSnapshot quantitySnapshot = itemSnapshot.child("quantity");
                        if (quantitySnapshot.exists() && quantitySnapshot.getValue() != null) {
                            int itemQuantity = quantitySnapshot.getValue(Integer.class);
                            int itemPrice = getItemPriceFromJson(itemName) ;
                            String itemCategory = getItemCategoryFromJson(itemName) ;
                            //
                            if (itemQuantity >0){
                                String idItem = getIdFromJson(itemName) ;
                                String itemNameInInventory = getNameItem(itemName);
                                int itemExp = getExpFromJsonData(itemName);
                                int itemImageResource = getImageResourceForItem(itemName);
                                Item itemData = new Item(idItem,itemNameInInventory, itemQuantity, itemImageResource,itemPrice, itemCategory , itemExp);
                                itemDataList.add(itemData);
                            }
                        } else {

                        }

                    }
                }
                // Cập nhật ListView
                customAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗi nếu cần thiết
            }
        });
    }

    private int getItemPriceFromJson(String itemId) {
        int itemPrice = 0; // Default value
        try {
            // Read the JSON file from the assets folder
            InputStream inputStream = getAssets().open("data.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();

            // Convert the byte array to a string
            String jsonString = new String(buffer, "UTF-8");

            // Parse the JSON string to retrieve the price
            JSONArray jsonArray = new JSONArray(jsonString);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject itemJson = jsonArray.getJSONObject(i);
                if (itemJson.getString("id").equals(itemId)) {
                    itemPrice = itemJson.getInt("sellPrice");
                    break; // Found the item, exit the loop
                }
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return itemPrice;
    }

    private int getExpFromJsonData(String itemId){
        int itemExp = 0; // Default value
        try {
            // Read the JSON file from the assets folder
            InputStream inputStream = getAssets().open("data.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();

            // Convert the byte array to a string
            String jsonString = new String(buffer, "UTF-8");

            // Parse the JSON string to retrieve the price
            JSONArray jsonArray = new JSONArray(jsonString);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject itemJson = jsonArray.getJSONObject(i);
                if (itemJson.getString("id").equals(itemId)) {
                    itemExp = itemJson.getInt("exp");
                    break; // Found the item, exit the loop
                }
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return itemExp;
    }
    private String getItemCategoryFromJson(String itemId) {
        String itemCategory = "non"; // Default value
        try {
            // Read the JSON file from the assets folder
            InputStream inputStream = getAssets().open("data.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();

            // Convert the byte array to a string
            String jsonString = new String(buffer, "UTF-8");

            // Parse the JSON string to retrieve the price
            JSONArray jsonArray = new JSONArray(jsonString);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject itemJson = jsonArray.getJSONObject(i);
                if (itemJson.getString("id").equals(itemId)) {
                    itemCategory = itemJson.getString("category");
                    break; // Found the item, exit the loop
                }
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return itemCategory;
    }
    private String getIdFromJson(String itemId) {
        String itemCategory = "non"; // Default value
        try {
            // Read the JSON file from the assets folder
            InputStream inputStream = getAssets().open("data.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();

            // Convert the byte array to a string
            String jsonString = new String(buffer, "UTF-8");

            // Parse the JSON string to retrieve the price
            JSONArray jsonArray = new JSONArray(jsonString);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject itemJson = jsonArray.getJSONObject(i);
                if (itemJson.getString("id").equals(itemId)) {
                    itemCategory = itemJson.getString("id");
                    break; // Found the item, exit the loop
                }
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return itemCategory;
    }
    private String getNameItem(String itemId){
        switch (itemId) {
            case "ga":
                return "Gà";
            case "bo":
                return "Bò";
            case "heo":
                return "Heo";
            case "cuu" :
                return "Cừu";
            case "Slua" :
                return "Lúa" ;
            case "Smia" :
                return "Mía" ;
            case "Sbap" :
                return "Bắp" ;
            case "Scarot" :
                return "Cà Rốt" ;
            case "lua" :
                return "Cây Lúa"  ;
            case "mia" :
                return "Cây Mía" ;
            case "bap" :
                return "Cây Bắp" ;
            case "carot" :
                return "Cây Cà Rốt" ;
            default:
                return null;
        }
    }
    private int getImageResourceForItem(String itemId) {
        switch (itemId) {
            case "ga":
                return R.drawable.chicken;
            case "bo":
                return R.drawable.cow;
            case "heo":
                return R.drawable.pig;
            case "cuu" :
                return R.drawable.sheep ;
            case "Slua" :
                return R.drawable.wheatseed ;
            case "Smia" :
                return R.drawable.cornseed;
            case "Sbap" :
                return R.drawable.cornseed ;
            case "Scarot" :
                return R.drawable.carrotseed ;
            case "mia" :
                return R.drawable.sugarcane_product ;
            case  "bap" :
                return R.drawable.corn_product ;
            case "carot" :
                return R.drawable.carrot_product ;
            case "lua" :
                return R.drawable.wheat_product ;
            // Add cases for other items
            default:
                return R.drawable.default_ava;
        }
    }
    public void addControls()
    {
        close = (ImageButton) findViewById(R.id.btnCloseTrade);
        lstData = findViewById(R.id.lstTradeItem) ;
    }
    public void addEvents()
    {
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TradeActivity.this, GameActivity.class);
                startActivity(intent);
            }
        });
        //
        lstData.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Item selectedItem = itemDataList.get(position);
                showDialogBuyItem(selectedItem);

            }
        });
    }
    // Các hàm xử lý nhạc nền bao gồm: chạy lại nhạc sau 3 giây từ khi kết thúc và giữ trạng thái nhạc khi chuyển đổi giữa các activity
    private void playMusic() {
        backgroundMusic.start();
        backgroundMusic.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                handler.postDelayed(runnable, 3000);
            }
        });
        runnable = new Runnable() {
            @Override
            public void run() {
                if (backgroundMusic != null) {
                    backgroundMusic.seekTo(0);
                    backgroundMusic.start();
                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (backgroundMusic != null && !backgroundMusic.isPlaying()) {
            playMusic();
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (backgroundMusic != null && backgroundMusic.isPlaying()) {
            backgroundMusic.pause();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (backgroundMusic != null) {
            backgroundMusic.release();
            backgroundMusic = null;
        }
        handler.removeCallbacks(runnable);
    }
}