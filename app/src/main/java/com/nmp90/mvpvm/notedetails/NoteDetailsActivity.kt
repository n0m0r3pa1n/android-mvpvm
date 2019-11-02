package com.nmp90.mvpvm.notedetails

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.nmp90.mvpvm.R
import kotlinx.android.synthetic.main.activity_note_details.*

class NoteDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_details)
        textView.setText(intent.getStringExtra(KEY_NOTE_ID))
    }

    companion object {
        const val KEY_NOTE_ID = "KEY_NOTE_ID"

        fun makeIntent(context: Activity, noteId: String) =
            Intent(context, NoteDetailsActivity::class.java).apply {
                putExtra(KEY_NOTE_ID, noteId)
            }
    }
}
