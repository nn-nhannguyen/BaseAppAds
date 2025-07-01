package com.app.base.mvvm.ui.api.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.base.mvvm.R
import com.app.base.mvvm.databinding.ItemLayoutBinding
import com.app.base.mvvm.model.User
import com.bumptech.glide.Glide

class ApiAdapter(
  private val users: ArrayList<User>
) : RecyclerView.Adapter<ApiAdapter.DataViewHolder>() {

  class DataViewHolder(private val itemLayoutBinding: ItemLayoutBinding) :
    RecyclerView.ViewHolder(itemLayoutBinding.root) {
    fun bind(user: User) {
      itemLayoutBinding.apply {
        textViewUserName.text = user.name
        textViewUserEmail.text = user.email
        Glide.with(imageViewAvatar.context)
          .load(user.avatar)
          .error(R.mipmap.ic_launcher)
          .into(imageViewAvatar)
      }
    }
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
    val binding = ItemLayoutBinding.inflate(
      LayoutInflater.from(parent.context),
      parent,
      false
    )
    return DataViewHolder(binding)
  }

  override fun getItemCount(): Int = users.size

  override fun onBindViewHolder(holder: DataViewHolder, position: Int) = holder.bind(users[position])

  fun addData(list: List<User>) {
    users.addAll(list)
  }
}
