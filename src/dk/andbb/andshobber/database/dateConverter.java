package dk.andbb.andshobber.database;

import java.util.Calendar;

/**
 * Created by IntelliJ IDEA.
 * User: Anders og Charlotte
 * Date: 06-08-11
 * Time: 16:43
 * To change this template use File | Settings | File Templates.
 */
public class dateConverter {

    static class DateHolder {
        public int Day;
        public int Month;
        public int Year;
    }

    public static String msec2Str(long msecTime) {
        Calendar calTime;

        calTime = Calendar.getInstance();
        calTime.setTimeInMillis(msecTime);
        String calStr = cal2Str(calTime);

        return calStr;
    }

    public static Calendar msec2Cal(long msecTime) {

        Calendar calTime;

        calTime = Calendar.getInstance();
        calTime.setTimeInMillis(msecTime);

        return calTime;
    }

    public static String cal2Str(Calendar calTime) {
        String calStr;
        int mYear = calTime.get(Calendar.YEAR);
        int mMonth = calTime.get(Calendar.MONTH);
        int mDay = calTime.get(Calendar.DAY_OF_MONTH);
        calStr = Integer.toString(mDay) + "-" + Integer.toString(mMonth + 1) + "-" + Integer.toString(mYear);

        return calStr;
    }

    public static DateHolder cal2Date(Calendar calTime) {
        String calStr;
        DateHolder dateDate = new DateHolder();
        dateDate.Year = calTime.get(Calendar.YEAR);
        dateDate.Month = calTime.get(Calendar.MONTH);
        dateDate.Day = calTime.get(Calendar.DAY_OF_MONTH);
//        calStr = Integer.toString(mDay) + "-" + Integer.toString(mMonth + 1) + "-" + Integer.toString(mYear);

        return dateDate;
    }

    public static long cal2Msec(Calendar calTime) {
        long mSecTime;
        mSecTime = calTime.getTimeInMillis();
        return mSecTime;
    }

    public static Calendar ymd2Cal(int mYear, int mMonth, int mDay) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, mYear);
        cal.set(Calendar.MONTH, mMonth);
        cal.set(Calendar.DAY_OF_MONTH, mDay);
        return cal;

    }

    public static long ymd2Msec(int mYear, int mMonth, int mDay) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, mYear);
        cal.set(Calendar.MONTH, mMonth);
        cal.set(Calendar.DAY_OF_MONTH, mDay);
        Long dateMSec = cal.getTimeInMillis();

        return dateMSec;

    }

/*    public String ymd2Strfm(Integer tYear, Integer tMonth, Integer tDay) {


//        myString = DateFormat.getDateInstance().format(myDate);
        DateFormat dateFormat = DateFormat.getDateFormat().format(myDate);

        String dateStr = "04/05/2010";

        SimpleDateFormat curFormater = new SimpleDateFormat("dd/MM/yyyy");
        Date dateObj = curFormater.parse(dateStr);
        SimpleDateFormat postFormater = new SimpleDateFormat("MMMM dd, yyyy");

        String newDateStr = postFormater.format(dateObj);

        String calStr = (String) new StringBuilder()
                // Month is 0 based so add 1
                .append(tMonth + 1).append("-")
                .append(tDay).append("-")
                .append(tYear).append(" ");
        return calStr;
    }


    public String cal2Strfm(Calendar calDate) {

        SimpleDateFormat sdf = new SimpleDateFormat("YYYY-mm-dd");

        String calStr = sdf.format(calDate);
        return calStr;
//        myString = DateFormat.getDateInstance().format(myDate);
*//*
        DateFormat dateFormat = DateFormat.getDateFormat().format(calDate);
        DateFormat df = dateFormat.getDateInstance();
        String date = df.format(today);)

        String dateStr = "04/05/2010";

        SimpleDateFormat curFormater = new SimpleDateFormat("dd/MM/yyyy");
        Date dateObj = curFormater.parse(dateStr);
        SimpleDateFormat postFormater = new SimpleDateFormat("MMMM dd, yyyy");

        String newDateStr = postFormater.format(dateObj);

        String calStr=new StringBuilder()
                            // Month is 0 based so add 1
                            .append(tMonth + 1).append("-")
                            .append(tDay).append("-")
                            .append(tYear).append(" "));
        return  calStr;
*//**//*

    }
*//*
    }*/
}