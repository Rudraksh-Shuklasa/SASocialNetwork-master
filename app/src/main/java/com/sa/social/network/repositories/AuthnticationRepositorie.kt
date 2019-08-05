package com.sa.social.network.repositories

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.sa.social.network.utils.SharedPrefrenceUtils
import com.facebook.AccessToken
import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore
import com.sa.social.network.model.User


///this repo containt all function regardin Authntication

class AuthnticationRepositorie(context: Context){


//    Authntication Type 0 : Email,PasssWord,1:Gmail,2:FaceBook

    private var mContext : Context
    private var fireDbInstance : FirebaseFirestore
    private var TAG =this.javaClass.simpleName
    private var sharedPrefManager= SharedPrefrenceUtils.Companion.getSharefPrefrenceManager(context)
    private var editor= sharedPrefManager!!.edit()
    private var currentUSer= MutableLiveData<Boolean>()



    init {
        mContext=context
        currentUSer.postValue(false)
        fireDbInstance = FirebaseFirestore.getInstance()

    }
    var mAuth=FirebaseAuth(FirebaseApp.initializeApp(context));

    fun checkUserIsLogin(): FirebaseUser? {
        mAuth= FirebaseAuth.getInstance()
        var user=mAuth.currentUser
        return user
    }

    fun signinUser(email:String,password:String,activity: Activity)
    {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(activity) { task ->
            if(task.isSuccessful){
                 if(mAuth.currentUser != null)
                 {
                     editor.putInt(SharedPrefrenceUtils.AuthnticationType,0)
                     editor.commit()
                     currentUSer.postValue(true)
                     addUserToFireStore(mAuth.currentUser!!)
                 }
            }else{
                Toast.makeText(mContext,task.exception!!.message,Toast.LENGTH_SHORT).show()
            }
        }

    }

    fun getcurrentUserLiveData(): MutableLiveData<Boolean> {
        return currentUSer
    }

    fun loginUser(email:String ,password: String,activity: Activity)
    {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(activity) { task ->
            if(task.isSuccessful){
                if(mAuth.currentUser != null)
                {
                    currentUSer.postValue(true)
                    editor.putBoolean(SharedPrefrenceUtils.Companion.IsLogin,true)
                    editor.putBoolean(SharedPrefrenceUtils.IsLogin,true)
                    editor.putString(SharedPrefrenceUtils.CurrentUserId, mAuth.currentUser!!.uid)
                    editor.putString(SharedPrefrenceUtils.CurrentUserName, mAuth.currentUser!!.displayName)
                    editor.commit()

                }
            }else{
                Toast.makeText(mContext,task.exception!!.message,Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun firebaseAuthWithGoogle(account: GoogleSignInAccount,activity: Activity)
    {
        Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId())

        //getting the auth credential
        val credential = GoogleAuthProvider.getCredential(account.getIdToken(), null)

        //Now using firebase we are signing in the user here
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(activity, OnCompleteListener<AuthResult> { task ->
                if (task.isSuccessful) {
                    if(mAuth.currentUser != null)
                    {
                        currentUSer.postValue(true)
                        editor.putInt(SharedPrefrenceUtils.AuthnticationType,1)
                        editor.commit()
                        addUserToFireStore(mAuth.currentUser!!)
                    }
                } else {
                    Toast.makeText(
                        activity, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()

                }
            })

    }

    fun handleFacebookAccessToken(token: AccessToken,activity: Activity) {
        Log.d(TAG, "handleFacebookAccessToken:$token")

        val credential = FacebookAuthProvider.getCredential(token.token)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(activity, object : OnCompleteListener<AuthResult> {
                override fun onComplete(task: Task<AuthResult>) {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success")
                        val user = mAuth.getCurrentUser()
                        if (task.isSuccessful) {
                            if (mAuth.currentUser != null) {
                                currentUSer.postValue(true)
                                editor.putInt(SharedPrefrenceUtils.AuthnticationType,2)
                                editor.commit()

                                addUserToFireStore(mAuth.currentUser!!)
                            }
                        }
                    }else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.getException())
                        Toast.makeText(
                            activity, "Authentication failed.",
                            Toast.LENGTH_SHORT
                        ).show()

                    }
                }
            })
}


    fun addUserToFireStore(user:FirebaseUser){
        editor.putBoolean(SharedPrefrenceUtils.IsLogin,true)
        editor.putString(SharedPrefrenceUtils.CurrentUserId, mAuth.currentUser!!.uid)
        editor.putString(SharedPrefrenceUtils.CurrentUserName, mAuth.currentUser!!.displayName)
        editor.commit()
        var curretUserInfor=User(user.email!!, user.displayName!!,user.metadata!!.creationTimestamp,user.uid,"")

        fireDbInstance.collection("User").document(curretUserInfor.userId).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result
                if (document!!.exists()) {
                    Log.d(TAG, "User is exists!")
                } else {
                    fireDbInstance.collection("User")
                        .document(curretUserInfor!!.userId)
                        .set(curretUserInfor)
                        .addOnSuccessListener {
                            Log.d(TAG,user.toString())
                        }
                        .addOnFailureListener {
                            Log.d(TAG,it.message)
                        }
                }
            } else {
                Log.d(TAG, "Failed with: ", task.exception)
            }
        }

    }

}