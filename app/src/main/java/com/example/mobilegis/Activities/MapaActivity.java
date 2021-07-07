package com.example.mobilegis.Activities;

import android.content.Intent;
import android.os.Bundle;

import com.example.mobilegis.Fragments.MapaFragment;
import com.example.mobilegis.Models.Camada;
import com.example.mobilegis.Models.ComunicacaoMapa;
import com.example.mobilegis.Models.Poligono;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.ProgressBar;

import com.example.mobilegis.R;

import org.locationtech.jts.geom.Polygon;

import java.util.List;

import static com.example.mobilegis.Config.Constantes.SRID_MAPA;

public class MapaActivity extends AppCompatActivity implements ComunicacaoMapa {

    private MapaFragment mapa;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        progressBar = (ProgressBar)findViewById(R.id.progressBarMapaOs);
        progressBar.setVisibility(View.GONE);

        Camada camadaNovo = new Camada();
        Camada camadaConcluido = new Camada();
        Camada camadaPendente = new Camada();
        camadaNovo.setColor(0,0,255);
        camadaConcluido.setColor(0,255,0);
        camadaPendente.setColor(255,255,0);
        LoteDAO loteDAO = new LoteDAO();
        try {
            List<Lote> lotes = loteDAO.buscarPorOrdemServicoMapa(SRID_MAPA,4326);
            for (int i = 0 ; i < lotes.size() ; i++){
                Poligono poligono = new Poligono(lotes.get(i).getGeom(),lotes.get(i).getId());
                poligono.setTitle("Deseja ir para o cadastro do terreno?   ");
                VisitaDAO visitaDAO = new VisitaDAO();
                String situacao = visitaDAO.visitado(lotes.get(i).getId());
                // Caso não haja visita criada  no tablet para o lote a pesquisa retorna "n"
                if(!situacao.equals("n")){
                    if(situacao.equals("o_imovel_estava_fechado_impossibilitando_a_vistoria")){
                        camadaPendente.addObjeto(poligono);
                    }else{
                        camadaConcluido.addObjeto(poligono);
                    }
                }else{
                    camadaNovo.addObjeto(poligono);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(camadaConcluido.size()>0){
            mapa.addCamada(camadaConcluido);
        }
        if(camadaNovo.size()>0){
            mapa.addCamada(camadaNovo);
        }
        if(camadaPendente.size()>0){
            mapa.addCamada(camadaPendente);
        }
    }

    @Override
    public void salvaPoligono(Polygon poligono, int id) {
        progressBar.setVisibility(View.VISIBLE);
        Intent intent = new Intent(this, CadastroLotesActivity.class);
        intent.putExtra("poligono",poligono);
        intent.putExtra("id",id);
        intent.putExtra("osId",osId);
//        mapa.getMap().getTileProvider().clearTileCache();
        startActivity(intent);
        this.onPause();

    }
    @Override
    protected void onRestart() {
        super.onRestart();
        progressBar.setVisibility(View.GONE);
        this.recreate();
    }
}