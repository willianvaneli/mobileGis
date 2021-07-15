package com.example.mobilegis.Activities;


import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobilegis.DAOs.VisitaDAO;
import com.example.mobilegis.Models.Visita;
import com.example.mobilegis.R;
import com.example.mobilegis.Utils.DialogMenssage;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CadastroVisitaActivity extends AppCompatActivity {
    Spinner spinnerSituacao;
    Visita visita;
    private boolean click = true;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_visita);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        iniciaSpinners();
        int id = (int) getIntent().getSerializableExtra("id");
        int idLote = (int) getIntent().getSerializableExtra("idLote");

        if(id >0){
            VisitaDAO visitaDAO = new VisitaDAO();
            try {
                visita = visitaDAO.buscar((long) id);

                if(visita.getObservacao()!= null && !visita.getObservacao().equals("")){
                    ((EditText) findViewById(R.id.edit_observacao)).setText(visita.getObservacao());
                }else {
//                    ((EditText)findViewById(R.id.edit_observacao)).setText("\n\n\n\n\n\n");
                    ((EditText)findViewById(R.id.edit_observacao)).setSelection(0);
                }

                if(visita.getSituacao() != null)
                    spinnerSituacao.setSelection(ArrayAdapter.createFromResource(this,R.array.spinner_situacao_valor,R.layout.support_simple_spinner_dropdown_item).getPosition(visita.getSituacao()));

//                ((TextView)findViewById(R.id.textDataCadastroValor)).setText(visita.getDataCadastro());


            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            visita = new Visita();
            visita.setTerrenoId(idLote);
            visita.setUsuarioId(LoginActivity.usuarioAtual.getId());

            SimpleDateFormat formataData = new SimpleDateFormat("dd/MM/yyyy");
            Date data = new Date();
            visita.setDataCadastro(formataData.format(data));

            // Pegando maior numero da visita para o lote
            VisitaDAO visitaDAO = new VisitaDAO();
            visita.setVisitaNumero(visitaDAO.getMaiorNumeroVisita(idLote)+1);

//            ((EditText)findViewById(R.id.edit_observacao)).setText("\n\n\n\n\n\n");
//            ((EditText)findViewById(R.id.edit_observacao)).setSelection(0);

        }
        ((TextView)findViewById(R.id.textDataCadastroValor)).setText(visita.getDataCadastro());
        ((TextView)findViewById(R.id.textVisitaNumero)).setText(Integer.toString(visita.getVisitaNumero()));


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_cad_visita, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(!click){
            return true;
        }
        click = false;
        int id = item.getItemId();

        switch (id){

            case R.id.action_salvar_visita:
                salvarVisita();
                break;

            case R.id.action_sair_visita:
                Intent returnIntent = new Intent();
                returnIntent.putExtra("id",0);
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    private void iniciaSpinners(){
        spinnerSituacao = (Spinner) findViewById(R.id.spinner_observacao_visita);

        ArrayAdapter<CharSequence> adapterSituacao = ArrayAdapter.createFromResource(this,R.array.spinner_situacao_texto,R.layout.support_simple_spinner_dropdown_item);
        adapterSituacao.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        spinnerSituacao.setAdapter(adapterSituacao);
    }


    private void salvarVisita(){


        try {
            visita.setSituacao(ArrayAdapter.createFromResource(this,R.array.spinner_situacao_valor,R.layout.support_simple_spinner_dropdown_item).getItem(spinnerSituacao.getSelectedItemPosition()).toString());

            if(visita.getSituacao().equals("")){
                dialogoSituacao();
                return;
            }

            visita.setObservacao(((EditText)findViewById(R.id.edit_observacao)).getText().toString());

            visita.setUsuarioId(LoginActivity.usuarioAtual.getId());

            VisitaDAO dao = new VisitaDAO();
            Intent returnIntents = new Intent();
            if(visita.getId() > 0){
                dao.editar(visita);
                returnIntents.putExtra("id",0);

            }else{
                visita.setId(dao.getMaiorId()+1);
                dao.inserir(visita);
                returnIntents.putExtra("id",visita.getId());
            }
            setResult(Activity.RESULT_OK,returnIntents);
            Toast.makeText(this,"Visita salva com sucesso ",Toast.LENGTH_SHORT).show();
            finish();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this,"Erro ao salvar visita = " + e.getMessage(),Toast.LENGTH_SHORT).show();
        }

    }

    private void dialogoSituacao(){
        final DialogMenssage dialogMenssage = new DialogMenssage(this,"Atenção!\nNecessário preencher situação da visita");

        dialogMenssage.setImage(R.drawable.baseline_error_black_48dp);

        dialogMenssage.mostrarDialogoPersonalizado();

        dialogMenssage.getBtnCancelar().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogMenssage.dismiss();
            }
        });

        dialogMenssage.getBtnConfirmar().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogMenssage.dismiss();
            }
        });
        click = true;
    }

}
