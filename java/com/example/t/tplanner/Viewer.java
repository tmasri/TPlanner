package com.example.t.tplanner;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class Viewer extends AppCompatActivity {

    private Button back, delete;
    private TextView titleBar;

    private TextView dateTime, description, descLabel, contactLabel;

    private LinearLayout contactListViewer;

    private Typeface tf;
    private ChangeFont font;

    private DBHandler db;
    private JSONObject value;
    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewer);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Intent i = getIntent();
        id = i.getIntExtra("id", 0);

        font = new ChangeFont(getAssets());
        db = new DBHandler(this, "r");
        value = db.getId(id);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setImageDrawable(getResources().getDrawable(R.drawable.border_color));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Viewer.this, Editor.class);
                i.putExtra("id", id);
                startActivity(i);
            }
        });

        topBar();
        body();
        designText();

    }

    @Override
    protected void onRestart() {

        super.onRestart();
        value = db.getId(id);
        topBar();
        body();
    }

    private void body() {

        dateTime = (TextView) findViewById(R.id.dateTimeViewer);
        descLabel = (TextView) findViewById(R.id.descLabel);
        description = (TextView) findViewById(R.id.descriptionViewer);
        contactLabel = (TextView) findViewById(R.id.contactLabel);
        contactListViewer = (LinearLayout) findViewById(R.id.contactListViewer);
        contactListViewer.removeAllViews();

        String dateContent = "";
        String descContent = "";
        try {

            MyCalendar calendar = new MyCalendar(value.getString("startDate"));
            calendar.setDay(calendar.getDay()+1);
            dateContent += "Your meeting is on " + calendar.getDayOfWeekString()+" " + value.getString("startDate");
            dateContent += " at "+ value.getString("time");

            descContent = value.getString("desc");
            if (descContent.equals("")) {
                descContent = "There is no description for this event";
            }

            String contactString = value.getString("contacts");
            if (!contactString.equals("")) {
                String[] contacts = contactString.split(",");
                LinearLayout[] contactsList = getContacts(contacts);
                for (int i = 0; i < contactsList.length; i++) {
                    contactListViewer.addView(contactsList[i]);
                }

            } else {
                TextView noContact = new TextView(this);
                noContact.setText("There are no contacts for this event");

                tf = font.light();
                noContact.setTypeface(tf);
                noContact.setTextSize(20);
                noContact.setPadding(10, 0, 40, 40);
                contactListViewer.addView(noContact);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }


        dateTime.setText(dateContent);
        description.setText(descContent);

    }

    private LinearLayout[] getContacts(String[] contacts) {

        LinearLayout[] list = new LinearLayout[contacts.length];
        Uri uri = null;
        String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME};
        Cursor cursor = null;
        String name;

        for (int i = 0; i < contacts.length; i++) {

            uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(contacts[i]));
            cursor = getContentResolver().query(uri, projection, null, null, null);

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    name = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
                    list[i] = buildContact(name);
                }
            }

        }

        return list;

    }

    private LinearLayout buildContact(String name) {

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        params.setMargins(0, 0,0, 10);

        LinearLayout container = new LinearLayout(this);
        TextView value = new TextView(this);

        // container design
        container.setLayoutParams(params);
        container.setBackground(getDrawable(R.drawable.rect_radius));
        container.setOrientation(LinearLayout.HORIZONTAL);

        // textview design
        value.setText(name);
        value.setTextSize(15);
        value.setTypeface(font.light());
        value.setPadding(30, 30, 30, 30);

        container.addView(value);

        return container;

    }

    private void designText() {

        // box shadow
        dateTime.setBackground(getDrawable(R.drawable.rect));
        descLabel.setBackground(getDrawable(R.drawable.rect));
        description.setBackground(getDrawable(R.drawable.rect));
//        description.setElevation(1);
        contactLabel.setBackground(getDrawable(R.drawable.rect));

        // padding
        dateTime.setPadding(40, 60, 40, 40);
        descLabel.setPadding(40, 60, 40, 40);
        description.setPadding(40, 0, 40, 40);
        contactLabel.setPadding(40, 60, 40, 40);

        // text size
        dateTime.setTextSize(25);
        descLabel.setTextSize(25);
        description.setTextSize(20);
        contactLabel.setTextSize(23);

        // type face
        tf = font.regular();
        dateTime.setTypeface(tf);
        description.setTypeface(tf);
        tf = font.bold();
        descLabel.setTypeface(tf);
        contactLabel.setTypeface(tf);

    }

    private void topBar() {

        designTopBar();
        goHome();

    }

    private void goHome() {

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.deleteId(id);
                finish();
            }
        });

    }

    private void designTopBar() {

        tf = font.light();
        back = (Button) findViewById(R.id.backViewer);
        titleBar = (TextView) findViewById(R.id.titleViewer);
        delete = (Button) findViewById(R.id.deleteView);

        // colors
        back.setBackgroundColor(Color.TRANSPARENT);
        delete.setBackgroundColor(Color.TRANSPARENT);
        titleBar.setTextColor(Color.BLACK);

        // type face
        back.setTypeface(tf);
        titleBar.setTypeface(tf);
        delete.setTypeface(tf);

        // change title text
        try {
            titleBar.setText(value.getString("title"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}
