package net.fhtagn.orientation.orientation.ui;

import android.content.Context;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import net.fhtagn.orientation.orientation.R;
import net.fhtagn.orientation.orientation.sensors.OrientationTracker;
import net.fhtagn.orientation.orientation.sensors.RotVectorOrientationTracker;

// A fragment that displays a 3D cuboid representing the phone's orientation
public class OrientationViewerFragment extends Fragment {
    private final static String TAG = "OrientationViewerFragment";
    private GLSurfaceView surfaceView;
    private TextView debugText;
    private OrientationRenderer renderer;
    private LevelView levelView;

    private SensorManager sensorManager;
    private OrientationTracker orientationTracker;

    private Handler uiHandler = new Handler();
    private UpdateDebugTaskWorker debugTextWorker = new UpdateDebugTaskWorker();

    public static OrientationViewerFragment newInstance() {
        OrientationViewerFragment fragment = new OrientationViewerFragment();
        Bundle args = new Bundle();
        //args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    public OrientationViewerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //mParam1 = getArguments().getString(ARG_PARAM1);
        }

        sensorManager = (SensorManager)getActivity().getSystemService(Context.SENSOR_SERVICE);
        //orientationTracker = new RelativeOrientationTracker(
        //        new GyroOrientationTracker(sensorManager));
        //orientationTracker = new MagnAccelOrientationTracker(sensorManager);
        orientationTracker = new RotVectorOrientationTracker(sensorManager);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_orientation_viewer, container, false);
        debugText = (TextView)rootView.findViewById(R.id.debugtext);
        debugText.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        surfaceView = (GLSurfaceView)rootView.findViewById(R.id.surface);
        renderer = new OrientationRenderer(getActivity(), orientationTracker);
        surfaceView.setRenderer(renderer);

        levelView = (LevelView)rootView.findViewById(R.id.level_view);
        levelView.setOrientationProvider(orientationTracker);

        Button btnBaseRot = (Button)rootView.findViewById(R.id.btn_baserot);
        btnBaseRot.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                orientationTracker.reset();
            }
        });

        debugTextWorker.start();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        orientationTracker.onResume(sensorManager);
        surfaceView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        orientationTracker.onPause(sensorManager);
        surfaceView.onPause();
        debugTextWorker.stop();
    }

    class UpdateDebugTaskWorker implements Runnable {
        private final long DELAY_MS = 200;
        public void start() {
            uiHandler.postDelayed(this, DELAY_MS);
        }
        @Override
        public void run() {
            debugText.setText(orientationTracker.getDebugText());
            uiHandler.postDelayed(this, DELAY_MS);
        }

        public void stop() {
            uiHandler.removeCallbacks(this);
        }
    }
}
