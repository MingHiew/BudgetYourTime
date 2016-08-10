package ch.supsi.minhhieu.budgetyourtime.Helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import ch.supsi.minhhieu.budgetyourtime.Models.Budget;
import ch.supsi.minhhieu.budgetyourtime.Models.BudgetRecord;
import ch.supsi.minhhieu.budgetyourtime.Models.Item;
import ch.supsi.minhhieu.budgetyourtime.Utils.Period;
import ch.supsi.minhhieu.budgetyourtime.Utils.RecurUtils;
import ch.supsi.minhhieu.budgetyourtime.Utils.RecurUtils.Recur;

/**
 * Created by acer on 18/07/2016.
 */
public class DBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;

    private static final String TAG = "DatabaseHelper";
    public final static String DATABASE_NAME = "BudgetYourTime.sqlite";
    public final static String TABLE_ITEM = "ITEMS";
    public final static String TABLE_BUDGET = "BUDGETS";
    public final static String TABLE_BR = "BUDGETRECORDS";

    //Common keys
    public final static String KEY_ID = "id";
    public static final String _ID = "_id";

    //items
    public final static String KEY_BR = "budgetrecord";
    public final static String KEY_DATE = "date";
    public final static String KEY_STARTTIME = "starttime";
    public final static String KEY_ENDTIME = "endtime";
    public final static String KEY_LOCATION = "location";
    public final static String KEY_IDESCRIPTION = "description";
    public final static String KEY_DURATION = "duration";
    public final static String CREATE_TABLE_ITEM ="CREATE TABLE " + TABLE_ITEM + "("
            + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_DATE + " DATE,"
            + KEY_STARTTIME + " DATETIME,"
            + KEY_ENDTIME + " DATETIME,"
            + KEY_LOCATION + " TEXT,"
            + KEY_IDESCRIPTION + " TEXT,"
            + KEY_DURATION + " NUMERIC,"
            + KEY_BR + " INTEGER,"
            + "FOREIGN KEY (" + KEY_BR + ") REFERENCES " + TABLE_BR + " (" + KEY_ID + ")"
            + ")";

    //budgets
    public final static String KEY_NAME = "name";
    public final static String KEY_AMOUNT = "amount";
    public final static String KEY_RECUR = "recur";
    public final static String CREATE_TABLE_BUDGET = "CREATE TABLE " + TABLE_BUDGET + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_NAME + " TEXT,"
            + KEY_AMOUNT + " INTEGER,"
            + KEY_RECUR + " TEXT"
            + ")";

    //budgetRecords
    public final static String KEY_STARTDATE = "startdate";
    public final static String KEY_ENDATE =  "enddate";
    public final static String KEY_BUDGET = "budget";
    public final static String KEY_SPENT = "spent";
    public final static String KEY_BALANCE = "balance";
    public volatile boolean updated = false;
    public final static String CREATE_TABLE_BR = "CREATE TABLE " + TABLE_BR + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_STARTDATE + " DATETIME,"
            + KEY_ENDATE + " DATETIME,"
            + KEY_BUDGET + " INTEGER,"
            + KEY_SPENT + " NUMERIC,"
            + KEY_BALANCE + " NUMERIC,"
            + "FOREIGN KEY (" + KEY_BUDGET + ") REFERENCES " + TABLE_BUDGET + " (" + KEY_ID + ")"
            + ")";

    //Singleton
    private static DBHelper instance;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version)
    {
        super(context, name, factory, version);
    }

    public static DBHelper getInstance(Context context){
        if(instance ==  null){
            instance = new DBHelper(context);
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_BUDGET);
        db.execSQL(CREATE_TABLE_BR);
        db.execSQL(CREATE_TABLE_ITEM);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BUDGET);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BR);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEM);
        onCreate(db);
    }

    /***********************************************************************************
     BUDGETS
     **********************************************************************************/
    public void addNewBudget(Budget b){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues bugetValues = new ContentValues();
        bugetValues.put(KEY_NAME, b.name);
        bugetValues.put(KEY_AMOUNT,b.amount);
        bugetValues.put(KEY_RECUR, b.recur);

        db.insert(TABLE_BUDGET,null,bugetValues);
        Recur recur = RecurUtils.createFromExtraString(b.recur);
        Period[] periods = RecurUtils.periods(recur);
        for (int i=0; i<periods.length; i++) {
            Period p = periods[i];
            BudgetRecord br = new BudgetRecord();
            ContentValues brValues = new ContentValues();
            br.budgetID = b.getId();
            br.startDate = p.start;
            br.endDate = p.end;
            brValues.put(KEY_BUDGET,br.budgetID);
            brValues.put(KEY_STARTDATE,br.startDate);
            brValues.put(KEY_ENDATE,br.endDate);
            db.insert(TABLE_BR,null,brValues);
        }

        db.close();
    }

    public Budget getBudget (int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Budget budget = new Budget();
        Cursor cursor = db.query(TABLE_BUDGET, new String[] {KEY_ID, KEY_NAME,
                         KEY_AMOUNT, KEY_RECUR}, KEY_ID + "=?",
                new String[] {String.valueOf(id)}, null, null, null, null);
        if(cursor!=null)
            cursor.moveToFirst();
            budget = new Budget(cursor.getString(1),cursor.getInt(2),cursor.getString(3));

        return budget;
    }

    public List<Budget> getAllBudgets (){
        List<Budget> list = new ArrayList<Budget>();

        String query = "SELECT * FROM " + TABLE_BUDGET;
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query,null);
        if(cursor.moveToFirst()){
            do{
                Budget b = new Budget(cursor.getString(1),cursor.getInt(2),cursor.getString(3));

                list.add(b);
            }
            while(cursor.moveToNext());
        }
        return list;
    }

    public int editBudget (Budget b){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, b.name);
        values.put(KEY_AMOUNT,b.amount);
        values.put(KEY_RECUR, b.recur);



        return db.update(TABLE_BUDGET, values, KEY_ID + " = ?",
                new String[] { String.valueOf(b.getId()) });
    }

    public Cursor getAllBudgetCursor(boolean adapter){
        SQLiteDatabase db = this.getWritableDatabase();

        String id = KEY_ID;
        if(adapter){
            id = KEY_ID +" "+_ID;
        }
        String columns = id+", "+KEY_NAME;
        String selectQuery = "SELECT " + columns + " FROM " + TABLE_BUDGET;
        return db.rawQuery(selectQuery, null);

    }

    public void deleteBudget (Budget b){}

    /***********************************************************************************
     ITEMS
     **********************************************************************************/
    public void addItem (Item i){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_DATE,i.getDate().getTimeInMillis());
        values.put(KEY_STARTTIME, i.getStartTime());
        values.put(KEY_ENDTIME, i.getEndTime());
        values.put(KEY_LOCATION, i.getLocation());
        values.put(KEY_IDESCRIPTION, i.getDescription());
        values.put(KEY_DURATION, i.getDuration());
        values.put(KEY_BUDGET,i.getBudget());
        db.insert(TABLE_ITEM,null,values);
        db.close();
    }

    public Item getItem (int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ITEM, new String[]{KEY_ID, KEY_DATE, KEY_STARTTIME, KEY_ENDTIME,
                        KEY_LOCATION, KEY_IDESCRIPTION, KEY_DURATION,KEY_BUDGET},KEY_ID + "=?",
                new String[] {String.valueOf(id)}, null, null, null, null);

        if(cursor!=null)
            cursor.moveToFirst();

        Calendar mDate = Calendar.getInstance();
        mDate.setTimeInMillis(cursor.getLong(1));
        Item item = new Item( mDate, cursor.getLong(2),cursor.getLong(3),
                cursor.getString(4),cursor.getString(5), cursor.getDouble(6), cursor.getInt(7));

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
                Calendar mDate = Calendar.getInstance();
                mDate.setTimeInMillis(cursor.getLong(1));
                Item item = new Item(mDate, cursor.getLong(2),cursor.getLong(3),
                        cursor.getString(4),cursor.getString(5), cursor.getDouble(6), cursor.getInt(7));
                list.add(item);
            } while (cursor.moveToNext());
        }
        return list;
    }



    public int editItem (Item i) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_DATE,i.getDate().getTimeInMillis());
        values.put(KEY_STARTTIME, i.getStartTime());
        values.put(KEY_ENDTIME, i.getEndTime());
        values.put(KEY_LOCATION,i.getLocation());
        values.put(KEY_IDESCRIPTION,i.getDescription());
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

}
