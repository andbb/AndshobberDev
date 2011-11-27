package dk.andbb.andshobber.database;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Calendar;

public class ItemArrayAdapter extends CursorAdapter implements Filterable {

    private ViewHolder holder;
    private View rowView;
    private Cursor mCursor;

    private final LayoutInflater mInflater;

    public ItemArrayAdapter(Context context, Cursor cursor) {

        super(context, cursor, true);

        mInflater = LayoutInflater.from(context);

        Context mContext = context;

    }

    // static to save the reference to the outer class and to avoid access to
    // any members of the containing class
    static class ViewHolder {
        public ImageView imageView;
        public TextView tvNeed;
        public TextView tvItem;
        public TextView tvCategory;
        public TextView tvQuantity;
        public TextView notes;
        public TextView date;
        public TextView aisle;
        public ImageView priority;
    }

    @Override

    public void bindView(View view, Context context, Cursor cursor) {
//        ViewHolder holder;

//		View rowView = view.findViewById(R.layout.itemrow);
        rowView = view;
        holder = new ViewHolder();
        holder.tvNeed = (TextView) rowView.findViewById(R.id.lblNeed);
        holder.tvItem = (TextView) rowView.findViewById(R.id.lblItem);
        holder.tvCategory = (TextView) rowView.findViewById(R.id.lblCat);
        holder.tvQuantity = (TextView) rowView.findViewById(R.id.quantity);
        holder.imageView = (ImageView) rowView.findViewById(R.id.need_icon);
        holder.notes = (TextView) rowView.findViewById(R.id.lblNote);
        holder.priority = (ImageView) rowView.findViewById(R.id.prio_icon);

        holder.aisle = (TextView) rowView.findViewById(R.id.lblAisle);

        rowView.setTag(holder);

        String t1 = cursor.getString(cursor.getColumnIndexOrThrow(ShopDbAdapter.KEY_NEED));
        String t2 = cursor.getString(cursor.getColumnIndexOrThrow(ShopDbAdapter.KEY_ITEM));
        String t3 = cursor.getString(cursor.getColumnIndexOrThrow(ShopDbAdapter.KEY_CATEGORY));
        String aisle = cursor.getString(cursor.getColumnIndexOrThrow(ShopDbAdapter.KEY_AISLE));
        String notes = cursor.getString(cursor.getColumnIndexOrThrow(ShopDbAdapter.KEY_NOTE));
//        String notes = cursor.getString(cursor.getColumnIndexOrThrow(ShopDbAdapter.KEY_NOTE));

        Integer prio=dbShowPriority(cursor, ShopDbAdapter.KEY_PRIORITY, holder.priority);
        Long q = (long) 0;

        if (cursor.getColumnIndex(ShopDbAdapter.KEY_QUANTITY) != -1) {
            q = cursor.getLong(cursor.getColumnIndexOrThrow(ShopDbAdapter.KEY_QUANTITY));
        }
        holder.tvNeed.setText(t1);
        holder.tvItem.setText(t2);
        holder.tvCategory.setText(t3);
        holder.tvQuantity.setText(q.toString());
        holder.aisle.setText(aisle);

        holder.date = (TextView) rowView.findViewById(R.id.lblDate);
        long dMsec = cursor.getLong(cursor.getColumnIndexOrThrow(ShopDbAdapter.KEY_DATE));
        String date = "";
        if (dMsec > 1000) {
            date = msec2Str(dMsec);
        }

        holder.date.setText(date);

        holder.notes = (TextView) rowView.findViewById(R.id.lblNote);
        String note = cursor.getString(cursor.getColumnIndexOrThrow(ShopDbAdapter.KEY_DATE));
        holder.notes.setText(note);

        String s = cursor.getString(cursor.getColumnIndexOrThrow(ShopDbAdapter.KEY_NEED));
        if (s != null) {
            if (s.startsWith("x")) {
                holder.imageView.setImageResource(R.drawable.btn_check_on);
            } else if (s.startsWith("have")) {
                holder.imageView.setImageResource(R.drawable.btn_circle_normal);
            } else if (s.equalsIgnoreCase("")) {
                holder.imageView.setImageResource(R.drawable.btn_check_off);
            } else if (s.startsWith("later")) {
                holder.imageView.setImageResource(R.drawable.btn_check_off_pressed);
            } else {
//                  error in NEED
//                holder.imageView.setImageResource(R.drawable.btn_circle_normal);
            }

        }
        int rowID = cursor.getInt(cursor.getColumnIndexOrThrow(ShopDbAdapter.KEY_ROWID));

        holder.tvItem.setTag(rowID);
        holder.imageView.setTag(rowID);
        holder.tvQuantity.setTag(rowID);
        holder.date.setTag(rowID);
        holder.notes.setTag(rowID);
    }


