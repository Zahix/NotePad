package com.example.zahid.notepad;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    EditText addText;
    Button btnSave , btnOpen;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE };
        ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);

        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
        {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            context = this;

            addText = (EditText)findViewById(R.id.Ed_text);
            btnSave = (Button)findViewById(R.id.btn_save);
            btnOpen = (Button)findViewById(R.id.btn_openFile) ;

            addText.setSelection(addText.getText().length());



            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                    LayoutInflater inflater = getLayoutInflater();

                    View content = inflater.inflate(R.layout.alertdialog, null);

                    builder1.setView(content);

                    AlertDialog alertDialog = builder1.create();

                    final EditText dialogFileName = (EditText) content.findViewById(R.id.dialog_name_ET);
                    Button  dialogSubmit = (Button)content.findViewById(R.id.dialog_submit_btn);

                    alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

                    dialogSubmit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            WriteFile(context,dialogFileName.getText().toString()+".txt",addText.getText().toString());
                        }
                    });

                    alertDialog.show();
                }
            });

            btnOpen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Openfile();
                }
            });
        }

    }

    void WriteFile(Context context, String sFileName, String sBody){
        try {
            //Environment.getExternalStorageState();
            File root = new File(Environment.getExternalStorageDirectory(), "Notes");
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, sFileName);
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(sBody);
            writer.flush();
            writer.close();
            Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    void Openfile(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");
        try{
            startActivityForResult(intent, 2);
        } catch (ActivityNotFoundException e){
            Toast.makeText(MainActivity.this, "There are no file explorer clients installed.", Toast.LENGTH_SHORT).show();
        }
    }

    String result = null;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);



        // check if the request code is same as what is passed  here it is 2
        if(requestCode==2)
        {

            getFileName(data.getData());
            //Uri contactUri = data.getData();


            //Find the directory for the SD Card using the API
//*Don't* hardcode "/sdcard"
           // File sdcard = Environment.getExternalStorageDirectory();

//            File myfile = new File(contactUri.getPath()) ;
//            String path = myfile.getAbsolutePath();

//Get the text file
           // File file = new File(sdcard, result.toString());

//Read text from file
            StringBuilder text = new StringBuilder();

            try {
                BufferedReader br = new BufferedReader(new FileReader("/storage/emulated/0/Notes/"+result));
                String line;

                while ((line = br.readLine()) != null) {
                    text.append(line);
                    text.append('\n');
                }
                br.close();
            }
            catch (IOException e) {
                //You'll need to add proper error handling here
            }

////Find the view by its id
//            TextView tv = (TextView)findViewById(R.id.text_view);
//
////Set the text
//            tv.setText(text.toString());


           addText.setText(text.toString());
//            String message=data.getStringExtra("MESSAGE");
//            textView1.setText(message);
        }
    }

    public String getFileName(Uri uri) {

        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }
}
