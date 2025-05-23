import java.util.logging.Logger;

public abstract class Agent {

	public abstract boolean vote(int[] contract, int[] proposal);

	public abstract void printUtility(int[] contract, Logger logger);

	public abstract int getContractSize();

}
