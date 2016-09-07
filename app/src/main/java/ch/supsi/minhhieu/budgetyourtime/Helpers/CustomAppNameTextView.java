package ch.supsi.minhhieu.budgetyourtime.Helpers;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import ch.supsi.minhhieu.budgetyourtime.R;

/**
 * Created by acer on 07/09/2016.
 */
public class CustomAppNameTextView extends TextView{
        public CustomAppNameTextView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
            setType(context);
        }

        public CustomAppNameTextView(Context context, AttributeSet attrs) {
            super(context, attrs);
            setType(context);
        }

        public CustomAppNameTextView(Context context) {
            super(context);
            setType(context);
        }

        private void setType(Context context){
            this.setTypeface(Typeface.createFromAsset(context.getAssets(),
                    "KaushanScript-Regular.ttf"));

            this.setShadowLayer(1.5f, 5, 5, getContext().getResources().getColor(R.color.black));
        }

}
