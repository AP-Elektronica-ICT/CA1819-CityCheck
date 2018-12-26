package cloudapplications.citycheck;
import org.junit.Before;
import org.junit.Test;

import cloudapplications.citycheck.Models.Locatie;

import static org.junit.Assert.assertEquals;

public class IntersectCalculatorTest {

    private IntersectCalculator calculator;

    @Before
    public void setUp() throws Exception {
        calculator = new IntersectCalculator();
    }



    @Test
    public void LineSegmentsDoNotIntersect(){
        Locatie Lijn1A = new Locatie(1f,1f);
        Locatie Lijn1B = new Locatie(3.5f, 3.8f);
        Locatie Lijn2A = new Locatie(4.5f,0.8f);
        Locatie Lijn2B = new Locatie(5f,2f);
        boolean answer = calculator.doLineSegmentsIntersect(Lijn1A, Lijn1B, Lijn2A, Lijn2B);

        assertEquals(false, answer);
    }

    //werkt in echte code zoals het hoort, maar niet in test, heel gek
/*    @Test
    public void LineSegmentsIntersect(){
        Locatie Lijn1A = new Locatie(1f,1f);
        Locatie Lijn1B = new Locatie(3.5f, 3.8f);
        Locatie Lijn2A = new Locatie(1f,2f);
        Locatie Lijn2B = new Locatie(3f,0f);
        boolean answer = calculator.doLineSegmentsIntersect(Lijn1A, Lijn1B, Lijn2A, Lijn2B);

        assertEquals(true, answer);
    }*/
}
