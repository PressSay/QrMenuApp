package com.example.qfmenu.util

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.qfmenu.R
import com.example.qfmenu.database.entity.AccountDb
import com.example.qfmenu.viewmodels.SaveStateViewModel

class MemberAdapter(
     private val context: Context,
     private val saveStateViewModel: SaveStateViewModel
) : ListAdapter<AccountDb , MemberAdapter.ItemViewHolder>(
     DiffCallback
) {
     companion object {
          private val DiffCallback = object : DiffUtil.ItemCallback<AccountDb>() {
               override fun areItemsTheSame(oldItem: AccountDb, newItem: AccountDb): Boolean {
                    return oldItem === newItem
               }

               override fun areContentsTheSame(oldItem: AccountDb, newItem: AccountDb): Boolean {
                    return oldItem.id == newItem.id
               }
          }
     }

     class ItemViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
          private val itemsMemberAdmin = view.findViewById<LinearLayout>(R.id.itemsMemberAdmin) as ViewGroup
          val memberId = itemsMemberAdmin.getChildAt(0) as TextView
          val btnMember = itemsMemberAdmin.getChildAt(1) as AppCompatImageButton
     }

     override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
          val adapter = LayoutInflater.from(parent.context).inflate(R.layout.item_member_admin, parent, false)
          return ItemViewHolder(
               adapter
          )
     }
     override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
          val item = currentList[position]
          "member Id: ${item.id}".also { holder.memberId.text = it }
          holder.btnMember.setOnClickListener {
               saveStateViewModel.stateAccountDb = item
               holder.view.findNavController().navigate(R.id.action_memberFragment_to_memberProfileFragment)
          }
     }
}
