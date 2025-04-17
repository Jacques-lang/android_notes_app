package com.example.notesapp.fragments

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.notesapp.MainActivity
import com.example.notesapp.R
import com.example.notesapp.ReminderReceiver
import com.example.notesapp.databinding.FragmentEditNoteBinding
import com.example.notesapp.model.Note
import com.example.notesapp.viewmodel.NoteViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import java.util.Calendar
import java.util.UUID


class EditNoteFragment : Fragment(R.layout.fragment_edit_note){

    private var editNoteBinding: FragmentEditNoteBinding? = null
    private val binding get() = editNoteBinding!!

    private lateinit var editNoteView: View
    private lateinit var notesViewModel: NoteViewModel
    private lateinit var currentNote: Note
    private var isImageUploading = false
    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>
    private val args: EditNoteFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as AppCompatActivity).supportActionBar?.hide()
        editNoteBinding = FragmentEditNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        notesViewModel = (activity as MainActivity).noteViewModel
        currentNote = args.note!!
        editNoteView = view
        imagePickerLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                val imageUri: Uri? = result.data?.data
                imageUri?.let {
                    uploadImageToFireBase(it)

                    binding.editUploadedImageView.visibility = View.VISIBLE
                    val newHeight = (250 * resources.displayMetrics.density).toInt()
                    binding.editframelayout.layoutParams?.height = newHeight
                    binding.editframelayout.requestLayout()

                    Glide.with(this)
                        .load(it)
                        .into(binding.editUploadedImageView)
                }
            }
        }
        binding.edittitleinput.setText(currentNote.title)
        binding.editdescriptioninput.setText(currentNote.description)

        val edit_button_animation = AnimationUtils.loadAnimation(context, R.anim.button_press)
        val delete_button_animation = AnimationUtils.loadAnimation(context, R.anim.button_press)
        val edit_upload_button = AnimationUtils.loadAnimation(context, R.anim.button_press)
        val edit_image_animation = AnimationUtils.loadAnimation(context, R.anim.button_press)
        val shareicon_animation = AnimationUtils.loadAnimation(context, R.anim.button_press)
        val arrowback_animation = AnimationUtils.loadAnimation(context, R.anim.button_press)
        val reminder_animation = AnimationUtils.loadAnimation(context, R.anim.button_press)



        val restoredUrl = savedInstanceState?.getString("editedImageUrl")
        if (!restoredUrl.isNullOrEmpty()) {
            currentNote = currentNote.copy(fileURL = restoredUrl)

            binding.editUploadedImageView.visibility = View.VISIBLE
            val newHeight = (250 * resources.displayMetrics.density).toInt()
            binding.editframelayout.layoutParams?.height = newHeight
            binding.editframelayout.requestLayout()

            Glide.with(requireContext())
                .load(restoredUrl)
                .into(binding.editUploadedImageView)
        }

        if(!currentNote.fileURL.isNullOrEmpty()){
            Glide.with(this)
                .load(currentNote.fileURL)
                .into(binding.editUploadedImageView)
        }
        binding.editNotebutton.setOnClickListener {
            it.postDelayed({editSaveNote(editNoteView)}, 600)

            it.startAnimation(edit_button_animation)
            val editIcon = ContextCompat.getDrawable(requireContext(), R.drawable.baseline_edit_24)
            val doneIcon = ContextCompat.getDrawable(requireContext(), R.drawable.baseline_done_24)

            binding.editNotebutton.setImageDrawable(editIcon)
            binding.editNotebutton.animate()
                .alpha(0f)
                .setDuration(150)
                .withEndAction {
                    binding.editNotebutton.setImageDrawable(doneIcon)
                    binding.editNotebutton.alpha = 0f
                    binding.editNotebutton.animate().alpha(1f).setDuration(150).start()
                }.start()
        }

        binding.editdeleteNotebutton.setOnClickListener {
            it.startAnimation(delete_button_animation)
            it.postDelayed({
                deleteNote()
            }, 400)
        }

        binding.editUploadFileIcon.setOnClickListener {
            it.startAnimation(edit_upload_button)
            it.postDelayed({
                val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                    type = "image/*"
                }
                imagePickerLauncher.launch(intent)
            },200)
        }

        binding.shareicon.setOnClickListener {
            it.startAnimation(shareicon_animation)
            it.postDelayed({
                val shareText = "Title: ${currentNote.title}\n\n${currentNote.description}"
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, shareText)
                }
                startActivity(Intent.createChooser(shareIntent, "Share note via"))
            }, 100)
        }

        binding.arrowBack.setOnClickListener {
            it.startAnimation(arrowback_animation)
            it.postDelayed({
                view.findNavController().popBackStack(R.id.homeFragment, false)
            },100)
        }

        binding.setReminder.setOnClickListener {
            it.startAnimation(reminder_animation)
            it.postDelayed({
                setReminder()
            },100)
        }

        binding.editUploadedImageView.setOnLongClickListener {
            if(currentNote.fileURL.isNullOrEmpty()){
                return@setOnLongClickListener true
            }
            it.startAnimation(edit_image_animation)
            MaterialAlertDialogBuilder(requireContext()).apply{
                setTitle("Delete image")
                setMessage("Are you sure you want to delete this image?")
                setPositiveButton("Yes"){_,_ ->
                    currentNote = currentNote.copy(fileURL = "")
                    Glide.with(requireContext()).clear(binding.editUploadedImageView)
                    binding.editUploadedImageView.visibility = View.GONE
                    val newHeight = (450 * resources.displayMetrics.density).toInt()
                    binding.editframelayout.layoutParams?.height = newHeight
                    binding.editframelayout.requestLayout()

                    Toast.makeText(context, "Image deleted", Toast.LENGTH_SHORT).show()
                }
                setNegativeButton("No",null)
            }.create().show()
            true
        }

        if (!currentNote.fileURL.isNullOrEmpty()){
            binding.editUploadedImageView.visibility = View.VISIBLE
            val newHeight = (250 * resources.displayMetrics.density).toInt()
            binding.editframelayout.layoutParams?.height = newHeight
            binding.editframelayout.requestLayout()
        }
    }

    private fun formatReminderTime(timeInMillis: Long):String {
        val sdf = java.text.SimpleDateFormat("EEEE, h:mm a", java.util.Locale.getDefault())
        sdf.timeZone = java.util.TimeZone.getDefault()
        return sdf.format(java.util.Date(timeInMillis))
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        currentNote.fileURL?.let{
            outState.putString("editedImageUrl", it)
        }

    }

    private fun uploadImageToFireBase(uri: Uri) {
        isImageUploading = true
        val userID = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val filename = UUID.randomUUID().toString()
        val storageRef = FirebaseStorage.getInstance().reference
            .child("user_images/$userID/$filename")

        storageRef.putFile(uri)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    val imageUrl = downloadUri.toString()
                    isImageUploading = false


                    if(isAdded){
                        Toast.makeText(context, "Image uploaded", Toast.LENGTH_SHORT).show()
                    }

                    currentNote = currentNote.copy(fileURL = imageUrl)
                    if (isAdded) {
                        Glide.with(requireContext())
                            .load(imageUrl)
                            .into(binding.editUploadedImageView)
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

    @OptIn(ExperimentalMaterial3Api::class)
    private fun deleteNote() {
        MaterialAlertDialogBuilder(requireContext()).apply {
            setTitle("Delete Note")
            setMessage("Are you sure you want to delete this note?")
            setPositiveButton("Delete"){_,_ ->
                notesViewModel.deleteNote(currentNote)
                view?.findNavController()?.popBackStack(R.id.homeFragment, false)
                Snackbar.make(requireView(), "Note deleted", Snackbar.LENGTH_SHORT)
                    .setAction("Undo") { notesViewModel.addNote(currentNote) }
                    .show()
            }
            setNegativeButton("Cancel",null)
        }.create().show()
    }

    private fun editSaveNote(view:View){
        val noteTitle = binding.edittitleinput.text.toString().trim()
        val noteDescription = binding.editdescriptioninput.text.toString().trim()
        val userID = FirebaseAuth.getInstance().currentUser?.uid
        val timeStamp = System.currentTimeMillis()

        if(noteTitle.isEmpty()){
            Toast.makeText(context, "Please enter a title", Toast.LENGTH_SHORT).show()
            return
        }
        if(isImageUploading){
            Toast.makeText(context, "Please wait until image is uploaded", Toast.LENGTH_SHORT).show()
            return
        }
        Toast.makeText(context, "Note edited", Toast.LENGTH_SHORT).show()

            val note = Note(currentNote.id,
                noteTitle,
                noteDescription,
                timeStamp, userID!!,
                fileURL = currentNote.fileURL?:"")

            notesViewModel.updateNote(note)
        Toast.makeText(context, "Note Edited", Toast.LENGTH_LONG).show()
        view.findNavController().popBackStack(R.id.homeFragment, false)
    }

    private fun setReminder(){
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select reminder date")
            .build()


        datePicker.show(parentFragmentManager, "datePicker")

        datePicker.addOnPositiveButtonClickListener { selectedDate ->
            val calendar = Calendar.getInstance(java.util.TimeZone.getTimeZone("UTC")).apply {
                timeInMillis = selectedDate
            }

            calendar.timeZone = java.util.TimeZone.getDefault()

            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)

            val timePicker = TimePickerDialog(
                requireContext(),
                { _, hourOfDay, minute ->
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    calendar.set(Calendar.MINUTE, minute)
                    calendar.set(Calendar.SECOND, 0)
                    calendar.set(Calendar.MILLISECOND, 0)
                    scheduleReminder(calendar.timeInMillis)
                }, calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                false
            )
            timePicker.show()
        }
    }

    private fun scheduleReminder(triggerTime: Long) {
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
            if(!alarmManager.canScheduleExactAlarms()){
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                startActivity(intent)
                Toast.makeText(context, "Please give permission to set alarms", Toast.LENGTH_LONG).show()
                return
            }
        }
        val intent = Intent(requireContext(), ReminderReceiver::class.java).apply {
            putExtra("noteTitle", currentNote.title)
            putExtra("noteDescription", currentNote.description)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(), System.currentTimeMillis().toInt(),
            intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try{
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)

            val updateNote = currentNote.copy(reminderTime = triggerTime)
            notesViewModel.updateNote(updateNote)
            currentNote = updateNote

            val formatted = formatReminderTime(triggerTime)
            Toast.makeText(context, "Reminder set for ${formatted}", Toast.LENGTH_SHORT).show()

        }catch (_: SecurityException){
            Toast.makeText(context, "Alarm permission denied", Toast.LENGTH_SHORT).show()
        }


    }
    override fun onDestroy() {
        super.onDestroy()
        editNoteBinding = null
    }
}

