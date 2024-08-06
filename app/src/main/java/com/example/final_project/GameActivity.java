package com.example.final_project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Objects;

public class GameActivity extends AppCompatActivity {
    MediaPlayer backgroundMusic, openSound, closeSound, levelupSound;
    Button cuaHang, TTGDich, datTrong, chuongTrai;
    TextView tvCoin , tvLevel ;
    ImageButton avatar ;
    ImageButton setting;
    Handler handler;
    Runnable runnable;
    ProgressBar progressBar ;
    //
    FirebaseAuth auth ;

    FirebaseDatabase firebaseDatabase ;
    GoogleSignInClient googleSignInClient ;
    FirebaseUser currentUser ;
    int RC_SIGN_IN = 2;
    DatabaseReference dataCoin ;
    DatabaseReference levelFromFirebase ;
    DatabaseReference lvlExpFromFirebase ;
    DatabaseReference expFromFirebase ;
    int currentExp ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        //
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        //
        auth = FirebaseAuth.getInstance() ;
        firebaseDatabase = FirebaseDatabase.getInstance() ;
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id) )
                .requestEmail().build() ;
        googleSignInClient = GoogleSignIn.getClient(this , gso) ;
        //
        addControls();
        addEvents();
        //
        FirebaseUser user = auth.getCurrentUser() ;
        updateUI(user);
        //
        if (user != null){
            dataCoin = FirebaseDatabase.getInstance().getReference().child("users").child(currentUser.getUid()).child("coin");
            levelFromFirebase = FirebaseDatabase.getInstance().getReference().child("users").child(currentUser.getUid()).child("lv") ;
            expFromFirebase = FirebaseDatabase.getInstance().getReference().child("users").child(currentUser.getUid()).child("exp") ;
            lvlExpFromFirebase = FirebaseDatabase.getInstance().getReference().child("users").child(currentUser.getUid()).child("lvlExp") ;
            setupPlayerInfoListener();
        }
        //
        backgroundMusic = MediaPlayer.create(this, R.raw.game_background_music);
        handler = new Handler();
        playMusic();

    }
    private void setupPlayerInfoListener() {
        dataCoin.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    int currentCoin = dataSnapshot.getValue(Integer.class);
                    tvCoin.setText(String.valueOf(currentCoin));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý khi có lỗi đọc dữ liệu từ Firebase
                Log.e("GameActivity", "Error reading coin data", error.toException());
            }
        });
        //
        levelFromFirebase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                int currentLevel = snapshot.getValue(Integer.class) ;
                tvLevel.setText(String.valueOf(currentLevel));
                //
                showExp(currentLevel);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        }) ;
        //

    }

     public void showExp(int currentLevel){
         expFromFirebase.addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot snapshot) {
                 if (snapshot.exists()){
                     currentExp = snapshot.getValue(Integer.class) ;
                     //logic for progressbar
                     lvlExpFromFirebase.addValueEventListener(new ValueEventListener() {
                         @Override
                         public void onDataChange(@NonNull DataSnapshot snapshot) {
                             if (snapshot.exists()){
                                 int maxProgressBar = snapshot.getValue(Integer.class)  ;
                                 progressBar.setMax(maxProgressBar);
                                 progressBar.setProgress(currentExp);
                                 if (currentExp >= maxProgressBar) {
                                     expFromFirebase.setValue(currentExp - maxProgressBar) ;
                                     levelFromFirebase.setValue(currentLevel + 1) ;
                                     lvlExpFromFirebase.setValue(maxProgressBar + 100);
                                     //+50 coin every lv
                                     dataCoin.addListenerForSingleValueEvent(new ValueEventListener() {
                                         @Override
                                         public void onDataChange(@NonNull DataSnapshot snapshot) {
                                             if (snapshot.exists()){
                                                 int currentCoin = snapshot.getValue(Integer.class) ;
                                                 dataCoin.setValue(currentCoin + 50) ;
                                             }
                                         }

                                         @Override
                                         public void onCancelled(@NonNull DatabaseError error) {

                                         }
                                     });
                                     Toast.makeText(GameActivity.this , "Bạn đã lên cấp " + String.valueOf(currentLevel + 1) , Toast.LENGTH_SHORT).show();
                                 }
                             }
                         }

                         @Override
                         public void onCancelled(@NonNull DatabaseError error) {

                         }
                     }) ;
                 }
             }

             @Override
             public void onCancelled(@NonNull DatabaseError error) {
             }
         });
    }
    private void updateProgressBarUI() {
        // Add your code to update the progress bar UI
        // You can use progressBar.setProgress() or any other method based on your UI implementation
    }

    public void addControls()
    {
        avatar = (ImageButton) findViewById(R.id.btnAvatar);
        setting = (ImageButton) findViewById(R.id.btnSetting);
        cuaHang = (Button) findViewById(R.id.btnCuaHang);
        TTGDich = (Button) findViewById(R.id.btnTTGDich);
        datTrong =  findViewById(R.id.btnDatTrong);
        chuongTrai =  findViewById(R.id.btnChuongTrai);
        tvCoin = findViewById(R.id.tvGold) ;
        tvLevel = findViewById(R.id.tvLevel) ;
        progressBar = findViewById(R.id.pgbExp) ;
    }
    public void addEvents()
    {
        openSound = MediaPlayer.create(this, R.raw.open_sound);
        cuaHang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSound.start();
                Intent intent = new Intent(GameActivity.this, ShopActivity.class);
                startActivity(intent);
            }
        });
        TTGDich.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSound.start();
                Intent intent = new Intent(GameActivity.this, TradeActivity.class);
                startActivity(intent);
            }
        });
        datTrong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSound.start();
                Intent intent = new Intent(GameActivity.this, PlantActivity.class);
                startActivity(intent);
            }
        });
        chuongTrai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSound.start();
                Intent intent = new Intent(GameActivity.this, FeedActivity.class);
                startActivity(intent);
            }
        });
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSound.start();
                Intent intent = new Intent(GameActivity.this, PlayerActivity.class);
                startActivity(intent);
            }
        });
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSound.start();
                if (currentUser != null){
                    showDialog_logout();
                }else {
                    showCustomDialogSetting();
                }

            }
        });
    }

