package com.example.petswithmaps.Fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.example.petswithmaps.Activities.LoginActivity;
import com.example.petswithmaps.PicassoTmp.CircleBubbleTransformation;
import com.example.petswithmaps.Activities.MapTempActivity;
import com.example.petswithmaps.R;
import com.example.petswithmaps.Models.KonumModel;
import com.example.petswithmaps.Activities.MapFromActivity;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapFragment extends Fragment {
    String konum1, konum2;

    public MapFragment(String konum1, String konum2) {
        this.konum1 = konum1;
        this.konum2 = konum2;
    }

    public MapFragment() {

    }

    static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 23;
    boolean konum = false;
    KonumModel konumModel;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference = database.getReference("konumlar");
    int count = 0;
    List<String> k1, k2, baslık, key, resim;
    List<Double> loc1, loc2;
    Geocoder geocoder;
    List<Address> addresses;
    String address, state,city;
    FloatingActionButton floatingActionButton;
    public static Fragment fa;
    boolean ekle;
    LatLng latlng;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fa = this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        k1 = new ArrayList<>();
        k2 = new ArrayList<>();
        baslık = new ArrayList<>();
        key = new ArrayList<>();
        resim = new ArrayList<>();
        loc1 = new ArrayList<>();
        loc2 = new ArrayList<>();
        floatingActionButton = view.findViewById(R.id.floatingActionButton);
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {

                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot d : snapshot.getChildren()) {
                            konumModel = d.getValue(KonumModel.class);
                            k1.add(konumModel.getKonum1());
                            k2.add(konumModel.getKonum2());
                            key.add(konumModel.getKey());
                            baslık.add(konumModel.getText());
                            resim.add(konumModel.getResim());
                            double latitude = Double.parseDouble(k1.get(count));
                            double longitude = Double.parseDouble(k2.get(count));
                            String keya = key.get(count);
                            String bas = baslık.get(count);
                            String res = resim.get(count);
                            LatLng location = new LatLng(latitude, longitude);
                            Picasso.get()
                                    .load(res)
                                    .resize(200, 200)
                                    .centerCrop()
                                    .transform(new CircleBubbleTransformation())
                                    .into(new com.squareup.picasso.Target() {
                                        @Override
                                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                            Marker driver_marker = googleMap.addMarker(new MarkerOptions()
                                                    .position(location)
                                                    .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                                                    .title(bas)
                                                    .snippet(keya));
                                        }

                                        @Override
                                        public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                                        }

                                        @Override
                                        public void onPrepareLoad(Drawable placeHolderDrawable) {

                                        }
                                    });
                            count++;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }

                });
                if (konum1 == null) {
                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
                            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
                                    latlng, 15);
                            if (konum == false) {
                                googleMap.animateCamera(cameraUpdate);
                                konum = true;
                            }

                        }
                    });
                    floatingActionButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String latitude = String.valueOf(latlng.latitude).substring(0, 6);
                            String longitude = String.valueOf(latlng.longitude).substring(0, 6);
                            ekle = false;
                            for (int i = 0; i <= k1.size() - 1; i++) {
                                String lat = k1.get(i).substring(0, 6);
                                String lon = k2.get(i).substring(0, 6);
                                if (lat.equals(latitude) && lon.equals(longitude)) {
                                    ekle = true;
                                }
                                System.out.println(k1.get(i));
                                System.out.println(latlng.latitude);
                            }
                            if (ekle == false) {
                                MarkerOptions markerOptions = new MarkerOptions();
                                markerOptions.title(latlng.latitude + " : " + latlng.longitude);
                                geocoder = new Geocoder(getContext(), Locale.getDefault());
                                try {
                                    addresses = geocoder.getFromLocation(latlng.latitude, latlng.longitude, 1);
                                    address = addresses.get(0).getAddressLine(0);
                                    state = addresses.get(0).getAdminArea();
                                    city = addresses.get(0).getSubAdminArea();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                ArrayList<String> a = new ArrayList<String>();
                                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 15));
                                Intent i = new Intent(getActivity(), MapFromActivity.class);
                                a.add(String.valueOf(latlng.latitude));
                                a.add(String.valueOf(latlng.longitude));
                                i.putExtra("title4", city);
                                i.putExtra("title3", state);
                                i.putExtra("title2", address);
                                i.putExtra("title", a);
                                startActivity(i);
                            } else if (ekle == true) {
                                Toast.makeText(getActivity(), "Burada marker mevcut", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    googleMap.setMyLocationEnabled(true);
                    double latitudee = Double.parseDouble(konum1);
                    double longitudee = Double.parseDouble(konum2);
                    LatLng latlng = new LatLng(latitudee, longitudee);
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 15));
                }
                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {

                        Intent i = new Intent(getActivity(), MapTempActivity.class);
                        String b = marker.getSnippet();
                        i.putExtra("item", b);
                        startActivity(i);

                        return false;
                    }
                });
            }

        });


        return view;

    }


}