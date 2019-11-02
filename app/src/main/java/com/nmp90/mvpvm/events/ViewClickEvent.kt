package com.nmp90.mvpvm.events

import androidx.annotation.IdRes

/**
 * Notifies of a view with the specific [id] being clicked, which corresponds to the layoutResId.
 */
open class ViewClickEvent(@IdRes override val id: Int) : ClickEvent
