package com.velundra.ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.velundra.data.PlayerDatabase
import com.velundra.data.PlayerEntity
import com.velundra.databinding.ActivityMainBinding
import com.velundra.viewmodel.GameViewModel
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val gameViewModel: GameViewModel by viewModels()
    private var playerId: Int = -1
    private var currentPlayer: PlayerEntity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        playerId = intent.getIntExtra("player_id", -1)
        if (playerId == -1) {
            Toast.makeText(this, "Gagal memuat karakter!", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        lifecycleScope.launch {
            val player = PlayerDatabase.getDatabase(applicationContext)
                .playerDao().getPlayerById(playerId)

            if (player != null) {
                currentPlayer = player
                setupUI(player)
                setupGameLogic(player)
            } else {
                Toast.makeText(this@MainActivity, "Karakter tidak ditemukan!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun setupUI(player: PlayerEntity) {
        binding.tvHero.text =
            "🧝 ${player.username} (Class: ${player.heroClass})\nHP: ${player.hp}/${player.maxHp} | LV: ${player.level} | ATK: ${player.atk} | EXP: ${player.exp}/100"

        // Tombol bisa dihubungkan ke logika GameViewModel atau buat logikamu sendiri
        binding.btnAttack.setOnClickListener {
            Toast.makeText(this, "Serangan diklik (implementasi menyusul)", Toast.LENGTH_SHORT).show()
        }

        binding.btnHeal.setOnClickListener {
            Toast.makeText(this, "Penyembuhan diklik (implementasi menyusul)", Toast.LENGTH_SHORT).show()
        }

        binding.btnRestart.setOnClickListener {
            Toast.makeText(this, "Reset game (implementasi menyusul)", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupGameLogic(player: PlayerEntity) {
        // Nanti bisa diteruskan ke gameViewModel.loadPlayer(player)
        // Lalu logic game jalan seperti sebelumnya
    }
}
