package com.example.trainingnotes;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.jetbrains.annotations.Nullable;

public class perfilFragment extends Fragment {
    private NavController navController;
    private ImageView fotoPerfilImageView;
    private TextView displayNameTextView;
    private AuthViewModel authViewModel;
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
                authViewModel.deleteAccount();
                navController.navigate(R.id.signInFragment);
            }
        });

    }
}
