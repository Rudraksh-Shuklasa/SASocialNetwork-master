package com.sa.social.network.views

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Html
import kotlinx.android.synthetic.main.activity_login.*
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.sa.social.network.viewmodel.LoginViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import android.widget.Toast
import com.facebook.*
import com.facebook.appevents.AppEventsLogger
import com.google.android.gms.common.api.ApiException
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.common.api.GoogleApiClient
import com.sa.social.network.R
import com.sa.social.network.base.BaseActivity


class LoginActivity : BaseActivity() {
    lateinit var loginViewModel : LoginViewModel
    var TAG = this.javaClass.simpleName
    private val RC_SIGN_IN = 1
    var doubleBackToExitPressedOnce = false
    lateinit var mGoogleSignInClient: GoogleSignInClient
    private var mCallbackManager: CallbackManager? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // facebook init
        FacebookSdk.sdkInitialize(getApplicationContext())
        AppEventsLogger.activateApp(applicationContext)

        setContentView(R.layout.activity_login)

        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)

        var signUpString ="Dont have an account?<font color='#504de4'> Sgn Up</font>"
        txtSignup.setText(Html.fromHtml(signUpString))


        loginViewModel=  ViewModelProviders.of(this).get(LoginViewModel::class.java)


        //Google Login
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val gApiClient = GoogleApiClient.Builder(this)
            .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);



        mCallbackManager = CallbackManager.Factory.create();


        BtnFacebookLogin.setReadPermissions("email", "public_profile")





        BtnFacebookLogin.registerCallback(mCallbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                Log.d(TAG, "facebook:onSuccess:$loginResult")
                loginViewModel.handleFacebookAccessToken(loginResult.accessToken,this@LoginActivity)
            }

            override fun onCancel() {
                Log.d(TAG, "facebook:onCancel")
            }

            override fun onError(error: FacebookException) {
                Log.d(TAG, "facebook:onError", error)

            }
        })


        BtnGmailLogin.setOnClickListener {
            val signInIntent = mGoogleSignInClient.getSignInIntent()
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }

        loginViewModel.getCurrentUser().observe(this, Observer<Boolean> {
            if(it)
            {
                PrgLoginProcess.visibility=View.GONE

                if(gApiClient.isConnected){
                    gApiClient.clearDefaultAccountAndReconnect().await()
                }
                var intent=Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        })

        BtnLogin.setOnClickListener {
            if(EdtEmailLogin.text.toString().isEmpty() || EdtPasswordLogin.text.isEmpty())
            {
                Toast.makeText(this,"Please Enter All The Fields",Toast.LENGTH_SHORT).show()
            }
            else{
                loginViewModel.loginUser(EdtEmailLogin.text.toString(),EdtPasswordLogin.text.toString(),this)
                PrgLoginProcess.visibility=View.VISIBLE
            }

        }

        txtSignup.setOnClickListener {
            var goToSignUp= Intent(this, SignupActivity::class.java)
            startActivity(goToSignUp)
            finish()

        }

    }





    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //handling facebook call back
        mCallbackManager!!.onActivityResult(requestCode, resultCode, data);

        //handlig Google Call back
        if (requestCode == RC_SIGN_IN) {

            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {

                val account = task.getResult(ApiException::class.java)
                PrgLoginProcess.visibility=View.VISIBLE
                loginViewModel.firebaseAuthWithGoogle(account!!,this)
            } catch (e: ApiException) {
                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
            }

        }
    }
    override fun onStart() {
        super.onStart()

    }

    override fun onBackPressed() {

            if (doubleBackToExitPressedOnce) {
                super.onBackPressed()
                return
            }

            this.doubleBackToExitPressedOnce = true
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()

            Handler().postDelayed(Runnable { doubleBackToExitPressedOnce = false }, 2000)

    }


}
