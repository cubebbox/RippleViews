package com.cubebox.rippleviews;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.ArrayList;


public class RecyclerActivity extends AppCompatActivity {
    private ArrayList<String> sourcesArrayList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_recycler);


        sourcesArrayList.add("Samsung");
        sourcesArrayList.add("Android");
        sourcesArrayList.add("Google");
        sourcesArrayList.add("Asus");
        sourcesArrayList.add("Apple");
        sourcesArrayList.add("Samsung");
        sourcesArrayList.add("Android");
        sourcesArrayList.add("Google");
        sourcesArrayList.add("Asus");
        sourcesArrayList.add("Apple");
        sourcesArrayList.add("Samsung");
        sourcesArrayList.add("Android");
        sourcesArrayList.add("Google");
        sourcesArrayList.add("Asus");
        sourcesArrayList.add("Apple");


        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        CustomAdapter customAdapter = new CustomAdapter();
        customAdapter.updateList(sourcesArrayList);

        customAdapter.setOnTapListener(new OnTapListener() {
            @Override
            public void onTapView(int position) {
                Log.e("MainActivity", " item : " + position);
            }
        });
        recyclerView.setAdapter(customAdapter);
    }


}
