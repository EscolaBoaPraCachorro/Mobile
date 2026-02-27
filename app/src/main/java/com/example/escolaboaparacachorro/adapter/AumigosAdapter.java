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

public class AumigosAdapter extends RecyclerView.Adapter<AumigosAdapter.AumigosViewHolder>{

    private List<Cachorro> listaCachorros;

    public AumigosAdapter(List<Cachorro> lista) {
        this.listaCachorros = lista;
    }

    @NonNull
    @Override
    public AumigosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_aumigo, parent, false);
        return new AumigosViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AumigosViewHolder holder, int position) {
        Cachorro cachorro = listaCachorros.get(position);

        holder.txtNome.setText(cachorro.getNome());
        holder.txtRacaSexo.setText(cachorro.getRaca() + "     " + cachorro.getSexo());


        Glide.with(holder.itemView.getContext())
                .load(cachorro.getImagem()).into(holder.imgFoto);

    }

    @Override
    public int getItemCount() {
        return listaCachorros != null ? listaCachorros.size() : 0;
    }


    public static class AumigosViewHolder extends RecyclerView.ViewHolder {
        TextView txtNome, txtRacaSexo;
        ShapeableImageView imgFoto;

        public AumigosViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNome = itemView.findViewById(R.id.nome);
            txtRacaSexo = itemView.findViewById(R.id.info);
            imgFoto = itemView.findViewById(R.id.foto);
        }
    }

}
