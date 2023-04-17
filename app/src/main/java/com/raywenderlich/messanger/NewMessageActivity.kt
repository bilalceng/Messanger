package com.raywenderlich.messanger


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ProgressBar
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.raywenderlich.messanger.model.User
import com.raywenderlich.messanger.recyclerViewComponents.NewMessagesAdaptor


class NewMessageActivity : AppCompatActivity(), NewMessagesAdaptor.Communicator{
    private lateinit var progressBar: ProgressBar
    private lateinit var recyclerView: RecyclerView
    private lateinit var itemArrayList: ArrayList<User>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_new_message)

         recyclerView = findViewById(R.id.NewMessagesRcyclerView)
        //val layoutManager = LinearLayoutManager(this)
        // recyclerView.layoutManager = layoutManager

        progressBar = findViewById(R.id.progress)
        title = "Select User"
        returnLatestMessageActivity()

        itemArrayList = arrayListOf()

        fetchUsers()

    }

    companion object{
        const val USER_KEY= "Name"
    }


    fun returnLatestMessageActivity() {

        var actionBar = getSupportActionBar()

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun fetchUsers() {
        val itemArrayList: ArrayList<User> = arrayListOf()
        val ref = FirebaseDatabase.getInstance().getReference("/users")
        progressBar.isVisible = true
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                itemArrayList.clear()
                if (snapshot.exists()){
                    for (item in snapshot.children){
                        val user = item.getValue(User::class.java)
                        if(user != null){
                            itemArrayList.add(user)
                        }

                    }
                    progressBar.isVisible = false
                    recyclerView.adapter = NewMessagesAdaptor(this@NewMessageActivity,itemArrayList)

                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }



    override fun onItemClicked(user:User) {
        val intent = Intent(this@NewMessageActivity, ChatLogActivity::class.java)
        intent.putExtra(USER_KEY,user)
        startActivity(intent)
        finish()
    }


}



