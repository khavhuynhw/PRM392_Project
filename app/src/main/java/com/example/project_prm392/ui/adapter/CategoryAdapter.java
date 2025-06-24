package com.example.project_prm392.ui.adapter;

import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_prm392.R;
import com.example.project_prm392.model.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private ArrayList<Category> categories;
    private OnCategoryClickListener listener;
    private int selectedPosition = 0; // Vị trí "Tất cả" được chọn mặc định

    // Interface để gửi sự kiện click ngược lại Fragment
    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
    }

    public CategoryAdapter(ArrayList<Category> categories, OnCategoryClickListener listener) {
        this.categories = categories;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categories.get(position);
        holder.name.setText(category.getName());
        holder.image.setImageResource(category.getImageResourceId());

        // Làm nổi bật item được chọn
        if (selectedPosition == position) {
            holder.itemView.setBackgroundResource(R.drawable.category_selected_background);
            holder.name.setTypeface(null, Typeface.BOLD);
        } else {
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
            holder.name.setTypeface(null, Typeface.NORMAL);
        }

        // Gán sự kiện click
        holder.itemView.setOnClickListener(v -> {
            listener.onCategoryClick(category);
            int previousSelected = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(previousSelected);
            notifyItemChanged(selectedPosition);
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name;
        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.categoryImageView);
            name = itemView.findViewById(R.id.categoryNameTextView);
        }
    }

    public void setCategories(List<Category> newCategories) {
        // Xóa danh sách cũ
        this.categories.clear();
        // Thêm toàn bộ danh sách mới
        this.categories.addAll(newCategories);
        // Thông báo cho RecyclerView rằng dữ liệu đã thay đổi
        notifyDataSetChanged();
    }
}
