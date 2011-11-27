package dk.andbb.andshobber.database;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class ValueArrayAdapter extends CursorAdapter {

    private Cursor mCursor;

    private final LayoutInflater mInflater;
//    private String dbGlobal;


    public ValueArrayAdapter(Context context, Cursor cursor) {
        super(context, cursor, true);

//        dbGlobal=dbName;

        mInflater = LayoutInflater.from(context);

        Context mContext = context;

    }

    // static to save the reference to the outer class and to avoid access to
    // any members of the containing class
    static class ViewHolder {
        public ImageView imageView;
        public TextView valueString;
        public TextView valueAisle;
        public TextView valuePrice;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder;
        String key;
//		View rowView = view.findViewById(R.layout.itemrow);
        View rowView = view;
        String dbx=ListValue.dbName;

        if (cursor.getColumnIndex(ShopDbAdapter.KEY_CATEGORY) != -1) {
            key = ShopDbAdapter.KEY_CATEGORY;
        } else if (cursor.getColumnIndex(ShopDbAdapter.KEY_STORE) != -1) {
            key = ShopDbAdapter.KEY_STORE;
        } else {
            key = ShopDbAdapter.KEY_STORE;
        }
        holder = new ViewHolder();

        if (cursor.getColumnIndex(key) != -1) {
            String string = cursor.getString(cursor.getColumnIndexOrThrow(key));
            holder.valueString = (TextView) rowView.findViewById(R.id.val_string);
            holder.valueString.setText(string);
        }

        if (cursor.getColumnIndex(ShopDbAdapter.KEY_AISLE) != -1) {
            String aisle = cursor.getString(cursor.getColumnIndexOrThrow(ShopDbAdapter.KEY_AISLE));
            holder.valueAisle = (TextView) rowView.findViewById(R.id.val_aisle);
            holder.valueAisle.setText(aisle);
        } else {
            holder.valueAisle = (TextView) rowView.findViewById(R.id.val_aisle);
            holder.valueAisle.setVisibility(View.GONE);
        }
        if (cursor.getColumnIndex(ShopDbAdapter.KEY_PRICE) != -1) {
            Float price = cursor.getFloat(cursor.getColumnIndexOrThrow(ShopDbAdapter.KEY_PRICE));

            NumberFormat formatter = new DecimalFormat("#0.00");
            String price2d=formatter.format(price);

            holder.valuePrice = (TextView) rowView.findViewById(R.id.val_price);
            holder.valuePrice.setText(price2d);
            holder.imageView = (ImageView) rowView.findViewById(R.id.need_icon);
        } else {
            holder.valuePrice = (TextView) rowView.findViewById(R.id.val_price);
            holder.valuePrice.setVisibility(View.GONE);
        }
        rowView.setTag(holder);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view;

        view = mInflater.inflate(R.layout.valuerow, parent, false);

        return view;

    }

}


