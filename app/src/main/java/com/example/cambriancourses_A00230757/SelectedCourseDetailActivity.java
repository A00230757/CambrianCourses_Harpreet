package com.example.cambriancourses_A00230757;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class SelectedCourseDetailActivity extends AppCompatActivity {

    TextView textview_course_code ,textview_course_name,textview_course_description,textview_course_professor,textview_course_underdept;
    ImageView courseimage;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference mainrefcourse;
    DatabaseReference courseref;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_course_detail);


        textview_course_code = (TextView)(findViewById(R.id.textview_course_code));
        textview_course_name = (TextView)(findViewById(R.id.textview_course_name));
        textview_course_description = (TextView)(findViewById(R.id.textview_course_description));
        textview_course_professor = (TextView)(findViewById(R.id.textview_course_professor));
        textview_course_underdept = (TextView)(findViewById(R.id.textview_course_underdept));
        courseimage = (ImageView)(findViewById(R.id.courseimage));

        Intent intent = getIntent();
        String coursecode = intent.getStringExtra("coursecode");
        String studentid = intent.getStringExtra("studentid");

        firebaseDatabase = FirebaseDatabase.getInstance(new firebase_cloud().getLink());
        mainrefcourse = firebaseDatabase.getReference();
        courseref = mainrefcourse.child("courses").child(coursecode);



        fetchCoursesFromFirebase();

    }

    public void fetchCoursesFromFirebase() {


        courseref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                course obj = dataSnapshot.getValue(course.class);
                textview_course_code.setText("course code: "+obj.coursecode);
                textview_course_name.setText("course name:"+obj.name);
                textview_course_description.setText("course description:"+obj.description);
                textview_course_professor.setText("professor:"+obj.professor);
                textview_course_underdept.setText("under Dept:"+obj.under_dept);
                String imagepath=obj.path;
                setImage(imagepath);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void setImage(String path){
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference course_photo_reference = storageRef.child("courses"+path);
        course_photo_reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
        {
            @Override
            public void onSuccess(Uri downloadUrl)
            {
                //do something with downloadurl
                Picasso.with(SelectedCourseDetailActivity.this).load(downloadUrl).into(courseimage);
            }
        });
    }
}