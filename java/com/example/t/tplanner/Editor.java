package com.example.t.tplanner;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class Editor extends AppCompatActivity implements View.OnClickListener {

    private Typeface tf;

    private Button back, date, time, endDate, endTime, addContact;
    private TextView titleBar, startText, endText, contactLabel;

    private LinearLayout contactList;

    private EditText name, description;

    private MyCalendar calendar, endCal;

    private DBHandler db, dbEdit;
    private ChangeFont font;

    private int id = -1;
    private JSONObject values = null;

    private ArrayList<String> contact = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Intent i = getIntent();

        if (i.hasExtra("id")) {
            id = i.getIntExtra("id", -1);
        }

        font = new ChangeFont(getAssets());
        calendar = new MyCalendar(
                i.getIntExtra("day", 1),
                i.getIntExtra("month", 1),
                i.getIntExtra("year", 1)
        );
        endCal = new MyCalendar(calendar.getDay()+1,
                calendar.getMonthInt(),
                calendar.getYear());
        db = new DBHandler(this, "w");

        topBar();
        body();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setImageDrawable(getResources().getDrawable(R.drawable.check));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
                Snackbar.make(view, "Entry Saved", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 1000);
            }
        });

    }

    private void body() {

        getDBInfo();
        initializeBody();
        fillBody();
        bodyDesign();
        dateChanger();
        endDateChanger();
        timeChanger();
        endTimeChanger();
        getContacts();

    }

    private void getDBInfo() {

        if (id != -1) {
            dbEdit = new DBHandler(this, "r");
            values = dbEdit.getId(id);
        }

    }

    private void dateChanger() {

        final DatePickerDialog.OnDateSetListener dates = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.setDay(dayOfMonth);
                calendar.setMonth(month);
                calendar.setYear(year);

                date.setText(calendar.getDate());
            }

        };

        this.date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(Editor.this, dates, calendar.getYear(),
                        calendar.getMonthInt(),
                        calendar.getDay()).show();
            }
        });

    }

    private void timeChanger() {

        final TimePickerDialog.OnTimeSetListener times = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                calendar.setHour(hourOfDay);
                calendar.setMinute(minute);

                time.setText(calendar.getTime());
            }
        };

        this.time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(Editor.this, times, calendar.getHour(),
                        calendar.getMinute(), false).show();
            }
        });

    }

    private void endDateChanger() {

        final DatePickerDialog.OnDateSetListener dates = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                endCal.setDay(dayOfMonth);
                endCal.setMonth(month);
                endCal.setYear(year);

                endDate.setText(endCal.getDate());
            }

        };

        this.endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(Editor.this, dates, endCal.getYear(),
                        endCal.getMonthInt(),
                        endCal.getDay()).show();
            }
        });

    }

    private void endTimeChanger() {

        final TimePickerDialog.OnTimeSetListener times = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                endCal.setHour(hourOfDay);
                endCal.setMinute(minute);

                endTime.setText(endCal.getTime());
            }
        };

        this.endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(Editor.this, times, endCal.getHour(),
                        endCal.getMinute(), false).show();
            }
        });

    }

    private void getContacts() {

        contactList.removeAllViews();
        if (contact.size() > 0) {

            Uri uri = null;
            String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME};
            Cursor cursor = null;
            String name;
            for (int i = 0; i < contact.size(); i++) {
                uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(contact.get(i)));

                cursor = getContentResolver().query(uri, projection, null, null, null);

                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        name = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
                        contactList.addView(buildContact(name, i));
                    }
                }

            }

            cursor.close();

        }

        buildAddContactButton();

    }

    private LinearLayout buildContact(String name, int i) {

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        params.setMargins(0, 0,0, 10);

        LinearLayout container = new LinearLayout(this);
        TextView value = new TextView(this);
        Button delete = new Button(this);

        // container design
        container.setLayoutParams(params);
        container.setBackground(getDrawable(R.drawable.rect_radius));
        container.setOrientation(LinearLayout.HORIZONTAL);

        // textview design
        value.setText(name);
        value.setTextSize(15);
        value.setTypeface(font.light());
        value.setPadding(30, 30, 30, 30);

        LinearLayout.LayoutParams deleteParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        deleteParams.setMargins(0,20,-90, -20);
        delete.setLayoutParams(deleteParams);
        delete.setText(" ");
        delete.setTextSize(5);
        delete.setPadding(0, 30, 0, 30);
        delete.setBackground(getDrawable(R.drawable.delete));

        final int index = i;
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeContact(index);
            }
        });

        container.addView(value);
        container.addView(delete);

        return container;

    }

    private void removeContact(int i) {
        contact.remove(i);
        getContacts();
    }

    private void buildAddContactButton() {

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        addContact = new Button(this);

        tf = font.regular();
        addContact.setLayoutParams(params);
        addContact.setText("+");
        addContact.setTypeface(tf);
        addContact.setTextSize(25);
        addContact.setBackground(getDrawable(R.drawable.rect_radius));

        addContactListener();
        contactList.addView(addContact);

    }

    private void addContactListener() {

        addContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                pickContact.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                startActivityForResult(pickContact, 1);
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                Log.d("data = ", data+"");
                Uri contactData = data.getData();
                Cursor cursor = getContentResolver().query(contactData, null, null, null, null);
                if (cursor.moveToFirst()) {
                    int phoneNumber = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                    String num = cursor.getString(phoneNumber);
                    contact.add(num);
                    getContacts();
                }
            }
        }

    }

    private void save() {

        if (values ==  null) {
            db.add("title", name.getText().toString());
            db.add("startDate", calendar.getDate());
            db.add("endDate", endCal.getDate());
            db.add("startTime", calendar.getTime());
            db.add("endTime", endCal.getTime());
            db.add("description", description.getText().toString());

            String contactList = "";
            for (int i = 0; i < contact.size(); i++) {
                if (i == contact.size() - 1) {
                    contactList += contact.get(i);
                } else {
                    contactList += contact.get(i) + ",";
                }
            }

            db.add("contacts", contactList);

        } else {
            db.update(id, "title", name.getText().toString());
            db.update(id, "startDate", calendar.getDate());
            db.update(id, "endDate", endCal.getDate());
            db.update(id, "startTime", calendar.getTime());
            db.update(id, "endTime", endCal.getTime());
            db.update(id, "description", description.getText().toString());

            String contactList = "";
            for (int i = 0; i < contact.size(); i++) {
                if (i == contact.size() - 1) {
                    contactList += contact.get(i);
                } else {
                    contactList += contact.get(i) + ",";
                }
            }

            db.update(id, "contacts", contactList);

        }

        db.close();

    }

    private void initializeBody() {

        startText = (TextView) findViewById(R.id.startText);
        endText = (TextView) findViewById(R.id.endText);
        date = (Button) findViewById(R.id.dateEditor);
        time = (Button) findViewById(R.id.timeEditor);
        endDate = (Button) findViewById(R.id.endDateEditor);
        endTime = (Button) findViewById(R.id.endTimeEditor);
        name = (EditText) findViewById(R.id.nameEditor);
        description = (EditText) findViewById(R.id.descriptionEditor);
        contactLabel = (TextView) findViewById(R.id.contactLabelEditor);
        contactList = (LinearLayout) findViewById(R.id.contact_list_editor);

    }

    private void fillBody() {

        if (values != null) {

            try {
                name.setText(values.getString("title"));
                description.setText(values.getString("desc"));

                calendar = new MyCalendar(values.getString("startDate"));
                calendar.setTime(values.getString("time"));
                endCal = new MyCalendar(values.getString("endDate"));
                endCal.setTime(values.getString("endTime"));

                String list = values.getString("contacts");
                if (!list.equals("")) {
                    String[] listValues = list.split(",");
                    for (int i = 0; i < listValues.length; i++) {
                        contact.add(listValues[i]);
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    private void bodyDesign() {

        // background color
        date.setBackgroundColor(Color.TRANSPARENT);
        time.setBackgroundColor(Color.TRANSPARENT);
        endDate.setBackgroundColor(Color.TRANSPARENT);
        endTime.setBackgroundColor(Color.TRANSPARENT);

        // set text
        date.setText(calendar.getDate());
        time.setText(calendar.getTime());
        endDate.setText(endCal.getDate());
        endTime.setText(endCal.getTime());

        // type face
        tf = font.regular();
        startText.setTypeface(tf);
        endText.setTypeface(tf);
        date.setTypeface(tf);
        time.setTypeface(tf);
        endDate.setTypeface(tf);
        endTime.setTypeface(tf);
        name.setTypeface(tf);
        description.setTypeface(tf);
        tf = font.bold();
        contactLabel.setTypeface(tf);

        // text color
        startText.setTextColor(Color.BLACK);
        endText.setTextColor(Color.BLACK);
        contactLabel.setTextColor(Color.BLACK);

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

    }

    private void designTopBar() {

        back = (Button) findViewById(R.id.backEditor);
        titleBar = (TextView) findViewById(R.id.titleEditor);

        // colors
        back.setBackgroundColor(Color.TRANSPARENT);
        titleBar.setTextColor(Color.BLACK);

        // type face
        tf = font.light();
        back.setTypeface(tf);
        titleBar.setTypeface(tf);

    }

    @Override
    public void onClick(View v) {

    }
}
