package denastya.thermostat.ui.Popups;

import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.widget.PopupWindow;

/**
 * Created by admin on 01.06.2015.
 */
public class BasePopup extends PopupWindow {


    protected View rootView;


    public BasePopup(int width, int height, View view) {
        super(width, height);
        rootView = view;
        setContentView(view);
        setBackgroundDrawable(new BitmapDrawable());
        setOutsideTouchable(true);
        setFocusable(true);

        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
    }


    public View getRootView() {
        return rootView;
    }

    public void onCreate() {

    }

}
