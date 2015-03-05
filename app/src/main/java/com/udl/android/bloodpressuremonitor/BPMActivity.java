package com.udl.android.bloodpressuremonitor;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.udl.android.bloodpressuremonitor.application.BPMmasterActivity;
import com.udl.android.bloodpressuremonitor.interfaces.HomeButtonsListener;

import org.w3c.dom.Text;


public class BPMActivity extends BPMmasterActivity
                         implements HomeButtonsListener {

    public static enum HomeButton{

        HEART,
        LIST,
        PROFILE,
        HELP,
        BLUETOOTH
    }

    private TextView headertextview;
    private ImageButton buttonbar;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bloodpressuremonitorlayout);
        configureActionBar();
        defineListeners();
        addTypefacesToTextViews();
    }


    private void defineListeners(){

        ImageView heart = (ImageView) findViewById(R.id.heartimage);
        ImageView list  = (ImageView) findViewById(R.id.listimage);
        ImageView perfil = (ImageView) findViewById(R.id.prefilimage);
        ImageView help = (ImageView) findViewById(R.id.helpimage);

    }

    private void addTypefacesToTextViews(){

        TextView menu1 = (TextView) findViewById(R.id.heartratetext);
        TextView menu2 = (TextView) findViewById(R.id.measurementtext);
        TextView menu3 = (TextView) findViewById(R.id.obtainpressurestext);
        TextView menu4 = (TextView) findViewById(R.id.addmeditiontext);
        TextView menu5 = (TextView) findViewById(R.id.perfiltext);
        TextView menu6 = (TextView) findViewById(R.id.helptext);

        menu1.setTypeface(getMntcorsiva());
        menu2.setTypeface(getMntcorsiva());
        menu3.setTypeface(getMntcorsiva());
        menu4.setTypeface(getMntcorsiva());
        menu5.setTypeface(getMntcorsiva());
        menu6.setTypeface(getMntcorsiva());

    }

    private void configureActionBar(){

        configureBaseActionBar();
        View view = getActionBarView();
        headertextview = (TextView) view.findViewById(R.id.textactionbar);
        headertextview.setText(getResources().getString(R.string.principaltext).toUpperCase());
        buttonbar = (ImageButton) findViewById(R.id.actionbarbutton);
        buttonbar.setVisibility(View.GONE);
    }

    @Override
    public void OnHomeButtonClick(View view) {
        int tag = Integer.parseInt((String)view.getTag());
       switch (tag){

           case 1:
               System.out.println("TAG ES 1");
               break;
           case 2:
               System.out.println("TAG ES 2");
               break;
           case 3:
               System.out.println("TAG ES 3");
               break;
           case 4:
               System.out.println("TAG ES 4");
               break;
           default:
               break;

       }

    }
}
