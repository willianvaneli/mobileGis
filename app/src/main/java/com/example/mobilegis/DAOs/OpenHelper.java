package com.example.mobilegis.DAOs;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;

import com.example.mobilegis.MyApp;

import org.spatialite.database.SQLiteDatabase;
import org.spatialite.database.SQLiteOpenHelper;

import static com.example.mobilegis.Config.Constantes.*;

public class OpenHelper extends SQLiteOpenHelper {

    private static OpenHelper sInstance;
    private SQLiteDatabase db;

    public OpenHelper(Context context){

        super(context, NOME_BANCO, null, VERSAO_BANCO);
        db = getWritableDatabase();
    }

    public static synchronized OpenHelper getInstance() {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new OpenHelper(MyApp.getContext());
        }
        return sInstance;
    }

    @Override
    //Chamado quando o BD for criado pela primeira vez
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        try {

            db.execSQL(SQL_CREATE_TABLE_USUARIOS);
            //insereUsuarioAdmin();
            db.execSQL(SQL_CREATE_USUARIO_ADMIN);
            db.execSQL(SQL_CREATE_TABLE_TERRENOS);
            db.execSQL(SQL_CREATE_TABLE_EDIFICACOES);
            db.execSQL(SQL_CREATE_TABLE_UNIDADES);
            db.execSQL(SQL_CREATE_TABLE_ORDENS_SEVICO);
            db.execSQL(SQL_CREATE_TABLE_TERRENOS_OS);
            db.execSQL(SQL_CREATE_TABLE_VISITAS);
            db.execSQL(SQL_CREATE_TABLE_FOTOS);
            db.execSQL(SQL_CREATE_TABLE_BENFEITORIA);
            db.execSQL(SQL_CREATE_ULTIMO_USUARIO);
            db.execSQL(SQL_CREATE_TABLE_RESPONSAVEL);
            db.execSQL(SQL_CREATE_TABLE_TESTADAS);

            Cursor cursor = db.rawQuery(SQL_INICIALIZA_SPATIALITE, null);
            cursor.moveToFirst();
            String saida = cursor.getString(0);


            cursor = db.rawQuery(SQL_CREATE_GEOMETRIA_TERRENOS, null);
            cursor.moveToFirst();
            saida = cursor.getString(0);

            cursor = db.rawQuery(SQL_CREATE_GEOMETRIA_EDIFICACOES, null);
            cursor.moveToFirst();
            saida = cursor.getString(0);





        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    // Chamado quando temos uma nova versão do BD ou da app
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stuba
        db.execSQL("DROP TABLE IF EXISTS " + NOME_TABELA_USUARIOS);
        db.execSQL("DROP TABLE IF EXISTS " + NOME_TABELA_ORDENS_SERVICO);
        db.execSQL("DROP TABLE IF EXISTS " + NOME_TABELA_TERRENOS);
        db.execSQL("DROP TABLE IF EXISTS " + NOME_TABELA_TERRENOS_OS);
        db.execSQL("DROP TABLE IF EXISTS " + NOME_TABELA_EDIFICACOES);
        db.execSQL("DROP TABLE IF EXISTS " + NOME_TABELA_UNIDADES);
        db.execSQL("DROP TABLE IF EXISTS " + NOME_TABELA_VISITAS);
        db.execSQL("DROP TABLE IF EXISTS " + NOME_TABELA_FOTOS);
        db.execSQL("DROP TABLE IF EXISTS " + NOME_TABELA_BENFEITORIA);
        db.execSQL("DROP TABLE IF EXISTS " + NOME_TABELA_ULTIMO_USUARIO);
        db.execSQL("DROP TABLE IF EXISTS " + NOME_TABELA_RESPONSAVEL);
        db.execSQL("DROP TABLE IF EXISTS " + NOME_TABELA_TESTADAS);

        onCreate(db);
    }
}
