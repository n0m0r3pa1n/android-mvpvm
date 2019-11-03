# Android MVP + VM architecture with Koin (Vertical Architecture)

The idea is that we have a Presenter which sets some properties of a ViewModel which is used by the activity/fragment.

## Communication

Activity has reference to Presenter & ViewModel. Meaning Presenter & ViewModel share the same scope. You can check the MainModule.
So whenever the presenter receives the data, he updates it on the ViewModel. If you want to tell the presenter do something, you just:
`presenter.doSomething()`. Then you will either receive the data through the ViewModel or you will receive a `PresenterAction`
object. You can decide on it what to do.

## Specifics

- UI emits events, Presenters emit Actions
- Presenters use the Android lifecycle class to dispose requests or resume them.  
- Added extension functions to the View class to emit click events through RxJava
- Presenter & ViewModel share the same scope so the ViewModel in the presenter & the ViewModel in the Activity/Fragment are the same object

## To be added

- RecyclerView BindingAdapter that works with DataBinding and accepts ViewModel data and automatically updates the data
- Adapter class which works with DataBinding and exposes a uiEvents variable which is responsible for emitting item clicks


