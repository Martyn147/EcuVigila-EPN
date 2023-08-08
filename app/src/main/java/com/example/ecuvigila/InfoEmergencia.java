package com.example.ecuvigila;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class InfoEmergencia extends Fragment {

    private DatabaseReference databaseReference;
    private RecyclerView recyclerView;
    private ArrayList<UsersItem> usersItemArrayList;
    private UsersRecyclerAdapter adapter;
    private Button buttonAdd;
    private FirebaseAuth mAuth;
    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_info_emergencia, container, false);

        context = getContext();

        databaseReference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        usersItemArrayList = new ArrayList<>();

        buttonAdd = rootView.findViewById(R.id.buttonAdd);
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewDialogAdd viewDialogAdd = new ViewDialogAdd();
                viewDialogAdd.showDialog(context);
            }
        });

        readData();

        return rootView;
    }

    private void readData() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference()
                .child("USERS")
                .child(uid);

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
            EditText textRelacion = dialog.findViewById(R.id.textRelacion);
            EditText textContTlf = dialog.findViewById(R.id.textContTlf);


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
                    String name = textName.getText().toString();
                    String relacion = textRelacion.getText().toString();
                    String contTlf = textContTlf.getText().toString();

                    if (name.isEmpty() || relacion.isEmpty() || contTlf.isEmpty()) {
                        Toast.makeText(context, "Por favor, ingrese todos los datos...", Toast.LENGTH_SHORT).show();
                    } else {
                        String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
                        DatabaseReference userReference = databaseReference.child("USERS").child(uid).child(contTlf);

                        //DatabaseReference newContactRef = userReference.push(); // Generate a unique key

                        UsersItem contact = new UsersItem(name, relacion, contTlf);
                        userReference.setValue(contact);
                        //newContactRef.setValue(contact);

                        Toast.makeText(context, "Información ingresada...", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }
            });


            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();

        }
    }
}
