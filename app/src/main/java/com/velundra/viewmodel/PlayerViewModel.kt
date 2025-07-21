package com.velundra.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.velundra.data.PlayerDatabase
import com.velundra.data.PlayerEntity
import kotlinx.coroutines.launch

class PlayerViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = PlayerDatabase.getDatabase(application).playerDao()
    val allPlayers = dao.getAllPlayers()

    fun insert(player: PlayerEntity) {
        viewModelScope.launch {
            dao.insert(player)
        }
    }
}
