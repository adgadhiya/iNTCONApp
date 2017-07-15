package com.myapp.unknown.iNTCON.InstRequest;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.myapp.unknown.iNTCON.OtherClasses.GlobalNames;
import com.myapp.unknown.iNTCON.OtherClasses.NetworkConnectionCheck;
import com.myapp.unknown.iNTCON.Preferences.AllPreferences;
import com.myapp.unknown.iNTCON.R;
import com.myapp.unknown.iNTCON.SignIn.MainActivity;
import com.myapp.unknown.iNTCON.ViewHolders.FieldHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CoursesOffered extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EditText uniField;
    private String key;

    private DatabaseReference reference;
    private  String group_key;

    SharedPreferences sp;


    //////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////ON CREATE ACTIVITY////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(FirebaseAuth.getInstance().getCurrentUser() == null)
        {
            startActivity(new Intent(this, MainActivity.class));
        }

        setContentView(R.layout.activity_courses_offered);

        recyclerView = (RecyclerView) findViewById(R.id.course_rv);
        uniField = (EditText)findViewById(R.id.course_name);
        Button btnAdd = (Button) findViewById(R.id.course_button);

        Toolbar toolbar = (Toolbar) findViewById(R.id.course_toolbar);
        setSupportActionBar(toolbar);

        sp = getSharedPreferences(AllPreferences.getPreferenceName(),MODE_PRIVATE);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        group_key = getIntent().getStringExtra(getString(R.string.group_id_intent));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        reference = FirebaseDatabase.getInstance().getReference()
                .getRoot().child(GlobalNames.getGroupDetail()).child(GlobalNames.getCoursesOffered())
                .child(group_key);

        final NetworkConnectionCheck connectionCheck = new NetworkConnectionCheck(this);

        //////////////////////////////////////////////////////////////////////////////////////////////////////
        ///////WHEN ADD BUTTON SELECTED (ADD COURSES TO DATABASE)////////////////////////////////////////////
        //////////////////////////////////////////////////////////////////////////////////////////////////////
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(connectionCheck.checkingConnextion()) {
                    Snackbar.make(v, "No Internet Connection", Snackbar.LENGTH_LONG)
                            .setAction("OK", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                                    startActivity(intent);
                                }
                            })
                            .setActionTextColor(Color.YELLOW)
                            .show();
                    return;
                }

                if(uniField.getText().toString().isEmpty())
                {
                    Toast.makeText(CoursesOffered.this, "Field Name can not be Empty.", Toast.LENGTH_SHORT).show();
                    return;
                }

                key = reference.push().getKey();

                FieldDetail fieldDetail = new FieldDetail();
                fieldDetail.setFieldName(uniField.getText().toString().toUpperCase());
                fieldDetail.setKey(key);

                reference.child(key).setValue(fieldDetail);

                uniField.setText("");
            }
        });

        setRecyclerView();
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////ON OPTION ITEM SELECTED///////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////SET RECYCLER VIEW///////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    private void setRecyclerView(){

        FirebaseRecyclerAdapter<FieldDetail,FieldHolder> adapter = new FirebaseRecyclerAdapter<FieldDetail,FieldHolder>(
                FieldDetail.class,
                R.layout.field_list,
                FieldHolder.class,
                reference
        ) {
            @Override
            protected void populateViewHolder(final FieldHolder viewHolder, final FieldDetail model, int position) {


                if(sp.getString(AllPreferences.getFieldKey(),GlobalNames.getNONE()).equals(model.getKey()))
                {
                    String field = getColoredSpanned(model.getFieldName());
                    String default_str = getColoredSpanned(" (Selected as your default field)");

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                    {
                        viewHolder.uni_field.setText(Html.fromHtml(field + " " + default_str,Html.FROM_HTML_MODE_COMPACT));
                    }
                    else
                    {
                        viewHolder.uni_field.setText(Html.fromHtml(field + " " + default_str));
                    }
                }
                else
                {
                    viewHolder.uni_field.setText(model.getFieldName());
                }

                //////////////////////////////////////////////////////////////////////////////////////////////////////
                ///////WHEN DELETE CLICKED////////////////////////////////////////////////////////////////////////////
                //////////////////////////////////////////////////////////////////////////////////////////////////////
                viewHolder.deleteField(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if(view.getId() == R.id.delete_field)
                        {
                            AlertDialog.Builder builder = new AlertDialog.Builder(CoursesOffered.this);
                            builder.setTitle("Are you sure?");
                            builder.setMessage("By deleting this field you will loose all the data of this field.");
                            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    reference.child(model.getKey()).removeValue();

                                    if(sp.getString(AllPreferences.getFieldKey(),GlobalNames.getDummyNode()).equals(model.getKey()))
                                    {
                                        SharedPreferences.Editor editor = sp.edit();
                                        editor.putString(AllPreferences.getFieldKey(),GlobalNames.getDummyNode());
                                        editor.apply();
                                        editor.clear();
                                    }

                                }
                            }).setNegativeButton("NO",null);

                            builder.show();
                        }
                        //////////////////////////////////////////////////////////////////////////////////////////////////////
                        ///////SET AS DEFAULT FIELD FOR THE USER/////////////////////////////////////////////////////////////
                        //////////////////////////////////////////////////////////////////////////////////////////////////////
                        else if(view.getId() == R.id.default_field)
                        {
                            FirebaseDatabase.getInstance().getReference().getRoot()
                                    .child(GlobalNames.getUserGroupDetail())
                                    .child(GlobalNames.getUserGroupList())
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .child(group_key)
                                    .child(GlobalNames.getFieldKey()).setValue(model.getKey());

                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString(AllPreferences.getFieldKey(),model.getKey());
                            editor.apply();
                            editor.clear();
                            notifyDataSetChanged();
                        }

                    }
                });

            }
        };

        recyclerView.setAdapter(adapter);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////DIFFERENT COLOR FOR SAME TEXT VIEW///////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    private String getColoredSpanned(String text)
    {
        return  "<font color=" + "#9FA8DA" + ">" + text + "</font>";
    }


}
