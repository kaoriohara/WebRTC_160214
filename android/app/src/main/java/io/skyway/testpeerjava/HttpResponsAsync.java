package io.skyway.testpeerjava;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.memetix.mst.language.Language;
import com.memetix.mst.language.SpokenDialect;
import com.memetix.mst.speak.Speak;
import com.memetix.mst.translate.Translate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by mmachi on 2016/02/14.
 */
public class HttpResponsAsync extends AsyncTask<Void, Void, String> {

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // doInBackground�O����
    }

    @Override
    protected String doInBackground(Void... params) {

        Translate.setClientId("tad2016");
        Translate.setClientSecret("n+c88QCb5WqsID86yHC0tFK5Wtr5vlBeXBxYTJRxn9k=");
        Speak.setClientId("tad2016");
        Speak.setClientSecret("n+c88QCb5WqsID86yHC0tFK5Wtr5vlBeXBxYTJRxn9k=");

        String translatedText = null;

//        Toast.makeText(this, results.get(0), Toast.LENGTH_SHORT).show();
        try {
//            translatedText = Translate.execute(results.get(0), Language.JAPANESE, Language.ENGLISH);
//            translatedText = Speak.execute(results.get(0), SpokenDialect.JAPANESE_JAPAN);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.d("VOICE", translatedText);

//        HttpURLConnection con = null;
//        URL url = null;
//        String urlSt = "http://sample.jp";
//
//        try {
//            // URL�̍쐬
//            url = new URL(urlSt);
//            // �ڑ��pHttpURLConnection�I�u�W�F�N�g�쐬
//            con = (HttpURLConnection)url.openConnection();
//            // ���N�G�X�g���\�b�h�̐ݒ�
//            con.setRequestMethod("POST");
//            // ���_�C���N�g�������ŋ����Ȃ��ݒ�
//            con.setInstanceFollowRedirects(false);
//            // URL�ڑ�����f�[�^��ǂݎ��ꍇ��true
//            con.setDoInput(true);
//            // URL�ڑ��Ƀf�[�^���������ޏꍇ��true
//            con.setDoOutput(true);
//
//            // �ڑ�
//            con.connect();
//            // �{���̎擾
//            InputStream in = con.getInputStream();
//            String readSt = readInputStream(in);
//
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        return null;
    }

    public String readInputStream(InputStream in) throws IOException, UnsupportedEncodingException {
        StringBuffer sb = new StringBuffer();
        String st = "";

        BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        while((st = br.readLine()) != null)
        {
            sb.append(st);
        }
        try
        {
            in.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return sb.toString();
    }
    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        // doInBackground�㏈��
    }

}
