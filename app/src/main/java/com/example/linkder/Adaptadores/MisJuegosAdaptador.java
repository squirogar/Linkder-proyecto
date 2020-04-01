package com.example.linkder.Adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.linkder.Modelos.Juego;
import com.example.linkder.R;

import java.util.List;

public class MisJuegosAdaptador extends RecyclerView.Adapter<MisJuegosAdaptador.ViewHolder> {

    List<Juego> ShowList;
    Context context;
    int position;
    private OnItemClickListener itemClickListener;

    public MisJuegosAdaptador(List<Juego> showList, OnItemClickListener itemClickListener) {
        ShowList = showList;
        this.itemClickListener = itemClickListener;
    }


    /*se crea el viewholder que es el que se encargara de mostrar la información de cada elemento con el diseño respectivo*/
    @NonNull
    @Override
    public MisJuegosAdaptador.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        /*aqui se asocia el la vista individual que en este caso es el layout: list_item_alumno */
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adaptador_mis_juegos, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        /*se guarda el contexto y la posicion actual*/
        context = viewGroup.getContext();
        position = i;
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull MisJuegosAdaptador.ViewHolder viewHolder, final int i) {
        /*se pasa a cada uno de los elementos el evento listener junto con el respectivo elemento*/
        viewHolder.bind(ShowList.get(i), itemClickListener);
        position = i;
    }

    /*metodo para obtener el tamaño de la lista*/
    @Override
    public int getItemCount() {
        return ShowList.size();
    }

    /*eliminar un elemento del listado*/
    public void removeItem(int position) {
        ShowList.remove(position);
        notifyItemRemoved(position);
    }

    /*Se remplaza el listado*/
    public void updateList(List<Juego> data) {
        ShowList = data;
        notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textNombre;
        ImageView imageDelete;
        CardView cv;
        /*se referencian los elemento, tomar en cuenta que cada elemento posee su propia vista por eso se antepone itemview*/
        public ViewHolder(View itemView) {
            super(itemView);

            textNombre = itemView.findViewById(R.id.textNombre);
            imageDelete = itemView.findViewById(R.id.imageDelete);

            cv = itemView.findViewById(R.id.cardviewItemA);
        }

        /*se carga la información de cada juego y se crean los listener que se deseen para cada uno de los atributos*/
        public void bind(final Juego juego, final OnItemClickListener listener) {

            textNombre.setText(juego.getNombreJuego());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.OnItemClick(juego, getAdapterPosition());
                }
            });

            imageDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.OnDeleteClick(juego, getAdapterPosition());
                }
            });

        }
    }

    public interface OnItemClickListener {
        void OnItemClick(Juego juego, int position);

        void OnDeleteClick(Juego juego, int position);
    }

}
