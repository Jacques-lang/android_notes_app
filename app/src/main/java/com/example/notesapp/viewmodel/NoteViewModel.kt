package com.example.notesapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.notesapp.model.Note
import com.example.notesapp.repository.Repository
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class NoteViewModel(app: Application, private val noteRepository: Repository): AndroidViewModel(app) {
    fun addNote(note: Note) = viewModelScope.launch {
        noteRepository.insert(note)
    }
    fun deleteNote(note:Note) = viewModelScope.launch {
        noteRepository.delete(note)
    }
    fun updateNote(note: Note) = viewModelScope.launch {
        noteRepository.update(note)
    }

    fun searchNote(query: String?): LiveData<List<Note>> {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        return noteRepository.search(query, uid!!)
    }

    fun getNotesForCurrentUser(): LiveData<List<Note>>{
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return MutableLiveData(emptyList())
        return noteRepository.getNotesByUser(uid)
    }

    fun getReminderDate(timestamp: Long): LiveData<Note>{
        return noteRepository.getReminderDate(timestamp)
    }
}