package ch.supsi.minhhieu.budgetyourtime.Models;

import android.support.annotation.Nullable;

import ch.supsi.minhhieu.budgetyourtime.Utils.RecurUtils;
import ch.supsi.minhhieu.budgetyourtime.Utils.RecurUtils.Recur;
/**
 * Created by acer on 18/07/2016.
 */
public class Budget {

    private long id;

    public String name;
    public int amount;
    public String recur;
    public String notes;

    public Budget() {
    }

    public Budget(long id, String mName, int mAmount, String recur) {
        this.id = id;
        this.name = mName;
        this.amount = mAmount;
        this.recur = recur;
    }

    public Budget(String mName, int mAmount, String recur) {
        this.name = mName;
        this.amount = mAmount;
        this.recur = recur;
    }

    public long getId() {
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
