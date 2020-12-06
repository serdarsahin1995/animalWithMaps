package com.example.petswithmaps.Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.petswithmaps.Activities.EditActivity;
import com.example.petswithmaps.FcmUtil;
import com.example.petswithmaps.MainActivity;
import com.example.petswithmaps.PicassoTmp.CircleTransform;
import com.example.petswithmaps.PicassoTmp.RoundedCornersTransformation;
import com.example.petswithmaps.R;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

public class MapDetailFragment extends Fragment {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference, reference2;
    String baslık, resim, acıklama, uid, key, url, email, konum1, konum2;
    PhotoView imageView;
    EditText detail;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();
    ImageView profilFoto;
    TextView profilText;
    final int radius = 50, margin = 50;
    Context context;
    ImageView button;
    final Transformation transformation = new RoundedCornersTransformation(radius, margin);

    public MapDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map_detail, container, false);
        imageView = view.findViewById(R.id.imageView);
        profilFoto = view.findViewById(R.id.profilFoto);
        profilText = view.findViewById(R.id.Profiltext);
        detail = view.findViewById(R.id.textView7);
        detail.setEnabled(false);
        context = container.getContext();
        button = view.findViewById(R.id.button3);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        String gelen = bundle.getString("item");

        reference = database.getReference("konumlar").child(gelen);

        reference.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                try {
                                                    baslık = snapshot.child("text").getValue().toString();
                                                    acıklama = snapshot.child("detail").getValue().toString();
                                                    resim = snapshot.child("resim").getValue().toString();
                                                    key = snapshot.child("key").getValue().toString();
                                                    uid = snapshot.child("uid").getValue().toString();
                                                    konum1 = snapshot.child("konum1").getValue().toString();
                                                    konum2 = snapshot.child("konum2").getValue().toString();

                                                    Picasso.get().load(resim).into(imageView);

                                                    getActivity().setTitle(baslık);
                                                    detail.setText(acıklama);
                                                    if (user.getUid().equals(uid)) {
                                                        button.setVisibility(View.INVISIBLE);
                                                    }
                                                    reference2 = database.getReference("users").child(uid);
                                                    reference2.addValueEventListener(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            url = snapshot.child("photo").getValue().toString();
                                                            profilText.setText(snapshot.child("name").getValue().toString());
                                                            email = snapshot.child("email").getValue().toString();
                                                            Picasso.get().load(url).transform(new CircleTransform()).into(profilFoto);
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }
                                                    });
                                                    profilFoto.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            Fragment childFragment = new ProfileDetailFragment();
                                                            Bundle bundle = new Bundle();
                                                            bundle.putString("key", email);
                                                            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                                                            childFragment.setArguments(bundle);
                                                            transaction.replace(R.id.frameLayout, childFragment).commit();
                                                        }
                                                    });
                                                    button.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            String title = "sa";
                                                            String message = "as" + auth.getCurrentUser().getEmail();
                                                            FcmUtil fcmUtil = new FcmUtil();
                                                            fcmUtil.sendNotification(context, title, message, uid);
                                                            String uri = "http://maps.google.com/maps?q=loc:" + konum1 + "," + konum2;
                                                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                                                            intent.setPackage("com.google.android.apps.maps");
                                                            startActivity(intent);
                                                        }
                                                    });
                                                } catch (Exception exception) {
                                                    System.out.println("xa");
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }

                                        }
        );


    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        if (uid.equals(user.getUid())) {
            inflater.inflate(R.menu.map_detail, menu);
        }

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        System.out.println("lannn");
        int id = item.getItemId();
        if (uid.equals(user.getUid())) {
            if (id == R.id.sil) {
                DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("konumlar").child(key);
                DatabaseReference reference3 = FirebaseDatabase.getInstance().getReference("users").child(user.getUid()).child("konumlar").child(key);
                reference2.removeValue();
                reference3.removeValue();
                Intent i = new Intent(getActivity(), MainActivity.class);
                startActivity(i);
                getActivity().finish();
            }
            if (id == R.id.Duzenle) {
                Intent i = new Intent(getActivity(), EditActivity.class);
                String b = key;
                i.putExtra("item", b);
                startActivity(i);
                getActivity().finish();
            }
        }
        if (id == android.R.id.home) {
            getActivity().finish();
            return super.onOptionsItemSelected(item);
        }

        return super.onOptionsItemSelected(item);
    }

}