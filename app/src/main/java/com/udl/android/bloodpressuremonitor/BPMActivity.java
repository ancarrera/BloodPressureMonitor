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
import com.udl.android.bloodpressuremonitor.fragments.HomeFragment;
import com.udl.android.bloodpressuremonitor.fragments.MeasurementsFragment;

import org.w3c.dom.Text;


public class BPMActivity extends BPMmasterActivity
                         implements View.OnClickListener {

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
        setContentView(R.layout.homeactivitylayout);
        configureActionBar();

        selectFragment(HomeFragment.getNewInstace(),false,false);

    }

    private void configureActionBar(){

        configureBaseActionBar();
        View view = getActionBarView();
        headertextview = (TextView) view.findViewById(R.id.textactionbar);
        headertextview.setText(getResources().getString(R.string.principaltext).toUpperCase());
        buttonbar = (ImageButton) findViewById(R.id.actionbarbutton);
        buttonbar.setVisibility(View.GONE);
    }


//
    private void selectFragment(Fragment fragment,boolean isBack,boolean animation){

        FragmentManager fragmentManager = this.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        int id = R.id.fragmentframe;
        if (animation) {
            if (isBack) {
                fragmentTransaction.setCustomAnimations(R.anim.fade_in,
                        R.anim.slide_out_down);
            } else {
                fragmentTransaction.setCustomAnimations(R.anim.slide_in_up,
                        R.anim.fade_out);
            }
        }
//
//        if (fragment instanceof HomeFragment){
//            while (HomeFragmentManager.getInstance(this).getHomeFragmentStack().size() > 1){
//                HomeFragmentManager.getInstance(this).getHomeFragmentStack().pop();
//            }
//            HomeFragmentManager.getInstance(this).setHomeFragment(fragment);
//        }
//        Fragment lastFragment = this.getSupportFragmentManager().findFragmentById(R.id.fragmentframe);
//        if (!isBack)
//            HomeFragmentManager.getInstance(this).getHomeFragmentStack().push(lastFragment);

        if (fragmentManager.findFragmentById(id) == null) {
            fragmentTransaction.add(id, fragment);
            fragmentTransaction.commit();
        } else {
            fragmentTransaction.replace(id, fragment);
            fragmentTransaction.commit();
        }


    }


    @Override
    public void onClick(View view) {

        int tag = Integer.parseInt((String)view.getTag());
        switch (tag){

            case 1:
                System.out.println("TAG ES 1");
                break;
            case 2:
                MeasurementsFragment measurementsFragment = MeasurementsFragment.getNewInstance();
                selectFragment(measurementsFragment,false,false);
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


}
