package com.example.myqrcodeattendance;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ScanQRCodeActivity extends AppCompatActivity {
    private CaptureManager capture;
    private DecoratedBarcodeView barcodeScannerView;
    private MediaPlayer mediaPlayer;
    private static final String BASE_URL = "https://projectverse.000webhostapp.com/insert_attendance.php";
    private String studentID;
    private String name;
    private String year;
    private String department;
    TextView tv;
    private String date;
    private String sect;
    private String term;
    private final OkHttpClient client = new OkHttpClient();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        Intent intent = getIntent();
        studentID = intent.getStringExtra("StudentID");
        year = intent.getStringExtra("Year");
        department = intent.getStringExtra("Department");

        date = intent.getStringExtra("Curryear");
        sect = intent.getStringExtra("Sect");
        term = intent.getStringExtra("Term");


        // Check for camera permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 0);
        } else {
            initializeScanner(savedInstanceState);
        }
        initializeMediaPlayer();
    }

    private void initializeScanner(Bundle savedInstanceState) {
        barcodeScannerView = findViewById(R.id.zxing_barcode_scanner);
        barcodeScannerView.initializeFromIntent(getIntent());
        capture = new CaptureManager(this, barcodeScannerView);
        capture.initializeFromIntent(getIntent(), savedInstanceState);
        capture.decode();
        barcodeScannerView.decodeContinuous(new BarcodeCallback() {
            @Override
            public void barcodeResult(BarcodeResult result) {
                if (result.getText() != null) {
                    barcodeScannerView.pause();
                    playBeep();
                    //showResultDialog(result.getText());
                    saveData(result.getText(), studentID, date, year, sect, term, department);
                }
            }
        });
    }
    private void saveData(String result, String studentID, String date, String year, String sect, String term, String department) {
        RequestBody formData = new FormBody.Builder()
                .add("id", result)
                .add("stu_id", studentID)
                .add("date", date)
                .add("year", year)
                .add("sect", sect)
                .add("term", term)
                .add("department", department)
                .build();
        Request request = new Request.Builder().url(BASE_URL).post(formData).build();
        client.newCall(request).enqueue(new Callback() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> Toast.makeText(ScanQRCodeActivity.this, "Failed to connect to the server: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                final String resp = response.body().string();
                runOnUiThread(() -> {
                    try {
                        JSONObject jsonObject = new JSONObject(resp);
                        handleLoginResponse(jsonObject);  // Call the method to handle the login response
                    } catch (Exception e) {
                        Toast.makeText(ScanQRCodeActivity.this, "Error parsing the server response.", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    private void handleLoginResponse(JSONObject jsonObject) {
        try {
            String answer = jsonObject.getString("response");
            showResultDialog(answer);
        } catch (JSONException e) {
            runOnUiThread(() -> Toast.makeText(ScanQRCodeActivity.this, "Error in JSON parsing: " + e.getMessage(), Toast.LENGTH_LONG).show());
        }
    }



    private void initializeMediaPlayer() {
        mediaPlayer = MediaPlayer.create(this, R.raw.beep);
    }

    private void showResultDialog(String result) {
        new AlertDialog.Builder(this)
                .setTitle("Scan Result")
                .setMessage(result)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Optionally do something after acknowledging the result
                        Intent intent = new Intent(ScanQRCodeActivity.this, DashboardActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish(); // Finish the current activity
                    }
                })
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (capture != null) {
            capture.onResume();
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (capture != null) {
            capture.onPause();
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (capture != null) {
            capture.onDestroy();
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (capture != null) {
            capture.onSaveInstanceState(outState);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        }
    }
    private void playBeep() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            initializeScanner(null); // Passing null because we are initializing after permission granted
        } else {
            Toast.makeText(this, "Camera permission is required to scan QR codes", Toast.LENGTH_LONG).show();
        }
    }

}
