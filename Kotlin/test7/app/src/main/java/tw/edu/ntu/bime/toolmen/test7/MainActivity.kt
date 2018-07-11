package tw.edu.ntu.bime.toolmen.test7

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import org.jetbrains.anko.activityManager

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        savedInstanceState ?: supportFragmentManager.beginTransaction()
                .replace(R.id.base, camera_fragment.newInstance())
                .commit()
    }

}