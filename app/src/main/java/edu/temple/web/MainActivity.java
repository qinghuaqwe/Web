package edu.temple.web;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements WebFragment.OnFragmentInteractionListener {
    private StatePageAdapter webAdapter;//set page adapter let fragment manage each page
    private ViewPager webPager;//viewpager will help move front and back
    private Button goButton;
    private EditText editText;

    /*EditText urlTextView;
    TextView display;
    Button goButton;

    Handler showContent = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            display.setText((String) msg.obj);
            return false;
        }
    });*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            requestPermissions(new String[]{Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE}, 1234);

        display = (TextView) findViewById(R.id.display);
        urlTextView = (EditText) findViewById(R.id.urlTextView);
        goButton = (Button) findViewById(R.id.gobutton);

        goButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Thread loadContent = new Thread() {
                    @Override
                    public void run() {

                        if (isNetworkActive()) {

                            URL url;

                            try {
                                url = new URL(urlTextView.getText().toString());
                                BufferedReader reader = new BufferedReader(
                                        new InputStreamReader(
                                                url.openStream()));

                                String response = "", tmpResponse;

                                tmpResponse = reader.readLine();
                                while (tmpResponse != null) {
                                    response = response + tmpResponse;
                                    tmpResponse = reader.readLine();
                                }

                                Message msg = Message.obtain();

                                msg.obj = response;

                                showContent.sendMessage(msg);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        } else
                            Toast.makeText(MainActivity.this, "Please connect to a network", Toast.LENGTH_SHORT).show();
                    }
                };

                loadContent.start();
            }
        });
    }

    public boolean isNetworkActive() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}*/
        goButton = (Button) findViewById(R.id.goBtn);
        editText = (EditText) findViewById(R.id.urlTextBox);
        webAdapter = new StatePageAdapter(getFragmentManager());//use statePageAdapter to saving
        //and restoring of fragment's state. Useful for handle large mount of pages
        webPager = (ViewPager)findViewById(R.id.pager);
        webPager.setAdapter(webAdapter);
        //use newInstance() to provide user's choice back tot he fragment when creating the next fragment instance
        webAdapter.addFragment(WebFragment.newInstance(""+webAdapter.getCount()));
        //go button would load the url typed in text line
        //it will retrieve the current fragment from adapter and add new url to the url list of fragment
        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = editText.getText().toString();
                WebFragment item = (WebFragment) webAdapter.getItem(webPager.getCurrentItem());
                item.addAndLoadUrl(url);//add url to the fragment.
            }
        });
        webPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {//add a listner invokes when the page changes
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }
            @Override
            public void onPageSelected(int position) {
                //call onUrlChanged method each time fragment is visted. So user will no longer to hit go
                //button and the page will load automatically.
                ((WebFragment)webAdapter.getItem(position)).onUrlChange();
            }
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {//inflate menu resource buttons.xml into the menu
        getMenuInflater().inflate(R.menu.buttons, menu);
        return super.onCreateOptionsMenu(menu);
    }
    //checks which action to be used
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.previous:
                webPager.setCurrentItem(webPager.getCurrentItem()-1);
                return true;

            case R.id.newFrag://when create new page, it creates new fragment
                webAdapter.addFragment(BrowserFragment.newInstance(""+webAdapter.getCount()));
                webPager.setCurrentItem(webAdapter.getCount()-1);
                return true;

            case R.id.next:
                webPager.setCurrentItem(webPager.getCurrentItem()+1);
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }
    //communicate with fragments with uri so each time fragment visible, it will return last visted url
    //in the fragment.
    @Override
    public void onFragmentInteraction(String uri) {
        editText.setText(uri);
    }
    //Hold the list of fragments in a list so user can move back and forward with the restored url.
    public static class StatePageAdapter extends FragmentStatePagerAdapter {
        //pass the data of list<Fragments>
        private List<BrowserFragment> fragments;

        public StatePageAdapter(FragmentManager fm) {
            super(fm);
            fragments = new ArrayList<>();//use arraylist to manage fragments
        }
        //add fragment to the list when new button is hit.
        public void addFragment(BrowserFragment fragment){
            fragments.add(fragment);
            notifyDataSetChanged();//when list is updated, notify the changes
        }
        @Override
        public int getCount() {
            return fragments.size();
        }
        @Override
        public Fragment getItem(int position) {//return fragment from the list at specific position
            return fragments.get(position);
        }
    }
}

