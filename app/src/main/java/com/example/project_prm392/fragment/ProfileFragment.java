package com.example.project_prm392.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project_prm392.R;
import com.example.project_prm392.ui.auth.LoginActivity;
import com.example.project_prm392.ui.cart.OrderHistoryActivity;
import com.example.project_prm392.ui.profile.AddressBookActivity;
import com.example.project_prm392.ui.profile.SettingsActivity;
import com.example.project_prm392.ui.admin.AdminProductListActivity;
import com.example.project_prm392.utils.AdminUtils;

public class ProfileFragment extends Fragment {

    private LinearLayout loggedInView;
    private ImageView avatarImageView;
    private TextView userNameTextView, userEmailTextView;
    private TextView customerSupportTextView;

    private TextView orderHistoryTextView, addressBookTextView, settingsTextView;
    private TextView adminProductManagementTextView;
    private Button logoutButton;

    private LinearLayout loggedOutView;
    private Button loginButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Ánh xạ tất cả các views
        initViews(view);

        // Kiểm tra trạng thái đăng nhập
        checkLoginStatus();
    }

    private void initViews(View view) {
        // Views cho trạng thái đã đăng nhập
        loggedInView = view.findViewById(R.id.loggedInView);
        avatarImageView = view.findViewById(R.id.avatarImageView);
        userNameTextView = view.findViewById(R.id.userNameTextView);
        userEmailTextView = view.findViewById(R.id.userEmailTextView);
        orderHistoryTextView = view.findViewById(R.id.orderHistoryTextView);
        addressBookTextView = view.findViewById(R.id.addressBookTextView);
        settingsTextView = view.findViewById(R.id.settingsTextView);
        adminProductManagementTextView = view.findViewById(R.id.adminProductManagementTextView);
        customerSupportTextView = view.findViewById(R.id.customerSupportTextView);
        logoutButton = view.findViewById(R.id.logoutButton);

        // Views cho trạng thái chưa đăng nhập
        loggedOutView = view.findViewById(R.id.loggedOutView);
        loginButton = view.findViewById(R.id.loginButton);
    }

    private void checkLoginStatus() {
        // Sử dụng SharedPreferences để kiểm tra
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("USER_SESSION", Context.MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        if (isLoggedIn) {
            // Hiển thị giao diện đã đăng nhập
            loggedInView.setVisibility(View.VISIBLE);
            loggedOutView.setVisibility(View.GONE);

            // Tải thông tin user và gán sự kiện
            loadUserProfile();
            setupClickListenersForLoggedInUser();
        } else {
            // Hiển thị giao diện chưa đăng nhập
            loggedInView.setVisibility(View.GONE);
            loggedOutView.setVisibility(View.VISIBLE);

            // Gán sự kiện cho nút đăng nhập
            loginButton.setOnClickListener(v -> {
                startActivity(new Intent(requireContext(), LoginActivity.class));
            });
        }
    }

    private void setupClickListenersForLoggedInUser() {
        orderHistoryTextView.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), OrderHistoryActivity.class)));

        addressBookTextView.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), AddressBookActivity.class)));

        settingsTextView.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), SettingsActivity.class));
        });

        adminProductManagementTextView.setOnClickListener(v -> {
            AdminUtils.checkAdminRole(requireContext(), new AdminUtils.AdminRoleCallback() {
                @Override
                public void onAdmin() {
                    startActivity(new Intent(requireContext(), AdminProductListActivity.class));
                }

                @Override
                public void onNotAdmin(String message) {
                    Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
                }
            });
        });

        customerSupportTextView.setOnClickListener(v -> showCustomerSupportDialog());

        logoutButton.setOnClickListener(v -> logoutUser());
    }

    private void loadUserProfile() {
        // Lấy thông tin người dùng từ SharedPreferences đã được lưu khi đăng nhập
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("USER_SESSION", Context.MODE_PRIVATE);
        String userName = sharedPreferences.getString("userName", "Người dùng"); // Giá trị mặc định
        String userEmail = sharedPreferences.getString("userEmail", "email@example.com"); // Giá trị mặc định

        // Gán dữ liệu lên giao diện
        userNameTextView.setText(userName);
        userEmailTextView.setText(userEmail);

        // Gán ảnh đại diện
        avatarImageView.setImageResource(R.drawable.ic_profile);

        // Check if user is admin and show admin options
        checkAdminRole();
    }

    private void checkAdminRole() {
        AdminUtils.checkAdminRole(requireContext(), new AdminUtils.AdminRoleCallback() {
            @Override
            public void onAdmin() {
                adminProductManagementTextView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onNotAdmin(String message) {
                adminProductManagementTextView.setVisibility(View.GONE);
            }
        });
    }

    private void logoutUser() {
        // Xóa trạng thái đăng nhập trong SharedPreferences
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("USER_SESSION", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        // Chuyển về màn hình Login
        Intent intent = new Intent(requireActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }

    private void showCustomerSupportDialog() {
        final String phoneNumber = "19001009";

        new AlertDialog.Builder(requireContext())
                .setTitle("Chăm sóc khách hàng")
                .setMessage("Bạn có muốn gọi đến tổng đài " + phoneNumber + " không?")
                .setIcon(android.R.drawable.ic_dialog_dialer)
                .setPositiveButton("Gọi", (dialog, which) -> {
                    // Tạo Intent để mở trình quay số
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + phoneNumber));
                    startActivity(intent);
                })
                .setNegativeButton("Hủy", (dialog, which) -> {
                    // Đóng hộp thoại nếu người dùng chọn Hủy
                    dialog.dismiss();
                })
                .show();
    }
}