package com.example.project_prm392.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;

import com.example.project_prm392.DAO.AppDatabase;
import com.example.project_prm392.model.UserEntity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AdminUtils {
    
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    private static final Handler mainHandler = new Handler(Looper.getMainLooper());
    
    public static boolean isAdmin(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("USER_SESSION", Context.MODE_PRIVATE);
        int userId = sharedPreferences.getInt("userId", -1);
        
        if (userId == -1) {
            return false;
        }
        
        AppDatabase database = AppDatabase.getDatabase(context);
        UserEntity user = database.shopDao().getUserByIdForRoleCheck(userId);
        
        return user != null && "admin".equals(user.role);
    }
    
    public static void checkAdminRole(Context context, AdminRoleCallback callback) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("USER_SESSION", Context.MODE_PRIVATE);
        int userId = sharedPreferences.getInt("userId", -1);
        
        if (userId == -1) {
            callback.onNotAdmin("Vui lòng đăng nhập để truy cập tính năng này");
            return;
        }
        
        executor.execute(() -> {
            AppDatabase database = AppDatabase.getDatabase(context);
            UserEntity user = database.shopDao().getUserByIdForRoleCheck(userId);
            
            if (user != null && "admin".equals(user.role)) {
                mainHandler.post(callback::onAdmin);
            } else {
                mainHandler.post(() -> callback.onNotAdmin("Bạn không có quyền truy cập tính năng này"));
            }
        });
    }
    
    public interface AdminRoleCallback {
        void onAdmin();
        void onNotAdmin(String message);
    }
} 