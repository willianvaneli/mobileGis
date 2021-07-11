package com.example.mobilegis.Models;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.SimpleDateFormat;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.util.Date;

public class Foto {
    private int id;
    private int paiId;
    private String paiTipo;
    private String foto;

    public Foto(int paiId, String paiTipo, String foto){
        this.paiId = paiId;
        this.paiTipo = paiTipo;
        this.foto = foto;
    }

    public Foto() {}

    public Foto(JSONObject jsonObj) throws JSONException {
        this.id = jsonObj.getInt("id");
        this.paiId = jsonObj.getInt("pai_id");
        this.paiTipo = jsonObj.getString("pai_tipo");
        Bitmap img = decodeBase64(jsonObj.getString("foto"));
        this.foto = escreveImagens(img);
    }

    private String escreveImagens(Bitmap bmp){
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);

            byte[] bytes = stream.toByteArray();;
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            File pasta = Environment.getExternalStoragePublicDirectory("//CadastroMobile/fotos");
            String patch = pasta.getPath() + File.separator + "CM_" + timeStamp + ".jpg";
            FileOutputStream fos = new FileOutputStream(patch);
            fos.write(bytes);
            fos.close();
            return patch;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public byte[] toByteArray(){
        Bitmap img = (BitmapFactory.decodeFile(this.getFoto()));
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        img.compress(Bitmap.CompressFormat.JPEG,100,stream);
        return stream.toByteArray();
    }

    public String encodeTobase64(Bitmap image){
        Bitmap immagex = image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immagex.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        String imageEncoded = null;
        byte[] b = baos.toByteArray();
        try {
            imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);
        }catch(Exception e){
            e.printStackTrace();
        }catch(OutOfMemoryError e){
            baos=new  ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG,50, baos);
            b=baos.toByteArray();
            imageEncoded=Base64.encodeToString(b, Base64.DEFAULT);
            Log.e("EWN", "Out of memory error catched");
        }
        immagex.recycle();
        return imageEncoded;
    }

    public Bitmap decodeBase64(String input) {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0,      decodedByte.length);
    }

    public JSONObject toJson() throws JSONException {
        JSONObject json = new JSONObject();

        json.put("id",this.id);
        json.put("pai_id",this.paiId);
        json.put("pai_tipo", this.paiTipo);

        try {
            Bitmap img = (BitmapFactory.decodeFile(this.getFoto()));

            json.put("foto", encodeTobase64(img));
        }catch (Exception e){
            Log.e(e.getMessage(),"Erro ao montar imagem");
        }
        return json;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPaiId() {
        return paiId;
    }

    public void setPaiId(int paiId) {
        this.paiId = paiId;
    }

    public String getPaiTipo() {
        return paiTipo;
    }

    public void setPaiTipo(String paiTipo) {
        this.paiTipo = paiTipo;
    }

    public String getFoto() {
        return foto;
    }

//    public Bitmap getFotoBitmap() {
//        ByteArrayInputStream imagemStream = new ByteArrayInputStream(this.foto);
//        return BitmapFactory.decodeStream(imagemStream);
//    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

//    public void setFoto(Bitmap foto){
//        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//        foto.compress(Bitmap.CompressFormat.JPEG,100,stream);
//        this.foto = stream.toByteArray();
//    }

    public boolean existe(){
        File file = new File(foto);
        if(file.exists()){
            return true;
        }else{
            return false;
        }
    }

}

