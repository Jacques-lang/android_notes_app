package com.example.notesapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.notesapp.databinding.NoteLayoutBinding
import com.example.notesapp.fragments.HomeFragmentDirections
import com.example.notesapp.model.Note

class NoteAdapter() : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    class NoteViewHolder(val itemBinding: NoteLayoutBinding) : RecyclerView.ViewHolder(itemBinding.root)

    private val differCallback = object : DiffUtil.ItemCallback<Note>() {

        override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
                return oldItem == newItem
            }
        }
        val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
            return NoteViewHolder(
                NoteLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
        }

        override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
            val currentNote = differ.currentList[position]

            holder.itemBinding.titleoutput.text = currentNote.title
            holder.itemBinding.descriptionoutput.text = currentNote.description

            if(!currentNote.fileURL.isNullOrEmpty()){
                Glide.with(holder.itemView.context)
                    .load(currentNote.fileURL)
                    .into(holder.itemBinding.imageOutput!!)
            } else {
                holder.itemBinding.imageOutput?.setImageDrawable(null)
            }
            holder.itemView.setOnClickListener {
                val direction = HomeFragmentDirections.actionHomeFragmentToEditNoteFragment(currentNote)
                it.findNavController().navigate(direction)
            }

        }

        override fun getItemCount(): Int {
            return differ.currentList.size
        }


    }
