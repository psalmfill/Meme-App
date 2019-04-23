package com.samfieldhawb.memeapp;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private  static final int RESULT_LOAD_IMAGE = 1;
    EditText mTopTextInput,mBottomTextInput;
    TextView mTopText,mBottomText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBottomTextInput = findViewById(R.id.bottom_text_input);
        mTopTextInput = findViewById(R.id.top_text_input);
        mBottomText = findViewById(R.id.bottom_text);
        mTopText = findViewById(R.id.top_text);
    }

    public void addImage(View view){
        Intent intent = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,RESULT_LOAD_IMAGE);
    }
    public void tryMeme(View view){
        mBottomText.setText(mBottomTextInput.getText().toString().trim());
        mTopText.setText(mTopTextInput.getText().toString().trim());

        hideKeyboard(view);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RESULT_LOAD_IMAGE && resultCode== RESULT_OK && data !=null){
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage,filePathColumn,null,null,null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            ImageView imageView = findViewById(R.id.meme_image);
            Bitmap image = BitmapFactory.decodeFile(picturePath);
            imageView.setImageBitmap(image);
        }

    }
    public void hideKeyboard(View v){
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
    }

    public void saveImage(View view){
       Bitmap bitmap = getScreenShot(findViewById(R.id.meme));
       String filename = "meme" + System.currentTimeMillis()+".png";
       store(bitmap,filename);
    }
    public static Bitmap getScreenShot(View view){
        view.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);
        return bitmap;
    }

    public void store(Bitmap bitmap, String filename){
        String dirPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString();
        File dir = new File(dirPath);
        if(!dir.exists()){
            dir.mkdir();
        }
        File file = new File(dirPath,filename);
        try {
            FileOutputStream fos = null;
            fos =new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG,100,fos);
            fos.flush();
            fos.close();
            Toast.makeText(this,"File save Successfully",Toast.LENGTH_SHORT).show();
        }catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
