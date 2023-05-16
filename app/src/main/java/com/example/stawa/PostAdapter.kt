package com.example.stawa

import android.app.Activity
import android.content.Intent
import android.service.autofill.Dataset
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.w3c.dom.Text
import java.text.SimpleDateFormat

class PostAdapter(private val activity:Activity,private var dataset:List<Post>):RecyclerView.Adapter<PostAdapter.ViewHolder>() {
    class ViewHolder(val layout:View):RecyclerView.ViewHolder(layout){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layout = LayoutInflater.from(parent.context).inflate(R.layout.itempost,parent,false)

        return ViewHolder(layout)
    }




    override fun getItemCount():Int {
        return dataset.size
    }
    fun setDataset(dataset: List<Post>){
        this.dataset=dataset.toMutableList()
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = dataset[position]
        holder.layout.findViewById<TextView>(R.id.nameperson_tv).text = post.username
        holder.layout.findViewById<TextView>(R.id.post_tv).text = post.contenido
        holder.layout.findViewById<TextView>(R.id.post_tv2).text = post.cantidad


        //holder.layout.findViewById<Button>(R.id.compartirbtn).setOnClickListener{
            val i=Intent().apply {
                action=Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT,post.contenido)
                type="text/plain"
            }
            val sharedintent=Intent.createChooser(i,null)
            activity.startActivity(i)
        //}
    }
}