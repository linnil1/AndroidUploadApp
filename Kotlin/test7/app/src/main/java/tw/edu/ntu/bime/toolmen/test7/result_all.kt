package tw.edu.ntu.bime.toolmen.test7

import android.content.Context
import android.database.sqlite.SQLiteException
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.result_item.view.*
import org.jetbrains.anko.db.*
import org.jetbrains.anko.support.v4.find
import org.jetbrains.anko.support.v4.toast
import java.io.File



class result_all : Fragment(), View.OnClickListener {
    companion object {
        @JvmStatic fun newInstance(): result_all = result_all()
        val TAG = "myDebug"
    }

    lateinit var recyclerView: RecyclerView
    lateinit var recycleadaper: RecycleAdapter
    lateinit var filedir :File

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.result_all, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        filedir = activity?.getExternalFilesDir(null) as File
        val result_data = getList()
        /*val tmp = File(filedir, "my.jpg")
        result_data.add(ResultData(tmp, tmp, "dog"))
        result_data.add(ResultData(tmp, tmp, "cat"))
        result_data.add(ResultData(tmp, tmp, "sugoi"))*/
        recycleadaper = RecycleAdapter(result_data, this.context!!)
        recyclerView = find<RecyclerView>(R.id.recycleview).apply {
            layoutManager = LinearLayoutManager(this.context)
            adapter = recycleadaper
            setHasFixedSize(true)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<View>(R.id.button_reset).setOnClickListener(this)
        view.findViewById<View>(R.id.button_start).setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.button_start -> {
                (getFragmentManager() as FragmentManager).popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            }
            R.id.button_reset -> {
                createDB(this.context!!).use {
                    dropTable("test", true)
                    // rm image files
                    recycleadaper.rseultReset()
                }
            }
        }
    }

    private fun getList(): List<ResultData> {
        lateinit var result_data: List<ResultData>
        createDB(this.context!!).use {
               /* for (i in 0..1)
                    insert("test",
                            "id" to i,
                            "oriimg" to "my$i.jpg",
                            "resimg" to "my$i.jpg",
                            "text" to "no $i")*/

            result_data = select("test")
                    .orderBy("id")
                    .parseList(object : MapRowParser<ResultData> {
                        override fun parseRow(columns: Map<String, Any?>): ResultData {
                            val oriimg = columns.getValue("oriimg").toString()
                            val resimg = columns.getValue("resimg").toString()
                            val text = columns.getValue("text").toString()
                            return ResultData(File(filedir, oriimg), File(filedir, resimg), text)
                        }
                    })
        }
        Log.d(TAG, result_data.toString())
        return result_data
    }
}

class RecycleAdapter(private var myDataset: List<ResultData>,val context: Context) :
        RecyclerView.Adapter<RecycleAdapter.ViewHolder>() {

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val myview = view
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): RecycleAdapter.ViewHolder {
        // create a new view
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.result_item, parent, false) as View
        // set the view's size, margins, paddings and layout parameters
        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.myview.text_result.text = myDataset[position].text
        showImage(myDataset[position].resimg, holder.myview.image_result)
    }

    private fun showImage(file: File, imageView: ImageView) {
        val requestOptions =  RequestOptions()
        requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE)
        requestOptions.skipMemoryCache(true)
        Glide.with(context)
                .load(file)
                .apply(requestOptions)
                .into(imageView)
    }

    fun rseultReset() {
        val s = myDataset.size
        myDataset = emptyList()
        notifyItemRangeRemoved(0, s)
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = myDataset.size
}

