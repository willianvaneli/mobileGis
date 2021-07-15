package com.example.mobilegis.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobilegis.DAOs.LoteDAO;
import com.example.mobilegis.Fragments.MapaFragment;
import com.example.mobilegis.Models.ComunicacaoMapa;
import com.example.mobilegis.Models.Lote;
import com.example.mobilegis.R;
import com.example.mobilegis.Utils.DialogMenssage;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Polygon;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class CadastroLotesActivity extends AppCompatActivity implements ComunicacaoMapa {
    private Polygon poligono;
    private Lote lote;
    //    Spinner spinnerAtualizacao;
//    private Spinner spinnerCobrancaIPTU;
    private Spinner spinnerSituacaoLote;
    private Spinner spinnerDelimitacao;
    private Spinner spinnerTopografia;
    private Spinner spinnerOcupacaoLote;
    private Spinner spinnerCategoriaPropriedade;
    private Spinner spinnerPedologia;
    private MapaFragment mapa;
    private int osId;
    private boolean click = true;
    private boolean atualizacao = false;

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            atualizacao = true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_lotes);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        lote = new Lote();
        FragmentManager manager = getSupportFragmentManager();
        mapa = (MapaFragment) manager.findFragmentById(R.id.fragment_mapa_cad_lote);
        mapa.setComunicator(this);

        adicionaMascaras();


        try{
            Intent i = getIntent();
            lote.setGeom((Polygon) i.getSerializableExtra("poligono"));
            lote.setId((int) i.getSerializableExtra("id"));


            if ( lote.getId() > 0 ){
                LoteDAO loteDAO = new LoteDAO();
                this.lote = loteDAO.buscaSuave((long) lote.getId(),4326);
                preencheCampos();
            }else{
                preencheCamposNovo();
            }

            iniciaSpinners();
        }
        catch (Exception e){
            Toast.makeText(this, "Falha ao carregar poligono", Toast.LENGTH_LONG).show();
        }
        mapa.setPoligonoEditavel(lote.getGeom(),lote.getId());
        mapa.mapCad();

    }

    // CRIA MASCARAS PARA CAMPOS ESPECIFICOS
    private void adicionaMascaras() {

    }

    private void preencheCampos() {
        // OS SPINNERS SÃO PREENCHIDOS NO MÉTODO INICIA SPINNERS DEVIDO AO FATO DE GRANDE PARTE DO CÓDIGO SER REPETIDO.


        ((TextView)findViewById(R.id.edit_area_terreno)).setText(String.format("%.2f m²", lote.getAreaTerrenoMedida()));


        if (lote.getComprimentoTestadaMedida() != 0)
            ((EditText)findViewById(R.id.edit_comprimento_testada)).setText(Double.toString(lote.getComprimentoTestadaMedida()));


        ((TextView)findViewById(R.id.edit_qt_edificacao)).setText(Integer.toString(lote.getQtEdificacao()));

        if (lote.getAreaConstruidaLote() != 0)
            ((EditText)findViewById(R.id.edit_area_construida)).setText(Double.toString(lote.getAreaConstruidaLote()));

        ((EditText)findViewById(R.id.edit_classificacao_risco)).setText(lote.getClassificacaoDeRisco());


        ((EditText)findViewById(R.id.edit_registro_imovel)).setText(lote.getRegistroImovel());


        ((Switch)findViewById(R.id.switch_area_risco)).setChecked(lote.isAreaDeRisco());

        ((Switch)findViewById(R.id.switch_calcada)).setChecked(lote.isPossuiCalcada());

        ((Switch)findViewById(R.id.switch_coleta_lixo)).setChecked(lote.isColetaLixo());

        ((Switch)findViewById(R.id.switch_transporte_publico)).setChecked(lote.isTransporteColetivo());

        ((Switch)findViewById(R.id.switch_via_pavimentada)).setChecked(lote.isViaPavimentada());

        ((Switch)findViewById(R.id.switch_abastecimento_agua)).setChecked(lote.isAbastecimentoDAgua());

        ((Switch)findViewById(R.id.switch_iluminacao_publica)).setChecked(lote.isIluminacaoPublica());

        ((Switch)findViewById(R.id.switch_esgoto_sanitario)).setChecked(lote.isEsgotoSanitario());

        ((Switch)findViewById(R.id.switch_rede_eletrica)).setChecked(lote.isRedeEletrica());

        ((Switch)findViewById(R.id.switch_rede_telefonica)).setChecked(lote.isRedeTelefonica());

        if(lote.getQuadra() != null){
            ((EditText)findViewById(R.id.edit_quadra)).setText(lote.getQuadra());
        }

        if(lote.getLote() != null){
            ((EditText)findViewById(R.id.edit_lote)).setText(lote.getLote());
        }

        if(lote.getObsCadastro() != null){
            ((TextView)findViewById(R.id.edit_observacao)).setText(lote.getObsCadastro()+ "\n\n\n");
        }
    }

    private void preencheCamposNovo() {

        ((Switch)findViewById(R.id.switch_calcada)).setChecked(true);

        ((Switch)findViewById(R.id.switch_coleta_lixo)).setChecked(true);

        ((Switch)findViewById(R.id.switch_transporte_publico)).setChecked(true);

        ((Switch)findViewById(R.id.switch_via_pavimentada)).setChecked(true);

        ((Switch)findViewById(R.id.switch_abastecimento_agua)).setChecked(true);

        ((Switch)findViewById(R.id.switch_iluminacao_publica)).setChecked(true);

        ((Switch)findViewById(R.id.switch_esgoto_sanitario)).setChecked(true);

        ((Switch)findViewById(R.id.switch_rede_eletrica)).setChecked(true);

        ((Switch)findViewById(R.id.switch_rede_telefonica)).setChecked(true);
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_cad_lotes, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        if(lote.getId() == 0) {
            menu.findItem(R.id.action_foto_cad_lote).setVisible(false);
            menu.findItem(R.id.action_visitas_cad_lote).setVisible(false);
            menu.findItem(R.id.action_excluir_cad_lote).setVisible(false);
        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (!click) { //Evita duplo click
            return true;
        }
        click = false;

        int id = item.getItemId();

        switch (id){

            case R.id.action_salvar_cad_lote:
                salvaLote();
                break;

            case R.id.action_foto_cad_lote:
                irFotos();
                break;

            case R.id.action_visitas_cad_lote:
                irVisitas(this.lote.getId());
                break;

            case R.id.action_excluir_cad_lote:
                mostrarDialogoPersonalizado();
                break;
        }

        return super.onOptionsItemSelected(item);
    }



    private void excluirLote() {
        try {
            if(lote.getId() != 0) {
                LoteDAO loteDAO = new LoteDAO();
                if(loteDAO.possuiVisitaCadastrada(lote.getId())){
                    loteDAO.deletar(lote);
                    Toast.makeText(this, "Lote excluido com sucesso", Toast.LENGTH_SHORT).show();
                }else{
                    alertSemVisita();
                }
            }
            finish();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this,"Não foi possível excluir o lote", Toast.LENGTH_LONG).show();
        }
    }


    public void irFotos(){
        Intent intent = new Intent(getApplicationContext(), FotoActivity.class);
        intent.putExtra("paiId",this.lote.getId());
        intent.putExtra("paiTipo","terreno");
        startActivity(intent);
    }


    private void irVisitas(int id){
        Intent intent = new Intent(this, ListaVisitasActivity.class);
        intent.putExtra("idLote",id);
        startActivity(intent);
    }

    // FUNCAO QUE PREENCHE TODOS SPINNERS
    public void iniciaSpinners(){

//        spinnerAtualizacao =  (Spinner) findViewById(R.id.spinner_atualizacao);
//        spinnerCobrancaIPTU =  (Spinner) findViewById(R.id.spinner_cobranca_iptu);
        spinnerSituacaoLote =  (Spinner) findViewById(R.id.spinner_situacao_lote);
        spinnerDelimitacao =  (Spinner) findViewById(R.id.spinner_delimitacao);
        spinnerTopografia =  (Spinner) findViewById(R.id.spinner_topografia);
        spinnerOcupacaoLote =  (Spinner) findViewById(R.id.spinner_ocupacao_lote);
        spinnerCategoriaPropriedade = (Spinner) findViewById(R.id.spinner_categoria_propriedade);
        spinnerPedologia = (Spinner) findViewById(R.id.spinner_pedologia);

//        ArrayAdapter<CharSequence> adapterAtualizacao = ArrayAdapter.createFromResource(this,R.array.spinner_atualizacao_texto,R.layouts.support_simple_spinner_dropdown_item);
//        adapterAtualizacao.setDropDownViewResource(R.layouts.support_simple_spinner_dropdown_item);

//        ArrayAdapter<CharSequence> adapterCobIPTU = ArrayAdapter.createFromResource(this,R.array.spinner_cobranca_iptu_texto,R.layout.support_simple_spinner_dropdown_item);
//        adapterCobIPTU.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        ArrayAdapter<CharSequence> adapterSituacaoLote = ArrayAdapter.createFromResource(this,R.array.spinner_situacao_lote_texto,R.layout.support_simple_spinner_dropdown_item);
        adapterSituacaoLote.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        ArrayAdapter<CharSequence> adapterDelimitacao = ArrayAdapter.createFromResource(this,R.array.spinner_delimitacao_texto,R.layout.support_simple_spinner_dropdown_item);
        adapterDelimitacao.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        ArrayAdapter<CharSequence> adapterTopografia = ArrayAdapter.createFromResource(this,R.array.spinner_topografia_texto,R.layout.support_simple_spinner_dropdown_item);
        adapterTopografia.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        ArrayAdapter<CharSequence> adapterOcupacaoLote = ArrayAdapter.createFromResource(this,R.array.spinner_ocupacao_lote_texto,R.layout.support_simple_spinner_dropdown_item);
        adapterOcupacaoLote.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        ArrayAdapter<CharSequence> adapterCategoriaPropriedade = ArrayAdapter.createFromResource(this,R.array.spinner_categoria_propriedade_texto,R.layout.support_simple_spinner_dropdown_item);
        adapterCategoriaPropriedade.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        ArrayAdapter<CharSequence> adapterPedologia = ArrayAdapter.createFromResource(this,R.array.spinner_pedologia_texto,R.layout.support_simple_spinner_dropdown_item);
        adapterPedologia.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);


