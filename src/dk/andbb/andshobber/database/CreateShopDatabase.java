package dk.andbb.andshobber.database;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class CreateShopDatabase extends SQLiteOpenHelper {
    public static String DATABASE_NAME = "item";
    public static String OLD_TABLE = "todo";
    public SQLiteDatabase sqBase;
    public static final int DATABASE_VERSION = 10;

    // Database creation sql statement

    private static final String CREATE_STORE_TABLE = "create table stores (_id integer primary key autoincrement, " +
            "storename text)";

    public static final String CREATE_SETTINGS = "create table settings (_id integer primary key autoincrement, " +
            "view text default '', " +
            "settings_name TEXT DEFAULT ''," +
            "filter_def text default '', " +
            "filter_category text default '', " +
            "filter_store text default '', " +
            "sort_1 text default \"category\", " +
            "sort_2 text default \"item\", " +
            "sort_3 text default \"date\", " +
            "sort_4 text default \"priority\", " +
            "sort_1_dir text default ASC, " +
            "sort_2_dir text default ASC, " +
            "sort_3_dir text default ASC, " +
            "sort_4_dir text default ASC" +
            ")";

    private static final String CREATE_CAT_TABLE = "create table categories (_id integer primary key autoincrement, " +
            "category text)";

    private static final String CREATE_CONSTANTS = "CREATE TABLE constants (_id integer primary key autoincrement, " +
            "tax real, tax2 real)";

    private static final String CREATE_ITEMINSTORE_TABLE = "create table itemsinshop (_id integer primary key autoincrement, " +
            "item_id integer, " +
            "aisle text," +
            "price real," +
            "category text," +
            "storename text)";
//            "store_id integer)";

    private static final String CREATE_CAT_STORE_TABLE = "create table catsinshop (_id integer primary key autoincrement, " +
            "storename text, " +
//            "store_id integer, " +
//            "store_aisle text," +
            "aisle text," +
            "category text)";

    public static final String DB_INT = " integer default 0, ";
    public static final String DB_TXT = " text default '', ";

    public static final String DATABASE_FIELDS = "(_id integer primary key autoincrement, " +
            "need text default '', " +
            "priority text default '', " +
            "item text default '', " +
            "note text default '', " +
            "category text default '', " +
            "quantity integer default 0, " +
            "units string default '', " +
            "price text default '', " +
            "aisle text default ''," +
            "date int default '', " +
            "entryorder integer default 0, " +
            "coupon integer default 0, " +
            "tax integer default 0, " +
            "tax2 integer default 0, " +
            "autoDelete integer default 0, " +
            "private integer default 0, " +
            "alarm integer default 0, " +
            "alarmmidi integer default 0, " +
            "icon integer default 0," +
            "created int default '', " +
            "modified int default ''" +
            ");";

    public static final String CREATE_DATABASE = "create table " + ShopDbAdapter.DATABASE_TABLE + " " + DATABASE_FIELDS;
    private Context ctx;

    public CreateShopDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        ctx = context;
    }

    // Method is called during creation of the database
    @Override
    public void onCreate(SQLiteDatabase database) {
        sqBase = database;
        dbCreate();

/*
        final AlertDialog.Builder alert;
        alert = new AlertDialog.Builder(ctx);
        alert.setTitle("Creating new file " + DATABASE_NAME);

        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
//                    dbHelper.updateItem(rowId, values);
                dbCreate();
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
*/

    }

    public void dbCreate() {
        sqBase.execSQL(CREATE_DATABASE);
        sqBase.execSQL(CREATE_STORE_TABLE);
        sqBase.execSQL(CREATE_ITEMINSTORE_TABLE);
        sqBase.execSQL(CREATE_CAT_STORE_TABLE);
        sqBase.execSQL(CREATE_CAT_TABLE);
        sqBase.execSQL(CREATE_SETTINGS);
        sqBase.execSQL(CREATE_CONSTANTS);
    }
    // Method is called during an upgrade of the database, e.g. if you increase
    // the database version
    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion,
                          int newVersion) {

        Log.w(CreateShopDatabase.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");

        if ((oldVersion == 2) & (newVersion == 2)) {
            database.execSQL(CREATE_STORE_TABLE);
            database.execSQL(CREATE_ITEMINSTORE_TABLE);
            database.execSQL(CREATE_CAT_STORE_TABLE);
        }

        if ((oldVersion < 3) & (newVersion == 3)) {
            database.execSQL(CREATE_CAT_TABLE);
        }

        if ((oldVersion < 3) & (newVersion == 3)) {
            database.execSQL(CREATE_SETTINGS);
        }

        if ((oldVersion < 6) & (newVersion == 6)) {
            database.execSQL("DROP TABLE IF EXISTS settings");
            database.execSQL(CREATE_SETTINGS);
        }
        if ((oldVersion < 7) & (newVersion == 7)) {
            ren2note(database);
        }
        if ((oldVersion < 9) & (newVersion == 9)) {
            Cursor mCursor = database.query("settings", null, "_id==1", null, null, null, null);
            if (mCursor.getColumnIndex(ShopDbAdapter.KEY_SETTINGSNAME) == -1) {
                database.execSQL("ALTER TABLE " + ShopDbAdapter.DATABASE_SETTINGS
                        + " ADD COLUMN " + ShopDbAdapter.KEY_SETTINGSNAME + " TEXT DEFAULT ''");
            }
        }
        if ((oldVersion < 10) & (newVersion == 10)) {
            database.execSQL(CREATE_CONSTANTS);

// TODO: Drop column Price in ShopDbAdapter.DATABASE_CAT_STORE_TABLE
        }

        if ((oldVersion < 99) & (newVersion == 99)) {
//            copyDb(database);
        }
        Cursor mCursor = database.query("settings", null, "_id==1", null, null, null, null);
    }

    public void ren2note(SQLiteDatabase database) {
        String REUSE_FIELDS = "need , " +
                "priority , " +
                "item, " +
                "category , " +
                "quantity , " +
                "units , " +
                "price , " +
                "aisle ," +
                "entryorder , " +
                "coupon , " +
                "tax , " +
                "tax2 , " +
                "autoDelete , " +
                "private , " +
                "alarm , " +
                "alarmmidi , " +
                "icon ," +
                "created , " +
                "modified";


        String NEW_DATABASE_FIELDS = "_id integer primary key autoincrement, " +
                "need text default '', " +
                "priority text default '', " +
                "item text default '', " +
                "note text default '', " +
                "category text default '', " +
                "quantity integer default 0, " +
                "units string default '', " +
                "price text default '', " +
                "aisle text default ''," +
                "date integer default 0, " +
                "entryorder integer default 0, " +
                "coupon integer default 0, " +
                "tax text default '', " +
                "tax2 text default '', " +
                "autoDelete integer default 0, " +
                "private integer default 0, " +
                "alarm integer default 0, " +
                "alarmmidi integer default 0, " +
                "icon integer default 0," +
                "created integer , " +
                "modified integer";

        Log.w(CreateShopDatabase.class.getName(),
                "Copying database");
        database.execSQL("DROP TABLE IF EXISTS temp");
        database.execSQL("DROP TABLE IF EXISTS " + ShopDbAdapter.DATABASE_TABLE);

        String CREATE_DB_COPY = "create table TEMP (" + NEW_DATABASE_FIELDS + ")";

        String CREATE_FROM_OLD = "INSERT INTO temp (" + REUSE_FIELDS + ") " +
                "SELECT " +
                REUSE_FIELDS +
                " FROM " + OLD_TABLE;
//        Additional (new) fields are filled in automatically

        try {
            database.execSQL(CREATE_DB_COPY);
        } catch (SQLException e) {
            Log.v("CreateShopDatabase SQL", "sqlString");
        }

        try {
            database.execSQL(CREATE_FROM_OLD);
        } catch (SQLException e) {
            Log.v("CreateShopDatabase SQL", "sqlString");
        }

/*
        try {
            database.execSQL("ALTER TABLE " +
                    ShopDbAdapter.DATABASE_TABLE + " RENAME TO tableold");
        } catch (SQLException e) {
            Log.v("CreateShopDatabase SQL", "sqlString");
        }
*/

        try {
            database.execSQL("ALTER TABLE temp" +
                    " RENAME TO " + ShopDbAdapter.DATABASE_TABLE);
        } catch (SQLException e) {
            Log.v("CreateShopDatabase SQL", "sqlString");
        }
        database.execSQL("DROP TABLE IF EXISTS " + OLD_TABLE);

//        ContentValues values = new ContentValues();
//        values.put("time_format", "TIMESTAMP");
//        database.update("settings", values, "_id" + "=" + 1, null);
    }
}
