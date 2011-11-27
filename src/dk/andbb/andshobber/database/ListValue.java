package dk.andbb.andshobber.database;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import android.widget.AdapterView.AdapterContextMenuInfo;

import java.util.ArrayList;

public class ListValue extends ListActivity {
    public String aisleCat;
    public String aisleSt;
    public float aislePrice;
    public ShopDbAdapter dbHelper;
    private EditText mTitleText;
    private EditText mBodyText;
    private Long mRowId;
    private ShopDbAdapter mDbHelper;
    private String spinName;
    public static String dbName;
    private String dbStore;

    private String selectSpinner;
    private static final int DELETE_ID = 7;
    private static final int EDIT_ID = 8;
    //    private static final int EDIT_ID = 8;
    private String itemStr;
    private String valueName;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        try {
            spinName = getIntent().getExtras().getString("spinName");
            dbName = getIntent().getExtras().getString("dbName");
        } catch (Exception e) {
        }

        setContentView(R.layout.valuelist);
        TextView valStr = (TextView) findViewById(R.id.val_string);
        TextView valAisle = (TextView) findViewById(R.id.val_aisle);
        TextView valPrice = (TextView) findViewById(R.id.val_price);

        Spinner valueSpinner = (Spinner) findViewById(R.id.v_spinner);

        dbHelper = new ShopDbAdapter(this);
        dbHelper.open();

        if (dbName.equals(ShopDbAdapter.DATABASE_SHOPITEMS)) {
            mRowId = getIntent().getExtras().getLong("itemID");
            itemStr = (String) getIntent().getExtras().getCharSequence("itemStr");
            valueName = ShopDbAdapter.KEY_ITEM;
            valueSpinner.setVisibility(View.GONE);
            fillValues(dbName);
        } else if (dbName.equals(ShopDbAdapter.DATABASE_CAT_STORE_TABLE)) {
//            mRowId = getIntent().getExtras().getLong("itemID");
//            valueSpinner.setVisibility(View.GONE);
            if (spinName.equals(ShopDbAdapter.KEY_CATEGORY)) {
                valueName = ShopDbAdapter.KEY_STORE;
            } else {
                valueName = ShopDbAdapter.KEY_CATEGORY;
            }
            spinFill(spinName, valueSpinner);
            valPrice.setVisibility(View.GONE);
            fillValues(dbName);
        } else if (dbName.equals(ShopDbAdapter.DATABASE_CATEGORIES)) {
//            mRowId = getIntent().getExtras().getLong("itemID");

            valueName = ShopDbAdapter.KEY_CATEGORY;
            valPrice.setVisibility(View.GONE);
            valAisle.setVisibility(View.GONE);
            valueSpinner.setVisibility(View.GONE);
            fillValues(dbName);
        } else if (dbName.equals(ShopDbAdapter.DATABASE_STORES)) {
//            mRowId = getIntent().getExtras().getLong("itemID");
            valueName = ShopDbAdapter.KEY_STORE;
            valPrice.setVisibility(View.GONE);
            valAisle.setVisibility(View.GONE);

            valueSpinner.setVisibility(View.GONE);
            fillValues(dbName);
        } else {
            spinFill(spinName, valueSpinner);
        }
        valStr.setText(valueName);

        this.getListView().setDividerHeight(2);

        registerForContextMenu(getListView());
        final Context xx = this;
        ListView list = getListView();

