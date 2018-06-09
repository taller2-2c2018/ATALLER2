package taller2.ataller2.services;

import android.content.Context;
import android.graphics.BitmapFactory;

import java.util.ArrayList;
import java.util.List;

import taller2.ataller2.model.Amistad;
import taller2.ataller2.R;
public class MockAmistadesService implements AmistadesService {

    //private static Context context;
    private List<Amistad> mAmistades;
    private Context mContext;
    public MockAmistadesService(Context context){
        mContext = context;
    }

    @Override
    public void updateAmistadesData() {

        mAmistades = new ArrayList<>();
        Amistad c1 = new Amistad("Fernando Nitz");
        Amistad c2 = new Amistad("Manuel Ortiz");
        c1.setPicture(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ringo));
        c2.setPicture(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.markz));
        mAmistades.add(c1);
        mAmistades.add(c2);
    }

    @Override
    public List<Amistad> getAmistades() {
        if (mAmistades == null) {
            updateAmistadesData();
        }
        return mAmistades;
    }

    @Override
    public Amistad getAmistad(int index) {
        return mAmistades.get(index);
    }
}