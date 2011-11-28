package dk.andbb.andshobber.database;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.*;
import android.database.SQLException;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class fileslist extends ListActivity {
    public Integer mItem;
    public static final int mNewDB = 0;
    public static final int mQuit = 1;
    public static final int mCopy = 2;
    public static final int mDelete = 4;
    public static final int mExport = 5;
    public static final int mXMLImp = 6;
    public static final int mXMLRead = 7;
    public static final int mRename = 8;

    private ShopDbAdapter mDbHelper;

    private List<String> item = null;
    private List<String> itemPath = null;
    private List<String> path = null;
    private String root = "/data/data/dk.andbb.andshobber.database/databases/";
    private String sdcard = "/sdcard/";
    private String expDir = sdcard;
    public static String dbCp;
    private TextView myPath;
    public String fileName;
    public String fileNameIn;
    private Button confirmButton;
    private int fileRequest;
    private Intent i;
    private String selectFile;
    private int openCode;

    private String fileRegExp = ".*\\.abb";
    //    private String fileRegExp=".*";
    private String xmlFileIn;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        mDbHelper = new ShopDbAdapter(this);
        File sdcardPath = Environment.getExternalStorageDirectory();
        sdcard = sdcardPath.toString();
/*
        File rootPath= getApplicationContext().getFilesDir()
        root=rootPath.toString();
*/

        setContentView(R.layout.fileslist);
        confirmButton = (Button) findViewById(R.id.file_confirm_button);
        myPath = (TextView) findViewById(R.id.path);

//        root=this.getFilesDir();
//        getDir(sdcard);
        getDir(root, fileRegExp);

        registerForContextMenu(getListView());
        final Context xx = this;
        ListView list = getListView();
        confirmButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                confirmButton.setVisibility(View.GONE);

                String value = nameDialog("Export to:", "");
                if (value != null && value.length() > 0) {
                    fileName = selectFile + "/" + value + ".abb"; //TODO: Check for dir
                    cpDb(fileName);
                }

                // context.deleteDatabase(DATABASE_NAME);
                // http://stackoverflow.com/questions/4406067/how-to-delete-sqlite-database-from-android-programmatically
                return;
            }

        }


        );
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

// Get the info on which item was selected
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

// Get the Adapter behind your ListView (this assumes you're using
// a ListActivity; if you're not, you'll have to store the Adapter yourself
// in some way that can be accessed here.)
        Adapter adapter = getListAdapter();

// Retrieve the item that was clicked on
        Object selItem = adapter.getItem(info.position);
        long mRowId = info.id;
        String itemStr = (String) selItem;

        menu.setHeaderTitle((String) selItem);
        menu.add(0, mDelete, 0, R.string.menu_delete);
        menu.add(0, mRename, 0, R.string.menu_rename);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
                .getMenuInfo();
        File file;

        Adapter adapter = getListAdapter();
        Object selItem = adapter.getItem(info.position);
        long mRowId = info.id;
        String itemStr = (String) selItem;
