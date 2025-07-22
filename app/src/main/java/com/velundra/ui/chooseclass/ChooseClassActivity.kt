package com.velundra.ui.chooseclass

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.velundra.data.PlayerEntity
import com.velundra.databinding.ActivityChooseClassBinding
import com.velundra.utils.BaseActivity
import com.velundra.viewmodel.PlayerViewModel

class ChooseClassActivity : BaseActivity() {
    private lateinit var binding: ActivityChooseClassBinding
    private val playerViewModel: PlayerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChooseClassBinding.inflate(layoutInflater)
        setContentView(binding.root)


        applyEdgeToEdgeInsets(binding.layoutRoot, applyBottomPadding = true)

        binding.btnCreate.setOnClickListener {
            val name = binding.etName.text.toString()
            val selectedClass = when {
                binding.rbKnight.isChecked -> "Knight"
                binding.rbMage.isChecked -> "Mage"
                binding.rbArcher.isChecked -> "Archer"
                else -> null
            }

            if (name.isBlank() || selectedClass == null) {
                Toast.makeText(this, "Isi nama & pilih class!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val (hp, atk) = when (selectedClass) {
                "Knight" -> 120 to 15
                "Mage" -> 70 to 25
                "Archer" -> 90 to 20
                else -> 100 to 20
            }

            val newPlayer = PlayerEntity(
                username = name,
                heroClass = selectedClass,
                hp = hp,
                maxHp = hp,
                atk = atk,
                level = 1,
                exp = 0
            )

            playerViewModel.insert(newPlayer)
            Toast.makeText(this, "Karakter $name ($selectedClass) dibuat!", Toast.LENGTH_SHORT).show()
            finish() // kembali ke character selection
        }
    }
}
