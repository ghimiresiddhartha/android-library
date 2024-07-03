package np.com.siddharthaghimire

import android.content.Context
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.widget.FrameLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import np.com.siddharthaghimire.model.ParallaxOrientation
import np.com.siddharthaghimire.sensor.SensorDataManager

private const val INITIAL_VERTICAL_OFFSET = 0.5f

class ParallaxView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var movementIntensityMultiplier: Int = 50
    private var verticalOffsetLimit: Float = 0.5f
    private var horizontalOffsetLimit: Float = 0.5f
    private var orientation: ParallaxOrientation = ParallaxOrientation.Full

    private val sensorDataManager = SensorDataManager(context)
    private val coroutineScope = CoroutineScope(Dispatchers.Main + Job())

    init {
        sensorDataManager.init(resources.configuration.orientation)

        coroutineScope.launch {
            sensorDataManager.data.consumeEach { sensorData ->
                updateParallax(sensorData.roll, sensorData.pitch)
            }
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        sensorDataManager.cancel()
        coroutineScope.cancel()
    }

    private fun updateParallax(roll: Float, pitch: Float) {
        val clampedRoll = roll.coerceIn(getRange(horizontalOffsetLimit)).times(movementIntensityMultiplier)
        val clampedPitch = (pitch.coerceIn(getRange(verticalOffsetLimit)) + INITIAL_VERTICAL_OFFSET).times(movementIntensityMultiplier)

        when (orientation) {
            ParallaxOrientation.Horizontal -> applyParallax(clampedRoll, 0f)
            ParallaxOrientation.Vertical -> applyParallax(0f, clampedPitch)
            ParallaxOrientation.Full -> applyParallax(clampedRoll, clampedPitch)
        }
    }

    private fun applyParallax(roll: Float, pitch: Float) {
        getChildAt(0)?.let { view ->
            view.scaleX = 1.4f
            view.scaleY = 1.4f
            view.translationX = (1.5 * -roll.convertDpToPixels()).toFloat()
            view.translationY = (1.5 * pitch.convertDpToPixels()).toFloat()
        }
    }

    private fun Float.convertDpToPixels(): Int {
        val metrics = context.resources.displayMetrics
        return (this * (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)).toInt()
    }

    private fun getRange(value: Float): ClosedFloatingPointRange<Float> {
        val transformedValue = value * 1.5f
        return -transformedValue..transformedValue
    }

    override fun onLayout(
        changed: Boolean,
        left: Int,
        top: Int,
        right: Int,
        bottom: Int
    ) {
        for (i in 0 until childCount) {
            getChildAt(i).layout(left, top, right, bottom)
        }
    }
}