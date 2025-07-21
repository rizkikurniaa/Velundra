package com.velundra.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "players")
data class PlayerEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val username: String,
    val heroClass: String,
    val hp: Int,
    val maxHp: Int,
    val atk: Int,
    val level: Int,
    val exp: Int
)
