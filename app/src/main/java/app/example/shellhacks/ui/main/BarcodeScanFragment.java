package app.example.shellhacks.ui.main;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LifecycleOwner;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.common.util.concurrent.ListenableFuture;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import app.example.shellhacks.BarcodeAnalyzer;
import app.example.shellhacks.R;

import static android.content.Context.VIBRATOR_SERVICE;

public class BarcodeScanFragment extends Fragment {


    public static BarcodeScanFragment newInstance() {return new BarcodeScanFragment();}

    private ExecutorService cameraExecutor;
    private final int CAMERA_PERM = 15;
    private PreviewView mPreviewView;
    private boolean mCodeFound = false;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("Fragment", "Testing the onCreateView");
        return inflater.inflate(R.layout.barcode_scan_fragment, container, false);
    }

    private void afterDialogConfirmed(String name) {
        goToDateScanner(name);
    }

    private void goToDateScanner(String name) {
        cameraExecutor.shutdown();

        Fragment fragment = new DateScanFragment(name);
        FragmentManager fManager = getActivity().getSupportFragmentManager();
        fManager.beginTransaction()
                .setCustomAnimations(
                        R.anim.slide_in,
                        R.anim.fade_out,
                        R.anim.fade_in,
                        R.anim.slide_out)
                .replace(R.id.container, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void cancelledNameDialog(String name) {
        mCodeFound = false;
    }

    private void showDialog(String barcode, String name) {
        new MaterialAlertDialogBuilder(getContext())
                .setTitle("Item Found!")
                .setMessage("The barcode " + barcode + " was matched to " + name + ".\n Is this correct?")
                .setPositiveButton("YES", (DialogInterface dialogInterface, int i) -> {
                    goToDateScanner(name);
                })
                .setNegativeButton("NO", (DialogInterface dialogInterface, int i) -> {
                    mCodeFound = false;
                })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        mCodeFound = false;
                    }
                })
                .show();
    }

    private void getName(String barcode) {

    }

    public void afterAnalysis(String output) {
        if (mCodeFound)
            return;

        mCodeFound = true;
        if (Build.VERSION.SDK_INT >= 26) {
            ((Vibrator) getActivity().getSystemService(VIBRATOR_SERVICE)).vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
        }


        final String apiKey = "8EA440995629B1CE67019A179E63AA8E";
        String url = "https://api.upcdatabase.org/product/" + output + "?apikey=" + apiKey;

        RequestQueue queue = Volley.newRequestQueue(getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("API", response);
                        try {
                            JSONObject json = new JSONObject(response);
                            boolean success = (boolean)json.get("success");
                            if (success) {
                                String name = (String) json.get("title");
                                showDialog(output, name);
                            } else {
                                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                ItemNameDialog dialog = new ItemNameDialog(BarcodeScanFragment.this::afterDialogConfirmed,
                                        BarcodeScanFragment.this::cancelledNameDialog, ItemNameDialog.NEW_ITEM);
                                dialog.show(fragmentManager, null);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Snackbar.make(getView(), "Could not find a name for this code",
                        Snackbar.LENGTH_SHORT).show();
            }
        });

        queue.add(stringRequest);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mPreviewView = getView().findViewById(R.id.view_finder);

        Button manualButton = getView().findViewById(R.id.manualButton);
        manualButton.setOnClickListener((View v) -> {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            ItemNameDialog dialog = new ItemNameDialog(this::afterDialogConfirmed, this::afterDialogConfirmed, ItemNameDialog.MANUAL);
            dialog.show(fragmentManager, null);
        });

        if (allPermissionsGranted()) {
            startCamera();
        } else {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERM);
        }
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture
                = ProcessCameraProvider.getInstance(getContext());

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                Preview preview = new Preview.Builder().build();

                cameraExecutor = Executors.newSingleThreadExecutor();

                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                        .setTargetResolution(new Size(1280, 720))
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

                imageAnalysis.setAnalyzer(cameraExecutor, new BarcodeAnalyzer(this::afterAnalysis));;

                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build();

                Camera camera = cameraProvider.bindToLifecycle(
                        ((LifecycleOwner) this),
                        cameraSelector,
                        preview,
                        imageAnalysis);

                preview.setSurfaceProvider(
                        mPreviewView.getSurfaceProvider()
                );
            }
            catch (ExecutionException | InterruptedException ignored) {

            }
        }, ContextCompat.getMainExecutor(getContext()));
    }

    private boolean allPermissionsGranted() {
        return ContextCompat.checkSelfPermission(Objects.requireNonNull(getContext()), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CAMERA_PERM) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else {
                Snackbar.make(getView(), R.string.camera_permissions_denied, Snackbar.LENGTH_SHORT);
            }
        } else {
            Snackbar.make(getView(), R.string.camera_permissions_denied, Snackbar.LENGTH_SHORT).show();
        }
    }

}

