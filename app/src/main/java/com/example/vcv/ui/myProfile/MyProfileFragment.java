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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.vcv.ForgotPasswordActivity;
import com.example.vcv.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class MyProfileFragment extends Fragment {

    private static int RESULT_LOAD_IMAGE_FROM_GALLERY = 1;
    private static int RESULT_LOAD_IMAGE_FROM_CAMERA = 0;
    private static final String DIR_IMAGE_NAME = "imageDir";
    private static final int MY_PERMISSIONS_CODE = 1;
    private Runnable waitingTaskForLoadImage = new Runnable() {
        public void run() {
            loadImageFromStorage();
        }
    };

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
