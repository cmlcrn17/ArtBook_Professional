package com.cemilecerenerdem.artbook_professional;

import android.Manifest;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Main2Activity extends AppCompatActivity {

    ImageView imageView;
    EditText editText;
    Button btn_update, btn_delete, btn_save;

    String firstName;
    Bitmap selectedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        imageView = findViewById(R.id.imageView);
        editText = findViewById(R.id.editText);

        btn_update = findViewById(R.id.btn_update);
        btn_delete = findViewById(R.id.btn_delete);
        btn_save = findViewById(R.id.btn_save);


        Intent intent = getIntent();
        String info = intent.getStringExtra("info");

        if (info.matches("new")) {
            editText.setText("");
            btn_save.setVisibility(View.VISIBLE);     //Gizle
            btn_delete.setVisibility(View.INVISIBLE); //Göster
            btn_update.setVisibility(View.INVISIBLE); //Göster
        } else {

            String name = intent.getStringExtra("name");
            editText.setText(name);
            firstName = name;
            int position = intent.getIntExtra("position", 0);
            imageView.setImageBitmap(MainActivity.artImageList.get(position));

            btn_save.setVisibility(View.INVISIBLE);     //Göster
            btn_delete.setVisibility(View.VISIBLE); //Gizle
            btn_update.setVisibility(View.VISIBLE); //Gizle
        }

    }


    public void saveRecord(View view) {

        String artName = editText.getText().toString();

        //Resmi al. bytes array içerisine at.
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        selectedImage.compress(Bitmap.CompressFormat.PNG, 50, outputStream);
        byte[] bytes = outputStream.toByteArray();

        ContentValues contentValues = new ContentValues();
        contentValues.put(ArtContentProvider.NAME, artName); //ContentProvider içerisinde oluşturduğum NAME alanını key olarak belirliyorum.
        contentValues.put(ArtContentProvider.IMAGE, bytes); //ContentProvider içerisinde oluşturduğum NAME alanını key olarak belirliyorum.

        getContentResolver().insert(ArtContentProvider.CONTENT_URI, contentValues);

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);

    }

    public void updateRecord(View view) {

        String artName = editText.getText().toString();

        //Resmi al. bytes array içerisine at.
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        bitmap.compress(Bitmap.CompressFormat.PNG, 50, outputStream);
        byte[] bytes = outputStream.toByteArray();


        ContentValues contentValues = new ContentValues();
        contentValues.put(ArtContentProvider.NAME, artName);
        contentValues.put(ArtContentProvider.IMAGE, bytes);

        String[] selectedArg = {firstName};
        getContentResolver().update(ArtContentProvider.CONTENT_URI, contentValues, "name=?", selectedArg);

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    public void deleteRecord(View view) {

        String recordName = editText.getText().toString();
        String[] selectionArg = {recordName};
        getContentResolver().delete(ArtContentProvider.CONTENT_URI, "name=?", selectionArg);
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        Toast.makeText(getApplicationContext(), "Silme işlemi başarılı", Toast.LENGTH_LONG);
    }

    public void selectImage(View view) {
        //Resim okuma izni var mı?
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1); //Resim okuma izni yoksa, ister.
        } else {
            //Resim okuma izni varsa,
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 2);
        }
    }


    //selectImage voidi eğer izin verildi döndürürse buraya gelecek.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 2);
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        //İzin işlemleri olumlu olarak geri dönüyorsa...
        if (requestCode == 2 && resultCode == RESULT_OK && data != null) {

            Uri image = data.getData();
            try {
                selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), image); //Seçili görsel içerisine image yükler.
                imageView.setImageBitmap(selectedImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
