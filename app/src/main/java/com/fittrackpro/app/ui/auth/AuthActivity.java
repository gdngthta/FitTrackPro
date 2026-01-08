package com.fittrackpro. app.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.fittrackpro.app.R;
import com.fittrackpro.app.data.repository.AuthRepository;
import com. fittrackpro.app.ui.main.MainActivity;

/**
 * AuthActivity hosts login and registration fragments.
 */
public class AuthActivity extends AppCompatActivity {

    private AuthRepository authRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        authRepository = new AuthRepository();

        // Check if user is already logged in
        if (authRepository. isUserLoggedIn()) {
            navigateToMain();
        }
    }

    private void navigateToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}