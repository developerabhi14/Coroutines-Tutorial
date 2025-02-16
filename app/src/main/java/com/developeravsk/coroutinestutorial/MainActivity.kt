package com.developeravsk.coroutinestutorial

import android.content.Intent
import android.os.Bundle
import android.provider.Settings.Global
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.w3c.dom.Text
import kotlin.system.measureTimeMillis

class MainActivity : AppCompatActivity() {
    val TAG = "MainActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val dummy: TextView = findViewById(R.id.dummy)
        // two useful coroutines scope
        // globalscope will live as long as application does
        // bad practice to use global scope because we rarely need our coroutines to be alive as long as our application
        // for android, we have two useful scopes, lifecycle scopes and viewmodel scopes
        // i have added some lifecycle dependencies
        val button: Button=findViewById(R.id.button)
        button.setOnClickListener {
            lifecycleScope.launch {
                while(true){
                    delay(1000L)
                    Log.d(TAG, "Still running")
                }
            }
            GlobalScope.launch {
                delay(5000L)
                Intent(this@MainActivity, SecondActivity::class.java).also {
                    startActivity(it)
                    finish()
                }
            }
        }
        //as we run this, the couroutine from MainActivity remains alive even though the activity is destroyed, because we defined in GlobalScope
        // this is a big state, it creates memory leaks
        // to solve this problem we swap global scope with lifecyclescope
        // viewmodelscope will keep corutines alive as long as viewmodel is alive

    }
}
//        //async and await
//        // if we have several suspend function and execute them both in a coroutine then they are sequential by default
//        // if we want to do two network calls we want them to execute at the same time
//        // we could just start two coroutines and execute them at once
//        GlobalScope.launch(Dispatchers.IO) {
//            val time = measureTimeMillis {
//                val answer1 = networkcall1()
//                val answer2 = networkcall2()
//                Log.d(TAG, "Answer 1 is $answer1")
//                Log.d(TAG, "Answer 2 is $answer2")
//            }
//            Log.d(TAG, "Request took $time milisecond")
//        }
//
//        GlobalScope.launch(Dispatchers.IO) {
//            val time = measureTimeMillis {
//                var answer1: String? = null
//                var answer2: String? = null
//                val job1= launch {
//                    answer1 = networkcall1()
//                }
//                val job2 = launch { answer2 = networkcall2() }
//                job1.join()
//                job2.join()
//            }
//            Log.d(TAG, "New Request took $time milisecond")
//        }
//        //but this above is a bad way to do it
//        GlobalScope.launch(Dispatchers.IO) {
//            val time= measureTimeMillis {
//                val answer1=async { networkcall1() }
//                val answer2=async { networkcall2() }
//                Log.d(TAG, "${answer1.await()}")
//                Log.d(TAG, "${answer2.await()}")
//            }
//            Log.d(TAG, "Completely New Request took $time milisecond")
//
//        }
//    }
//
//    suspend fun networkcall1(): String {
//        delay(3000L)
//        return "Answer 1"
//    }
//
//    suspend fun networkcall2(): String {
//        delay(3000L)
//        return "Answer 2"
//    }
//
//
//}
//        //simples way to start a coroutine
//        // globalscope: this corutine will live longer as app lives, but once it finishes its job, it will end
//        // coroutines will be started in a separate thread
////        GlobalScope.launch {
////            //just like sleep in thread, coroutines have their own sleep function called delay() and entering a  number of miliseconds
////            // for how long we want to delay
////            delay(3000L)
////            Log.d(TAG, "Corutine says hello frm thread ${Thread.currentThread().name}")
////        }
////        Log.d(TAG, "Hello frm thread ${Thread.currentThread().name}")
////        //delay will only pause current coroutine and not block the whole thread
////        //if main thread finishes its work , all other threads and coroutines will also be cancelled
////
////        GlobalScope.launch {
////            // delay is a suspend function
////            // suspend function can only be executed within another suspend function or inside of a coroutine
////            // we cannot call delay() inside main thread
////            val networkCall = doNetworkCall()
////            Log.d(TAG, networkCall)
////            val networkcall2 = doNetworkCall2()
////            Log.d(TAG, networkcall2)
////        }
////        //dispatchers.MAIN-starts coroutines in main thread, that will be
////        //useful for UI operations from withincoroutines
////        //dispatcher.IO-useful for all kind of data operations, like network call
////        //writing to databases or reading and writing to files
////        //dispatchers.Default-complex and long running calculations to prevent blocking main thread
////        //dispatchers.Unconfined-
////        //we can also start our own new thread by writing newSingleThreadContext("MyThread_name")
////        //useful thing about coroutine context is that we can easily switch them from within a coroutine
////        GlobalScope.launch(Dispatchers.IO) {
////            val answer = doNetworkCall3()
////            //we can switch context now to invoke ui operation in main thread
////            Log.d(TAG, "Starting coroutine in thread ${Thread.currentThread().name}")
////
////            withContext(Dispatchers.Main) {
////                //this code in this block will now be executed in mainthread
////                Log.d(TAG, "Setting text in thread ${Thread.currentThread().name}")
////                dummy.text = answer
////            }
////        }
////
////        // will actually block the main thread
////        //if we use delay here, it will block UI update
////        // but why would i need it
////        //if i don't want coroutine behaviour , but still want to hold up main thread
////        Log.d(TAG, "Before Run Blocking")
////        runBlocking {
////            // we can also start a new coroutine because we are already inside a coroutine
////            launch(Dispatchers.IO) {
////                //coroutine 1
////            }
////            launch(Dispatchers.IO) {
////                //coroutine 2
////            }
////
////            Log.d(TAG, "Start Run Blocking")
////            delay(20000L)
////            Log.d(TAG, "End Run Blocking")
////
////        }
////        Log.d(TAG, "After Run Blocking")
////
////        //coroutine jobs, wait and cancel
////        //when we launch a coroutine , it returns a job and we can save it
//
//        val job = GlobalScope.launch(Dispatchers.Default) {
////            repeat(5){
////                Log.d(TAG, "Coroutine is still working")
////                delay(1000L)
////            }
//            Log.d(TAG, "Starting long running calculation...")
//            withTimeout(2000L) {
//                for (i in 30..50) {
//                    if (isActive) {
//                        Log.d(TAG, "Result for i=$i:${fib(i)}")
//                    }
//                }
//            }
//            Log.d(TAG, "Ending long running calculation...")
//        }
//        //we can wait for the job using job.join()
//        //but join is a suspend function so we cannot execute it in main thread
//        // so let's use runBlocking
////        runBlocking {
////
////            //now it wil block our thread until this coroutine is finished
//////            job.join()
////            //alternatively we can also cancel() the job using job.cancel()
////            //cancelling is a coroutine is not always as easy as it seems
////            //our coroutine is so bust with the calculation that there is no time to check for cancellation, so we need to check manually of coroutine has been cancelled or not
////
////            //in practice we cancel coroutines using timeout
////            delay(2000L)
////            job.cancel()
////
////            Log.d(TAG, "Cancelled job!")
////        }
//
//    }
//
//    // we can also write our own suspend function
//    //but we cannot call the suspend function from outside the coroutine
//    suspend fun doNetworkCall(): String {
//        delay(5000L)
//        return "This is the answer"
//    }
//
//    suspend fun doNetworkCall3(): String {
//        delay(5000L)
//        return "This is the context answer"
//    }
//
//    suspend fun doNetworkCall2(): String {
//        delay(5000L)
//        return "This is the second answer"
//    }
//
//    fun fib(n: Int): Long {
//        return if (n == 0) 0
//        else if (n == 1) 1
//        else fib(n - 1) + fib(n - 2)
//    }
//}