package com.example.project_prm392.ui.cart;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.project_prm392.R;
import com.google.android.material.textfield.TextInputEditText;

public class FakePaymentActivity extends AppCompatActivity {

    private TextInputEditText cardNumber, cardName, expiryDate, cvv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fake_payment);

        Toolbar toolbar = findViewById(R.id.toolbarFakePayment);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Thanh toán thẻ");

        TextView amountTextView = findViewById(R.id.paymentAmountTextView);
        Button payButton = findViewById(R.id.payButton);
        Button cancelButton = findViewById(R.id.cancelButton);

        cardNumber = findViewById(R.id.cardNumberEditText);
        cardName = findViewById(R.id.cardNameEditText);
        expiryDate = findViewById(R.id.expiryDateEditText);
        cvv = findViewById(R.id.cvvEditText);

        double amount = getIntent().getDoubleExtra("TOTAL_AMOUNT", 0);
        amountTextView.setText(String.format("%,.0fđ", amount));

        payButton.setOnClickListener(v -> {
            if (validateInputs()) {
                // Giả lập xử lý thành công và trả về kết quả
                Intent resultIntent = new Intent();
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });

        cancelButton.setOnClickListener(v -> {
            // Trả về kết quả hủy
            Intent resultIntent = new Intent();
            setResult(RESULT_CANCELED, resultIntent);
            finish();
        });
    }

    /**
     * Hàm kiểm tra đơn giản các thông tin nhập vào.
     */
    private boolean validateInputs() {
        String cardNumberText = cardNumber.getText().toString().trim();
        String cardNameText = cardName.getText().toString().trim();
        String expiryDateText = expiryDate.getText().toString().trim();
        String cvvText = cvv.getText().toString().trim();

        if (TextUtils.isEmpty(cardNumberText) || TextUtils.isEmpty(cardNameText) ||
                TextUtils.isEmpty(expiryDateText) || TextUtils.isEmpty(cvvText)) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin thẻ", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (cardNumberText.length() < 16) {
            Toast.makeText(this, "Số thẻ không hợp lệ", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Bạn có thể thêm các kiểm tra khác nếu muốn (ví dụ: định dạng MM/YY)...

        return true;
    }

    @Override
    public void onBackPressed() {
        // Trả về kết quả hủy khi người dùng nhấn nút Back của hệ thống
        Intent resultIntent = new Intent();
        setResult(RESULT_CANCELED, resultIntent);
        super.onBackPressed();
    }
}
