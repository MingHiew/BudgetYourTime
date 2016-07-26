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
    private int user;
    private String name;
    private int amount;
    private int usedAmount;
    private String description;

    public enum Budget_Type {
        WEEKLY(0),
        MONTHLY(1);
        private int value;
        private static Map map = new HashMap<>();

        Budget_Type(int value) {
            this.value = value;
        }

        static {
            for (Budget_Type type : Budget_Type.values()) {
                map.put(type.value, type);
            }
        }

        public static Budget_Type valueOf (int type_value){
            return (Budget_Type) map.get(type_value);
        }

        public int getValue (){
            return value;
        }
    }

    public Budget_Type type;

    public Budget_Type getType() {
        return type;
    }

    public void setType(Budget_Type mType) {
        this.type = mType;
    }

    public Budget() {
    }

    public Budget(int id, int user, Budget_Type mType,String mName, int mAmount, @Nullable String mDescription) {
        this.id = id;
        this.user = user;
        this.type = mType;
        this.name = mName;
        this.amount = mAmount;
        this.description = mDescription;
    }

    public Budget(int user, String mName, int mAmount, String mDescription) {
        this.user = user;
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

    public int getUser() {
        return user;
    }

    public void setUser(int user) {
        this.user = user;
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

    public int getUsedAmount() {
        return usedAmount;
    }

    public void setUsedAmount(int mUsedAmount) {
        this.usedAmount = mUsedAmount;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String mDescription) {
        this.description = mDescription;
    }
}
