package com.example.project_prm392.ui.admin;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.project_prm392.R;
import com.example.project_prm392.DAO.AppDatabase;
import com.example.project_prm392.model.Category;

public class AdminCategoryFormActivity extends AppCompatActivity {
    private EditText edtCategoryName;
    private Button btnSaveCategory;
    private int categoryId = -1;
    private Category editingCategory;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_category_form);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        edtCategoryName = findViewById(R.id.edtCategoryName);
        btnSaveCategory = findViewById(R.id.btnSaveCategory);

        if (getIntent() != null && getIntent().hasExtra("categoryId")) {
            categoryId = getIntent().getIntExtra("categoryId", -1);
            loadCategoryForEdit();
        }

        btnSaveCategory.setOnClickListener(v -> saveCategory());
    }

    private void loadCategoryForEdit() {
        AppDatabase db = AppDatabase.getDatabase(this);
        db.shopDao().getAllCategories().observe(this, categories -> {
            for (Category cat : categories) {
                if (cat.getCategoryId() == categoryId) {
                    editingCategory = cat;
                    edtCategoryName.setText(cat.getName());
                    break;
                }
            }
        });
    }

    private void saveCategory() {
        String name = edtCategoryName.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Vui lòng nhập tên danh mục", Toast.LENGTH_SHORT).show();
            return;
        }
        AppDatabase db = AppDatabase.getDatabase(this);
        if (categoryId == -1) {
            // Thêm mới
            Category newCat = new Category(name, R.drawable.ic_launcher_background);
            AppDatabase.databaseWriteExecutor.execute(() -> {
                db.shopDao().insertCategory(newCat);
            });
        } else {
            // Sửa
            editingCategory.setName(name);
            AppDatabase.databaseWriteExecutor.execute(() -> {
                db.shopDao().updateCategory(editingCategory);
            });
        }
        Toast.makeText(this, "Đã lưu danh mục", Toast.LENGTH_SHORT).show();
        finish();
    }
} 