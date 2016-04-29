package blueinteraction.mamn01blueinteraction;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class HighScoreActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_score);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPreferences highscore = this.getSharedPreferences("CheckPointHighScore", Context.MODE_PRIVATE);
        Map<String,?> test = highscore.getAll();
        Set<String> keys = test.keySet();
        ArrayList<String> scoreList = new ArrayList<>();
        ArrayList<String> keyList = new ArrayList<>();
        ListAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, scoreList);
        ListView listView = (ListView)findViewById(R.id.listview);

        if(keys!=null){
            Iterator iterator = keys.iterator();
            String key;

            while(iterator.hasNext()){
                key = (String)iterator.next();
                keyList.add(key);
            }
        }
        String value;
        for(int i=0; i<keyList.size(); i++){
            value = highscore.getString(keyList.get(i),"");
            scoreList.add(value);
        }

        Collections.sort(scoreList, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                String arr1[] = s1.split(" ");
                String arr2[] = s2.split(" ");
                if (Integer.parseInt(arr1[0]) > Integer.parseInt(arr2[0]))
                    return -1;
                if (Integer.parseInt(arr1[0]) < Integer.parseInt(arr2[0]))
                    return 1;
                return 0;
            }
        });
        listView.setAdapter(adapter);
    }

}
