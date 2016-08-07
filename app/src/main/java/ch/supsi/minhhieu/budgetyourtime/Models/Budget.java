package ch.supsi.minhhieu.budgetyourtime.Models;

import android.content.Context;
import android.support.annotation.Nullable;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by acer on 18/07/2016.
 */
public class Budget {

    private int id;
    private String name;
    private int amount;
    private String notes;
    public int budgetType;
    public static final int Weekly = 0;
    public static final int Monthly = 1;

    public Budget() {
    }

    public static Budget constructWeeklyBudget(String mName, int mAmount) {

        Budget budget = new Budget();

        budget.setName(mName);
        budget.setAmount(mAmount);
        budget.budgetType = Weekly;

        return budget;
    }

    public static Budget constructMonthlyBudget(String mName, int mAmount) {

        Budget budget = new Budget();

        budget.setName(mName);
        budget.setAmount(mAmount);
        budget.budgetType = Monthly;

        return budget;
    }

    public Budget(int user, String mName, int mAmount,@Nullable String mNotes) {
        this.name = mName;
        this.amount = mAmount;
        this.notes = mNotes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String mName) {
        this.name = mName;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int mAmount) {
        this.amount = mAmount;
    }

    public int getBudgetType() {
        return budgetType;
    }

}
