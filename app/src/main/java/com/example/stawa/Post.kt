package com.example.stawa

import com.google.firebase.database.Exclude
import java.util.*

class Post (val username:String? =null, val contenido:String?=null,val cantidad:String?=null){
    @Exclude
    @set:Exclude
    @get:Exclude
    var uid:String?= null
    constructor():this(null,null,null)

}