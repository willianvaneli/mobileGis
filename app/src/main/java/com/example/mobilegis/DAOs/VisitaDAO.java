package com.example.mobilegis.DAOs;


import android.database.Cursor;

import com.example.mobilegis.Models.Visita;

import org.spatialite.database.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import static com.example.mobilegis.Config.Constantes.NOME_TABELA_VISITAS;

public class VisitaDAO implements IDAO<Visita>{
    private OpenHelper sqliteHelper;

    public VisitaDAO (){this.sqliteHelper = OpenHelper.getInstance();}

    @Override
    public void inserir(Visita visita) throws Exception {
        try {
            SQLiteDatabase db = sqliteHelper.getWritableDatabase();

            List<String> lista = new ArrayList<String>();

            String sql = "INSERT INTO visitas "
                    + "(id,terreno_id,usuario_id , data_cadastro, visita_numero, situacao, observacao) "
                    + "VALUES (?,?,?,?,?,?,?);";

            lista.add(Integer.toString(visita.getId()));
            lista.add(Integer.toString(visita.getTerrenoId()));
            lista.add(Integer.toString(visita.getUsuarioId()));
            lista.add(visita.getDataCadastro());
            lista.add(Integer.toString(visita.getVisitaNumero()));
            lista.add(visita.getSituacao());
            lista.add(visita.getObservacao());

            String[] arr = lista.toArray(new String[lista.size()]);
            db.execSQL(sql, arr);
            db.close();

        }catch (Exception e){
            String f = e.getMessage();
        }

    }


    @Override
    public void editar(Visita visita) throws Exception {
        SQLiteDatabase db = sqliteHelper.getWritableDatabase();

        List<String> lista = new ArrayList<String>();

        String sql = "UPDATE visitas "
                + "SET (terreno_id,usuario_id , data_cadastro, visita_numero, situacao, observacao) "
                + "= (?,?,?,?,?,?) " +
                " WHERE id = " + Integer.toString(visita.getId());

        lista.add(Integer.toString(visita.getTerrenoId()));
        lista.add(Integer.toString(visita.getUsuarioId()));
        lista.add(visita.getDataCadastro());
        lista.add(Integer.toString(visita.getVisitaNumero()));
        lista.add(visita.getSituacao());
        lista.add(visita.getObservacao());


        String[] arr = lista.toArray(new String[lista.size()]);
        db.execSQL(sql, arr);
        db.close();

    }

    @Override
    public void deletar(Visita visita) throws Exception {
        SQLiteDatabase db = sqliteHelper.getWritableDatabase();
        db.delete(NOME_TABELA_VISITAS,"id = ?", new String[]{Integer.toString(visita.getId())});
        db.close();
    }

    @Override
    public Visita buscar(Long id) throws Exception {
        SQLiteDatabase db = sqliteHelper.getReadableDatabase();

        Cursor dbCursor = db.query(NOME_TABELA_VISITAS, null, "0", null, null, null, null);
        String[] columnNames = dbCursor.getColumnNames();


        Cursor cursor = db.query(NOME_TABELA_VISITAS, columnNames, "id = ?", new String[]{id.toString()}, null, null, null, null);

        Visita visita = new Visita();

        if (cursor != null && cursor.moveToFirst()) {


            visita.setId(cursor.getInt(cursor.getColumnIndex("id")));
            visita.setTerrenoId(cursor.getInt(cursor.getColumnIndex("terreno_id")));
            visita.setUsuarioId(cursor.getInt(cursor.getColumnIndex("usuario_id")));
            visita.setDataCadastro(cursor.getString(cursor.getColumnIndex("data_cadastro")));
            visita.setVisitaNumero(cursor.getInt(cursor.getColumnIndex("visita_numero")));
            visita.setSituacao(cursor.getString(cursor.getColumnIndex("situacao")));
            visita.setObservacao(cursor.getString(cursor.getColumnIndex("observacao")));

        }

        db.close();
        return visita;
    }



