package webservice;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Console;

import entidades.Municipio;
import entidades.Produto;
import entidades.Suspeito;
import entidades.Triatomineo;


public class Utils {
    static Context contexto;

    public Utils(Context ctx) {
        contexto = ctx;
    }


    public String getInformacao(String end){
        String json;
        String retorno;
        json = NetworkUtils.getJSONFromAPI(end);
        try{
            retorno = parseJson(json);
        } catch (Exception ex) {
            retorno = "Erro importando informação.";
        }


        return retorno;
    }

    public String sendInformacao(String end){
        String json;
        String retorno;
        json = NetworkUtils.getJSONFromAPI(end);
        //Log.i("Resultado", json);

        retorno = parseRetorno(json);

        return retorno;
    }

    private String parseJson(String json){
        int quant = 0;
        int linhas = 0;
        int inseridos = 0;
        String tabela = "";
        String resultado = "";
        try {
            JSONObject jsonObj = new JSONObject(json);
            JSONObject dados = jsonObj.getJSONObject("dados");
            JSONArray tabelas = dados.names();
            quant = tabelas.length();
            //Log.w("tabelas",""+dados.names());
            for (int j=0;j<quant;j++){
                tabela = tabelas.getString(j); //nome da tabela
                JSONArray objetos = dados.getJSONArray(tabela);//array da tabela do banco (municipio, area,..)
                linhas = objetos.length(); //registros da tabela
                //Log.w("Registros",""+linhas);
                if (linhas==0){
                    resultado += "  -" + tabela + ": nenhum registro;";
                    continue;
                }
                JSONArray names = objetos.getJSONObject(0).names(); //nomes dos campos
                //Log.w("Campos",""+names);
                int fields = names.length(); //quantidade de campos
                String[] campos = new String[fields];
                String[] valores = new String[fields];

                for (int x=0;x<linhas;x++){
                    //Log.w("Linha:",""+x);
                    for (int i = 0; i<fields;i++){
                        campos[i] = names.getString(i);
                        valores[i]= objetos.getJSONObject(x).getString(names.getString(i));
                    }
                    if (tabela.equals("suspeito")){
                        if (valores.length==0){

                        } else {
                            Suspeito mun = new Suspeito(0);
                            if (mun.insere(campos, valores))
                                inseridos++;
                        }
                    } else if (tabela.equals("municipio")){
                        Municipio mun = new Municipio() ;
                        if (inseridos==0) mun.limpar();
                        if (mun.insere(campos, valores))
                            inseridos++;
                    } else if (tabela.equals("produto")){
                        Produto prod = new Produto() ;
                        if (inseridos==0) prod.limpar();
                        if (prod.insere(campos, valores))
                            inseridos++;
                    }
                }
                resultado += "  -" + tabela + ": " + inseridos;
                if (inseridos>1)
                    resultado += " registros\n";
                else
                    resultado += " registro\n";
                inseridos=0;
            }

        } catch (JSONException e) {
            e.printStackTrace();
            inseridos = -1;
            resultado = e.getMessage();
        }
        return resultado;
    }

    //recebimento da resposta do envio (atualização do status)
    public String parseRetorno(String json){
        int quant = 0;
        int linhas = 0;
        int inseridos = 0;
        String tabela = "";
        String resultado = "";
        try {
            JSONArray registros = new JSONArray(json);
            //Log.d("recebido: ",json);
            quant = registros.length();
            Triatomineo triat = new Triatomineo(0);
            for (int j=0;j<quant;j++) {
                JSONObject obj = registros.getJSONObject(j);//array da tabela do banco (municipio, area,..)
                // if (inseridos==0) mun.limpar();
                triat.atualizaStatus(obj.get("id").toString(), obj.get("status").toString());
                inseridos++;
            }
            resultado += "  -" + tabela + ": " + inseridos;
            if (inseridos>1)
                resultado += " registros\n";
            else
                resultado += " registro\n";
            inseridos=0;

        } catch (JSONException e) {
            e.printStackTrace();
            inseridos = -1;
            resultado = e.getMessage();
        }
        return resultado;
    }
}
