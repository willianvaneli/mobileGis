package com.example.mobilegis.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;

import com.example.mobilegis.Adapters.FotoAdapter;
import com.example.mobilegis.DAOs.FotoDAO;
import com.example.mobilegis.Models.Foto;
import com.example.mobilegis.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FotoActivity extends AppCompatActivity {
    private final int GALERIA_IMAGENS = 1;
    private final int PERMISSAO_REQUEST = 2;
    private int ultimoId;
    private final int CAMERA = 4;
    private File arquivoFoto = null;
    private ProgressBar progressBarFoto;
    private boolean click = true;

    int paiId;
    String paiTipo;
    List<Foto> fotos;
    private FotoAdapter fotoAdapter;
    private List<Foto> fotosDel = new ArrayList<Foto>();

    FloatingActionButton fabInserir;
    FloatingActionButton fabDeletar;
    FloatingActionButton fabArq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foto);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},0);
        }
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)){

            }else{
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},PERMISSAO_REQUEST);
            }
        }
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)){

            }else{
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},PERMISSAO_REQUEST);
            }
        }

        this.paiId = getIntent().getExtras().getInt("paiId");
        this.paiTipo = getIntent().getExtras().getString("paiTipo");

        final GridView gridView = (GridView)findViewById(R.id.gridViewFoto);

        gridView.setHasTransientState(true);

        final FotoDAO fotoDAO = new FotoDAO();
        try {
            fotos = fotoDAO.buscarTodosPorPai(paiId,paiTipo);
            fotoAdapter = new FotoAdapter(this,fotos);
            gridView.setAdapter(fotoAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }



        gridView.setOnItemClickListener(new GridView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                if (!fotosDel.contains(fotos.get(position))) {
                    View tv = (View) gridView.getChildAt(position);
                    tv.setBackgroundColor(Color.BLUE);
                    tv.setAlpha(0.5f);
                    fotosDel.add(fotos.get(position));
                    fabDeletar.show();

                } else {
                    View tv = (View) gridView.getChildAt(position);
                    tv.setBackgroundColor(Color.TRANSPARENT);
                    tv.setAlpha(1.0f);
                    fotosDel.remove(fotos.get(position));
                    if (fotosDel.size() == 0) {
                        fabDeletar.hide();
                    }
                }
            }
        });

        fabInserir = findViewById(R.id.fab_inserir_foto);
        fabInserir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!click) { // 1000 = 1second
                    return;
                }
                click = false;
//                progressBarFoto.setVisibility(View.VISIBLE);
                tirarFoto();
//                progressBarFoto.setVisibility(View.GONE);
            }
        });

        fabDeletar = findViewById(R.id.fab_deletar_foto);
        fabDeletar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!click) { // 1000 = 1second
                    return;
                }
                click = false;
                FotoDAO delete = new FotoDAO();
                if(fotosDel.size() > 0) {
                    for (int i = 0; i < fotosDel.size(); i++) {
                        try {
                            delete.deletar(fotosDel.get(i));
//                            boolean p = new File(fotosDel.get(i).getFoto()).delete();
                            fotos.remove(fotosDel.get(i));
                            Toast.makeText(view.getContext(), "Deletado com sucesso!", Toast.LENGTH_SHORT).show();

                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(view.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    fotoAdapter.atualizaFotos(fotos);
                }
                fabDeletar.hide();
                click = true;
            }
        });

        fabArq = findViewById(R.id.fab_arq);
        fabArq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!click) { // 1000 = 1second
                    return;
                }
                click = false;
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, GALERIA_IMAGENS);

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_foto, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (!click) { // 1000 = 1second
            return true;
        }
        click = false;
        int id = item.getItemId();

        switch (id){

            case R.id.action_sair_fotos:
                finish();
                break;

        }

        return super.onOptionsItemSelected(item);
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == GALERIA_IMAGENS) {
            Uri selectedImage = data.getData();
            String[] filePath = { MediaStore.Images.Media.DATA };
            Cursor c = getContentResolver().query(selectedImage,filePath, null, null, null);
            c.moveToFirst();
            int columnIndex = c.getColumnIndex(filePath[0]);
            String picturePath = c.getString(columnIndex);
            c.close();

            Bitmap thumbnail = BitmapFactory.decodeFile(picturePath);
            String patch = escreveImagens(thumbnail);
//            Uri photoURI = FileProvider.getUriForFile(getBaseContext(),getBaseContext().getApplicationContext().getPackageName() + ".provider"  , arquivoFoto);
            salvaFoto(patch);
//            carregaImagem();
            fotoAdapter.atualizaFotos(fotos);
        }
        if (requestCode == CAMERA && resultCode == RESULT_OK) {
            sendBroadcast(new Intent( Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,Uri.fromFile(arquivoFoto)));
            salvaFoto(arquivoFoto.getAbsolutePath());
//            carregaImagem();
            fotoAdapter.atualizaFotos(fotos);
        }
        click = true;
    }


    private void salvaFoto(String path){
        Foto foto = new Foto();
        foto.setFoto(path);
        FotoDAO fotoDAO = new FotoDAO();
        ultimoId = fotoDAO.getMaiorId() + 1;
        foto.setId(ultimoId);
        foto.setPaiId(paiId);
        foto.setPaiTipo(paiTipo);

        try {
            fotoDAO.inserir(foto);
            fotos.add(foto);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public String escreveImagens(Bitmap bmp){
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


    private File criarArquivo() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File pasta = Environment.getExternalStoragePublicDirectory("//CadastroMobile/fotos");
        //Verifica se existe a pasta, caso não a mesma é criada
        if (!pasta.exists()) {
            pasta.mkdir();
        }

        File imagem = new File(pasta.getPath() + File.separator + "CM_" + timeStamp + ".jpg");
        return imagem;
    }


    private void tirarFoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            try {
                arquivoFoto = criarArquivo();
            } catch (IOException ex) {
                // Manipulação em caso de falha de criação do arquivo
            }
            if (arquivoFoto != null) {
                Uri photoURI = FileProvider.getUriForFile(getBaseContext(),getBaseContext().getApplicationContext().getPackageName() + ".provider"  , arquivoFoto);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA);
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        if (requestCode == PERMISSAO_REQUEST) {
            if (grantResults.length > 0  && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // A permissão foi concedida. Pode continuar
            } else {
                // A permissão foi negada. Precisa ver o que deve ser desabilitado
            }
            return;
        }
    }


}
