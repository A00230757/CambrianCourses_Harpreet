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

public class AddProfessorActivity extends AppCompatActivity {

    ArrayList<professor> arraylist_professor = new ArrayList<professor>();//array list of type professor
    // to store professor data
    myadapter mycustomadapter_professor;//customized adapter for professors to store image and other text data of professor in listview

    ListView listview_professor;//listview reference to display professors
    EditText edittext_professor_name,edittext_email,edittext_imagepath,edittext_mobile;//edittexts to input different professor data

    Spinner spinnerdepartment;
    ArrayList<String> arraydepartments = new ArrayList<>();
    ArrayAdapter<String> adapter_departments ;

    FirebaseDatabase firebaseDatabase;//firebase  database instance
    DatabaseReference mainrefprofessor;// firebase database main reference
    DatabaseReference professorref;//reference to child professors
    FirebaseStorage firebaseStorage;//firebase storage instance
    StorageReference mainrefstorage;//firebase storage reference to child professor photos

    String professor_photopath="";
    String selected_department="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_professor);

        // memory to different views
        listview_professor = (ListView) (findViewById(R.id.listview_professor));
        edittext_professor_name = (EditText) (findViewById(R.id.edittext_professor_name));
        edittext_email = (EditText) (findViewById(R.id.edittext_email));
        edittext_mobile = (EditText) (findViewById(R.id.edittext_mobile));
        edittext_imagepath = (EditText) (findViewById(R.id.edittext_imagepath));

