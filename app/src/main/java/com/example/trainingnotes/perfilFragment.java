package com.example.trainingnotes;

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
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.Nullable;

public class perfilFragment extends Fragment {
    private NavController navController;
    private ImageView fotoPerfilImageView;
    private TextView displayNameTextView;
    private AuthViewModel authViewModel;
    private static final String TAG = "signInFragment";
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

        navController = Navigation.findNavController(view);

        Button cerrarSessionButton = view.findViewById(R.id.buttoncerrarsesion);

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

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            displayNameTextView.setText(user.getDisplayName());
            Glide.with(requireView()).load(user.getPhotoUrl())//obtenemos la imagen del usuario
                    .circleCrop()//para redondear la iamgen del perfil
                    .into(fotoPerfilImageView);
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



    }
}
