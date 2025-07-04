package com.example.project_prm392.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_prm392.DAO.AppDatabase;
import com.example.project_prm392.R;
import com.example.project_prm392.model.Category;
import com.example.project_prm392.model.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AdminProductAdapter extends RecyclerView.Adapter<AdminProductAdapter.AdminProductViewHolder> {

    private List<Product> products;
    private List<Category> categories;
    private Context context;
    private AdminProductAdapterListener listener;
    private ExecutorService executor;

    public interface AdminProductAdapterListener {
        void onEditProduct(Product product);
        void onDeleteProduct(Product product);
    }

    public AdminProductAdapter(Context context, AdminProductAdapterListener listener) {
        this.context = context;
        this.listener = listener;
        this.products = new ArrayList<>();
        this.categories = new ArrayList<>();
        this.executor = Executors.newSingleThreadExecutor();
        loadCategories();
    }

    private void loadCategories() {
        executor.execute(() -> {
            AppDatabase database = AppDatabase.getDatabase(context);
            List<Category> categoryList = database.shopDao().getAllCategories().getValue();
            if (categoryList != null) {
                categories.clear();
                categories.addAll(categoryList);
            }
        });
    }

    public void setProducts(List<Product> products) {
        this.products = products;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AdminProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_product, parent, false);
        return new AdminProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminProductViewHolder holder, int position) {
        Product product = products.get(position);
        holder.bind(product);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    class AdminProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewProduct;
        TextView textViewProductName;
        TextView textViewProductPrice;
        TextView textViewProductCategory;
        Button buttonEditProduct;
        Button buttonDeleteProduct;

        public AdminProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewProduct = itemView.findViewById(R.id.imageViewProduct);
            textViewProductName = itemView.findViewById(R.id.textViewProductName);
            textViewProductPrice = itemView.findViewById(R.id.textViewProductPrice);
            textViewProductCategory = itemView.findViewById(R.id.textViewProductCategory);
            buttonEditProduct = itemView.findViewById(R.id.buttonEditProduct);
            buttonDeleteProduct = itemView.findViewById(R.id.buttonDeleteProduct);
        }

        public void bind(Product product) {
            textViewProductName.setText(product.getName());
            textViewProductPrice.setText(String.format("Giá: %,.0f VNĐ", product.getPrice()));
            imageViewProduct.setImageResource(product.getImageResourceId());

            // Find category name
            String categoryName = "Chưa phân loại";
            for (Category category : categories) {
                if (category.getCategoryId() == product.getProductCategoryId()) {
                    categoryName = category.getName();
                    break;
                }
            }
            textViewProductCategory.setText("Danh mục: " + categoryName);

            buttonEditProduct.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEditProduct(product);
                }
            });

            buttonDeleteProduct.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteProduct(product);
                }
            });
        }
    }
} 