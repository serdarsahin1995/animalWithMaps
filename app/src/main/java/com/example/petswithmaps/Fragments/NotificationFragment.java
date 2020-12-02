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
    DatabaseReference databaseReference;
    List<String> title_list,item_list;
    ArrayList<FcmModel> itemlist= new ArrayList<>();

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
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        title_list=new ArrayList<>();
        item_list=new ArrayList<>();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        ArrayAdapter<String> adapter= new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1,title_list);
        setListAdapter(adapter);
        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(user.getUid()).child("bildirim");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot){
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
                adapter.notifyDataSetChanged();
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
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_layout, new MapFragment(sublistviewList.getKonum1(), sublistviewList.getKonum2())).commit();
    }
}