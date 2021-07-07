package com.example.mobilegis.Utils;


public class Retorno {

    private TipoRetorno tipo;
    private Object Dados;
    private String mensagem;

    public Retorno(){

    }

    public Retorno(TipoRetorno tipo, Object dados){
        this.setTipo(tipo);
        this.setDados(dados);
    }

    public Retorno(TipoRetorno tipo, Object dados, String mensagem){
        this.setTipo(tipo);
        this.setDados(dados);
        this.setMensagem(mensagem);
    }

    public TipoRetorno getTipo() {
        return tipo;
    }

    public void setTipo(TipoRetorno tipo) {
        this.tipo = tipo;
    }

    public Object getDados() {
        return Dados;
    }

    public void setDados(Object dados) {
        Dados = dados;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }
}
