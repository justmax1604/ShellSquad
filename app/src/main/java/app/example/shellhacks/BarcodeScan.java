package app.example.shellhacks;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import app.example.shellhacks.ui.main.CameraFragment;

public class BarcodeScan extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.barcode_scan_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, CameraFragment.newInstance())
                    .commitNow();
        }
    }
}