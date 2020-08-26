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
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.thang.jointjourney.R;
import com.thang.jointjourney.Utils.FirebaseMethods;
import com.thang.jointjourney.Utils.SectionsStatePageAdapter;

public class RegisterStepOneFragment extends Fragment {


    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabse;
    private DatabaseReference mRef;
    private FirebaseMethods mFirebaseMethods;
    private String userID;

    //Fragment variables
    private SectionsStatePageAdapter pageAdapter;
    private ViewPager mViewPager;
    private RelativeLayout mRelativeLayout;

    //Interface variables
    private OnButtonClickListener mOnButtonClickListener;

    //widgets
    private Button mNextButton1;
    private ImageView mbackButton1;
    private EditText mEmail, mPassword;


    public interface OnButtonClickListener {
        void onButtonClicked(View view);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View mView = inflater.inflate(R.layout.fragment_register_one, container, false);

        //Firebase setup
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabse = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabse.getReference();
        mFirebaseMethods = new FirebaseMethods(getActivity());

        //instantiate objects
        mViewPager = mView.findViewById(R.id.container);
        mRelativeLayout = mView.findViewById(R.id.removeableLayout);

        mNextButton1 = mView.findViewById(R.id.nextBtn1);
        mEmail = mView.findViewById(R.id.emailStepOneEditText);
        mPassword = mView.findViewById(R.id.passwordStepOneEditText);

        mNextButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEmail.getText().length() > 0 && mPassword.getText().length() > 0) {
                    if (isValidEmailAddress(mEmail.getText().toString())) {
                        if (mPassword.getText().length() > 6) {
                            //Creates Account with email and password entered
                            mOnButtonClickListener.onButtonClicked(v);
                        } else {
                            Toast.makeText(mView.getContext(), "Mật khẩu phải lớn hơn 6 ký tự", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(mView.getContext(), "Sai địa chỉ Email.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(mView.getContext(), "Bạn phải điền đầy đủ thông tin.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mbackButton1 = mView.findViewById(R.id.loginBackArrow);
        mbackButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnButtonClickListener.onButtonClicked(v);
            }
        });

        return mView;
    }

    // check Email có đúng định dạng không
    public boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mOnButtonClickListener = (OnButtonClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(((Activity) context).getLocalClassName()
                    + "must implement OnButtonClickListener");
        }
    }

    public String getEmail() {
        return mEmail.getText().toString().trim();
    }

    public String getPassword() {
        return mPassword.getText().toString().trim();
    }


    /** --------------------------- Firebase ---------------------------- **/

    /***
     *  Setup the firebase object
     */
    @Override
    public void onStart() {
        super.onStart();
        // Check user có đăng nhập không .
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }
}


