package com.example.petswithmaps.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.petswithmaps.FcmUtil;
import com.example.petswithmaps.MainActivity;
import com.example.petswithmaps.Models.RegisterModel;
import com.example.petswithmaps.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class SingUpActivity extends AppCompatActivity {
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    Button Sing_btn;
    TextView logoText, callSingIn, sloganText, adres;
    TextInputLayout username, passwordG, emailG;
    ProgressBar progressBar, load;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    String userName,userMail,userPassword,random = "https://firebasestorage.googleapis.com/v0/b/todoandroid-b0acf.appspot.com/o/image%2F002dc8f6-1ab3-4c46-b229-e6f936843745?alt=media&token=a2e0618f-b1b4-442d-8d5e-21f871b7fa47", city = "Bilinmeyen";
    ImageView imageView;
    public static Activity registerdur;
    LatLng latlng;
    Geocoder geocoder;
    List<Address> addresses;
    CheckBox checkBox;
    TextInputEditText password, editText, name;
    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sing_up);
        callSingIn = findViewById(R.id.textView2);
        logoText = findViewById(R.id.textView);
        sloganText = findViewById(R.id.textView3);
        SupportMapFragment supportMapFragment;
        supportMapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
        supportMapFragment.getView().setVisibility(View.INVISIBLE);
        username = findViewById(R.id.name);
        passwordG = findViewById(R.id.password);
        Sing_btn = findViewById(R.id.buttonSing);
        emailG = findViewById(R.id.email);
        progressBar = findViewById(R.id.progressBar);
        adres = findViewById(R.id.adresT);
        load = findViewById(R.id.progressBar5);
        password = (TextInputEditText) findViewById(R.id.password2);
        editText = (TextInputEditText) findViewById(R.id.editText);
        name = (TextInputEditText) findViewById(R.id.name2);
        imageView = findViewById(R.id.support);
        Sing_btn.setEnabled(false);
        checkBox = findViewById(R.id.checkBox);
        registerdur = this;
        Sing_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userName = username.getEditText().getText().toString().trim();
                userMail = emailG.getEditText().getText().toString().trim();
                userPassword = passwordG.getEditText().getText().toString().trim();
                signUpFirabase(userMail, userPassword);

                System.out.println(userMail);
            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialog = new AlertDialog.Builder(SingUpActivity.this).setTitle("Bilgilendirme").setMessage("Bu butona tıkladığınızda sizden konum bilgileriniz için izin ister, onaylarsanız otomatik olarak konumnuz belirlenir. Bu özellik isteğe bağlı olup amacı ilçenizdeki duyuruları size bildirmektir.").
                        setPositiveButton("Tamam", null).setNegativeButton("reddet", null).show();
                Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        askCameraPermissions();
                        dialog.dismiss();
                    }
                });
            }
        });
        callSingIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SingUpActivity.this, LoginActivity.class);
                Pair[] pairs = new Pair[6];

                pairs[0] = new Pair<View, String>(logoText, "logo_text");
                pairs[1] = new Pair<View, String>(sloganText, "logo_desc");
                pairs[2] = new Pair<View, String>(username, "email_tran");
                pairs[3] = new Pair<View, String>(passwordG, "password_tran");
                pairs[4] = new Pair<View, String>(Sing_btn, "buton_tran");
                pairs[5] = new Pair<View, String>(callSingIn, "login_singup_tran");
                try {
                    LoginActivity.singdur.finish();
                } catch (Exception exception) {
                    System.out.println("la");
                }
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SingUpActivity.this, pairs);

                startActivity(intent, options.toBundle());

            }
        });
        adres.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                askCameraPermissions();
                imageView.setVisibility(View.INVISIBLE);
                load.setVisibility(View.VISIBLE);
                supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        if (ActivityCompat.checkSelfPermission(SingUpActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(SingUpActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

                                                                            geocoder = new Geocoder(SingUpActivity.this, Locale.getDefault());
                                                                            try {
                                                                                addresses = geocoder.getFromLocation(latlng.latitude, latlng.longitude, 1);
                                                                                city = addresses.get(0).getSubAdminArea();
                                                                                System.out.println(city);
                                                                                load.setVisibility(View.INVISIBLE);
                                                                                Toast.makeText(SingUpActivity.this, city, Toast.LENGTH_LONG).show();
                                                                                adres.setClickable(false);
                                                                                checkBox.setVisibility(View.VISIBLE);
                                                                                checkBox.setChecked(true);
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
        password.addTextChangedListener(new SingUpActivity.ValidationTextWatcher(password));
        editText.addTextChangedListener(new SingUpActivity.ValidationTextWatcher(editText));
        name.addTextChangedListener(new SingUpActivity.ValidationTextWatcher(name));
    }


    public void signUpFirabase(String userEmail, String userPassword) {
        progressBar.setVisibility(View.VISIBLE);
        auth.createUserWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(SingUpActivity.this, auth.getCurrentUser().getUid(), Toast.LENGTH_SHORT).show();
                    DatabaseReference reference = database.getReference("users").child(auth.getCurrentUser().getUid());
                    RegisterModel registerModelPush = new RegisterModel(userName, userMail, random, city);
                    reference.setValue(registerModelPush);
                    finish();
                    SingUpActivity.registerdur.finish();
                    progressBar.setVisibility(View.INVISIBLE);
                } else {
                    Toast.makeText(SingUpActivity.this, "Hesabın oluşturulmadı", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);

                }
            }
        });

    }

    private boolean askCameraPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                ActivityCompat.requestPermissions(SingUpActivity.this,
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

    }

    public class ValidationTextWatcher implements TextWatcher {
        public ValidationTextWatcher(TextInputEditText password) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String name1 = name.getText().toString();
            String email = editText.getText().toString();
            String multi = password.getText().toString();
            Sing_btn.setEnabled(!email.isEmpty() && !multi.isEmpty() && multi.length() > 5 && !name1.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {

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