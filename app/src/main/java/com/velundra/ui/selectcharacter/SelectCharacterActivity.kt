package com.velundra.ui.selectcharacter

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.velundra.databinding.ActivitySelectCharacterBinding
import com.velundra.viewmodel.PlayerViewModel
import com.velundra.ui.MainActivity
import com.velundra.ui.chooseclass.ChooseClassActivity

class SelectCharacterActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySelectCharacterBinding
    private lateinit var adapter: CharacterAdapter
    private val playerViewModel: PlayerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectCharacterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = CharacterAdapter { player ->
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("player_id", player.id)
            startActivity(intent)
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        playerViewModel.allPlayers.observe(this) { players ->
            adapter.submitList(players)
        }

        binding.btnNewCharacter.setOnClickListener {
            startActivity(Intent(this, ChooseClassActivity::class.java))
        }
    }
}
