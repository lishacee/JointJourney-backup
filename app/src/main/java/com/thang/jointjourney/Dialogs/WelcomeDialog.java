package com.thang.jointjourney.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.thang.jointjourney.Account.HelpFragment;
import com.thang.jointjourney.R;
import com.thang.jointjourney.Utils.SectionsStatePageAdapter;

public class WelcomeDialog extends Dialog implements
        View.OnClickListener  {

    private static final String TAG = "WelcomeDialog";
    public Context c;
    public Dialog d;
    private TextView cancelDialog;
    private  Button confirmDialog;
    private SectionsStatePageAdapter pageAdapter;





    public interface onConfirmPasswordListener{
        public void onConfirmPassword(String password);
    }

    onConfirmPasswordListener mOnConfirmPassowrdListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_welcome);
        cancelDialog = (TextView) findViewById(R.id.dialogCancel);
        confirmDialog = (Button) findViewById(R.id.dialogConfirm);
        cancelDialog.setOnClickListener(this);
        confirmDialog.setOnClickListener(this);

    }

    public WelcomeDialog(Context a) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialogConfirm:
                dismiss();
                Intent intent1 = new Intent(c, HelpFragment.class);
                c.startActivity(intent1);
                Log.d(TAG, "dialogConfirm: After press.");
                break;
            case R.id.dialogCancel:
                dismiss();
                Log.d(TAG, "dialogCancel: After press.");
                break;
            default:
                break;
        }
        dismiss();
    }

}
