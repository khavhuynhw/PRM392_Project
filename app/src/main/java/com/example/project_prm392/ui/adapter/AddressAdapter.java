package com.example.project_prm392.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_prm392.DAO.AppDatabase;
import com.example.project_prm392.R;
import com.example.project_prm392.model.Address;
import com.example.project_prm392.ui.profile.AddAddressActivity;

import java.util.ArrayList;
import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressViewHolder> {
    private List<Address> addressList;
    private AppDatabase appDatabase; // Thêm biến database
    private int currentUserId;

    public AddressAdapter(List<Address> addressList, int currentUserId) {
        this.addressList = addressList;
        this.currentUserId = currentUserId;
    }

    @NonNull
    @Override
    public AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Khởi tạo database
        appDatabase = AppDatabase.getDatabase(parent.getContext());
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_address, parent, false);
        return new AddressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressViewHolder holder, int position) {
        Address address = addressList.get(position);
        holder.recipientName.setText(address.getRecipientName() + " | " + address.getPhone());
        String fullAddress = address.getStreetAddress() + ", " + address.getCity();
        holder.addressLine.setText(fullAddress);
        holder.defaultTag.setVisibility(address.isDefault() ? View.VISIBLE : View.GONE);

        if (address.isDefault()) {
            // Nếu là mặc định: hiển thị tag, ẩn nút
            holder.defaultTag.setVisibility(View.VISIBLE);
            holder.setDefaultButton.setVisibility(View.GONE);
        } else {
            // Nếu không phải mặc định: ẩn tag, hiển thị nút
            holder.defaultTag.setVisibility(View.GONE);
            holder.setDefaultButton.setVisibility(View.VISIBLE);
        }

        // Gán sự kiện cho nút "Đặt làm mặc định"
        holder.setDefaultButton.setOnClickListener(v -> {
            AppDatabase.databaseWriteExecutor.execute(() -> {
                // Gọi hàm transaction mới trong DAO
                appDatabase.shopDao().setDefaultAddress(currentUserId, address.addressId);
            });
            // LiveData sẽ tự động cập nhật lại giao diện, không cần làm gì thêm
        });

        // Xử lý sự kiện nút SỬA
        holder.editButton.setOnClickListener(v -> {
            Context context = v.getContext();
            Intent intent = new Intent(context, AddAddressActivity.class);
            intent.putExtra("EDIT_ADDRESS", address); // Gửi đối tượng Address cần sửa
            context.startActivity(intent);
        });

        // Xử lý sự kiện nút XÓA
        holder.deleteButton.setOnClickListener(v -> {
            new AlertDialog.Builder(v.getContext())
                    .setTitle("Xác nhận xóa")
                    .setMessage("Bạn có chắc muốn xóa địa chỉ này?")
                    .setPositiveButton("Xóa", (dialog, which) -> {
                        // Thực hiện xóa trên luồng nền
                        AppDatabase.databaseWriteExecutor.execute(() -> {
                            appDatabase.shopDao().deleteAddress(address);
                        });
                        // Không cần gọi notify... nữa, LiveData sẽ tự động cập nhật
                    })
                    .setNegativeButton("Hủy", null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return addressList.size();
    }

    public void setAddresses(List<Address> newAddressList) {
        this.addressList = newAddressList;
        notifyDataSetChanged();
    }

    public static class AddressViewHolder extends RecyclerView.ViewHolder {
        TextView recipientName, addressLine, defaultTag;
        ImageButton editButton, deleteButton;
        Button setDefaultButton;

        public AddressViewHolder(@NonNull View itemView) {
            super(itemView);
            recipientName = itemView.findViewById(R.id.recipientNameTextView);
            addressLine = itemView.findViewById(R.id.addressLineTextView);
            defaultTag = itemView.findViewById(R.id.defaultAddressTag);
            editButton = itemView.findViewById(R.id.editAddressButton);
            deleteButton = itemView.findViewById(R.id.deleteAddressButton);
            setDefaultButton = itemView.findViewById(R.id.setDefaultButton);
        }
    }
}
