# Draggable XML Layout

There are `TWO` usage of this library.

1) DraggableFrameLayout
2) DraggableScrollViewLayout

Use `DraggableFrameLayout` for your activities that does not contain any scrollable content! (Story, Image, etc.)

Use `DraggableScrollViewLayout` for your activities that contain scrollable content! (Look for the second gif)

> Java example can be found here: [Class](https://github.com/denizavsar/DraggableLayout/blob/master/app/src/main/java/com/deniz/draggable/JavaActivity.java) / [XML](https://github.com/denizavsar/DraggableLayout/blob/master/app/src/main/res/layout/activity_java.xml)

<p align="center">
    <img src="https://github.com/denizavsar/DraggableLayout/blob/master/art/story.gif" width="180" height="320">
    <img src="https://github.com/denizavsar/DraggableLayout/blob/master/art/scroll.gif" width="180" height="320">
    <img src="https://github.com/denizavsar/DraggableLayout/blob/master/art/bottom.gif" width="180" height="320">
    <img src="https://github.com/denizavsar/DraggableLayout/blob/master/art/image.gif" width="180" height="320">
</p>

## Installation

```groovy
implementation "com.github.denizavsar:DraggableLayout:7.0.0"
```

## Usage

Add `android:theme="@style/Draggable.NoActionBar"` on your activity decleration on `AndroidManifest.xml`

```xml
<activity
    android:name=".MainActivity"
    android:theme="@style/Draggable.NoActionBar" />
```

Add `overridePendingTransition(R.anim.draggable_enter_animation, 0)` before `super.onCreate(savedInstanceState)` on your activity class

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    overridePendingTransition(R.anim.draggable_enter_animation, 0)
    super.onCreate(savedInstanceState)
}
```

---

You can merge `Draggable` styles (`Draggable.NoActionBar`, `Draggable.FullScreen`) with your own styles on `styles.xml`

```xml
<style name="StoryScreen" parent="Draggable.FullScreen">
    <item name="colorPrimary">@color/primary_color</item>
    <item name="colorPrimaryDark">@color/primary_color_dark</item>
    <item name="colorAccent">@color/accent_color</item>
    <!-- more items -->
</style>
```

and use it like on your `AndroidManifest.xml`

```xml
<activity
    android:name=".MainActivity"
    android:theme="@style/StoryScreen" />
```

**IMPORTANT 1**

If you are going to use `Draggable.FullScreen` style, you should put that code in your `onCreate` method before the `setContentView(...)`

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    overridePendingTransition(R.anim.draggable_enter_animation, 0)
    super.onCreate(savedInstanceState)
    
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
    }
    
    setContentView(R.layout.activity_main)
}
```

**IMPORTANT 2**

If you use `ViewPager` (or something like listen to `left` and `right` swipe events) inside `DraggableLayout`, you should only enable moveable directions (`top` and `bottom`) with `app:draggableDirections="top|bottom"` to prevent the `DraggableLayout` catch the `left` and `right` swipe events!

---

**DraggableFrameLayout**

This xml layout is suitable for instagram like stories swipe down to close feature. `draggableDirections` is set to `bottom` so you can only start the drag operation with hold and swipe down gesture

```xml
<com.deniz.draggablelibrary.DraggableFrameLayout 
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:id="@+id/root"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:layout_gravity="center"
    app:draggableCorners="topLeft|topRight"
    app:draggableMaxCornerRadius="40dp"
    app:draggableScaleFactor="0.5"
    app:draggableDirections="bottom">

</com.deniz.draggablelibrary.DraggableFrameLayout>
```

