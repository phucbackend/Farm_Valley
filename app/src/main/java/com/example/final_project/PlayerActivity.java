package com.example.final_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

public class PlayerActivity extends AppCompatActivity {
    TextView playerName, farmName , playerLv;
    ListView listViewData ;
    ImageButton changePlayerName, changeFarmName, close , avatar;
    MediaPlayer backgroundMusic, closeSound, errorSound;
    Handler handler;
    Runnable runnable;
    FirebaseAuth auth ;
    FirebaseUser firebaseUser ;
    //
    CustomAdapterInventory customAdapter ;
    ArrayList<Item> itemDataList ;
    Button btnSeed , btnAnimal ,btnPlant ;
    DatabaseReference currentLevel ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        //
        addControls();
        addEvents();

        //
        showImg();
        //
        backgroundMusic = MediaPlayer.create(this, R.raw.player_backgorund_music);
        handler = new Handler();
        playMusic();
        //
        setupPlayerInfoListener();
        //
        if (firebaseUser != null) {
            showAllInventory();
        }
        //
        if (firebaseUser != null){
            currentLevel = FirebaseDatabase.getInstance().getReference()
                    .child("users").child(firebaseUser.getUid()).child("lv") ;
            currentLevel.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int currentLv = snapshot.getValue(Integer.class) ;
                    playerLv.setText("LV : " + String.valueOf(currentLv));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

    }

