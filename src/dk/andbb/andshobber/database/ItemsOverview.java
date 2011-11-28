package dk.andbb.andshobber.database;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.*;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.*;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.*;
import android.widget.AdapterView.AdapterContextMenuInfo;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;


public class ItemsOverview extends ListActivity {
    public static final String BASENAME = "item";
    public ShopDbAdapter dbHelper;

    SharedPreferences mASHSettings;
    public static final String ASH_PREFERENCES = "ASHPrefs";
    public static final String ASH_PREFERENCES_DBNAME = "dbName";

    public static final int VIEW_SHOBBER = 0;
    public static final int VIEW_DATE = 1;
    public static final int VIEW_PRIO = 2;
    public static final int VIEW_DETAIL = 3;

    public static final int ITEM_DELETE = Menu.FIRST + 1;
    public static final int SORT_ID = Menu.FIRST + 2;
    public static final int EDIT_ID = Menu.FIRST + 3;
    public static final int ITEM_INSERT = Menu.FIRST + 4;
    public static final int ITEM_CREATE = Menu.FIRST + 5;
    public static final int ITEM_EDIT = Menu.FIRST + 6;
    public static final int EDIT_ACTION = Menu.FIRST + 7;
    public static final int GET_FILE = Menu.FIRST + 8;
    public static final int DB_EDIT = Menu.FIRST + 9;
    public static final int mInsert = Menu.FIRST + 10;
    public static final int mQuit = Menu.FIRST + 11;
    public static final int mPlanShop = Menu.FIRST + 12;
    public static final int mCheckout = Menu.FIRST + 13;
    public static final int mDelete = Menu.FIRST + 14;
    public static final int mDatabase = Menu.FIRST + 15;
    public static final int mDbEdit = Menu.FIRST + 16;
    public static final int mViewEdit = Menu.FIRST + 17;
    public static final int ITEM_DATE = Menu.FIRST + 18;
    public static final int ITEM_CLEAR_DATE = Menu.FIRST + 19;
    public static final int ITEM_POSTPONE = Menu.FIRST + 20;
    public static final int ITEM_COPY = Menu.FIRST + 21;
    public static int CLICKED = -1;

    private Cursor cursor;
    private Spinner cspinner;
    private Spinner sspinner;
    public EditText searchBox;
    private ListView listOView;
    public String searchStrFilter = "";
    private boolean DIRECTXML = false;
    private View header;
    private LinearLayout headerLL;
    private View footer;
    private boolean bChkName;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean chkDb;
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // Retrieve the shared preferences
        mASHSettings = getSharedPreferences(ASH_PREFERENCES, Context.MODE_PRIVATE);
        // Initialize the dbname entry
        CreateShopDatabase.DATABASE_NAME = initDBnameEntry();
//        CreateShopDatabase.DATABASE_NAME = "dbShopper.abb";
//        CreateShopDatabase.DATABASE_NAME = "";

//        dbFiles();
//        setContentView(R.layout.itemlistold);
//        setContentView(R.layout.itemlistbottom);
        setContentView(R.layout.itemlist);

        listOView = getListView();

        do {
            dbHelper = new ShopDbAdapter(this);
            if (dbHelper == null) {
                updateDBName("db");

                Intent i = new Intent(ItemsOverview.this, fileslist.class);
                startActivityForResult(i, GET_FILE);

//                dbFiles();
//            fillData("");
                updateDBName(CreateShopDatabase.DATABASE_NAME);
            }
            Boolean openDB = dbHelper.open();

            if (openDB == false) {
                CreateShopDatabase.DATABASE_NAME = "dbShopper";
            }


//            fillData("");
//            updateDBName(CreateShopDatabase.DATABASE_NAME);
//
//            dbHelper.open();

            chkDb = dbHelper.chkDatabase();
            if (!chkDb) {
                dbHelper.close();
                bChkName = CreateShopDatabase.DATABASE_NAME.equals(BASENAME);
                if (bChkName) {
//                if (CreateShopDatabase.DATABASE_NAME.equals("item")) {
                    bChkName = this.deleteDatabase(BASENAME);
                } else {

                    updateDBName(BASENAME);
                    CreateShopDatabase.DATABASE_NAME = BASENAME;
                    dbFiles();
                }
            }
        } while (!chkDb);

        dbHelper.chkSettingsCol();
        dbHelper.getSettings(dbHelper.currentView());


        headerControl(ShopDbAdapter.VIEW_KEY );
//        headerControl();
/*
        if (header != null) {
            listOView.addHeaderView(header);
        }
*/


        cspinner = (Spinner) findViewById(R.id.cat_spinner);
        sspinner = (Spinner) findViewById(R.id.store_spinner);

        searchBox = (EditText) findViewById(R.id.search_edit);

//		Cursor ccursor = dbHelper.fetchAllValues(ShopDbAdapter.DATABASE_CATEGORIES );

