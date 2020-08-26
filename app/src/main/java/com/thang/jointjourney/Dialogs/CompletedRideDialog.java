package com.thang.jointjourney.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.thang.jointjourney.R;
import com.thang.jointjourney.Rides.RidesActivity;
import com.thang.jointjourney.Utils.FirebaseMethods;

public class CompletedRideDialog extends Dialog implements View.OnClickListener {
    private static final String TAG = "CompletedRideDialog";
    public Context c;
    public Dialog d;

    // variables
    private Button mConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_complete_ride);
        setupWidgets();
        mConfirm.setOnClickListener(this);
    }


    public CompletedRideDialog(@NonNull Context context) {
        super(context);
        this.c = context;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.dialogConfirm) {
            dismiss();
        }
        dismiss();
    }

    private void setupWidgets(){
        //Setup widgets
        mConfirm = (Button) findViewById(R.id.dialogConfirm);
    }
}
