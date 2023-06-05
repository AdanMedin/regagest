package com.example.regagest.activity;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.regagest.database.AdminSQLiteOpenHelper;
import com.example.regagest.R;
import com.example.regagest.model.User;
import com.example.regagest.splash.ExitSplashActivity;

import java.util.Objects;

public class LogInActivity extends AppCompatActivity {

    EditText etUser, etPass;
    Button btnAcces;
    ImageButton btnExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        //Escucha si se cierra la app desde otro activity
        if (getIntent().getBooleanExtra("EXIT", false)) {
            Intent intent = new Intent(getApplicationContext(), ExitSplashActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }

        //Inicia la toolbar
        Toolbar toolbar = findViewById(R.id.toolbAcceso);
        setSupportActionBar(toolbar);

        //Elimina el titulo de la toolBar
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        etUser = findViewById(R.id.etUsuario);
        etPass = findViewById(R.id.etPass);
        btnAcces = findViewById(R.id.btnAcceder);
        btnExit = findViewById(R.id.btnExitLogIn);

        //Evento click para el boton exit
        Animation e = AnimationUtils.loadAnimation(this, R.anim.click);
        btnExit.setOnClickListener(v -> {
            btnExit.startAnimation(e);
            Intent intent = new Intent(getApplicationContext(), ExitSplashActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });

        //Evento click para el boton acceder
        btnAcces.setOnClickListener(this::access);
    }

    /********************************** MÉTODOS ****************************************/

    public void access(View v) {

        //Creamos conexión con la base de datos
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "regagest", null, 1);
        try {
            SQLiteDatabase database = admin.getWritableDatabase();//Modo lectura y escritura
            //Consulta
            String userPar = etUser.getText().toString();
            String passPar = etPass.getText().toString();
            Cursor cursor = database.rawQuery("select usuario, pass, id_comunero, admin from usuarios where usuario = ? and pass = ?", new String[]{userPar, passPar});

            Log.i(TAG, "Request result: " + cursor);

            //Try Catch para recoger posibles errores
            try {
                if (cursor.moveToFirst()) {
                    //Tomamos los datos
                    String usuario = cursor.getString(0);
                    String id = cursor.getString(2);
                    String isAdmin = cursor.getString(3);

                    User user = new User(id, usuario);

                    //Pasamos el objeto usuario con los datos a la siguiente activity
                    Bundle extras = new Bundle();
                    extras.putSerializable("user", user);
                    Intent intent;

                    //En caso de haber coincidencia y ser admin iniciamos el AdminActivity
                    if(isAdmin.equalsIgnoreCase("si")){
                        intent = new Intent(LogInActivity.this, AdminActivity.class);
                    } else {
                        //En caso de haber coincidencia y no ser admin iniciamos el ValveControlActivity
                        intent = new Intent(LogInActivity.this, ValveControlActivity.class);
                    }

                    intent.putExtras(extras);
                    startActivity(intent);

                    //Limpiamos los campos
                    etUser.setText("");
                    etPass.setText("");

                } else {
                    Toast.makeText(this, "Usuario o contraseña incorrectos o no registrados", Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "Incorrect or unregistered username (" + userPar + ") or password (" + passPar + ")");
                }

                //Cerramos db y cursor
                database.close();
                cursor.close();

            } catch (Exception e) {
                Toast.makeText(this, "Error" + e.getMessage(), Toast.LENGTH_LONG).show();
                Log.e(TAG, "Error processing request: " + e.getMessage());
            }
        } catch (SQLiteException e) {
            Toast.makeText(this, "No se ha podido establecer la conexión con la base de datos de RegaGest", Toast.LENGTH_LONG).show();
            Log.e(TAG, "Error processing request, imposible database connection: " + e.getMessage());
        }
    }
}
