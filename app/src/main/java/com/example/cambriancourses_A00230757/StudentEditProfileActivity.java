package com.example.cambriancourses_A00230757;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

import java.io.File;

public class StudentEditProfileActivity extends AppCompatActivity {


    //variables declartion to store student ifi
    String studentid="";
    String path="";
    String department ="";

    String password="";

    String newpath="";


    //reference to different views to stor student detail
    TextView textviewstudentid,textviewunderdepartment;
    EditText edittextemail,edittextmobile,edittextname,edittextpassword;
    ImageView imageviewstudentimage;

    FirebaseDatabase firebaseDatabase;//firebase  database instance
    DatabaseReference mainrefstudent;// firebase database main reference
    DatabaseReference studentref;//reference to child students
    FirebaseStorage firebaseStorage;//firebase storage instance
    StorageReference mainrefstorage;//firebase storage reference to child student photos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_edit_profile);
        setTitle("STUDENT EDIT PROFILE");
        Intent intent = getIntent();
        studentid=intent.getStringExtra("studentid");

        //memory to views
        textviewstudentid = (TextView)(findViewById(R.id.textviewstudentid));
        textviewunderdepartment = (TextView)(findViewById(R.id.textviewunderdepartment));
        edittextemail = (EditText) (findViewById(R.id.edittextemail));
        edittextmobile = (EditText) (findViewById(R.id.edittextmobile));
        edittextname = (EditText) (findViewById(R.id.edittextname));
        edittextpassword = (EditText) (findViewById(R.id.edittextpassword));
        imageviewstudentimage =(ImageView)(findViewById(R.id.imageviewstudentimage));

        //objects of firebase reference classes defined at top of oncreate  are made here
        firebaseDatabase = FirebaseDatabase.getInstance(new firebase_cloud().getLink());
        mainrefstudent = firebaseDatabase.getReference();
        studentref =mainrefstudent.child("students");

        firebaseStorage = FirebaseStorage.getInstance();
        mainrefstorage = firebaseStorage.getReference();
        fetchStudentData();//fetch student data
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


                    //set data to views
                    textviewstudentid.setText("ID : "+stTemp.studentid);
                    textviewunderdepartment.setText("Dept : "+ stTemp.under_dept);
                   edittextemail.setText(stTemp.email);
                   edittextmobile.setText(stTemp.mobile);
                   edittextname.setText(stTemp.name);
                   edittextpassword.setText(stTemp.password+"");
                   password=stTemp.password;
                   path=stTemp.path;
                   department=stTemp.under_dept;
                    Log.d("MSSGGPHOTO",stTemp.path+"");

                    setImage("students"+stTemp.path);


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

    //function to update student info
    public void updateStudentInfo(View v)
    {
        try{
            String student_name=edittextname.getText().toString();
            String student_mobile = edittextmobile.getText().toString();
            String student_email = edittextemail.getText().toString();
            String student_password = edittextpassword.getText().toString();
            if(student_email.isEmpty()){
                Toast.makeText(this, "Enter Student Email", Toast.LENGTH_SHORT).show();
            }
            else if(student_mobile.isEmpty()){
                Toast.makeText(this, "Enter Student Mobile", Toast.LENGTH_SHORT).show();
            }
            else if(student_name.isEmpty()){
                Toast.makeText(this, "Enter Student Name", Toast.LENGTH_SHORT).show();
            }
            else if(student_password.isEmpty()){
                Toast.makeText(this, "Enter Student Password", Toast.LENGTH_SHORT).show();
            }
            else if(student_password.length()!=6){
                Toast.makeText(this, "Student Password Must Be 6 Digits Long", Toast.LENGTH_SHORT).show();
            }
            else{
                student stnew = new student(studentid,student_name,student_email,path,student_mobile,department,student_password+"");
                DatabaseReference st4 = studentref.child(studentid);
                st4.setValue(stnew);
                Toast.makeText(this, "Student Records Updated"+student_password, Toast.LENGTH_SHORT).show();
            }

        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }


    //to open gallery to choose image for student
    public void gallery(View view)
    {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent,91);
    }

    //the image choosed from gallery is available through this function i.e., on activity result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==90 && resultCode==RESULT_OK)
        {
            Bitmap bmp  = (Bitmap) data.getExtras().get("data");
        }
        else if(resultCode==RESULT_OK)
        {
            Uri uri = data.getData();
            Uri selectedImageUri = data.getData();
            String selectedImagePath = getPath(getApplicationContext(),selectedImageUri);
            System.out.println("Image Path : " + selectedImagePath);
            newpath=selectedImagePath;
            deletefile();
            uploadlogic(newpath);
        }
    }

    //upload student photo to firebase storage
    public void uploadlogic(String newpath)
    {
        File localfile=new File(newpath);
        path=newpath+"/"+studentid;
        final long uploadfilesize = localfile.length();
        StorageReference filerefoncloud = mainrefstorage.child("/students/"+newpath+"/"+studentid);
        Log.d("MSSGG",path);
        UploadTask myuploadtask = filerefoncloud.putFile(Uri.fromFile(localfile));
        myuploadtask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(StudentEditProfileActivity.this, "Photo Updated !!!!", Toast.LENGTH_SHORT).show();
                DatabaseReference st4 = studentref.child(studentid).child("path");
                st4.setValue(path);
                setImage("/students/"+newpath+"/"+studentid);
            }
        });
        myuploadtask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(StudentEditProfileActivity.this, "Photo upload Failed!!!", Toast.LENGTH_SHORT).show();
            }
        });
        myuploadtask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                // int per = (int)((taskSnapshot.getBytesTransferred()*100)/uploadfilesize);
                //pbar2.setProgress(per);
            }
        });
    }

    //delete previous photo from firebase storage
    public void deletefile()
    {
        StorageReference file11 = mainrefstorage.child("students"+path);
        Log.d("MSSGG","log students"+path+"/"+studentid);
        file11.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //Toast.makeText(EditDepartmentDialogActivity.this, "Department Photo deleted from Storage", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //this function give us absolute path of image selected from gallery
    public static String getPath(Context context, Uri uri ) {
        String result = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver( ).query( uri, proj, null, null, null );
        if(cursor != null){
            if ( cursor.moveToFirst( ) ) {
                int column_index = cursor.getColumnIndexOrThrow( proj[0] );
                result = cursor.getString( column_index );
            }
            cursor.close( );
        }
        if(result == null) {
            result = "Not found";
        }
        return result;
    }


    //this function set image of student on image view
    public void setImage(String path){
        StorageReference student_photo_reference = mainrefstorage.child(path);
        student_photo_reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
        {
            @Override
            public void onSuccess(Uri downloadUrl)
            {
                //do something with downloadurl
                Picasso.with(StudentEditProfileActivity.this).load(downloadUrl).resize(200,200).into(imageviewstudentimage);
            }
        });
    }

}