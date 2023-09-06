package com.example.ecuvigila;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Login extends AppCompatActivity {

    private FirebaseAuth mAuth;
    FirebaseFirestore firebaseFirestore;
    EditText editEmail, editPass;
    Button btnRegistrar, btnIngresar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);

        mAuth = FirebaseAuth.getInstance();
        editEmail = findViewById(R.id.editEmail);
        editPass = findViewById(R.id.editPass);
//        btnRegistrar = findViewById(R.id.btnRegistrar);
        btnIngresar = findViewById(R.id.btnIngresar);

        btnIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = editEmail.getText().toString().trim();
                String pass = editPass.getText().toString().trim();

                if(email.isEmpty() || pass.isEmpty()){
                    Toast.makeText(Login.this, "Ingresar los datos", Toast.LENGTH_SHORT).show();
                } else {
                    loginUser(email, pass);
                }
            }
        });

//        btnRegistrar.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String email = editEmail.getText().toString().trim();
//                String pass = editPass.getText().toString().trim();
//
//                if(email.isEmpty() || pass.isEmpty()){
//                    Toast.makeText(Login.this, "Ingresar los datos", Toast.LENGTH_SHORT).show();
//                } else {
//                    registerUser(email, pass);
//                }
//            }
//        });
    }

    private void loginUser(String email, String pass) {
        mAuth.signInWithEmailAndPassword(email, pass).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                finish();

                if(email.equals("ppc@gmail.com")){
                    startActivity(new Intent(Login.this, GestionUsuarios.class));
                } else {
                    startActivity(new Intent(Login.this, MainActivity.class));
                }

                Toast.makeText(Login.this, "Bienvenido!", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null){
            String email = user.getEmail();

            if(email.equals("ppc@gmail.com")){
                startActivity(new Intent(Login.this, GestionUsuarios.class));
            } else {
                startActivity(new Intent(Login.this, MainActivity.class));
            }
            finish();
        }
    }
}
