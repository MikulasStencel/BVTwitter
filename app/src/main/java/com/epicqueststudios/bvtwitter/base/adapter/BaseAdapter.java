package com.epicqueststudios.bvtwitter.base.adapter;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;



public abstract class BaseAdapter<T> extends RecyclerView.Adapter<BaseAdapter<T>.BaseViewHolder> {

    public BaseAdapter() {

    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ViewDataBinding binding = DataBindingUtil.inflate(layoutInflater, viewType, parent, false);
        return getViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(BaseAdapter<T>.BaseViewHolder holder, int position) {
        holder.bind(getItemForPosition(position));
    }

    @Override
    public int getItemViewType(int position) {
        return getLayoutIdForPosition(position);
    }

    protected abstract T getItemForPosition(int position);

    protected abstract int getLayoutIdForPosition(int position);

    public abstract class BaseViewHolder extends RecyclerView.ViewHolder {
        protected final ViewDataBinding binding;


        public BaseViewHolder(ViewDataBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }


        public void bind(final T item) {
           // binding.setVariable(BR.obj, item);
            binding.executePendingBindings();

            //itemView.setOnClickListener(v -> listener.onItemClick(item));
        }
    }

    protected abstract BaseViewHolder getViewHolder(ViewDataBinding binding);
}