    @Override

    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view;


        switch (ShopDbAdapter.VIEW_KEY) {
            case ItemsOverview.VIEW_SHOBBER: {
                view = mInflater.inflate(R.layout.itemrow, parent, false);
                return view;
            }
            case ItemsOverview.VIEW_DETAIL: {
                view = mInflater.inflate(R.layout.itemrowall, parent, false);
                return view;
            }
            case ItemsOverview.VIEW_DATE: {
                view = mInflater.inflate(R.layout.itemrowdate, parent, false);
                return view;
            }
            case ItemsOverview.VIEW_PRIO: {
                view = mInflater.inflate(R.layout.itemprio, parent, false);
                return view;
            }
        }

        view = mInflater.inflate(R.layout.itemrow, parent, false);
        holder.tvNeed.setOnClickListener(mOnNeedClickListener);
        holder.tvQuantity.setOnClickListener(mOnQuantityClickListener);

        return view;

    }


    private OnClickListener mOnQuantityClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            ItemsOverview.CLICKED = ShopDbAdapter.CLICK_QUANTITY;
        }
    };

    private OnClickListener mOnNeedClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            ItemsOverview.CLICKED = ShopDbAdapter.CLICK_NEED;
//                    getPositionForView((View) v.getParent());
//            Log.v(TAG, "Title clicked, row %d", position);
        }
    };

    @Override
    public Cursor runQueryOnBackgroundThread(CharSequence constraint) {

        if (getFilterQueryProvider() != null) {
            return getFilterQueryProvider().runQuery(constraint);
        }

        StringBuilder buffer = null;
        String[] args = null;
        if (constraint != null) {
            buffer = new StringBuilder();
            buffer.append("UPPER(");
            buffer.append(ShopDbAdapter.KEY_ITEM);
            buffer.append(") GLOB ?");
            args = new String[]{constraint.toString().toUpperCase() + "*"};
        }

        return mCursor;
    }

    public String msec2Str(long msecTime) {
        Calendar calTime;

        calTime = Calendar.getInstance();
        calTime.setTimeInMillis(msecTime);
//                (msecTime);
        String calStr = cal2Str(calTime);

        return calStr;
    }

    public Calendar msec2Cal(long msecTime) {

        Calendar calTime;

        calTime = Calendar.getInstance();
        calTime.setTimeInMillis(msecTime);

        return calTime;
    }

    public String cal2Str(Calendar calTime) {

//        Calendar calTime=Calendar.setTimeInMillis(msecTime);
        String calStr;
        int mYear = calTime.get(Calendar.YEAR);
        int mMonth = calTime.get(Calendar.MONTH);
        int mDay = calTime.get(Calendar.DAY_OF_MONTH);
        calStr = Integer.toString(mDay) + "-" + Integer.toString(mMonth + 1) + "-" + Integer.toString(mYear);

        return calStr;
    }

    public long cal2Msec(Calendar calTime) {
        long mSecTime;
        mSecTime = calTime.getTimeInMillis();
        return mSecTime;
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

}


