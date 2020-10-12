package com.example.mycalender;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GetYear#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GetYear extends DialogFragment
        implements AdapterView.OnItemSelectedListener

{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private OnButtonDone onButtonDone;
    private int monthSelected =0;
    private EditText mYearTxt;
    NumberPicker np;
    NumberPicker npTens;
    NumberPicker npOnes;

    public interface OnButtonDone{
        public void OnGoButtonClicked(int year, int month);
    }

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private View view;

    public GetYear() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try{
            onButtonDone = (OnButtonDone) context;
        }catch(ClassCastException e){
           throw new ClassCastException(context.toString() + " must implement OnButtonDone");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = inflater.inflate(R.layout.fragment_get_year,null);
        Spinner sp = view.findViewById(R.id.spinner);
        np = view.findViewById(R.id.idCent);
        np.setMinValue(19);
        np.setMaxValue(21);
        npTens = view.findViewById(R.id.idYeartens);
        npTens.setMinValue(0);
        npTens.setMaxValue(9);
        npOnes = view.findViewById(R.id.idYearones);
        npOnes.setMinValue(0);
        npOnes.setMaxValue(9);
        ArrayAdapter<CharSequence>  aa= ArrayAdapter.createFromResource(getActivity(),R.array.months,R.layout.support_simple_spinner_dropdown_item);
        if (sp!=null) {
            sp.setAdapter(aa);
            sp.setOnItemSelectedListener(this);
            }
        Button button = view.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                      int year = 0;
                      int yCent = np.getValue();
                      int ytens = npTens.getValue();
                      int yOnes = npOnes.getValue();
                          year = yCent*100 +ytens*10 +yOnes;//Integer.parseInt(strYear);
                          onButtonDone.OnGoButtonClicked(year,monthSelected);
                  }
                                });

        builder.setView(view);
        return builder.create();
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        monthSelected = position;
       String msg = String.valueOf(position);

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
