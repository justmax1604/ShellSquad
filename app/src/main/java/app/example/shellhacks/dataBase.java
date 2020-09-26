package app.example.shellhacks;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class dataBase {
    private static dataBase mInstance= null;

    // Access a Cloud Firestore instance from your Activity
    public FirebaseFirestore db;
    public CollectionReference userItems;
    protected dataBase(){}

    public static synchronized dataBase getInstance() {
        if(null == mInstance){
            mInstance = new dataBase();
        }
        return mInstance;
    }
}
