package com.developeravsk.coroutinestutorial

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    val TAG = "MainActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val dummy:TextView=findViewById(R.id.dummy)
        //simples way to start a coroutine
        // globalscope: this corutine will live longer as app lives, but once it finishes its job, it will end
        // coroutines will be started in a separate thread
        GlobalScope.launch {
            //just like sleep in thread, coroutines have their own sleep function called delay() and entering a  number of miliseconds
            // for how long we want to delay
            delay(3000L)
            Log.d(TAG, "Corutine says hello frm thread ${Thread.currentThread().name}")
        }
        Log.d(TAG, "Hello frm thread ${Thread.currentThread().name}")
        //delay will only pause current coroutine and not block the whole thread
        //if main thread finishes its work , all other threads and coroutines will also be cancelled

        GlobalScope.launch {
            // delay is a suspend function
            // suspend function can only be executed within another suspend function or inside of a coroutine
            // we cannot call delay() inside main thread
            val networkCall = doNetworkCall()
            Log.d(TAG, networkCall)
            val networkcall2 = doNetworkCall2()
            Log.d(TAG, networkcall2)
        }
        //dispatchers.MAIN-starts coroutines in main thread, that will be
        //useful for UI operations from withincoroutines
        //dispatcher.IO-useful for all kind of data operations, like network call
        //writing to databases or reading and writing to files
        //dispatchers.Default-complex and long running calculations to prevent blocking main thread
        //dispatchers.Unconfined-
        //we can also start our own new thread by writing newSingleThreadContext("MyThread_name")
        //useful thing about coroutine context is that we can easily switch them from within a coroutine
        GlobalScope.launch(Dispatchers.IO) {
            val answer=doNetworkCall3()
            //we can switch context now to invoke ui operation in main thread
            Log.d(TAG,"Starting coroutine in thread ${Thread.currentThread().name}")

            withContext(Dispatchers.Main){
                //this code in this block will now be executed in mainthread
                Log.d(TAG,"Setting text in thread ${Thread.currentThread().name}")
                dummy.text=answer
            }
        }
    }

    // we can also write our own suspend function
    //but we cannot call the suspend function from outside the coroutine
    suspend fun doNetworkCall(): String {
        delay(5000L)
        return "This is the answer"
    }
    suspend fun doNetworkCall3(): String {
        delay(5000L)
        return "This is the context answer"
    }

    suspend fun doNetworkCall2(): String {
        delay(5000L)
        return "This is the second answer"
    }
}