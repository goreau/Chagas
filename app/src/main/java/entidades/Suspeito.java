package entidades;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.widget.Toast;
import com.sucen.chagas.PrincipalActivity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import util.MyToast;
import util.gerenciaBanco;

public class Suspeito {
    long id_suspeito;
    int id_inseto_suspeito, id_municipio, qt_insetos, id_usuario;
    String dt_captura, localidade;
    public List<String> idMun, idLoc;
    MyToast toast;
    Context context;

    public Suspeito(int id_suspeito) {
        this.id_suspeito = id_suspeito;
        context = PrincipalActivity.getChagasContext();
        if (id_suspeito>0){
            popula();
        }
    }

    public void popula(){
        gerenciaBanco db = new gerenciaBanco(this.context);
        String selectQuery = "SELECT id_inseto_suspeito, id_municipio, dt_captura, qt_insetos, localidade, id_usuario"
                + " FROM Suspeito t where id_suspeito=" + this.id_suspeito;

        Cursor cursor = db.getWritableDatabase().rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            this.id_inseto_suspeito     = cursor.getInt(0);
            this.id_municipio 	        = cursor.getInt(1);
            this.dt_captura 	        = cursor.getString(2);
            this.qt_insetos 	        = cursor.getInt(3);
            this.localidade	            = cursor.getString(4);
            this.id_usuario 	        = cursor.getInt(5);
        }
        db.close();

    }

    public boolean manipula() {
        gerenciaBanco db = new gerenciaBanco(this.context);
        try {
            ContentValues valores = new ContentValues();
            valores.put("id_inseto_suspeito", this.id_inseto_suspeito);
            valores.put("id_municipio", this.id_municipio);
            valores.put("dt_captura", this.dt_captura);
            valores.put("qt_insetos", this.qt_insetos);
            valores.put("localidade", this.localidade);
            valores.put("id_usuario", this.id_usuario);
            if (this.id_suspeito > 0) {
                String[] args = { Long.toString(this.id_suspeito) };
                db.getWritableDatabase().update("Suspeito", valores, "id_suspeito=?", args);
            } else {
                this.id_suspeito = db.getWritableDatabase().insert("Suspeito", null,
                        valores);
            }
            return true;
        } catch (SQLException e) {
            toast = new MyToast(this.context, Toast.LENGTH_SHORT);
            toast.show(e.getMessage());
            return false;
        } finally {
            db.close();
        }
    }

    public boolean insere(String[] campos, String[] val){
        gerenciaBanco db = new gerenciaBanco(this.context);

        try{
            ContentValues valores = new ContentValues();
            for (int i = 0; i<val.length;i++){
                valores.put(campos[i],val[i]);
            }
            db.getWritableDatabase().insert("suspeito", null, valores);
            return true;
        } catch (SQLException e) {
            toast.show(e.getMessage());
            return false;
        } finally {
            db.close();
        }

    }

    public boolean delete() {
        gerenciaBanco db = new gerenciaBanco(this.context);
        try {
            String[] args = { Long.toString(this.id_suspeito) };

            db.getWritableDatabase().delete("Suspeito", "id_suspeito=?", args);
            return true;
        } catch (SQLException e) {
            toast = new MyToast(this.context, Toast.LENGTH_SHORT);
            toast.show(e.getMessage());
            return false;
        } finally {
            db.close();
        }
    }

    public ArrayList<HashMap<String, String>> getAllSuspeitos() {
        gerenciaBanco db = new gerenciaBanco(this.context);
        ArrayList<HashMap<String, String>> wordList;
        wordList = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT v.id_suspeito as id, localidade as texto FROM Suspeito v";

        Cursor cursor = db.getWritableDatabase().rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("id", cursor.getString(0));
                map.put("texto", cursor.getString(1));
                wordList.add(map);
            } while (cursor.moveToNext());
        }
        db.close();
        return wordList;
    }

    public List<String> comboMunicipio(){
        List<String> mun = new ArrayList<String>();
        idMun = new ArrayList<String>();
        gerenciaBanco db = new gerenciaBanco(this.context);
        String sql = "SELECT distinct v.id_municipio as id, m.nome as text FROM Suspeito v join municipio m on m.id_municipio=v.id_municipio";
        Cursor cursor = db.getReadableDatabase().rawQuery(sql, null);

        if(cursor.moveToFirst()){
            do {
                //Log.i("Munic: ",cursor.getString(1));
                mun.add(cursor.getString(1));
                idMun.add(cursor.getString(0));
            } while (cursor.moveToNext());
        } else {
           // Log.i("Munic: ","erro");
        }
        cursor.close();
        db.close();
        return mun;
    }

    public List<String> comboLocalidade(String idmun){
        List<String> mun = new ArrayList<String>();
        idLoc = new ArrayList<String>();
        gerenciaBanco db = new gerenciaBanco(this.context);
        String sql = "SELECT v.id_suspeito as id, ('Nº Cap: ' || v.id_inseto_suspeito || ' - (' || trim(localidade) || ')') as texto FROM suspeito v WHERE id_municipio="+idmun;
        Cursor cursor = db.getReadableDatabase().rawQuery(sql, null);

        if(cursor.moveToFirst()){
            do {
                mun.add(cursor.getString(1));
                idLoc.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return mun;
    }

    public int dbCount(){
        gerenciaBanco db = new gerenciaBanco(context);
        int count = 0;
        String selectQuery = "SELECT  * FROM Suspeito";
        Cursor cursor = db.getWritableDatabase().rawQuery(selectQuery, null);
        count = cursor.getCount();
        db.close();
        return count;
    }

    public void limpar(){
        gerenciaBanco db = new gerenciaBanco(this.context);
        try{
            db.getWritableDatabase().delete("suspeito", null, null);
        } catch (SQLException e) {
            toast = new MyToast(this.context, Toast.LENGTH_SHORT);
            toast.show(e.getMessage());
        }
    }

    public long getId_suspeito() {
        return id_suspeito;
    }

    public void setId_suspeito(long id_suspeito) {
        this.id_suspeito = id_suspeito;
    }

    public int getId_inseto_suspeito() {
        return id_inseto_suspeito;
    }

    public void setId_inseto_suspeito(int id_inseto_suspeito) {
        this.id_inseto_suspeito = id_inseto_suspeito;
    }

    public int getId_municipio() {
        return id_municipio;
    }

    public void setId_municipio(int id_municipio) {
        this.id_municipio = id_municipio;
    }

    public int getQt_insetos() {
        return qt_insetos;
    }

    public void setQt_insetos(int qt_insetos) {
        this.qt_insetos = qt_insetos;
    }

    public int getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(int id_usuario) {
        this.id_usuario = id_usuario;
    }

    public String getDt_captura() {
        return dt_captura;
    }

    public void setDt_captura(String dt_captura) {
        this.dt_captura = dt_captura;
    }

    public String getLocalidade() {
        return localidade;
    }

    public void setLocalidade(String localidade) {
        this.localidade = localidade;
    }
}