    @Override
    public List<Visita> buscarTodos() throws Exception {
        List<Visita> visitas = new ArrayList<Visita>();

        SQLiteDatabase db = sqliteHelper.getReadableDatabase();

        Cursor dbCursor = db.query(NOME_TABELA_VISITAS, null, "0", null, null, null, null);
        String[] columnNames = dbCursor.getColumnNames();


        Cursor cursor = db.query(NOME_TABELA_VISITAS, columnNames, null, null, null, null, null);



        if (cursor != null && cursor.moveToFirst()) {
            do {
                Visita visita = new Visita();
                visita.setId(cursor.getInt(cursor.getColumnIndex("id")));
                visita.setTerrenoId(cursor.getInt(cursor.getColumnIndex("terreno_id")));
                visita.setUsuarioId(cursor.getInt(cursor.getColumnIndex("usuario_id")));
                visita.setDataCadastro(cursor.getString(cursor.getColumnIndex("data_cadastro")));
                visita.setVisitaNumero(cursor.getInt(cursor.getColumnIndex("visita_numero")));
                visita.setSituacao(cursor.getString(cursor.getColumnIndex("situacao")));
                visita.setObservacao(cursor.getString(cursor.getColumnIndex("observacao")));

                visitas.add(visita);

            }while (cursor.moveToNext());
        }

        db.close();
        return visitas;
    }


    public List<Visita> buscarTodosPorTerreno(int id) throws Exception {
        List<Visita> visitas = new ArrayList<Visita>();

        SQLiteDatabase db = sqliteHelper.getReadableDatabase();

//        Cursor dbCursor = db.query(NOME_TABELA_VISITAS, null, "0", null, null, null, null);
//        String[] columnNames = dbCursor.getColumnNames();
//
//
//        Cursor cursor = db.query(NOME_TABELA_VISITAS, columnNames, "terreno_id = ?", new String[]{Integer.toString(id)}, null, null, null, null);


        Cursor cursor = db.rawQuery("SELECT * FROM visitas WHERE terreno_id = " + Integer.toString(id),null);


        if (cursor != null && cursor.moveToFirst()) {
            do {
                Visita visita = new Visita();
                visita.setId(cursor.getInt(cursor.getColumnIndex("id")));
                visita.setTerrenoId(cursor.getInt(cursor.getColumnIndex("terreno_id")));
                visita.setUsuarioId(cursor.getInt(cursor.getColumnIndex("usuario_id")));
                visita.setDataCadastro(cursor.getString(cursor.getColumnIndex("data_cadastro")));
                visita.setVisitaNumero(cursor.getInt(cursor.getColumnIndex("visita_numero")));
                visita.setSituacao(cursor.getString(cursor.getColumnIndex("situacao")));
                visita.setObservacao(cursor.getString(cursor.getColumnIndex("observacao")));

                visitas.add(visita);

            }while (cursor.moveToNext());
        }

        db.close();
        return visitas;
    }

    public int getMaiorNumeroVisita(int id){
        SQLiteDatabase db = sqliteHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT MAX(visita_numero) as max FROM visitas WHERE terreno_id =  " + Integer.toString(id), null);
        cursor.moveToFirst();
        int num = cursor.getInt(cursor.getColumnIndex("max"));
        db.close();
        return num;
    }

    public int maiorId(){
        SQLiteDatabase db = sqliteHelper.getReadableDatabase();
        int id = 0;
        Cursor cursor = db.rawQuery("SELECT * FROM visitas WHERE id = (SELECT MAX( id ) FROM visitas)",null);
        if(cursor.getCount() > 0){
            cursor.moveToNext();
            id = cursor.getInt(cursor.getColumnIndex("id"));
        }

        return id;
    }


    public int getMaiorId(){
        SQLiteDatabase db = sqliteHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT CASE WHEN MAX(id) > 899999999 " +
                "THEN MAX(id) " +
                "ELSE 900000000 " +
                "END AS id " +
                "FROM visitas;", null);
        cursor.moveToFirst();
        int id = cursor.getInt(cursor.getColumnIndex("id"));
        db.close();
        return id;
    }


    public String visitado(int terreno_id){
        SQLiteDatabase db = sqliteHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT CASE WHEN MAX(id) > 899999999 " +
                "THEN visitas.situacao " +
                "ELSE 'n' " +
                "END AS situacao " +
                "FROM visitas WHERE terreno_id = "+ terreno_id + " ;", null);
        cursor.moveToFirst();
        String situacao = cursor.getString(cursor.getColumnIndex("situacao"));
        db.close();
        return situacao;
    }

}
