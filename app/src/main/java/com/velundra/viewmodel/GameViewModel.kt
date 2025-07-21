package com.velundra.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.velundra.data.PlayerEntity
import kotlin.math.max
import kotlin.math.min

class GameViewModel : ViewModel() {

    private val _player = MutableLiveData<PlayerEntity>()
    val player: LiveData<PlayerEntity> get() = _player

    private val _enemyHp = MutableLiveData<Int>(50)
    val enemyHp: LiveData<Int> get() = _enemyHp

    fun loadPlayer(player: PlayerEntity) {
        _player.value = player
    }

    fun attackEnemy() {
        val current = _player.value ?: return
        val newEnemyHp = (_enemyHp.value ?: 0) - current.atk

        // Tambahkan EXP saat serang
        val newExp = current.exp + 10
        var updatedPlayer = current.copy(exp = newExp)

        // Naik level kalau exp >= 100
        if (newExp >= 100) {
            updatedPlayer = updatedPlayer.copy(
                level = updatedPlayer.level + 1,
                exp = 0,
                atk = updatedPlayer.atk + 2,
                maxHp = updatedPlayer.maxHp + 10,
                hp = updatedPlayer.maxHp + 10
            )
        }

        _player.value = updatedPlayer

        if (newEnemyHp <= 0) {
            _enemyHp.value = 50 // respawn monster
        } else {
            _enemyHp.value = newEnemyHp
        }

        // Monster balas serang
        enemyAttack()
    }

    private fun enemyAttack() {
        val current = _player.value ?: return
        val newHp = max(0, current.hp - 5) // monster serang 5

        _player.value = current.copy(hp = newHp)
    }

    fun healPlayer() {
        val current = _player.value ?: return
        val healedHp = min(current.maxHp, current.hp + 15)
        _player.value = current.copy(hp = healedHp)
    }

    fun resetGame() {
        val current = _player.value ?: return
        _player.value = current.copy(
            hp = current.maxHp,
            exp = 0,
            level = 1,
            atk = 10
        )
        _enemyHp.value = 50
    }
}
