package com.example.mobilegis.Models;


import org.json.JSONException;
import org.json.JSONObject;
import org.locationtech.jts.io.ParseException;

public class Visita {

    private int id;
    private int usuarioId;
    private String dataCadastro;
    private int visitaNumero;
    private String situacao;
    private String observacao;
    private int terrenoId;

    public Visita(){}

    public Visita(JSONObject json)throws JSONException, ParseException {
        this.id = json.getInt("id");
        this.usuarioId = json.getInt("usuario_id");
        this.dataCadastro = json.getString("data_cadastro");
        this.visitaNumero = json.getInt("visita_numero");
        this.situacao = json.getString("situacao");
        this.observacao = json.getString("observacao");
        this.terrenoId = json.getInt("terreno_id");

    }

    public JSONObject toJson() throws JSONException{
        JSONObject json = new JSONObject();

        json.put("id",this.id);
        json.put("usuarioId",this.usuarioId);
        json.put("data_cadastro",this.dataCadastro);
        json.put("visita_numero",this.visitaNumero);
        json.put("situacao",this.situacao);
        json.put("observacao",this.observacao);
        json.put("terreno_id",this.terrenoId);

        return json;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(String dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public int getVisitaNumero() {
        return visitaNumero;
    }

    public void setVisitaNumero(int visitaNumero) {
        this.visitaNumero = visitaNumero;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public int getTerrenoId() {
        return terrenoId;
    }

    public void setTerrenoId(int terrenoId) {
        this.terrenoId = terrenoId;
    }
}