        spinFill(ShopDbAdapter.DATABASE_CATEGORIES, ShopDbAdapter.FILTER_CATEGORY, cspinner);
        spinFill(ShopDbAdapter.DATABASE_STORES, ShopDbAdapter.FILTER_STORE, sspinner);

        this.getListView().setDividerHeight(2);

        fillData(searchStrFilter);
//        fillData("");

        registerForContextMenu(getListView());
        final Context xx = this;
// TODO:Temp skal slettes

        cspinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {

            //			@Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ShopDbAdapter.FILTER_CATEGORY = chooseCategory();

//                fillData("");
            }

            //@Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        sspinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {

            //			@Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ShopDbAdapter.FILTER_STORE = chooseStore();

                dbHelper.getSettings(dbHelper.currentView());
                ShopDbAdapter.currentViewSettings = dbHelper.currentView();


//                fillData(searchStrFilter);
            }

            //@Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        searchBox.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            public void afterTextChanged(Editable searchStr) {
                searchStrFilter = searchStr.toString();
                fillData(searchStrFilter);
                //To change body of implemented methods use File | Settings | File Templates.
            }
        });
    }

    private void headerControl(Integer viewKey) {
        header = null;
        footer = null;

        header = (View) findViewById(R.id.dateHeaderLayout);
        header.setVisibility(View.GONE);
        header = (View) findViewById(R.id.compactHeaderLayout);
        header.setVisibility(View.GONE);
        header = (View) findViewById(R.id.allHeaderLayout);
        header.setVisibility(View.GONE);
        header = (View) findViewById(R.id.prioHeaderLayout);
        header.setVisibility(View.GONE);

        switch (viewKey) {
            case VIEW_SHOBBER: {
                header = (View) findViewById(R.id.compactHeaderLayout);
                header.setVisibility(View.VISIBLE);
                break;
            }
            case VIEW_DATE: {
                header = (View) findViewById(R.id.dateHeaderLayout);
                header.setVisibility(View.VISIBLE);
                break;
            }
            case VIEW_PRIO: {
                header = (View) findViewById(R.id.prioHeaderLayout);
                header.setVisibility(View.VISIBLE);
                break;
            }
            case VIEW_DETAIL: {
                header = (View) findViewById(R.id.allHeaderLayout);
                header.setVisibility(View.VISIBLE);
                break;
            }
        }
    }

