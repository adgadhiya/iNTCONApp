package com.myapp.unknown.iNTCON.NavigationView;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.myapp.unknown.iNTCON.R;
import com.myapp.unknown.iNTCON.ViewHolders.FAQViewHolder;

public class FAQ extends AppCompatActivity {

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);

        Toolbar toolbar = (Toolbar) findViewById(R.id.faq_toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.faq_rv);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

         setRecyclerView();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home)
        {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void setRecyclerView(){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().getRoot().child("faq");

        FirebaseRecyclerAdapter<FAQClass,FAQViewHolder> adapter = new FirebaseRecyclerAdapter<FAQClass, FAQViewHolder>
                (
                        FAQClass.class,
                        R.layout.faq_layout,
                        FAQViewHolder.class,
                        reference
                ) {
            @Override
            protected void populateViewHolder(final FAQViewHolder viewHolder, FAQClass model, int position) {

                viewHolder.title.setText(model.getTitle());
                viewHolder.detail.setText(model.getDetail());

                Typeface typeface = Typeface.createFromAsset(getAssets(),"Roboto-Regular.ttf");
                viewHolder.detail.setTypeface(typeface);

                viewHolder.bindToUIlayout(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if(viewHolder.detail.getVisibility() == View.VISIBLE)
                        {
                            viewHolder.detail.setVisibility(View.GONE);
                            viewHolder.imageView.setRotation(90);
                        }
                        else
                        {
                            viewHolder.detail.setVisibility(View.VISIBLE);
                            viewHolder.imageView.setRotation(-90);
                        }

                    }
                });

            }
        };

        recyclerView.setAdapter(adapter);

    }

}
