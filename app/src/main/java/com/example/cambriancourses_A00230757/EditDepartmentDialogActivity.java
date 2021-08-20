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
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;

public class EditDepartmentDialogActivity extends AppCompatActivity {
    ImageView departmentimage;//image view reference to show department image
    EditText edittextdepartmentname,edittextdepartmentdescription;//edittext reference
    // for department name , description

    String name="",description="",path="";//variables to store department name, description and path

    String newpath="";//in case image update new path is stored in this variable

    //firebase connections reference
    FirebaseDatabase firebaseDatabase;
    DatabaseReference mainrefdepartment;
    DatabaseReference departmentref;
    FirebaseStorage firebaseStorage;
    StorageReference mainrefstorage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_department_dialog);
//memory to varios views
        departmentimage = (ImageView)(findViewById(R.id.departmentimage));
        edittextdepartmentname =(EditText)(findViewById(R.id.edittextdepartmentname));
        edittextdepartmentdescription =(EditText)(findViewById(R.id.edittextdepartmentdescription));

        //objects of firebase reference classes defined at top of oncreate  are made here
        firebaseDatabase = FirebaseDatabase.getInstance(new firebase_cloud().getLink());
        mainrefdepartment = firebaseDatabase.getReference();
        departmentref =mainrefdepartment.child("departments");

        firebaseStorage = FirebaseStorage.getInstance();
        mainrefstorage = firebaseStorage.getReference();

        //get intent to get data passed from activity on which dialog is open
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        description = intent.getStringExtra("description");
        path = intent.getStringExtra("path");
        //Toast.makeText(getApplicationContext(),name,Toast.LENGTH_SHORT).show();

        //initialize all the details of department on edit texts
        edittextdepartmentname.setText(name);
        edittextdepartmentname.setEnabled(false);
        edittextdepartmentdescription.setText(description);

    setImage();//this functiona set image on image view from firebase storage
    }

    public  void disableDialog(View view){
       finish();
    }//function to close dialog activity

    public void gallery(View view)//function to open gallery
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
        else if(resultCode==RESULT_OK)//this runs for gallery result
        {
            Uri uri = data.getData();//this runs for gallery result
            Uri selectedImageUri = data.getData();
            String selectedImagePath = getPath(getApplicationContext(),selectedImageUri);//get selected image path from uri, absolute path in real device
            System.out.println("Image Path : " + selectedImagePath);
            newpath=selectedImagePath;//set this path to global new path  variable
           deletefile();//this function delete previous photo from database
           uploadlogic(newpath);//this function upload image on realtime firebase database
        }
    }
    public void uploadlogic(String newpath)//this function upload department image to firebase storage
    {
        File localfile=new File(newpath);
        path=newpath+"/"+name;
        final long uploadfilesize = localfile.length();
        StorageReference filerefoncloud = mainrefstorage.child("/departments/"+newpath+"/"+name);
        Log.d("MSSGG",path);
        UploadTask myuploadtask = filerefoncloud.putFile(Uri.fromFile(localfile));
        myuploadtask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(EditDepartmentDialogActivity.this, "Photo Updated !!!!", Toast.LENGTH_SHORT).show();
                setImage();
                department department_object = new department(name,description,path);
                DatabaseReference deparmtment_reference = departmentref.child(name);
                deparmtment_reference.setValue(department_object);
            }
        });
        myuploadtask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditDepartmentDialogActivity.this, "Photo upload Failed!!!", Toast.LENGTH_SHORT).show();
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

    public void deletefile()//this function delete previous image on storage
    {
        StorageReference file11 = mainrefstorage.child("departments"+path);
        Log.d("MSSGG","log departments"+path+"/"+name);
        file11.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
               //Toast.makeText(EditDepartmentDialogActivity.this, "Department Photo deleted from Storage", Toast.LENGTH_SHORT).show();
            }
        });
    }
    //this function get absolute path of image from uri
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

//this function update department details in databse
    public  void updateDepartment(View view){
        description = edittextdepartmentdescription.getText().toString();
        if(description.isEmpty()){
            Toast.makeText(getApplicationContext(),"enter department description",Toast.LENGTH_SHORT).show();
        }
        else{
            department department_object = new department(name,description,path);
            DatabaseReference deparmtment_reference = departmentref.child(name);
            deparmtment_reference.setValue(department_object);
            Toast.makeText(getApplicationContext(),"Department Updated",Toast.LENGTH_SHORT).show();
        }
    }

    //to load image on imageview from firebase storage
    public void setImage(){
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference department_photo_reference = storageRef.child("departments"+path);
        department_photo_reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
        {
            @Override
            public void onSuccess(Uri downloadUrl)
            {
                //do something with downloadurl
                Picasso.with(EditDepartmentDialogActivity.this).load(downloadUrl).into(departmentimage);
            }
        });
    }
}