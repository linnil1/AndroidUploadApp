package com.example.test7

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson
import java.io.File

data class Result_data (
    val resimg: File,
    val oriimg: File,
    val text: String
)

data class Json_Resp_data (
    var resimg: String,
    var oriimg: String,
    var text: String){
    //User Deserializer
    class Deserializer : ResponseDeserializable<Json_Resp_data> {
        override fun deserialize(content: String) = Gson().fromJson(content, Json_Resp_data::class.java)
    }
}
