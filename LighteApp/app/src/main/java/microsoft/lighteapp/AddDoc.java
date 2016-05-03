package microsoft.lighteapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.table.TableOperationCallback;

import java.io.ByteArrayOutputStream;
import java.net.MalformedURLException;

/**
 * Created by Axle on 01/03/2016.
 */
public class AddDoc extends Activity {

    private static int RESULT_LOAD_IMAGE = 1;
    EditText e;
    Button upload;
    String imageEncoded,name;
    private MobileServiceClient mClient;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adddoc);
        e=(EditText)findViewById(R.id.name);
        upload=(Button)findViewById(R.id.upload);
        Button buttonLoadImage = (Button) findViewById(R.id.buttonLoadPicture);
        buttonLoadImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            ImageView imageView = (ImageView) findViewById(R.id.imgView);
            imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
            Bitmap bm = BitmapFactory.decodeFile(picturePath);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
            byte[] b = baos.toByteArray();
            imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);
            name=e.getText().toString();
            upload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        mClient = new MobileServiceClient(
                                "https://lighte.azure-mobile.net/",
                                "bTUGBzmtEmoUUqkUxfBRFKYmYXQBzu32",
                                AddDoc.this
                        );
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    SharedPreferences pref = PreferenceManager
                            .getDefaultSharedPreferences(AddDoc.this);
                    String email=pref.getString("KEY_E",null);
                    Main item = new Main();
                    item.Text = name;
                    item.Image = imageEncoded;
                    item.email=email;
                    mClient.getTable(Main.class).insert(item, new TableOperationCallback<Main>() {
                        public void onCompleted(Main entity, Exception exception, ServiceFilterResponse response) {
                            if (exception == null) {
                                Toast.makeText(AddDoc.this, "Success yayyy", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(AddDoc.this, Documents.class));
                                finish();
                            } else {
                                Toast.makeText(AddDoc.this, "Failed ooooooo", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            });


           // startActivity(new Intent(AddDoc.this, Documents.class));
        }


    }
}