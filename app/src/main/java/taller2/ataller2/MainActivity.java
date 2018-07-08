package taller2.ataller2;


import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
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

import java.util.ArrayList;
import java.util.List;

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
import taller2.ataller2.services.AmistadesService;
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

    private ViewPager mViewPager;
    private TabLayout mTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
       // getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        mViewPager = findViewById(R.id.container);
        setupViewPager(mViewPager);

        mTabLayout = findViewById(R.id.tabs);
        mTabLayout.setupWithViewPager(mViewPager);

        mTabLayout.getTabAt(0).setIcon(R.drawable.ic_home);
        mTabLayout.getTabAt(1).setIcon(R.drawable.ic_group_add_black_18dp);
        mTabLayout.getTabAt(2).setIcon(R.drawable.ic_sms_black_18dp);
        mTabLayout.getTabAt(3).setIcon(R.drawable.user_perfil);
        mTabLayout.getTabAt(4).setIcon(R.drawable.location);

//        ServiceLocator.get(HistoriasService.class).updateHistoriasData(this);
//        ServiceLocator.get(PerfilService.class).updatePerfilData(this,ServiceLocator.get(FacebookService.class).getFacebookID());
//        ServiceLocator.get(AmistadesService.class).getAmistades(this);
//
    }

    private void setupViewPager(ViewPager viewPager) {
        viewPager.setOffscreenPageLimit(2);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ListadoHistoriasFragment(), "Inicio");
        adapter.addFragment(new ListadoAmistadesFragment(), "Amigos");
        adapter.addFragment(new ListadoConversacionesFragment(), "Chats");
        adapter.addFragment(new PerfilFragment(), "Perfil");
        adapter.addFragment(new MapaHistoriasFragment(), "Mapa");
        viewPager.setAdapter(adapter);
    }

    public void goMenu(){

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

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
