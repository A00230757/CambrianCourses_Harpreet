package com.example.cambriancourses_A00230757;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class StudentLoginActivity extends AppCompatActivity {

    EditText edittextstudentid, edittextstudentpassword;
String studentid = "";
String studentpassword = "";

    FirebaseDatabase firebaseDatabase;
    DatabaseReference mainrefcourse;
    DatabaseReference studentsref;
    String id_student="";
    String password_student ="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_login);
        setTitle("STUDENT LOGIN");
        edittextstudentid = (EditText) (findViewById(R.id.edittextstudentid));
        edittextstudentpassword= (EditText) (findViewById(R.id.edittextstudentpassword));

        firebaseDatabase = FirebaseDatabase.getInstance(new firebase_cloud().getLink());
        mainrefcourse = firebaseDatabase.getReference();
        studentsref =mainrefcourse.child("students");
        fetchStudentDataFromFirebase();
    }
    public void login(View v ) {
        studentid = edittextstudentid.getText().toString();
        studentpassword = edittextstudentpassword.getText().toString();
        Toast.makeText(getApplicationContext(),"ok",Toast.LENGTH_SHORT);
        Log.d("MSSGG","entered");
        if(studentid.isEmpty()){
            Toast.makeText(getApplicationContext(),"enter student id",Toast.LENGTH_SHORT);
        }
        else if(studentpassword.isEmpty()){
            Toast.makeText(getApplicationContext(),"enter student password",Toast.LENGTH_SHORT);
        }
        else {
            String student_id_array[]=id_student.split(",");
            String student_password_array[]=password_student.split(",");
            boolean flag =false;
            for(int i =0; i<student_id_array.length;i++){
                if(student_id_array[i].equals(studentid)&&student_password_array[i].equals(studentpassword)){
                    flag = true;
                    break;
                }
            }

            if(flag){
                flag=false;
                Intent in =new Intent(this,StudentHomeActivity.class);
                in.putExtra("studentid",studentid);
                startActivity(in);

            }
            else{
                Toast.makeText(getApplicationContext(),"invalid credentials",Toast.LENGTH_SHORT);
            }

        }

    }

    public void fetchStudentDataFromFirebase(){
        try{
            studentsref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    //Log.d("MYESSAGE",dataSnapshot.toString());
                    for(DataSnapshot  singlesnapshot : dataSnapshot.getChildren())
                    {
//                        professor proftemp = singlesnapshot.getValue(professor.class);
                        String k =  singlesnapshot.getKey();
                        String v =  singlesnapshot.child("password").getValue().toString();
                        id_student=id_student+k+",";
                        password_student=password_student+v+",";
                        //Toast.makeText(getApplicationContext(),k,Toast.LENGTH_SHORT).show();
//                        Log.d("MSSGG",k+","+v+","+adminid+","+adminpassword);
                    }
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
}




