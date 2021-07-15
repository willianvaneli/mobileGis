package com.example.mobilegis.Adapters;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobilegis.Models.Visita;
import com.example.mobilegis.R;

import java.util.List;

public class VisitaAdapter extends RecyclerView.Adapter<VisitaAdapter.ViewHolderVisita> {
    private VisitaAdapterListener listener;
    private List<Visita> visitas;

    public VisitaAdapter (List<Visita> visitas){ this.visitas = visitas; }

    public void setListener( VisitaAdapterListener listener){this.listener = listener;}

    public void atualizaVisitas(List<Visita> visitas){
        this.visitas = visitas;
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VisitaAdapter.ViewHolderVisita onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.linha_visita,parent, false);
        ViewHolderVisita viewHolderVisita = new ViewHolderVisita(view);

        return viewHolderVisita;
    }

    @Override
    public void onBindViewHolder(@NonNull VisitaAdapter.ViewHolderVisita holder, final int position) {
        if(visitas.size() > 0 && visitas != null){
            final Visita visita = visitas.get(position);

            holder.textIdVisita.setText(String.valueOf(visita.getId()));
            holder.btEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(position,visita.getId(),0);
                }
            });

            holder.btExcluir.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(position,visita.getId(),1);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if(visitas != null){
            return visitas.size();
        }else{
            return 0;
        }
    }


    public class ViewHolderVisita extends RecyclerView.ViewHolder{

        public TextView textIdVisita;
        public ImageView btEdit;
        public ImageView btExcluir;

        public ViewHolderVisita(@NonNull View itemView) {
            super(itemView);
            textIdVisita = (TextView) itemView.findViewById(R.id.textIdVisita);
            btEdit = (ImageView) itemView.findViewById(R.id.btEditVisita);
            btExcluir = (ImageView) itemView.findViewById(R.id.btExcluirLv);
        }
    }

    public interface VisitaAdapterListener {
        void onItemClick(int position, int id,int funcao);
        void onItemLongClick(int position, int id, int funcao);
    }
}
