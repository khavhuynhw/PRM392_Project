package com.example.project_prm392.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_prm392.R;
import com.example.project_prm392.model.Address;

import java.util.List;

public class AddressSelectionAdapter extends RecyclerView.Adapter<AddressSelectionAdapter.ViewHolder> {

    private final List<Address> addressList;
    private final OnAddressSelectedListener listener;

    // Interface để gửi sự kiện click ngược lại Activity
    public interface OnAddressSelectedListener {
        void onAddressSelected(Address address);
    }

    public AddressSelectionAdapter(List<Address> addressList, OnAddressSelectedListener listener) {
        this.addressList = addressList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Sử dụng một layout item đơn giản hơn cho dialog
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_address_selection, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Address address = addressList.get(position);
        holder.recipientName.setText(address.getRecipientName() + " | " + address.getPhone());
        String fullAddress = address.getStreetAddress() + ", " + address.getCity();
        holder.addressLine.setText(fullAddress);

        // Gán sự kiện click cho toàn bộ item
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAddressSelected(address);
            }
        });
    }

    @Override
    public int getItemCount() {
        return addressList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView recipientName, addressLine;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            recipientName = itemView.findViewById(R.id.dialogRecipientNameTextView);
            addressLine = itemView.findViewById(R.id.dialogAddressLineTextView);
        }
    }
}