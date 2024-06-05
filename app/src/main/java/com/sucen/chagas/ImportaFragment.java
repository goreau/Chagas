package com.sucen.chagas;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import com.google.android.material.snackbar.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import webservice.Utils;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ImportaFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ImportaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ImportaFragment extends Fragment {
    RadioGroup rgImporta, rgNivel;
    RadioButton rbInseto, rbCadastro, rbMunicipio, rbColegiado, rbRegional;
    AutoCompleteTextView etLocal;
    Button btImporta;
    TextView tvConecta;

    String webUri;
    String resultado = "Recebidos:\n";
    List<String> municipio, id_municipio, colegiado, id_colegiado, regional, id_regional;

    static Context context;
    private ProgressDialog load;
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ImportaFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static ImportaFragment newInstance(String param1, String param2) {
        ImportaFragment fragment = new ImportaFragment();
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
        View v = inflater.inflate(R.layout.fragment_importa, container, false);
        setupComps(v);
        return v;
    }

    private void setupComps(View v) {
        this.context = PrincipalActivity.getChagasContext();
        tvConecta   = (TextView) v.findViewById(R.id.tvConecta);
        rgImporta   = (RadioGroup) v.findViewById(R.id.rgImporta);
        rgNivel     = (RadioGroup) v.findViewById(R.id.rgLocal);
        rbInseto    = (RadioButton) v.findViewById(R.id.rbInseto);
        rbCadastro  = (RadioButton) v.findViewById(R.id.rbCadastro);
        rbMunicipio = (RadioButton) v.findViewById(R.id.rbMunicipio);
        rbColegiado = (RadioButton) v.findViewById(R.id.rbColegiado);
        rbRegional  = (RadioButton) v.findViewById(R.id.rbRegional);
        etLocal     = (AutoCompleteTextView) v.findViewById(R.id.etLocal);
        btImporta   = (Button) v.findViewById(R.id.btImporta);

        if(isConnected()){
            //  tvIsConnected.setBackgroundColor(0xFF00CC00);
            Drawable img = this.context.getResources().getDrawable(R.drawable.verde);
            tvConecta.setText("Conectado");
            tvConecta.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
        }
        else{
            Drawable img = this.context.getResources().getDrawable(R.drawable.vermelho);
            tvConecta.setText("Não conectado");
            tvConecta.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
        }

        rgNivel.setOnCheckedChangeListener(onMudaNivel);

        btImporta.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                chamaProcessa(v);
            }
        });
        criaEntradas();
    }

    RadioGroup.OnCheckedChangeListener onMudaNivel=new RadioGroup.OnCheckedChangeListener(){
        @Override
        public void onCheckedChanged(RadioGroup rg, int chk) {
            if (chk == R.id.rbRegional){
                addItemsOnLocal(regional);
            } else if (chk == R.id.rbColegiado){
                addItemsOnLocal(colegiado);
            } else {
                addItemsOnLocal(municipio);
            }
        }
    };

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

    private boolean isConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }

    private void criaEntradas() {
        XmlPullParserFactory pullParserFactory;
        try {
            pullParserFactory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = pullParserFactory.newPullParser();
            //carrega os xml
            InputStream in_mun = getActivity().getApplicationContext().getAssets().open("municipio.xml");
            InputStream in_idmun = getActivity().getApplicationContext().getAssets().open("id_municipio.xml");
            InputStream in_col = getActivity().getApplicationContext().getAssets().open("colegiado.xml");
            InputStream in_idcol = getActivity().getApplicationContext().getAssets().open("id_colegiado.xml");
            InputStream in_reg = getActivity().getApplicationContext().getAssets().open("regional.xml");
            InputStream in_idreg = getActivity().getApplicationContext().getAssets().open("id_regional.xml");

            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);

            //popula os List
            parser.setInput(in_mun, null);
            municipio = parseXML(parser);

            parser.setInput(in_idmun, null);
            id_municipio = parseXML(parser);

            parser.setInput(in_col, null);
            colegiado = parseXML(parser);

            parser.setInput(in_idcol, null);
            id_colegiado = parseXML(parser);

            parser.setInput(in_reg, null);
            regional = parseXML(parser);

            parser.setInput(in_idreg, null);
            id_regional = parseXML(parser);

            addItemsOnLocal(municipio);

        } catch (XmlPullParserException e) {
            e.printStackTrace();
            //Log.e("ERRO","Erro Pull Parser");
        } catch (IOException e) {
            e.printStackTrace();
            //Log.e("ERRO","Erro IO");
        }
    }

    public void addItemsOnLocal(List<String> list) {
        ArrayAdapter<String> dados = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item,list);

        dados.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        etLocal.setAdapter(dados);
    }

    private List<String> parseXML(XmlPullParser parser) throws XmlPullParserException, IOException
    {
        String parc="";
        List<String> generico = new ArrayList<String>();

        int eventType = parser.getEventType();

        while (eventType != XmlPullParser.END_DOCUMENT){
            switch (eventType){
                case XmlPullParser.TEXT:
                    parc = parser.getText().trim();
                    if (!parc.equals("")){
                        generico.add(parc);
                    }
                    break;
            }
            eventType = parser.next();
        }
        return generico;
    }

    public void chamaProcessa(View v){
        int nivel = 0;
        String id = "1";
        int pos;
        final GetJson download = new GetJson();
        download.context = context;
        if (rgImporta.getCheckedRadioButtonId()==-1){
            Snackbar.make(getActivity().findViewById(android.R.id.content), "Informe o tipo de informação a importar!", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            return;
        }
        if (rgNivel.getCheckedRadioButtonId()==-1){
            Snackbar.make(getActivity().findViewById(android.R.id.content), "Informe o nível das informações a importar!", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            return;
        }
        if (etLocal.getText().toString().matches("")){
            Snackbar.make(getActivity().findViewById(android.R.id.content), "Informe o local a importar!", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            return;
        }
        switch (rgNivel.getCheckedRadioButtonId()) {
            case R.id.rbRegional:
                nivel = 0;
                pos = regional.indexOf(etLocal.getText().toString());
                id=id_regional.get(pos);
                break;
            case R.id.rbColegiado:
                nivel = 1;
                pos = colegiado.indexOf(etLocal.getText().toString());
                id=id_colegiado.get(pos);
                break;
            default:
                nivel = 2;
                pos = municipio.indexOf(etLocal.getText().toString());
                id=id_municipio.get(pos);
                break;
        }
        switch (rgImporta.getCheckedRadioButtonId()) {
            case R.id.rbInseto:
                webUri = "https://vigent.saude.sp.gov.br/wchagas/Importa.php?tipo=insetos&nivel=" + nivel + "&id=" + id;
                //webUri = "http://200.144.1.23/wchagas/Importa.php?tipo=insetos&nivel=" + nivel + "&id=" + id;
                break;
            default:
                webUri = "https://vigent.saude.sp.gov.br/wchagas/Importa.php?tipo=sistema&nivel=" + nivel + "&id=" + id;
                //webUri = "https://vigent.saude.sp.gov.br/wcapop/Importa.php?tipo=sistema";
                break;
        }
        download.execute();
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
//importação
    private class GetJson extends AsyncTask<Void, Void, String>{
        private Context context;
        @Override
        protected void onPreExecute(){
            load = ProgressDialog.show(getActivity(), "Por favor Aguarde ...", "Recuperando Informações do Servidor...");
        }

        @Override
        protected String doInBackground(Void... params) {
            Utils util = new Utils(context);
            return util.getInformacao(webUri);
        }

        @Override
        protected void onPostExecute(String result){
            mostraResultado(result);
            load.dismiss();
        }
    }

    private void mostraResultado(String resultado){
        Fragment rFragment = new RelImportaFragment();
        Bundle data = new Bundle();
        // Setting the index of the currently selected item of mDrawerList
        data.putString("resultado", resultado);

        // Setting the position to the fragment
        rFragment.setArguments(data);

        // Getting reference to the FragmentManager
        FragmentManager fragmentManager = getFragmentManager();

        // Creating a fragment transaction
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.addToBackStack(null);
        // Adding a fragment to the fragment transaction
        ft.replace(R.id.content_frame, rFragment);

        // Committing the transaction
        ft.commit();

    }


}
