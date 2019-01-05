package cloudapplications.citycheck;

import android.graphics.PointF;

import cloudapplications.citycheck.Models.Locatie;

public class IntersectCalculator {
    //bron https://stackoverflow.com/questions/49535476/how-to-find-intersect-between-two-polylines-in-android

    /**
     * See if two line segments intersect. This uses the
     * vector cross product approach described below:
     * http://stackoverflow.com/a/565282/786339
     *
     * @param {Object} p point object with x and y coordinates
     *  representing the start of the 1st line.
     * @param {Object} p2 point object with x and y coordinates
     *  representing the end of the 1st line.
     * @param {Object} q point object with x and y coordinates
     *  representing the start of the 2nd line.
     * @param {Object} q2 point object with x and y coordinates
     *  representing the end of the 2nd line.
     */
    public boolean doLineSegmentsIntersect(Locatie start, Locatie einde, Locatie anderTeamStart, Locatie anderTeamEinde) {
        //convert Locatie naar PointF, Long is de y-as, Lat is de x-as
        PointF p = new PointF((float)start.getLong(), (float)start.getLat());
        PointF p2 = new PointF((float)einde.getLong(), (float)einde.getLat());
        PointF q = new PointF((float)anderTeamStart.getLong(), (float)anderTeamStart.getLat());
        PointF q2 = new PointF((float)anderTeamEinde.getLong(), (float)anderTeamEinde.getLat());
        PointF r = subtractPoints(p2, p);
        PointF s = subtractPoints(q2, q);

        float uNumerator = crossProduct(subtractPoints(q, p), r);
        float denominator = crossProduct(r, s);

        if (denominator == 0) {
            // lines are paralell
            return false;
        }

        float u = uNumerator / denominator;
        float t = crossProduct(subtractPoints(q, p), s) / denominator;

        if((t >= 0) && (t <= 1) && (u > 0) && (u <= 1))
            return true;
        else
            return false;

    }

    /**
     * Calculate the cross product of the two points.
     *
     * @param {Object} point1 pointf object with x and y coordinates
     * @param {Object} point2 pointf object with x and y coordinates
     *
     * @return the cross product result as a float
     */
    private float crossProduct(PointF point1, PointF point2) {
        return point1.x * point2.y - point1.y * point2.x;
    }

    /**
     * Subtract the second point from the first.
     *
     * @param {Object} point1 pointf object with x and y coordinates
     * @param {Object} point2 pointf object with x and y coordinates
     *
     * @return the subtraction result as a pointf object
     */
    private PointF subtractPoints(PointF point1,PointF point2) {
        PointF result = new PointF();
        result.x = point1.x - point2.x;
        result.y = point1.y - point2.y;

        return result;
    }
}
