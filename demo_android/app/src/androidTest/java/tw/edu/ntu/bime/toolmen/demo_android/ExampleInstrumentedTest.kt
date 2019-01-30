package tw.edu.ntu.bime.toolmen.demo_android

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import android.util.Log
import com.bumptech.glide.Glide
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.json.responseJson

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    private val url = "http://192.168.1.2:5000"

    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getTargetContext()
        assertEquals("tw.edu.ntu.bime.toolmen.demo_android", appContext.packageName)
    }


    @Test
    fun testGlide() {
        Glide.with(InstrumentationRegistry.getTargetContext())
             .load("https://ffp4g1ylyit3jdyti1hqcvtb-wpengine.netdna-ssl.com/firefox/files/2017/12/firefox-logo-300x310.png")
    }

    @Test
    fun testGlideFile() {
        Glide.with(InstrumentationRegistry.getTargetContext())
             .load(R.drawable.firefox)
    }

    @Test
    fun testFuel() {
        Fuel.get("$url/json")
            .response { request, response, result ->
                println(request)
                println(response)
                val (bytes, error) = result
                println(error)
                if (bytes != null) {
                    println("[response bytes] ${String(bytes)}")
                }
                assert(bytes != null)
                assert(result.toString() == "{'result': 123}")
            }
    }

    @Test
    fun testFuelJson() {
        Fuel.get("$url/json")
            .responseJson { _, _, result ->
                result.fold(success = { json ->
                    println("Success $json")
                }, failure = { error ->
                    println("Fail $error")
                    assert(false)
                })
            }
    }

    @Test
    fun testFuelJsonError() {
        Fuel.get("$url/json123123123")
            .responseJson { request, response, result ->
                println(request)
                println(response)
                result.fold(success = { json ->
                    println("Success $json")
                    assert(false)
                }, failure = { error ->
                    println("Fail $error")
                })
            }
    }

    @Test
    fun testJson() {
        Fuel.get("$url/json")
            .responseJson { _, _, result ->
                result.fold(success = { json ->
                    val jsonobj = json.obj()
                    println("Json object status = ${jsonobj["status"]}")
                    assertEquals(jsonobj["status"], 123)
                }, failure = { error ->
                    println("Fail $error")
                    assert(false)
                })
            }
    }
}
