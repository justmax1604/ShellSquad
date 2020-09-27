package app.example.shellhacks;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignIn extends AppCompatActivity{
    SignInButton signInButton;
    GoogleSignInClient mGoogleSignInClient;
    String firebaseUserItemsCollectionName = "userItems";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.signin);
        signInButton = findViewById(R.id.sign_in_button);

        Log.d("Signin", "Client ID: " + getString(R.string.default_web_client_id));

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        signInButton.setOnClickListener((v) -> {
            signIn();
        });
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == 1) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void setupDatabase(String id) {
        userData.getInstance().userId = id;
        dataBase.getInstance().setDb(FirebaseFirestore.getInstance());
        dataBase.getInstance().setUserDocument(FirebaseFirestore.getInstance().collection("users").document(id));
        dataBase.getInstance().setUserItems(dataBase.getInstance().getUserDocument().collection("userItems"));
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            Log.d("SignIn", "Singed In successfully");
            Log.d("SignIn", "firebaseAuthWithGoogle:" + account.getId());
            setupDatabase(account.getId());
            // Signed in successfully, show authenticated UI.
            startActivity(new Intent(SignIn.this, MainActivity.class));
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.e("Google Sign In Error", "signInResult:failed code=" + e.getStatusCode());
            Toast.makeText(SignIn.this, "Failed Error Code: " + e.getStatusCode(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onStart() {
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            Log.d("SignIn", "firebaseAuthWithGoogle:" + account.getId()); // 108768014883478741352
            setupDatabase(account.getId());
            startActivity(new Intent(SignIn.this, MainActivity.class));
            super.onStart();
        }
        else{
            Log.e("SignIn", "Is Null");
            super.onStart();
        }

    }
}

