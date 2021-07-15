package com.example.mobilegis.Activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobilegis.Adapters.VisitaAdapter;
import com.example.mobilegis.DAOs.VisitaDAO;
import com.example.mobilegis.Models.Visita;
import com.example.mobilegis.R;

import java.util.List;

public class ListaVisitasActivity extends AppCompatActivity {
    private int idLote;
    private List<Visita> visitas;
    private RecyclerView recyclerLstVisitas;
    private VisitaAdapter visitaAdapter;
    private boolean click = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_visitas);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        idLote = getIntent().getExtras().getInt("idLote");

        VisitaDAO dao = new VisitaDAO();

        try {
            visitas = dao.buscarTodosPorTerreno(idLote);
        } catch (Exception e) {
            e.printStackTrace();
        }

        recyclerLstVisitas = (RecyclerView)findViewById(R.id.recycler_visita);
        recyclerLstVisitas.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerLstVisitas.setLayoutManager(linearLayoutManager);

        visitaAdapter = new VisitaAdapter(visitas);
        recyclerLstVisitas.setAdapter(visitaAdapter);

        visitaAdapter.setListener(new VisitaAdapter.VisitaAdapterListener() {
            @Override
            public void onItemClick(int position, int id, int funcao) {
                if (!click) { // 1000 = 1second
                    return ;
                }
                click = false;
                if (funcao == 0){
                    entrarCadastroVisita(id);
                }
                if (funcao == 1){
                    mostrarDialogoPersonalizado(id);
                    click = true;
                }
            }

            @Override
            public void onItemLongClick(int position, int id, int funcao) {

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_lista_visita, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (!click) { // 1000 = 1second
            return true;
        }
        click = false;
        int id = item.getItemId();

        switch (id){

            case R.id.action_ir_visita:
                entrarCadastroVisita(0);

        }

        return super.onOptionsItemSelected(item);
    }


    private void mostrarDialogoPersonalizado(final int id) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_personalizado, null);

        builder.setView(view);

        //TODO BOTONES POR DEFECTO

//         builder.setView(inflater.inflate(R.layouts.dialog_personalizado,null))
//         .setPositiveButton("Reintentar", new DialogInterface.OnClickListener() {
//        @Override public void onClick(DialogInterface dialog, int which) {
//        Toast.makeText(getApplicationContext(),"Conectando...",Toast.LENGTH_SHORT).show();
//        }
//        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//        @Override public void onClick(DialogInterface dialog, int which) {
//        Toast.makeText(getApplicationContext(),"Cancel",Toast.LENGTH_SHORT).show();
//        }
//        });

        final AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();

        TextView txt = view.findViewById(R.id.text_dialog);
        txt.setText("Deseja realmente excluir a visita?");

        Button btnCancelar = view.findViewById(R.id.btnCancelar);
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getApplicationContext(), "Cancelar...", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        Button btnConfirmar = view.findViewById(R.id.btnConfirm);
        btnConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletarVisita(id);
                Toast.makeText(getApplicationContext(), "Excluindo...", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

    }


    private void deletarVisita(int id){
        VisitaDAO visitaDAO = new VisitaDAO();
        try {
            for (Visita visita : visitas){
                if(visita.getId() == id){
                    visitas.remove(visita);
                    visitaDAO.deletar(visita);
                }
            }
            visitaAdapter.atualizaVisitas(visitas);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void entrarCadastroVisita(int id){
        Intent i = new Intent(this, CadastroVisitaActivity.class);
        i.putExtra("id",id);
        i.putExtra("idLote",idLote);
        startActivityForResult(i,1);
        click = true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1) {
            int id = data.getExtras().getInt("id");
            if(id > 0 ){
                VisitaDAO benfeitoriaDAO = new VisitaDAO();
                try {
                    Visita benfeitoria = benfeitoriaDAO.buscar((long) id);
                    visitas.add(benfeitoria);
                    visitaAdapter.atualizaVisitas(visitas);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        click = true;
    }


}
