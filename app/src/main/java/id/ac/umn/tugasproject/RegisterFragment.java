package id.ac.umn.tugasproject;

import android.app.ProgressDialog;
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
    public RegisterFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View Register_inflater = inflater.inflate(R.layout.fragment_register, container, false);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        progressDialog = new ProgressDialog(getContext());

        buttonRegister = (Button) Register_inflater.findViewById(R.id.btn_register);

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
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(getContext(), "Authentication failed.",Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }
        });
    }
}
