package com.example.petswithmaps;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.petswithmaps.Models.FcmModel;
import com.example.petswithmaps.Models.RegisterModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

public class FcmUtil {
    LinkedHashSet<String> uniqueStrings;

    public void updateDeviceToken(final Context context, String token) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser != null) {
            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
            DatabaseReference databaseReference = rootRef.child("users").child(currentUser.getUid());
            try {
                databaseReference.child(FcmModel.TOKEN).setValue(token);
            } catch (Exception exception) {
                System.out.println(exception);
            }

        }
    }

    public void sendNotification(final Context context, final String title, final String message, String userId) {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference databaseReference = rootRef.child("users").child(userId);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(FcmModel.TOKEN).getValue() != null) {
                }
                String deviceToken = dataSnapshot.child(FcmModel.TOKEN).getValue().toString();
                JSONObject notification = new JSONObject();
                JSONObject notificationData = new JSONObject();

                try {
                    notificationData.put(FcmModel.NOTIFICATION_TITLE, title);
                    notificationData.put(FcmModel.NOTIFICATION_MESSAGE, message);
                    notification.put(FcmModel.NOTIFICATION_TO, deviceToken);
                    notification.put(FcmModel.NOTIFICATION_DATA, notificationData);

                    String fcmApiUrl = "https://fcm.googleapis.com/fcm/send";
                    final String contentType = "application/json";

                    Response.Listener successListener = new Response.Listener() {
                        @Override
                        public void onResponse(Object response) {
                            Log.d("serdar","bildirim gönderildi");
                        }
                    };

                    Response.ErrorListener failureListener = new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(context,
                                    "bildirim gönderilmedi"
                                    , Toast.LENGTH_SHORT).show();
                        }
                    };
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(fcmApiUrl, notification,
                            successListener, failureListener) {

                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {

                            Map<String, String> params = new HashMap<>();

                            params.put("Authorization", "key=AAAAmplTFiw:APA91bGYDSx5MtQAlMTiqJZclAu9pNZseZp3XrauamclYDHAYWdiwGg04IYkUeykZ2zv53ayQLbIXHqPnYDTJq-2HlXVne8UaUrjfPPGW4k846tKN2E7rmwal0n6jlDkU43Mwx5Z2XZe");
                            params.put("Sender", "id=663997322796");
                            params.put("Content-Type", contentType);

                            return params;
                        }
                    };

                    RequestQueue requestQueue = Volley.newRequestQueue(context);
                    requestQueue.add(jsonObjectRequest);


                } catch (JSONException e) {
                    Toast.makeText(context,
                            "bildirim gönderilmedi"
                            , Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context,
                        "bildirim gönderilmedi"
                        , Toast.LENGTH_SHORT).show();
            }
        });


    }

    public void sendNotificationCommon(final Context context, final String title, final String message, List<String> adresList,String konum1,String konum2) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("users");
        for (int i = 0; i <= adresList.size()-1; i++) {
            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
            DatabaseReference databaseReference = rootRef.child("users").child(adresList.get(i));
            DatabaseReference databaseReference2 = rootRef.child("users").child(adresList.get(i)).child("bildirim").push();
            databaseReference2.child("title").setValue(title);
            databaseReference2.child("message").setValue(message);
            databaseReference2.child("konum1").setValue(konum1);
            databaseReference2.child("konum2").setValue(konum2);
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(FcmModel.TOKEN).getValue() != null) {
                    }
                    String deviceToken = dataSnapshot.child(FcmModel.TOKEN).getValue().toString();
                    JSONObject notification = new JSONObject();
                    JSONObject notificationData = new JSONObject();

                    try {
                        notificationData.put(FcmModel.NOTIFICATION_TITLE, title);
                        notificationData.put(FcmModel.NOTIFICATION_MESSAGE, message);
                        notification.put(FcmModel.NOTIFICATION_TO, deviceToken);
                        notification.put(FcmModel.NOTIFICATION_DATA, notificationData);

                        String fcmApiUrl = "https://fcm.googleapis.com/fcm/send";
                        final String contentType = "application/json";

                        Response.Listener successListener = new Response.Listener() {
                            @Override
                            public void onResponse(Object response) {
                            }
                        };

                        Response.ErrorListener failureListener = new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(context,
                                        "bildirim gönderilmedi"
                                        , Toast.LENGTH_SHORT).show();
                            }
                        };
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(fcmApiUrl, notification,
                                successListener, failureListener) {

                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {

                                Map<String, String> params = new HashMap<>();

                                params.put("Authorization", "key=AAAAmplTFiw:APA91bGYDSx5MtQAlMTiqJZclAu9pNZseZp3XrauamclYDHAYWdiwGg04IYkUeykZ2zv53ayQLbIXHqPnYDTJq-2HlXVne8UaUrjfPPGW4k846tKN2E7rmwal0n6jlDkU43Mwx5Z2XZe");
                                params.put("Sender", "id=663997322796");
                                params.put("Content-Type", contentType);

                                return params;
                            }
                        };

                        RequestQueue requestQueue = Volley.newRequestQueue(context);
                        requestQueue.add(jsonObjectRequest);


                    } catch (JSONException e) {
                        Toast.makeText(context,
                                "bildirim gönderilmedi"
                                , Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(context,
                            "bildirim gönderilmedi"
                            , Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
