package app.example.shellhacks;

import android.annotation.SuppressLint;
import android.media.Image;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.experimental.UseExperimental;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.barcode.Barcode;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.common.InputImage;

import java.util.List;

import app.example.shellhacks.ui.main.AfterAnalysis;

public class BarcodeAnalyzer implements ImageAnalysis.Analyzer {

    private AfterAnalysis mAfterFunction;

    public BarcodeAnalyzer(AfterAnalysis finishFunction) {
        super();
        mAfterFunction = finishFunction;
    }


    @Override
    public void analyze(@NonNull ImageProxy imageProxy) {

        BarcodeScannerOptions options =
                new BarcodeScannerOptions.Builder()
                .setBarcodeFormats(
                        Barcode.FORMAT_UPC_A,
                        Barcode.FORMAT_UPC_E
                ).build();

        BarcodeScanner scanner = BarcodeScanning.getClient(options);

        @SuppressLint("UnsafeExperimentalUsageError") Image mediaImage = imageProxy.getImage();
        if (mediaImage != null) {
            InputImage image =
                    InputImage.fromMediaImage(mediaImage, imageProxy.getImageInfo().getRotationDegrees());


            Task<List<Barcode>> result = scanner.process(image)
                    .addOnSuccessListener(new OnSuccessListener<List<Barcode>>() {
                        @Override
                        public void onSuccess(List<Barcode> barcodes) {
                            if (barcodes.size() == 1) {
                                Barcode barcode = barcodes.get(0);
                                if (barcode.getValueType() == Barcode.TYPE_PRODUCT) {
                                    mAfterFunction.actionAfterAnalysis(barcode.getRawValue());
                                }
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {

                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    })
                    .addOnCompleteListener(new OnCompleteListener<List<Barcode>>() {
                        @Override
                        public void onComplete(@NonNull Task<List<Barcode>> task) {
                            imageProxy.close();
                        }
                    });

        }
    }
}
