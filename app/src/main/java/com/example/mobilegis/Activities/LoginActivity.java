package com.example.mobilegis.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.example.mobilegis.APLs.AplAcesso;
import com.example.mobilegis.APLs.AplSincronizaUsuarios;
import com.example.mobilegis.DAOs.UsuarioDAO;
import com.example.mobilegis.MainActivity;
import com.example.mobilegis.Models.Usuario;
import com.example.mobilegis.R;
import com.example.mobilegis.Utils.Retorno;
import com.example.mobilegis.Utils.TipoRetorno;

import static com.example.mobilegis.Config.Constantes.VERSAO_APP;


public class LoginActivity extends AppCompatActivity {

    private EditText txtUsername;
    private EditText txtPassword;
    private Button btnLogar;
    private ImageView btUsuarios;
    private ProgressBar progressBarSincro;
    private TextView textVersion;
    private String ultimoUsuario;
    private boolean click = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        progressBarSincro = findViewById(R.id.progressBarSincro);

        this.txtUsername = (EditText) findViewById(R.id.txtUsername);
        this.txtPassword = (EditText) findViewById(R.id.txtPassword);
        this.btnLogar = (Button)findViewById(R.id.btnLogin);
        this.btUsuarios = (ImageView)findViewById(R.id.btnUsuariosLogin);
        this.textVersion = (TextView) findViewById(R.id.textVersion);

        //Preenche último usuário logado
        preencheUsuario();


        textVersion.setText("Versão "+ VERSAO_APP);

        btnLogar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (!click) {
                    return;
                }
                desabilitaClick();
                tentaLogarHash();
            }
        });

        btUsuarios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!click) { // 1000 = 1second
                    return ;
                }
                desabilitaClick();
                sincronizarUsuarios();
            }
        });

        final int REQUEST_EXTERNAL_STORAGE = 1;
        String[] PERMISSIONS_STORAGE = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }

//        if (BuildConfig.DEBUG) {
//            logaDebug();
//        }
    }

    private void logaDebug(){
        try{
            AplAcesso apl = new AplAcesso();
//            Retorno r = apl.criaEmpresa();


            String login = "admin";
            String senha = "1234";

            if(login != null && senha != null){
                Retorno retorno = apl.tentaLogarHash(login, senha);
                if(retorno.getTipo() == TipoRetorno.SUCESSO){
                    exibeToast("Logado com sucesso!");
                    redirecionaHome((Usuario) retorno.getDados());
                }
                else{
                    exibeToast("Não foi possível logar!");
                }
            }
            else {
                exibeToast("Você precisa preencher o usuario e senha!");
            }
        }
        catch(Exception ex){
            exibeToast(ex.getMessage());
        }
    }




    private void tentaLogarHash(){
        try{
            AplAcesso apl = new AplAcesso();

            String login = txtUsername.getText().toString();
            String senha = txtPassword.getText().toString();

            if(login != null && senha != null){
                Retorno retorno = apl.tentaLogarHash(login, senha);
                if(retorno.getTipo() == TipoRetorno.SUCESSO){
                    exibeToast("Logado com sucesso!");
                    UsuarioDAO usuarioDAO = new UsuarioDAO();
                    if(ultimoUsuario.equals("")){
                        usuarioDAO.criarUltimoUsuario(login);
                    }else{
                        usuarioDAO.atualizarUltimoUsuario(login);
                    }
                    redirecionaHome((Usuario) retorno.getDados());
                }
                else{
                    exibeToast("Não foi possível logar!");
                    habilitaClick();
                }
            }
            else {
                exibeToast("Você precisa preencher o usuario e senha!");
                habilitaClick();
            }
        }
        catch(Exception ex){
            exibeToast(ex.getMessage());
        }
    }

    private void exibeToast(String text){
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }

    private void redirecionaMain(Usuario usuario){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("idUsuario", usuario.getId());
        startActivity(intent);
    }

    private void redirecionaHome(Usuario usuario){
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        intent.putExtra("usuario", usuario);
        startActivity(intent);
    }





    public void sincronizarUsuarios() {
        this.btUsuarios.setColorFilter(Color.parseColor("#1A237E"));

        Handler handlerSincronizar = new Handler(){

            @Override
            public void handleMessage(Message msg) {
                if(msg.arg1 == 0){
                    btUsuarios.setColorFilter(Color.parseColor("#FFFFFF"));
                }
                result(msg);
            }
        };

        AplSincronizaUsuarios apl = new AplSincronizaUsuarios(handlerSincronizar);
        new Thread(apl).start();

    }


    public void result(Message menssage){
        habilitaClick();
        Toast.makeText(this, (String)menssage.obj, Toast.LENGTH_LONG).show();
    }

    private void preencheUsuario(){
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        ultimoUsuario = usuarioDAO.ultimoUsuario();
        if(!ultimoUsuario.equals("")){
            this.txtUsername.setText(usuarioDAO.ultimoUsuario());
            this.txtPassword.requestFocus();
        }
    }

    private void desabilitaClick(){
        click = false;
        progressBarSincro.setVisibility(View.VISIBLE);
    }

    private void habilitaClick(){
        click = true;
        progressBarSincro.setVisibility(View.GONE);
    }


    @Override
    protected void onRestart() {
        habilitaClick();
        super.onRestart();
    }

}
