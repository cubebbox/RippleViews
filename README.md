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

### Usage

Declare an RippleView inside your XML layout file. You also need more:
``` xml
  <com.cubebox.ripples.RippleImageView
        android:id="@+id/more"
        android:layout_width="?android:actionBarSize"
        android:layout_height="?android:actionBarSize"
        android:layout_gravity="center"
        android:layout_margin="5dp"
        android:layout_toLeftOf="@+id/more2"
        android:background="@android:color/holo_blue_dark"
        android:padding="10dp"
        android:src="@android:drawable/ic_menu_edit"
        ripple:rv_centered="true"
        ripple:rv_holdBgInPressing="true"
        ripple:rv_pressBgType="circleFitMin" />
```   

### Customization
``` java
 <attr name="rv_alpha" format="integer" />
        <attr name="rv_rippleDuration" format="integer" />
        <attr name="rv_zoomDuration" format="integer" />
        <attr name="rv_rippleColor" format="color" />
        <attr name="rv_pressColor" format="color" />
        <attr name="rv_centered" format="boolean" />
        <attr name="rv_pressBgType" format="enum">
            <enum name="rect" value="0" />
            <enum name="circleFitMin" value="1" />
            <enum name="circleFitMax" value="2" />
        </attr>
        <attr name="rv_ripplePadding" format="dimension" />
        <attr name="rv_zoom" format="boolean" />
        <attr name="rv_zoomScale" format="float" />
        <attr name="rv_pressBgEnable" format="boolean" />
        <attr name="rv_holdBgInPressing" format="boolean" />
```  

### Troubleshooting
``` java
 rippleView.setOnClickConfirmListener(new RippleLinearLayout.OnClickConfirmListener() {
            @Override
            public void onConfirmClick(View v) {
                startActivity(new Intent(MainActivity.this, RecyclerActivity.class));
            }
        });
      
  If you want to know when the RippleViews is finished, you can set a listener on your view
        rippleView.setOnRippleCompleteListener(new OnRippleCompleteListener() {
            @Override
            public void onComplete(View v) {
                Toast.makeText(MainActivity.this, "click ", Toast.LENGTH_SHORT).show();
            }
        });
```
