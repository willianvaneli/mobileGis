package com.example.mobilegis.APLs;

import android.os.Handler;
import android.os.Message;

import com.example.mobilegis.DAOs.UsuarioDAO;
import com.example.mobilegis.Utils.WebService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AplSincronizaUsuarios implements Runnable{

    private Handler handler;

    public AplSincronizaUsuarios(Handler handler){
        this.handler = handler;
    }

    @Override
    public void run() {

        Message message = new Message();

        WebService webService = new WebService("http://serra.hiparc.com/api/");
        String resultado = webService.getTabela("usuarios");
        try {
            JSONObject obj = new JSONObject(resultado);
            if(obj.getBoolean("success")) {
                UsuarioDAO usuarioDAO = new UsuarioDAO();
                usuarioDAO.limparUsuarios();

                JSONArray json = obj.getJSONArray("data");
                usuarioDAO.inserirJson(json);

                message.obj = "Usuários carregados com sucesso.";
                message.arg1 = 1;
            }else{
                message.obj = "Não há usuários a serem carregados";
                message.arg1 = 0;
            }


        } catch (JSONException e) {
            message.obj = "Não foi possivel baixar os usuários, verifique a conexão com a internet\nCaso erro persista entre em contato com o suporte";
            message.arg1 = 0;
        } catch (Exception e) {
            e.printStackTrace();
            message.obj = "Falha ao limparUsuarios ";
            message.arg1 = 0;
        }finally {
            handler.sendMessage(message);
        }
    }
}
