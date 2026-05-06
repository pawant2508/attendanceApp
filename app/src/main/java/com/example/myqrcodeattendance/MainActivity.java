package com.example.myqrcodeattendance;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity {
    private static final String BASE_URL = "https://qr-attendance-backend-k724.onrender.com/check_login.php";
    private static final int REQUEST_CAMERA_PERMISSION = 200;

    private final OkHttpClient client = new OkHttpClient();
    EditText t1, t2;
    TextView tv;
    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        checkLoginStatus();
        initializeUI();
        checkRequirements();
    }

    private void checkLoginStatus() {
        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        if (sharedPreferences.getBoolean("IsLoggedIn", false)) {
            startActivity(new Intent(this, DashboardActivity.class));
            finish();
        }
    }

    private void initializeUI() {
        t1 = findViewById(R.id.useremail);
        t2 = findViewById(R.id.password);
        tv = findViewById(R.id.signupText);
        btn = findViewById(R.id.loginButton);
    }

    private void checkRequirements() {
        if (isNetworkAvailable()) {
            showNoInternetDialog();
        } else {
            setupButtonClickListener();
        }
    }

    private void setupButtonClickListener() {
        btn.setOnClickListener(view -> {
            if (isNetworkAvailable()) {
                showNoInternetDialog();
            } else {
                attemptLogin();
            }
        });
    }

    private void attemptLogin() {
        if (!checkCameraPermission()) {
            requestCameraPermission();
            return;
        }

        String email = t1.getText().toString().trim();
        String password = t2.getText().toString().trim();
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
        } else {
            saveData(email, password);
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork == null || !activeNetwork.isConnected();
    }

    private boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed to use the camera")
                    .setPositiveButton("OK", (dialog, which) -> ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION))
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                    .create()
                    .show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission granted to use the camera", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Permission denied to use the camera", Toast.LENGTH_SHORT).show();
        }
        checkRequirements(); // recheck the requirements after handling permissions
    }

    private void showNoInternetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("No Internet Connection")
                .setMessage("Please check your internet connection and try again.")
                .setPositiveButton("Retry", (dialog, id) -> checkRequirements())
                .setCancelable(false)
                .show();
    }

    private void saveData(String email, String password) {
        RequestBody formData = new FormBody.Builder().add("email", email).add("password", password).build();
        Request request = new Request.Builder().url(BASE_URL).post(formData).build();
        client.newCall(request).enqueue(new Callback() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> tv.setText("Failed to connect to the server: " + e.getMessage()));
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                assert response.body() != null;
                String resp = response.body().string();
                runOnUiThread(() -> {
                    try {
                        JSONObject jsonObject = new JSONObject(resp);
                        handleLoginResponse(jsonObject);
                    } catch (Exception e) {
                        tv.setText("Error parsing the server response.");
                    }
                });
            }
        });
    }

    private void handleLoginResponse(JSONObject jsonObject) throws Exception {
        String answer = jsonObject.getString("response");
        if ("Login Successful".equals(answer)) {
            SharedPreferences.Editor editor = getSharedPreferences("LoginPrefs", MODE_PRIVATE).edit();
            editor.putBoolean("IsLoggedIn", true);
            editor.putString("StudentID", jsonObject.getString("studentID"));
            editor.putString("Name", jsonObject.getString("name"));
            editor.putString("Year", jsonObject.getString("year"));
            editor.putString("Department", jsonObject.getString("department"));
            editor.putString("Curryear", jsonObject.getString("curryear"));
            editor.putString("Sect", jsonObject.getString("sect"));
            editor.putString("Term", jsonObject.getString("term"));


            editor.apply();

            startActivity(new Intent(this, DashboardActivity.class));
            finish();
        } else {
            Toast.makeText(this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
            t1.setText("");
            t2.setText("");
        }
    }
}
