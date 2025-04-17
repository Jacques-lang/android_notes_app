package com.example.notesapp.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "note_table")
@Parcelize
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id:Int = 0,
    val title: String,
    val description: String,
    val timeStamp : Long,
    val userID: String,
    val fileURL: String? = null,
    val reminderTime: Long? = null
) : Parcelable