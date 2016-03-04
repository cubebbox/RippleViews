# RippleViews
ripple views for layout or button

the stickyScrollView can Free sliding and customize all insert and exit the animation

### Example very smooth

![GIF a](https://github.com/cubebbox/RippleViews/blob/master/img/3.gif)


![GIF b](https://github.com/cubebbox/RippleViews/blob/master/img/4.gif)

![GIF c](https://github.com/cubebbox/RippleViews/blob/master/img/5.gif)

![GIF d](https://github.com/cubebbox/RippleViews/blob/master/img/6.gif)

![GIF e](https://github.com/cubebbox/RippleViews/blob/master/img/7.gif)

![GIF f](https://github.com/cubebbox/RippleViews/blob/master/img/8.gif)

Declare an StickyView inside your XML layout file. You also need to other layouts for contentLayout and floatLayout:

    <com.cubebox.library.StickyView
        android:id="@+id/act_roll_view_stickyView"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:layout_centerInParent="true"
        android:background="@color/transparent"
        app:contentLayout="@layout/roll_content_view"
        app:floatLayout="@layout/float_view"
        app:releaseTouch="true"
        app:startHeight="200dp"
        app:stickyBackground="@color/white" />
        

### Customization
You can change contentLayout and floatLayout of the view
app:startHeight ------the height in scrollView init
app:releaseTouch ------Whether to release touch events
app:stickyBackground ------When the view is suspended, the underlying background color


### Troubleshooting

If you want to use the StickyScrollView with EditText, you have to set android:windowSoftInputMode="adjustPan" to prevent the parent view to redraw itself .
