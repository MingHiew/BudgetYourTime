package ch.supsi.minhhieu.budgetyourtime;

import android.os.Bundle;

/**
 * Created by acer on 17/08/2016.
 */
public interface BudgetActions {
    void openItemListFragment(Bundle args);

    void updateBudget(int which, long budgetID);

    void createBudgetUpdateDialog(final int which,final long budgetID);

    void createBudgetDeleteDialog(final int which, final long budgetID);

    void updateBudgetFragment();

}
