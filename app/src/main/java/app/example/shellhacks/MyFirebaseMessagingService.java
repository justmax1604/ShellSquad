package app.example.shellhacks;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.SetOptions;
import com.google.firebase.messaging.FirebaseMessagingService;

import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String token) {
        Log.d(TAG, "Refreshed token: " + token);

        sendRegistrationToServer(token);
    }

    private void sendRegistrationToServer(String token) {
        Map<String, String> regToken = new HashMap<>();
        regToken.put("registrationToken", token);
        if (dataBase.getInstance().getUserDocument() == null)
            return;
        dataBase.getInstance().getUserDocument().set(regToken, SetOptions.merge())
                .addOnSuccessListener((aVoid) -> {
                    Log.d(TAG, "Successfully updated registrationToken");
                })
                .addOnFailureListener((e) -> {
                    Log.d(TAG, "Error updating document", e);
                });
    }
}
