package com.example.project_prm392.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.project_prm392.DAO.AppDatabase;
import com.example.project_prm392.R;
import com.example.project_prm392.model.Category;
import com.example.project_prm392.model.Product;
import com.example.project_prm392.ui.adapter.CategoryAdapter;
import com.example.project_prm392.ui.adapter.ProductAdapter;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements CategoryAdapter.OnCategoryClickListener {

    private RecyclerView recyclerCategories, recyclerProducts;
    private CategoryAdapter categoryAdapter;
    private ProductAdapter productAdapter;
    private AppDatabase appDatabase;
    private EditText searchEditText;

    // Các biến để quản lý việc quan sát LiveData
    private LiveData<List<Product>> currentProductLiveData;
    private Observer<List<Product>> productObserver;
    private Integer currentCategoryId = null;

    public HomeFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        appDatabase = AppDatabase.getDatabase(requireContext());
        recyclerCategories = view.findViewById(R.id.recyclerViewCategories);
        recyclerProducts = view.findViewById(R.id.recyclerViewBestSellers);
        searchEditText = view.findViewById(R.id.searchEditText);

        setupRecyclerViews();
        loadCategories();
        // Tải tất cả sản phẩm lúc ban đầu
        filterProducts(null);
        setupSearch();
    }

    private void setupRecyclerViews() {
        // Setup Category RecyclerView
        recyclerCategories.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        categoryAdapter = new CategoryAdapter(new ArrayList<>(), this);
        recyclerCategories.setAdapter(categoryAdapter);

        // Setup Product RecyclerView
        recyclerProducts.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerProducts.setNestedScrollingEnabled(false);
        productAdapter = new ProductAdapter(new ArrayList<>());
        recyclerProducts.setAdapter(productAdapter);
    }

    private void setupSearch() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                // Khi người dùng nhập chữ, thực hiện tìm kiếm
                String query = s.toString().trim();
                performSearch(query);
            }
        });
    }

    private void loadCategories() {
        appDatabase.shopDao().getAllCategories().observe(getViewLifecycleOwner(), categories -> {
            if (categories != null) {
                // Tạo một danh sách mới để có thể thêm mục "Tất cả"
                ArrayList<Category> displayList = new ArrayList<>();
                // Thêm mục "Tất cả" vào đầu danh sách
                displayList.add(new Category("Tất cả", R.drawable.ic_launcher_background));
                displayList.addAll(categories);

                categoryAdapter.setCategories(displayList);
            }
        });
    }

    private void performSearch(String query) {
        // Hủy quan sát luồng cũ
        if (currentProductLiveData != null && productObserver != null) {
            currentProductLiveData.removeObserver(productObserver);
        }

        // Tạo observer mới
        productObserver = products -> {
            if (products != null) {
                productAdapter.setProducts(products);
            }
        };

        if (query.isEmpty()) {
            // Nếu ô tìm kiếm trống, quay lại lọc theo danh mục đã chọn
            filterProducts(currentCategoryId);
        } else {
            // Nếu có từ khóa, thực hiện tìm kiếm
            currentProductLiveData = appDatabase.shopDao().searchProducts(query);
            currentProductLiveData.observe(getViewLifecycleOwner(), productObserver);
        }
    }

    // Được gọi khi nhấn vào một danh mục
    @Override
    public void onCategoryClick(Category category) {
        searchEditText.setText("");
        // categoryId của "Tất cả" là 0 vì nó không được lấy từ DB
        if (category.categoryId == 0) {
            filterProducts(null); // Truyền null để lấy tất cả sản phẩm
        } else {
            filterProducts(category.categoryId);
        }
    }

    /**
     * Hàm lọc sản phẩm. Sẽ hủy quan sát luồng dữ liệu cũ và bắt đầu quan sát luồng mới.
     */
    private void filterProducts(Integer categoryId) {
        // Hủy quan sát luồng LiveData cũ nếu có
        if (currentProductLiveData != null && productObserver != null) {
            currentProductLiveData.removeObserver(productObserver);
        }

        // Tạo một Observer mới
        productObserver = products -> {
            if (products != null) {
                productAdapter.setProducts(products);
            }
        };

        // Lấy luồng LiveData mới dựa trên categoryId
        if (categoryId == null) {
            currentProductLiveData = appDatabase.shopDao().getAllProducts();
        } else {
            currentProductLiveData = appDatabase.shopDao().getProductsByCategory(categoryId);
        }

        // Bắt đầu quan sát luồng LiveData mới
        currentProductLiveData.observe(getViewLifecycleOwner(), productObserver);
    }
}