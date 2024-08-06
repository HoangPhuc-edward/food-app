package com.example.foodapp.Fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.foodapp.Activity.MainActivity;
import com.example.foodapp.Activity.TestActivity;
import com.example.foodapp.Domain.User;
import com.example.foodapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserInfoFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private String address;

    private EditText editFirstName, editLastName, editPhoneNumber, editAddress;

    Spinner editPayment;
    ArrayAdapter<String> adapterItems;
    String[] items = {"Cash", "Debit card", "Credit card"};

    ImageView img;
    AppCompatButton imgBtn;

    String userAvatar;
    private ActivityResultLauncher<Intent> resultLauncher;


    public UserInfoFragment() {
        // Required empty public constructor
    }

    public UserInfoFragment(String address){
        this.address = address;
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserInfoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserInfoFragment newInstance(String param1, String param2) {
        UserInfoFragment fragment = new UserInfoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_user_info, container, false);


        editFirstName = fragmentView.findViewById(R.id.editFirstName);
        editLastName = fragmentView.findViewById(R.id.editLastName);
        editPhoneNumber = fragmentView.findViewById(R.id.editPhoneNumber);
        editAddress = fragmentView.findViewById(R.id.editAddress);
        editPayment = fragmentView.findViewById(R.id.paymentSp);


        img = fragmentView.findViewById(R.id.avatarImg);
        imgBtn = fragmentView.findViewById(R.id.avatarImgBtn);

        editAddress.setText(address);


        registerResult();

        adapterItems = new ArrayAdapter<String>(getActivity(), R.layout.list_item, items);
        adapterItems.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        editPayment.setAdapter(adapterItems);

        AppCompatButton btn = fragmentView.findViewById(R.id.confirmBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUserInfo();
                startActivity(new Intent(getContext(), MainActivity.class));
            }
        });

        imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickImage();
            }
        });

        return fragmentView;
    }


    private void pickImage(){
        Intent intent = new Intent(MediaStore.ACTION_PICK_IMAGES);
        resultLauncher.launch(intent);
    }

    private void registerResult(){
        resultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult o) {
                        try {
                            Uri imageUri = o.getData().getData();
                            img.setImageURI(imageUri);
                            uploadImageToFirebase(imageUri);
                        }
                        catch (Exception e){
                            Toast.makeText(getContext(), "Failed to upload img", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }

    private void uploadImageToFirebase(Uri file){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference ref = storage.getReference().child("avatar/" + file.getLastPathSegment());
        userAvatar = file.getLastPathSegment();

        UploadTask uploadTask = ref.putFile(file);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

            }
        });
    }


    private void updateUserInfo(){
        String firstName, lastName, phoneNumber, address, paymentMethod;

        firstName = editFirstName.getText().toString();
        lastName = editLastName.getText().toString();
        phoneNumber = editPhoneNumber.getText().toString();
        address = editAddress.getText().toString();
        paymentMethod = editPayment.getSelectedItem().toString();

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        User userInfo = new User(firstName, lastName, uid, userAvatar, phoneNumber, address, paymentMethod, 0);
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                String token = task.getResult();
                userInfo.setToken(token);
                FirebaseDatabase.getInstance().getReference("User").child(uid).setValue(userInfo);
            }
        });

    }


}