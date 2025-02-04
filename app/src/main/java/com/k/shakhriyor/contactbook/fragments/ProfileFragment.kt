package com.k.shakhriyor.contactbook.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity.LOCATION_SERVICE
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.k.shakhriyor.contactbook.R
import com.k.shakhriyor.contactbook.databinding.FragmentProfileBinding
import com.k.shakhriyor.contactbook.databinding.QrCodeDialogBinding
import com.k.shakhriyor.contactbook.other.ContactBook
import com.k.shakhriyor.contactbook.other.callPhonePermission
import com.k.shakhriyor.contactbook.other.sendSmsPermission


@Suppress("UNREACHABLE_CODE")
class ProfileFragment: Fragment() {
    private var _binding:FragmentProfileBinding?=null
    private val binding get() = _binding!!
    private var number:String?=""


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

    private var locationPermission=registerForActivityResult(ActivityResultContracts.RequestPermission()){

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding=FragmentProfileBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val name=arguments?.getString(ContactBook.Name)
         number=arguments?.getString(ContactBook.Number)
        val image=arguments?.getString(ContactBook.Image)



        binding.profileNameTv.text=name
        binding.phoneNumberTv.text=number
        binding.textView.text=number
        if (image=="0"){
            val nL=(name?.trim())?.split(" ")
            var alpha=""
            nL?.forEach {
               alpha+=it[0]
            }
            binding.profileImg.setImageResource(R.drawable.profile_img_rectangle)
            binding.profileAlphaTv.text=alpha
        }else{
            binding.profileAlphaTv.text=""
            binding.profileImg.setImageURI(Uri.parse(image))
        }

        binding.phoneImg.setOnClickListener {
            makeCall(number)
        }
        binding.mesageImg.setOnClickListener {
            makeSms(number)
        }
        binding.cardView2.setOnClickListener {
            makeSms(number)
        }
        binding.cardView.setOnClickListener {
            makeCall(number)
        }

        binding.cardView3.setOnClickListener {
            startVideoCall()
        }

        binding.cardView4.setOnClickListener {
            sendEmail()
        }

        binding.shareImg.setOnClickListener {
            shareContact(name,number)
        }

        binding.backImg.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.qrCodeImg.setOnClickListener {
            val qrCodeDialogBinding = QrCodeDialogBinding.inflate(layoutInflater, null, false)
            createQrCode(number, qrCodeDialogBinding.qrCodeImg)
            val dialog = AlertDialog.Builder(requireContext())
            dialog.setTitle("Qr kodni skannerlang")
            dialog.setView(qrCodeDialogBinding.root)
            dialog.create()
            dialog.setPositiveButton("OK") { _, _ ->

            }
            dialog.setCancelable(true)
            dialog.show()
        }


    }

    fun makeCall(number:String?){
        Log.d("asd", "makeCall: $number")
        if (callPhonePermission(requireContext())){
            Intent(Intent.ACTION_CALL, Uri.parse("tel:$number")).apply {
                startActivity(this)
            }
        }else{
            callPermission.launch(Manifest.permission.CALL_PHONE)
        }
    }

    fun makeSms(number: String?){
        Log.d("asd", "makeSms: $number")
        if (sendSmsPermission(requireContext())){
            Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:$number")).apply {
                startActivity(this)
            }
        }else{
            smsPermission.launch(Manifest.permission.SEND_SMS)
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun startVideoCall() {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse("https://meet.google.com/")
        startActivity(intent)
    }

    private fun sendEmail() {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")

        }
            startActivity(intent)
    }

    private fun shareContact(name:String?,number:String?) {


        val contactInfo = """
            Name: $name
            Phone: $number
        """.trimIndent()

        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, contactInfo)
            type = "text/plain"
        }

            startActivity(intent)

    }

    private fun createQrCode(text:String?,imageView: ImageView){
        val qrCodeWriter = QRCodeWriter()
        try {
            val bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, 200, 200)
            val barcodeEncoder = BarcodeEncoder()
            val bitmap: Bitmap = barcodeEncoder.createBitmap(bitMatrix)
            imageView.setImageBitmap(bitmap)
        } catch (e: WriterException) {
            e.printStackTrace()
        }
    }



    }

