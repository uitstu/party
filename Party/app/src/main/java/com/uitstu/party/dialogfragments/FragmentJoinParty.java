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

public class FragmentJoinParty extends DialogFragment {

    EditText edtPartyCode;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View rootView = inflater.inflate(R.layout.dialog_fragment_join_party, null);

        edtPartyCode = (EditText) rootView.findViewById(R.id.edtPartyCode);

        builder.setTitle("Tham gia nhóm");
        builder.setView(rootView);

        builder.setPositiveButton("THAM GIA", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                PartyFirebase.getInstant().joinParty(edtPartyCode.getText().toString());
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
