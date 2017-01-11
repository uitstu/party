package com.uitstu.party.presenter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.uitstu.party.MainActivity;
import com.uitstu.party.fragments.FragmentDrawer;
import com.uitstu.party.fragments.FragmentMap;
import com.uitstu.party.models.Anchor;
import com.uitstu.party.models.Tracking;
import com.uitstu.party.models.User;
import com.uitstu.party.presenter.interfaces.ILogin;
import com.uitstu.party.presenter.interfaces.IRegister;
import com.uitstu.party.presenter.interfaces.IUpdateMap;
import com.uitstu.party.supports.MemberAvatars;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Created by Huy on 11/10/2016.
 */

public class PartyFirebase {
    public static IUpdateMap map;

    public static String PARTY_ID;
    public static String USER_ID = "";

    private static PartyFirebase partyFirebase;
    public static PartyFirebase getInstant(){
        if (partyFirebase == null)
            partyFirebase = new PartyFirebase();
        return partyFirebase;
    }
    public static void setNull(){
        partyFirebase = null;
    }

    public FirebaseAuth firebaseAuth;
    public FirebaseDatabase firebaseDatabase;

    public DatabaseReference userInfo;
    public ValueEventListener eventUserInfo;
    public static User user;

    public DatabaseReference partyName;
    public ValueEventListener eventPartyName;

    public DatabaseReference partyChat;
    public ValueEventListener eventPartyChatting;

    public DatabaseReference partyMembers;
    public ChildEventListener eventPartyMembers;
    public static ArrayList<User> users;

    public static DatabaseReference AnchorReference;
    public static ValueEventListener eventAnchor;
    public static Anchor anchor;

    //listener
    private FirebaseAuth.AuthStateListener firebaseAuthListener;

    public PartyFirebase(){
        PARTY_ID = "";
        users = new ArrayList<User>();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        setFirebaseListener();
        addFirebaseListener();

    }

