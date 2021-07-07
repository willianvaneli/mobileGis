package com.example.mobilegis.DAOs;

import android.content.ContentValues;

import com.example.mobilegis.APLs.AplAcesso;
import com.example.mobilegis.Models.Usuario;
import android.database.Cursor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.spatialite.database.SQLiteDatabase;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import static com.example.mobilegis.Config.Constantes.*;

public class UsuarioDAO implements IDAO<Usuario>{

    private OpenHelper sqliteHelper;

    public UsuarioDAO(){
        this.sqliteHelper = sqliteHelper.getInstance();
    }

    @Override
    public void inserir(Usuario usuario) throws Exception {

        SQLiteDatabase db = sqliteHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("id",usuario.getId());
        values.put("nome", usuario.getNome());
        values.put("login", usuario.getLogin());
        values.put("senha", usuario.getSenha());
        values.put("ativo", usuario.isAtivo() ? 1 : 0);

        db.insert(NOME_TABELA_USUARIOS, null, values);
        db.close();
    }

    @Override
    public void editar(Usuario usuario) throws Exception {

        SQLiteDatabase db = sqliteHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("nome", usuario.getNome());
        values.put("login", usuario.getLogin());
        values.put("senha", usuario.getSenha());
        values.put("ativo", usuario.isAtivo() ? 1 : 0);

        db.update(NOME_TABELA_USUARIOS, values, "id = ?", new String[]{Integer.toString(usuario.getId())});
        db.close();
    }

    @Override
    public void deletar(Usuario usuario) throws Exception {

        SQLiteDatabase db = sqliteHelper.getWritableDatabase();

        db.delete(NOME_TABELA_USUARIOS,"id = ?", new String[]{Integer.toString(usuario.getId())});
        db.close();
    }

    public void limparUsuarios() throws Exception {
        SQLiteDatabase db = sqliteHelper.getWritableDatabase();

        db.execSQL("DELETE FROM " + NOME_TABELA_USUARIOS + ";");

        Usuario usuario = new Usuario();
        usuario.setId(999999);
        usuario.setNome("Super Admin");
        usuario.setLogin("admin");
        usuario.setSenha("2019");
        usuario.setAtivo(true);

        UsuarioDAO usuarioDAO = new UsuarioDAO();
        usuarioDAO.inserir(usuario);

        db.close();
    }

    @Override
    public Usuario buscar(Long id) throws Exception {

        SQLiteDatabase db = sqliteHelper.getReadableDatabase();

        Cursor cursor = db.query(NOME_TABELA_USUARIOS, null, "id = ?", new String[]{id.toString()}, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            Usuario usuario = new Usuario();
            usuario.setId(cursor.getInt(cursor.getColumnIndex("id")));
            usuario.setNome(cursor.getString(cursor.getColumnIndex("nome")));
            usuario.setLogin(cursor.getString(cursor.getColumnIndex("login")));
            usuario.setSenha(cursor.getString(cursor.getColumnIndex("senha")));
            usuario.setAtivo(cursor.getInt(cursor.getColumnIndex("ativo")) == 1);

            db.close();
            return usuario;
        }
        else {
            db.close();
            return null;
        }
    }

    @Override
    public List<Usuario> buscarTodos() {

        List<Usuario> usuarios = new ArrayList<Usuario>();
        SQLiteDatabase db = sqliteHelper.getReadableDatabase();

        Cursor cursor = db.query(NOME_TABELA_USUARIOS, null, null, null, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Usuario usuario = new Usuario();
                usuario.setId(cursor.getInt(cursor.getColumnIndex("id")));
                usuario.setNome(cursor.getString(cursor.getColumnIndex("nome")));
                usuario.setLogin(cursor.getString(cursor.getColumnIndex("login")));
                usuario.setSenha(cursor.getString(cursor.getColumnIndex("senha")));
                usuario.setAtivo(cursor.getInt(cursor.getColumnIndex("ativo")) == 1);

                usuarios.add(usuario);
            } while (cursor.moveToNext());
        }

