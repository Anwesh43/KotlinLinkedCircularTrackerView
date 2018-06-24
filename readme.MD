## KotlinLinledCircularTrackerView

### Android custom view library to create a circular order tracker view written in Kotlin. This is based on DoubleLinkedList concept.

### Usage

#### adding dependency in app/build.gradle

```
    compile project(":linkedcirculartrackerview")
```

#### showing view from activity

```
    val view : LinkedCircularTrackerView = LinkedCircularTrackerView.create(this)
```

#### adding step completion listener

```
    view.addOnStepCompletionListener {
        Toast.makeText(this, "${it} is completed" ,Toast.LENGTH_SHORT).show()
    }
```

#### Demo

<img src="https://github.com/Anwesh43/KotlinLinkedCircularTrackerView/blob/master/screenshots/kotlinlinkedcirculartrackerview.gif" width="350px" height="600px">