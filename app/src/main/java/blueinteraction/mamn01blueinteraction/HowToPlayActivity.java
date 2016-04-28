package blueinteraction.mamn01blueinteraction;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

/**
 * Created by Daniel on 2016-04-28.
 */
public class HowToPlayActivity extends AppCompatActivity{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_how_to_play);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ImageView animImageView = (ImageView) findViewById(R.id.ivAnimation);
        animImageView.setBackgroundResource(R.drawable.anim);
        animImageView.post(new Runnable() {
            @Override
            public void run() {
                AnimationDrawable frameAnimation =
                        (AnimationDrawable) animImageView.getBackground();
                frameAnimation.start();
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
