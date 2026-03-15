package com.example.escolaboaparacachorro.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.escolaboaparacachorro.R;
import com.example.escolaboaparacachorro.model.Cachorro;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

public class EscolhaCachorroAdapter extends RecyclerView.Adapter<EscolhaCachorroAdapter.EscolhaViewHolder> {

    private List<Cachorro> listaCachorros;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Cachorro cachorro);
    }

    public EscolhaCachorroAdapter(List<Cachorro> lista, OnItemClickListener listener) {
        this.listaCachorros = lista;
        this.listener = listener;
    }

    @NonNull
    @Override
    public EscolhaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Infla o layout do card que criamos (item_cachorro_card)
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_escolha_cachorro, parent, false);
        return new EscolhaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EscolhaViewHolder holder, int position) {
        Cachorro cachorro = listaCachorros.get(position);

        holder.txtNome.setText(cachorro.getNome());

        Glide.with(holder.itemView.getContext())
                .load(cachorro.getImagem())
                .placeholder(R.drawable.dogduble) // Placeholder padrão
                .into(holder.imgFoto);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(cachorro);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaCachorros != null ? listaCachorros.size() : 0;
    }

    public static class EscolhaViewHolder extends RecyclerView.ViewHolder {
        TextView txtNome;
        ShapeableImageView imgFoto;

        public EscolhaViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNome = itemView.findViewById(R.id.txtNomeCachorro);
            imgFoto = itemView.findViewById(R.id.imgCachorro);
        }
    }
}