package com.thang.jointjourney.Register;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.thang.jointjourney.R;
import com.thang.jointjourney.Utils.FirebaseMethods;

public class RegisterStepTwoFragment extends Fragment {


    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabse;
    private DatabaseReference mRef;
    private FirebaseMethods mFirebaseMethods;
    private String userID;

    //Widgets
    private View mView;
    private EditText mUsernameStepTwoEditText;
    private ImageView mRestartRegistration, backButton2;

    private OnButtonClickListener mOnButtonClickListener;

    public interface OnButtonClickListener {
        void onButtonClicked(View view);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_register_two, container, false);

        //Firebase setup
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabse = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabse.getReference();
        mFirebaseMethods = new FirebaseMethods(getActivity());

        mUsernameStepTwoEditText = mView.findViewById(R.id.usernameStepTwoEditText);

        Button mNextBtn2 = mView.findViewById(R.id.nextBtn2);
        mNextBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUsernameStepTwoEditText.getText().length() > 0) {
                    mOnButtonClickListener.onButtonClicked(v);
                } else {
                    Toast.makeText(mView.getContext(), "Bạn phải nhập tên người dùng.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        backButton2 = mView.findViewById(R.id.loginBackArrowStep);
        backButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnButtonClickListener.onButtonClicked(v);
            }
        });

        mRestartRegistration = mView.findViewById(R.id.restartRegistrationBtn);
        mRestartRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnButtonClickListener.onButtonClicked(v);
            }
        });

        return mView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mOnButtonClickListener = (RegisterStepTwoFragment.OnButtonClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(((Activity) context).getLocalClassName()
                    + " must implement OnButtonClickListener");
        }
    }

    public String getUsername() {
        return mUsernameStepTwoEditText.getText().toString().trim().replaceAll("\\s+", "");
    }


    /** --------------------------- Firebase ---------------------------- **/
    /***
     *  Setup the firebase object
     */
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }
}
