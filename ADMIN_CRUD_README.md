# Admin CRUD Product Management

This document describes the implementation of CRUD (Create, Read, Update, Delete) operations for products with admin role functionality in the PRM392_Project Android application.

## Features Implemented

### 1. User Role System
- Added `role` field to `UserEntity` model
- Default role is "user" for new registrations
- Admin role is "admin"
- Sample admin user: `admin@example.com` / `admin123`

### 2. Admin Authentication
- `AdminUtils` class for role checking
- Automatic role verification before accessing admin features
- UI callbacks for admin/non-admin responses

### 3. Product Management Activities

#### AdminProductListActivity
- Displays all products in a RecyclerView
- Search functionality to filter products by name
- Edit and Delete buttons for each product
- Add Product button to create new products
- Admin role verification on startup

#### AdminProductFormActivity
- Form for adding new products
- Form for editing existing products
- Category selection via Spinner
- Image selection (demo with predefined options)
- Input validation for all fields
- Admin role verification on startup

### 4. Database Updates
- Updated `ShopDao` with CRUD operations for products:
  - `updateProduct(Product product)`
  - `deleteProduct(Product product)`
  - `getProductByIdNow(int id)` for immediate access
- Added admin role checking queries
- Database version updated to 2

### 5. UI Components
- Admin product list item layout (`item_admin_product.xml`)
- Admin product form layout (`activity_admin_product_form.xml`)
- Admin product list layout (`activity_admin_product_list.xml`)
- Admin menu option in ProfileFragment (only visible to admins)

## How to Use

### For Admin Users:
1. Login with admin credentials: `admin@example.com` / `admin123`
2. Navigate to Profile section
3. Click "Quản lý sản phẩm (Admin)" option
4. Use the product management interface:
   - **View**: All products are displayed in a list
   - **Search**: Use the search bar to filter products
   - **Add**: Click "+ Thêm" button to create new products
   - **Edit**: Click "Sửa" button on any product
   - **Delete**: Click "Xóa" button on any product (with confirmation)

### For Regular Users:
- Admin features are hidden from the UI
- Attempting to access admin URLs directly will show permission denied message

## Technical Implementation

### Key Classes:
- `AdminUtils`: Utility class for role checking
- `AdminProductAdapter`: RecyclerView adapter for product list
- `AdminProductListActivity`: Main admin product management screen
- `AdminProductFormActivity`: Add/Edit product form
- Updated `UserEntity`, `Product`, `ShopDao`, and `AppDatabase`

### Security Features:
- Role-based access control
- Admin role verification on every admin activity
- Database operations on background threads
- Input validation for all forms

### Database Schema Changes:
- Added `role` column to `users` table
- Database version increased to 2
- Fallback migration strategy implemented

## Testing

To test the admin functionality:

1. **Install and run the app**
2. **Login as admin**: Use `admin@example.com` / `admin123`
3. **Access admin features**: Go to Profile → "Quản lý sản phẩm (Admin)"
4. **Test CRUD operations**:
   - Create new products
   - Edit existing products
   - Delete products
   - Search for products

## Future Enhancements

1. **Image Upload**: Implement actual image picker functionality
2. **Category Management**: Add CRUD operations for categories
3. **User Management**: Add admin interface for managing users
4. **Order Management**: Add admin interface for managing orders
5. **Analytics**: Add sales and inventory analytics
6. **Bulk Operations**: Add bulk import/export functionality

## Notes

- The current implementation uses predefined drawable resources for product images
- Database migration uses `fallbackToDestructiveMigration()` for simplicity
- All database operations are performed on background threads
- UI updates are properly handled on the main thread 