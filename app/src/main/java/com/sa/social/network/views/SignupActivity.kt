package com.sa.social.network.views

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Html
import android.text.SpannableString
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.sa.social.network.R
import com.sa.social.network.base.BaseActivity
import com.sa.social.network.viewmodel.SignupViewModel
import kotlinx.android.synthetic.main.activity_signup.*

class SignupActivity : BaseActivity() {
    var doubleBackToExitPressedOnce = false
    lateinit var signupViewModel : SignupViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)

        var signUpString ="Already have an account?<font color='#504de4'>Log in</font>"
        txtLogin.setText(Html.fromHtml(signUpString))
        val ss = SpannableString(txtLogin.text.toString())

        txtLogin.setOnClickListener {
            var goToSignUp= Intent(this, LoginActivity::class.java)
            startActivity(goToSignUp)
            finish()
        }


        signupViewModel=  ViewModelProviders.of(this).get(SignupViewModel::class.java);

        signupViewModel.getCurrentUser().observe(this, Observer<Boolean> {
            if(it)
            {
                PrgSignupProcess.visibility= View.GONE
                var intent=Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        })


        BtnSignup.setOnClickListener {
            signupViewModel.signinUser(EdtEmailSignUp.text.toString(),EdtPasswordSignup.text.toString(),this)
            PrgSignupProcess.visibility= View.VISIBLE
        }

    }

    override fun onStart() {
        super.onStart()
        var user=signupViewModel.checkUSerStatus()
        if(user!=null)
        {
            var intent=Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

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
