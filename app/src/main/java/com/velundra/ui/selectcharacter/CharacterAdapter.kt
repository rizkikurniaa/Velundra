package com.velundra.ui.selectcharacter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.velundra.data.PlayerEntity
import com.velundra.databinding.ItemCharacterBinding

class CharacterAdapter(
    private val onItemClick: (PlayerEntity) -> Unit
) : RecyclerView.Adapter<CharacterAdapter.CharacterViewHolder>() {

    private var characters = listOf<PlayerEntity>()

    fun submitList(newList: List<PlayerEntity>) {
        characters = newList
        notifyDataSetChanged()
    }

    inner class CharacterViewHolder(private val binding: ItemCharacterBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(player: PlayerEntity) {
            binding.tvName.text = player.username
            binding.tvClass.text = "Class: ${player.heroClass}"
            binding.tvStats.text = "LV: ${player.level} | HP: ${player.hp}/${player.maxHp} | ATK: ${player.atk}"
            binding.root.setOnClickListener {
                onItemClick(player)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterViewHolder {
        val binding = ItemCharacterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CharacterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CharacterViewHolder, position: Int) {
        holder.bind(characters[position])
    }

    override fun getItemCount(): Int = characters.size
}
