package entidades;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sucen.chagas.PrincipalActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import util.MyToast;
import util.gerenciaBanco;

/**
 * Created by Acer on 22/05/2016.
 */
public class Triatomineo implements Serializable {
    long id_triatomineo;
    int id_municipio,  situacao, id_aux_local_captura,  id_aux_atividade, id_usuario, casa_tratada, peri_tratado, id_aux_inseticida;
    int id_inseto_suspeito, nao_tratado, id_execucao, status;
    String localidade, numero_casa, dt_atendimento, latitude, longitude;
    float consumo_casa,  consumo_peri;
    MyToast toast;
    Context context;

    public Triatomineo(long id_triatomineo) {
        this.id_triatomineo = id_triatomineo;
        context = PrincipalActivity.getChagasContext();
        if (id_triatomineo>0){
            popula();
        }
    }

    public void popula(){
        gerenciaBanco db = new gerenciaBanco(this.context);
        String selectQuery = "SELECT id_municipio, localidade, numero_casa, situacao, id_aux_local_captura,  id_aux_atividade, id_usuario,"
            + " casa_tratada, peri_tratado, id_aux_inseticida, consumo_casa,  consumo_peri, dt_atendimento, id_inseto_suspeito, nao_tratado, latitude, longitude, status, id_execucao"
            + " FROM Triatomineo t where id_triatomineo=" + this.id_triatomineo;

        Cursor cursor = db.getWritableDatabase().rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            this.id_municipio 	        = cursor.getInt(0);
            this.localidade	            = cursor.getString(1);
            this.numero_casa 	        = cursor.getString(2);
            this.situacao 		        = cursor.getInt(3);
            this.id_aux_local_captura 	= cursor.getInt(4);
            this.id_aux_atividade 	    = cursor.getInt(5);
            this.id_usuario 	        = cursor.getInt(6);
            this.casa_tratada 	        = cursor.getInt(7);
            this.peri_tratado 		    = cursor.getInt(8);
            this.id_aux_inseticida   	= cursor.getInt(9);
            this.consumo_casa 		    = cursor.getFloat(10);
            this.consumo_peri        	= cursor.getFloat(11);
            this.dt_atendimento	        = cursor.getString(12);
            this.id_inseto_suspeito		= cursor.getInt(13);
            this.nao_tratado	        = cursor.getInt(14);
            this.latitude		        = cursor.getString(15);
            this.longitude		        = cursor.getString(16);
            this.status 		        = cursor.getInt(17);
            this.id_execucao         = cursor.getInt(18);
        }
        db.close();

    }

    public boolean manipula() {
        gerenciaBanco db = new gerenciaBanco(this.context);
        toast = new MyToast(this.context, Toast.LENGTH_SHORT);
        String msg = "";
        try {
            ContentValues valores = new ContentValues();
            valores.put("dt_atendimento", this.dt_atendimento);
            valores.put("localidade", this.localidade);
            valores.put("id_municipio", this.id_municipio);
            valores.put("numero_casa", this.numero_casa);
            valores.put("situacao", this.situacao);
            valores.put("id_aux_local_captura", this.id_aux_local_captura);
            valores.put("id_aux_atividade", this.id_aux_atividade);
            valores.put("id_usuario", this.id_usuario);
            valores.put("casa_tratada", this.casa_tratada);
            valores.put("peri_tratado", this.peri_tratado);
            valores.put("id_aux_inseticida", this.id_aux_inseticida);
            valores.put("consumo_casa", this.consumo_casa);
            valores.put("consumo_peri", this.consumo_peri);
            valores.put("id_inseto_suspeito", this.id_inseto_suspeito);
            valores.put("nao_tratado", this.nao_tratado);
            valores.put("latitude", this.latitude);
            valores.put("longitude", this.longitude);
            valores.put("id_execucao",this.id_execucao);
            valores.put("status", this.status);
            if (this.id_triatomineo > 0) {
                String[] args = { Long.toString(this.id_triatomineo) };
                db.getWritableDatabase().update("Triatomineo", valores, "id_triatomineo=?", args);
                msg="Registro atualizado";
            } else {
                this.id_triatomineo = db.getWritableDatabase().insert("Triatomineo", null,
                        valores);
                msg="Registro inserido";
            }
            return true;
        } catch (SQLException e) {
            msg = e.getMessage();
            return false;
        } finally {
            db.close();
            toast.show(msg);
        }
    }

    public boolean delete() {
        gerenciaBanco db = new gerenciaBanco(this.context);
        try {
            String[] args = { Long.toString(this.id_triatomineo) };

            db.getWritableDatabase().delete("Triatomineo", "id_triatomineo=?", args);
            return true;
        } catch (SQLException e) {
            toast = new MyToast(this.context, Toast.LENGTH_SHORT);
            toast.show(e.getMessage());
            return false;
        } finally {
            db.close();
        }
    }

    public ArrayList<HashMap<String, String>> getAllTriatomineos() {
        gerenciaBanco db = new gerenciaBanco(this.context);
        ArrayList<HashMap<String, String>> wordList;
        wordList = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT v.id_triatomineo as id, localidade as texto FROM Triatomineo v";

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

    /**
     * Compose JSON out of SQLite records
     *
     * @return
     */
    public String composeJSONfromSQLite() {
        gerenciaBanco db 	= new gerenciaBanco(context);

        ArrayList<HashMap<String, String>> wordList;
        wordList = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT id_municipio, localidade, numero_casa, situacao, id_aux_local_captura,  id_aux_atividade, id_usuario,"+
                " casa_tratada, peri_tratado, id_aux_inseticida, consumo_casa,  consumo_peri, dt_atendimento, id_inseto_suspeito, nao_tratado, " +
                "latitude, longitude, id_triatomineo,  status, id_execucao" +
                " FROM Triatomineo t WHERE status = 0";
        Cursor cursor = db.getWritableDatabase().rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> valores = new HashMap<String, String>();
                valores.put("id_municipio", cursor.getString(0));
                String locBco = cursor.getString(1).trim();
                String loc = locBco.substring(locBco.lastIndexOf("(") + 1).replace(")","");
                valores.put("localidade", loc.replace(" ","_"));

                valores.put("numero_casa", cursor.getString(2));
                valores.put("situacao", cursor.getString(3));
                valores.put("id_aux_local_captura",cursor.getString(4));
                valores.put("id_aux_atividade", cursor.getString(5));
                valores.put("id_usuario", cursor.getString(6));
                valores.put("casa_tratada", cursor.getString(7));
                valores.put("peri_tratado", cursor.getString(8));
                valores.put("id_aux_inseticida", cursor.getString(9));
                valores.put("consumo_casa", cursor.getString(10));
                valores.put("consumo_peri", cursor.getString(11));
                valores.put("dt_atendimento", cursor.getString(12));
                valores.put("id_inseto_suspeito", cursor.getString(13));
                valores.put("nao_tratado", cursor.getString(14));
                valores.put("latitude", cursor.getString(15));
                valores.put("longitude", cursor.getString(16));
                valores.put("id_triatomineo", cursor.getString(17));
                valores.put("status", cursor.getString(18));
                valores.put("id_execucao", cursor.getString(19));
                wordList.add(valores);
            } while (cursor.moveToNext());
        }
        db.close();
        Gson gson = new GsonBuilder().create();
        // Use GSON to serialize Array List to JSON
        //System.out.println(wordList);
        return gson.toJson(wordList);
    }

    public int dbSyncCount(){
        gerenciaBanco db = new gerenciaBanco(context);
        int count = 0;
        String selectQuery = "SELECT  * FROM Triatomineo where status = 0";
        Cursor cursor = db.getWritableDatabase().rawQuery(selectQuery, null);
        count = cursor.getCount();
        db.close();
        return count;
    }

    public int dbCount(){
        gerenciaBanco db = new gerenciaBanco(context);
        int count = 0;
        String selectQuery = "SELECT  * FROM Triatomineo";
        Cursor cursor = db.getWritableDatabase().rawQuery(selectQuery, null);
        count = cursor.getCount();
        db.close();
        return count;
    }
    /**
     * Update sync status against each User ID
     * @param id
     * @param status
     */
    public void atualizaStatus(String id, String status){
        gerenciaBanco db = new gerenciaBanco(context);
        String updateQuery = "Update Triatomineo set status = '"+ status +"' where id_triatomineo="+"'"+ id +"'";
        //Log.d("query",updateQuery);
        db.getWritableDatabase().execSQL(updateQuery);

        db.close();
    }

    public static int Limpar(Context context, String filt){
        gerenciaBanco db = new gerenciaBanco(context);
        int id = 0;
        int regs=0;
        String sql = "SELECT id_inseto_suspeito as id FROM Triatomineo "+filt;
        Cursor cursor = db.getWritableDatabase().rawQuery(sql, null);
        regs = cursor.getCount();
        if (cursor.moveToFirst()) {
            do {
                id = cursor.getInt(0);
                sql = "DELETE FROM Suspeito where id_inseto_suspeito="+"'"+ id +"'";
                //Log.d("query",sql);
                db.getWritableDatabase().execSQL(sql);
            } while (cursor.moveToNext());
        }


        sql = "DELETE FROM Triatomineo "+filt;
        //Log.d("query",sql);
        db.getWritableDatabase().execSQL(sql);
        db.close();
        return regs;
    }

    /*public List<RelatorioList> getList(){
        gerenciaBanco db = new gerenciaBanco(this.context);

        String selectQuery = "SELECT a.nome, count(v._id) FROM vc_folha v join atividade a using(id_atividade) group by a.nome";

        Cursor cursor = db.getReadableDatabase().rawQuery(selectQuery, null);
        List<RelatorioList> lista = new ArrayList<RelatorioList>();

        if (cursor.moveToFirst()) {
            do {
                RelatorioList list = new RelatorioList(cursor.getString(0),cursor.getString(1));
                lista.add(list);
            } while (cursor.moveToNext());
        }
        return lista;
    }*/

    public long getId_triatomineo() {
        return id_triatomineo;
    }

    public void setId_triatomineo(long id_triatomineo) {
        this.id_triatomineo = id_triatomineo;
    }

    public int getId_municipio() {
        return id_municipio;
    }

    public void setId_municipio(int id_municipio) {
        this.id_municipio = id_municipio;
    }

    public int getSituacao() {
        return situacao;
    }

    public void setSituacao(int situacao) {
        this.situacao = situacao;
    }

    public int getId_aux_local_captura() {
        return id_aux_local_captura;
    }

    public void setId_aux_local_captura(int id_aux_local_captura) {
        this.id_aux_local_captura = id_aux_local_captura;
    }

    public int getId_aux_atividade() {
        return id_aux_atividade;
    }

    public void setId_aux_atividade(int id_aux_atividade) {
        this.id_aux_atividade = id_aux_atividade;
    }

    public int getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(int id_usuario) {
        this.id_usuario = id_usuario;
    }

    public int getCasa_tratada() {
        return casa_tratada;
    }

    public void setCasa_tratada(int casa_tratada) {
        this.casa_tratada = casa_tratada;
    }

    public int getPeri_tratado() {
        return peri_tratado;
    }

    public void setPeri_tratado(int peri_tratado) {
        this.peri_tratado = peri_tratado;
    }

    public int getId_aux_inseticida() {
        return id_aux_inseticida;
    }

    public void setId_aux_inseticida(int id_aux_inseticida) {
        this.id_aux_inseticida = id_aux_inseticida;
    }

    public int getId_inseto_suspeito() {
        return id_inseto_suspeito;
    }

    public void setId_inseto_suspeito(int id_inseto_suspeito) {
        this.id_inseto_suspeito = id_inseto_suspeito;
    }

    public int getNao_tratado() {
        return nao_tratado;
    }

    public void setNao_tratado(int nao_tratado) {
        this.nao_tratado = nao_tratado;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getLocalidade() {
        return localidade;
    }

    public void setLocalidade(String localidade) {
        this.localidade = localidade;
    }

    public String getNumero_casa() {
        return numero_casa;
    }

    public void setNumero_casa(String numero_casa) {
        this.numero_casa = numero_casa;
    }

    public String getDt_atendimento() {
        return dt_atendimento;
    }

    public void setDt_atendimento(String dt_atendimento) {
        this.dt_atendimento = dt_atendimento;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public float getConsumo_casa() {
        return consumo_casa;
    }

    public void setConsumo_casa(float consumo_casa) {
        this.consumo_casa = consumo_casa;
    }

    public float getConsumo_peri() {
        return consumo_peri;
    }

    public void setConsumo_peri(float consumo_peri) {
        this.consumo_peri = consumo_peri;
    }

    public int getId_execucao() {
        return id_execucao;
    }

    public void setId_execucao(int id_execucao) {
        this.id_execucao = id_execucao;
    }
}
