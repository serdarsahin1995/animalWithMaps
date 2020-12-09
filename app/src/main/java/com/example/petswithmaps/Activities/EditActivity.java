package com.example.petswithmaps.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.petswithmaps.MainActivity;
import com.example.petswithmaps.Models.KonumModel;
import com.example.petswithmaps.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

public class EditActivity extends AppCompatActivity {
    public static final int CAMERA_PERM_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference;
    String baslık, resim, acıklama, key, gelen, text, detail, sehir,city;
    ImageView imageView;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();
    EditText editText, editTextMulti;
    Button button, button2;
    ImageView imageViewa;
    ProgressBar progressBar;
    Bitmap image;
    StorageReference storageReference;
    StorageReference Folder;
    String konum1 = "", konum2 = "", adres1 = "", adres2 = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Düzenle");
        Intent intent = getIntent();
        gelen = intent.getStringExtra("item");
        System.out.println(gelen);
        storageReference = FirebaseStorage.getInstance().getReference();
        Folder = FirebaseStorage.getInstance().getReference().child("Image");
        button = findViewById(R.id.button);
        editText = findViewById(R.id.editTextName);
        editTextMulti = findViewById(R.id.editTextMultiLine);
        button2 = findViewById(R.id.button2);
        imageViewa = findViewById(R.id.imageView3);
        progressBar = findViewById(R.id.progressBar3);
        DatabaseReference reference = database.getReference("konumlar").child(gelen);
        DatabaseReference reference2;
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    baslık = snapshot.child("text").getValue().toString();
                    acıklama = snapshot.child("detail").getValue().toString();
                    resim = snapshot.child("resim").getValue().toString();
                    key = snapshot.child("key").getValue().toString();
                    adres1 = snapshot.child("adres1").getValue().toString();
                    adres2 = snapshot.child("adres2").getValue().toString();
                    konum1 = snapshot.child("konum1").getValue().toString();
                    konum2 = snapshot.child("konum2").getValue().toString();
                    sehir = snapshot.child("sehir").getValue().toString();
                    city = snapshot.child("city").getValue().toString();
                    Picasso.get().load(resim).into(imageView);
                } catch (Exception exception) {
                    System.out.println("xa");
                }
                editText.setText(baslık);
                editTextMulti.setText(acıklama);
                Picasso.get().load(resim).into(imageViewa);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }

        });
        reference2 = database.getReference("users").child(auth.getCurrentUser().getUid()).child("konumlar").child(gelen);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text = editText.getText().toString();
                detail = editTextMulti.getText().toString();
                adres1 = adres1.substring(0, 1).toLowerCase() + adres1.substring(1);
                city = city.substring(0, 1).toLowerCase() + city.substring(1);
                sehir = sehir.substring(0, 1).toLowerCase() + sehir.substring(1);
                KonumModel konumModelD = new KonumModel(konum1, konum2, text, detail, resim, gelen, adres1, adres2, sehir, auth.getCurrentUser().getUid(),false,city);
                text = editText.getText().toString();
                detail = editTextMulti.getText().toString();
                reference2.setValue(konumModelD);
                reference.setValue(konumModelD);
                MainActivity.dur.finish();
                Intent intent = new Intent(EditActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askCameraPermissions();
            }
        });
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
        text = editText.getText().toString();
        detail = editTextMulti.getText().toString();
        progressBar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        resim = UUID.randomUUID().toString();
        StorageReference imageRef = storageReference.child("image/" + resim);
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
                        Toast.makeText(EditActivity.this, "Fotoğraf Güncellendi", Toast.LENGTH_SHORT).show();

                        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                resim = String.valueOf(uri);
                                System.out.println(resim);
                                adres1 = adres1.substring(0, 1).toLowerCase() + adres1.substring(1);
                                city = city.substring(0, 1).toLowerCase() + city.substring(1);
                                sehir = sehir.substring(0, 1).toLowerCase() + sehir.substring(1);
                                DatabaseReference reference3 = database.getReference("users").child(auth.getCurrentUser().getUid()).child("konumlar").child(gelen);
                                reference = database.getReference("konumlar").child(gelen);
                                KonumModel konumModelD = new KonumModel(konum1, konum2, text, detail, resim, gelen, adres1, adres2, sehir, auth.getCurrentUser().getUid(),false,city);
                                reference3.setValue(konumModelD).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        reference3.setValue(konumModelD);

                                    }
                                });
                                reference.setValue(konumModelD).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        reference.setValue(konumModelD);
                                        Intent intent = new Intent(EditActivity.this, MainActivity.class);
                                        startActivity(intent);
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

                        Toast.makeText(EditActivity.this, "Fotoğraf Güncellenemedi", Toast.LENGTH_SHORT).show();
                    }
                });


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}