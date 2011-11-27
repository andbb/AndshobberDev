package dk.andbb.andshobber.database;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.TextView;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;

public class xmlActivity extends Activity {
    public String DB_PATH = "/data/data/dk.andbb.andshobber.database/assets/";
    public ShopDbAdapter mDbHelper;
    public Context ctx;

    private Data _parseXml(String xmlFilename) {
        Data data = null;

        // sax stuff
        try {
            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser sp = spf.newSAXParser();

            XMLReader xr = sp.getXMLReader();
            Context ctx = this;
            // this holds the data


            XMLHandler dataHandler = new XMLHandler(this);
            xr.setContentHandler(dataHandler);
            AssetManager mgr = getAssets();

            //Find the directory for the SD Card using the API
            //*Don't* hardcode "/sdcard"
            File sdcard = Environment.getExternalStorageDirectory();

            //Get the text file
            File file = new File(xmlFilename);
            //            InputStream in = mgr.open("hs2_indkob.xml");

            InputStream in;
//            in = mgr.open(xmlFilename);
//            in = openFileInput(xmlFilename);
//            in = new FileInputStream(file);
            in = mgr.open(xmlFilename);
//            in = InputStream(file);
//            in = mgr.open(xmlFilename);
//            in = mgr.open("hs2rejse.xml");
//            InputStream in = mgr.open("rejse.xml");
//            InputStream in = mgr.open("hs2_indkob.xml");
//            copyfile(in);
//            in.close();
//             in = mgr.open("temp");
//            FileInputStream in2=openFileInput("temp.xml");

//            xr.parse(new InputSource(new FileInputStream("/sdcard/temp.xml")));
            String x = "";
            BufferedReader r = new BufferedReader(new InputStreamReader(in));
            int l = 0;
            do {
                x = r.readLine();
                l = l + x.length() + 1;
            } while (!x.equals("<Database>"));
            l = l - x.length() - 1;
            in.reset();
            in.skip(l);

            xr.parse(new InputSource(in));

//            xr.parse(new InputSource(in));

            data = dataHandler.getData();

        } catch (ParserConfigurationException pce) {
            Log.e("SAX XML", "sax parse error", pce);
        } catch (SAXException se) {
            Log.e("SAX XML", "sax error", se);
        } catch (IOException ioe) {
            Log.e("SAX XML", "sax parse io error", ioe);
        }

        return data;
    }


    private Data _parseXmlCopy(String xmlFilename) {
        Data data = null;

        // sax stuff
        try {
            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser sp = spf.newSAXParser();

            XMLReader xr = sp.getXMLReader();
            Context ctx = this;
            // this holds the data


            XMLHandler dataHandler = new XMLHandler(this);
            xr.setContentHandler(dataHandler);
            AssetManager mgr = getAssets();

            //Find the directory for the SD Card using the API
            //*Don't* hardcode "/sdcard"
            File sdcard = Environment.getExternalStorageDirectory();

            //Get the text file
            File file = new File(xmlFilename);
//            File file = new File(getApplication().getFileStreamPath(xmlFilename).toString());
            //            InputStream in = mgr.open("hs2_indkob.xml");

            InputStream in;
//            in = mgr.open(xmlFilename);
//            in = openFileInput(xmlFilename);
            in = new FileInputStream(file);
//            in = mgr.open(xmlFilename);
//            in = InputStream(file);
//            in = mgr.open(xmlFilename);
//            in = mgr.open("hs2rejse.xml");
//            InputStream in = mgr.open("rejse.xml");
//            InputStream in = mgr.open("hs2_indkob.xml");
            copyfile(in);
            in.close();
            in = getApplication().openFileInput("temp.xml");
//            in = mgr.open("temp.xml");
//            FileInputStream in2=openFileInput("temp.xml");

//            xr.parse(new InputSource(new FileInputStream("/sdcard/temp.xml")));
            String x = "";
            BufferedReader r = new BufferedReader(new InputStreamReader(in));
/*
            int l = 0;
            do {
                x = r.readLine();
                l = l + x.length() + 1;
            } while (!x.equals("<Database>"));
            l = l - x.length() - 1;
            in.reset();
            in.skip(l);
*/

            xr.parse(new InputSource(in));

//            xr.parse(new InputSource(in));

            data = dataHandler.getData();

        } catch (ParserConfigurationException pce) {
            Log.e("SAX XML", "sax parse error", pce);
        } catch (SAXException se) {
            Log.e("SAX XML", "sax error", se);
        } catch (IOException ioe) {
            Log.e("SAX XML", "sax parse io error", ioe);
        }

        return data;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        String fileName = "";
        setContentView(R.layout.dialog);
//        setContentView(R.layout.xmllist);

//        setContentView(R.layout.get_text_entry);
/*
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
        WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
*/
        // title
    }

//    private class InsertDataTask extends AsyncTask<String, Void, Void> {
/*
        private final ProgressDialog dialog = new ProgressDialog(this);

        // can use UI thread here
        protected void onPreExecute() {
           this.dialog.setMessage("Inserting data...");
           this.dialog.show();
        }

        // automatically done on worker thread (separate from UI thread)
        protected Void doInBackground(final String... args) {
           xmlActivity.this.application.getDataHelper().insert(args[0]);
           return null;
        }

        // can use UI thread here
        protected void onPostExecute(final Void unused) {
           if (this.dialog.isShowing()) {
              this.dialog.dismiss();
           }
           // reset the output view by retrieving the new data
           // (note, this is a naive example, in the real world it might make sense
           // to have a cache of the data and just append to what is already there, or such
           // in order to cut down on expensive database operations)
           new SelectDataTask().execute();

        }
}*/


