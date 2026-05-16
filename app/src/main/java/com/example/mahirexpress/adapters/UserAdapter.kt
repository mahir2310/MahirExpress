package com.example.mahirexpress.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.mahirexpress.R
import com.example.mahirexpress.databinding.ItemUserBinding
import com.example.mahirexpress.models.User

class UserAdapter(
    private var users: List<User>,
    private val onUpdateRole: (User, String) -> Unit
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    inner class UserViewHolder(val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]
        val binding = holder.binding

        binding.tvUserName.text = user.name
        binding.tvUserEmail.text = user.email

        val roles = binding.root.context.resources.getStringArray(R.array.roles)
        val adapter = ArrayAdapter(binding.root.context, android.R.layout.simple_spinner_item, roles)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerRole.adapter = adapter

        val roleIndex = roles.indexOf(user.role)
        if (roleIndex >= 0) {
            binding.spinnerRole.setSelection(roleIndex)
        }

        binding.btnUpdateRole.setOnClickListener {
            val selectedRole = binding.spinnerRole.selectedItem.toString()
            onUpdateRole(user, selectedRole)
        }
    }

    override fun getItemCount() = users.size

    fun updateData(newUsers: List<User>) {
        val diffCallback = UserDiffCallback(users, newUsers)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        users = newUsers
        diffResult.dispatchUpdatesTo(this)
    }

    class UserDiffCallback(private val oldList: List<User>, private val newList: List<User>) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldList.size
        override fun getNewListSize(): Int = newList.size
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].userId == newList[newItemPosition].userId
        }
        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}