        spinnerdepartment = (Spinner) (findViewById(R.id.spinnerdepartment));
        adapter_departments = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,arraydepartments);
        spinnerdepartment.setAdapter(adapter_departments);


        //objects of firebase reference classes defined at top of oncreate  are made here
        firebaseDatabase = FirebaseDatabase.getInstance(new firebase_cloud().getLink());
        mainrefprofessor = firebaseDatabase.getReference();
        professorref =mainrefprofessor.child("professors");

        firebaseStorage = FirebaseStorage.getInstance();
        mainrefstorage = firebaseStorage.getReference();

        //custom adapter object
        mycustomadapter_professor= new myadapter();
        listview_professor.setAdapter(mycustomadapter_professor);
        fetchDepartmentsFromFirebase();//this function is called to fetch already added departments from firebase and store data in array list.

        spinnerdepartment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected_department = arraydepartments.get(position);
                fetchProfessorsFromFirebase(selected_department);
                Toast.makeText(getApplicationContext(),selected_department,Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        listview_professor.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), arraylist_professor.get(position).name+" "+arraylist_professor.get(position).email, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void fetchDepartmentsFromFirebase(){
        arraydepartments.clear();
        DatabaseReference mainrefdepartment;
        DatabaseReference departmentref;
        mainrefdepartment = firebaseDatabase.getReference();
        departmentref =mainrefdepartment.child("departments");
        departmentref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                arraydepartments.clear();
                //Log.d("MYESSAGE",dataSnapshot.toString());
                for(DataSnapshot  singlesnapshot : dataSnapshot.getChildren())
                {
                    department depttemp = singlesnapshot.getValue(department.class);
                    try {
                        Log.d("MYESSAGE",singlesnapshot.getValue(department.class).name);
                    }
                    catch (Exception ex){
                        ex.printStackTrace();
                    }
                    arraydepartments.add(depttemp.name);
                }
                adapter_departments.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    // function to fetch already added professorss from firebase and store data in array list.
    public void fetchProfessorsFromFirebase(String department_selected){
        arraylist_professor.clear();//clear all elements of array list before store data
        professorref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                arraylist_professor.clear();
                //Log.d("MYESSAGE",dataSnapshot.toString());
                for(DataSnapshot  singlesnapshot : dataSnapshot.getChildren())//a lop which runs and give one professor data object at a time
                {
                    professor professortemp = singlesnapshot.getValue(professor.class);//one professor data stored in professor class
                    try {
                        if(professortemp.under_dept.equals(department_selected)){
                            arraylist_professor.add(professortemp);
                        }
                    }
                    catch (Exception ex){
                        ex.printStackTrace();
                    }

                }
                Toast.makeText(getApplicationContext(),arraylist_professor.size()+"",Toast.LENGTH_SHORT).show();
                mycustomadapter_professor.notifyDataSetChanged();// custom datapter refereshed to show show latest data in listview
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //while adding new department this function checks
    // whether professor with same email already exists or not in database
    public boolean checkDuplicateEntry (String professoremail){
        boolean flag = true;
        for(int i=0; i<arraylist_professor.size(); i++) {
            String single_professor_mobile = arraylist_professor.get(i).mobile;
            if (single_professor_mobile.equals(professoremail)){
                flag = false;
                break;
            }
        }
        return flag;
    }


    public void camera(View view)
    {
        Intent in  = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(in,90);
    }
    //to open gallery to choose image for professor
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
        else if(requestCode==91 && resultCode==RESULT_OK)
        {
                Uri uri = data.getData();
                Uri selectedImageUri = data.getData();
                String selectedImagePath = getPath(getApplicationContext(),selectedImageUri);
                System.out.println("Image Path : " + selectedImagePath);
                professor_photopath =selectedImagePath;
                edittext_imagepath.setText(professor_photopath);
                edittext_imagepath.setEnabled(false);
                Log.d("MYMESSAGE",selectedImagePath);
        }
    }
    //this function give us absolute path of image selected from gallery
    public static String getPath( Context context, Uri uri ) {
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

    //this function add new professor data to firebase realtime database after checking whether data is valid or not.
    public void add(View view)
    {
        String name_professor = edittext_professor_name.getText().toString();
        String email_professor = edittext_email.getText().toString();
        String mobile_professor =edittext_mobile.getText().toString();

        //here data is stored in variable and checked for empty or not
        if (name_professor.isEmpty()){
            Toast.makeText(getApplicationContext(),"Enter professor name",Toast.LENGTH_SHORT).show();
        }
        else if(email_professor.isEmpty()){
            Toast.makeText(getApplicationContext(),"Enter professor email",Toast.LENGTH_SHORT).show();
        }
        else if (mobile_professor.isEmpty()){
            Toast.makeText(getApplicationContext(),"Enter professor mobile number",Toast.LENGTH_SHORT).show();
        }
        else if (professor_photopath.isEmpty()){
            Toast.makeText(getApplicationContext(),"Choose professor image",Toast.LENGTH_SHORT).show();
        }
        else{//this else runs if everything is ok

            //we can store object of a class directly to firebase database, so here an object of prodessor class is set
            professor professor_object = new professor(name_professor,email_professor,professor_photopath+"/"+name_professor,mobile_professor,selected_department);
            DatabaseReference professor_reference = professorref.child(mobile_professor);
            Log.d("MYMESSAGE",professor_reference.getKey());
            if(checkDuplicateEntry(mobile_professor)) {
                professor_reference.setValue(professor_object);
                uploadlogic(professor_photopath , name_professor);//this function upload image to firebase storage
            }
            else{
                Toast.makeText(getApplicationContext(),"professor with same mobile number in this department already exists",Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void uploadlogic(String path , String professorname)//this function read image from phone memoery and upload it into firebase storage
    {
        File localfile=new File(path);
        final long uploadfilesize = localfile.length();
        StorageReference filerefoncloud = mainrefstorage.child("/professors/"+professor_photopath+"/"+professorname);
        UploadTask myuploadtask = filerefoncloud.putFile(Uri.fromFile(localfile));
        myuploadtask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(AddProfessorActivity.this, "New professor Added ,Upload DONE !!!!", Toast.LENGTH_SHORT).show();
                //tv3.setText(taskSnapshot.getMetadata().getReference().getDownloadUrl().toString()+"");
                edittext_professor_name.setText("");
                edittext_email.setText("");
                edittext_mobile.setText("");
                edittext_imagepath.setText("");
                professor_photopath="";
                //fetchProfessorsFromFirebase(selected_department);
                mycustomadapter_professor.notifyDataSetChanged();//adapter is refreshed to show newly added professor
            }
        });
        myuploadtask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddProfessorActivity.this, "New professor Upload Failed !!!", Toast.LENGTH_SHORT).show();
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
    //this function delete professor photo datafrom firebase storage when admin delete a professor from the list
    //to save memory on firabase storage
    public void deletefile(String path)
    {
        StorageReference file11 = mainrefstorage.child("professors/"+path);
        file11.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(AddProfessorActivity.this, "Professor Deleted ,File Deleted from Storage", Toast.LENGTH_SHORT).show();
            }
        });
    }
    //this is custom adapter class to show array list data of professors in list view
    class myadapter extends BaseAdapter
    {
        @Override
        public int getCount() {
            return arraylist_professor.size();
        }

        @Override
        public Object getItem(int position) {
            return arraylist_professor.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position*10;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if(convertView==null) {
                LayoutInflater l = LayoutInflater.from(getApplicationContext());
                convertView = l.inflate(R.layout.single_professor_layout, parent, false);
            }
            TextView texview_professor_name = (TextView) (convertView.findViewById(R.id.texview_professor_name));
            TextView texview_professor_email = (TextView) (convertView.findViewById(R.id.texview_professor_email));
            TextView texview_professor_mobile = (TextView) (convertView.findViewById(R.id.texview_professor_mobile));
            Button btdelete =(Button)(convertView.findViewById(R.id.btdeleteprofessor));
            ImageView imv1professor =(ImageView) (convertView.findViewById(R.id.imv1professor));

            professor p = arraylist_professor.get(position);
            Log.d("TTHHGG",p.name+","+p.email+","+p.mobile);
            texview_professor_name.setText("Name: "+p.name);
            texview_professor_email.setText("Email: "+p.email);
            texview_professor_mobile.setText("Mobile: "+p.mobile);

            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            StorageReference professor_photo_reference = storageRef.child("professors"+p.path);
            professor_photo_reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
            {
                @Override
                public void onSuccess(Uri downloadUrl)
                {
                    //do something with downloadurl
                    Picasso.with(AddProfessorActivity.this).load(downloadUrl).resize(200,200).into(imv1professor);
                }
            });

            new Thread(new Runnable() {
                @Override
                public void run() {
                    btdelete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            professorref.child(p.mobile).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot singleSnapshot: dataSnapshot.getChildren()) {
                                        singleSnapshot.getRef().removeValue();
                                        deletefile(p.path);
                                        fetchProfessorsFromFirebase(selected_department);
                                    }
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            });
                        }
                    });
                }
            }).start();
            return convertView;
        }
    }

}
