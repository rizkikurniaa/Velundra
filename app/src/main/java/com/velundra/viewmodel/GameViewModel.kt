package com.velundra.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.velundra.data.PlayerDatabase
import com.velundra.data.PlayerEntity
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = PlayerDatabase.getDatabase(application).playerDao()

    private val _player = MutableLiveData<PlayerEntity>()
    val player: LiveData<PlayerEntity> get() = _player

    private val _enemyHp = MutableLiveData(50)
    val enemyHp: LiveData<Int> get() = _enemyHp

    fun loadPlayer(player: PlayerEntity) {
        _player.value = player
    }

    fun attackEnemy() {
        val current = _player.value ?: return
        val newEnemyHp = (enemyHp.value ?: 0) - current.atk

        var newExp = current.exp + 10
        var updatedPlayer = current.copy(exp = newExp)

        if (newExp >= 100) {
            updatedPlayer = updatedPlayer.copy(
                level = current.level + 1,
                exp = 0,
                atk = current.atk + 2,
                maxHp = current.maxHp + 10,
                hp = current.maxHp + 10
            )
        }

        _player.value = updatedPlayer

        viewModelScope.launch {
            dao.updatePlayer(updatedPlayer)
        }

        if (newEnemyHp <= 0) {
            _enemyHp.value = 50
        } else {
            _enemyHp.value = newEnemyHp
        }

        enemyAttack()
    }

    private fun enemyAttack() {
        val current = _player.value ?: return
        val newHp = max(0, current.hp - 5)
        val updated = current.copy(hp = newHp)
        _player.value = updated

        viewModelScope.launch {
            dao.updatePlayer(updated)
        }
    }

    fun healPlayer() {
        val current = _player.value ?: return
        val healed = current.copy(hp = min(current.maxHp, current.hp + 15))
        _player.value = healed

        viewModelScope.launch {
            dao.updatePlayer(healed)
        }
    }

    fun resetGame() {
        val current = _player.value ?: return
        val reset = current.copy(level = 1, hp = current.maxHp, exp = 0, atk = 10)
        _player.value = reset
        _enemyHp.value = 50

        viewModelScope.launch {
            dao.updatePlayer(reset)
        }
    }
}