> Example: [Class](https://github.com/denizavsar/DraggableLayout/blob/master/app/src/main/java/com/deniz/draggable/FrameActivity.kt) / [XML](https://github.com/denizavsar/DraggableLayout/blob/master/app/src/main/res/layout/activity_frame.xml)

---

**DraggableScrollViewLayout**

This xml layout is for activities that contain scrollable content. Just wrap your `ScrollView` with `DraggableScrollViewLayout`!

```xml
<com.deniz.draggablelibrary.DraggableScrollViewLayout 
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    app:draggableCorners="all"
    app:draggableDirections="all"
    app:draggableMaxCornerRadius="50dp"
    app:draggableMinCornerRadius="5dp">
    
    <ScrollView
        android:layout_width="match_parent"
        android:layout_height="match_parent">
    
    </ScrollView>

</com.deniz.draggablelibrary.DraggableScrollViewLayout>
```

> Example: [Class](https://github.com/denizavsar/DraggableLayout/blob/master/app/src/main/java/com/deniz/draggable/ScrollActivity.kt) / [XML](https://github.com/denizavsar/DraggableLayout/blob/master/app/src/main/res/layout/activity_scroll.xml)

**You are ready to go!**

## Detailed Usage

* **Listeners**

```kotlin
// DraggableFrameLayout - DragListener

val root = findViewById<DraggableFrameLayout>(R.id.root)

root.setDragListener(object : DraggableFrameLayout.DragListener {
    override fun onDragStarted(rawX: Float, rawY: Float) {
        // DO YOUR WORK HERE
    }

    override fun onDrag(rawX: Float, rawY: Float, touchDeltaX: Float, touchDeltaY: Float) {
        // DO YOUR WORK HERE
    }

    override fun onDragFinishing(distance: Float) {
        // DO YOUR WORK HERE
    }

    override fun onDragFinished() {
        // DO YOUR WORK HERE
    }
})
```

```kotlin
// DraggableScrollViewLayout - ScrollListener

val layout = findViewById<DraggableScrollViewLayout>(R.id.root)

layout.setScrollListener(object : DraggableScrollViewLayout.ScrollListener {
    override fun onScrollStarted(rawX: Float, rawY: Float) {
        // DO YOUR WORK HERE
    }

    override fun onScroll(scrollX: Int, scrollY: Int) {
        // DO YOUR WORK HERE
    }

    override fun onScrollFinished() {
        // DO YOUR WORK HERE
    }
})
```

* **Xml Attributes**

> draggableMinCornerRadius
```text
Usage       : app:draggableMinCornerRadius="15dp"
Default     : 0F
Format      : dimension
Limitations : draggableMinCornerRadius >= 0F
Explanation : See 'draggableCorners' attribute
```
***
> draggableMaxCornerRadius
```text
Usage       : app:draggableMaxCornerRadius="40dp"
Default     : 0F
Format      : dimension
Limitations : draggableMaxCornerRadius >= draggableMinCornerRadius
Explanation : See 'draggableCorners' attribute
```
***
> draggableDetectionAngle (Only works with DraggableFrameLayout)
```text
Usage       : app:draggableDetectionAngle="45"
Default     : 90F
Format      : float
Limitations : 0 <= draggableDetectionAngle <= 90
```
***
> draggableBackgroundColor
```text
Usage       : app:draggableBackgroundColor="@color/black"
Default     : #000000
Format      : color
Limitations : -
```
***
> draggableBackgroundOpacityMin
```text
Usage       : app:draggableBackgroundOpacityMin="100"
Default     : 70F
Format      : float
Limitations : draggableBackgroundOpacityMin >= 0
```
***
> draggableBackgroundOpacityMax
```text
Usage       : app:draggableBackgroundOpacityMax="155"
Default     : 255F
Format      : float
Limitations : draggableBackgroundOpacityMax >= draggableBackgroundOpacityMin
```
***
> draggableTransparentBackground
```text
Usage       : app:draggableTransparentBackground="true"
Default     : false
Format      : boolean
Limitations : -
Explanation : If true, draggableBackgroundOpacityMin and draggableBackgroundOpacityMax is useless
```
***
> draggableScaleFactor
```text
Usage       : app:draggableScaleFactor="0.5"
Default     : 0.6F
Format      : float
Limitations : 0.1 <= draggableScaleFactor <= 1.0
Explanation : Higher factor means less scale | (1.0 = no scale) , (0.1 = highest scale)
```
***
> draggableScaleEnabled
```text
Usage       : app:draggableScaleEnabled="true"
Default     : true
Format      : boolean
Limitations : -
Explanation : If false, draggableScaleFactor is useless
```
***
> draggableExitAnimation
```text
Usage       : app:draggableExitAnimation="@anim/alpha_out_animation"
Default     : R.anim.draggable_exit_animation
Format      : reference
Limitations : -
```
***
> draggableFinishOffset
```text
Usage       : app:draggableFinishOffset="100dp"
Default     : 0F
Format      : dimension
Limitations : -
```
***
> draggableCorners
```text
Usage       : app:draggableCorners="topLeft|topRight"
Default     : none
Format      : flag
Limitations : none - topLeft - topRight - bottomLeft - bottomRight - all
Explanation : Determine the corners that will have radius with drag event
```
***
> draggableDirections
```text
Usage       : app:draggableDirections="bottom"
Default     : none
Format      : flag
Limitations : none - top - bottom - left - right - all
Explanation : Determine the layout moveable directions
```
***
> draggableDistanceAxis
```text
Usage       : app:draggableDistanceAxis="y"
Default     : all
Format      : flag
Limitations : none - x - y - all
Explanation : Determine which axis should be used for distance calculation
```

* **In Class Attributes**

```kotlin
val layout = findViewById<DraggableScrollViewLayout>(R.id.root)

layout.setConfig()
            .setMinCornerRadius(15)
            .setMaxCornerRadius(35)
            .setTransparentBackground(false)
            .setBackgroundColorOpacityMin(70F)
            .setBackgroundColorOpacityMax(200F)
            .setScaleEnabled(true)
            .setScaleFactor(0.6)
            .setFinishOffset(60F)
            .apply()
```

## Examples

**Instagram Story** -> [Class](https://github.com/denizavsar/DraggableLayout/blob/master/app/src/main/java/com/deniz/draggable/FrameActivity.kt) / [XML](https://github.com/denizavsar/DraggableLayout/blob/master/app/src/main/res/layout/activity_frame.xml)

**ScrollView** -> [Class](https://github.com/denizavsar/DraggableLayout/blob/master/app/src/main/java/com/deniz/draggable/ScrollActivity.kt) / [XML](https://github.com/denizavsar/DraggableLayout/blob/master/app/src/main/res/layout/activity_scroll.xml)

**Image Display** -> [Class](https://github.com/denizavsar/DraggableLayout/blob/master/app/src/main/java/com/deniz/draggable/ImageDisplayActivity.kt) / [XML](https://github.com/denizavsar/DraggableLayout/blob/master/app/src/main/res/layout/activity_image_display.xml)

**Bottom Sheet** -> [Class](https://github.com/denizavsar/DraggableLayout/blob/master/app/src/main/java/com/deniz/draggable/BottomSheetActivity.kt) / [XML](https://github.com/denizavsar/DraggableLayout/blob/master/app/src/main/res/layout/activity_bottom_sheet.xml)

> You can clone this project for the detailed examples and usages of draggable layout

## Contributing
Pull requests are welcome. 

For major changes, please open an issue first to discuss what you would like to change.

## License

```text
MIT License

Copyright (c) 2020 Fırat Deniz Avşar, Connected2.me

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
