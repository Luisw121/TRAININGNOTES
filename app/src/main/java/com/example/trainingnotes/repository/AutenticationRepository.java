package com.example.trainingnotes.repository;

import android.app.Application;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.trainingnotes.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

public class AutenticationRepository {
    private FirebaseAuth mAuth;
    private MutableLiveData<FirebaseUser> firebaseUserMutableLiveData;
    private MutableLiveData<Boolean> userLoggedMutableLiveData;

    public AutenticationRepository(Application application) {
        mAuth = FirebaseAuth.getInstance();
        firebaseUserMutableLiveData = new MutableLiveData<>();
        userLoggedMutableLiveData = new MutableLiveData<>();

        // Verificar si hay un usuario actualmente autenticado
        FirebaseUser currentUser = mAuth.getCurrentUser();
        firebaseUserMutableLiveData.setValue(currentUser);
        userLoggedMutableLiveData.setValue(currentUser != null);
    }

    public MutableLiveData<FirebaseUser> getFirebaseUserMutableLiveData() {
        return firebaseUserMutableLiveData;
    }

    public MutableLiveData<Boolean> getUserLoggedMutableLiveData() {
        return userLoggedMutableLiveData;
    }

    public void register(String email, String password, String name) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(name)
                                .build();

                        user.updateProfile(profileUpdates)
                                .addOnCompleteListener(updateTask -> {
                                    if (updateTask.isSuccessful()) {
                                        firebaseUserMutableLiveData.setValue(user);
                                        userLoggedMutableLiveData.setValue(true);
                                    } else {
                                        System.out.println("Error");
                                    }
                                });
                    } else {
                        System.out.println("Error");
                    }
                });
    }

    public void login(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        firebaseUserMutableLiveData.setValue(user);
                        userLoggedMutableLiveData.setValue(true);
                    } else {
                        firebaseUserMutableLiveData.setValue(null);
                        userLoggedMutableLiveData.setValue(false);
                    }
                });
    }

    public void signOut() {
        mAuth.signOut();
        firebaseUserMutableLiveData.setValue(null);
        userLoggedMutableLiveData.setValue(false);
    }
}
