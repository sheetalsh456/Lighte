package microsoft.lighteapp;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageButton a,c;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        a=(ImageButton)findViewById(R.id.im1);
        c=(ImageButton)findViewById(R.id.im3);
       // d=(ImageButton)findViewById(R.id.im4);



        a.setOnClickListener(this);
        c.setOnClickListener(this);
       // d.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.im1:
                startActivity(new Intent(MainActivity.this,Documents.class));
                break;
            case R.id.im3:
                startActivity(new Intent(MainActivity.this,SpeedDial.class));
                break;

        }
    }
}
