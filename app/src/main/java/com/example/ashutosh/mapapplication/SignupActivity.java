package com.example.ashutosh.mapapplication;

/**
 * Created by Ashutosh on 27-08-2016.
 */
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword,inputName,inputAddress,inputMobile,UID;
    private Button btnSignIn, btnSignUp, btnResetPassword;
    private ImageView image;
    private ProgressBar progressBar;
    StorageReference mstorage;
    private FirebaseAuth auth;
    private FirebaseUser User;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    String imagename;
    private static final String FIREBASE_URL="https://mapapplication-141609.firebaseio.com/Users";
    //private static final String FIREBASE_URL1="https://mapapplication-141609.firebaseio.com/ProfilePics";
    private Firebase FireBaseRef,FireBaseRef1;
    private static int RESULT_LOAD_IMAGE=1;
    Uri selectedImage;
    String email;
    String Name;
    String Address;
    String Mobile,password,UID1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();
        Firebase.setAndroidContext(this);
        mstorage=FirebaseStorage.getInstance().getReference();
        FireBaseRef=new Firebase(FIREBASE_URL);
        //FireBaseRef1=new Firebase(FIREBASE_URL1);
        image=(ImageView) findViewById(R.id.image);
        btnSignIn = (Button) findViewById(R.id.sign_in_button);
        btnSignUp = (Button) findViewById(R.id.sign_up_button);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnResetPassword = (Button) findViewById(R.id.btn_reset_password);
        inputName=(EditText)findViewById(R.id.Name);
        inputAddress=(EditText)findViewById(R.id.Address);
        inputMobile=(EditText)findViewById(R.id.Mobileno);
        UID=(EditText)findViewById(R.id.UniqueID);

        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignupActivity.this, ResetPasswordActivity.class));
            }
        });

       image.setOnClickListener(new View.OnClickListener()
       {


           @Override
           public void onClick(View v) {
               Intent intent =new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.INTERNAL_CONTENT_URI);
               startActivityForResult(intent, RESULT_LOAD_IMAGE);
           }
       });


        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 Name=inputName.getText().toString().trim();
                 email = inputEmail.getText().toString().trim();
                 password = inputPassword.getText().toString().trim();
                  Mobile=inputMobile.getText().toString().trim();
                 Address=inputAddress.getText().toString().trim();
                 UID1=UID.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(Name)) {
                    Toast.makeText(getApplicationContext(), "Enter  your name please!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(Mobile)) {
                    Toast.makeText(getApplicationContext(), "Enter your mobile number please!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(Address)) {
                    Toast.makeText(getApplicationContext(), "Enter your  address please!", Toast.LENGTH_SHORT).show();
                    return;
                }


                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(image.getDrawable()==null)
                {
                    Toast.makeText(getApplicationContext(),"Please upload a image",Toast.LENGTH_SHORT).show();

                }

                if (password.length() < 6 || password.toString().matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{4,}$")) {
                    Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters and atleast 1 Capital letter and special charaacter!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (Mobile.length() < 10 || Mobile.length()>10 ||Mobile.toString().matches("[A-Z][a-z]+( [A-Z][a-z]+)*")) {
                    Toast.makeText(getApplicationContext(),"Please enter a valid Mobile number!", Toast.LENGTH_SHORT).show();
                    return;
                }




                Map info=new HashMap<>();
                info.put("Name",Name);
                info.put("Address",Address);
                info.put("Mobile", Mobile);
                info.put("Email", email);
                info.put("UniqueID",UID1);
                storeImageToFirebase();
                FireBaseRef.child(Mobile).push().setValue(info);


                progressBar.setVisibility(View.VISIBLE);
                //create user
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                User=FirebaseAuth.getInstance().getCurrentUser();
                                  User.sendEmailVerification();

                                Toast.makeText(SignupActivity.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    Toast.makeText(SignupActivity.this, "Authentication failed." + task.getException(),
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                                    Toast.makeText(getApplicationContext(), "User Registration was successful.Please login now!", Toast.LENGTH_LONG);
                                    finish();
                                }
                            }
                        });

            }
        });
    }







    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }

    private void storeImageToFirebase() {
        imagename="Picture.jpg";
        StorageReference filepath=mstorage.child(email).child("Images").child(imagename);

        filepath.putFile(selectedImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Toast.makeText(getApplicationContext(), "Upload Done", Toast.LENGTH_LONG).show();
            }
        });

      /*  BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 8; // shrink it down otherwise we will use stupid amounts of memory
        Bitmap bitmap = BitmapFactory.decodeFile(selectedImage.getPath(), options);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] bytes = baos.toByteArray();
        String base64Image = Base64.encodeToString(bytes, Base64.DEFAULT);

        // we finally have our base64 string version of the image, save it.
        FireBaseRef1.push().setValue(base64Image);
        System.out.println("Stored image with length: " + bytes.length);*/
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
             selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            ImageView imageView = (ImageView) findViewById(R.id.image);


            Bitmap bmp = null;
            try {
                bmp = getBitmapFromUri(selectedImage);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            imageView.setImageBitmap(bmp);

        }




    }
    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }

    private boolean hasImage(@NonNull ImageView view) {
        Drawable drawable = view.getDrawable();
        boolean hasImage = (drawable != null);

        if (hasImage && (drawable instanceof BitmapDrawable)) {
            hasImage = ((BitmapDrawable)drawable).getBitmap() != null;
        }

        return hasImage;
    }

}





