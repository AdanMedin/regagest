package com.example.regagest.activity;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.regagest.R;
import com.example.regagest.database.AdminSQLiteOpenHelper;
import com.example.regagest.model.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AdminActivity extends AppCompatActivity {

    User user;
    Button btnAssignQuota, btnExpandQuota, btnResetConsumption, btnCurrentConsumption, btnResetQuota;
    ImageButton btnExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        //Inicia la toolbar
        Toolbar toolbar = findViewById(R.id.toolbarAdmin);
        setSupportActionBar(toolbar);

        //Elimina el titulo de la toolBar
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        btnExit = findViewById(R.id.iBtnExitAdmin);
        btnAssignQuota = findViewById(R.id.btn1Admin);
        btnExpandQuota = findViewById(R.id.btnAdmin2);
        btnResetConsumption = findViewById(R.id.btnAdmin3);
        btnResetQuota = findViewById(R.id.btnAdmin4);
        btnCurrentConsumption = findViewById(R.id.btnAdmin5);


        Animation e = AnimationUtils.loadAnimation(this, R.anim.click);

        //Evento click para el boton exit
        btnExit.setOnClickListener(v -> {
            btnExit.startAnimation(e);
            Intent intent = new Intent(getApplicationContext(), LogInActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("EXIT", true);
            startActivity(intent);
        });

        btnAssignQuota.setOnClickListener(v -> {
            btnAssignQuota.startAnimation(e);
            fireCustomDialog();
        });

        btnExpandQuota.setOnClickListener(v -> {
            btnExpandQuota.startAnimation(e);
            fireCustomDialog1();
        });

        btnResetConsumption.setOnClickListener(v -> {
            btnResetConsumption.startAnimation(e);

            //Diálogo de confirmación
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            //Acción deseada al presionar si o no
            builder.setMessage("¿Seguro que quiere reiniciar el consumo de los comuneros?").setCancelable(false).setPositiveButton("Si", (dialog, i) -> resetConsumptions()).setNegativeButton("No", (dialog, i) -> dialog.cancel());
            AlertDialog alert = builder.create();
            alert.show();
        });

        btnResetQuota.setOnClickListener(v -> {
            btnResetQuota.startAnimation(e);

            //Diálogo de confirmación
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            //Acción deseada al presionar si o no
            builder.setMessage("¿Seguro que quiere reiniciar la cuota de los comuneros?").setCancelable(false).setPositiveButton("Si", (dialog, i) -> resetQuotas()).setNegativeButton("No", (dialog, i) -> dialog.cancel());
            AlertDialog alert = builder.create();
            alert.show();
        });

        btnCurrentConsumption.setOnClickListener(v -> {
            btnCurrentConsumption.startAnimation(e);
            Intent intent = new Intent(AdminActivity.this, AdminStatsActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Tomamos el objeto usuario enviado desde AccesoActivity
        //Para poder pasar el objeto Usuario, la clase Usuario tine que implementar serializable.
        user = (User) getIntent().getSerializableExtra("user");
    }

    /********************************** MÉTODOS ****************************************/

    //Metodo para crear dialogo e insertar el layout del pop up asignar cuota
    private void fireCustomDialog() {

        //Creamos el dialogo y seteamos el layout de registro y el background del dialogo
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_assign_quota);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        //Buscamos componentes del layout y los guardamos en variables
        EditText etAssignQuota = dialog.findViewById(R.id.editTextPUAssignQuota);
        Button btnAssignPU = dialog.findViewById(R.id.btnAssignPUAssignQuota);
        Button btnCancelPU = dialog.findViewById(R.id.btnCancelPUAssignQuota);

        //On clicks de los botones registrar y cancelar
        btnAssignPU.setOnClickListener(v -> {
            // Verificar si el EditText está en blanco
            if (etAssignQuota.getText().toString().isBlank()) {
                Toast.makeText(this, "Ingrese un valor de cuota", Toast.LENGTH_SHORT).show();
                return;
            }
            // Verificar si el valor no es un número
            try {
                Double.parseDouble(etAssignQuota.getText().toString());
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Ingrese un número válido", Toast.LENGTH_SHORT).show();
                return;
            }
            //Diálogo de confirmación
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            //Acción deseada al presionar si o no
            builder.setMessage("¿Quieres asignar " + etAssignQuota.getText().toString() + " m3 de cuota anual a los comuneros?").setCancelable(false).setPositiveButton("Si", (dialogo, i) -> {

                boolean addOk = setQuotasForCommoners(Double.parseDouble(etAssignQuota.getText().toString()));

                if (addOk) {
                    dialog.dismiss();
                }

            }).setNegativeButton("No", (dialogo, i) -> dialog.show());
            AlertDialog alert = builder.create();
            alert.show();
        });
        btnCancelPU.setOnClickListener(v -> dialog.dismiss());
        //Mostramos el dialogo
        dialog.show();
    }

    //Metodo para crear dialogo e insertar el layout del pop up aumentar cuota
    private void fireCustomDialog1() {

        //Creamos el dialogo y seteamos el layout de registro y el background del dialogo
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_expand_quota);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        //Buscamos componentes del layout y los guardamos en variables
        EditText etAmount = dialog.findViewById(R.id.etAmountToExpandPopUpExpandQuota);
        EditText etDNI = dialog.findViewById(R.id.etDNIPopUpExpandQuota);
        Button btnAssignPU = dialog.findViewById(R.id.btnAssignPUExpandQuota);
        Button btnCancelPU = dialog.findViewById(R.id.btnCancelPUExpandQuota);

        //On clicks de los botones registrar y cancelar
        btnAssignPU.setOnClickListener(v -> {

            // Verificar si el EditText está en blanco
            if (etAmount.getText().toString().isBlank()) {
                Toast.makeText(this, "Ingrese un valor de cuota", Toast.LENGTH_SHORT).show();
                return;
            }
            if (etDNI.getText().toString().isBlank()) {
                Toast.makeText(this, "Ingrese un valor de cuota", Toast.LENGTH_SHORT).show();
                return;
            }
            // Verificar si el valor no es un número
            try {
                Double.parseDouble(etAmount.getText().toString());
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Ingrese un número válido", Toast.LENGTH_SHORT).show();
                return;
            }
            // Verificar si el contenido cumple con la expresión regular
            String dni = etDNI.getText().toString().trim();
            String dniRegex = "\\d{8}[a-zA-Z]$";

            if (!dni.matches(dniRegex)) {
                Toast.makeText(this, "Ingrese un DNI válido", Toast.LENGTH_SHORT).show();
                return;
            }
            //Diálogo de confirmación
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            //Acción deseada al presionar si o no
            builder.setMessage("¿Quieres aumentar la cuota del comunero con DNI: " + etDNI.getText().toString() + " en " + etAmount.getText().toString() + " m3?").setCancelable(false).setPositiveButton("Si", (dialogo, i) -> {

                boolean addOk = setExpandQuota(Double.parseDouble(etAmount.getText().toString()), etDNI.getText().toString().toLowerCase());

                if (addOk) {
                    dialog.dismiss();
                }

            }).setNegativeButton("No", (dialogo, i) -> dialog.show());
            AlertDialog alert = builder.create();
            alert.show();
        });
        btnCancelPU.setOnClickListener(v -> dialog.dismiss());
        //Mostramos el dialogo
        dialog.show();
    }

    //Metodo que inserta la cuota ampliada al comunero en la base de datos
    public boolean setExpandQuota(double amountQuota, String dni) {
        boolean setOk = false;

        // Creamos conexión con la base de datos
        AdminSQLiteOpenHelper adminDB = new AdminSQLiteOpenHelper(this, "regagest", null, 1);
        try {
            SQLiteDatabase db = adminDB.getWritableDatabase(); // Modo lectura y escritura

            String commonerId = getCommonerIdByDNI(db, dni);

            if (!commonerId.isBlank()) {

                // Obtener el valor actual de la cuota
                Cursor cursor = db.rawQuery("SELECT cuota FROM comuneros WHERE id_comunero = ?", new String[]{commonerId});

                if (cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndex("cuota");

                    if (columnIndex != -1) {
                        int currentQuota = cursor.getInt(columnIndex);

                        Double newQuota = currentQuota + amountQuota;

                        ContentValues values = new ContentValues();
                        values.put("cuota", newQuota);

                        db.update("comuneros", values, "id_comunero = ?", new String[]{commonerId});

                        setOk = true;

                        Toast.makeText(this, "La cuota del comunero " + commonerId + " ha sido ampliada en " + amountQuota + " m3.", Toast.LENGTH_LONG).show();
                    }
                }
                cursor.close();
            } else {
                Toast.makeText(this, "Comunero con DNI: " + dni + " no existe", Toast.LENGTH_SHORT).show();
            }
            db.close();

        } catch (SQLiteException e) {
            Toast.makeText(this, "No se ha podido establecer la conexión con la base de datos de RegaGest", Toast.LENGTH_LONG).show();
            Log.e(TAG, "Error processing request, imposible database connection: " + e.getMessage());
        }
        return setOk;
    }


    //Metodo que inserta las cuotas calculadas en la base de datos
    public boolean setQuotasForCommoners(double annualCommunityQuota) {
        boolean setOk = false;

        // Creamos conexión con la base de datos
        AdminSQLiteOpenHelper adminDB = new AdminSQLiteOpenHelper(this, "regagest", null, 1);
        try {
            SQLiteDatabase db = adminDB.getWritableDatabase(); // Modo lectura y escritura

            Map<String, Integer> commonersAssignments = quotaDealer(annualCommunityQuota, db);

            for (Map.Entry<String, Integer> entry : commonersAssignments.entrySet()) {
                String commonerId = entry.getKey();
                int quota = entry.getValue();

                ContentValues values = new ContentValues();
                values.put("cuota", quota);

                db.update("comuneros", values, "id_comunero = ?", new String[]{commonerId});
            }

            db.close();

            setOk = true;

            Toast.makeText(this, "La cuota anual de los comuneros ha sido repartida. Cuota total de la comunidad para este año: " + annualCommunityQuota + " m3", Toast.LENGTH_LONG).show();

        } catch (SQLiteException e) {
            Toast.makeText(this, "No se ha podido establecer la conexión con la base de datos de RegaGest", Toast.LENGTH_LONG).show();
            Log.e(TAG, "Error processing request, imposible database connection: " + e.getMessage());
        }
        return setOk;
    }


    //Metodo que obtiene a todos los comuneros con el total de terrenos y los devuelve en un map
    public Map<String, Integer> getAllCommonerAndLand(SQLiteDatabase db) {
        Map<String, Integer> landCommoner = new HashMap<>();

        try {
            Cursor cursor = db.rawQuery("SELECT id_comunero, SUM(tamaño) AS total_terrenos FROM parcelas GROUP BY id_comunero", null);

            if (cursor.moveToFirst()) {

                int columnIndexCommonerId = cursor.getColumnIndex("id_comunero");
                int columnIndexTotalLands = cursor.getColumnIndex("total_terrenos");

                do {
                    if (columnIndexCommonerId != -1 && columnIndexTotalLands != -1) {
                        String commonerId = cursor.getString(columnIndexCommonerId);
                        int totalLands = cursor.getInt(columnIndexTotalLands);
                        landCommoner.put(commonerId, totalLands);
                    }
                } while (cursor.moveToNext());
            }

            cursor.close();

        } catch (SQLiteException e) {
            Toast.makeText(this, "No se ha podido establecer la conexión con la base de datos de RegaGest", Toast.LENGTH_LONG).show();
            Log.e(TAG, "Error processing request, imposible database connection: " + e.getMessage());
        }
        return landCommoner;
    }

    //Metodo que reparte el total de la cuota de la comunidad de regantes de forma proporcional al terreo que posea cada comunero
    // y los devuelve en un map con clave id del comunero y valor la cuota correspondiente.
    public Map<String, Integer> quotaDealer(double annualCommunityQuota, SQLiteDatabase db) {
        Map<String, Integer> commonersAndLands = getAllCommonerAndLand(db);
        // Calculamos la suma total de terrenos
        int totalLands = 0;
        for (int lands : commonersAndLands.values()) {
            totalLands += lands;
        }

        // Creamos un nuevo mapa para almacenar la cantidad asignada a cada comunero
        Map<String, Integer> commonersAssignments = new HashMap<>();

        // Repartimos el número proporcionalmente al terreno de cada comunero
        for (Map.Entry<String, Integer> entry : commonersAndLands.entrySet()) {
            String idCommoners = entry.getKey();
            int lands = entry.getValue();

            // Calculamos la asignación proporcional al terreno
            int assignment = (int) Math.round(annualCommunityQuota * lands / totalLands);

            // Agregamos la asignación al mapa de commonersAssignments
            commonersAssignments.put(idCommoners, assignment);
        }

        return commonersAssignments;
    }

    //Metodo que obtiene el id del comunero a partir del dni
    public String getCommonerIdByDNI(SQLiteDatabase db, String dni) {
        String commonerId = "";

        try {
            Cursor cursor = db.rawQuery("SELECT id_comunero FROM comuneros WHERE dni = ?", new String[]{dni});

            if (cursor.moveToFirst()) {
                int columnIndexCommonerDNI = cursor.getColumnIndex("id_comunero");

                do {
                    if (columnIndexCommonerDNI != -1) {
                        commonerId = cursor.getString(columnIndexCommonerDNI);
                    }
                } while (cursor.moveToNext());
            }

            cursor.close();

        } catch (SQLiteException e) {
            Toast.makeText(this, "No se ha podido establecer la conexión con la base de datos de RegaGest", Toast.LENGTH_LONG).show();
            Log.e(TAG, "Error processing request, imposible database connection: " + e.getMessage());
        }

        return commonerId;
    }

    public void resetConsumptions() {

        // Creamos conexión con la base de datos
        AdminSQLiteOpenHelper adminDB = new AdminSQLiteOpenHelper(this, "regagest", null, 1);
        try {
            SQLiteDatabase db = adminDB.getWritableDatabase(); // Modo lectura y escritura

            // Setea a 0 todas las entradas de la columna consumo
            db.execSQL("UPDATE parcelas SET consumo = 0");

            Toast.makeText(this, "El consumo de los comuneros ha sido reiniciado", Toast.LENGTH_LONG).show();

            db.close();

        } catch (SQLiteException e) {
            Toast.makeText(this, "No se ha podido establecer la conexión con la base de datos de RegaGest", Toast.LENGTH_LONG).show();
            Log.e(TAG, "Error processing request, imposible database connection: " + e.getMessage());
        }
    }

    public void resetQuotas() {

        // Creamos conexión con la base de datos
        AdminSQLiteOpenHelper adminDB = new AdminSQLiteOpenHelper(this, "regagest", null, 1);
        try {
            SQLiteDatabase db = adminDB.getWritableDatabase(); // Modo lectura y escritura

            // Setea a 0 todas las entradas de la columna cuota
            db.execSQL("UPDATE comuneros SET cuota = 0");

            Toast.makeText(this, "La cuota de los comuneros ha sido reiniciado", Toast.LENGTH_LONG).show();

            db.close();

        } catch (SQLiteException e) {
            Toast.makeText(this, "No se ha podido establecer la conexión con la base de datos de RegaGest", Toast.LENGTH_LONG).show();
            Log.e(TAG, "Error processing request, imposible database connection: " + e.getMessage());
        }
    }
}