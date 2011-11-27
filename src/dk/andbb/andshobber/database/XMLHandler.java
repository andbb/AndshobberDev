package dk.andbb.andshobber.database;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import static java.lang.System.currentTimeMillis;

/**
 * http://www.jondev.net/articles/Android_XML_SAX_Parser_Example * Created by IntelliJ IDEA.
 * User: Anders og Charlotte
 * Date: 13-08-11
 * Time: 20:39
 * To change this template use File | Settings | File Templates.
 */
public class XMLHandler extends DefaultHandler {

    // booleans that check whether it's in a specific tag or not
    private boolean _inSection, _inArea;
    private Data _data;
    private boolean _inCategories;
    private boolean _inDatabase;
    private boolean _inCategory;
    private boolean _inIcon;
    private boolean _inStores;
    private boolean _inStore;
    private boolean _inItems;
    private boolean _inItem;
    private boolean _inDate;
    private boolean _inAisle;
    private boolean _inPerStore;
    private boolean _inAutoOrder;
    private boolean _inentryorder;
    public String[] category = new String[50];
    public int nCats = 0;
    public String tag;
    public String storeStr;
    public ContentValues updateValues = new ContentValues();
    public ContentValues perStoreUpdateValues = new ContentValues();
    private int nStores = 0;
    private String[] stores = new String[50];
    private boolean _inDescription;
    private boolean _inQuantity;
    public ShopDbAdapter mDbHelper;
    public Context ctx;
    private long lastTime;
    private long nowTime;
    private long mRowID;
    private String toastString;
    private float priceVal;
    private String aisleStr;
    private long guessID;
    private ContentValues itemStoreUpdateValues = new ContentValues();
    private String cat = "";
    private long csi;
    // this holds the data


    public XMLHandler(Context context) {
        ctx = context;
        mDbHelper = new ShopDbAdapter(ctx);
        if (mDbHelper == null) {
//            updateDBName("");
//            dbFiles();
//            fillData("");
//            updateDBName(CreateShopDatabase.DATABASE_NAME);
        }
        mDbHelper.open();
    }


    /**
     * Returns the data object
     *
     * @return
     */
//  public String getData() {
    public Data getData() {
        return _data;
    }

    /**
     * This gets called when the xml document is first opened
     *
     * @throws org.xml.sax.SAXException
     */
    @Override
    public void startDocument() throws SAXException {
/*
        Activity act=this.
                 Context.getApplicationContext();
        Activity.getApplication();
*/
/*
        Context appCtxt2=getBaseContext();
        appCtxt2 = Context.getApplicationContext();
        Context appCtxt= Activity.getApplication();
*/
/*
        mDbHelper = new ShopDbAdapter(appCtxt);
        mDbHelper.open();
*/
        _data = new Data();
    }

    /**
     * Called when it's finished handling the document
     *
     * @throws org.xml.sax.SAXException
     */
    @Override
    public void endDocument() throws SAXException {

    }

    /**
     * This gets called at the start of an element. Here we're also setting the booleans to true if it's at that specific tag. (so we
     * know where we are)
     *
     * @param namespaceURI
     * @param localName
     * @param qName
     * @param atts
     * @throws org.xml.sax.SAXException
     */
    @Override
    public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {

        tag = localName;
        if (localName.equalsIgnoreCase("database")) {
            _inDatabase = true;
        } else if (localName.equalsIgnoreCase("categories")) {
            _inCategories = true;
        } else if (localName.equalsIgnoreCase("category")) {
            _inCategory = true;
//        TODO: Add category
            if (_inCategories) {
//            TODO: Category to table
            } else if (_inItem) {
//            TODO: Category to item
            }
        } else if (localName.equalsIgnoreCase("icon")) {
//        TODO: Add icon
            _inIcon = true;
        } else if (localName.equalsIgnoreCase("stores")) {
//        TODO: Add stores
            _inStores = true;
        } else if (localName.equalsIgnoreCase("store")) {
//        TODO: Add store
            _inStore = true;
            if (_inItems) {
            }
        } else if (localName.equalsIgnoreCase("items")) {
            _inItems = true;
        } else if (localName.equalsIgnoreCase("item")) {
//        TODO: Add item
//        TODO: Find NEED/HAVE/
//            if (!updateValues.equals(null)) {
//                updateValues.clear();
//            }
            String need = atts.getValue("NEED");
            _inItem = true;
        } else if (localName.equalsIgnoreCase("description")) {
//        TODO: Add description
            _inDescription = true;
        } else if (localName.equalsIgnoreCase("quantity")) {
//        TODO: Add quantity
            _inQuantity = true;
        } else if (localName.equalsIgnoreCase("aisle")) {
//        TODO: Add aisle
            _inAisle = true;
        } else if (localName.equalsIgnoreCase("date")) {
//        TODO: Add date
            _inDate = true;
        } else if (localName.equalsIgnoreCase("perstore")) {
//        TODO: Add perstore
            _inPerStore = true;
        } else if (localName.equalsIgnoreCase("autoorder")) {
//        TODO: Add autoorder
            _inAutoOrder = true;
        } else if (localName.equalsIgnoreCase("entryorder")) {
//        TODO: Add entryorder
            _inentryorder = true;
        }
    }

