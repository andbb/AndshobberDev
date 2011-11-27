/**
 *
 */
package dk.andbb.andshobber.database;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * @author Anders og Charlotte
 */
public class DbSettings extends Activity {
    public static final int mInsert = 0;
    public static final int mQuit = 1;
    public static final int mSort = 2;
    public static final int mCheckout = 3;
    public static final int mDelete = 4;
    public static final int mDatabase = 5;
    public static final int mDbEdit = 6;
    public static final int mCatEdit = 7;
    public static final int mStoreEdit = 8;
    public static final int mCatStoreEdit = 9;

    private static final int ACTIVITY_CREATE = 0;
    private static final int ACTIVITY_EDIT = 1;
    private static final int EDIT_ACTION = 2;
    private static final int GET_FILE = 5;
    private static final int DB_EDIT = 6;

    private ShopDbAdapter mDbHelper;
    private Spinner mCategory;
    private Spinner mNeed;
    private Spinner mSortKey;
    private Spinner mViewKey;
    private Spinner mSort1;
    private Spinner mSort2;
    private Spinner mSort3;
    private Spinner mSort4;
    private Button sort_button1;
    private Button sort_button2;
    private Button sort_button3;
    private Button sort_button4;

    private EditText mNewStore;
    private EditText mNewCat;
    private EditText mTax1;
    private EditText mTax2;
    private String settingsChosen;
    //this counts how many Gallery's are on the UI
    private int mGalleryCount = 0;

    //this counts how many Gallery's have been initialized
    private int mGalleryInitializedCount = 0;
// cf http://stackoverflow.com/questions/5624825/spinner-onitemselected-executes-when-it-is-not-suppose-to/5918177#5918177

//UI reference
//        private Gallery mGallery;

//	@param args;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);


        final Context xx = this;

        mDbHelper = new ShopDbAdapter(this);
        mDbHelper.open();

        setContentView(R.layout.dbsettings);
//        mNewStore = (EditText) findViewById(R.id.newstore);
        mNewCat = (EditText) findViewById(R.id.newcat);
        mTax1 = (EditText) findViewById(R.id.tax1);
        mTax2 = (EditText) findViewById(R.id.tax2);

        Button addStoreButton = (Button) findViewById(R.id.addstore);
        Button addCatButton = (Button) findViewById(R.id.addcat);
        Button editCatButton = (Button) findViewById(R.id.editCats);
        Button editStoreButton = (Button) findViewById(R.id.editStores);
        Button editCatStoreButton = (Button) findViewById(R.id.editCatStores);
        Button cleanCatButton = (Button) findViewById(R.id.cleanCat);
        Button cleanDataButton = (Button) findViewById(R.id.cleanData);

        Button cancelButton = (Button) findViewById(R.id.settingsCancel);

        populateFields();
        mGalleryCount = 5;

        addCatButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                mNewCat = (EditText) findViewById(R.id.newcat);
                String newCat = mNewCat.getText().toString();
                if (!newCat.equals("")) {
                    mDbHelper.createValue(ShopDbAdapter.DATABASE_CATEGORIES, newCat);
                    mDbHelper.storeInNewCatUnfold(newCat);
                    mNewCat.setText("");
                }

//				saveState();
            }

        });

        addStoreButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                mNewStore = (EditText) findViewById(R.id.newcat);
                String newStore = mNewStore.getText().toString();
                if (!newStore.equals("")) {
                    mDbHelper.createValue(ShopDbAdapter.DATABASE_STORES, newStore);
                    mNewStore.setText("");
                    mDbHelper.catsInNewStoreUnfold(newStore);
                    mDbHelper.itemsInNewStoreUnfold(newStore);
                }
//				saveState();
            }

        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
//                saveState();
                setResult(RESULT_OK);
                finish();
            }

        });

        editCatStoreButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
//				cleanCat();
                Intent i = new Intent(xx, ListValue.class);
                String dbName = ShopDbAdapter.DATABASE_STORES;
//				String dbName=ShopDbAdapter.DATABASE_STORES;
                i.putExtra("spinName", ShopDbAdapter.DATABASE_STORES);
                i.putExtra("dbName", ShopDbAdapter.DATABASE_CATEGORIES);
//                TODO:Catsin stores? virkede ikke
                i.putExtra("dbName", ShopDbAdapter.DATABASE_CAT_STORE_TABLE);

                startActivityForResult(i, mCatStoreEdit );

                finish();
            }

        });

        editCatButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
