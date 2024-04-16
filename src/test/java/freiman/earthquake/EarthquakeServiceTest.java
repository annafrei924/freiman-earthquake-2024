package freiman.earthquake;

import freiman.earthquake.json.Feature;
import freiman.earthquake.json.FeatureCollection;
import freiman.earthquake.json.Properties;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class EarthquakeServiceTest {

    @Test
    void oneHour() {

        //given
        EarthquakeService service = new EarthquakeServiceFactory().getService();

        //when
        FeatureCollection collection = service.oneHour().blockingGet();

        //then
        Properties properties = collection.features[0].properties;
        assertNotNull(properties.place);
        assertNotEquals(0, properties.mag);
        assertNotEquals(0, properties.time);
    }

    @Test
    void topMonth() {

        //given
        EarthquakeService service = new EarthquakeServiceFactory().getService();

        //when
        FeatureCollection collection = service.topMonth().blockingGet();

        //then
        Properties properties = collection.features[0].properties;
        assertNotNull(properties.place);
        assertNotEquals(0, properties.mag);
        assertNotEquals(0, properties.time);
    }
}
