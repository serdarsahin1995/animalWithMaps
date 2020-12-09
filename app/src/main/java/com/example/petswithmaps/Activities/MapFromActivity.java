package com.example.petswithmaps.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.petswithmaps.FcmUtil;
import com.example.petswithmaps.Fragments.MapFragment;
import com.example.petswithmaps.MainActivity;
import com.example.petswithmaps.Models.KonumModel;
import com.example.petswithmaps.Models.RegisterModel;
import com.example.petswithmaps.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.UUID;

public class MapFromActivity extends AppCompatActivity {
    RegisterModel konumModel;

    public static final int CAMERA_PERM_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;
    public static final int ImageBack = 1;
    EditText editText, editTextMulti;
    Button button, button2;
    ImageView imageViewa;
    ProgressBar progressBar;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    Bitmap image;
    List<String> adresList, senduid;
    String text, key, detail, adres, sehir, city;
    DatabaseReference reference2 = database.getReference("konumlar").push();
    DatabaseReference reference3 = database.getReference("users");
    String random = "https://firebasestorage.googleapis.com/v0/b/petswithmaps.appspot.com/o/image%2F00661090-674d-48cf-83dd-4e04d743ca2f?alt=media&token=ab232a60-3f22-4db9-83ae-9cb10220ae26";
    StorageReference storageReference;
    StorageReference Folder;
    Context context;
    String konum1 = "", konum2 = "", adres1 = "", adres2 = "";
    ArrayList<String> a = new ArrayList<>();
    int count = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_form);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        storageReference = FirebaseStorage.getInstance().getReference();
        adresList = new ArrayList<>();
        senduid = new ArrayList<>();
        Folder = FirebaseStorage.getInstance().getReference().child("Image");
        button = findViewById(R.id.button);
        editText = findViewById(R.id.editTextName);
        editTextMulti = findViewById(R.id.editTextMultiLine);
        button2 = findViewById(R.id.button2);
        imageViewa = findViewById(R.id.imageView3);
        progressBar = findViewById(R.id.progressBar3);
        a = getIntent().getStringArrayListExtra("title");
        adres = getIntent().getStringExtra("title2");
        sehir = getIntent().getStringExtra("title3");
        city = getIntent().getStringExtra("title4");
        System.out.println(city);
        adres1 = adres.split(",")[0];
        adres2 = adres.split(",")[2];
        konum1 = a.get(0);
        konum2 = a.get(1);
        reference3.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                senduid.clear();
                for (DataSnapshot d : snapshot.getChildren()) {
                    konumModel = d.getValue(RegisterModel.class);
                    if (konumModel.getAdres().equals(city)) {
                        if (d.getKey().equals(auth.getUid())) {
                            System.out.println("kendisi");
                        } else {
                            senduid.add(d.getKey());
                        }


                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askCameraPermissions();
            }
        });


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text = editText.getText().toString();
                detail = editTextMulti.getText().toString();
                key = reference2.getKey();
                DatabaseReference reference = database.getReference("users").child(auth.getCurrentUser().getUid()).child("konumlar").child(key);
                adres1 = adres1.substring(0, 1).toLowerCase() + adres1.substring(1);
                city = city.substring(0, 1).toLowerCase() + city.substring(1);
                sehir = sehir.substring(0, 1).toLowerCase() + sehir.substring(1);
                KonumModel konumModelD = new KonumModel(konum1, konum2, text, detail, random, key, adres1, adres2, sehir, auth.getCurrentUser().getUid(), false, city);
                reference2.setValue(konumModelD);
                reference.setValue(konumModelD);
                MapFragment.fa.getActivity().finish();
                adres1 = adres1.substring(0, 1).toUpperCase() + adres1.substring(1);
                Intent intent = new Intent(MapFromActivity.this, MainActivity.class);
                FcmUtil fcmUtil = new FcmUtil();
                fcmUtil.sendNotificationCommon(MapFromActivity.this, "Selam", adres1+" konumunda yeni bir duyuru var!", senduid,konum1,konum2,adres1,false);
                startActivity(intent);
                finish();


            }
        });
        editText.addTextChangedListener(sendText);
        editTextMulti.addTextChangedListener(sendText);
    }

    TextWatcher sendText = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String name = editText.getText().toString();
            String multi = editTextMulti.getText().toString();
            text = editText.getText().toString();
            detail = editTextMulti.getText().toString();
            key = reference2.getKey();
            button.setEnabled(!name.isEmpty() && !multi.isEmpty());
            button2.setEnabled(!name.isEmpty() && !multi.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    private void askCameraPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERM_CODE);
        } else {
            openCamera();
        }
    }

    private void openCamera() {

        Intent camere = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(camere, CAMERA_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CAMERA_PERM_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Camera Permission is Required to Use camera.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (data != null) {
                image = (Bitmap) data.getExtras().get("data");
                imageViewa.setImageBitmap(image);
                imageViewa.setVisibility(View.VISIBLE);
                update();
            } else {
                System.out.println("Image null");
            }
        }
    }

    private void update() {
        progressBar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        random = UUID.randomUUID().toString();
        StorageReference imageRef = storageReference.child("image/" + random);
        byte[] b = stream.toByteArray();
        imageRef.putBytes(b)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressBar.setVisibility(View.GONE);
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                        taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Uri downloadUri = uri;
                            }
                        });
                        Toast.makeText(MapFromActivity.this, "Fotoğraf Yüklendi", Toast.LENGTH_SHORT).show();

                        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                random = String.valueOf(uri);
                                adres1 = adres1.substring(0, 1).toLowerCase() + adres1.substring(1);
                                city = city.substring(0, 1).toLowerCase() + city.substring(1);
                                sehir = sehir.substring(0, 1).toLowerCase() + sehir.substring(1);
                                System.out.println(random);
                                Intent intent = new Intent(MapFromActivity.this, MainActivity.class);
                                DatabaseReference reference = database.getReference("users").child(auth.getCurrentUser().getUid()).child("konumlar").child(key);
                                KonumModel konumModelD = new KonumModel(konum1, konum2, text, detail, random, key, adres1, adres2, sehir, auth.getCurrentUser().getUid(), false, city);
                                reference2.setValue(konumModelD).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        System.out.println(random);
                                        reference2.setValue(konumModelD);
                                        startActivity(intent);
                                    }
                                });
                                reference.setValue(konumModelD).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        reference.setValue(konumModelD);
                                        adres1 = adres1.substring(0, 1).toUpperCase() + adres1.substring(1);
                                        FcmUtil fcmUtil = new FcmUtil();
                                        fcmUtil.sendNotificationCommon(MapFromActivity.this, "Selam", adres1+" konumunda yeni bir duyuru var!", senduid,konum1,konum2,adres1,false);
                                        MapFragment.fa.getActivity().finish();
                                        finish();
                                    }
                                });
                            }

                        });

                    }

                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.GONE);
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                        Toast.makeText(MapFromActivity.this, "Fotoğraf Yüklenemedi", Toast.LENGTH_SHORT).show();
                    }
                });

    }
}