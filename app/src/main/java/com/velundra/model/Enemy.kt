package com.velundra.model

data class Enemy(
    val name: String,
    val maxHp: Int,
    val atk: Int,
    val expReward: Int,
    var currentHp: Int = maxHp
)