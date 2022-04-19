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

        //--------------------
        //7. Толщина зуба по вершине гребенки
        //--------------------

        float r7_Sv = getTrunc(r3_S - 2 * r4_hg * Math.tan(Math.toRadians(r1_ai)), 1000);

        resultCalculation = resultCalculation + getString(R.string.r7_heightToothTop, r7_Sv + "") + getString(R.string.textRN);

        //--------------------
        //8. Угол фланкирования
        //--------------------

        float r8_alfaF = 0;
        if (classA){
            if (m_gearModule <= 2) r8_alfaF = 1.67f;
            else if (m_gearModule <= 3.5) r8_alfaF = 1.33f;
            else if (m_gearModule <= 5) r8_alfaF = 1.12f;
            else if (m_gearModule <= 7) r8_alfaF = 1;
            else if (m_gearModule <= 11) r8_alfaF = 0.9f;
            else if (m_gearModule <= 20) r8_alfaF = 0.67f;
        }
        else {
            if (m_gearModule <= 2.75) r8_alfaF = 2.22f;
            else if (m_gearModule <= 4.25) r8_alfaF = 1.92f;
            else if (m_gearModule <= 5) r8_alfaF = 1.67f;
            else if (m_gearModule <= 9) r8_alfaF = 1.33f;
            else if (m_gearModule <= 20) r8_alfaF = 1.12f;
        }
        float r8_alfa1F = getTrunc(Math.atan(Math.tan(Math.toRadians(r8_alfaF)) * Math.cos(Math.toRadians(r1_Y))), 100);

        resultCalculation = resultCalculation + getString(R.string.r8_flankingAngle, r8_alfa1F + "") + getString(R.string.textRN);

        //--------------------
        //9. Расстояние от делительной прямой до начала фланкирования
        //--------------------

        float r9_1delta = 0;
        float r9_2delta = 0;
        if (D_gearDiameter >=40 && D_gearDiameter <= 100) {
            if (m_gearModule <= 2.25) {r9_1delta = 70; r9_2delta = 50;}
            else if (m_gearModule <= 4) {r9_1delta = 80; r9_2delta = 50;}
            else if (m_gearModule <= 6) {r9_1delta = 70; r9_2delta = 50;}
            else if (m_gearModule <= 8) {r9_1delta = 90; r9_2delta = 60;}
            else if (m_gearModule <= 10) {r9_1delta = 110; r9_2delta = 60;}
        }
        else if (D_gearDiameter > 100 && D_gearDiameter <= 200) {
            if (m_gearModule <= 2.25) {r9_1delta = 90; r9_2delta = 50;}
            else if (m_gearModule <= 4) {r9_1delta = 90; r9_2delta = 60;}
            else if (m_gearModule <= 6) {r9_1delta = 100; r9_2delta = 60;}
            else if (m_gearModule <= 8) {r9_1delta = 110; r9_2delta = 70;}
            else if (m_gearModule <= 10) {r9_1delta = 110; r9_2delta = 70;}
            else if (m_gearModule <= 14) {r9_1delta = 120; r9_2delta = 70;}
            else if (m_gearModule <= 20) {r9_1delta = 140; r9_2delta = 80;}
        }
        else if (D_gearDiameter > 200 && D_gearDiameter <= 400) {
            if (m_gearModule <= 2.25) {r9_1delta = 130; r9_2delta = 60;}
            else if (m_gearModule <= 4) {r9_1delta = 130; r9_2delta = 70;}
            else if (m_gearModule <= 6) {r9_1delta = 130; r9_2delta = 70;}
            else if (m_gearModule <= 8) {r9_1delta = 140; r9_2delta = 80;}
            else if (m_gearModule <= 10) {r9_1delta = 140; r9_2delta = 80;}
            else if (m_gearModule <= 14) {r9_1delta = 150; r9_2delta = 80;}
            else if (m_gearModule <= 20) {r9_1delta = 170; r9_2delta = 90;}
        }
        else if (D_gearDiameter > 400 && D_gearDiameter <= 800) {
            if (m_gearModule <= 2.25) {r9_1delta = 190; r9_2delta = 80;}
            else if (m_gearModule <= 4) {r9_1delta = 190; r9_2delta = 90;}
            else if (m_gearModule <= 6) {r9_1delta = 200; r9_2delta = 90;}
            else if (m_gearModule <= 8) {r9_1delta = 200; r9_2delta = 90;}
            else if (m_gearModule <= 10) {r9_1delta = 210; r9_2delta = 100;}
            else if (m_gearModule <= 14) {r9_1delta = 220; r9_2delta = 110;}
            else if (m_gearModule <= 20) {r9_1delta = 230; r9_2delta = 110;}
        }
        else if (D_gearDiameter > 800 && D_gearDiameter <= 1200) {
            if (m_gearModule <= 4) {r9_1delta = 260; r9_2delta = 110;}
            else if (m_gearModule <= 6) {r9_1delta = 260; r9_2delta = 120;}
            else if (m_gearModule <= 8) {r9_1delta = 260; r9_2delta = 120;}
            else if (m_gearModule <= 10) {r9_1delta = 270; r9_2delta = 130;}
            else if (m_gearModule <= 14) {r9_1delta = 280; r9_2delta = 130;}
            else if (m_gearModule <= 20) {r9_1delta = 290; r9_2delta = 140;}
        }

        float r9_hf = getTrunc((0.55 * m_gearModule) + (r9_1delta / 1000) + ((r9_2delta / 1000) / 2), 1000);
        float r9_hf1 = getTrunc(r9_hf / Math.cos(Math.toRadians(r1_Y)), 1000);

        resultCalculation = resultCalculation + getString(R.string.r9_distance, r9_hf + "", r9_hf1 +  "") + getString(R.string.textRN);

        //------------------
        //10. Ширина впадины между зубьями в начале фланкирования
        //------------------

        float r10_Sf = r2_t - r3_S - getTrunc(2 * r9_hf * getTrunc(Math.tan(Math.toRadians(a_angleEngagement)), 100000), 1000);

        resultCalculation = resultCalculation + getString(R.string.r10_widthBetweenTeeth, r10_Sf + "") + getString(R.string.textRN);

        //-----------------
        //11. Угол профиля
        //-----------------

        float r11_av = 0;
        if (type) r11_av = 5.5f;
        else r11_av = 6.87f;
        float r11_aiN = getTrunc( Math.toDegrees(Math.atan(getTrunc((Math.tan(Math.toRadians(a_angleEngagement)) * Math.cos(Math.toRadians(r1_Y))) / Math.cos(Math.toRadians(r1_Y + r11_av)), 1000 ))), 100);

        resultCalculation = resultCalculation + getString(R.string.r11_profileAngle, r11_aiN + "") + getString(R.string.textRN);

        //------------------
        //12. Высота головки зуба
        //------------------

        float r12_h1N = getTrunc((r5_hn * Math.cos(Math.toRadians(r1_Y + r11_av))) / Math.cos(Math.toRadians(r1_Y)), 100);

        resultCalculation = resultCalculation + getString(R.string.r12_toothHeadHeight, r12_h1N + "") + getString(R.string.textRN);

        //------------------
        //13. Высота ножки зуба
        //------------------

        float r13_h2N = getTrunc(((r4_hg + r5_delta) * Math.cos(Math.toRadians(r1_Y + r11_av))) / Math.cos(Math.toRadians(r1_Y)), 100);

        resultCalculation = resultCalculation + getString(R.string.r13_toothFootHeight, r13_h2N + "") + getString(R.string.textRN);

        //------------------
        //14. Общая высота зуба
        //------------------

        float r14_hN = r12_h1N + r13_h2N;

        resultCalculation = resultCalculation + getString(R.string.r14_toothHeightTotal, r14_hN + "") + getString(R.string.textRN);

        //-------------------
        //15. Радиус закругления вершин зубьев
        //-------------------

        float r15_rad1 = m_gearModule * 0.4f;

        resultCalculation = resultCalculation + getString(R.string.r15_radiusTopTooth, r15_rad1 + "") + getString(R.string.textRN);

        //-------------------
        //16. Радиус закругления впадин
        //-------------------

        float r16_rad2 = m_gearModule * 0.2f;

        resultCalculation = resultCalculation + getString(R.string.r16_radiusDepressions, r16_rad2 + "") + getString(R.string.textRN);

        //-------------------
        //17. Диаметр калибра для контроля толщины зуба гребенки
        //-------------------

        float r17_dCalibre = getTrunc(((r2_t - r7_Sv) * Math.tan(Math.toRadians((90 - r11_aiN) / 2))), 1000);

        resultCalculation = resultCalculation + getString(R.string.r17_diameterCalibre, r17_dCalibre + "");

        //-------------------
        //18. Конструктивные размеры зуборезной гребенки
        //-------------------





















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