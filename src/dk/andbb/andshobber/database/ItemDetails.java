package dk.andbb.andshobber.database;

//import java.text.DateFormat;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;

import java.util.ArrayList;
import java.util.Calendar;

public class ItemDetails extends Activity {
    private EditText mTitleText;
    private EditText mBodyText;
    private Long mRowId;
    private ShopDbAdapter dbHelper;
    private Spinner mCategory;
    //    private Spinner mNeed;
    private EditText mQuantity;
    private TextView mDate;
    private Button cancelButton;

    private TextView mDateDisplay;
    private Button mPickDate;
    private int mYear;
    private int mMonth;
    private int mDay;
    public static Integer requestCode;
    public static Cursor shopDB;
    private long dateMSec;
    public dateConverter.DateHolder date = new dateConverter.DateHolder();

    static final int DATE_DIALOG_ID = 0;
    private Button storeButton;
    final static int mAisleEdit = 0;

    /*   static class DateHolder {
        public int Day;
        public int Month;
        public int Year;
    }*/

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        final Context xx = this;
        dbHelper = new ShopDbAdapter(this);
        dbHelper.open();

        setContentView(R.layout.itemedit);
        mCategory = (Spinner) findViewById(R.id.category);
        mTitleText = (EditText) findViewById(R.id.item_edit);
        mBodyText = (EditText) findViewById(R.id.note_edit);
        mQuantity = (EditText) findViewById(R.id.item_quantity);
        mDate = (TextView) findViewById(R.id.dateDisplay);
//        mNeed = (Spinner) findViewById(R.id.item_need);
        cancelButton = (Button) findViewById(R.id.cancel_edit_button);
        storeButton = (Button) findViewById(R.id.item_store);

        mPickDate = (Button) findViewById(R.id.pickDate);
        mRowId = null;

        Bundle extras = getIntent().getExtras();

        mRowId = (bundle == null) ? null : (Long) bundle
                .getSerializable(ShopDbAdapter.KEY_ROWID);

        if (extras != null) {
            mRowId = extras.getLong(ShopDbAdapter.KEY_ROWID);
        }

        if (extras != null) {
            requestCode = extras.getInt("requestCode");
        }

        if ((mRowId != null) & (mRowId != 0)) {
            shopDB = dbHelper.fetchItem(mRowId);
            startManagingCursor(shopDB);
        } else {
            shopDB = null;
        }

        if (requestCode != ItemsOverview.ITEM_CREATE) {
            populateFields();
            InputMethodManager imm = (InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE);
//            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mTitleText.getWindowToken(),0);
        } else {
            mTitleText.requestFocus();
            mTitleText.performClick();
            spinFill(ShopDbAdapter.DATABASE_CATEGORIES, "-", mCategory);
        }

        if (requestCode == ItemsOverview.ITEM_COPY) {
            mRowId = (long) 0;
            shopDB = null;
        }

//        spinFill(ShopDbAdapter.DATABASE_CATEGORIES, ShopDbAdapter.FILTER_CATEGORY, mCategory);

//        mDateDisplay = (TextView) findViewById(R.id.dateDisplay);
//
        // add a click listener to the button
        mPickDate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dateClickHandler(mRowId, 0);
//                showDialog(DATE_DIALOG_ID);
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
//                saveState();
                setResult(RESULT_OK);
                finish();
            }
        });

        storeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
//				cleanCat();
                saveState();
                Intent i = new Intent(xx, ListValue.class);
                String dbName = ShopDbAdapter.DATABASE_SHOPITEMS;
//				String dbName = ShopDbAdapter.DATABASE_STORES;
                i.putExtra("spinName", "");
                i.putExtra("dbName", dbName);
                i.putExtra("itemID", mRowId);
//                String item = mTitleText.getText().toString();
                i.putExtra("itemStr", mTitleText.getText().toString());

                startActivityForResult(i, mAisleEdit);

