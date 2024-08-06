package com.example.foodapp.Fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.foodapp.Activity.IntroActivity;
import com.example.foodapp.Domain.User;
import com.example.foodapp.R;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private User currentUser;

    public UserFragment() {
        // Required empty public constructor
    }

    public UserFragment(User user){
        currentUser = user;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserFragment newInstance(String param1, String param2) {
        UserFragment fragment = new UserFragment();
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

    String address, paymentMethod, userName, phoneNumber;
    String userAvatar;

    ImageView avatarImg;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View fragmentView = inflater.inflate(R.layout.fragment_user, container, false);


        TextView userNameTxt = fragmentView.findViewById(R.id.userNameTxt);
        TextView phoneNumberTxt = fragmentView.findViewById(R.id.phoneNumberTxt);
        LinearLayout addressBtn = fragmentView.findViewById(R.id.addressBtn);
        LinearLayout paymentBtn = fragmentView.findViewById(R.id.paymentBtn);
        LinearLayout helpBtn = fragmentView.findViewById(R.id.helpBtn);
        LinearLayout settingBtn = fragmentView.findViewById(R.id.settingBtn);
        LinearLayout policyBtn = fragmentView.findViewById(R.id.policyBtn);
        AppCompatButton signOutBtn = fragmentView.findViewById(R.id.signOutBtn);
        avatarImg = fragmentView.findViewById(R.id.avatarImg);

        User user = currentUser;
        userAvatar = user.getImagePath();
        setAvatar();

        userName = user.getFirstName() + " " +  user.getLastName();
        phoneNumber = user.getPhoneNumber();
        userNameTxt.setText(userName);
        phoneNumberTxt.setText(phoneNumber);

        address = user.getAddress();
        paymentMethod = user.getMethod();

        addressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog("Address", address);
            }
        });

        paymentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog("Payment", paymentMethod);
            }
        });

        signOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getContext(), IntroActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });



        return fragmentView;
    }

    private void showDialog(String title, String msg){
        DialogFragment dialogFragment = new NotifyDialogFragment(title, msg);
        dialogFragment.show(getActivity().getSupportFragmentManager(), "notify");
    }

    private void setAvatar(){

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference ref = storage.getReference().child("avatar/" + userAvatar);

        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getContext()).load(uri.toString()).transform(new CenterCrop(), new RoundedCorners(30)).into(avatarImg);
            }
        }).addOnCanceledListener(new OnCanceledListener() {
            @Override
            public void onCanceled() {
                Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

}