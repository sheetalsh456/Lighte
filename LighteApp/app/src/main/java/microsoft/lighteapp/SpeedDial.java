package microsoft.lighteapp;

import android.app.Activity;
import android.app.ListActivity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Axle on 01/03/2016.
 */
public class SpeedDial extends ListActivity implements View.OnClickListener {

    Button button;
    ArrayAdapter<String> adapter;
    String x;
    private ImageButton btnSpeak;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    ArrayList<String> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.speeddial);
        list = new ArrayList<String>();
        button = (Button) findViewById(R.id.button);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
        setListAdapter(adapter);
        btnSpeak = (ImageButton) findViewById(R.id.btnSpeak);
        button.setOnClickListener(this);
        btnSpeak.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSpeak:
                promptSpeechInput();
                break;
            case R.id.button:
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, 123);
                break;
        }
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

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        switch (reqCode) {
            case 123:
                if (resultCode == Activity.RESULT_OK) {
                    Uri contactData = data.getData();
                    Cursor c = managedQuery(contactData, null, null, null, null);
                    if (c.moveToFirst()) {
                        String id = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));

                        String hasPhone =
                                c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

                        if (hasPhone.equalsIgnoreCase("1")) {
                            Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id, null, null);
                            phones.moveToFirst();
                            String cNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            String cName = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                            Toast.makeText(getApplicationContext(), cNumber + " " + cName, Toast.LENGTH_SHORT).show();
                            x = cName + "@" + cNumber;
                            list.add(x);
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
            case REQ_CODE_SPEECH_INPUT:
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    try {
                        Toast.makeText(SpeedDial.this, result.get(0), Toast.LENGTH_SHORT).show();
                        String s=(result.get(0)).toString();
                        for(int i=0;i<list.size();i++){
                            String[] parts = list.get(i).split("@", 2);
                            String string1 = parts[0];
                            String string2 = parts[1];
                            Toast.makeText(SpeedDial.this,string1+"/" +string2,Toast.LENGTH_LONG).show();;
                            if(s.equalsIgnoreCase(string1)) {
                                //Toast.makeText(SpeedDial.this,"/add" ,Toast.LENGTH_LONG).show();
                                //startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(string2)));
                                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                                callIntent.setData(Uri.parse("tel:"+Uri.encode(string2.trim())));
                                callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(callIntent);
                            }
                        }
                    }catch(NullPointerException e){
                        e.printStackTrace();
                    }
                }
                break;

        }
    }


}
