package com.example.vcv.ui.myProfile;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.vcv.R;
import com.example.vcv.activity.ForgotPasswordActivity;
import com.example.vcv.utility.User;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class MyProfileFragment extends Fragment {

    private User user;
    private MyProfileViewModel myProfileViewModel;
    private static int RESULT_LOAD_IMAGE_FROM_GALLERY = 1;
    private static int RESULT_LOAD_IMAGE_FROM_CAMERA = 0;
    private static final String DIR_IMAGE_NAME = "imageDir";
    private static final int MY_PERMISSIONS_CODE = 1;
    private Runnable waitingTaskForLoadImage = new Runnable() {
        public void run() {
            loadImageFromStorage();
        }
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        MyProfileViewModel.context = this.getContext();
        myProfileViewModel =
                ViewModelProviders.of(this).get(MyProfileViewModel.class);

        final View root = inflater.inflate(R.layout.fragment_myprofile, container, false);

        Button b_changePassword = root.findViewById(R.id.b_change_password);
        b_changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(root.getContext(), ForgotPasswordActivity.class);
                myIntent.putExtra("isChangingPassword", true);
                startActivity(myIntent);
            }
        });

        final EditText etName = root.findViewById(R.id.et_name_user_data);
        final EditText etSurname = root.findViewById(R.id.et_surname_user_data);
        final EditText etPhone = root.findViewById(R.id.et_telephone_user_data);
        final EditText etEmail = root.findViewById(R.id.et_email_signin_user_data);
        myProfileViewModel.getUser().observe(getViewLifecycleOwner(), new Observer<User>() {
            @Override
            public void onChanged(@Nullable User u) {
                if (u != null) {
                    user = u;
                    etName.setText(u.name);
                    etSurname.setText(u.surname);
                    etPhone.setText(u.telephone);
                    etEmail.setText(u.email);
                }
            }
        });

        Button b_editProfilePicture = root.findViewById(R.id.b_edit_profile_picture);
        b_editProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check if there are permissions.
                // TRUE -> There are no permission about READ_EXTERNAL_STORAGE and WRITE_EXTERNAL_STORAGE
                // FALSE -> Permissions have already been setted
                if ((ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) ||
                        (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_CODE); // MY_PERMISSIONS_CODE is a value that we can define ourself in this class
                } else {
                    chooseImage();
                }
            }
        });

        Button b_removeProfilePicture = root.findViewById(R.id.b_remove_profile_picture);
        b_removeProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeImageFromStorage();
            }
        });

        Button b_saveChanges = root.findViewById(R.id.save_changes);
        b_saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveChanges();
            }
        });

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        Handler handler = new Handler();
        handler.postDelayed(waitingTaskForLoadImage, 10);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            if (resultCode != RESULT_CANCELED) {
                // requestCode == 0 -> take a photo
                // requestCode == 1 -> choose photo from gallery
                switch (requestCode) {
                    case 0:
                        if (requestCode == RESULT_LOAD_IMAGE_FROM_CAMERA && resultCode == RESULT_OK && data != null) {
                            Bitmap selectedImage = (Bitmap) data.getExtras().get("data");
                            new SaveImage(selectedImage).execute();
                        } else {
                            Toast.makeText(getContext(), getString(R.string.can_not_load_image), Toast.LENGTH_SHORT).show();
                            stopProgressBar();
                        }
                        break;
                    case 1:
                        if (requestCode == RESULT_LOAD_IMAGE_FROM_GALLERY && resultCode == RESULT_OK && data != null) {
                            Uri selectedImage = data.getData();
                            String[] filePathColumn = {MediaStore.Images.Media.DATA};

                            if (selectedImage != null) {
                                Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                                cursor.moveToFirst();

                                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                                String picturePath = cursor.getString(columnIndex);
                                cursor.close();

                                new SaveImage(BitmapFactory.decodeFile(picturePath)).execute();
                            } else {
                                Toast.makeText(getContext(), getString(R.string.can_not_load_image), Toast.LENGTH_SHORT).show();
                                stopProgressBar();
                            }
                        }
                        break;
                }
            } else {
                Toast.makeText(getContext(), getString(R.string.can_not_load_image), Toast.LENGTH_SHORT).show();
                stopProgressBar();
            }
        } catch (Exception e) {
            Toast.makeText(getContext(), getString(R.string.can_not_load_image), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            stopProgressBar();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        try {
            if (requestCode == MY_PERMISSIONS_CODE) {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    chooseImage();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveChanges() {
        String name = ((EditText) getActivity().findViewById(R.id.et_name_user_data)).getText() + "";
        String surname = ((EditText) getActivity().findViewById(R.id.et_surname_user_data)).getText() + "";
        String telephone = ((EditText) getActivity().findViewById(R.id.et_telephone_user_data)).getText() + "";

        if ((!name.equals(user.name) || !surname.equals(user.surname) || !telephone.equals(user.telephone)) && !name.equals("") && !surname.equals("") && !telephone.equals("")) {
            User newUser = new User(name, surname, telephone, user.badgeNumber, user.email);
            myProfileViewModel.writeNewDataInDB(newUser);
            myProfileViewModel.setUser(newUser);
            Toast.makeText(getContext(), getString(R.string.save_data_success), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), getString(R.string.change_field), Toast.LENGTH_SHORT).show();
        }
    }

    private class SaveImage extends AsyncTask<Void, Void, Void> {
        private Bitmap bitmapImage;

        public SaveImage(Bitmap bitmapImage) {
            super();
            this.bitmapImage = bitmapImage;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            FileOutputStream fos = null;

            try {
                ContextWrapper cw = new ContextWrapper(getActivity().getApplicationContext());
                // path to /data/data/yourapp/app_data/ + DIR_IMAGE_NAME
                File directory = cw.getDir(DIR_IMAGE_NAME, Context.MODE_PRIVATE);
                // Create a directory with the name specified in DIR_IMAGE_NAME
                File mypath = new File(directory, "profile.jpg");

                fos = new FileOutputStream(mypath);
                bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            loadImageFromStorage();
            stopProgressBar();
        }

    }

    // Utility
    private void chooseImage() {
        final CharSequence[] options = {getString(R.string.take_picture), getString(R.string.choose_from_gallery), getString(R.string.cancel)};
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getString(R.string.choose_profile_pic_title));

        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals(getString(R.string.take_picture))) {
                    startProgressBar();

                    Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePicture, RESULT_LOAD_IMAGE_FROM_CAMERA);
                } else if (options[item].equals(getString(R.string.choose_from_gallery))) {
                    startProgressBar();

                    Intent pickPhoto = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto, RESULT_LOAD_IMAGE_FROM_GALLERY);
                } else if (options[item].equals(getString(R.string.cancel))) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void setDefaultIcon() {
        try {
            ImageView profilePicture = getActivity().findViewById(R.id.profile_picture);
            profilePicture.setImageBitmap(null); // remove uploaded picture
            profilePicture.setBackgroundResource(R.drawable.user);
            Button buttonClear = getActivity().findViewById(R.id.b_remove_profile_picture);
            buttonClear.setVisibility(View.INVISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                    profilePicture.setBackgroundResource(0); // Remove the default icon
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
            setDefaultIcon();
        }
    }

    private void removeImageFromStorage() {
        try {
            ContextWrapper cw = new ContextWrapper(getActivity().getApplicationContext());
            File directory = cw.getDir(DIR_IMAGE_NAME, Context.MODE_PRIVATE);

            File f = new File(directory, "profile.jpg");
            if (f.delete()) {
                Toast.makeText(getContext(), getString(R.string.success_remove_image), Toast.LENGTH_SHORT).show();

                setDefaultIcon();
            } else {
                Toast.makeText(getContext(), getString(R.string.error_remove_image), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopProgressBar() {
        try {
            ProgressBar progressBar = getActivity().findViewById(R.id.progress_bar_image);
            progressBar.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startProgressBar() {
        try {
            ProgressBar progressBar = getActivity().findViewById(R.id.progress_bar_image);
            progressBar.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
