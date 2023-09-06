package com.example.ecuvigila;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class UsersRecyclerAdapter extends RecyclerView.Adapter<UsersRecyclerAdapter.ViewHolder> {

    Context context;
    ArrayList<UsersItem> usersItemArrayList;
    DatabaseReference databaseReference;

    public UsersRecyclerAdapter(Context context, ArrayList<UsersItem> usersItemArrayList) {
        this.context = context;
        this.usersItemArrayList = usersItemArrayList;
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.user_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        UsersItem users = usersItemArrayList.get(position);

        holder.textName.setText("Nombre: " + users.getUserName());
        holder.textCorreo.setText("Correo: " + users.getuserCorreo());
        holder.textRol.setText("Rol: " + users.getuserRol());

        holder.buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewDialogUpdate viewDialogUpdate = new ViewDialogUpdate();
                viewDialogUpdate.showDialog(context, users.getUserName(), users.getuserCorreo(), users.getuserRol());
            }
        });

        holder.buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewDialogConfirmDelete viewDialogConfirmDelete = new ViewDialogConfirmDelete();
                viewDialogConfirmDelete.showDialog(context, users.getUserName());
            }
        });

    }

    @Override
    public int getItemCount() {
        return usersItemArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView textName;
        TextView textCorreo;
        TextView textRol;
        Button buttonDelete;
        Button buttonUpdate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textName = itemView.findViewById(R.id.textName);
            textCorreo = itemView.findViewById(R.id.textCorreo);
            textRol = itemView.findViewById(R.id.textRol);

            buttonDelete = itemView.findViewById(R.id.buttonDelete);
            buttonUpdate = itemView.findViewById(R.id.buttonUpdate);
        }
    }

    public class ViewDialogUpdate {
        public void showDialog(Context context, String name, String correo, String rol) {
            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.alert_dialog_update_user);

            EditText textName = dialog.findViewById(R.id.textName);
            EditText textCorreo = dialog.findViewById(R.id.textCorreo);
            EditText textRol = dialog.findViewById(R.id.textRol);

            textName.setText(name);
            textCorreo.setText(correo);
            textRol.setText(rol);


            Button buttonUpdate = dialog.findViewById(R.id.buttonAdd);
            Button buttonCancel = dialog.findViewById(R.id.buttonCancel);

            buttonUpdate.setText("ACTUALIZAR");

            buttonCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            buttonUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String newName = textName.getText().toString();
                    String newCorreo = textCorreo.getText().toString();
                    String newRol = textRol.getText().toString();

                    if (newName.isEmpty() || newCorreo.isEmpty() || newRol.isEmpty()) {
                        Toast.makeText(context, "Por favor, ingresar todos los datos...", Toast.LENGTH_SHORT).show();
                    } else {

                        if (newName.equals(name) && newCorreo.equals(correo) && newRol.equals(rol)) {
                            Toast.makeText(context, "Ningún dato ha cambiado", Toast.LENGTH_SHORT).show();
                        } else {

                            DatabaseReference currentUserReference = databaseReference.child("USERS");

                            currentUserReference.child(newName).setValue(new UsersItem(newName, newCorreo, newRol))
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            currentUserReference.child(name).removeValue()
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Toast.makeText(context, "Contacto actualizado exitosamente!", Toast.LENGTH_SHORT).show();
                                                            dialog.dismiss();
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(context, "Error al eliminar el contacto anterior: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                            dialog.dismiss();
                                                        }
                                                    });
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(context, "Error al actualizar el contacto: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            dialog.dismiss();
                                        }
                                    });
                        }


                    }
                }
            });

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();

        }
    }


    public class ViewDialogConfirmDelete {
        public void showDialog(Context context, String name) {
            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.view_dialog_confirm_delete);

            Button buttonDelete = dialog.findViewById(R.id.buttonDelete);
            Button buttonCancel = dialog.findViewById(R.id.buttonCancel);

            buttonCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            buttonDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    databaseReference.child("USERS").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).child(name).removeValue()
                    databaseReference.child("USERS").child(name).removeValue()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(context, "Información borrada exitosamente", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Handle failure
                                    Toast.makeText(context, "Eliminación falló: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }
                            });
                    dialog.dismiss();

                }
            });

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();

        }
    }
}
