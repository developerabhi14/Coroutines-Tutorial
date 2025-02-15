package com.developeravsk.coroutinestutorial

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    val TAG = "MainActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
    }

    // we can also write our own suspend function
    //but we cannot call the suspend function from outside the coroutine
    suspend fun doNetworkCall(): String {
        delay(5000L)
        return "This is the answer"
    }

    suspend fun doNetworkCall2(): String {
        delay(5000L)
        return "This is the second answer"
    }
}