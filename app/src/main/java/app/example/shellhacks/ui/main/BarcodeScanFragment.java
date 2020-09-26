package app.example.shellhacks.ui.main;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

import app.example.shellhacks.R;

public class BarcodeScanFragment extends CameraFragment {


    public static BarcodeScanFragment newInstance() {return new BarcodeScanFragment();}


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("Fragment", "Testing the onCreateView");
        return inflater.inflate(R.layout.barcode_scan_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        mPreviewView = getView().findViewById(R.id.view_finder);


        super.onActivityCreated(savedInstanceState);
    }

}
