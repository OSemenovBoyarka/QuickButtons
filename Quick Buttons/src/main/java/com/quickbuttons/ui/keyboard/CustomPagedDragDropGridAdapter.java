package com.quickbuttons.ui.keyboard;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.quickbuttons.Constants;
import com.quickbuttons.data_model.ButtonsDbHelper;
import com.quickbuttons.data_model.QuickButton;

import java.util.ArrayList;
import java.util.List;

import ca.laplanete.mobile.pageddragdropgrid.PagedDragDropGrid;
import ca.laplanete.mobile.pageddragdropgrid.PagedDragDropGridAdapter;

public class CustomPagedDragDropGridAdapter implements PagedDragDropGridAdapter {

    private final static String LOG_TAG = "adapter";
    private Context context;
    private PagedDragDropGrid gridview;

    List<Page> pages = new ArrayList<Page>();

    public CustomPagedDragDropGridAdapter(Context context, PagedDragDropGrid gridview) {
        this.gridview = gridview;
        this.context = context;

        List<QuickButton> items = ButtonsDbHelper.getInstance(context).loadButtons();
        Log.d(LOG_TAG, "loaded items from db: " + items);
        int pageCount = calculateTotalPageCount(items);
        for (int pageIndex = 0; pageIndex < pageCount; pageIndex++) {
            pages.add(new Page(Constants.BUTTONS_ON_PAGE));
        }

        for (QuickButton button : items) {
            pages.get(button.page).addItem(button);
        }
    }


    public static int calculateTotalPageCount(List<QuickButton> items) {
        int pageCount = 1;
        for (QuickButton button : items) {
            //buttons has 0 based page index, and we need to return 1-based page count
            if (button.page >= pageCount) pageCount = button.page + 1;
        }
        return pageCount;
    }

    @Override
    public int pageCount() {
        return pages.size();
    }

    @Override
    public int itemCountInPage(int page) {
        if (page > pages.size()) return 0;
        else return pages.get(page).size();
    }

    @Override
    public View view(int page, int index) {
        QuickButton quickButton = getPage(page).getItemAt(index);
        if (quickButton == null) return null;
        Button button = new Button(context);
        button.setText(quickButton.label);
        quickButton.image = null;
        button.setTag(quickButton);

        button.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return gridview.onLongClick(v);
            }
        });
        return button;
    }

    @Override
    public int rowCount() {
        return Constants.ROWS_COUNT;
    }

    @Override
    public int columnCount() {
        return Constants.COLUMN_COUNT;
    }

    @Override
    public void printLayout() {

    }

    @Override
    public void swapItems(int pageIndex, int itemIndexA, int itemIndexB) {
        getPage(pageIndex).swapItems(itemIndexA, itemIndexB);
        updateItemInDb(getItemAt(pageIndex, itemIndexA));
        updateItemInDb(getItemAt(pageIndex, itemIndexB));
    }

    @Override
    public void moveItemToPreviousPage(int pageIndex, int itemIndex) {
        int leftPageIndex = pageIndex - 1;
        if (leftPageIndex >= 0) {
            moveItemToPage(pageIndex, itemIndex, leftPageIndex);
        }
    }

    @Override
    public void moveItemToNextPage(int pageIndex, int itemIndex) {
        int rightPageIndex = pageIndex + 1;
        if (rightPageIndex < pageCount()) {
            moveItemToPage(pageIndex, itemIndex, rightPageIndex);
        }
    }

    private void moveItemToPage(int startPageIndex, int itemIndex, int landingPageIndex) {
        Page startpage = getPage(startPageIndex);
        Page landingPage = getPage(landingPageIndex);

        QuickButton item = startpage.removeItem(itemIndex);
        landingPage.addItem(item);

        item.page = landingPageIndex;
        updateItemInDb(item);
    }

    private void updateItemInDb(final QuickButton item) {
        if (item == null) return;
        new Thread(new Runnable() {
            @Override
            public void run() {
                ButtonsDbHelper.getInstance(context).updateButton(item);
            }
        }).start();
    }

    private void deleteItemFromDb(final QuickButton item) {
        if (item == null) return;
        new Thread(new Runnable() {
            @Override
            public void run() {
                ButtonsDbHelper.getInstance(context).deleteButton(item);
            }
        }).start();
    }

    @Override
    public void deleteItem(int pageIndex, int itemIndex) {
        QuickButton deleted = getPage(pageIndex).removeItem(itemIndex);
        deleteItemFromDb(deleted);
    }

    @Override
    public int deleteDropZoneLocation() {
        return BOTTOM;
    }

    @Override
    public boolean showRemoveDropZone() {
        return true;
    }

    @Override
    public int getPageWidth(int page) {
        return 0;
    }

    @Override
    public QuickButton getItemAt(int page, int index) {
        return pages.get(page).getItemAt(index);
    }

    @Override
    public boolean disableZoomAnimationsOnChangePage() {
        return true;
    }

    private Page getPage(int pageIndex) {
        return pages.get(pageIndex);
    }

    public void addNewButton(QuickButton button) {
        findFreeSpaceAndInsertButton(button, gridview.currentPage());
        ButtonsDbHelper.getInstance(context).insertButton(button);
        gridview.notifyDataSetChanged();
    }


    private void findFreeSpaceAndInsertButton(QuickButton button, int currentPage) {
        for (int i = currentPage; i<pageCount(); i++){
        Page page = pages.get(i);
        try {
            button.page = i;
            page.addItem(button);
            return;
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        }
        //all pages aren't free - creating new one
        button.page = pages.size();
        Page page = new Page(Constants.BUTTONS_ON_PAGE);
        page.addItem(button);
        pages.add(page);
    }
}