    //show inventory
    private void showAllInventory(){
        itemDataList = new ArrayList<>() ;
        customAdapter = new CustomAdapterInventory(this, R.layout.layout_item_inventory, itemDataList);
        listViewData.setAdapter(customAdapter);
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
                            if (itemQuantity >0){
                                String itemNameInInventory = getNameItem(itemName);
                                int itemImageResource = getImageResourceForItem(itemName);
                                Item itemData = new Item(itemNameInInventory, itemQuantity, itemImageResource);
                                itemDataList.add(itemData);
                            }
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

    private void showInventory(String category) {
        itemDataList = new ArrayList<>();
        customAdapter = new CustomAdapterInventory(this, R.layout.layout_item_inventory, itemDataList);
        listViewData.setAdapter(customAdapter);

        DatabaseReference itemRef = FirebaseDatabase.getInstance().getReference().child("users").child(firebaseUser.getUid()).child("item");
        itemRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                itemDataList.clear();
                for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
                    for (DataSnapshot itemSnapshot : categorySnapshot.getChildren()) {
                        String itemName = itemSnapshot.getKey();
                        DataSnapshot quantitySnapshot = itemSnapshot.child("quantity");
                        if (quantitySnapshot.exists() && quantitySnapshot.getValue() != null) {
                            int itemQuantity = quantitySnapshot.getValue(Integer.class);
                            if (itemQuantity > 0) {
                                // Filter items based on the specified category
                                if (("seed".equals(category) && isSeedItem(itemName)) ||
                                        ("animal".equals(category) && isAnimalItem(itemName)) ||
                                        ("plant".equals(category)) && isPlantItem(itemName)) {
                                    String itemNameInInventory = getNameItem(itemName);
                                    int itemImageResource = getImageResourceForItem(itemName);
                                    Item itemData = new Item(itemNameInInventory, itemQuantity, itemImageResource);
                                    itemDataList.add(itemData);
                                }
                            }
                        }
                    }
                }
                customAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled
            }
        });
    }

    private boolean isSeedItem(String itemId) {
        return itemId.equals("Slua") || itemId.equals("Smia") || itemId.equals("Sbap") || itemId.equals("Scarot");
    }

    private boolean isAnimalItem(String itemId) {
        return itemId.equals("ga") || itemId.equals("bo") || itemId.equals("heo") || itemId.equals("cuu");
    }

    private boolean isPlantItem(String itemId){
        return itemId.equals("lua") || itemId.equals("mia") || itemId.equals("bap") || itemId.equals("carot");
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
                return R.drawable.sugarcaneseed;
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

    //show avatar
    private void showImg(){
        auth = FirebaseAuth.getInstance() ;
        firebaseUser = auth.getCurrentUser() ;
        if(firebaseUser != null){
                String photoUrl = Objects.requireNonNull(firebaseUser.getPhotoUrl()).toString();
                Picasso.with(this).load(photoUrl).into(avatar);
            }
        }

    public void addControls()
    {
        playerName = (TextView) findViewById(R.id.tvPlayerName);
        farmName = (TextView) findViewById(R.id.tvFarmName);
        changePlayerName = (ImageButton) findViewById(R.id.btnChangePlayerName);
        changeFarmName = (ImageButton) findViewById(R.id.btnChangeFarmName);
        close = (ImageButton) findViewById(R.id.btnClose);
        avatar = (ImageButton) findViewById(R.id.btnAvatar) ;
        listViewData = findViewById(R.id.lstData) ;
        btnSeed = findViewById(R.id.btnSeed) ;
        btnAnimal = findViewById(R.id.btnVatNuoi) ;
        btnPlant = findViewById(R.id.btnPlant) ;
        playerLv = findViewById(R.id.tvPlayerLevel) ;
    }



    public void addEvents()
    {
            btnSeed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showInventory("seed");
                }
            });
            //
            btnAnimal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showInventory("animal");
                }
            });
            //
            btnPlant.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showInventory("plant");
                }
            });

        //
        closeSound = MediaPlayer.create(this, R.raw.close_sound);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeSound.start();
                Intent intent = new Intent(PlayerActivity.this, GameActivity.class);
                startActivity(intent);
            }
        });
        changePlayerName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogChangePlayerName();
            }
        });
        changeFarmName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogChangeFarmName();
            }
        });
        //

    }
    private void showDialogChangePlayerName() {
        closeSound = MediaPlayer.create(this, R.raw.close_sound);
        errorSound = MediaPlayer.create(this, R.raw.error_sound);
        final Dialog dialogPlayerName = new Dialog(PlayerActivity.this);
        dialogPlayerName.setContentView(R.layout.dialog_changeplayername);
        Window window = dialogPlayerName.getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(window.getAttributes());
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT; // Đặt width thành MATCH_PARENT
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT; // Đặt height thành MATCH_PARENT
            window.setAttributes(layoutParams);
        }
        //add data to firebase
        EditText name = dialogPlayerName.findViewById(R.id.edtPlayerName);
        Button ok = dialogPlayerName.findViewById(R.id.btnOK);
        ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!name.getText().toString().isEmpty()){
                            String newName = name.getText().toString();
                            if (newName.length() <= 10) {
                                PlayerInfo playerInfo = new PlayerInfo() ;
                                playerInfo.setPlayerName(newName);
                                firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                if (firebaseUser != null){
                                    DatabaseReference player_name = FirebaseDatabase.getInstance().getReference().child("users").child(firebaseUser.getUid());
                                    player_name.child("userName").setValue(newName) ;
                                }
                                playerName.setText(newName);
                                dialogPlayerName.dismiss();
                            } else {
                                errorSound.start();
                                Toast toast = new Toast(getApplicationContext());
                                LayoutInflater inflater = getLayoutInflater();
                                View view1 = inflater.inflate(R.layout.custom_layout_toast_fail, (ViewGroup) findViewById(R.id.layout_custom_toast_success));
                                TextView txtMess = view1.findViewById(R.id.tv_message_fail);
                                txtMess.setText("Bạn đã nhập vượt quá ký tự cho phép !");
                                toast.setView(view1);
                                toast.setGravity(Gravity.BOTTOM, 0, 0);
                                toast.setDuration(Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        }else {
                            Toast.makeText(getApplicationContext() , "Vui lòng nhập tên" , Toast.LENGTH_SHORT).show() ;
                        }
                    }
                });

        Button cancel = dialogPlayerName.findViewById(R.id.btnCancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeSound.start();
                dialogPlayerName.dismiss();
            }
        });
        dialogPlayerName.show();
    }

    private void showDialogChangeFarmName() {
        closeSound = MediaPlayer.create(this, R.raw.close_sound);
        errorSound = MediaPlayer.create(this, R.raw.error_sound);
        final Dialog dialogFarmName = new Dialog(PlayerActivity.this);
        dialogFarmName.setContentView(R.layout.dialog_changefarmname);
        Window window = dialogFarmName.getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(window.getAttributes());
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT; // Đặt width thành MATCH_PARENT
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT; // Đặt height thành MATCH_PARENT
            window.setAttributes(layoutParams);
        }
        EditText name = dialogFarmName.findViewById(R.id.edtFarmName);
        Button ok = dialogFarmName.findViewById(R.id.btnOK);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!name.getText().toString().isEmpty()){
                    String newName = name.getText().toString();
                    if (newName.length() <= 10) {
                        PlayerInfo playerInfo = new PlayerInfo() ;
                        playerInfo.setFarmName(newName);
                        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        if (firebaseUser != null){
                            DatabaseReference player_name = FirebaseDatabase.getInstance().getReference().child("users").child(firebaseUser.getUid());
                            player_name.child("farmName").setValue(newName) ;
                        }
                        farmName.setText(newName);
                        dialogFarmName.dismiss();
                        } else {
                            errorSound.start();
                            Toast toast = new Toast(getApplicationContext());
                            LayoutInflater inflater = getLayoutInflater();
                            View view1 = inflater.inflate(R.layout.custom_layout_toast_fail, (ViewGroup) findViewById(R.id.layout_custom_toast_success));
                            TextView txtMess = view1.findViewById(R.id.tv_message_fail);
                            txtMess.setText("Bạn đã nhập vượt quá ký tự cho phép !");
                            toast.setView(view1);
                            toast.setGravity(Gravity.BOTTOM, 0, 0);
                            toast.setDuration(Toast.LENGTH_SHORT);
                            toast.show();
                        }
                }else {
                    Toast.makeText(getApplicationContext() , "Vui lòng nhập tên" , Toast.LENGTH_SHORT).show() ;
                }

            }
        });
        Button cancel = dialogFarmName.findViewById(R.id.btnCancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeSound.start();
                dialogFarmName.dismiss();
            }
        });
        dialogFarmName.show();
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

    //
    private void setupPlayerInfoListener() {
        // Thêm listener để theo dõi thay đổi trên nút người dùng hiện tại trong Firebase
        if (firebaseUser != null) {
            DatabaseReference playerRef = FirebaseDatabase.getInstance().getReference().child("users").child(firebaseUser.getUid());
            playerRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DataSnapshot dataSnapshot = task.getResult();
                    if (dataSnapshot.exists()) {
                        playerName.setText(dataSnapshot.child("userName").getValue(String.class));
                        farmName.setText(dataSnapshot.child("farmName").getValue(String.class));
                    }
                } else {
                    // Handle exceptions
                    Exception exception = task.getException();
                    if (exception != null) {
                        exception.printStackTrace();
                    }
                }
            });
        }
    }
}