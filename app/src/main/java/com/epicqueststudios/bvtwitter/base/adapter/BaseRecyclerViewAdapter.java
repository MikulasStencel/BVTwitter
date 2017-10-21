package com.epicqueststudios.bvtwitter.base.adapter;


import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;


import com.epicqueststudios.bvtwitter.base.viewmodel.BaseViewModel;

import java.util.ArrayList;

public abstract class BaseRecyclerViewAdapter<ITEM_T, VIEW_MODEL_T extends BaseViewModel<ITEM_T>>
        extends RecyclerView.Adapter<BaseRecyclerViewAdapter.BaseViewHolder<ITEM_T, VIEW_MODEL_T>> {

    protected final ArrayList<ITEM_T> items = new ArrayList<>();
    protected RecyclerView recyclerView;
    public BaseRecyclerViewAdapter() {
        super();
    }

    @Override
    public final void onBindViewHolder(BaseViewHolder<ITEM_T, VIEW_MODEL_T> holder, int position) {
        holder.setItem(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class BaseViewHolder<T, VT extends BaseViewModel<T>>
            extends RecyclerView.ViewHolder {

        protected final VT viewModel;
        private final ViewDataBinding binding;

        public BaseViewHolder(View itemView, ViewDataBinding binding, VT viewModel) {
            super(itemView);
            this.binding = binding;
            this.viewModel = viewModel;
        }

        void setItem(T item) {
            viewModel.setItem(item);
            binding.executePendingBindings();
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        this.recyclerView = null;
    }
}