private void showCustomDialogSetting() {
    closeSound = MediaPlayer.create(this, R.raw.close_sound);
    final Dialog dialogSetting = new Dialog(GameActivity.this);
    dialogSetting.setContentView(R.layout.dialog_setting);
    Window window = dialogSetting.getWindow();
    if (window != null) {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(window.getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT; // Đặt width thành MATCH_PARENT
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT; // Đặt height thành MATCH_PARENT
        window.setAttributes(layoutParams);
    }
    ImageButton closeButton = dialogSetting.findViewById(R.id.btnCloseDialogSetting);
    Button instruction = dialogSetting.findViewById(R.id.btnInstruction);
    Button contact = dialogSetting.findViewById(R.id.btnContact);

    //
    Button btnGoogle = dialogSetting.findViewById(R.id.btnLoginGoogle) ;
    btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = googleSignInClient.getSignInIntent() ;
                startActivityForResult(intent ,RC_SIGN_IN);
            }
        });
    //
    closeButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            closeSound.start();
            dialogSetting.dismiss();
        }
    });
    instruction.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final Dialog dialogInstruction = new Dialog(GameActivity.this);
            dialogInstruction.setContentView(R.layout.dialog_instruction);
            Window window = dialogInstruction.getWindow();
            if (window != null) {
                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                layoutParams.copyFrom(window.getAttributes());
                layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT; // Đặt width thành MATCH_PARENT
                layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT; // Đặt height thành MATCH_PARENT
                window.setAttributes(layoutParams);
            }
            Button ok = dialogInstruction.findViewById(R.id.btnOK);
            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    closeSound.start();
                    dialogInstruction.dismiss();
                }
            });
            dialogInstruction.show();
        }
    });
    contact.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String facebookUrl = "https://www.facebook.com/profile.php?id=61553593828929";
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(facebookUrl));
            startActivity(intent);
        }
    });
    dialogSetting.show();

}

