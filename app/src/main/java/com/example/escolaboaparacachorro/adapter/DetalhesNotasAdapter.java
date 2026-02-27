package com.example.escolaboaparacachorro.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.escolaboaparacachorro.R;
import com.example.escolaboaparacachorro.model.Notas;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class DetalhesNotasAdapter extends RecyclerView.Adapter<DetalhesNotasAdapter.NotasViewHolder> {

    private List<Notas> listaNotas;
    public DetalhesNotasAdapter(List<Notas> listaNotas){
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
        Notas notas = listaNotas.get(position);

        holder.valorNota.setText(notas.getNota());

        int numeroNota = position +1 ;
        holder.notaN.setText("Nota " + numeroNota);

        //logica backgrond do card
        double nota = notas.getNota();
        if(nota <5 ){
            holder.card.setCardBackgroundColor(Color.parseColor("#6ECB3A"));
        } else if (nota >=5 || nota <7) {
            holder.card.setCardBackgroundColor(Color.parseColor("#FFC144"));
        }
        else {
            holder.card.setCardBackgroundColor(Color.parseColor("#E62B0D"));
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
