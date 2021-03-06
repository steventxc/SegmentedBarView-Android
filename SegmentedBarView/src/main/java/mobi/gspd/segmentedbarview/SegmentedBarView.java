package mobi.gspd.segmentedbarview;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.Layout;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import java.text.DecimalFormat;
import java.util.List;

import static android.graphics.Canvas.ALL_SAVE_FLAG;

@SuppressWarnings("unused")
public class SegmentedBarView extends View {

    private List<Segment> segments;
    private String unit;

    private Float value;
    private Integer valueSegment;
    private String valueSegmentText;

    private Rect rectBounds;
    private Rect valueSignBounds;
    private RectF roundRectangleBounds;
    private Paint fillPaint;
    private Paint segmentTextPaint;
    private Paint descriptionTextPaint;

    private DecimalFormat formatter;

    private int valueSignHeight;
    private int valueSignColor;
    private int emptySegmentColor;
    private int valueSignWidth;
    private int arrowHeight;
    private int arrowWidth;
    private int gapWidth;
    private int barHeight;
    private int descriptionBoxHeight;
    private int valueSignRound;
    private String emptySegmentText;

    private int barRoundingRadius = 0;

    private int valueSignCenter = -1;

    private boolean showDescriptionText;
    private boolean showSegmentText;

    private int sideStyle = SegmentedBarViewSideStyle.ROUNDED;
    private int sideTextStyle = SegmentedBarViewSideTextStyle.ONE_SIDED;

    private int valueTextSize;
    private int descriptionTextSize;

    private int segmentTextSize;
    private int valueTextColor = Color.WHITE;
    private int descriptionTextColor = Color.DKGRAY;

    private int segmentTextColor = Color.WHITE;
    private TextPaint valueTextPaint;
    private Path trianglePath;
    private StaticLayout valueTextLayout;
    private Point point1;
    private Point point2;
    private Point point3;
    private Rect segmentRect;

    private RectF valueRect;
    private Paint valuePaint;
    private int progressColor;
    private boolean enableProgressMode;

    public SegmentedBarView(Context context) {
        super(context);
        init(context, null);
    }

