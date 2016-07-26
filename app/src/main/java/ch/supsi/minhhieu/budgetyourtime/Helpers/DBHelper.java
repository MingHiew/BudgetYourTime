package ch.supsi.minhhieu.budgetyourtime.Helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import ch.supsi.minhhieu.budgetyourtime.Models.Budget;
import ch.supsi.minhhieu.budgetyourtime.Models.Item;
import ch.supsi.minhhieu.budgetyourtime.Models.User;

/**
 * Created by acer on 18/07/2016.
 */
public class DBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;

    private static final String TAG = "DatabaseHelper";
    public final static String DATABASE_NAME = "BudgetYourTime";
    public final static String TABLE_USER = "users";
    public final static String TABLE_ITEM = "items";
    public final static String TABLE_BUDGET = "budgets";

    //Common keys
    public final static String KEY_ID = "id";
    public static final String _ID = "_id";

    //users
    public final static String KEY_USERNAME = "username";
    public final static String KEY_EMAIL = "email";
    public final static String KEY_THUMB_URL = "thumburl";

    //items
    public final static String KEY_BUDGET = "budget";
    public final static String KEY_STARTTIME = "starttime";
    public final static String KEY_ENDTIME = "endtime";
    public final static String KEY_LOCATION = "location";
    public final static String KEY_IDESCRIPTION = "description";
    public final static String KEY_DURATION = "duration";

    //budgets
    public final static String KEY_NAME = "name";
    public final static String KEY_AMOUNT = "amount";
    public final static String KEY_USEDAMOUNT = "usedamount";
    public final static String KEY_TYPE = "type";
    public final static String KEY_DESCRIPTION = "description";
    public final static String KEY_USER = "user";

    //Singleton
    private static DBHelper instance;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static DBHelper getInstance(Context context){
        if(instance ==  null){
            instance = new DBHelper(context);
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableUser = "CREATE TABLE " + TABLE_USER + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_USERNAME + " TEXT,"
                + KEY_EMAIL + " TEXT,"
                + KEY_THUMB_URL + " TEXT"
                + ")";
        String createTableBudget = "CREATE TABLE " + TABLE_BUDGET + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_NAME + " TEXT,"
                + KEY_AMOUNT + " INTEGER,"
                + KEY_USEDAMOUNT + " INTEGER,"
                + KEY_TYPE + " INTEGER,"
                + KEY_DESCRIPTION + " TEXT,"
                + KEY_USER + "INTEGER,"
                + "FOREIGN KEY (" + KEY_USER + ") REFERENCES " + TABLE_USER + " (" + KEY_ID + ") "
                + ")";
        String createTableItem = "CREATE TABLE" + TABLE_ITEM + "("
                + KEY_ID + "INTEGER PRIMARY KEY,"
                + KEY_STARTTIME + " DATETIME,"
                + KEY_ENDTIME + " DATETIME,"
                + KEY_LOCATION + " TEXT,"
                + KEY_IDESCRIPTION + " TEXT,"
                + KEY_DURATION + " INTEGER,"
                + KEY_BUDGET + " INTEGER,"
                + "FOREIGN KEY (" + KEY_BUDGET + ") REFERENCES " + TABLE_BUDGET + " (" + KEY_ID + ") "
                + ")";

        db.execSQL(createTableUser);
        db.execSQL(createTableBudget);
        db.execSQL(createTableItem);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BUDGET);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEM);
        onCreate(db);
    }

    /***********************************************************************************
     BUDGETS
     **********************************************************************************/
    public void addBudget(Budget b){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_USER,b.getUser());
        values.put(KEY_NAME, b.getName());
        values.put(KEY_AMOUNT,b.getAmount());
        values.put(KEY_USEDAMOUNT, b.getUsedAmount());
        values.put(KEY_TYPE, b.getType().getValue());
        values.put(KEY_DESCRIPTION, b.getDescription());
        db.insert(TABLE_BUDGET,null,values);
        db.close();
    }

    public Budget getBudget (int id){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_BUDGET, new String[] {KEY_ID, KEY_NAME,
                        KEY_USER, KEY_AMOUNT, KEY_USEDAMOUNT, KEY_TYPE, KEY_DESCRIPTION}, KEY_ID + "=?",
                new String[] {String.valueOf(id)}, null, null, null, null);
        if(cursor!=null)
            cursor.moveToFirst();

        Budget budget = new Budget(cursor.getInt(0),
                cursor.getInt(1), Budget.Budget_Type.valueOf(cursor.getInt(2)),
                cursor.getString(3),cursor.getInt(4),cursor.getString(5));

        return budget;
    }

    public List<Budget> getAllUserBudgets (User user){
        List<Budget> list = new ArrayList<Budget>();
        SQLiteDatabase db = this.getWritableDatabase();
        int userID = user.getId();
        String query = "SELECT * FROM" + TABLE_BUDGET + " WHERE USER = " + userID;
        Cursor cursor = db.rawQuery(query,null);
        if(cursor.moveToFirst()){
            do{
                Budget b= new Budget();
                b.setId(cursor.getInt(0));
                b.setType(Budget.Budget_Type.valueOf(cursor.getInt(2)));
                b.setName(cursor.getString(3));
                b.setAmount(cursor.getInt(4));
                b.setDescription(cursor.getString(5));
                list.add(b);
            }
            while(cursor.moveToNext());
        }
        return list;
    }

    public int editBudget (Budget b){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_USER,b.getUser());
        values.put(KEY_NAME, b.getName());
        values.put(KEY_AMOUNT,b.getAmount());
        values.put(KEY_TYPE, b.getType().getValue());
        values.put(KEY_DESCRIPTION, b.getDescription());

        return db.update(TABLE_BUDGET, values, KEY_ID + " = ?",
                new String[] { String.valueOf(b.getId()) });
    }

    public Cursor getAllUserBudgetCursors(boolean adapter, User user){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "";
        if(adapter) {
            int userID = user.getId();
            query = "SELECT * FROM" + TABLE_BUDGET + " WHERE USER = " + userID;
        }
        return db.rawQuery(query,null);
    }

    public void deleteBudget (Budget b){}

    /***********************************************************************************
     ITEMS
     **********************************************************************************/
    public void addItem (Item i){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_STARTTIME, String.valueOf(i.getStartTime()));
        values.put(KEY_ENDTIME, String.valueOf(i.getEndTime()));
        values.put(KEY_LOCATION, i.getLocation());
        values.put(KEY_IDESCRIPTION, i.getDescription());
        values.put(KEY_DURATION, i.getDuration());
        values.put(KEY_BUDGET,i.getBudget());
        db.insert(TABLE_ITEM,null,values);
        db.close();
    }

    public Item getItem (int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ITEM, new String[]{KEY_ID, KEY_STARTTIME, KEY_ENDTIME,
                        KEY_LOCATION, KEY_IDESCRIPTION, KEY_DESCRIPTION, KEY_BUDGET},KEY_ID + "=?",
                new String[] {String.valueOf(id)}, null, null, null, null);
        if(cursor!=null)
            cursor.moveToFirst();
        Item item = new Item();
        //Item item = new Item(cursor.getInt(0),cursor.getLong(1),cursor.getLong(2),
                //cursor.getString(3),cursor.getString(4),cursor.getLong(5),cursor.getInt(6));

        return item;
    }

    public List<Item> getItemByBudget (Budget budget) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Item> list = new ArrayList<Item>();

        int budgetID = budget.getId();
        String query = "SELECT * FROM" + TABLE_ITEM + " WHERE BUDGET = " + budgetID;
        Cursor cursor = db.rawQuery(query,null);
        if(cursor.moveToFirst()){
            do {
                Item item = new Item();
                //Item item = new Item(cursor.getInt(0),cursor.getLong(1),cursor.getLong(2),
                        //cursor.getString(3),cursor.getString(4),cursor.getLong(5),cursor.getInt(6));
                list.add(item);
            } while (cursor.moveToNext());
        }
        return list;
    }

    public List<Item> getItemByUser (int user) {
        List<Item> list = new ArrayList<Item>();
        return list;
    }

    public int editItem (Item i) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_STARTTIME, String.valueOf(i.getStartTime()));
        values.put(KEY_ENDTIME, String.valueOf(i.getEndTime()));
        values.put(KEY_LOCATION,i.getLocation());
        values.put(KEY_DESCRIPTION,i.getDescription());
        values.put(KEY_DURATION,i.getDuration());
        values.put(KEY_BUDGET,i.getBudget());
        return db.update(TABLE_ITEM, values, KEY_ID + " = ?",
                new String[] { String.valueOf(i.getId()) });
    }

    public void deleteItem (Item i){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ITEM, KEY_ID + " = ?",
                new String[] { String.valueOf(i.getId()) });
        db.close();
    }

    public double getSpentDuration (Budget b) {
        double d = 0;
        return d;
    }

    /***********************************************************************************
     USERS
     **********************************************************************************/

    public void addUser (User u) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_USERNAME,u.getUserName());
        values.put(KEY_EMAIL,u.getUserEmail());
        values.put(KEY_THUMB_URL,u.getUserPhoto());
    }

    public User getUser (int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USER, new String[]{KEY_ID, KEY_USERNAME, KEY_EMAIL,
                        KEY_THUMB_URL},KEY_ID + "=?",
                new String[] {String.valueOf(id)}, null, null, null, null);
        if(cursor!=null)
            cursor.moveToFirst();

        User u = new User(cursor.getInt(0),cursor.getString(1),cursor.getString(2),
                cursor.getString(3));
        return u;
    }

    public int editUser (User u) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_USERNAME,u.getUserName());
        values.put(KEY_EMAIL,u.getUserEmail());
        values.put(KEY_THUMB_URL,u.getUserPhoto());
        return db.update(TABLE_USER, values, KEY_ID + " = ?",
                new String[] { String.valueOf(u.getId()) });
    }

}
