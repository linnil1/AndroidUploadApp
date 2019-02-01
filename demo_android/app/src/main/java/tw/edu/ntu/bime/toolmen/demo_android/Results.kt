package tw.edu.ntu.bime.toolmen.demo_android

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.cards.*


class Results : Fragment(), View.OnClickListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.cards, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<View>(R.id.go_picture).setOnClickListener(this)
        view.findViewById<View>(R.id.clear_results).setOnClickListener(this)

    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.go_picture -> this.fragmentManager!!.beginTransaction()
                    .replace(R.id.base, CameraCapture())
                    .commit()
            // This function will clear and notifiy recycleview to clear
            R.id.clear_results -> {
                viewAdapter.notifyItemRangeRemoved(0, result_datas.size)
                result_datas.clear()
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewManager = LinearLayoutManager(this.context)

        viewAdapter = CardAdapter()

        Log.d("RecycleView", "Created")
        recyclerView = card_recycle_view.apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)

            // use a linear layout manager
            layoutManager = viewManager

            // specify an viewAdapter (see also next example)
            adapter = viewAdapter
        }

    }
}


