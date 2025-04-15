package com.example.notesapp.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.notesapp.MainActivity
import com.example.notesapp.R
import com.example.notesapp.adapter.NoteAdapter
import com.example.notesapp.databinding.FragmentHomeBinding
import com.example.notesapp.model.Note
import com.example.notesapp.viewmodel.NoteViewModel
import com.google.firebase.auth.FirebaseAuth


class HomeFragment : Fragment(R.layout.fragment_home) {
    private var homeBinding: FragmentHomeBinding? = null
    private val binding get() = homeBinding!!

    private lateinit var notesViewModel: NoteViewModel
    private lateinit var notesAdapter: NoteAdapter

    private var isSearching = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeBinding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val add_new_note_button_animation =
            AnimationUtils.loadAnimation(context, R.anim.button_press)

        val searchCard_button_animation =
            AnimationUtils.loadAnimation(context, R.anim.button_press)

        val menuIcon_animation = AnimationUtils.loadAnimation(context, R.anim.button_press)



        val user = FirebaseAuth.getInstance().currentUser
        val email = user?.email
        val username = email?.substringBefore("@")
        binding.menuLabel?.text = "Welcome " + username + "..."
        binding.menuLabel?.apply {
            alpha = 0f
            visibility = View.VISIBLE
            animate()
                .alpha(1f)
                .setDuration(800)
                .withEndAction {
                    postDelayed({
                        animate()
                            .alpha(0f)
                            .setDuration(800)
                            .withEndAction {
                                visibility = View.GONE
                            }.start()
                    }, 1000)
                }.start()
        }
        notesViewModel = (activity as MainActivity).noteViewModel
        setUpRecyclerView()

        binding.addnewnotebtn.setOnClickListener {
            it.startAnimation(add_new_note_button_animation)
            it.postDelayed({
                findNavController().navigate(R.id.action_homeFragment_to_addNoteFragment)
            }, 200)
        }

        binding.menuIcon?.setOnClickListener {
            it.startAnimation(menuIcon_animation)
            it.postDelayed({
                val mainActivity = activity as? MainActivity
                mainActivity?.drawerLayout?.openDrawer(androidx.core.view.GravityCompat.END)
            },100)

        }

        binding.editSearch.setOnTouchListener { v, event ->
            val editText = v as EditText

            editText.startAnimation(searchCard_button_animation)

            if (event.action == MotionEvent.ACTION_UP) {
                //cancel button right side
                editText.compoundDrawablesRelative[2]?.let { cancelButton ->
                    val drawableEndStart = editText.width - editText.paddingEnd - cancelButton.bounds.width()
                    if (event.x >= drawableEndStart) {
                        //if cancel button clicked
                        editText.text.clear()
                        isSearching = false
                        return@setOnTouchListener true
                    }
                }
            }
            return@setOnTouchListener false
        }
        binding.editSearch.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val query = s.toString().trim()
                if(query.isNotEmpty()){
                    isSearching = true
                    searchNote(query)
                }

                else{
                    isSearching = false
                    notesViewModel.getNotesForCurrentUser().observe(viewLifecycleOwner) { list ->
                        notesAdapter.differ.submitList(list)
                        updateUI(list)
                    }
                    binding.noResultsText.visibility = View.GONE


                }

            }

            override fun afterTextChanged(p0: Editable?) {}
        })
    }

    private fun updateUI(note: List<Note>?) {
        if (note != null) {
            if (note.isNotEmpty()) {
                binding.recyclerview.visibility = View.VISIBLE
                binding.emptyNotesImage?.visibility = View.GONE
                binding.noNotesLetter?.visibility = View.GONE
            } else {
                binding.emptyNotesImage?.visibility = View.VISIBLE
                binding.noNotesLetter?.visibility = View.VISIBLE
                binding.recyclerview.visibility = View.GONE
            }
        }
    }

    private fun setUpRecyclerView() {
        notesAdapter = NoteAdapter()
        binding.recyclerview.apply {
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            setHasFixedSize(true)
            adapter = notesAdapter
        }
        activity?.let {
            notesViewModel.getNotesForCurrentUser().observe(viewLifecycleOwner) { note ->
                if(!isSearching){
                    notesAdapter.differ.submitList(note)
                    updateUI(note)
                }
            }
        }
    }

    private fun searchNote(query: String?) {
        isSearching = true
        val searchQuery = "%${query?.trim()}%"
        notesViewModel.getNotesForCurrentUser().observe(viewLifecycleOwner) { allNotes ->
            // Only continue if database actually has notes
            if (!allNotes.isNullOrEmpty()) {
                notesViewModel.searchNote(searchQuery).observe(viewLifecycleOwner) { filteredList ->
                    notesAdapter.differ.submitList(filteredList)

                    // Only show noResultsText if filteredList is empty but DB is not
                    binding.noResultsText.visibility =
                        if (filteredList.isNullOrEmpty()) View.VISIBLE else View.GONE
                }
            } else {
                // DB is empty, don't show "no results"
                notesAdapter.differ.submitList(emptyList())
                binding.noResultsText.visibility = View.GONE
            }
        }
    }

}

