package com.example.mobilegis.Adapters;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.mobilegis.Models.Foto;

import java.util.List;

public class FotoAdapter extends BaseAdapter {
    private Context context;
    private List<Foto> fotos;

    public FotoAdapter(Context context, List<Foto> fotos){
        this.context = context;
        this.fotos = fotos;
    }

    public void atualizaFotos(List<Foto> fotos){
        this.fotos = fotos;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return fotos.size();
    }

    @Override
    public Object getItem(int position) {
        return fotos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if((fotos.size() > 0) && (fotos != null)) {
            ImageView imageView = new ImageView(context);
//            FotoDAO fotoDAO = new FotoDAO();
            try {
                int targetW = 160;
                int targetH = 160;

//                Foto foto = (Foto) fotoDAO.buscar((long)fotos.get(position).getId());
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                bmOptions.inJustDecodeBounds= true;
                BitmapFactory.decodeFile(fotos.get(position).getFoto(), bmOptions);

                int photoW = bmOptions.outWidth;
                int photoH = bmOptions.outHeight;

                int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

                bmOptions.inJustDecodeBounds= false;
                bmOptions.inSampleSize= scaleFactor;

                Bitmap thumbnail = (BitmapFactory.decodeFile(fotos.get(position).getFoto(), bmOptions));
                Matrix matrix = new Matrix();
                matrix.postRotate(90);
                Bitmap rotated = Bitmap.createBitmap(thumbnail, 0, 0, thumbnail.getWidth(), thumbnail.getHeight(), matrix, true);
                imageView.setImageBitmap(rotated);
                imageView.setAdjustViewBounds(true);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return imageView;
        }

        return null;
    }
}
