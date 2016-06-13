package com.pouch.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.pouch.R;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {
    private static final String TAG = SearchActivity.class.getSimpleName();
    ListView resultView;
    SearchAdapter adapter;
    ArrayList<String> address;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        address = new ArrayList<String>();
        address.add("토니모리 전주점\n전라북도 전주시 완산구 고사동 105");
        address.add("토니모리 전주고사점\n전라북도 전주시 완산구 고사동 119-6");
        address.add("토니모리 세이브존전주점\n전라북도 전주시 완산구 서노송동 627-1");
        address.add("토니모리 전주전북대점\n전라북도 전주시 덕진구 덕진동1가 1266-22");
        address.add("토니모리 롯데마트전주점\n전라북도 전주시 완산구 효자동2가 1234-2");

        resultView = (ListView) findViewById(R.id.SearchResult);
        adapter = new SearchAdapter(this,address,R.layout.activity_search_item);

        resultView.setAdapter(adapter);
        resultView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String getView = address.get(position);
                Log.v(TAG, getView);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Log.v(TAG,"id : "+ id);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


        private class SearchAdapter extends BaseAdapter{
            Context context;
            ArrayList<String> data;
            int layoutId;
            TextView txt;
            ImageView img;
            LayoutInflater inflater;

            SearchAdapter(Context context,ArrayList<String> data,int itemLayoutId){
                this.context = context;
                this.data =data;
                this.layoutId = itemLayoutId;

                inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }
            @Override
            public int getCount() {
                return data.size();
            }

            @Override
            public Object getItem(int position) {
                return data.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                    if(convertView == null){
                        convertView = inflater.inflate(layoutId,parent,false);
                    }

                    String getText = data.get(position);
                    txt = (TextView)convertView.findViewById(R.id.address);
                    img = (ImageView)convertView.findViewById(R.id.map_icon);

                    txt.setText(getText);

                    return convertView;
            }
        }
}
