package com.example.petswithmaps.Fragments;

import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.petswithmaps.Fragments.MapDetailFragment;
import com.example.petswithmaps.R;

public class ProfileDetailFragment extends Fragment {
    String email;
    TextView textView;
    ImageView imageView;
    ConstraintLayout constraintLayout;
    public ProfileDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            email = bundle.getString("key");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_profile_detail, container, false);
        textView=view.findViewById(R.id.textView6);
        textView.setText(email);
        imageView = view.findViewById(R.id.exit);
        constraintLayout = view.findViewById(R.id.cons);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                constraintLayout.setVisibility(View.INVISIBLE);
            }
        });
        return view;
    }
}