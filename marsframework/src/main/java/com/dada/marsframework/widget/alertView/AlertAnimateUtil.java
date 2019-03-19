package com.dada.marsframework.widget.alertView;

import android.view.Gravity;

import com.dada.marsframework.R;
import com.dada.marsframework.utils.SystemUtils;


/**
 * Created by laidayuan on 2018/3/23.
 */

public class AlertAnimateUtil {
    private static final int INVALID = -1;
    /**
     * Get default animation resource when not defined by the user
     *
     * @param gravity       the gravity of the dialog
     * @param isInAnimation determine if is in or out animation. true when is is
     * @return the id of the animation resource
     */
    static int getAnimationResource(int gravity, boolean isInAnimation) {
        if (SystemUtils.getSDKVersion() < 12) {
            return INVALID;
        }

        switch (gravity) {
            case Gravity.BOTTOM:
                return isInAnimation ? R.anim.slide_in_bottom : R.anim.slide_out_bottom;
            case Gravity.CENTER:
                return isInAnimation ? R.anim.fade_in_center : R.anim.fade_out_center;
        }
        return INVALID;
    }
}
