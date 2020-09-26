package app.example.shellhacks;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class EditDialog extends DialogFragment {

    private EditText etItem;
    private Button btnSave;
    //interface to be implemented in MainActivity
    public interface EditItemDialogListener {
        void onFinishEditDialog(String item_txt, int position);
    }

    private EditItemDialogListener listener;

    //inflate the layout here
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.edit_fragment, container);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etItem = view.findViewById(R.id.etItem);
        btnSave = view.findViewById(R.id.btnSave);

        //get the bundle from MainActivity with item text and position
        final Bundle bundle = getArguments();
        //set hint with the item that will be edited
        etItem.setHint(bundle.getString("item_text"));

        //set onClickListener to pass the data back to MainActivity and close the dialog
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditItemDialogListener activity = (EditItemDialogListener) getActivity();
                activity.onFinishEditDialog(etItem.getText().toString(), bundle.getInt("item_position"));
                dismiss();

            }
        });


    }


}