    @Override
    protected void onResume() {
        super.onResume();

        String fileName = "";
        mDbHelper = new ShopDbAdapter(this);

        try {
            fileName = getIntent().getExtras().getString("file");
        } catch (Exception e) {
        }
// value

        Data data;
//        fileName="hs2_indkob.xml";
//        fileName = "HS2_Jul.pdb.xml";
        data = _parseXmlCopy(fileName);
        finish();
    }

    private Data _parseXml2(String xmlFilename) {
        Data data = null;

        Context ctx = this;
        // this holds the data


        XMLHandler dataHandler = new XMLHandler(this);
        AssetManager mgr = getAssets();
//        mgr=getFilesDir();
        //Find the directory for the SD Card using the API
        //*Don't* hardcode "/sdcard"
        File sdcard = Environment.getExternalStorageDirectory();

        //Get the text file
        File file = new File(xmlFilename);
        //            InputStream in = mgr.open("hs2_indkob.xml");

        InputStream in = null;
        try {
//            in = openFileInput(xmlFilename);
//            FileInputStream
            in = new FileInputStream(file);
            in = mgr.open(xmlFilename);
//            in= new InputStream(file);
//            in = mgr.open("hs2rejse.xml");
//            InputStream in = mgr.open("rejse.xml");
//            InputStream in = mgr.open("hs2_indkob.xml");
//            copyfile(in);
//            in.close();
//             in = mgr.open("temp");
//            FileInputStream in2=openFileInput("temp.xml");

//            xr.parse(new InputSource(new FileInputStream("/sdcard/temp.xml")));
            String x = "";
            BufferedReader r = new BufferedReader(new InputStreamReader(in));
            int l = 0;
            do {
                in.mark(1000);
                x = r.readLine();
                l = l + x.length() + 1;
            } while (!x.equals(""));
/*
            l = l - x.length() - 1;
            in.reset();
            x = r.readLine();
*/
        } catch (IOException ioe) {
            Log.e("SAX XML", "sax parse io error", ioe);
        }
//            in.skip(l);
        try {
            // sax stuff
            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser sp = spf.newSAXParser();

            XMLReader xr = sp.getXMLReader();
            xr.setContentHandler(dataHandler);

            xr.parse(new InputSource(in));

//            xr.parse(new InputSource(in));

            data = dataHandler.getData();

        } catch (ParserConfigurationException pce) {
            Log.e("SAX XML", "sax parse error", pce);
        } catch (SAXException se) {
            Log.e("SAX XML", "sax error", se);
        } catch (IOException ioe) {
            Log.e("SAX XML", "sax parse io error", ioe);
        }

        return data;
    }

    private void copyfile(InputStream inputStream) throws IOException {
        String newline = System.getProperty("line.separator");
        FileOutputStream fos = null;
        ObjectOutputStream out = null;
        String total;
        setContentView(R.layout.main);
        BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
        String x = "";
        total = "";
        int i = 0;
        Integer j = 0;
        while (!x.equals("<Database>")) {
            x = r.readLine();
        }


        File root = Environment.getExternalStorageDirectory();

        //Open your local db as the input stream
//	    	InputStream myInput = myContext.getAssets().open(DB_NAME);
//            String inFileName = DB_PATH + old_NAME;
//            InputStream myInput = new FileInputStream(inFileName);

        // Path to the just created empty db
//        String outFileName = "/sdcard/temp.xml";
        String outFileName = "temp.xml";
//            outFileName = fileName;
        TextView outTV;
        //Open the output stream
//        OutputStream myOutput = new FileOutputStream(outFileName);
//        File gpxfile = new File(outFileName);
        FileOutputStream outst = openFileOutput("temp.xml", this.MODE_PRIVATE);
//        FileWriter writer = new FileWriter(gpxfile);
//        FileWriter writer = new FileWriter(outst);
//        outTV=(TextView) findViewById(R.id.outtv);
        outst.write(x.getBytes());
        outst.write(newline.getBytes());
        while (x != null) {
            x = r.readLine();
//            writer.append(x);
            if (x != null) {
                outst.write(x.getBytes());
                outst.write(newline.getBytes());
            }
            i += 1;
            if (i == 1000) {
                i = 0;
                j += 1;
            }
        }
        String fn=outst.toString();
        outst.flush();
        outst.close();
//        writer.flush();
//        writer.close();

        inputStream.close();

    }
    public void onDestroy() {
        super.onDestroy();

        // Replace mDbHelper as needed with your database connection, or
        // whatever wraps your database connection. (See below.)
//        mDbHelper.close();
    }

}