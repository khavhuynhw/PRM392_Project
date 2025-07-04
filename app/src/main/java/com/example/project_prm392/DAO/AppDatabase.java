package com.example.project_prm392.DAO;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.project_prm392.R;
import com.example.project_prm392.model.Address;
import com.example.project_prm392.model.CartItem;
import com.example.project_prm392.model.Category;
import com.example.project_prm392.model.Order;
import com.example.project_prm392.model.OrderProductCrossRef;
import com.example.project_prm392.model.Product;
import com.example.project_prm392.model.UserEntity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {UserEntity.class, Category.class, Product.class, Address.class, CartItem.class, Order.class, OrderProductCrossRef.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract ShopDao shopDao();

    private static volatile AppDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "shop_database")
                            .addCallback(sRoomDatabaseCallback)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * Callback này sẽ được gọi khi database được tạo lần đầu tiên.
     */
    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            databaseWriteExecutor.execute(() -> {
                ShopDao dao = INSTANCE.shopDao();

                // Xóa tất cả dữ liệu cũ để đảm bảo sạch sẽ
                dao.deleteAllUsers();
                dao.deleteAllCategories();
                dao.deleteAllProducts();
                dao.deleteAllAddresses();
                dao.deleteAllOrders();
                dao.deleteAllOrderProductCrossRef();


                // --- 1. Thêm Người dùng mẫu ---
                UserEntity user = new UserEntity();
                user.fullName = "Trần Thị Bích Hảo";
                user.email = "bichhao.dev@example.com";
                user.password = "123456"; // Nhớ mã hóa trong ứng dụng thật
                user.role = "user"; // Default role
                // Giả sử sau khi insert, user này sẽ có userId là 1
                dao.insertUser(user);

                // Thêm admin user
                UserEntity adminUser = new UserEntity();
                adminUser.fullName = "Admin User";
                adminUser.email = "admin@example.com";
                adminUser.password = "admin123";
                adminUser.role = "admin";
                dao.insertUser(adminUser);


                // --- 2. Thêm Địa chỉ mẫu cho người dùng (userId = 1) ---
                Address address1 = new Address("Trần Thị Bích Hảo (Nhà)", "0987654321", "123 Đường ABC, Phường 1, Quận 1", "TP. Hồ Chí Minh", true, 1);
                Address address2 = new Address("Trần Thị Bích Hảo (Công ty)", "0123456789", "Tòa nhà Bitexco, Tầng 50", "TP. Hồ Chí Minh", false, 1);
                dao.insertAddress(address1);
                dao.insertAddress(address2);


                // --- 3. Thêm Danh mục mẫu (Thêm mới) ---
                dao.insertCategory(new Category("Rau Củ", R.drawable.ic_launcher_background));    // id = 1
                dao.insertCategory(new Category("Trái Cây", R.drawable.ic_launcher_background));     // id = 2
                dao.insertCategory(new Category("Thịt, Cá", R.drawable.ic_launcher_background));       // id = 3
                dao.insertCategory(new Category("Đồ Khô", R.drawable.ic_launcher_background));      // id = 4
                dao.insertCategory(new Category("Gia Vị", R.drawable.ic_launcher_background));       // id = 5

                // --- 4. Thêm Sản phẩm mẫu (Thêm mới) ---
                // Rau Củ (categoryId = 1)
                dao.insertProduct(new Product("Cải Thìa", 15000, "/ bó", R.drawable.ic_launcher_background, 1));
                dao.insertProduct(new Product("Bí Đỏ", 12000, "/ kg", R.drawable.ic_launcher_background, 1));
                dao.insertProduct(new Product("Cà Rốt", 18000, "/ kg", R.drawable.ic_launcher_background, 1));

                // Trái Cây (categoryId = 2)
                dao.insertProduct(new Product("Cam Sành", 35000, "/ kg", R.drawable.ic_launcher_background, 2));
                dao.insertProduct(new Product("Táo Envy", 80000, "/ kg", R.drawable.ic_launcher_background, 2));

                // Thịt, Cá (categoryId = 3)
                dao.insertProduct(new Product("Thịt Ba Rọi", 120000, "/ kg", R.drawable.ic_launcher_background, 3));
                dao.insertProduct(new Product("Cá Diêu Hồng", 60000, "/ con", R.drawable.ic_launcher_background, 3));

                // Đồ Khô (categoryId = 4)
                dao.insertProduct(new Product("Gạo ST25", 150000, "/ 5kg", R.drawable.ic_launcher_background, 4));
                dao.insertProduct(new Product("Miến dong", 25000, "/ gói", R.drawable.ic_launcher_background, 4));

                // Gia Vị (categoryId = 5)
                dao.insertProduct(new Product("Nước mắm Nam Ngư", 30000, "/ chai", R.drawable.ic_launcher_background, 5));
                dao.insertProduct(new Product("Bột ngọt Ajinomoto", 22000, "/ gói", R.drawable.ic_launcher_background, 5));

                // --- 5. Thêm Đơn hàng mẫu cho người dùng (userId = 1) ---
                // Lưu ý: orderDate nên lưu dưới dạng Long (timestamp) để dễ sắp xếp
                String name = "Trần Thị Bích Hảo";
                String phone = "0987654321";
                String address = "123 Đường ABC, P.1, Q.1, TP. HCM";
                Order order1 = new Order(System.currentTimeMillis() - 86400000, 170000, "Đã giao", 1, 20000, name, address, phone); // id = 1
                Order order2 = new Order(System.currentTimeMillis(), 67000, "Đang xử lý", 1, 20000, name, address, phone); // id = 2
                dao.insertOrder(order1);
                dao.insertOrder(order2);


                // --- 6. Thêm chi tiết cho đơn hàng (bảng nối) ---
                // Đơn hàng 1 có 2 sản phẩm (id 1 và 4)
                dao.insertOrderProductCrossRef(new OrderProductCrossRef(1, 1, 2)); // 2 bó Cải Thìa
                dao.insertOrderProductCrossRef(new OrderProductCrossRef(1, 4, 1)); // 1 kg Thịt Ba Rọi
                // Đơn hàng 2 có 2 sản phẩm (id 2 và 3)
                dao.insertOrderProductCrossRef(new OrderProductCrossRef(2, 2, 1)); // 1 kg Bí Đỏ
                dao.insertOrderProductCrossRef(new OrderProductCrossRef(2, 3, 1)); // 1 kg Cam Sành
            });
        }
    };
}

