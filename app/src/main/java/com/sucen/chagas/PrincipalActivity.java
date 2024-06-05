package com.sucen.chagas;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import util.MyToast;
import util.gerenciaBanco;

public class PrincipalActivity extends AppCompatActivity
        implements NotificacaoFragment.OnFragmentInteractionListener, TratamentoFragment.OnFragmentInteractionListener,
        ImportaFragment.OnFragmentInteractionListener, RelImportaFragment.OnFragmentInteractionListener,
        ConsultaFragment.OnFragmentInteractionListener, EnvioFragment.OnFragmentInteractionListener,
        LimpezaFragment.OnFragmentInteractionListener, InicialFragment.OnFragmentInteractionListener,
        NavigationView.OnNavigationItemSelectedListener {

    public static Context chagasContext;
    gerenciaBanco gerenciador;
    private MyToast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        chagasContext = getApplicationContext();
        inicial();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.principal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            mnuSobre();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void mnuSobre() {
        String versName = "Chagas vs: " + BuildConfig.VERSION_NAME;
        String sobre = "Sistema para automação da coleta de informações sobre atividades de controle da doença de Chagas.\r\nSucen\r\n" + "\nVersão: "+ BuildConfig.VERSION_NAME;
        new AlertDialog.Builder(this)
                .setTitle(versName)
                .setMessage(sobre)
                .setCancelable(true)
                .create().show();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment rFragment = null;

        if (id == R.id.notificacao) {
            rFragment = new NotificacaoFragment();
        } else if (id == R.id.atendimento) {
            rFragment = new ConsultaFragment();
        } else if (id == R.id.manutencao) {
            rFragment = new LimpezaFragment();
        } else if (id == R.id.receber) {
            rFragment = new ImportaFragment();
        } else if (id == R.id.envio) {
            rFragment = new EnvioFragment();
        }
        // Creating a Bundle object
        Bundle data = new Bundle();
        // Setting the index of the currently selected item of mDrawerList
        data.putInt("position", id);
        data.putLong("id_inseto", 0);
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

        // Closing the drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void inicial(){
        Fragment frag = new InicialFragment();
        // Getting reference to the FragmentManager
        FragmentManager fragmentManager = getFragmentManager();
        // Creating a fragment transaction
        FragmentTransaction ft = fragmentManager.beginTransaction();
        // Adding a fragment to the fragment transaction
        ft.replace(R.id.content_frame, frag);
        // Committing the transaction
        ft.commit();
    }

    public static Context getChagasContext(){
        return chagasContext;
    }

    public void onFragmentInteraction(Uri uri){
        //you can leave it empty
    }

    //------------------Classes Assincronas ----------------//

    private class VerificaBanco extends AsyncTask<String, Void, String> {
        ProgressDialog dialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            dialog = new ProgressDialog(chagasContext);
            dialog.setMessage("Verificando base de dados...");
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        }

        @Override
        protected String doInBackground(String... params) {
            gerenciador = new gerenciaBanco(getApplicationContext());
            return "Verificado";
        }

        @Override
        protected void onPostExecute(String result) {
            dialog.dismiss();
            toast.show(result);
            gerenciador.closeDB();
        }

    }
}
