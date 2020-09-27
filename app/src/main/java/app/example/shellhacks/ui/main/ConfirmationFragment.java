package app.example.shellhacks.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.time.format.DateTimeFormatter;
import java.util.Date;

import app.example.shellhacks.R;

public class ConfirmationFragment extends Fragment {

    private Date expirationDate;
    private String itemName;

    public ConfirmationFragment(String itemName, Date expirationDate) {
        super();
        this.expirationDate = expirationDate;
        this.itemName = itemName;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.item_confirm_fragment, container);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        TextView itemNameView = getView().findViewById(R.id.confirm_name_label);
        TextView expDateView = getView().findViewById(R.id.confirm_date_label);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

        itemNameView.setText(getString(R.string.confirm_item_name, itemName));
        expDateView.setText(getString(R.string.confirm_exp_date, formatter.format(expirationDate.toInstant())));

    }
}
