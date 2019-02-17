package com.cemilecerenerdem.artbook_professional;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    static ArrayList<Bitmap> artImageList;

    //Oluşturulan menu klasörü içerisindeki addart menü xml dosyamı buraya tanımlıyorum.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_art, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.addart) {
            Intent intent = new Intent(getApplicationContext(), Main2Activity.class);
            intent.putExtra("info", "new"); //Yeni bir aktivity oluşturmaya gider.
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Listeyi tanımla ve adaptör ile verileri bağla
        listView = findViewById(R.id.listview);

        final ArrayList<String> artNameList = new ArrayList<String>();
        artImageList = new ArrayList<Bitmap>();

        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, artNameList);
        listView.setAdapter(arrayAdapter);


        //Provider üzerinden verileri al.
        String Url = "content://com.cemilecerenerdem.artbook_professional.ArtContentProvider";
        Uri artUri = Uri.parse(Url);

        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(artUri, null, null, null, "name");


        if (cursor != null) {

            while (cursor.moveToNext()) {
                artNameList.add(cursor.getString(cursor.getColumnIndex(ArtContentProvider.NAME)));
                byte[] bytes = cursor.getBlob(cursor.getColumnIndex(ArtContentProvider.IMAGE));
                Bitmap image = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                artImageList.add(image);

                arrayAdapter.notifyDataSetChanged();

            }
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), Main2Activity.class);
                intent.putExtra("info", "old"); // Eski aktivity içeriğini görmeye gider.
                intent.putExtra("name", artNameList.get(i)); //i = position
                intent.putExtra("position", i);
                startActivity(intent);

            }
        });

    }
}
