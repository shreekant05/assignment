package com.shreekant.assignment.network;

import android.app.Dialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.shreekant.assignment.R;

public class InternetConnection {
    public static boolean checkConnection(Context context) {
        final ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetworkInfo = connMgr.getActiveNetworkInfo();

        if (activeNetworkInfo != null) { // connected to the internet
//            Toast.makeText(context, activeNetworkInfo.getTypeName(), Toast.LENGTH_SHORT).show();

            if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                // connected to wifi
                return true;
            } else if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                // connected to the mobile provider's data plan
                return true;
            }
        }
        return false;
    }


    public static void showNetworkDialog(Context context) {
        Dialog networkDialog = new Dialog(context);
        networkDialog.setContentView(R.layout.network_error);
        networkDialog.setCancelable(false);
        networkDialog.setCanceledOnTouchOutside(false);
        networkDialog.setTitle("Title...");

        TextView tvNetworkHeading = networkDialog.findViewById(R.id.tvNetworkHeading);
        TextView tvMessage = networkDialog.findViewById(R.id.tvMessage);
        ImageView imgClose = networkDialog.findViewById(R.id.imgClose);

        tvNetworkHeading.setText(context.getString(R.string.error));
        tvMessage.setText(context.getString(R.string.no_internet_connection));
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                networkDialog.dismiss();
            }
        });
        Window window = networkDialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
        window.setAttributes(wlp);
        networkDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        networkDialog.show();

    }


}
