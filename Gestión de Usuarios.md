# Gestión de Usuarios
...
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
                    DatabaseReference userReference = databaseReference.child("USERS").child(name);
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
...

Este README proporciona una visión general del código de la clase `GestionUsuarios`, que se encarga de la gestión de usuarios en una aplicación Android. Esta clase utiliza Firebase Authentication y Firebase Realtime Database para administrar y mostrar información de usuarios.

## Descripción General

La clase `GestionUsuarios` es una actividad de Android que permite realizar las siguientes acciones:

1. Mostrar una lista de usuarios en un `RecyclerView`.
2. Agregar nuevos usuarios a la lista.
3. Cerrar sesión de un usuario.
4. Registrar nuevos usuarios en Firebase Authentication y almacenar sus datos en Firebase Realtime Database.

## Componentes Principales

### Variables miembro

- `databaseReference`: Una referencia a Firebase Realtime Database.
- `recyclerView`: Un `RecyclerView` para mostrar la lista de usuarios.
- `usersItemArrayList`: Una lista de objetos `UsersItem` para almacenar los datos de los usuarios.
- `adapter`: Un adaptador personalizado para vincular los datos a la vista del `RecyclerView`.
- `buttonAdd` y `buttonLogout`: Botones para agregar usuarios y cerrar sesión.
- `mAuth`: Una instancia de Firebase Authentication para registrar nuevos usuarios y administrar la autenticación.
- `context`: El contexto de la actividad.

### Métodos Principales

#### `onCreate`

Este método se llama cuando la actividad se crea. Aquí se inicializan las vistas, se configuran los botones y se llama a `readData` para cargar la lista de usuarios existentes.

#### `readData`

Este método consulta Firebase Realtime Database para obtener la lista de usuarios y la muestra en el `RecyclerView` utilizando un adaptador personalizado.

#### `ViewDialogAdd`

Esta clase interna se utiliza para mostrar un cuadro de diálogo al usuario cuando se agrega un nuevo usuario. El cuadro de diálogo permite al usuario ingresar información como nombre, correo electrónico, contraseña y rol. Luego, se llama a `registerUser` para registrar al nuevo usuario en Firebase Authentication y almacenar los datos en Firebase Realtime Database.

#### `registerUser`

Este método se utiliza para registrar un nuevo usuario en Firebase Authentication y almacenar sus datos en Firebase Realtime Database. Si el registro es exitoso, se crea un nuevo usuario en la base de datos.

## Uso

Para utilizar esta clase en tu propia aplicación Android, sigue estos pasos:

1. Configura un proyecto en Firebase y habilita Firebase Authentication y Firebase Realtime Database.

2. Asegúrate de tener las dependencias necesarias en tu archivo `build.gradle` para Firebase.

3. Asegúrate de que la actividad `GestionUsuarios` esté declarada en tu archivo `AndroidManifest.xml`.

4. Personaliza la lógica según tus necesidades, como la forma en que se muestran los usuarios y cómo se gestionan los datos de usuario.

5. Asegúrate de que tu aplicación tenga los permisos necesarios para acceder a Internet si aún no lo has hecho.

6. Implementa la clase `UsersItem` según tus necesidades para representar los datos de usuario.

Este código proporciona una base sólida para desarrollar una funcionalidad de gestión de usuarios en tu aplicación Android utilizando Firebase. Asegúrate de comprender y personalizar el código según tus necesidades específicas.
