package ch.supsi.minhhieu.budgetyourtime.Models;

/**
 * Created by acer on 09/08/2016.
 */
public class BudgetRecord {

    private int id;
    public long startDate;
    public long endDate;
    public int budgetID;


    public long spent = 0;
    public long balance = 0;
    public volatile boolean updated = false;

    public BudgetRecord() {
    }

    public BudgetRecord(long startDate, long endDate, int budgetID) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.budgetID = budgetID;
    }
}
