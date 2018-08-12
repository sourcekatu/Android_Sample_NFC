// NFC-Aのコマンド仕様
// https://www.nxp.com/docs/en/data-sheet/MF0ICU1.pdf

package sample.sample_nfca;

import android.nfc.Tag;
import android.nfc.tech.NfcA;
import android.util.Log;

import static android.content.ContentValues.TAG;

public class NfcAControl {

    NfcA m_nfcA;

    public byte[] ReadData(byte a_block_Address) {
        try {
            m_nfcA.connect();

            byte[] l_command_List = new byte[]{0x30, a_block_Address};

            // コマンドを送信して結果を取得
            byte[] l_rec_List = m_nfcA.transceive(l_command_List);

            m_nfcA.close();

            return l_rec_List;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }

        return null;
    }

    public boolean WriteData(byte a_block_Address, byte [] a_data) {
        try {
            m_nfcA.connect();

            byte[] l_command_List = new byte[]{(byte)0xA2, a_block_Address, a_data[0], a_data[1], a_data[2], a_data[3]};

            // コマンドを送信して結果を取得
            m_nfcA.transceive(l_command_List);

            m_nfcA.close();

            return true;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }

        return false;
    }

    void SetTag(Tag a_tag)
    {
        m_nfcA = NfcA.get(a_tag);
    }
}