    //set listener
    public void setFirebaseListener(){
        if (eventPartyMembers == null) {
            eventPartyMembers = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    User usr = dataSnapshot.getValue(User.class);
                    usr.UID = dataSnapshot.getKey();

                    if (Tracking.getInstant().type == Tracking.MEMBER){
                        if (Tracking.getInstant().user != null && Tracking.getInstant().user.UID.equals(usr.UID))
                            Tracking.getInstant().setLatLng(usr.latitude, usr.longitude);
                    }

                    users.add(usr);

                    if (map != null)
                        map.updateMembers(users);

                    try {
                        MainActivity.getInstant().loadBitmap(usr);
                    } catch (Exception e) {

                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    User usr = dataSnapshot.getValue(User.class);
                    usr.UID = dataSnapshot.getKey();

                    if (usr.UID.equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                        return;

                    if (Tracking.getInstant().user != null){
                        Log.i("huycuoi","huycuoi: thay roi chua???"+Tracking.getInstant().user.UID+" ---- "+usr.UID);
                    }

                    if (Tracking.getInstant().type == Tracking.MEMBER && Tracking.getInstant().isTracking){
                        if (Tracking.getInstant().user != null && Tracking.getInstant().user.UID.equals(usr.UID)) {
                            Tracking.getInstant().setLatLng(usr.latitude, usr.longitude);
                            Tracking.getInstant().user = usr;
                            //map.updateMembers(users);
                        }
                    }

                    for (int i = users.size() - 1; i >= 0; i--) {
                        if (users.get(i).UID.equals(usr.UID)) {
                            try {
                                if (users.get(i).urlAvatar.equals(usr.urlAvatar)) {
                                    // aa users.add(usr);
                                }
                            } catch (Exception e) {

                            }

                            users.set(i, usr);
                        }
                    }

                    if (map != null) {
                        map.updateMembers(users);
                    }
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    User usr = dataSnapshot.getValue(User.class);
                    usr.UID = dataSnapshot.getKey();

                    if (usr.UID.equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                        return;

                    if (Tracking.getInstant().type == Tracking.MEMBER){
                        if (Tracking.getInstant().user != null && Tracking.getInstant().user.UID.equals(usr.UID))
                            Tracking.getInstant().user = null;
                    }

                    for (int i = users.size() - 1; i >= 0; i--) {
                        if (users.get(i).UID.equals(usr.UID)) {
                            users.remove(i);

                            try {
                                MemberAvatars.getInstant().remove(usr.UID);
                            } catch (Exception e) {

                            }

                            //users.add(usr);
                        }
                    }

                    map.updateMembers(users);
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
        }

        if (eventUserInfo == null){
            eventUserInfo = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User usr = dataSnapshot.getValue(User.class);
                    if (usr == null)
                        usr = new User();
                    usr.UID = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    if (user == null || user.curPartyID == null || user.curPartyID.equals("")){
                        user = new User();
                        user.latitude = usr.latitude;
                        user.longitude = usr.longitude;
                        user.vehicle = usr.vehicle;
                        user.maxVelocity = usr.maxVelocity;
                        user.urlAvatar = usr.urlAvatar;
                        user.name = usr.name;
                        user.status = usr.status;
                        user.UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        if (usr != null && !usr.curPartyID.equals("")){
                            joinParty(usr.curPartyID);
                        }
                    }
                    else if (user != null && user.curPartyID != null && !user.curPartyID.equals("") && !user.curPartyID.equals(usr.curPartyID)) {
                        joinParty(usr.curPartyID);
                    }

                    if (usr != null && usr.curPartyID != null && !usr.curPartyID.equals("")){
                        FragmentMap.getInstant().layoutShareParty.setVisibility(View.VISIBLE);
                        FragmentMap.getInstant().updatePartyCode(usr.curPartyID);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
        }


        if (firebaseAuthListener == null) {
            firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    if (firebaseAuth.getCurrentUser() != null) {
                        userInfo = firebaseDatabase.getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        userInfo.addValueEventListener(eventUserInfo);
                    }
                }
            };
        }

        if (eventAnchor == null){
            eventAnchor = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    anchor = dataSnapshot.getValue(Anchor.class);
                    if (anchor != null){
                        if (Tracking.getInstant().type == Tracking.ANCHOR){
                            Tracking.getInstant().setLatLng(anchor.latitude, anchor.longitude);
                        }

                        FragmentMap.getInstant().updateMembers(users);

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
        }

        if (eventPartyName == null){
            eventPartyName = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String partyName = dataSnapshot.getValue(String.class);
                    FragmentMap.getInstant().layoutShareParty.setVisibility(View.VISIBLE);
                    FragmentMap.getInstant().updatePartyName(partyName);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
        }
    }

    //add listener
    public void addFirebaseListener(){
        if (firebaseAuth != null && firebaseAuthListener != null)
            firebaseAuth.addAuthStateListener(firebaseAuthListener);
    }

    //remove listener
    public void removeFirebaseListener(){
        if (firebaseAuth != null && firebaseAuthListener != null)
            firebaseAuth.removeAuthStateListener(firebaseAuthListener);

        if (userInfo != null && eventUserInfo != null)
            userInfo.removeEventListener(eventUserInfo);

        if (partyMembers != null && eventPartyMembers != null)
            partyMembers.removeEventListener(eventPartyMembers);

        if (AnchorReference != null && eventAnchor != null)
            AnchorReference.removeEventListener(eventAnchor);

        if (partyName != null && eventPartyName!= null){
            partyName.removeEventListener(eventPartyName);
        }
    }

    public void createUserWithEmailAndPassword(IRegister activity, String email, String password){
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener((Activity) activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                        }
                    }
                });
    }

    public void signInWithEmailAndPassword(final Activity activity, String email, String password){
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                        }
                        String exString = "";
                        if (task.getException() != null)
                            exString += task.getException().toString();

