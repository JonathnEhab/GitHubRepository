package com.example.githubtask.feature.home.view

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.githubtask.R
import com.example.githubtask.data.model.Repository

class RepositoryAdapter(private var repositories: MutableList<Repository>) :
    RecyclerView.Adapter<RepositoryAdapter.RepositoryViewHolder>() {

    fun addRepositories(newRepositories: List<Repository>) {
        val startIndex = repositories.size
        repositories.addAll(newRepositories)
        notifyItemRangeInserted(startIndex, newRepositories.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepositoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_item, parent, false)
        return RepositoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: RepositoryViewHolder, position: Int) {
        val animation =
            AnimationUtils.loadAnimation(holder.itemView.context, R.anim.scale_in_animation)
        holder.itemView.startAnimation(animation)
        val repository = repositories[position]

        holder.repoName.text = repository.name.replaceFirstChar { it.uppercase() }
        holder.ownerName.text = repository.owner.login.replaceFirstChar { it.uppercase() }
        holder.repoDescription.text = repository.description ?: "No description available"
        holder.repoUrl.text = repository.html_url
        holder.repoUrl.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(repository.html_url))
            holder.itemView.context.startActivity(intent)
        }

        Glide.with(holder.itemView.context)
            .load(repository.owner.avatar_url)
            .placeholder(R.drawable.ic_launcher_foreground)
            .circleCrop()
            .into(holder.ownerAvatar)

        var isExpanded = false
        holder.expandedContent.visibility = View.GONE
        holder.expandButton.text = "▼"

        holder.expandButton.setOnClickListener {
            isExpanded = !isExpanded
            holder.expandedContent.visibility = if (isExpanded) View.VISIBLE else View.GONE
            holder.expandButton.text = if (isExpanded) "▲" else "▼"
        }
    }

    override fun getItemCount(): Int = repositories.size

    fun setRepositories(repositories: List<Repository>) {
        this.repositories.clear()
        this.repositories.addAll(repositories)
        notifyDataSetChanged()
    }

    fun clearRepositories() {
        repositories.clear()
        notifyDataSetChanged()
    }

    class RepositoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val repoName: TextView = itemView.findViewById(R.id.repoName)
        val ownerName: TextView = itemView.findViewById(R.id.ownerName)
        val ownerAvatar: ImageView = itemView.findViewById(R.id.ownerAvatar)
        val repoDescription: TextView = itemView.findViewById(R.id.repoDescription)
        val repoUrl: TextView = itemView.findViewById(R.id.repoUrl)
        val expandButton: Button = itemView.findViewById(R.id.expandButton)
        val expandedContent: View = itemView.findViewById(R.id.expandedContent)

    }
}