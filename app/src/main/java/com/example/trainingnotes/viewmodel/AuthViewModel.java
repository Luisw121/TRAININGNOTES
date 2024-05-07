package com.example.trainingnotes.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.navigation.NavController;

import com.example.trainingnotes.R;
import com.example.trainingnotes.repository.AutenticationRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthViewModel extends AndroidViewModel {
    private AutenticationRepository repository;
    private MutableLiveData<FirebaseUser> userData;
    private MutableLiveData<Boolean> loggedStatus;
    private MutableLiveData<String> userName;
    private FirebaseAuth mAuth;
    private NavController navController;
    public LiveData<String> getUserName() {
        return userName;
    }

    public MutableLiveData<FirebaseUser> getUserData() {
        return userData;
    }

    public MutableLiveData<Boolean> getLoggedStatus() {
        return loggedStatus;
    }

    public AuthViewModel(@NonNull  Application application) {
        super(application);
        repository = new AutenticationRepository(application);
        userData = repository.getFirebaseUserMutableLiveData();
        loggedStatus = repository.getUserLoggedMutableLiveData();
    }

    public void register(String email , String pass){
        repository.register(email, pass);
    }


    public void signIn(String email , String pass){
        repository.login(email, pass);
    }
    public void signOut(){
        repository.signOut();
    }
    public void setUserName(String name) {
        userName.setValue(name);
    }

    private MutableLiveData<Boolean> navigateToSignInFragment = new MutableLiveData<>();

    public LiveData<Boolean> getNavigateToSignInFragment() {
        return navigateToSignInFragment;
    }

    public void deleteAccount() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            user.delete()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            userData.setValue(null);
                            loggedStatus.setValue(false);
                            navigateToSignInFragment.setValue(true); // Notificar al fragmento para navegar
                        } else {
                            System.out.println("Error: No se pudo eliminar la cuenta");
                        }
                    });
        }
    }

}