//        fileRegExp=".*";

        switch (item.getItemId()) {
            case mDelete:
                this.deleteDatabase(itemStr);

                SharedPreferences mASHSettings = getSharedPreferences(ItemsOverview.ASH_PREFERENCES, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = mASHSettings.edit();
                editor.putString(ItemsOverview.ASH_PREFERENCES_DBNAME, "");
                editor.commit();
                getDir(root, fileRegExp);
                // http://stackoverflow.com/questions/4406067/how-to-delete-sqlite-database-from-android-programmatically
                return true;
            case mRename:
                file = new File(root + itemStr);
                File newFile = new File(root + new StringBuilder().append(itemStr).append(".abb").toString());
                file.renameTo(newFile);

                getDir(root, fileRegExp);
                // http://stackoverflow.com/questions/4406067/how-to-delete-sqlite-database-from-android-programmatically
                return true;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu filelistMenu) {
        filelistMenu.add(0, mNewDB, 1, "New Database").setIcon(android.R.drawable.ic_menu_add);
        filelistMenu.add(0, mXMLImp, 1, "HS2 XML import").setIcon(R.drawable.hs2ppt8);
        filelistMenu.add(0, mCopy, 1, "Copy Database").setIcon(android.R.drawable.ic_menu_set_as);
//        filelistMenu.add(0, mDelete, 1, "Delete Database").setIcon(android.R.drawable.ic_menu_delete);
        filelistMenu.add(0, mQuit, 1, "Go back").setIcon(android.R.drawable.ic_lock_power_off);
        filelistMenu.add(0, mExport, 1, "Export Database").setIcon(android.R.drawable.ic_menu_set_as);
        return super.onCreateOptionsMenu(filelistMenu);
    }

    // Reaction to the menu selection
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
//        Intent i;
        mItem = item.getItemId();
        openCode = mItem;
//        fileRegExp=".*";

        fileRequest = item.getItemId();
        switch (item.getItemId()) {
            case mNewDB:
                i = new Intent(this, getText.class);
                startActivityForResult(i, mNewDB);
//			getDBname();
                return true;
            case mCopy:
                fileName = CreateShopDatabase.DATABASE_NAME;
                confirmButton.performClick();

//                i = new Intent(this, getText.class);
//                startActivityForResult(i, mCopy);

//			getDBname();
            case mXMLImp:
                confirmButton.setVisibility(View.VISIBLE);
                fileRequest = item.getItemId();
//                fileRegExp=".*";

                fileRegExp = "(?i)hs2_.*\\.xml";
//                fileRegExp="hs2.*\\.xml";
                getDir(sdcard, "(?i)hs2_.*\\.xml");
//                getDir(sdcard, "hs2.*\\.xml");
//                getDir(sdcard, ".*");

//                i = new Intent(this, getText.class);
//                startActivityForResult(i, mXMLImp);

                return true;
            case mExport:
                fileRegExp = ".*";
                getDir(sdcard, "/");
                confirmButton.setVisibility(View.VISIBLE);
/*
                fileRequest = item.getItemId();
                fileName=nameDialog("Export to:","")
                i = new Intent(this, getText.class);
*/
//                startActivityForResult(i, mExport);

                // context.deleteDatabase(DATABASE_NAME);
                // http://stackoverflow.com/questions/4406067/how-to-delete-sqlite-database-from-android-programmatically
                return true;

            case mQuit:
                return true;

        }
        return super.onMenuItemSelected(featureId, item);
    }

    /**
     * getdir -
     *
     * @param dirPath
     * @param fileNamePattern
     */
    private void getDir(String dirPath, String fileNamePattern) {
        myPath.setText("Location: " + dirPath);

        item = new ArrayList<String>();
        itemPath = new ArrayList<String>();
        path = new ArrayList<String>();

        File f = new File(dirPath);
        File file = new File(dirPath);
        File[] files = f.listFiles();

        if (!dirPath.equals(root)) {

            item.add(root);
            itemPath.add(root);
            path.add(root);

            item.add("../");
            itemPath.add("../");
            String xx = f.getParent();
            if (xx != null) {
                path.add(xx);
            } else {
                path.add(f.getName());
            }

        }

        if (files==null) {

            return;
        }

        for (File oneFile : files) {
            if (oneFile.canRead()) {
                path.add(oneFile.getPath());
            }
        }

//        Collections.sort(item);
//        Collections.sort(itemPath);
        Collections.sort(path);

        for (String filePath : path) {
//            file=(file.getPath());
            file = new File(filePath);
//            file=(filePath.);
            if (file.isDirectory()) {
                item.add(file.getName() + "/");
                itemPath.add(file.getPath() + "/");
            } else {

                if (fileNamePattern.equalsIgnoreCase("/")) {

                } else {
                    {
                        boolean match = file.getName().matches(fileRegExp);
                        if (match) {
                            String fgn = file.getName();
                            int abb = fgn.lastIndexOf(".abb");
                            if (abb > 10000) {
                                String fgn1 = fgn.substring(0, fgn.lastIndexOf(".abb"));
                                fgn = fgn.substring(0, fgn.lastIndexOf(".abb"));
                            }
                            item.add(fgn);
                            itemPath.add(file.getPath());
                        }

                    }
                }
            }
        }

        ArrayAdapter<String> fileList =
                new ArrayAdapter<String>(this, R.layout.filesrow, item);
        setListAdapter(fileList);
    }


    @Override
    protected void onListItemClick
            (ListView
                     l, View
                    v, int position,
             long id) {

        File file;
        String fileName = item.get(position);

        file = new File(itemPath.get(position));

        if (file.isDirectory()) {
            if (file.canRead()) {
                selectFile = file.toString();
                getDir(itemPath.get(position), "");
            } else {
                Toast toast = Toast.makeText(this, "Cannot read directory", 2000);
                toast.show();
            }
        } else {
            if (openCode == mXMLImp) {
                try {
                    String value = file.getName();

                    if (value != null && value.length() > 0) {
                        i = new Intent(this, getText.class);
                        String shortName;
                        int len = value.length();
                        shortName = value.substring(4, len - 8);
                        i.putExtra("value", shortName);
                        startActivityForResult(i, mXMLImp);

//                    String fileOut = nameDialog("Import to:", value);
//                    CreateShopDatabase.DATABASE_NAME = fileOut;
//                    CreateShopDatabase.DATABASE_NAME = "new.abb";
                        //do something with value
/* Do not import until filename is found
                    mDbHelper = new ShopDbAdapter(this);

                    Intent i = new Intent(this, xmlActivity.class);
                    i.putExtra("file", value);

                    startActivity(i);
*/

                    }
                } catch (Exception ignored) {
                }
            }

            Toast toast = Toast.makeText(this, "[" + file.getName() + "]", 2000);
            toast.show();

//			ShopDbAdapter.close();

            selectFile = file.getName();
            xmlFileIn = file.getPath();

            if (openCode != mXMLImp) {
                CreateShopDatabase.DATABASE_NAME = file.getName();
                setResult(0);
                finish();
            }
        }
    }

    public void cpDb(String value) {
        dbCopy myDbCpHelperHelper = new dbCopy(this);

        try {

//            myDbCpHelperHelper.createDataBaseCp(value);
            myDbCpHelperHelper.createDataBaseCp(value);

        } catch (IOException ioe) {

            throw new Error("Unable to create database");

        }

        try {

            myDbCpHelperHelper.openDataBase();

        } catch (SQLException sqle) {

            throw sqle;

        }

//	return true;
    }

    private String nameDialog(String dialogTitle, String nameTxt) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final EditText nameInput = new EditText(this);
        final Long id2;
