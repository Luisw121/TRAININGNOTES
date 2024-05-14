package com.example.trainingnotes;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthManager {
    private static AuthManager instance;
    private FirebaseAuth mAuth;

    private AuthManager() {
        mAuth = FirebaseAuth.getInstance();
    }

    public static AuthManager getInstance() {
        if (instance == null) {
            instance = new AuthManager();
        }
        return instance;
    }

    public FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }

    public boolean isUserLoggedIn() {
        return getCurrentUser() != null;
    }

    public void signOut() {
        mAuth.signOut();
    }
}

