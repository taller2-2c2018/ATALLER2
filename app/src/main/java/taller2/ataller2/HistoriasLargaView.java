package taller2.ataller2;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.view.View;

/**
 * Created by FernandoN on 01/04/2018.
 */

public class HistoriasLargaView extends ConstraintLayout {

    private View aView;

    public HistoriasLargaView(Context context) {
        super(context);
        initView();
    }

    private void initView() {
        View view = inflate(getContext(), R.layout.layout_historia_larga, null);
        addView(view);
        aView = view;
    }

    public View getView(){
        return aView;
    }

}