//        String nameTxt;

//        nameInput.setInputType(0x00080000 | 0x00000002);
        nameInput.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

        if (nameTxt == null) {
            nameTxt = "";
            nameInput.setHint("Enter filename");
        }

        nameInput.setText(nameTxt);
        alert.setTitle(dialogTitle);
        alert.setView(nameInput);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = nameInput.getText().toString().trim();
                Toast.makeText(getApplicationContext(), value, Toast.LENGTH_SHORT).show();
                try {
                    int x = mItem;
                    boolean xb = (value != null);
                    xb = !(value.equals(""));
                    if ((value != null) && !(value.equals(""))) {
                        fileNameIn = value;
                        //getDir(sdcard);
                        switch (mItem) {
                            case mCopy:
                                fileName = root + value; //TODO: Check for dir
                                cpDb(fileName);
                                finish();
                                break;
                            case mDelete:
                                deleteDatabase(CreateShopDatabase.DATABASE_NAME);

                                finish();
                                break;

                            case mExport:
                                fileName = selectFile + "/" + value + ".abb"; //TODO: Check for dir
                                cpDb(fileName);
                                finish();
                                break;
                        }
                        //do something with value
                    }
                } catch (Exception ignored) {
                }
            }
        });

        alert.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                });
        alert.show();
        return nameTxt;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case mCopy:
                try {
                    String value = data.getStringExtra("value");
                    if (value != null && value.length() > 0) {
                        fileName = value + ".abb";
                        File f = new File(fileName);
                        if (!f.exists()) {
                            cpDb(value);
                            return;
                        } else {
                            alertAlready("Copy", value);
                        }
                    }
                    //do something with value
                } catch (Exception ignored) {
                }
//            break;
            case mNewDB:
                try {
                    String value = data.getStringExtra("value") + ".abb";
                    if (value != null && value.length() > 0) {
                        //do something with value
                        File f = new File(root + value);
                        if (!f.exists()) {
                            CreateShopDatabase.DATABASE_NAME = value;

                            executeDone("new");
                            return;
                        } else {
                            alertAlready("New file", value);
                        }
                    }
                } catch (Exception e) {
                }
            break;
            case mExport:
                try {
//                    getDir(sdcard);
                    String value = data.getStringExtra("value");
                    if (value != null && value.length() > 0) {
                        //getDir(sdcard);
                        fileName = selectFile + "/" + value + ".abb"; //TODO: Check for dir
                        File f = new File(fileName);
                        if (!f.exists()) {
                            cpDb(fileName);
                        } else {
                            alertAlready("Export", fileName);
                        }
                        //do something with value
                    }
                } catch (Exception e) {
                }
                return;
//            break;
            case mXMLImp:
                try {
                    String fileOut = data.getStringExtra("value") + ".abb";
                    File f = new File(CreateShopDatabase.DATABASE_NAME);
                    if (!f.exists()) {
                        if (fileOut != null && fileOut.length() > 0) {
                            CreateShopDatabase.DATABASE_NAME = fileOut;
                            //do something with value
                            mDbHelper = new ShopDbAdapter(this);


                            Intent i = new Intent(this, xmlActivity.class);
                            i.putExtra("file", xmlFileIn);

                            startActivityForResult(i, mXMLRead);
//                        startActivity(i);

                        }
                    } else {
                        alertAlready("XML import", fileOut);
                    }
                } catch (Exception e) {
                }
                return;
            case mXMLRead:
                try {
//                    startActivityForResult(i, mXMLImp);
                    ShopDbAdapter.DISP_TABLE = ShopDbAdapter.DATABASE_TABLE;
                    ShopDbAdapter.FILTER_CATEGORY = "";
                    ShopDbAdapter.FILTER_STORE = "";
                    setResult(0);
                    finish();
                } catch (Exception e) {
                }
                finish();
                return;
//            break;
            default:
                break;
        }

    }

    public void onDestroy() {
        super.onDestroy();

        // Replace mDbHelper as needed with your database connection, or
        // whatever wraps your database connection. (See below.)
//        mDbHelper.close();
    }

    public void alertAlready(String action, String filename) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final EditText aisle = new EditText(this);
        final NumberPicker npPicker = new NumberPicker(this);
        final ContentValues values = new ContentValues();

        alert.setTitle(action + " error! " + filename + " already exits.");


        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });

        alert.create();
        alert.show();
    }
    private void executeDone(String message) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("value", message);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }
}
