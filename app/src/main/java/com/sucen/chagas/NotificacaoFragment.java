package com.sucen.chagas;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.Settings;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.app.ActivityCompat;
import android.text.InputType;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import entidades.Suspeito;
import entidades.Triatomineo;

public class NotificacaoFragment extends Fragment {
    private static final int id = 0;
    private Long id_inseto;
    private static final int POSITION = 0;
    private DatePickerDialog dpData;
    private SimpleDateFormat dateFormatter;
    EditText etData, etCasa, etLat, etLong;
    Spinner spMunicipio, spLocalidade;
    List<String> valMunicipio, valLocalidade;
    RadioButton rbNotificacao, rbAtendimento, rbOutras;
    RadioButton rbTrabalhado, rbFechado, rbDesabitado;
    RadioButton rbIntra, rbPeri, rbAmbos;
    RadioButton rbSucen, rbMunicipio;
    Button btSalva, btTratamento;
    RadioGroup rgAtiv, rgExec, rgSit, rgLocal;
    int MY_PERMISSIONS_REQUEST_GPS;

    private int position;

    private OnFragmentInteractionListener mListener;

    public NotificacaoFragment() {
        // Required empty public constructor
    }

    public static NotificacaoFragment newInstance(int param1) {
        NotificacaoFragment fragment = new NotificacaoFragment();
        Bundle args = new Bundle();
        args.putInt("position", param1);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            position = getArguments().getInt("position");
            id_inseto = getArguments().getLong("id_inseto");
        }
    }

    private void popula() {
        Triatomineo triat = new Triatomineo(id_inseto);
        etData.setText(triat.getDt_atendimento());
        int aux = triat.getId_aux_atividade();
        switch (aux){
            case 1:
                rgAtiv.check(rbNotificacao.getId());
                break;
            case 2:
                rgAtiv.check(rbAtendimento.getId());
                break;
            default:
                rgAtiv.check(rbOutras.getId());
        }
        aux = valMunicipio.indexOf(triat.getId_municipio());
        spMunicipio.setSelection(aux);
        addItensOnLocalidade(aux+"");
        spLocalidade.setSelection(valLocalidade.indexOf(triat.getLocalidade()));
        etCasa.setText(triat.getNumero_casa());
        aux = triat.getSituacao();
        switch (aux){
            case 1:
                rgSit.check(rbTrabalhado.getId());
                break;
            case 2:
                rgSit.check(rbFechado.getId());
                break;
            default:
                rgSit.check(rbDesabitado.getId());
        }
        aux = triat.getId_execucao();
        switch (aux){
            case 1:
                rgExec.check(rbSucen.getId());
                break;
            case 2:
                rgExec.check(rbMunicipio.getId());
                break;
        }
        aux = triat.getId_aux_local_captura();
        switch (aux){
            case 1:
                rgLocal.check(rbIntra.getId());
                break;
            case 2:
                rgLocal.check(rbPeri.getId());
                break;
            default:
                rgLocal.check(rbAmbos.getId());
        }
        etLat.setText(triat.getLatitude());
        etLong.setText(triat.getLongitude());
    }

    private void setupComps(View v) {
        etData = (EditText) v.findViewById(R.id.etData);
        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        etData.setInputType(InputType.TYPE_NULL);
        Date now = new Date();
        etData.setText(DateFormat.format("dd-MM-yyyy",now).toString());

        etCasa = (EditText) v.findViewById(R.id.etCasa);
        etLat = (EditText) v.findViewById(R.id.etLatitude);
        etLong = (EditText) v.findViewById(R.id.etLongitude);

        rgAtiv = (RadioGroup) v.findViewById(R.id.rgAtiv);
        rbNotificacao = (RadioButton) v.findViewById(R.id.rbNotifica);
        rbAtendimento = (RadioButton) v.findViewById(R.id.rbAtendimento);
        rbOutras = (RadioButton) v.findViewById(R.id.rbOutras);

        rgExec = v.findViewById(R.id.rgExec);
        rbSucen = v.findViewById(R.id.rbExSucen);
        rbMunicipio = v.findViewById(R.id.rbExMun);

        rgSit = (RadioGroup) v.findViewById(R.id.rgSit);
        rbTrabalhado = (RadioButton) v.findViewById(R.id.rbTrabalhada);
        rbFechado = (RadioButton) v.findViewById(R.id.rbFechada);
        rbDesabitado = (RadioButton) v.findViewById(R.id.rbDesabitada);

        rgLocal = (RadioGroup) v.findViewById(R.id.rgLocal);
        rbIntra = (RadioButton) v.findViewById(R.id.rbIntradomicilio);
        rbPeri = (RadioButton) v.findViewById(R.id.rbPeridomicilio);
        rbAmbos = (RadioButton) v.findViewById(R.id.rbAmbos);

        spMunicipio = (Spinner) v.findViewById(R.id.spMunicipio);
        spLocalidade = (Spinner) v.findViewById(R.id.spLocalidade);
        btSalva = (Button) v.findViewById(R.id.btSalva);
        btTratamento = (Button) v.findViewById(R.id.btTratamento);

        rgAtiv.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton checkedRadioButton = (RadioButton) group.findViewById(checkedId);
                int tag = Integer.valueOf((String) checkedRadioButton.getTag());
                btTratamento.setEnabled(tag == 1);
                btSalva.setEnabled(tag != 1);
            }
        });

        spMunicipio.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String pos = valMunicipio.get(spMunicipio.getSelectedItemPosition());
                addItensOnLocalidade(pos);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spLocalidade.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btSalva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int idIns = Integer.valueOf(valLocalidade.get(spLocalidade.getSelectedItemPosition()));
                Triatomineo triat = preSalva(idIns);
                if (triat != null) {
                    triat.manipula();
                }
            }
        });

        btTratamento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment rFragment = new TratamentoFragment();
                // Creating a Bundle object
                Bundle data = new Bundle();
                // passando os valores para o próximo form
                data.putInt("position", id);
                int idIns = Integer.valueOf(valLocalidade.get(spLocalidade.getSelectedItemPosition()));
                data.putInt("id_inseto", idIns);
                Triatomineo triat = preSalva(idIns);
                if (triat != null) {
                    data.putSerializable("triatomineo", triat);
                    // Setting the position to the fragment
                    rFragment.setArguments(data);
                    // Getting reference to the FragmentManager
                    FragmentManager fragmentManager = getFragmentManager();
                    // Creating a fragment transaction
                    FragmentTransaction ft = fragmentManager.beginTransaction();
                    // Adding a fragment to the fragment transaction
                    ft.replace(R.id.content_frame, rFragment);
                    // Committing the transaction
                    ft.commit();
                }
            }
        });

        addItensOnMunicipio();
        setDateTimeField();
        if (id_inseto>0){
            popula();
        }
    }

    private Triatomineo preSalva(int idIns) {
        Triatomineo triat = null;

        if (rgAtiv.getCheckedRadioButtonId() == -1){
            Snackbar.make(getActivity().findViewById(android.R.id.content), "É necessário escolher a atividade!", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        } else if (rgSit.getCheckedRadioButtonId() == -1){
            Snackbar.make(getActivity().findViewById(android.R.id.content), "É necessário definir a situação do imóvel!", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        } else if (rgLocal.getCheckedRadioButtonId() == -1){
            Snackbar.make(getActivity().findViewById(android.R.id.content), "É necessário definir o local de captura!", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        } else {
            triat = new Triatomineo(id_inseto);
            triat.setDt_atendimento(etData.getText().toString());
            triat.setLocalidade(spLocalidade.getSelectedItem().toString());
            triat.setNumero_casa(etCasa.getText().toString());

            triat.setId_aux_atividade(rgAtiv.indexOfChild(getActivity().findViewById(rgAtiv.getCheckedRadioButtonId())) + 1);
            triat.setId_municipio(Integer.valueOf(valMunicipio.get(spMunicipio.getSelectedItemPosition())));
            triat.setLocalidade(spLocalidade.getSelectedItem().toString().trim());

            triat.setSituacao(rgSit.indexOfChild(getActivity().findViewById(rgSit.getCheckedRadioButtonId())) + 1);
            triat.setId_execucao(rgExec.indexOfChild(getActivity().findViewById(rgExec.getCheckedRadioButtonId())) + 1);

            triat.setId_aux_local_captura(rgLocal.indexOfChild(getActivity().findViewById(rgLocal.getCheckedRadioButtonId())) + 1);
            triat.setLatitude(etLat.getText().toString());
            triat.setLongitude(etLong.getText().toString());
            Suspeito susp = new Suspeito(idIns);
            triat.setId_usuario(susp.getId_usuario());
            triat.setId_inseto_suspeito(susp.getId_inseto_suspeito());

            triat.setStatus(id_inseto>0 ? 1 : 0);
        }
        return triat;
    }

    private void addItensOnMunicipio() {
        Suspeito susp = new Suspeito(0);

        List<String> list = susp.comboMunicipio();
        valMunicipio = susp.idMun;
        ArrayAdapter<String> dados = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, list);

        dados.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spMunicipio.setAdapter(dados);
    }

    private void addItensOnLocalidade(String mun) {
        Suspeito susp = new Suspeito(0);

        List<String> list = susp.comboLocalidade(mun);
        valLocalidade = susp.idLoc;
        ArrayAdapter<String> dados = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, list);

        dados.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spLocalidade.setAdapter(dados);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_notificacao, container, false);
        setupComps(v);
        startGPS(v);
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

    public void startGPS(View v) {
        final View v1 = v;
        Context ctx = getActivity();
        LocationManager lManager = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);

        boolean enabled = lManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!enabled) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        } else {
            LocationListener lListener = new LocationListener() {

                public void onStatusChanged(String provider, int status, Bundle extras) {
                }

                public void onProviderEnabled(String provider) {
                }

                public void onProviderDisabled(String provider) {
                }

                @Override
                public void onLocationChanged(Location locat) {
                    updateView(locat, v1);
                }
            };
            if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION)) {
                } else {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            MY_PERMISSIONS_REQUEST_GPS);
                }

                //MyToast toast = new MyToast(ctx, Toast.LENGTH_SHORT);
                //toast.show("É necessário autorizar o uso do GPS");
                //return;
            }
            lManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, lListener);
            //lManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0,lListener);
        }
    }

    //  M�todo que faz a atualiza��o da tela para o usu�rio.
    public void updateView(Location locat, View v){


        Double latitude 	= locat.getLatitude();
        Double longitude 	= locat.getLongitude();

        etLat.setText(latitude.toString());
        etLong.setText(longitude.toString());
    }

    private void setDateTimeField() {
        etData.setOnClickListener(onMudaData);

        Calendar newCalendar = Calendar.getInstance();
        dpData = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                etData.setText(dateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));


    }

    View.OnClickListener onMudaData = new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            dpData.show();
        }
    };

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
