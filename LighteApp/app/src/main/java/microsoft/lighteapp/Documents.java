package microsoft.lighteapp;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.table.CloudTable;
import com.microsoft.azure.storage.table.CloudTableClient;
import com.microsoft.azure.storage.table.TableQuery;
import com.microsoft.windowsazure.mobileservices.*;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.table.TableOperationCallback;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

/**
 * Created by Axle on 29/02/2016.
 */
public class Documents extends Activity {
    private MobileServiceClient mClient;
    private TextView txtSpeechInput;
    private ImageButton btnSpeak;
    private Button add;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    String x,y;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doc);
        txtSpeechInput = (TextView) findViewById(R.id.txtSpeechInput);
        btnSpeak = (ImageButton) findViewById(R.id.btnSpeak);
        add = (Button) findViewById(R.id.button);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Documents.this, AddDoc.class));
            }
        });
        // hide the action bar
        btnSpeak.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }
        });
    }

    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Receiving speech input
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    txtSpeechInput.setText(result.get(0));
                    Toast.makeText(Documents.this, result.get(0), Toast.LENGTH_LONG).show();
                    x=result.get(0);
                    // Retrieve storage account from connection-string.
                    try {
                        mClient = new MobileServiceClient(
                                "https://lighte.azure-mobile.net/",
                                "bTUGBzmtEmoUUqkUxfBRFKYmYXQBzu32",
                                Documents.this
                        );
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    final MobileServiceTable<Main> mToDoTable;
                    mToDoTable = mClient.getTable(Main.class);
                    SharedPreferences pref = PreferenceManager
                            .getDefaultSharedPreferences(Documents.this);
                    final String email=pref.getString("KEY_E",null);
                    new AsyncTask<Void, Void, Void>() {

                        @Override
                        protected Void doInBackground(Void... params) {
                            try {
                                final MobileServiceList<Main> result = mToDoTable.execute().get();
                                runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {

                                        for (Main item : result) {
                                            if((item.email).equalsIgnoreCase(email) && (item.Text).equalsIgnoreCase(x)){
                                                Toast.makeText(Documents.this,item.Image,Toast.LENGTH_SHORT).show();
                                                y=item.Image;
                                                Intent i = new Intent(Documents.this,ViewPic.class);
                                                i.putExtra("img",y);
                                                startActivity(i);
                                            }
                                        }
                                    }
                                });
                            } catch (Exception exception) {
                                //createAndShowDialog(exception, "Error");
                            }
                            return null;
                        }
                    }.execute();
                }
                break;
            }

        }
    }



}
