package com.example.cambriancourses_A00230757;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class EditDepartmentDialogActivity extends AppCompatActivity {
    ImageView departmentimage;
    EditText edittextdepartmentname,edittextdepartmentdescription;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_department_dialog);

        departmentimage = (ImageView)(findViewById(R.id.departmentimage));
        edittextdepartmentname =(EditText)(findViewById(R.id.edittextdepartmentname));
        edittextdepartmentdescription =(EditText)(findViewById(R.id.edittextdepartmentdescription));

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String description = intent.getStringExtra("description");
        String path = intent.getStringExtra("path");
        Toast.makeText(getApplicationContext(),name,Toast.LENGTH_SHORT).show();
        edittextdepartmentname.setText(name);
        edittextdepartmentname.setEnabled(false);
        edittextdepartmentdescription.setText(description);

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

    public  void disableDialog(View view){

    }
    public  void updateDepartment(View view){

    }
}