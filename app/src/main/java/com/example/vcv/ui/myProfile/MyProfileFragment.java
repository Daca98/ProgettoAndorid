package com.example.vcv.ui.myProfile;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.vcv.ForgotPasswordActivity;
import com.example.vcv.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;

public class MyProfileFragment extends Fragment {

    private MyProfileViewModel myProfileViewModel;
    private static int RESULT_LOAD_IMAGE = 1;
    private static final String DIR_IMAGE_NAME = "imageDir";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        myProfileViewModel =
                ViewModelProviders.of(this).get(MyProfileViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_myprofile, container, false);

        Button b_forgetPassword = root.findViewById(R.id.b_change_password);
        b_forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(root.getContext(), ForgotPasswordActivity.class);
                startActivity(myIntent);
            }
        });

        Button b_editProfilePicture = root.findViewById(R.id.b_edit_profile_picture);
        b_editProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });

        Button b_removeProfilePicture = root.findViewById(R.id.b_remove_profile_picture);
        b_removeProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeImageFromStorage();
            }
        });

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadImageFromStorage();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            try {
                Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();

                ImageView profilePicture = getActivity().findViewById(R.id.profile_picture);
                Bitmap bitmapImage = BitmapFactory.decodeFile(picturePath);
                profilePicture.setImageBitmap(bitmapImage);
                profilePicture.setBackgroundResource(0);

                saveToInternalStorage(bitmapImage);

            } catch (Exception e) {
                Toast.makeText(getContext(), getString(R.string.can_not_load_image), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

    // Utility
    private boolean saveToInternalStorage(Bitmap bitmapImage) {
        boolean saveOk = false;
        FileOutputStream fos = null;

        try {
            ContextWrapper cw = new ContextWrapper(getActivity().getApplicationContext());
            // path to /data/data/yourapp/app_data/ + dirName
            File directory = cw.getDir(DIR_IMAGE_NAME, Context.MODE_PRIVATE);
            // Creo imageDir
            File mypath = new File(directory, "profile.jpg");

            fos = new FileOutputStream(mypath);
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
            saveOk = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return saveOk;
    }

    private void loadImageFromStorage() {
        boolean hasToSetDefaultImage = true;

        try {
            ContextWrapper cw = new ContextWrapper(getActivity().getApplicationContext());
            File directory = cw.getDir(DIR_IMAGE_NAME, Context.MODE_PRIVATE);

            File f = new File(directory, "profile.jpg");
            if (f.exists()) {
                Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
                if (b != null) {
                    ImageView profilePicture = this.getActivity().findViewById(R.id.profile_picture);
                    profilePicture.setImageBitmap(b);
                    Button buttonClear = getActivity().findViewById(R.id.b_remove_profile_picture);
                    buttonClear.setVisibility(View.VISIBLE);
                    hasToSetDefaultImage = false;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if (hasToSetDefaultImage) {
            ImageView profilePicture = getActivity().findViewById(R.id.profile_picture);
            profilePicture.setBackgroundResource(R.drawable.user);
            Button buttonClear = getActivity().findViewById(R.id.b_remove_profile_picture);
            buttonClear.setVisibility(View.INVISIBLE);
        }
    }

    private void removeImageFromStorage() {
        try {
            ContextWrapper cw = new ContextWrapper(getActivity().getApplicationContext());
            File directory = cw.getDir(DIR_IMAGE_NAME, Context.MODE_PRIVATE);

            File f = new File(directory, "profile.jpg");
            if (f.delete()) {
                Toast.makeText(getContext(), getString(R.string.success_remove_image), Toast.LENGTH_SHORT).show();

                ImageView profilePicture = getActivity().findViewById(R.id.profile_picture);
                profilePicture.setImageBitmap(null);
                profilePicture.setBackgroundResource(R.drawable.user);
                Button buttonClear = getActivity().findViewById(R.id.b_remove_profile_picture);
                buttonClear.setVisibility(View.INVISIBLE);
            } else {
                Toast.makeText(getContext(), getString(R.string.error_remove_image), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
