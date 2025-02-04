package com.k.shakhriyor.contactbook.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.CallLog
import android.provider.ContactsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.k.shakhriyor.contactbook.adapters.ContactHistoryListAdapter
import com.k.shakhriyor.contactbook.adapters.ContactsListAdapter
import com.k.shakhriyor.contactbook.data.Profile
import com.k.shakhriyor.contactbook.other.callLogReadPermission
import com.k.shakhriyor.contactbook.data.ProfileHistory
import com.k.shakhriyor.contactbook.databinding.FragmentContactListBinding
import com.k.shakhriyor.contactbook.other.SwipeInterface
import com.k.shakhriyor.contactbook.other.callPhonePermission
import com.k.shakhriyor.contactbook.other.contactReadPermission
import com.k.shakhriyor.contactbook.other.sendSmsPermission

class ContactListFragment: Fragment() {

private var _binding:FragmentContactListBinding?=null
    private val binding get() = _binding!!
   private lateinit var contactHistoryListAdapter: ContactHistoryListAdapter
   private lateinit var contactsListAdapter: ContactsListAdapter
   private var number=""
    private var contactList= mutableListOf<Profile>()
    private var aList= mutableListOf<Profile>()
    private var bList= mutableListOf<Profile>()

   @RequiresApi(Build.VERSION_CODES.M)
   private var callLogPermission=registerForActivityResult(ActivityResultContracts.RequestPermission()){ isGranted->
       if (isGranted){
           contactHistoryList()
       }
   }


    private var callPermission=registerForActivityResult(ActivityResultContracts.RequestPermission()){
        if (it){
            makeCall(number)
        }
    }
    private var smsPermission=registerForActivityResult(ActivityResultContracts.RequestPermission()){
        if (it){
            makeSms(number)
        }
    }



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding=FragmentContactListBinding.inflate(layoutInflater,container,false)
        return binding.root
    }


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val photoUri="content://com.android.contacts/display_photo/3"
        binding.profileImg.setImageURI(Uri.parse(photoUri))

        contactHistoryListAdapter=ContactHistoryListAdapter(this)
        contactsListAdapter = ContactsListAdapter(this)
        getContactList()
        contactHistoryList()

        binding.contactsHistoryRecyclerView.apply {
            adapter=contactHistoryListAdapter
            layoutManager=LinearLayoutManager(requireContext(),
                LinearLayoutManager.HORIZONTAL,false)
        }
        binding.profilesRecyclerView.apply {
            adapter=contactsListAdapter
            layoutManager=LinearLayoutManager(requireContext())
        }

        contactsListAdapter.setListener(object :SwipeInterface{
            override fun alphaListener(alpha: Char) {
                binding.profilesGroupAlphaTv.text=alpha.toString()
            }
        })


        binding.contactSearch.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                aList.clear()
                aList.addAll(bList)
               val n= aList.filter { (it.name.toLowerCase()).startsWith((p0!!.trim()).toLowerCase()) }
                updateList(n)
                return false
            }
        })



        val itemTouchHelper=ItemTouchHelper(object :ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position=viewHolder.adapterPosition
                number=contactList[position].number
                if (direction==ItemTouchHelper.RIGHT){
                    makeCall(number)
                }else{
                    makeSms(number)
                }
                contactsListAdapter.notifyItemChanged(position)
            }
        })

        itemTouchHelper.attachToRecyclerView(binding.profilesRecyclerView)

    }


    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("Recycle")
    private fun contactHistoryList(){
        val contactList= mutableListOf<ProfileHistory>()
        if (callLogReadPermission(requireContext())){
        val cursor=requireContext().contentResolver.query(CallLog.Calls.CONTENT_URI,
            null,null,null,null,null
            )
        if (cursor!=null){
            if (cursor.moveToFirst()){
                val numberIndex=cursor.getColumnIndex(CallLog.Calls.NUMBER)
                val nameIndex=cursor.getColumnIndex(CallLog.Calls.CACHED_NAME)
                val dataIndex=cursor.getColumnIndex(CallLog.Calls.DATE)
                val imageIndex=cursor.getColumnIndex(CallLog.Calls.CACHED_PHOTO_URI)
                do {
                    contactList.add(ProfileHistory(cursor.getString(nameIndex)?:cursor.getString(numberIndex),
                        cursor.getLong(dataIndex),
                        cursor.getString(imageIndex)?:"0",
                        cursor.getString(numberIndex)
                        ))
                }while (cursor.moveToNext())
                cursor.close()
            }
            contactList.sortByDescending { it.time }
            contactHistoryListAdapter.submitList(contactList)
            contactHistoryListAdapter.notifyItemInserted(0)
        }
        }else{
            callLogPermission.launch(Manifest.permission.READ_CALL_LOG)
        }
    }


    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("Recycle")
    private fun getContactList(){
        if (contactReadPermission(requireContext())){
            val cursor=requireContext().contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,null,null,null
                )
            if (cursor!=null){
                if (cursor.moveToFirst()){
                    val nameIndex=cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                    val numberIndex=cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                    val imageIndex=cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI)
                    do {
                        val name=cursor.getString(nameIndex)?:cursor.getString(numberIndex)
                        contactList.add(Profile(
                           name ,
                            cursor.getString(numberIndex),
                            cursor.getString(imageIndex)?:"0"
                        ))
                    }while (cursor.moveToNext())
                    cursor.close()
                }
            }
            contactList.sortBy {
                it.name
            }
            aList.addAll(contactList)
            bList.addAll(contactList)
            addList(contactList)
        }
    }


    fun makeCall(number:String){
        Log.d("asd", "makeCall: $number")
        if (callPhonePermission(requireContext())){
            Intent(Intent.ACTION_CALL, Uri.parse("tel:$number")).apply {
                startActivity(this)
            }
        }else{
            callPermission.launch(Manifest.permission.CALL_PHONE)
        }
    }

    fun makeSms(number: String){
        Log.d("asd", "makeSms: $number")
        if (sendSmsPermission(requireContext())){
            Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:$number")).apply {
                startActivity(this)
            }
        }else{
            smsPermission.launch(Manifest.permission.SEND_SMS)
        }
    }

    fun addList(profileList:List<Profile>){

        contactsListAdapter.submitList(profileList)
        contactsListAdapter.notifyItemInserted(0)
    }


    @SuppressLint("NotifyDataSetChanged")
    fun updateList(list: List<Profile>){
        contactList.clear()
        contactList.addAll(list)
        contactsListAdapter.notifyDataSetChanged()
    }

}