        valueSpinner
                .setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {

                    // @Override
                    public void onItemSelected(AdapterView<?> adapterView,
                                               View view, int i, long l) {
                        selectValue();
                    }

                    // @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                    }
                });
    }

    private void selectValue() {
        Spinner spinner = (Spinner) findViewById(R.id.v_spinner);
        Integer showno = spinner.getFirstVisiblePosition();
        spinner.setSelection(showno);

        spinner.setSelection(showno);
        selectSpinner = (String) spinner.getSelectedItem();

        fillValues(dbName);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        int pos2 = this.getListView().getFirstVisiblePosition();

        Toast.makeText(this, "Edit", Toast.LENGTH_LONG).show();

        Long valId = id;
        editAisle(valId);
//        finish();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

// Get the info on which item was selected
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;

// Get the Adapter behind your ListView (this assumes you're using
// a ListActivity; if you're not, you'll have to store the Adapter yourself
// in some way that can be accessed here.)
        Adapter adapter = getListAdapter();

// Retrieve the item that was clicked on
        Object item = adapter.getItem(info.position);

        long valId = info.id;

        if (dbName.equals(ShopDbAdapter.DATABASE_SHOPITEMS)) {
            menu.setHeaderTitle("");
//            TODO: Enter item text
        } else {
            Cursor catCursor = dbHelper.fetchValue(dbName, valId);
//            Cursor catCursor = dbHelper.fetchValue(ShopDbAdapter.DATABASE_CAT_STORE_TABLE, valId);
            String catStr = catCursor.getString(catCursor
                    .getColumnIndexOrThrow(valueName));
/*
            String catStr = catCursor.getString(catCursor
                    .getColumnIndexOrThrow(ShopDbAdapter.KEY_CATEGORY));
*/

            menu.setHeaderTitle(catStr);
        }
        menu.add(0, DELETE_ID, 0, R.string.menu_delete);
        menu.add(0, EDIT_ID, 0, R.string.menu_sort);
    }
    // ListView and view (row) on which was clicked, position and

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
                .getMenuInfo();
        String store = "";
        Long valId = info.id;


        switch (item.getItemId()) {
            case DELETE_ID:
                Toast.makeText(this, "Delete", Toast.LENGTH_LONG).show();
                if (dbName.equals(ShopDbAdapter.DATABASE_SHOPITEMS)) {
                    Cursor dbCursor = dbHelper.fetchValue(dbName, valId);
                    startManagingCursor(dbCursor);
                    int indx = dbCursor.getColumnIndex(ShopDbAdapter.KEY_STORE);

                    if (indx != -1) {
                        store = dbCursor.getString(indx);
                    }
                    dbHelper.dbDeleteItem(dbName, mRowId, store);
                    fillValues(dbName);
                } else {
                    Cursor valCursor = dbHelper.fetchValue(dbName, valId);
                    String valStr;
                    if (dbName.equals(ShopDbAdapter.DATABASE_CATEGORIES)) {
                        valStr = valCursor.getString(valCursor
                                .getColumnIndexOrThrow(ShopDbAdapter.KEY_CATEGORY));
                    } else {
                        valStr = valCursor.getString(valCursor
                                .getColumnIndexOrThrow(ShopDbAdapter.KEY_STORE));
                    }

                    dbHelper.deleteValue(dbName, valId, valStr);
                    fillValues(dbName);
                }
//                delete(mRowID,
//                dbHelper.dbDeleteItem(ShopDbAdapter.DATABASE_SHOPITEMS,)
                return true;
            case EDIT_ID:
                Toast.makeText(this, "Edit", Toast.LENGTH_LONG).show();

                editAisle(valId);

                return true;
        }
        return super.onContextItemSelected(item);
    }

    private void editAisle(Long valId) {
        Cursor cursor;
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final EditText aisle = new EditText(this);
        final EditText price = new EditText(this);
        final Long id2;
        String aisleTxt = "";
        String priceTxt = "";
        int indx;
        aisle.setInputType(0x00080000 | 0x00000002);
        LinearLayout lila = new LinearLayout(this);
        id2 = valId;

//        Cursor dbCursor = dbHelper.fetchValue(ShopDbAdapter.DATABASE_CAT_STORE_TABLE, valId);
        Cursor dbCursor = dbHelper.fetchValue(dbName, valId);
        startManagingCursor(dbCursor);

        if (dbName.equals(ShopDbAdapter.DATABASE_STORES) | dbName.equals(ShopDbAdapter.DATABASE_CATEGORIES)) {
            if (dbName.equals(ShopDbAdapter.DATABASE_STORES)) {
                indx = dbCursor.getColumnIndex(ShopDbAdapter.KEY_STORE);
                aisleTxt = dbCursor.getString(indx);
            } else if (dbName.equals(ShopDbAdapter.DATABASE_CATEGORIES)) {
                indx = dbCursor.getColumnIndex(ShopDbAdapter.KEY_CATEGORY);
                aisleTxt = dbCursor.getString(indx);
            }

            aisle.setText(aisleTxt);
            lila.addView(aisle);
            alert.setView(lila);
        } else {
            indx = dbCursor.getColumnIndex(ShopDbAdapter.KEY_AISLE);
//        int indx = dbCursor.getColumnIndexOrThrow(ShopDbAdapter.KEY_AISLE);
            if (indx != -1) {
                aisleTxt = dbCursor.getString(indx);
            } else {
                aisleTxt = "";
            }
            indx = dbCursor.getColumnIndex(ShopDbAdapter.KEY_STORE);

            if (indx != -1) {
                aisleSt = dbCursor.getString(indx);
            }
            indx = dbCursor.getColumnIndex(ShopDbAdapter.KEY_CATEGORY);
            if (indx != -1) {
                aisleCat = dbCursor.getString(indx);
            }


            if (dbName.equals(ShopDbAdapter.DATABASE_SHOPITEMS)) {
//        if (dbName.equals(ShopDbAdapter.SHOPPER_TABLE)) {
                indx = dbCursor.getColumnIndexOrThrow(ShopDbAdapter.KEY_PRICE);

                if (indx != -1) {
                    aislePrice = dbCursor.getFloat(indx);
                    priceTxt = new String(String.valueOf(aislePrice));
                }

                price.setHint("Price?");
                price.setText(priceTxt);
                lila.addView(price);
//            aisleSt = itemStr;
//            alert.setView(price);

            }

            aisle.setHint("Aisle?");

            if (aisleTxt == null) {
                aisleTxt = "";
            }

            if (aisleSt == null) {
                aisleSt = "";
//            aisle.setHint("Aisle?");
            }

            aisle.setText(aisleTxt);
            if (dbName.equals(ShopDbAdapter.DATABASE_SHOPITEMS)) {
                alert.setTitle(itemStr + "/" + aisleCat);
            } else {
                alert.setTitle(aisleSt + "/" + aisleCat);
            }
            lila.addView(aisle);
            alert.setView(lila);
        }
//        alert.setView(price);


// TODO: Aisle og Price skal skrives rigtigt i itemsinshop
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if (dbName.equals(ShopDbAdapter.DATABASE_STORES) | dbName.equals(ShopDbAdapter.DATABASE_CATEGORIES)) {
                    String value = aisle.getText().toString().trim();
                    dbHelper.updateValue(dbName,id2,value);
                } else {
                    String value = aisle.getText().toString().trim();
                    String priceValue = price.getText().toString().trim();
                    Toast.makeText(getApplicationContext(), value, Toast.LENGTH_SHORT).show();
                    if (dbName.equals(ShopDbAdapter.DATABASE_CAT_STORE_TABLE)) {
                        dbHelper.updateAisle(ShopDbAdapter.DATABASE_CAT_STORE_TABLE, id2, value);
                    }
                    try {
                        boolean xb = (value != null);
                        xb = !(value.equals(""));
//                    if (dbName.equals(ShopDbAdapter.DATABASE_CATEGORIES)) {
                        if (dbName.equals(ShopDbAdapter.DATABASE_CAT_STORE_TABLE)) {
                            if ((value != null) && !(value.equals(""))) {
                                dbHelper.storesCatFill(aisleSt, aisleCat, value);
                            }
                        } else if (dbName.equals(ShopDbAdapter.DATABASE_SHOPITEMS)) {
//                            dbHelper.storesCatFill(aisleSt, aisleCat, value);
                            if ((value != null) && !(value.equals(""))) {
                                dbHelper.storeItemAisle(aisleSt, mRowId, value);
                            }
                            if ((priceValue != null) && !(priceValue.equals(""))) {
                                dbHelper.storeItemPrice(aisleSt, mRowId, priceValue);
//                            String priceTxt = price.getText().toString().trim();
                            }

//                            dbHelper.putValue(mRowId,ShopDbAdapter.KEY_PRICE,new Float(priceTxt));
//                            dbHelper.putValue(mRowId,ShopDbAdapter.KEY_AISLE,value);
//                        dbHelper.storesAisleFill(mRowId, aisleSt, value, new Float(priceValue));
//                            dbHelper.storesAisleFill(mRowId, aisleSt, aisleCat, new Float(value));
                        }

                    } catch (Exception e) {
                    }
                }
                fillValues(dbName);
            }
        }

        );

        alert.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener()

                {
                    public void onClick
                            (DialogInterface
                                     dialog, int whichButton) {
                        dialog.cancel();
                    }
                }

        );

        alert.create();

        alert.show();
    }

    private void populateFields() {

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
        // ShopDbAdapter.FILTER_CATEGORY= (String) mValue.getSelectedItem();
    }

    private String get(String str) {
        // TODO Auto-generated method stub
        return str.toString();
    }

    public void spinFill(String dbName, Spinner valueSpinner) {
        String key;
        ArrayList<String> valsArrayList = new ArrayList<String>();

        if (dbName.equals(ShopDbAdapter.DATABASE_CATEGORIES)) {
            key = ShopDbAdapter.KEY_CATEGORY;
        } else if (dbName.equals(ShopDbAdapter.DATABASE_STORES)) {
            key = ShopDbAdapter.KEY_STORE;
        } else if (dbName.equals(ShopDbAdapter.DATABASE_SHOPITEMS)) {
            return;
        } else {
            key = ShopDbAdapter.KEY_STORE;
        }

        Cursor valsCursor = dbHelper.fetchAllValues(dbName, key);
        startManagingCursor(valsCursor);

        if (valsCursor.getCount() != 0) {

            for (valsCursor.moveToFirst(); valsCursor.moveToNext(); valsCursor
                    .isAfterLast()) {
                String x = valsCursor.getString(valsCursor
                        .getColumnIndexOrThrow(key));

                valsArrayList.add(valsCursor.getString(valsCursor
                        .getColumnIndexOrThrow(key)));
            }
            ArrayAdapter<String> vAdapt = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, valsArrayList);
            valueSpinner.setAdapter(vAdapt);
        }
    }

    private void fillValues(String dbName) {
        Cursor cursor = null;
//      dbName=ShopDbAdapter.DATABASE_SHOPITEMS

        TextView hViewType = (TextView) findViewById(R.id.view_description);
        TextView hDBName = (TextView) findViewById(R.id.db_name);

        hDBName.setText(CreateShopDatabase.DATABASE_NAME);

//        cursor = dbHelper.fetchAllValues(dbName, ShopDbAdapter.KEY_CATEGORY);


//        Caused by: android.database.sqlite.SQLiteException: no such column: storename: , while compiling: SELECT _id, storename, aisle, price FROM itemsinshop WHERE (item_id=7) ORDER BY storename IS NULL ASC, storename ASC
//      TODO: Check storenames/store_id in tables
        if ((selectSpinner != null) & !(dbName.equals(ShopDbAdapter.DATABASE_SHOPITEMS))) {
//order by cast(<columnName> as integer)
//            if (dbName.equals(ShopDbAdapter.DATABASE_CATEGORIES)) {
            if (dbName.equals(ShopDbAdapter.DATABASE_CAT_STORE_TABLE)) {
                hViewType.setText("Aisles in Shop");
                cursor = dbHelper.catInStore(dbName, selectSpinner);
            } else if (dbName.equals(ShopDbAdapter.DATABASE_STORES)) {
                hViewType.setText("Define Aisles");
                cursor = dbHelper.catInStore(dbName, selectSpinner);
            }
        } else if (dbName.equals(ShopDbAdapter.DATABASE_SHOPITEMS)) {
            cursor = dbHelper.shopsAislePrice(dbName, mRowId);
        } else if (dbName.equals(ShopDbAdapter.DATABASE_CATEGORIES)) {
            cursor = dbHelper.fetchAllValues(dbName, ShopDbAdapter.KEY_CATEGORY);
        } else if (dbName.equals(ShopDbAdapter.DATABASE_STORES)) {
            cursor = dbHelper.fetchAllValues(dbName, ShopDbAdapter.KEY_STORE);
        } else {
            return;
        }

        startManagingCursor(cursor);
        // SQL Fill table
        // create table catinst as select category, storename FROM
        // categories, stores
        // Now create an array adapter and set it to display using our row
        int count = cursor.getCount();

        long one = 1L;

//            cursor = dbHelper.shopsAislePrice(dbName, null);
        if (dbName.equals(ShopDbAdapter.DATABASE_SHOPITEMS) & (cursor.getCount() == 0)) {
            cursor = dbHelper.shopsAislePrice(dbName, null);
            if ((cursor.getCount() == 0)) {
                dbHelper.itemsInStoreUnfold();
            }
            cursor = dbHelper.shopsAislePrice(dbName, mRowId);
        }
        count = cursor.getCount();

        if (count == 0) {
//        if (count == 1) {
            editAisle(one);
            finish();
        } else {
            ValueArrayAdapter notes = new ValueArrayAdapter(this, cursor);

            setListAdapter(notes);
            ListView list = getListView();
            list.invalidate();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case DELETE_ID:
                try {
                    String value = data.getStringExtra("value");
                    if (value != null && value.length() > 0) {
                        //do something with value
//                        TODO:deleteValue
                    }
                    fillValues(dbName);
                } catch (Exception e) {
                }
                break;
            case EDIT_ID:
                try {
                    String value = data.getStringExtra("value");
                    if (value != null && value.length() > 0) {
                        if (dbName.equals(ShopDbAdapter.KEY_CATEGORY)) {
                            dbHelper.storesCatFill(aisleSt, aisleCat, value);
                            //do something with value
                        }
                    }
                    fillValues(dbName);
                } catch (Exception e) {
                }
                break;
            default:
                break;
        }
    }

    public void onDestroy() {
        super.onDestroy();

        // Replace mDbHelper as needed with your database connection, or
        // whatever wraps your database connection. (See below.)
        dbHelper.close();
    }

}