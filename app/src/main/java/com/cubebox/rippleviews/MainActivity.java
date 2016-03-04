package com.cubebox.rippleviews;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.cubebox.ripples.RippleLinearLayout;
import com.cubebox.ripples.listener.OnClickConfirmListener;
import com.cubebox.ripples.listener.OnRippleCompleteListener;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final RippleLinearLayout rippleView = (RippleLinearLayout) findViewById(R.id.rect);
        final TextView textView1 = (TextView) findViewById(R.id.rect_child1);
        final TextView textView2 = (TextView) findViewById(R.id.rect_child2);

        rippleView.setOnClickConfirmListener(new OnClickConfirmListener() {
            @Override
            public void onConfirmClick(View v) {
                startActivity(new Intent(MainActivity.this, RecyclerActivity.class));
            }
        });

        rippleView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(MainActivity.this, "onLongClick ", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
//        rippleView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(MainActivity.this, RecyclerActivity.class));
//            }
//        });
        rippleView.setOnRippleCompleteListener(new OnRippleCompleteListener() {
            @Override
            public void onComplete(View v) {
                Toast.makeText(MainActivity.this, "click ", Toast.LENGTH_SHORT).show();
            }
        });
//        textView1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.e("Sample", "Click rect child1 !");
//            }
//        });
//        textView2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.e("Sample", "Click rect child 2 !");
//            }
//        });


    }


}
