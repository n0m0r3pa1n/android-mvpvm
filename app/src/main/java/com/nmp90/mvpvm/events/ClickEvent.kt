package com.nmp90.mvpvm.events

import com.nmp90.mvpvm.base.UiEvent

/**
 * UiEvents related to the android view clicks.
 */
interface ClickEvent : UiEvent {
    /**
     * Id that identifies the click (normally tha layoutResId, but it could by any id).
     */
    val id: Int
}