//                finish();
            }
        });


    }

    // the callback received when the user "sets" the date in the dialog
    private DatePickerDialog.OnDateSetListener mDateSetListener =
            new DatePickerDialog.OnDateSetListener() {

                public void onDateSet(DatePicker view, int year,
                                      int monthOfYear, int dayOfMonth) {
                    mYear = year;
                    mMonth = monthOfYear;
                    mDay = dayOfMonth;
                    updateDateDisplay(mYear, mMonth, mDay);
                    if (requestCode == ItemsOverview.ITEM_DATE) {
                        saveState();
                        setResult(RESULT_OK);
                        finish();
                    }
                }
            };

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
                dateMSec = shopDB.getLong(shopDB
                        .getColumnIndexOrThrow(ShopDbAdapter.KEY_DATE));
//                dateMSec = cal.getTimeInMillis();
                Calendar cal = dateConverter.msec2Cal(dateMSec);
                date = dateConverter.cal2Date(cal);
//                String cStr = cal2Str(cal);
//                String cStr = cal2Str(cal);

                return new DatePickerDialog(this,
                        mDateSetListener,
                        date.Year, date.Month, date.Day);
        }
        return null;
    }

    public void onClick(View view) {
        if (view.isFocusable()) {
            view.setBackgroundColor(Color.YELLOW);
        }
        if (view == mDate) {
//		DatePickerDialog

        }
    }

    /*@Override
    public void onResume(){
        if ((mRowId != null) & (mRowId != 0)) {
            shopDB = dbHelper.fetchItem(mRowId);
            startManagingCursor(shopDB);
        } else {
            shopDB = null;
        }

    }
*/
    /**
     * updates the date in the TextView
     * to current date
     */
    //
    private void updateDateDisplay(int dYear, int dMonth, int dDay) {
        dateMSec = shopDB.getLong(shopDB
                .getColumnIndexOrThrow(ShopDbAdapter.KEY_DATE));
//            } catch ()

        String dateStr = dateConverter.msec2Str(dateMSec);
//        String dateStr = msec2Str(dateMSec);
        mDate.setText(dateStr);

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, dYear);
        cal.set(Calendar.MONTH, dMonth);
        cal.set(Calendar.DAY_OF_MONTH, dDay);
//        getDisplayName(MONTH, SHORT, Locale.DK);
        String cStr2 = cal.toString();
        dateMSec = cal.getTimeInMillis();
        String cStr = dateConverter.cal2Str(cal);
        mDate.setText(dateConverter.cal2Str(cal));
