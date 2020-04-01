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


import com.example.linkder.Modelos.Jugador;
import com.example.linkder.R;

import java.util.List;

public class JugadoresAdaptador extends RecyclerView.Adapter<JugadoresAdaptador.ViewHolder> {

    List<Jugador> ShowList;
    Context context;
    int position;
    private OnItemClickListener itemClickListener;

    public JugadoresAdaptador(List<Jugador> showList, OnItemClickListener itemClickListener) {
        ShowList = showList;
        this.itemClickListener = itemClickListener;
    }


    /*se crea el viewholder que es el que se encargara de mostrar la información de cada elemento con el diseño respectivo*/
    @NonNull
    @Override
    public JugadoresAdaptador.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        /*aqui se asocia el la vista individual que en este caso es el layout: list_item_alumno */
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adaptador_jugadores, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        /*se guarda el contexto y la posicion actual*/
        context = viewGroup.getContext();
        position = i;
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull JugadoresAdaptador.ViewHolder viewHolder, final int i) {
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
    public void updateList(List<Jugador> data) {
        ShowList = data;
        notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textNick;
        ImageView imagePerfilUsuario;
        CardView cv;
        /*se referencian los elemento, tomar en cuenta que cada elemento posee su propia vista por eso se antepone itemview*/
        public ViewHolder(View itemView) {
            super(itemView);

            textNick = itemView.findViewById(R.id.textNick);
            imagePerfilUsuario = itemView.findViewById(R.id.imagePerfilUsuario);

            cv = itemView.findViewById(R.id.cardviewItemA);
        }

        /*se carga la información de cada jugador y se crean los listener que se deseen para cada uno de los atributos*/
        public void bind(final Jugador jugador, final OnItemClickListener listener) {

            textNick.setText(jugador.getNickJugador());

            imagePerfilUsuario.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.OnItemClick(jugador, getAdapterPosition());
                }
            });

        }
    }

    public interface OnItemClickListener {
        void OnItemClick(Jugador jugador, int position);

    }

}
