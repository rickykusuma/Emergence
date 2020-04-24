package id.ac.umn.tugasproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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

import org.w3c.dom.Text;

public class LoginFragment extends Fragment implements View.OnClickListener{
    private EditText input_email;
    private EditText input_password;
    private Button buttonSignIn;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    public LoginFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View login_inflater = inflater.inflate(R.layout.fragment_login, container, false);


        input_email = (EditText) login_inflater.findViewById(R.id.et_email);
        input_password = (EditText) login_inflater.findViewById(R.id.et_password);
        buttonSignIn = (Button) login_inflater.findViewById(R.id.btn_login);
        progressDialog = new ProgressDialog(getContext());
        firebaseAuth = FirebaseAuth.getInstance();

        buttonSignIn.setOnClickListener(this);


        return login_inflater;
    }


    @Override
    public void onClick(View v) {
        if(v == buttonSignIn){
            userLogin();
        }
    }

    private void userLogin() {
        String email = input_email.getText().toString().trim();
        String password = input_password.getText().toString().trim();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(getContext(),"Please Enter Email",Toast.LENGTH_LONG).show();
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(getContext(),"Please Enter Password",Toast.LENGTH_LONG).show();
        }

        progressDialog.setMessage("Please Wait ...");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    progressDialog.dismiss();
                    if(firebaseAuth.getCurrentUser().isEmailVerified()){
                        startActivity(new Intent(getActivity(),MainActivity.class));
                    }else{
                        Toast.makeText(getContext(),"Please verify your email address",Toast.LENGTH_LONG).show();
                    }
                }else{
                    progressDialog.dismiss();
                    Toast.makeText(getContext(),"Invalid E-mail or Password, Please Try Again ...",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}























