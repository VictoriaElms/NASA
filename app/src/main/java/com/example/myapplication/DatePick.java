package com.example.myapplication;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.AsyncQueryHandler;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DatePick extends menuActivity implements View.OnClickListener {

    private DatePickerDialog datePickerDialog;
    private Button dateButton;
    private final List<getImage> imageList = new ArrayList<>();
    private Adapter adapter;
    private ProgressBar progressBar;
    private String yes, no, alertTitle, clearList, noElements;
    private Database dbHelper;
    private SQLiteDatabase db;
    private final ContentValues cValues = new ContentValues();

    Button clear;
    ImageView nasa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dateselector);


        setTitle(R.string.DatePickerPage);
        Load();

        initDatePicker();
        dateButton = findViewById(R.id.datePickerButton);
        dateButton.setText(getTodaysDate());

        ListView listView = findViewById(R.id.listView);
        adapter = new Adapter();
        listView.setAdapter(adapter);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        yes = getString(R.string.yes);
        no = getString(R.string.no);
        alertTitle = getString(R.string.alertTitle);

        dbHelper = new Database(this);
        db = dbHelper.getWritableDatabase();
        dbHelper.onCreate(db);

        loadData();

        listView.setOnItemClickListener((adapterView, view, pos, l) -> {

            getImage nasaImage = imageList.get(pos);

            Bundle b = new Bundle();
            b.putString("DATE", nasaImage.getDate());
            b.putString("TITLE", nasaImage.getTitle());
            b.putString("URL", nasaImage.getUrl());

            Intent intent = new Intent(this, empty.class);
            intent.putExtras(b);
            startActivity(intent);

        });

        listView.setOnItemLongClickListener((p, b, pos, id) -> {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);

            getImage nasaImage = imageList.get(pos);

            alert.setTitle(alertTitle)
                    .setMessage(nasaImage.getTitle())
                    .setPositiveButton(yes, (click, arg) -> {
                        deleteImage(nasaImage);
                        imageList.remove(nasaImage);
                        adapter.notifyDataSetChanged();
                    })
                    .setNegativeButton(no, (click, arg) -> {
                    })
                    .setView(getLayoutInflater().inflate(R.layout.alert, null))
                    .create()
                    .show();
            return true;
        });

        clearList = getString(R.string.clear);
        noElements = getString(R.string.noElements);
        clear = findViewById(R.id.clear_list);
        clear.setOnClickListener(click -> {
            if (imageList.size() > 0) {
                for (int i = 0; i < imageList.size(); i++) {
                    getImage image = imageList.get(i);
                    deleteImage(image);
                }
                imageList.clear();
                adapter.notifyDataSetChanged();
                Toast.makeText(this, clearList, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, noElements, Toast.LENGTH_SHORT).show();
            }

        });

    }

    private void initDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = (datePicker, year, month, day) -> {
            month = month + 1;
            String stringMonth = getMonthFormat(month);
            String date = makeDateString(year, stringMonth, day);
            String ymd = makeYMD(year, month, day);
            dateButton.setText(date);

            final String baseUrl = "https://api.nasa.gov/planetary/apod?api_key=";
            final String apiKey = "s1ZSk6sMnIZ8t01GBRChs3Xpn23a9nnAw032fo8z";
            String parseUrl = baseUrl + apiKey + "&date=" + ymd;
            Log.d("onDateSet()", parseUrl);
            NASA nasa = new NASA();
            nasa.execute(parseUrl);
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        month = month + 1;
        int day = cal.get(Calendar.DAY_OF_WEEK);
        int style = AlertDialog.THEME_HOLO_DARK;

        datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
    }
    private String getTodaysDate() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        month = month + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);

        return makeDateString(year, getMonthFormat(month), day);
    }

    private String makeDateString(int year, String month, int day) {
        return month + " " + day + " " + year;
    }

    private String makeYMD(int year, int month, int day) {

        return year + "-" + month + "-" + day;
    }

    private String getMonthFormat(int month) {
        switch (month) {
            case 1:
                return getString(R.string.JAN);
            case 2:
                return getString(R.string.FEB);
            case 3:
                return getString(R.string.MAR);
            case 4:
                return getString(R.string.APR);
            case 5:
                return getString(R.string.MAY);
            case 6:
                return getString(R.string.JUN);
            case 7:
                return getString(R.string.JUL);
            case 8:
                return getString(R.string.AUG);
            case 9:
                return getString(R.string.SEP);
            case 10:
                return getString(R.string.OCT);
            case 11:
                return getString(R.string.NOV);
            case 12:
                return getString(R.string.DEC);
        }
        return null;
    }

    public void openDatePicker(View view) {
        datePickerDialog.show();
    }

    @Override
    public void onClick(View view) {

    }

    class NASA extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setMax(50);
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... strings) {
            try {

                InputStream response = request(strings[0]);

                String result = parser(response);
                JSONObject obj = new JSONObject(result);

                String date = obj.getString("date");
                String url = obj.getString("hdurl");
                String title = obj.getString("title");

                Log.d("NASA", date);
                Log.d("NASA", url);
                Log.d("NASA", title);

                cValues.put(Database.COL_DATE, date);
                cValues.put(Database.COL_URL, url);
                cValues.put(Database.COL_TITLE, title);
                long newId = db.insert(Database.TABLE_NAME, null, cValues);

                for (int i = 0; i < 50; i++) {
                    try {
                        progressBar.incrementProgressBy(1);
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                getImage nasaImage = new getImage(date, url, title, newId);
                imageList.add(nasaImage);
            } catch (IOException e) {
                Log.d("NASA", "Issue with request/response");
                e.printStackTrace();
            } catch (JSONException e) {
                Log.d("NASA", "Issue parsing JSON");
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

            progressBar.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            progressBar.setProgress(0);
            progressBar.setVisibility(View.INVISIBLE);
            adapter.notifyDataSetChanged();
            super.onPostExecute(s);

        }
    }

    public InputStream request(String x) throws IOException {
        URL url = new URL(x);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        return connection.getInputStream();
    }

    public String parser(InputStream response) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(response));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }

    private class Adapter extends BaseAdapter {

        @Override
        public int getCount() {

            return imageList.size();
        }

        @Override
        public getImage getItem(int position) {

            return imageList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            View v = getLayoutInflater().inflate(R.layout.display_image, viewGroup, false);
            TextView title = v.findViewById(R.id.imageTitle);
            TextView date = v.findViewById(R.id.imageDate);
            ImageView image = v.findViewById(R.id.image);
            getImage nasaImage = getItem(position);
            title.setText(nasaImage.getTitle());
            date.setText(nasaImage.getDate());
            image.setImageURI(Uri.parse(nasaImage.getUrl()));
            return v;
        }
    }

    private void deleteImage(getImage i) {
        db.delete(Database.TABLE_NAME, Database.COL_ID + "= ?", new String[]{Long.toString(i.getId())});
    }

    private void loadData() {
        dbHelper = new Database(this);
        db = dbHelper.getWritableDatabase();

        String[] columns = {Database.COL_ID,
                Database.COL_DATE,
                Database.COL_URL,
                Database.COL_TITLE};

        Cursor cursor = db.query(false, Database.TABLE_NAME, columns,
                null, null, null, null, null, null);

        int idColumn = cursor.getColumnIndex(Database.COL_ID);
        int dateColumn = cursor.getColumnIndex(Database.COL_DATE);
        int urlColumn = cursor.getColumnIndex(Database.COL_URL);
        int titleColumn = cursor.getColumnIndex(Database.COL_TITLE);

        while (cursor.moveToNext()) {
            long id = cursor.getLong(idColumn);
            String date = cursor.getString(dateColumn);
            String url = cursor.getString(urlColumn);
            String title = cursor.getString(titleColumn);

            imageList.add(new getImage(date, url, title, id));
        }
        cursor.close();
    }


}