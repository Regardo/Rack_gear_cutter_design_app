package com.example.rackgearcutterdesignapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.rackgearcutterdesignapp.databinding.FragmentFirstBinding;

import java.time.Year;
import java.util.IllegalFormatCodePointException;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.glowbuttonCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bundle resultCalculation = new Bundle();
                resultCalculation.putString("resultCalculation", doCalculate());
                resultCalculation.putString("initialData", getInitialData());



                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_SecondFragment, resultCalculation);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public String doCalculate() {
        String resultCalculation = "";

        //-------------
        //START get initial data
        //-------------

        float m_gearModule;
        if (binding.editTextGearModule.getText().toString().length() == 0) m_gearModule = 2.25F;
        else m_gearModule = Float.parseFloat(binding.editTextGearModule.getText().toString());

        float a_angleEngagement;
        if (binding.editTextAngle.getText().toString().length() == 0) a_angleEngagement = 20;
        else a_angleEngagement = Float.parseFloat(binding.editTextAngle.getText().toString());

        float c_radialClearance;
        if (binding.editTextClearance.getText().toString().length() == 0) c_radialClearance = 0.3F;
        else c_radialClearance = Float.parseFloat(binding.editTextClearance.getText().toString());

        float D_gearDiameter;
        if (binding.editTextDiameter.getText().toString().length() == 0) D_gearDiameter = 150;
        else D_gearDiameter = Float.parseFloat(binding.editTextDiameter.getText().toString());

        boolean type = binding.radioType1.isChecked();
        boolean classA = binding.radioClassA.isChecked();

        //-------------
        //END get initial data
        //-------------

        //------------
        //0. Проверка входных данных
        //------------
        if (2 >= m_gearModule | m_gearModule >= 20){
            return resultCalculation = getString(R.string.errorModule2_20);
        }

        if (40 >= D_gearDiameter | D_gearDiameter >= 1200){
            return resultCalculation = getString(R.string.errorDiameter40_1200);
        }
        else {
            if (D_gearDiameter < 100){
                if (m_gearModule <=10);
                else return resultCalculation = getString(R.string.errorMandD_40_100_2_10);
            }
            else {
                if (D_gearDiameter >= 800 && D_gearDiameter <1200){
                    if (m_gearModule >= 2.25 && m_gearModule <= 20);
                    else return resultCalculation = getString(R.string.errorMandD_800_1200_2_25_20);
                }
            }

        }
        if (a_angleEngagement < 0) return resultCalculation = getString(R.string.errorAngleLessZero);

        if (c_radialClearance < 0) return resultCalculation = getString(R.string.errorClearanceLessZero);


        //---------------
        //1. Угол профиля
        //---------------

        float r1_Y;
        if (type) r1_Y = 6.5F;
        else r1_Y = 4;

        float r1_ai;
        r1_ai = getTrunc(Math.atan((Math.tan(Math.toRadians(a_angleEngagement)) * Math.cos(Math.toRadians(r1_Y)))), 1000);
        r1_ai = getTrunc((float) Math.toDegrees(r1_ai),10);

        resultCalculation = resultCalculation + getString(R.string.r1_profileAngle, r1_ai + "") + getString(R.string.textRN);

        //---------------
        //2. Шаг гребенки
        //---------------

        float r2_t;
        r2_t = getTrunc(Math.PI * m_gearModule, 1000);

        resultCalculation = resultCalculation + getString(R.string.r2_rackPitch, r2_t + "") + getString(R.string.textRN);

        //--------------
        //3. Толщина зуба
        //--------------

        float r3_S = getTrunc((Math.PI * m_gearModule) / 2, 1000);
        resultCalculation = resultCalculation + getString(R.string.r3_toothThickness, r3_S + "") + getString(R.string.textRN);

        //---------------
        //4. Высота головки зуба гребенки
        //---------------

        float r4_hg = getTrunc((1.25 * m_gearModule) / Math.cos(Math.toRadians(r1_Y)), 100);
        resultCalculation = resultCalculation + getString(R.string.r4_heightToothHead, r4_hg + "") + getString(R.string.textRN);

        //----------------
        //5. Высота ножки зуба гребенки
        //----------------

        float r5_delta = 0;
        if (m_gearModule < 2) r5_delta = 0.6f;
        else if (m_gearModule < 3.75) r5_delta = 0.8f;
        else if (m_gearModule <= 6.5) r5_delta = 1;
        else if (m_gearModule <= 10) r5_delta = 1.5f;
        else if (m_gearModule <= 16) r5_delta = 2;
        else if (m_gearModule <= 24) r5_delta = 2.5f;

        float r5_hn = getTrunc((m_gearModule + r5_delta) / Math.cos(Math.toRadians(r1_Y)), 1000);

        resultCalculation = resultCalculation + getString(R.string.r5_heightToothStem, r5_hn + "") + getString(R.string.textRN);

        //-------------------
        //6. Общая высота зуба гребенки
        //-------------------

        float r6_h = r4_hg + r5_hn;

        resultCalculation = resultCalculation + getString(R.string.r6_heightToothTotal, r6_h + "") + getString(R.string.textRN);


















        return resultCalculation;
    }

    public String getInitialData() {
        String InitialData = "";

        float m_gearModule;
        if (binding.editTextGearModule.getText().toString().length() == 0) m_gearModule = 2.25F;
        else m_gearModule = Float.parseFloat(binding.editTextGearModule.getText().toString());

        float a_angleEngagement;
        if (binding.editTextAngle.getText().toString().length() == 0) a_angleEngagement = 20;
        else a_angleEngagement = Float.parseFloat(binding.editTextAngle.getText().toString());

        float c_radialClearance;
        if (binding.editTextClearance.getText().toString().length() == 0) c_radialClearance = 0.3F;
        else c_radialClearance = Float.parseFloat(binding.editTextClearance.getText().toString());

        float D_gearDiameter;
        if (binding.editTextDiameter.getText().toString().length() == 0) D_gearDiameter = 150;
        else D_gearDiameter = Float.parseFloat(binding.editTextDiameter.getText().toString());

        boolean type = binding.radioType1.isChecked();
        boolean classA = binding.radioClassA.isChecked();

        String typeS;
        if (type) typeS = getString(R.string.textFirstType);
        else typeS = getString(R.string.textSecondType);

        String classAS;
        if (classA) classAS = getString(R.string.textClassA);
        else classAS = getString(R.string.textClassB);


        InitialData = getString(R.string.textInitialData) + getString(R.string.textRN)
                + getString(R.string.textGearModule) + " " + m_gearModule + getString(R.string.textMillimeter) + getString(R.string.textRN)
                + getString(R.string.textAngleEngagement) + " " + a_angleEngagement + getString(R.string.textAngle) + getString(R.string.textRN)
                + getString(R.string.textRadialClearance) + " " + c_radialClearance + getString(R.string.textMillimeter) + getString(R.string.textRN)
                + getString(R.string.textGearWheelDiameter) + " " + D_gearDiameter + getString(R.string.textMillimeter) + getString(R.string.textRN)
                + getString(R.string.textType) + " " + typeS + getString(R.string.textRN)
                + getString(R.string.textClass) + " " + classAS + getString(R.string.textRN);


        return InitialData;

    }

    public float getTrunc(double value, int round) {
        float d = ((float)((int)(value*round)))/round;
        return d;
    }


}