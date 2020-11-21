import java.util.*;

public class GeneticAlgorithm {

        //population size
        public static int popSize = 50;
        //Chromosome Length
        private static final int CHROM_LENGTH = 15;
        //Mutation Rate (0 t0 1)
        private static double mutRate = 0;
        //Crossover rate (0 to 1)
        private static double xoRate = 0;
        //Generation Span
        private static double genSpan = 0;
        //Number of elite chromosmes to take forward
        private static int elitism = 2;
        //Crossover selection
        private static int selection = 0;


    //Initializing population array
    private static int[][] population = new int[popSize][CHROM_LENGTH];
    //The best chromosome out of all generations so far
    public static int[] bestChromosome= {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15};


    public static void main(String args[]){

        Scanner x = new Scanner(System.in);
        System.out.println("Please enter MUTATION RATE (eg: 0.1, 0.5, 0.7): ");
        mutRate = x.nextDouble();
        System.out.println("Please enter CROSSOVER RATE (eg: 0.1, 0.3, 0.4): ");
        xoRate = x.nextDouble();
        System.out.println("Please enter GENERATION SPAN: ");
        genSpan = x.nextInt();
        System.out.println("Which crossover to apply: 1. Order Crossover   2. Uniform Order Crossover");
        selection = x.nextInt();

        Random rand = new Random(12323123L);

        //Fill array with random chromosomes
        initPopulation(rand);
        runAlgorithm(rand);
    }// main

    // This method fills the population with random chromosomes.
    public static void initPopulation(Random rand){
        for (int i=0; i<popSize; i++){
            population[i] = HelperMethods.randomShuffle(new int[]{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14},rand);
        }
    }// initPopulation

    public static void runAlgorithm(Random rand){
        // Reading the shredded document
        char[][] shreddedDoc = FitnessCalculator.getShreddedDocument("document1-shredded.txt");
        // Loops run till the generation span
        for(int t = 0; t< genSpan; t++){
            System.out.println("Generation number: "+t + "/"+ genSpan);
            int fittestChromosome = getFittestChromosome(shreddedDoc,population);
            if(FitnessCalculator.fitness(shreddedDoc,bestChromosome)>
                    FitnessCalculator.fitness(shreddedDoc,population[fittestChromosome])){
                bestChromosome = population[fittestChromosome];
                System.out.println("***************************************************");
                System.out.println("best fitness: " + FitnessCalculator.fitness(shreddedDoc,bestChromosome) );
                System.out.println("***************************************************");
            }
            evolvePopulation(shreddedDoc,selection,rand);
        }//for
        System.out.println("Here is what the shredded document looks like");
        FitnessCalculator.prettyPrint(shreddedDoc);
        System.out.println("After unshredding using the correct perm array, the fitness is: " + FitnessCalculator.fitness(shreddedDoc, bestChromosome));
        char[][] unshredded = FitnessCalculator.unshred(shreddedDoc, bestChromosome);
        System.out.println("And here is what the unshredded document looks like.");
        FitnessCalculator.prettyPrint(unshredded);

        System.out.println("Population size: " + popSize);
        System.out.println("Chromosome length: " + CHROM_LENGTH);
        System.out.println("Displacement Mutation rate: " + mutRate);
        System.out.println("Crossover rate: " + xoRate);
        System.out.println("Generation span: " + genSpan);
        System.out.println("Number of elites: " + elitism);
        System.out.println("Random number seed: 12323123L");
        System.out.println("Best chromosome: ");
        HelperMethods.printArray(bestChromosome);
        System.out.println();
        System.out.println("Best chromosome fitness: " + FitnessCalculator.fitness(shreddedDoc,bestChromosome));

    }//run

    private static void evolvePopulation(char[][] doc,int selection, Random rand) {
        int newPopulation[][] = new int[popSize][CHROM_LENGTH];
        int[][] tempPop = population.clone();
        HelperMethods.bubbleSort(tempPop,doc);
        for (int i=0;i<elitism;i++){
            newPopulation[i] = tempPop[i];
        }
        int a = -1, b= -1;
        int winner ,loser;
        for (int i =elitism; i<popSize; i++) {

            do {
                a = tournamentSelection(rand);
                b = tournamentSelection(rand);

            } while (a == b);

            if (FitnessCalculator.fitness(doc, population[a]) > FitnessCalculator.fitness(doc, population[b])) {
                winner = b;
                loser = a;
            } else {
                winner = a;
                loser = b;
            }
            int[] tempChromo;
            tempChromo = population[winner];
            if (HelperMethods.randomDouble(rand) < xoRate) {
                if (selection == 2){tempChromo = HelperMethods.uniformOrderCrossover(population[winner], population[loser], doc,rand);}
                else tempChromo = HelperMethods.orderCrossover(population[winner], population[loser], doc,rand);
            }
            newPopulation[i] = tempChromo;
        }

        //Mutation
        if(HelperMethods.randomDouble(rand)<mutRate){
            for (int i=elitism; i<popSize;i++){
                    HelperMethods.mutation(newPopulation[i],rand);
                }
            }
        double avgFitness = HelperMethods.calcAverage(population,doc);
        System.out.println("Average fitness: "+avgFitness);
        //replacing old population with new
        population = newPopulation;
    }//evolvePopulation

    //Returns the index of the fittest chromosome
    private static int getFittestChromosome(char[][] doc,int[][]pop) {
        int result=0;
        double highestFitness = 1000;
        for(int i=0; i<pop.length;i++){
            double value = FitnessCalculator.fitness(doc,pop[i]);
            if(value<highestFitness){
                result = i;
                highestFitness = value;}
        }
    return result;
    }
    //Returns random numbers from the population
    public static int tournamentSelection(Random rand){
        int x = rand.nextInt(popSize);
        return x;
    }

} //class
