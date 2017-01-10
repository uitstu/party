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

public class FragmentCreateParty extends DialogFragment {
    EditText edtPartyName;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View rootView = inflater.inflate(R.layout.dialog_fragment_create_party, null);

        edtPartyName = (EditText) rootView.findViewById(R.id.edtPartyName);

        builder.setTitle("Tạo nhóm");
        builder.setView(rootView);

        builder.setPositiveButton("TẠO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                PartyFirebase.getInstant().createParty(edtPartyName.getText().toString());
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
