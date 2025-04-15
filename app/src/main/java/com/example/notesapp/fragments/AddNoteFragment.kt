package com.example.notesapp.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.example.notesapp.MainActivity
import com.example.notesapp.R
import com.example.notesapp.databinding.FragmentAddNoteBinding
import com.example.notesapp.model.Note
import com.example.notesapp.viewmodel.NoteViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID


class AddNoteFragment : Fragment(R.layout.fragment_add_note) {

    private var addNoteBinding: FragmentAddNoteBinding? = null
    private val binding get() = addNoteBinding

    private lateinit var notesViewModel: NoteViewModel
    private lateinit var addNoteView: View
    private var uploadedImagedURL : String? = null
    private var isImageUploading = false
    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as AppCompatActivity).supportActionBar?.hide()
        addNoteBinding = FragmentAddNoteBinding.inflate(inflater, container, false)
        return binding?.root
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        uploadedImagedURL?.let {
            outState.putString("uploadedImageUrl", it)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        notesViewModel = (activity as MainActivity).noteViewModel
        addNoteView = view
        imagePickerLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                val imageUri: Uri? = result.data?.data
                imageUri?.let {
                    uploadImageToFireBase(it)

                    binding?.uploadedImageView?.visibility = View.VISIBLE
                    val newHeight = (250 * resources.displayMetrics.density).toInt()
                    binding?.framelayout?.layoutParams?.height = newHeight
                    binding?.framelayout?.requestLayout()

                    Glide.with(this)
                        .load(it)
                        .into(binding?.uploadedImageView!!)
                }
            }
        }
        val save_button_animation = AnimationUtils.loadAnimation(context, R.anim.button_press)
        val upload_button_animation = AnimationUtils.loadAnimation(context, R.anim.button_press)
        val image_animation = AnimationUtils.loadAnimation(context, R.anim.button_press)

        uploadedImagedURL = savedInstanceState?.getString("uploadedImageUrl")
        uploadedImagedURL?.let {
            binding?.uploadedImageView?.visibility = View.VISIBLE
            val newHeight = (250 * resources.displayMetrics.density).toInt()
            binding?.framelayout?.layoutParams?.height = newHeight
            binding?.framelayout?.requestLayout()

            Glide.with(requireContext())
                .load(it)
                .into(binding?.uploadedImageView!!)
        }

        binding?.savebutton?.setOnClickListener {
            it.startAnimation(save_button_animation)
            it.postDelayed({
                saveNote(addNoteView)
            }, 100)
        }
        binding?.uploadFileIcon?.setOnClickListener {
            it.startAnimation(upload_button_animation)
            it.postDelayed({
                val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                    type = "image/*"
                }
                imagePickerLauncher.launch(intent)
            }, 200)
        }
        binding?.uploadedImageView?.setOnLongClickListener {
            if(uploadedImagedURL.isNullOrEmpty()){
                return@setOnLongClickListener true
            }
            it.startAnimation(image_animation)
            MaterialAlertDialogBuilder(requireContext()).apply{
                setTitle("Delete image")
                setMessage("Are you sure you want to delete this image?")
                setPositiveButton("Yes"){_,_ ->
                    uploadedImagedURL = ""
                    Glide.with(requireContext()).clear(binding?.uploadedImageView!!)
                    binding?.uploadedImageView?.visibility = View.GONE
                    val newHeight = (450 * resources.displayMetrics.density).toInt()
                    binding?.framelayout?.layoutParams?.height = newHeight
                    binding?.framelayout?.requestLayout()

                    Toast.makeText(context, "Image deleted", Toast.LENGTH_SHORT).show()
                }
                setNegativeButton("No",null)
            }.create().show()
            true
        }
    }
    //ok
    private fun uploadImageToFireBase(uri: Uri) {
        isImageUploading = true

        val userID = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val filename = UUID.randomUUID().toString()
        val storageRef = FirebaseStorage.getInstance().reference
            .child("user_images/$userID/$filename")

        storageRef.putFile(uri)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    uploadedImagedURL = downloadUri.toString()
                    isImageUploading = false

                    if(isAdded){
                        Toast.makeText(context, "Image uploaded", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .addOnFailureListener {
                isImageUploading = false
                if(isAdded){
                    Toast.makeText(context, "upload failed", Toast.LENGTH_SHORT).show()
                }
            }
    }


    private fun saveNote(view: View){
        val noteTitle = binding?.titleinput?.text.toString().trim()
        val noteDescription = binding?.descriptioninput?.text.toString().trim()
        val userID = FirebaseAuth.getInstance().currentUser?.uid
        val timeStamp = System.currentTimeMillis()

        if(noteTitle.isEmpty()){
            Toast.makeText(addNoteView.context, "Please enter a title", Toast.LENGTH_SHORT).show()
            return
        }
        if(isImageUploading){
            Toast.makeText(context, "Please wait until image is uploaded", Toast.LENGTH_SHORT).show()
            return
        }

            val note = Note(title = noteTitle,
                description = noteDescription,
                userID = userID!!,
                timeStamp = timeStamp,
                fileURL = uploadedImagedURL ?: ""
            )
            notesViewModel.addNote(note)
            Toast.makeText(addNoteView.context, "Note Successfully Saved", Toast.LENGTH_LONG).show()
            view.findNavController().popBackStack(R.id.homeFragment, false)
    }

    override fun onDestroy() {
        super.onDestroy()
        addNoteBinding = null
    }





}
