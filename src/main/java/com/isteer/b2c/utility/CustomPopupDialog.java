package com.isteer.b2c.utility;

import android.app.Dialog;
import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.isteer.b2c.R;

public class CustomPopupDialog {

    private final EditText et_popup_edittext;
    private final Dialog dialog;
    myOnClickListenerLeft listenerLeft;
    myOnClickListenerRight listenerRight;
    myOnClickListenerThird listenerThird;

    public CustomPopupDialog(Context context, String title, String message, String edittext, String btn_left, String btn_right,
                             final myOnClickListenerLeft myonclicklistenerleft, final myOnClickListenerRight myOnClickListenerRight,
                             final myOnClickListenerThird myOnClickListenerThird) {
       dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_visitclick);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        this.listenerLeft = myonclicklistenerleft;
        this.listenerRight = myOnClickListenerRight;
        this.listenerThird = myOnClickListenerThird;
        TextView tv_title = dialog.findViewById(R.id.tv_title);
        TextView tv_message = dialog.findViewById(R.id.tv_message);
        LinearLayout lt_popup_editext = dialog.findViewById(R.id.lt_popup_editext);
        LinearLayout lt_third = dialog.findViewById(R.id.lt_third);
        LinearLayout lt_third_btn = dialog.findViewById(R.id.lt_third_btn);
        et_popup_edittext = dialog.findViewById(R.id.et_popup_edittext);
        LinearLayout lt_left_btn = dialog.findViewById(R.id.lt_left_btn);
        TextInputLayout til_popup_edittext = dialog.findViewById(R.id.til_popup_edittext);
        TextView tv_left_btn = dialog.findViewById(R.id.tv_left_btn);
        LinearLayout lt_right_btn = dialog.findViewById(R.id.lt_right_btn);
        TextView tv_right_btn = dialog.findViewById(R.id.tv_right_btn);
        if (title.equalsIgnoreCase("Cancel Visit")) {
            lt_popup_editext.setVisibility(View.GONE);
            lt_third.setVisibility(View.GONE);
        } else if (title.equalsIgnoreCase("Payment Mode")) {
            lt_popup_editext.setVisibility(View.GONE);
            lt_third.setVisibility(View.VISIBLE);
        } else if (title.equalsIgnoreCase("Map Bill")) {
            lt_popup_editext.setVisibility(View.GONE);
            lt_third.setVisibility(View.GONE);
        } else {
            lt_popup_editext.setVisibility(View.VISIBLE);
            lt_third.setVisibility(View.GONE);
        }
        tv_title.setText(title);
        tv_message.setText(message);
        til_popup_edittext.setHint(edittext);
        tv_left_btn.setText(btn_left);
        tv_right_btn.setText(btn_right);
        lt_left_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listenerLeft.onButtonClickLeft(et_popup_edittext.getText().toString());
                dialog.dismiss();
            }
        });

        lt_right_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listenerRight.onButtonClickRight(et_popup_edittext.getText().toString());
                dialog.dismiss();
            }
        });
        lt_third_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listenerThird.onButtonClickThird(et_popup_edittext.getText().toString());
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    public interface myOnClickListenerLeft {
        void onButtonClickLeft(String s);

    }

    public interface myOnClickListenerRight {
        void onButtonClickRight(String s);
    }

    public interface myOnClickListenerThird {
        void onButtonClickThird(String s);
    }
    public void dismissDialog(){
        if (dialog != null && dialog.isShowing()){
            dialog.dismiss();
        }
    }
}
