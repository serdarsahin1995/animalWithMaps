package com.example.petswithmaps.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.petswithmaps.PicassoTmp.CircleTransform;
import com.example.petswithmaps.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
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
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class ProfilEditActivity extends AppCompatActivity {
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public static final int CAMERA_PERM_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;
    String userName, userMail, userPassword;
    TextInputLayout username, passwordG, emailG;
    LatLng latlng;
    Geocoder geocoder;
    List<Address> addresses;
    ProgressBar load;
    CheckBox checkBox;
    TextInputEditText password, editText, name;
    ImageView resim,rehlesh;
    StorageReference storageReference;
    Bitmap image;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    Button Sing_btn;
    ConstraintLayout constraintLayout;
    String resimG,gelen,adres2,city = "Bilinmeyen", random = "https://firebasestorage.googleapis.com/v0/b/todoandroid-b0acf.appspot.com/o/image%2F002dc8f6-1ab3-4c46-b229-e6f936843745?alt=media&token=a2e0618f-b1b4-442d-8d5e-21f871b7fa47";;
    TextView  adres;
    int count=0;
    DatabaseReference reference = database.getReference("users").child(auth.getCurrentUser().getUid());
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil_edit);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Profil Düzenle");
        username = findViewById(R.id.name);
        rehlesh=findViewById(R.id.reflesh);
        passwordG = findViewById(R.id.password);
        Sing_btn = findViewById(R.id.buttonSing);
        emailG = findViewById(R.id.email);
        adres=findViewById(R.id.adresT);
        storageReference = FirebaseStorage.getInstance().getReference();
        checkBox = findViewById(R.id.checkBox);
        load=findViewById(R.id.progressBar5);
        constraintLayout = findViewById(R.id.constraintLayout2);
        SupportMapFragment supportMapFragment;
        supportMapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
        supportMapFragment.getView().setVisibility(View.INVISIBLE);
        password = (TextInputEditText) findViewById(R.id.password2);
        editText = (TextInputEditText) findViewById(R.id.editText);
        name = (TextInputEditText) findViewById(R.id.name2);
        resim = findViewById(R.id.user_imageview2);
        username.setEnabled(false);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                name.setText(snapshot.child("email").getValue().toString());
                editText.setText(snapshot.child("name").getValue().toString());
                resimG = snapshot.child("photo").getValue().toString();
                adres2=snapshot.child("adres").getValue().toString();
                adres.setText("Konumunuz "+adres2);
                city=adres2;
                System.out.println(resimG);
                Picasso.get().load(resimG).transform(new CircleTransform()).placeholder(R.drawable.progress_animation).into(resim);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        Sing_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reference.child("name").setValue(gelen);
                reference.child("adres").setValue(city);
                reference.child("photo").setValue(random);
            }
        });
resim.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        askCameraPermissions();
    }
});
        constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askGPSPermissions();
                rehlesh.setVisibility(View.INVISIBLE);
                load.setVisibility(View.VISIBLE);
                supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        if (ActivityCompat.checkSelfPermission(ProfilEditActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ProfilEditActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        googleMap.setMyLocationEnabled(true);
                        googleMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                                                                    @Override
                                                                    public void onMyLocationChange(Location location) {
                                                                        latlng = new LatLng(location.getLatitude(), location.getLongitude());
                                                                        if (count == 0) {

                                                                            geocoder = new Geocoder(ProfilEditActivity.this, Locale.getDefault());
                                                                            try {
                                                                                addresses = geocoder.getFromLocation(latlng.latitude, latlng.longitude, 1);
                                                                                city = addresses.get(0).getSubAdminArea();
                                                                                System.out.println(city);
                                                                                load.setVisibility(View.INVISIBLE);
                                                                                Toast.makeText(ProfilEditActivity.this, city, Toast.LENGTH_LONG).show();
                                                                                adres.setClickable(false);
                                                                                adres.setText("Konumunuz "+city);
                                                                                checkBox.setVisibility(View.VISIBLE);
                                                                                checkBox.setChecked(true);
                                                                                constraintLayout.setClickable(false);
                                                                                count++;
                                                                            } catch (IOException e) {
                                                                                e.printStackTrace();
                                                                            }
                                                                        }

                                                                    }
                                                                }
                        );
                    }
                });
            }
        });
        editText.addTextChangedListener(new ValidationTextWatcher(editText));
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    public class ValidationTextWatcher implements TextWatcher {
        public ValidationTextWatcher(TextInputEditText password) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            gelen= editText.getText().toString();

        }

        @Override
        public void afterTextChanged(Editable s) {

        }

    }
    private boolean askGPSPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                ActivityCompat.requestPermissions(ProfilEditActivity.this,
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
                load.setVisibility(View.INVISIBLE);
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
        if (requestCode == CAMERA_PERM_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Camera Permission is Required to Use camera.", Toast.LENGTH_SHORT).show();
            }
        }

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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (data != null) {
                image = (Bitmap) data.getExtras().get("data");
                resim.setImageBitmap(image);
                resim.setVisibility(View.VISIBLE);
                update();
            } else {
                System.out.println("Image null");
            }
        }
    }
    private void update() {
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

                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                        taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Uri downloadUri = uri;
                            }
                        });
                        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                random = String.valueOf(uri);
                                System.out.println(random);

                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                        Toast.makeText(ProfilEditActivity.this, "Fotoraf Güncellenemedi", Toast.LENGTH_SHORT).show();
                    }
                });

    }

}