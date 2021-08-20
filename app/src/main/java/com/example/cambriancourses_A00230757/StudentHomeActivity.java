package com.example.cambriancourses_A00230757;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class StudentHomeActivity extends AppCompatActivity {

    //different buttons to perform different activities
    Button  editprofilebutton , selectnewcoursesbutton ,viewselectedcoursesbutton;

    TextView textviewwelcome;//student welcome message

    int a[] = {R.drawable.admin,  R.drawable.student};
    String studentid="";
    String under_dept="";
    FirebaseDatabase firebaseDatabase;//firebase  database instance
    DatabaseReference mainrefstudent;// firebase database main reference
    DatabaseReference studentref;//reference to child students
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_home);
        setTitle("STUDENT HOME");
        Intent intent = getIntent();
        studentid=intent.getStringExtra("studentid");
        editprofilebutton = (Button) (findViewById(R.id.editprofilebutton));

        //objects of firebase reference classes defined at top of oncreate  are made here
        firebaseDatabase = FirebaseDatabase.getInstance(new firebase_cloud().getLink());
        mainrefstudent = firebaseDatabase.getReference();
        studentref =mainrefstudent.child("students");

        textviewwelcome =(TextView)(findViewById(R.id.textviewwelcome));
        textviewwelcome.setText("WELCOME STUDENT ID:"+studentid);
        selectnewcoursesbutton = (Button) (findViewById(R.id.selectnewcoursesbutton));
        viewselectedcoursesbutton = (Button) (findViewById(R.id.viewselectedcoursesbutton));
        fetchStudentData();
    }
    //fetch student data from firebase
    public void fetchStudentData()
    {
        try{
            DatabaseReference st1 = studentref.child(studentid);
            st1.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Extract value from dataSnapShot and Convert it to Java Object
                    student stTemp = dataSnapshot.getValue(student.class);

                    under_dept=stTemp.under_dept;
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }
    public void logout(View view){
        Intent in =new Intent(this,MainInterface.class);
        startActivity(in);
    }

    public void goToNextActivity(View v ) {///go to different task activities upon button click
        if( v.getId() == R.id.editprofilebutton){
            Intent in =new Intent(this,StudentEditProfileActivity.class);
            in.putExtra("studentid",studentid);
            startActivity(in);
        }
        else if( v.getId() == R.id.selectnewcoursesbutton){
            Log.d("MSSGG",under_dept+"  ,home");
            Toast.makeText(getApplicationContext(),under_dept,Toast.LENGTH_SHORT).show();
            Intent in =new Intent(this,StudentSelectNewCoursesActivity.class);
            in.putExtra("studentid",studentid);
            in.putExtra("under_dept",under_dept);
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




