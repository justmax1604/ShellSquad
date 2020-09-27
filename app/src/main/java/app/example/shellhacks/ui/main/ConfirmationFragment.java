package app.example.shellhacks.ui.main;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Date;
import java.util.Objects;

import app.example.shellhacks.DateValidatorUsingDateFormat;
import app.example.shellhacks.FoodItem;
import app.example.shellhacks.MainActivity;
import app.example.shellhacks.R;
import app.example.shellhacks.dataBase;

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
        return inflater.inflate(R.layout.item_confirm_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        TextView itemNameView = Objects.requireNonNull(getView()).findViewById(R.id.confirm_name_label);
        TextView expDateView = getView().findViewById(R.id.confirm_date_label);

        itemNameView.setText(getString(R.string.confirm_item_name, itemName));
        expDateView.setText(getString(R.string.confirm_exp_date, DateValidatorUsingDateFormat.FormatDate(expirationDate)));

        Button confirmButton = getView().findViewById(R.id.confirmNewItem);
        confirmButton.setOnClickListener((v) -> {
            dataBase.getInstance().getUserItems().add(new FoodItem(itemName, expirationDate));

            Intent intent = new Intent(getActivity(), MainActivity.class);
            Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle();
            startActivity(intent, bundle);
        });
    }
}
