package cerbrendus.tasklist.Cleared

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import cerbrendus.tasklist.R

class ClearedActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cleared)
        val vm = ClearedViewModel.create(this)
    }
}
