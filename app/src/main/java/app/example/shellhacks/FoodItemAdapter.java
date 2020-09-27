package app.example.shellhacks;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FoodItemAdapter extends FirestoreRecyclerAdapter<FoodItem,FoodItemAdapter.FoodItemHolder> {

    private onItemClickListener listener;

    public FoodItemAdapter(@NonNull FirestoreRecyclerOptions<FoodItem> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull FoodItemHolder holder, int position, @NonNull FoodItem model) {
        holder.itemName.setText(model.getItem_name());
        holder.itemDate.setText(model.getExpiration_date());
        //holder.imagePath.setImageIcon(R.drawable.ic_launcher_foreground);

    }

    @NonNull
    @Override
    public FoodItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_food,parent,false );
        return new FoodItemHolder(v);
    }

    public void deleteItem(int position) {
        getSnapshots().getSnapshot(position).getReference().delete();
    }

    class FoodItemHolder extends RecyclerView.ViewHolder {

        TextView itemName;
        TextView itemDate;
        //ImageView imagePath;
        public FoodItemHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.tvName);
            itemDate = itemView.findViewById(R.id.tvDate);
            //imagePath = itemView.findViewById(R.id.ivFooditem);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if ((position != RecyclerView.NO_POSITION) && (listener != null)){

                        listener.onItemClick(getSnapshots().getSnapshot(position),position);

                    }
                }
            });
        }
    }

    public interface onItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(onItemClickListener listener){
        this.listener = listener;
    }

}
