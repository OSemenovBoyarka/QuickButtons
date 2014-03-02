package com.quickbuttons.ui.keyboard;

import android.util.SparseArray;
import com.quickbuttons.data_model.QuickButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Page {
    private final int mSize;

    private List<QuickButton> mItems;

    public Page(int size) {
        this.mSize = size;
        this.mItems = new ArrayList<QuickButton>();
    }


    public QuickButton getItemAt(int index) {
        return mItems.get(index);
    }

    public void addItem(QuickButton item) {
        if (mItems.size() >= mSize) throw new IllegalStateException("Page is full");

        mItems.add(item);
        item.position = mItems.indexOf(item);
    }

    public void swapItems(int itemAIndex, int itemBIndex) {
        mItems.get(itemAIndex).position = itemBIndex;
        mItems.get(itemBIndex).position = itemAIndex;
        Collections.swap(mItems, itemAIndex, itemBIndex);
    }

    public QuickButton removeItem(int itemIndex) {
        QuickButton item = mItems.get(itemIndex);
        mItems.remove(itemIndex);
        return item;
    }

    public void deleteItem(int itemIndex) {
        mItems.remove(itemIndex);
    }

    public int size() {
        return mItems.size();
    }
}
