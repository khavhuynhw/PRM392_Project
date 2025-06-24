package com.example.project_prm392.ui.cart;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_prm392.DAO.AppDatabase;
import com.example.project_prm392.R;
import com.example.project_prm392.model.Address;
import com.example.project_prm392.model.CartItemWithProduct;
import com.example.project_prm392.model.Order;
import com.example.project_prm392.ui.adapter.AddressSelectionAdapter;
import com.example.project_prm392.ui.adapter.CheckoutProductAdapter;
import com.example.project_prm392.ui.home.HomeActivity;
import com.example.project_prm392.ui.profile.AddressBookActivity;

import java.util.ArrayList;
import java.util.List;

public class CheckoutActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText fullNameEditText, addressEditText, phoneEditText;
    private RadioGroup paymentMethodRadioGroup;
    private Button confirmOrderButton;
    private RecyclerView productsRecyclerView;
    private TextView subtotalTextView, shippingFeeTextView, finalTotalTextView;
    private TextView changeAddressButton;
    private RadioButton codRadioButton;

    private AppDatabase appDatabase;
    private CheckoutProductAdapter adapter;
    private List<CartItemWithProduct> currentCartItems;
    private List<Address> userAddressList;
    private int currentUserId;
    private final double SHIPPING_FEE = 20000.0;

    private ActivityResultLauncher<Intent> paymentLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        appDatabase = AppDatabase.getDatabase(getApplicationContext());
        SharedPreferences sharedPreferences = getSharedPreferences("USER_SESSION", Context.MODE_PRIVATE);
        currentUserId = sharedPreferences.getInt("userId", -1);

        initToolbar();
        initViews();
        setupRecyclerView();

        paymentLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        // Thanh toán online thành công, tiến hành lưu đơn hàng với trạng thái "Đã thanh toán"
                        placeOrderOnDatabase("Đã thanh toán");
                    } else {
                        // Thanh toán thất bại hoặc bị hủy
                        Toast.makeText(this, "Thanh toán bị hủy", Toast.LENGTH_SHORT).show();
                    }
                });

        loadData();

        confirmOrderButton.setOnClickListener(v -> processOrder());
        changeAddressButton.setOnClickListener(v -> showAddressSelectionDialog());
    }

    private void initToolbar() {
        toolbar = findViewById(R.id.toolbarCheckout);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void initViews() {
        fullNameEditText = findViewById(R.id.fullNameEditText);
        addressEditText = findViewById(R.id.addressEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        paymentMethodRadioGroup = findViewById(R.id.paymentMethodRadioGroup);
        confirmOrderButton = findViewById(R.id.confirmOrderButton);
        productsRecyclerView = findViewById(R.id.checkoutProductsRecyclerView);
        subtotalTextView = findViewById(R.id.subtotalTextView);
        shippingFeeTextView = findViewById(R.id.shippingFeeTextView);
        finalTotalTextView = findViewById(R.id.finalTotalTextView);
        changeAddressButton = findViewById(R.id.changeAddressButton);
        codRadioButton = findViewById(R.id.codRadioButton);
}

    private void setupRecyclerView() {
        productsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CheckoutProductAdapter(new ArrayList<>());
        productsRecyclerView.setAdapter(adapter);
    }

    private void loadData() {
        if (currentUserId == -1) return;

        // Quan sát toàn bộ danh sách địa chỉ để dùng cho dialog
        appDatabase.shopDao().getAddressesForUser(currentUserId).observe(this, addresses -> {
            if (addresses != null) {
                this.userAddressList = addresses;
            }
        });

        // Tự động điền địa chỉ mặc định (chỉ một lần)
        appDatabase.shopDao().getDefaultAddress(currentUserId).observe(this, defaultAddress -> {
            // Chỉ tự động điền nếu các trường còn trống, để không ghi đè lựa chọn của người dùng
            if (defaultAddress != null && TextUtils.isEmpty(fullNameEditText.getText())) {
                populateAddressFields(defaultAddress);
            }
        });

        // Tải giỏ hàng (giữ nguyên)
        appDatabase.shopDao().getCartWithProducts(currentUserId).observe(this, cartItems -> {
            this.currentCartItems = cartItems;
            adapter.setItems(cartItems);
            calculateTotals();
        });
    }

    private void showAddressSelectionDialog() {
        if (userAddressList == null || userAddressList.isEmpty()) {
            // Logic cho trường hợp không có địa chỉ vẫn giữ nguyên
            new AlertDialog.Builder(this)
                    .setTitle("Không có địa chỉ khác")
                    .setMessage("Bạn chưa có địa chỉ nào khác được lưu. Bạn có muốn thêm địa chỉ mới không?")
                    .setPositiveButton("Thêm mới", (dialog, which) -> {
                        startActivity(new Intent(this, AddressBookActivity.class));
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
            return;
        }

        // Tạo Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_address_selection, null);
        builder.setView(dialogView);

        final AlertDialog dialog = builder.create();

        // Ánh xạ các View bên trong dialog
        RecyclerView addressRecyclerView = dialogView.findViewById(R.id.addressDialogRecyclerView);
        TextView cancelButton = dialogView.findViewById(R.id.cancelDialogButton);

        // Thiết lập RecyclerView
        addressRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        AddressSelectionAdapter selectionAdapter = new AddressSelectionAdapter(userAddressList, selectedAddress -> {
            // Khi một địa chỉ được chọn, cập nhật form và đóng dialog
            populateAddressFields(selectedAddress);
            dialog.dismiss();
        });
        addressRecyclerView.setAdapter(selectionAdapter);

        // Gán sự kiện cho nút Hủy
        cancelButton.setOnClickListener(v -> dialog.dismiss());

        // Hiển thị dialog
        dialog.show();
    }

    private void populateAddressFields(Address address) {
        fullNameEditText.setText(address.getRecipientName());
        phoneEditText.setText(address.getPhone());
        String fullAddress = address.getStreetAddress() + ", " + address.getCity();
        addressEditText.setText(fullAddress);
    }

    private void calculateTotals() {
        double subtotal = 0;
        if (currentCartItems != null) {
            for (CartItemWithProduct item : currentCartItems) {
                subtotal += item.product.getPrice() * item.cartItem.quantity;
            }
        }

        double finalTotal = subtotal + SHIPPING_FEE;

        subtotalTextView.setText(String.format("%,.0fđ", subtotal));
        shippingFeeTextView.setText(String.format("%,.0fđ", SHIPPING_FEE));
        finalTotalTextView.setText(String.format("%,.0fđ", finalTotal));
    }

    private void processOrder() {
        if (!validateInput()) {
            return; // Dừng lại nếu thông tin không hợp lệ
        }

        // Tính toán tổng tiền
        double subtotal = 0;
        if (currentCartItems != null) {
            for (CartItemWithProduct item : currentCartItems) {
                subtotal += item.product.getPrice() * item.cartItem.quantity;
            }
        }
        double finalTotal = subtotal + SHIPPING_FEE;

        // Kiểm tra phương thức thanh toán đã chọn
        if (codRadioButton.isChecked()) {
            // Nếu là COD, lưu đơn hàng ngay lập tức với trạng thái "Đang xử lý"
            placeOrderOnDatabase("Đang xử lý");
        } else {
            // Nếu là online, mở màn hình thanh toán giả lập
            Intent intent = new Intent(this, FakePaymentActivity.class);
            intent.putExtra("TOTAL_AMOUNT", finalTotal);
            paymentLauncher.launch(intent);
        }
    }

    private boolean validateInput() {
        String fullName = fullNameEditText.getText().toString().trim();
        String address = addressEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();

        if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(address) || TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin giao hàng", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (currentCartItems == null || currentCartItems.isEmpty()) {
            Toast.makeText(this, "Giỏ hàng của bạn đang trống.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void placeOrderOnDatabase(String status) {
        // Lấy thông tin từ form
        String fullName = fullNameEditText.getText().toString().trim();
        String address = addressEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();

        // Tính tổng tiền
        double subtotal = 0;
        if (currentCartItems != null) {
            for (CartItemWithProduct item : currentCartItems) {
                subtotal += item.product.getPrice() * item.cartItem.quantity;
            }
        }
        double finalTotal = subtotal + SHIPPING_FEE;

        // Tạo đối tượng Order với trạng thái tương ứng
        Order newOrder = new Order(System.currentTimeMillis(), finalTotal, status, currentUserId, SHIPPING_FEE, fullName, address, phone);

        // Lưu vào DB và xử lý kết quả
        AppDatabase.databaseWriteExecutor.execute(() -> {
            appDatabase.shopDao().placeOrder(newOrder, currentCartItems);
            runOnUiThread(() -> {
                Intent intent = new Intent(CheckoutActivity.this, OrderSuccessActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}