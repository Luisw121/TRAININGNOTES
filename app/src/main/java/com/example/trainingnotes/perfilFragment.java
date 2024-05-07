package com.example.trainingnotes;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.trainingnotes.viewmodel.AuthViewModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.firebase.firestore.FirebaseFirestore;


public class perfilFragment extends Fragment {
    private NavController navController;
    private ImageView fotoPerfilImageView;
    private TextView displayNameTextView;
    private AuthViewModel authViewModel;
    private TextView edadTextView, alturaTextView, pesoTextView, genero, textViewNombreUsuario;
    private Button buttonCambiarNombre;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private static final String TAG = "signInFragment";
    private List<String> generos = Arrays.asList("Masculino", "Femenino", "Otros");
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);
        return view;
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(requireView());

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        Button cerrarSessionButton = view.findViewById(R.id.buttoncerrarsesion);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        alturaTextView = view.findViewById(R.id.altura);
        obtenerYMostrarAlturaDesdeFirestore();
        cerrarSessionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoogleSignIn.getClient(requireActivity(), new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .build()).signOut();

                Glide.get(requireContext()).clearMemory();

                FirebaseAuth.getInstance().signOut();

                Navigation.findNavController(view).navigate(R.id.signInFragment);
            }
        });
        fotoPerfilImageView = view.findViewById(R.id.imageView);
        displayNameTextView = view.findViewById(R.id.textView3);

        if (user != null) {
            displayNameTextView.setText(user.getDisplayName());
            Glide.with(requireView()).load(user.getPhotoUrl())//obtenemos la imagen del usuario
                    .circleCrop()//para redondear la iamgen del perfil
                    .into(fotoPerfilImageView);
            obtenerYMostrarEdadDesdeFirestore();
            obtenerYMostrarAlturaDesdeFirestore();
            obtenerYMostrarPesoDesdeFirestore();
            obtenerYMostrarGeneroDesdeFirestore();
        }
        obtenerYMostrarNombreUsuarioDesdeFirestore();

        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);

        //Eliminamos la cuenta
        Button eliminarCUenta = view.findViewById(R.id.buttoneliminarcuenta);
        eliminarCUenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crear un diálogo de confirmación
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setMessage("¿Estás seguro de que quieres eliminar tu cuenta?");
                builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Si el usuario confirma, procede con la eliminación de la cuenta
                        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                        if (currentUser != null) {
                            // Eliminar la cuenta del Firestore
                            FirebaseFirestore.getInstance().collection("users").document(currentUser.getUid())
                                    .delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // Eliminar la cuenta de autenticación
                                            currentUser.delete()
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            // Revocar el token de acceso de Google
                                                            GoogleSignIn.getClient(requireActivity(),
                                                                            new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                                                                    .build())
                                                                    .revokeAccess()
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            // Redirigir al usuario al fragmento de inicio de sesión
                                                                            navController.navigate(R.id.signInFragment);
                                                                        }
                                                                    });
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            // Manejar el fallo de eliminación de cuenta de autenticación
                                                            Log.e(TAG, "Error al eliminar la cuenta de autenticación: " + e.getMessage());
                                                        }
                                                    });
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Manejar el fallo de eliminación de cuenta del Firestore
                                            Log.e(TAG, "Error al eliminar la cuenta del Firestore: " + e.getMessage());
                                        }
                                    });
                        }
                    }
                });
                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Si el usuario cancela, no hacer nada
                    }
                });
                // Mostrar el diálogo de confirmación
                builder.create().show();
            }
        });

        // Botón para seleccionar la edad
        Button edadButton = view.findViewById(R.id.button2);
        edadTextView = view.findViewById(R.id.edad);
        edadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarSelectorDeEdad();
            }
        });
        //Bototones para los links de instagram i tik tok
        ImageView imageView2 = view.findViewById(R.id.imageView2);
        imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Uri uri = Uri.parse("https://www.tiktok.com/@trainingnotes");
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
            }
        });
        ImageView instagramicon = view.findViewById(R.id.instagramicon);
        instagramicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://www.instagram.com/trainingnotesapp/?next=%2F");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        //Boton para la altura
        Button alturaButton = view.findViewById(R.id.buttonaltura);
        alturaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarSelectorDeAltura();
            }
        });
        //Boton para el peso
        Button pesoButton = view.findViewById(R.id.buttoncambiopeso);
        pesoTextView = view.findViewById(R.id.peso);

        pesoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarSelectorDePeso();
            }
        });
        Button buttongenero = view.findViewById(R.id.buttongenero);
        genero = view.findViewById(R.id.genero);
        buttongenero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarSelectorDeGenero();
            }
        });

        textViewNombreUsuario = view.findViewById(R.id.textView3);
        buttonCambiarNombre = view.findViewById(R.id.buttonnameusuario);

        buttonCambiarNombre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDialogoCambiarNombre(); 
            }
        });

    }

    private void mostrarDialogoCambiarNombre() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Cambiar nombre de usuario");

        // Set up the input
        final EditText input = new EditText(getContext());
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String nuevoNombre = input.getText().toString();
                if (!nuevoNombre.isEmpty()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
                        // Verificar si el nombre de usuario ya está actualizado en Firestore
                        DocumentReference userRef = db.collection("users").document(user.getUid());
                        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        String nombreActual = document.getString("nombre");
                                        if (nombreActual != null && nombreActual.equals(nuevoNombre)) {
                                            Toast.makeText(getContext(), "El nombre de usuario ya está actualizado", Toast.LENGTH_SHORT).show();
                                        } else {
                                            // Actualizar el TextView con el nuevo nombre
                                            textViewNombreUsuario.setText(nuevoNombre);

                                            // Guardar el nuevo nombre en Firestore
                                            Map<String, Object> data = new HashMap<>();
                                            data.put("nombre", nuevoNombre);
                                            db.collection("users").document(user.getUid())
                                                    .update(data)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Toast.makeText(getContext(), "Nombre de usuario cambiado correctamente", Toast.LENGTH_SHORT).show();
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(getContext(), "Error al cambiar el nombre de usuario: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        }
                                    }
                                } else {
                                    Log.d(TAG, "Error al obtener el documento:", task.getException());
                                }
                            }
                        });
                    }
                }
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
    private void obtenerYMostrarNombreUsuarioDesdeFirestore() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            DocumentReference userRef = db.collection("users").document(user.getUid());
            userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String nombreUsuario = document.getString("nombre");
                            if (nombreUsuario != null) {
                                textViewNombreUsuario.setText(nombreUsuario);
                            }
                        }
                    } else {
                        Log.d(TAG, "Error al obtener el nombre de usuario:", task.getException());
                    }
                }
            });
        }
    }


    private void obtenerYMostrarGeneroDesdeFirestore() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uidUsuario = user.getUid();

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users").document(uidUsuario)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                String generos = documentSnapshot.getString("genero");
                                if (generos != null) {
                                    // Mostrar la edad en el TextView
                                    genero.setText(generos);
                                }
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "Error al obtener el genero desde Firestore: " + e.getMessage());
                        }
                    });
        }
    }


    private void mostrarSelectorDeGenero() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Selecciona tu genero");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, generos);

        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String generoSeleccionado = generos.get(which);

                guardarGeneroSeleccionadoEnFirebase(generoSeleccionado);
            }
        });
        builder.show();
    }

    private void guardarGeneroSeleccionadoEnFirebase(String generoSeleccionado) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uidUsuario = user.getUid();

            // Obtener la referencia al documento del usuario en Firestore
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference userRef = db.collection("users").document(uidUsuario);

            // Verificar si el documento del usuario ya existe
            userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // El documento del usuario ya existe, actualizar la edad
                            userRef.update("genero", generoSeleccionado)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // La edad se guardó correctamente en Firestore
                                            // Puedes hacer cualquier acción adicional aquí si es necesario
                                            Log.d(TAG, "Genero guardada correctamente en Firestore");
                                            // Actualizar el TextView con la nueva edad
                                            genero.setText(generoSeleccionado);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Hubo un error al guardar la edad en Firestore
                                            Log.e(TAG, "Error al guardar el genero en Firestore: " + e.getMessage());
                                        }
                                    });
                        } else {
                            // El documento del usuario no existe, crearlo con la edad seleccionada
                            Map<String, Object> userData = new HashMap<>();
                            userData.put("genero", generoSeleccionado);
                            userRef.set(userData)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // El documento del usuario se creó correctamente
                                            Log.d(TAG, "Documento del usuario creado correctamente en Firestore");
                                            // Actualizar el TextView con la nueva edad
                                            genero.setText(generoSeleccionado);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Hubo un error al crear el documento del usuario en Firestore
                                            Log.e(TAG, "Error al crear el documento del usuario en Firestore: " + e.getMessage());
                                        }
                                    });
                        }
                    } else {
                        // Hubo un error al verificar la existencia del documento del usuario
                        Log.e(TAG, "Error al verificar la existencia del documento del usuario en Firestore: " + task.getException().getMessage());
                    }
                }
            });
        }
    }

    private void obtenerYMostrarPesoDesdeFirestore() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uidUsuario = user.getUid();

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users").document(uidUsuario)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                String peso = documentSnapshot.getString("peso");
                                if (peso != null) {
                                    // Mostrar la edad en el TextView
                                    pesoTextView.setText(peso);
                                }
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "Error al obtener la edad desde Firestore: " + e.getMessage());
                        }
                    });
        }
    }


    private void mostrarSelectorDePeso() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Cambiar Peso");

        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        builder.setView(input);

        // Configurar botón de "Guardar"
        builder.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String pesoIngresado = input.getText().toString().trim();
                // Verificar si se ingresó un peso
                if (!pesoIngresado.isEmpty()) {
                    // Guardar el peso en Firebase
                    guardarPesoSeleccionadoEnFirebase(pesoIngresado);
                } else {
                    Toast.makeText(requireContext(), "Por favor, ingresa tu peso", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Configurar botón de "Cancelar"
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        // Mostrar el cuadro de diálogo
        builder.show();
    }

    private void guardarPesoSeleccionadoEnFirebase(String pesoSeleccionado) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uidUsuario = user.getUid();

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference userRef = db.collection("users").document(uidUsuario);

            // Guardar el peso en Firestore
            userRef
                    .update("peso", pesoSeleccionado)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Actualizar el TextView con el nuevo peso
                            pesoTextView.setText(pesoSeleccionado);
                            // Mostrar mensaje de éxito
                            Toast.makeText(requireContext(), "Peso guardado correctamente", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Manejar errores al guardar el peso en Firestore
                            Log.e(TAG, "Error al guardar el peso en Firestore: " + e.getMessage());
                            Toast.makeText(requireContext(), "Error al guardar el peso", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }



    //ALTURA
    private void mostrarSelectorDeAltura() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Introduce tu altura");

        final EditText input = new EditText(requireContext());
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        builder.setView(input);

        // Configurar botón de "Guardar"
        builder.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String alturaIngresada = input.getText().toString().trim();
                // Verificar si se ingresó una altura válida
                if (!alturaIngresada.isEmpty()) {
                    // Guardar la altura en Firebase
                    guardarAlturaSeleccionadaEnFirebase(alturaIngresada);
                } else {
                    Toast.makeText(requireContext(), "Por favor, ingresa tu altura", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Configurar botón de "Cancelar"
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        // Mostrar el cuadro de diálogo
        builder.show();
    }

    private void guardarAlturaSeleccionadaEnFirebase(String alturaSeleccionada) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uidUsuario = user.getUid();

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference userRef = db.collection("users").document(uidUsuario);

            userRef.update("altura", alturaSeleccionada)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "Altura guardada correctamente en Firestore");
                            alturaTextView.setText(alturaSeleccionada);
                            Toast.makeText(requireContext(), "Altura guardada correctamente", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "Error al guardar la altura en Firestore: " + e.getMessage());
                            Toast.makeText(requireContext(), "Error al guardar la altura", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void obtenerYMostrarAlturaDesdeFirestore() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uidUsuario = user.getUid();

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users").document(uidUsuario)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                String altura = documentSnapshot.getString("altura");
                                if (altura != null) {
                                    // Mostrar la altura en el TextView
                                    alturaTextView.setText(altura);
                                }
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "Error al obtener la altura desde Firestore: " + e.getMessage());
                        }
                    });
        }
    }

    //EDAD
    private void mostrarSelectorDeEdad() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Introduce tu edad");

        final EditText input = new EditText(requireContext());
        input.setInputType(InputType.TYPE_CLASS_NUMBER); // Permitir solo números
        builder.setView(input);

        // Configurar botón de "Guardar"
        builder.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String edadIngresada = input.getText().toString().trim();
                // Verificar si se ingresó una edad válida
                if (!edadIngresada.isEmpty()) {
                    // Guardar la edad en Firebase
                    guardarEdadSeleccionadaEnFirebase(edadIngresada);
                } else {
                    Toast.makeText(requireContext(), "Por favor, ingresa tu edad", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Configurar botón de "Cancelar"
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        // Mostrar el cuadro de diálogo
        builder.show();
    }

    private void guardarEdadSeleccionadaEnFirebase(String edadSeleccionada) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uidUsuario = user.getUid();

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference userRef = db.collection("users").document(uidUsuario);

            userRef.update("edad", edadSeleccionada)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "Edad guardada correctamente en Firestore");
                            edadTextView.setText(edadSeleccionada);
                            Toast.makeText(requireContext(), "Edad guardada correctamente", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "Error al guardar la edad en Firestore: " + e.getMessage());
                            Toast.makeText(requireContext(), "Error al guardar la edad", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void obtenerYMostrarEdadDesdeFirestore() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uidUsuario = user.getUid();

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users").document(uidUsuario)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                String edad = documentSnapshot.getString("edad");
                                if (edad != null) {
                                    // Mostrar la edad en el TextView
                                    edadTextView.setText(edad);
                                }
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "Error al obtener la edad desde Firestore: " + e.getMessage());
                        }
                    });
        }
    }

}






/*
TextView edadTextView = getView().findViewById(R.id.edad);
    edadTextView.setText(edadSeleccionada);
 */

