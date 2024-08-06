package com.example.final_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
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

public class PlantActivity extends AppCompatActivity {
    ImageButton close, imgB1 , imgB2 , imgB3 , imgB4 , imgB5 ;
    MediaPlayer backgroundMusic, closeSound;;
    Handler handler;
    Runnable runnable;
    CustomAdapterInventory customAdapter ;
    ArrayList<Item> itemDataList ;
    FirebaseUser firebaseUser ;
    FirebaseAuth auth ;
    DatabaseReference currentExp ;
    DatabaseReference currentLvlExp ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant);
        //
        Intent intent = getIntent() ;
        //
        auth = FirebaseAuth.getInstance() ;
        firebaseUser = auth.getCurrentUser() ;
        //
        addControls();
        addEvents();
        //
        backgroundMusic = MediaPlayer.create(this, R.raw.plant_background_music);
        handler = new Handler();
        playMusic();
        //

    }
    public void addControls()
    {
        close = (ImageButton) findViewById(R.id.btnClosePlant);
        imgB1 = findViewById(R.id.btn1) ;
        imgB2 = findViewById(R.id.btn2) ;
        imgB3 = findViewById(R.id.btn3) ;
        imgB4 = findViewById(R.id.btn4) ;
        imgB5 = findViewById(R.id.btn5) ;
    }
    public void addEvents()
    {
        closeSound = MediaPlayer.create(this, R.raw.close_sound);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeSound.start();
                Intent intent = new Intent(PlantActivity.this, GameActivity.class);
                startActivity(intent);
            }
        });
        //
            imgB1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handlePlanting(imgB1);
                }
            });
            //
            imgB2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handlePlanting(imgB2);
                }
            });
            //
            imgB3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handlePlanting(imgB3);
                }
            });
            //
            imgB4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handlePlanting(imgB4);
                }
            });
            //
            imgB5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handlePlanting(imgB5);
                }
            });

    }

    private void handlePlanting(ImageButton selectedButton) {
        showSeedMenu(selectedButton);
    }




    private void playMusic() {
        backgroundMusic.start();
        backgroundMusic.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                handler.postDelayed(runnable, 1000);
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

    private void showSeedMenu(ImageButton selectedButton) {
        final Dialog dialogSetting = new Dialog(PlantActivity.this);
        dialogSetting.setContentView(R.layout.planting_dialog);
        Window window = dialogSetting.getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(window.getAttributes());
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT; // Đặt width thành MATCH_PARENT
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT; // Đặt height thành MATCH_PARENT
            window.setAttributes(layoutParams);
        }
        ImageButton closeButton = dialogSetting.findViewById(R.id.btnCloseDialogSeed);
        ListView lstSeed = dialogSetting.findViewById(R.id.lstSeed) ;

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSetting.dismiss();
            }
        });
        //
        itemDataList = new ArrayList<>() ;
        customAdapter = new CustomAdapterInventory(this, R.layout.layout_item_inventory, itemDataList);
        lstSeed.setAdapter(customAdapter);
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
                            if (itemQuantity >0 && "Seed".equals(itemCategory)){
                                    String idItem = getIdFromJson(itemName) ;
                                    String itemNameInInventory = getNameItem(itemName);
                                    int itemExp = getExpFromJsonData(itemName);
                                    int itemImageSeed = getImageResourceForItem(itemName);
                                    Item itemData = new Item(idItem,itemNameInInventory, itemQuantity, itemImageSeed,itemPrice, itemCategory , itemExp );
                                    itemDataList.add(itemData);
                                    //
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
        //event for lstSeed
        lstSeed.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Item selectedItem = itemDataList.get(position)  ;
                setSelectedItemImage(selectedItem , selectedButton);
                dialogSetting.dismiss();
            }
        });

        //
        dialogSetting.show();
    }

    public String getIdPlant(Item selectedItem){
        switch (selectedItem.getId()) {
            case "Slua" :
                return "lua" ;
            case "Smia" :
                return "mia" ;
            case "Sbap" :
                return "bap" ;
            case "Scarot" :
                return "carot" ;
            // Add cases for other items
            default:
                return null ;
        }
    }
    private void setSelectedItemImage(Item selectedItem  ,ImageButton selectedButton) {
        DatabaseReference itemImageEarlyRef = FirebaseDatabase.getInstance().getReference()
                .child("users").child(firebaseUser.getUid()).child("item")
                .child(selectedItem.getCategory())
                .child(selectedItem.getId())
                .child("imgEarly");

        DatabaseReference itemQuantityEarlyRef = FirebaseDatabase.getInstance().getReference()
                .child("users").child(firebaseUser.getUid()).child("item")
                .child(selectedItem.getCategory())
                .child(selectedItem.getId())
                .child("quantity");

        DatabaseReference itemTimeRef = FirebaseDatabase.getInstance().getReference()
                .child("users").child(firebaseUser.getUid()).child("item")
                .child(selectedItem.getCategory())
                .child(selectedItem.getId())
                .child("time");

        DatabaseReference itemImageProductRef = FirebaseDatabase.getInstance().getReference()
                .child("users").child(firebaseUser.getUid()).child("item")
                .child(selectedItem.getCategory())
                .child(selectedItem.getId())
                .child("imgProduct");

        DatabaseReference landStatus = FirebaseDatabase.getInstance().getReference()
                .child("users").child(firebaseUser.getUid()).child("land")
                        .child("land1").child("status") ;

        DatabaseReference landTime = FirebaseDatabase.getInstance().getReference()
                .child("users").child(firebaseUser.getUid()).child("land")
                .child("land1").child("time") ;
        //
        DatabaseReference currentExp = FirebaseDatabase.getInstance().getReference()
                .child("users").child(firebaseUser.getUid()).child("exp") ;
        //
        String idPlantName = getIdPlant(selectedItem);

        DatabaseReference quantityPlant = FirebaseDatabase.getInstance().getReference()
                .child("users").child(firebaseUser.getUid()).child("item")
                        .child("Plant")
                                .child(idPlantName).child("quantity");


        itemImageEarlyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String imageName = snapshot.getValue(String.class);
                    int resourceId = getResources().getIdentifier(imageName, "drawable", getPackageName());
                    if (resourceId != 0){
                       selectedButton.setImageResource(resourceId);
                       //update quantity seed
                        itemQuantityEarlyRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()){
                                    int quantity = snapshot.getValue(Integer.class) ;
                                    itemQuantityEarlyRef.setValue(quantity - 1) ;
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                       //
                        itemTimeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot timeSnapshot) {
                                if (timeSnapshot.exists()) {
                                    int timeInSecond = timeSnapshot.getValue(Integer.class);
                                    int milliSecond = timeInSecond*1000 ;
                                    landStatus.setValue(true) ;
                                    new CountDownTimer(milliSecond , 1000) {
                                        public void onTick(long millisUntilFinished) {
                                            // Cập nhật giao diện người dùng với thời gian còn lại nếu bạn muốn
                                            landTime.setValue(millisUntilFinished / 1000);
                                        }
                                        public void onFinish() {
                                            // Thời gian đã kết thúc, cập nhật ảnh của cây và thời gian trên Firebase
                                            itemImageProductRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    if (snapshot.exists()){
                                                        String itemNameProduct = snapshot.getValue(String.class) ;
                                                        int resourceIdProduct = getResources().getIdentifier(itemNameProduct, "drawable", getPackageName());
                                                        if (resourceIdProduct != 0){
                                                            selectedButton.setImageResource(resourceIdProduct);
                                                            //Harvest
                                                            selectedButton.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View v) {
                                                                        int resourceImgDefault = getResources().getIdentifier("empty_dirt", "drawable", getPackageName());
                                                                        selectedButton.setImageResource(resourceImgDefault);
                                                                        //update quantity plant
                                                                        quantityPlant.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                            @Override
                                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                if (snapshot.exists()){
                                                                                    int quantity = snapshot.getValue(Integer.class) ;
                                                                                    quantityPlant.setValue(quantity + 1) ;
                                                                                    //update exp
                                                                                    int exp = selectedItem.getExp() ;
                                                                                    currentExp.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                        @Override
                                                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                            if (snapshot.exists()){
                                                                                                int currntExp = snapshot.getValue(Integer.class) ;
                                                                                                currentExp.setValue(currntExp + exp) ;
                                                                                            }
                                                                                        }

                                                                                        @Override
                                                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                                                        }
                                                                                    });
                                                                                }
                                                                            }

                                                                            @Override
                                                                            public void onCancelled(@NonNull DatabaseError error) {

                                                                            }
                                                                        });
                                                                        //
                                                                        Toast.makeText(getApplicationContext() , "Thu hoạch thành công" , Toast.LENGTH_SHORT).show();
                                                                        resetImgB1(selectedButton);
                                                                }
                                                            });
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                            // Cập nhật thời gian mới trên Firebase (ví dụ: 0 là thời gian đã kết thúc)

                                        }
                                    }.start();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                // Xử lý lỗi nếu cần thiết
                            }
                        });
                    }
                } else {
                    // Handle the case where the image is not found
                    Toast.makeText(PlantActivity.this, "Image not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle the error if needed
                Toast.makeText(PlantActivity.this, "Failed to fetch image", Toast.LENGTH_SHORT).show();
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
                return R.drawable.sugarcaneseed;
            case "Sbap" :
                return R.drawable.cornseed ;
            case "Scarot" :
                return R.drawable.carrotseed ;
            // Add cases for other items
            default:
                return R.drawable.default_ava;
        }
    }
    private int getImageResourceEarlyForItem(String itemId){
        switch (itemId) {
            case "Slua" :
                return R.drawable.early_wheat ;
            case "Smia" :
                return R.drawable.early_sugarcane ;
            case "Sbap" :
                return R.drawable.early_corn ;
            case "Scarot" :
                return R.drawable.early_carrot ;
            // Add cases for other items
            default:
                return R.drawable.default_ava;
        }
    }

    private void resetImgB1(ImageButton selectedButton) {
        selectedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSeedMenu(selectedButton);
            }
        });
    }
}