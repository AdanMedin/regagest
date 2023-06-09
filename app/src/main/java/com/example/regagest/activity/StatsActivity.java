package com.example.regagest.activity;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.regagest.R;
import com.example.regagest.database.AdminSQLiteOpenHelper;
import com.example.regagest.model.User;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class StatsActivity extends AppCompatActivity {
    User user;
    ImageView btnExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        //Inicia la toolbar
        Toolbar toolbar = findViewById(R.id.toolbarStats);
        setSupportActionBar(toolbar);

        //Elimina el titulo de la toolBar
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        //Añade boton ir atras
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        btnExit = findViewById(R.id.iBtnExitStats);

        Animation e = AnimationUtils.loadAnimation(this, R.anim.click);

        //Evento click para el boton exit
        btnExit.setOnClickListener(v -> {
            btnExit.startAnimation(e);
            Intent intent = new Intent(getApplicationContext(), LogInActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("EXIT", true);
            startActivity(intent);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Tomamos el objeto usuario enviado desde AccesoActivity
        //Para poder pasar el objeto Usuario, la clase Usuario tine que implementar serializable.
        user = (User) getIntent().getSerializableExtra("user");

        PieChart pieChart = findViewById(R.id.pieChart);

        pieChartCreator(pieChart, loadQuotaAndConsumption());
    }


    //Metodo para consultar consumo y cuota. Y añadirlo al gráfico.
    private void pieChartCreator(PieChart pieChart, Map<String, Double> QuotaConsumptionMap) {

        ArrayList<PieEntry> entries = new ArrayList<>();
        float consumption = Objects.requireNonNull(QuotaConsumptionMap.get("consumption")).floatValue();
        float quota = Objects.requireNonNull(QuotaConsumptionMap.get("quota")).floatValue();
        entries.add(new PieEntry(consumption, "Consumo actual"));
        entries.add(new PieEntry(quota, "Cuota restante"));

        pieChartSetterAndAnimator(pieChart, entries);

    }

    //Metodo que setea los valores en el gráfico e inicia las animaciones
    private void pieChartSetterAndAnimator(PieChart pieChart, ArrayList<PieEntry> entries) {

        float startValueConsumoActual = 0f;
        float endValueConsumoActual = entries.get(0).getValue();
        float startValueCuotaRestante = entries.get(1).getValue();
        int duration = 1500;

        ValueAnimator animator = ValueAnimator.ofFloat(startValueConsumoActual, endValueConsumoActual);
        animator.setDuration(duration);

        animator.addUpdateListener(animation -> {
            float animatedValueConsumoActual = (float) animation.getAnimatedValue();
            float animatedValueCuotaRestante = startValueCuotaRestante - (animatedValueConsumoActual - startValueConsumoActual);

            int[] customPieColors = new int[]{
                    Color.rgb(255, 149, 96),  // Rojo (Consumo actual)
                    Color.rgb(72, 177, 255)   // Azul (Cuota restante)
            };

            int customValueColor = Color.rgb(86, 86, 86); // Gris oscuro

            Typeface boldType = Typeface.create(Typeface.DEFAULT, Typeface.BOLD);

            ArrayList<PieEntry> animatedEntries = new ArrayList<>();
            animatedEntries.add(new PieEntry(animatedValueConsumoActual, "Consumo actual"));
            animatedEntries.add(new PieEntry(animatedValueCuotaRestante, "Cuota restante"));

            PieDataSet dataSet = new PieDataSet(animatedEntries, "");
            dataSet.setColors(customPieColors);
            dataSet.setValueTextColor(customValueColor);
            dataSet.setValueTypeface(boldType);
            dataSet.setValueTextSize(18f);

            PieData pieData = new PieData(dataSet);
            pieChart.setData(pieData);
            pieChart.invalidate();
            pieChart.getDescription().setEnabled(false);
            pieChart.setCenterText("Consumo/Cuota");
            pieChart.setTransparentCircleRadius(45);
            pieChart.setHoleRadius(40);
            pieChart.setCenterTextTypeface(boldType);
            pieChart.setCenterTextSize(18f);
            pieChart.setCenterTextColor(customValueColor);
            pieChart.animate();

            pieChart.setDrawEntryLabels(true);
            pieChart.setEntryLabelColor(customValueColor);
            pieChart.setEntryLabelTextSize(15f);
            pieChart.setEntryLabelTypeface(boldType);
            pieChart.getLegend().setTextSize(20f);

            // Configura la sombra en el Paint del PieChart
            Paint paint = pieChart.getRenderer().getPaintRender();
            paint.setShadowLayer(30f, 0f, 2f, Color.DKGRAY);

            // Establecer márgenes adicionales en el gráfico
            pieChart.setExtraLeftOffset(10);
            pieChart.setExtraRightOffset(10);

        });

        animator.start();
    }


    //Método que consulta el consumo y la cuota del comunero y los añade a una lista clave valor.
    private Map<String, Double> loadQuotaAndConsumption() {

        Map<String, Double> QuotaConsumptionMap = new HashMap<>();

        //Creamos conexión con la base de datos
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "regagest", null, 1);
        SQLiteDatabase database = admin.getWritableDatabase();//Modo lectura y escritura

        //Consulta
        Cursor cursor = database.rawQuery(
                "SELECT c.cuota, SUM(p.consumo) AS consumo_total " +
                        "FROM comuneros c " +
                        "LEFT JOIN parcelas p ON c.id_comunero = p.id_comunero " +
                        "WHERE c.id_comunero = ? " +
                        "GROUP BY c.cuota", new String[]{user.getId()});

        if (cursor.moveToFirst()) {
            do {
                //Tomamos los datos
                double quota = cursor.getDouble(0);
                double consumption = cursor.getDouble(1);

                //Añadimos cada uno de los valores al Map con su clave
                QuotaConsumptionMap.put("quota", quota);
                QuotaConsumptionMap.put("consumption", consumption);

            } while (cursor.moveToNext());

        } else {
            Toast.makeText(this, "La base de datos está vacía", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "Error processing request: Database is empty");
        }
        database.close();
        cursor.close();

        return QuotaConsumptionMap;
    }
}