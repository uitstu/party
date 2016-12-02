package com.uitstu.party.presenter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;

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
import com.uitstu.party.fragments.FragmentDrawer;
import com.uitstu.party.fragments.FragmentMap;
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

        eventPartyMembers = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                User usr = dataSnapshot.getValue(User.class);
                usr.UID = dataSnapshot.getKey();

                try{
                    MemberAvatars.getInstant().putToList(usr.UID, usr.urlAvatar);
                }
                catch (Exception e){

                }

                users.add(usr);

                if (map != null)
                    map.updateMembers(users);

                Log.i("huy11","huy11");
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                User usr = dataSnapshot.getValue(User.class);
                usr.UID = dataSnapshot.getKey();

                for (int i=users.size()-1; i>=0; i--){
                    if (users.get(i).UID.equals(usr.UID)){
                        try {
                            if (users.get(i).urlAvatar.equals(usr.urlAvatar)) {
                                try {
                                    MemberAvatars.getInstant().putToList(usr.UID, usr.urlAvatar);
                                } catch (Exception e) {

                                }

                                users.add(usr);
                            }
                        }
                        catch (Exception e){

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

                for (int i=users.size()-1; i>=0; i--){
                    if (users.get(i).UID.equals(usr.UID)){
                        users.remove(i);

                        try {
                            MemberAvatars.getInstant().remove(usr.UID);
                        } catch (Exception e) {

                        }

                        users.add(usr);
                    }
                }

                map.updateMembers(users);

                Log.i("huy13","huy13");
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        if (eventUserInfo == null){
            eventUserInfo = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User usr = dataSnapshot.getValue(User.class);
                    if (usr == null)
                        usr = new User();
                    usr.UID = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    if (usr == null)
                        Log.i("huy11j1","dung la moi vo null22");
                    if (FirebaseAuth.getInstance() == null)
                        Log.i("huy11j1","dung la moi vo null11");
                    Log.i("huy11j1","dung la moi vo null33");

                    if (user == null || user.curPartyID == null || user.curPartyID.equals("")){
                        user = new User();
                        user.latitude = usr.latitude;
                        user.longitude = usr.longitude;
                        user.vehicle = usr.vehicle;
                        user.maxVelocity = usr.maxVelocity;
                        user.urlAvatar = usr.urlAvatar;
                        user.name = usr.name;
                        user.UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        if (usr != null && !usr.curPartyID.equals("")){
                            joinParty(usr.curPartyID);
                            Log.i("huy11j","huy11j");
                        }
                    }
                    else if (user != null && user.curPartyID != null && !user.curPartyID.equals("") && !user.curPartyID.equals(usr.curPartyID)) {
                        joinParty(usr.curPartyID);
                        Log.i("huy11j","huy11j");
                    }

/*
                    if (usr != null) {

                        usr.UID = FirebaseAuth.getInstance().getCurrentUser().getUid();

                        if (user == null) {

                            //if (usr.curPartyID != null){
                            //    joinParty(usr.curPartyID);
                            //}

                            user = new User();
                            user.UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            joinParty(usr.curPartyID);
                        }
                        else{
                            if (usr.curPartyID!=null && !usr.curPartyID.equals("") && (!usr.curPartyID.equals(user.curPartyID) ))
                                joinParty(usr.curPartyID);
                        }

                        //user = usr;


                        //if (usr.curPartyID!=null && !usr.curPartyID.equals("") && (!usr.curPartyID.equals(user.curPartyID) ))
                            //joinParty(usr.curPartyID);


                        if (usr.curPartyID!=null && usr.curPartyID.equals(""))
                            FragmentMap.getInstant().updateFabs(false);
                        else
                            FragmentMap.getInstant().updateFabs(true);


                    }
                    else {
                        usr = new User();
                        usr.UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    }

                    user = usr;
*/

/*
                    if (!usr.curPartyID.equals("") &&  !usr.curPartyID.equals(PARTY_ID)){
                        PARTY_ID = usr.curPartyID;
                        joinParty(usr.curPartyID);
                    }
*/

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
    }

    public void createUserWithEmailAndPassword(IRegister activity, String email, String password){
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener((Activity) activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Log.i("PartyFirebase: ",""+task.getException());
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
                            Log.i("PartyFirebase: ",""+task.getException());
                        }
                        String exString = "";
                        if (task.getException() != null)
                            exString += task.getException().toString();
                        //
                        removeFirebaseListener();
                        setNull();
                        getInstant();
                        //
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
        Log.i("huy11j","huy11j ----- do no ko goi");
        String strCurParty = "";
        if (user.curPartyID != null)
            strCurParty = new String(user.curPartyID);

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

                    users.clear();

                    try {
                        partyMembers.removeEventListener(eventPartyMembers);
                    }
                    catch (Exception e){

                    }

                    //partyMembers = firebaseDatabase.getReference().child("parties").child(partyID).child("members");
                    partyMembers = firebaseDatabase.getReference().child("parties").child(partyID).child("members");
                    partyMembers.addChildEventListener(eventPartyMembers);
                    Log.i("huy11j","huy11j ---- da add event");
                    if (eventPartyMembers == null)
                        Log.i("huy11j","huy11j ---- nhung rat tiec null");
                }
                else {
                    Log.i("huy11j","huy11j ---- ko thanh cong");
                }
            }
        });

    }

    public void outParty(){

        String strCurParty = new String(user.curPartyID);
        user.curPartyID = "";

        Log.i("shit","shit: "+strCurParty+" / "+user.curPartyID);

        Map<String, Object> postValues = user.toMap();
        Map<String, Object> childUpdates = new HashMap<>();

        childUpdates.put("users/"+user.UID,postValues);
        if (!strCurParty.equals("")){
            childUpdates.put("parties/"+strCurParty+"/members/"+user.UID,null);
            Log.i("shit","shit: "+"parties/"+strCurParty+"/members/"+user.UID);
        }

        firebaseDatabase.getReference().updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError == null){

                    users.clear();
                    FragmentMap.getInstant().updateMembers(users);

                    try {
                        partyMembers.removeEventListener(eventPartyMembers);
                    }
                    catch (Exception e){

                    }

                    //partyMembers = firebaseDatabase.getReference().child("parties").child(partyID).child("members");
                    //partyMembers.addChildEventListener(eventPartyMembers);
                }
            }
        });
    }

    public boolean updateUserDataToFirebase(){
/*
        firebaseDatabase.getReference().child("parties").child(user.curPartyID).child("members").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(PartyFirebase.user);
        firebaseDatabase.getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(PartyFirebase.user);
*/
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
