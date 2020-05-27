package Test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(
        {
                CustomerTest.class,
                InventoryReceivingNoteTest.class,
                InventoryDeliveryNoteTest.class,
                InventoryTest.class,
                OrderTest.class,
                SalesInvoiceTest.class,
                SupportTest.class,
        }
)
public class AllTest {
}
