package com.raywenderlich.messanger

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.raywenderlich.messanger.model.ChatMessage
import com.raywenderlich.messanger.model.User
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.chat_itemfrom.view.*
import kotlinx.android.synthetic.main.chat_itemto.view.*
import kotlinx.coroutines.currentCoroutineContext




class ChatLogActivity : AppCompatActivity() {
     val TAG = "RegisterActivity"
    var adaptor = GroupAdapter<GroupieViewHolder>()
    private lateinit var recyclerView : RecyclerView
    private lateinit var fromMessageList: MutableList<ChatMessage>
    private lateinit var sendButton: Button
    private lateinit var sendMessage: EditText
    private lateinit var user: User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)
        recyclerView = findViewById(R.id.recyclerItem)
        recyclerView.adapter = adaptor
        title = "Cheat chat"
        returnLatestMessageActivity()

        user = intent.getParcelableExtra(NewMessageActivity.USER_KEY)?: User()
        supportActionBar?.title = user.userName
        sendMessage = findViewById(R.id.sendMessage)

        sendButton = findViewById(R.id.button2)

        sendButton.setOnClickListener {
            performSendMessage()
        }

        fromMessageList = mutableListOf()

    listenForMessages()
    }

    fun returnLatestMessageActivity() {

        var actionBar = getSupportActionBar()

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun performSendMessage(){
        val text = sendMessage.text.toString()
        val fromId = FirebaseAuth.getInstance().uid
        val toId = user.uid
        val reference = FirebaseDatabase.getInstance().getReference("/user_messages/$fromId/$toId").push()
        val toReference = FirebaseDatabase.getInstance().getReference("/user_messages/$toId/$fromId").push()
        val chatMessage = ChatMessage(reference.key!!, text ,fromId!!,toId, System.currentTimeMillis() / 1000)

        reference.setValue(chatMessage).addOnSuccessListener {
            Log.d(TAG,"the message sent : ${chatMessage.text}")
            sendMessage.text.clear()
            recyclerView.scrollToPosition(adaptor.itemCount - 1)
        }.addOnFailureListener {

        }
        toReference.setValue(chatMessage)

        val latestFromMessageRef = FirebaseDatabase.getInstance().getReference("latest_messages/$fromId/$toId")
        val latestToMessageRef = FirebaseDatabase.getInstance().getReference("latest_messages/$toId/$fromId")

        latestFromMessageRef.setValue(chatMessage)
        latestToMessageRef.setValue(chatMessage)
    }

    private fun listenForMessages(){
        val fromId = FirebaseAuth.getInstance().uid
        val toId = user.uid
        val ref = FirebaseDatabase.getInstance().getReference("/user_messages/$fromId/$toId")

        ref.addChildEventListener(object :ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
               val chatMessage = snapshot.getValue(ChatMessage::class.java)
                Log.d(TAG, chatMessage!!.text)
                val currentUser = LatestMessagesActivity.currentUser
                if(chatMessage.fromId == FirebaseAuth.getInstance().uid){
                    adaptor.add(ChatFromItem(chatMessage.text, currentUser!!))

                }
                else{

                    adaptor.add(ChatToItem(chatMessage.text, user))
                }
                val downButton = findViewById<Button>(R.id.down)
                downButton.alpha = 0.7f
                downButton.setOnClickListener {
                    recyclerView.scrollToPosition(adaptor.itemCount - 1)
                }

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
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


}
class  ChatFromItem(var text: String,currentUser:User): Item<GroupieViewHolder>(){
    val uri = currentUser.profileImageUrl
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {

        viewHolder.itemView.chatItemTextView2.text = text
        Picasso.get().load(uri).into(viewHolder.itemView.chatMessageProfile2)


    }

    override fun getLayout(): Int {
       return R.layout.chat_itemfrom
    }


}

class ChatToItem(var text: String ,user:User): Item<GroupieViewHolder>(){
    var uri = user.profileImageUrl
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
       viewHolder.itemView.chatItemTextView.text =  text
        Picasso.get().load(uri).into(viewHolder.itemView.chatMessageProfile)
    }

    override fun getLayout(): Int {
        return R.layout.chat_itemto
    }

}