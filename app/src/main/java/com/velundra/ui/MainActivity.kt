package com.velundra.ui

import android.app.AlertDialog
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

            // Tambahkan pengecekan HP <= 0
            if (updatedPlayer.hp <= 0) {
                showGameOverDialog()
                setButtonsEnabled(false)
            } else {
                setButtonsEnabled(true)
            }
        }

        gameViewModel.enemy.observe(this) { enemy ->
            binding.tvMonster.text =
                "👹 ${enemy.name} - HP: ${enemy.currentHp}/${enemy.maxHp} | ATK: ${enemy.atk}"
        }

        gameViewModel.gameOver.observe(this) { isOver ->
            if (isOver) {
                Toast.makeText(this, "Game Over! Kamu kalah!", Toast.LENGTH_LONG).show()
                // Optional: disable tombol atau kembali ke menu
                binding.btnAttack.isEnabled = false
                binding.btnHeal.isEnabled = false
            }
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

    private fun setButtonsEnabled(enabled: Boolean) {
        binding.btnAttack.isEnabled = enabled
        binding.btnHeal.isEnabled = enabled
        binding.btnRestart.isEnabled = enabled
    }

    private fun showGameOverDialog() {
        AlertDialog.Builder(this)
            .setTitle("Game Over")
            .setMessage("Karakter kamu telah kalah.")
            .setPositiveButton("OK", null)
            .show()
    }

}
