package taller2.ataller2;


import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageButton;
import android.widget.SearchView;

import taller2.ataller2.model.Amistad;
import taller2.ataller2.model.Conversacion;
import taller2.ataller2.model.Historia;
import taller2.ataller2.model.ListadoAmistadesFragment;
import taller2.ataller2.model.ListadoConversacionesFragment;
import taller2.ataller2.model.ListadoHistoriasFragment;
import taller2.ataller2.model.ListadoNotificacionesFragment;
import taller2.ataller2.model.MapaHistoriasFragment;
import taller2.ataller2.model.Notificacion;
import taller2.ataller2.model.Perfil;
import taller2.ataller2.model.PerfilFragment;
import taller2.ataller2.services.HistoriasService;
import taller2.ataller2.services.PerfilService;
import taller2.ataller2.services.ServiceLocator;
import taller2.ataller2.services.facebook.FacebookService;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener,
        ListadoAmistadesFragment.AmistadesListListener,
        ListadoHistoriasFragment.HistoriasListListener,
        ListadoConversacionesFragment.ConversacionesListListener,
        ListadoNotificacionesFragment.NotificacionesListListener,
        PerfilFragment.PerfilListener{

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //ServiceLocator.get(HistoriasService.class).updateHistoriasData(this);
        //ServiceLocator.get(PerfilService.class).updatePerfilData(this,ServiceLocator.get(FacebookService.class).getFacebookID());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        ImageButton b_menu = findViewById( R.id.buttonMenu );
        b_menu.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                goMenu();
            }
        });

        ImageButton b_chat = findViewById( R.id.buttonChat );
        b_chat.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                goChat();
            }
        });

        ImageButton b_amigos = findViewById( R.id.buttonAmigos );
        b_amigos.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                goAmistades();
            }
        });

        /*
        ImageButton b_notif = findViewById( R.id.buttonNotificaciones );
        b_notif.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                goNotif();
            }
        }); */

        ImageButton b_perfil = findViewById( R.id.buttonOptions );
        b_perfil.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                goPerfil();
            }
        });

        ImageButton b_location = findViewById( R.id.buttonLocation );
        b_location.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                goLocation();
            }
        });


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //         .setAction("Action", null).show();
            }
        });

    }

    public void goMenu(){
        ServiceLocator.get(HistoriasService.class).updateHistoriasData(this);
        ServiceLocator.get(PerfilService.class).updatePerfilData(this,ServiceLocator.get(FacebookService.class).getFacebookID());
        mViewPager.setCurrentItem(0);
    }
    public void goAmistades(){
        mViewPager.setCurrentItem(1);
    }
    //public void goNotif(){ mViewPager.setCurrentItem(2); }
    public void goChat(){ mViewPager.setCurrentItem(2); }
    public void goPerfil(){ mViewPager.setCurrentItem(3); }
    public void goLocation() { mViewPager.setCurrentItem(4); }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.buttonChat)              {goChat(); return true;}
        //if (id == R.id.buttonNotificaciones)    {goNotif(); return true;}
        if (id == R.id.buttonAmigos)            {goAmistades(); return true;}
        if (id == R.id.buttonMenu)              { goMenu(); return true; }
        if (id == R.id.buttonOptions)           {goPerfil(); return true;}
        if (id == R.id.buttonLocation)          { goLocation();return true;}
        //if (id == R.id.action_logout)           {exit(); return true;}

        //if (id == R.id.action_perfil)         {goPerfil(); return true; }
        return super.onOptionsItemSelected(item);
    }

    public void exit(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
/*    public void goPerfil(){
        Intent intent = new Intent(this, MiPerfilActivity.class);
        startActivity(intent);
    }*/

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public void onAmistadClicked(Amistad amistad) {
    }

    @Override
    public void onHistoriaClicked(Historia historia) {
    }
    @Override
    public void onConversacionClickedRechazar(Conversacion conversacion){
        String rechazar = "rechazo";
        conversacion.notify();
    }
    @Override
    public void onConversacionClickedAceptar(Conversacion conversacion){
    }
    @Override
    public void onNotificacionClicked(Notificacion notificacion) {
        // Intent intent = new Intent();
        // intent.setClass(this, MiPerfilActivity.class);
        // startActivity(intent);
    }

    @Override
    public void onPerfilClicked(Perfil perfil) {

    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            final View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            ImageButton b_menu = (ImageButton) rootView.findViewById(R.id.buttonMenu);
            b_menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.setVisibility(View.GONE);
                }
            });
            return rootView;

        }
    }
    public void pantallaPPAL(){
        Intent intent = new Intent(this, PerfilActivity.class);
        startActivity(intent);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch(position) {
                case 0 :
                    return new ListadoHistoriasFragment();
                case 1 :
                    return new ListadoAmistadesFragment();
                //case 2 :
                //    return new ListadoNotificacionesFragment();
                case 2 :
                    return new ListadoConversacionesFragment();
                case 3 :
                    return new PerfilFragment();
                case 4:
                    return new MapaHistoriasFragment();
            }
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 5;
        }

    }
}
