package Tests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses({ ApproxVertexCoverTest.class, GraphTest.class,
        KruskalMSTTest.class, QuicksortTest.class, WeightedGraphTest.class,
        AntennaOrientationAlgorithmTest.class, DijkstraSSSPTest.class })

public class AllTests {}
