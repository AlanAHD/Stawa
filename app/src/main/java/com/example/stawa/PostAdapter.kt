package com.example.stawa

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.service.autofill.Dataset
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import org.w3c.dom.Text
import java.text.SimpleDateFormat

class PostAdapter(private val activity:Activity,private var dataset:List<Post>,private val currentUser: FirebaseUser):RecyclerView.Adapter<PostAdapter.ViewHolder>() {
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
        val imageView = holder.layout.findViewById<ImageView>(R.id.Ipost)
        // Limpiar la referencia del ImageView antes de cargar la nueva imagen
        Picasso.get().cancelRequest(imageView)
        imageView.setImageDrawable(null)

        // Cargar la imagen utilizando Picasso
        Picasso.get().load(post.imageurl).into(imageView)

        val currentUsername=currentUser.displayName
        val isCurrentUserpost=currentUsername==post.username
        if(isCurrentUserpost){
            holder.layout.findViewById<Button>(R.id.editpost).visibility=View.VISIBLE
            holder.layout.findViewById<Button>(R.id.deletepost).visibility=View.VISIBLE
        }else{
            holder.layout.findViewById<Button>(R.id.editpost).visibility=View.GONE
            holder.layout.findViewById<Button>(R.id.deletepost).visibility=View.GONE

        }
        holder.layout.findViewById<Button>(R.id.editpost).setOnClickListener{
            val i=Intent(activity,ViewPost::class.java)
            i.putExtra("postKey", post.postKey)
            activity.startActivity(i)

        }
        holder.layout.findViewById<Button>(R.id.deletepost).setOnClickListener {
            val postKey = post.postKey?:""

            // Aquí debes agregar el código para eliminar el post usando la referencia a la base de datos
            val database = FirebaseDatabase.getInstance()
            val postsRef = database.getReference("posts")
            val postRef = postsRef.child(postKey)
            postRef.removeValue()
                .addOnSuccessListener {
                    // Post eliminado exitosamente
                    Toast.makeText(activity, "Post eliminado correctamente", Toast.LENGTH_SHORT).show()
                    // Eliminar la imagen del post del almacenamiento
                    val storage = FirebaseStorage.getInstance()
                    val storageRef = storage.getReferenceFromUrl(post.imageurl?:"")
                    storageRef.delete()
                        .addOnSuccessListener {
                            // Imagen eliminada exitosamente
                            Toast.makeText(activity, "Imagen eliminada correctamente", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { exception ->
                            // Error al eliminar la imagen
                            Toast.makeText(activity, "Error al eliminar la imagen: ${exception.message}", Toast.LENGTH_SHORT).show()
                        }
                }
                .addOnFailureListener { exception ->
                    // Error al eliminar el post
                    Toast.makeText(activity, "Error al eliminar el post: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        }

        holder.layout.findViewById<Button>(R.id.sharepost).setOnClickListener{
            val i=Intent().apply {
                action=Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, "Nombre de usuario: ${post.username}\nContenido: ${post.contenido}\nCantidad: ${post.cantidad}\n" +
                        "Imagen: ${post.imageurl}")
                type = "text/plain"
            }
            val compartir=Intent.createChooser(i,"Compartir Post")
            activity.startActivity(compartir)
        }

        //holder.layout.findViewById<Button>(R.id.compartirbtn).setOnClickListener{
            //val i=Intent().apply {
                //action=Intent.ACTION_SEND
               // putExtra(Intent.EXTRA_TEXT,post.contenido)
              //  type="text/plain"
            //}
            //val sharedintent=Intent.createChooser(i,null)
           // activity.startActivity(i)
        //}
    }
}