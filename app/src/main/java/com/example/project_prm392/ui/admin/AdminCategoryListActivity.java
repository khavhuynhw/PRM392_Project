package com.example.project_prm392.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.project_prm392.R;
import com.example.project_prm392.DAO.AppDatabase;
import com.example.project_prm392.model.Category;
import com.example.project_prm392.ui.adapter.AdminCategoryAdapter;
import android.content.DialogInterface;
import java.util.ArrayList;
import java.util.List;

public class AdminCategoryListActivity extends AppCompatActivity {
    private RecyclerView recyclerViewCategories;
    private Button btnAddCategory;
    private ArrayList<Category> categoryList = new ArrayList<>();
    private AdminCategoryAdapter categoryAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_category_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        recyclerViewCategories = findViewById(R.id.recyclerViewCategories);
        recyclerViewCategories.setLayoutManager(new LinearLayoutManager(this));

        categoryAdapter = new AdminCategoryAdapter(categoryList, new AdminCategoryAdapter.OnCategoryActionListener() {
            @Override
            public void onEdit(Category category) {
                Intent intent = new Intent(AdminCategoryListActivity.this, AdminCategoryFormActivity.class);
                intent.putExtra("categoryId", category.getCategoryId());
                startActivity(intent);
            }
            @Override
            public void onDelete(Category category) {
                new AlertDialog.Builder(AdminCategoryListActivity.this)
                        .setTitle("Xóa danh mục")
                        .setMessage("Bạn có chắc muốn xóa danh mục này?")
                        .setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                AppDatabase db = AppDatabase.getDatabase(AdminCategoryListActivity.this);
                                AppDatabase.databaseWriteExecutor.execute(() -> {
                                    db.shopDao().deleteCategory(category);
                                });
                            }
                        })
                        .setNegativeButton("Hủy", null)
                        .show();
            }
        });
        recyclerViewCategories.setAdapter(categoryAdapter);

        AppDatabase db = AppDatabase.getDatabase(this);
        db.shopDao().getAllCategories().observe(this, new Observer<List<Category>>() {
            @Override
            public void onChanged(List<Category> categories) {
                if (categories != null) {
                    categoryAdapter.setCategories(categories);
                }
            }
        });

        btnAddCategory = findViewById(R.id.btnAddCategory);
        btnAddCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminCategoryListActivity.this, AdminCategoryFormActivity.class);
                startActivity(intent);
            }
        });
    }
} 