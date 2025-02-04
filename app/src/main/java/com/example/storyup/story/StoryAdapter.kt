package com.example.storyup.story

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.storyup.databinding.ItemStoryBinding
import com.example.storyup.response.ListStoryItem

class StoryAdapter(
    private val onClick: (ListStoryItem) -> Unit
) : PagingDataAdapter<ListStoryItem, StoryAdapter.StoryViewHolder>(StoryDiffCallback()) {

    class StoryViewHolder(private val binding: ItemStoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(story: ListStoryItem, onClick: (ListStoryItem) -> Unit) {
            binding.tvStory1.text = story.name ?: "Nama tidak tersedia"
            binding.tvStory2.text = story.description ?: "Deskripsi tidak tersedia"

            Glide.with(binding.root.context)
                .load(story.photoUrl)
                .into(binding.ivStory)

            binding.root.setOnClickListener { onClick(story) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val  story = getItem(position)
        if (story != null){
            holder.bind(story, onClick)
        }
    }

    class StoryDiffCallback : DiffUtil.ItemCallback<ListStoryItem>() {
        override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
            return oldItem == newItem
        }
    }
}
