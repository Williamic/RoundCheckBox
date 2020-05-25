package com.qfzn.roundcheckbox;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatCheckBox;

/**
 * 创建者     王威拓
 * 创建时间   2019/12/13 16:50
 * 描述
 * 更新者     $
 * 更新时间   $
 * 更新描述
 */

public class RoundCheck extends AppCompatCheckBox {

    public RoundCheck(Context context) {
        this(context, null);
    }

    public RoundCheck(Context context, AttributeSet attrs) {
        this(context, attrs, androidx.appcompat.R.attr.radioButtonStyle);
    }

    public RoundCheck(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

}


