package com.example.project_prm392.ui.product;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.project_prm392.DAO.AppDatabase;
import com.example.project_prm392.R;
import com.example.project_prm392.model.CartItem;
import com.example.project_prm392.model.Product;
import com.example.project_prm392.ui.auth.LoginActivity;

import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ProductDetailActivity extends AppCompatActivity {

    private ImageView productImageView;
    private TextView productNameTextView, productPriceTextView, productDescriptionTextView, quantityTextView;
    private Button decreaseQuantityButton, increaseQuantityButton, addToCartButton;

    private int quantity = 1;
    private Product currentProduct; // <-- Thêm biến để giữ sản phẩm hiện tại
    private Toolbar toolbar;
    private AppDatabase appDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        // Khởi tạo database
        appDatabase = AppDatabase.getDatabase(getApplicationContext());

        // Ánh xạ Views
        initViews();
        setupToolbar();

        // Lấy dữ liệu sản phẩm từ Intent
        if (getIntent().hasExtra("PRODUCT_DETAIL")) {
            currentProduct = getIntent().getParcelableExtra("PRODUCT_DETAIL");
        }

        // Hiển thị thông tin nếu có sản phẩm
        if (currentProduct != null) {
            bindDataToViews();
        } else {
            Toast.makeText(this, "Không thể tải thông tin sản phẩm", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Gán sự kiện cho các nút
        setupClickListeners();
    }

    private void initViews() {
        productImageView = findViewById(R.id.productImageView);
        productNameTextView = findViewById(R.id.productNameTextView);
        productPriceTextView = findViewById(R.id.productPriceTextView);
        productDescriptionTextView = findViewById(R.id.productDescriptionTextView);
        quantityTextView = findViewById(R.id.quantityTextView);
        decreaseQuantityButton = findViewById(R.id.decreaseQuantityButton);
        increaseQuantityButton = findViewById(R.id.increaseQuantityButton);
        addToCartButton = findViewById(R.id.addToCartButton);
        toolbar = findViewById(R.id.toolbarProductDetail);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void setupClickListeners() {
        increaseQuantityButton.setOnClickListener(v -> {
            quantity++;
            quantityTextView.setText(String.valueOf(quantity));
        });

        decreaseQuantityButton.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                quantityTextView.setText(String.valueOf(quantity));
            }
        });

        addToCartButton.setOnClickListener(v -> {
            if (currentProduct != null) {
                // Gọi hàm thêm vào giỏ hàng với logic database
                addItemToCart(currentProduct, quantity);
            }
        });
    }

    /**
     * Hàm thêm sản phẩm vào giỏ hàng trong database.
     */
    private void addItemToCart(Product product, int quantity) {
        // Lấy userId của người dùng đang đăng nhập
        SharedPreferences sharedPreferences = getSharedPreferences("USER_SESSION", Context.MODE_PRIVATE);
        int userId = sharedPreferences.getInt("userId", -1);

        if (userId == -1) {
//            Toast.makeText(this, "Vui lòng đăng nhập để thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
            showLoginRequiredDialog();
            return;
        }

        // Chạy tác vụ trên luồng nền
        AppDatabase.databaseWriteExecutor.execute(() -> {
            // Kiểm tra xem sản phẩm đã có trong giỏ hàng chưa
            CartItem existingItem = appDatabase.shopDao().getCartItemByUserAndProduct(userId, product.productId);

            if (existingItem != null) {
                // Nếu đã có, cập nhật lại số lượng
                existingItem.quantity += quantity;
                appDatabase.shopDao().updateCartItem(existingItem);
            } else {
                // Nếu chưa có, tạo một item mới
                CartItem newItem = new CartItem(product.productId, userId, quantity);
                appDatabase.shopDao().insertCartItem(newItem);
            }

            // Hiển thị thông báo trên luồng UI
            runOnUiThread(() -> Toast.makeText(ProductDetailActivity.this, "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show());
        });
    }

    private void bindDataToViews() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(currentProduct.getName());
        }
        productNameTextView.setText(currentProduct.getName());
        String priceText = String.format("%,.0fđ %s", currentProduct.getPrice(), currentProduct.getUnit());
        productPriceTextView.setText(priceText);
        productImageView.setImageResource(currentProduct.getImageResourceId());
        productDescriptionTextView.setText("Đây là mô tả cho sản phẩm " + currentProduct.getName() + ". Sản phẩm tươi ngon, đảm bảo chất lượng...");
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Xử lý sự kiện khi nhấn nút quay lại trên Toolbar
        if (item.getItemId() == android.R.id.home) {
            finish(); // Đóng Activity hiện tại để quay về màn hình trước đó
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showLoginRequiredDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Yêu cầu đăng nhập")
                .setMessage("Bạn cần đăng nhập để thêm sản phẩm vào giỏ hàng và có trải nghiệm mua sắm tốt nhất.")
                .setPositiveButton("Đăng nhập", (dialog, which) -> {
                    // Chuyển người dùng đến màn hình Đăng nhập
                    Intent intent = new Intent(ProductDetailActivity.this, LoginActivity.class);
                    startActivity(intent);
                })
                .setNegativeButton("Hủy", (dialog, which) -> {
                    // Đơn giản là đóng hộp thoại
                    dialog.dismiss();
                })
                .show();
    }
}