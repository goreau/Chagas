package entidades;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.widget.Toast;

import com.sucen.chagas.PrincipalActivity;

import java.util.ArrayList;
import java.util.List;

import util.MyToast;
import util.gerenciaBanco;

/**
 * Created by Acer on 23/05/2016.
 */
public class Produto {
    private int id_produto;
    private String codigo;
    private String nome;
    private int tipo_uso;
    public List<String> idProd;
    private Context context;
    MyToast toast;


    public Produto() {
        this.context = PrincipalActivity.getChagasContext();
      //  toast = new MyToast(context, Toast.LENGTH_SHORT);
    }

    public int getId_produto() {
        return id_produto;
    }

    public void setId_produto(int id_produto) {
        this.id_produto = id_produto;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public List<String> getId_prod() {
        return idProd;
    }

    public void setId_prod(List<String> id_prod) {
        this.idProd = id_prod;
    }

    public void limpar(){
        gerenciaBanco db = new gerenciaBanco(this.context);
        try{
            db.getWritableDatabase().delete("produto", null, null);
        } catch (SQLException e) {
            toast = new MyToast(this.context, Toast.LENGTH_SHORT);
            toast.show(e.getMessage());
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
            db.getWritableDatabase().insert("produto", null, valores);
            return true;
        } catch (SQLException e) {
            toast = new MyToast(this.context, Toast.LENGTH_SHORT);
            toast.show(e.getMessage());
            return false;
        } finally {
            db.close();
        }

    }

    public List<String> combo(){
        List<String> prod = new ArrayList<String>();
        idProd = new ArrayList<String>();
        gerenciaBanco db = new gerenciaBanco(this.context);
        String sql = "SELECT id_produto, trim(codigo)|| ' - ' || trim(nome) as produto from produto";

        Cursor cursor = db.getReadableDatabase().rawQuery(sql, new String[]{});
        if(cursor.moveToFirst()){
            do {
                prod.add(cursor.getString(1));
                idProd.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return prod;
    }

}
