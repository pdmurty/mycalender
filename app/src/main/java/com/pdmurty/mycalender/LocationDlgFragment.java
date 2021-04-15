package com.pdmurty.mycalender;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

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

    }

    public void setLocselected(int locselected) {
        this.locselected = locselected;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = inflater.inflate(R.layout.location_dlg_fragment,null);
        builder.setView(view);

        return   builder.create();

    }


}

