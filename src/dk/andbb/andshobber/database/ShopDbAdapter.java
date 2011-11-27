package dk.andbb.andshobber.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.View;
import android.widget.*;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

public class ShopDbAdapter {

    public static final String sort_1_dir = "sort_1_dir";
    public static final String DATABASE_TABLE = "shobber";
    public static final String DATABASE_SHOPITEMS = "itemsinshop"; // All shops all items
    public static final String DATABASE_CATEGORIES = "categories";
    public static final String DATABASE_STORES = "stores";
    public static final String DATABASE_SHOPITEMSSHOW = "atd";
    public static final String DATABASE_SETTINGS = "settings";
    public static final String DATABASE_CAT_STORE_TABLE = "catsinshop";
    public static final String DATABASE_CONSTANTS = "constants";
    public static String DISP_TABLE = DATABASE_TABLE;
    public static String SHOPPER_TABLE = "storeshopper";     // One shop DISP_TABLE
    // Database fields
    public static final String KEY_ROWID = "_id";
    public static final String KEY_CATEGORY = "category";
    public static final String KEY_STORE = "storename";
    public static final String KEY_ITEM = "item";
    public static final String KEY_NEED = "need";
    public static final String KEY_PRIORITY = "priority";
    public static final String KEY_CUSTOMTEXT = "customtext";
    public static final String KEY_QUANTITY = "quantity";
    public static final String KEY_UNITS = "units";
    public static final String KEY_PRICE = "price";
    public static final String KEY_AISLE = "aisle";
    public static final String KEY_DATE = "date";
    public static final String KEY_ENTRYORDER = "entryorder";
    public static final String KEY_COUPON = "coupon";
    public static final String KEY_TAX = "tax";
    public static final String KEY_TAX2 = "tax2";
    public static final String KEY_AUTODELETE = "autoDelete";
    public static final String KEY_PRIVATE = "private";
    //    public static final String KEY_NOTE = "tax";
    public static final String KEY_NOTE = "note";
    public static final String KEY_ALARM = "alarm";
    public static final String KEY_ALARMMIDI = "alarmmidi";
    public static final String KEY_ICON = "icon";
    public static final String KEY_CREATED = "created";
    public static final String KEY_MODIFIED = "modified";
    public static final String KEY_ITEMID = "item_id";
    public static final String KEY_SETTINGSNAME = "settings_name";

    public static final String KKEY_ROWID = DATABASE_TABLE + "." + KEY_ROWID;
    public static final String KKEY_CATEGORY = DATABASE_TABLE + "." + KEY_CATEGORY;
    public static final String KKEY_STORE = DATABASE_STORES + "." + KEY_STORE;
    public static final String KKEY_AISLE = DATABASE_STORES + "." + KEY_AISLE;
    public static final String KKEY_PRICE = DATABASE_STORES + "." + KEY_PRICE;

    public static final String NEED_NEED = "";
    public static final String NEED_HAVE = "have";
    public static final String NEED_BOUGHT = "x";
    public static final String NEED_LATER = "later";
    public static final String FILTER_NEED = "need = 'x' or need = '' or need = 'later'";
    //    public static final String FILTER_NEED="need = \"x\" or need = \"\" ";
    public static final String FILTER_ALL = "";
    public static String FILTER_CATEGORY = "";
    public static String FILTER_STORE = "";
    public static String SETTINGS_PLAN_STORE = "PlannerStore";
    public static String SETTINGS_SHOP_STORE = "ShopperStore";
    public static String SETTINGS_PLAN_ALL = "PlannerAll";
    public static String SETTINGS_SHOP_ALL = "ShopperAll";
    public static String currentViewSettings = "ShopperAll";

    public static final Integer TABLE_ALL = 1;
    public static final Integer TABLE_NEED = 0;

    public static final Integer CLICK_NEED = 0;
    public static final Integer CLICK_QUANTITY = 1;

    public static Integer TABLE_VIEW = 1;
    public static Integer VIEW_KEY = 0;
    public static String FILTER_DEF = "need = 'x' or need = '' ";
    public static String SORT_NEED = " need = '' DESC, need = 'x' DESC, need = 'later' DESC ";
    public static String SORT_KEY0 = "need , aisle, category, item";
    public static String SORT_KEY = SORT_KEY0;
    public static String SORT_1 = KEY_NEED;
    public static String SORT_2 = KEY_AISLE;
    public static String SORT_3 = KEY_ITEM;
    public static String SORT_4 = KEY_CATEGORY;
    public static String SORT_1_DIR = "ASC";
    public static String SORT_2_DIR = "ASC";
    public static String SORT_3_DIR = "ASC";
    public static String SORT_4_DIR = "ASC";
    public static String SORT_KEY1 = "date=='' ASC, date ASC , category, item, aisle";
    public static String SORT_KEY2 = "category, item";
    public static String SORT_KEY3 = "item, category";
    public static String NEED_VIEW;
    public static String NEED_VIEW2;

    public static Integer CONST_TAX = 1;
    public static Integer CONST_TAX2 = 2;

    public Float TAX1;
    public Float TAX2;

    private EditText mTax1;
    private EditText mTax2;

    private Context context;
    private SQLiteDatabase database;
    private CreateShopDatabase dbCreate;
    private Cursor settingsCursor;
    private int nHits;

    public ShopDbAdapter(Context context) {
        this.context = context;
    }

    public Boolean open() throws SQLException {
        dbCreate = new CreateShopDatabase(context);
        try {
            database = dbCreate.getWritableDatabase();
        } catch (SQLException e) {
//  Database already exists
            return true;
        } catch (RuntimeException e) {
            return false;
//            CreateShopDatabase.DATABASE_NAME = "forfra";
//            open();
        }
        return true;
    }

    public void close() {
        database.close();
        dbCreate.close();
    }

    /**
     * Create a new item If the item is successfully created return the new
     * rowId for that note, otherwise return a -1 to indicate failure.
     */
    public long createItem(ContentValues initialValues) {
        return database.insert(DATABASE_TABLE, null, initialValues);
    }

    /**
     * Create a new DB SHOPITEM item If the DB SHOPITEM item is successfully created return the new
     * rowId for that note, otherwise return a -1 to indicate failure.
     */
    public long createStoreItem(ContentValues initialValues) {

        return database.insert(DATABASE_SHOPITEMS, null, initialValues);
    }

    /**
     * * Update the item
     *
     * @param rowId
     * @param updateValues
     * @return
     */
    public boolean updateItem(long rowId, ContentValues updateValues) {
//		mDbHelper.updateItem(mRowId, category, summary, need, description,quantity);
        ContentValues inShopValues = new ContentValues();
        database.beginTransaction();
        int nrow = database.update(DATABASE_TABLE, updateValues, KEY_ROWID + "= ?"
                , new String[]{String.valueOf(rowId)});
        if (ShopDbAdapter.DISP_TABLE.equals(ShopDbAdapter.SHOPPER_TABLE)) {
            int nrow2 = database.update(SHOPPER_TABLE, updateValues, KEY_ROWID + "= ?"
                    , new String[]{String.valueOf(rowId)});
//            TODO: Update Aisle og Price i Itemsinshop
            if (updateValues.containsKey(KEY_AISLE)) {
                inShopValues.put(KEY_AISLE, updateValues.getAsString(KEY_AISLE));
            }

            if (updateValues.containsKey(KEY_PRICE)) {
                inShopValues.put(KEY_PRICE, updateValues.getAsFloat(KEY_PRICE));
            }

            if (updateValues.containsKey(KEY_CATEGORY)) {
                inShopValues.put(KEY_CATEGORY, updateValues.getAsString(KEY_CATEGORY));
            }

            if (inShopValues.size() > 0) {
                nrow = database.update(DATABASE_SHOPITEMS, inShopValues,
                        KEY_STORE + " = '" + FILTER_STORE + "' AND " + KEY_ITEMID + "= ?"
                        , new String[]{String.valueOf(rowId)});
            }

        }
//        int nrow= database.update(DATABASE_TABLE, updateValues, KEY_ROWID + "="
//                + rowId, null);
//        database.endTransaction();
        database.setTransactionSuccessful();
        database.endTransaction();
//        database.setTransactionSuccessful();
        return nrow > 1;
    }

    /**
     * Deletes item rowid
     *
     * @param rowId
     */
    public boolean deleteItem(long rowId) {
        int nItems = database.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null);
        int nShItems = database.delete(DATABASE_SHOPITEMS, KEY_ITEMID + "=" + rowId, null);
        int nShopIds = database.delete(SHOPPER_TABLE, KEY_ROWID + "=" + rowId, null);

