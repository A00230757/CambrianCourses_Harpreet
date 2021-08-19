package com.example.cambriancourses_A00230757;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class StudentLoginActivity extends AppCompatActivity {

    EditText edittextstudentid, edittextstudentpassword;
String studentid = "";
String studentpassword = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_login);
        setTitle("STUDENT LOGIN");
        edittextstudentid = (EditText) (findViewById(R.id.edittextstudentid));
        edittextstudentpassword= (EditText) (findViewById(R.id.edittextstudentpassword));
    }
    public void login(View v ) {
        studentid = edittextstudentid.getText().toString();
        studentpassword = edittextstudentpassword.getText().toString();
        if(studentid.isEmpty()){
            Toast.makeText(getApplicationContext(),"enter student id",Toast.LENGTH_SHORT);
        }
        else if(studentpassword.isEmpty()){
            Toast.makeText(getApplicationContext(),"enter student password",Toast.LENGTH_SHORT);
        }
        else{
            Intent in =new Intent(this,StudentHomeActivity.class);
            startActivity(in);
        }

    }
}




