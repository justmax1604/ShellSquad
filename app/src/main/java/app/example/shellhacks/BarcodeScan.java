package app.example.shellhacks;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import app.example.shellhacks.ui.main.BarcodeScanFragment;

public class BarcodeScan extends AppCompatActivity {

    public static final String BACK_STACK_ROOT_TAG = "root_fragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.barcode_scan_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, BarcodeScanFragment.newInstance())
                    .commitNow();
        }
    }
}