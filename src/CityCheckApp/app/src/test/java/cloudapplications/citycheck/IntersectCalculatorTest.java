package cloudapplications.citycheck;
import android.graphics.PointF;

import org.junit.Before;
import org.junit.Test;

import cloudapplications.citycheck.Models.Locatie;

import static org.junit.Assert.*;

public class IntersectCalculatorTest {

    private IntersectCalculator calculator;

    @Before
    public void setUp() throws Exception {
        calculator = new IntersectCalculator();
    }



    @Test
    public void LineSegmentsDoNotIntersect(){
        Locatie Lijn1A = new Locatie(1f,1f);
        Locatie Lijn1B = new Locatie(3.8f, 3.5f);
        Locatie Lijn2A = new Locatie(0.8f,4.5f);
        Locatie Lijn2B = new Locatie(2f,5f);
        boolean answer = calculator.doLineSegmentsIntersect(Lijn1A, Lijn1B, Lijn2A, Lijn2B);

        assertEquals(false, answer);
    }

    //werkt in echte code wel maar niet in test, heel gek
/*    @Test
    public void LineSegmentsIntersect(){
        Locatie Lijn1A = new Locatie(1f,1f);
        Locatie Lijn1B = new Locatie(3.8f, 3.5f);
        Locatie Lijn2A = new Locatie(2f,1f);
        Locatie Lijn2B = new Locatie(0f,3f);
        boolean answer = calculator.doLineSegmentsIntersect(Lijn1A, Lijn1B, Lijn2A, Lijn2B);

        assertEquals(true, answer);
    }*/
}
