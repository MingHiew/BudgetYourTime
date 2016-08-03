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
    private long amount;
    private long usedAmount;
    private String description;
    public int budgetType;
    public static final int Weekly = 0;
    public static final int Monthly = 1;

    public Budget() {
    }

    public static Budget constructWeeklyBudget(String mName, long mAmount, long mUsedAmount, @Nullable String mDescription) {

        Budget budget = new Budget();

        budget.setName(mName);
        budget.setAmount(mAmount);
        budget.setUsedAmount(mUsedAmount);
        budget.setDescription(mDescription);
        budget.budgetType = Weekly;

        return budget;
    }

    public static Budget constructMonthlyBudget(String mName, long mAmount, long mUsedAmount, @Nullable String mDescription) {

        Budget budget = new Budget();

        budget.setName(mName);
        budget.setAmount(mAmount);
        budget.setUsedAmount(mUsedAmount);
        budget.setDescription(mDescription);
        budget.budgetType = Monthly;

        return budget;
    }

    public Budget(int user, String mName, int mAmount,@Nullable String mDescription) {
        this.name = mName;
        this.amount = mAmount;
        this.description = mDescription;
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

    public long getAmount() {
        return amount;
    }

    public void setAmount(long mAmount) {
        this.amount = mAmount;
    }

    public long getUsedAmount() {
        return usedAmount;
    }

    public void setUsedAmount(long mUsedAmount) {
        this.usedAmount = mUsedAmount;
    }

    public String getDescription() {
        return description;
    }

    public int getBudgetType() {
        return budgetType;
    }

    public void setDescription(String mDescription) {
        this.description = mDescription;
    }
}
