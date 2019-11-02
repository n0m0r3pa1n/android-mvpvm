package com.nmp90.mvpvm.repo

import com.nmp90.mvpvm.data.Note
import io.reactivex.Single

class NotesRepository {
    fun getNote(): Single<Note> = Single.defer{ Single.just(Note("1","My Note")) }
}
