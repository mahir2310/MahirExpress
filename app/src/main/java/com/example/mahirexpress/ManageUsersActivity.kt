package com.example.mahirexpress

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mahirexpress.adapters.UserAdapter
import com.example.mahirexpress.databinding.ActivityManageUsersBinding
import com.example.mahirexpress.models.User
import com.google.firebase.database.*

class ManageUsersActivity : AppCompatActivity() {

    private lateinit var binding: ActivityManageUsersBinding
    private lateinit var database: DatabaseReference
    private lateinit var adapter: UserAdapter
    private val userList = mutableListOf<User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageUsersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }

        database = FirebaseDatabase.getInstance().getReference("users")

        adapter = UserAdapter(emptyList()) { user, newRole ->
            updateUserRole(user, newRole)
        }
        binding.rvUsers.layoutManager = LinearLayoutManager(this)
        binding.rvUsers.adapter = adapter

        binding.swipeRefresh.setOnRefreshListener {
            fetchUsers()
        }

        fetchUsers()
    }

    private fun fetchUsers() {
        if (!binding.swipeRefresh.isRefreshing) {
            binding.progressBar.visibility = View.VISIBLE
        }
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (isFinishing) return
                
                val newList = mutableListOf<User>()
                for (userSnapshot in snapshot.children) {
                    val user = userSnapshot.getValue(User::class.java)
                    if (user != null) {
                        newList.add(user)
                    }
                }
                
                userList.clear()
                userList.addAll(newList)
                
                binding.progressBar.visibility = View.GONE
                binding.swipeRefresh.isRefreshing = false
                adapter.updateData(newList)
            }

            override fun onCancelled(error: DatabaseError) {
                if (isFinishing) return
                binding.progressBar.visibility = View.GONE
                binding.swipeRefresh.isRefreshing = false
                Toast.makeText(this@ManageUsersActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateUserRole(user: User, newRole: String) {
        if (user.role == newRole) return

        binding.progressBar.visibility = View.VISIBLE
        database.child(user.userId).child("role").setValue(newRole)
            .addOnCompleteListener { task ->
                binding.progressBar.visibility = View.GONE
                if (task.isSuccessful) {
                    Toast.makeText(this, "Role updated to $newRole", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Failed to update role", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
