package com.example.escolaboaparacachorro.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.escolaboaparacachorro.R;
import com.example.escolaboaparacachorro.model.Nota;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class DetalhesNotasAdapter extends RecyclerView.Adapter<DetalhesNotasAdapter.NotasViewHolder> {

    private List<Nota> listaNotas;
    public DetalhesNotasAdapter(List<Nota> listaNotas){
        this.listaNotas= listaNotas;
    }

    @NonNull
    @Override
    public DetalhesNotasAdapter.NotasViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_nota, parent, false);
        return new DetalhesNotasAdapter.NotasViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DetalhesNotasAdapter.NotasViewHolder holder, int position) {
        Nota notaObjeto = listaNotas.get(position);

        holder.valorNota.setText(String.valueOf(notaObjeto.getNota()));

        // Exibe "Nota 1", "Nota 2", etc.
        int numeroPosicao = position + 1;
        holder.notaN.setText("Nota " + numeroPosicao);

        double valorNota = notaObjeto.getNota();

        if (valorNota < 5.0) {
            // Vermelho
            holder.card.setCardBackgroundColor(Color.parseColor("#E62B0D"));
        } else if (valorNota < 7.0) {
            // Amarelo/Laranja
            holder.card.setCardBackgroundColor(Color.parseColor("#FFC144"));
        } else {
            // Verde
            holder.card.setCardBackgroundColor(Color.parseColor("#6ECB3A"));
        }
    }


    @Override
    public int getItemCount() {
        return listaNotas != null ? listaNotas.size() : 0;
    }

    public static class NotasViewHolder extends RecyclerView.ViewHolder{

        TextView valorNota, notaN ;
        MaterialCardView card;

        public NotasViewHolder(@NonNull View itemView) {
            super(itemView);
            valorNota = itemView.findViewById(R.id.valorNota);
            notaN = itemView.findViewById(R.id.notaN);
            card = itemView.findViewById(R.id.cardViewNota);
        }

    }
}
