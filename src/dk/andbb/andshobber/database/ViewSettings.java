/**
 *
 */
package dk.andbb.andshobber.database;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;

/**
 * @author Anders og Charlotte
 */
public class ViewSettings extends Activity {
    public static final int mInsert = 0;
    public static final int mQuit = 1;
    public static final int mSort = 2;
    public static final int mCheckout = 3;
    public static final int mDelete = 4;
    public static final int mDatabase = 5;
    public static final int mDbEdit = 6;
    public static final int mCatEdit = 7;
    public static final int mStoreEdit = 8;

    private static final int ACTIVITY_CREATE = 0;
    private static final int ACTIVITY_EDIT = 1;
    private static final int EDIT_ACTION = 2;
    private static final int GET_FILE = 5;
    private static final int DB_EDIT = 6;

    private ShopDbAdapter mDbHelper;
    private Spinner mViewKey;
    private Spinner mSort1;
    private Spinner mSort2;
    private Spinner mSort3;
    private Spinner mSort4;
    private ImageButton sort_button1;
    private ImageButton sort_button2;
    private ImageButton sort_button3;
    private ImageButton sort_button4;

    //    private EditText mNewStore;
//    private EditText mNewCat;
    private String settingsChosen;
    //this counts how many Gallery's are on the UI
    private int mGalleryCount = 0;

    //this counts how many Gallery's have been initialized
    private int mGalleryInitializedCount = 0;
    private Spinner mSettingsKey;
    private String startSettings;
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

        setContentView(R.layout.viewsettings);

        Button cancelButton = (Button) findViewById(R.id.settingsCancel);
        sort_button1 = (ImageButton) findViewById(R.id.sort_button1);
        sort_button2 = (ImageButton) findViewById(R.id.sort_button2);
        sort_button3 = (ImageButton) findViewById(R.id.sort_button3);
        sort_button4 = (ImageButton) findViewById(R.id.sort_button4);

        sort_button1.setImageResource(sortIcon(ShopDbAdapter.SORT_1_DIR));
        sort_button2.setImageResource(sortIcon(ShopDbAdapter.SORT_2_DIR));
        sort_button3.setImageResource(sortIcon(ShopDbAdapter.SORT_3_DIR));
        sort_button4.setImageResource(sortIcon(ShopDbAdapter.SORT_4_DIR));

//        mSortKey = (Spinner) findViewById(R.id.sortSpinner);
        mSettingsKey = (Spinner) findViewById(R.id.settingsSpinner);
        mViewKey = (Spinner) findViewById(R.id.viewSpinner);
        mSort1 = (Spinner) findViewById(R.id.sort1);
        mSort2 = (Spinner) findViewById(R.id.sort2);
        mSort3 = (Spinner) findViewById(R.id.sort3);
        mSort4 = (Spinner) findViewById(R.id.sort4);

        settingsChosen = mDbHelper.currentView();
        startSettings = settingsChosen;
        mDbHelper.getSettings(settingsChosen);
        populateFields();
        mGalleryCount = 6;

        mSettingsKey.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {

            //			@Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (mGalleryInitializedCount < mGalleryCount) {
                    mGalleryInitializedCount++;
                } else {
                    settingsChosen = (String) mSettingsKey.getSelectedItem();
                }
            }

            //@Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        mSort1.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {

            //			@Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (mGalleryInitializedCount < mGalleryCount) {
                    mGalleryInitializedCount++;
                } else {
                    ShopDbAdapter.SORT_1 = (String) mSort1.getSelectedItem();
                    mDbHelper.setSetting("sort_1", ShopDbAdapter.SORT_1, settingsChosen);
                }
            }

            //@Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        mSort2.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {

            //			@Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (mGalleryInitializedCount < mGalleryCount) {
                    mGalleryInitializedCount++;
                } else {
                    ShopDbAdapter.SORT_2 = (String) mSort2.getSelectedItem();
                    mDbHelper.setSetting("sort_2", ShopDbAdapter.SORT_2, settingsChosen);
                }
            }

            //@Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        mSort3.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {

            //			@Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (mGalleryInitializedCount < mGalleryCount) {
                    mGalleryInitializedCount++;
                } else {
                    ShopDbAdapter.SORT_3 = (String) mSort3.getSelectedItem();
                    mDbHelper.setSetting("sort_3", ShopDbAdapter.SORT_3, settingsChosen);
                }
            }

            //@Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });


        mSort4.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {

            //			@Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (mGalleryInitializedCount < mGalleryCount) {
                    mGalleryInitializedCount++;
                } else {
                    ShopDbAdapter.SORT_4 = (String) mSort4.getSelectedItem();
                    mDbHelper.setSetting("sort_4", ShopDbAdapter.SORT_4, settingsChosen);
                }
            }

            //@Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        sort_button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (ShopDbAdapter.SORT_1_DIR.equals("ASC")) {
                    ShopDbAdapter.SORT_1_DIR = "DESC";
                } else {
                    ShopDbAdapter.SORT_1_DIR = "ASC";
                }
                sort_button1.setImageResource(sortIcon(ShopDbAdapter.SORT_1_DIR));
                mDbHelper.setSetting("sort_1_dir", ShopDbAdapter.SORT_1_DIR, settingsChosen);

            }

        });

        sort_button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (ShopDbAdapter.SORT_2_DIR.equals("ASC")) {
                    ShopDbAdapter.SORT_2_DIR = "DESC";
                } else {
                    ShopDbAdapter.SORT_2_DIR = "ASC";
                }
                sort_button2.setImageResource(sortIcon(ShopDbAdapter.SORT_2_DIR));
                mDbHelper.setSetting("sort_2_dir", ShopDbAdapter.SORT_2_DIR, settingsChosen);
            }

        });

        sort_button3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (ShopDbAdapter.SORT_3_DIR.equals("ASC")) {
                    ShopDbAdapter.SORT_3_DIR = "DESC";
                } else {
                    ShopDbAdapter.SORT_3_DIR = "ASC";
                }
                sort_button3.setImageResource(sortIcon(ShopDbAdapter.SORT_3_DIR));
                mDbHelper.setSetting("sort_3_dir", ShopDbAdapter.SORT_3_DIR, settingsChosen);
            }

        });

        sort_button4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (ShopDbAdapter.SORT_4_DIR.equals("ASC")) {
                    ShopDbAdapter.SORT_4_DIR = "DESC";
                } else {
                    ShopDbAdapter.SORT_4_DIR = "ASC";
                }
                sort_button4.setImageResource(sortIcon(ShopDbAdapter.SORT_4_DIR));
                mDbHelper.setSetting("sort_4_dir", ShopDbAdapter.SORT_4_DIR, settingsChosen);
            }

        });

        mViewKey.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            //			@Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (mGalleryInitializedCount < mGalleryCount) {
                    mGalleryInitializedCount++;
                } else {
                    ShopDbAdapter.VIEW_KEY = (Integer) mViewKey.getSelectedItemPosition();
                    mDbHelper.setSetting("view", ShopDbAdapter.VIEW_KEY.toString(), settingsChosen);
                }
            }

            //@Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
