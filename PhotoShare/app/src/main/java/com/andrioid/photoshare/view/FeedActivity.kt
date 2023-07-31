package com.andrioid.photoshare.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.andrioid.myapplication.R
import com.andrioid.myapplication.databinding.ActivityFeedBinding
import com.andrioid.photoshare.model.Post
import com.andrioid.photoshare.adapter.FeedRecyclerAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FeedActivity : AppCompatActivity() {

    private lateinit var binding : ActivityFeedBinding
    private lateinit var feedRecyclerAdapter : FeedRecyclerAdapter

    private lateinit var auth : FirebaseAuth
    private lateinit var db : FirebaseFirestore
    var postList = ArrayList<Post>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_feed)
        auth = FirebaseAuth.getInstance()
        db =FirebaseFirestore.getInstance()

        getFirebaseData()

        binding.feedRecyclerView.layoutManager = LinearLayoutManager(this)

        feedRecyclerAdapter = FeedRecyclerAdapter(postList)
        binding.feedRecyclerView.adapter = feedRecyclerAdapter
    }


    fun getFirebaseData(){
        db.collection("Post").addSnapshotListener{snapshot,exception ->
            if (exception != null){
                Toast.makeText(applicationContext,exception.localizedMessage,Toast.LENGTH_LONG).show()
            }else{
                if (snapshot!=null){
                    if (!snapshot.isEmpty){
                        val documentsList = snapshot.documents

                        postList.clear()

                        for(doc in documentsList){
                            val usermail = doc.get("useremail") as String
                            val userComment = doc.get("usercomment") as String
                            val imageUrl = doc.get("imageurl") as String

                            val postExample = Post(usermail,userComment,imageUrl)
                            postList.add(postExample)
                        }
                        feedRecyclerAdapter.notifyDataSetChanged()
                    }
                }
            }



        }
    }

    //Options menu baglama
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.options_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    //menu itemi secildiğinde yapilacaklar burada tanimlanir
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId== R.id.logOut){
            auth.signOut()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        else if (item.itemId== R.id.sharePhoto){
            val intent = Intent(this, PhotoShareActivity::class.java)
            startActivity(intent)
        }

        return super.onOptionsItemSelected(item)
    }
}