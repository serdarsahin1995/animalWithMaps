package com.example.petswithmaps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.MenuItem;

import com.example.petswithmaps.Fragments.NotificationFragment;
import com.example.petswithmaps.Fragments.MapFragment;
import com.example.petswithmaps.Fragments.ListFragment;
import com.example.petswithmaps.Fragments.ProfilFragment;
import com.example.petswithmaps.Models.FcmModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class MainActivity extends AppCompatActivity {
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public static Activity dur;
    int b, a;
    DatabaseReference databaseReference;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();
    FcmModel fcmModel;
    public static int bildirimCount;
    public static BadgeDrawable badge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(user.getUid()).child("bildirim");
        SharedPreferences sp = getApplicationContext().getSharedPreferences("User", Context.MODE_PRIVATE);
        String gece = sp.getString("gece", "a");
        String gunduz = sp.getString("gunduz", "b");
        if (!gunduz.equals("b")) {
            a = Integer.parseInt(gunduz);
        } else if (!gece.equals("a")) {
            b = Integer.parseInt(gece);
        }
        if (b == 1) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else if (a == 2) {
            System.out.println("lann");
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        BottomNavigationView btnNav = findViewById(R.id.bottomNavigationview);
        btnNav.setOnNavigationItemSelectedListener(navlistener);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot d : snapshot.getChildren()) {
                    fcmModel = d.getValue(FcmModel.class);
                    bildirimCount=0;
                    if (!fcmModel.isOkundu()) {
                        bildirimCount++;
                    }
                    int menuItemId = btnNav.getMenu().getItem(2).getItemId();
                    badge = btnNav.getOrCreateBadge(menuItemId);
                    badge.setBackgroundColor(Color.rgb(0, 155, 121));
                    badge.setVisible(true);
                    if (bildirimCount == 0) {
                        badge.setVisible(false);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        checkLocationPermission();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout, new ListFragment()).commit();
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                FcmUtil fcmUtil = new FcmUtil();
                fcmUtil.updateDeviceToken(MainActivity.this, instanceIdResult.getToken());
            }
        });
        dur = this;
    }

    BottomNavigationView.OnNavigationItemSelectedListener navlistener = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.EFFECT_TICK));
            } else {
                v.vibrate(50);
            }
            switch (item.getItemId()) {
                case R.id.fisrtFragment:
                    selectedFragment = new ListFragment();
                    break;
                case R.id.secondFragment:
                    selectedFragment = new MapFragment();
                    break;
                case R.id.thirdFragment:
                    selectedFragment = new ProfilFragment();
                    break;
                case R.id.fourthFragment:
                    selectedFragment = new NotificationFragment();
                    break;

            }
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout, selectedFragment).commit();
            return true;
        }
    };

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                    }
                } else {
                }
                return;
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
        }

    }

}

