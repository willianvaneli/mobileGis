package com.example.mobilegis.Models;

import org.json.JSONException;
import org.json.JSONObject;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;

import java.util.List;

public class Lote implements Cloneable {

    @Override
    public Lote clone() throws CloneNotSupportedException {
        return (Lote) super.clone();
    }



    private List<Foto> fotos;
    private int id;
    private Polygon geom;
    private String atualizacao;
    private String inscricaoImobiliaria;
    private int faceQuadraId;
    private double areaTerrenoMedida = 0.0;
    private double areaTerrenoTributario = 0.0;
    private double comprimentoTestadaMedida = 0.0;
    private double comprimentoTestadaTributario = 0.0;
    private int qtEdificacao;
    private double areaConstruidaLote = 0.0;
    private boolean formaRegular;
    private String situacao;
    private String delimitacao;
    private String topografia;
    private boolean areaDeRisco;
    private String classificacaoDeRisco;
    private String ocupacaoLote;
    private String categoriaPropriedade;
    private String registroImovel;
    private boolean possuiCalcada;
    private String pedologia;
    private String quadra;
    private String lote;
    private String obsCadastro;
    private boolean coletaLixo;
    private boolean transporteColetivo;
    private boolean viaPavimentada;
    private boolean abastecimentoDAgua;
    private boolean iluminacaoPublica;
    private boolean esgotoSanitario;
    private boolean redeEletrica;
    private boolean redeTelefonica;




    // Visitas realizadas referentes ao lote
    private List<Visita> visitas;

    public List<Foto> getFotos() {
        return fotos;
    }

