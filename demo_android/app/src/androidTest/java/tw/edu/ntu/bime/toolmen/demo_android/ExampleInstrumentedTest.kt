package tw.edu.ntu.bime.toolmen.demo_android

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import android.util.Log
import com.bumptech.glide.Glide
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FileDataPart
import com.github.kittinunf.fuel.core.Method
import com.github.kittinunf.fuel.json.responseJson

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import java.io.File

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
             .load("$url/images/firefox.png")
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
                // println(request)
                // println(response)
                result.fold(success = { res ->
                    // println(String(res))
                    assertEquals(String(res), "{\"result\":123}\n")
                }, failure = { error ->
                    // println(error)
                    assertTrue(false)
                })
            }
    }

    @Test
    fun testFuelJson() {
        Fuel.get("$url/json")
            .responseJson { _, _, result ->
                result.fold(success = { json ->
                    // println("Success $json")
                }, failure = { error ->
                    // println("Fail $error")
                    assertTrue(false)
                })
            }
    }

    @Test
    fun testFuelJsonError() {
        Fuel.get("$url/json123123123")
            .responseJson { request, response, result ->
                // println(request)
                // println(response)
                result.fold(success = { json ->
                    // println("Success $json")
                    assertTrue(false)
                }, failure = { error ->
                    // println("Fail $error")
                })
            }
    }

    @Test
    fun testJson() {
        Fuel.get("$url/json")
            .responseJson { _, _, result ->
                result.fold(success = { json ->
                    val jsonobj = json.obj()
                    // println("Json object result = ${jsonobj["result"]}")
                    assertEquals(jsonobj["result"], 123)
                }, failure = { error ->
                    // println("Fail $error")
                    assertTrue(false)
                })
            }
    }

    @Test
    fun testUpload() {
        val appContext = InstrumentationRegistry.getTargetContext()
        val outputDir = appContext.getCacheDir()
        val filename = File.createTempFile("firefox",".png", outputDir)
        val ( _, _, result) = Fuel.download("$url/images/firefox.png")
                                  .fileDestination { _, _ -> filename }
                                  .response()
        result.fold(success = {},
                    failure = { error ->
                    // println(error)
                    assertTrue(false)
                    })

        Fuel.upload("$url/", method=Method.POST)
            .add(FileDataPart(filename, name="photo"))
            .responseJson { _, _, resultjson ->
                println("Uploaded")
                resultjson.fold(success = { json ->
                    val jsonobj = json.obj()
                    println("Json object = $jsonobj")
                    assertEquals(jsonobj["result"], "0 results")
                    assertTrue(jsonobj["resimg"] is String)

                }, failure = { error ->
                    // println("Fail $error")
                    assertTrue(false)
                })
            }
    }

    @Test
    fun testProgress() {
        println("Waiting test")
        Fuel.get("$url/json")
            .requestProgress { _, _ ->
                println("Request wait")
            }
            .responseProgress{ _, _ ->
                println("Response wait")
            }
            .responseJson{ _, _, result ->
                println(result)
                println("OK")
            }
    }

    @Test
    fun testAsync() {
        println("Waiting test")
        val (_, _, result) = Fuel.get("$url/json")
                                 .responseJson()
        result.fold(success = { json ->
            val jsonobj = json.obj()
            // println("Json object result = ${jsonobj["result"]}")
            assertEquals(jsonobj["result"], 123)
        }, failure = { error ->
            // println("Fail $error")
            assertTrue(false)
        })
    }
}
