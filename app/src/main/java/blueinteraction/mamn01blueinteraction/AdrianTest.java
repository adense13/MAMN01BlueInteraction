package blueinteraction.mamn01blueinteraction;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class AdrianTest extends AppCompatActivity {

    private EditText radius;
    private TextWatcher t;
    private Button buttonPlay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adrian_test);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        buttonPlay = (Button) findViewById(R.id.buttonPlayGame);
//        buttonPlay.setTextColor(Color.BLACK);
//        radius = (EditText) findViewById((R.id.textGameRadius));
//        radius.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void afterTextChanged(Editable s) {}
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if(s.length() != 0){
//                    buttonPlay.setEnabled(true);
//                    buttonPlay.setTextColor(Color.WHITE);
//                    //Field2.setText("");
//                }
//                else{
//                    buttonPlay.setEnabled(false);
//                    buttonPlay.setTextColor(Color.BLACK);
//                }
//            }
//        });
    }

    public void clickToGPS(View view){
        Intent intent = new Intent(this, PlaySetupActivity.class);
        startActivity(intent);
    }

    public void highScoreWu(View view){
        Intent intent = new Intent(this, HighScoreActivity.class);
        startActivity(intent);
    }

}
