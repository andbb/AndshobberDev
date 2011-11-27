package dk.andbb.andshobber.database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import java.io.*;


public class dbCopy extends SQLiteOpenHelper {
    private static String DB_CPATH = "/data/data/dk.andbb.andshobber.database/databases/";

    //The Android's default system path of your application database.
    String DB_PATH = "/data/data/dk.andbb.andshobber.database/databases/";
    private String mydoc = "/sdcard";

    String old_NAME;// = "items_hzh";
    String new_NAME = fileslist.dbCp;

    private SQLiteDatabase myDataBase;

    /**
     * Constructor
     * Takes and keeps a reference of the passed context in order to access to the application assets and resources.
     *
     * @param context
     */
    public dbCopy(Context context) {

        super(context, CreateShopDatabase.DATABASE_NAME, null, CreateShopDatabase.DATABASE_VERSION);
        Context myContext = context;
        old_NAME = CreateShopDatabase.DATABASE_NAME;
    }

    /**
     * Creates a empty database on the system and rewrites it with your own database.
     */
    public void createDataBaseCp(String dbName) throws IOException {

        boolean dbExist = checkDataBase(dbName);

        if (dbExist) {
            //do nothing - database already exist
        } else {

            //By calling this method and empty database will be created into the default system path
            //of your application so we are gonna be able to overwrite that database with our database.
// TODO: Close database???
//            10-30 08:39:56.612: ERROR/Database(352): close() was never explicitly called on database '/data/data/dk.andbb.andshobber.database/databases/Bogholmen'
//                    android.database.sqlite.DatabaseObjectNotClosedException: Application did not close the cursor or database object that was opened here

            this.getReadableDatabase();

            try {

                copyDataBase(dbName);

            } catch (IOException e) {

                throw new Error("Error copying database");

            }
        }

    }

    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     *
     * @return true if it exists, false if it doesn't
     */
    private boolean checkDataBase(String chkName) {

        SQLiteDatabase checkDB = null;

        try {
//	    		new_NAME = DB_PATH + fileslist.dbCp;
/*
            new_NAME = fileslist.dbCp;
            String myPath = DB_PATH + new_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
*/
            checkDB = SQLiteDatabase.openDatabase(chkName, null, SQLiteDatabase.OPEN_READONLY);

        } catch (SQLiteException e) {

            //database does't exist yet.

        }

        if (checkDB != null) {

            checkDB.close();

        }

        return checkDB != null;
    }

    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     */
    private void copyDataBase(String dbName) throws IOException {
        FileOutputStream fos = null;
        ObjectOutputStream out = null;

        File root = Environment.getExternalStorageDirectory();

        //Open your local db as the input stream
//	    	InputStream myInput = myContext.getAssets().open(DB_NAME);
        String inFileName = DB_PATH + old_NAME;
        InputStream myInput = new FileInputStream(inFileName);

        // Path to the just created empty db
        String outFileName = DB_PATH + "/" + new_NAME;
//        outFileName = root + "/" + dbName+".abb";
        outFileName = dbName;
//        outFileName = root + "/export";

        //Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);


        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }

        //Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();

    }

    public void openDataBase() throws SQLException {

        //Open the database
        String myPath = DB_PATH + old_NAME;
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

    }

    @Override
    public synchronized void close() {

        if (myDataBase != null)
            myDataBase.close();

        super.close();

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    // Add your public helper methods to access and get content from the database.
    // You could return cursors by doing "return myDataBase.query(....)" so it'd be easy
    // to you to create adapters for your views.

}

