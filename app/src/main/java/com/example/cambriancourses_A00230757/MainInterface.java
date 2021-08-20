package com.example.cambriancourses_A00230757;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class MainInterface extends AppCompatActivity {

    ImageView imv1, imv2;//image to show admin or atudent icon

    int a[] = {R.drawable.admin,  R.drawable.student};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_interface);
        setTitle("MAIN INTERFACE");
        imv1 = (ImageView) (findViewById(R.id.imv1));
        imv2 = (ImageView) (findViewById(R.id.imv2));
        imv1.setImageResource(a[0]);
        imv2.setImageResource(a[1]);
    }
    public void login(View v ) {//chk whether to go to student or admin login
if( v.getId() == R.id.imv1){
    Intent in =new Intent(this,AdminLoginActivity.class);
    startActivity(in);
}
        else if( v.getId() == R.id.imv2){
    Intent in =new Intent(this,StudentLoginActivity.class);
    startActivity(in);
        }

        else{}
    }
}




