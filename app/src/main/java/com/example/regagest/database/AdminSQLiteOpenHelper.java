package com.example.regagest.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class AdminSQLiteOpenHelper extends SQLiteOpenHelper {

    public AdminSQLiteOpenHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        /*TABLAS*/

        //Tabla comuneros
        db.execSQL("CREATE TABLE comuneros (" +
                "id_comunero CHAR(7) PRIMARY KEY NOT NULL," +
                "nombre VARCHAR(100) NOT NULL," +
                "dni CHAR(9) NOT NULL," +
                "tlf INTEGER NOT NULL," +
                "cuota INTEGER NOT NULL DEFAULT 0," +
                "CONSTRAINT chk_id_comunero CHECK (id_comunero REGEXP '^[a-z]{3}[0-9]{4}$'))"
        );

        //Tabla usuarios
        db.execSQL("CREATE TABLE usuarios (" +
                "usuario VARCHAR(20) PRIMARY KEY NOT NULL," +
                "pass VARCHAR(20) NOT NULL," +
                "email VARCHAR(255) NOT NULL," +
                "id_comunero CHAR(7) NOT NULL," +
                "admin TEXT NOT NULL DEFAULT 'no'," +
                "CONSTRAINT fk_usuario_id_comunero FOREIGN KEY (id_comunero) REFERENCES comuneros(id_comunero) ON DELETE CASCADE ON UPDATE CASCADE," +
                "CONSTRAINT chk_admin_usuario CHECK (admin IN ('si', 'no')))"
        );


        //Tabla parcelas
        db.execSQL("CREATE TABLE parcelas (" +
                "num_parcela INTEGER PRIMARY KEY NOT NULL," +
                "id_comunero CHAR(7) NOT NULL," +
                "nombre_parcela VARCHAR(30)," +
                "tipo TEXT NOT NULL DEFAULT 'regadio'," +
                "tamaño INTEGER NOT NULL DEFAULT 0," +
                "consumo NUMERIC(7, 3) NOT NULL DEFAULT 0.0," +
                "CONSTRAINT fk_parcela_comunero FOREIGN KEY (id_comunero) REFERENCES comuneros(id_comunero) ON DELETE NO ACTION ON UPDATE CASCADE, " +
                "CONSTRAINT chk_tipo_parcela CHECK (tipo IN ('secano', 'regadio')))"
        );

        //Tabla hidrantes
        db.execSQL("CREATE TABLE hidrantes (" +
                "num_hidrante INTEGER PRIMARY KEY AUTOINCREMENT," +
                "id_comunero CHAR(7) NOT NULL," +
                "num_parcela INTEGER NOT NULL," +
                "estado TEXT NOT NULL DEFAULT 'off'," +
                "CONSTRAINT fk_hidrante_comunero FOREIGN KEY (id_comunero) REFERENCES comuneros(id_comunero) ON DELETE NO ACTION ON UPDATE CASCADE," +
                "CONSTRAINT fk_hidrante_parcela FOREIGN KEY (num_parcela) REFERENCES parcelas(num_parcela) ON DELETE NO ACTION ON UPDATE CASCADE," +
                "CONSTRAINT chk_estado_hidrante CHECK (estado IN ('on', 'off')))"
        );



        /*INSERTS*/

        //Inserts por defecto tabla comuneros
        db.execSQL("INSERT INTO comuneros (id_comunero, nombre, dni, tlf, cuota) VALUES " +
                "('jpp1234', 'Juan Pérez Pérez', '12345678a', 623456789, 14550), " +
                "('agl5678', 'Ana García Lopez', '23456789b', 687654321, 28281), " +
                "('prs9012', 'Pedro Ruiz Saenz', '34567890c', 655555555, 7169)"
        );

        //Inserts por defecto tabla usuarios
        db.execSQL("INSERT INTO usuarios (usuario, pass, email, id_comunero) VALUES " +
                "('Juan', 'Juan123', 'jpp@example.com', 'jpp1234'), " +
                "('Ana', 'Ana123', 'agl@example.com', 'agl5678'), " +
                "('Pedro', 'Pedro123', 'prs@example.com', 'prs9012')"
        );

        //Inserts por defecto tabla parcelas
        db.execSQL("INSERT INTO parcelas (num_parcela, id_comunero, nombre_parcela, tipo, tamaño) VALUES " +
                "(42, 'jpp1234', 'parcela a', 'secano', 18325), " +
                "(223, 'jpp1234', 'parcela b', 'regadio', 8999), " +
                "(68, 'agl5678', 'parcela c', 'secano', 21042), " +
                "(40, 'agl5678', 'parcela d', 'regadio', 11027), " +
                "(52, 'prs9012', 'parcela e', 'secano', 5732), " +
                "(73, 'prs9012', 'parcela f', 'secano', 7732), " +
                "(4, 'agl5678', 'parcela g', 'regadio', 21042)"
        );

        //Inserts por defecto tabla hidrantes
        db.execSQL("INSERT INTO hidrantes (id_comunero, num_parcela, estado) VALUES " +
                "('jpp1234', 42, 'off'), " +
                "('jpp1234', 223, 'off'), " +
                "('agl5678', 68, 'off'), " +
                "('agl5678', 40, 'off'), " +
                "('prs9012', 52, 'off'), " +
                "('prs9012', 73, 'off'), " +
                "('agl5678', 4, 'off')"
        );

        //Inserts por defecto admin
        db.execSQL("INSERT INTO comuneros (id_comunero, nombre, dni, tlf) VALUES " +
                "('adm1234', 'Adán Alonso Medín', '44660126d', '647632891')"
        );

        db.execSQL("INSERT INTO usuarios (usuario, pass, email, id_comunero, admin) VALUES " +
                "('Admin', 'Admin123', 'aam@example.com', 'adm1234', 'si')"
        );

        db.execSQL("UPDATE parcelas SET consumo = 2065 WHERE num_parcela = 42");
        db.execSQL("UPDATE parcelas SET consumo = 934 WHERE num_parcela = 223");
        db.execSQL("UPDATE parcelas SET consumo = 1412 WHERE num_parcela = 40");
        db.execSQL("UPDATE parcelas SET consumo = 412 WHERE num_parcela = 73");
        db.execSQL("UPDATE parcelas SET consumo = 812 WHERE num_parcela = 4");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        //Comuneros
        db.execSQL("drop table if exists comuneros;");
        db.execSQL("CREATE TABLE comuneros (" +
                "id_comunero CHAR(7) PRIMARY KEY NOT NULL, " +
                "nombre VARCHAR(100) NOT NULL, " +
                "dni CHAR(9) NOT NULL, " +
                "tlf INTEGER NOT NULL, " +
                "cuota INTEGER NOT NULL DEFAULT 0, " +
                "CONSTRAINT chk_id_comunero CHECK (id_comunero REGEXP '^[a-z]{3}[0-9]{4}$'))"
        );

        //Usuarios
        db.execSQL("drop table if exists usuarios;");
        db.execSQL("CREATE TABLE usuarios (" +
                "usuario VARCHAR(20) PRIMARY KEY NOT NULL," +
                "pass VARCHAR(20) NOT NULL," +
                "email VARCHAR(255) NOT NULL," +
                "id_comunero CHAR(7) NOT NULL," +
                "admin TEXT NOT NULL DEFAULT 'no'," +
                "CONSTRAINT fk_usuario_id_comunero FOREIGN KEY (id_comunero) REFERENCES comuneros(id_comunero) ON DELETE CASCADE ON UPDATE CASCADE," +
                "CONSTRAINT chk_admin_usuario CHECK (admin IN ('si', 'no')))"
        );


        //Parcelas
        db.execSQL("drop table if exists parcelas;");
        db.execSQL("CREATE TABLE parcelas (" +
                "num_parcela INTEGER PRIMARY KEY NOT NULL," +
                "id_comunero CHAR(7) NOT NULL," +
                "nombre_parcela VARCHAR(30)," +
                "tipo TEXT NOT NULL DEFAULT 'regadio'," +
                "tamaño INTEGER NOT NULL DEFAULT 0, " +
                "consumo INTEGER NOT NULL DEFAULT 0, " +
                "CONSTRAINT fk_parcela_comunero FOREIGN KEY (id_comunero) REFERENCES comuneros(id_comunero) ON DELETE NO ACTION ON UPDATE CASCADE, " +
                "CONSTRAINT chk_tipo_parcela CHECK (tipo IN ('secano', 'regadio')))"
        );

        //Hidrantes
        db.execSQL("drop table if exists hidrantes;");
        db.execSQL("CREATE TABLE hidrantes (" +
                "num_hidrante INTEGER PRIMARY KEY AUTOINCREMENT," +
                "id_comunero CHAR(7) NOT NULL," +
                "num_parcela INTEGER NOT NULL," +
                "estado TEXT NOT NULL DEFAULT 'off'," +
                "CONSTRAINT fk_hidrante_comunero FOREIGN KEY (id_comunero) REFERENCES comuneros(id_comunero) ON DELETE NO ACTION ON UPDATE CASCADE," +
                "CONSTRAINT fk_hidrante_parcela FOREIGN KEY (num_parcela) REFERENCES parcelas(num_parcela) ON DELETE NO ACTION ON UPDATE CASCADE," +
                "CONSTRAINT chk_estado_hidrante CHECK (estado IN ('on', 'off')))"
        );
    }
}
