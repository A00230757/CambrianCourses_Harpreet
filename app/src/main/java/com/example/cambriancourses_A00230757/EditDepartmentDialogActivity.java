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
    ImageView departmentimage;
    EditText edittextdepartmentname,edittextdepartmentdescription;

    String name="",description="",path="";

    String newpath="";
    FirebaseDatabase firebaseDatabase;
    DatabaseReference mainrefdepartment;
    DatabaseReference departmentref;
    FirebaseStorage firebaseStorage;
    StorageReference mainrefstorage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_department_dialog);

        departmentimage = (ImageView)(findViewById(R.id.departmentimage));
        edittextdepartmentname =(EditText)(findViewById(R.id.edittextdepartmentname));
        edittextdepartmentdescription =(EditText)(findViewById(R.id.edittextdepartmentdescription));

        firebaseDatabase = FirebaseDatabase.getInstance(new firebase_cloud().getLink());
        mainrefdepartment = firebaseDatabase.getReference();
        departmentref =mainrefdepartment.child("departments");

        firebaseStorage = FirebaseStorage.getInstance();
        mainrefstorage = firebaseStorage.getReference();

        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        description = intent.getStringExtra("description");
        path = intent.getStringExtra("path");
        //Toast.makeText(getApplicationContext(),name,Toast.LENGTH_SHORT).show();
        edittextdepartmentname.setText(name);
        edittextdepartmentname.setEnabled(false);
        edittextdepartmentdescription.setText(description);

    setImage();
    }

    public  void disableDialog(View view){
       finish();
    }

    public void gallery(View view)
    {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent,91);
    }

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
    public void uploadlogic(String newpath)
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

    public void deletefile()
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