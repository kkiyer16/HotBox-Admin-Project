package com.example.hotboxadmin

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.ScrollView
import androidx.viewpager.widget.PagerAdapter
import com.google.firebase.storage.UploadTask

class PageView : PagerAdapter{
    private var con : Context
    private var path : IntArray
    lateinit var inflater : LayoutInflater

    constructor(con: Context, path: IntArray) : super() {
        this.con = con
        this.path = path
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object` as RelativeLayout

    }

    override fun getCount(): Int {
        return path.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val img : ImageView
        inflater = con.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val rv : View = inflater.inflate(R.layout.swipe_fragment, container, false)
        img = rv.findViewById(R.id.image_view_swipe) as ImageView
        img.setImageResource(path[position])
        container.addView(rv)
        return rv
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as RelativeLayout)
    }

}