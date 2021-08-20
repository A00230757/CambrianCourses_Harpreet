package com.example.cambriancourses_A00230757;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class StudentViewSelectedCoursesActivity extends AppCompatActivity {


    String studentid = "";//to store student id
    ArrayList<selectedcourse> arraylist_courses = new ArrayList<selectedcourse>();//array list  to store selected courses
    myadapter mycustomadapter_courses;//custom adapter for courses list view

    ListView listview_courses;//listview to show courses

    FirebaseDatabase firebaseDatabase;//firebase  database instance
    DatabaseReference mainrefcourse;// firebase database main reference
    DatabaseReference courseref;//reference to child available course

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_view_selected_courses);
        Intent intent = getIntent();
        studentid=intent.getStringExtra("studentid");

        // memory to different views
        listview_courses = (ListView) (findViewById(R.id.listview_selectedcourses));

        //objects of firebase reference classes defined at top of oncreate  are made here
        firebaseDatabase = FirebaseDatabase.getInstance(new firebase_cloud().getLink());
        mainrefcourse = firebaseDatabase.getReference();
        courseref = mainrefcourse.child("selectedcourses");
        mycustomadapter_courses = new myadapter();
        listview_courses.setAdapter(mycustomadapter_courses);
        fetchCoursesFromFirebase();


        //click listener to list view
        listview_courses.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), arraylist_courses.get(position).coursecode + " " + arraylist_courses.get(position).studentid, Toast.LENGTH_SHORT).show();
            }
        });
    }


    //fetch selected courses from firebase
    public void fetchCoursesFromFirebase() {
        arraylist_courses.clear();
        courseref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                arraylist_courses.clear();
                //Log.d("MYESSAGE",dataSnapshot.toString());
                for (DataSnapshot singlesnapshot : dataSnapshot.getChildren()) {
                    selectedcourse coursetemp = singlesnapshot.getValue(selectedcourse.class);
                    try {
                        if (coursetemp.studentid.equals(studentid)) {
                            arraylist_courses.add(coursetemp);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                }
                mycustomadapter_courses.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //this is custom adapter class to show array list data of selected courses in list view
    class myadapter extends BaseAdapter {
        @Override
        public int getCount() {
            return arraylist_courses.size();
        }

        @Override
        public Object getItem(int position) {
            return arraylist_courses.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position * 10;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                LayoutInflater l = LayoutInflater.from(getApplicationContext());
                convertView = l.inflate(R.layout.selected_courses_single_layout, parent, false);
            }
            TextView texview_course_name = (TextView) (convertView.findViewById(R.id.texview_course_name));
            Button btdelete = (Button) (convertView.findViewById(R.id.btdeletecourse));

            selectedcourse d = arraylist_courses.get(position);
            texview_course_name.setText("CourseName: " + d.coursecode);


            new Thread(new Runnable() {
                @Override
                public void run() {
                    btdelete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent myIntent = new Intent(StudentViewSelectedCoursesActivity.this, SelectedCourseDetailActivity.class);
                            myIntent.putExtra("coursecode", d.coursecode); //Optional parameters
                            myIntent.putExtra("studentid", d.studentid); //Optional parameters
                            startActivity(myIntent);
                        }
                    });
                }
            }).start();
            return convertView;
        }

    }



}
