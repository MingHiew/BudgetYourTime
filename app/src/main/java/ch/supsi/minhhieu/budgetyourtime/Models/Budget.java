package ch.supsi.minhhieu.budgetyourtime.Models;

import android.support.annotation.Nullable;

import ch.supsi.minhhieu.budgetyourtime.Utils.RecurUtils;
import ch.supsi.minhhieu.budgetyourtime.Utils.RecurUtils.Recur;
/**
 * Created by acer on 18/07/2016.
 */
public class Budget {

    private int id;

    public String name;
    public int amount;
    public String recur;
    public String notes;

    public Budget() {
    }

    public Budget(String mName, int mAmount, String recur) {
        this.name = mName;
        this.amount = mAmount;
        this.recur = recur;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String mName) {
        this.name = mName;
    }

    public Recur getRecur() {
        return RecurUtils.createFromExtraString(recur);
    }



}
