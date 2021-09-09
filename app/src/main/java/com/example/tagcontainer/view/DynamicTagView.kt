package com.example.tagcontainer.view

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.DimenRes
import com.example.tagcontainer.R

/**
 * create by 胡汉君
 * date 2021/9/9 14：51
 *
 *自定义一个tag容器view，可以动态设置容器的颜色和里面字体的颜色
 */
class DynamicTagView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var tagText: String = ""
    private var tagColor: ColorStateList? = null
    private var tagContainerColor: ColorStateList? = null
    private var tagSize = context.resources.getDimension(R.dimen.size_12_sp)
    private var tagPaddingTop = context.resources.getDimension(R.dimen.size_2_dp)
    private var tagPaddingBottom = context.resources.getDimension(R.dimen.size_2_dp)
    private var tagPaddingLeft = context.resources.getDimension(R.dimen.size_4_dp)
    private var tagPaddingRight = context.resources.getDimension(R.dimen.size_4_dp)
    private var rectF: RectF = RectF()
    private val tagPaint: Paint = Paint()
    private val containPaint: Paint = Paint()
    private val strokeWidth = context.resources.getDimension(R.dimen.size_1_dp) / 2
    private val path = Path()

    //圆角
    private var radius = context.resources.getDimension(R.dimen.size_20_dp)

    companion object {
        private const val TAG = "DynamicTagView"
    }

    private val radii = FloatArray(8)

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.DynamicTagView,
            0, 0
        ).apply {
            try {
                tagText = getString(R.styleable.DynamicTagView_tagText) ?: "hello world"
                tagColor = getColorStateList(R.styleable.DynamicTagView_tagColor)
                tagContainerColor = getColorStateList(R.styleable.DynamicTagView_containerColor)
                tagSize = getDimension(R.styleable.DynamicTagView_tagDTextSize, tagSize)
                tagPaddingTop = getDimension(R.styleable.DynamicTagView_tagPaddingTop, 0f)
                tagPaddingBottom = getDimension(R.styleable.DynamicTagView_tagPaddingBottom, 0f)
                tagPaddingLeft = getDimension(R.styleable.DynamicTagView_tagPaddingLeft, 0f)
                tagPaddingRight = getDimension(R.styleable.DynamicTagView_tagPaddingRight, 0f)
                radius = getDimension(R.styleable.DynamicTagView_radius, radius)
                applyAttributes()
                Log.d(TAG, "tagText $tagText  tagSize $tagSize")
            } finally {
                recycle()
            }
        }

    }

    /**
     * 应用属性
     */
    private fun applyAttributes() {
        val drawableState = drawableState
        val color: Int = tagColor?.getColorForState(drawableState, 0) ?: 0
        tagPaint.color = color
        tagPaint.textSize = tagSize
        tagPaint.isAntiAlias = true
        tagPaint.isDither = true
        val containColor = tagContainerColor?.getColorForState(drawableState, 0) ?: 0
        containPaint.color = containColor
        containPaint.strokeWidth = strokeWidth
        containPaint.style = Paint.Style.STROKE
        containPaint.isAntiAlias = true
        containPaint.isDither = true
        containPaint.textAlign = Paint.Align.CENTER
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        //绘制文字居中]
        path.rewind()
        canvas?.drawText(
            tagText,
            tagPaddingLeft,
            rectF.centerY() - (tagPaint.ascent() + tagPaint.descent()) / 2,
            tagPaint
        )
        path.addRoundRect(rectF, getRadii(), Path.Direction.CCW)
        canvas?.drawPath(path, containPaint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        //设置布局的宽高
        val containWidth = getContainerWidth()
        setMeasuredDimension(containWidth, heightSize)
        Log.d(
            TAG,
            "onMeasure widthSize $widthSize heightSize $heightSize  containerWidth $containWidth"
        )
        rectF.set(
            0f + strokeWidth,
            0f + strokeWidth,
            containWidth.toFloat() - strokeWidth,
            heightSize.toFloat() - strokeWidth
        )
    }


    /**
     * 这个一次更新tag，和颜色，最好调用这个
     */
    fun setTagInfo(
        tag: String,
        @ColorInt tagColor: Int = context.resources.getColor(R.color.colorAccent),
        @ColorInt containColor: Int = context.resources.getColor(R.color.colorAccent),
        @DimenRes tagSizeId: Int = R.dimen.size_12_sp
    ) {
        tagText = tag
        tagPaint.color = tagColor
        containPaint.color = containColor
        val newTagSize = context.resources.getDimension(tagSizeId)
        this.tagSize = newTagSize
        requestLayout()

    }

    fun setRadius(radius: Float) {
        this.radius = radius
        requestLayout()
    }

    /**
     *获取tag的宽度
     */
    private fun getTagWidth(): Float {
        return tagPaint.measureText(tagText)
    }

    /**
     * 获取tagContainer的宽度
     */
    private fun getContainerWidth(): Int {
        return (tagPaddingLeft + tagPaddingRight + getTagWidth()).toInt()
    }

    private fun getRadii(): FloatArray {
        radii.apply {
            set(0, radius)
            set(1, radius)
            //topRight
            set(2, radius)
            set(3, radius)
            //bottomRight
            set(4, radius)
            set(5, radius)
            //bottomLeft
            set(6, radius)
            set(7, radius)
        }
        return radii
    }
}