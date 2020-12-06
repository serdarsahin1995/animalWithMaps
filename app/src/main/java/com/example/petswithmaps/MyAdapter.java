package com.example.petswithmaps;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petswithmaps.Activities.MapTempActivity;
import com.example.petswithmaps.Fragments.ListFragment;
import com.example.petswithmaps.Fragments.MapFragment;
import com.example.petswithmaps.Models.KonumModel;
import com.example.petswithmaps.PicassoTmp.RoundedCornersTransformation;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

public class MyAdapter extends FirebaseRecyclerAdapter<KonumModel, MyAdapter.myviewholder> {

    public MyAdapter(@NonNull FirebaseRecyclerOptions<KonumModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull myviewholder holder, int i, @NonNull KonumModel konumModel) {
        final int radius = 6,margin = 6;
        holder.text.setText(konumModel.getText());
        holder.detail.setText(konumModel.getDetail());
        holder.konum1.setText(konumModel.getAdres1());
        holder.konum2.setText(konumModel.getAdres2());
        final Transformation transformation = new RoundedCornersTransformation(radius, margin);
        Picasso.get().load(konumModel.getResim()).transform(transformation).placeholder(R.drawable.progress_animation).into(holder.img1);
        ListFragment.progressBar.setVisibility(View.INVISIBLE);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                Intent i = new Intent(activity, MapTempActivity.class);
                String b = konumModel.getKey();
                i.putExtra("item", b);
                activity.startActivity(i);
            }
        });
        holder.gomap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                BottomNavigationView mBottomNavigationView = activity.findViewById(R.id.bottomNavigationview);
                mBottomNavigationView.setSelectedItemId(R.id.secondFragment);
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout, new MapFragment(konumModel.getKonum1(), konumModel.getKonum2())).commit();

            }
        });

    }

    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.design, parent, false);

        return new myviewholder(view);
    }


    public class myviewholder extends RecyclerView.ViewHolder {
        ImageView img1, gomap;
        TextView text, detail, konum1, konum2;
        CardView cardView;

        public myviewholder(@NonNull View itemView) {
            super(itemView);
            img1 = itemView.findViewById(R.id.img1);
            text = itemView.findViewById(R.id.textV);
            detail = itemView.findViewById(R.id.detailV);
            konum1 = itemView.findViewById(R.id.konum1V);
            konum2 = itemView.findViewById(R.id.konum2V);
            cardView = itemView.findViewById(R.id.gonder);
            gomap = itemView.findViewById(R.id.gomap);

        }
    }
}