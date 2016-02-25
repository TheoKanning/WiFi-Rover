package theokanning.rover.ui.view;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnTouch;
import theokanning.rover.R;

/**
 * Custom view that listens for touch events with a circle and reports them in polar coordinates
 *
 * @author Theo Kanning
 */
public class Dpad extends RelativeLayout {

    public interface DpadListener {
        void onDpadPressed(float radians, int magnitude);
    }

    private static final String TAG = "Dpad";

    @Bind(R.id.dpad)
    View touchableArea;

    public Dpad(Context context) {
        super(context);
        init(context);
    }

    public Dpad(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public Dpad(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        View view = inflate(context, R.layout.widget_dpad, this);
        ButterKnife.bind(this, view);
    }

    @OnTouch(R.id.dpad)
    public boolean onDpadTouched(View v, MotionEvent event) {
        Point point = new Point((int) event.getX(), (int) event.getY());
        calculatePress(point);
        return false;
    }

    private void calculatePress(Point point) {
        float rawMagnitude = getMagnitude(point);
        float boundedMagnitude = Math.min(rawMagnitude, 1.0f);
        float angle = getAngle(point);
        Log.d(TAG, "Point detected with magnitude " + boundedMagnitude + " and angle " + angle);
    }

    private float getMagnitude(Point point) {
        float distance = getDistance(point, getCenter());
        return (distance / getRadius());
    }

    private float getDistance(Point point1, Point point2) {
        return (float) Math.sqrt((point1.x - point2.x) * (point1.x - point2.x) + (point1.y - point2.y) * (point1.y - point2.y));
    }

    private float getAngle(Point point) {
        final int xOffset = point.x - getCenter().x;
        final int yOffset = point.y - getCenter().y;

        return (float) Math.atan2(-yOffset, xOffset);
    }

    private int getRadius() {
        //todo store after drawing
        return getWidth() / 2;
    }

    private Point getCenter() {
        //todo store after drawing
        int x = getWidth() / 2;
        int y = getHeight() / 2;
        return new Point(x, y);
    }
}
