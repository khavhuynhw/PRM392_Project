package com.example.project_prm392.ui.admin;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.project_prm392.DAO.AppDatabase;
import com.example.project_prm392.R;
import com.example.project_prm392.model.Category;
import com.example.project_prm392.model.Product;
import com.example.project_prm392.utils.AdminUtils;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class AdminProductFormActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextInputEditText editTextProductName;
    private TextInputEditText editTextProductPrice;
    private TextInputEditText editTextProductUnit;
    private Spinner spinnerProductCategory;
    private ImageView imageViewProductPreview;
    private Button buttonSelectImage;
    private Button buttonCancel;
    private Button buttonSaveProduct;

    private AppDatabase appDatabase;
    private List<Category> categories;
    private Product editingProduct;
    private boolean isEditMode = false;
    private int selectedImageResourceId = R.drawable.ic_launcher_background;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_product_form);

        // Check admin role
        AdminUtils.checkAdminRole(this, new AdminUtils.AdminRoleCallback() {
            @Override
            public void onAdmin() {
                initializeViews();
                setupToolbar();
                loadCategories();
                checkEditMode();
                setupClickListeners();
            }

            @Override
            public void onNotAdmin(String message) {
                Toast.makeText(AdminProductFormActivity.this, message, Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    private void initializeViews() {
        toolbar = findViewById(R.id.toolbarAdminProductForm);
        editTextProductName = findViewById(R.id.editTextProductName);
        editTextProductPrice = findViewById(R.id.editTextProductPrice);
        editTextProductUnit = findViewById(R.id.editTextProductUnit);
        spinnerProductCategory = findViewById(R.id.spinnerProductCategory);
        imageViewProductPreview = findViewById(R.id.imageViewProductPreview);
        buttonSelectImage = findViewById(R.id.buttonSelectImage);
        buttonCancel = findViewById(R.id.buttonCancel);
        buttonSaveProduct = findViewById(R.id.buttonSaveProduct);

        appDatabase = AppDatabase.getDatabase(this);
        categories = new ArrayList<>();
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Thêm sản phẩm mới");
        }
    }

    private void loadCategories() {
        appDatabase.shopDao().getAllCategories().observe(this, categoryList -> {
            if (categoryList != null) {
                categories.clear();
                categories.addAll(categoryList);
                setupCategorySpinner();
            }
        });
    }

    private void setupCategorySpinner() {
        List<String> categoryNames = new ArrayList<>();
        for (Category category : categories) {
            categoryNames.add(category.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProductCategory.setAdapter(adapter);
    }

    private void checkEditMode() {
        int productId = getIntent().getIntExtra("product_id", -1);
        if (productId != -1) {
            isEditMode = true;
            getSupportActionBar().setTitle("Chỉnh sửa sản phẩm");
            loadProductForEditing(productId);
        }
    }

    private void loadProductForEditing(int productId) {
        appDatabase.shopDao().getProductById(productId).observe(this, product -> {
            if (product != null) {
                editingProduct = product;
                populateFormWithProduct(product);
            }
        });
    }

    private void populateFormWithProduct(Product product) {
        editTextProductName.setText(product.getName());
        editTextProductPrice.setText(String.valueOf((int) product.getPrice()));
        editTextProductUnit.setText(product.getUnit());
        selectedImageResourceId = product.getImageResourceId();
        imageViewProductPreview.setImageResource(selectedImageResourceId);

        // Set category spinner
        for (int i = 0; i < categories.size(); i++) {
            if (categories.get(i).getCategoryId() == product.getProductCategoryId()) {
                spinnerProductCategory.setSelection(i);
                break;
            }
        }
    }

    private void setupClickListeners() {
        buttonSelectImage.setOnClickListener(v -> selectImage());
        buttonCancel.setOnClickListener(v -> finish());
        buttonSaveProduct.setOnClickListener(v -> saveProduct());
    }

    private void selectImage() {
        // For simplicity, we'll use a predefined set of images
        // In a real app, you'd implement image picker functionality
        String[] imageOptions = {"Hình 1", "Hình 2", "Hình 3"};
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Chọn hình ảnh")
                .setItems(imageOptions, (dialog, which) -> {
                    // For demo purposes, we'll use different drawable resources
                    int[] imageResources = {
                            R.drawable.ic_launcher_background,
                            R.drawable.ic_launcher_foreground,
                            R.drawable.ic_launcher_background
                    };
                    selectedImageResourceId = imageResources[which];
                    imageViewProductPreview.setImageResource(selectedImageResourceId);
                })
                .show();
    }

    private void saveProduct() {
        String name = editTextProductName.getText().toString().trim();
        String priceStr = editTextProductPrice.getText().toString().trim();
        String unit = editTextProductUnit.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(priceStr) || TextUtils.isEmpty(unit)) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double price = Double.parseDouble(priceStr);
            if (price <= 0) {
                Toast.makeText(this, "Giá phải lớn hơn 0", Toast.LENGTH_SHORT).show();
                return;
            }

            int selectedCategoryPosition = spinnerProductCategory.getSelectedItemPosition();
            if (selectedCategoryPosition < 0 || selectedCategoryPosition >= categories.size()) {
                Toast.makeText(this, "Vui lòng chọn danh mục", Toast.LENGTH_SHORT).show();
                return;
            }

            Category selectedCategory = categories.get(selectedCategoryPosition);

            if (isEditMode && editingProduct != null) {
                // Update existing product
                editingProduct.setName(name);
                editingProduct.setPrice(price);
                editingProduct.setUnit(unit);
                editingProduct.setImageResourceId(selectedImageResourceId);
                editingProduct.productCategoryId = selectedCategory.getCategoryId();

                AppDatabase.databaseWriteExecutor.execute(() -> {
                    appDatabase.shopDao().updateProduct(editingProduct);
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Cập nhật sản phẩm thành công", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                });
            } else {
                // Create new product
                Product newProduct = new Product(name, price, unit, selectedImageResourceId, selectedCategory.getCategoryId());

                AppDatabase.databaseWriteExecutor.execute(() -> {
                    appDatabase.shopDao().insertProduct(newProduct);
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Thêm sản phẩm thành công", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                });
            }

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Giá không hợp lệ", Toast.LENGTH_SHORT).show();
        }
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