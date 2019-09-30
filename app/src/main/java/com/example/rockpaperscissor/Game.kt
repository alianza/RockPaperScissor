package com.example.rockpaperscissor

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "gameTable")
data class Game (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") var id: Long? = null,
    @ColumnInfo(name = "result") var result: String,
    @ColumnInfo(name = "date") var date: Date,
    @ColumnInfo(name = "userAction") var userAction: Gesture,
    @ColumnInfo(name = "computerAction") var computerAction: Gesture
)