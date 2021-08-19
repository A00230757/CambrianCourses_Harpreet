package com.example.cambriancourses_A00230757;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class StudentHomeActivity extends AppCompatActivity {

    Button  editprofilebutton , selectnewcoursesbutton ,viewselectedcoursesbutton;

    TextView textviewwelcome;

    int a[] = {R.drawable.admin,  R.drawable.student};
    String studentid="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_home);
        setTitle("STUDENT HOME");
        Intent intent = getIntent();
        studentid=intent.getStringExtra("studentid");
        editprofilebutton = (Button) (findViewById(R.id.editprofilebutton));

        textviewwelcome =(TextView)(findViewById(R.id.textviewwelcome));
        textviewwelcome.setText("WELCOME STUDENT ID:"+studentid);
        selectnewcoursesbutton = (Button) (findViewById(R.id.selectnewcoursesbutton));
        viewselectedcoursesbutton = (Button) (findViewById(R.id.viewselectedcoursesbutton));
    }
    public void logout(View view){
        Intent in =new Intent(this,MainInterface.class);
        startActivity(in);
    }

    public void goToNextActivity(View v ) {
        if( v.getId() == R.id.editprofilebutton){
            Intent in =new Intent(this,StudentEditProfileActivity.class);
            in.putExtra("studentid",studentid);
            startActivity(in);
        }
        else if( v.getId() == R.id.selectnewcoursesbutton){
            Intent in =new Intent(this,StudentSelectNewCoursesActivity.class);
            in.putExtra("studentid",studentid);
            startActivity(in);
        }
        else if( v.getId() == R.id.viewselectedcoursesbutton){
            Intent in =new Intent(this,StudentViewSelectedCoursesActivity.class);
            in.putExtra("studentid",studentid);
            startActivity(in);
        }
        else{}
    }
}




