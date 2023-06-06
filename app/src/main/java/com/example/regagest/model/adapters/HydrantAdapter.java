package com.example.regagest.model.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.regagest.R;
import com.example.regagest.model.Hydrant;

import java.util.List;

public class HydrantAdapter extends ArrayAdapter<Hydrant> {

    public HydrantAdapter(@NonNull Context context, @NonNull List<Hydrant> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        // Obtener inflater
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //Creamos HolderView
        ViewHolder holder;

        //Comprobamos si ya está infada la view
        if (convertView == null) {

            //Si no existe, entonces inflamos con item_cita.xml
            convertView = inflater.inflate(R.layout.item_hydrant, parent, false);

            //Inicializamos HolderView
            holder = new ViewHolder();

            //Referencias
            holder.tvNumeroParcela = convertView.findViewById(R.id.tv_numeroParcela);
            holder.tvEstado = convertView.findViewById(R.id.tv_estado);
            holder.tvNumeroHidrante = convertView.findViewById(R.id.tv_numeroHidrante);
            holder.ivTestigo = convertView.findViewById(R.id.iv_testigo);
            holder.tvNP = convertView.findViewById(R.id.ivNumP);
            holder.tvNH = convertView.findViewById(R.id.ivNumH);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        //Usuario actual
        Hydrant hydrant = getItem(position);

        //Setup
        holder.tvNumeroParcela.setText(String.valueOf(hydrant.getParcelNumber()));
        holder.tvEstado.setText(hydrant.getEstado());
        holder.tvNumeroHidrante.setText(String.valueOf(hydrant.getHydrantNumber()));
        holder.tvNP.setText(R.string.n_parcela);
        holder.tvNH.setText(R.string.n_hidrante);

        if(hydrant.getEstado().equalsIgnoreCase("off")){
            holder.ivTestigo.setImageResource(R.drawable.btn_off);
        }else if(hydrant.getEstado().equalsIgnoreCase("on")){
            holder.ivTestigo.setImageResource(R.drawable.btn_on);
        } else {
            holder.ivTestigo.setImageResource(R.drawable.btn_off);
        }

        return convertView;
    }
    static class ViewHolder {
        TextView tvNumeroParcela;
        TextView tvEstado;
        TextView tvNumeroHidrante;
        ImageView ivTestigo;
        TextView tvNP;
        TextView tvNH;
    }
}
