package com.example.cambriancourses_A00230757;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class StudentSelectNewCoursesActivity extends AppCompatActivity {

    ArrayList<course> arraylist_courses = new ArrayList<course>();
    //arraylist of type course,
    // which stores course information fetched from realtime firebase databse
    ArrayList<studentselectcourseclass> arraylist_selectedcourses = new ArrayList<studentselectcourseclass>();
    myadapter mycustomadapter_courses;//customized adapter for courses to store image and other text data of available courses in listview

    ListView listview_courses;//list view to reference t show available courses

    FirebaseDatabase firebaseDatabase;//firebase  database instance
    DatabaseReference mainrefcourse;// firebase database main reference
    DatabaseReference selectedmainrefcourse;
    DatabaseReference courseref;//reference to child available course
    DatabaseReference selectedcourseref;//reference to child availble courses
    FirebaseStorage firebaseStorage;//firebase storage instance
    StorageReference mainrefstorage;//firebase storage reference to child available course photos



    String course_photopath="/storage/emulated/0/Pictures/Title (30).jpg/d1";
    String selected_department="";
    String studentid="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_select_new_courses);
        setTitle("Student Select Courses");
        Intent intent = getIntent();
        studentid=intent.getStringExtra("studentid");
        selected_department=intent.getStringExtra("under_dept");
        Log.d("MSSGG",selected_department+"  ,oncreate");

        // memory to different views
        listview_courses = (ListView) (findViewById(R.id.listview_courses));

        //objects of firebase reference classes defined at top of oncreate  are made here
        firebaseDatabase = FirebaseDatabase.getInstance(new firebase_cloud().getLink());
        mainrefcourse = firebaseDatabase.getReference();
        courseref =mainrefcourse.child("courses");
        selectedmainrefcourse = firebaseDatabase.getReference();
        selectedcourseref =selectedmainrefcourse.child("selectedcourses");



        firebaseStorage = FirebaseStorage.getInstance();
        mainrefstorage = firebaseStorage.getReference();

        mycustomadapter_courses = new myadapter();//memory to custom adapter
        listview_courses.setAdapter(mycustomadapter_courses);//adapter set to list view

        fetchCoursesFromFirebase("");
        alreadySelectedOrNot();

        //click listener on list view
        listview_courses.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), arraylist_courses.get(position).name+" "+arraylist_courses.get(position).description, Toast.LENGTH_SHORT).show();
            }
        });
    }



    //to fetvch available courses from firebase
    public void fetchCoursesFromFirebase(String department_selected){
        arraylist_courses.clear();
        courseref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                arraylist_courses.clear();
                //Log.d("MYESSAGE",dataSnapshot.toString());
                for(DataSnapshot  singlesnapshot : dataSnapshot.getChildren())
                {
                    course coursetemp = singlesnapshot.getValue(course.class);
                    try {
                        Log.d("MSSGG",coursetemp.under_dept+","+selected_department+"  ,tttt");
                       if(coursetemp.under_dept.equals(selected_department)){
                            arraylist_courses.add(coursetemp);
                        }
                    }
                    catch (Exception ex){
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

    //this is custom adapter class to show array list data of availaable courses in list view
    class myadapter extends BaseAdapter
    {
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
            return position*10;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if(convertView==null) {
                LayoutInflater l = LayoutInflater.from(getApplicationContext());
                convertView = l.inflate(R.layout.selectcourse_single_layout, parent, false);
            }
            TextView texview_course_name = (TextView) (convertView.findViewById(R.id.texview_course_name));
            TextView texview_course_description = (TextView) (convertView.findViewById(R.id.texview_course_description));
            TextView texview_course_photo = (TextView) (convertView.findViewById(R.id.texview_course_photo));
            CheckBox checkboxselectcourse =(CheckBox) (convertView.findViewById(R.id.checkboxselectcourse));
            ImageView imv1course =(ImageView) (convertView.findViewById(R.id.imv1course));

            course c = arraylist_courses.get(position);
            texview_course_name.setText("Name: "+c.name);
            texview_course_description.setText("Description: "+c.description);
            texview_course_photo.setText("path: "+c.path);
            boolean f =test(c.coursecode);
            Toast.makeText(getApplicationContext(),arraylist_selectedcourses.size()+"",Toast.LENGTH_SHORT).show();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.d("MSSGGSS",f+"hello");
                }
            }).start();
if(f){
    Toast.makeText(getApplicationContext(),f+"",Toast.LENGTH_SHORT).show();
    checkboxselectcourse.setChecked(true);
}


            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            StorageReference course_photo_reference = storageRef.child("courses"+c.path);
            course_photo_reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
            {
                @Override
                public void onSuccess(Uri downloadUrl)
                {
                    //do something with downloadurl
                    Picasso.with(StudentSelectNewCoursesActivity.this).load(downloadUrl).resize(200,200).into(imv1course);
                }
            });

            new Thread(new Runnable() {
                @Override
                public void run() {
                    checkboxselectcourse.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {// checkbox listener to on / off background music
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if(isChecked)
                            {
                                studentselectcourseclass obj = new studentselectcourseclass(c.coursecode,studentid);
                                DatabaseReference selected_course_reference = selectedcourseref.child(studentid+""+c.coursecode);
                                Log.d("MYMESSAGE",selected_course_reference.getKey());
                                selected_course_reference.setValue(obj);
                                Toast.makeText(getApplicationContext(),"Selected",Toast.LENGTH_SHORT).show();
                                alreadySelectedOrNot();

                            }
                            else
                            {
                                studentselectcourseclass obj = new studentselectcourseclass(c.coursecode,studentid);
                                DatabaseReference selected_course_reference = selectedcourseref.child(studentid+""+c.coursecode);
                                selected_course_reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for (DataSnapshot singleSnapshot: dataSnapshot.getChildren()) {
                                            singleSnapshot.getRef().removeValue();
                                        }
                                    }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                    }
                                });
                                Toast.makeText(getApplicationContext(),"UnSelected",Toast.LENGTH_SHORT).show();
                                alreadySelectedOrNot();
                            }
                        }
                    });
