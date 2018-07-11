package tw.edu.ntu.bime.toolmen.test7

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson
import java.io.File

data class ResultData (
    val oriimg: File,
    val resimg: File,
    val text: String
)

data class Json_Resp_data (
    var oriimg: String,
    var resimg: String,
    var text: String){
    //User Deserializer
    class Deserializer : ResponseDeserializable<Json_Resp_data> {
        override fun deserialize(content: String) = Gson().fromJson(content, Json_Resp_data::class.java)
    }
}