/*
    public boolean onBackPressed(){
        // TODO:UNDO
        if (undoable) {

        }

    }
*/

    public boolean onKeyDown(int keyCode, KeyEvent kEvent) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (searchBox.getVisibility() == View.VISIBLE) {
                searchBox.setText("");
                searchBox.setVisibility(View.GONE);
            }

        }
        if (kEvent.isLongPress() && keyCode == KeyEvent.KEYCODE_MENU) {
            if (searchBox.getVisibility() != View.VISIBLE) {
                searchBox.setVisibility(View.VISIBLE);
                searchBox.requestFocus();
                searchBox.performClick();
                searchBox.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
            } else {
                searchBox.setText("");
                searchBox.setVisibility(View.GONE);
            }
        }
        return false;
    }


    private String initDBnameEntry() {
        // Save Nickname
        String dbName;
        final EditText dbname1;
        if (mASHSettings.contains(ASH_PREFERENCES_DBNAME)) {
            dbName = mASHSettings.getString(ASH_PREFERENCES_DBNAME, "");
        } else {
            dbName = CreateShopDatabase.DATABASE_NAME;
        }
        return dbName;
    }

    public void updateDBName(String strDBname) {
        Editor editor = mASHSettings.edit();
        editor.putString(ASH_PREFERENCES_DBNAME, strDBname);
        editor.commit();
    }

    @Override
    public boolean onSearchRequested() {
        switchSearchBox();
        return true;
    }

    public void switchSearchBox() {
        if (searchBox.getVisibility() == View.VISIBLE) {
            searchBox.setVisibility(View.GONE);
            searchBox.setText("");
            searchBox.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        } else {
            searchBox.setVisibility(View.VISIBLE);
            searchBox.requestFocus();
            searchBox.performClick();
            searchBox.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu todolist) {
//        todolist.add(0, mInsert, 1, "Insert Item").setIcon(android.R.drawable.ic_menu_add);
//        todolist.add(0, mCopy, 1, "Insert Item").setIcon(android.R.drawable.ic_menu_add);
        if (ShopDbAdapter.TABLE_VIEW.equals(ShopDbAdapter.TABLE_ALL)) {
            todolist.add(0, mPlanShop, 1, "Plan/Shop").setIcon(R.drawable.basket);
        } else {
            todolist.add(0, mPlanShop, 1, "Plan/Shop").setIcon(R.drawable.pencil);
        }
//        TODO: Separate ikoner til Shop vs. Plan
        todolist.add(0, mCheckout, 1, "Checkout").setIcon(android.R.drawable.ic_menu_agenda);
//        todolist.add(0, mCheckout, 1, "Checkout").setIcon(R.drawable.cashier);
        todolist.add(0, mViewEdit, 1, "View Settings").setIcon(android.R.drawable.ic_menu_view);
//        todolist.add(0, mViewEdit, 1, "View Settings").setIcon(R.drawable.icon_display_setting);
        todolist.add(0, mDbEdit, 1, "Database Settings").setIcon(android.R.drawable.ic_menu_manage);
        todolist.add(0, mDatabase, 1, "Files and Data").setIcon(android.R.drawable.ic_menu_save);
        todolist.add(0, mQuit, 1, "Quit").setIcon(android.R.drawable.ic_lock_power_off);
        return super.onCreateOptionsMenu(todolist);
    }

    // Reaction to the menu selection
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        Intent i;
        switch (item.getItemId()) {
/*
            case mInsert:
                createItem();
                return true;
*/
            case mPlanShop:
                if (ShopDbAdapter.TABLE_VIEW.equals(ShopDbAdapter.TABLE_NEED)) {
                    ShopDbAdapter.TABLE_VIEW = ShopDbAdapter.TABLE_ALL;
                    ShopDbAdapter.FILTER_DEF = ShopDbAdapter.FILTER_ALL;
                    Toast.makeText(ItemsOverview.this,
                            "Showing All", Toast.LENGTH_LONG).show();
                } else {
                    ShopDbAdapter.FILTER_DEF = ShopDbAdapter.FILTER_NEED;
                    ShopDbAdapter.TABLE_VIEW = ShopDbAdapter.TABLE_NEED;
                    Toast.makeText(ItemsOverview.this,
                            "Showing Need", Toast.LENGTH_LONG).show();
                }
                dbHelper.setSetting("need_view", ShopDbAdapter.TABLE_VIEW.toString(), "");

                fillData(searchStrFilter);
                dbHelper.getSettings(dbHelper.currentView());

//                fillData("");
                return true;
            case mCheckout:
                checkout();
                return true;
            case mDbEdit:
                i = new Intent(this, DbSettings.class);
                startActivityForResult(i, DB_EDIT);
                return true;
            case mViewEdit:
                i = new Intent(this, ViewSettings.class);
                startActivityForResult(i, mViewEdit);
                return true;
            case mDatabase:
                dbFiles();
//                fillData("");
                updateDBName(CreateShopDatabase.DATABASE_NAME);
                return true;
            case mDelete:
//              Not actove
                dbHelper.deleteAll();
                fillData("");
                return true;
            case mQuit:
                finish();
                dbHelper.close();
                return true;
        }
        return super.onMenuItemSelected(featureId, item);
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

        long mRowId = info.id;
        Cursor shopDB = dbHelper.fetchItem(mRowId);
        String itemStr;
        itemStr = shopDB.getString(shopDB
                .getColumnIndexOrThrow(ShopDbAdapter.KEY_ITEM));

        menu.setHeaderTitle(itemStr);
        menu.add(0, ITEM_EDIT, 0, R.string.menu_edit);
        menu.add(0, ITEM_COPY, 0, R.string.menu_copy);
        menu.add(0, ITEM_CREATE, 0, R.string.menu_create);
        menu.add(0, ITEM_DATE, 0, R.string.menu_date);
        menu.add(0, ITEM_CLEAR_DATE, 0, R.string.menu_clear_date);
        menu.add(0, ITEM_POSTPONE, 0, R.string.menu_postpone);
        menu.add(0, ITEM_DELETE, 0, R.string.menu_delete);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
                .getMenuInfo();

        Long rowId = info.id;

        switch (item.getItemId()) {
            case ITEM_DELETE: {
                if (rowId != -1) {
                    dbHelper.deleteItem(rowId);
                    fillData(searchStrFilter);
//                    fillData("");
                }
                return true;
            }

            case ITEM_EDIT: {
                if (rowId != -1) {
                    Intent i = new Intent(this, ItemDetails.class);
                    i.putExtra(ShopDbAdapter.KEY_ROWID, rowId);
                    i.putExtra("requestCode", ITEM_EDIT);
                    startActivityForResult(i, ITEM_EDIT);
                }
                // Return true to consume the click event. In this case the
                // onListItemClick listener is not called anymore.
                return true;
            }
            case ITEM_COPY: {
                if (rowId != -1) {
                    Intent i = new Intent(this, ItemDetails.class);
                    i.putExtra(ShopDbAdapter.KEY_ROWID, rowId);
                    i.putExtra("requestCode", ITEM_COPY);
                    startActivityForResult(i, ITEM_COPY);
                }
                // Return true to consume the click event. In this case the
                // onListItemClick listener is not called anymore.
                return true;
            }
            case ITEM_CREATE: {
                Intent i = new Intent(this, ItemDetails.class);
                i.putExtra(ShopDbAdapter.KEY_ROWID, 0);
                i.putExtra("requestCode", ITEM_CREATE);
                startActivityForResult(i, ITEM_CREATE);

                // Return true to consume the click event. In this case the
                // onListItemClick listener is not called anymore.
                return true;
            }
            case ITEM_DATE: {
//                Intent i = new Intent(this, ItemDateDetails.class);
                if (rowId != -1) {
                    Intent i = new Intent(this, ItemDetails.class);
                    i.putExtra(ShopDbAdapter.KEY_ROWID, rowId);
                    i.putExtra("requestCode", ITEM_DATE);
                    startActivityForResult(i, ITEM_DATE);
                }
                // Return true to consume the click event. In this case the
                // onListItemClick listener is not called anymore.
                return true;
            }
            case ITEM_CLEAR_DATE: {
                if (rowId != -1) {
                    dbHelper.putValue(rowId, ShopDbAdapter.KEY_DATE, "");
//                    fillData("");
                    fillData(searchStrFilter);
                }
                return true;
            }
            case ITEM_POSTPONE: {
                if (rowId != -1) {
                    dbHelper.updateNeed(rowId, ShopDbAdapter.NEED_LATER);
//                    fillData("");
                    fillData(searchStrFilter);
                }
                return true;
            }
        }
        return super.onContextItemSelected(item);
    }

    private void createItem() {
        Intent i = new Intent(this, ItemDetails.class);
        startActivityForResult(i, ITEM_CREATE);
    }

    private String chooseCategory() {

        Spinner spinner = (Spinner) findViewById(R.id.cat_spinner);
        Integer showno = spinner.getFirstVisiblePosition();
        spinner.setSelection(showno);

        spinner.setSelection(showno);
        String cat = (String) spinner.getSelectedItem();
        if (cat.equals("<Add>")) {
            Intent inputIntent = new Intent(this, getText.class);
            inputIntent.putExtra("value", "old value to edit");
            this.startActivityForResult(inputIntent, EDIT_ACTION);
        } else {
            dbHelper.setSetting("filter_category", cat, ShopDbAdapter.SETTINGS_PLAN_STORE);
//            TODO:Condtional SETTINGS
            dbHelper.setSetting("filter_category", cat, ShopDbAdapter.SETTINGS_SHOP_STORE);
            dbHelper.setSetting("filter_category", cat, ShopDbAdapter.SETTINGS_PLAN_ALL);
            dbHelper.setSetting("filter_category", cat, ShopDbAdapter.SETTINGS_PLAN_STORE);
            if (!ShopDbAdapter.FILTER_CATEGORY.equals(cat)) {
                ShopDbAdapter.FILTER_CATEGORY = cat;
                fillData(searchStrFilter);
            }
//            fillData("");
        }
        return cat;
    }

    private String chooseStore() {

        Spinner spinner = (Spinner) findViewById(R.id.store_spinner);
        Integer showno = spinner.getFirstVisiblePosition();
        spinner.setSelection(showno);

        String store = (String) spinner.getSelectedItem();

        if (!store.equals("")) {
            dbHelper.itemAisleToDb(store);
            dbHelper.setSetting("filter_store", store, ShopDbAdapter.SETTINGS_PLAN_STORE);
//            TODO:Condtional SETTINGS
            dbHelper.setSetting("filter_store", store, ShopDbAdapter.SETTINGS_SHOP_STORE);

            ShopDbAdapter.DISP_TABLE = ShopDbAdapter.SHOPPER_TABLE;
        } else {
            ShopDbAdapter.DISP_TABLE = ShopDbAdapter.DATABASE_TABLE;
        }
        dbHelper.getSettings(dbHelper.currentView());
//        if (!ShopDbAdapter.FILTER_STORE.equals(store)) {
        ShopDbAdapter.FILTER_STORE = store;
        fillData(searchStrFilter);
//        }
//        }
        return store;
    }

    protected void dbFiles() {
        Intent i = new Intent(ItemsOverview.this, fileslist.class);
        startActivityForResult(i, GET_FILE);
    }

    protected void checkout() {
        boolean ok;
        cursor = dbHelper.fetchShopItems("need='" + ShopDbAdapter.NEED_BOUGHT + "'");
        startManagingCursor(cursor);
        String newNeed = ShopDbAdapter.NEED_HAVE;
        while (!cursor.isAfterLast()) {
            int rowId = cursor.getInt(cursor.getColumnIndex(ShopDbAdapter.KEY_ROWID));
            ok = dbHelper.updateNeed(rowId, newNeed);
            // do something useful with these
            cursor.moveToNext();
        }

        cursor = dbHelper.fetchShopItems(" need='" + ShopDbAdapter.NEED_LATER + "'");
        startManagingCursor(cursor);
        newNeed = ShopDbAdapter.NEED_NEED;
        while (!cursor.isAfterLast()) {
            int rowId = cursor.getInt(cursor.getColumnIndex(ShopDbAdapter.KEY_ROWID));
            dbHelper.updateNeed(rowId, newNeed);
            // do something useful with these
            cursor.moveToNext();
        }
//        fillData("");
        fillData(searchStrFilter);

    }

