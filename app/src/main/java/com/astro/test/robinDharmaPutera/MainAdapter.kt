package com.astro.test.robinDharmaPutera

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class MainAdapter : RecyclerView.Adapter<MainAdapter.MainViewHolder>() {

    var userList = mutableListOf<User>()

    @SuppressLint("NotifyDataSetChanged")
    fun setUser(user: List<User>) {
        this.userList = user.toMutableList()
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clear() {
        this.userList.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {

        return MainViewHolder(
            LayoutInflater.from(
                parent.context
            ).inflate(
                R.layout.list_user,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        holder.bindLocation(userList[position])
    }

    inner class MainViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        private var view: View = v
        private var user: User? = null
//        init {
//            v.setOnClickListener{
//                if (view.context is LocationActivity) {
//                    (view.context as LocationActivity).returnActivity(location!!)
//                }
//            }
//        }

        fun bindLocation(user: User) {
            this.user = user

            view.findViewById<TextView>(R.id.username).text = user.login
            Glide.with(view.context).load(user.avatar_url)
                .placeholder(R.drawable.ic_profileplaceholder)
                .error(R.drawable.ic_profileplaceholder)
                .into(view.findViewById(R.id.thumbnail)
            )

        }

    }
}
