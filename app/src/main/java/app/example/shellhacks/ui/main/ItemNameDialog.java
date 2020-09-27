package app.example.shellhacks.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.textfield.TextInputEditText;

import app.example.shellhacks.R;

public class ItemNameDialog extends DialogFragment {

    public static final int NEW_ITEM = 14;
    public static final int MANUAL = 13;

    ItemNameDialogCallback onConfirm;
    ItemNameDialogCallback onCancel;

    private TextInputEditText inputField;
    private int dialogType;

    public ItemNameDialog(ItemNameDialogCallback confirmCallback, ItemNameDialogCallback cancelCallback, int dialogType) {
        super();
        onConfirm = confirmCallback;
        this.dialogType = dialogType;
        onCancel = cancelCallback;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.item_name_dialog, container, false);

        TextView instructions = view.findViewById(R.id.item_dialog_instructions);
        if (dialogType == NEW_ITEM) {
            String extra = "We could not find that code in our database. ";
            extra += instructions.getText().toString();
            instructions.setText(extra);
        }

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        inputField = getView().findViewById(R.id.item_name_field);

        Button confirmButton = getView().findViewById(R.id.name_dialog_confirm);
        confirmButton.setOnClickListener((View v) -> {
            if (inputField.getText().length() == 0)
                return;
            onConfirm.onFinish(inputField.getText().toString());
            dismiss();
        });

        Button cancelButton = getView().findViewById(R.id.name_dialog_cancel);
        cancelButton.setOnClickListener((v) -> {
            onCancel.onFinish(inputField.getText().toString());
            dismiss();
        });

    }
}