//        dbHelper.dbShowDateStr(shopDB,ShopDbAdapter.KEY_DATE,(TextView) findViewById(R.id.itemDate));
        Calendar cal2 = Calendar.getInstance();
        cal2.setTimeInMillis(dateMSec);
    }

    public boolean onKeyDown(int keyCode, KeyEvent kEvent) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            saveState();
            setResult(RESULT_OK);
            finish();
        }
        return true;
    }

    //
    private void populateFields() {
        String catstr;
        String dateStr;
        String category = shopDB.getString(shopDB
                .getColumnIndexOrThrow(ShopDbAdapter.KEY_CATEGORY));

//        dbHelper.spinFill(this, ShopDbAdapter.DATABASE_CATEGORIES, ShopDbAdapter.FILTER_CATEGORY, mCategory);
        spinFill(ShopDbAdapter.DATABASE_CATEGORIES, category, mCategory);
/*        do {
            for (int i = 0; i < mCategory.getCount(); i++) {

                String s = (String) mCategory.getItemAtPosition(i);
//            Log.e(null, s + " " + category);
                if (s.equalsIgnoreCase(category)) {
                    mCategory.setSelection(i);
                }
            }
            catstr = (String) mCategory.getSelectedItem();
*//*
            if (catstr.equals("") & !catstr.equals(category)) {
                dbHelper.uniqueValue(ShopDbAdapter.DATABASE_CATEGORIES, ShopDbAdapter.DATABASE_TABLE);
                spinFill(ShopDbAdapter.DATABASE_CATEGORIES, category, mCategory);
//                dbHelper.spinFill(this, ShopDbAdapter.DATABASE_CATEGORIES, ShopDbAdapter.FILTER_CATEGORY, mCategory);
            }
*//*
        } while (catstr.equals("") & !catstr.equals(category));*/
        ImageView needView = (ImageView) findViewById(R.id.need_icon);

//        String s = cursor.getString(cursor.getColumnIndexOrThrow(ShopDbAdapter.KEY_NEED));
        String need = shopDB.getString(shopDB.getColumnIndexOrThrow(ShopDbAdapter.KEY_NEED));

        if (need != null) {
            dbHelper.needViewCreate(need, needView);
        }
        boolean changed = false;
        dbHelper.dbShowStr(shopDB, ShopDbAdapter.KEY_ITEM, (EditText) findViewById(R.id.item_edit));
//        dbHelper.dbShowInt(shopDB, ShopDbAdapter.KEY_QUANTITY, (EditText) findViewById(R.id.item_quantity));

//        dbHelper.dbShowInt(shopDB, ShopDbAdapter.KEY_UNITS, (EditText) findViewById(R.id.item_unit));
        dbHelper.dbShowStr(shopDB, ShopDbAdapter.KEY_UNITS, (EditText) findViewById(R.id.item_unit));
        dbHelper.dbShowStr(shopDB, ShopDbAdapter.KEY_NOTE, (EditText) findViewById(R.id.note_edit));
        dbHelper.dbShowDateStr(shopDB, ShopDbAdapter.KEY_DATE, (Button) findViewById(R.id.pickDate));
        dbHelper.dbShowInt(shopDB, ShopDbAdapter.KEY_PRIORITY, (EditText) findViewById(R.id.item_priority));
        dbHelper.dbShowPriority(shopDB, ShopDbAdapter.KEY_PRIORITY, (ImageView) findViewById(R.id.importance_icon));
//        dbHelper.dbShowInt(shopDB, ShopDbAdapter.KEY_QUANTITY, (EditText) findViewById(R.id.item_quantity));
        dbHelper.dbShowFloat(shopDB, ShopDbAdapter.KEY_QUANTITY, (EditText) findViewById(R.id.item_quantity));
        dbHelper.dbShowBool(shopDB, ShopDbAdapter.KEY_COUPON, (ToggleButton) findViewById(R.id.item_coupon));
        dbHelper.dbShowBool(shopDB, ShopDbAdapter.KEY_TAX, (ToggleButton) findViewById(R.id.item_tax));
        dbHelper.dbShowBool(shopDB, ShopDbAdapter.KEY_TAX2, (ToggleButton) findViewById(R.id.item_tax2));
        dbHelper.dbShowBool(shopDB, ShopDbAdapter.KEY_AUTODELETE, (ToggleButton) findViewById(R.id.item_autodelete));
        dbHelper.dbShowBool(shopDB, ShopDbAdapter.KEY_PRIVATE, (ToggleButton) findViewById(R.id.item_private));
//      TODO:Price
        dbHelper.dbShowFloat(shopDB, ShopDbAdapter.KEY_PRICE, (EditText) findViewById(R.id.item_price));
        dbHelper.dbShowInt(shopDB, ShopDbAdapter.KEY_AISLE, (EditText) findViewById(R.id.item_aisle));
//      TODO:Order
        dbHelper.dbShowBool(shopDB, ShopDbAdapter.KEY_ALARM, (ToggleButton) findViewById(R.id.item_alarm));
//      TODO:Alarmmidi
//      TODO:Icon
        dbHelper.dbShowDateStr(shopDB, ShopDbAdapter.KEY_CREATED, (TextView) findViewById(R.id.item_createdate));
        dbHelper.dbShowDateStr(shopDB, ShopDbAdapter.KEY_MODIFIED, (TextView) findViewById(R.id.item_modifydate));
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
//        final long dateMSec;
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
            String todayStr = dateConverter.msec2Str(date1MSec);
            datesLong[0] = dayMSec + date1MSec;
            dTomorrow = "Tomorrow " + dateConverter.msec2Str(datesLong[0]);
            dates[0] = dTomorrow;

            datesLong[1] = 7 * dayMSec + date1MSec;
            dPlus7 = "In a week " + dateConverter.msec2Str(datesLong[1]);
            dates[1] = dPlus7;

            if ((date1MSec > 1000) & (dbCursor.getCount() > 0)) {
                date1MSec = dbCursor.getLong(dbCursor.getColumnIndexOrThrow(ShopDbAdapter.KEY_DATE));
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
            mPickDate.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    dateClickHandler(mRowId, 0);
//                showDialog(DATE_DIALOG_ID);
                }
            });

            alert.setItems(dates, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    long date0;
                    Toast.makeText(getApplicationContext(), dates[item], Toast.LENGTH_SHORT).show();
                    dateMSec = datesLong[item];
                    values.put(ShopDbAdapter.KEY_DATE, dateMSec);
                    dbHelper.updateItem(rowId, values);
                    mDate.setText(dateConverter.msec2Str(dateMSec));

                    if (requestCode == ItemsOverview.ITEM_DATE) {
                        saveState();
                        setResult(RESULT_OK);
                        finish();
                    }
                }

            });

