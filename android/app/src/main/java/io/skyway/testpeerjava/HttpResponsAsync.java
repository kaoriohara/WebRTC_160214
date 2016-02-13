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
        // doInBackground前処理
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
//            // URLの作成
//            url = new URL(urlSt);
//            // 接続用HttpURLConnectionオブジェクト作成
//            con = (HttpURLConnection)url.openConnection();
//            // リクエストメソッドの設定
//            con.setRequestMethod("POST");
//            // リダイレクトを自動で許可しない設定
//            con.setInstanceFollowRedirects(false);
//            // URL接続からデータを読み取る場合はtrue
//            con.setDoInput(true);
//            // URL接続にデータを書き込む場合はtrue
//            con.setDoOutput(true);
//
//            // 接続
//            con.connect();
//            // 本文の取得
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
        // doInBackground後処理
    }

}
