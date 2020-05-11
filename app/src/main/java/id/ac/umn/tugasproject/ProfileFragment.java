package id.ac.umn.tugasproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.Phaser;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {
    private Button buttonLogout, btnAdd_or_edit_fam;
    private ImageView ivProfile;
    private TextView tvName,tvEmail,bloodType,phoneNumb,fam1,fam2,fam3;;
    private EditText ePhoneNumb,eFam1,eFam2,eFam3;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseUser;
    private FirebaseDatabase database;
    private static final String user = "user";
    private String uEmail,uId;
    private String gender;
    private boolean edit = true;
    private static final int PICK_IMAGE = 1;
    private static final int MY_INTENT_REQUEST_CODE = 263;
    Uri imageUri;
    private StorageReference profilePicture;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View profile_inflater =  inflater.inflate(R.layout.fragment_profile, container, false);

        ivProfile = (ImageView) profile_inflater.findViewById(R.id.ivProfile);
        tvName = (TextView) profile_inflater.findViewById(R.id.tvName);
        tvEmail = (TextView)profile_inflater.findViewById(R.id.tvEmail);
        fam1 = (TextView) profile_inflater.findViewById(R.id.fam1);
        fam2 = (TextView) profile_inflater.findViewById(R.id.fam2);
        fam3 = (TextView) profile_inflater.findViewById(R.id.fam3);
        bloodType = (TextView)profile_inflater.findViewById(R.id.bloodType);
        phoneNumb = (TextView) profile_inflater.findViewById(R.id.phoneNumb);

        eFam1 = (EditText) profile_inflater.findViewById(R.id.eFam1);
        eFam2 = (EditText) profile_inflater.findViewById(R.id.eFam2);
        eFam3 = (EditText) profile_inflater.findViewById(R.id.eFam3);
        ePhoneNumb = (EditText) profile_inflater.findViewById(R.id.ePhoneNumb);

        buttonLogout = (Button)profile_inflater.findViewById(R.id.logout);
        btnAdd_or_edit_fam = (Button)profile_inflater.findViewById(R.id.btnAdd_or_edit_fam);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        uId = firebaseAuth.getUid();
        uEmail = firebaseUser.getEmail();
        profilePicture = FirebaseStorage.getInstance().getReference().child("profilePicture/" + uId);
        Log.d("MASUK","EMAIL = " + uEmail  );




        if (firebaseUser != null) {
            database = FirebaseDatabase.getInstance();
            databaseUser = database.getReference(user);
            // INI NGAMBIL DATANYAA DARI FIREBASE LIVE DATABASE //
            loadUserInfo();

            // GOOGLE PROFILE //
            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getContext());
            if(account !=  null){
                String personName = account.getDisplayName();
                String personGivenName = account.getGivenName();
                String personFamilyName = account.getFamilyName();
                String personEmail = account.getEmail();
                String personId = account.getId();
                Uri personPhoto = account.getPhotoUrl();

                Picasso.get().load(personPhoto).into(ivProfile);
                tvName.setText(personGivenName);
                tvEmail.setText(personEmail);
            }

            ivProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent gallery = new Intent();
                    gallery.setType("image/*");
                    gallery.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(gallery,"Select Profile Picture"),PICK_IMAGE);

                }
            });

            buttonLogout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAuth.getInstance().signOut();
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            });
        } else {
            // GAADA USER YG LAGI LOGIN //
            startActivity(new Intent(getActivity(),LoginActivity.class));
        }

        return profile_inflater;
    }

    public void loadUserInfo(){
        databaseUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds:dataSnapshot.getChildren()){
                    if(ds.child("email").getValue().equals(uEmail)){

                        if(ds.child("gender").getValue().equals("Pria") && !(ds.hasChild("profilePicture"))){
                            ivProfile.setBackgroundResource(R.drawable.ic_boy);
                        }else{
                            ivProfile.setBackground(null);
                            Glide.with(getContext()).load(ds.child("profilePicture").child("imageurl").getValue()).into(ivProfile);
                        }

                        if(ds.child("gender").getValue().equals("Wanita") && !(ds.hasChild("profilePicture"))){
                            ivProfile.setBackgroundResource(R.drawable.ic_girl);
                        }else{
                            Glide.with(getContext()).load(ds.child("profilePicture").child("imageurl").getValue()).into(ivProfile);
                        }

                        tvName.setText(ds.child("fullname").getValue(String.class));
                        tvEmail.setText(uEmail);

                        phoneNumb.setText(ds.child("phone").getValue(String.class));
                        bloodType.setText(ds.child("bloodType").getValue(String.class));

                        // EDIT//
                        if( ds.hasChild("fam1") && ds.hasChild("fam2") && ds.hasChild("fam3") ){
                            fam1.setText(ds.child("fam1").getValue(String.class));
                            fam2.setText(ds.child("fam2").getValue(String.class));
                            fam3.setText(ds.child("fam3").getValue(String.class));
                            btnAdd_or_edit_fam.setText("Edit family number");
                            btnAdd_or_edit_fam.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if(edit){
                                        btnAdd_or_edit_fam.setText("Save");
                                        // BIKIN EDIT TEXT NYA VISIBLE //
                                        eFam1.setVisibility(View.VISIBLE);
                                        eFam2.setVisibility(View.VISIBLE);
                                        eFam3.setVisibility(View.VISIBLE);

                                        // BIKIN TEXT VIEW NYA INVISIBLE DULU //
                                        fam1.setVisibility(View.INVISIBLE);
                                        fam2.setVisibility(View.INVISIBLE);
                                        fam3.setVisibility(View.INVISIBLE);
                                        edit = false;
                                    }else{
                                        // BIAR SETIDAKNY DIA MASUKIN 1 FAM NUMBER //
                                        if(eFam1.getText().toString().isEmpty()){
                                            Toast.makeText(getContext(), "Please fill at least 1 phone number",Toast.LENGTH_SHORT).show();
                                        }else{
                                            // CHECK KALO GA EMPTY MASUKIN NO telp yg di input //
                                            if(!(eFam1.getText().toString().isEmpty())){
                                                databaseUser.child(uId).child("fam1").setValue(eFam1.getText().toString());
                                            }// KALO EMPTY MASUKIN  "-"
                                            else{
                                                databaseUser.child(uId).child("fam1").setValue("-");
                                            }

                                            if(!(eFam2.getText().toString().isEmpty())){
                                                databaseUser.child(uId).child("fam2").setValue(eFam2.getText().toString());
                                            }else{
                                                databaseUser.child(uId).child("fam2").setValue("-");
                                            }

                                            if(!(eFam3.getText().toString().isEmpty())){
                                                databaseUser.child(uId).child("fam3").setValue(eFam3.getText().toString());
                                            }else{
                                                databaseUser.child(uId).child("fam3").setValue("-");
                                            }

                                            // BIKIN EDIT TEXT NYA INVISIBLE //
                                            eFam1.setVisibility(View.INVISIBLE);
                                            eFam2.setVisibility(View.INVISIBLE);
                                            eFam3.setVisibility(View.INVISIBLE);

                                            // BIKIN TEXT VIEW NYA VISIBLE  //
                                            fam1.setVisibility(View.VISIBLE);
                                            fam2.setVisibility(View.VISIBLE);
                                            fam3.setVisibility(View.VISIBLE);
                                            btnAdd_or_edit_fam.setText("Edit family number");
                                            edit = true;
                                        }
                                    }
                                }
                            });
                        }// ADD //
                        else{
                            fam1.setText("Haven't add any family number");
                            fam2.setText("Haven't add any family number");
                            fam3.setText("Haven't add any family number");
                            btnAdd_or_edit_fam.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if(edit){
                                        btnAdd_or_edit_fam.setText("Save Added phone number");
                                        // BIKIN EDIT TEXT NYA VISIBLE //
                                        eFam1.setVisibility(View.VISIBLE);
                                        eFam2.setVisibility(View.VISIBLE);
                                        eFam3.setVisibility(View.VISIBLE);

                                        // BIKIN TEXT VIEW NYA INVISIBLE DULU //
                                        fam1.setVisibility(View.INVISIBLE);
                                        fam2.setVisibility(View.INVISIBLE);
                                        fam3.setVisibility(View.INVISIBLE);
                                        edit = false;
                                    }else{
                                        // BIAR SETIDAKNY DIA MASUKIN 1 FAM NUMBER //
                                        if(eFam1.getText().toString().isEmpty()){
                                            Toast.makeText(getContext(), "Please fill at least 1 phone number",Toast.LENGTH_SHORT).show();
                                        }else{

                                            // CHECK KALO GA EMPTY MASUKIN NO telp yg di input //
                                            if(!(eFam1.getText().toString().isEmpty())){
                                                databaseUser.child(uId).child("fam1").setValue(eFam1.getText().toString());
                                            }// KALO EMPTY MASUKIN  "-"
                                            else{
                                                databaseUser.child(uId).child("fam1").setValue("-");
                                            }

                                            if(!(eFam2.getText().toString().isEmpty())){
                                                databaseUser.child(uId).child("fam2").setValue(eFam2.getText().toString());
                                            }else{
                                                databaseUser.child(uId).child("fam2").setValue("-");
                                            }

                                            if(!(eFam3.getText().toString().isEmpty())){
                                                databaseUser.child(uId).child("fam3").setValue(eFam3.getText().toString());
                                            }else{
                                                databaseUser.child(uId).child("fam3").setValue("-");
                                            }

                                            // BIKIN EDIT TEXT NYA INVISIBLE //
                                            eFam1.setVisibility(View.INVISIBLE);
                                            eFam2.setVisibility(View.INVISIBLE);
                                            eFam3.setVisibility(View.INVISIBLE);

                                            // BIKIN TEXT VIEW NYA VISIBLE  //
                                            fam1.setVisibility(View.VISIBLE);
                                            fam2.setVisibility(View.VISIBLE);
                                            fam3.setVisibility(View.VISIBLE);
                                            btnAdd_or_edit_fam.setText("Edit family number");
                                            edit = true;
                                        }

                                    }
                                }
                            });

                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE && resultCode == RESULT_OK){
            final Uri ImageData = data.getData();
            ivProfile.setImageURI(data.getData());

            final StorageReference imageName = profilePicture.child("image"+ImageData.getLastPathSegment());

            imageName.putFile(ImageData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    imageName.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            HashMap<String,String> hashMap = new HashMap<>();
                            hashMap.put("imageurl",String.valueOf(uri));

                            databaseUser.child(uId).child("profilePicture").setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(getContext(), "Successfully change profile picture",Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    });


                }
            });

        }
    }
}
