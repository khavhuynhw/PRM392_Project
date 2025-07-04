package com.example.project_prm392.DAO;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.project_prm392.model.Address;
import com.example.project_prm392.model.CartItem;
import com.example.project_prm392.model.CartItemWithProduct;
import com.example.project_prm392.model.Category;
import com.example.project_prm392.model.Order;
import com.example.project_prm392.model.OrderProductCrossRef;
import com.example.project_prm392.model.OrderWithProducts;
import com.example.project_prm392.model.Product;
import com.example.project_prm392.model.UserEntity;

import java.util.List;

@Dao
public interface ShopDao {

    //================== UserEntity CRUD ==================
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertUser(UserEntity user);

    @Update
    void updateUser(UserEntity user);

    @Delete
    void deleteUser(UserEntity user);

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    UserEntity findUserByEmail(String email);

    @Query("SELECT * FROM users WHERE userId = :id LIMIT 1")
    LiveData<UserEntity> getUserById(int id);

    @Query("SELECT * FROM users WHERE userId = :id LIMIT 1")
    UserEntity getUserByIdNow(int id);

    //================== Category CRUD ==================
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertCategory(Category category);

    @Query("SELECT * FROM categories ORDER BY name ASC")
    LiveData<List<Category>> getAllCategories();


    //================== Product CRUD ==================
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertProduct(Product product);

    @Update
    void updateProduct(Product product);

    @Delete
    void deleteProduct(Product product);

    @Query("SELECT * FROM products ORDER BY name ASC")
    LiveData<List<Product>> getAllProducts();

    @Query("SELECT * FROM products WHERE productCategoryId = :categoryId ORDER BY name ASC")
    LiveData<List<Product>> getProductsByCategory(int categoryId);

    @Query("SELECT * FROM products WHERE productId = :id LIMIT 1")
    LiveData<Product> getProductById(int id);

    @Query("SELECT * FROM products WHERE productId = :id LIMIT 1")
    Product getProductByIdNow(int id);

    @Query("SELECT * FROM products WHERE name LIKE '%' || :query || '%'")
    LiveData<List<Product>> searchProducts(String query);


    //================== Address CRUD ==================
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAddress(Address address);

    @Update
    void updateAddress(Address address);

    @Delete
    void deleteAddress(Address address);

    @Query("SELECT * FROM addresses WHERE ownerUserId = :userId ORDER BY isDefault DESC")
    LiveData<List<Address>> getAddressesForUser(int userId);

    @Query("UPDATE addresses SET isDefault = 0 WHERE ownerUserId = :userId")
    void clearDefaultAddress(int userId);

    @Query("UPDATE addresses SET isDefault = 1 WHERE addressId = :newDefaultAddressId")
    void setNewDefaultAddress(int newDefaultAddressId);

    @Query("SELECT * FROM addresses WHERE ownerUserId = :userId AND isDefault = 1 LIMIT 1")
    LiveData<Address> getDefaultAddress(int userId);

    // THÊM HÀM MỚI NÀY
    /**
     * Hàm transaction để đảm bảo việc đặt địa chỉ mặc định luôn đúng.
     * Nó sẽ chạy 2 câu lệnh: tắt tất cả các địa chỉ cũ, sau đó bật địa chỉ mới.
     */
    @Transaction
    default void setDefaultAddress(int userId, int newDefaultAddressId) {
        clearDefaultAddress(userId);
        setNewDefaultAddress(newDefaultAddressId);
    }


    //================== Order CRUD ==================
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insertOrder(Order order); // Trả về long là ID của đơn hàng vừa thêm

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertOrderProductCrossRef(OrderProductCrossRef crossRef);

    @Transaction
    @Query("SELECT * FROM orders WHERE buyerUserId = :userId ORDER BY orderDate DESC")
    LiveData<List<OrderWithProducts>> getOrdersForUser(int userId);

    // HÀM MỚI: Xử lý toàn bộ quá trình đặt hàng
    @Transaction
    default void placeOrder(Order order, List<CartItemWithProduct> cartItems) {
        // 1. Thêm đơn hàng mới và lấy ID của nó
        long orderId = insertOrder(order);

        // 2. Thêm các sản phẩm vào bảng nối Order-Product
        if (cartItems != null) {
            for (CartItemWithProduct item : cartItems) {
                OrderProductCrossRef crossRef = new OrderProductCrossRef(orderId, item.product.productId, item.cartItem.quantity);
                insertOrderProductCrossRef(crossRef);
            }
        }

        // 3. Xóa giỏ hàng của người dùng
        clearCartForUser(order.buyerUserId);
    }


//================== CartItem CRUD (Hoàn chỉnh) ==================

    // Dùng để kiểm tra xem sản phẩm đã có trong giỏ hàng chưa
    @Query("SELECT * FROM cart_items WHERE userId = :userId AND productId = :productId LIMIT 1")
    CartItem getCartItemByUserAndProduct(int userId, int productId);

    // Thêm một sản phẩm mới vào giỏ
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertCartItem(CartItem item);

    // Cập nhật một sản phẩm trong giỏ (thường là thay đổi số lượng)
    @Update
    void updateCartItem(CartItem item);

    // Xóa một sản phẩm khỏi giỏ
    @Delete
    void deleteCartItem(CartItem item);

    // Lấy toàn bộ giỏ hàng của một người dùng (với đầy đủ thông tin sản phẩm)
    @Transaction
    @Query("SELECT * FROM cart_items WHERE userId = :userId")
    LiveData<List<CartItemWithProduct>> getCartWithProducts(int userId);

    @Query("DELETE FROM cart_items WHERE userId = :userId")
    void clearCartForUser(int userId);


    //================== Các hàm dọn dẹp (cho dữ liệu mẫu) ==================
    @Query("DELETE FROM users")
    void deleteAllUsers();

    @Query("DELETE FROM categories")
    void deleteAllCategories();

    @Query("DELETE FROM products")
    void deleteAllProducts();

    @Query("DELETE FROM addresses")
    void deleteAllAddresses();

    @Query("DELETE FROM orders")
    void deleteAllOrders();

    @Query("DELETE FROM order_product_cross_ref")
    void deleteAllOrderProductCrossRef();

    //================== Admin Role Checking ==================
    @Query("SELECT role FROM users WHERE userId = :userId LIMIT 1")
    String getUserRole(int userId);

    @Query("SELECT * FROM users WHERE userId = :userId LIMIT 1")
    UserEntity getUserByIdForRoleCheck(int userId);

    @Transaction
    @Query("SELECT * FROM orders ORDER BY orderDate DESC")
    LiveData<List<OrderWithProducts>> getAllOrdersWithProducts();

    @Query("UPDATE orders SET status = :newStatus WHERE orderId = :orderId")
    void updateOrderStatus(long orderId, String newStatus);
}
