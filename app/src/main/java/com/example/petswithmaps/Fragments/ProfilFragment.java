package com.example.petswithmaps.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.petswithmaps.Activities.LoginActivity;
import com.example.petswithmaps.PicassoTmp.CircleTransform;
import com.example.petswithmaps.PicassoTmp.RoundedCornersTransformation;
import com.example.petswithmaps.Activities.ProfilEditActivity;
import com.example.petswithmaps.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;


public class ProfilFragment extends Fragment {
    public static final int CAMERA_PERM_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;
    final int radius = 8, margin = 8;
    final Transformation transformation = new RoundedCornersTransformation(radius, margin);
    TextView name, email;
    ImageView resim;
    Bitmap image;
    SharedPreferences sp;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    StorageReference storageReference;
    Button button;
    DatabaseReference reference = database.getReference("users").child(auth.getCurrentUser().getUid());
    String emailG, resimG, nameG, random = "https://firebasestorage.googleapis.com/v0/b/todoandroid-b0acf.appspot.com/o/image%2F002dc8f6-1ab3-4c46-b229-e6f936843745?alt=media&token=a2e0618f-b1b4-442d-8d5e-21f871b7fa47";

    public ProfilFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        sp=getActivity().getSharedPreferences("User", Context.MODE_PRIVATE);
        super.onCreate(savedInstanceState);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profil, container, false);
        resim = view.findViewById(R.id.user_imageview);
        storageReference = FirebaseStorage.getInstance().getReference();
        name = view.findViewById(R.id.name_textview);

        button = view.findViewById(R.id.button4);
        getActivity().setTitle("Profil");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), ProfilEditActivity.class);
                startActivity(i);
            }
        });
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                emailG = snapshot.child("email").getValue().toString();
                nameG = snapshot.child("name").getValue().toString();
                resimG = snapshot.child("photo").getValue().toString();
                name.setText(nameG);
                Picasso.get().load(resimG).transform(new CircleTransform()).placeholder(R.drawable.progress_animation).into(resim);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.profilmenu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.cikis) {
            reference.child("token").setValue("");
            FirebaseAuth.getInstance().signOut();
            Intent i = new Intent(getActivity(), LoginActivity.class);
            startActivity(i);
            getActivity().finish();
        }
        if(id == R.id.degis){
            SharedPreferences.Editor editor;
            BottomNavigationView mBottomNavigationView = getActivity().findViewById(R.id.bottomNavigationview);
            editor= sp.edit();
            int nightModeFlags =
                    getContext().getResources().getConfiguration().uiMode &
                            Configuration.UI_MODE_NIGHT_MASK;
            switch (nightModeFlags) {
                case Configuration.UI_MODE_NIGHT_YES:
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    editor.putString("gece","1");
                    sp.edit().remove("gunduz").apply();
                    break;
                case Configuration.UI_MODE_NIGHT_NO:
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    editor.putString("gunduz","2");
                    sp.edit().remove("gece").apply();
                    break;
            }
            editor.apply();
            mBottomNavigationView.setSelectedItemId(R.id.fisrtFragment);
        }
        return super.onOptionsItemSelected(item);
    }
}