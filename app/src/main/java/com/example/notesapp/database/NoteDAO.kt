package com.example.notesapp.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.notesapp.model.Note

@Dao
interface NoteDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: Note)

    @Update
    suspend fun update(note: Note)

    @Delete
    suspend fun delete(note: Note)

    @Query("SELECT * FROM note_table ORDER BY id DESC")
    fun getAllNotes(): LiveData<List<Note>>

    @Query("SELECT * FROM note_table WHERE (title LIKE :query OR description LIKE :query) AND userID =:uid")
    fun search(query: String?, uid: String): LiveData<List<Note>>

    @Query("SELECT * FROM note_table WHERE userID = :uid  ORDER BY timeStamp DESC")
    fun getNotesByUser(uid: String): LiveData<List<Note>>
}