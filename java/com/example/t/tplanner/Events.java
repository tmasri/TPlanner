package com.example.t.tplanner;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.Calendar;

public class Events extends AppCompatActivity {

    private Typeface tf;
    private ChangeFont font;

    private Button yesterday, today, tomorrow, clear, clearAll, pushOne, pushMulti;
    private LinearLayout eventHolder;
    private LinearLayout[] events;

    private MyCalendar calendar;
    private DBHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        font = new ChangeFont(getAssets());

        calendar = new MyCalendar();

        db = new DBHandler(this, "r");
//        db.deleteAll();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_plus));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(new Intent(Events.this, Editor.class));
                i.putExtra("day", calendar.getDay());
                i.putExtra("month", calendar.getMonthInt());
                i.putExtra("year", calendar.getYear());
                startActivity(i);
            }
        });

        topBar();
        body();

    }

    @Override
    protected void onRestart() {

        super.onRestart();
        body();
    }

    private void body() {

        eventHolder = (LinearLayout) findViewById(R.id.event_items);
        events = null;
        eventHolder.removeAllViews();

        TextView title, desc, time;
        String description;

        JSONObject[] values = db.get(calendar.getDate());
        if (values != null) {
            events = new LinearLayout[db.size(calendar.getDate())];

            for (int i = 0; i < values.length; i++) {
                try {
                    title = buildText(values[i].getString("title"));

                    description = values[i].getString("description");
                    if (description.length() > 11) {
                        description = description.substring(0, 8)+"...";
                    }
                    desc = buildText(description);
                    time = buildText(values[i].getString("time"));

                    events[i] = buildRow(values[i].getInt("id"));
                    events[i].addView(title);
                    events[i].addView(desc);
                    events[i].addView(time);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            for (int i = 0; i < events.length; i++) {
                if (events[i] != null) {
                    eventHolder.addView(events[i]);
                }
            }
        }



    }

    private LinearLayout buildRow(int id) {

        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        param.setMargins(0, 20, 0, 20);

        LinearLayout l = new LinearLayout(this);
        l.setLayoutParams(param);
        l.setBackground(getDrawable(R.drawable.rect));
        l.setElevation(2);
        l.setPadding(70, 70, 70, 70);

        final int ID = id;
        l.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Events.this, Viewer.class);
                i.putExtra("id", ID);
                startActivity(i);
            }
        });

        return l;

    }

    private TextView buildText(String s) {

        tf = font.regular();
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT,
                1
        );

        TextView v = new TextView(this);
        v.setText(s);
        v.setTypeface(tf);
        v.setTextSize(17);
        v.setLayoutParams(param);
        v.setGravity(1);
        return v;

    }

    private void topBar() {

        buildTopBar();
        designTopBar();
        onclickTopBar();

    }

    private void onclickTopBar() {


        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.setDay(dayOfMonth);
                calendar.setMonth(month);
                calendar.setYear(year);

                today.setText(calendar.getDate());
                body();

            }

        };

        today.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(Events.this, date, calendar
                        .getYear(), calendar.getMonthInt(),
                        calendar.getDay()).show();
            }
        });

        yesterday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.setDay(calendar.getDay() - 1);
                today.setText(calendar.getDate());
                body();
            }
        });

        tomorrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.setDay(calendar.getDay() + 1);
                today.setText(calendar.getDate());
                body();
            }
        });

        clearAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.deleteAll();
                body();
            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.deleteToday(calendar.getDate());
                body();
            }
        });

        pushOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pushOneDay();
            }
        });

        final DatePickerDialog.OnDateSetListener date2 = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                pushMultiDay(dayOfMonth, month, year);
            }

        };

        pushMulti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(Events.this, date2, calendar
                        .getYear(), calendar.getMonthInt(),
                        calendar.getDay()).show();

            }
        });

    }

    private void pushOneDay() {

        String currentDate = calendar.getDate();
        int dayOfWeek = calendar.getDayOfWeek();
        MyCalendar newCal = new MyCalendar(currentDate);

        // monday to thursday
        if (dayOfWeek >= 2 && dayOfWeek < 6 || dayOfWeek == 7) {
            // just add one to the date
            newCal.setDay(newCal.getDay()+2);
        } else if (dayOfWeek == 6) {
            // if its friday
            newCal.setDay(newCal.getDay()+4);
        } else if (dayOfWeek == 1) {
            // if its sunday
            newCal.setDay(newCal.getDay()+7);
        }

        changeDate(currentDate, newCal);

    }

    private void pushMultiDay(int d, int m, int y) {

        MyCalendar newCal = new MyCalendar(d,m,y);
        changeDate(calendar.getDate(), newCal);

    }

    private void changeDate(String currentDate, MyCalendar newCal) {

        JSONObject[] valueIds = null;
        valueIds = db.get(currentDate);
        if (valueIds != null) {
            int[] ids = new int[valueIds.length];

            for (int i = 0; i < valueIds.length; i++) {
                try {
                    ids[i] = valueIds[i].getInt("id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            Log.d("new start date = ", newCal.getDate());
            for (int i = 0; i < ids.length; i++) {
                db.update(ids[i], "startDate", newCal.getDate());
            }
            body();
        }

    }

    private void buildTopBar() {

        initializeTopBar();

        today.setText(calendar.getDate());

    }

    private void designTopBar() {

        // background
        tf = font.light();
        yesterday.setBackgroundColor(Color.TRANSPARENT);
        today.setBackgroundColor(Color.TRANSPARENT);
        tomorrow.setBackgroundColor(Color.TRANSPARENT);

        yesterday.setTypeface(tf);
        today.setTypeface(tf);
        tomorrow.setTypeface(tf);
        clearAll.setTypeface(tf);
        clear.setTypeface(tf);
        pushOne.setTypeface(tf);
        pushMulti.setTypeface(tf);

    }

    private void initializeTopBar() {

        yesterday = (Button) findViewById(R.id.yesterday);
        today = (Button) findViewById(R.id.today);
        tomorrow = (Button) findViewById(R.id.tomorrow);

        clear = (Button) findViewById(R.id.clearButton);
        clearAll = (Button) findViewById(R.id.clearAllButton);
        pushOne = (Button) findViewById(R.id.pushOne);
        pushMulti = (Button) findViewById(R.id.pushMulti);

    }

}