    public SegmentedBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);

    }

    public static Builder builder(Context context) {
        return new SegmentedBarView(context).new Builder();
    }

    private void init(Context context, AttributeSet attrs) {

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.SegmentedBarView,
                0, 0);

        try {
            Resources resources = getResources();
            segmentTextSize = a.getDimensionPixelSize(R.styleable.SegmentedBarView_sbv_segment_text_size,
                    resources.getDimensionPixelSize(R.dimen.sbv_segment_text_size));
            valueTextSize = a.getDimensionPixelSize(R.styleable.SegmentedBarView_sbv_value_text_size,
                    resources.getDimensionPixelSize(R.dimen.sbv_value_text_size));
            descriptionTextSize = a.getDimensionPixelSize(R.styleable.SegmentedBarView_sbv_description_text_size,
                    resources.getDimensionPixelSize(R.dimen.sbv_description_text_size));
            barHeight = a.getDimensionPixelSize(R.styleable.SegmentedBarView_sbv_bar_height,
                    resources.getDimensionPixelSize(R.dimen.sbv_bar_height));
            valueSignHeight = a.getDimensionPixelSize(R.styleable.SegmentedBarView_sbv_value_sign_height,
                    resources.getDimensionPixelSize(R.dimen.sbv_value_sign_height));
            valueSignWidth = a.getDimensionPixelSize(R.styleable.SegmentedBarView_sbv_value_sign_width,
                    resources.getDimensionPixelSize(R.dimen.sbv_value_sign_width));
            arrowHeight = a.getDimensionPixelSize(R.styleable.SegmentedBarView_sbv_arrow_height,
                    resources.getDimensionPixelSize(R.dimen.sbv_arrow_height));
            arrowWidth = a.getDimensionPixelSize(R.styleable.SegmentedBarView_sbv_arrow_width,
                    resources.getDimensionPixelSize(R.dimen.sbv_arrow_width));
            gapWidth = a.getDimensionPixelSize(R.styleable.SegmentedBarView_sbv_segment_gap_width,
                    resources.getDimensionPixelSize(R.dimen.sbv_segment_gap_width));
            valueSignRound = a.getDimensionPixelSize(R.styleable.SegmentedBarView_sbv_value_sign_round,
                    resources.getDimensionPixelSize(R.dimen.sbv_value_sign_round));
            descriptionBoxHeight = a.getDimensionPixelSize(R.styleable.SegmentedBarView_sbv_description_box_height,
                    resources.getDimensionPixelSize(R.dimen.sbv_description_box_height));

            showSegmentText = a.getBoolean(R.styleable.SegmentedBarView_sbv_show_segment_text, true);
            showDescriptionText = a.getBoolean(R.styleable.SegmentedBarView_sbv_show_description_text, false);

            valueSegmentText = a.getString(R.styleable.SegmentedBarView_sbv_value_segment_text);
            if (valueSegmentText == null) {
                valueSegmentText = resources.getString(R.string.sbv_value_segment);
            }
            emptySegmentText = a.getString(R.styleable.SegmentedBarView_sbv_empty_segment_text);
            if (emptySegmentText == null) {
                emptySegmentText = resources.getString(R.string.sbv_empty);
            }

            valueSignColor = a.getColor(R.styleable.SegmentedBarView_sbv_value_sign_background,
                    ContextCompat.getColor(context, R.color.sbv_value_sign_background));
            emptySegmentColor = a.getColor(R.styleable.SegmentedBarView_sbv_empty_segment_background,
                    ContextCompat.getColor(context, R.color.sbv_empty_segment_background));

            sideStyle = a.getInt(R.styleable.SegmentedBarView_sbv_side_style,
                    SegmentedBarViewSideStyle.ROUNDED);
            sideTextStyle = a.getInt(R.styleable.SegmentedBarView_sbv_side_text_style,
                    SegmentedBarViewSideTextStyle.ONE_SIDED);


            progressColor = a.getColor(R.styleable.SegmentedBarView_sbv_progress_color, Color.TRANSPARENT);
            enableProgressMode = a.getBoolean(R.styleable.SegmentedBarView_sbv_progress_mode_enable, false);

        } finally {
            a.recycle();
        }

        formatter = new DecimalFormat("##.####");

        segmentTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        segmentTextPaint.setColor(Color.WHITE);
        segmentTextPaint.setStyle(Paint.Style.FILL);

        valueTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        valueTextPaint.setColor(Color.WHITE);
        valueTextPaint.setStyle(Paint.Style.FILL);
        valueTextPaint.setTextSize(valueTextSize);
        valueTextPaint.setColor(valueTextColor);

        descriptionTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        descriptionTextPaint.setColor(Color.DKGRAY);
        descriptionTextPaint.setStyle(Paint.Style.FILL);

        fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        fillPaint.setStyle(Paint.Style.FILL);

        rectBounds = new Rect();
        roundRectangleBounds = new RectF();
        valueSignBounds = new Rect();
        segmentRect = new Rect();

        trianglePath = new Path();
        trianglePath.setFillType(Path.FillType.EVEN_ODD);
        point1 = new Point();
        point2 = new Point();
        point3 = new Point();


        valueRect = new RectF();
        valuePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        valuePaint.setStyle(Paint.Style.FILL);
        valuePaint.setColor(progressColor);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        valueSignCenter = -1;
        int segmentsSize = segments == null ? 0 : segments.size();
        if (segmentsSize > 0) {
            for (int segmentIndex = 0; segmentIndex < segmentsSize; segmentIndex++) {
                Segment segment = segments.get(segmentIndex);

                drawSegment(canvas, segment, segmentIndex, segmentsSize);
            }
        } else {
            drawEmptySegment(canvas);
        }

        if (!valueIsEmpty()) {
            drawValueSign(canvas, valueSignSpaceHeight(), valueSignCenter);
        }
    }

    private void drawEmptySegment(Canvas canvas) {
        int segmentsSize = 1;

        int singleSegmentWidth = getContentWidth() / segmentsSize;
        rectBounds.set(getPaddingLeft(), valueSignSpaceHeight() + getPaddingTop(), singleSegmentWidth + getPaddingLeft(), barHeight + valueSignSpaceHeight() + getPaddingTop());

        fillPaint.setColor(emptySegmentColor);

        barRoundingRadius = rectBounds.height() / 2;
        if (barRoundingRadius > singleSegmentWidth / 2) {
            sideStyle = SegmentedBarViewSideStyle.NORMAL;
        }

        segmentRect.set(rectBounds);

        switch (sideStyle) {
            case SegmentedBarViewSideStyle.ROUNDED:
                roundRectangleBounds.set(rectBounds.left, rectBounds.top, rectBounds.right, rectBounds.bottom);
                canvas.drawRoundRect(roundRectangleBounds, barRoundingRadius, barRoundingRadius, fillPaint);
                break;
            case SegmentedBarViewSideStyle.ANGLE:
                rectBounds.set(barRoundingRadius + getPaddingLeft(),
                        valueSignSpaceHeight() + getPaddingTop(),
                        getWidth() - getPaddingRight() - barRoundingRadius,
                        barHeight + valueSignSpaceHeight() + getPaddingTop());
                canvas.drawRect(
                        rectBounds,
                        fillPaint
                );
                //Draw left triangle
                point1.set(rectBounds.left - barRoundingRadius, rectBounds.top + barRoundingRadius);
                point2.set(rectBounds.left, rectBounds.top);
                point3.set(rectBounds.left, rectBounds.bottom);

                drawTriangle(canvas, point1, point2, point3, fillPaint);

                //Draw right triangle
                point1.set(rectBounds.right + barRoundingRadius, rectBounds.top + barRoundingRadius);
                point2.set(rectBounds.right, rectBounds.top);
                point3.set(rectBounds.right, rectBounds.bottom);

                drawTriangle(canvas, point1, point2, point3, fillPaint);
                break;
            case SegmentedBarViewSideStyle.NORMAL:
                canvas.drawRect(
                        rectBounds,
                        fillPaint
                );
            default:
                break;
        }


        if (showSegmentText) {
            String textToShow;
            textToShow = emptySegmentText;
            segmentTextPaint.setTextSize(segmentTextSize);
            drawTextCentredInRectWithSides(canvas, segmentTextPaint, textToShow, segmentRect.left, segmentRect.top, segmentRect.right, segmentRect.bottom);
        }
    }

    private int getContentWidth() {
        return getWidth() - getPaddingLeft() - getPaddingRight();
    }

    private int getContentHeight() {
        return getHeight() - getPaddingTop() - getPaddingBottom();
    }

    //
    private void drawSegment(Canvas canvas, Segment segment, int segmentIndex, int segmentsSize) {
        boolean isLeftSegment = segmentIndex == 0;
        boolean isRightSegment = segmentIndex == segmentsSize - 1;
        boolean isLeftAndRight = isLeftSegment && isRightSegment;

        // 单个segment宽度，一个segment带一个gap，出去最后一个segment的gap(最右边不需要gap)
        int singleSegmentWidth = (getContentWidth() + gapWidth) / segmentsSize - gapWidth;
        // segment左边位置
        int segmentLeft = (singleSegmentWidth + gapWidth) * segmentIndex;
        // segment右边位置
        int segmentRight = segmentLeft + singleSegmentWidth;

        // 左右都需加上左侧的padding，上下都需要加上册的padding，否则画的位置有偏差。
        //
        // Segment bounds
        rectBounds.set(segmentLeft + getPaddingLeft(), valueSignSpaceHeight() + getPaddingTop(),
                segmentRight + getPaddingLeft(), barHeight + valueSignSpaceHeight() + getPaddingTop());

        ///////////
        if (enableProgressMode)
            valueRect.set(rectBounds);
        ///////////

        // Calculating value sign position
        if (valueSegment != null && valueSegment == segmentIndex) {
            // segment centerX
            valueSignCenter = segmentLeft + getPaddingLeft() + (singleSegmentWidth / 2);
        } else if (value != null &&
                (value >= segment.getMinValue() && value < segment.getMaxValue() // 在最大最小值之间
                        || (isRightSegment && segment.getMaxValue() == value)) // 最右边的segment,并且当前值和最大值相同
                ) {
            // 按比例的相对位置
            float valueSignCenterPercent = (value - segment.getMinValue()) / (segment.getMaxValue() - segment.getMinValue());
            valueSignCenter = (int) (segmentLeft + getPaddingLeft() + valueSignCenterPercent * singleSegmentWidth);

            /////////
            if (enableProgressMode)
                valueRect.right = valueSignCenter;
            /////////
        } else if (enableProgressMode && value != null && value < segment.getMinValue()) {
            valueRect.setEmpty();
        }

        fillPaint.setColor(segment.getColor());

        segmentRect.set(rectBounds);

        // Drawing segment (with specific bounds if left or right)
        if (isLeftSegment || isRightSegment) {
            barRoundingRadius = rectBounds.height() / 2;
            // 如果圆角的半径大于segment高度的一般，不合法，直接改成默认矩形的形状
            if (barRoundingRadius > singleSegmentWidth / 2) {
                sideStyle = SegmentedBarViewSideStyle.NORMAL;
            }

            switch (sideStyle) {
                case SegmentedBarViewSideStyle.ROUNDED:
                    // 绘制圆角矩形
                    roundRectangleBounds.set(rectBounds.left, rectBounds.top, rectBounds.right, rectBounds.bottom);
                    canvas.drawRoundRect(roundRectangleBounds, barRoundingRadius, barRoundingRadius, fillPaint);

                    if (!isLeftAndRight) { // 不是单一的segment
                        if (isLeftSegment) {
                            // 绘制最左边的segment，避开左侧圆角区域，再绘制一个矩形，同时覆盖右侧的圆角区域，合成一个左圆右方的形状。
                            rectBounds.set(segmentLeft + barRoundingRadius + getPaddingLeft(), valueSignSpaceHeight() + getPaddingTop(),
                                    segmentRight + getPaddingLeft(), barHeight + valueSignSpaceHeight() + getPaddingTop());
                            canvas.drawRect(
                                    rectBounds,
                                    fillPaint
                            );
                        } else {
                            // 不是最左边的segment，避开右侧的圆角区域，绘制一个矩形，形成一个左方右圆的形状。
                            rectBounds.set(segmentLeft + getPaddingLeft(), valueSignSpaceHeight() + getPaddingTop(),
                                    segmentRight - barRoundingRadius + getPaddingLeft(), barHeight + valueSignSpaceHeight() + getPaddingTop());
                            canvas.drawRect(
                                    rectBounds,
                                    fillPaint
                            );
                        }

                    }

                    // ---------------------------------------------
                    if (enableProgressMode && !valueRect.isEmpty()) {
                        int sc_rounded = canvas.saveLayer(valueRect, valuePaint, ALL_SAVE_FLAG);

                        canvas.drawRoundRect(roundRectangleBounds, barRoundingRadius, barRoundingRadius, valuePaint);

                        if (!isLeftAndRight) {
                            // 如果有多个segment，那么需要覆盖一个或者同事覆盖两个半圆区域
                            canvas.drawRect(rectBounds, valuePaint);
                        }

                        canvas.restoreToCount(sc_rounded);
                    }
                    // ---------------------------------------------

                    break;
                case SegmentedBarViewSideStyle.ANGLE:
                    if (isLeftAndRight) {
                        // 先画中间的矩形区间
                        rectBounds.set(segmentLeft + barRoundingRadius + getPaddingLeft(), valueSignSpaceHeight() + getPaddingTop(),
                                segmentRight - barRoundingRadius + getPaddingLeft(), barHeight + valueSignSpaceHeight() + getPaddingTop());
                        canvas.drawRect(rectBounds, fillPaint);

                        // 画左侧的三角形
                        point1.set(rectBounds.left - barRoundingRadius, rectBounds.top + barRoundingRadius);// 最左边的点
                        point2.set(rectBounds.left, rectBounds.top); // 上边的点
                        point3.set(rectBounds.left, rectBounds.bottom); // 下边的点
                        drawTriangle(canvas, point1, point2, point3, fillPaint);

                        // 画右侧的三角形
                        point1.set(rectBounds.right + barRoundingRadius, rectBounds.top + barRoundingRadius);
                        point2.set(rectBounds.right, rectBounds.top);
                        point3.set(rectBounds.right, rectBounds.bottom);
                        drawTriangle(canvas, point1, point2, point3, fillPaint);

                    } else {
                        if (isLeftSegment) {
                            rectBounds.set(segmentLeft + barRoundingRadius + getPaddingLeft(), valueSignSpaceHeight() + getPaddingTop(), segmentRight + getPaddingLeft(), barHeight + valueSignSpaceHeight() + getPaddingTop());
                            canvas.drawRect(rectBounds, fillPaint);

                            //Draw left triangle
                            point1.set(rectBounds.left - barRoundingRadius, rectBounds.top + barRoundingRadius);
                            point2.set(rectBounds.left, rectBounds.top);
                            point3.set(rectBounds.left, rectBounds.bottom);

                            drawTriangle(canvas, point1, point2, point3, fillPaint);
                        } else {
                            rectBounds.set(segmentLeft + getPaddingLeft(), valueSignSpaceHeight() + getPaddingTop(), segmentRight - barRoundingRadius + getPaddingLeft(), barHeight + valueSignSpaceHeight() + getPaddingTop());
                            canvas.drawRect(rectBounds, fillPaint);

                            //Draw right triangle
                            point1.set(rectBounds.right + barRoundingRadius, rectBounds.top + barRoundingRadius);
                            point2.set(rectBounds.right, rectBounds.top);
                            point3.set(rectBounds.right, rectBounds.bottom);

                            drawTriangle(canvas, point1, point2, point3, fillPaint);
                        }
                    }

                    // ---------------------------------------------
                    if (enableProgressMode && !valueRect.isEmpty()) {
                        int sc_angle = canvas.saveLayer(valueRect, valuePaint, ALL_SAVE_FLAG);
                        canvas.drawRect(rectBounds, valuePaint);
                        drawTriangle(canvas, point1, point2, point3, valuePaint);
                        if (isLeftAndRight) {
                            // 补一个左侧的三角
                            point1.set(rectBounds.left - barRoundingRadius, rectBounds.top + barRoundingRadius);// 最左边的点
                            point2.set(rectBounds.left, rectBounds.top); // 上边的点
                            point3.set(rectBounds.left, rectBounds.bottom); // 下边的点
                            drawTriangle(canvas, point1, point2, point3, valuePaint);
                        }
                        canvas.restoreToCount(sc_angle);
                    }
                    // ---------------------------------------------

                    break;
                case SegmentedBarViewSideStyle.NORMAL:
                    // 矩形直接绘制即可。
                    canvas.drawRect(rectBounds, fillPaint);

                    // ---------------------------------------------
                    if (enableProgressMode && !valueRect.isEmpty()) {
                        int sc_normal = canvas.saveLayer(valueRect, valuePaint, ALL_SAVE_FLAG);
                        canvas.drawRect(rectBounds, valuePaint);
                        canvas.restoreToCount(sc_normal);
                    }
                    // ---------------------------------------------
                default:
                    break;
            }
        } else {
            // 位于中间的segment(不是最左或最右的segment)，直接画矩形即可
            canvas.drawRect(rectBounds, fillPaint);

            // ---------------------------------------------
            if (enableProgressMode && !valueRect.isEmpty()) {
                int sc = canvas.saveLayer(valueRect, valuePaint, ALL_SAVE_FLAG);
                canvas.drawRect(rectBounds, valuePaint);
                canvas.restoreToCount(sc);
            }
            // ---------------------------------------------

        }

        // Drawing segment text
        if (showSegmentText) {
            String textToShow;
            if (segment.getCustomText() != null) {
                textToShow = segment.getCustomText();
            } else {
                if (isLeftSegment || isRightSegment) {
                    if (isLeftAndRight || sideTextStyle == SegmentedBarViewSideTextStyle.TWO_SIDED) {
                        textToShow = String.format("%s - %s", formatter.format(segment.getMinValue()), formatter.format(segment.getMaxValue()));
                    } else if (isLeftSegment) {
                        textToShow = String.format("<%s", formatter.format(segment.getMaxValue()));
                    } else {
                        textToShow = String.format(">%s", formatter.format(segment.getMinValue()));
                    }
                } else {
                    textToShow = String.format("%s - %s", formatter.format(segment.getMinValue()), formatter.format(segment.getMaxValue()));
                }
            }

            segmentTextPaint.setTextSize(segmentTextSize);
            segmentTextPaint.setColor(segmentTextColor);
            drawTextCentredInRect(canvas, segmentTextPaint, textToShow, segmentRect);
        }

        //Drawing segment description text
        if (showDescriptionText) {
            descriptionTextPaint.setTextSize(descriptionTextSize);
            descriptionTextPaint.setColor(descriptionTextColor);
            drawTextCentredInRectWithSides(canvas, descriptionTextPaint, segment.getDescriptionText(), segmentRect.left, segmentRect.bottom, segmentRect.right, segmentRect.bottom + descriptionBoxHeight);
        }


    }

    private void drawValueSign(Canvas canvas, int valueSignSpaceHeight, int valueSignCenter) {
        boolean valueNotInSegments = valueSignCenter == -1;
        if (valueNotInSegments) {
            valueSignCenter = getContentWidth() / 2 + getPaddingLeft();
        }
        valueSignBounds.set(valueSignCenter - valueSignWidth / 2,
                getPaddingTop(),
                valueSignCenter + valueSignWidth / 2,
                valueSignHeight - arrowHeight + getPaddingTop());
        fillPaint.setColor(valueSignColor);

        // Move if not fit horizontal
        if (valueSignBounds.left < getPaddingLeft()) {
            int difference = -valueSignBounds.left + getPaddingLeft();
            roundRectangleBounds.set(valueSignBounds.left + difference, valueSignBounds.top, valueSignBounds.right + difference, valueSignBounds.bottom);
        } else if (valueSignBounds.right > getMeasuredWidth() - getPaddingRight()) {
            int difference = valueSignBounds.right - getMeasuredWidth() + getPaddingRight();
            roundRectangleBounds.set(valueSignBounds.left - difference, valueSignBounds.top, valueSignBounds.right - difference, valueSignBounds.bottom);
        } else {
            roundRectangleBounds.set(valueSignBounds.left, valueSignBounds.top, valueSignBounds.right, valueSignBounds.bottom);
        }
        canvas.drawRoundRect(
                roundRectangleBounds,
                valueSignRound,
                valueSignRound,
                fillPaint
        );

        // Draw arrow
        if (!valueNotInSegments) {
            int difference = 0;
            if (valueSignCenter - arrowWidth / 2 < barRoundingRadius + getPaddingLeft()) {
                difference = barRoundingRadius - valueSignCenter + getPaddingLeft();
            } else if (valueSignCenter + arrowWidth / 2 > getMeasuredWidth() - barRoundingRadius - getPaddingRight()) {
                difference = (getMeasuredWidth() - barRoundingRadius) - valueSignCenter - getPaddingRight();
            }

            point1.set(valueSignCenter - arrowWidth / 2 + difference, valueSignSpaceHeight - arrowHeight + getPaddingTop());
            point2.set(valueSignCenter + arrowWidth / 2 + difference, valueSignSpaceHeight - arrowHeight + getPaddingTop());
            point3.set(valueSignCenter + difference, valueSignSpaceHeight + getPaddingTop());

            drawTriangle(canvas, point1, point2, point3, fillPaint);
        }

        // Draw value text
        if (valueTextLayout != null) {
            canvas.translate(roundRectangleBounds.left, roundRectangleBounds.top + roundRectangleBounds.height() / 2 - valueTextLayout.getHeight() / 2);
            valueTextLayout.draw(canvas);
        }
    }

    private void drawTriangle(Canvas canvas, Point point1, Point point2, Point point3, Paint paint) {
        trianglePath.reset();
        trianglePath.moveTo(point1.x, point1.y);
        trianglePath.lineTo(point2.x, point2.y);
        trianglePath.lineTo(point3.x, point3.y);
        trianglePath.lineTo(point1.x, point1.y);
        trianglePath.close();

        canvas.drawPath(trianglePath, paint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int minWidth = getPaddingLeft() + getPaddingRight();
        int minHeight = barHeight + getPaddingBottom() + getPaddingTop();
        if (!valueIsEmpty()) {
            minHeight += valueSignHeight + arrowHeight;
        }
        if (showDescriptionText) {
            minHeight += descriptionBoxHeight;
        }
        int w = resolveSizeAndState(minWidth, widthMeasureSpec, 0);
        int h = resolveSizeAndState(minHeight, heightMeasureSpec, 0);

        setMeasuredDimension(w, h);
    }

    private int valueSignSpaceHeight() {
        if (valueIsEmpty()) return 0;
        return valueSignHeight;
    }

    private boolean valueIsEmpty() {
        return value == null && valueSegment == null;
    }

    public void drawTextCentredInRect(Canvas canvas, Paint paint, String text, Rect outsideRect) {
        drawTextCentredInRectWithSides(canvas, paint, text, outsideRect.left, outsideRect.top, outsideRect.right, outsideRect.bottom);
    }

    public void drawTextCentredInRectWithSides(Canvas canvas, Paint paint, String text, float left, float top, float right, float bottom) {
        paint.setTextAlign(Paint.Align.CENTER);

        float textHeight = paint.descent() - paint.ascent();
        float textOffset = (textHeight / 2) - paint.descent();

        canvas.drawText(text, (left + right) / 2, (top + bottom) / 2 + textOffset, paint);
    }

    private void createValueTextLayout() {
        if (valueIsEmpty()) {
            valueTextLayout = null;
            return;
        }
        String text = value != null ? formatter.format(value) : valueSegmentText;
        if (value != null && unit != null && !unit.isEmpty())
            text += String.format(" <small>%s</small>", unit);
        Spanned spanned = Html.fromHtml(text);

        valueTextLayout = new StaticLayout(spanned, valueTextPaint, valueSignWidth, Layout.Alignment.ALIGN_CENTER, 1, 0, false);
    }

    public String getValueSegmentText() {
        return valueSegmentText;
    }

    public void setValueSegmentText(String valueSegmentText) {
        this.valueSegmentText = valueSegmentText;
        createValueTextLayout();
        invalidate();
        requestLayout();
    }

    public void setSegments(List<Segment> segments) {
        this.segments = segments;
        invalidate();
        requestLayout();
    }

    public void setUnit(String unit) {
        this.unit = unit;
        createValueTextLayout();
        invalidate();
        requestLayout();
    }

    public void setValue(Float value) {
        this.value = value;
        createValueTextLayout();
        invalidate();
        requestLayout();
    }

    public void setValueWithUnit(Float value, String unitHtml) {
        this.value = value;
        this.unit = unitHtml;
        if (!valueIsEmpty()) createValueTextLayout();
        invalidate();
        requestLayout();
    }

    public void setGapWidth(int gapWidth) {
        this.gapWidth = gapWidth;
        invalidate();
        requestLayout();
    }

    public void setBarHeight(int barHeight) {
        this.barHeight = barHeight;
        invalidate();
        requestLayout();
    }

    public void setShowDescriptionText(boolean showDescriptionText) {
        this.showDescriptionText = showDescriptionText;
        invalidate();
        requestLayout();
    }

    public void setValueSignSize(int width, int height) {
        this.valueSignWidth = width;
        this.valueSignHeight = height;
        if (!valueIsEmpty()) createValueTextLayout();
        invalidate();
        requestLayout();
    }

    public void setValueSignColor(int valueSignColor) {
        this.valueSignColor = valueSignColor;
        invalidate();
        requestLayout();
    }

    public void setShowSegmentText(boolean showSegmentText) {
        this.showSegmentText = showSegmentText;
        invalidate();
        requestLayout();
    }

    public void setSideStyle(int sideStyle) {
        this.sideStyle = sideStyle;
        invalidate();
        requestLayout();
    }

    public void setEmptySegmentColor(int emptySegmentColor) {
        this.emptySegmentColor = emptySegmentColor;
        invalidate();
        requestLayout();
    }

    public void setSideTextStyle(int sideTextStyle) {
        this.sideTextStyle = sideTextStyle;
        invalidate();
        requestLayout();
    }

    public void setDescriptionTextSize(int descriptionTextSize) {
        this.descriptionTextSize = descriptionTextSize;
        invalidate();
        requestLayout();
    }

    public void setSegmentTextSize(int segmentTextSize) {
        this.segmentTextSize = segmentTextSize;
        invalidate();
        requestLayout();
    }

    public void setValueTextSize(int valueTextSize) {
        this.valueTextSize = valueTextSize;
        valueTextPaint.setTextSize(valueTextSize);
        invalidate();
        requestLayout();
    }

    public void setDescriptionTextColor(int descriptionTextColor) {
        this.descriptionTextColor = descriptionTextColor;
        invalidate();
        requestLayout();
    }

    public void setSegmentTextColor(int segmentTextColor) {
        this.segmentTextColor = segmentTextColor;
        invalidate();
        requestLayout();
    }

    public void setValueTextColor(int valueTextColor) {
        this.valueTextColor = valueTextColor;
        valueTextPaint.setColor(valueTextColor);
        invalidate();
        requestLayout();
    }

    public void setDescriptionBoxHeight(int descriptionBoxHeight) {
        this.descriptionBoxHeight = descriptionBoxHeight;
        invalidate();
        requestLayout();
    }

    public Integer getValueSegment() {
        return valueSegment;
    }

    public void setValueSegment(Integer valueSegment) {
        this.valueSegment = valueSegment;
    }

    /**
     * 设置进度条的颜色。默认为透明。
     *
     * @param color color int
     */
    public void setProgressColor(@ColorInt int color) {
        this.progressColor = color;
        valuePaint.setColor(color);
        invalidate();
        requestLayout();
    }

    /**
     * 设置进度条模式可见与否。
     *
     * @param enable true可见，false不可见。
     */
    public void setProgressEnable(boolean enable) {
        this.enableProgressMode = enable;
        invalidate();
//        requestLayout();
    }

    public class Builder {

        private Builder() {
        }

        public Builder segments(List<Segment> segments) {
            SegmentedBarView.this.segments = segments;
            return this;
        }

        public Builder unit(String unit) {
            SegmentedBarView.this.unit = unit;
            SegmentedBarView.this.createValueTextLayout();
            return this;
        }

        public Builder value(Float value) {
            SegmentedBarView.this.value = value;
            SegmentedBarView.this.createValueTextLayout();
            return this;
        }

        public Builder valueSegment(Integer valueSegment) {
            SegmentedBarView.this.valueSegment = valueSegment;
            SegmentedBarView.this.createValueTextLayout();
            return this;
        }

        public Builder valueSegmentText(String valueSegmentText) {
            SegmentedBarView.this.valueSegmentText = valueSegmentText;
            SegmentedBarView.this.createValueTextLayout();
            return this;
        }

        public Builder gapWidth(int gapWidth) {
            SegmentedBarView.this.gapWidth = gapWidth;
            return this;
        }

        public Builder barHeight(int barHeight) {
            SegmentedBarView.this.barHeight = barHeight;
            return this;
        }

        public Builder showDescriptionText(boolean showDescriptionText) {
            SegmentedBarView.this.showDescriptionText = showDescriptionText;
            return this;
        }

        public Builder valueSignSize(int width, int height) {
            SegmentedBarView.this.valueSignWidth = width;
            SegmentedBarView.this.valueSignHeight = height;
            return this;
        }

        public Builder valueSignColor(int valueSignColor) {
            SegmentedBarView.this.valueSignColor = valueSignColor;
            return this;
        }

        public Builder showSegmentText(boolean showText) {
            SegmentedBarView.this.showSegmentText = showText;
            return this;
        }

        public Builder sideStyle(int sideStyle) {
            SegmentedBarView.this.sideStyle = sideStyle;
            return this;
        }

        public Builder emptySegmentColor(int emptySegmentColor) {
            SegmentedBarView.this.emptySegmentColor = emptySegmentColor;
            return this;
        }

        public Builder sideTextStyle(int sideTextStyle) {
            SegmentedBarView.this.sideTextStyle = sideTextStyle;
            return this;
        }

        public Builder descriptionTextSize(int descriptionTextSize) {
            SegmentedBarView.this.descriptionTextSize = descriptionTextSize;
            return this;
        }

        public Builder segmentTextSize(int segmentTextSize) {
            SegmentedBarView.this.segmentTextSize = segmentTextSize;
            return this;
        }

        public Builder valueTextSize(int valueTextSize) {
            SegmentedBarView.this.valueTextSize = valueTextSize;
            return this;
        }

        public Builder descriptionTextColor(int descriptionTextColor) {
            SegmentedBarView.this.descriptionTextColor = descriptionTextColor;
            return this;
        }

        public Builder segmentTextColor(int segmentTextColor) {
            SegmentedBarView.this.segmentTextColor = segmentTextColor;
            return this;
        }

        public Builder valueTextColor(int valueTextColor) {
            SegmentedBarView.this.valueTextColor = valueTextColor;
            return this;
        }

        public Builder descriptionBoxHeight(int descriptionBoxHeight) {
            SegmentedBarView.this.descriptionBoxHeight = descriptionBoxHeight;
            return this;
        }

        public Builder progressColor(@ColorInt int color) {
            SegmentedBarView.this.progressColor = color;
            return this;
        }

        public SegmentedBarView build() {
            return SegmentedBarView.this;
        }

    }
}
