package com.raywenderlich.messanger

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var passwordText:EditText
    private lateinit var emailText: EditText
    private lateinit var loginBtn: Button
    private lateinit var returnRegisterBtn :  TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        returnRegisterBtn = findViewById(R.id.returnRegister)
        loginBtn = findViewById(R.id.login)
        transactionReverse()
        loginBtn.setOnClickListener {


            emailText = findViewById(R.id.EmailText)
            passwordText = findViewById(R.id.PasswordText)



            val pass = passwordText.text.toString()
            val email = emailText.text.toString()

            if(email.isEmpty() || pass.isEmpty()){
                Toast.makeText(this, "invalid", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener {
                    val intent = Intent(this, LatestMessagesActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
                .addOnFailureListener {
                    Toast.makeText(this, "your password or email is wrong", Toast.LENGTH_LONG)
                        .show()
                }


        }
    }


    private  fun transactionReverse(){
        returnRegisterBtn.setOnClickListener {
           finish()
        }
    }
}