package com.nmp90.mvpvm.main

import androidx.databinding.ObservableField

class MainViewModel(
    val noteId: ObservableField<String> = ObservableField(""),
    val noteText: ObservableField<String> = ObservableField("")
)
