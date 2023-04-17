package com.raywenderlich.messanger

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log

import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.raywenderlich.messanger.model.User
import de.hdodenhof.circleimageview.CircleImageView
import java.util.UUID

class RegisterActivity : AppCompatActivity() {

    private lateinit var userInstance: User
    private lateinit var  refDatabase: DatabaseReference
    private lateinit var ref: StorageReference
    private lateinit var fileName: String
    private  var selectedPhotoUri: Uri? = null
    private lateinit var photoAdd: Button
    private lateinit var circular: CircleImageView
    private lateinit var name: String
    private lateinit  var email: String
    private  lateinit var password: String
    private lateinit var myAuth: FirebaseAuth
    private lateinit var transactionBtn: TextView
    private lateinit var passwordEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var userNameEditText: EditText
    private lateinit var registerButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        transactionBtn = findViewById(R.id.signIn)
        passwordEditText = findViewById(R.id.userPasswordText)
        emailEditText = findViewById(R.id.userEmailText)
        userNameEditText = findViewById(R.id.userNameText)
        registerButton = findViewById(R.id.register)
        photoAdd = findViewById(R.id.photo)
        circular = findViewById(R.id.circularImage)


        transaction()

        registerButton.setOnClickListener {
            getInformation()

        }
        photoAdd.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type  = "image/*"
            startActivityForResult(intent,0)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && data != null && resultCode == Activity.RESULT_OK) {
            Log.d("RegisterActivity", "photo was selected.")
            // Uri show the address of the image that we picked from gallery.
            selectedPhotoUri = data.data
        if (selectedPhotoUri != null){
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)
            circular.setImageBitmap(bitmap)
            photoAdd.alpha = 0f
            //val bitmapDrawable = BitmapDrawable(bitmap)
            //photoAdd.setBackgroundDrawable(bitmapDrawable)
        }


        }
    }



    @SuppressLint("SuspiciousIndentation")
    private fun getInformation() {

        password = passwordEditText.text.toString()
        email = emailEditText.text.toString()
        myAuth = FirebaseAuth.getInstance()

        if(email.isEmpty() || password.isEmpty()){
            Toast.makeText(this, "invalid", Toast.LENGTH_SHORT).show()
            return
        }
                myAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {

                    if (!it.isSuccessful) return@addOnCompleteListener
                        uploadImageToFirebaseStorage()
                    Toast.makeText(this, "successful", Toast.LENGTH_SHORT).show()
                    Log.d("RegisterActivity", "successfully created with uid: " + it.result.user?.uid)

            }.addOnFailureListener {
                    Toast.makeText(this, "please enter valid password and email ", Toast.LENGTH_SHORT).show()
                    Log.d("RegisterActivity", "${it.message}")

                }

            }


    private fun transaction() {
        transactionBtn.setOnClickListener {
            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
            startActivity(intent)
        }
    }


    private fun uploadImageToFirebaseStorage(){

        if (selectedPhotoUri == null) return
        Log.d("RegisterActivity", "I am here $selectedPhotoUri")
        fileName = UUID.randomUUID().toString()
        ref = FirebaseStorage.getInstance().getReference("/images/${fileName}")
        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d("RegisterActivity" ,"the image is uploaded successfully")

                ref.downloadUrl.addOnSuccessListener {
                        Log.d("yarrrrak", "the download url is: $it")
                    saveUserToFirebaseDatabase(it)

                }
            }.addOnFailureListener{
                //do something
            }

    }


    private fun saveUserToFirebaseDatabase(profileImage: Uri){
        val uid = FirebaseAuth.getInstance().uid ?:""
        refDatabase = FirebaseDatabase.getInstance().getReference("/users/$uid")
        userInstance = User.newInstance(
            uid,
            password,
            userNameEditText.text.toString(),
            emailEditText.text.toString(),
            profileImage.toString(),
        )
        refDatabase.setValue(userInstance).addOnSuccessListener {
            Log.d("RegisterActivity", "the uid is: $uid")
            val intent = Intent(this, LatestMessagesActivity::class.java)
            //This feature delete all previous activity , so that we have chance to not turn back to register activity.
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }.addOnFailureListener {
            //do something
        }
    }

}