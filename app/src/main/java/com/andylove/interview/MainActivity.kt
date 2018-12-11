package com.andylove.interview

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.andylove.interview.eventdistribution.EventActivity
import com.andylove.interview.ipc.IPCActivity
import com.andylove.interview.leak.LeakActivity
import com.andylove.interview.oom.OomActivity
import com.andylove.interview.pattern.PatternActivity
import com.andylove.interview.thread.ThreadActivity
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

        interview_oom?.setOnClickListener {
            startActivity(Intent(this@MainActivity, OomActivity::class.java))
        }
        interview_thread?.setOnClickListener {
            startActivity(Intent(this@MainActivity, ThreadActivity::class.java))
        }
        interview_event?.setOnClickListener {
            startActivity(Intent(this@MainActivity, EventActivity::class.java))
        }

        interview_pattern?.setOnClickListener {
            startActivity(Intent(this@MainActivity, PatternActivity::class.java))
        }

    }
}