/*
    public void itemClick(View itemView) {
        Integer rowID;
        Intent i;
        rowID = (Integer) itemView.getTag();
        i = new Intent(this, DbSettings.class);
        if ((rowID != -1) & (rowID !=null)) {
            i = new Intent(this, ItemDetails.class);
            i.putExtra(ShopDbAdapter.KEY_ROWID, rowID);
            startActivityForResult(i, ITEM_EDIT);
        }
        fillData(searchStrFilter);
    }
*/

    public void quantclick(View quantView) {
        Integer rowID;
        rowID = (Integer) quantView.getTag();
        int rowPos = this.getListView().getFirstVisiblePosition();
        quantClickHandler(rowID, rowPos);
        fillData(searchStrFilter);
    }

    public void dateclick(View dateView) {
        int rowID = (Integer) dateView.getTag();
//        Long rowID = (Long) needView.getTag();
        int rowPos = listOView.getFirstVisiblePosition();
        if (rowID != -1) {
            Intent i = new Intent(this, ItemDetails.class);
            i.putExtra(ShopDbAdapter.KEY_ROWID, (long) rowID);
            i.putExtra("requestCode", ITEM_DATE);
            startActivityForResult(i, ITEM_DATE);
        }
//        dateClickHandler((long) rowID, rowPos);
        fillData(searchStrFilter);
    }

    public void quantClickHandler(final long rowId, int rowPos) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final EditText aisle = new EditText(this);
        final NumberPicker npPicker = new NumberPicker(this);
        final ContentValues values = new ContentValues();

        if (rowId != -1) {
            Cursor dbCursor = dbHelper.fetchItem(rowId);

            alert.setTitle("Select quantity");
            String item = dbCursor.getString(dbCursor.getColumnIndexOrThrow(ShopDbAdapter.KEY_ITEM));
            int quantity = dbCursor.getInt(dbCursor.getColumnIndexOrThrow(ShopDbAdapter.KEY_QUANTITY));


            npPicker.setCurrent(quantity);
            alert.setTitle(item + "/Quantity?");
            alert.setView(npPicker); // Adding Quantity picker to alert


            alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
//                    dbHelper.updateItem(rowId, values);
                    int quantInt = npPicker.getCurrent();
                    values.put(ShopDbAdapter.KEY_QUANTITY, (long) quantInt);
                    dbHelper.updateItem(rowId, values);
                    fillData(searchStrFilter);
                }
            });

            alert.setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.cancel();
