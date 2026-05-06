package com.example.myqrcodeattendance;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class DashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            // Adjust the padding based on system bars to ensure UI doesn't overlap with system bars
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return WindowInsetsCompat.CONSUMED;
        });
        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);

        if (!NetworkUtils.isNetworkConnected(this)) {
            AlertDialogManager.showNoInternetConnectionDialog(this);
        } else {
            Button enterAttendanceButton = findViewById(R.id.loginButton);
            enterAttendanceButton.setOnClickListener(v -> {
                Intent intent = new Intent(DashboardActivity.this, ScanQRCodeActivity.class);
                // Put the extra values into the intent from the shared preferences
                intent.putExtra("StudentID", sharedPreferences.getString("StudentID", "N/A"));
                intent.putExtra("Name", sharedPreferences.getString("Name", "N/A"));
                intent.putExtra("Year", sharedPreferences.getString("Year", "N/A"));
                intent.putExtra("Department", sharedPreferences.getString("Department", "N/A"));
                intent.putExtra("Curryear", sharedPreferences.getString("Curryear", "N/A"));
                intent.putExtra("Sect", sharedPreferences.getString("Sect", "N/A"));
                intent.putExtra("Term", sharedPreferences.getString("Term", "N/A"));

                startActivity(intent);
            });

            displayUserDetails(sharedPreferences);
        }
    }

    public void displayUserDetails(SharedPreferences sharedPreferences) {
        // Retrieve the user details from SharedPreferences
        String studentID = sharedPreferences.getString("StudentID", "N/A");
        String name = sharedPreferences.getString("Name", "N/A");
        String year = sharedPreferences.getString("Year", "N/A");
        String department = sharedPreferences.getString("Department", "N/A");
        String sect = sharedPreferences.getString("Sect", "N/A");

        // Find the TextView by ID and set the name
        TextView tvName = findViewById(R.id.nameTextView);
        tvName.setText("Welcome " + name);
    }
}