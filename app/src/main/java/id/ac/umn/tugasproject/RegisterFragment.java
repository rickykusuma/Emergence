package id.ac.umn.tugasproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterFragment extends Fragment implements  View.OnClickListener{
    private EditText name;
    private EditText address;
    private EditText phone;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextRePassword;
    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    private Button buttonRegister;
    private  DatabaseReference databaseUser;
//    private DatabaseReference databaseReference;
    private FirebaseDatabase database;
    public RegisterFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View Register_inflater = inflater.inflate(R.layout.fragment_register, container, false);
        mAuth = FirebaseAuth.getInstance();
//        databaseReference = FirebaseDatabase.getInstance().getReference();
        database = FirebaseDatabase.getInstance();
        databaseUser = FirebaseDatabase.getInstance().getReference("User");
        FirebaseUser currentUser = mAuth.getCurrentUser();
        progressDialog = new ProgressDialog(getContext());

        buttonRegister = (Button) Register_inflater.findViewById(R.id.btn_register);


        name = (EditText) Register_inflater.findViewById(R.id.et_name);
        address = (EditText) Register_inflater.findViewById(R.id.et_address);
        phone = (EditText) Register_inflater.findViewById(R.id.et_phone);


        editTextEmail = (EditText) Register_inflater.findViewById(R.id.et_email);
        editTextPassword = (EditText) Register_inflater.findViewById(R.id.et_password);
        buttonRegister.setOnClickListener(this);
        // Inflate the layout for this fragment
        return Register_inflater;
    }

    @Override
    public void onClick(View v) {
        if (v == buttonRegister) {
            registerUser();
        }
    }
    public void saveUserInformation(){
        String nama = name.getText().toString().trim();
        String add = address.getText().toString().trim();
        String nomor = phone.getText().toString().trim();
        String e_mail = editTextEmail.getText().toString().trim();

        User_Information user_info = new User_Information(nama,add,nomor,e_mail);
        String id = databaseUser.push().getKey();
        databaseUser.child(id).setValue(user_info);


//        FirebaseUser user = mAuth.getCurrentUser();
//
//        database.getReference(user.getUid()).setValue(user_info);

    }
    private void registerUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        Log.d("MASUK","SNI");
        if(TextUtils.isEmpty(email)){
            Toast.makeText(getContext(), "Please enter Email",Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(getContext(), "Please enter Password",Toast.LENGTH_SHORT).show();
        }
        progressDialog.setMessage("Registering User.....");
        progressDialog.show();
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Toast.makeText(getContext(), "Authentication successful.   ",Toast.LENGTH_SHORT).show();
                    FirebaseUser user = mAuth.getCurrentUser();
                    progressDialog.dismiss();
                    saveUserInformation();
                    startActivity(new Intent(getContext(),MainActivity.class));
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(getContext(), "Authentication failed.",Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }
        });
    }
}
