package com.velundra.ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.velundra.data.PlayerDatabase
import com.velundra.data.PlayerEntity
import com.velundra.databinding.ActivityMainBinding
import com.velundra.utils.BaseActivity
import com.velundra.viewmodel.GameViewModel
import kotlinx.coroutines.launch

class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding
    private val gameViewModel: GameViewModel by viewModels {
        ViewModelProvider.AndroidViewModelFactory.getInstance(application)
    }

    private var playerId: Int = -1
    private var currentPlayer: PlayerEntity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        applyEdgeToEdgeInsets(binding.layoutRoot, applyBottomPadding = true)

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
    }

    private fun setupGameLogic(player: PlayerEntity) {
        gameViewModel.loadPlayer(player)

        gameViewModel.player.observe(this) { updatedPlayer ->
            binding.tvHero.text =
                "🧝 ${updatedPlayer.username} (Class: ${updatedPlayer.heroClass})\n" +
                        "HP: ${updatedPlayer.hp}/${updatedPlayer.maxHp} | LV: ${updatedPlayer.level} | " +
                        "ATK: ${updatedPlayer.atk} | EXP: ${updatedPlayer.exp}/100"
        }

        gameViewModel.enemyHp.observe(this) { enemyHp ->
            binding.tvMonster.text = "👹 Enemy HP: $enemyHp"
        }

        binding.btnAttack.setOnClickListener {
            gameViewModel.attackEnemy()
            Toast.makeText(this, "Menyerang musuh!", Toast.LENGTH_SHORT).show()
        }

        binding.btnHeal.setOnClickListener {
            gameViewModel.healPlayer()
            Toast.makeText(this, "Menyembuhkan diri!", Toast.LENGTH_SHORT).show()
        }

        binding.btnRestart.setOnClickListener {
            gameViewModel.resetGame()
            Toast.makeText(this, "Game direset!", Toast.LENGTH_SHORT).show()
        }
    }
}
