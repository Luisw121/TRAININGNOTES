package com.example.trainingnotes.views;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.trainingnotes.MainActivity;
import com.example.trainingnotes.R;
import com.example.trainingnotes.viewmodel.AuthViewModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.List;

public class pantallaPrincipalFragment extends Fragment {

    private NavController navController;
    private TextView pesoTextView;
    private List<String> pesos = Arrays.asList("30kg", "31kg", "32kg", "33kg", "34kg", "35kg", "36kg", "37kg", "38kg", "39kg", "40kg", "41kg", "42kg", "43kg", "44kg", "45kg", "46kg", "47kg", "48kg", "49kg", "50kg", "51kg", "52kg", "53kg", "54kg", "55kg", "56kg", "57kg", "58kg", "59kg", "60kg", "61kg", "62kg", "63kg", "64kg", "65kg", "66kg", "67kg", "68kg", "69kg", "70kg", "71kg", "72kg", "73kg", "74kg", "75kg", "76kg", "77kg", "78kg", "79kg", "80kg", "81kg", "82kg", "83kg", "84kg", "85kg", "86kg", "87kg", "88kg", "89kg", "90kg", "91kg", "92kg", "93kg", "94kg", "95kg", "96kg", "97kg", "98kg", "99kg", "100kg", "101kg", "102kg", "103kg", "104kg", "105kg", "106kg", "107kg", "108kg", "109kg", "110kg", "111kg", "112kg", "113kg", "114kg", "115kg", "116kg", "117kg", "118kg", "119kg", "120kg", "121kg", "122kg", "123kg", "124kg", "125kg", "126kg", "127kg", "128kg", "129kg", "130kg", "131kg", "132kg", "133kg", "134kg", "135kg", "136kg", "137kg", "138kg", "139kg", "140kg", "141kg", "142kg", "143kg", "144kg", "145kg", "146kg", "147kg", "148kg", "149kg", "150kg", "151kg", "152kg", "153kg", "154kg", "155kg", "156kg", "157kg", "158kg", "159kg", "160kg", "161kg", "162kg", "163kg", "164kg", "165kg", "166kg", "167kg", "168kg", "169kg", "170kg", "171kg", "172kg", "173kg", "174kg", "175kg", "176kg", "177kg", "178kg", "179kg", "180kg", "181kg", "182kg", "183kg", "184kg", "185kg", "186kg", "187kg", "188kg", "189kg", "190kg", "191kg", "192kg", "193kg", "194kg", "195kg", "196kg", "197kg", "198kg", "199kg", "200kg", "201kg", "202kg", "203kg", "204kg", "205kg", "206kg", "207kg", "208kg", "209kg", "210kg", "211kg", "212kg", "213kg", "214kg", "215kg", "216kg", "217kg", "218kg", "219kg", "220kg", "221kg", "222kg", "223kg", "224kg", "225kg", "226kg", "227kg", "228kg", "229kg", "230kg", "231kg", "232kg", "233kg", "234kg", "235kg", "236kg", "237kg", "238kg", "239kg", "240kg", "241kg", "242kg", "243kg", "244kg", "245kg", "246kg", "247kg", "248kg", "249kg", "250kg");

    private static final String TAG = "signInFragment";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pantalla_principal, container, false);

        pesoTextView = requireActivity().findViewById(R.id.peso);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);

        ImageView perfil = view.findViewById(R.id.perfil);
        //displayNameTextVIew = view.findViewById(R.id.nombreperfil);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            //displayNameTextVIew.setText(user.getDisplayName());
            Glide.with(requireActivity()).load(user.getPhotoUrl())
                    .circleCrop()
                    .into(perfil);
        }
        perfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_pantallaPrincipalFragment_to_perfilFragment);
            }
        });
        ConstraintLayout entrenamiento = view.findViewById(R.id.buttonEntrenamiento);
        entrenamiento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_pantallaPrincipalFragment_to_entrenamientoFragment);
            }
        });
        ConstraintLayout buttonListaEjercicios = view.findViewById(R.id.buttonListaEjercicios);
        buttonListaEjercicios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_pantallaPrincipalFragment_to_listaEjerciciosFragment);
            }
        });

        ConstraintLayout pesoCorporalLayout = view.findViewById(R.id.pesoCorporalBottom);

        pesoCorporalLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarSelectorDePeso();
            }
        });
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

            userRef.update("peso", pesoSeleccionado)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(requireContext(), "Peso guardado correctamente", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "Error al guardar el peso en Firestore: " + e.getMessage());
                        }
                    });
        }
    }
}