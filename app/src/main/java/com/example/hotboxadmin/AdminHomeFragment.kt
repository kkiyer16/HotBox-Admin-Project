package com.example.hotboxadmin

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.admin_home_frag_scroll_layout.*
import java.util.*

class AdminHomeFragment : Fragment() {

    lateinit var dots_layout : LinearLayout
    lateinit var mPager : ViewPager
    var path : IntArray = intArrayOf(R.drawable.slide_food1, R.drawable.slide_food2, R.drawable.slide_food3, R.drawable.slide_food4)
    lateinit var dots : Array<ImageView>
    lateinit var adapter : PageView
    var currentPage : Int = 0
    lateinit var timer : Timer
    private val DELAY_MS : Long = 1500
    private val PERIOD_MS : Long = 1500
    private val adminID = "F0y2F2SeaoWHjY7sIHFr4JRf1HF2"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val inc_lay = view.findViewById<View>(R.id.include_lay_home)
        val dis_card1 = inc_lay.findViewById<CardView>(R.id.display_card_1)
        val dis_card2 = inc_lay.findViewById<CardView>(R.id.display_card_2)

        Glide.with(context!!).load(R.drawable.fd_menu_admin).centerCrop().dontAnimate().into(hf_image_view_1)

        Glide.with(context!!).load(R.drawable.offers_menu).centerCrop().dontAnimate().into(hf_image_view_2)

        dis_card1.setOnClickListener {
            startActivity(Intent(activity, AdminFoodMenuActivity::class.java))
        }

        dis_card2.setOnClickListener {
            startActivity(Intent(activity, AdminOffersActivity::class.java))
        }

        btn_send_messages_to_user.setOnClickListener {
            startActivity(Intent(context, AdminSendMessagesActivity::class.java))
        }

        remove_food_from_menu_card.setOnClickListener {
            startActivity(Intent(context, AdminRemoveFoodActivity::class.java))
        }

        mPager = view.findViewById(R.id.view_pager) as ViewPager
        adapter = PageView(context!!, path)
        mPager.adapter = adapter
        dots_layout = view.findViewById(R.id.dotsLayout) as LinearLayout
        createDots(0)
        updatePage()
        mPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                currentPage = position
                try {
                    createDots(position)
                }catch (e: NullPointerException){

                }
            }
        })
    }

    private fun updatePage(){
        val handler = Handler()
        val Update : Runnable = Runnable {
            if (currentPage == path.size){
                currentPage = 0
            }
            mPager.setCurrentItem(currentPage++, true)
        }
        timer = Timer()
        timer.schedule(object : TimerTask(){
            override fun run() {
                handler.post(Update)
            }
        }, DELAY_MS,PERIOD_MS)
    }

    fun createDots(position : Int){
        if (dots_layout != null){
            dots_layout.removeAllViews()
        }
        dots = Array(path.size) { ImageView(context) }

        for (i in 0..path.size-1){
            dots[i] = ImageView(context)
            if(i == position){
                dots[i].setImageDrawable(ContextCompat.getDrawable(context!!, R.drawable.active_dots))
            }
            else{
                dots[i].setImageDrawable(ContextCompat.getDrawable(context!!, R.drawable.inactive_dots))
            }

            val params : LinearLayout.LayoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
            params.setMargins(4,0,4,0)
            dots_layout.addView(dots[i], params)

        }
    }

}