private void showDialog_logout(){
    closeSound = MediaPlayer.create(this, R.raw.close_sound);
    final Dialog dialogSetting = new Dialog(GameActivity.this);
    dialogSetting.setContentView(R.layout.dialog_logout);
    Window window = dialogSetting.getWindow();
    if (window != null) {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(window.getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT; // Đặt width thành MATCH_PARENT
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT; // Đặt height thành MATCH_PARENT
        window.setAttributes(layoutParams);
    }
    ImageButton closeButton = dialogSetting.findViewById(R.id.btnCloseDialogSetting_logout);
    Button instruction = dialogSetting.findViewById(R.id.btnInstruction_logout);
    Button contact = dialogSetting.findViewById(R.id.btnContact_logout);
    Button btnLogout = dialogSetting.findViewById(R.id.btn_logout_urgent) ;

    //
    btnLogout.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showLogoutConfirmationDialog();
        }
    });
    //
    closeButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            closeSound.start();
            dialogSetting.dismiss();
        }
    });
    instruction.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final Dialog dialogInstruction = new Dialog(GameActivity.this);
            dialogInstruction.setContentView(R.layout.dialog_instruction);
            Window window = dialogInstruction.getWindow();
            if (window != null) {
                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                layoutParams.copyFrom(window.getAttributes());
                layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT; // Đặt width thành MATCH_PARENT
                layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT; // Đặt height thành MATCH_PARENT
                window.setAttributes(layoutParams);
            }
            Button ok = dialogInstruction.findViewById(R.id.btnOK);
            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    closeSound.start();
                    dialogInstruction.dismiss();
                }
            });
            dialogInstruction.show();
        }
    });
    contact.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String facebookUrl = "https://www.facebook.com/profile.php?id=61553593828929";
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(facebookUrl));
            startActivity(intent);
        }
    });
    dialogSetting.show();

}
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data) ;
            try {
               GoogleSignInAccount account = task.getResult(ApiException.class) ;
               firebaseAuth(account.getIdToken());

            }catch (Exception e){
                Toast.makeText(this , "Fail" , Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuth(String idToken){
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken , null) ;
        auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    FirebaseUser user = auth.getCurrentUser();
                    String uid = user.getUid() ;
                    DatabaseReference userRef = firebaseDatabase.getReference().child("users").child(uid) ;
                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (!snapshot.exists()){
                                //
                                HashMap<String, Object> map = new HashMap<>() ;
                                map.put("id" , user.getUid()) ;
                                map.put("name" ,user.getDisplayName()) ;
                                map.put("profile" , user.getPhotoUrl().toString()) ;
                                map.put("lvlExp" , 100) ;
                                //
                                map.put("userName" , "NoName") ;
                                map.put("farmName" , "NoName") ;
                                map.put("exp" , 0) ;
                                map.put("lv" , 1) ;
                                map.put("coin" , 50) ;
                                //
                                HashMap<String,Object> land = new HashMap<>() ;
                                HashMap<String , Object> att = new HashMap<>()  ;
                                att.put("status" , false) ;
                                att.put("time" , 0) ;

                                land.put("land1" ,att );
                                map.put("land" , land);
                                //
                                HashMap<String, Object> item = new HashMap<>();
                                HashMap<String, Object> animalData = new HashMap<>();
                                HashMap<String, Object> seedData = new HashMap<>();
                                HashMap<String , Object> plantData = new HashMap<>() ;

                                //
                                HashMap<String , Object> Slua = new HashMap<>() ;
                                Slua.put("quantity" , 0) ;
                                Slua.put("time" , 10) ;
                                Slua.put("imgEarly" ,"early_wheat") ;
                                Slua.put("imgProduct" , "wheat_product");
                                //
                                HashMap<String , Object> Scarot = new HashMap<>() ;
                                Scarot.put("quantity" , 0) ;
                                Scarot.put("time" , 10) ;
                                Scarot.put("imgEarly" ,"early_carrot") ;
                                Scarot.put("imgProduct" , "carrot_product");
                                //
                                HashMap<String , Object> Sbap = new HashMap<>() ;
                                Sbap.put("quantity" , 0) ;
                                Sbap.put("time" , 5) ;
                                Sbap.put("imgEarly" , "early_corn");
                                Sbap.put("imgProduct" , "corn_product") ;
                                //
                                HashMap<String , Object> Smia = new HashMap<>() ;
                                Smia.put("quantity" , 0) ;
                                Smia.put("time" , 5) ;
                                Smia.put("imgEarly" , "early_sugarcane");
                                Smia.put("imgProduct" , "sugarcane_product") ;
                                //
                                HashMap<String , Object> ga = new HashMap<>() ;
                                ga.put("quantity" , 0) ;
                                ga.put("time" , 1000) ;
                                //
                                HashMap<String , Object> bo = new HashMap<>() ;
                                bo.put("quantity" , 0) ;
                                bo.put("time" , 1000) ;
                                //
                                HashMap<String , Object> heo = new HashMap<>() ;
                                heo.put("quantity" , 0) ;
                                heo.put("time" , 1000) ;
                                //
                                HashMap<String , Object> cuu = new HashMap<>() ;
                                cuu.put("quantity" , 0) ;
                                cuu.put("time" , 1000) ;

                                seedData.put("Slua" , Slua) ;
                                seedData.put("Scarot" , Scarot) ;
                                seedData.put("Sbap",  Sbap) ;
                                seedData.put("Smia" ,Smia) ;

                                animalData.put("ga" , ga ) ;
                                animalData.put("bo" , bo) ;
                                animalData.put("heo" , heo) ;
                                animalData.put("cuu" , cuu) ;

                                //
                                HashMap<String , Object> attPLantLua = new HashMap<>() ;
                                attPLantLua.put("quantity", 0 ) ;
                                HashMap<String , Object> attPLantMia = new HashMap<>() ;
                                attPLantMia.put("quantity" , 0) ;
                                HashMap<String , Object> attPLantBap = new HashMap<>() ;
                                attPLantBap.put("quantity" , 0) ;
                                HashMap<String , Object> attPLantCarrot = new HashMap<>() ;
                                attPLantCarrot.put("quantity" , 0) ;

                                plantData.put("lua" ,attPLantLua) ;
                                plantData.put("carot" , attPLantCarrot);
                                plantData.put("bap" , attPLantBap) ;
                                plantData.put("mia" , attPLantMia) ;


                                item.put("Seed", seedData);
                                item.put("Animal", animalData);
                                item.put("Plant" ,plantData ) ;
                                map.put("item", item);

                                userRef.setValue(map) ;
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    Intent intent = new Intent(getApplicationContext() , GameActivity.class) ;
                    startActivity(intent);
                    Toast.makeText(GameActivity.this , "Successfully!" , Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(GameActivity.this , "Something went wrong" , Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateUI(FirebaseUser user) {
        if(user != null){
            String photoUrl = Objects.requireNonNull(user.getPhotoUrl()).toString();
            Picasso.with(this).load(photoUrl).into(avatar);
        }
    }
    //dialog message
    private void showLogoutConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Logout Confirmation");
        builder.setMessage("Are you sure you want to log out?");

        // Add the buttons
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked Yes button
                performLogout();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked No button
                dialog.dismiss(); // Dismiss the dialog without logging out
            }
        });

        // Create and show the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void performLogout() {
        FirebaseAuth.getInstance().signOut();
        GoogleSignIn.getClient(getApplicationContext(), GoogleSignInOptions.DEFAULT_SIGN_IN).signOut();
        Intent intent = new Intent(getApplicationContext(), StartActivity.class);
        startActivity(intent);
        finish();
    }


}