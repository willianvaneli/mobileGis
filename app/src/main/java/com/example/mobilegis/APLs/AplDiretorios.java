package com.example.mobilegis.APLs;


import android.os.Environment;

import java.io.File;

public class AplDiretorios implements Runnable{

    public static void criaDiretorios(){
        //Pasta principal
        File pasta = Environment.getExternalStoragePublicDirectory("//CadastroMobile");
        //Verifica se existe a pasta, caso não a mesma é criada
        if (!pasta.exists()) {
            pasta.mkdir();
        }

        //Pasta fotos
        File pastaFotos = Environment.getExternalStoragePublicDirectory("//CadastroMobile/fotos");
        if (!pastaFotos.exists()) {
            pastaFotos.mkdir();
        }

        //Pasta Log
        File pastaLog = Environment.getExternalStoragePublicDirectory("//CadastroMobile/log");
        if (!pastaLog.exists()) {
            pastaLog.mkdir();
        }


        //Pasta Log
        File pastaBackup = Environment.getExternalStoragePublicDirectory("//CadastroMobile/backups");
        if (!pastaBackup.exists()) {
            pastaBackup.mkdir();
        }
    }

    @Override
    public void run() {
        criaDiretorios();
    }
}
