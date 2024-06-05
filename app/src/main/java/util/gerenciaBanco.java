package util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.HashMap;


public class gerenciaBanco extends SQLiteOpenHelper {
    HashMap<String,ContentValues[]> registros = new HashMap<String,ContentValues[]>();
    private static final String NOME_BANCO = "chagas.db";
    private static final int VERSAO_SCHEMA = 8;
    private static final String[] sql = {
            "CREATE TABLE Municipio(id_municipio INTEGER, nome TEXT, codigo TEXT)",
            "CREATE TABLE Produto(id_produto INTEGER, codigo TEXT, nome TEXT)",
            "CREATE TABLE Triatomineo(id_triatomineo INTEGER PRIMARY KEY AUTOINCREMENT, id_municipio INTEGER, localidade TEXT, numero_casa TEXT, situacao INTEGER, id_execucao INTEGER, "
                    + "id_aux_local_captura INTEGER,  id_aux_atividade INTEGER, id_usuario INTEGER, casa_tratada INTEGER, peri_tratado INTEGER, id_aux_inseticida INTEGER, "
                    + " consumo_casa REAL,  consumo_peri REAL, dt_atendimento TEXT, id_inseto_suspeito INTEGER, nao_tratado INTEGER, latitude TEXT, longitude TEXT, status INTEGER)",
            "CREATE TABLE Suspeito(id_suspeito INTEGER PRIMARY KEY AUTOINCREMENT, id_inseto_suspeito INTEGER, id_municipio INTEGER, dt_captura TEXT, qt_insetos INTEGER, localidade TEXT, id_usuario INTEGER)"

    };
    private static final String[] tabelas = {"Municipio","Produto","Triatomineo", "Suspeito"};

    public gerenciaBanco(Context context) {
        super(context, NOME_BANCO, null, VERSAO_SCHEMA);
        getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        for (int i = 0; i < sql.length; i++){
            db.execSQL(sql[i]);
          //  Log.w(gerenciaBanco.class.getName(), "Tabela " + tabelas[i] + " criada...");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        persiste(db);
        for (int i = 0; i < tabelas.length; i++){
            db.execSQL("DROP TABLE IF EXISTS " + tabelas[i]);
        }
        onCreate(db);
        recupera(db);
    }

    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }

    public void persiste(SQLiteDatabase db){
        //fornecer valor padrão para o campo alterado
        String[] sqlPersiste = {
                "SELECT id_municipio, nome, codigo FROM Municipio",
                "SELECT id_produto, codigo, nome FROM Produto",
                "SELECT id_triatomineo, id_municipio, localidade, numero_casa, situacao, id_execucao, id_aux_local_captura, id_aux_atividade, " +
                        "id_usuario, casa_tratada, peri_tratado, id_aux_inseticida, consumo_casa,  consumo_peri, dt_atendimento, id_inseto_suspeito, nao_tratado, latitude, longitude, status FROM Triatomineo",
                "SELECT id_suspeito, id_inseto_suspeito, id_municipio, dt_captura, qt_insetos, localidade, id_usuario FROM Suspeito"
        };

        for (int i = 0; i < sqlPersiste.length; i++) {
            int x = 0;
            Cursor cursor = db.rawQuery(sqlPersiste[i], null);
            ContentValues[] total = new ContentValues[cursor.getCount()];
            if (cursor.moveToFirst()) {
                do {
                    ContentValues map = new ContentValues();
                    for (int j = 0; j < cursor.getColumnCount(); j++) {
                        map.put(cursor.getColumnName(j), cursor.getString(j));
                    }
                    total[x++] = map;
                } while (cursor.moveToNext());
                registros.put(tabelas[i], total);
            }
        }
    }



    public void recupera(SQLiteDatabase db){
        ContentValues[] dados;
        for (int x = 0; x < tabelas.length; x++) {
            dados = registros.get(tabelas[x]);
            try {
                for (int i = 0; i < dados.length; i++) {
                    ContentValues valores = dados[i];
                    db.insert(tabelas[x], null, valores);
                }
            } catch (SQLException e) {
                // Log.i("Exception",tabelas[x]);
                continue;
            } catch (NullPointerException nex){
                //  Log.i("Null",tabelas[x]);
                continue;
            }
        }
    }

}
