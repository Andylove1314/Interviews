package com.andylove.interview

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.andylove.interview.R.id.interview_ipc
import com.andylove.interview.R.id.interview_leak
import com.andylove.interview.ipc.IPCActivity
import com.andylove.interview.leak.LeakActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        interview_ipc?.setOnClickListener {
            startActivity(Intent(this@MainActivity, IPCActivity::class.java))
        }
        interview_leak?.setOnClickListener {
            startActivity(Intent(this@MainActivity, LeakActivity::class.java))
        }

    }
}
