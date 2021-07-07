package com.example.mobilegis.Utils;

import android.os.StrictMode;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;


/**
 * Created by User on 09/07/2016.
 */
public class WebService {
    private static final int TIMEOUT_CONEXAO = 20000; // 20 segundos
    private static final int TIMEOUT_SOCKET = 30000; // 30 segundos
    private static final int TAM_MAX_BUFFER = 10240; // 10Kbytes
    private String url;

    public WebService(String url) {
        this.url = url;
    }


    // busca a tabela inteira do servidor
    public String getTabela(String tabela){
        String parserbuilder = "";
        try{
            HttpParams httpParameters = new BasicHttpParams();
            // Configura o timeout da conexão em milisegundos até que a conexão seja estabelecida
            HttpConnectionParams.setConnectionTimeout(httpParameters, TIMEOUT_CONEXAO);
            // Configura o timeout do socket em milisegundos do tempo que será utilizado para aguardar os dados
            HttpConnectionParams.setSoTimeout(httpParameters, TIMEOUT_SOCKET);

            HttpClient httpclient = new DefaultHttpClient(httpParameters);
//            HttpPost httppost = new HttpPost(url + "/getTabela");
//            HttpPost httppost = new HttpPost(url);
            HttpGet httpget = new HttpGet(url + tabela);


            List<NameValuePair> params = new ArrayList<NameValuePair>();
            //params.add(new BasicNameValuePair("NomeTabela", Tabela));
            //httppost.setEntity(new UrlEncodedFormEntity(params));
            //httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

            //Aqui o ideal é colocar a requesição assíncrona
            if (android.os.Build.VERSION.SDK_INT > 9) {
                StrictMode.ThreadPolicy policy =
                        new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);}

            HttpResponse response = httpclient.execute(httpget);
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(),"UTF-8"), TAM_MAX_BUFFER);
            StringBuilder builder = new StringBuilder();

            for (String line = null ; (line = reader.readLine())!= null;) {
                builder.append(line).append("\n");
            }

            parserbuilder = builder.toString();
            // Retira a string <?xml version="1.0" encoding="utf-8" ?>
            // <string xmlns="http://tempuri.org/"> e a tag </string>
            // para obter o resultado em Json, já que o webservice está retornando uma string
            //Integer firstTagString = parserbuilder.indexOf("<string");
//            Integer posXml = parserbuilder.indexOf(">", firstTagString);
//            Integer posTagString = parserbuilder.indexOf("</string>");
//            parserbuilder = parserbuilder.substring(posXml + 1, posTagString + 1);

        }catch(ClientProtocolException e){
            Log.e("WebService", e.toString());
        }
        catch(IOException e){
            Log.e("WebService", e.toString());
        }
        return parserbuilder;
    }

    // busca a tabela inteira do servidor
    public String getTabelaPost(String tabela, List<NameValuePair> listParams){
        String parserbuilder = "";
        try{
            HttpParams httpParameters = new BasicHttpParams();
            // Configura o timeout da conexão em milisegundos até que a conexão seja estabelecida
            HttpConnectionParams.setConnectionTimeout(httpParameters, TIMEOUT_CONEXAO);
            // Configura o timeout do socket em milisegundos do tempo que será utilizado para aguardar os dados
            HttpConnectionParams.setSoTimeout(httpParameters, TIMEOUT_SOCKET);

            HttpClient httpclient = new DefaultHttpClient(httpParameters);
            HttpPost httppost = new HttpPost(url + tabela);
//            HttpPost httppost = new HttpPost(url);
            //HttpGet httpget = new HttpGet(url + tabela);

//            ContentValues values = new ContentValues();
//            values.put("key1", "value1");
//            values.put("key2", "value2");

//            List<NameValuePair> params = new ArrayList<NameValuePair>();

            //params.add(new BasicNameValuePair("NomeTabela", Tabela));
            //httppost.setEntity(new UrlEncodedFormEntity(params));
            httppost.setEntity(new UrlEncodedFormEntity(listParams, "UTF-8"));


            //Aqui o ideal é colocar a requesição assíncrona
            if (android.os.Build.VERSION.SDK_INT > 9) {
                StrictMode.ThreadPolicy policy =
                        new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);}

            HttpResponse response = httpclient.execute(httppost);
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(),"UTF-8"), TAM_MAX_BUFFER);
            StringBuilder builder = new StringBuilder();

            for (String line = null ; (line = reader.readLine())!= null;) {
                builder.append(line).append("\n");
            }

            parserbuilder = builder.toString();
            // Retira a string <?xml version="1.0" encoding="utf-8" ?>
            // <string xmlns="http://tempuri.org/"> e a tag </string>
            // para obter o resultado em Json, já que o webservice está retornando uma string
            //Integer firstTagString = parserbuilder.indexOf("<string");
