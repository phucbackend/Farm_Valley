package com.example.final_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.AssetManager;
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


import com.example.final_project.CustomAdapterItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

public class ShopActivity extends AppCompatActivity {

    private static final int MAX_QUANTITY = 10;
    ListView lvItem;
    CustomAdapterItem adapterItem;
    ArrayList<Item> lvData = new ArrayList<>();
    MediaPlayer backgroundMusic, closeSound, purchaseSound;
    Button muaCay, muaVat;
    ImageButton close;
    Handler handler;
    Runnable runnable;

    //
    DatabaseReference coinBalance ;
    FirebaseUser firebaseUser ;
    int userCoinBalance  ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);
        addControls();
        addEvents();
        readJsonFromAssets();
        backgroundMusic = MediaPlayer.create(this, R.raw.shopandtrade_background_music);
        handler = new Handler();
        playMusic();
    }
    public void addControls()
    {
        close = (ImageButton) findViewById(R.id.btnCloseShop);
        lvItem = (ListView) findViewById(R.id.lstItem);

    }
    public void addEvents()
    {
        closeSound = MediaPlayer.create(this, R.raw.close_sound);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeSound.start();
                Intent intent = new Intent(ShopActivity.this, GameActivity.class);
                startActivity(intent);
            }
        });
        lvItem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Lấy mục đã chọn từ adapter
                Item selectedItem = lvData.get(position);

                // Hiển thị hộp thoại cho mục đã chọn
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

    private void readJsonFromAssets() {
        AssetManager assetManager = getAssets();
        try {
            // Mở file từ thư mục Assets
            InputStream inputStream = assetManager.open("data.json");

            // Đọc dữ liệu từ InputStream thành chuỗi
            String jsonString = convertInputStreamToString(inputStream);

            // Chuyển chuỗi JSON thành đối tượng Item hoặc danh sách các đối tượng Item
            Gson gson = new Gson();
            Item[] items = gson.fromJson(jsonString, Item[].class);
            lvData.clear();
            for (Item item : items) {
                // Only add items to lvData if the category is not "Plant"
                if (!item.getCategory().equals("Plant")) {
                    lvData.add(item);
                }
            }


            // Tạo và set adapter cho ListView
            adapterItem = new CustomAdapterItem(this, R.layout.layout_shop_item, lvData);
            lvItem.setAdapter(adapterItem);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line);
        }
        //comment
        inputStream.close();
        return stringBuilder.toString();
    }

    private void showDialogBuyItem(Item selectedItem) {

        final Dialog dialogBuyItem = new Dialog(ShopActivity.this);
        dialogBuyItem.setContentView(R.layout.dialog_buy_item);

        // Khởi tạo các view trong hộp thoại

        TextView txtItem = dialogBuyItem.findViewById(R.id.txtItem);
        TextView txtPrice = dialogBuyItem.findViewById(R.id.txtPrice);
        Button btnOK = dialogBuyItem.findViewById(R.id.btnOK);
        Button btnCancel = dialogBuyItem.findViewById(R.id.btnCancel);
        ImageButton btnPlus = dialogBuyItem.findViewById(R.id.btnPlus);
        ImageButton btnMinus = dialogBuyItem.findViewById(R.id.btnMinus);
        TextView txtQuantity = dialogBuyItem.findViewById(R.id.txtEditNum);
        TextView txtTotalPrice = dialogBuyItem.findViewById(R.id.txtTotalPrice);
        // Đặt giá trị dựa trên mục đã chọn
        txtItem.setText(selectedItem.getName());
        // Lấy giá của mục hàng được chọn
        int price = selectedItem.getPrice();
        txtPrice.setText(String.valueOf(price));
        // Khởi tạo giá trị cho số lượng và tổng giá
        final int[] soLuong = {1};
        final int[] tongGia = {price * soLuong[0]};
        // Đặt giá trị ban đầu cho txtEditNum và txtTotalPrice
        txtQuantity.setText(String.valueOf(soLuong[0]));
        txtTotalPrice.setText(String.valueOf(tongGia[0]));

        btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (soLuong[0] < MAX_QUANTITY){
                    soLuong[0]++;
                    txtQuantity.setText(String.valueOf(soLuong[0]));
                    tongGia[0] = price * soLuong[0];
                    txtTotalPrice.setText(String.valueOf(tongGia[0]));
                } else
                    Toast.makeText(ShopActivity.this,"Số lượng mua không được lớn hơn 10" , Toast.LENGTH_SHORT).show();
            }
        });

        btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (soLuong[0] > 1) {
                    soLuong[0]--;
                    txtQuantity.setText(String.valueOf(soLuong[0]));
                    tongGia[0] = price * soLuong[0];
                    txtTotalPrice.setText(String.valueOf(tongGia[0]));
                }
                else
                    Toast.makeText(ShopActivity.this,"Số lượng không được bé hơn 1",Toast.LENGTH_SHORT).show();
            }
        });

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
                            int totalCost = tongGia[0];
                            if (userCoinBalance >= totalCost) {
                                String selectedItemName = selectedItem.getId();
                                String selectedItemCategory = selectedItem.getCategory();
                                performPurchase(totalCost , selectedItemName , selectedItemCategory , Integer.parseInt(txtQuantity.getText().toString()));
                                Toast.makeText(ShopActivity.this, "Mua thành công", Toast.LENGTH_LONG).show();
                                dialogBuyItem.dismiss();
                            } else {
                                // Insufficient coins, show a message to the user
                                Toast.makeText(ShopActivity.this, "Không đủ tiền !", Toast.LENGTH_LONG).show();
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

    private void performPurchase(int totalCost , String itemName , String itemCategory , int quantity) {
        // Update the user's coin balance on Firebase
        if(itemCategory == "Animal"){
            DatabaseReference itemQuantityRef = FirebaseDatabase.getInstance()
                    .getReference().child("users")
                    .child(firebaseUser.getUid()).child("item")
                    .child(itemCategory).child(itemName) ;
            itemQuantityRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int currentQuantity = snapshot.getValue(Integer.class) ;
                    itemQuantityRef.setValue(currentQuantity + quantity) ;
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            coinBalance.setValue(userCoinBalance - totalCost);
        }else {
            DatabaseReference itemQuantityRef = FirebaseDatabase.getInstance()
                    .getReference().child("users")
                    .child(firebaseUser.getUid()).child("item")
                    .child(itemCategory).child(itemName).child("quantity");
            itemQuantityRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int currentQuantity = snapshot.getValue(Integer.class) ;
                    itemQuantityRef.setValue(currentQuantity + quantity) ;
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            coinBalance.setValue(userCoinBalance - totalCost);
        }

    }

}