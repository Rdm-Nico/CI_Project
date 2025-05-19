import java.io.File;
import java.io.FileNotFoundException;

public class Test {
    public static void main(String[] args) throws FileNotFoundException {

        CustomerAdvanced customer = new CustomerAdvanced(new File("daten3ACustomer_200_10.txt"));
    }
}
