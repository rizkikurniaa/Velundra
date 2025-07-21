package com.velundra.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.velundra.model.Hero
import com.velundra.model.Monster
import kotlin.math.min

class GameViewModel : ViewModel() {

    val hero = MutableLiveData<Hero>()
    val monster = MutableLiveData<Monster>()
    val isGameOver = MutableLiveData<Boolean>()

    init {
        hero.value = Hero("Knight", 100, 100, 20, 1, 0)
        spawnMonster()
        isGameOver.value = false
    }

    fun attack() {
        val h = hero.value ?: return
        val m = monster.value ?: return
        if (h.hp <= 0) return

        // Hero menyerang dulu
        m.hp = maxOf(0, m.hp - h.atk)

        // Jika monster mati
        if (m.hp == 0) {
            gainExp(m.expDrop)
            spawnMonster()
            return  // jangan lanjut karena monster baru sudah muncul
        }

        // Kalau monster belum mati, dia serang balik
        h.hp = maxOf(0, h.hp - m.atk)
        if (h.hp == 0) {
            isGameOver.value = true
        }

        hero.value = h
        monster.value = m  // hanya update monster kalau belum diganti
    }


    fun heal() {
        val h = hero.value ?: return
        h.hp = min(h.maxHp, h.hp + 30)
        hero.value = h
    }

    private fun gainExp(exp: Int) {
        val h = hero.value ?: return
        h.exp += exp
        if (h.exp >= 100) {
            h.level++
            h.exp = 0
            h.maxHp += 20
            h.hp = h.maxHp
            h.atk += 5
        }
        hero.value = h
    }

    private fun spawnMonster() {
        val newMonster = Monster("Goblin", 50, 10, 40)
        monster.value = newMonster
    }

    fun resetGame() {
        hero.value = Hero("Knight", 100, 100, 20, 1, 0)
        spawnMonster()
        isGameOver.value = false
    }
}