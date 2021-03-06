package tw.edu.ntu.bime.toolmen.demo_android

import android.util.Log
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.json.responseJson
import org.junit.Test
import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun array() {
        var a = ArrayList<Int>()
        for (i in 0 until 10)
            a.add(i)
        for (i in 0 until 10)
            assertEquals(a[i], i)
        assertEquals(a.size, 10)
        a.clear()
        assertEquals(a.size, 0)
    }
}
