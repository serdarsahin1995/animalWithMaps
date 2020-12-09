package com.example.petswithmaps.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.fragment.app.ListFragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.petswithmaps.MainActivity;
import com.example.petswithmaps.Models.FcmModel;
import com.example.petswithmaps.Models.RegisterModel;
import com.example.petswithmaps.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class NotificationFragment extends ListFragment implements AdapterView.OnItemClickListener {
    FcmModel fcmModel;
    DatabaseReference databaseReference,databaseReference2;
    List<String> title_list,item_list;
    ArrayList<FcmModel> itemlist= new ArrayList<>();
    ProgressBar progressBar;
    TextView textView;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();
    public NotificationFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_fourth, container, false);
        getActivity().setTitle("Bildirim");
        progressBar =view.findViewById(R.id.progressBar6);
        textView = view.findViewById(R.id.textView9);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        title_list=new ArrayList<>();

        item_list=new ArrayList<>();

        ArrayAdapter<String> adapter= new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1,title_list);
        setListAdapter(adapter);
        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(user.getUid()).child("bildirim");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot){
                if(snapshot.exists()){
                    System.out.println("onDataChange");
                    title_list.clear();
                    itemlist.clear();
                    System.out.println("item_list= "+itemlist);
                    for(DataSnapshot d:snapshot.getChildren()){
                        fcmModel = d.getValue(FcmModel.class);
                        title_list.add(fcmModel.getTitle());
                        itemlist.add(fcmModel);

                    }
                    System.out.println("title_list= "+title_list);
                    System.out.println("item_list values added= "+itemlist);
                    progressBar.setVisibility(View.INVISIBLE);
                    adapter.notifyDataSetChanged();
                }else {
                    progressBar.setVisibility(View.INVISIBLE);
                    textView.setText("Bildirim Yok");
                    textView.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        getListView().setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        FcmModel sublistviewList=null;
        System.out.println("lan");
        if(itemlist.get(position).getTitle().equals(title_list.get(position))){
            System.out.println(position);
            sublistviewList=itemlist.get(position);
        }
        MainActivity.bildirimCount--;
        databaseReference2 = FirebaseDatabase.getInstance().getReference("users").child(user.getUid()).child("bildirim").child(fcmModel.getKey());
        databaseReference2.child("okundu").setValue(true);
        BottomNavigationView mBottomNavigationView = getActivity().findViewById(R.id.bottomNavigationview);
        mBottomNavigationView.setSelectedItemId(R.id.secondFragment);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_layout, new MapFragment(sublistviewList.getKonum1(), sublistviewList.getKonum2())).commit();
    }
}