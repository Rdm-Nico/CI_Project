import java.io.File;
import java.io.FileNotFoundException;
import java.util.logging.Logger;

public class NorwellTest {
    public static void main(String[] args){
        //test for the crossover
        
        try{
        Mediator med;
        int a = 10,b = 10;
        med =  new Mediator(a,b);    


        int[] mama = {7,2,9,4,10,8,6,3,1,5};
        int[] papa = {5,4,2,1,3,9,10,6,8,7};
        int[][] child;
        int[] son;
        int[] doughter;

        //child = med.oderCrossover(mama, papa, 3);
        //child = med.positionBasedCrossover(mama, papa, 3);
        child = med.cycleCrossover(mama, papa,3);
    
        son = child[0];
        doughter = child[1];


        ////print
        System.out.println("SON: ");
        for (int i=0; i<a; i++){
            System.out.println(son[i]);
        }
        System.out.println("Doughter: ");
        for (int i=0; i<a; i++){
            System.out.println(doughter[i]);
        }
        }catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		}
        
        /* 
        try{
            Mediator med;
            int a = 6,b = 6;
            med =  new Mediator(a,b); 

            int[] contract1 = {1,2,3,4,5,6};
       

            //int[] newcontract = med.scrambleMutation(contract1, 4);
            //int[] newcontract = med.inverseMutation(contract1, 4);
            int[] newcontract = med.displacementMutation(contract1, 4);


            System.out.println("newcontract: ");
            for (int i=0; i<a; i++){
                System.out.println(newcontract[i]);
            }
        }catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		}
            */
    }
        
}
