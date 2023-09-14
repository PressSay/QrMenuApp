package com.example.qfmenu.member

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.qfmenu.R
import com.example.qfmenu.viewmodels.models.Member

class MemberManagerAdapter(
     private val context: Context,
     private val dataset: List<Member>
) : RecyclerView.Adapter<MemberManagerAdapter.ItemViewHolder>() {
     class ItemViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
          val itemsMemberAdmin = view.findViewById<LinearLayout>(R.id.itemsMemberAdmin) as ViewGroup
          val memberId = itemsMemberAdmin.getChildAt(0) as TextView
          val btnMember = itemsMemberAdmin.getChildAt(1) as AppCompatImageButton
     }

     override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
          val adapter = LayoutInflater.from(parent.context).inflate(R.layout.item_member_admin, parent, false)
          return ItemViewHolder(adapter)
     }

     override fun getItemCount(): Int {
          return dataset.size
     }

     override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
          val item = dataset[position]
          holder.memberId.text = item.name
          holder.btnMember.setOnClickListener {
               holder.view.findNavController().navigate(R.id.action_memberManagerFragment_to_memberEditProfileFragment)
          }
     }
}
