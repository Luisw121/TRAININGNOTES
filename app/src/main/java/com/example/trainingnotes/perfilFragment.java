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

    private List<String> edades = Arrays.asList("5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40");
    private List<String> alturas = Arrays.asList("1.30m","1.31m","1.32m","1.33m","1.34m", "1.35m","1.36m","1.37m", "1.38m", "1.39m", "1.40m","1.41m", "1.45m", "1.46m", "1.47m", "1.48m", "1.49m", "1.50m", "1.51m", "1.52m", "1.53m", "1.54m", "1.55m", "1.56m", "1.57m", "1.58m", "1.59m", "1.60m", "1.61m", "1.62m", "1.63m", "1.64m", "1.65m", "1.66m", "1.67m", "1.68m", "1.69m", "1.70m", "1.71m", "1.72m", "1.73m", "1.74m", "1.75m", "1.76m", "1.77m", "1.78m", "1.79m", "1.80m", "1.85m", "1.86m", "1.87m", "1.88m", "1.89m", "1.90m", "1.91m", "1.92m", "1.93m", "1.94m", "1.95m", "1.96m", "1.97m", "1.98m", "1.99m", "2.00m", "2.01m", "2.02m", "2.03m", "2.04m", "2.05m", "2.06m", "2.07m", "2.08m", "2.09m", "2.10m", "2.11m", "2.12m", "2.13m", "2.14m", "2.15m", "2.16m", "2.17m", "2.18m", "2.19m", "2.20m", "2.21m", "2.22m", "2.23m", "2.24m", "2.25m", "2.26m", "2.27m", "2.28m", "2.29m", "2.30m");
    private List<String> pesos = Arrays.asList("30kg", "31kg", "32kg", "33kg", "34kg", "35kg", "36kg", "37kg", "38kg", "39kg", "40kg", "41kg", "42kg", "43kg", "44kg", "45kg", "46kg", "47kg", "48kg", "49kg", "50kg", "51kg", "52kg", "53kg", "54kg", "55kg", "56kg", "57kg", "58kg", "59kg", "60kg", "61kg", "62kg", "63kg", "64kg", "65kg", "66kg", "67kg", "68kg", "69kg", "70kg", "71kg", "72kg", "73kg", "74kg", "75kg", "76kg", "77kg", "78kg", "79kg", "80kg", "81kg", "82kg", "83kg", "84kg", "85kg", "86kg", "87kg", "88kg", "89kg", "90kg", "91kg", "92kg", "93kg", "94kg", "95kg", "96kg", "97kg", "98kg", "99kg", "100kg", "101kg", "102kg", "103kg", "104kg", "105kg", "106kg", "107kg", "108kg", "109kg", "110kg", "111kg", "112kg", "113kg", "114kg", "115kg", "116kg", "117kg", "118kg", "119kg", "120kg", "121kg", "122kg", "123kg", "124kg", "125kg", "126kg", "127kg", "128kg", "129kg", "130kg", "131kg", "132kg", "133kg", "134kg", "135kg", "136kg", "137kg", "138kg", "139kg", "140kg", "141kg", "142kg", "143kg", "144kg", "145kg", "146kg", "147kg", "148kg", "149kg", "150kg", "151kg", "152kg", "153kg", "154kg", "155kg", "156kg", "157kg", "158kg", "159kg", "160kg", "161kg", "162kg", "163kg", "164kg", "165kg", "166kg", "167kg", "168kg", "169kg", "170kg", "171kg", "172kg", "173kg", "174kg", "175kg", "176kg", "177kg", "178kg", "179kg", "180kg", "181kg", "182kg", "183kg", "184kg", "185kg", "186kg", "187kg", "188kg", "189kg", "190kg", "191kg", "192kg", "193kg", "194kg", "195kg", "196kg", "197kg", "198kg", "199kg", "200kg", "201kg", "202kg", "203kg", "204kg", "205kg", "206kg", "207kg", "208kg", "209kg", "210kg", "211kg", "212kg", "213kg", "214kg", "215kg", "216kg", "217kg", "218kg", "219kg", "220kg", "221kg", "222kg", "223kg", "224kg", "225kg", "226kg", "227kg", "228kg", "229kg", "230kg", "231kg", "232kg", "233kg", "234kg", "235kg", "236kg", "237kg", "238kg", "239kg", "240kg", "241kg", "242kg", "243kg", "244kg", "245kg", "246kg", "247kg", "248kg", "249kg", "250kg");
    private List<String> generos = Arrays.asList("Masculino", "Femenino");
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
        builder.setTitle("Selecciona tu peso");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, pesos);

        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String pesoSeleccionado = pesos.get(which);
                // Llama a la función para guardar el peso seleccionado en Firebase
                guardarPesoSeleccionadoEnFirebase(pesoSeleccionado);
            }
        });

        builder.show();
    }

    private void guardarPesoSeleccionadoEnFirebase(String pesoSeleccionado) {
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
                            userRef.update("peso", pesoSeleccionado)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // La edad se guardó correctamente en Firestore
                                            // Puedes hacer cualquier acción adicional aquí si es necesario
                                            Log.d(TAG, "Peso guardada correctamente en Firestore");
                                            // Actualizar el TextView con la nueva edad
                                            pesoTextView.setText(pesoSeleccionado);
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
                            userData.put("peso", pesoSeleccionado);
                            userRef.set(userData)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // El documento del usuario se creó correctamente
                                            Log.d(TAG, "Documento del usuario creado correctamente en Firestore");
                                            // Actualizar el TextView con la nueva edad
                                            pesoTextView.setText(pesoSeleccionado);
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



    //ALTURA
    private void mostrarSelectorDeAltura() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Selecciona tu altura");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, alturas);

        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String alturaSeleccionada = alturas.get(which);

                guardarAlturaSeleccionadaEnFirebase(alturaSeleccionada);
            }
        });
        builder.show();
    }

    private void guardarAlturaSeleccionadaEnFirebase(String alturaSeleccionada) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uidUsuario = user.getUid();

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference userRef = db.collection("users").document(uidUsuario);

            userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            userRef.update("altura", alturaSeleccionada)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "Altura generada correctamente en FIrestore");
                                    alturaTextView.setText(alturaSeleccionada);
                                    //obtenerYMostrarAlturaDesdeFirestore();
                                }
                            })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e(TAG, "Error al guardar la altura en Firestore: " + e.getMessage());
                                        }
                                    });
                        }else {
                            Map<String, Object> userData = new HashMap<>();
                            userData.put("altura", alturaSeleccionada);
                            userRef.set(userData)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "Documento del usuario creado correctametne en FIrestore");
                                            alturaTextView.setText(alturaSeleccionada);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e(TAG, "Error al crear el documento del usuario en Firestore: " + e.getMessage());
                                        }
                                    });
                        }
                    }else {
                        Log.e(TAG, "Error al verificar la existencia del documento del usuario en Firestore: " + task.getException().getMessage());                    }
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

