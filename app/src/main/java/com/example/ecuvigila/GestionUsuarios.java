package com.example.ecuvigila;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class GestionUsuarios extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private RecyclerView recyclerView;
    private ArrayList<UsersItem> usersItemArrayList;
    private UsersRecyclerAdapter adapter;
    private Button buttonAdd, buttonLogout;
    private FirebaseAuth mAuth;
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_info_emergencia);

        context = this;

        databaseReference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        usersItemArrayList = new ArrayList<>();

        buttonAdd = findViewById(R.id.buttonAdd);
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewDialogAdd viewDialogAdd = new ViewDialogAdd();
                viewDialogAdd.showDialog(context);
            }
        });

        buttonLogout = findViewById(R.id.buttonLogout);

        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(GestionUsuarios.this, Login.class));
                finish();
            }
        });

        readData();
    }

    private void readData() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference()
                .child("USERS");

        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usersItemArrayList.clear();

                for (DataSnapshot contactSnapshot : dataSnapshot.getChildren()) {
                    UsersItem contact = contactSnapshot.getValue(UsersItem.class);
                    if (contact != null) {
                        usersItemArrayList.add(contact);
                    }
                }

                adapter = new UsersRecyclerAdapter(context, usersItemArrayList);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }

    public class ViewDialogAdd {
        public void showDialog(Context context) {
            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.alert_dialog_add_new_user);

            EditText textName = dialog.findViewById(R.id.textName);
            EditText textCorreo = dialog.findViewById(R.id.textCorreo);
            EditText textPass = dialog.findViewById(R.id.textContraseña);
            EditText textRol = dialog.findViewById(R.id.textRol);


            Button buttonAdd = dialog.findViewById(R.id.buttonAdd);
            Button buttonCancel = dialog.findViewById(R.id.buttonCancel);

            buttonAdd.setText("ADD");
            buttonCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            buttonAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String name = textName.getText().toString().trim();
                    String correo = textCorreo.getText().toString().trim();
                    String pass = textPass.getText().toString().trim();
                    String rol = textRol.getText().toString().trim();

                    if (name.isEmpty() || correo.isEmpty() || rol.isEmpty() || pass.isEmpty()) {
                        Toast.makeText(context, "Por favor, ingrese todos los datos...", Toast.LENGTH_SHORT).show();
                    } else {
                        //String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
//                        DatabaseReference userReference = databaseReference.child("USERS").child(name);

                        //DatabaseReference newContactRef = userReference.push(); // Generate a unique key

//                        UsersItem contact = new UsersItem(name, correo, rol);
//                        userReference.setValue(contact);
                        //newContactRef.setValue(contact);

                        registerUser(correo, pass, name, rol);

                        Toast.makeText(context, "Usuario Ingresado!", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }
            });


            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();

        }
    }

    private void registerUser(String email, String pass, String name, String rol) {
        mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    String uid = mAuth.getCurrentUser().getUid();
                    DatabaseReference userReference = databaseReference.child("USERS").child(uid);
                    UsersItem contact = new UsersItem(name, email, rol);
                    userReference.setValue(contact);
                    Toast.makeText(GestionUsuarios.this, "Usuario Ingresado!", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(GestionUsuarios.this, "Error al crear cuenta...", Toast.LENGTH_SHORT).show();
            }
        });
    }
}