//                            fillData("");
                        }
                    });
            alert.create();
            alert.show();
        }
    }

    public void dateClickHandler(final long rowId, int rowPos) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final EditText aisle = new EditText(this);
        final DatePicker dpDate = new DatePicker(this);
        final String dPlus7;
        final String dpPlus7;
        final String dTomorrow;
        final Long id2;
        final ContentValues values = new ContentValues();
        final long dateMSec;
        long date1MSec;
        final long date2MSec;
        final long dayMSec = 24 * 3600 * 1000;
        Calendar calTime;
        final CharSequence[] dates = {"Tomorrow", "In a week", "Postpone a week"};
        final long datesLong[] = {0, 0, 0};
//        final CharSequence[] items = {"Tomorrow","In a week", "Postpone a week"};


        if (rowId != -1) {
            Cursor dbCursor = dbHelper.fetchItem(rowId);

            alert.setTitle("Pick a date");

            calTime = Calendar.getInstance();
            date1MSec = calTime.getTimeInMillis();

            datesLong[0] = dayMSec + date1MSec;
            dTomorrow = "Tomorrow " + dateConverter.msec2Str(datesLong[0]);
            dates[0] = dTomorrow;

            datesLong[1] = 7 * dayMSec + date1MSec;
            dPlus7 = "In a week " + dateConverter.msec2Str(datesLong[1]);
            dates[1] = dPlus7;

            date1MSec = dbCursor.getLong(dbCursor.getColumnIndexOrThrow(ShopDbAdapter.KEY_DATE));
            if (date1MSec > 1000) {
                datesLong[2] = 7 * dayMSec + date1MSec;
                dpPlus7 = "Postpone a week " + dateConverter.msec2Str(datesLong[2]);
                dates[2] = dpPlus7;
            } else {
                date1MSec = calTime.getTimeInMillis();
                datesLong[2] = date1MSec;
                dpPlus7 = dateConverter.msec2Str(datesLong[2]);
                dates[2] = dpPlus7;
//                date2MSec=date1MSec;
            }

            dateMSec = date1MSec;
            date2MSec = date1MSec;

//            date2MSec = dbCursor.getLong(dbCursor.getColumnIndexOrThrow(ShopDbAdapter.KEY_DATE));

            alert.setItems(dates, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    long date0;
                    Toast.makeText(getApplicationContext(), dates[item], Toast.LENGTH_SHORT).show();
                    switch (item) {
                        case 0:
                            date0 = dayMSec + date2MSec;
                            values.put(ShopDbAdapter.KEY_DATE, datesLong[0]);
                            dbHelper.updateItem(rowId, values);

                            break;
                        case 1:
                            date0 = 7 * dayMSec + date2MSec;
                            values.put(ShopDbAdapter.KEY_DATE, datesLong[1]);
                            dbHelper.updateItem(rowId, values);
                            break;
                        case 2:
                            date0 = 7 * dayMSec + date2MSec;
                            values.put(ShopDbAdapter.KEY_DATE, datesLong[2]);
                            dbHelper.updateItem(rowId, values);
                            break;
                    }
                }

            });

            String need = dbCursor.getString(dbCursor.getColumnIndexOrThrow(ShopDbAdapter.KEY_NEED));
            String category = dbCursor.getString(dbCursor.getColumnIndexOrThrow(ShopDbAdapter.KEY_CATEGORY));
            String item = dbCursor.getString(dbCursor.getColumnIndexOrThrow(ShopDbAdapter.KEY_ITEM));
            String note = dbCursor.getString(dbCursor.getColumnIndexOrThrow(ShopDbAdapter.KEY_NOTE));
            Long quantity = dbCursor.getLong(dbCursor.getColumnIndexOrThrow(ShopDbAdapter.KEY_QUANTITY));
            calTime = dateConverter.msec2Cal(date1MSec);
            int mYear = calTime.get(Calendar.YEAR);
            int mMonth = calTime.get(Calendar.MONTH);
            int mDay = calTime.get(Calendar.DAY_OF_MONTH);

            dpDate.init(mYear, mMonth, mDay, null);

            alert.setTitle(item);
            alert.setView(dpDate); // Adding Aisle to alert

            alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