    /**
     * Called at the end of the element. Setting the booleans to false, so we know that we've just left that tag.
     *
     * @param namespaceURI
     * @param localName
     * @param qName
     * @throws org.xml.sax.SAXException
     */
    @Override
    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
//        Log.v("endElement", localName);
        tag = "";
        if (localName.equalsIgnoreCase("categories")) {
            _inCategories = false;
            _data.sectionId = "Øv";
//            updateValues.clear();
        } else if (localName.equalsIgnoreCase("icon")) {
            _inIcon = false;
            _data.sectionId = "Øv";
//            updateValues.clear();
        } else if (localName.equalsIgnoreCase("stores")) {
            _inStores = false;
            if (!_inItem) {
                nStores = 0;
            }
            _data.sectionId = "Øv";
//            updateValues.clear();
        } else if (localName.equalsIgnoreCase("store")) {
            _inStore = false;
            if (!_inItem) {
//                UpdateNo store
            }
            _data.sectionId = "Øv";
//            updateValues.clear();
        } else if (localName.equalsIgnoreCase("item")) {
            _inItem = false;

            mRowID = mDbHelper.createItem(updateValues);
            itemStoreUpdateValues.put(ShopDbAdapter.KEY_ITEMID, mRowID);
            csi = mDbHelper.createStoreItem(itemStoreUpdateValues);
            guessID = mRowID + 1;
            cat = "";
            updateValues.clear();
            itemStoreUpdateValues.clear();

//                TODO: Multi stores
            if (nStores > 0) {
                for (String store1 : stores) {
//                    updateValues.get()
                }
            }
            _data.sectionId = "Øv";
        } else if (localName.equalsIgnoreCase("PerStore")) {
            _inPerStore = false;

            perStoreUpdateValues.put(ShopDbAdapter.KEY_ITEMID, guessID);
            perStoreUpdateValues.put(ShopDbAdapter.KEY_CATEGORY, cat);
//            mDbHelper.createItem(perStoreUpdateValues);
//            perStoreUpdateValues.putAll(updateValues);
//            perStoreUpdateValues.put();

            csi = mDbHelper.createStoreItem(perStoreUpdateValues);

            perStoreUpdateValues.clear();

            _data.sectionId = "Øv";
        } else if (localName.equalsIgnoreCase("AutoOrder")) {
            _inAutoOrder = false;

            _data.sectionId = "Øv";
        } else if (localName.equalsIgnoreCase("items")) {
            _inItems = false;
//            updateValues.clear();
            _data.sectionId = "Øv";
        } else if (localName.equalsIgnoreCase("area")) {
            _inArea = false;
        }
    }

    /**
     * Calling when we're within an element. Here we're checking to see if there is any content in the tags that we're interested in
     * and populating it in the Config object.
     *
     * @param ch
     * @param start
     * @param length
     */
    @Override
    public void characters(char ch[], int start, int length) {
        String chars = new String(ch, start, length);
        chars = chars.trim();

        if (_inCategories) {
            if (tag.equalsIgnoreCase("Category")) {
                if (!chars.equals("")) {
                    mDbHelper.createValue(ShopDbAdapter.DATABASE_CATEGORIES, chars);
                    mDbHelper.storeInNewCatUnfold(chars);
                    category[nCats] = chars;
                    nCats++;
                }
            }
        } else if (_inStores & !_inItem) {
            if (tag.equalsIgnoreCase("Store")) {
                if (!chars.equals("")) {
                    csi = mDbHelper.createValue(ShopDbAdapter.DATABASE_STORES, chars);
                    mDbHelper.catsInNewStoreUnfold(chars);
                    stores[nStores] = chars;
                    nStores++;
                }
            }
        } else if (_inStores & _inItem) {
            if (tag.equalsIgnoreCase("Store")) {
                if (!chars.equals("")) {
// TODO:Instores ad and later update
                }
            }
        } else if (_inItem) {
            if (_inStores | _inPerStore | _inAutoOrder) { // TODO:Adding store specifics etc.
                if (_inStores) {
                    if (tag.equalsIgnoreCase("Store")) {
                        if (!chars.equals("")) {
                            stores[nStores] = chars;
                            nStores++;
//                            TODO: Lookup Store/Item and set = Active
                        }
                    }
                }

                if (_inPerStore) {
                    if (tag.equalsIgnoreCase("Store")) {
                        if (!chars.equals("")) {
                            storeStr = chars;
                            perStoreUpdateValues.put(ShopDbAdapter.KEY_STORE, chars);
//                            TODO: Lookup Store/Item and set = Active
                        }
                    } else if (tag.equalsIgnoreCase("Aisle")) {
                        if (!chars.equals("")) {
                            aisleStr = chars;
                            perStoreUpdateValues.put(ShopDbAdapter.KEY_AISLE, chars);
//                            TODO: Lookup Store/Item and store Aisle
                        }
                    } else if (tag.equalsIgnoreCase("Price")) {
                        if (!chars.equals("")) {
                            priceVal = new Float(chars);
                            perStoreUpdateValues.put(tag, priceVal);
//                            TODO: Lookup Store/Item and store Price
                        }
                    }
                }

            } else {
                if (tag.equalsIgnoreCase("Store")) {
                    if (!chars.equals("")) {
                        storeStr = chars;
                        itemStoreUpdateValues.put(ShopDbAdapter.KEY_STORE, chars);
//                            TODO: Lookup Store/Item and set = Active
                    }
                } else if (tag.equalsIgnoreCase("Aisle")) {
                    if (!chars.equals("")) {
                        aisleStr = chars;
                        itemStoreUpdateValues.put(ShopDbAdapter.KEY_AISLE, chars);
//                            TODO: Lookup Store/Item and store Aisle
                    }
                } else if (tag.equalsIgnoreCase("Price")) {
                    if (!chars.equals("")) {
                        priceVal = new Float(chars);
                        itemStoreUpdateValues.put(tag, priceVal);
//                            TODO: Lookup Store/Item and store Price
                    }
                } else if (tag.equalsIgnoreCase("Category")) {
                    if (!chars.equals("")) {
                        cat = chars;
                        itemStoreUpdateValues.put(tag, cat);
//                            TODO: Lookup Store/Item and store Price
                    }
                }

                if (!chars.equals("")) {
                    if (tag.equals("Date")) {
/*
                        String Year=chars.substring(1,4);
                        String Month=chars[6:7];
                        String Day=chars[9:10];
                        int dYear=Integer (Year);
*/
                        if (chars.length() >= 10) {
                            String Year = (String) chars.substring(0, 4);
                            String Month = (String) chars.substring(5, 7);
                            String Day = (String) chars.substring(8, 10);
                            int dYear = new Integer(chars.substring(0, 4));
                            int dMonth = new Integer(chars.substring(5, 7));
                            int dDay = new Integer(chars.substring(8, 10));
                            long calMsec = dateConverter.ymd2Msec(dYear, dMonth, dDay);

                            updateValues.put(tag, calMsec);
                        } else {
                            toastString = "Date error";
                            Toast toast = Toast.makeText(ctx, (CharSequence) toastString, 2000);
                            toast.show();

                        }
                    } else if (tag.equals("Description")) {
                        updateValues.put(ShopDbAdapter.KEY_ITEM, chars);
                        toastString = new StringBuilder().append(chars).append(" ").append(Long.toString(mRowID)).toString();

                        nowTime = currentTimeMillis();
                        if ((nowTime - lastTime) > 10000) {
                            Toast toast = Toast.makeText(ctx, (CharSequence) toastString, 2000);
                            toast.show();
                            lastTime = nowTime;
                        }


                    } else if (tag.equals("Quantity") | tag.equals("Priority")) {
                        int val = 0;
                        try {
                            val = new Integer(chars);
                            updateValues.put(tag, val);
                        } catch (NumberFormatException e) {
                            Log.e("XMLParser", "Non integer quantity");
                        }

                    } else if (tag.equals("EntryOrder")) {
//                        TODO: Check
                    } else if (tag.equals("Stores")) {
                        updateValues.put(ShopDbAdapter.KEY_STORE, chars);
                    } else {
                        updateValues.put(tag, chars);
                    }

                }
            }
        } else if (_inStores) {  // Store and not Item - increase stores list
            if (tag.equalsIgnoreCase("Store")) {
                if (!chars.equals("")) {
                    mDbHelper.createValue(ShopDbAdapter.DATABASE_STORES, chars);
                    mDbHelper.catsInNewStoreUnfold(chars);
                    mDbHelper.itemsInNewStoreUnfold(chars);

                    stores[nStores] = chars;
                    nStores++;
                }
            }
//            _data.area = chars;
        }
    }
}