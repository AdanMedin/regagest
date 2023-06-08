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

// Inserts por defecto tabla parcelas
        db.execSQL("INSERT INTO parcelas (num_parcela, id_comunero, nombre_parcela, tipo, tamaño) VALUES " +
                "(42, 'jpp1234', 'parcela a', 'secano', 18325), " +
                "(223, 'jpp1234', 'parcela b', 'regadio', 8999), " +
                "(68, 'agl5678', 'parcela c', 'secano', 21042), " +
                "(40, 'agl5678', 'parcela d', 'regadio', 11027), " +
                "(52, 'prs9012', 'parcela e', 'secano', 5732), " +
                "(73, 'prs9012', 'parcela f', 'secano', 7732), " +
                "(4, 'agl5678', 'parcela g', 'regadio', 21042), " +
                "(99, 'jpp1234', 'parcela h', 'secano', 12000), " +
                "(31, 'agl5678', 'parcela j', 'secano', 8000), " +
                "(17, 'agl5678', 'parcela k', 'regadio', 20000), " +
                "(83, 'prs9012', 'parcela l', 'secano', 5000), " +
                "(44, 'prs9012', 'parcela m', 'secano', 9000), " +
                "(61, 'agl5678', 'parcela n', 'regadio', 18000), " +
                "(29, 'jpp1234', 'parcela o', 'secano', 10000), " +
                "(13, 'jpp1234', 'parcela p', 'regadio', 12000), " +
                "(87, 'agl5678', 'parcela q', 'secano', 6000), " +
                "(55, 'agl5678', 'parcela r', 'regadio', 15000), " +
                "(64, 'prs9012', 'parcela s', 'secano', 3000), " +
                "(39, 'prs9012', 'parcela t', 'secano', 7000), " +
                "(28, 'agl5678', 'parcela u', 'regadio', 14000), " +
                "(58, 'jpp1234', 'parcela w', 'regadio', 13000), " +
                "(92, 'agl5678', 'parcela x', 'secano', 9000), " +
                "(41, 'agl5678', 'parcela y', 'regadio', 17000), " +
                "(67, 'prs9012', 'parcela z', 'secano', 4000), " +
                "(78, 'prs9012', 'parcela aa', 'secano', 8000), " +
                "(10, 'agl5678', 'parcela bb', 'regadio', 16000), " +
                "(30, 'jpp1234', 'parcela cc', 'secano', 13000), " +
                "(23, 'jpp1234', 'parcela dd', 'regadio', 11000), " +
                "(81, 'agl5678', 'parcela ee', 'secano', 7000), " +
                "(50, 'agl5678', 'parcela ff', 'regadio', 14000), " +
                "(69, 'prs9012', 'parcela gg', 'secano', 5000), " +
                "(35, 'prs9012', 'parcela hh', 'secano', 9000), " +
                "(54, 'agl5678', 'parcela ii', 'regadio', 12000), " +
                "(75, 'jpp1234', 'parcela jj', 'secano', 10000), " +
                "(76, 'jpp1234', 'parcela kk', 'regadio', 18000)"
        );


        //Inserts por defecto tabla hidrantes
        db.execSQL("INSERT INTO hidrantes (id_comunero, num_parcela, estado) VALUES " +
                "('jpp1234', 42, 'off'), " +
                "('jpp1234', 223, 'off'), " +
                "('agl5678', 68, 'off'), " +
                "('agl5678', 40, 'off'), " +
                "('prs9012', 52, 'off'), " +
                "('prs9012', 73, 'off'), " +
                "('agl5678', 4, 'off'), " +
                "('jpp1234', 99, 'off'), " +
                "('agl5678', 31, 'off'), " +
                "('agl5678', 17, 'off'), " +
                "('prs9012', 83, 'off'), " +
                "('prs9012', 44, 'off'), " +
                "('agl5678', 61, 'off'), " +
                "('jpp1234', 29, 'off'), " +
                "('jpp1234', 13, 'off'), " +
                "('agl5678', 87, 'off'), " +
                "('agl5678', 55, 'off'), " +
                "('prs9012', 64, 'off'), " +
                "('prs9012', 39, 'off'), " +
                "('agl5678', 28, 'off'), " +
                "('jpp1234', 76, 'off'), " +
                "('jpp1234', 58, 'off'), " +
                "('agl5678', 92, 'off'), " +
                "('agl5678', 41, 'off'), " +
                "('prs9012', 67, 'off'), " +
                "('prs9012', 78, 'off'), " +
                "('agl5678', 10, 'off'), " +
                "('jpp1234', 30, 'off'), " +
                "('jpp1234', 23, 'off'), " +
                "('agl5678', 81, 'off'), " +
                "('agl5678', 50, 'off'), " +
                "('prs9012', 69, 'off'), " +
                "('prs9012', 35, 'off'), " +
                "('agl5678', 54, 'off'), " +
                "('jpp1234', 75, 'off')"
        );

        //Inserts por defecto admin
        db.execSQL("INSERT INTO comuneros (id_comunero, nombre, dni, tlf) VALUES " +
                "('adm1234', 'Adán Alonso Medín', '44660126d', '647632891')"
        );

        db.execSQL("INSERT INTO usuarios (usuario, pass, email, id_comunero, admin) VALUES " +
                "('Admin', 'Admin123', 'aam@example.com', 'adm1234', 'si')"
        );

        db.execSQL("UPDATE parcelas SET consumo = 605 WHERE num_parcela = 42");
        db.execSQL("UPDATE parcelas SET consumo = 934 WHERE num_parcela = 223");
        db.execSQL("UPDATE parcelas SET consumo = 412 WHERE num_parcela = 40");
        db.execSQL("UPDATE parcelas SET consumo = 412 WHERE num_parcela = 73");
        db.execSQL("UPDATE parcelas SET consumo = 812 WHERE num_parcela = 4");
        db.execSQL("UPDATE parcelas SET consumo = 200 WHERE num_parcela = 99");
        db.execSQL("UPDATE parcelas SET consumo = 700 WHERE num_parcela = 76");
        db.execSQL("UPDATE parcelas SET consumo = 100 WHERE num_parcela = 31");
        db.execSQL("UPDATE parcelas SET consumo = 200 WHERE num_parcela = 17");
        db.execSQL("UPDATE parcelas SET consumo = 900 WHERE num_parcela = 83");
        db.execSQL("UPDATE parcelas SET consumo = 600 WHERE num_parcela = 44");
        db.execSQL("UPDATE parcelas SET consumo = 800 WHERE num_parcela = 61");
        db.execSQL("UPDATE parcelas SET consumo = 500 WHERE num_parcela = 29");
        db.execSQL("UPDATE parcelas SET consumo = 450 WHERE num_parcela = 13");
        db.execSQL("UPDATE parcelas SET consumo = 500 WHERE num_parcela = 87");
        db.execSQL("UPDATE parcelas SET consumo = 630 WHERE num_parcela = 55");
        db.execSQL("UPDATE parcelas SET consumo = 800 WHERE num_parcela = 64");
        db.execSQL("UPDATE parcelas SET consumo = 970 WHERE num_parcela = 39");
        db.execSQL("UPDATE parcelas SET consumo = 360 WHERE num_parcela = 28");
        db.execSQL("UPDATE parcelas SET consumo = 420 WHERE num_parcela = 58");
        db.execSQL("UPDATE parcelas SET consumo = 900 WHERE num_parcela = 92");
        db.execSQL("UPDATE parcelas SET consumo = 480 WHERE num_parcela = 41");
        db.execSQL("UPDATE parcelas SET consumo = 320 WHERE num_parcela = 67");
        db.execSQL("UPDATE parcelas SET consumo = 700 WHERE num_parcela = 78");
        db.execSQL("UPDATE parcelas SET consumo = 340 WHERE num_parcela = 10");
        db.execSQL("UPDATE parcelas SET consumo = 370 WHERE num_parcela = 30");
        db.execSQL("UPDATE parcelas SET consumo = 510 WHERE num_parcela = 23");
        db.execSQL("UPDATE parcelas SET consumo = 900 WHERE num_parcela = 81");
        db.execSQL("UPDATE parcelas SET consumo = 720 WHERE num_parcela = 50");
        db.execSQL("UPDATE parcelas SET consumo = 800 WHERE num_parcela = 69");
        db.execSQL("UPDATE parcelas SET consumo = 750 WHERE num_parcela = 35");
        db.execSQL("UPDATE parcelas SET consumo = 300 WHERE num_parcela = 54");
        db.execSQL("UPDATE parcelas SET consumo = 400 WHERE num_parcela = 75");
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
