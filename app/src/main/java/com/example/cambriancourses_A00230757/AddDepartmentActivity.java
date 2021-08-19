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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
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
import java.io.File;
import java.util.ArrayList;

public class AddDepartmentActivity extends AppCompatActivity {

    ArrayList<department> arraylist_departments = new ArrayList<department>();//arraylist of type department,
    // which stores department information fetched from realtime firebase databse
    myadapter mycustomadapter_departments;//customized adapter for departments to store image and other text data of department in listview

    ListView listview_departments;//listview reference to display departments
    EditText edittext_department_name,edittext_description,edittext_imagepath;//edittexts to input department
    //name , description and show image path selected from phone memory

    FirebaseDatabase firebaseDatabase;//firebase  database instance
    DatabaseReference mainrefdepartment;// firebase database main reference
    DatabaseReference departmentref;//reference to child departments
    FirebaseStorage firebaseStorage;//firebase storage instance
    StorageReference mainrefstorage;//firebase storage reference to child department photos

    String department_photopath="";// string to store department photo path selected by admin while adding new department
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_department);

       // memory to different views
        listview_departments = (ListView) (findViewById(R.id.listview_departments));
        edittext_department_name = (EditText) (findViewById(R.id.edittext_department_name));
        edittext_description = (EditText) (findViewById(R.id.edittext_description));
        edittext_imagepath = (EditText) (findViewById(R.id.edittext_imagepath));

        //objects of firebase reference classes defined at top of oncreate  are made here
        firebaseDatabase = FirebaseDatabase.getInstance(new firebase_cloud().getLink());
        mainrefdepartment = firebaseDatabase.getReference();
        departmentref =mainrefdepartment.child("departments");

        firebaseStorage = FirebaseStorage.getInstance();
        mainrefstorage = firebaseStorage.getReference();

        //custom adapter object
        mycustomadapter_departments = new myadapter();
        listview_departments.setAdapter(mycustomadapter_departments);//adapter set to list view
        fetchDepartmentsFromFirebase();//this function is called to fetch already added departments from firebase and store data in array list.

        listview_departments.setOnItemClickListener(new AdapterView.OnItemClickListener() {//click listener on list view
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(getApplicationContext(), arraylist_departments.get(position).name+" "+arraylist_departments.get(position).description, Toast.LENGTH_SHORT).show();
            }
        });
    }
   // function to fetch already added departments from firebase and store data in array list.
    public void fetchDepartmentsFromFirebase(){
        arraylist_departments.clear();//clear all elements of array list before store data
        departmentref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                arraylist_departments.clear();
                //Log.d("MYESSAGE",dataSnapshot.toString());
                for(DataSnapshot  singlesnapshot : dataSnapshot.getChildren())//a lop which runs and give one department data object at a time
                {
                    department depttemp = singlesnapshot.getValue(department.class);//one department data stored in department class
                    try {
                        //Log.d("MYESSAGE",singlesnapshot.getValue(department.class));
                    }
                    catch (Exception ex){
                        ex.printStackTrace();
                    }
                    arraylist_departments.add(depttemp);//added to array list
                }
                mycustomadapter_departments.notifyDataSetChanged();// custom datapter refereshed to show show latest data in listview
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public boolean checkDuplicateEntry (String deptname){//while adding new department this function checks
        // whether department with same name already exists or not in database
        boolean flag = true;
        for(int i=0; i<arraylist_departments.size(); i++) {
            String single_department_name = arraylist_departments.get(i).name;
            if (single_department_name.equals(deptname)){
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
    public void gallery(View view)//to open gallery to choose image for department
    {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent,91);//91 code for gallery
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
           Uri uri = data.getData();//get image uri
            Uri selectedImageUri = data.getData();
            String selectedImagePath = getPath(getApplicationContext(),selectedImageUri);//get selected image path from uri, absolute path in real device
            System.out.println("Image Path : " + selectedImagePath);
            department_photopath =selectedImagePath;//set this path to global department photopath variable
            edittext_imagepath.setText(department_photopath);//set this same path to edit text image path also
            edittext_imagepath.setEnabled(false);//make edit text editabel false so admin cannot by mistake delete / change path
        }
    }
    public static String getPath( Context context, Uri uri ) {//this function give us absolute path of image selected from gallery
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

    public void add(View view)//this function add new department data to firebase realtime database after checking whether data is valid or not.
    {
        //here data is stored in variable and checked for empty or not
        String name_department = edittext_department_name.getText().toString();
        String description_department = edittext_description.getText().toString();
        if (name_department.isEmpty()){
            Toast.makeText(getApplicationContext(),"Enter department name",Toast.LENGTH_SHORT).show();
        }
        else if(description_department.isEmpty()){
            Toast.makeText(getApplicationContext(),"Enter department description",Toast.LENGTH_SHORT).show();
        }
        else if (department_photopath.isEmpty()){
            Toast.makeText(getApplicationContext(),"Choose department image",Toast.LENGTH_SHORT).show();
        }
        else{//this else runs if everything is ok

            //we can store object of a class directly to firebase database, so here an object of department class is set
            department department_object = new department(name_department,description_department,department_photopath+"/"+name_department);
            DatabaseReference deparmtment_reference = departmentref.child(name_department);//department name is key
            Log.d("MYMESSAGE",deparmtment_reference.getKey());
            if(checkDuplicateEntry(name_department)) {//duplicacy is checked for department name
                deparmtment_reference.setValue(department_object);//added to database
                uploadlogic(department_photopath , name_department);//this function upload image to firebase storage
            }
            else{
                Toast.makeText(getApplicationContext(),"Department with same name already exists",Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void uploadlogic(String path , String deptname)//this function read image from phone memoery and upload it into firebase storage
    {
        File localfile=new File(path);
        final long uploadfilesize = localfile.length();
        StorageReference filerefoncloud = mainrefstorage.child("/departments/"+department_photopath+"/"+deptname);
        UploadTask myuploadtask = filerefoncloud.putFile(Uri.fromFile(localfile));
        myuploadtask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(AddDepartmentActivity.this, "New department Added ,Upload DONE !!!!", Toast.LENGTH_SHORT).show();
                //tv3.setText(taskSnapshot.getMetadata().getReference().getDownloadUrl().toString()+"");
                edittext_department_name.setText("");//all the edittext are cleared to add new department if any
                edittext_description.setText("");
                edittext_imagepath.setText("");
                department_photopath="";
               //fetchDepartmentsFromFirebase();
                mycustomadapter_departments.notifyDataSetChanged();//adapter is refreshed to show newly added department
            }
        });
        myuploadtask.addOnFailureListener(new OnFailureListener() {//in case some failure occur while uploading this function inform us
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddDepartmentActivity.this, "New Department Upload Failed !!!", Toast.LENGTH_SHORT).show();
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

    public void deletefile(String path)//this function delete departmnet photo datafrom firebase storage when admin delete a department from the list
            //to save memory on firabase storage
    {
        StorageReference file11 = mainrefstorage.child("departments/"+path);
        file11.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(AddDepartmentActivity.this, "Department Deleted ,File Deleted from Storage", Toast.LENGTH_SHORT).show();
            }
        });
    }

    class myadapter extends BaseAdapter//this is custom adapter class to show array list data of departments in list view
    {
        @Override
        public int getCount() {
            return arraylist_departments.size();
        }//to gice list size

        @Override
        public Object getItem(int position) {
            return arraylist_departments.get(position);
        }//list position

        @Override
        public long getItemId(int position) {
            return position*10;
        }//items id

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {//to set data on views of single_row_adddepartment.xml file
            //see single_row_adddepartment.xml for views detail

            if(convertView==null) {
                LayoutInflater l = LayoutInflater.from(getApplicationContext());
                convertView = l.inflate(R.layout.single_row_adddepartment, parent, false);
            }

            //single_row_adddepartment.xml views object are made
            TextView texview_department_name = (TextView) (convertView.findViewById(R.id.texview_department_name));
            TextView texview_department_description = (TextView) (convertView.findViewById(R.id.texview_department_description));
            TextView texview_department_photo = (TextView) (convertView.findViewById(R.id.texview_department_photo));
            Button btdelete =(Button)(convertView.findViewById(R.id.btdeletedept));
            Button btedit =(Button)(convertView.findViewById(R.id.bteditdept));
            ImageView imv1dept =(ImageView) (convertView.findViewById(R.id.imv1dept));

            //date set on views of single_row_adddepartment.xml
            department d = arraylist_departments.get(position);
            texview_department_name.setText("Name: "+d.name);
            texview_department_description.setText("Description: "+d.description);
            texview_department_photo.setText("path: "+d.path);

            //to load department image on  imv1dept imageview of singel layout file
            //here we put this code on separate thread to load data faster
            //this means it is duty of this separate thread to load image
            //this code has no realtion with other logic
            new Thread(new Runnable() {
                @Override
                public void run() {
                    StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                    StorageReference department_photo_reference = storageRef.child("departments"+d.path);
                    department_photo_reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
                    {
                        @Override
                        public void onSuccess(Uri downloadUrl)
                        {
                            //picasso is used to load image on image view
                            //this is external library in build.gradle file which is capble to load from from any url/phone memory etc
                            Picasso.with(AddDepartmentActivity.this).load(downloadUrl).resize(200,200).into(imv1dept);
                        }
                    });
                }
            }).start();

            //to delete particular department of singel layout file
            //here we put this code on separate thread to deletre single department
            //this means it is duty of this separate thread to delee department
            //this code has no realtion with other logic
            new Thread(new Runnable() {
                @Override
                public void run() {
                    btdelete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            departmentref.child(d.name).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot singleSnapshot: dataSnapshot.getChildren()) {
                                        singleSnapshot.getRef().removeValue();
                                        deletefile(d.path);
                                        fetchDepartmentsFromFirebase();
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

            //to edit particular department of singel layout file
            //here we put this code on separate thread to edit single department
            //this means it is duty of this separate thread to edit department
            //this code has no realtion with other logic
            new Thread(new Runnable() {
                @Override
                public void run() {
                    btedit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            departmentref.child(d.name).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot singleSnapshot: dataSnapshot.getChildren()) {
//to open dialog activity on top of this activity whih shows department details and butoons to update department data
                                        Intent myIntent = new Intent(AddDepartmentActivity.this, EditDepartmentDialogActivity.class);
                                        myIntent.putExtra("name",d.name); //Optional parameters
                                        myIntent.putExtra("description",d.description); //Optional parameters
                                        myIntent.putExtra("path",d.path); //Optional parameters
                                    startActivity(myIntent);
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
