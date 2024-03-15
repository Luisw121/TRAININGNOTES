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

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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
    private TextView edadTextView;
    private static final String TAG = "signInFragment";

    private List<String> edades = Arrays.asList("5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40");

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

        Button cerrarSessionButton = view.findViewById(R.id.buttoncerrarsesion);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

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
        }

        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);

        //Eliminamos la cuenta
        Button eliminarCUenta = view.findViewById(R.id.buttoneliminarcuenta);
        eliminarCUenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
// Botón para seleccionar la edad
        Button edadButton = view.findViewById(R.id.button2);
        edadTextView = view.findViewById(R.id.edad);
        edadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarSelectorDeEdad();
            }
        });
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
    }



    private void mostrarSelectorDeEdad() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Selecciona tu edad");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, edades);

        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String edadSeleccionada = edades.get(which);
                // Llama a la función para guardar la edad seleccionada en Firebase
                guardarEdadSeleccionadaEnFirebase(edadSeleccionada);
            }
        });

        builder.show();
    }

    private void guardarEdadSeleccionadaEnFirebase(String edadSeleccionada) {
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
                            userRef.update("edad", edadSeleccionada)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // La edad se guardó correctamente en Firestore
                                            // Puedes hacer cualquier acción adicional aquí si es necesario
                                            Log.d(TAG, "Edad guardada correctamente en Firestore");
                                            // Actualizar el TextView con la nueva edad
                                            edadTextView.setText(edadSeleccionada);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Hubo un error al guardar la edad en Firestore
                                            Log.e(TAG, "Error al guardar la edad en Firestore: " + e.getMessage());
                                        }
                                    });
                        } else {
                            // El documento del usuario no existe, crearlo con la edad seleccionada
                            Map<String, Object> userData = new HashMap<>();
                            userData.put("edad", edadSeleccionada);
                            userRef.set(userData)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // El documento del usuario se creó correctamente
                                            Log.d(TAG, "Documento del usuario creado correctamente en Firestore");
                                            // Actualizar el TextView con la nueva edad
                                            edadTextView.setText(edadSeleccionada);
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

