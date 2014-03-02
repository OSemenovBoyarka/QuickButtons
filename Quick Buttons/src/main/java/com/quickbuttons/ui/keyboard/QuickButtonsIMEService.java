package com.quickbuttons.ui.keyboard;

import android.content.Context;
import android.inputmethodservice.InputMethodService;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;

import com.quickbuttons.R;
import com.quickbuttons.data_model.QuickButton;

import ca.laplanete.mobile.pageddragdropgrid.PagedDragDropGrid;

public class QuickButtonsIMEService extends InputMethodService implements View.OnClickListener {

    public static final String LOG_TAG = "QuickButtonsIMEService";
    private final GridViewController gridViewController = new GridViewController(this,this);

    @Override
    public View onCreateInputView() {
        View root = getLayoutInflater().inflate(R.layout.keyboard_test_buttons, null);
        gridViewController.setupGridVIew((PagedDragDropGrid) root.findViewById(R.id.gridview));
        setupLangKey(root);
        return root;
    }

    private void setupLangKey(View root) {
        root.findViewById(R.id.btn_lang).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                InputMethodManager imm = getInputMethodManager();
                imm.showInputMethodPicker();
                return true;
            }
        });
    }


    @Override
    public void onClick(View v) {
        gridViewController.onClick(v);
        Log.d(QuickButtonsIMEService.LOG_TAG, "onClick, view: " + v);
        String text = null;
        switch (v.getId()) {
            case R.id.btn_lang:
                launchLangPicker();
                return;
            case R.id.btn_space:
                text = " ";
                break;
            case R.id.btn_return:
                //TODO review behaviour of this button
                getCurrentInputConnection().performEditorAction(EditorInfo.IME_ACTION_DONE);
            default:
                if (v.getTag() != null && v.getTag() instanceof QuickButton) {
                    text = ((QuickButton) v.getTag()).text;
                }
                break;
        }
        if (!TextUtils.isEmpty(text)) getCurrentInputConnection().commitText(text, 1);
    }

    private void launchLangPicker() {
        InputMethodManager imm = getInputMethodManager();
        final IBinder token = this.getWindow().getWindow().getAttributes().token;
        imm.switchToLastInputMethod(token);
    }

    private InputMethodManager getInputMethodManager() {
        return (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    }


}
