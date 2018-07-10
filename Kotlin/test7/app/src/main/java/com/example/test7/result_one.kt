package com.example.test7

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.Method
import kotlinx.android.synthetic.main.result_one.*
import org.jetbrains.anko.support.v4.toast
import java.io.File

class result_one : Fragment(), View.OnClickListener {
    companion object {
        @JvmStatic fun newInstance(): result_one = result_one()
        val myAPI = "http://192.168.1.2:5000"
        lateinit var filedir :File
        lateinit var upfile :File
        lateinit var downfile :File
        lateinit var jsondata: Json_Resp_data

    }
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.result_one, container, false)


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        filedir = activity?.getExternalFilesDir(null) as File
        upfile = File(filedir, "my.jpg")

        showImage(upfile)
        this.progressBar1.visibility = View.VISIBLE
        res_upload()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<View>(R.id.result_all_button).setOnClickListener(this)
        view.findViewById<View>(R.id.return_button).setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.return_button -> {
                (getFragmentManager() as FragmentManager).popBackStack()
            }
        }
    }

    private fun showImage(file: File) {
        val requestOptions =  RequestOptions()
        requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE)
        requestOptions.skipMemoryCache(true)
        Glide.with(this)
             .load(file)
             .apply(requestOptions)
             .into(result_img_view)
    }

    private fun res_upload() {
        result_text.setText(R.string.uploading)
        val jsonneed = listOf("json" to "True")
        // Fuel.get(myAPI).responseObject(Json_Resp_data.Deserializer()) { request1, response1, result1 ->
        Fuel.upload(myAPI, Method.POST, jsonneed)
                .source { request, url -> upfile}
                .name {"photo"}
                .responseObject(Json_Resp_data.Deserializer())
                { request1, response1, result1 ->
                    Log.d("myDebug", response1.toString())
                    Log.d("myDebug", result1.toString())
                    result1.fold({ data ->
                        Log.d("myDebug", data.toString())
                        jsondata = data
                        res_download()
                    }, { error ->
                        toast("Network or Server Error")
                        Log.d("myDebug","An error of type ${error.exception} happened: ${error.message}")
                    })
                }
    }

    private fun res_download() {
        result_text.setText(R.string.downloading)

        downfile = File(filedir, "down.jpg")

        Fuel.download(myAPI + jsondata.resimg
        ).destination { response, url ->
            downfile
        }.response { request2, response2, result2 ->
            Log.d("myDebug", response2.toString())
            Log.d("myDebug", result2.toString())
            result2.fold({ data ->
                showImage(downfile)
                result_text.setText(jsondata.text)
                this.progressBar1.visibility = View.GONE
            },{ error ->
                toast("Network or Server Error")
                Log.d("myDebug","An error of type ${error.exception} happened: ${error.message}")
                this.progressBar1.visibility = View.GONE
            })
        }
    }
}
