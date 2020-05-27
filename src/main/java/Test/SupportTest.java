package Test;

import org.junit.Test;
import java.io.IOException;
import static Test.TestConfig.*;
import static org.junit.Assert.assertEquals;

public class SupportTest {
    @Test
    public void testProvider() throws IOException {
        getActualOutputPostMethod("providers/create",
                "{\"name\": \"KAT\"}");
        getActualOutputGetMethod("providers/read/all/?page=-1");
        assertEquals(true, providers.size() > 0);
    }

    @Test
    public void testProduct() throws IOException {
        getActualOutputPostMethod("products/create",
                "{\"name\": \"KAT\"}");
        getActualOutputGetMethod("products/read/all/?page=-1");
        assertEquals(true, products.size() > 0);
    }
}
