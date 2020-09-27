package app.example.shellhacks;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.SetOptions;


import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import app.example.shellhacks.ui.main.BarcodeScanFragment;
import app.example.shellhacks.ui.main.CameraFragment;

public class MainActivity extends AppCompatActivity implements EditDialog.EditItemDialogListener {

    List<String> items;

    Button btnAdd;
    EditText edItem;
    RecyclerView rvItems;
    ItemsAdapter itemsAdapter;
    FloatingActionButton floatingAddButton;
    String TAG = "MainActivity";
    String userIdInFireStore = "user_id";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("userItems");

    public static final String KEY_ITEM_NAME = "item_name";
    public static final String KEY_EXP_DATE = "expiration_date";

    private FoodItemAdapter foodItemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnAdd = findViewById(R.id.btnAdd);
        edItem = findViewById(R.id.edItem);


        setUpRecyclerView();

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Add item to the model
                saveFoodItem();

            }
        });

    }

    private void saveFoodItem() {
        String fooditemwithdate = edItem.getText().toString().trim();
        String[] fooditems = fooditemwithdate.split(",", 0);
        collectionReference.add(new FoodItem(fooditems[0],fooditems[1]));
        edItem.getText().clear();
        Toast.makeText(getApplicationContext(), "Item was added", Toast.LENGTH_SHORT).show();


    }

    private void setUpRecyclerView() {
        Query query = collectionReference.orderBy("expiration_date", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<FoodItem> options = new FirestoreRecyclerOptions.Builder<FoodItem>()
                .setQuery(query,FoodItem.class)
                .build();

        foodItemAdapter = new FoodItemAdapter(options);
        RecyclerView rvItems = findViewById(R.id.rvItems);
        rvItems.setHasFixedSize(true);

        rvItems.setLayoutManager(new LinearLayoutManager(this));
        rvItems.setAdapter(foodItemAdapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                foodItemAdapter.deleteItem(viewHolder.getAdapterPosition());

            }
        }).attachToRecyclerView(rvItems);

        foodItemAdapter.setOnItemClickListener(new FoodItemAdapter.onItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                FoodItem foodItem = documentSnapshot.toObject(FoodItem.class);
                String path = documentSnapshot.getReference().getPath();

                Log.d("Main Activity", "Single click");
                EditDialog dialogFragment = new EditDialog();
                Bundle bundle = new Bundle();
                bundle.putString("item_name",foodItem.getItem_name());
                bundle.putString("expiration_date",foodItem.getExpiration_date());
                bundle.putString("path",path);
                dialogFragment.setArguments(bundle);
                dialogFragment.show(getSupportFragmentManager(),"Item Dialog");
            }
        });
    }



    @Override
    protected void onStart() {
        super.onStart();
        foodItemAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        foodItemAdapter.stopListening();
    }

    // to handle the result of edit activity
    @Override
    public void onFinishEditDialog(String item_name, String expiration_date, String path) {
        //update the model at the right position with new item text
        Toast.makeText(getApplicationContext(), path, Toast.LENGTH_SHORT ).show();
        String documentP = (path.split("/",2))[1];
        if (!item_name.trim().isEmpty()) {
            collectionReference.document(documentP).update(KEY_ITEM_NAME,item_name);
        }
        if (!expiration_date.trim().isEmpty()) {
            collectionReference.document(documentP).update(KEY_EXP_DATE,expiration_date);
        }

        Toast.makeText(getApplicationContext(), "Item updated successfully", Toast.LENGTH_SHORT ).show();
    }

}