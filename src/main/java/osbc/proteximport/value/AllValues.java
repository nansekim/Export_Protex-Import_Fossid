package osbc.proteximport.value;

import java.util.ArrayList;
import java.util.List;

public class AllValues {
    public static final AllValues allValues = new AllValues();

    public ProtexLoginValues protexLoginValues = ProtexLoginValues.getInstance();
    public FossidLoginValues fossidLoginValues = FossidLoginValues.getInstance();
    public List<String> successScan = new ArrayList<>();
}
