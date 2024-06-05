package com.sucen.chagas;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonParseException;

import java.util.HashMap;
import java.util.Map;

import entidades.Triatomineo;
import util.MyToast;
import webservice.Utils;
import webservice.onServiceCallCompleted;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EnvioFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EnvioFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EnvioFragment extends Fragment {
    static Context context;
    MyToast toast;
    private ProgressDialog load;
    private TextView tvConecta, tvResumo;
    private Button btExpo;
    private int registros = 0;
    int rec = 0;
    String webUri;
    String resultado = "Enviados:\n";

    HashMap<String, String> map = new HashMap<String, String>();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public EnvioFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EnvioFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EnvioFragment newInstance(String param1, String param2) {
        EnvioFragment fragment = new EnvioFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_envio, container, false);
        setupComps(v);
        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void setupComps(View v) {
        context = getActivity();
        toast = new MyToast(context, Toast.LENGTH_SHORT);

        //Initialize Progress Dialog properties
        load = new ProgressDialog(context);
        load.setMessage("Sincronizando dados com servidor remoto. Aguarde...");
        load.setCancelable(false);

        tvConecta = (TextView) v.findViewById(R.id.tvConecta);
        tvResumo = (TextView) v.findViewById(R.id.tvResEnvio);
        btExpo = (Button) v.findViewById(R.id.btSincroniza);
        btExpo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sincro(v);
            }
        });

        montaDados();
        if(isConnected()){
            //  tvIsConnected.setBackgroundColor(0xFF00CC00);
            Drawable img = context.getResources().getDrawable(R.drawable.verde);
            tvConecta.setText("Conectado");
            tvConecta.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
        }
        else{
            Drawable img = context.getResources().getDrawable(R.drawable.vermelho);
            tvConecta.setText("Não conectado");
            tvConecta.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
        }
    }

    private boolean isConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }

    public void sincro(View v){
        final Triatomineo controller = new Triatomineo(0);
        controller.getAllTriatomineos();
        if(controller.dbSyncCount() != 0){
            load.show();
            String dados = controller.composeJSONfromSQLite();
            final postAsync download = new postAsync();
            download.mTipo  = "notificacao";
            download.mDados = dados;
            //  download.context = context;
            download.execute();


           /* load.show();
            String dados = controller.composeJSONfromSQLite();
            //System.out.println(dados);
            webUri = "http://200.144.1.23/wchagas/Exporta.php?tipo=notificacao&dados=" + dados;
            //System.out.println(webUri);
            final GetJson download = new GetJson();
            download.context = context;
            download.execute();*/
        }else{
            toast.show("Sincronização Ok!");
        }
        registros++;
    }


    private void montaDados(){
        int quant;
        String resumo = "";
        Triatomineo vcImovel = new Triatomineo(0);
        quant = vcImovel.dbSyncCount();
        if (quant>0){
            map.put("vc_imovel", vcImovel.composeJSONfromSQLite());
            resumo += "\n  Visitas: " + quant + " registros";
        }
        tvResumo.setText(tvResumo.getText().toString() + resumo);
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private class GetJson extends AsyncTask<Void, Void, String> {
        private Context context;

        @Override
        protected void onPreExecute() {
            load.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            Utils util = new Utils(context);

            return util.sendInformacao(webUri);
        }

        @Override
        protected void onPostExecute(String result) {
            tvResumo.setText("Sincronizados:\n" + result);
            //Log.d("Resultado", result);
            load.dismiss();
        }
    }

        public class postAsync extends AsyncTask<String, Boolean, Boolean> implements onServiceCallCompleted {
            private onServiceCallCompleted mListener = this;
            //  private static final String URL = "http://200.144.1.24/dados/exporta.php?tipo=";
            private String mTipo;
            private String mDados;

            @Override
            protected Boolean doInBackground(final String... params) {
                final String url = "https://vigent.saude.sp.gov.br/wchagas/Exporta.php?tipo=" + mTipo;

                // Instantiate the RequestQueue.
                com.android.volley.RequestQueue queue = Volley.newRequestQueue(context);

                // Request a string response from the provided URL.
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.d(mTipo, "Response from Service received");
                                // First validation
                                try {

                                    // JsonObject jsonObject = new JsonParser().parse(response).getAsJsonObject();
                                    // Call onServiceCallComplete
                                    //
                                    Utils util = new Utils(context);
                                    String retorno = util.parseRetorno(response);
                                    mListener.onServiceCallComplete(retorno);
                                } catch (JsonParseException e) {
                                    Log.e(mTipo, e.getMessage());
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // @TODO: Catch error and print out proper message.
                        Log.e(mTipo, "Something went wrong");
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> postParams = new HashMap<>();
                        postParams.put("dados", mDados);
                        return postParams;
                    }
                };
                // Add the request to the RequestQueue.
                queue.add(stringRequest);

                return true;
            }

            @Override
            public boolean onServiceCallComplete(String response) {
                // String responseString = response.toString();

                tvResumo.setText("Sincronizados:\n" + response);
                load.dismiss();

                return true;
            }
        }
}

