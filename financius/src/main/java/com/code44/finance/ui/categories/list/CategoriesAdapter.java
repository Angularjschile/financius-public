package com.code44.finance.ui.categories.list;

import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.code44.finance.R;
import com.code44.finance.data.model.Category;
import com.code44.finance.ui.common.adapters.ModelsAdapter;
import com.code44.finance.ui.common.presenters.ModelsActivityPresenter;

class CategoriesAdapter extends ModelsAdapter<Category> {
    public CategoriesAdapter(OnModelClickListener<Category> onModelClickListener) {
        super(onModelClickListener);
    }

    @Override protected ViewHolder createModelViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.li_category, parent, false));
    }

    @Override protected Category modelFromCursor(Cursor cursor) {
        return Category.from(cursor);
    }

    private static class ViewHolder extends ModelsAdapter.ViewHolder<Category> {
        private final ImageView colorImageView;
        private final TextView titleTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            colorImageView = (ImageView) itemView.findViewById(R.id.colorImageView);
            titleTextView = (TextView) itemView.findViewById(R.id.titleTextView);
        }

        @Override protected void bind(Category model, Cursor cursor, int position, ModelsActivityPresenter.Mode mode, boolean isSelected) {
            colorImageView.setColorFilter(model.getColor());
            titleTextView.setText(model.getTitle());
        }
    }
}