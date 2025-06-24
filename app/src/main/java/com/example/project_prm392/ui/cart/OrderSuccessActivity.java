package com.example.project_prm392.ui.cart;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.project_prm392.R;
import com.example.project_prm392.ui.home.HomeActivity;

public class OrderSuccessActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_success);

        Button viewOrdersButton = findViewById(R.id.viewOrdersButton);
        Button continueShoppingButton = findViewById(R.id.continueShoppingButton);

        viewOrdersButton.setOnClickListener(v -> {
            // Tạo một Intent để quay về HomeActivity (màn hình gốc)
            Intent homeIntent = new Intent(this, HomeActivity.class);
            homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

            // Tạo một Intent để mở OrderHistoryActivity
            Intent orderHistoryIntent = new Intent(this, OrderHistoryActivity.class);

            // Bắt đầu cả hai activities, tạo ra một back stack đúng:
            // HomeActivity -> OrderHistoryActivity
            startActivities(new Intent[]{homeIntent, orderHistoryIntent});

            // Đóng màn hình hiện tại
            finish();
        });

        continueShoppingButton.setOnClickListener(v -> {
            goToHomeScreen();
        });
    }

    private void goToHomeScreen() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        // Khi nhấn nút back, cũng quay về trang chủ
        goToHomeScreen();
        super.onBackPressed();
    }
}