package com.example.notesapp.repository

import com.example.notesapp.database.NoteDatabase
import com.example.notesapp.model.Note

class Repository(private val db: NoteDatabase){

    suspend fun insert(note: Note)= db.getNoteDao().insert(note)

    suspend fun delete(note: Note) = db.getNoteDao().delete(note)

    suspend fun update(note: Note) = db.getNoteDao().update(note)

    fun getNotesByUser(userId: String) = db.getNoteDao().getNotesByUser(userId)

    fun search(query: String?, uid:String) = db.getNoteDao().search(query, uid)

    fun getNoteById(noteId:Int) = db.getNoteDao().getNoteById(noteId)
}