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
import com.k.shakhriyor.contactbook.data.ProfileHistory
import com.k.shakhriyor.contactbook.databinding.ContactHistoryListBinding
import com.k.shakhriyor.contactbook.other.ContactBook
import java.util.concurrent.TimeUnit
import kotlin.concurrent.fixedRateTimer

class MyDiff():DiffUtil.ItemCallback<ProfileHistory>(){
    override fun areItemsTheSame(oldItem: ProfileHistory, newItem: ProfileHistory): Boolean {
        return oldItem==newItem
    }

    override fun areContentsTheSame(oldItem: ProfileHistory, newItem: ProfileHistory): Boolean {
        return oldItem==newItem
    }

}

class ContactHistoryListAdapter(val fragment: Fragment):ListAdapter<ProfileHistory,ContactHistoryListAdapter.ContactHistoryViewHolder>(MyDiff()) {
  inner  class ContactHistoryViewHolder(private var binding:ContactHistoryListBinding):ViewHolder(binding.root){
        fun bind(profileHistory: ProfileHistory){
            binding.profileNameTv.text=profileHistory.profileName.trim()
            val name=(profileHistory.profileName.trim()).split(" ")
            var alpha=""
            name.forEach {
                alpha+=it.trim()[0]
            }



            if (profileHistory.image=="0"){
                binding.NameAlpha.text=alpha
                binding.profileImg.setImageResource(R.drawable.profile_img_rectangle)
            }else{
                binding.NameAlpha.text=""
                binding.profileImg.setImageURI(Uri.parse(profileHistory.image))
            }

            val resultDate=System.currentTimeMillis()-profileHistory.time
            val hour= TimeUnit.MILLISECONDS.toHours(resultDate)
            val minute= TimeUnit.MILLISECONDS.toMinutes(resultDate)%60
            binding.timeTv.text =if (hour==0L&&minute==0L){
                "Hozir"
            }else if(hour==0L){
                "$minute daqiqa oldin"
            }else{
                "$hour soat \n $minute daqiqa oldin"
            }

            if (name.count() >= 9) {
                binding.profileNameTv.text = "${((profileHistory.profileName).trim()).substring(0, 8)}..."
            } else {
                binding.profileNameTv.text = profileHistory.profileName
            }


            binding.root.setOnClickListener {
                fragment.findNavController().navigate(R.id.action_contactListFragment_to_profileFragment,
                    bundleOf(
                        ContactBook.Name to profileHistory.profileName,
                        ContactBook.Number to profileHistory.number,
                        ContactBook.Image to profileHistory.image
                    )
                    )
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactHistoryViewHolder {
        return ContactHistoryViewHolder(ContactHistoryListBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ContactHistoryViewHolder, position: Int) {
       holder.bind(getItem(position))
    }
}