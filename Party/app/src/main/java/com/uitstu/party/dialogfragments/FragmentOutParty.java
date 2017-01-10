package com.uitstu.party.dialogfragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.uitstu.party.R;
import com.uitstu.party.presenter.PartyFirebase;

/**
 * Created by Huy on 11/17/2016.
 */

public class FragmentOutParty extends DialogFragment {
    EditText edtPartyName;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("RỜI NHÓM");

        builder.setMessage("Bạn đã chắc chắn?");

        builder.setPositiveButton("RỜI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                PartyFirebase.getInstant().outParty();
            }
        });
        builder.setNegativeButton("HỦY", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        return builder.create();
    }
}