//                    dbHelper.updateItem(rowId, values);
                    values.put(ShopDbAdapter.KEY_DATE, date2MSec);
                    dbHelper.updateItem(rowId, values);
                }
            });

            alert.setNeutralButton("Clear date", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    values.put(ShopDbAdapter.KEY_DATE, 0);
                    dbHelper.updateItem(rowId, values);
                }
            });
            alert.setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.cancel();
//                            fillData("");
                        }
                    });
            alert.create();
            alert.show();

        }
    }

    public void aisleClickHandler(int rowId, int rowPos) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final EditText aisle = new EditText(this);
        final Long id2;

        if (rowId != -1) {
            aisle.setInputType(0x00080000 | 0x00000002);

            Cursor dbCursor = dbHelper.fetchItem(rowId);

            String need = dbCursor.getString(dbCursor.getColumnIndexOrThrow(ShopDbAdapter.KEY_NEED));
            String category = dbCursor.getString(dbCursor.getColumnIndexOrThrow(ShopDbAdapter.KEY_CATEGORY));
            String item = dbCursor.getString(dbCursor.getColumnIndexOrThrow(ShopDbAdapter.KEY_ITEM));
            String note = dbCursor.getString(dbCursor.getColumnIndexOrThrow(ShopDbAdapter.KEY_NOTE));
            Long quantity = dbCursor.getLong(dbCursor.getColumnIndexOrThrow(ShopDbAdapter.KEY_QUANTITY));
            Long dateMSec = dbCursor.getLong(dbCursor.getColumnIndexOrThrow(ShopDbAdapter.KEY_DATE));

            aisle.setText(category);
            alert.setTitle(item);
            alert.setView(aisle); // Adding Aisle to alert

            alert.setPositiveButton("Single", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                }
            });

            alert.setNeutralButton("Category", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                }
            });
            alert.setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.cancel();
                        }
                    });
            alert.show();
        }

    }

    public void needclick(View needView) {
        int rowID;
        rowID = (Integer) needView.getTag();
        int rowPos = listOView.getFirstVisiblePosition();
        String need = needClickHandler((long) rowID, rowPos);
//        fillData(searchStrFilter);
        dbHelper.needViewCreate(need, (ImageView) needView);
        ListView list = getListView();
        list.invalidate();
//        fillData("");
        fillData(searchStrFilter);
        listOView.setSelection(rowPos);

    }

    public void addClick(View blankView) {
        Intent i = new Intent(this, ItemDetails.class);
        i.putExtra(ShopDbAdapter.KEY_ROWID, 0);
        i.putExtra("requestCode", ITEM_CREATE);
        startActivityForResult(i, ITEM_CREATE);
//        createItem();
    }

    private String needClickHandler(long rowId, int rowPos) {
//        int pos2 = this.getListView().getFirstVisiblePosition();

//        fillData("");
        Cursor dbCursor = dbHelper.fetchItem(rowId);

        String need = dbCursor.getString(dbCursor.getColumnIndexOrThrow(ShopDbAdapter.KEY_NEED));
        need = dbHelper.needSwitch(need);
        ContentValues values = new ContentValues();
        values.put(ShopDbAdapter.KEY_NEED, need);
        boolean xb = dbHelper.updateItem(rowId, values);
        return need;
    }

    private void fillData(String filter) {
        TextView hViewType = (TextView) findViewById(R.id.view_description);
        TextView hDBName = (TextView) findViewById(R.id.db_name);
        TextView hAppName = (TextView) findViewById(R.id.view_app_name);
        TextView hSort1 = (TextView) findViewById(R.id.view_sort1);
        TextView hSort2 = (TextView) findViewById(R.id.view_sort2);
        TextView hSort3 = (TextView) findViewById(R.id.view_sort3);
        TextView hSort4 = (TextView) findViewById(R.id.view_sort4);
        TextView hView = (TextView) findViewById(R.id.view_view);
        TextView hCartPrice = (TextView) findViewById(R.id.sumPrice);
        TextView hMissingPrice = (TextView) findViewById(R.id.remainPrice);
        TextView hCartCount = (TextView) findViewById(R.id.inCart);
        TextView hMissCount = (TextView) findViewById(R.id.itemsMissing);
        Integer cartCount = 10;
        Integer missingCount = 4;
        Float cartPrice;
        Float missingPrice;
        int hBackground;

        headerControl(ShopDbAdapter.VIEW_KEY );
/*
        switch (ShopDbAdapter.VIEW_KEY + 99) {
            case 0: {
                header = getLayoutInflater().inflate(R.layout.itemrowheader, null);
                break;
            }
            case 1: {
                header = (View) getLayoutInflater().inflate(R.layout.itemrowdetail, null);
                break;
            }
            case 2: {
                header = getLayoutInflater().inflate(R.layout.itemrowdateheader, null);
                break;
            }
        }

        if (header != null) {
//            listOView.addFooterView(header);
//            listOView.removeAllViews();
        }

        hDBName.setText(CreateShopDatabase.DATABASE_NAME);
        dbHelper.getSettings(dbHelper.currentView());
        hSort1.setText(ShopDbAdapter.SORT_1);
        hSort2.setText(ShopDbAdapter.SORT_2);
        hSort3.setText(ShopDbAdapter.SORT_3);
        hSort4.setText(ShopDbAdapter.SORT_4);
//        hView.setText(ShopDbAdapter.VIEW_KEY.toString());
        String cv = dbHelper.currentView();
        hView.setText(cv);

*/
        if (ShopDbAdapter.TABLE_VIEW.equals(ShopDbAdapter.TABLE_NEED)) {
            hBackground = Color.YELLOW;
            hViewType.setText("Shopping");
        } else {
            hBackground = Color.GREEN;
            hViewType.setText("Planning");
        }

        hDBName.setBackgroundColor(hBackground);
        hViewType.setBackgroundColor(hBackground);
        hAppName.setBackgroundColor(hBackground);

        cursor = dbHelper.fetchAllShops(filter);
        startManagingCursor(cursor);
        TextView blankList = (TextView) findViewById(R.id.blankList);
        if (cursor.getCount() == 0) {
            blankList.setVisibility(View.VISIBLE);
        } else {
            blankList.setVisibility(View.GONE);
        }
// Now create an array adapter and set it to display using our row
        ItemArrayAdapter notes = new ItemArrayAdapter(this, cursor);

        ListView list = getListView();
        list.invalidate();

        setListAdapter(notes);

        cartPrice = dbHelper.dbSumPrice(ShopDbAdapter.NEED_BOUGHT);
        missingPrice = dbHelper.dbSumPrice(ShopDbAdapter.NEED_NEED);
        cartCount = dbHelper.dbCountItems(ShopDbAdapter.NEED_BOUGHT);
        missingCount = dbHelper.dbCountItems(ShopDbAdapter.NEED_NEED);
/*
        String tStr=String.valueOf(missingPrice);
        tStr=String.valueOf(cartPrice);
        tStr=String.valueOf(cartCount);
        tStr=String.valueOf(missingCount);
*/

        NumberFormat formatter = new DecimalFormat("#0.00");

        hMissingPrice.setText(formatter.format(missingPrice)); // Float
        hCartPrice.setText(formatter.format(cartPrice));
        hCartCount.setText(String.valueOf(cartCount));
        hMissCount.setText(String.valueOf(missingCount));
    }

    public void spinFill(String dbName, String valSetting, Spinner valueSpinner) {
        String key;
        ArrayList<String>
                valsArrayList = new ArrayList<String>();

        if (dbName.equals(ShopDbAdapter.DATABASE_CATEGORIES)) {
            key = ShopDbAdapter.KEY_CATEGORY;
        } else {
            key = ShopDbAdapter.KEY_STORE;
        }

        Cursor valsCursor = dbHelper.fetchAllValues(dbName, key);
        startManagingCursor(valsCursor);

/*
        if (valsCursor.getCount() == 0) {
            dbHelper.createValue(dbName, "");
        }
*/
        Cursor filter = dbHelper.fetchMatchValues(dbName, key, "");
        if (filter.getCount() == 0) {
            valsArrayList.add("");
        }
        valsCursor.moveToFirst();

        if (valsCursor.getCount() != -1) {
//        if (valsCursor.getCount() != 0) {

            String x;
            for (int i = 0; i < valsCursor.getCount(); i++) {
                x = valsCursor.getString(valsCursor.getColumnIndexOrThrow(key));

//                valsArrayList.add(valsCursor.getString(valsCursor.getColumnIndexOrThrow(key)));
                valsArrayList.add(x);
                valsCursor.moveToNext();
            }
            ArrayAdapter<String> vAdapt;
            vAdapt = new ArrayAdapter<String>(this, R.layout.ash_spinner,
                    valsArrayList);
//            vAdapt = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,
//                    valsArrayList);
            valueSpinner.setAdapter(vAdapt);
            String s;

            for (int i = 0; i < valueSpinner.getCount(); i++) {
                s = (String) valueSpinner.getItemAtPosition(i);
//                Log.e(null, s + " " + valSetting);
                if (s.equalsIgnoreCase(valSetting)) {
                    valueSpinner.setSelection(i);
                    if (key == ShopDbAdapter.KEY_CATEGORY) {
                        ShopDbAdapter.FILTER_CATEGORY = s;
                    } else {
                        ShopDbAdapter.FILTER_STORE = s;
                    }
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (!ShopDbAdapter.FILTER_STORE.equals("")) {
            dbHelper.itemAisleToDb(ShopDbAdapter.FILTER_STORE);

            ShopDbAdapter.DISP_TABLE = ShopDbAdapter.SHOPPER_TABLE;
        }

        switch (requestCode) {
            case ITEM_DATE:

                fillData(searchStrFilter);
//                fillData("");
                break;
            case ITEM_EDIT:
                try {
                    String value = data.getStringExtra("value");
                    if (value != null && value.length() > 0) {
                        //do something with value
                    }
//                    fillData("");
                    fillData(searchStrFilter);

                } catch (Exception e) {
                }
                break;
            case EDIT_ACTION: //ITEM_EDIT
                try {
                    String value = data.getStringExtra("value");
                    if (value != null && value.length() > 0) {
                        //do something with value
                    }
                } catch (Exception e) {
                }
                break;
            case DB_EDIT: //DB_EDIT
                Toast.makeText(ItemsOverview.this,
                        "Database edited", Toast.LENGTH_LONG).show();
                spinFill(ShopDbAdapter.DATABASE_CATEGORIES, ShopDbAdapter.FILTER_CATEGORY, cspinner);
                spinFill(ShopDbAdapter.DATABASE_STORES, ShopDbAdapter.FILTER_STORE, sspinner);
//                fillData("");
                fillData(searchStrFilter);

                break;
            case mViewEdit: //DB_EDIT
                Toast.makeText(ItemsOverview.this,
                        "Views edited", Toast.LENGTH_LONG).show();
                spinFill(ShopDbAdapter.DATABASE_CATEGORIES, ShopDbAdapter.FILTER_CATEGORY, cspinner);
                spinFill(ShopDbAdapter.DATABASE_STORES, ShopDbAdapter.FILTER_STORE, sspinner);
//                fillData("");
                fillData(searchStrFilter);

                break;
            case GET_FILE:
/*
                String value = data.getStringExtra("value");
                if (value != null && value.length() > 0) {
                    if (value.equals("new")) {
*/
                dbHelper.close();
                updateDBName(CreateShopDatabase.DATABASE_NAME);
                dbHelper.open();

                dbHelper.chkDatabase();
                dbHelper.chkSettingsCol();
//                dbHelper.getSettings(dbHelper.currentView());
                dbHelper.getSettings(ShopDbAdapter.SETTINGS_PLAN_ALL);

//                        dbHelper.createValue(ShopDbAdapter.DATABASE_CAT_STORE_TABLE, "-");

                spinFill(ShopDbAdapter.DATABASE_CATEGORIES, ShopDbAdapter.FILTER_CATEGORY, cspinner);
                spinFill(ShopDbAdapter.DATABASE_STORES, ShopDbAdapter.FILTER_STORE, sspinner);
//                    } else {
//                fillData("");
                //do something with value
//                    }
//        }
                fillData(searchStrFilter);
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


