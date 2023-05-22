package com.example.stawa

import com.google.firebase.database.Exclude
import java.util.*

class Post (val postKey:String?=null,val userId:String?=null,var username:String? =null, val contenido:String?=null,val cantidad:String?=null,var imageurl:String?=null){
    @Exclude
    @set:Exclude
    @get:Exclude
    var uid:String?= null
    constructor():this(null,null,null,null,null,null)

}