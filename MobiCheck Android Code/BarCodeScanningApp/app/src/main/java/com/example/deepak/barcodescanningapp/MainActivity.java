package com.example.deepak.barcodescanningapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
//scanner import
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.loopj.android.http.HttpGet;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.protocol.BasicHttpContext;
import cz.msebera.android.httpclient.protocol.HttpContext;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    String responseString = null, scannedContent=null;
    private Button scanBtn,validateBtn;
   //private ZXingScannerView mScannerView;
    private TextView formatTxt, contentTxt, respTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //scanner code

        scanBtn = (Button)findViewById(R.id.scan_button);
        formatTxt = (TextView)findViewById(R.id.scan_format);
        contentTxt = (TextView)findViewById(R.id.scan_content);

        validateBtn = (Button)findViewById(R.id.validate_button);
        respTxt = (TextView)findViewById(R.id.res_text);

        scanBtn.setOnClickListener(this);
        validateBtn.setOnClickListener(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    /*public void onClick(View v) {
        // TODO Auto-generated method stub
        Intent intent = new Intent("com.google.zxing.client.android.SCAN");
        intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
        startActivityForResult(intent, 0);
    }
*/
    public void onClick(View v){

        if(v.getId()==R.id.scan_button){
            scan();
        }
         if (v.getId()==R.id.validate_button){
            RestCall();
        }
    }


    public void scan(){
        IntentIntegrator scanIntegrator = new IntentIntegrator(this);
        scanIntegrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
        scanIntegrator.setPrompt("Scan a barcode");
        //scanIntegrator.setResultDisplayDuration(0);
        //scanIntegrator.setWide();  // Wide scanning rectangle, may work better for 1D barcodes
        scanIntegrator.setCameraId(0);  // Use a specific camera of the device
        scanIntegrator.initiateScan();
    }

    public void RestCall(){
        RequestTask task = (RequestTask) new RequestTask().execute();
        //String test = task.toString();
        respTxt.setText("Product : "+responseString);
    }
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);

        if (scanningResult != null) {
            String scanContent = scanningResult.getContents();
            String scanFormat = scanningResult.getFormatName();
            scannedContent = scanContent;
            formatTxt.setText("FORMAT: " + scanFormat);
            contentTxt.setText("CONTENT: " + scanContent);

        }else{
            Toast toast = Toast.makeText(getApplicationContext(),
                    "No scan data received!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //inner class
    class RequestTask extends AsyncTask<String, String, String> {

        //  TextView jsonParsed = (TextView) findViewById(R.id.api_output);

        //private ProgressDialog Dialog = new ProgressDialog(thi);



        protected String getASCIIContentFromEntity(HttpEntity entity) throws IllegalStateException, IOException {

            InputStream in = entity.getContent();

            StringBuffer out = new StringBuffer();
            int n = 1;
            while (n>0) {
                byte[] b = new byte[4096];

                n =  in.read(b);

                if (n>0) out.append(new String(b, 0, n));

            }

            return out.toString();

        }

        @Override
        protected String doInBackground(String... params) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpContext localContext = new BasicHttpContext();
            HttpGet httpGet = new HttpGet("https://mobicheck.mybluemix.net/mobicheck?bcode="+scannedContent);
            String text = null;
            try {

                HttpResponse response = httpClient.execute(httpGet, localContext);

                HttpEntity entity = response.getEntity();

                text = getASCIIContentFromEntity(entity);

            } catch (Exception e) {
                return e.getLocalizedMessage();

            }

            return text;
        }

        protected void onPostExecute(String results) {
            if (results!=null) {

                /*EditText et = (EditText)findViewById(R.id.my_edit);

                et.setText(results);*/
                try{
                    JSONObject prod=(new JSONObject(results)).getJSONObject("product");
                    String pstatus=prod.getString("status");
                    responseString = pstatus;

                }catch (Exception e) {e.printStackTrace();}


            }

            /*Button b = (Button)findViewById(R.id.my_button);

            b.setClickable(true);*/

        }

    }
}
