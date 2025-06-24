package com.example.project_prm392.ui.home;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_prm392.R;
import com.example.project_prm392.fragment.CartFragment;
import com.example.project_prm392.fragment.HomeFragment;
import com.example.project_prm392.fragment.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;

public class HomeActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // --- Thiết lập Toolbar ---
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // --- Thiết lập Navigation Drawer ---
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        // Thiết lập Toggle (nút hamburger) để mở/đóng drawer
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        // Xử lý sự kiện click trên các item của navigation view
        navigationView.setNavigationItemSelectedListener(item -> {
            selectDrawerItem(item);
            return true;
        });

        // Load Fragment mặc định
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
            navigationView.setCheckedItem(R.id.nav_home);
        }
    }

    // Hàm xử lý khi một item trong drawer được chọn
    private void selectDrawerItem(MenuItem menuItem) {
        Fragment fragment = null;
        int itemId = menuItem.getItemId();

        if (itemId == R.id.nav_home) {
            fragment = new HomeFragment();
        } else if (itemId == R.id.nav_cart) {
            fragment = new CartFragment();
        } else if (itemId == R.id.nav_profile) {
            fragment = new ProfileFragment();
        }

        if (fragment != null) {
            loadFragment(fragment);
        }

        // Đóng ngăn kéo sau khi chọn
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    // Hàm để thay đổi Fragment
    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }

    // Hiển thị icon chuông thông báo trên Toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    // Xử lý sự kiện khi nhấn vào icon chuông
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_notifications) {
            // Lấy View của icon chuông để PopupMenu neo vào
            View anchorView = findViewById(R.id.action_notifications);
            // Hiển thị popup
            showNotificationsPopup(anchorView);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showNotificationsPopup(View anchorView) {
        PopupMenu popup = new PopupMenu(this, anchorView);
        // "Thổi" file menu vào popup
        popup.getMenuInflater().inflate(R.menu.popup_notifications_menu, popup.getMenu());

        // Gán sự kiện click cho từng item trong popup
        popup.setOnMenuItemClickListener(menuItem -> {
            int itemId = menuItem.getItemId();
            if (itemId == R.id.notification_order_1) {
                Toast.makeText(this, "Xem chi tiết đơn hàng #G12399", Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.notification_promo_1) {
                Toast.makeText(this, "Xem chi tiết khuyến mãi", Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.notification_welcome) {
                Toast.makeText(this, "Cảm ơn bạn đã sử dụng app!", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });

        // Hiển thị popup
        popup.show();
    }

    // Xử lý nút back của hệ thống (nếu drawer đang mở thì đóng lại)
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}