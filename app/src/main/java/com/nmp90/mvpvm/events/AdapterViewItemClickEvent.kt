package com.nmp90.mvpvm.events

import androidx.annotation.IdRes

open class AdapterViewItemClickEvent(@IdRes override val id: Int, val position: Int) : ClickEvent
