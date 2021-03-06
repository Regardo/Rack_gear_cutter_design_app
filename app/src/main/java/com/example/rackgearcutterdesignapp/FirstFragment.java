package com.example.rackgearcutterdesignapp;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.rackgearcutterdesignapp.databinding.FragmentFirstBinding;

import java.lang.reflect.Array;
import java.time.Year;
import java.util.Arrays;
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
        //0. ???????????????? ?????????????? ????????????
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
        //1. ???????? ??????????????
        //---------------

        float r1_Y;
        if (type) r1_Y = 6.5F;
        else r1_Y = 4;

        float r1_ai;
        r1_ai = getTrunc(Math.atan((Math.tan(Math.toRadians(a_angleEngagement)) * Math.cos(Math.toRadians(r1_Y)))), 1000);
        r1_ai = getTrunc((float) Math.toDegrees(r1_ai),10);

        resultCalculation = resultCalculation + getString(R.string.r1_profileAngle, r1_ai + "") + getString(R.string.textRN);

        //---------------
        //2. ?????? ????????????????
        //---------------

        float r2_t;
        r2_t = getTrunc(Math.PI * m_gearModule, 1000);

        resultCalculation = resultCalculation + getString(R.string.r2_rackPitch, r2_t + "") + getString(R.string.textRN);

        //--------------
        //3. ?????????????? ????????
        //--------------

        float r3_S = getTrunc((Math.PI * m_gearModule) / 2, 1000);
        resultCalculation = resultCalculation + getString(R.string.r3_toothThickness, r3_S + "") + getString(R.string.textRN);

        //---------------
        //4. ???????????? ?????????????? ???????? ????????????????
        //---------------

        float r4_hg = getTrunc((1.25 * m_gearModule) / Math.cos(Math.toRadians(r1_Y)), 100);
        resultCalculation = resultCalculation + getString(R.string.r4_heightToothHead, r4_hg + "") + getString(R.string.textRN);

        //----------------
        //5. ???????????? ?????????? ???????? ????????????????
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
        //6. ?????????? ???????????? ???????? ????????????????
        //-------------------

        float r6_h = r4_hg + r5_hn;

        resultCalculation = resultCalculation + getString(R.string.r6_heightToothTotal, r6_h + "") + getString(R.string.textRN);

        //--------------------
        //7. ?????????????? ???????? ???? ?????????????? ????????????????
        //--------------------

        float r7_Sv = getTrunc(r3_S - 2 * r4_hg * Math.tan(Math.toRadians(r1_ai)), 1000);

        resultCalculation = resultCalculation + getString(R.string.r7_heightToothTop, r7_Sv + "") + getString(R.string.textRN);

        //--------------------
        //8. ???????? ??????????????????????????
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
        //9. ???????????????????? ???? ?????????????????????? ???????????? ???? ???????????? ??????????????????????????
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
        //10. ???????????? ?????????????? ?????????? ?????????????? ?? ???????????? ??????????????????????????
        //------------------

        float r10_Sf = r2_t - r3_S - getTrunc(2 * r9_hf * getTrunc(Math.tan(Math.toRadians(a_angleEngagement)), 100000), 1000);

        resultCalculation = resultCalculation + getString(R.string.r10_widthBetweenTeeth, r10_Sf + "") + getString(R.string.textRN);

        //-----------------
        //11. ???????? ??????????????
        //-----------------

        float r11_av = 0;
        if (type) r11_av = 5.5f;
        else r11_av = 6.87f;
        float r11_aiN = getTrunc( Math.toDegrees(Math.atan(getTrunc((Math.tan(Math.toRadians(a_angleEngagement)) * Math.cos(Math.toRadians(r1_Y))) / Math.cos(Math.toRadians(r1_Y + r11_av)), 1000 ))), 100);

        resultCalculation = resultCalculation + getString(R.string.r11_profileAngle, r11_aiN + "") + getString(R.string.textRN);

        //------------------
        //12. ???????????? ?????????????? ????????
        //------------------

        float r12_h1N = getTrunc((r5_hn * Math.cos(Math.toRadians(r1_Y + r11_av))) / Math.cos(Math.toRadians(r1_Y)), 100);

        resultCalculation = resultCalculation + getString(R.string.r12_toothHeadHeight, r12_h1N + "") + getString(R.string.textRN);

        //------------------
        //13. ???????????? ?????????? ????????
        //------------------

        float r13_h2N = getTrunc(((r4_hg + r5_delta) * Math.cos(Math.toRadians(r1_Y + r11_av))) / Math.cos(Math.toRadians(r1_Y)), 100);

        resultCalculation = resultCalculation + getString(R.string.r13_toothFootHeight, r13_h2N + "") + getString(R.string.textRN);

        //------------------
        //14. ?????????? ???????????? ????????
        //------------------

        float r14_hN = r12_h1N + r13_h2N;

        resultCalculation = resultCalculation + getString(R.string.r14_toothHeightTotal, r14_hN + "") + getString(R.string.textRN);

        //-------------------
        //15. ???????????? ?????????????????????? ???????????? ????????????
        //-------------------

        float r15_rad1 = m_gearModule * 0.4f;

        resultCalculation = resultCalculation + getString(R.string.r15_radiusTopTooth, r15_rad1 + "") + getString(R.string.textRN);

        //-------------------
        //16. ???????????? ?????????????????????? ????????????
        //-------------------

        float r16_rad2 = m_gearModule * 0.2f;

        resultCalculation = resultCalculation + getString(R.string.r16_radiusDepressions, r16_rad2 + "") + getString(R.string.textRN);

        //-------------------
        //17. ?????????????? ?????????????? ?????? ???????????????? ?????????????? ???????? ????????????????
        //-------------------

        float r17_dCalibre = getTrunc(((r2_t - r7_Sv) * Math.tan(Math.toRadians((90 - r11_aiN) / 2))), 1000);

        resultCalculation = resultCalculation + getString(R.string.r17_diameterCalibre, r17_dCalibre + "") + getString(R.string.textRN);

        //-------------------
        //18. ???????????????????????????? ?????????????? ???????????????????? ????????????????
        //-------------------

        Resources resources = getResources();

        int r18_standardModule = Arrays.asList(resources.getStringArray(R.array.r18_standardModule)).indexOf(m_gearModule + "");

        if (r18_standardModule == -1) {
            resultCalculation = resultCalculation + getString(R.string.r18_moduleNotStandard) + getString(R.string.textRN);
        }
        else {
            String r18_t = Arrays.asList(resources.getStringArray(R.array.r18_t)).get(r18_standardModule);
            String r18_H = Arrays.asList(resources.getStringArray(R.array.r18_H)).get(r18_standardModule);
            String r18_B = Arrays.asList(resources.getStringArray(R.array.r18_B)).get(r18_standardModule);
            String r18_L = Arrays.asList(resources.getStringArray(R.array.r18_L)).get(r18_standardModule);
            String r18_Z = Arrays.asList(resources.getStringArray(R.array.r18_Z)).get(r18_standardModule);
            String r18_a = Arrays.asList(resources.getStringArray(R.array.r18_a)).get(r18_standardModule);
            String r18_a1 = Arrays.asList(resources.getStringArray(R.array.r18_a1)).get(r18_standardModule);
            String r18_K = Arrays.asList(resources.getStringArray(R.array.r18_K)).get(r18_standardModule);

            resultCalculation = resultCalculation + getString(R.string.r18_moduleStandard, r18_t, r18_H, r18_B, r18_L, r18_Z, r18_a, r18_a1, r18_K) + getString(R.string.textRN);
        }

        //-----------------
        //19. ?????????????????????? ?????????? ??????????????
        //-----------------

        float r19_ab = getTrunc(Math.atan(getTrunc((Math.sin(Math.toRadians(r11_av)) * Math.sin(Math.toRadians(a_angleEngagement)) * Math.cos(Math.toRadians(r1_Y))) / (Math.cos(Math.toRadians(r11_av + r1_Y)) + ( (Math.sin(Math.toRadians(a_angleEngagement)) * Math.sin(Math.toRadians(a_angleEngagement))) * Math.sin(Math.toRadians(r11_av)) * Math.sin(Math.toRadians(r1_Y)) )), 1000)), 100);
        float r19_y1 = getTrunc(( Math.atan( Math.tan(Math.toRadians(r1_Y)) * Math.sin(Math.toRadians(r11_av)))), 100);

        resultCalculation = resultCalculation + getString(R.string.r19_sharpeningAngle, r19_ab + "", r19_y1 + "") + getString(R.string.textRN);

        if (m_gearModule >= 10) {
            float r19_D = 0;
            float r19_b = 0;
            float r19_y = 0;
            float r19_yc = 0;
            if (m_gearModule >= 10 && m_gearModule <= 14) {r19_D = 35; r19_b = 7; r19_y = 11.53f;r19_yc = 13.75f;}
            else if (m_gearModule > 14 && m_gearModule <= 17) {r19_D = 50; r19_b = 10; r19_y = 11.53f;r19_yc = 13.75f;}
            else if (m_gearModule > 17 && m_gearModule <= 21) {r19_D = 60; r19_b = 13; r19_y = 12.53f;r19_yc = 14.75f;}
            else if (m_gearModule > 21 && m_gearModule <= 24) {r19_D = 70; r19_b = 15; r19_y = 12.42f;r19_yc = 14.63f;}

            resultCalculation = resultCalculation + getString(R.string.r19_bigModule, r19_D + "", r19_b + "", r19_y + "", r19_yc + "") + getString(R.string.textRN);
        }

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