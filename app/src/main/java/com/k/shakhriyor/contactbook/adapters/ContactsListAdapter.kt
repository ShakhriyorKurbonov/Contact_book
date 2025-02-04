package com.k.shakhriyor.contactbook.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.k.shakhriyor.contactbook.R
import com.k.shakhriyor.contactbook.data.Profile
import com.k.shakhriyor.contactbook.databinding.ContactListItemBinding
import com.k.shakhriyor.contactbook.other.ContactBook
import com.k.shakhriyor.contactbook.other.SwipeInterface
import com.qtalk.recyclerviewfastscroller.RecyclerViewFastScroller

class Diff():DiffUtil.ItemCallback<Profile>(){
    override fun areItemsTheSame(oldItem: Profile, newItem: Profile): Boolean {
       return oldItem===newItem
    }

    override fun areContentsTheSame(oldItem: Profile, newItem: Profile): Boolean {
       return oldItem==newItem
    }
}

class ContactsListAdapter(val fragment: Fragment):ListAdapter<Profile,ContactsListAdapter.ContactViewHolder>(Diff()),RecyclerViewFastScroller.OnPopupTextUpdate {
    var inter:SwipeInterface?=null
  inner  class ContactViewHolder(private val binding:ContactListItemBinding):ViewHolder(binding.root){
        fun bind(profile: Profile){
            val name=profile.name.trim()

            val alpha=name[0]
            inter?.alphaListener(alpha)

            binding.profileNameTv.text=name
            binding.phoneNumber.text=profile.number
            if (profile.image=="0"){
                val n=name.split(" ")
                var alphaL=""
                n.forEach {
                    alphaL+=it[0]
                }
                binding.profileAlphaTv.text=alphaL
                binding.profileImg.setImageResource(R.drawable.profile_img_rectangle)
            }else{
                binding.profileAlphaTv.text=""
                binding.profileImg.setImageURI(Uri.parse(profile.image))
            }

            binding.root.setOnClickListener {
                fragment.findNavController().navigate(R.id.action_contactListFragment_to_profileFragment,
                    bundleOf(ContactBook.Name to profile.name,
                        ContactBook.Number to profile.number,
                        ContactBook.Image to profile.image
                    )
                )
            }

        }

    }

    fun setListener(listener:SwipeInterface){
        inter=listener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        return ContactViewHolder(ContactListItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onChange(position: Int): CharSequence {
        return getItem(position).name[0].toString()
    }

}