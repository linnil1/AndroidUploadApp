package tw.edu.ntu.bime.toolmen.demo_android

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FileDataPart
import com.github.kittinunf.fuel.core.Method
import com.github.kittinunf.fuel.json.responseJson
import kotlinx.android.synthetic.main.result_one.*
import org.json.JSONObject
import java.io.File


class ResultOne : Fragment() {
    private val url = "http://192.168.1.2:5000"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.result_one, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        download()
    }

    private fun download() {
        val outputDir = this.context!!.filesDir
        val filename = File.createTempFile("firefox",".png", outputDir)
        Log.d("Image File", filename.toString())
        Fuel.download("$url/images/firefox.png")
            .fileDestination { _, _ -> filename }
            .response{ _, _, result ->
                result.fold(success = {
                    uploadAndShow(filename)
                }, failure = { error ->
                    Log.e("Image", error.toString())
                })
            }
    }

    private fun uploadAndShow(filename: File) {
        // set running
        Log.d("Server Start", "Start Uploading")
        result_text.text = getString(R.string.waiting)
        progress_one.visibility = View.VISIBLE

        // upload
        Fuel.upload("$url/", method=Method.POST)
             .add(FileDataPart(filename, name="photo"))
             .responseJson { _, _, result ->
                 var jsonobj :JSONObject? = null
                 result.fold(success = { json ->
                     jsonobj = json.obj()
                     Log.d("Server Response", jsonobj.toString())
                     result_text.text = jsonobj!!["result"].toString()
                     show("$url/images/" + jsonobj!!["resimg"])
                 }, failure = { error ->
                     Log.e("Server Error", error.toString())
                     result_text.text = "Server Error $error"
                 })
             }
    }

    private fun show(image_url: String) {
        Glide.with(this)
             .load(image_url)
             .into(result_img_view)
        progress_one.visibility = View.GONE
    }
}