package com.example.cambriancourses_A00230757;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class AdminHomeActivity extends AppCompatActivity {

    ImageView imv1, imv2, imv3, imv4;//icons of department student professor course
    TextView textviewwelcome;//admin welcme message
    String adminid="";


    //to store pics to dset on image view
    int a[] = {R.drawable.depticon, R.drawable.courses, R.drawable.professor , R.drawable.student};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);
        setTitle("MAIN INTERFACE");
        imv1 = (ImageView) (findViewById(R.id.imv1));
        imv2 = (ImageView) (findViewById(R.id.imv2));
        imv3 = (ImageView) (findViewById(R.id.imv3));
        imv4 = (ImageView) (findViewById(R.id.imv4));

        Intent intent = getIntent();
        adminid = intent.getStringExtra("adminid");

        textviewwelcome=(TextView)(findViewById(R.id.textviewwelcome));
        textviewwelcome.setText("WELCOME ADMIN ID:"+adminid);

        imv1.setImageResource(a[0]);
        imv2.setImageResource(a[1]);
        imv3.setImageResource(a[2]);
        imv4.setImageResource(a[3]);
    }
    public void logout(View view){//admin logout
        Intent in =new Intent(this,MainInterface.class);
        startActivity(in);
    }
    public void login(View v ) {//when admin click on different pics goto different activities
        if( v.getId() == R.id.imv1){
            Intent in =new Intent(this,AddDepartmentActivity.class);
            startActivity(in);
            Toast.makeText(getApplicationContext(),"Add Department",Toast.LENGTH_SHORT).show();
        }
        else if( v.getId() == R.id.imv2){
            Toast.makeText(getApplicationContext(),"Add Course",Toast.LENGTH_SHORT).show();
            Intent in =new Intent(this,AddCoursesActivity.class);
            startActivity(in);
        }
        else if( v.getId() == R.id.imv3){
            Toast.makeText(getApplicationContext(),"Add Professor",Toast.LENGTH_SHORT).show();
            Intent in =new Intent(this,AddProfessorActivity.class);
            startActivity(in);
        }
        else if( v.getId() == R.id.imv4){
            Toast.makeText(getApplicationContext(),"Add Student",Toast.LENGTH_SHORT).show();
            Intent in =new Intent(this,AddStudentActivity.class);
            startActivity(in);
        }
        else{}
    }
}




