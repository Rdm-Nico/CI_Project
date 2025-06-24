# CI_Project
by Kang Chun-Yu,Nicoló Rossi,Lin Chin-Jung
## How to run 🚀
### run with all the combination
To run the program with all the combination of data and methods just run `GARunner.java`.
### run all the combination with differents hyperparameters
To run the program using differents hyperparameters(MaxRounds, population size, ecc...) you can change the values of the variables beetween line 13 and 18 in the `GenericAlgorithm.java` file, and then run again `GARunner.java`.

e.g.
        int maxRounds = 100;
        int NUM_CONTRACTS = 100;
        int selection_size = 50;
        int array_size = 69;
        double temperature = 50;
        double alpha = 0.9;


### run a specific combination of problem instances
You can run a specific combination of istances by change the path in line 115 and 117 in `GenericAlgorithm.java` and run this time `GenericAlgorithm.java`.

e.g.
```
        int maxRounds = 100;
        int NUM_CONTRACTS = 100;
        int selection_size = 50;
        int array_size = 69;
        double temperature = 50;
        double alpha = 0.9;
```

## Generate Charts 📊
For generate the charts we use a Jupiter Nootbook that you can find in `visualization/drawDiagram.ipynb`.


