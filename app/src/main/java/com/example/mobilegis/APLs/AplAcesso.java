package com.example.mobilegis.APLs;

import com.example.mobilegis.DAOs.UsuarioDAO;
import com.example.mobilegis.Models.Usuario;


import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import com.example.mobilegis.Utils.Retorno;
import com.example.mobilegis.Utils.TipoRetorno;

public class AplAcesso {

    private UsuarioDAO usuarioDAO;

    public AplAcesso(){

        this.usuarioDAO = new UsuarioDAO();
    }


    public Retorno tentaLogarHash(String login, String senha){

        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(senha.getBytes(StandardCharsets.UTF_8));

            String senhaHash = bytesToHex(encodedhash).toUpperCase();

            Usuario usuario = this.usuarioDAO.buscar(login, senhaHash);


            if(usuario != null){
                return new Retorno(TipoRetorno.SUCESSO, usuario, null);
            }
            else{
                return new Retorno(TipoRetorno.ERRO, null, "Não foi possível logar com a credenciais inseridas!");
            }
        }
        catch (Exception ex){
            return new Retorno(TipoRetorno.ERRO, null, "Ocorreu um erro ao tentar logar. Se o problema persistir, entre em contato com o Administração!");
        }

    }

    public Retorno buscarUsuario(Long id){

        try{
            Usuario usuario = this.usuarioDAO.buscar(id);
            if(usuario != null){
                return new Retorno(TipoRetorno.SUCESSO, usuario, null);
            }
            else{
                return new Retorno(TipoRetorno.ERRO, null, "Não foi possível logar com a credenciais inseridas!");
            }
        }
        catch (Exception ex){
            return new Retorno(TipoRetorno.ERRO, null, "Ocorreu um erro ao tentar logar. Se o problema persistir, entre em contato com o Administração!");
        }

    }


    private static int getRandomNumberInRange(int min, int max) {

        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }

        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }


    private String bytesToHex(byte[] hash) {
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if(hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }


    public String criptografaSenha(String senha) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] encodedhash = digest.digest(senha.getBytes(StandardCharsets.UTF_8));

        return bytesToHex(encodedhash).toUpperCase();
    }


}
