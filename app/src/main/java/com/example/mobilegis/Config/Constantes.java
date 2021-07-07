package com.example.mobilegis.Config;

public final class Constantes {
    private Constantes(){}

    public static final String NOME_BANCO = "db_cadastro";
    public static final int VERSAO_BANCO = 1;

    public static final String ADMIN_NOME = "Super Admin";
    public static final String ADMIN_LOGIN = "admin";
    public static final String ADMIN_SENHA = "2019";
    public static final String VERSAO_APP = "1.00";

    public static final int SRID = 31984;

    public static final int SRID_MAPA = 4386;

    public static final String SQL_INICIALIZA_SPATIALITE = "SELECT InitSpatialMetaData();";


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //                                          USUARIOS
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static final String NOME_TABELA_USUARIOS = "usuarios";

    public static final String SQL_CREATE_TABLE_USUARIOS = "CREATE TABLE " + NOME_TABELA_USUARIOS +
            " (id int primary key, "
            + "nome text, "
            + "login text, "
            + "senha text, "
            + "ativo integer);";

    public static final String SQL_CREATE_USUARIO_ADMIN = "INSERT INTO " + NOME_TABELA_USUARIOS
            + " (id, nome, login, senha, ativo) "
            + "VALUES (999999, '" + ADMIN_NOME + "', '" + ADMIN_LOGIN + "', '" + ADMIN_SENHA + "', 1);";



    ////////////////////////////////////////////////////////////////////////////////////////////////
    //                                          TERRENOS
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static final String NOME_TABELA_TERRENOS = "terrenos";

    public static final String SQL_CREATE_TABLE_TERRENOS = "CREATE TABLE " + NOME_TABELA_TERRENOS +
            " (id int primary key , "
            + "atualizacao text, "
            + "inscricao_imobiliaria text, "
            + "face_quadra_id_principal integer, "
//            + "cobranca_iptu text, "
//            + "logradouro_id integer, "
//            + "nome_logradouro text, "
//            + "numero text, "
//            + "bairro text, "
//            + "cep text, "
            + "area_terreno_medida real, "
            + "area_terreno_tributario real, "
            + "comprimento_testada real, "
            + "comprimento_testada_tributario real, "
            + "quant_edificacao integer, "
            + "area_construida_lote real, "
            + "forma_regular integer, "
            + "situacao text, "
            + "delimitacao text, "
            + "topografia text, "
            + "classificacao_risco text, "
            + "ocupacao_lote text, "
            + "categoria_propriedade text, "
            + "area_risco integer, "
            + "registro_imovel text, "
            + "possui_calcada integer, "
            //+ "coleta_lixo integer, "
            //+ "transporte_coletivo integer, "
            //+ "via_pavimentada integer, "
            //+ "abastecimento_dagua integer, "
            //+ "iluminacao_publica integer, "
            //+ "esgoto_sanitario integer, "
            //+ "rede_eletrica integer, "
            //+ "rede_telefonica integer, "
            //+ "sarjeta integer, "
            + "pedologia text, "
            + "quadra text, "
            + "lote text, "
            + "obs_cadastro text "
            + "); ";


    public static final String SQL_CREATE_GEOMETRIA_TERRENOS = "SELECT AddGeometryColumn('" + NOME_TABELA_TERRENOS + "', 'geom', " + SRID + ", 'POLYGON', 'XY',1);";
    //public static final String SQL_CREATE_GEOMETRIA_EMPRESA = "SELECT AddGeometryColumn('" + NOME_TABELA_EMPRESAS + "', 'localizacao', 31982, 'POINT', 'XY');";


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //                                          EDIFICACOES
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static final String NOME_TABELA_EDIFICACOES = "edificacoes";

    public static final String SQL_CREATE_TABLE_EDIFICACOES = "CREATE TABLE " + NOME_TABELA_EDIFICACOES +
            " (id int primary key , "
            + "terreno_id integer, "
            + "area_construida real, "
//            + "vertical integer, "
//            + "alinhamento text, "
//            + "caracteristica text, "
            + "quant_pavimentos integer, "
            + "quant_unidades integer, "
            + "elevador text, "
            + "estrutura text, "
            + "cobertura text, "
            + "revestimento_externo text, "
            + "pintura_externa text, "
            + "esquadrias text, "
            + "estado_construcao text, "
            + "ano_construcao integer, "
            + "possui_piscina integer, "
            + "complemento_endereco_edif text, "
            + "atualizacao text, "
            + "area_tetos_pavimentos real, "
            + "FOREIGN KEY(terreno_id) REFERENCES terrenos(id) "
            + "); ";


    public static final String SQL_CREATE_GEOMETRIA_EDIFICACOES = "SELECT AddGeometryColumn('" + NOME_TABELA_EDIFICACOES + "', 'geom', " + SRID + ", 'POLYGON', 'XY',1);";


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //                                          UNIDADES
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static final String NOME_TABELA_UNIDADES = "unidades";

