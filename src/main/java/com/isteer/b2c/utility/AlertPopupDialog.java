package com.isteer.b2c.utility;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.isteer.b2c.R;
import com.isteer.b2c.activity.B2CLancher.B2CLoginScreen;
import com.isteer.b2c.app.B2CApp;

public class AlertPopupDialog {

    public  static   Dialog dialog;
    myOnClickListenerLeft listenerLeft;
    myOnClickListenerRight listenerRight;

    public AlertPopupDialog(Context context, String title, String message,  String btn_left, String btn_right,
                            final myOnClickListenerLeft myonclicklistenerleft, final myOnClickListenerRight myOnClickListenerRight) {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.alert_screen);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

this.listenerLeft = myonclicklistenerleft;
this.listenerRight = myOnClickListenerRight;
        TextView tv_title =dialog.findViewById(R.id.tv_title);
        TextView tv_message =dialog.findViewById(R.id.tv_message);
        ImageView iv_icon =dialog.findViewById(R.id.iv_icon);
   LinearLayout lt_left_btn = dialog.findViewById(R.id.lt_left_btn);
   TextInputLayout til_popup_edittext = dialog.findViewById(R.id.til_popup_edittext);
   TextView tv_left_btn = dialog.findViewById(R.id.tv_left_btn);
   LinearLayout lt_right_btn = dialog.findViewById(R.id.lt_right_btn);
   TextView tv_right_btn = dialog.findViewById(R.id.tv_right_btn);
Logger.LogError("message",""+message);
   if (title.equalsIgnoreCase(context.getResources().getString(R.string.nointernet))){
       iv_icon.setImageResource(R.drawable.ic_signal_cellular_connected_no_internet_0_bar_black_24dp);
   }else {
       iv_icon.setImageResource(R.drawable.ic_transfer_within_a_station_black_24dp);
   }
        tv_title.setText(title);
        tv_message.setText(message);
        tv_left_btn.setText(btn_left);
        tv_right_btn.setText(btn_right);
        lt_left_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myonclicklistenerleft.onButtonClickLeft();
                dialog.dismiss();
            }
        });

        lt_right_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               myOnClickListenerRight.onButtonClickRight();

            }
        });

        dialog.show();

    }
    public interface myOnClickListenerLeft {
        void onButtonClickLeft();

    }public interface myOnClickListenerRight {
        void  onButtonClickRight();
    }
    public static void noInternetAlert(Activity activity,String message,myOnClickListenerRight myOnClickListenerRight) {

        String title = activity.getResources().getString(R.string.nointernet);
        String left_btn = activity.getResources().getString(R.string.cancel);
        String right_btn = activity.getResources().getString(R.string.retry);

        AlertPopupDialog alertPopupDialog = new AlertPopupDialog(activity, title, message, left_btn, right_btn,
                new AlertPopupDialog.myOnClickListenerLeft() {
                    @Override
                    public void onButtonClickLeft() {

                    }
                }, new AlertPopupDialog.myOnClickListenerRight() {
            @Override
            public void onButtonClickRight() {
                myOnClickListenerRight.onButtonClickRight();
            }
        });
    }
    public static void dialogDismiss(){
        if ((dialog != null) && (dialog.isShowing())){
            dialog.dismiss();
        }
    }

}
