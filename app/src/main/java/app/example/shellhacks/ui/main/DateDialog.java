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

    private Date date;

    public DateDialog(DateDialogCallback onConfirm, DateDialogCallback onCancel) {
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

        date = new Date();

        calendarView.setOnDateChangeListener((calendarView1, year, month, day) -> {
            date = new Date(year - 1900, month, day);
        });

        confirmButton.setOnClickListener((View v) -> {
            onConfirm.onCompleted(date);
            this.dismiss();
        });
        cancelButton.setOnClickListener((View v) -> {
            onCancel.onCompleted(date);
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
