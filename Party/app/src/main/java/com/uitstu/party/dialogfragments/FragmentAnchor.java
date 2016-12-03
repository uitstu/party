package com.uitstu.party.dialogfragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.uitstu.party.MainActivity;
import com.uitstu.party.models.Anchor;
import com.uitstu.party.presenter.PartyFirebase;
import com.uitstu.party.services.MyService;

/**
 * Created by Huy on 11/17/2016.
 */

public class FragmentAnchor extends DialogFragment {
    EditText edtPartyName;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Dropping");

        builder.setMessage("Drop anchor here?");

        builder.setPositiveButton("DROP", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (PartyFirebase.user != null && PartyFirebase.user.curPartyID != null) {
                    Anchor anchor = new Anchor();
                    anchor.latitude = MyService.lat;
                    anchor.longitude = MyService.lng;
                    PartyFirebase.getInstant().firebaseDatabase.getReference().child("anchor").child(PartyFirebase.user.curPartyID).setValue(anchor, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            try {
                                if (databaseError == null) {
                                    Toast.makeText(MainActivity.getInstant().getApplicationContext(), "The new anchor have dropped...", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(MainActivity.getInstant().getApplicationContext(), "Your connection is weak, check it...", Toast.LENGTH_SHORT).show();
                                }
                            }
                            catch (Exception ex){

                            }
                        }
                    });
                }

            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        return builder.create();
    }
}