    public static final String SQL_CREATE_TABLE_UNIDADES = "CREATE TABLE " + NOME_TABELA_UNIDADES +
            " (id int primary key , "
            + "pai_id integer, "
            + "pai_tipo text, "
            + "inscricao_unidade text, "
            + "complemento_endereco_unidade text, "
            //+ "tipo_contribuinte text, "
            + "situacao_patrimonial text, "
            + "uso text, "
            //+ "tipo text, "
            //+ "apartamento text, "
            + "inscricao_municipal text, "
            + "inscricao_estadual text, "
            + "licenciamento_ambiental text, "
            + "num_licenca_ambiental text, "
            + "alvara_funcionamento text, "
            + "alvara_publicidade text, "
            + "nome_fantasia text, "
            + "razao_social text, "
            //+ "responsavel text, "
            //+ "data_nascimento text, "
            //+ "cpf_cnpj text, "
            //+ "rg_responsavel text, "
            //+ "endereco_responsavel text, "
            + "piso text, "
            + "forro text, "
            + "revestimento_interno text, "
            + "pintura_interna text, "
            + "instalacao_eletrica text, "
            + "instalacao_sanitaria text, "
            + "pe_direito text, "
            + "quant_salas integer, "
            + "quant_quartos integer, "
            + "quant_cozinhas integer, "
            + "quant_banheiros integer, "
            + "quant_dependencias_empregada integer, "
            + "quant_area_servico integer, "
            + "quant_garagem integer, "
            + "atualizacao text, "
            + "area_unidade real, "
            + "num_alvara_funcionamento text, "
            + "num_alvara_publicidade text, "
            + "tipo_construcao text, "
            + "logradouro_id integer, "
            + "nome_logradouro text, "
            + "numero text, "
            + "bairro text, "
            + "cep text, "
            + "id_fisico integer, "
            + "situacao_fisico text, "
            + "cobranca_iptu text, "
            + "inscricao_anterior text, "
            + "responsavel_principal integer"

            //+ "FOREIGN KEY(edificacao_id) REFERENCES edificacoes(id) "

            + "); ";





    ////////////////////////////////////////////////////////////////////////////////////////////////
    //                                          ORDEM SERVICO
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static final String NOME_TABELA_ORDENS_SERVICO = "ordens_servico";

    public static final String SQL_CREATE_TABLE_ORDENS_SEVICO = "CREATE TABLE " + NOME_TABELA_ORDENS_SERVICO +
            " (id int primary key , "
            + "descricao text, "
            + "status text, "

            + "usuario_criador_id integer, "
            + "usuario_executor_id integer, "
            + "usuario_validador_id integer, "

            + "data_validacao text, "
            + "data_cadastro text, "

            + "observacao text, "
            + "observacao_campo text, "
            + "enviado integer, "

            + "FOREIGN KEY(usuario_criador_id) REFERENCES usuarios(id), "
            + "FOREIGN KEY(usuario_executor_id) REFERENCES usuarios(id), "
            + "FOREIGN KEY(usuario_validador_id) REFERENCES usuarios(id) "

            + "); ";

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //                                          ORDEM SERVICO / TERRENO
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static final String NOME_TABELA_TERRENOS_OS = "terrenos_os";

    public static final String SQL_CREATE_TABLE_TERRENOS_OS = "CREATE TABLE " + NOME_TABELA_TERRENOS_OS +
            " (os_id integer, "
            + "terreno_id integer, "
            + "PRIMARY KEY (os_id, terreno_id )"
            + "FOREIGN KEY(os_id) REFERENCES ordem_servico(id), "
            + "FOREIGN KEY(terreno_id) REFERENCES terrenos(id) "
            + "); ";

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //                                          VISITAS
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static final String NOME_TABELA_VISITAS = "visitas";

    public static final String SQL_CREATE_TABLE_VISITAS = "CREATE TABLE " + NOME_TABELA_VISITAS +
            " (id int primary key , "
            + "terreno_id integer, "
            + "usuario_id integer, "
            + "data_cadastro text, "
            + "visita_numero integer, "
            + "situacao text, "
            + "observacao text, "

            + "FOREIGN KEY(usuario_id) REFERENCES ordem_servico(id), "
            + "FOREIGN KEY(terreno_id) REFERENCES terrenos(id) "
            + "); ";


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //                                          FOTOS
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static final String NOME_TABELA_FOTOS = "fotos";

    public static final String SQL_CREATE_TABLE_FOTOS = "CREATE TABLE " + NOME_TABELA_FOTOS +
            " (id int primary key , "
            + "pai_id integer, "
            + "pai_tipo text, "
            + "foto text "
            + "); ";



    ////////////////////////////////////////////////////////////////////////////////////////////////
    //                                          BENFEITORIA
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static final String NOME_TABELA_BENFEITORIA = "benfeitorias";

    public static final String SQL_CREATE_TABLE_BENFEITORIA = "CREATE TABLE " + NOME_TABELA_BENFEITORIA +
            " (id int primary key , "
            + "unidade_id integer, "
            + "tipo_benfeitoria text, "
            + "metragem integer, "
            + "atualizacao text "
            + "); ";


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //                                          ULTIMO_USUARIO
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static final String NOME_TABELA_ULTIMO_USUARIO = "ultimo_usuario";

    public static final String SQL_CREATE_ULTIMO_USUARIO = "CREATE TABLE " + NOME_TABELA_ULTIMO_USUARIO +
            " (id int, " +
            "ultimo_usuario text" +
            "); ";


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //                                          RESPONSAVEIS
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static final String NOME_TABELA_RESPONSAVEL = "responsaveis";

    public static final String SQL_CREATE_TABLE_RESPONSAVEL = "CREATE TABLE " + NOME_TABELA_RESPONSAVEL +
            " (id int primary key , "
            + "unidade_id integer, "
            + "nome text, "
            + "data_nascimento text, "
            + "data_entrada text, "
            + "data_saida text, "
            + "rg text, "
            + "cpf_cnpj text, "
            + "endereco text, "
            + "telefone text, "
            + "email text, "
            + "tipo text, "
            + "principal integer, "
            + "atualizacao text "
            + "); ";

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //                                          TESTADAS
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static final String NOME_TABELA_TESTADAS = "testadas";

    public static final String SQL_CREATE_TABLE_TESTADAS = "CREATE TABLE " + NOME_TABELA_TESTADAS +
            " (id int primary key , "
            + "terreno_id integer, "
            + "comprimento_testada real, "
            + "face_quadra_id integer, "
            + "atualizacao text "
            + "); ";

}
