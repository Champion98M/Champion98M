package com.example.biggers;

import android.annotation.SuppressLint;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class AccountAdaper extends RecyclerView.Adapter<AccountAdaper.AccountViewHolder> implements Filterable {
    public ArrayList<Account> mAccountList;
    public ArrayList<Account> mCopyaAccountList;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onAdminButtonClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public static class AccountViewHolder extends RecyclerView.ViewHolder {

        public TextView mUsernameTextView;
        public Button mAdminButton;
        public ImageView imageView;

        @SuppressLint("ClickableViewAccessibility")
        public AccountViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            mUsernameTextView = itemView.findViewById(R.id.username_manager);
            mAdminButton = itemView.findViewById(R.id.admin_button_manager);
            imageView = itemView.findViewById(R.id.image_view);

            mUsernameTextView.setOnTouchListener((v, event) -> {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            });


            mUsernameTextView.setMovementMethod(new ScrollingMovementMethod());
            mUsernameTextView.setHorizontallyScrolling(true);

            mAdminButton.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getBindingAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onAdminButtonClick(position);
                        if(mAdminButton.getText().toString().equals("Abilita Admin")) {
                            mAdminButton.setText("Disabilita Admin");
                            imageView.setImageResource(R.drawable.crown_icon);
                        } else {
                            mAdminButton.setText("Abilita Admin");
                            imageView.setImageResource(R.drawable.account_icon);
                        }
                    }
                }
            });
        }
    }

    public AccountAdaper(ArrayList<Account> accountArrayList) {
        mAccountList = accountArrayList;
        mCopyaAccountList = new ArrayList<>(accountArrayList);
    }

    @NonNull
    @Override
    public AccountViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.account, parent, false);
        return new AccountViewHolder(v, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull AccountViewHolder holder, int position) {
        Account currentAccount = mAccountList.get(position);

        holder.mUsernameTextView.setText(currentAccount.getUsername());

        if(currentAccount.isAdmin()) {
            holder.mAdminButton.setText("Disabilita Admin");
            holder.imageView.setImageResource(R.drawable.crown_icon);
        } else {
            holder.imageView.setImageResource(R.drawable.account_icon);
        }
    }

    @Override
    public int getItemCount() {
        return mAccountList.size();
    }

    public Account getAccountAt(int position) {
        return mAccountList.get(position);
    }

    public void filterList(ArrayList<Account> filteredList) {
        mAccountList = filteredList;
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return accountFilter;
    }

    private final Filter accountFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Account> filteredList = new ArrayList<>();

            if(constraint == null || constraint.length() == 0) {
                filteredList.addAll(mCopyaAccountList);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for(Account item : mCopyaAccountList) {
                    if(item.getUsername().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mAccountList.clear();
            mAccountList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };
}
