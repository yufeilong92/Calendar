package com.liang.myapplication

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.liang.myapplication.calendar.Utils
import com.liang.myapplication.calendar.component.CalendarAttr
import com.liang.myapplication.calendar.component.CalendarViewAdapter
import com.liang.myapplication.calendar.interf.OnSelectDateListener
import com.liang.myapplication.calendar.model.CalendarDate
import com.liang.myapplication.calendar.view.Calendar
import com.liang.myapplication.calendar.view.MonthPager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var currentCalendars: ArrayList<Calendar> = ArrayList<Calendar>()
    private var calendarAdapter: CalendarViewAdapter? = null
    private val onSelectDateListener: OnSelectDateListener? = null
    private var mCurrentPage: Int = MonthPager.CURRENT_DAY_INDEX
    private var context: Context? = null
    private var currentDate: CalendarDate? = null
    private var initiated = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        context=this
        //此处强行setViewHeight，毕竟你知道你的日历牌的高度
        calendar_view.setViewHeight(Utils.dpi2px(context, 270.0f))
        list.setHasFixedSize(true)
        list.layoutManager = LinearLayoutManager(this)
        list.adapter = ExampleAdapter(this)
        initCurrentDate()
        initCalendarView()
        initToolbarClickListener()
    }

    private fun initCurrentDate() {

        currentDate = CalendarDate()
        show_year_view.setText(currentDate?.getYear().toString() + "年")
        show_month_view.setText(currentDate?.getMonth().toString() + "")
    }

    private fun initCalendarView() {
        initListener()
        val customDayView = CustomDayView(context, R.layout.custom_day)
        calendarAdapter = CalendarViewAdapter(
                context,
                onSelectDateListener,
                CalendarAttr.WeekArrayType.Monday,
                customDayView)
        calendarAdapter?.setOnCalendarTypeChangedListener { list.scrollToPosition(0) }
        initMarkData()
        initMonthPager()

    }




    private fun initListener() {

        back_today_button.setOnClickListener(View.OnClickListener { onClickBackToDayBtn() })
        scroll_switch.setOnClickListener(View.OnClickListener {
            if (calendarAdapter!!.calendarType === CalendarAttr.CalendarType.WEEK) {
                Utils.scrollTo(content, list, calendar_view.getViewHeight(), 200)
                calendarAdapter!!.switchToMonth()
            } else {
                Utils.scrollTo(content, list, calendar_view.getCellHeight(), 200)
                calendarAdapter!!.switchToWeek(calendar_view.getRowIndex())
            }
        })
        theme_switch.setOnClickListener(View.OnClickListener { refreshSelectBackground() })
        next_month.setOnClickListener(View.OnClickListener { calendar_view.setCurrentItem(calendar_view.getCurrentPosition() + 1) })
        last_month.setOnClickListener(View.OnClickListener { calendar_view.setCurrentItem(calendar_view.getCurrentPosition() - 1) })
    }

    private fun initToolbarClickListener() {

        back_today_button.setOnClickListener(View.OnClickListener { onClickBackToDayBtn() })
        scroll_switch.setOnClickListener(View.OnClickListener {
            if (calendarAdapter!!.calendarType === CalendarAttr.CalendarType.WEEK) {
                Utils.scrollTo(content, list, calendar_view.getViewHeight(), 200)
                calendarAdapter!!.switchToMonth()
            } else {
                Utils.scrollTo(content, list, calendar_view.getCellHeight(), 200)
                calendarAdapter!!.switchToWeek(calendar_view.getRowIndex())
            }
        })
        theme_switch.setOnClickListener(View.OnClickListener { refreshSelectBackground() })
        next_month.setOnClickListener(View.OnClickListener { calendar_view.setCurrentItem(calendar_view.getCurrentPosition() + 1) })
        last_month.setOnClickListener(View.OnClickListener { calendar_view.setCurrentItem(calendar_view.getCurrentPosition() - 1) })
    }

    /**
     * onWindowFocusChanged回调时，将当前月的种子日期修改为今天
     *
     * @return void
     */
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus && !initiated) {
            refreshMonthPager()
            initiated = true
        }
    }

    /*
    * 如果你想以周模式启动你的日历，请在onResume是调用
    * Utils.scrollTo(content, rvToDoList, monthPager.getCellHeight(), 200);
    * calendarAdapter.switchToWeek(monthPager.getRowIndex());
    * */
    override fun onResume() {
        super.onResume()
    }

    /**
     * 初始化monthPager，MonthPager继承自ViewPager
     *
     * @return void
     */
    private fun initMonthPager() {
        calendar_view.setAdapter(calendarAdapter)
        calendar_view.setCurrentItem(MonthPager.CURRENT_DAY_INDEX)
        calendar_view.setPageTransformer(false) { page, position ->
            var position = position
            position = Math.sqrt(1 - Math.abs(position).toDouble()).toFloat()
            page.alpha = position
        }
        calendar_view.addOnPageChangeListener(object : MonthPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {
                mCurrentPage = position
                currentCalendars = calendarAdapter!!.pagers
                if (currentCalendars[position % currentCalendars.size] != null) {
                    val date: CalendarDate = currentCalendars[position % currentCalendars.size].getSeedDate()
                    currentDate = date
                    show_year_view.setText(date.getYear().toString() + "年")
                    show_month_view.setText(date.getMonth().toString() + "")
                }
            }

            override   fun onPageScrollStateChanged(state: Int) {}
        })
    }


    fun onClickBackToDayBtn() {
        refreshMonthPager()
    }
    /**
     * 初始化标记数据，HashMap的形式，可自定义
     * 如果存在异步的话，在使用setMarkData之后调用 calendarAdapter.notifyDataChanged();
     */
    private fun initMarkData() {
        val markData = HashMap<String, String>()
        markData["2017-8-9"] = "1"
        markData["2017-7-9"] = "0"
        markData["2017-6-9"] = "1"
        markData["2017-6-10"] = "0"
        calendarAdapter!!.setMarkData(markData)
    }
    private fun refreshMonthPager() {
        val today = CalendarDate()
        calendarAdapter!!.notifyDataChanged(today)
        show_year_view.setText(today.getYear().toString() + "年")
        show_month_view.setText(today.getMonth().toString() + "")
    }

    private fun refreshSelectBackground() {
        val themeDayView = ThemeDayView(context, R.layout.custom_day_focus)
        calendarAdapter!!.setCustomDayRenderer(themeDayView)
        calendarAdapter!!.notifyDataSetChanged()
        calendarAdapter!!.notifyDataChanged(CalendarDate())
    }
}