//            Integer posXml = parserbuilder.indexOf(">", firstTagString);
//            Integer posTagString = parserbuilder.indexOf("</string>");
//            parserbuilder = parserbuilder.substring(posXml + 1, posTagString + 1);

        }catch(ClientProtocolException e){
            Log.e("WebService", e.toString());
        }
        catch(IOException e){
            Log.e("WebService", e.toString());
        }
        return parserbuilder;
    }

    //envia territoriais para o servidor
    public String setTabelaTerr(final String Tabela, final String cpe, final JSONObject json) {
        String result = "false";
        try{
            HttpParams httpParameters = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, TIMEOUT_CONEXAO);
            HttpConnectionParams.setSoTimeout(httpParameters, TIMEOUT_SOCKET);

            HttpClient httpclient = new DefaultHttpClient(httpParameters);
            HttpPost httppost = new HttpPost(url + "/setTabelaTerr");

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("NomeTabela", Tabela));
            params.add(new BasicNameValuePair("cpe", cpe));
            params.add(new BasicNameValuePair("serializedResult", json.toString()));
            httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));



            //Aqui o ideal é colocar a requesição assíncrona
            if (android.os.Build.VERSION.SDK_INT > 9) {
                StrictMode.ThreadPolicy policy =
                        new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);}

            HttpResponse resp = httpclient.execute(httppost);


            if (resp != null) {
                if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    InputStreamReader  iSR = new InputStreamReader(resp.getEntity().getContent());
                    BufferedReader br = new BufferedReader(iSR);
                    String line;
                    while ((line = br.readLine()) != null)
                        if(line.contains(">ok<")){
                            result = "true";
                        }
                }
            }

            Log.d("Status line", "" + resp.getStatusLine().getStatusCode());

        }catch(ClientProtocolException e){
            Log.e("WebService", e.toString());
        }
        catch(IOException e){
            Log.e("WebService", e.toString());
        }
        return result;
    }


    //envia edificacoes para o servidor
    public String setTabelaEdif(final String Tabela, final String cpe, final String seq, final String orgao, final JSONObject json, final String funcao) {
        String result = "false";
        try{
            HttpParams httpParameters = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, TIMEOUT_CONEXAO);
            HttpConnectionParams.setSoTimeout(httpParameters, TIMEOUT_SOCKET);

            HttpClient httpclient = new DefaultHttpClient(httpParameters);
            HttpPost httppost = new HttpPost(url + "/" + funcao);

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("NomeTabela", Tabela));
            params.add(new BasicNameValuePair("cpe", cpe));
            params.add(new BasicNameValuePair("seq", seq));
            params.add(new BasicNameValuePair("serializedResult", json.toString()));
            params.add(new BasicNameValuePair("orgao", orgao));
            //httppost.setEntity(new UrlEncodedFormEntity(params));
            httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

            //Aqui o ideal é colocar a requesição assíncrona
            if (android.os.Build.VERSION.SDK_INT > 9) {
                StrictMode.ThreadPolicy policy =
                        new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);}

            HttpResponse resp = httpclient.execute(httppost);

            if (resp != null) {
                if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    InputStreamReader  iSR = new InputStreamReader(resp.getEntity().getContent());
                    BufferedReader br = new BufferedReader(iSR);
                    String line;
                    while ((line = br.readLine()) != null)
                        if(line.contains(">ok<")){
                            result = "true";
                        }
                }
            }

           /* if (resp != null) {
                if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    result = "true";
                }
            }*/

            Log.d("Status line", "" + resp.getStatusLine().getStatusCode());

        }catch(ClientProtocolException e){
            Log.e("WebService", e.toString());
        }
        catch(IOException e){
            Log.e("WebService", e.toString());
        }
        return result;
    }

    //envia fotos para o servidor
    public String setTabelaFotos(final String Tabela, final String nomeFoto, final JSONObject json) {
        String result = "false";
        try{
            HttpParams httpParameters = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, TIMEOUT_CONEXAO);
            HttpConnectionParams.setSoTimeout(httpParameters, TIMEOUT_SOCKET);

            HttpClient httpclient = new DefaultHttpClient(httpParameters);
            HttpPost httppost = new HttpPost(url + "/setTabelaFotos");

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("NomeTabela", Tabela));
            params.add(new BasicNameValuePair("nome", nomeFoto));
            params.add(new BasicNameValuePair("serializedResult", json.toString()));
            httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));


            //Aqui o ideal é colocar a requesição assíncrona
            if (android.os.Build.VERSION.SDK_INT > 9) {
                StrictMode.ThreadPolicy policy =
                        new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);}

            HttpResponse resp = httpclient.execute(httppost);

            if (resp != null) {
                if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    InputStreamReader  iSR = new InputStreamReader(resp.getEntity().getContent());
                    BufferedReader br = new BufferedReader(iSR);
                    String line;
                    while ((line = br.readLine()) != null)
                        if(line.contains(">ok<")){
                            result = "true";
                        }
                }
            }

           /* if (resp != null) {
                if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    result = "true";
                }
                //if (resp.getStatusLine().getStatusCode() == 204)
            }*/

            Log.d("Status line", "" + resp.getStatusLine().getStatusCode());

        }catch(ClientProtocolException e){
            Log.e("WebService", e.toString());
        }
        catch(IOException e){
            Log.e("WebService", e.toString());
        }
        return result;
    }



    public String  performPostCall(String requestURL,
                                   HashMap<String, String> postDataParams) {

        URL url;
        String response = "";
        try {
            url = new URL(requestURL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);


            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getPostDataString(postDataParams));

            writer.flush();
            writer.close();
            os.close();
            int responseCode=conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line=br.readLine()) != null) {
                    response+=line;
                }
            }
            else {
                response="";

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }

    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }

}
