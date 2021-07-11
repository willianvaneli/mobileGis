package com.example.mobilegis.DAOs;

import android.database.Cursor;

import com.example.mobilegis.Models.Lote;

import org.spatialite.database.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.example.mobilegis.Config.Constantes.NOME_TABELA_TERRENOS;
import static com.example.mobilegis.Config.Constantes.SRID;

public class LoteDAO implements IDAO<Lote> {
    private OpenHelper sqliteHelper;

    public LoteDAO(){
        this.sqliteHelper = sqliteHelper.getInstance();
    }

    @Override
    public void inserir(Lote lote) throws Exception {
        try{
            SQLiteDatabase db = sqliteHelper.getWritableDatabase();

            List<String> lista = new ArrayList<String>();

            String sql = "INSERT INTO terrenos "
                    +"(id, atualizacao, inscricao_imobiliaria, face_quadra_id_principal, area_terreno_medida, area_terreno_tributario, comprimento_testada, comprimento_testada_tributario, quant_edificacao, area_construida_lote, forma_regular, situacao, delimitacao, topografia, classificacao_risco, ocupacao_lote, categoria_propriedade, area_risco, registro_imovel, possui_calcada, coleta_lixo, transporte_coletivo, via_pavimentada, abastecimento_dagua, iluminacao_publica, esgoto_sanitario, rede_eletrica, rede_telefonica, pedologia,quadra,lote,obs_cadastro, geom) "
                    + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,@GEOMETRIA);";

            lista.add(Objects.toString(lote.getId()));
            if(lote.getId() > 899999999 ){
                lista.add("inclusao");
            }else{
                lista.add("sem_alteracao");
            }
            lista.add(lote.getInscricaoImobiliaria());
            lista.add(Objects.toString(lote.getFaceQuadraId()));
            lista.add(Objects.toString(lote.getAreaTerrenoMedida()));
            lista.add(Objects.toString(lote.getAreaTerrenoTributario()));
            lista.add(Objects.toString(lote.getComprimentoTestadaMedida()));
            lista.add(Objects.toString(lote.getComprimentoTestadaTributario()));
            lista.add(Objects.toString(lote.getQtEdificacao()));
            lista.add(Objects.toString(lote.getAreaConstruidaLote()));
            if (lote.isFormaRegular()){
                lista.add("1");
            }else{
                lista.add("0");
            }
            lista.add(lote.getSituacao());
            lista.add(lote.getDelimitacao());
            lista.add(lote.getTopografia());
            lista.add(lote.getClassificacaoDeRisco());
            lista.add(lote.getOcupacaoLote());
            lista.add(lote.getCategoriaPropriedade());
//            lista.add(lote.getInscricaoAnterior());
            if (lote.isAreaDeRisco()){
                lista.add("1");
            }else{
                lista.add("0");
            }
            lista.add(lote.getRegistroImovel());
            if (lote.isPossuiCalcada()){
                lista.add("1");
            }else{
                lista.add("0");
            }
            if (lote.isColetaLixo()){
                lista.add("1");
            }else{
                lista.add("0");
            }
            if (lote.isTransporteColetivo()){
                lista.add("1");
            }else{
                lista.add("0");
            }
            if (lote.isViaPavimentada()){
                lista.add("1");
            }else{
                lista.add("0");
            }
            if (lote.isAbastecimentoDAgua()){
                lista.add("1");
            }else{
                lista.add("0");
            }
            if (lote.isIluminacaoPublica()){
                lista.add("1");
            }else{
                lista.add("0");
            }
            if (lote.isEsgotoSanitario()){
                lista.add("1");
            }else{
                lista.add("0");
            }
            if (lote.isRedeEletrica()){
                lista.add("1");
            }else{
                lista.add("0");
            }
            if (lote.isRedeTelefonica()){
                lista.add("1");
            }else{
                lista.add("0");
            }
            lista.add(lote.getPedologia());

            lista.add(lote.getQuadra());
            lista.add(lote.getLote());
            lista.add(lote.getObsCadastro());

            String textGeom = String.format("GeomFromText('%s' , %d )", lote.getGeom().toText(), lote.getGeom().getSRID() );

            if(lote.getGeom().getSRID() != SRID){
                textGeom = String.format("transform( %s , %d )", textGeom, SRID);
            }
            sql = sql.replace("@GEOMETRIA",textGeom);

            String[] arr = lista.toArray(new String[lista.size()]);
            db.execSQL(sql, arr);
            db.close();

            if (lote.getFotos() != null){
                FotoDAO fotoDAO = new FotoDAO();
                for (int i =0; i < lote.getFotos().size(); i++){
                    fotoDAO.inserir(lote.getFotos().get(i));
                }
            }



        }catch (Exception e){
            String f = e.getMessage();
        }
    }

    @Override
    public void editar(Lote lote) throws Exception {
        SQLiteDatabase db = sqliteHelper.getWritableDatabase();

        List<String> lista = new ArrayList<String>();

        String sql = "UPDATE terrenos "
                +"SET (atualizacao, inscricao_imobiliaria, face_quadra_id_principal, area_terreno_medida, area_terreno_tributario, comprimento_testada, comprimento_testada_tributario, quant_edificacao, area_construida_lote, forma_regular, situacao, delimitacao, topografia, classificacao_risco, ocupacao_lote, categoria_propriedade, area_risco, registro_imovel, possui_calcada, coleta_lixo, transporte_coletivo, via_pavimentada, abastecimento_dagua, iluminacao_publica, esgoto_sanitario, rede_eletrica, rede_telefonica, pedologia,quadra,lote, obs_cadastro, geom) "
                + "= (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,@GEOMETRIA) "
                + "WHERE id = " + lote.getId() + " ;";

        if(lote.getId() > 899999999 ){
            lista.add("inclusao");
        }else{
            lista.add("alteracao");
        }
        lista.add(lote.getInscricaoImobiliaria());
        lista.add(Objects.toString(lote.getFaceQuadraId()));
        lista.add(Objects.toString(lote.getAreaTerrenoMedida()));
        lista.add(Objects.toString(lote.getAreaTerrenoTributario()));
        lista.add(Objects.toString(lote.getComprimentoTestadaMedida()));
        lista.add(Objects.toString(lote.getComprimentoTestadaTributario()));
        lista.add(Objects.toString(lote.getQtEdificacao()));
        lista.add(Objects.toString(lote.getAreaConstruidaLote()));
        if (lote.isFormaRegular()){
            lista.add("1");
        }else{
            lista.add("0");
        }
        lista.add(lote.getSituacao());
        lista.add(lote.getDelimitacao());
        lista.add(lote.getTopografia());
        lista.add(lote.getClassificacaoDeRisco());
        lista.add(lote.getOcupacaoLote());
        lista.add(lote.getCategoriaPropriedade());
        if (lote.isAreaDeRisco()){
            lista.add("1");
        }else{
            lista.add("0");
        }
        lista.add(lote.getRegistroImovel());
        if (lote.isPossuiCalcada()){
            lista.add("1");
        }else{
            lista.add("0");
        }
        if (lote.isColetaLixo()){
            lista.add("1");
        }else{
            lista.add("0");
        }
        if (lote.isTransporteColetivo()){
            lista.add("1");
        }else{
            lista.add("0");
        }
        if (lote.isViaPavimentada()){
            lista.add("1");
        }else{
            lista.add("0");
        }
        if (lote.isAbastecimentoDAgua()){
            lista.add("1");
        }else{
            lista.add("0");
        }
        if (lote.isIluminacaoPublica()){
            lista.add("1");
        }else{
            lista.add("0");
        }
        if (lote.isEsgotoSanitario()){
            lista.add("1");
        }else{
            lista.add("0");
        }
        if (lote.isRedeEletrica()){
            lista.add("1");
        }else{
            lista.add("0");
        }
        if (lote.isRedeTelefonica()){
            lista.add("1");
        }else{
            lista.add("0");
        }

        lista.add(lote.getPedologia());
        lista.add(lote.getQuadra());
        lista.add(lote.getLote());
        lista.add(lote.getObsCadastro());

        String textGeom = String.format("GeomFromText( '%s' , %d )", lote.getGeom().toText(), lote.getGeom().getSRID() );

        if(lote.getGeom().getSRID() != SRID){
            textGeom = String.format("transform( %s , %d )", textGeom, SRID);
        }

        sql = sql.replace("@GEOMETRIA",textGeom);


        String[] arr = lista.toArray(new String[lista.size()]);
        db.execSQL(sql, arr);
        db.close();


        if (lote.getFotos() != null){
            FotoDAO fotoDAO = new FotoDAO();
            for (int i =0; i < lote.getFotos().size(); i++){
                fotoDAO.editar(lote.getFotos().get(i));
            }
        }

    }

    @Override
    public void deletar(Lote lote) throws Exception {
        if (lote.getId()> 899999999){
            excluir(lote);
        }else{
            deletarAt(lote);
        }
    }

    private void deletarAt(Lote lote) throws Exception {
        SQLiteDatabase db = sqliteHelper.getWritableDatabase();

        String sql = "UPDATE terrenos "
                +"SET atualizacao = 'exclusao' "
                + "WHERE id = " + lote.getId() + " ;";
        db.execSQL(sql);

        if (lote.getFotos() != null) {
            FotoDAO fotoDAO = new FotoDAO();
            for (int i = 0; i < lote.getFotos().size(); i++) {
                fotoDAO.deletar(lote.getFotos().get(i));
            }
        }

        db.close();
    }

    public void deletarExclusivo(Lote lote) throws Exception {
        SQLiteDatabase db = sqliteHelper.getWritableDatabase();

        String sql = "UPDATE terrenos "
                +"SET atualizacao = 'exclusao' "
                + "WHERE id = " + lote.getId() + " ;";
        db.execSQL(sql);

        if (lote.getFotos() != null){
            FotoDAO fotoDAO = new FotoDAO();
            for (int i =0; i < lote.getFotos().size(); i++){
                fotoDAO.deletar(lote.getFotos().get(i));
            }
        }

        db.close();
    }


    public void excluir(Lote lote) throws Exception {
        SQLiteDatabase db = sqliteHelper.getWritableDatabase();
        db.delete(NOME_TABELA_TERRENOS,"id = ?", new String[]{Integer.toString(lote.getId())});

        if (lote.getFotos() != null){
            FotoDAO fotoDAO = new FotoDAO();
            for (int i =0; i < lote.getFotos().size(); i++){
                fotoDAO.deletar(lote.getFotos().get(i));
            }
        }

        db.close();
    }

    @Override
    public Lote buscar(Long id) throws Exception {
        return this.buscar(id, SRID);
    }


    public Lote buscar(Long id, int srid) throws Exception
    {
        SQLiteDatabase db = sqliteHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT terrenos.*, ST_AsText(Transform(geom,"+ srid + ")) as geom_wkt FROM terrenos WHERE id = ?;", new String[]{id.toString()});

        Lote lote = new Lote();

        if (cursor != null && cursor.moveToFirst()) {

            String valor;

            lote.setId(cursor.getInt(cursor.getColumnIndex("id")));

            valor = cursor.getString(cursor.getColumnIndex("atualizacao"));
            if(!valor.equals("null"))
                lote.setAtualizacao(valor);

            valor = cursor.getString(cursor.getColumnIndex("inscricao_imobiliaria"));
            if(!valor.equals("null"))
                lote.setInscricaoImobiliaria(valor);

            lote.setFaceQuadraId(cursor.getInt(cursor.getColumnIndex("face_quadra_id_principal")));

            lote.setAreaTerrenoMedida(cursor.getDouble(cursor.getColumnIndex("area_terreno_medida")));
            lote.setAreaTerrenoTributario(cursor.getDouble(cursor.getColumnIndex("area_terreno_tributario")));
            lote.setComprimentoTestadaMedida(cursor.getDouble(cursor.getColumnIndex("comprimento_testada")));
            lote.setComprimentoTestadaTributario(cursor.getDouble(cursor.getColumnIndex("comprimento_testada_tributario")));
            lote.setQtEdificacao(cursor.getInt(cursor.getColumnIndex("quant_edificacao")));
            lote.setAreaConstruidaLote(cursor.getDouble(cursor.getColumnIndex("area_construida_lote")));

            if (cursor.getInt(cursor.getColumnIndex("forma_regular"))==1){
                lote.setFormaRegular(true);
            }else {
                lote.setFormaRegular(false);
            }

            valor = cursor.getString(cursor.getColumnIndex("situacao"));
            if(!valor.equals("null"))
                lote.setSituacao(valor);

            valor = cursor.getString(cursor.getColumnIndex("delimitacao"));
            if(!valor.equals("null"))
                lote.setDelimitacao(cursor.getString(cursor.getColumnIndex("delimitacao")));

            valor = cursor.getString(cursor.getColumnIndex("topografia"));
            if(!valor.equals("null"))
                lote.setTopografia(valor);

            valor = cursor.getString(cursor.getColumnIndex("classificacao_risco"));
            if(!valor.equals("null"))
                lote.setClassificacaoDeRisco(valor);

            valor = cursor.getString(cursor.getColumnIndex("ocupacao_lote"));
            if(!valor.equals("null"))
                lote.setOcupacaoLote(valor);

            valor = cursor.getString(cursor.getColumnIndex("categoria_propriedade"));
            if(!valor.equals("null"))
                lote.setCategoriaPropriedade(valor);

            if (cursor.getInt(cursor.getColumnIndex("area_risco"))==1){
                lote.setAreaDeRisco(true);
            }else {
                lote.setAreaDeRisco(false);
            }

            valor = cursor.getString(cursor.getColumnIndex("registro_imovel"));
            if(!valor.equals("null"))
                lote.setRegistroImovel(valor);

            if (cursor.getInt(cursor.getColumnIndex("possui_calcada"))==1){
                lote.setPossuiCalcada(true);
            }else {
                lote.setPossuiCalcada(false);
            }
            if (cursor.getInt(cursor.getColumnIndex("coleta_lixo"))==1){
                lote.setColetaLixo(true);
            }else {
                lote.setColetaLixo(false);
            }
            if (cursor.getInt(cursor.getColumnIndex("transporte_coletivo"))==1){
                lote.setTransporteColetivo(true);
            }else {
                lote.setTransporteColetivo(false);
            }
            if (cursor.getInt(cursor.getColumnIndex("via_pavimentada"))==1){
                lote.setViaPavimentada(true);
            }else {
                lote.setViaPavimentada(false);
            }
            if (cursor.getInt(cursor.getColumnIndex("abastecimento_dagua"))==1){
                lote.setAbastecimentoDAgua(true);
            }else {
                lote.setAbastecimentoDAgua(false);
            }
            if (cursor.getInt(cursor.getColumnIndex("iluminacao_publica"))==1){
                lote.setIluminacaoPublica(true);
            }else {
                lote.setIluminacaoPublica(false);
            }
            if (cursor.getInt(cursor.getColumnIndex("esgoto_sanitario"))==1){
                lote.setEsgotoSanitario(true);
            }else {
                lote.setEsgotoSanitario(false);
            }
            if (cursor.getInt(cursor.getColumnIndex("rede_eletrica"))==1){
                lote.setRedeEletrica(true);
            }else {
                lote.setRedeEletrica(false);
            }
            if (cursor.getInt(cursor.getColumnIndex("rede_telefonica"))==1){
                lote.setRedeTelefonica(true);
            }else {
                lote.setRedeTelefonica(false);
            }

            valor = cursor.getString(cursor.getColumnIndex("pedologia"));
            if(!valor.equals("null"))
                lote.setPedologia(valor);
            else
                lote.setPedologia("");

            valor = cursor.getString(cursor.getColumnIndex("quadra"));
            if(!valor.equals("null"))
                lote.setQuadra(valor);


            valor = cursor.getString(cursor.getColumnIndex("lote"));
            if(!valor.equals("null"))
                lote.setLote(valor);

            valor = cursor.getString(cursor.getColumnIndex("obs_cadastro"));
            if(!valor.equals("null"))
                lote.setObsCadastro(valor);


            lote.setGeom(cursor.getString(cursor.getColumnIndex("geom_wkt")), srid);

            FotoDAO fotoDAO = new FotoDAO();
            lote.setFotos(fotoDAO.buscarTodosPorPai(lote.getId(),"terreno"));


            VisitaDAO visitaDAO = new VisitaDAO();
            lote.setVisitas(visitaDAO.buscarTodosPorTerreno(lote.getId()));

        }
        db.close();
        return lote;
    }

    public Lote buscaSuave(Long id, int srid) throws Exception
    {
        SQLiteDatabase db = sqliteHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT terrenos.*, ST_AsText(Transform(geom,"+ srid + ")) as geom_wkt FROM terrenos WHERE id = ?;", new String[]{id.toString()});

        Lote lote = new Lote();

        if (cursor != null && cursor.moveToFirst()) {

            String valor;

            lote.setId(cursor.getInt(cursor.getColumnIndex("id")));

            valor = cursor.getString(cursor.getColumnIndex("atualizacao"));
            if(!valor.equals("null"))
                lote.setAtualizacao(valor);

            valor = cursor.getString(cursor.getColumnIndex("inscricao_imobiliaria"));
            if(!valor.equals("null"))
                lote.setInscricaoImobiliaria(valor);

            lote.setFaceQuadraId(cursor.getInt(cursor.getColumnIndex("face_quadra_id_principal")));

            lote.setAreaTerrenoMedida(cursor.getDouble(cursor.getColumnIndex("area_terreno_medida")));
            lote.setAreaTerrenoTributario(cursor.getDouble(cursor.getColumnIndex("area_terreno_tributario")));
            lote.setComprimentoTestadaMedida(cursor.getDouble(cursor.getColumnIndex("comprimento_testada")));
            lote.setComprimentoTestadaTributario(cursor.getDouble(cursor.getColumnIndex("comprimento_testada_tributario")));
            lote.setQtEdificacao(cursor.getInt(cursor.getColumnIndex("quant_edificacao")));
            lote.setAreaConstruidaLote(cursor.getDouble(cursor.getColumnIndex("area_construida_lote")));

            if (cursor.getInt(cursor.getColumnIndex("forma_regular"))==1){
                lote.setFormaRegular(true);
            }else {
                lote.setFormaRegular(false);
            }

            valor = cursor.getString(cursor.getColumnIndex("situacao"));
            if(!valor.equals("null"))
                lote.setSituacao(valor);

            valor = cursor.getString(cursor.getColumnIndex("delimitacao"));
            if(!valor.equals("null"))
                lote.setDelimitacao(cursor.getString(cursor.getColumnIndex("delimitacao")));

            valor = cursor.getString(cursor.getColumnIndex("topografia"));
            if(!valor.equals("null"))
                lote.setTopografia(valor);

            valor = cursor.getString(cursor.getColumnIndex("classificacao_risco"));
            if(!valor.equals("null"))
                lote.setClassificacaoDeRisco(valor);

            valor = cursor.getString(cursor.getColumnIndex("ocupacao_lote"));
            if(!valor.equals("null"))
                lote.setOcupacaoLote(valor);

            valor = cursor.getString(cursor.getColumnIndex("categoria_propriedade"));
            if(!valor.equals("null"))
                lote.setCategoriaPropriedade(valor);

            if (cursor.getInt(cursor.getColumnIndex("area_risco"))==1){
                lote.setAreaDeRisco(true);
            }else {
                lote.setAreaDeRisco(false);
            }

            valor = cursor.getString(cursor.getColumnIndex("registro_imovel"));
            if(!valor.equals("null"))
                lote.setRegistroImovel(valor);


            if (cursor.getInt(cursor.getColumnIndex("possui_calcada"))==1){
                lote.setPossuiCalcada(true);
            }else {
                lote.setPossuiCalcada(false);
            }

            valor = cursor.getString(cursor.getColumnIndex("pedologia"));
            if(!valor.equals("null"))
                lote.setPedologia(valor);


            valor = cursor.getString(cursor.getColumnIndex("quadra"));
            if(!valor.equals("null"))
                lote.setQuadra(valor);


            valor = cursor.getString(cursor.getColumnIndex("lote"));
            if(!valor.equals("null"))
                lote.setLote(valor);

            valor = cursor.getString(cursor.getColumnIndex("obs_cadastro"));
            if(!valor.equals("null"))
                lote.setObsCadastro(valor);


            lote.setGeom(cursor.getString(cursor.getColumnIndex("geom_wkt")), srid);

        }
        db.close();
        return lote;
    }

    @Override
    public List<Lote> buscarTodos() throws Exception {
        return this.buscarTodos(SRID);
    }


    public List<Lote> buscarTodos(int srid) throws Exception {
        List<Lote> lotes = new ArrayList<Lote>();
        SQLiteDatabase db = sqliteHelper.getReadableDatabase();

        final String MY_QUERY = "SELECT terrenos.*, ST_AsText(Transform(geom,"+ srid + " )) AS geom_wkt FROM terrenos";

        Cursor cursor = db.rawQuery(MY_QUERY, new String[]{});

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Lote lote = new Lote();

                String valor;

                lote.setId(cursor.getInt(cursor.getColumnIndex("id")));

                valor = cursor.getString(cursor.getColumnIndex("atualizacao"));
                if(!valor.equals("null"))
                    lote.setAtualizacao(valor);


                valor = cursor.getString(cursor.getColumnIndex("inscricao_imobiliaria"));
                if(!valor.equals("null"))
                    lote.setInscricaoImobiliaria(valor);


                lote.setFaceQuadraId(cursor.getInt(cursor.getColumnIndex("face_quadra_id_principal")));

                lote.setAreaTerrenoMedida(cursor.getDouble(cursor.getColumnIndex("area_terreno_medida")));
                lote.setAreaTerrenoTributario(cursor.getDouble(cursor.getColumnIndex("area_terreno_tributario")));
                lote.setComprimentoTestadaMedida(cursor.getDouble(cursor.getColumnIndex("comprimento_testada")));
                lote.setComprimentoTestadaTributario(cursor.getDouble(cursor.getColumnIndex("comprimento_testada_tributario")));
                lote.setQtEdificacao(cursor.getInt(cursor.getColumnIndex("quant_edificacao")));
                lote.setAreaConstruidaLote(cursor.getDouble(cursor.getColumnIndex("area_construida_lote")));

                if (cursor.getInt(cursor.getColumnIndex("forma_regular"))==1){
                    lote.setFormaRegular(true);
                }else {
                    lote.setFormaRegular(false);
                }

                valor = cursor.getString(cursor.getColumnIndex("situacao"));
                if(!valor.equals("null"))
                    lote.setSituacao(valor);


                valor = cursor.getString(cursor.getColumnIndex("delimitacao"));
                if(!valor.equals("null"))
                    lote.setDelimitacao(cursor.getString(cursor.getColumnIndex("delimitacao")));


                valor = cursor.getString(cursor.getColumnIndex("topografia"));
                if(!valor.equals("null"))
                    lote.setTopografia(valor);


                valor = cursor.getString(cursor.getColumnIndex("classificacao_risco"));
                if(!valor.equals("null"))
                    lote.setClassificacaoDeRisco(valor);

                valor = cursor.getString(cursor.getColumnIndex("ocupacao_lote"));
                if(!valor.equals("null"))
                    lote.setOcupacaoLote(valor);

                valor = cursor.getString(cursor.getColumnIndex("categoria_propriedade"));
                if(!valor.equals("null"))
                    lote.setCategoriaPropriedade(valor);

                if (cursor.getInt(cursor.getColumnIndex("area_risco"))==1){
                    lote.setAreaDeRisco(true);
                }else {
                    lote.setAreaDeRisco(false);
                }

                valor = cursor.getString(cursor.getColumnIndex("registro_imovel"));
                if(!valor.equals("null"))
                    lote.setRegistroImovel(valor);

                if (cursor.getInt(cursor.getColumnIndex("possui_calcada"))==1){
                    lote.setPossuiCalcada(true);
                }else {
                    lote.setPossuiCalcada(false);
                }
                if (cursor.getInt(cursor.getColumnIndex("coleta_lixo"))==1){
                    lote.setColetaLixo(true);
                }else {
                    lote.setColetaLixo(false);
                }
                if (cursor.getInt(cursor.getColumnIndex("transporte_coletivo"))==1){
                    lote.setTransporteColetivo(true);
                }else {
                    lote.setTransporteColetivo(false);
                }
                if (cursor.getInt(cursor.getColumnIndex("via_pavimentada"))==1){
                    lote.setViaPavimentada(true);
                }else {
                    lote.setViaPavimentada(false);
                }
                if (cursor.getInt(cursor.getColumnIndex("abastecimento_dagua"))==1){
                    lote.setAbastecimentoDAgua(true);
                }else {
                    lote.setAbastecimentoDAgua(false);
                }
                if (cursor.getInt(cursor.getColumnIndex("iluminacao_publica"))==1){
                    lote.setIluminacaoPublica(true);
                }else {
                    lote.setIluminacaoPublica(false);
                }
                if (cursor.getInt(cursor.getColumnIndex("esgoto_sanitario"))==1){
                    lote.setEsgotoSanitario(true);
                }else {
                    lote.setEsgotoSanitario(false);
                }
                if (cursor.getInt(cursor.getColumnIndex("rede_eletrica"))==1){
                    lote.setRedeEletrica(true);
                }else {
                    lote.setRedeEletrica(false);
                }
                if (cursor.getInt(cursor.getColumnIndex("rede_telefonica"))==1){
                    lote.setRedeTelefonica(true);
                }else {
                    lote.setRedeTelefonica(false);
                }

                valor = cursor.getString(cursor.getColumnIndex("pedologia"));
                if(!valor.equals("null"))
                    lote.setPedologia(valor);


                valor = cursor.getString(cursor.getColumnIndex("quadra"));
                if(!valor.equals("null"))
                    lote.setQuadra(valor);


                valor = cursor.getString(cursor.getColumnIndex("lote"));
                if(!valor.equals("null"))
                    lote.setLote(valor);

                valor = cursor.getString(cursor.getColumnIndex("obs_cadastro"));
                if(!valor.equals("null"))
                    lote.setObsCadastro(valor);

                lote.setGeom(cursor.getString(cursor.getColumnIndex("geom_wkt")), srid);

                FotoDAO fotoDAO = new FotoDAO();
                lote.setFotos(fotoDAO.buscarTodosPorPai(lote.getId(),"terreno"));

                VisitaDAO visitaDAO = new VisitaDAO();
                lote.setVisitas(visitaDAO.buscarTodosPorTerreno(lote.getId()));

                lotes.add(lote);

            } while (cursor.moveToNext());

        }
        db.close();
        return lotes;
    }

    public List<Lote> buscarTodosGeom(int srid) throws Exception {
        List<Lote> lotes = new ArrayList<Lote>();

        SQLiteDatabase db = sqliteHelper.getReadableDatabase();

        final String MY_QUERY = "SELECT terrenos.id , ST_AsText(Transform(geom,"+ srid + " )) AS geom_wkt " +
                "FROM terrenos ;";

        Cursor cursor = db.rawQuery(MY_QUERY, new String[]{});

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Lote lote = new Lote();

                lote.setId(cursor.getInt(cursor.getColumnIndex("id")));

                lote.setGeom(cursor.getString(cursor.getColumnIndex("geom_wkt")),srid);

                lotes.add(lote);

            } while (cursor.moveToNext());

        }
        db.close();
        return lotes;
    }


    public int getMaiorId(){
        SQLiteDatabase db = sqliteHelper.getReadableDatabase();
        String sql = "SELECT CASE WHEN MAX(id) > 899999999 " +
                "THEN MAX(id) " +
                "ELSE 900000000 " +
                "END id " +
                "FROM terrenos;";
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        int id = cursor.getInt(cursor.getColumnIndex("id"));
        db.close();
        return id;
    }


    public boolean possuiVisitaCadastrada(int id) throws  Exception{
        boolean valida = false;

        SQLiteDatabase db = sqliteHelper.getReadableDatabase();

        String MY_QUERY = "SELECT te.id, MAX(vi.id) as max FROM terrenos te " +
                "LEFT JOIN visitas vi ON vi.terreno_id = te.id " +
                "WHERE te.id = ? ";

        Cursor cursor = db.rawQuery(MY_QUERY, new String[]{String.valueOf(id)});

        if (cursor != null && cursor.moveToFirst()) {
            int idMax = cursor.getInt(cursor.getColumnIndex("max"));
            if(idMax > 899999999){
                valida = true;
            }

        }

        return valida;
    }
}