//            String need = dbCursor.getString(dbCursor.getColumnIndexOrThrow(ShopDbAdapter.KEY_NEED));
            String category;
            String note;
            Long quantity;
            String item;
            if ((dbCursor.getCount() > 0)) {
                category = dbCursor.getString(dbCursor.getColumnIndexOrThrow(ShopDbAdapter.KEY_CATEGORY));
                item = dbCursor.getString(dbCursor.getColumnIndexOrThrow(ShopDbAdapter.KEY_ITEM));
                note = dbCursor.getString(dbCursor.getColumnIndexOrThrow(ShopDbAdapter.KEY_NOTE));
                quantity = dbCursor.getLong(dbCursor.getColumnIndexOrThrow(ShopDbAdapter.KEY_QUANTITY));
            } else {
                item  = mTitleText.getText().toString();
            }
            calTime = dateConverter.msec2Cal(date1MSec);
            int mYear = calTime.get(Calendar.YEAR);
            int mMonth = calTime.get(Calendar.MONTH);
            int mDay = calTime.get(Calendar.DAY_OF_MONTH);

            dpDate.init(mYear, mMonth, mDay, null);

            alert.setTitle(item + " (" + todayStr + ")");
            alert.setView(dpDate); // Adding Aisle to alert

            alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    int mYear = dpDate.getYear();
                    int mMonth = dpDate.getMonth();
                    int mDay = dpDate.getDayOfMonth();

                    dpDate.init(mYear, mMonth, mDay, null);
                    Calendar cal = Calendar.getInstance();
                    cal.set(Calendar.YEAR, mYear);
                    cal.set(Calendar.MONTH, mMonth);
                    cal.set(Calendar.DAY_OF_MONTH, mDay);
                    dateMSec = cal.getTimeInMillis();

//                    dateMSec=date2MSec;
                    values.put(ShopDbAdapter.KEY_DATE, dateMSec);
//                    dbHelper.dbShowDateStr(shopDB, ShopDbAdapter.KEY_DATE, (Button) findViewById(R.id.pickDate));
//                    dbHelper.dbShowDateStr(shopDB, ShopDbAdapter.KEY_DATE, (Button)

                    Button pdButton = (Button) findViewById(R.id.pickDate);
                    pdButton.setText(dateConverter.cal2Str(cal));

                    if (requestCode == ItemsOverview.ITEM_DATE) {
                        dbHelper.updateItem(rowId, values);
                        saveState();
                        setResult(RESULT_OK);
                        finish();
                    }
