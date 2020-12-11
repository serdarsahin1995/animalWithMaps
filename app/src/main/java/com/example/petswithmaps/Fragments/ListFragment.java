package com.example.petswithmaps.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.example.petswithmaps.PicassoTmp.CircleBubbleTransformation;
import com.example.petswithmaps.R;
import com.example.petswithmaps.Models.KonumModel;
import com.example.petswithmaps.MyAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;


public class ListFragment extends Fragment {
    FirebaseAnalytics mFirebaseAnalytics;
    RecyclerView recView;
    MyAdapter adapter;
    SwipeRefreshLayout swipeRefreshLayout;
    KonumModel konumModel;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference = database.getReference("konumlar");
    int count = 0;
    List<String> k1, k2, baslık, key, resim, sehirtmp, sehir;
    List<Double> loc1, loc2;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();
    boolean gecis, gecis2, degis = false;
    Spinner spinner;
    LinkedHashSet<String> uniqueStrings;
    String uid;
    TextView duyuru;
    public static ProgressBar progressBar;

    public ListFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        recView = view.findViewById(R.id.recycler);
        SupportMapFragment supportMapFragment;
        supportMapFragment = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map));
        supportMapFragment.getView().setVisibility(View.INVISIBLE);
        swipeRefreshLayout = view.findViewById(R.id.swipe_container);
        initializeRefreshListener();
        getActivity().setTitle("Liste");
        k1 = new ArrayList<>();
        k2 = new ArrayList<>();
        duyuru=view.findViewById(R.id.textView8);
        baslık = new ArrayList<>();
        key = new ArrayList<>();
        resim = new ArrayList<>();
        loc1 = new ArrayList<>();
        loc2 = new ArrayList<>();
        sehirtmp = new ArrayList<>();
         progressBar = view.findViewById(R.id.progressBar4);
        uniqueStrings = new LinkedHashSet<String>();
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot d : snapshot.getChildren()) {
                            konumModel = d.getValue(KonumModel.class);
                            k1.add(konumModel.getKonum1());
                            k2.add(konumModel.getKonum2());
                            key.add(konumModel.getKey());
                            baslık.add(konumModel.getText());
                            resim.add(konumModel.getResim());
                            sehirtmp.add(konumModel.getSehir());
                            double latitude = Double.parseDouble(k1.get(count));
                            double longitude = Double.parseDouble(k2.get(count));
                            String keya = key.get(count);
                            String bas = baslık.get(count);
                            String res = resim.get(count);
                            uid = konumModel.getUid();
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

            }
        });
        recView.setLayoutManager(new LinearLayoutManager(getContext()));
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    duyuru.setVisibility(View.INVISIBLE);
                }else {
                    duyuru.setText("Duyuru Yok");
                    duyuru.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                    recView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        FirebaseRecyclerOptions<KonumModel> options =
                new FirebaseRecyclerOptions.Builder<KonumModel>()
                        .setQuery(reference, KonumModel.class)
                        .build();
        adapter = new MyAdapter(options);
        recView.setAdapter(adapter);
        return view;
    }

    private void initializeRefreshListener() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if (!degis) {
                    if (gecis) {
                        customLocation();
                    } else if (!gecis) {
                        defaultLocation();
                    }
                } else {
                    choose();
                }
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (swipeRefreshLayout.isRefreshing()) {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }
                }, 350);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.listmenu, menu);
        MenuItem item = menu.findItem(R.id.arama);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                newText=newText.toLowerCase();
                processearch(newText);

                return true;
            }

        });

    }
    private void processearch(String s) {
        Query ref=FirebaseDatabase.getInstance().getReference().child("konumlar").orderByChild("adres1").startAt(s).endAt(s + "\uf8ff");
        FirebaseRecyclerOptions<KonumModel> options =
                new FirebaseRecyclerOptions.Builder<KonumModel>()
                        .setQuery(ref, KonumModel.class).build();
        adapter = new MyAdapter(options);
        adapter.startListening();
        recView.setAdapter(adapter);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.ilan) {
            if (gecis == false) {
                item.setIcon(R.drawable.ic_baseline_person_24);
                customLocation();
                gecis = true;
            } else {
                item.setIcon(R.drawable.ic_baseline_person_outline_24);
                defaultLocation();
                gecis = false;
            }
        }
        if (id == R.id.kisitlama) {
            if (gecis2 == false) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                View view = getLayoutInflater().inflate(R.layout.dialog_spinner, null);
                builder.setTitle("Şehir Seç");
                spinner = (Spinner) view.findViewById(R.id.spinner);
                for (int i = 0; i < sehirtmp.size(); i++) {
                    uniqueStrings.add(sehirtmp.get(i));
                }
                sehir = new ArrayList<>(uniqueStrings);


                ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, sehir);
                adapter2.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                spinner.setAdapter(adapter2);
                builder.setPositiveButton("Kısıtla", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!spinner.getSelectedItem().toString().equalsIgnoreCase("Seç")) {
                            gecis2 = true;
                            degis = true;
                            choose();
                            Toast.makeText(getActivity(), spinner.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            item.setIcon(R.drawable.ic_baseline_clear_24);

                        }
                    }
                });
                builder.setNegativeButton("Geri", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setView(view);
                AlertDialog dialog = builder.create();
                dialog.show();

            } else {
                item.setIcon(R.drawable.ic_baseline_format_list_bulleted_24);
                chooseCustom();
                gecis2 = false;
            }

        }
        return super.onOptionsItemSelected(item);
    }

    public void defaultLocation() {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    duyuru.setVisibility(View.INVISIBLE);
                }else {
                    duyuru.setText("Duyuru Yok");
                    duyuru.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        FirebaseRecyclerOptions<KonumModel> options =
                new FirebaseRecyclerOptions.Builder<KonumModel>()
                        .setQuery(reference, KonumModel.class)
                        .build();
        adapter = new MyAdapter(options);
        adapter.startListening();
        recView.setAdapter(adapter);
    }

    public void customLocation() {
        DatabaseReference reference2 = database.getReference().child("users").child(auth.getCurrentUser().getUid()).child("konumlar");
        reference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){

                }else {
                    duyuru.setText("Duyurunuz Yok");
                    duyuru.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        FirebaseRecyclerOptions<KonumModel> options =
                new FirebaseRecyclerOptions.Builder<KonumModel>()
                        .setQuery(reference2, KonumModel.class)
                        .build();
        adapter = new MyAdapter(options);
        adapter.startListening();
        recView.setAdapter(adapter);
    }

    public void choose() {
        FirebaseRecyclerOptions<KonumModel> options =
                new FirebaseRecyclerOptions.Builder<KonumModel>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("konumlar").orderByChild("sehir").startAt(spinner.getSelectedItem().toString()).endAt(spinner.getSelectedItem().toString() + "\uf8ff"), KonumModel.class)
                        .build();
        adapter = new MyAdapter(options);
        adapter.startListening();
        recView.setAdapter(adapter);
    }

    public void chooseCustom() {
        degis = false;
        FirebaseRecyclerOptions<KonumModel> options =
                new FirebaseRecyclerOptions.Builder<KonumModel>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("konumlar"), KonumModel.class)
                        .build();
        adapter = new MyAdapter(options);
        adapter.startListening();
        recView.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }


    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}