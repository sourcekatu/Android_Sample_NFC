// 参考URL
// https://qiita.com/yamacraft/items/04bd3dee738c95ef13e1
// https://developer.android.com/guide/topics/connectivity/nfc/advanced-nfc

package sample.sample_nfca;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NfcA;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity{

    private NfcAdapter mNfcAdapter;
    private PendingIntent mPendingIntent;
    private IntentFilter[] mIntentFileters;

    NfcAControl mNfcAControl;
    EditText mEdtWriteData;
    TextView mTxtMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ------書き込み用の設定-----------------//
        setContentView(R.layout.activity_main);

        // アプリが立ち上がっているときのみNFCを動作させたいので、その設定。
        // Foreground Dispatch Systemの設定
        mNfcAdapter = NfcAdapter.getDefaultAdapter(getApplicationContext());
        Intent intent = new Intent(getApplicationContext(), getClass());
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        mPendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        IntentFilter intentFilter = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        mIntentFileters = new IntentFilter[]{intentFilter};

        mEdtWriteData = findViewById(R.id.edtWriteData);
        Button btnWrite = findViewById(R.id.btnWrite);
        mTxtMessage = findViewById(R.id.txtMessage);

        btnWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTxtMessage.setText("アプリをNFCタグにかざしてください。");
            }
        });
        // ------書き込み用の設定-----------------//


        // ------読み込み用の設定-----------------//
        Intent intent_Read = getIntent();
        String action = intent_Read.getAction();

        // NFCかどうかActionの判定
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)
                ||  NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                ||  NfcAdapter.ACTION_TAG_DISCOVERED.equals(action))
        {
            setContentView(R.layout.read_layout);
            Tag tag = intent_Read.getParcelableExtra(NfcAdapter.EXTRA_TAG);

            if (mNfcAControl == null)
            {
                mNfcAControl = new NfcAControl();
            }
            mNfcAControl.SetTag(tag);

            byte []l_rcv_Data_List;
            l_rcv_Data_List = mNfcAControl.ReadData((byte)4);

            TextView txtRcvData;
            txtRcvData = findViewById(R.id.txtRcvData);
            String l_string = "";
            String l_string_Char = "  ";

            for (int ii = 0; ii < 4; ii++)
            {
                l_string = l_string + String.format("%02x",l_rcv_Data_List[ii]) + " ";
                l_string_Char = l_string_Char + (char)l_rcv_Data_List[ii] + " ";
            }

            txtRcvData.setText("Data（16進）        = " + l_string + "\n" + "Data（アスキー）= " + l_string_Char);
        }
        // ------読み込み用の設定-----------------//
    }

    @Override
    protected  void onResume(){
        super.onResume();


        String [][] techListsArray = new String[][] {new String[] {NfcA.class.getName()}};

        // この設定を行うことで、アプリを開いている時にonNewIntent(Intent intent)の処理に入れるようにしている。
        mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, mIntentFileters, techListsArray);
    }

    @Override
    protected void onPause(){
        super.onPause();
        mNfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onNewIntent(Intent intent){
        super.onNewIntent(intent);

        if ((intent != null) && NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())){
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

            if (mNfcAControl == null) {
                mNfcAControl = new NfcAControl();
            }

            mNfcAControl.SetTag(tag);

            String string = mEdtWriteData.getText().toString();
            mNfcAControl.WriteData((byte)4, string.getBytes());

            mTxtMessage.setText("書き込み成功です。\n" + string);
        }
    }
}
