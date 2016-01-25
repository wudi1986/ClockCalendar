package com.leaf8.alicx.loadmorerecycler;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity /*implements ContentListFragment.OnListFragmentInteractionListener*/ {

    private ContentListFragment mListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        attachFragment(0);
    }

    /**
     * 加载布局
     */
    private void attachFragment(int type) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        switch (type) {
            case 0:
                if (null == mListFragment) {
                    mListFragment = new ContentListFragment();
                }
                mListFragment.setArguments(getIntent().getExtras());
                transaction.replace(R.id.container, mListFragment);
                break;
            case 1:
                if (null == mListFragment) {
                    mListFragment = new ContentListFragment();
                }
                mListFragment.setArguments(getIntent().getExtras());
                transaction.replace(R.id.container, mListFragment);
                break;
        }
        transaction.commit();
    }

    /**
     * 点击事件
     * @param view
     */
    public void clickCategory(View view) {
        int clickId = view.getId();
        if (clickId == R.id.waimai) {
            attachFragment(1);
        } else {
            attachFragment(0);
        }
    }

//    @Override
//    public void onListFragmentInteraction(DummyContent.DummyItem item) {
//
//    }
}
