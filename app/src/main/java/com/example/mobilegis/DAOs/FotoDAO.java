package com.example.mobilegis.DAOs;


import android.database.Cursor;

import com.example.mobilegis.Models.Foto;

import org.spatialite.database.SQLiteDatabase;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.example.mobilegis.Config.Constantes.NOME_TABELA_FOTOS;
import static com.example.mobilegis.Config.Constantes.SQL_CREATE_TABLE_FOTOS;


public class FotoDAO implements IDAO<Foto> {
    private OpenHelper sqliteHelper;

    public FotoDAO(){
        this.sqliteHelper = sqliteHelper.getInstance();
    }

    @Override
    public void inserir(Foto foto) throws Exception {
        SQLiteDatabase db = sqliteHelper.getWritableDatabase();

        List<String> lista = new ArrayList<String>();

        String sql = "INSERT INTO fotos "
                +"(id, pai_id, pai_tipo, foto) "
                + "VALUES (?,?,?,?);";
        lista.add(String.valueOf(foto.getId()));
        lista.add(String.valueOf(foto.getPaiId()));
        lista.add(foto.getPaiTipo());
        lista.add(foto.getFoto());

        String[] arr = lista.toArray(new String[lista.size()]);
        db.execSQL(sql, arr);
        db.close();

    }

    @Override
    public void editar(Foto foto) throws Exception {
        SQLiteDatabase db = sqliteHelper.getWritableDatabase();

        List<String> lista = new ArrayList<String>();

        String sql = "UPDATE fotos "
                + "SET (pai_id, pai_tipo, foto) "
                + "= (?,?,?) "
                + "WHERE id = " + foto.getId() + " ;";

        lista.add(String.valueOf(foto.getPaiId()));
        lista.add(foto.getPaiTipo());
        lista.add(foto.getFoto());

        String[] arr = lista.toArray(new String[lista.size()]);
        db.execSQL(sql, arr);
        db.close();
    }

    @Override
    public void deletar(Foto foto) throws Exception {
        SQLiteDatabase db = sqliteHelper.getWritableDatabase();
        db.delete(NOME_TABELA_FOTOS,"id = ?", new String[]{Integer.toString(foto.getId())});
        boolean p = new File(foto.getFoto()).delete();
        db.close();
    }

    @Override
    public Foto buscar(Long id) throws Exception {
        SQLiteDatabase db = sqliteHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM fotos WHERE id = ? ;", new String[]{id.toString()});

        Foto foto = new Foto();
        if (cursor != null && cursor.moveToFirst()) {
            foto.setId(cursor.getInt(cursor.getColumnIndex("id")));
            foto.setPaiId(cursor.getInt(cursor.getColumnIndex("pai_id")));
            foto.setPaiTipo(cursor.getString(cursor.getColumnIndex("pai_tipo")));
            foto.setFoto(cursor.getString(cursor.getColumnIndex("foto")));
            if(!foto.existe()){
                deletar(foto);
            }

        }
        db.close();
        return foto;
    }

    @Override
    public List buscarTodos() throws Exception {

        return null;
    }

    public List<Foto> buscarTodosPorPai(int paiId, String paiTipo) throws Exception {
        List<Foto> fotos = new ArrayList<Foto>();
        SQLiteDatabase db = sqliteHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM fotos WHERE pai_id = ? AND pai_tipo = ?;", new String[]{String.valueOf(paiId),paiTipo});
        if (cursor != null && cursor.moveToFirst()) {

            do {
                Foto foto = new Foto();

                foto.setId(cursor.getInt(cursor.getColumnIndex("id")));
                foto.setPaiId(cursor.getInt(cursor.getColumnIndex("pai_id")));
                foto.setPaiTipo(cursor.getString(cursor.getColumnIndex("pai_tipo")));
                foto.setFoto(cursor.getString(cursor.getColumnIndex("foto")));

                if(!foto.existe() ){
                    deletar(foto);
                }else{
                    fotos.add(foto);
                }

            } while (cursor.moveToNext());

        }
        db.close();
        return fotos;
    }

    public int getMaiorId(){
        SQLiteDatabase db = sqliteHelper.getReadableDatabase();
        String sql = "SELECT CASE WHEN MAX(id) > 899999999 " +
                "THEN MAX(id) " +
                "ELSE 900000000 " +
                "END id " +
                "FROM fotos;";
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        int id = cursor.getInt(cursor.getColumnIndex("id"));
        db.close();
        return id;
    }


    public void createTable(){
        SQLiteDatabase db = sqliteHelper.getReadableDatabase();
        db.execSQL(SQL_CREATE_TABLE_FOTOS);
        db.close();
    }

    public void droTable(){
        SQLiteDatabase db = sqliteHelper.getReadableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + NOME_TABELA_FOTOS);
        db.close();
    }

}