        return nItems + nShItems + nShopIds > 0;
    }

    /**
     * Deletes item rowid
     *
     * @param itemId
     */
    public boolean dbDeleteItem(String dbTable, long itemId, String store) {
        int nDel = database.delete(dbTable, KEY_ITEMID + "=" + itemId + " and " + KEY_STORE + "='" + store + "'", null);
        return nDel > 0;
    }

    /**
     * Delete all items in databaser
     *
     * @return
     */
    public boolean deleteAll() {
        return database.delete(DATABASE_TABLE, "", null) > 0;
    }

    /**
     * Retrieve all database settings from database table DATABASE_SETTINGS
     */
    public void getSettings(String settingsName) {
        ContentValues initialValues;
        long n_inst;
        boolean settingsOK = true;
//        String settingsName="PlanStore";
        try {
            settingsCursor = database.query(DATABASE_SETTINGS, null, KEY_SETTINGSNAME + "=\"" + settingsName + "\"", null, null,
                    null, null);
// Tilføj settingscolumn - VIEW - Shop/Plan - Store osv.
            if (settingsCursor.getCount() == 0) {
                initialValues = new ContentValues();
                initialValues.put(KEY_SETTINGSNAME, settingsName);
                n_inst = database.insert(DATABASE_SETTINGS, null, initialValues);
            }
            settingsCursor.moveToFirst();
        } catch (SQLException e) {
/*
            database.execSQL("DROP TABLE IF EXISTS settings");
            database.execSQL(CreateShopDatabase.CREATE_SETTINGS);
*/

            settingsOK = false;
        }

        if (settingsOK) {
            if (settingsCursor.getCount() > 0) {
                ShopDbAdapter.SORT_1 = getSingleSetting("sort_1");

                SORT_1_DIR = getSingleSetting("sort_1_dir");
                SORT_2 = getSingleSetting("sort_2");
                SORT_2_DIR = getSingleSetting("sort_2_dir");
                SORT_3 = getSingleSetting("sort_3");
                SORT_3_DIR = getSingleSetting("sort_3_dir");
                SORT_4 = getSingleSetting("sort_4");
                SORT_4_DIR = getSingleSetting("sort_4_dir");
                FILTER_DEF = getSingleSetting("filter_def");
                FILTER_CATEGORY = getSingleSetting("filter_category");
                FILTER_STORE = getSingleSetting("filter_store");
//                NEED_VIEW=getSingleSetting("need_view");
                try {
                    TABLE_VIEW = Integer.decode(getSingleSetting("need_view"));
                } catch (NumberFormatException e) {
                    TABLE_VIEW = TABLE_ALL;
                }

                String viewStr = getSingleSetting("view");
                if (!viewStr.equals("")) {
                    VIEW_KEY = new Integer(viewStr);
                } else {
                    VIEW_KEY = 0;
                }
            } else {
                settingsOK = false;
            }
        }
        if (!settingsOK) {
/*
            database.execSQL("DROP TABLE IF EXISTS settings");
            database.execSQL(CreateShopDatabase.CREATE_SETTINGS);
*/
            initialValues = new ContentValues();
            initialValues.put(KEY_SETTINGSNAME, settingsName);
//            n_inst=database.insert(DATABASE_SETTINGS, null, initialValues);
            initialValues.put("sort_1", "category");
            initialValues.put("view", "1");
            database.insert(DATABASE_SETTINGS, null, initialValues);
        }
        settingsCursor.close();
    }

    /**
     * Get a single setting from table DATABASE_SETTINGS
     *
     * @param settingStr - setting identifier
     * @return setting value (string)
     */
    private String getSingleSetting(String settingStr) {
        int colIndex = settingsCursor.getColumnIndex(settingStr);
        String settingVal;
        if (colIndex != -1) {
            try {
                settingVal = settingsCursor.getString(colIndex);
            } catch (Error e) {
                settingVal = "";
            }
        } else {
            c_execSQL("ALTER TABLE " + DATABASE_SETTINGS + " ADD COLUMN " + settingStr + " TEXT DEFAULT ''");
            settingVal = "";
        }
        return settingVal;
//           ShopDbAdapter.VIEW_KEY=0;
    }

    public Float getFloatConstants(String constStr) {
        Cursor constantsCursor = null;
        Float constant;
        ContentValues initialValues;
        long n_inst;
        int colIndex;

        try {
            constantsCursor = database.query(DATABASE_CONSTANTS, null, KEY_ROWID + "=1", null, null,
                    null, null);
            if (constantsCursor.getCount() == 0) {
                initialValues = new ContentValues();
                initialValues.put(constStr, 0F);
                n_inst = database.insert(DATABASE_CONSTANTS, null, initialValues);
            }
            constantsCursor.moveToFirst();
        } catch (SQLException e) {

        }


        colIndex = constantsCursor.getColumnIndex(constStr);

        if (colIndex != -1) {
            try {
                constant = constantsCursor.getFloat(colIndex);
            } catch (Exception e) {
                constant = 0F;
            }
        } else {
            c_execSQL("ALTER TABLE " + DATABASE_CONSTANTS + " ADD COLUMN " + constStr + " FLOAT DEFAULT 0.0 ");
            initialValues = new ContentValues();
            initialValues.put(constStr, 0F);
            n_inst = database.insert(DATABASE_CONSTANTS, null, initialValues);
            constant = 0F;
        }
        constantsCursor.close();
        return constant;
    }

    public String getStringConstants(String constStr) {
        Cursor constantsCursor = null;
        String constant;
        int colIndex;

        ContentValues initialValues;
        long n_inst;

        try {
            constantsCursor = database.query(DATABASE_CONSTANTS, null, KEY_ROWID + "=0", null, null,
                    null, null);
            if (settingsCursor.getCount() == 0) {
                initialValues = new ContentValues();
                initialValues.put(constStr, 0F);
                n_inst = database.insert(DATABASE_CONSTANTS, null, initialValues);
            }
            constantsCursor.moveToFirst();
        } catch (SQLException e) {

        }

        colIndex = constantsCursor.getColumnIndex(constStr);

        if (colIndex != -1) {
            try {
                constant = constantsCursor.getString(colIndex);
            } catch (Error e) {
                constant = "";
            }
        } else {
            c_execSQL("ALTER TABLE " + DATABASE_CONSTANTS + " ADD COLUMN " + constStr + " STRING DEFAULT ''");
            constant = "";
        }
        return constant;
    }

    public int setFloatConstants(String constStr, Float constant) {
        Cursor constantsCursor = null;
        ContentValues values = new ContentValues();

        int colIndex = constantsCursor.getColumnIndex(constStr);

        if (colIndex == -1) {
            c_execSQL("ALTER TABLE " + DATABASE_CONSTANTS + " ADD COLUMN " + constStr + " FLOAT DEFAULT 0.0 ");
            colIndex = constantsCursor.getColumnIndex(constStr);
        }

        if (colIndex != -1) {
            try {
                constant = constantsCursor.getFloat(colIndex);
                values.put(constStr, constant);
                int row = database.update(DATABASE_CONSTANTS, values, null, null);
            } catch (Error e) {
                Log.v("Constant error", constStr);
            }
        } else {
            Log.v("Constant error", constStr);
        }

        return colIndex;
    }

    public int setStringConstants(String constStr, String constant) {
        Cursor constantsCursor = null;
        ContentValues values = new ContentValues();

        int colIndex = constantsCursor.getColumnIndex(constStr);

        if (colIndex == -1) {
            c_execSQL("ALTER TABLE " + DATABASE_CONSTANTS + " ADD COLUMN " + constStr + " STRING DEFAULT '' ");
            colIndex = constantsCursor.getColumnIndex(constStr);
        }

        if (colIndex != -1) {
            try {
                values.put(constStr, constant);
                int row = database.update(DATABASE_CONSTANTS, values, null, null);
            } catch (Error e) {
                Log.v("Constant error", constStr);
            }
        } else {
            Log.v("Constant error", constStr);
        }

        return colIndex;
    }

    //    public void setSetting(String setting, String value){
    public boolean setSetting(String setting, String value, String settingsName) {
        ContentValues values = new ContentValues();
        int row = 0;
        values.put(setting, value);
        if (settingsName.equals("")) {
            setSetting(setting, value, SETTINGS_PLAN_ALL);
            setSetting(setting, value, SETTINGS_PLAN_STORE);
            setSetting(setting, value, SETTINGS_SHOP_ALL);
            setSetting(setting, value, SETTINGS_SHOP_STORE);
        } else {
            row = database.update(DATABASE_SETTINGS, values, KEY_SETTINGSNAME + "=\"" + settingsName + "\"", null);
        }
        return row > 0;
//        return database.update(DATABASE_SETTINGS, values, KEY_SETTINGSNAME + "=\""+settingsName+"\"", null) > 0;
//        return true;
    }

    /**
     * Return a Cursor over the list of all items in the database
     *
     * @return Cursor over all notes
     */
    public Cursor fetchAllShops(String itemFilter) {
        Cursor mCursor;
        String FILTER;
        String needed = NEED_NEED;
        String SS1;

        if (SORT_1.equalsIgnoreCase("Date") | SORT_1.equalsIgnoreCase("Aisle")) {
            SS1 = SORT_1 + "='' ASC, CAST (" + SORT_1 + " AS INTEGER) " + SORT_1_DIR + " , ";
        } else {
            SS1 = SORT_1 + "='' ASC, " + SORT_1 + " " + SORT_1_DIR + " , ";
        }

        String sort = SORT_NEED + " , " + SS1 +
                SORT_2 + " " + SORT_2_DIR + " , " +
                SORT_3 + " " + SORT_3_DIR + " , " +
                SORT_4 + " " + SORT_4_DIR;

        if (TABLE_VIEW.equals(TABLE_ALL)) {
            FILTER = FILTER_ALL;
            if (!FILTER_CATEGORY.equals("")) {
                FILTER = "(category='" + FILTER_CATEGORY + "')";
            }
        } else {
            FILTER = FILTER_NEED;

            if (!FILTER_CATEGORY.equals("")) {
                FILTER ="("+ FILTER + ") and (category='" + FILTER_CATEGORY + "')";
            }
        }

/*
        if (FILTER.equals("")) {
            FILTER = FILTER_ALL;
*/
/*
            if (!FILTER_STORE.equals("")) {
                FILTER = "("+KEY_STORE+"='" + FILTER_STORE + "')";
            }
*//*

        } else {
            FILTER = FILTER + FILTER_ALL;

*/
/*
            if (!FILTER_STORE.equals("")) {
                FILTER = FILTER + "("+KEY_STORE+"='" + FILTER_STORE+ "')";
            }
*//*

        }
*/

        if (!itemFilter.equals("")) {
            if (!FILTER.equals("")) {
                FILTER = "("+FILTER + ") and ";
            }
            FILTER = FILTER + " (" + KEY_ITEM + " like '" + itemFilter + "%' OR " + KEY_ITEM + " like '% " + itemFilter + "%')";
        }

        mCursor = database.query(DISP_TABLE, null, FILTER, null, null,
                null, sort);

        if (mCursor != null) {
            mCursor.moveToFirst();

        }
        return mCursor;
    }

    /**
     * Fetch shop items matching FILTERVAL
     *
     * @param FILTERVAL - Must include Field(s) and values
     * @return
     * @throws SQLException
     */
    public Cursor fetchShopItems(String FILTERVAL) throws SQLException {
        String needed = NEED_NEED;
        Cursor mCursor = database.query(DISP_TABLE, null, FILTERVAL, null, null,
                null, SORT_KEY);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    /**
     * Return a Cursor positioned at the defined item identified by rowId
     *
     * @param rowId
     * @return
     * @throws SQLException
     */
    public Cursor fetchItem(long rowId) throws SQLException {
        Cursor mCursor = database.query(true, DISP_TABLE, null,
                KEY_ROWID + "=" + rowId, null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    /**
     * Return a Cursor over the list of all values in the table
     *
     * @param dbTable - Table name
     * @return Cursor over all notes
     */
    public Cursor fetchAllValues(String dbTable, String orderKey) {
        Cursor mCursor;

        mCursor = database.query(dbTable, null, "", null, null, null, orderKey);

        if (mCursor != null) {
            mCursor.moveToFirst();

        }
        return mCursor;
    }

    //    overload
    public Cursor fetchAllValues(String dbTable) {
        return fetchAllValues(dbTable, "");

    }

    /**
     * Return a Cursor over the list of all values matching key and value in the table
     *
     * @param dbTable - Table name
     * @return Cursor over all notes
     */
    public Cursor fetchMatchValues(String dbTable, String key, String value) {
        Cursor mCursor;

        mCursor = database.query(dbTable, null, key + "= '" + value + "'", null, null, null, null);

        if (mCursor != null) {
            mCursor.moveToFirst();

        }
        return mCursor;
    }

    /**
     * Return a Cursor over the list of a single value in the table
     *
     * @param dbTable - Name for table of values
     * @param rowId
     * @return Cursor over all notes
     */
    public Cursor fetchValue(String dbTable, long rowId) {
        Cursor mCursor;

        mCursor = database.query(dbTable, null, KEY_ROWID + "=" + rowId, null, null, null, null);

        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    /**
     * Switch need to new value
     * TODO: Standard NEED sequence
     *
     * @param rowId
     * @param need
     * @return
     */
    public boolean updateNeed(long rowId, String need) {
        ContentValues updateValues = new ContentValues();
        updateValues.put(KEY_NEED, need);
        return database.update(DATABASE_TABLE, updateValues, KEY_ROWID + "="
                + rowId, null) > 0;
    }

    /**
     * putValue
     *
     * @param rowId - Item to be modified
     * @param field - Field
     * @param value - Value
     * @return
     */
    public boolean putValue(long rowId, String field, String value) {
        ContentValues updateValues = new ContentValues();
        updateValues.put(field, value);
        return database.update(DATABASE_TABLE, updateValues, KEY_ROWID + "="
                + rowId, null) > 0;
    }

    public boolean putFloat(long rowId, String field, Float value) {
        ContentValues updateValues = new ContentValues();
        updateValues.put(field, value);
        return database.update(DATABASE_TABLE, updateValues, KEY_ROWID + "="
                + rowId, null) > 0;
    }

    /**
     * Create a new item If the item is successfully created return the new
     * rowId for that note, otherwise return a -1 to indicate failure.
     *
     * @param dbTable
     * @param description
     * @return
     */
    public long createValue(String dbTable, String description) {
        String key;
        ContentValues values = new ContentValues();
        if (dbTable.equals(DATABASE_CATEGORIES)
                | dbTable.equals(DATABASE_CAT_STORE_TABLE)) {
            key = KEY_CATEGORY;
        } else {
            key = KEY_STORE;
        }

        // TODO If !value exist
        String filter = key + "='" + description + "'";

        Cursor mCursor = database.query(dbTable, new String[]{KEY_ROWID,
                key}, filter, null, null, null, null);

        //		Cursor mCursor = database.query(dbTable, new String[] { KEY_ROWID,
//				key}, filter, null, null, null, null);

        Long returnVal = 0L;
        if (mCursor == null) {
//Invalid database
            return 0;
        } else if (mCursor.getCount() == 0) {
            mCursor.moveToFirst();
            values.put(key, description);
            mCursor.close();
            returnVal = database.insert(dbTable, null, values);
        } else {
// Duplicate value
            return 0;
        }


        return returnVal;
    }

    /**
     * Update the value (Store/Category)
     *
     * @param dbTable     - Store/Category
     * @param rowId
     * @param description
     * @return
     */
    public boolean updateValue(String dbTable, long rowId, String description) {
        ContentValues values = new ContentValues();
        if (dbTable.equals(DATABASE_CATEGORIES)) {
            // TODO If !value exist
            values.put(KEY_CATEGORY, description);
        } else {
            values.put(KEY_STORE, description);
        }

        return database.update(dbTable, values, KEY_ROWID + "="
                + rowId, null) > 0;
    }

    /**
     * Deletes value rowid
     *
     * @param valId
     */
    public boolean deleteValue(String dbTable, long valId, String value) {
        int dbDel = database.delete(dbTable, KEY_ROWID + "=" + valId, null);
//        TODO:Delete cat's/stores
        if (dbTable.equals(DATABASE_CATEGORIES)) {

            String sqlQuery = "UPDATE " + DATABASE_TABLE + " SET " + KEY_CATEGORY
                    + "='-' WHERE " + KEY_CATEGORY + " ='" + value + "'";
            c_execSQL(sqlQuery);
            sqlQuery = "UPDATE " + DATABASE_SHOPITEMS + " SET " + KEY_CATEGORY
                    + "='-' WHERE " + KEY_CATEGORY + " ='" + value + "'";
            c_execSQL(sqlQuery);
            sqlQuery = "UPDATE " + SHOPPER_TABLE + " SET " + KEY_CATEGORY
                    + "='-' WHERE " + KEY_CATEGORY + " ='" + value + "'";
            c_execSQL(sqlQuery);
            dbDel = database.delete(DATABASE_CAT_STORE_TABLE, KEY_CATEGORY + "='" + value + "'", null);
            if (FILTER_CATEGORY.equals(value)) {
                FILTER_CATEGORY = "";
            }
// UPDATE dbTable  set CATEGORY='' where CATEGORY='
            getSettings(currentView());
            ShopDbAdapter.currentViewSettings = currentView();
        } else if (dbTable.equals(DATABASE_STORES)) {
            dbDel = database.delete(DATABASE_SHOPITEMS, KEY_STORE + "=" + value, null);
            dbDel = database.delete(DATABASE_CAT_STORE_TABLE, KEY_STORE + "=" + value, null);

            String sqlQuery = "UPDATE " + DATABASE_SHOPITEMS + " SET " + KEY_STORE
                    + "='' WHERE " + KEY_STORE + " ='" + value + "'";

            if (FILTER_STORE.equals(value)) {
                FILTER_STORE = "";
            }
            getSettings(currentView());
            ShopDbAdapter.currentViewSettings = currentView();
        }

        return dbDel > 0;
    }

    public boolean updateConstValue(String dbTable, ContentValues values) {

        int Row = database.update(dbTable, values, null
                , null);
        return Row > 0;
    }

    public boolean updateAisle(String dbTable, long rowId, String aisleTxt) {
        ContentValues values = new ContentValues();
        // TODO If !value exist
        values.put(KEY_AISLE, aisleTxt);

        return database.update(dbTable, values, KEY_ROWID + "="
                + rowId, null) > 0;
    }

    /**
     * Update the item
     * <p/>
     * Cleanup table to include only unique values
     *
     * @param valTable
     * @param dbTable
     */
    public void uniqueValue(String valTable, String dbTable) {
        String key;
        String sqlExe;

        ContentValues values = new ContentValues();
        if (valTable.equals(DATABASE_CATEGORIES)) {
            key = KEY_CATEGORY;
        } else {
            key = KEY_STORE;
        }

        c_execSQL("ALTER TABLE cattemp RENAME TO " + valTable);
        c_execSQL("DROP TABLE IF EXISTS csold");

        c_execSQL("CREATE TABLE cattemp (_id INTEGER PRIMARY KEY AUTOINCREMENT, " + key + " TEXT)");
        c_execSQL("INSERT INTO cattemp (" + key + ") SELECT DISTINCT " + key + " FROM " + dbTable);

        c_execSQL("ALTER TABLE " + valTable + " RENAME TO catold");
        c_execSQL("ALTER TABLE cattemp RENAME TO " + valTable);

        c_execSQL("DROP TABLE IF EXISTS catold");
    }

    /**
     * Save default aisles per category in Store
     *
     * @param store
     * @param category
     * @param aisle
     */
    public void storesCatFill(String store, String category, String aisle) {
//        UPDATE itemsinshop SET aisle='700' WHERE (category='Frost' AND storename='Føtex')
        c_execSQL("UPDATE " + DATABASE_SHOPITEMS + " SET " + KEY_AISLE + " = '" + aisle + "' WHERE ("
                + KEY_CATEGORY + "='" + category + "' AND " + KEY_STORE + "= '" + store + "')");
    }

    public void storeItemPrice(String store, Long itemID, String Price) {
//        UPDATE itemsinshop SET aisle='700' WHERE (category='Frost' AND storename='Føtex')
        ContentValues values = new ContentValues();
        Float fPrice = new Float(Price);
        values.put(KEY_PRICE, fPrice);
        int Row = database.update(DATABASE_SHOPITEMS, values,
                KEY_ITEMID + "=" + itemID + " AND " + KEY_STORE + "= '" + store + "'", null);
/*
        c_execSQL("UPDATE " + DATABASE_SHOPITEMS + " SET " + KEY_PRICE+ " = '" + new Float(Price) + "' WHERE ("
                + KEY_ITEMID+ "='" + itemID + "' AND " + KEY_STORE + "= '" + store + "')");
*/
    }

    public void storeItemAisle(String store, Long itemID, String Aisle) {
//        UPDATE itemsinshop SET aisle='700' WHERE (category='Frost' AND storename='Føtex')
        ContentValues values = new ContentValues();
        values.put(KEY_AISLE, Aisle);
        int Row = database.update(DATABASE_SHOPITEMS, values,
                KEY_ITEMID + "=" + itemID + " AND " + KEY_STORE + "= '" + store + "'", null);
/*
        c_execSQL("UPDATE " + DATABASE_SHOPITEMS + " SET " + KEY_AISLE + " = '" + Aisle + "' WHERE ("
                + KEY_ITEMID + "='" + itemID + "' AND " + KEY_STORE + "= '" + store + "')");
*/
    }


    /**
     * Put aisle and price in DATABASE_SHOPITEMS for item_id in store
     *
     * @param item_id
     * @param store
     * @param aisle
     * @param price
     * @return
     */
    public boolean storesAisleFill(Long item_id, String store, String aisle, Float price) {
//        UPDATE itemsinshop SET aisle='700' WHERE (category='Frost' AND storename='Føtex')
        ContentValues updateValues = new ContentValues();
        updateValues.put(KEY_AISLE, aisle);
        updateValues.put(KEY_PRICE, price);
//        updateValues.put(KEY_AISLE, aisle);
/*
        int xb=database.update(DATABASE_SHOPITEMS, updateValues, KEY_ROWID + "="
                + item_id, null);
*/
        int xb = database.update(DATABASE_SHOPITEMS, updateValues,

                KEY_ITEMID + "=" + item_id + " AND " + KEY_STORE + "= '" + store + "'",
//                KEY_ROWID + "="+ item_id,
                null);
/*
        c_execSQL("UPDATE " + DATABASE_SHOPITEMS + " SET ("
                + KEY_AISLE + " = '" + aisle+" , "
                + KEY_PRICE + " = '" + price
                +"') WHERE ("
                + KEY_ITEMID + "='" + item_id + "' AND " + KEY_STORE + "= '" + store + "')");
*/
        return xb > 0;
    }

    public Cursor shopsAislePrice(String dbName, Long dbValue) {
        Cursor mCursor;
        String FILTER;
        String key;
        String list;
        String needed = NEED_NEED;
        key = KEY_ITEMID;
        list = KEY_STORE;

//        FILTER = "(" + key + "=\"" + dbValue.toString() + "\")";
        if (dbValue == null) {
            FILTER = null;
        } else {
            FILTER = "(" + key + "=" + dbValue.toString() + ")";
        }
        mCursor = database.query(DATABASE_SHOPITEMS, new String[]{KEY_ROWID,
                list, KEY_AISLE, KEY_PRICE}, FILTER, null, null,
                null, KEY_STORE + " IS NULL ASC, " + KEY_STORE + " ASC");
//order by cast(<columnName> as integer)
        if (mCursor != null) {
            mCursor.moveToFirst();

        }
        return mCursor;
    }

    /**
     * Make all Category/Store combinations
     */
    public void catsInStoreUnfold() {
        c_execSQL("CREATE TABLE cstemp (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_CATEGORY + " TEXT, " + KEY_STORE + " TEXT, " + KEY_AISLE + " TEXT, " +
                KEY_PRICE + " TEXT)");

        c_execSQL("INSERT INTO cstemp (category, storename) SELECT category, storename FROM categories, stores");
        c_execSQL("ALTER TABLE " + DATABASE_CAT_STORE_TABLE + " RENAME TO csold");
        c_execSQL("ALTER TABLE cstemp RENAME TO " + DATABASE_CAT_STORE_TABLE);
        c_execSQL("DROP TABLE csold");
    }

    /**
     * Make all Category/Store combinations
     */
    public void itemInStoresUnfold(long rowID) {
/*
        c_execSQL("CREATE TABLE istemp (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_ITEMID + " integer, " +
                KEY_CATEGORY + " TEXT, " +
                KEY_STORE + " TEXT, " +
                KEY_AISLE + " TEXT, " +
                KEY_PRICE + " TEXT)");

        String SQLSTR = "INSERT INTO "+DATABASE_SHOPITEMS+" (" + KEY_ITEMID + ", " + KEY_CATEGORY + ", " + KEY_STORE + ", " + KEY_AISLE + ") " +
                "SELECT DISTINCT "
                + DATABASE_TABLE + "." + KEY_ROWID + ", "
                + DATABASE_TABLE + "." + KEY_CATEGORY + ", "
                + DATABASE_CAT_STORE_TABLE + "." + KEY_STORE + ", "
                + DATABASE_CAT_STORE_TABLE + "." + KEY_AISLE +
                " FROM " + DATABASE_TABLE + ", " + DATABASE_CAT_STORE_TABLE
                + " WHERE " + DATABASE_TABLE + "." + KEY_ROWID + "="
                + rowID;
        String SSQL = "INSERT INTO istemp (" + KEY_ITEMID + ", " + KEY_CATEGORY + ", " + KEY_STORE + ", " + KEY_AISLE + ") " +
                "SELECT DISTINCT "
                + DATABASE_TABLE + "." + KEY_ROWID + ", "
                + DATABASE_CAT_STORE_TABLE + "." + KEY_STORE + ", "
                + DATABASE_CAT_STORE_TABLE + "." + KEY_AISLE +
                " FROM " + DATABASE_TABLE + ", " + DATABASE_CAT_STORE_TABLE
                + " WHERE "
                + DATABASE_TABLE + "." + KEY_CATEGORY + "="
                + DATABASE_CAT_STORE_TABLE + "." + KEY_CATEGORY
                + " AND "
                + DATABASE_TABLE + "." + KEY_ROWID + "="
                + rowID;
*/
        c_execSQL("INSERT INTO "+DATABASE_SHOPITEMS+" (" + KEY_ITEMID + ", " + KEY_STORE + ", " + KEY_AISLE + ", " + KEY_CATEGORY + ") " +
                "SELECT DISTINCT "
                + DATABASE_TABLE + "." + KEY_ROWID + ", "
                + DATABASE_CAT_STORE_TABLE + "." + KEY_STORE + ", "
                + DATABASE_CAT_STORE_TABLE + "." + KEY_AISLE + ", "
                + DATABASE_CAT_STORE_TABLE + "." + KEY_CATEGORY
                + " FROM " + DATABASE_TABLE + ", " + DATABASE_CAT_STORE_TABLE
                + " WHERE "
                + DATABASE_TABLE + "." + KEY_CATEGORY + "="
                + DATABASE_CAT_STORE_TABLE + "." + KEY_CATEGORY
                + " AND "
                + DATABASE_TABLE + "." + KEY_ROWID + "="
                + rowID);
/*
        c_execSQL("INSERT INTO istemp (" + KEY_ITEMID + ", " + KEY_CATEGORY + ", " + KEY_STORE + ", " + KEY_AISLE + ") " +
                "SELECT DISTINCT "
                + DATABASE_TABLE + "."+KEY_ROWID + ", "
                + DATABASE_TABLE + "." + KEY_CATEGORY + ", "
                + DATABASE_CAT_STORE_TABLE + "." + KEY_STORE + ", "
                + DATABASE_CAT_STORE_TABLE + "." + KEY_AISLE +
                " FROM " + DATABASE_TABLE + ", " + DATABASE_CAT_STORE_TABLE
                + " WHERE " + DATABASE_TABLE + "." + KEY_ROWID + "="
                + rowID);
*/
//		DATABASE_CAT_STORE_TABLE
/*
        c_execSQL("ALTER TABLE " + DATABASE_SHOPITEMS + " RENAME TO isold");
        c_execSQL("ALTER TABLE istemp RENAME TO " + DATABASE_SHOPITEMS);
        c_execSQL("DROP TABLE isold");
*/
    }

    /**
     * Make all Category/Store combinations for new store
     *
     * @param newStore
     */
    public void catsInNewStoreUnfold(String newStore) {
// INSERT INTO cstemp (category, storename) SELECT category, storename FROM categories, stores WHERE storename='oneStore'
        String cINSU = "INSERT INTO " + DATABASE_CAT_STORE_TABLE +
                " (" + KEY_CATEGORY + ", " + KEY_STORE + ") " +
                "SELECT " + KEY_CATEGORY + ", " + KEY_STORE +
                " FROM " + DATABASE_CATEGORIES + ", " + DATABASE_STORES +
                " WHERE " + KEY_STORE + "='" + newStore + "'";
        c_execSQL(cINSU);
    }

    public void storeInNewCatUnfold(String newCat) {
// INSERT INTO cstemp (category, storename) SELECT category, storename FROM categories, stores WHERE storename='oneStore'
        String sINCU = "INSERT INTO " + DATABASE_CAT_STORE_TABLE +
                " (" + KEY_CATEGORY + ", " + KEY_STORE + ") " +
                "SELECT " + KEY_CATEGORY + ", " + KEY_STORE +
                " FROM " + DATABASE_CATEGORIES + ", " + DATABASE_STORES +
                " WHERE " + KEY_CATEGORY + "='" + newCat + "'";
        c_execSQL(sINCU);
    }

    /**
     * Unfold - Not called?
     */
    public void unfold() {
        String newStore = "";
        c_execSQL("INSERT INTO istemp (" + KEY_ITEMID + ", "
                + KEY_CATEGORY + ", "
                + KEY_STORE + ", "
                + KEY_AISLE + ") " +
                "SELECT "
                + DATABASE_SHOPITEMS + ".item_id, "
                + DATABASE_SHOPITEMS + "." + KEY_CATEGORY + ", "
                + DATABASE_SHOPITEMS + "." + KEY_AISLE +
                " FROM " + DATABASE_SHOPITEMS
                + " WHERE " + DATABASE_SHOPITEMS + "." + KEY_STORE + "="
                + "''");
        String cuf = "INSERT INTO " + DATABASE_CAT_STORE_TABLE +
                " (" + KEY_CATEGORY + ", " + KEY_STORE + ") " +
                "SELECT " + KEY_CATEGORY + ", " + KEY_STORE +
                " FROM " + DATABASE_CATEGORIES + ", " + DATABASE_STORES +
                " WHERE " + KEY_STORE + "='" + newStore + "'";
        c_execSQL(cuf);
        // INSERT INTO cstemp (category, storename) SELECT category, storename
        //              FROM categories, stores WHERE storename='oneStore'
    }

    /**
     * Items in store unfold - Build Items in store table SHOPITEMS - incl. aisles etc.
     */
    public void itemsInStoreUnfold() {
        c_execSQL("CREATE TABLE istemp (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_ITEMID + " integer, " +
                KEY_CATEGORY + " TEXT, " +
                KEY_STORE + " TEXT, " +
                KEY_AISLE + " TEXT, " +
                KEY_PRICE + " TEXT)");

        c_execSQL("INSERT INTO istemp (" + KEY_ITEMID + ", " + KEY_CATEGORY + ", " + KEY_STORE + ", " + KEY_AISLE + ") " +
                "SELECT "
                + DATABASE_TABLE + "._id, "
                + DATABASE_TABLE + "." + KEY_CATEGORY + ", "
                + DATABASE_CAT_STORE_TABLE + "." + KEY_STORE + ", "
                + DATABASE_CAT_STORE_TABLE + "." + KEY_AISLE +
                " FROM " + DATABASE_TABLE + ", " + DATABASE_CAT_STORE_TABLE
                + " WHERE " + DATABASE_TABLE + "." + KEY_CATEGORY + "="
                + DATABASE_CAT_STORE_TABLE + "." + KEY_CATEGORY);
//		DATABASE_CAT_STORE_TABLE
        c_execSQL("ALTER TABLE " + DATABASE_SHOPITEMS + " RENAME TO isold");
        c_execSQL("ALTER TABLE istemp RENAME TO " + DATABASE_SHOPITEMS);
        c_execSQL("DROP TABLE isold");
    }

    public void itemsInNewStoreUnfold(String newStore) {
/*
        String iINSU = "INSERT INTO " +
                DATABASE_SHOPITEMS +
                "(" + KEY_ITEMID + ", " + KEY_CATEGORY + ", " + KEY_STORE + ", " + KEY_AISLE + ") " +
                "SELECT DISTINCT "
                + DATABASE_TABLE + "._id, "
                + DATABASE_TABLE + "." + KEY_CATEGORY + ", "
                + DATABASE_CAT_STORE_TABLE + "." + KEY_STORE + ", "
                + DATABASE_CAT_STORE_TABLE + "." + KEY_AISLE +
                " FROM " + DATABASE_TABLE + ", " + DATABASE_CAT_STORE_TABLE
                + " WHERE " + DATABASE_TABLE + "." + KEY_CATEGORY + "="
                + DATABASE_CAT_STORE_TABLE + "." + KEY_CATEGORY
                + " AND " + DATABASE_CAT_STORE_TABLE + "." + KEY_STORE + "="
                + "'" + newStore + "'";
*/

        String iINSU = "INSERT INTO " +
                DATABASE_SHOPITEMS +
                "(" + KEY_ITEMID + ", " + KEY_CATEGORY + ", " + KEY_STORE + ", " + KEY_AISLE + ") " +
                "SELECT DISTINCT "
                + DATABASE_TABLE + "._id, "
                + DATABASE_TABLE + "." + KEY_CATEGORY + ", "
                + DATABASE_CAT_STORE_TABLE + "." + KEY_STORE + ", "
                + DATABASE_CAT_STORE_TABLE + "." + KEY_AISLE +
                " FROM " + DATABASE_TABLE + ", " + DATABASE_CAT_STORE_TABLE
                + " WHERE " + DATABASE_TABLE + "." + KEY_CATEGORY + "="
                + DATABASE_CAT_STORE_TABLE + "." + KEY_CATEGORY
                + " AND " + DATABASE_CAT_STORE_TABLE + "." + KEY_STORE + "="
                + "'" + newStore + "'";

        c_execSQL("INSERT INTO " +
                DATABASE_SHOPITEMS +
                "(" + KEY_ITEMID + ", " + KEY_CATEGORY + ", " + KEY_STORE + ", " + KEY_AISLE + ") " +
                "SELECT DISTINCT "
                + DATABASE_TABLE + "._id, "
                + DATABASE_TABLE + "." + KEY_CATEGORY + ", "
                + DATABASE_CAT_STORE_TABLE + "." + KEY_STORE + ", "
                + DATABASE_CAT_STORE_TABLE + "." + KEY_AISLE +
                " FROM " + DATABASE_TABLE + ", " + DATABASE_CAT_STORE_TABLE
                + " WHERE " + DATABASE_TABLE + "." + KEY_CATEGORY + "="
                + DATABASE_CAT_STORE_TABLE + "." + KEY_CATEGORY
                + " AND " + DATABASE_CAT_STORE_TABLE + "." + KEY_STORE + "="
                + "'" + newStore + "'");
    }

    /**
     * Create a storeshopper table
     * All items are assigned aisle price from SHOPITEMS table
     *
     * @param chosenStore
     */
    public void itemAisleToDb(String chosenStore) {
        c_execSQL("DROP TABLE storeshopper");
        String CREATE_DATABASE2 = "create table storeshopper (_id integer primary key autoincrement, " +
                "need text default '', " +
                "priority text default '', " +
                "item text default '', " +
                "note text default '', " +
                "category text default '', " +
                "quantity integer default 0, " +
                "units string default '', " +
                "price text default '', " +
                "aisle text default ''," +
                "date text default '', " +
                "entryorder integer default 0, " +
                "coupon integer default 0, " +
                "tax text default '', " +
                "tax2 text default '', " +
                "autoDelete integer default 0, " +
                "private integer default 0, " +
                "alarm integer default 0, " +
                "alarmmidi integer default 0, " +
                "icon integer default 0," +
                "created text default '', " +
                "modified text default ''" +
                ");";
        c_execSQL(CREATE_DATABASE2);

//        c_execSQL("DROP TABLE IF EXISTS" + DATABASE_SHOPITEMSSHOW);

        String ATD = "CREATE TABLE " + DATABASE_SHOPITEMSSHOW + " AS SELECT " +
                DATABASE_TABLE + "._id, " +
                KEY_NEED + "," +
                KEY_PRIORITY + "," +
                KEY_ITEM + "," +
                KEY_NOTE + "," +
                DATABASE_TABLE + "." + KEY_CATEGORY + ", " +
                KEY_QUANTITY + "," +
                KEY_UNITS + "," +
                DATABASE_SHOPITEMS + "." + KEY_PRICE + ", " +
                DATABASE_SHOPITEMS + "." + KEY_AISLE + ", " +
                KEY_DATE + "," +
                KEY_ENTRYORDER + "," +
                KEY_COUPON + "," +
                KEY_TAX + "," +
                KEY_TAX2 + "," +
                KEY_AUTODELETE + "," +
                KEY_PRIVATE + "," +
                KEY_ALARM + "," +
                KEY_ALARMMIDI + "," +
                KEY_ICON + "," +
                KEY_CREATED + "," +
                KEY_MODIFIED +
                " FROM " + DATABASE_TABLE + ", " + DATABASE_SHOPITEMS +
                " WHERE (" + DATABASE_TABLE + "." + KEY_CATEGORY + "=" +
                DATABASE_SHOPITEMS + "." + KEY_CATEGORY +
                " AND " + DATABASE_SHOPITEMS + "." + KEY_ITEMID + "=" + DATABASE_TABLE + "." + KEY_ROWID +
                " AND " + DATABASE_SHOPITEMS + "." + KEY_STORE + "='" + chosenStore + "')";

        c_execSQL(ATD);
        c_execSQL("INSERT INTO " + SHOPPER_TABLE + " SELECT * FROM " + DATABASE_SHOPITEMSSHOW);
        c_execSQL("DROP TABLE IF EXISTS " + DATABASE_SHOPITEMSSHOW);
    }

    /**
     * Error catching SQL executer
     *
     * @param sqlString
     */
    public void c_execSQL(String sqlString) {
        try {
            database.execSQL(sqlString);
        } catch (SQLException e) {
            Log.v("ShopDbAdapter SQL", "sqlString");
        }
    }

    /**
     * Deletes item
     */
    public boolean deleteItem(String dbTable, long rowId) {
        return database.delete(dbTable, KEY_ROWID + "=" + rowId, null) > 0;
    }

    /**
     * Creat Store/Category table combinations for a single Store/Category
     *
     * @param dbName
     * @param dbValue
     * @return
     */
    public Cursor catInStore(String dbName, String dbValue) {
        Cursor mCursor = null;
        String FILTER;
        String key;
        String list;
        String needed = NEED_NEED;
        if (dbName.equals(DATABASE_STORES)) {
            key = KEY_CATEGORY;
            list = KEY_STORE;
        } else {
            key = KEY_STORE;
            list = KEY_CATEGORY;
        }

        if (dbValue == null) {
            FILTER = "(" + key + "=\"\")";
        } else {
            FILTER = "(" + key + "=\"" + dbValue + "\")";
        }

        if (dbName.equals(DATABASE_CAT_STORE_TABLE)) {
            mCursor = database.query(dbName, new String[]{KEY_ROWID,
                    list, KEY_AISLE}, FILTER, null, null,
                    null, KEY_AISLE + " IS NULL ASC, CAST(" + KEY_AISLE + " AS INTEGER) ASC");
        }
/*
        mCursor = database.query(DATABASE_CAT_STORE_TABLE, new String[]{KEY_ROWID,
                list, KEY_AISLE, KEY_PRICE}, FILTER, null, null,
                null, KEY_AISLE + " IS NULL ASC, CAST(" + KEY_AISLE + " AS INTEGER) ASC");
*/
        if (dbName.equals(SHOPPER_TABLE)) {
            mCursor = database.query(SHOPPER_TABLE, new String[]{KEY_ROWID,
                    list, KEY_AISLE, KEY_PRICE}, FILTER, null, null,
                    null, KEY_AISLE + " IS NULL ASC, CAST(" + KEY_AISLE + " AS INTEGER) ASC");
        }
//order by cast(<columnName> as integer)
        if (mCursor != null) {
            mCursor.moveToFirst();

        }
        return mCursor;
    }

    /**
     * Fill Spinner with value
     *
     * @param conThis
     * @param dbName
     * @param valSetting
     * @param valueSpinner
     */
    public void spinFill(Context conThis, String dbName, String valSetting, Spinner valueSpinner) {
        String key;
        ArrayList<String> valsArrayList;
        valsArrayList = new ArrayList<String>();

        if (dbName.equals(ShopDbAdapter.DATABASE_CATEGORIES)) {
            key = ShopDbAdapter.KEY_CATEGORY;
        } else {
            key = ShopDbAdapter.KEY_STORE;
        }

        Cursor valsCursor = fetchAllValues(dbName, key);
//        startManagingCursor(valsCursor);

        if (valsCursor.getCount() == 0) {
            createValue(dbName, "");
        }

        valsArrayList.add("");

        if (valsCursor.getCount() != 0) {

            for (valsCursor.moveToFirst(); valsCursor.moveToNext(); valsCursor.isAfterLast()) {
                String x = valsCursor.getString(valsCursor.getColumnIndexOrThrow(key));

                valsArrayList.add(valsCursor.getString(valsCursor.getColumnIndexOrThrow(key)));
            }
            ArrayAdapter<String> vAdapt;
            vAdapt = new ArrayAdapter<String>(conThis, android.R.layout.simple_spinner_item, valsArrayList);
            valueSpinner.setAdapter(vAdapt);
            for (int i = 0; i < valsCursor.getCount(); i++) {
                String s = (String) valueSpinner.getItemAtPosition(i);
//                Log.e(null, s + " " + valSetting);
                if (s.equalsIgnoreCase(valSetting)) {
                    valueSpinner.setSelection(i);
                }
            }
        }
    }

    public String needSwitch(String need) {
        if (ShopDbAdapter.TABLE_VIEW.equals(ShopDbAdapter.TABLE_NEED)) {
            if (need.equals(ShopDbAdapter.NEED_NEED)) {
                need = ShopDbAdapter.NEED_BOUGHT;
            } else if (need.equals(ShopDbAdapter.NEED_BOUGHT)) {
                need = ShopDbAdapter.NEED_NEED;
            } else if (need.equals(ShopDbAdapter.NEED_LATER)) {
                need = ShopDbAdapter.NEED_BOUGHT;
            }
        } else {
            if (need.equals(ShopDbAdapter.NEED_NEED)) {
                need = ShopDbAdapter.NEED_HAVE;
            } else if (need.equals(ShopDbAdapter.NEED_HAVE)) {
                need = ShopDbAdapter.NEED_NEED;
            } else if (need.equals(ShopDbAdapter.NEED_BOUGHT)) {
                need = ShopDbAdapter.NEED_HAVE;
            } else if (need.equals(ShopDbAdapter.NEED_LATER)) {
                need = ShopDbAdapter.NEED_HAVE;
            }
        }
        return need;
    }

    void needViewCreate(String need, ImageView needView) {
//        TODO: Separate ikoner til Shop vs. Plan

        if (need.startsWith("x")) {
            needView.setImageResource(R.drawable.btn_check_on); // (x) vs _x_
        } else if (need.startsWith("have")) {
            needView.setImageResource(R.drawable.btn_circle_normal); // o
        } else if (need.equalsIgnoreCase("")) {
            needView.setImageResource(R.drawable.btn_check_off); // ( ) vs __
        } else if (need.startsWith("later")) {
            needView.setImageResource(R.drawable.btn_check_off_pressed);  //
        } else {
//                  error in NEED
//                holder.needView.setImageResource(R.drawable.btn_circle_normal);
        }
        needView.setTag(need);
    }


    /**
     * Show boolean in ToggleButton
     *
     * @param cursorDB
     * @param keyString
     * @param viewById
     * @return
     */
    public Boolean dbShowBool(Cursor cursorDB, String keyString, ToggleButton viewById) {
        int boolInt = cursorDB.getInt(cursorDB.getColumnIndexOrThrow(keyString));

        viewById.setChecked(boolInt != 0);

        return boolInt != 0;
    }


    /**
     * Show Integer in EditText
     *
     * @param cursorDB
     * @param keyString
     * @param viewById
     * @return
     */
    public Integer dbShowInt(Cursor cursorDB, String keyString, EditText viewById) {
        Integer intVal = cursorDB.getInt(cursorDB.getColumnIndexOrThrow(keyString));

        viewById.setText(String.valueOf(intVal));

        return intVal;
    }

    /**
     * Show Integer in EditText
     *
     * @param cursorDB
     * @param keyString
     * @param viewById
     * @return
     */
    public Float dbShowFloat(Cursor cursorDB, String keyString, EditText viewById) {
        Float floatVal = cursorDB.getFloat(cursorDB.getColumnIndexOrThrow(keyString));

        NumberFormat formatter;
        String floatStr;

        formatter = new DecimalFormat("#0.###");
        if (keyString.equals(KEY_PRICE)) {
            formatter = new DecimalFormat("#0.00");
        }
        floatStr = formatter.format(floatVal);

        viewById.setText(floatStr);

        return floatVal;
    }

    /**
     * Show priority image
     *
     * @param cursorDB
     * @param keyString
     * @param viewById
     * @return
     */
    public Integer dbShowPriority(Cursor cursorDB, String keyString, ImageView viewById) {
        Integer prioVal = cursorDB.getInt(cursorDB.getColumnIndexOrThrow(keyString));

        switch (prioVal) {
            case 1: {
                viewById.setImageResource(R.drawable.star1);
                break;
            }
            case 2: {
                viewById.setImageResource(R.drawable.star2);
                break;
            }
            case 3: {
                viewById.setImageResource(R.drawable.star3);
                break;
            }
            case 4: {
                viewById.setImageResource(R.drawable.star4);
                break;
            }
            case 5: {
                viewById.setImageResource(R.drawable.star5);
                break;
            }
            case 6: {
                viewById.setImageResource(R.drawable.star6);
                break;
            }
        }
        viewById.setTag(prioVal);
//            viewById.setText(String.valueOf(intVal));

        return prioVal;
    }

    /**
     * Show string
     *
     * @param cursorDB
     * @param keyString
     * @param viewById
     * @return
     */
    public String dbShowStr(Cursor cursorDB, String keyString, EditText viewById) {
        String strVal = cursorDB.getString(cursorDB.getColumnIndexOrThrow(keyString));

        viewById.setText(strVal);

        return strVal;
    }

    /**
     * Show date as string - and store msec in tag
     *
     * @param cursorDB
     * @param keyString
     * @param viewById
     * @return
     */
    public String dbShowDateStr(Cursor cursorDB, String keyString, TextView viewById) {
        Long dateMSecVal;
        if (cursorDB != null) {
            dateMSecVal = cursorDB.getLong(cursorDB.getColumnIndexOrThrow(keyString));
        } else {
            dateMSecVal = 0L;
        }
        String dateStr = "";
        if (dateMSecVal == 0L) {
//               dateMSec = Calendar.getInstance().getTimeInMillis();
            if (keyString.equals(KEY_DATE)) {
                dateStr = "Click to enter date";
            }

        } else {
            dateStr = dateConverter.msec2Str(dateMSecVal);
        }

        viewById.setText(dateStr);
        viewById.setTag(dateMSecVal);

        return dateStr;
    }

    public String dbShowDateStr2(Cursor cursorDB, String keyString, TextView viewById) {
        Long dateMSecVal = cursorDB.getLong(cursorDB.getColumnIndexOrThrow(keyString));
        String dateStr = "";
        if (dateMSecVal == 0) {
//               dateMSec = Calendar.getInstance().getTimeInMillis();
            if (keyString.equals(KEY_DATE)) {
                dateStr = "Click to enter date";
            }

        } else {
            dateStr = dateConverter.msec2Str(dateMSecVal);
        }

        viewById.setText(dateStr);
        viewById.setTag(dateMSecVal);

        return dateStr;
    }

    /**
     * Store boolean in database ContentValues
     *
     * @param cursorDB
     * @param values
     * @param keyString
     * @param viewById
     * @return
     */
    public boolean dbPutBool(Cursor cursorDB, ContentValues values, String keyString, ToggleButton viewById) {
        int boolInt;
        boolean boolVal;
        int oldBoolInt;

//        boolVal = viewById.isChecked() ? true : false;
        boolVal = viewById.isChecked();
        boolInt = boolVal ? 1 : 0;
        oldBoolInt = (cursorDB == null) ? 0 : cursorDB.getInt(cursorDB.getColumnIndexOrThrow(keyString));
//        oldBoolInt = (cursorDB == null) ? null : cursorDB.getInt(cursorDB.getColumnIndexOrThrow(keyString));

        if (oldBoolInt != boolInt) {
            values.put(keyString, boolInt);
            return true;
        } else {
            return false;
        }
    }

    public boolean dbPutNeed(Cursor cursorDB, ContentValues values, String keyString, ImageView viewById) {
        int boolInt;
        String needVal;
        String oldNeedVal;

//        boolVal = viewById.isChecked() ? true : false;
        needVal = (String) viewById.getTag();
        if (needVal == null) {
            needVal = "";
            values.put(keyString, needVal);
        }

        oldNeedVal = (cursorDB == null) ? "" : cursorDB.getString(cursorDB.getColumnIndexOrThrow(keyString));
        if (oldNeedVal == null) {
            values.put(keyString, needVal);
            oldNeedVal = "";
        }

        if (!oldNeedVal.equals(needVal)) {
            values.put(keyString, needVal);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Store Integer in database ContentValues
     *
     * @param cursorDB
     * @param values
     * @param keyString
     * @param viewById
     * @return True if updated / False if not
     */
    public boolean dbPutInt(Cursor cursorDB, ContentValues values, String keyString, EditText viewById) {
        int intVal;
        int oldIntVal;
        try {
            String intTxt = viewById.getText().toString();
            intVal = new Integer(intTxt);
        } catch (NumberFormatException e) {
            Log.v("ItemDetails", keyString);
            intVal = 0;
        }

        oldIntVal = (cursorDB == null) ? 0 : cursorDB.getInt(cursorDB.getColumnIndexOrThrow(keyString));

        if (intVal != oldIntVal) {
            values.put(keyString, intVal);
            return true;
        } else {
            return false;
        }
    }

    public boolean dbPutFloat(Cursor cursorDB, ContentValues values, String keyString, EditText viewById) {
        Float floatVal;
        Float oldFloatVal;
        try {
            String intTxt = viewById.getText().toString();
            floatVal = new Float(intTxt);
        } catch (NumberFormatException e) {
            Log.v("ItemDetails", keyString);
            floatVal = 0F;
        }

        oldFloatVal = (cursorDB == null) ? 0F : cursorDB.getFloat(cursorDB.getColumnIndexOrThrow(keyString));

        if (floatVal != oldFloatVal) {
            values.put(keyString, floatVal);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Store string in database ContentValues
     *
     * @param cursorDB
     * @param values
     * @param keyString
     * @param viewById
     * @return
     */
    public boolean dbPutStr(Cursor cursorDB, ContentValues values, String keyString, EditText viewById) {
        String strVal;
        String oldStrVal;
        try {
            strVal = viewById.getText().toString();
        } catch (NumberFormatException e) {
            Log.v("ItemDetails", keyString);
            strVal = "";
        }

        boolean cdb = (cursorDB == null);

        oldStrVal = (cursorDB == null) ? null : cursorDB.getString(cursorDB.getColumnIndexOrThrow(keyString));
//        oldStrVal = cursorDB==null ? null :cursorDB.getShort(cursorDB.getColumnIndexOrThrow(keyString));
/*
        if (cursorDB == null) {
            oldStrVal = "";
        } else {
            oldStrVal = cursorDB==null ? null :cursorDB.getShort(cursorDB.getColumnIndexOrThrow(keyString));
        }
*/

//        int oldStrVal = cursorDB.getShort(cursorDB.getColumnIndexOrThrow(keyString));

        if (!strVal.equals(oldStrVal)) {
            values.put(keyString, strVal);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Store date in database ContentValues
     * Value is stored in tag
     *
     * @param cursorDB
     * @param values
     * @param keyString
     * @param viewById
     * @return
     */
    public boolean dbPutDateStr(Cursor cursorDB, ContentValues values, String keyString, View viewById) {
        Long mSecVal;
        try {
//            mSecVal = viewById.getText().toString();
// TODO: If tag is blank!
            mSecVal = (Long) viewById.getTag();
        } catch (NumberFormatException e) {
            Log.v("ItemDetails", keyString);
            mSecVal = (long) 0;
        }
        Long oldMSecVal = (cursorDB == null) ? 0 : cursorDB.getLong(cursorDB.getColumnIndexOrThrow(ShopDbAdapter.KEY_DATE));

        if (!mSecVal.equals(oldMSecVal)) {
            values.put(keyString, mSecVal);
            return true;
        } else {
            return false;
        }
    }

    public boolean dbPutPriority(Cursor cursorDB, ContentValues values, String keyString, ImageView viewById) {
        Integer priority;
        try {
// TODO:Priority icons           mSecVal = viewById.getText().toString();
            priority = (Integer) viewById.getTag();
        } catch (NumberFormatException e) {
            Log.v("ItemDetails", keyString);
            priority = 0;
        }
        Integer oldPrio = (cursorDB == null) ? 0 : cursorDB.getInt(cursorDB.getColumnIndexOrThrow(ShopDbAdapter.KEY_PRIORITY));

        if (!priority.equals(oldPrio)) {
            values.put(keyString, priority);
            return true;
        } else {
            return false;
        }
    }

    public Float dbSumPrice(String needCriteria) {

        Float sumPrice;
        Float sumTax1 = 0.0F;
        Float sumTax2 = 0.0F;
        Float tax1 = 0.0F;
        Float tax2 = 0.0F;
        Cursor sumCursor;

        tax1 = getFloatConstants(KEY_TAX) / 100;
        tax2 = getFloatConstants(KEY_TAX2) / 100;

        String query = "SELECT SUM(" + KEY_PRICE + ") FROM "
                + DISP_TABLE + " WHERE " + KEY_NEED + "='" + needCriteria + "'";
//        sumCursor = database.rawQuery(query);
        sumCursor = database.rawQuery("SELECT SUM(" + KEY_PRICE + ") FROM "
                + DISP_TABLE + " WHERE " + KEY_NEED + "='" + needCriteria + "'", null);
//        startManagingCursor(sumCursor);
/*
        String query = "SELECT SUM(" + KEY_PRICE + ") FROM "
               + DATABASE_TABLE  + " WHERE " + KEY_NEED + "='" + needCriteria + "'";
//        sumCursor = database.rawQuery(query);
        sumCursor = database.rawQuery("SELECT SUM(" + KEY_PRICE + ") FROM "
                + DATABASE_TABLE + " WHERE " + KEY_NEED + "='" + needCriteria + "'", null);
*/

        if (sumCursor.moveToFirst()) {
            sumPrice = sumCursor.getFloat(0);
        } else {
            return null;
        }
        ;

        sumCursor = database.rawQuery("SELECT SUM(" + KEY_PRICE + ") FROM "
                + DISP_TABLE + " WHERE "
                + KEY_NEED + "='" + needCriteria + "' AND "
                + KEY_TAX + "=1 ", null);

        if (sumCursor.moveToFirst()) {
            sumTax1 = sumCursor.getFloat(0);
        } else {
            sumTax1 = 0F;
        }
        sumCursor = database.rawQuery("SELECT SUM(" + KEY_PRICE + ") FROM "
                + DISP_TABLE + " WHERE "
                + KEY_NEED + "='" + needCriteria + "' AND "
                + KEY_TAX2 + "=1 ", null);
        if (sumCursor.moveToFirst()) {
            sumTax2 = sumCursor.getFloat(0);
        } else {
            sumTax2 = 0F;
        }

        if (sumCursor.moveToFirst()) {
            return sumPrice + sumTax1 * tax1 + sumTax2 * tax2;
        } else {
            return null;
        }
    }

    public Integer dbCountItems(String needCriteria) {

        Float count;
        Cursor countCursor;

        String query = "SELECT SUM(" + KEY_QUANTITY + ") FROM "
                + DISP_TABLE + " WHERE " + KEY_NEED + "='" + needCriteria + "'";
//        sumCursor = database.rawQuery(query);
        countCursor = database.rawQuery("SELECT SUM(" + KEY_QUANTITY + ") FROM "
                + DISP_TABLE + " WHERE " + KEY_NEED + "='" + needCriteria + "'", null);

        if (countCursor.moveToFirst()) {
            int cc = countCursor.getInt(0);
            countCursor.close();
            return cc;
        } else {
            return null;
        }
    }

    /**
     * Create item from code
     *
     * @param need
     * @param priority
     * @param description
     * @param customtext
     * @param quantity
     * @param date
     * @param category
     * @param entryorder
     * @param autodelete
     * @param note
     * @return
     */
    public long createItemStr(String need,
                              String priority,
                              String description,
                              String customtext,
                              String quantity,
//			String units,
//			String price,
//			String aisle,
                              String date,
                              String category,
                              String entryorder,
//			String coupon,
//			String tax,
//			String tax2,
                              String autodelete,
//			String privateitem,
                              String note
//			String alarm,
//			String alarmmidi,
//			String icon,
//			String created,
//			String modified){
    ) {
        ContentValues initialValues = createContentValuesFromStr(need,
                priority,
                description,
                customtext,
                quantity,
//				units,
//				price,
//				aisle,
                date,
                category,
                entryorder,
//				coupon,
//				tax,
//				tax2,
                autodelete,
//				privateitem,
                note);
//				alarm,
//				alarmmidi,
//				icon,
//				created,
//				modified);

        return database.insert(DATABASE_TABLE, null, initialValues);
    }

    public String currentView() {
        String viewSettings;
//        if (ShopDbAdapter.TABLE_VIEW.equals(ShopDbAdapter.TABLE_NEED)) {
        if (ShopDbAdapter.TABLE_VIEW == ShopDbAdapter.TABLE_NEED) {
            if (ShopDbAdapter.FILTER_STORE.equals("")) {
                viewSettings = ShopDbAdapter.SETTINGS_SHOP_ALL;
            } else {
                viewSettings = ShopDbAdapter.SETTINGS_SHOP_STORE;
            }
        } else {
            if (ShopDbAdapter.FILTER_STORE.equals("")) {
                viewSettings = ShopDbAdapter.SETTINGS_PLAN_ALL;
            } else {
                viewSettings = ShopDbAdapter.SETTINGS_PLAN_STORE;
            }
        }
        return viewSettings;
    }

    /**
     * Create values from code
     *
     * @param need
     * @param priority
     * @param description
     * @param customtext
     * @param quantity
     * @param date
     * @param category
     * @param entryorder
     * @param autodelete
     * @param note
     * @return
     */

    private ContentValues createContentValuesFromStr(String need,
                                                     String priority,
                                                     String description,
                                                     String customtext,
                                                     String quantity,
//			String units,
//			String price,
//			String aisle,
                                                     String date,
                                                     String category,
                                                     String entryorder,
//			String coupon,
//			String tax,
//			String tax2,
                                                     String autodelete,
//			String privateitem,
                                                     String note) {
//			String alarm,
//			String alarmmidi,
//			String icon,
//			String created,
//			String modified) {

        ContentValues values = new ContentValues();
        values.put(KEY_NEED, need);
        values.put(KEY_PRIORITY, Integer.parseInt(priority));
        values.put(KEY_ITEM, description);
        values.put(KEY_CUSTOMTEXT, customtext);
        values.put(KEY_QUANTITY, Integer.parseInt(quantity));
//		values.put(KEY_UNITS, units);
//		values.put(KEY_PRICE, price);
//		values.put(KEY_AISLE, aisle);
        values.put(KEY_DATE, date);
        values.put(KEY_CATEGORY, category);
        values.put(KEY_ENTRYORDER, Integer.parseInt(entryorder));
//		values.put(KEY_COUPON, coupon);
//		values.put(KEY_TAX, tax);
//		values.put(KEY_TAX2, tax2);
        values.put(KEY_AUTODELETE, autodelete);
//		values.put(KEY_PRIVATE, privateitem);
        values.put(KEY_NOTE, note);
//		values.put(KEY_ALARM, alarm);
//		values.put(KEY_ALARMMIDI, Integer.parseInt(alarmmidi));
//		values.put(KEY_ICON, Integer.parseInt(icon));
//		values.put(KEY_CREATED, created);
//		values.put(KEY_MODIFIED, modified);

        return values;
    }

    /**
     * Load Key values to ContentValues object
     *
     * @param category
     * @param item
     * @param need
     * @param quantity
     * @return
     */
    private ContentValues createContentValues(String category, String item,
                                              String need, Long quantity) {
        ContentValues values = new ContentValues();
        values.put(KEY_CATEGORY, category);
        values.put(KEY_ITEM, item);
        values.put(KEY_NEED, need);
        values.put(KEY_QUANTITY, quantity);
        values.put(KEY_AISLE, "1");
        values.put(KEY_PRIORITY, "1");

        return values;
    }

    public void itemAisles(Integer selItem) {
        String key;
        String sqlExe;

        Log.w(CreateShopDatabase.class.getName(),
                "Copying database");

        database.execSQL("DROP TABLE IF EXISTS temp");
        String CREATE_DB_SHOPS = "CREATE TABLE shops (" + KEY_STORE + "," + KEY_AISLE + "," + KEY_PRICE + ")";
/*
SELECT storename, aisle, price
FROM itemsinshop
WHERE item_id=18
        */

        c_execSQL("INSERT INTO shops (" + KEY_STORE + "," + KEY_AISLE + "," + KEY_PRICE + ")" +
                " FROM " + DATABASE_SHOPITEMS +
                " WHERE " + KEY_ITEMID + "=" + selItem);

        database.execSQL("DROP TABLE IF EXISTS " + ShopDbAdapter.DATABASE_TABLE);

    }

    public boolean chkDatabase() {
        //open database, if doesn't exist, create it

        Cursor c = null;
        boolean tableExists = false;
        //get cursor on it
        try {
            c = database.query(DATABASE_TABLE, null,
                    null, null, null, null, null);
            tableExists = true;
            nHits = c.getCount();
        } catch (Exception e) {
            //fail
            Log.w("chkDatabase()", "Main table doesn't exist :(((");
        }

        try {
            c = database.query(DATABASE_CATEGORIES, null,
                    null, null, null, null, null);
            nHits = c.getCount();
            createValue(ShopDbAdapter.DATABASE_CATEGORIES, "-");
            storeInNewCatUnfold("-");
//            if nHits<0

            tableExists = true;
        } catch (Exception e) {
            //fail
            Log.w("chkDatabase()", "Categories table doesn't exist - but was generated");
//            uniqueValue(ShopDbAdapter.DATABASE_CATEGORIES, ShopDbAdapter.DATABASE_TABLE);
        }

        try {
            c = database.query(DATABASE_STORES, null,
                    null, null, null, null, null);
            nHits = c.getCount();
            if (nHits==0) {
                uniqueValue(ShopDbAdapter.DATABASE_STORES, ShopDbAdapter.DATABASE_TABLE);
                createValue(ShopDbAdapter.DATABASE_STORES, "");
            }

            tableExists = true;
        } catch (Exception e) {
            //fail
            Log.w("chkDatabase()", "Stores table doesn't exist - but was generated");
            uniqueValue(ShopDbAdapter.DATABASE_STORES, ShopDbAdapter.DATABASE_TABLE);
            createValue(ShopDbAdapter.DATABASE_STORES, "");
        }

        try {
            c = database.query(DATABASE_CAT_STORE_TABLE, null,
                    null, null, null, null, null);
            nHits = c.getCount();
            if (nHits==0) {
                catsInStoreUnfold();
                createValue(ShopDbAdapter.DATABASE_CAT_STORE_TABLE, "-");
            }

            tableExists = true;
        } catch (Exception e) {
            //fail
            Log.w("chkDatabase()", "Cat/Stores table doesn't exist - but was generated");
//            itemsInStoreUnfold
            catsInStoreUnfold();
            createValue(ShopDbAdapter.DATABASE_CAT_STORE_TABLE, "-");
//            uniqueValue(ShopDbAdapter.DATABASE_STORES, ShopDbAdapter.DATABASE_TABLE);
        }

        try {
            c = database.query(DATABASE_SHOPITEMS, null,
                    null, null, null, null, null);
            nHits = c.getCount();
            if (nHits==0) {
                itemsInStoreUnfold();
            }

            tableExists = true;
        } catch (Exception e) {
            //fail
            Log.w("chkDatabase()", "Item/Stores table doesn't exist - but was generated");
            itemsInStoreUnfold();
//            uniqueValue(ShopDbAdapter.DATABASE_STORES, ShopDbAdapter.DATABASE_TABLE);
        }



        return tableExists;
    }

    public void chkSettingsCol() {
//        Cursor mCursor = database.query("settings", null, "_id==1", null, null, null, null);
        Cursor mCursor = database.query("settings", null, null, null, null, null, null);
        ContentValues initialValues;
        String settingsName;
        long n_inst;
        boolean settingsMod;

        if (mCursor.getColumnIndex(ShopDbAdapter.KEY_SETTINGSNAME) == -1) {
            database.execSQL("ALTER TABLE " + ShopDbAdapter.DATABASE_SETTINGS
                    + " ADD COLUMN " + ShopDbAdapter.KEY_SETTINGSNAME + " TEXT DEFAULT ''");
        }

        settingsMod = chkSettingsInit(ShopDbAdapter.SETTINGS_PLAN_ALL);
        settingsMod = chkSettingsInit(ShopDbAdapter.SETTINGS_PLAN_STORE);
        settingsMod = chkSettingsInit(ShopDbAdapter.SETTINGS_SHOP_ALL);
        settingsMod = chkSettingsInit(ShopDbAdapter.SETTINGS_SHOP_STORE);

    }

    public boolean chkSettingsInit(String settingsName) {
//        Cursor mCursor = database.query("settings", null, "_id==1", null, null, null, null);
        Cursor mCursor = database.query("settings", null, null, null, null, null, null);
        ContentValues initialValues;
//        String settingsName;
        long n_inst;
        boolean settingsInit = false;

        try {
//            settingsName=ShopDbAdapter.SETTINGS_PLAN_ALL;
            settingsCursor = database.query(DATABASE_SETTINGS, null, KEY_SETTINGSNAME + "=\"" + settingsName + "\"", null, null,
                    null, null);
            if (settingsCursor.getCount() > 1) {
                n_inst = database.delete(DATABASE_SETTINGS, KEY_SETTINGSNAME + "=\"" + settingsName + "\"", null);
                initialValues = new ContentValues();
                initialValues.put(KEY_SETTINGSNAME, settingsName);
                n_inst = database.insert(DATABASE_SETTINGS, null, initialValues);
                settingsInit = true;
            }

            if (settingsCursor.getCount() == 0) {
                initialValues = new ContentValues();
                initialValues.put(KEY_SETTINGSNAME, settingsName);
                n_inst = database.insert(DATABASE_SETTINGS, null, initialValues);
                settingsInit = true;
            }
            settingsCursor.moveToFirst();
        } catch (SQLException e) {
        }
        return settingsInit;

    }

}



