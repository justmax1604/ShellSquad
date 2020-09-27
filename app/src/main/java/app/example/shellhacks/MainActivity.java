package app.example.shellhacks;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.List;

public class MainActivity extends AppCompatActivity {

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

    private FoodItemAdapter foodItemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnAdd = findViewById(R.id.btnAdd);
        edItem = findViewById(R.id.edItem);


        setUpRecyclerView();
        /***
        dataBase.getInstance().userItems
                .whereEqualTo(userIdInFireStore, userData.getInstance().userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.e(TAG, document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
        loadItems();


        ItemsAdapter.OnLongClickListener onLongClickListener = new ItemsAdapter.OnLongClickListener() {

            @Override
            public void onItemLongClicked(int position) {
                // Delete the item from the model
                items.remove(position);
                // Notify the adapter
                itemsAdapter.notifyItemRemoved(position);
                Toast.makeText(getApplicationContext(), "Item was removed", Toast.LENGTH_SHORT).show();
                saveItems();
            }
        };
        ItemsAdapter.OnClickListener onClickListener = new ItemsAdapter.OnClickListener(){

            @Override
            public void onItemClicked(int position) {
                Log.d("Main Activity", "Single click");
                EditDialog dialogFragment = new EditDialog();
                Bundle bundle = new Bundle();
                bundle.putString("item_text",items.get(position));
                bundle.putInt("item_position",position);
                dialogFragment.setArguments(bundle);
                dialogFragment.show(getSupportFragmentManager(),"Item Dialog");
            }
        };
        itemsAdapter = new ItemsAdapter(items, onLongClickListener,onClickListener);
        rvItems.setAdapter(itemsAdapter);
        rvItems.setLayoutManager(new LinearLayoutManager(this));

        ***/
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Add item to the model
                saveFoodItem();

            }
        });
        floatingAddButton = (FloatingActionButton)findViewById(R.id.floatingAddButton);
        floatingAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, BarcodeScan.class);
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(MainActivity.this).toBundle());
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

    // TODO: Change the query to only find foods for the logged in user
    private void setUpRecyclerView() {
        Query query = collectionReference.orderBy("item_name", Query.Direction.DESCENDING);
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
    /***
    private File getDataFile() {
        return new File(getFilesDir(), "data.txt");
    }

    // this function will load items by reloading every line of the data file
    private void loadItems() {
        try {
            items = new ArrayList<>(FileUtils.readLines(getDataFile(), Charset.defaultCharset()));
        } catch (IOException e) {
            Log.e("MainActivity", "Error reading items", e);
            items = new ArrayList<>();
        }

    }

    private void saveItems() {
        try {
            FileUtils.writeLines(getDataFile(),items);
        } catch (IOException e) {
            Log.e("MainActivity", "Error writing items", e);
        }
    }

    // to handle the result of edit activity
    @Override
    public void onFinishEditDialog(String item_txt,int position) {
        //update the model at the right position with new item text
        items.set(position,item_txt);
        //notify the adapter
        itemsAdapter.notifyItemChanged(position);
        //persist the changes
        saveItems();
        Toast.makeText(getApplicationContext(), "Item updated successfully", Toast.LENGTH_SHORT ).show();
    }
    ***/
}