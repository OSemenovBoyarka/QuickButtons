package com.quickbuttons.ui.keyboard;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.quickbuttons.R;
import com.quickbuttons.data_model.QuickButton;

import ca.laplanete.mobile.pageddragdropgrid.PagedDragDropGrid;

public class GridViewController implements View.OnClickListener {


    Context context;
    View.OnClickListener gridViewListener;
    private PagedDragDropGrid gridView;

    public CustomPagedDragDropGridAdapter getAdapter() {
        return adapter;
    }

    private CustomPagedDragDropGridAdapter adapter;

    public GridViewController(Context context, View.OnClickListener gridViewListener) {
        this.context = context;
        this.gridViewListener = gridViewListener;
    }

    public void setupGridVIew(PagedDragDropGrid grid) {
        gridView = grid;
        gridView.setClickListener(gridViewListener);
        adapter = new CustomPagedDragDropGridAdapter(context, grid);
        grid.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {

    }

    public void addNewButton(QuickButton button){
        adapter.addNewButton(button);
    }
}