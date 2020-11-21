
import com.sun.xml.internal.fastinfoset.tools.FI_DOM_Or_XML_DOM_SAX_SAXEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

class HelperMethods {
    //This method shuffles the elements of array
    public static int[] randomShuffle(int[] arr,Random rand){
        for (int i = 0; i < arr.length; i++) {
            int randomIndexToSwap = rand.nextInt(arr.length);
            int temp = arr[randomIndexToSwap];
            arr[randomIndexToSwap] = arr[i];
            arr[i] = temp;
        }
        return arr;
    }

    //This elements prints a given array.
    public static void printArray(int a[]){
        for(int i=0; i<a.length; i++){
            System.out.print(a[i]+" ");
        }
    }

    public static void fillChild(int[] child, int[] parent){
        for(int i=0, j=0;;){
            if(j == parent.length){
                break;
            }
            if(child[i] == 0 && !(ifExists(child,parent[j]))){
                child[i] = parent[j];
                i++;
                j++;
            }
            else if(child[i] != 0){i++; continue;}
            else{
                j++;
            }
        }
    }

    //This method performs a crossover between 2 parents and return offspring
    public static int[] orderCrossover(int[] parent1, int[] parent2,char[][] doc,Random rand){
        List<int[]> children = new ArrayList<>();

        int[] child1 = new int[parent1.length];
        int child2[] = new int[parent2.length];

        int rand1 = rand.nextInt(child1.length);
        int rand2 = rand.nextInt(child1.length);

        if(rand1 > rand2){
            int temp = rand1;
            rand1 = rand2;
            rand2 = temp;
        }
        int size = 15 - (rand2-rand1);
        //Generates slices to pass on
        int[] sliceP1 = Arrays.copyOfRange(parent1,rand1,rand2);
        int[] sliceP2 = Arrays.copyOfRange(parent2,rand1,rand2);
        for(int i = 0, j=rand1; i< sliceP1.length;i++,j++){
            child1[j] = sliceP1[i];
            child2[j] = sliceP2[i];
        }
        //find missing elements in chromosomes
        int[] missingElementsChild1 = func(parent2,sliceP1,rand2,size);
        int[] missingElementsChild2 = func(parent1,sliceP2,rand2,size);

        // Fill the child with other parents elements in sequence
        for(int i=rand2,count=0; count<missingElementsChild1.length;i++){
            child1[i] = missingElementsChild1[count];
            count++;
            if(count == missingElementsChild1.length){ break;}
            if(i == child1.length-1){ i = -1;}
        }
        for(int i=rand2,count=0; count<missingElementsChild2.length;i++){
            child2[i] = missingElementsChild2[count];
            count++;
            if(count == missingElementsChild2.length){ break;}
            if(i == child2.length-1){ i = -1;}
        }
        // Add children to the list
        children.add(child1);
        children.add(child2);
        if(FitnessCalculator.fitness(doc,children.get(0))<FitnessCalculator.fitness(doc,children.get(1))){
            return children.get(0);
        }
        else {
            return children.get(1);
        }
    }// orderCrossover

    //Returns offspring after performing uniform order crossover
    public static int[] uniformOrderCrossover(int[] parent1, int[] parent2,char[][] doc,Random rand){
        List<int[]> children = new ArrayList<>();
        int[] zeroesIndex = new int[10];
        int[] child1 = new int[parent1.length];
        int[] child2 = new int[parent2.length];
        int[] sliceP1 = new int[5];
        int[] sliceP2 = new int[5];
        // Mask to copy all elements that lie below a 1
        int[] mask = randomShuffle(new int[]{0,0,0,0,0,0,0,1,1,1,1,1,0,0,0},rand);

        //int k = (int)(Math.random()*15);
        for(int i=0,j=0,k=0; i< mask.length;i++){
            if(mask[i] == 1){
                child1[i] = parent1[i];
                sliceP1[j] = parent1[i];
                child2[i] = parent2[i];
                sliceP2[j] = parent2[i];
                j++;
            }
            if(mask[i] == 0){
                zeroesIndex[k] = i;
                k++;
            }
        }

        //find the elements missig from child
        int[] missingElementsChild1 = func(parent2,sliceP1,0,10);
        int[] missingElementsChild2 = func(parent1,sliceP2,0,10);
        // Fill missing elements of the child

        for(int i =0,j=0; i<10; i++,j++ ){
            child1[zeroesIndex[i]] = missingElementsChild1[i];
            child2[zeroesIndex[j]] = missingElementsChild2[j];
        }
        children.add(child1);
        children.add(child2);

        double m = randomDouble(rand);
        if(m<0.5){
            if(FitnessCalculator.fitness(doc,child1)< FitnessCalculator.fitness(doc,child2)){
                return child1;
            }
            else return child2;
        }
        else return child2;
    }

    //swaps a random element of a given chromosome
    public static void mutation(int[] arr,Random rand){
        int rand1 = rand.nextInt(arr.length);
        int rand2 = rand.nextInt(arr.length);

        int temp = arr[rand1];
        arr[rand1] = arr[rand2];
        arr[rand2] = temp;
    }// mutation

    //Returns true if n exists in array
    public static boolean ifExists(int a[], int n){
        for(int i=0; i<a.length; i++){
            if(a[i] == n) return true;
        }
        return false;
    }// ifExists

    //Returns a random a double
    public static double randomDouble(Random rand){
       double x = rand.nextDouble();
       return x;
    }

    public static void bubbleSort(int[][] a, char[][] doc) {
        boolean sorted = false;
        int temp[];
        while(!sorted) {
            sorted = true;
            for (int i = 0; i < a.length - 1; i++) {
                if (FitnessCalculator.fitness(doc,a[i]) > FitnessCalculator.fitness(doc,a[i+1])) {
                    temp = a[i];
                    a[i] = a[i+1];
                    a[i+1] = temp;
                    sorted = false;
                }
            }
        }
    }

    //finds missing elements from child
    public static int[] func(int[] parent, int[] slice, int rand2, int size){
        int[] result = new int[size];
        for(int i=rand2, j=0; i<parent.length; i++){

            if(!ifExists(slice,parent[i])){
                result[j] = parent[i];
                j++;
                if(j == size){return result;}
            }//if
            if(i == parent.length-1){
                i = -1;
            }
        }//for
        return result;
    }
    // calculates average of population
    public static double calcAverage(int[][] arr,char[][] doc){
    double total = 0;

        for(int i=0; i<arr.length; i++) {
            total = total + FitnessCalculator.fitness(doc,arr[i]);
        }
        return total;
    }
    public static int[] randomShuffleW(int[] arr){
        Random rand = new Random();
        for (int i = 0; i < arr.length; i++) {
            int randomIndexToSwap = rand.nextInt(arr.length);
            int temp = arr[randomIndexToSwap];
            arr[randomIndexToSwap] = arr[i];
            arr[i] = temp;
        }
        return arr;
    }
}// class

