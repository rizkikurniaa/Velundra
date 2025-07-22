package com.velundra.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.velundra.data.PlayerDatabase
import com.velundra.data.PlayerEntity
import com.velundra.model.Enemy
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = PlayerDatabase.getDatabase(application).playerDao()

    private val _player = MutableLiveData<PlayerEntity>()
    val player: LiveData<PlayerEntity> get() = _player

    private val _enemyHp = MutableLiveData(50)

    private val enemies = listOf(
        Enemy("Goblin", maxHp = 50, atk = 5, expReward = 10),
        Enemy("Orc", maxHp = 80, atk = 10, expReward = 20),
        Enemy("Troll", maxHp = 120, atk = 15, expReward = 30)
    )

    private var enemyIndex = 0
    private val _enemy = MutableLiveData(enemies[enemyIndex])
    val enemy: LiveData<Enemy> get() = _enemy

    private val _gameOver = MutableLiveData(false)
    val gameOver: LiveData<Boolean> get() = _gameOver

    fun loadPlayer(player: PlayerEntity) {
        _player.value = player
    }

    fun attackEnemy() {
        val current = _player.value ?: return
        val currentEnemy = _enemy.value ?: return

        currentEnemy.currentHp -= current.atk

        var newExp = current.exp
        var updatedPlayer = current

        if (currentEnemy.currentHp <= 0) {
            newExp += currentEnemy.expReward
            enemyIndex = (enemyIndex + 1).coerceAtMost(enemies.lastIndex)
            val nextEnemy = enemies[enemyIndex].copy(currentHp = enemies[enemyIndex].maxHp)
            _enemy.value = nextEnemy
        } else {
            _enemy.value = currentEnemy.copy()
        }

        // Level up logic
        if (newExp >= 100) {
            updatedPlayer = current.copy(
                level = current.level + 1,
                exp = 0,
                atk = current.atk + 2,
                maxHp = current.maxHp + 10,
                hp = current.maxHp + 10
            )
        } else {
            updatedPlayer = current.copy(exp = newExp)
        }

        _player.value = updatedPlayer

        viewModelScope.launch {
            dao.updatePlayer(updatedPlayer)
        }

        enemyAttack()
    }

    private fun enemyAttack() {
        val current = _player.value ?: return
        val enemyAtk = _enemy.value?.atk ?: 0
        val newHp = max(0, current.hp - enemyAtk)
        val updated = current.copy(hp = newHp)
        _player.value = updated

        if (newHp == 0) {
            _gameOver.value = true
        }

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
        _gameOver.value = false

        viewModelScope.launch {
            dao.updatePlayer(reset)
        }
    }
}