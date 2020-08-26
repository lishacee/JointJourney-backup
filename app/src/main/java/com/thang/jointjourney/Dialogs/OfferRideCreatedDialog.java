package com.thang.jointjourney.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.thang.jointjourney.Account.HelpFragment;
import com.thang.jointjourney.R;
import com.thang.jointjourney.Rides.RidesActivity;
import com.thang.jointjourney.Utils.SectionsStatePageAdapter;


public class OfferRideCreatedDialog extends Dialog {

    private static final String TAG = "OfferRideCreatedDialog";
    public Context c;
    public Dialog d;
    private TextView cancelDialog;
    private Button confirmDialog;
    private SectionsStatePageAdapter pageAdapter;





    public interface onConfirmPasswordListener{
        public void onConfirmPassword(String password);
    }

    onConfirmPasswordListener mOnConfirmPassowrdListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_offer_ride_created);
        cancelDialog = (TextView) findViewById(R.id.dialogCancel);
        confirmDialog = (Button) findViewById(R.id.dialogConfirm);
        cancelDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        confirmDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Shows the ride has been created successfully
                dismiss();
                Intent intent = new Intent(c, RidesActivity.class);
                c.startActivity(intent);
//                ViewRideCreatedDialog dialog = new ViewRideCreatedDialog(c);
//                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                dialog.show();
            }
        });
    }

    public OfferRideCreatedDialog(Context a) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
    }


}
