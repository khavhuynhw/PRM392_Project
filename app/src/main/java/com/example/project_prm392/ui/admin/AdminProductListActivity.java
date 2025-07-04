package com.example.project_prm392.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_prm392.DAO.AppDatabase;
import com.example.project_prm392.R;
import com.example.project_prm392.model.Product;
import com.example.project_prm392.ui.adapter.AdminProductAdapter;
import com.example.project_prm392.utils.AdminUtils;

import java.util.ArrayList;
import java.util.List;

public class AdminProductListActivity extends AppCompatActivity implements AdminProductAdapter.AdminProductAdapterListener {

    private Toolbar toolbar;
    private EditText editTextSearchProduct;
    private RecyclerView recyclerViewAdminProducts;
    private AdminProductAdapter adapter;
    private AppDatabase appDatabase;
    private List<Product> allProducts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_product_list);

        // Check admin role
        AdminUtils.checkAdminRole(this, new AdminUtils.AdminRoleCallback() {
            @Override
            public void onAdmin() {
                initializeViews();
                setupToolbar();
                setupRecyclerView();
                loadProducts();
                setupSearch();
            }

            @Override
            public void onNotAdmin(String message) {
                Toast.makeText(AdminProductListActivity.this, message, Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    private void initializeViews() {
        toolbar = findViewById(R.id.toolbarAdminProductList);
        editTextSearchProduct = findViewById(R.id.editTextSearchProduct);
        recyclerViewAdminProducts = findViewById(R.id.recyclerViewAdminProducts);
        Button buttonAddProduct = findViewById(R.id.buttonAddProduct);
        appDatabase = AppDatabase.getDatabase(this);
        allProducts = new ArrayList<>();

        // Add click listener for Add Product button
        buttonAddProduct.setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminProductFormActivity.class);
            startActivity(intent);
        });
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Quản lý sản phẩm");
        }
    }

    private void setupRecyclerView() {
        adapter = new AdminProductAdapter(this, this);
        recyclerViewAdminProducts.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewAdminProducts.setAdapter(adapter);
    }

    private void loadProducts() {
        appDatabase.shopDao().getAllProducts().observe(this, products -> {
            if (products != null) {
                allProducts.clear();
                allProducts.addAll(products);
                adapter.setProducts(allProducts);
            }
        });
    }

    private void setupSearch() {
        editTextSearchProduct.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterProducts(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void filterProducts(String query) {
        List<Product> filteredList = new ArrayList<>();
        for (Product product : allProducts) {
            if (product.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(product);
            }
        }
        adapter.setProducts(filteredList);
    }

    @Override
    public void onEditProduct(Product product) {
        Intent intent = new Intent(this, AdminProductFormActivity.class);
        intent.putExtra("product_id", product.productId);
        startActivity(intent);
    }

    @Override
    public void onDeleteProduct(Product product) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa sản phẩm '" + product.getName() + "' không?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    deleteProduct(product);
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void deleteProduct(Product product) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            appDatabase.shopDao().deleteProduct(product);
            runOnUiThread(() -> {
                Toast.makeText(this, "Đã xóa sản phẩm: " + product.getName(), Toast.LENGTH_SHORT).show();
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