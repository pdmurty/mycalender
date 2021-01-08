package com.example.mycalender;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class LocationDlgFragment extends DialogFragment {
    private LocationDone locationDone;
    private int locselected=0;
    public interface LocationDone{
        void OnLocationSelected(int locSelection);
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
/*
        try{
            locationDone = (LocationDlgFragment.LocationDone) context;
        }catch(ClassCastException e){
            throw new ClassCastException(context.toString() + " must implement LocationDone");
        }

 */
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = inflater.inflate(R.layout.location_dlg_fragment,null);
        builder.setView(view);
        return builder.create();

    }

    public void onButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.locCur:
                locselected=1;
                    // Pirates are the best
                    break;
            case R.id.locDb:
                locselected=2;
                    // Ninjas rule
                    break;
            case R.id.locMap:
                locselected=3;
                    // Ninjas rule
                    break;
            case R.id.ok:
                   locationDone.OnLocationSelected(locselected);
                    break;
            case R.id.cancel:
                locationDone.OnLocationSelected(0);
                    break;
        }
    }
}

