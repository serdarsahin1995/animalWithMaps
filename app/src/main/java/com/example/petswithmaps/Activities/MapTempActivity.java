package com.example.petswithmaps.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;

import com.example.petswithmaps.Fragments.MapDetailFragment;
import com.example.petswithmaps.R;

public class MapTempActivity extends AppCompatActivity {
    String gelen;
    Bundle bundle = new Bundle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_temp);
        Intent intent = getIntent();
        gelen = intent.getStringExtra("item");
        bundle.putString("item", gelen);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        MapDetailFragment mapDetailFragment = new MapDetailFragment();
        mapDetailFragment.setArguments(bundle);
        fragmentTransaction.add(R.id.frame, mapDetailFragment);
        fragmentTransaction.commit();
    }
}

