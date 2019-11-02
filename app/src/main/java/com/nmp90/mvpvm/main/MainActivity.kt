package com.nmp90.mvpvm.main

import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import com.nmp90.mvpvm.R
import com.nmp90.mvpvm.actions.GoToNoteDetailsAction
import com.nmp90.mvpvm.base.BaseActivity
import com.nmp90.mvpvm.base.PresenterAction
import com.nmp90.mvpvm.base.UiEvent
import com.nmp90.mvpvm.base.register
import com.nmp90.mvpvm.databinding.ActivityMainBinding
import com.nmp90.mvpvm.notedetails.NoteDetailsActivity
import com.nmp90.mvpvm.extensions.click
import com.nmp90.mvpvm.extensions.subscribe
import io.reactivex.Observable

import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.scope.currentScope
import org.koin.core.parameter.parametersOf

class MainActivity : BaseActivity() {

    private val presenter: MainPresenter by currentScope.inject {
        parametersOf(this@MainActivity)
    }

    private val viewModel: MainViewModel by currentScope.inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        binding.viewModel = viewModel

        setSupportActionBar(toolbar)
        setupPresenter()
    }

    private fun setupPresenter() {
        with(presenter) {
            presenterEvents.subscribe(
                lifecycle,
                { handlePresenterActions(it) },
                { Log.e("Main", "Error with screen events", it) }
            )
        }

        presenter.register(lifecycle)
    }

    private fun handlePresenterActions(action: PresenterAction) {
        when(action) {
            is GoToNoteDetailsAction -> openNoteDetails(action.noteId)
        }
    }

    private fun openNoteDetails(noteId: String) {
        startActivity(NoteDetailsActivity.makeIntent(this, noteId))
    }

    override fun collectViewUiEventGenerators(): Array<Observable<out UiEvent>> = arrayOf(
        fab.click()
    )
}
