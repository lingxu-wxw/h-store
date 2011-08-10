package edu.brown.benchmark.airline;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import org.json.JSONObject;
import org.voltdb.VoltTable;
import org.voltdb.catalog.Catalog;
import org.voltdb.catalog.Table;
import org.voltdb.types.TimestampType;

import edu.brown.BaseTestCase;
import edu.brown.benchmark.airline.util.CustomerId;
import edu.brown.benchmark.airline.util.CustomerIdIterable;
import edu.brown.benchmark.airline.util.FlightId;
import edu.brown.statistics.Histogram;

public class TestAirlineLoader extends AirlineBaseTestCase {

    private MockAirlineLoader loader;

    private final double scale_factor = 1000;
    private final int num_airports = 10;
    private final int num_customers[] = new int[this.num_airports];
    private final int max_num_customers = 4;
    private final Random rand = new Random(0);
    private final HashSet<CustomerId> customer_ids = new HashSet<CustomerId>();

    private final HashSet<FlightId> flight_ids = new HashSet<FlightId>();
    private final long num_flights = 10l;
    private final TimestampType flightStartDate = new TimestampType(1262630005000l); // Monday 01.04.2010 13:33:25
    private final int flightPastDays = 7;
    private final int flightFutureDays = 14;
    
    
    class MockAirlineLoader extends AirlineLoader {
        public MockAirlineLoader(String[] args) {
            super(args);
        }
        @Override
        public Catalog getCatalog() {
            return (BaseTestCase.catalog);
        }
        @Override
        protected void loadVoltTable(String tableName, VoltTable vt) {
            assertNotNull(vt);
        }
//        @Override
//        protected Iterable<Object[]> getScalingIterable(Table catalog_tbl) {
//            if (catalog_tbl.getName().equalsIgnoreCase(AirlineConstants.TABLENAME_AIRPORT_DISTANCE)) {
//                return new AirportDistanceIterable(catalog_tbl, 0) {
//                    @Override
//                    protected boolean hasNext() {
//                        return (false);
//                    }
//                };
//            } else {
//                return super.getScalingIterable(catalog_tbl);
//            }
//        }
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        String loaderArgs[] = {
            "CLIENT.SCALEFACTOR=" + scale_factor, 
            "HOST=localhost",
            "NUMCLIENTS=1",
            "DATADIR=" + AIRLINE_DATA_DIR,
            "NOCONNECTIONS=true",
        };
        loader = new MockAirlineLoader(loaderArgs);
        assertNotNull(loader);
    }
    
    private void initializeLoader(final MockAirlineLoader loader) {
        loader.setFlightPastDays(flightPastDays);
        loader.setFlightFutureDays(flightFutureDays);
        loader.setFlightStartDate(flightStartDate);
        loader.setFlightUpcomingDate(flightStartDate);
        
        for (long airport_id = 0; airport_id < num_airports; airport_id++) {
            num_customers[(int)airport_id] = rand.nextInt(max_num_customers) + 1;
            for (int customer_id = 0; customer_id < num_customers[(int)airport_id]; customer_id++) {
                loader.incrementAirportCustomerCount(airport_id);
                customer_ids.add(new CustomerId(customer_id, airport_id));
            } // FOR
//            System.err.println(airport_id + ": " + this.num_customers[(int)airport_id] + " customers");
        } // FOR
//        System.err.println("------------------------");
        
        // Add a bunch of FlightIds
        int count = 0;
        for (long depart_airport_id = 0; depart_airport_id < num_airports; depart_airport_id++) {
            for (long arrive_airport_id = 0; arrive_airport_id < num_airports; arrive_airport_id++) {
                if (depart_airport_id == arrive_airport_id) continue;
                int time_offset = rand.nextInt(86400000 * (int)flightFutureDays);
                TimestampType flight_date = new TimestampType(flightStartDate.getTime() + time_offset);
                FlightId id = new FlightId(count++, depart_airport_id, arrive_airport_id, flightStartDate, flight_date);
                loader.addFlightId(id);
                flight_ids.add(id);
                if (count >= num_flights) break;
            } // FOR
            if (count >= num_flights) break;
        } // FOR
        assertEquals(num_flights, flight_ids.size());
    }
    
    
    /**
     * testIncrementAirportCustomerCount
     */
    public void testIncrementAirportCustomerCount() {
        this.initializeLoader(loader);
        for (long airport_id = 0; airport_id < this.num_airports; airport_id++) {
            Long cnt = loader.getCustomerIdCount(airport_id);
            assertNotNull(cnt);
            assertEquals((long)this.num_customers[(int)airport_id], (long)cnt);
        } // FOR
    }
    