        db.close();
        return usuarios;
    }

    public Usuario buscar(String login, String senha) throws Exception {

        SQLiteDatabase db = sqliteHelper.getReadableDatabase();

        Cursor cursor = db.query(NOME_TABELA_USUARIOS, null, "login = ? AND senha = ?", new String[]{login, senha}, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            Usuario usuario = new Usuario();
            usuario.setId(cursor.getInt(cursor.getColumnIndex("id")));
            usuario.setNome(cursor.getString(cursor.getColumnIndex("nome")));
            usuario.setLogin(cursor.getString(cursor.getColumnIndex("login")));
            usuario.setSenha(cursor.getString(cursor.getColumnIndex("senha")));
            usuario.setAtivo(cursor.getInt(cursor.getColumnIndex("ativo")) == 1);

            db.close();
            return usuario;
        }
        else {
            db.close();
            return null;
        }
    }

    public boolean existe(int id){
        SQLiteDatabase db = sqliteHelper.getReadableDatabase();

        final String MY_QUERY = "SELECT 0 FROM usuarios WHERE id = ?;";

        Cursor cursor = db.rawQuery(MY_QUERY, new String[]{String.valueOf(id)});

        if (cursor.getCount() > 0) {
            return true;
        }else {
            return false;
        }
    }

    public String ultimoUsuario(){
        SQLiteDatabase db = sqliteHelper.getReadableDatabase();

        final String MY_QUERY = "SELECT * FROM ultimo_usuario;";

        Cursor cursor = db.rawQuery(MY_QUERY, new String[]{});

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            return cursor.getString(cursor.getColumnIndex("ultimo_usuario"));
        }else {
            return "";
        }
    }

    public void createTableUltimoUsuario(){
        SQLiteDatabase db = sqliteHelper.getReadableDatabase();
        db.execSQL(SQL_CREATE_ULTIMO_USUARIO);
        db.close();
    }

    public void atualizarUltimoUsuario(String login) {
        SQLiteDatabase db = sqliteHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("ultimo_usuario", login);

        db.update(NOME_TABELA_ULTIMO_USUARIO, values, "id = 1", new String[]{});
        db.close();
    }

    public void criarUltimoUsuario(String login) {
        SQLiteDatabase db = sqliteHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("id",1);
        values.put("ultimo_usuario", login);

        db.insert(NOME_TABELA_ULTIMO_USUARIO, null, values);
        db.close();
    }


    public void inserirJson(JSONArray json) throws Exception {

        SQLiteDatabase db = sqliteHelper.getWritableDatabase();

        String sqlUsuarios = "INSERT OR IGNORE INTO usuarios "
                + "(id,nome, login, senha, ativo) "
                + "VALUES ";


        List<String> strUsuarios = new ArrayList<String>();

        for (int i = 0; i < json.length(); i++) {

            //Adicionando linha para os
            strUsuarios.add(getValuesUsuarios(json.getJSONObject(i)));
        }

        if (strUsuarios.size() > 0) {
            String valuesOs = strUsuarios.get(0);
            for (int i = 1; i < strUsuarios.size(); i++) {
                valuesOs = valuesOs + ", " + strUsuarios.get(i);
            }
            sqlUsuarios += valuesOs;
            db.execSQL(sqlUsuarios);
        }


        db.close();
    }

    private String getValuesUsuarios (JSONObject jsonObj) throws JSONException, NoSuchAlgorithmException {
        String usuario = "( " + jsonObj.getString("id");
        if(!jsonObj.isNull("nome")){
            usuario += ", '" + jsonObj.getString("nome") + "'";
        }else{usuario += ", NULL";}

        if(!jsonObj.isNull("login")){
            usuario += ", '" + jsonObj.getString("login") + "'";
        }else{usuario += ", NULL";}

        if(!jsonObj.isNull("senha_desc")){
            AplAcesso aplAcesso = new AplAcesso();
            usuario += ", '" + aplAcesso.criptografaSenha(jsonObj.getString("senha_desc")) + "'";
        }else{usuario += ", NULL";}

        if(!jsonObj.isNull("ativo")){
            if(jsonObj.getBoolean("ativo")){
                usuario += ", 1";
            }else{
                usuario += ", 0";
            }
        }else{
            usuario += ", 1";
        }

        usuario += " )";
        return usuario;
    }



}
