package com.example.petswithmaps.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.petswithmaps.MainActivity;
import com.example.petswithmaps.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    Button login_btn;
    TextView logoText, callSingUp, sloganText;
    TextInputLayout email, pass;
    ProgressBar progressBar;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    DatabaseReference reference2;
    TextInputEditText password, editText;
    FirebaseUser user = auth.getCurrentUser();
    int b,a;
    public static Activity singdur;
    String emailG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        callSingUp = findViewById(R.id.textView5);
        logoText = findViewById(R.id.textView);
        sloganText = findViewById(R.id.textView3);
        email = findViewById(R.id.name);
        pass = findViewById(R.id.password);
        login_btn = findViewById(R.id.buttonSing);
        SharedPreferences sp= getApplicationContext().getSharedPreferences("User", Context.MODE_PRIVATE);
        String gece = sp.getString("gece","a");
        String gunduz = sp.getString("gunduz","b");
        if(!gunduz.equals("b")){
            a = Integer.parseInt(gunduz);
        }
        else if(!gece.equals("a")){
            b = Integer.parseInt(gece);
        }else if(gunduz.equals("b")&&gece.equals("a")){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }
        if(b==1){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }else if(a==2){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        progressBar = findViewById(R.id.progressBar2);
        password = (TextInputEditText) findViewById(R.id.password2);
        editText = (TextInputEditText) findViewById(R.id.editText);
        login_btn.setEnabled(false);
        try {
            reference2 = database.getReference("users").child(auth.getCurrentUser().getUid());
        }catch (Exception e){

        }
        singdur = this;


        callSingUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SingUpActivity.class);
                Pair[] pairs = new Pair[6];

                pairs[0] = new Pair<View, String>(logoText, "logo_text");
                pairs[1] = new Pair<View, String>(sloganText, "logo_desc");
                pairs[2] = new Pair<View, String>(email, "email_tran");
                pairs[3] = new Pair<View, String>(pass, "password_tran");
                pairs[4] = new Pair<View, String>(login_btn, "buton_tran");
                pairs[5] = new Pair<View, String>(callSingUp, "login_singup_tran");
                try {
                    SingUpActivity.registerdur.finish();
                } catch (Exception exception) {
                    Log.d("giris", String.valueOf(exception));
                }
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this, pairs);

                startActivity(intent, options.toBundle());

            }
        });
        password.addTextChangedListener(new ValidationTextWatcher(password));
        editText.addTextChangedListener(new ValidationTextWatcher(editText));
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emaila = email.getEditText().getText().toString().trim();
                String passwords = pass.getEditText().getText().toString().trim();
                signInFirebase(emaila, passwords);
            }
        });
    }

    private class ValidationTextWatcher implements TextWatcher {
        private View view;
        private ValidationTextWatcher(View view) {
            this.view = view;
        }
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String name = editText.getText().toString();
            String multi = password.getText().toString();
                login_btn.setEnabled(!name.isEmpty() && !multi.isEmpty()&&multi.length()>5);
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }
    public void signInFirebase(String email, String password) {
        progressBar.setVisibility(View.VISIBLE);
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent i = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(i);
                            finish();
                            progressBar.setVisibility(View.INVISIBLE);

                        } else {
                            Toast.makeText(LoginActivity.this, "Email veya Şifre Hatalı", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.INVISIBLE);
                        }// ...
                    }
                });

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            try {
                reference2.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try {
                            emailG = snapshot.child("email").getValue().toString();
                        }catch (Exception e){
                            FirebaseAuth.getInstance().signOut();
                            Intent i = new Intent(LoginActivity.this,SingUpActivity.class);
                            startActivity(i);
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }catch (Exception exception){

            }
            Toast.makeText(LoginActivity.this, auth.getCurrentUser().getUid(), Toast.LENGTH_LONG).show();
            Intent i = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        }
    }
}