    /**
     * testCustomerIdIterable
     */
    public void testCustomerIdIterable() {
        this.initializeLoader(loader);
        Map<Long, AtomicInteger> airport_counts = new HashMap<Long, AtomicInteger>();
        for (long airport_id = 0; airport_id < this.num_airports; airport_id++) {
            airport_counts.put(airport_id, new AtomicInteger(0));
        } // FOR
        
//        int idx = 0;
        for (CustomerId customer_id : new CustomerIdIterable(loader.airport_max_customer_id)) {
            long airport_id = customer_id.getDepartAirportId();
            airport_counts.get(airport_id).incrementAndGet();
//            System.err.println("[" + (idx++) + "]: " + customer_id);
            assertTrue(this.customer_ids.contains(customer_id));
        } // FOR
        assertFalse(airport_counts.isEmpty());
        
        for (long airport_id = 0; airport_id < this.num_airports; airport_id++) {
//            System.err.println(airport_id + ": " + airport_counts.get(airport_id));
            assertTrue(airport_counts.containsKey(airport_id));
            assertEquals(this.num_customers[(int)airport_id], airport_counts.get(airport_id).get());
        } // FOR
    }
    
    /**
     * testLoadHistograms
     */
    public void testLoadHistograms() throws Exception {
        loader.loadHistograms();
        assertEquals(AirlineConstants.HISTOGRAM_DATA_FILES.length, loader.getHistogramCount());
        for (String histogram_name : AirlineConstants.HISTOGRAM_DATA_FILES) {
            Histogram<String> h = loader.getHistogram(histogram_name);
            assertNotNull(h);
            assertTrue(h.getSampleCount() > 0);
        } // FOR
    }
    
    /**
     * testGetFixedIterable
     */
    public void testGetFixedIterable() throws Exception {
        for (String table_name : AirlineConstants.TABLE_DATA_FILES) {
            Table catalog_tbl = this.getTable(table_name);
            Iterable<Object[]> it = loader.getFixedIterable(catalog_tbl);
            assertNotNull(catalog_tbl.getName(), it);
            assertTrue(catalog_tbl.getName(), it.iterator().hasNext());
        } // FOR
    }
//    
//    /**
//     * testLoadAllTables
//     */
//    public void testLoadAllTables() throws Exception {
//        loader.runLoop();
//    }

    /**
     * testToJSONString
     */
    public void testToJSONString() throws Exception {
        this.initializeLoader(loader);
        String jsonString = loader.toJSONString();
        for (FlightId flight_id : this.flight_ids) {
            String encoded = Long.toString(flight_id.encode());
            assertTrue(jsonString.contains(encoded));
        } // FOR
    }
    
    /**
     * testFromJSONString
     */
    public void testFromJSONString() throws Exception {
        String jsonString = loader.toJSONString();
        JSONObject jsonObject = new JSONObject(jsonString);
        
        MockAirlineLoader clone = new MockAirlineLoader(new String[]{ "DATADIR=" + AIRLINE_DATA_DIR });
        clone.fromJSON(jsonObject, null);
        
        assertEquals(loader.getCustomerIdCount(), clone.getCustomerIdCount());
        assertEquals(loader.getFlightIdCount(), clone.getFlightIdCount());
        assertEquals(loader.getFlightStartDate(), clone.getFlightStartDate());
        
        for (FlightId clone_id : clone.getFlightIds()) {
            assert(this.flight_ids.contains(clone_id)) : "Unknown flight id " + clone_id;
        } // FOR
        
    }
    
}