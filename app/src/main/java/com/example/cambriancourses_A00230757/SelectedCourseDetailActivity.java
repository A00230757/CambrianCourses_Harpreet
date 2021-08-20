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

    //different views reference to store detail and image
    TextView textview_course_code ,textview_course_name,textview_course_description,textview_course_professor,textview_course_underdept;
    ImageView courseimage;

    FirebaseDatabase firebaseDatabase;//firebase  database instance
    DatabaseReference mainrefcourse;// firebase database main reference
    DatabaseReference courseref;//reference to child selected course



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_course_detail);

//memory to views
        textview_course_code = (TextView)(findViewById(R.id.textview_course_code));
        textview_course_name = (TextView)(findViewById(R.id.textview_course_name));
        textview_course_description = (TextView)(findViewById(R.id.textview_course_description));
        textview_course_professor = (TextView)(findViewById(R.id.textview_course_professor));
        textview_course_underdept = (TextView)(findViewById(R.id.textview_course_underdept));
        courseimage = (ImageView)(findViewById(R.id.courseimage));

        Intent intent = getIntent();
        String coursecode = intent.getStringExtra("coursecode");
        String studentid = intent.getStringExtra("studentid");

        //objects of firebase reference classes defined at top of oncreate  are made here
        firebaseDatabase = FirebaseDatabase.getInstance(new firebase_cloud().getLink());
        mainrefcourse = firebaseDatabase.getReference();
        courseref = mainrefcourse.child("courses").child(coursecode);


//fetch course detail
        fetchCoursesFromFirebase();

    }

    public void fetchCoursesFromFirebase() {


        courseref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                course obj = dataSnapshot.getValue(course.class);

                //detail set to views
                textview_course_code.setText("course code: "+obj.coursecode);
                textview_course_name.setText("course name:"+obj.name);
                textview_course_description.setText("course description:"+obj.description);
                textview_course_professor.setText("professor:"+obj.professor);
                textview_course_underdept.setText("under Dept:"+obj.under_dept);
                String imagepath=obj.path;
                setImage(imagepath);//set image on image view
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    //set image on image view from firebase
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