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
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class RegisterFragment extends Fragment {
    private EditText name;
    private EditText address;
    private EditText phone;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextRePassword;
    private ProgressDialog progressDialog;
    private Button buttonRegister;

    private FirebaseAuth mAuth;
    private  DatabaseReference databaseUser;
    private FirebaseDatabase database;
    private static final String USER = "user";
    private User user;

    public RegisterFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View Register_inflater = inflater.inflate(R.layout.fragment_register, container, false);
        progressDialog = new ProgressDialog(getContext());
        buttonRegister = (Button) Register_inflater.findViewById(R.id.btn_register);
        name = (EditText) Register_inflater.findViewById(R.id.et_name);
        address = (EditText) Register_inflater.findViewById(R.id.et_address);
        phone = (EditText) Register_inflater.findViewById(R.id.et_phone);
        editTextEmail = (EditText) Register_inflater.findViewById(R.id.et_email);
        editTextPassword = (EditText) Register_inflater.findViewById(R.id.et_password);
        editTextRePassword = (EditText) Register_inflater.findViewById(R.id.et_repassword);


        database = FirebaseDatabase.getInstance();
        databaseUser = database.getReference(USER);
        mAuth = FirebaseAuth.getInstance();

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();
                Log.d("MASUK","SNI");
                if(TextUtils.isEmpty(email)){
                    Toast.makeText(getContext(), "Please enter Email",Toast.LENGTH_SHORT).show();
                }
                if(TextUtils.isEmpty(password)){
                    Toast.makeText(getContext(), "Please enter Password",Toast.LENGTH_SHORT).show();
                }
                String fullname = name.getText().toString();
                String addr = address.getText().toString() ;
                String phne = phone.getText().toString();
                user = new User(email,password,fullname,phne,addr);
                registerUser(email,password);
                progressDialog.setMessage("Registering User.....");
                progressDialog.show();
            }
        });

        return Register_inflater;
    }

    private void registerUser(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            mAuth.getCurrentUser().sendEmailVerification()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(getContext(), "Registered successfully. Please verify your email address",Toast.LENGTH_SHORT).show();
                                                FirebaseUser user = mAuth.getCurrentUser();
                                                updateUI(user);
                                            }else{
                                                Toast.makeText(getContext(), "Register failed, please check your credentials",Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        } else{
                            Toast.makeText(getContext(), "Register failed, please check your credentials",Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    public void updateUI(FirebaseUser currentUser){
        String keyId = databaseUser.push().getKey();
        databaseUser.child(keyId).setValue(user);
        Intent loginIntent = new Intent(getActivity(),LoginActivity.class);
        startActivity(loginIntent);
    }
}