/*
                String value = aisle.getText().toString().trim();
                Toast.makeText(getApplicationContext(), value, Toast.LENGTH_SHORT).show();
                dbHelper.updateAisle(ShopDbAdapter.DATABASE_CAT_STORE_TABLE, id2, value);
                try {
                    boolean xb = (value != null);
                    xb = !(value.equals(""));
                    if ((value != null) && !(value.equals(""))) {
                        if (dbName.equals(ShopDbAdapter.DATABASE_CATEGORIES)) {
                            dbHelper.storesCatFill(aisleSt, aisleCat, value);
                        }
                    }
                    fillValues(dbName);
                } catch (Exception e) {
                }
*/
                }
            });

            alert.setNeutralButton("Clear date", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    dateMSec = 0;
                    values.put(ShopDbAdapter.KEY_DATE, 0);
                    dbHelper.updateItem(rowId, values);
                    Button pdButton = (Button) findViewById(R.id.pickDate);
                    pdButton.setText("Click to enter date");
                    if (requestCode == ItemsOverview.ITEM_DATE) {
                        saveState();
                        setResult(RESULT_OK);
                        finish();
                    }
/*
                String value = aisle.getText().toString().trim();
                Toast.makeText(getApplicationContext(), value, Toast.LENGTH_SHORT).show();
                dbHelper.updateAisle(ShopDbAdapter.DATABASE_CAT_STORE_TABLE, id2, value);
                try {
                    boolean xb = (value != null);
                    xb = !(value.equals(""));
                    if ((value != null) && !(value.equals(""))) {
                        if (dbName.equals(ShopDbAdapter.DATABASE_CATEGORIES)) {
                            dbHelper.storesCatFill(aisleSt, aisleCat, value);
                        }
                    }
                    fillValues(dbName);
                } catch (Exception e) {
                }

*/
                }
            });
            alert.setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.cancel();
                            if (requestCode == ItemsOverview.ITEM_DATE) {
                                saveState();
                                setResult(RESULT_OK);
                                finish();
                            }
                        }
                    });
            alert.create();
            alert.show();

            /*        Intent i = new Intent(this, ItemDetails.class);
                    i.putExtra(ShopDbAdapter.KEY_ROWID, rowId);
                    i.putExtra("requestCode", ITEM_DATE);
                    startActivityForResult(i, ITEM_DATE);
            */
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(ShopDbAdapter.KEY_ROWID, mRowId);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (requestCode == ItemsOverview.ITEM_DATE) {
            mPickDate.performClick();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
//		saveState();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if ((mRowId != null) & (mRowId != 0)) {
            shopDB = dbHelper.fetchItem(mRowId);
            startManagingCursor(shopDB);

            if (requestCode == ItemsOverview.ITEM_EDIT) {
                populateFields();
            }
        } else {
            shopDB = null;
        }

    }

    private void saveState() {
        Integer quantity;
        String category = (String) mCategory.getSelectedItem();
        String item = mTitleText.getText().toString();
        String note = mBodyText.getText().toString();
//        String need = (String) mNeed.getSelectedItem();
        String date = mDate.getText().toString();
        ContentValues values = new ContentValues();

        try {
            String quantityTxt = mQuantity.getText().toString();
            quantity = new Integer(quantityTxt);
        } catch (NumberFormatException e) {
            Log.v("ItemDetails", "parseLong");
            quantity = 0;
        }

//        boolean changed=false;
        EditText ET = (EditText) findViewById(R.id.item_edit);
//        dbHelper.dbPutStr(shopDB, values, ShopDbAdapter.KEY_ITEM, ET);
        dbHelper.dbPutStr(shopDB, values, ShopDbAdapter.KEY_ITEM, (EditText) findViewById(R.id.item_edit));
//        dbHelper.dbPutStr(shopDB, values, ShopDbAdapter.KEY_ITEM, (EditText) findViewById(R.id.item_edit));
        dbHelper.dbPutStr(shopDB, values, ShopDbAdapter.KEY_NOTE, (EditText) findViewById(R.id.note_edit));
//        dbHelper.dbPutDateStr(shopDB, values, ShopDbAdapter.KEY_DATE, mDate, findViewById(R.id.dateDisplay));
//        dbHelper.dbShowDateStr(shopDB,ShopDbAdapter.KEY_DATE,mDate,R.id.dateDisplay);

//  TODO:Quantity non-int
        dbHelper.dbPutInt(shopDB, values, ShopDbAdapter.KEY_QUANTITY, (EditText) findViewById(R.id.item_quantity));
        dbHelper.dbPutFloat(shopDB, values, ShopDbAdapter.KEY_QUANTITY, (EditText) findViewById(R.id.item_quantity));
//        dbHelper.dbPutInt(shopDB, values, ShopDbAdapter.KEY_QUANTITY, (EditText) findViewById(R.id.item_quantity));
//        dbHelper.dbPutBool(shopDB, values, ShopDbAdapter.KEY_AUTODELETE, (ToggleButton) findViewById(R.id.item_autodelete));
//        dbHelper.dbPutBool(shopDB, values, ShopDbAdapter.KEY_PRIVATE, (ToggleButton) findViewById(R.id.item_private));
        dbHelper.dbPutInt(shopDB, values, ShopDbAdapter.KEY_PRIORITY, (EditText) findViewById(R.id.item_priority));
//    TODO:    dbHelper.dbPutPriority(shopDB, values, ShopDbAdapter.KEY_PRIORITY, (ImageView) findViewById(R.id.importance_icon));
//        dbHelper.dbPutInt(shopDB, values, ShopDbAdapter.KEY_QUANTITY, (EditText) findViewById(R.id.item_quantity));

        dbHelper.dbPutStr(shopDB, values, ShopDbAdapter.KEY_UNITS, (EditText) findViewById(R.id.item_unit));
//        TODO:Dateget
//        dbHelper.dbPutDateStr(shopDB, values, ShopDbAdapter.KEY_DATE, (TextView) findViewById(R.id.itemDate));
//        dbHelper.dbPutDateStr(shopDB, values, ShopDbAdapter.KEY_DATE, (View) findViewById(R.id.pickDate));
//   TODO:     dbHelper.dbPutDateStr(shopDB, values, ShopDbAdapter.KEY_DATE, (EditText) findViewById(R.id.itemDate));
//        dbHelper.dbPutDateStr(shopDB, values, ShopDbAdapter.KEY_DATE,(View) findViewById(R.id.itemDate));
/*
        View vDate=(View) findViewById(R.id.pickDate);
        dbHelper.dbPutDateStr(shopDB, values, ShopDbAdapter.KEY_DATE, vDate);
        values.put(ShopDbAdapter.KEY_DATE, dateMSec);
*/
        dbHelper.dbPutBool(shopDB, values, ShopDbAdapter.KEY_COUPON, (ToggleButton) findViewById(R.id.item_coupon));
        dbHelper.dbPutBool(shopDB, values, ShopDbAdapter.KEY_TAX, (ToggleButton) findViewById(R.id.item_tax));
        dbHelper.dbPutBool(shopDB, values, ShopDbAdapter.KEY_TAX2, (ToggleButton) findViewById(R.id.item_tax2));
        dbHelper.dbPutBool(shopDB, values, ShopDbAdapter.KEY_AUTODELETE, (ToggleButton) findViewById(R.id.item_autodelete));
        dbHelper.dbPutBool(shopDB, values, ShopDbAdapter.KEY_PRIVATE, (ToggleButton) findViewById(R.id.item_private));
        dbHelper.dbPutBool(shopDB, values, ShopDbAdapter.KEY_ALARM, (ToggleButton) findViewById(R.id.item_alarm));
//      TODO:Private - noshow
//      TODO:Autodelete - checkout
        dbHelper.dbPutFloat(shopDB, values, ShopDbAdapter.KEY_PRICE, (EditText) findViewById(R.id.item_price));
        dbHelper.dbPutInt(shopDB, values, ShopDbAdapter.KEY_AISLE, (EditText) findViewById(R.id.item_aisle));
        dbHelper.dbPutNeed(shopDB, values, ShopDbAdapter.KEY_NEED, (ImageView) findViewById(R.id.need_icon));
//TODO:KEY_CATEGORY
        if (!ShopDbAdapter.KEY_CATEGORY.equals(category)) {
            values.put(ShopDbAdapter.KEY_CATEGORY, category);
        }

        Integer changed = values.size();

        if ((mRowId == null) | (mRowId == 0)) {
            if (changed > 0) {
                Calendar cal = Calendar.getInstance();
                dateMSec = cal.getTimeInMillis();
                values.put(ShopDbAdapter.KEY_CREATED, dateMSec);

                long id = dbHelper.createItem(values);
                if (id > 0) {
                    mRowId = id;
                    dbHelper.itemInStoresUnfold(mRowId);
                }
            }
        } else {
            if (changed > 0) {
                Calendar cal = Calendar.getInstance();
                dateMSec = cal.getTimeInMillis();

                values.put(ShopDbAdapter.KEY_MODIFIED, dateMSec);
                dbHelper.updateItem(mRowId, values);

            }
//            }
        }
    }

    public void needclick2(View needView) {
        int rowID;
        String need = (String) needView.getTag();
        needclick(needView);
    }

    public void needclick(View nView) {
        int rowID;

        ImageView needView = (ImageView) findViewById(R.id.need_icon);
        String need = (String) needView.getTag();

        need = dbHelper.needSwitch(need);
        dbHelper.needViewCreate(need, (ImageView) needView);
    }

    private String get(String str) {
        // TODO Auto-generated method stub
        return str.toString();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case mAisleEdit: //DB_EDIT
                Toast.makeText(this,
                        "Aisles edited", Toast.LENGTH_LONG).show();
                break;
            default:
                break;
        }
    }

    public void spinFill1(String dbName, String valSetting, Spinner valueSpinner) {
        String key;
        ArrayList<String>
                valsArrayList = new ArrayList<String>();

        if (dbName.equals(ShopDbAdapter.DATABASE_CATEGORIES)) {
            key = ShopDbAdapter.KEY_CATEGORY;
        } else {
            key = ShopDbAdapter.KEY_STORE;
        }

        Cursor valsCursor = dbHelper.fetchAllValues(dbName,key);
        startManagingCursor(valsCursor);

        if (valsCursor.getCount() == 0) {
            dbHelper.createValue(ShopDbAdapter.DATABASE_STORES, "");
        }
//        valsArrayList.add("");

        if (valsCursor.getCount() != 0) {

            for (valsCursor.moveToFirst(); valsCursor.moveToNext(); valsCursor.isAfterLast()) {
                String x = valsCursor.getString(valsCursor.getColumnIndexOrThrow(key));

                valsArrayList.add(valsCursor.getString(valsCursor.getColumnIndexOrThrow(key)));
            }
            ArrayAdapter<String> vAdapt;
            vAdapt = new ArrayAdapter<String>(this, R.layout.ash_spinner,
                    valsArrayList);
//            vAdapt = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,
//                    valsArrayList);
            valueSpinner.setAdapter(vAdapt);
            for (int i = 0; i < valsCursor.getCount(); i++) {
                String s = (String) valueSpinner.getItemAtPosition(i);
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

    public void spinFill(String dbName, String valSetting, Spinner valueSpinner) {
        String key;
        ArrayList<String>
                valsArrayList = new ArrayList<String>();

        if (dbName.equals(ShopDbAdapter.DATABASE_CATEGORIES)) {
            key = ShopDbAdapter.KEY_CATEGORY;
        } else {
            key = ShopDbAdapter.KEY_STORE;
        }

        Cursor valsCursor = dbHelper.fetchAllValues(dbName,key);
        startManagingCursor(valsCursor);


        if (valsCursor.getCount() == 0) {
//            dbHelper.createValue(ShopDbAdapter.DATABASE_STORES, "");
            dbHelper.createValue(dbName, "-");
        }
//        valsArrayList.add("");
        valsCursor.moveToFirst();

        if (valsCursor.getCount() != 0) {

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
            int n = valueSpinner.getCount();
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
    public void onDestroy() {
        super.onDestroy();

        // Replace mDbHelper as needed with your database connection, or
        // whatever wraps your database connection. (See below.)
        dbHelper.close();
    }

}