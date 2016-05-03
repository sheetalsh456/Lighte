package microsoft.lighteapp;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageView;

/**
 * Created by Axle on 02/03/2016.
 */
public class ViewPic extends Activity {
    ImageView i;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picview);
        i=(ImageView)findViewById(R.id.imageView);
        Bundle bundle = getIntent().getExtras();
        String y=bundle.getString("img");
        Bitmap bitmap = decodeBase64(y);
        i.setImageBitmap(bitmap);
    }

    public static Bitmap decodeBase64(String input) {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory
                .decodeByteArray(decodedByte, 0, decodedByte.length);
    }
}
