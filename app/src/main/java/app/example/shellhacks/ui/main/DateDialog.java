package app.example.shellhacks.ui.main;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CalendarView;

import androidx.fragment.app.DialogFragment;

import java.util.Date;

import app.example.shellhacks.R;

public class DateDialog extends DialogFragment {

    private DateDialogCallback onCancel;
    private DateDialogCallback onConfirm;

    public DateDialog(DateDialogCallback onCancel, DateDialogCallback onConfirm) {
        super();
        this.onCancel = onCancel;
        this.onConfirm = onConfirm;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.date_daialog, container, false);

        Button confirmButton = view.findViewById(R.id.date_picker_confirm);
        Button cancelButton = view.findViewById(R.id.date_picker_cancel);

        CalendarView calendarView = view.findViewById(R.id.calendarView);
        calendarView.setMinDate(new Date().getTime());

        confirmButton.setOnClickListener((View v) -> {
            onConfirm.onCompleted(calendarView.getDate());
            this.dismiss();
        });
        cancelButton.setOnClickListener((View v) -> {
            onCancel.onCompleted(calendarView.getDate());
            this.dismiss();
        });

        return view;
    }

    public void setOnConfirmCallback(DateDialogCallback onConfirm) {
        this.onConfirm = onConfirm;
    }

    public void setOnCancelCallback(DateDialogCallback onCancel) {
        this.onCancel = onCancel;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        return dialog;
    }

}
