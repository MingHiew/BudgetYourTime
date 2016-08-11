package ch.supsi.minhhieu.budgetyourtime.Models;

/**
 * Created by acer on 09/08/2016.
 */
public class BudgetRecord {

    private long id;
    public long startDate;
    public long endDate;
    public long budgetID;


    public long spent = 0;
    public long balance = 0;
    public volatile boolean updated = false;

    public BudgetRecord() {
    }

    public BudgetRecord(long mID, long startDate, long endDate, long budgetID,long spent,long balance) {
        this.id = mID;
        this.startDate = startDate;
        this.endDate = endDate;
        this.budgetID = budgetID;
        this.spent = spent;
        this.balance = balance;
    }

    public BudgetRecord(long startDate, long endDate, long budgetID, long spent,long balance) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.budgetID = budgetID;
        this.spent = spent;
        this.balance = balance;
    }

    public long getId() {
        return id;
    }

    public BudgetRecord(long startDate, long endDate, long spent, long balance) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.spent = spent;

        this.balance = balance;
    }
}