//                    btdelete.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            courseref.child(d.name).addListenerForSingleValueEvent(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(DataSnapshot dataSnapshot) {
//                                    for (DataSnapshot singleSnapshot: dataSnapshot.getChildren()) {
//                                        singleSnapshot.getRef().removeValue();
//                                       // deletefile(d.path);
//                                       // fetchCoursesFromFirebase(selected_department,selected_professor);
//                                    }
//                                }
//                                @Override
//                                public void onCancelled(DatabaseError databaseError) {
//                                }
//                            });
//                        }
//                    });

                }
            }).start();
            return convertView;
        }

    }


    //to chk whether course is already selected or not, to show check box already checked
    public void  alreadySelectedOrNot(){
        arraylist_selectedcourses.clear();
        selectedcourseref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Log.d("MYESSAGE",dataSnapshot.toString());
                for(DataSnapshot  singlesnapshot : dataSnapshot.getChildren())
                {
                    studentselectcourseclass obj = singlesnapshot.getValue(studentselectcourseclass.class);
                    try {
                        Log.d("MSSGGSS","fetchtest"+obj.coursecode+","+obj.studentid);
                       arraylist_selectedcourses.add(obj);
                    }
                    catch (Exception ex){
                        ex.printStackTrace();
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

//to chk whetehr course already selected or not
    public boolean test(String cc){
        boolean v= false;
        for (int i =0 ; i<arraylist_selectedcourses.size();i++){
            Log.d("MSSGGSS","hitop"+cc+studentid+"--"+arraylist_selectedcourses.get(i).studentid+"++"+arraylist_selectedcourses.get(i).coursecode);
            if (arraylist_selectedcourses.get(i).studentid.equals(studentid) & arraylist_selectedcourses.get(i).coursecode.equals(cc)){
                Log.d("MSSGGSS","hiinside"+cc+studentid+"--"+arraylist_selectedcourses.get(i).studentid+"++"+arraylist_selectedcourses.get(i).coursecode);
                v = true;
                break;
            }
        }
        return v;
    }



}