//                saveState();
                setResult(RESULT_OK);
                finish();
            }

        });
    }

    private int sortIcon(String sort_dir) {
        int icon;
        if (sort_dir.equals("ASC")) {
            icon=android.R.drawable.arrow_up_float;
//            icon=R.drawable.ascend;
        } else {
            icon=android.R.drawable.arrow_down_float;
//            icon=R.drawable.descend;
        }
        return icon;  //To change body of created methods use File | Settings | File Templates.
    }


    private void populateFields() {
        Integer viewKey = ShopDbAdapter.VIEW_KEY;
        mViewKey.setSelection(viewKey);

//        mSettingsKey.setSelection(settingsChosen);

        for (int i = 0; i < mSettingsKey.getCount(); i++) {

            String s = (String) mSettingsKey.getItemAtPosition(i);
            Log.e(null, s + " Settings");
            if (s.equalsIgnoreCase(settingsChosen)) {
                mSettingsKey.setSelection(i);
            }
        }
        populateSort(mSort1, ShopDbAdapter.SORT_1);
        populateSort(mSort2, ShopDbAdapter.SORT_2);
        populateSort(mSort3, ShopDbAdapter.SORT_3);
        populateSort(mSort4, ShopDbAdapter.SORT_4);
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


/*
    private void saveState() {
        mNewStore = (EditText) findViewById(R.id.newcat);
        mNewCat = (EditText) findViewById(R.id.newstore);

        String newStore = mNewStore.getText().toString();
        if (!newStore.equals("")) {
            mDbHelper.createValue(ShopDbAdapter.DATABASE_STORES, newStore);
        }

        String newCat = mNewCat.getText().toString();
        if (!newCat.equals("")) {
            mDbHelper.createValue(ShopDbAdapter.DATABASE_CATEGORIES, newCat);
        }

    }
*/

/*
    private void cleanCat() {
*/
/*    new AlertDialog.Builder( this )
    .setTitle( "Confirm cleanup!" )
    .setMessage( "Cleanup will remove duplicate stores and categories, and may delete aisle assignments." )
    .setPositiveButton( "Confirm", new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {*//*

        mNewStore = (EditText) findViewById(R.id.newcat);
        mNewCat = (EditText) findViewById(R.id.newstore);

        mDbHelper.uniqueValue(ShopDbAdapter.DATABASE_CATEGORIES, ShopDbAdapter.DATABASE_TABLE);
        mDbHelper.uniqueValue(ShopDbAdapter.DATABASE_STORES, ShopDbAdapter.DATABASE_TABLE);
        mDbHelper.catsInStoreUnfold();
*/
/*        	Log.d("AlertDialog", "Positive");
        }
    })
    .setNegativeButton( "Cancel", new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            Log.d("AlertDialog","Negative");
        }
    })
    .show();
  *//*

    }
*/

/*
    private void cleanData() {
        */
/*    new AlertDialog.Builder( this )
          .setTitle( "Confirm cleanup!" )
          .setMessage( "Cleanup will remove duplicate stores and categories, and may delete aisle assignments." )
          .setPositiveButton( "Confirm", new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int which) {*//*

        mDbHelper.itemsInStoreUnfold();
        */
/*        	Log.d("AlertDialog", "Positive");
              }
          })
          .setNegativeButton( "Cancel", new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int which) {
                  Log.d("AlertDialog","Negative");
              }
          })
          .show();
        *//*

    }
*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
/*
            case mStoreEdit: //DB_EDIT
                Toast.makeText(this,
                        "Database Stores edited", Toast.LENGTH_LONG).show();

                break;
            case mCatEdit: //DB_EDIT
                Toast.makeText(this,
                        "Database Categories edited", Toast.LENGTH_LONG).show();
                break;
*/
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