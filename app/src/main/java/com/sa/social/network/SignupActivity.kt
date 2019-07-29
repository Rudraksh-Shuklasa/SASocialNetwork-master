package com.sa.social.network

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.text.SpannableString
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.sa.social.network.viewmodel.SignupViewModel
import kotlinx.android.synthetic.main.activity_signup.*

class SignupActivity : AppCompatActivity() {
    lateinit var signupViewModel : SignupViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)

        var signUpString ="Already have an account?<font color='#504de4'>Log in</font>"
        txtLogin.setText(Html.fromHtml(signUpString))
        val ss = SpannableString(txtLogin.text.toString())

        txtLogin.setOnClickListener {
            var goToSignUp= Intent(this,LoginActivity::class.java)
            startActivity(goToSignUp)
            finish()
        }


        signupViewModel=  ViewModelProviders.of(this).get(SignupViewModel::class.java);

        signupViewModel.getCurrentUser().observe(this, Observer<Boolean> {
            if(it)
            {
                var intent=Intent(this,MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        })


        BtnSignup.setOnClickListener {
            signupViewModel.signinUser(EdtEmailSignUp.text.toString(),EdtPasswordSignup.text.toString(),this)
        }

    }

    override fun onStart() {
        super.onStart()
        var user=signupViewModel.checkUSerStatus()
        if(user!=null)
        {
            var intent=Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        }

    }
}
