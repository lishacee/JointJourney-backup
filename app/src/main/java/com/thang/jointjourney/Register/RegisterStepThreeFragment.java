package com.thang.jointjourney.Register;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;

import com.thang.jointjourney.R;
import com.thang.jointjourney.Utils.FirebaseMethods;

import static android.app.Activity.RESULT_OK;

public class RegisterStepThreeFragment extends Fragment {

    //widgets
    private View mView;
    private Button mNextButton3;
    private ImageView mbackButton3, mRegistrationPicture, mRestartRegistration;
    private RadioGroup mGenderGroup;
    private RadioButton maleRadioButton, femaleRadioButton;
    private EditText mFullname, mMobileNumber, mDob, mWork, mBio;
    private String gender, imgURL;
    private Calendar mCalandar;
    private DatePickerDialog.OnDateSetListener date;


    //Profile picture vars
    private final int PICK_IMAGE_REQUEST = 71;
    private Uri filePath;
    private FirebaseStorage storage;
    private StorageReference storageReference;


    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabse;
    private DatabaseReference mRef;
    private FirebaseMethods mFirebaseMethods;
    private String userID;

    //Interface variables
    private OnButtonClickListener mOnButtonClickListener;

    public interface OnButtonClickListener {
        void onButtonClicked(View view);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_register_three, container, false);

        //Firebase setup
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabse = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabse.getReference();
        mFirebaseMethods = new FirebaseMethods(getActivity());
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        //instantiate objects
        mNextButton3 = mView.findViewById(R.id.nextBtn3);
        mDob = mView.findViewById(R.id.dobStepThreeEditText);
        mFullname = mView.findViewById(R.id.fullnameStepThreeEditText);
        mMobileNumber = mView.findViewById(R.id.mobileStepThreeEditText);
        mGenderGroup = mView.findViewById(R.id.genderToggle);
        maleRadioButton = mView.findViewById(R.id.femaleButton);
        femaleRadioButton = mView.findViewById(R.id.maleButton);
        mRegistrationPicture = mView.findViewById(R.id.registrationPicture);
        mWork = mView.findViewById(R.id.workEditTextStepThree);
        mBio = mView.findViewById(R.id.bioEditTextStepThree);

        mCalandar = Calendar.getInstance();
        date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                mCalandar.set(Calendar.YEAR, year);
                mCalandar.set(Calendar.MONTH, month);
                mCalandar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
        };

        mNextButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (maleRadioButton.isChecked() || femaleRadioButton.isChecked()) {
                    if (mDob.getText().length() > 0 && mFullname.getText().length() > 0 && mMobileNumber.getText().length() > 0 &&
                            mWork.getText().length() > 0 && mBio.getText().length() > 0) {
                        if (mMobileNumber.getText().length() >= 7) {
                            mOnButtonClickListener.onButtonClicked(v);
                        } else {
                            Toast.makeText(mView.getContext(), "Hãy nhập đúng số điện thoại", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(mView.getContext(), "Hãy điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(mView.getContext(), "Hãy chọn giới tính của bạn", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mbackButton3 = mView.findViewById(R.id.loginBackArrowStep);
        mbackButton3.setOnClickListener(new View.OnClickListener() {
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

        mRegistrationPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("STEP3", "onClick: adding profile photo");
                chooseImage();
            }
        });

        mDob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DATE, 0);

                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), date, mCalandar
                        .get(Calendar.YEAR), mCalandar.get(Calendar.MONTH),
                        mCalandar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
                datePickerDialog.show();
            }
        });

        return mView;
    }

    /**
     * ----------------------------------- UPLOADS IMAGE TO FIREBASE STORAGE --------------------------------------------
     **/
    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Chọn hình của bạn"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            filePath = data.getData();
            Log.i("STEP3", "onActivityResult: " + filePath);
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                mRegistrationPicture.setImageBitmap(bitmap);
                uploadImage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImage() {
        if (filePath != null) {
            final StorageReference ref = storageReference.child("profileImages/" + UUID.randomUUID().toString());
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Log.i("STEP3", "onActivityResult: Image uploaded" + uri);
                                    imgURL = uri.toString();
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), "Lỗi tải ảnh", Toast.LENGTH_SHORT).show();
                            Log.i("STEP3", "onActivityResult: failed to upload" + e.getMessage());
                        }
                    });
        }
    }

    /**
     * Interface attach on view show
     *
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mOnButtonClickListener = (RegisterStepThreeFragment.OnButtonClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(((Activity) context).getLocalClassName()
                    + " must implement OnButtonClickListener");
        }
    }

    private void updateLabel() {
        String dateFormat = "dd/MM/yy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.UK);

        mDob.setText(simpleDateFormat.format(mCalandar.getTime()));
    }


    public String getFullname() {
        return mFullname.getText().toString().trim();
    }

    public long getMobileNumber() {
        return Long.parseLong(mMobileNumber.getText().toString().trim());
    }

    public String getDob() {
        return mDob.getText().toString().trim();
    }

    public String getGender() {
        int whichIndex = mGenderGroup.getCheckedRadioButtonId();
        if (whichIndex == R.id.femaleButton) {
            return "Female";
        } else if (whichIndex == R.id.maleButton) {
            return "Male";
        }
        return "Not specified";
    }

    public String getRegistrationPicture() {
        return imgURL;
    }

    public String getWork() {
        return mWork.getText().toString().trim();
    }

    public String getBio() {
        return mBio.getText().toString().trim();
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
