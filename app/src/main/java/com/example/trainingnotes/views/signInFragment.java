package com.example.trainingnotes.views;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trainingnotes.R;
import com.example.trainingnotes.viewmodel.AuthViewModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;

public class signInFragment extends Fragment {
    private NavController navController;
    private EditText emailEdit, passEdit;
    private TextView signUpText;
    private Button signInBtn;
    private SignInButton googleSignInButton;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private ProgressBar signInProgressBar;
    private ConstraintLayout signInForm;
    private FirebaseAuth mAuth;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);
        signUpText = view.findViewById(R.id.signUpText);
        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(requireView());

        emailEdit = view.findViewById(R.id.emailEditSignIn);
        passEdit = view.findViewById(R.id.passEditSignIn);
        signUpText = view.findViewById(R.id.signUpText);
        signInBtn = view.findViewById(R.id.signInBtn);
        signInForm = view.findViewById(R.id.signInForm);
        signInProgressBar = view.findViewById(R.id.signInProgressBar);
        googleSignInButton = view.findViewById(R.id.googleSignInButton);
        signUpText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_signInFragment_to_signUpFragment);
            }
        });



            signInBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    accederConEmail();
                }
            });

        mAuth = FirebaseAuth.getInstance();

        googleSignInButton = view.findViewById(R.id.googleSignInButton);
        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            try {
                                firebaseAuthWithGoogle(GoogleSignIn.getSignedInAccountFromIntent(data
                                ).getResult(ApiException.class));
                            } catch (ApiException e) {
                                Log.e("ABCD", "signInResult:failed code=" +
                                        e.getStatusCode());
                            }
                        }
                    }
                });
        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                accederConGoogle();
            }
        });
    }
    private void accederConGoogle() {
        GoogleSignInClient googleSignInClient =
                GoogleSignIn.getClient(requireActivity(), new
                        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build());
        activityResultLauncher.launch(googleSignInClient.getSignInIntent());
    }

    private void accederConEmail() {
        signInForm.setVisibility(View.GONE);
        signInProgressBar.setVisibility(View.VISIBLE);

        String email = emailEdit.getText().toString();
        String password = passEdit.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(requireContext(), "Por favor, complete todos los campos.", Toast.LENGTH_SHORT).show();
            signInForm.setVisibility(View.VISIBLE);
            signInProgressBar.setVisibility(View.GONE);
            return;
        }

        signInProgressBar.setVisibility(View.VISIBLE);
        signInForm.setVisibility(View.GONE);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            // Asignar el nombre de usuario como el correo electrónico
                            String displayName = email;
                            // Establecer la foto de perfil predeterminada desde los recursos locales
                            Uri photoUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                                    "://" + getResources().getResourcePackageName(R.drawable.perfilimage)
                                    + '/' + getResources().getResourceTypeName(R.drawable.perfilimage)
                                    + '/' + getResources().getResourceEntryName(R.drawable.perfilimage));

                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(displayName)
                                    .setPhotoUri(photoUri)
                                    .build();

                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (!task.isSuccessful()) {
                                                Snackbar.make(requireView(), "Error al actualizar el perfil: " + task.getException().getMessage(), Snackbar.LENGTH_LONG).show();
                                                signInForm.setVisibility(View.VISIBLE);
                                            }
                                        }
                                    });
                            navController.navigate(R.id.pantallaPrincipalFragment);
                        } else {
                            Toast.makeText(requireContext(), "Contraseña incorrecta", Toast.LENGTH_SHORT).show();
                            signInForm.setVisibility(View.VISIBLE);

                        }
                    }
                });



        mAuth.signInWithEmailAndPassword(emailEdit.getText().toString(), passEdit.getText().toString())
                .addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //actualizarUI(mAuth.getCurrentUser());
                        } else {
                            Snackbar.make(requireView(), "Error: Contraseña incorrecta", Snackbar.LENGTH_LONG).show();
                        }
                        signInForm.setVisibility(View.VISIBLE);
                        signInProgressBar.setVisibility(View.GONE);
                    }
                });

    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        if(acct == null) return;
        signInProgressBar.setVisibility(View.VISIBLE);
        signInForm.setVisibility(View.GONE);
        mAuth.signInWithCredential(GoogleAuthProvider.getCredential(acct.getIdToken(), null))
                .addOnCompleteListener(requireActivity(), new
                        OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Log.e("ABCD", "signInWithCredential:success");
                                    actualizarUI(mAuth.getCurrentUser());
                                } else {
                                    Log.e("ABCD", "signInWithCredential:failure",
                                            task.getException());
                                    signInProgressBar.setVisibility(View.GONE);
                                    signInForm.setVisibility(View.VISIBLE);
                                }
                            }
                        });
    }


    private void actualizarUI(FirebaseUser currentUser) {
        if(currentUser != null){
            // Crear opciones de navegación para agregar el fragmento al back stack
            NavOptions.Builder navBuilder = new NavOptions.Builder();
            navBuilder.setEnterAnim(R.anim.slide_in_right); // Animación de entrada
            navBuilder.setExitAnim(R.anim.fade_out); // Animación de salida
            navBuilder.setPopEnterAnim(R.anim.fade_in); // Animación de entrada al retroceder
            navBuilder.setPopExitAnim(R.anim.slide_out_right); // Animación de salida al retroceder
            NavOptions options = navBuilder.build();

            // Navegar al destino con las opciones creadas
            navController.navigate(R.id.action_signInFragment_to_pantallaPrincipalFragment, null, options);
        }
    }

}