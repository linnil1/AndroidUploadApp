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


class ResultOne :Fragment(), View.OnClickListener {
    private val url = "http://192.168.1.2:5000"
    private lateinit var tmpData :ResultData

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.result_one, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<View>(R.id.picture_ok).setOnClickListener(this)
        view.findViewById<View>(R.id.cancel_result).setOnClickListener(this)

    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.picture_ok -> {
                result_datas.add(tmpData)
                this.fragmentManager!!.beginTransaction()
                    .replace(R.id.base, CameraCapture())
                    .commit()
            }
            R.id.cancel_result -> {
                this.fragmentManager!!.beginTransaction()
                    .replace(R.id.base, CameraCapture())
                    .commit()
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // test
        // download()
        val file = File(activity!!.getExternalFilesDir(null), PIC_FILE_NAME)
        uploadAndShow(file)
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
                     val resText = jsonobj!!["result"].toString()
                     val resImg = "$url/images/" + jsonobj!!["resimg"]
                     result_text.text = resText
                     show(resImg)
                     tmpData = ResultData(resText, resImg)

                 }, failure = { error ->
                     Log.e("Server Error", error.toString())
                     result_text.text = getString(R.string.server_error) + error.toString()
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