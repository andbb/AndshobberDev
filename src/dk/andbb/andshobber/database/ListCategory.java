package dk.andbb.andshobber.database;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class ListCategory extends Activity {
    private Long mRowId;
    private ShopDbAdapter mDbHelper;
    private Spinner mCategory;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.itemedit);
        mCategory = (Spinner) findViewById(R.id.category);
        EditText mTitleText = (EditText) findViewById(R.id.item_edit);
        EditText mBodyText = (EditText) findViewById(R.id.note_edit);

        Button confirmButton = (Button) findViewById(R.id.item_edit_button);
//		Button fillButton = (Button) findViewById(R.id.todo_fill_button);

        populateFields();

        confirmButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                setResult(RESULT_OK);
                finish();
            }
        });
    }

    private void populateFields() {
        if (mRowId != null) {
            Cursor todo = mDbHelper.fetchItem(mRowId);
            startManagingCursor(todo);
            String category = ShopDbAdapter.FILTER_CATEGORY;

            for (int i = 0; i < mCategory.getCount(); i++) {

                String s = (String) mCategory.getItemAtPosition(i);
                Log.e(null, s + " " + category);
                if (s.equalsIgnoreCase(category)) {
                    mCategory.setSelection(i);
                }
            }
        }
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveState();
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        populateFields();
    }

    private void saveState() {
        ShopDbAdapter.FILTER_CATEGORY = (String) mCategory.getSelectedItem();
    }

    private String get(String str) {
        // TODO Auto-generated method stub
        return str.toString();
    }

}