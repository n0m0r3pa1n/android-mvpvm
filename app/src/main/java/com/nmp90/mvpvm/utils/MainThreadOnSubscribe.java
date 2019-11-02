package com.nmp90.mvpvm.utils;

import android.view.View;

import androidx.annotation.NonNull;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.MainThreadDisposable;

/**
 * Handles template functions that allow a common ground for using a view V bound with an model of type T to which we
 * subscribe that needs to add/remove a listener L when subscribing/unsubscribing.
 */
public abstract class MainThreadOnSubscribe<V extends View, T, L> implements ObservableOnSubscribe<T> {

    @NonNull
    private final V mView;

    public MainThreadOnSubscribe(@NonNull final V view) {
        mView = view;
    }

    @Override
    public void subscribe(@NonNull final ObservableEmitter<T> emitter) {
        createSubscription(emitter);
        emitOnSubscriptionCreated(mView, emitter);
    }

    private void createSubscription(@NonNull final ObservableEmitter<T> emitter) {
        final L listener = createListener(mView, emitter);
        //noinspection ConstantConditions
        if (listener == null) {
            throw new IllegalStateException("The created Listener must not be null");
        }

        emitter.setDisposable(new MainThreadDisposable() {
            @Override
            protected void onDispose() {
                removeListener(mView, listener);
            }
        });

        addListener(mView, listener);
    }


    /**
     * Creates the listener that we need to listen to changes on a view .
     *
     * @param view the view to observe on.
     * @param subscriber the current subscriber we are at.
     *
     * @return the listener to be used for listening to view events.
     */
    @NonNull
    abstract L createListener(@NonNull final V view, @NonNull final ObservableEmitter<T> subscriber);

    /**
     * Calls the right function to add the listener into the view.
     *
     * @param view the view to observe on.
     * @param listener the listener that observes on the view.
     */
    abstract void addListener(@NonNull final V view, @NonNull final L listener);

    /**
     * Calls the right function to remove the listener from the view.
     *
     * @param view the view to observe on.
     * @param listener the listener that observes on the view.
     */
    abstract void removeListener(@NonNull final V view, @NonNull final L listener);

    /**
     * Gives the possibility of calling the emitter.onNext() to emmit the initial value.
     *  @param view the view to observe on.
     * @param emitter the current emitter we are at.
     */
    abstract void emitOnSubscriptionCreated(@NonNull V view, @NonNull final ObservableEmitter<T> emitter);

}
