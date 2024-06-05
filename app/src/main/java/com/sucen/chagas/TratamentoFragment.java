package com.sucen.chagas;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.List;

import entidades.Produto;
import entidades.Suspeito;
import entidades.Triatomineo;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TratamentoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TratamentoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TratamentoFragment extends Fragment {
    CheckBox ckIntra, ckPeri, ckNada;
    EditText etConsIntra, etConsPeri;
    Spinner spProduto;
    Button btSalva, btVolta;
    List<String> valProduto;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "triatomineo";
    private static final String ARG_PARAM2 = "id_inseto";

    // TODO: Rename and change types of parameters
    private Triatomineo triat;
    private int id_inseto;

    private OnFragmentInteractionListener mListener;

    public TratamentoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TratamentoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TratamentoFragment newInstance(String param1, String param2) {
        TratamentoFragment fragment = new TratamentoFragment();
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
            triat     = (Triatomineo) getArguments().getSerializable(ARG_PARAM1);
            id_inseto = getArguments().getInt(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tratamento, container, false);
        setupComps(v);
        return v;
    }

    private void setupComps(View v) {
        ckIntra     = (CheckBox) v.findViewById(R.id.ckIntra);
        ckPeri      = (CheckBox) v.findViewById(R.id.ckPeri);
        ckNada      = (CheckBox) v.findViewById(R.id.ckNada);
        etConsIntra = (EditText) v.findViewById(R.id.etConsumoIntra);
        etConsPeri  = (EditText) v.findViewById(R.id.etConsumoPeri);
        spProduto   = (Spinner) v.findViewById(R.id.spProduto);
        btSalva     = (Button) v.findViewById(R.id.btSalva);
        btVolta     = (Button) v.findViewById(R.id.btVoltaNot);

        btSalva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                salvaReg();
            }
        });

        btVolta.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Fragment rFragment = new NotificacaoFragment();
                Bundle data = new Bundle();
                // Setting the index of the currently selected item of mDrawerList
                data.putInt("position", 1);
                data.putLong("id_inseto", triat.getId_triatomineo());
                // Setting the position to the fragment
                rFragment.setArguments(data);
                FragmentManager fragmentManager = getFragmentManager();
                // Creating a fragment transaction
                FragmentTransaction ft = fragmentManager.beginTransaction();
                // Adding a fragment to the fragment transaction
                ft.replace(R.id.content_frame, rFragment);
                // Committing the transaction
                ft.commit();
            }
        });

        addItensOnProduto();
        if (id_inseto>0) Popula();
    }

    private void Popula() {
        ckIntra.setChecked(triat.getCasa_tratada()==1);
        etConsIntra.setText(String.valueOf(triat.getConsumo_casa()));
        ckPeri.setChecked(triat.getPeri_tratado()==1);
        etConsPeri.setText(String.valueOf(triat.getConsumo_peri()));
        String prod = String.valueOf(triat.getId_aux_inseticida());
        int pd = valProduto.indexOf(prod);
        spProduto.setSelection(pd);
        ckNada.setChecked(triat.getNao_tratado()==1);
    }

    private void salvaReg() {
        if (ckNada.isChecked()) {
            triat.setCasa_tratada(0);
            triat.setConsumo_casa(0);
            triat.setPeri_tratado(0);
            triat.setConsumo_peri(0);
            triat.setNao_tratado(1);
            triat.setId_aux_inseticida(0);
        } else {
            triat.setCasa_tratada(ckIntra.isChecked() ? 1 : 0);
            triat.setConsumo_casa(Float.valueOf(etConsIntra.getText().toString()));
            triat.setPeri_tratado(ckPeri.isChecked() ? 1 : 0);
            triat.setConsumo_peri(Float.valueOf(etConsPeri.getText().toString()));
            triat.setId_aux_inseticida(Integer.valueOf(valProduto.get(spProduto.getSelectedItemPosition())));
        }
    triat.manipula();
}

    private void addItensOnProduto() {
        Produto prod = new Produto();

        List<String> list = prod.combo();
        valProduto = prod.idProd;
        ArrayAdapter<String> dados = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item,list);

        dados.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spProduto.setAdapter(dados);
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
