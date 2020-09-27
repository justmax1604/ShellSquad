package app.example.shellhacks;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class dataBase {
    private static dataBase mInstance= null;

    // Access a Cloud Firestore instance from your Activity
    private FirebaseFirestore db;
    private CollectionReference userItems;
    private DocumentReference userDocument;
    protected dataBase(){}

    public static synchronized dataBase getInstance() {
        if(null == mInstance){
            mInstance = new dataBase();
        }
        return mInstance;
    }

    public void setDb(FirebaseFirestore firebaseFirestore) {db = firebaseFirestore;}
    public void setUserItems(CollectionReference collectionReference) { userItems = collectionReference; }
    public void setUserDocument(DocumentReference documentReference) {userDocument = documentReference;}

    public FirebaseFirestore getDB() { return db; }
    public CollectionReference getUserItems() { return userItems; }
    public DocumentReference getUserDocument() { return userDocument; }
}
