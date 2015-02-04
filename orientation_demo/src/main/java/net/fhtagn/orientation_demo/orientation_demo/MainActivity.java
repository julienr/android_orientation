package net.fhtagn.orientation_demo.orientation_demo;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

import net.fhtagn.orientation.orientation.ui.OrientationViewerFragment;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(
            R.id.fragment_container,
            OrientationViewerFragment.newInstance()
        );
        fragmentTransaction.commit();
    }
}
