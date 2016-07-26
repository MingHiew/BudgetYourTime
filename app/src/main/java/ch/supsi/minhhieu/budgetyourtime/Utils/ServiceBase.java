package ch.supsi.minhhieu.budgetyourtime.Utils;

import android.content.Context;

/**
 * Created by acer on 21/07/2016.
 */
public abstract class ServiceBase {

    public ServiceBase(Context context) {
//        mContext = context.getApplicationContext();
        mContext = context;
    }

    private Context mContext;

    public Context getContext() {
        return mContext;
    }
}