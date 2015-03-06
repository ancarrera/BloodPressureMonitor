package com.udl.android.bloodpressuremonitor;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.udl.android.bloodpressuremonitor.application.BPMmasterActivity;
import com.udl.android.bloodpressuremonitor.fragments.MeasurementsFragment;
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
               MeasurementsFragment measurementsFragment = MeasurementsFragment.getNewInstance();

               break;
           case 3:
               System.out.println("TAG ES 3");
               break;
           case 4:
               System.out.println("TAG ES 4");
               break;
           case 5:
               break;
           case 6:
               break;
           default:
               break;

       }

    }
//
//    private void selectFragment(Fragment fragment, b){
//
//        FragmentManager fragmentManager = this.getSupportFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        int id = R.id.content_frame;
//        if (displayAnimation) {
//            if (isBack) {
//                fragmentTransaction.setCustomAnimations(R.anim.fade_in,
//                        R.anim.slide_out_down);
//            } else {
//                fragmentTransaction.setCustomAnimations(R.anim.slide_in_up,
//                        R.anim.fade_out);
//            }
//        }
//
//        if (fragment instanceof HomeFragment){
//            while (HomeFragmentManager.getInstance(this).getHomeFragmentStack().size() > 1){
//                HomeFragmentManager.getInstance(this).getHomeFragmentStack().pop();
//            }
//            HomeFragmentManager.getInstance(this).setHomeFragment(fragment);
//        }
//        Fragment lastFragment = this.getSupportFragmentManager().findFragmentById(R.id.content_frame);
//        if (!isBack)
//            HomeFragmentManager.getInstance(this).getHomeFragmentStack().push(lastFragment);
//
//        if (fragmentManager.findFragmentById(id) == null) {
//            fragmentTransaction.add(id, fragment);
//            fragmentTransaction.commit();
//        } else {
//            fragmentTransaction.replace(id, fragment);
//            fragmentTransaction.commit();
//        }
//
//
//    }
}
