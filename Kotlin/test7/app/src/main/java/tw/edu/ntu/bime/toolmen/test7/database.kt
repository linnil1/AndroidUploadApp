package tw.edu.ntu.bime.toolmen.test7

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import org.jetbrains.anko.db.*

class MyDatabaseOpenHelper(ctx: Context) : ManagedSQLiteOpenHelper(ctx, "MyDatabase", null, 1) {
    companion object {
        private var instance: MyDatabaseOpenHelper? = null

        @Synchronized
        fun getInstance(ctx: Context): MyDatabaseOpenHelper {
            if (instance == null) {
                instance = MyDatabaseOpenHelper(ctx.getApplicationContext())
            }
            return instance!!
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        Log.d("myDebug", "Create DB")
        // Here you create tables
        db.createTable("test", true,
                "id" to INTEGER + PRIMARY_KEY + UNIQUE,
                "oriimg" to TEXT + UNIQUE,
                "resimg" to TEXT + UNIQUE,
                "text" to TEXT)
                //"photo" to BLOB)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Here you can upgrade tables, as usual
        db.dropTable("test", true)
    }
}

fun createDB (ctx: Context) :MyDatabaseOpenHelper {
    val database = MyDatabaseOpenHelper.getInstance(ctx)
    database.use {
        createTable("test", true,
                "id" to TEXT + PRIMARY_KEY + UNIQUE,
                "oriimg" to TEXT + UNIQUE,
                "resimg" to TEXT + UNIQUE,
                "text" to TEXT)
    }
    return database
}


/*
// Access property for Context
val Context.database: MyDatabaseOpenHelper
    get() = MyDatabaseOpenHelper.getInstance(getApplicationContext())
*/

