package ch.supsi.minhhieu.budgetyourtime.Helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import org.joda.time.DateTimeZone;
import org.joda.time.MutableDateTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import ch.supsi.minhhieu.budgetyourtime.Models.Budget;
import ch.supsi.minhhieu.budgetyourtime.Models.BudgetRecord;
import ch.supsi.minhhieu.budgetyourtime.Models.Item;
import ch.supsi.minhhieu.budgetyourtime.Utils.Period;
import ch.supsi.minhhieu.budgetyourtime.Utils.PeriodType;
import ch.supsi.minhhieu.budgetyourtime.Utils.RecurUtils;
import ch.supsi.minhhieu.budgetyourtime.Utils.RecurUtils.Recur;

/**
 * Created by acer on 18/07/2016.
 */
public class DBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public String LOG ="";
    private static final String TAG = "DatabaseHelper";
    public final static String DATABASE_NAME = "BudgetYourTime.sqlite";
    public final static String TABLE_ITEM = "ITEMS";
    public final static String TABLE_BUDGET = "BUDGETS";
    public final static String TABLE_BR = "BUDGETRECORDS";

    //Common keys
    public final static String KEY_ID = "id";
    public static final String _ID = "_id";

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

    //items
    public final static String KEY_BUDGET = "budget";
    public final static String KEY_DATE = "date";
    public final static String KEY_STARTTIME = "starttime";
    public final static String KEY_ENDTIME = "endtime";
    public final static String KEY_LOCATION = "location";
    public final static String KEY_IDESCRIPTION = "description";
    public final static String KEY_DURATION = "duration";
    public final static String CREATE_TABLE_ITEM ="CREATE TABLE " + TABLE_ITEM + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_DATE + " DATE,"
            + KEY_STARTTIME + " DATETIME,"
            + KEY_ENDTIME + " DATETIME,"
            + KEY_LOCATION + " TEXT,"
            + KEY_IDESCRIPTION + " TEXT,"
            + KEY_DURATION + " NUMERIC,"
            + KEY_BUDGET + " INTEGER,"
            + "FOREIGN KEY (" + KEY_BUDGET + ") REFERENCES " + TABLE_BUDGET + " (" + KEY_ID + ")"
            + ")";

    //budgetRecords
    public final static String KEY_STARTDATE = "startdate";
    public final static String KEY_ENDATE =  "enddate";
    public final static String KEY_SPENT = "spent";
    public final static String KEY_BALANCE = "balance";
    public volatile boolean updated = false;
    public final static String CREATE_TABLE_BR = "CREATE TABLE " + TABLE_BR + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_STARTDATE + " DATETIME,"
            + KEY_ENDATE + " DATETIME,"
            + KEY_BUDGET + " INTEGER,"
            + KEY_SPENT + " INTEGER DEFAULT 0,"
            + KEY_BALANCE + " INTEGER,"
            + "FOREIGN KEY (" + KEY_BUDGET + ") REFERENCES " + TABLE_BUDGET + " (" + KEY_ID + ")"
            + ")";

    //inner join
    String INNER_JOIN = TABLE_BR+" inner join "+TABLE_BUDGET+ " on BUDGETS.id = BUDGETRECORDS.budget";


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

        long budgetid = db.insert(TABLE_BUDGET,null,bugetValues);
        Recur recur = RecurUtils.createFromExtraString(b.recur);
        Period[] periods = RecurUtils.periods(recur);
        for (int i=0; i<periods.length; i++) {
            Period p = periods[i];
            BudgetRecord br = new BudgetRecord();
            ContentValues brValues = new ContentValues();
            br.budgetID = budgetid;
            br.startDate = p.start;
            br.endDate = p.end;
            br.balance = b.amount;
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
            budget = new Budget(cursor.getLong(0),cursor.getString(1),cursor.getInt(2),cursor.getString(3));

        return budget;
    }

    public List<Budget> getAllBudgets (){
        List<Budget> list = new ArrayList<Budget>();

        String query = "SELECT * FROM " + TABLE_BUDGET;
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query,null);
        if(cursor.moveToFirst()){
            do{
                Budget b = new Budget(cursor.getLong(0),cursor.getString(1),cursor.getInt(2),cursor.getString(3));

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

    public BudgetRecord getLatestInterval(Budget b){
        SQLiteDatabase db = this.getReadableDatabase();
        MutableDateTime dt = new MutableDateTime();
        long current = dt.getMillis();
        long id = b.getId();
        Cursor cursor = db.query(TABLE_BR,
                                new String[] {KEY_STARTDATE, KEY_ENDATE,KEY_SPENT,KEY_BALANCE},
                                KEY_BUDGET+"=? and "+KEY_STARTDATE+"<=? and "+KEY_ENDATE+">=?",
                new String[] {String.valueOf(id),String.valueOf(current),String.valueOf(current)}, null, null, null, null);
        if(cursor!=null)
            cursor.moveToFirst();
        BudgetRecord br = new BudgetRecord(cursor.getLong(0),cursor.getLong(1),cursor.getLong(2),cursor.getLong(3));
        return br;
    }

    public List<Period> getHistoricalWeeklyIntervals(){
        SQLiteDatabase db = this.getReadableDatabase();
        List<Period> list = new ArrayList<>();
        MutableDateTime dt = new MutableDateTime();
        long current = dt.getMillis();
        Cursor cursor = db.query(true,INNER_JOIN,
                new String[] {KEY_STARTDATE, KEY_ENDATE},
                KEY_STARTDATE+"<=?"+" and "+KEY_RECUR+" like ?",
                new String[] {String.valueOf(current),"WEEKLY%"}, null, null, KEY_ENDATE+" DESC", null);
        if(cursor.moveToFirst()) {
            do {
                Period p = new Period(PeriodType.CUSTOM, cursor.getLong(0), cursor.getLong(1));
                list.add(p);
            }
            while (cursor.moveToNext());
        }
        return list;
    }

    public List<Period> getHistoricalMonthlyIntervals(){
        SQLiteDatabase db = this.getReadableDatabase();
        List<Period> list = new ArrayList<>();
        MutableDateTime dt = new MutableDateTime();
        long current = dt.getMillis();
        Cursor cursor = db.query(true,INNER_JOIN,
                new String[] {KEY_STARTDATE, KEY_ENDATE},
                KEY_STARTDATE+"<=?"+" and "+KEY_RECUR+" like ?",
                new String[] {String.valueOf(current),"MONTHLY%"}, null, null, KEY_ENDATE+ " DESC", null);
        if(cursor.moveToFirst()) {
            do {
                Period p = new Period(PeriodType.CUSTOM, cursor.getLong(0), cursor.getLong(1));
                list.add(p);
            }
            while (cursor.moveToNext());
        }
        return list;
    }

    public List<Budget> getBudgetListByPeriod(long startDate, long endDate){
        SQLiteDatabase db = this.getReadableDatabase();
        List<Budget> list = new ArrayList<>();
        Cursor cursor = db.query(INNER_JOIN,
                new String[] {"BUDGETS.id", KEY_NAME, KEY_AMOUNT, KEY_RECUR},
                "BUDGETRECORDS.startdate=? and BUDGETRECORDS.enddate=?",
                new String[] {String.valueOf(startDate),String.valueOf(endDate)}, null, null, null, null);
        if(cursor.moveToFirst()){
            do{
                Budget b = new Budget(cursor.getLong(0),cursor.getString(1),cursor.getInt(2),cursor.getString(3));
                list.add(b);
            }
            while(cursor.moveToNext());
        }
        return list;
    }

    public BudgetRecord getBudgetRecordByInterval(Budget b, long startDate, long endDate){
        SQLiteDatabase db = this.getReadableDatabase();
        long id = b.getId();
        Cursor cursor = db.query(TABLE_BR,
                new String[] {KEY_STARTDATE, KEY_ENDATE,KEY_SPENT,KEY_BALANCE},
                KEY_BUDGET+"=?"+" and "+KEY_STARTDATE+"=?"+" and "+KEY_ENDATE+"=?",
                new String[] {String.valueOf(id),String.valueOf(startDate),String.valueOf(endDate)}, null, null, null, null);
        if(cursor!=null)
            cursor.moveToFirst();
        BudgetRecord br = new BudgetRecord(cursor.getLong(0),cursor.getLong(1),cursor.getLong(2),cursor.getLong(3));
        return br;
    }

    public void deleteBudget (Budget b){}

    /***********************************************************************************
     ITEMS
     **********************************************************************************/
    public boolean addItem (Item i){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues itemValues = new ContentValues();
        itemValues.put(KEY_DATE,i.getDate().getMillis());
        itemValues.put(KEY_STARTTIME, i.getStartTime());
        itemValues.put(KEY_ENDTIME, i.getEndTime());
        itemValues.put(KEY_LOCATION, i.getLocation());
        itemValues.put(KEY_IDESCRIPTION, i.getDescription());
        itemValues.put(KEY_DURATION, i.getDuration());
        itemValues.put(KEY_BUDGET,i.getBudget());
        db.insert(TABLE_ITEM,null,itemValues);
        boolean returnValue = true;
        long budgetID = i.getBudget();
        long date = i.getDate().getMillis();
        Cursor brcursor = db.query(TABLE_BR,
                new String[] {KEY_ID, KEY_STARTDATE, KEY_ENDATE,KEY_BUDGET, KEY_SPENT,KEY_BALANCE},
                KEY_BUDGET+"=?"+" and "+KEY_STARTDATE+"<=?"+" and "+KEY_ENDATE+">=?",
                new String[] {String.valueOf(budgetID),String.valueOf(date),String.valueOf(date)}, null, null, null, null);
        if(!brcursor.moveToFirst()){
            returnValue = false;
        } else {
            brcursor.moveToFirst();
            BudgetRecord br = new BudgetRecord(brcursor.getLong(0), brcursor.getLong(1),
                    brcursor.getLong(2), brcursor.getLong(3), brcursor.getLong(4), brcursor.getLong(5));
            Cursor bcursor = db.query(TABLE_BUDGET, new String[]{KEY_AMOUNT}, KEY_ID + "=?",
                    new String[]{String.valueOf(budgetID)},
                    null, null, null, null);
            if (bcursor != null)
                bcursor.moveToFirst();
            br.spent = br.spent + i.getDuration();
            ContentValues brValues = new ContentValues();
            br.balance = bcursor.getLong(0) - br.spent;
            brValues.put(KEY_SPENT, br.spent);
            brValues.put(KEY_BALANCE, br.balance);
            db.update(TABLE_BR, brValues, KEY_ID + "=?", new String[]{String.valueOf(br.getId())});

            db.close();
        }
        return returnValue;
    }

    public Item getItem (int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ITEM, new String[]{KEY_ID, KEY_DATE, KEY_STARTTIME, KEY_ENDTIME,
                        KEY_LOCATION, KEY_IDESCRIPTION, KEY_DURATION,KEY_BUDGET},KEY_ID + "=?",
                new String[] {String.valueOf(id)}, null, null, null, null);

        if(cursor!=null)
            cursor.moveToFirst();
        MutableDateTime dateTime = new MutableDateTime();
        dateTime.setTime(cursor.getLong(1));
        Item item = new Item(cursor.getInt(0),dateTime, cursor.getLong(2),cursor.getLong(3),
                cursor.getString(4),cursor.getString(5), cursor.getLong(6), cursor.getLong(7));

        return item;
    }

    public List<Item> getItemsByBudget (long budgetID) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Item> list = new ArrayList<Item>();
        Cursor cursor = db.query(TABLE_ITEM, new String[]{KEY_ID, KEY_DATE, KEY_STARTTIME, KEY_ENDTIME,
                        KEY_LOCATION, KEY_IDESCRIPTION, KEY_DURATION,KEY_BUDGET},KEY_BUDGET+ "=?",
                new String[] {String.valueOf(budgetID)}, null, null, KEY_DATE+ " DESC", null);
        if(cursor.moveToFirst()){
            do {
                MutableDateTime mDate = new MutableDateTime(cursor.getLong(1));
                Item item = new Item(cursor.getLong(0), mDate,cursor.getLong(2),
                        cursor.getLong(3),cursor.getString(4), cursor.getString(5), cursor.getInt(6),cursor.getLong(7));
                list.add(item);
            } while (cursor.moveToNext());
        }
        return list;
    }

    public Cursor getTimeRangeforItem(long budgetID, long itemDate){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_BR,
                new String[] {KEY_STARTDATE, KEY_ENDATE},
                KEY_BUDGET+"=?"+" and "+KEY_STARTDATE+"<=?"+" and "+KEY_ENDATE+">=?",
                new String[] {String.valueOf(budgetID),String.valueOf(itemDate),String.valueOf(itemDate)}, null, null, null, null);
        if(cursor!=null)
            cursor.moveToFirst();
        return cursor;
    }

    public boolean editItem (Item i) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        ContentValues brValues = new ContentValues();

        values.put(KEY_DATE,i.getDate().getMillis());
        values.put(KEY_STARTTIME, i.getStartTime());
        values.put(KEY_ENDTIME, i.getEndTime());
        values.put(KEY_LOCATION,i.getLocation());
        values.put(KEY_IDESCRIPTION,i.getDescription());
        values.put(KEY_DURATION,i.getDuration());
        values.put(KEY_BUDGET,i.getBudget());
        boolean returnValue = true;
        long itemID = i.getId();
        long budgetID = i.getBudget();
        long date = i.getDate().getMillis();
        Cursor brcursor = db.query(TABLE_BR,
                new String[] {KEY_ID, KEY_STARTDATE, KEY_ENDATE,KEY_BUDGET, KEY_SPENT,KEY_BALANCE},
                KEY_BUDGET+"=?"+" and "+KEY_STARTDATE+"<=?"+" and "+KEY_ENDATE+">=?",
                new String[] {String.valueOf(budgetID),String.valueOf(date),String.valueOf(date)}, null, null, null, null);
        if(!brcursor.moveToFirst()){
            returnValue = false;
        } else {
            brcursor.moveToFirst();
            BudgetRecord br = new BudgetRecord(brcursor.getLong(0), brcursor.getLong(1),
                    brcursor.getLong(2), brcursor.getLong(3), brcursor.getLong(4), brcursor.getLong(5));
            Cursor itemCursor = db.query(TABLE_ITEM, new String[]{KEY_DURATION},
                                KEY_ID+"=?",new String[]{String.valueOf(itemID)}, null, null, null, null);
            if(itemCursor!=null)itemCursor.moveToFirst();
            br.spent = br.spent + (i.getDuration() - itemCursor.getLong(0));
            br.balance = br.balance - (i.getDuration() - itemCursor.getLong(0));

            brValues.put(KEY_SPENT,br.spent);
            brValues.put(KEY_BALANCE,br.balance);
            db.update(TABLE_ITEM, values, KEY_ID + " = ?",
                    new String[] { String.valueOf(i.getId()) });
            db.update(TABLE_BR,brValues,KEY_ID+"=?",
                    new String[]{String.valueOf(brcursor.getLong(0))});
        }
        db.close();
        return returnValue;
    }

    public boolean deleteItem (Item i){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues brValues = new ContentValues();
        boolean returnValue = true;
        long itemID = i.getId();
        long budgetID = i.getBudget();
        long date = i.getDate().getMillis();
        Cursor brcursor = db.query(TABLE_BR,
                new String[] {KEY_ID, KEY_STARTDATE, KEY_ENDATE,KEY_BUDGET, KEY_SPENT,KEY_BALANCE},
                KEY_BUDGET+"=?"+" and "+KEY_STARTDATE+"<=?"+" and "+KEY_ENDATE+">=?",
                new String[] {String.valueOf(budgetID),String.valueOf(date),String.valueOf(date)}, null, null, null, null);
        if(!brcursor.moveToFirst()){
            returnValue = false;
        } else {
            brcursor.moveToFirst();
            BudgetRecord br = new BudgetRecord(brcursor.getLong(0), brcursor.getLong(1),
                    brcursor.getLong(2), brcursor.getLong(3), brcursor.getLong(4), brcursor.getLong(5));
            br.spent = br.spent - i.getDuration();
            br.balance = br.balance + i.getDuration();

            brValues.put(KEY_SPENT,br.spent);
            brValues.put(KEY_BALANCE,br.balance);
            db.delete(TABLE_ITEM, KEY_ID + " = ?",
                    new String[] { String.valueOf(itemID) });
            db.update(TABLE_BR,brValues,KEY_ID+"=?",
                    new String[]{String.valueOf(brcursor.getLong(0))});
        }
        db.close();
        return returnValue;
    }

}
