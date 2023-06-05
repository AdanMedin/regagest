package com.example.regagest.activity;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;


import com.example.regagest.R;
import com.example.regagest.database.AdminSQLiteOpenHelper;
import com.example.regagest.model.Hydrant;
import com.example.regagest.model.User;
import com.example.regagest.model.adapters.HydrantAdapter;

import java.util.ArrayList;
import java.util.Objects;

public class ValveControlActivity extends AppCompatActivity {

    User user;
    HydrantAdapter adapter;
    ListView hydrantsListView;
    ImageView btnExit, btnStats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_valve_control);

        //Inicia la toolbar
        Toolbar toolbar = findViewById(R.id.toolbarStats);
        setSupportActionBar(toolbar);

        //Elimina el titulo de la toolBar
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        hydrantsListView = findViewById(R.id.listViewHydrants);
        btnExit = findViewById(R.id.iBtnExitStats);
        btnStats = findViewById(R.id.ibtnStats);

        Animation e = AnimationUtils.loadAnimation(this, R.anim.click);

        Bundle extras = new Bundle();

        //Evento click para el boton exit
        btnExit.setOnClickListener(v -> {
            btnExit.startAnimation(e);
            Intent intent = new Intent(getApplicationContext(), LogInActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("EXIT", true);
            startActivity(intent);
        });

        btnStats.setOnClickListener(v -> {
            btnStats.startAnimation(e);

            //Pasamos el objeto usuario con los datos a la siguiente activity
            extras.putSerializable("user", user);

            Intent intent = new Intent(ValveControlActivity.this, StatsActivity.class);
            intent.putExtras(extras);
            startActivity(intent);
        });

        //Evento click para los items de la lista de hidrantes
        hydrantsListView.setOnItemLongClickListener((parent, view, position, id) -> {
            Hydrant currentHydrant = adapter.getItem(position);

            if (checkQuota(user.getId())){
                Toast.makeText(this, "Ya ha consumido su cuota anual. Si quiere ampliar su cuota comuníqueselo al administrador", Toast.LENGTH_LONG).show();
                return true;
            }

            //Diálogo de confirmación
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            //Acción deseada al presionar si o no
            builder.setMessage("¿Quieres abrir el hidrante: " + currentHydrant.getHydrantNumber() +
                            " perteneciente a la parcela número: " + currentHydrant.getParcelNumber() + " ?")
                    .setCancelable(false).setPositiveButton("Si", (dialog, i) -> {

                        //Enciende/apaga hidrante y cambia estado del hidrante en la base de datos
                        turnOnOffHydrant(currentHydrant);

                        //Cargamos de nuevo la lista de hydrantes
                        searchAllHydrant();

                    }).setNegativeButton("No", (dialog, i) -> dialog.cancel());
            AlertDialog alert = builder.create();
            alert.show();
            return true;
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Tomamos el objeto usuario enviado desde AccesoActivity
        //Para poder pasar el objeto Usuario, la clase Usuario tine que implementar serializable.
        user = (User) getIntent().getSerializableExtra("user");
        searchAllHydrant();
    }


    /********************************** MÉTODOS ****************************************/

    private void turnOnOffHydrant(Hydrant currenthydrant) {

        // Creamos conexión con la base de datos
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "regagest", null, 1);
        try {
            SQLiteDatabase database = admin.getWritableDatabase(); // Modo lectura y escritura

            // Ejecutar la consulta SQL para obtener el estado actual del hidrante
            Cursor cursor = database.rawQuery("SELECT estado FROM hidrantes WHERE num_hidrante = ?",
                    new String[]{String.valueOf(currenthydrant.getHydrantNumber())});

            if (cursor.moveToFirst()) {
                // Obtener el estado actual del hidrante
                String estadoActual = cursor.getString(0);

                // Calcular el nuevo estado del hidrante
                String nuevoEstado = (estadoActual.equals("off")) ? "on" : "off";

                // Parámetros de la consulta de actualización
                String[] updateArgs = {nuevoEstado, String.valueOf(currenthydrant.getHydrantNumber())};

                // Ejecutar la SQL para actualizar el estado del hidrante
                database.execSQL("UPDATE hidrantes SET estado = ? WHERE num_hidrante = ?", updateArgs);

                Toast.makeText(this, "Estado del hidrante número " + currenthydrant.getHydrantNumber() + " actualizado: " + nuevoEstado, Toast.LENGTH_SHORT).show();
                Log.i(TAG, "State of hydrant updated: " + nuevoEstado);

            } else {
                Toast.makeText(this, "No se encontró el hidrante con el número: " + currenthydrant.getHydrantNumber(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Hydrant with number: " + currenthydrant.getHydrantNumber() + " not found");
            }

            // Cerrar el cursor y la base de datos
            cursor.close();
            database.close();
        } catch (SQLiteException e) {
            Toast.makeText(this, "Error al actualizar el estado del hidrante: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Error updating hydrant status: " + e.getMessage());
        }
    }

    //Metodo para consultar todos los hidrantes del usuario
    private void searchAllHydrant() {
        ArrayList<Hydrant> hydrantList;
        hydrantList = loadHydrantData();

        //Instanciamos el adapter y le pasamos el array con los hidrantes encontrados
        adapter = new HydrantAdapter(this, hydrantList);
        hydrantsListView.setAdapter(adapter);
    }

    //Metodo carga todas los hirantes del usuario en un array
    private ArrayList<Hydrant> loadHydrantData() {
        //Creamos conexión con la base de datos
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "regagest", null, 1);
        SQLiteDatabase database = admin.getWritableDatabase();//Modo lectura y escritura
        ArrayList<Hydrant> hydrantList = new ArrayList<>();

        //Consulta
        Cursor cursor = database.rawQuery(
                "SELECT num_hidrante, num_parcela, estado " +
                        "FROM hidrantes WHERE id_comunero = " +
                        "(SELECT id_comunero FROM usuarios " +
                        "WHERE usuario = ?)", new String[]{user.getUser()});

        if (cursor.moveToFirst()) {
            do {
                //Tomamos los datos y creamos un objeto Hydrant que se guarda en el array para cada uno de los resultados obtenidos con la consulta
                int numeroHidrante = cursor.getInt(0);
                int numeroParcela = cursor.getInt(1);
                String estado = cursor.getString(2);

                //Añadimis cada uno de los hidrantes creados al array
                hydrantList.add(new Hydrant(numeroHidrante, numeroParcela, estado));

            } while (cursor.moveToNext());

        } else {
            Toast.makeText(this, "La base de datos está vacía", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "Error processing request: Database is empty");
        }
        database.close();
        cursor.close();
        return hydrantList;
    }

    public boolean checkQuota(String idComunero) {
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "regagest", null, 1);
        SQLiteDatabase db = admin.getReadableDatabase();

        // Obtener la suma del consumo total de las parcelas del comunero
        Cursor cursor = db.rawQuery(
                "SELECT SUM(consumo) AS totalConsumo FROM parcelas WHERE id_comunero = ?", new String[]{idComunero}
        );

        double totalConsumo = 0;
        if (cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex("totalConsumo");
            if (columnIndex != -1 && !cursor.isNull(columnIndex)) {
                totalConsumo = cursor.getDouble(columnIndex);
            }
        }
        cursor.close();

        // Obtener la cuota del comunero
        cursor = db.rawQuery(
                "SELECT cuota FROM comuneros WHERE id_comunero = ?", new String[]{idComunero}
        );

        int cuota = 0;
        if (cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex("cuota");
            if (columnIndex != -1 && !cursor.isNull(columnIndex)) {
                cuota = cursor.getInt(columnIndex);
            }
        }
        cursor.close();

        return totalConsumo >= cuota;
    }
}