//        spinnerAtualizacao.setAdapter(adapterAtualizacao);
//        spinnerCobrancaIPTU.setAdapter(adapterCobIPTU);
        spinnerSituacaoLote.setAdapter(adapterSituacaoLote);
        spinnerDelimitacao.setAdapter(adapterDelimitacao);
        spinnerTopografia.setAdapter(adapterTopografia);
        spinnerOcupacaoLote.setAdapter(adapterOcupacaoLote);
        spinnerCategoriaPropriedade.setAdapter(adapterCategoriaPropriedade);
        spinnerPedologia.setAdapter(adapterPedologia);


        //Preenchimento caso exista um lote
//        if(lote.getAtualizacao() != null)
//            spinnerAtualizacao.setSelection(ArrayAdapter.createFromResource(this,R.array.spinner_atualizacao_valor,R.layouts.support_simple_spinner_dropdown_item).getPosition(lote.getAtualizacao()));

//        if(lote.getCobrancaIptu() != null)
//            spinnerCobrancaIPTU.setSelection(ArrayAdapter.createFromResource(this,R.array.spinner_cobranca_iptu_valor,R.layout.support_simple_spinner_dropdown_item).getPosition(lote.getCobrancaIptu()));

        if(lote.getSituacao() != null)
            spinnerSituacaoLote.setSelection(ArrayAdapter.createFromResource(this,R.array.spinner_situacao_lote_valor,R.layout.support_simple_spinner_dropdown_item).getPosition(lote.getSituacao()));

        if(lote.getDelimitacao() != null)
            spinnerDelimitacao.setSelection(ArrayAdapter.createFromResource(this,R.array.spinner_delimitacao_valor,R.layout.support_simple_spinner_dropdown_item).getPosition(lote.getDelimitacao()));

        if(lote.getTopografia() != null)
            spinnerTopografia.setSelection(ArrayAdapter.createFromResource(this,R.array.spinner_topografia_valor,R.layout.support_simple_spinner_dropdown_item).getPosition(lote.getTopografia()));

        if(lote.getOcupacaoLote() != null){
            spinnerOcupacaoLote.setSelection(ArrayAdapter.createFromResource(this,R.array.spinner_ocupacao_lote_valor,R.layout.support_simple_spinner_dropdown_item).getPosition(lote.getOcupacaoLote()));
//            if(lote.getOcupacaoLote().equals("baldio")){
//                spinnerOcupacaoLote.setEnabled(false);
//            }
        }

        if(lote.getCategoriaPropriedade() != null)
            spinnerCategoriaPropriedade.setSelection(ArrayAdapter.createFromResource(this,R.array.spinner_categoria_propriedade_valor,R.layout.support_simple_spinner_dropdown_item).getPosition(lote.getCategoriaPropriedade()));

        if(lote.getPedologia() != null)
            spinnerPedologia.setSelection(ArrayAdapter.createFromResource(this,R.array.spinner_pedologia_valor,R.layout.support_simple_spinner_dropdown_item).getPosition(lote.getPedologia()));
    }

    public void salvaLote() {
        boolean reacreate = false;
        String mensagemResult = "Lote salvo com sucesso";
        Polygon pol = mapa.getPoligonoEditavel();
        if (pol != null && pol.getNumPoints()>2){
            lote.setGeom(pol);
        }else{
            lote.setGeom(this.poligono);
            reacreate = true;
//            mapa.setPoligonoEditavel(this.poligono,lote.getId());
//            mapa.mapCad();
            mensagemResult = "Lote salvo sem geometria\n\nNecessário geometria completa" ;
        }

        if (lote.getGeom()!= null && lote.getGeom().getNumPoints()>2){

            lote.setAreaTerrenoMedida(mapa.getAreaPolEdit());



            String sdouble = ((EditText)findViewById(R.id.edit_comprimento_testada)).getText().toString();
            if (!(sdouble.equals("")))
                lote.setComprimentoTestadaMedida(Double.parseDouble(sdouble));
            else
                lote.setComprimentoTestadaMedida(0.0);



            sdouble = ((EditText)findViewById(R.id.edit_area_construida)).getText().toString();
            if (!(sdouble.equals(""))) {
                lote.setAreaConstruidaLote(Double.parseDouble(sdouble));
            }else
                lote.setAreaConstruidaLote(0.0);

            String valor = ArrayAdapter.createFromResource(this,R.array.spinner_situacao_lote_valor,R.layout.support_simple_spinner_dropdown_item).getItem(spinnerSituacaoLote.getSelectedItemPosition()).toString();
            if(!valor.equals(""))
                lote.setSituacao(valor);
            else
                lote.setSituacao(null);

            valor = ArrayAdapter.createFromResource(this,R.array.spinner_delimitacao_valor,R.layout.support_simple_spinner_dropdown_item).getItem(spinnerDelimitacao.getSelectedItemPosition()).toString();
            if(!valor.equals(""))
                lote.setDelimitacao(valor);
            else
                lote.setDelimitacao(null);

            valor = ArrayAdapter.createFromResource(this,R.array.spinner_topografia_valor,R.layout.support_simple_spinner_dropdown_item).getItem(spinnerTopografia.getSelectedItemPosition()).toString();
            if(!valor.equals(""))
                lote.setTopografia(valor);
            else
                lote.setTopografia(null);

            valor = ((EditText)findViewById(R.id.edit_classificacao_risco)).getText().toString();
            if(!valor.equals(""))
                lote.setClassificacaoDeRisco(valor);
            else
                lote.setClassificacaoDeRisco(null);

            valor = ArrayAdapter.createFromResource(this,R.array.spinner_ocupacao_lote_valor,R.layout.support_simple_spinner_dropdown_item).getItem(spinnerOcupacaoLote.getSelectedItemPosition()).toString();
            if(!valor.equals(""))
                lote.setOcupacaoLote(valor);
            else
                lote.setOcupacaoLote(null);

            valor = ArrayAdapter.createFromResource(this,R.array.spinner_categoria_propriedade_valor,R.layout.support_simple_spinner_dropdown_item).getItem(spinnerCategoriaPropriedade.getSelectedItemPosition()).toString();
            if(!valor.equals(""))
                lote.setCategoriaPropriedade(valor);
            else
                lote.setCategoriaPropriedade(null);

            lote.setAreaDeRisco(((Switch)findViewById(R.id.switch_area_risco)).isChecked());

            valor = ((EditText)findViewById(R.id.edit_registro_imovel)).getText().toString();
            if(!valor.equals(""))
                lote.setRegistroImovel(valor);
            else
                lote.setRegistroImovel(null);

            lote.setPossuiCalcada(((Switch)findViewById(R.id.switch_calcada)).isChecked());

            lote.setColetaLixo(((Switch)findViewById(R.id.switch_coleta_lixo)).isChecked());

            lote.setTransporteColetivo(((Switch)findViewById(R.id.switch_transporte_publico)).isChecked());

            lote.setViaPavimentada(((Switch)findViewById(R.id.switch_via_pavimentada)).isChecked());

            lote.setAbastecimentoDAgua(((Switch)findViewById(R.id.switch_abastecimento_agua)).isChecked());

            lote.setIluminacaoPublica(((Switch)findViewById(R.id.switch_iluminacao_publica)).isChecked());

            lote.setEsgotoSanitario(((Switch)findViewById(R.id.switch_esgoto_sanitario)).isChecked());

            lote.setRedeEletrica(((Switch)findViewById(R.id.switch_rede_eletrica)).isChecked());

            lote.setRedeTelefonica(((Switch)findViewById(R.id.switch_rede_telefonica)).isChecked());


            valor = ArrayAdapter.createFromResource(this,R.array.spinner_pedologia_valor,R.layout.support_simple_spinner_dropdown_item).getItem(spinnerPedologia.getSelectedItemPosition()).toString();
            if(!valor.equals(""))
                lote.setPedologia(valor);
            else
                lote.setPedologia(null);

            valor = ((EditText)findViewById(R.id.edit_quadra)).getText().toString();
            if(!valor.equals(""))
                lote.setQuadra(valor);
            else
                lote.setQuadra(null);

            valor = ((EditText)findViewById(R.id.edit_lote)).getText().toString();
            if(!valor.equals(""))
                lote.setLote(valor);
            else
                lote.setLote(null);

            valor = ((EditText)findViewById(R.id.edit_observacao)).getText().toString();
            if(!valor.equals(""))
                lote.setQuadra(valor);
            else
                lote.setQuadra(null);

            LoteDAO loteDAO = new LoteDAO();

            try {
                if (lote.getId() == 0){
                    lote.setAtualizacao("inclusao");
                    lote.setId(loteDAO.getMaiorId() + 1);

                    loteDAO.inserir(lote);
                    getIntent().putExtra("id",lote.getId());
                    recreate();
                }else{
                    loteDAO.editar(lote);
                }

                Toast.makeText(this, mensagemResult , Toast.LENGTH_LONG).show();

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Falha ao salvar a lote, erro : " + e.toString(), Toast.LENGTH_LONG).show();
            }

        }else{
            Toast.makeText(this, "Falha ao salvar a lote, necessário geometria " , Toast.LENGTH_LONG).show();
        }

        click = true;
    }


    @Override
    public void salvaPoligono(Polygon poligono, int id) {
        salvaLote();
    }

    @Override
    public void remembrar(List<Integer> ids) {

    }

    @Override
    public void desmembrar(Geometry geoms, int id) {

    }

    private void result(Message message){
        Toast.makeText(this, (String)message.obj, Toast.LENGTH_LONG).show();
        click = true;
        if(message.what == 1){
            invalidateOptionsMenu();
            getIntent().putExtra("id",lote.getId());
            recreate();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LoteDAO loteDAO = new LoteDAO();
        try {
            this.lote = loteDAO.buscar((long) lote.getId());
            preencheCampos();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void hideSoftKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        hideSoftKeyboard();
        click = true;
        recreate();


    }



    private void mostrarDialogoPersonalizado() {

        final DialogMenssage dialogMenssage = new DialogMenssage(this,"Deseja confirmar alteração?");

        dialogMenssage.setImage(R.drawable.layers_clear_48dp);

        dialogMenssage.mostrarDialogoPersonalizado();

        dialogMenssage.getBtnCancelar().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Operação cancelada", Toast.LENGTH_SHORT).show();
                dialogMenssage.dismiss();
            }
        });

        dialogMenssage.getBtnConfirmar().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Excluindo...", Toast.LENGTH_SHORT).show();
                dialogMenssage.dismiss();
                excluirLote();
            }
        });

        click = true;
    }


    private void alertSemVisita() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_personalizado, null);

        builder.setView(view);

        final AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();

        TextView txt = view.findViewById(R.id.text_dialog);
        txt.setText("Lote sem visita cadastrada");

        Button btnCancelar = view.findViewById(R.id.btnCancelar);
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        Button btnConfirmar = view.findViewById(R.id.btnConfirm);
        btnConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

}
