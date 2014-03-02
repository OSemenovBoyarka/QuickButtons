package com.quickbuttons.ui.settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.quickbuttons.R;
import com.quickbuttons.data_model.ButtonsDbHelper;
import com.quickbuttons.data_model.QuickButton;
import com.quickbuttons.ui.keyboard.GridViewController;

import ca.laplanete.mobile.pageddragdropgrid.PagedDragDropGrid;

public class SettingsActivity extends Activity implements View.OnClickListener {

    private final GridViewController gridViewController = new GridViewController(this, this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        gridViewController.setupGridVIew((PagedDragDropGrid) findViewById(R.id.gridview));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_button:
                final Context context = SettingsActivity.this;

                final View editTextLayout = getLayoutInflater().inflate(R.layout.dialog_add_button, null);
                AlertDialog dialog = new AlertDialog.Builder(context)
                        .setTitle("Add new button")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String title = ((EditText) editTextLayout.findViewById(R.id.et_add_button_title)).getText().toString();
                                String text = ((EditText) editTextLayout.findViewById(R.id.et_add_button_text)).getText().toString();

                                //TODO find better solution
                                if (TextUtils.isEmpty(title) || TextUtils.isEmpty(text)){
                                    Toast.makeText(context, "Title or text is empty", Toast.LENGTH_LONG).show();
                                    return;
                                }

                                QuickButton button = new QuickButton(title, text, 0, 0);
                                gridViewController.addNewButton(button);
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, null)
                        .create();
                dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                dialog.setView(editTextLayout);

                dialog.show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View v) {

    }
}
