package com.example.cambriancourses_A00230757;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminLoginActivity extends AppCompatActivity {

    EditText edittextadminid, edittextadminpassword;//input admin login credentials
String adminid = "";
String adminpassword = "";

    FirebaseDatabase firebaseDatabase;//firebase  database instance
    DatabaseReference mainrefcourse;// firebase database main reference
    DatabaseReference adminsref;//reference to child admins
String id_admin="";
String password_admin ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);
        setTitle("ADMIN LOGIN");
        edittextadminid = (EditText) (findViewById(R.id.edittextadminid));
        edittextadminpassword= (EditText) (findViewById(R.id.edittextadminpassword));

        //objects of firebase reference classes defined at top of oncreate  are made here
        firebaseDatabase = FirebaseDatabase.getInstance(new firebase_cloud().getLink());
        mainrefcourse = firebaseDatabase.getReference();
        adminsref =mainrefcourse.child("admins");
        fetchAdminDataFromFirebase();//fetch admin data from firebase
    }

    //chk credentials match or not from firebase to give login access
    public void adminlogin(View v ) {
        adminid = edittextadminid.getText().toString();
        adminpassword = edittextadminpassword.getText().toString();
        Toast.makeText(getApplicationContext(),"ok",Toast.LENGTH_SHORT);
        Log.d("MSSGG","entered");
        if(adminid.isEmpty()){
            Toast.makeText(getApplicationContext(),"enter admin id",Toast.LENGTH_SHORT);
        }
        else if(adminpassword.isEmpty()){
            Toast.makeText(getApplicationContext(),"enter admin password",Toast.LENGTH_SHORT);
        }
        else {
            String admin_id_array[]=id_admin.split(",");
            String admin_password_array[]=password_admin.split(",");
            boolean flag =false;
            for(int i =0; i<admin_id_array.length;i++){
                if(admin_id_array[i].equals(adminid)&&admin_password_array[i].equals(adminpassword)){
                    flag = true;
                    break;
                }
            }

            if(flag){
                flag=false;
            Intent in =new Intent(this,AdminHomeActivity.class);
            in.putExtra("adminid",adminid);
            startActivity(in);

            }
            else{
                Toast.makeText(getApplicationContext(),"invalid credentials",Toast.LENGTH_SHORT);
            }

        }
        }

    //fetch admin data from firebase
        public void fetchAdminDataFromFirebase(){
        try{

            adminsref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    //Log.d("MYESSAGE",dataSnapshot.toString());
                    for(DataSnapshot  singlesnapshot : dataSnapshot.getChildren())
                    {
//                        professor proftemp = singlesnapshot.getValue(professor.class);
                      String k =  singlesnapshot.getKey();
                        String v =  singlesnapshot.child("password").getValue().toString();
                        id_admin=id_admin+k+",";
                        password_admin=password_admin+v+",";
                        //Toast.makeText(getApplicationContext(),k,Toast.LENGTH_SHORT).show();
//                        Log.d("MSSGG",k+","+v+","+adminid+","+adminpassword);
                    }
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


}