//				cleanCat();

                Intent i = new Intent(xx, ListValue.class);
                String dbName = ShopDbAdapter.DATABASE_CATEGORIES;

                i.putExtra("dbName", ShopDbAdapter.DATABASE_CATEGORIES);

                startActivityForResult(i, mCatEdit);
                setResult(RESULT_OK);
                finish();
            }

        });

        editStoreButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
//				cleanCat();

                Intent i = new Intent(xx, ListValue.class);
                String dbName = ShopDbAdapter.DATABASE_STORES;

                i.putExtra("dbName", ShopDbAdapter.DATABASE_STORES);

                startActivityForResult(i, mStoreEdit);
                setResult(RESULT_OK);
                finish();
            }

        });

        cleanCatButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                cleanCat();

                setResult(RESULT_OK);
                finish();
            }

        });

        cleanDataButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                cleanData();

                setResult(RESULT_OK);
                finish();
            }

        });

    }

    public boolean onKeyDown(int keyCode, KeyEvent kEvent) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            saveState();
            setResult(RESULT_OK);
            finish();
        }
        return true;
    }

    private void populateFields() {
//        mNewStore.setText("");
        mNewCat.setText("");
    }

    private void populateSort(Spinner sortSpin, String sortField) {
        for (int i = 0; i < sortSpin.getCount(); i++) {

            String s = (String) sortSpin.getItemAtPosition(i);
            Log.e(null, s + " Sort");
            if (s.equalsIgnoreCase(sortField)) {
                sortSpin.setSelection(i);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        populateFields();
    }


    private void saveState() {
        ContentValues constantsVals=new ContentValues();
        mNewStore = (EditText) findViewById(R.id.newcat);
//        mNewCat = (EditText) findViewById(R.id.newstore);
        mTax1 = (EditText) findViewById(R.id.tax1);
        mTax2 = (EditText) findViewById(R.id.tax2);
        Float tax1;
        Float tax2;

/*
        String newStore = mNewStore.getText().toString();
        if (!newStore.equals("")) {
            mDbHelper.createValue(ShopDbAdapter.DATABASE_STORES, newStore);
        }
        String newCat = mNewCat.getText().toString();
        if (!newCat.equals("")) {
            mDbHelper.createValue(ShopDbAdapter.DATABASE_CATEGORIES, newCat);
        }
*/

        String newTax1 = mTax1.getText().toString();
        if (!newTax1.equals("")) {
            tax1 = new Float(newTax1);
            constantsVals.put(ShopDbAdapter.KEY_TAX, tax1);
//            mDbHelper.updateConstValue(ShopDbAdapter.DATABASE_CONSTANTS, ShopDbAdapter.KEY_TAX, tax1);
            mDbHelper.updateConstValue(ShopDbAdapter.DATABASE_CONSTANTS, constantsVals);
        }

        String newTax2 = mTax2.getText().toString();
        if (!newTax2.equals("")) {
            tax2 = new Float(newTax2);
            constantsVals.put(ShopDbAdapter.KEY_TAX2, tax2);
//            mDbHelper.updateConstValue(ShopDbAdapter.DATABASE_CONSTANTS, ShopDbAdapter.KEY_TAX, tax1);
            mDbHelper.updateConstValue(ShopDbAdapter.DATABASE_CONSTANTS, constantsVals);
        }
    }

    private void cleanCat() {
        mNewStore = (EditText) findViewById(R.id.newcat);
//        mNewCat = (EditText) findViewById(R.id.newstore);

        mDbHelper.uniqueValue(ShopDbAdapter.DATABASE_CATEGORIES, ShopDbAdapter.DATABASE_TABLE);
        mDbHelper.uniqueValue(ShopDbAdapter.DATABASE_STORES, ShopDbAdapter.DATABASE_TABLE);
        mDbHelper.catsInStoreUnfold();
    }

    private void cleanData() {
            mDbHelper.itemsInStoreUnfold();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case mStoreEdit: //DB_EDIT
                Toast.makeText(this,
                        "Database Stores edited", Toast.LENGTH_LONG).show();

                break;
            case mCatEdit: //DB_EDIT
                Toast.makeText(this,
                        "Database Categories edited", Toast.LENGTH_LONG).show();
                break;
            default:
                break;
        }
    }
    public void onDestroy() {
        super.onDestroy();

        // Replace mDbHelper as needed with your database connection, or
        // whatever wraps your database connection. (See below.)
        mDbHelper.close();
    }

}