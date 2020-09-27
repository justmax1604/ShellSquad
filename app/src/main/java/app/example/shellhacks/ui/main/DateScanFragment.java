package app.example.shellhacks.ui.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LifecycleOwner;

import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.type.DateTime;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import app.example.shellhacks.DateValidatorUsingDateFormat;
import app.example.shellhacks.R;

public class DateScanFragment extends Fragment {

    private ExecutorService cameraExecutor;
    private final int CAMERA_PERM = 15;
    private PreviewView mPreviewView;
    private ImageView mImageView;

    private ImageCapture imageCapture;
    private Button captureButton;
    private Button manualButton;
    private boolean mViewingPicture;
    private List<Date> scanResults;

    private Date selectedDate;

    private String barcode;

    public DateScanFragment(String barcode) {
        this.barcode = barcode;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.date_scan_fragment, container, false);
    }

    public void dateDialogReturned(Date date) {

        toConfirmationScreen(date);

    }

    public void dateDialogCancelled(Date date) {

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mPreviewView = getView().findViewById(R.id.view_finder);
        captureButton = getView().findViewById(R.id.scanDateButton);
        mImageView = getView().findViewById(R.id.imageView);

        manualButton = getView().findViewById(R.id.manual_entry);
        manualButton.setOnClickListener((View v) -> {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            DateDialog newFragment = new DateDialog(this::dateDialogReturned, this::dateDialogCancelled);
            newFragment.show(fragmentManager, "dialog");
        });

        captureButton.setOnClickListener((View v) -> {
            if (mViewingPicture) {
                mImageView.setVisibility(ImageView.INVISIBLE);
                mPreviewView.setVisibility(PreviewView.VISIBLE);
                captureButton.setText("SCAN");
                mViewingPicture = false;
                return;
            }
            mViewingPicture = true;
            captureButton.setText("RETAKE");
            mPreviewView.setVisibility(PreviewView.INVISIBLE);

            imageCapture.takePicture(cameraExecutor, new ImageCapture.OnImageCapturedCallback() {
                @Override
                public void onCaptureSuccess(@NonNull ImageProxy imageProxy) {

                    @SuppressLint("UnsafeExperimentalUsageError") Image mediaImage = imageProxy.getImage();
                    if (mediaImage != null) {
                        InputImage image =
                                InputImage.fromMediaImage(mediaImage, imageProxy.getImageInfo().getRotationDegrees());
                        Bitmap overlay = image.getBitmapInternal();

                        TextRecognizer recognizer = TextRecognition.getClient();

                        Task<Text> result = recognizer.process(image)
                                .addOnSuccessListener((Text visionText) -> {
                                    Paint paint = new Paint();
                                    paint.setAntiAlias(true);
                                    paint.setStyle(Paint.Style.STROKE);
                                    paint.setColor(Color.RED);
                                    paint.setStrokeWidth(10f);

                                    Canvas canvas = new Canvas(overlay);

                                    List<Date> resultingDates = new ArrayList<>();
                                    DateValidatorUsingDateFormat validator = new DateValidatorUsingDateFormat();

                                    for (Text.TextBlock block : visionText.getTextBlocks()) {
                                        for (Text.Line line : block.getLines()) {

                                            Date lineDate = validator.getParsedDate(line.getText());
                                            if (lineDate != null) {
                                                resultingDates.add(validator.fixYear(lineDate));
                                            }

                                            for (Text.Element element : line.getElements()) {
                                                Rect rect = element.getBoundingBox();
                                                canvas.drawRect(rect, paint);
                                                Log.d("String Found", element.getText());
                                                Date possibleDate = validator.getParsedDate(element.getText());
                                                if (possibleDate != null) {
                                                    resultingDates.add(validator.fixYear(possibleDate));
                                                }
                                            }
                                        }
                                    }

                                    mImageView.setImageBitmap(overlay);
                                    mImageView.setVisibility(ImageView.VISIBLE);

                                    if (resultingDates.size() == 0) {
                                        Snackbar.make(getView(), "Could not find a valid date in image", Snackbar.LENGTH_SHORT).show();
                                    } else if (resultingDates.size() == 1) {
                                        new MaterialAlertDialogBuilder(getContext())
                                                .setTitle("Found Date")
                                                .setMessage("The date " + DateValidatorUsingDateFormat.FormatDate(resultingDates.get(0)) + " was found.\n Is this correct?")
                                                .setPositiveButton("YES", (DialogInterface dialogInterface, int i) -> {
                                                    toConfirmationScreen(resultingDates.get(0));
                                                })
                                                .setNegativeButton("NO", (DialogInterface dialogI, int i) -> {

                                                })
                                                .show();
                                    } else {
                                        openMultipleDateDialog(resultingDates);
                                    }

                                })
                                .addOnFailureListener((Exception e) -> {

                                })
                                .addOnCompleteListener((Task<Text> visionText) -> {
                                   imageProxy.close();
                                });

                    }
                }
            });
        });

        if (allPermissionsGranted()) {
            startCamera();
        } else {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERM);
        }
    }

    private void openMultipleDateDialog(List<Date> dateList) {

        String[] dateStrings = new String[dateList.size()];

        for (int i = 0; i < dateList.size(); i++) {
            dateStrings[i] = DateValidatorUsingDateFormat.FormatDate(dateList.get(i));
        }

        selectedDate = dateList.get(0);

        new MaterialAlertDialogBuilder(getContext())
                .setTitle("Select Date")
                .setNeutralButton("Cancel", ((dialogInterface, i) -> {

                }))
                .setPositiveButton("Submit", ((dialogInterface, i) -> {
                    toConfirmationScreen(selectedDate);
                }))
                .setSingleChoiceItems(dateStrings, 0, ((dialogInterface, i) -> {
                    selectedDate = dateList.get(i);
                }))
                .show();
    }

    private void toConfirmationScreen(Date expDate) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, new ConfirmationFragment(barcode, expDate))
                .addToBackStack(null)
                .commit();
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture
                = ProcessCameraProvider.getInstance(getContext());

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                Preview preview = new Preview.Builder().build();

                cameraExecutor = Executors.newSingleThreadExecutor();

                imageCapture = new ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                        .build();

                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build();

                Camera camera = cameraProvider.bindToLifecycle(
                        ((LifecycleOwner) this),
                        cameraSelector,
                        preview,
                        imageCapture);

                preview.setSurfaceProvider(mPreviewView.getSurfaceProvider());

            } catch (ExecutionException | InterruptedException ignored) {

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
