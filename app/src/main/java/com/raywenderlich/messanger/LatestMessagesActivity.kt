package com.raywenderlich.messanger

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.raywenderlich.messanger.model.ChatMessage
import com.raywenderlich.messanger.model.User
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.chat_itemfrom.view.*
import kotlinx.android.synthetic.main.latest_message_row.view.*

class LatestMessagesActivity : AppCompatActivity() {

    val hashMap = HashMap<String, ChatMessage>()
    private var adaptor = GroupAdapter<GroupieViewHolder>()
    private lateinit var recyclerView: RecyclerView
    companion object{
        var currentUser: User? = null
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContentView(R.layout.activity_latest_messages)

        verifyUserLoggedIn()
        recyclerView = findViewById(R.id.latestRecyclerView)


        recyclerView.adapter = adaptor

        adaptor.setOnItemClickListener{item, view ->
            var row = item as LatestMessageRow
            val intent = Intent(this@LatestMessagesActivity, ChatLogActivity::class.java)
            intent.putExtra(NewMessageActivity.USER_KEY,row.partner)
            startActivity(intent)
        }
        recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))


        fetchUser()

       // setupDummyData()

        listenLatestMessages()


    }


    private fun refreshList(){

        hashMap.values.forEach{
            adaptor.add(LatestMessageRow(it))
        }
    }


    private fun listenLatestMessages(){
        val fromId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("latest_messages/$fromId")
        ref.addChildEventListener(object:ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java)?: return
                hashMap[snapshot.key!!] = chatMessage
                refreshList()
               // adaptor.add(LatestMessageRow(chatMessage))

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java)?: return
                adaptor.add(LatestMessageRow(chatMessage))
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                TODO("Not yet implemented")
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }


    private fun fetchUser(){
        var uid = FirebaseAuth.getInstance().uid
        val ref  = FirebaseDatabase.getInstance().getReference("/users/$uid")

        ref.addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                currentUser = snapshot.getValue(User::class.java)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
    fun verifyUserLoggedIn(){

        val  uid  = FirebaseAuth.getInstance().uid
        if(uid == null){
            val intent = Intent(this, RegisterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_res,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

   when(item.itemId){
        R.id.menu_new_message ->   {
        val intent  = Intent(this, NewMessageActivity::class.java )
            startActivity(intent)
        }

        R.id.menu_sign_out ->   {
            FirebaseAuth.getInstance().signOut()

                val intent = Intent(this, RegisterActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)

        }
    }
        return super.onOptionsItemSelected(item)
    }

}

class LatestMessageRow(var chatMessage:ChatMessage):Item<GroupieViewHolder>(){
  var partner: User? = null
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.latestMessageText.text = chatMessage.text

        val partnerId: String
        if (chatMessage.fromId == FirebaseAuth.getInstance().uid){
            partnerId = chatMessage.toId
            Log.d("yarrak", "fromId: ${partnerId}")
        }else{
            partnerId = chatMessage.fromId
            Log.d("yarrak", partnerId)
        }


        val ref = FirebaseDatabase.getInstance().getReference("/users/$partnerId")

        ref.addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                 partner = snapshot.getValue(User::class.java)
                viewHolder.itemView.latestMessageNameText.text = partner?.userName
                Picasso.get().load(partner?.profileImageUrl).into(viewHolder.itemView.latestPhoto)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }

    override fun getLayout(): Int {
        return R.layout.latest_message_row
    }

}