package com.isteer.b2c.picker;

import com.isteer.b2c.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;

public class StringPickerDialog extends DialogFragment {

    private OnClickListener mListener;
    private OnDismissListener mDismissListener;
    
    private Activity mActivity;

    @Override
    public void onAttach(Activity activity) {
        if (!(activity instanceof OnClickListener) || !(activity instanceof OnDismissListener)) {
            throw new RuntimeException("callback is must implements StringPickerDialog.OnClickListener!");
        }
        mListener = (OnClickListener) activity;
        mDismissListener = (OnDismissListener) activity;
        mActivity = activity;
        
        super.onAttach(activity);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = mActivity.getLayoutInflater();
        View view = inflater.inflate(R.layout.string_picker_dialog, null, false);

        final StringPicker stringPicker = (StringPicker) view.findViewById(R.id.string_picker);
        final Bundle params = getArguments();
        if (params == null) {
            throw new RuntimeException("params is null!");
        }

        final int optype = params.getInt(getString(R.string.string_picker_type, "type"));
        final String[] values = params.getStringArray(getValue(R.string.string_picker_dialog_values));
        stringPicker.setValues(values);
        
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity, AlertDialog.THEME_HOLO_LIGHT);
        builder.setTitle(params.getString(getString(R.string.string_picker_title, "Select")));
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mListener.onClick(true, stringPicker.getCurrentValue(), optype);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mListener.onClick(false, null, optype);
            }
        });
        builder.setView(view);
        return builder.create();
    }

    @Override
    public void onDismiss(final DialogInterface dialog) {
        super.onDismiss(dialog);
        
    	mDismissListener.onDismiss(0);
    }
    
    private String getValue(final int resId) {
        return mActivity.getString(resId);
    }

    public interface OnClickListener {
        void onClick(final boolean isPsitive, final String value, final int type);
    }
    
    public interface OnDismissListener {
        void onDismiss(final int type);
    }

}