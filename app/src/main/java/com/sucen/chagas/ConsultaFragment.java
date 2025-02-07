package com.sucen.chagas;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import consulta.Children;
import consulta.ConsultaListAdapter;
import consulta.Grupo;
import util.gerenciaBanco;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ConsultaFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ConsultaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ConsultaFragment extends Fragment {
    gerenciaBanco db;
    Context context;
    SparseArray<Grupo> groups = new SparseArray<Grupo>();
    int j = 0;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ConsultaFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ConsultaFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ConsultaFragment newInstance(String param1, String param2) {
        ConsultaFragment fragment = new ConsultaFragment();
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
        View v = inflater.inflate(R.layout.fragment_consulta, container, false);
        context = PrincipalActivity.chagasContext;
        db = new gerenciaBanco(context);

        // Updating the action bar title
       // getActivity().getActionBar().setTitle("Consulta");
        createLinha();

        if (j==0){
            groups.append(j, new Grupo("Nenhum registro cadastrado."));
        }
        ExpandableListView listView = (ExpandableListView) v.findViewById(R.id.listView);
        ConsultaListAdapter adapter = new ConsultaListAdapter(getActivity(), groups);
        listView.setAdapter(adapter);
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

    public void createLinha() {
        String sit="", ativ="", sql="", strGrupo="", strLinha="", oldGrupo="";
        Cursor cursor;
        Grupo group = null;

        sql = "SELECT  m.nome as mun, v.localidade, v.numero_casa, v.situacao, v.status, v.id_triatomineo, v.id_aux_atividade " +
                "FROM triatomineo v join municipio m using(id_municipio)";
        cursor = db.getWritableDatabase().rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            do {
                strGrupo = cursor.getString(0);
                sit  = (cursor.getInt(3)==1 ? "Trab." : (cursor.getInt(3)==2 ? "Fech." : "Desab."));
                ativ = (cursor.getInt(6)==1 ? "Notif" : (cursor.getInt(3)==2 ? "Atend" : "Outra"));
                if (oldGrupo == ""){
                    oldGrupo = strGrupo;
                    strLinha = "- Atividade: " + ativ + "\n- Localidade: " + cursor.getString(1)+"\n- Casa: "+cursor.getString(2)+"\n- Situação: " + sit;
                    group = new Grupo(strGrupo);
                    group.setStatus(cursor.getInt(4));
                    group.children.add(new Children((long) cursor.getInt(5),strLinha));
                } else if (strGrupo.equals(oldGrupo)){
                    strLinha = "- Atividade: " + ativ + "\n- Localidade: " + cursor.getString(1)+"\n- Casa: "+cursor.getString(2)+"\n- Situação: " + sit;
                    group.children.add(new Children((long) cursor.getInt(5),strLinha));
                } else {
                    groups.append(j++, group);
                    //strGrupo = cursor.getString(0)+"-"+cursor.getString(3)+"-"+cursor.getString(1)+"- Quarteirao: "+cursor.getString(2);
                    oldGrupo = strGrupo;
                    strLinha = "- Atividade: " + ativ + "\n- Localidade: " + cursor.getString(1)+"\n- Casa: "+cursor.getString(2)+"\n- Situação: " + sit;
                    group = new Grupo(strGrupo);
                    group.setStatus(cursor.getInt(5));
                    group.children.add(new Children((long) cursor.getInt(5),strLinha));
                }
            } while (cursor.moveToNext());
            groups.append(j++, group);
        }
        db.close();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