                        ((ILogin)activity).onLogin(exString);
                    }
                });
    }

    public void createParty(final String name){

        String strCurParty = new String(user.curPartyID);
        user.curPartyID = "";

        Map<String, Object> postValues = user.toMap();
        Map<String, Object> childUpdates = new HashMap<>();

        childUpdates.put("users/"+user.UID,postValues);
        if (!strCurParty.equals("")){
            childUpdates.put("parties/"+strCurParty+"/members/"+user.UID,null);
        }

        firebaseDatabase.getReference().updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError == null){
                    //
                    anchor = null;
                    FragmentMap.getInstant().updateMembers(users);
                    //

                    users.clear();

                    try {
                        partyMembers.removeEventListener(eventPartyMembers);
                    }
                    catch (Exception e){

                    }

                    final DatabaseReference push = firebaseDatabase.getReference("parties").push();
                    push.child("name").setValue(name, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError == null){
                                joinParty(push.getKey());
                            }
                        }
                    });
                }
            }
        });

    }

    public void joinParty(final String partyID){
        firebaseDatabase.getReference().child("parties").child(partyID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null){
                    String strCurParty = "";
                    if (user.curPartyID != null)
                        strCurParty = new String(user.curPartyID);
                    //
                    anchor = null;
                    FragmentMap.getInstant().updateMembers(users);
                    //
                    user.curPartyID = partyID;

                    Map<String, Object> postValues = user.toMap();
                    Map<String, Object> childUpdates = new HashMap<>();

                    childUpdates.put("users/"+user.UID,postValues);
                    if (!strCurParty.equals(""))
                        childUpdates.put("parties/"+strCurParty+"/members/"+user.UID,null);
                    childUpdates.put("parties/"+partyID+"/members/"+user.UID,postValues);
                    firebaseDatabase.getReference().updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError == null){

                                FragmentMap.getInstant().updateFabs(true);

                                users.clear();

                                try {
                                    partyMembers.removeEventListener(eventPartyMembers);
                                }
                                catch (Exception e){

                                }

                                partyMembers = firebaseDatabase.getReference().child("parties").child(partyID).child("members");

                                DatabaseReference statusOnOff = firebaseDatabase.getReference().child("parties").child(user.curPartyID).child("members").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                statusOnOff.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        User usr = dataSnapshot.getValue(User.class);
                                        DatabaseReference statusOO = firebaseDatabase.getReference().child("parties").child(user.curPartyID).child("members").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                        if (usr != null){
                                            statusOO.child("status").setValue("online");
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                                partyMembers.addChildEventListener(eventPartyMembers);

                                try{
                                    AnchorReference.removeEventListener(eventAnchor);
                                }
                                catch (Exception e){

                                }
                                AnchorReference = firebaseDatabase.getReference().child("anchor").child(user.curPartyID);
                                AnchorReference.addValueEventListener(eventAnchor);

                                //

                                try {
                                    partyName.removeEventListener(eventPartyName);
                                }
                                catch (Exception e){

                                }
                                partyName = firebaseDatabase.getReference().child("parties").child(partyID).child("name");
                                partyName.addValueEventListener(eventPartyName);
                            }
                            else {

                            }
                        }
                    });
                }
                else{
                    MainActivity.getInstant().showToast("Mã nhóm không tồn tại");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }

    public void updateOnlineStatus(){

        if (user != null && user.curPartyID != null && !user.curPartyID.equals("")){
            DatabaseReference statusOnOff = firebaseDatabase.getReference().child("parties").child(user.curPartyID).child("members").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            statusOnOff.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User usr = dataSnapshot.getValue(User.class);
                    DatabaseReference statusOO = firebaseDatabase.getReference().child("parties").child(user.curPartyID).child("members").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("status");
                    if (usr != null){
                        statusOO.onDisconnect().setValue("offline");
                        statusOO.setValue("online");
                    }
                    else{

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    public void outParty(){

        String strCurParty = new String(user.curPartyID);
        user.curPartyID = "";

        Map<String, Object> postValues = user.toMap();
        Map<String, Object> childUpdates = new HashMap<>();

        childUpdates.put("users/"+user.UID,postValues);
        if (!strCurParty.equals("")){
            childUpdates.put("parties/"+strCurParty+"/members/"+user.UID,null);
        }

        firebaseDatabase.getReference().updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError == null){

                    users.clear();
                    FragmentMap.getInstant().updateMembers(users);

                    FragmentMap.getInstant().updateFabs(false);

                    try {
                        partyMembers.removeEventListener(eventPartyMembers);
                    }
                    catch (Exception e){

                    }

                    FragmentMap.getInstant().setPartyInfoInVisible();

                    anchor = null;
                    FragmentMap.getInstant().updateMembers(users);
                }
            }
        });
    }

    public boolean updateUserDataToFirebase(){
        Map<String, Object> postValues = user.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("users/"+user.UID,postValues);
        childUpdates.put("parties/"+user.curPartyID+"/members/"+user.UID,postValues);

        firebaseDatabase.getReference().updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError == null){

                }
            }
        });

        return true;
    }
}
