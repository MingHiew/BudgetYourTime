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
    public long amount = 0;

    public BudgetRecord() {
    }

    public BudgetRecord(long mID, long startDate, long endDate, long budgetID,long spent,long balance, long amount) {
        this.id = mID;
        this.startDate = startDate;
        this.endDate = endDate;
        this.budgetID = budgetID;
        this.spent = spent;
        this.balance = balance;
        this.amount = amount;
    }

    public BudgetRecord(long startDate, long endDate, long budgetID, long spent,long balance, long amount) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.budgetID = budgetID;
        this.spent = spent;
        this.balance = balance;
        this.amount = amount;
    }

    public long getId() {
        return id;
    }

    public BudgetRecord(long startDate, long endDate, long spent, long balance, long amount) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.spent = spent;
        this.balance = balance;
        this.amount = amount;
    }
}