    public void setFotos(List<Foto> fotos) {
        this.fotos = fotos;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Polygon getGeom() {
        return geom;
    }

    public void setGeom(Polygon geom) {
        this.geom = geom;
    }

    public void setGeom(String geomWKT, int srid) throws ParseException {
        if(geomWKT != null && !geomWKT.equals("")){
            WKTReader reader = new WKTReader();
            this.geom = (Polygon) reader.read(geomWKT);
            this.geom.setSRID(srid);

        }
    }



    public boolean isAreaDeRisco() {
        return areaDeRisco;
    }

    public void setAreaDeRisco(boolean areaDeRisco){this.areaDeRisco = areaDeRisco;}

    public String getAtualizacao() {
        return atualizacao;
    }

    public void setAtualizacao(String atualizacao) {
        this.atualizacao = atualizacao;
    }

    public String getInscricaoImobiliaria() {
        return inscricaoImobiliaria;
    }

    public void setInscricaoImobiliaria(String inscricaoImobiliaria) {
        this.inscricaoImobiliaria = inscricaoImobiliaria;
    }

    public int getFaceQuadraId() {
        return faceQuadraId;
    }

    public void setFaceQuadraId(int faceQuadraId) {
        this.faceQuadraId = faceQuadraId;
    }

    public double getAreaTerrenoMedida() {
        return areaTerrenoMedida;
    }

    public void setAreaTerrenoMedida(double areaTerrenoMedida) {
        this.areaTerrenoMedida = areaTerrenoMedida;
    }

    public double getAreaTerrenoTributario() {
        return areaTerrenoTributario;
    }

    public void setAreaTerrenoTributario(double areaTerrenoTributario) {
        this.areaTerrenoTributario = areaTerrenoTributario;
    }

    public double getComprimentoTestadaMedida() {
        return comprimentoTestadaMedida;
    }

    public void setComprimentoTestadaMedida(double comprimentoTestadaMedida) {
        this.comprimentoTestadaMedida = comprimentoTestadaMedida;
    }

    public double getComprimentoTestadaTributario() {
        return comprimentoTestadaTributario;
    }

    public void setComprimentoTestadaTributario(double comprimentoTestadaTributario) {
        this.comprimentoTestadaTributario = comprimentoTestadaTributario;
    }

    public int getQtEdificacao() {
        return qtEdificacao;
    }

    public void setQtEdificacao(int qtEdificacao) {
        this.qtEdificacao = qtEdificacao;
    }

    public double getAreaConstruidaLote() {
        return areaConstruidaLote;
    }

    public void setAreaConstruidaLote(double areaConstruidaLote) {
        this.areaConstruidaLote = areaConstruidaLote;
    }

    public boolean isFormaRegular() {
        return formaRegular;
    }

    public void setFormaRegular(boolean formaRegular) {
        this.formaRegular = formaRegular;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

    public String getDelimitacao() {
        return delimitacao;
    }

    public void setDelimitacao(String delimitacao) {
        this.delimitacao = delimitacao;
    }

    public String getTopografia() {
        return topografia;
    }

    public void setTopografia(String topografia) {
        this.topografia = topografia;
    }

    public String getClassificacaoDeRisco() {
        return classificacaoDeRisco;
    }

    public void setClassificacaoDeRisco(String classificacaoDeRisco) {
        this.classificacaoDeRisco = classificacaoDeRisco;
    }

    public String getOcupacaoLote() {
        return ocupacaoLote;
    }

    public void setOcupacaoLote(String ocupacaoLote) {
        this.ocupacaoLote = ocupacaoLote;
    }

    public String getCategoriaPropriedade() {
        return categoriaPropriedade;
    }

    public void setCategoriaPropriedade(String categoriaPropriedade) {
        this.categoriaPropriedade = categoriaPropriedade;
    }


    public String getRegistroImovel() {
        return registroImovel;
    }

    public void setRegistroImovel(String registroImovel) {
        this.registroImovel = registroImovel;
    }

    public boolean isPossuiCalcada() {
        return possuiCalcada;
    }

    public void setPossuiCalcada(boolean possuiCalcada) {
        this.possuiCalcada = possuiCalcada;
    }



    public List<Visita> getVisitas() {
        return visitas;
    }

    public void setVisitas(List<Visita> visitas) {
        this.visitas = visitas;
    }

    public String getPedologia() {
        return pedologia;
    }

    public void setPedologia(String pedologia) {
        this.pedologia = pedologia;
    }


    // VERIFICA SE É NULL, CASO SEJA RETORNA VALOR PADRÃO, CASO NÃO RETORNA VALOR
    private boolean validaBoleano(JSONObject json, String campo, boolean padrao) throws JSONException {
        if (json.isNull(campo))
            return padrao;
        return json.getBoolean(campo);
    }

    public String getQuadra() {
        return quadra;
    }

    public void setQuadra(String quadra) {
        this.quadra = quadra;
    }

    public String getLote() {
        return lote;
    }

    public void setLote(String lote) {
        this.lote = lote;
    }

    public String getObsCadastro() {
        return obsCadastro;
    }

    public void setObsCadastro(String obsCadastro) {
        this.obsCadastro = obsCadastro;
    }

    public boolean isColetaLixo() {
        return coletaLixo;
    }

    public void setColetaLixo(boolean coletaLixo) {
        this.coletaLixo = coletaLixo;
    }

    public boolean isTransporteColetivo() {
        return transporteColetivo;
    }

    public void setTransporteColetivo(boolean transporteColetivo) {
        this.transporteColetivo = transporteColetivo;
    }

    public boolean isViaPavimentada() {
        return viaPavimentada;
    }

    public void setViaPavimentada(boolean viaPavimentada) {
        this.viaPavimentada = viaPavimentada;
    }

    public boolean isAbastecimentoDAgua() {
        return abastecimentoDAgua;
    }

    public void setAbastecimentoDAgua(boolean abastecimentoDAgua) {
        this.abastecimentoDAgua = abastecimentoDAgua;
    }

    public boolean isIluminacaoPublica() {
        return iluminacaoPublica;
    }

    public void setIluminacaoPublica(boolean iluminacaoPublica) {
        this.iluminacaoPublica = iluminacaoPublica;
    }

    public boolean isEsgotoSanitario() {
        return esgotoSanitario;
    }

    public void setEsgotoSanitario(boolean esgotoSanitario) {
        this.esgotoSanitario = esgotoSanitario;
    }

    public boolean isRedeEletrica() {
        return redeEletrica;
    }

    public void setRedeEletrica(boolean redeEletrica) {
        this.redeEletrica = redeEletrica;
    }

    public boolean isRedeTelefonica() {
        return redeTelefonica;
    }

    public void setRedeTelefonica(boolean redeTelefonica) {
        this.redeTelefonica = redeTelefonica;
    }
}
