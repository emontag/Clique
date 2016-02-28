/**
 * Genetic algorithm for Clique problem
 * Brute force included
 * @author Ephraim Montag
 */

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;


public class Clique {
   /**
    * Takes in input sets up GA and runs brute force algorithm
    * @param args command line arguments parameters input output
    */

   public static void main(String[] args) {
      if(args.length<3) {
         System.out.println("Not enough command line arguments");
         System.exit(1);
      }
      try {
         Scanner scan=new Scanner(new FileReader(args[1]));//input
         Writer writer=new BufferedWriter(new OutputStreamWriter(
               new FileOutputStream(args[2])));//output
            int nodes=scan.nextInt();
            int k=scan.nextInt();// k nodes as input
            if(k<1 || k>nodes) {
               System.out.println("Data bad!");
               System.exit(1);
            }
            boolean[][] graph=new boolean[nodes][nodes];//initialization data structure for Graph
            /*
             * defaulting graph outer loop 
             */
            for(int i=0;i<nodes;i++){
               //default graph inner loop
               for(int j=0;j<nodes;j++){
                  graph[i][j]=false;
               }
            }
            //inputting graph and inserting into boolean[][]
            while(scan.hasNext()){//input graph
               int firstPoint=scan.nextInt();
               int secondPoint=scan.nextInt();
               graph[firstPoint][secondPoint]=true;
               graph[secondPoint][firstPoint]=true;
            }
            scan.close();
            writer.write("Input file is "+ args[1]+"\n");
            //start of GA
            scan=new Scanner(new FileReader(args[0]));//parameters file
            int popSize=scan.nextInt();//population size
            int numGen=scan.nextInt();//how many generations
            int fitThreshold=scan.nextInt();//fitness threshold
            int popPercent=scan.nextInt();//% pop use
            int crossRatio=scan.nextInt();//what crossover rate
            int mutationRate=scan.nextInt();//mutation rate
            int[][] currentGeneration=new int[popSize][k];
            int[] fitnessScores=new int[popSize];
            double[] percentFit=new double[popSize];
            writer.write("Population size: "+popSize+"\n");
            writer.write("Number of Generations Possible: "+numGen+"\n");
            writer.write("Fitness Threshold: "+fitThreshold+"\n");
            writer.write("Percentage used in mating: "+popPercent+"\n");
            writer.write("Crossover rate: "+crossRatio+"\n");
            writer.write("Mutation rate: "+mutationRate+"\n");
            Random random=new Random();
            /*
             * randomly choosing generation
             * outer loop is for the count of the generation
             */
            for(int number=0;number<currentGeneration.length;number++)//get first generation
               //inner loop making random chromosome
               for(int generation=0;generation<currentGeneration[number].length;generation++)
                  currentGeneration[number][generation]=random.nextInt(nodes);
            int curGen=0;
            /*
             * going through GA process while the currentGeneration is less than the total generations allocated
             * condition exists inside if fitness matches the accepted one
             */
            while(curGen<numGen){
               writer.write("Generation number "+curGen+"\n");
               int totalFitness=0;//base start
               //Generating all fitnesses of chromosomes
               for(int count=0;count<currentGeneration.length;count++){
                  Arrays.sort(currentGeneration[count]);
                  fitnessScores[count]=fitness(currentGeneration[count],graph);
              //    System.out.println("Fitness score of "+ i+ " is "+ fitnessScores[i]);
                  totalFitness+=fitnessScores[count];
                  
               }
               makeFrequencies(currentGeneration, fitnessScores, writer,k);
               //get percentages of the fitness based on its fitness divided by total
               for(int count=0;count<fitnessScores.length;count++){
                  if(fitnessScores[count]==0) percentFit[count]=0;//ensures no division by zero ever  
                  else {
                     double a=fitnessScores[count];
                     //double x=a/totalFitness*100;
                     double x=a/(k*10.0)*100.0;
                     percentFit[count]= x;
              //       System.out.println("Percent Fit of "+ i+ " is "+percentFit[i]);
                  }
               }
               if(criteriaMet(currentGeneration, percentFit,fitThreshold,writer)) {
                  break;//if no need new generations
               }
               List propList=new ArrayList();
               int countProportion=0;
               /*
                * add spots in proportion list for roulette wheel selection. If percent fir less than 1 still give 1 spot otherwise give what is due
                */
               for(int count=0;count<percentFit.length;count++){//make proportional array
                  if(percentFit[count]<1.0) {
                     propList.add(count);
                     countProportion++;
                  }
                  else{
                     for(int j=0;j<percentFit[count];j++){
                        propList.add(count);
                        countProportion++;
                  }
                     
                     
                  }
               }
               
               Object[] prop=propList.toArray();
               int[] proportion=new int[prop.length];
               //place values from list to array
               for(int i=0;i<prop.length;i++) {
                  proportion[i]=Integer.parseInt(prop[i].toString());
               }
               
               int[] maters=new int[(int)(popSize*(popPercent/100.0))];
               if(maters.length<=1) {
                  System.out.println("Error: Insufficient population for GA!");
                  System.exit(1);
               }
               int count=0;//now use for mating count
               //determine group of parents for next generation randomly
               while(count<maters.length){
                  maters[count]=random.nextInt(countProportion);
            //      if(++timeCount<times && noDuplicateMaters(maters)) {//takes too long to not have duplicates
                     count++;
            //      }
             //     else continue;
                  
               }
               
               //now get next generation
               count=0;
               int[][] newGen=new int[popSize][k];//arrays for new combos
               /*
                * get 2  parents and ensure they are not the same. Produce child based on crossover rate. 
                * Go until population full again. 
                */
               while(count<popSize){
                  int mater1=random.nextInt(maters.length);
                  int mater2=random.nextInt(maters.length);
                  
                  if(mater1==mater2) continue;
                  
                  //crossover-default parent mater1, random, crossover based on rate
                  for(int mateCount=0;mateCount<k;mateCount++){
                     int rate=random.nextInt(100);
                     if(rate<=crossRatio) newGen[count][mateCount]=currentGeneration[mater1][mateCount];
                     else newGen[count][mateCount]=currentGeneration[mater2][mateCount];
                  }
                  count++;
               }
             //now mutation based on mutation rate and place in currentGeneration
               for(int number=0;number<newGen.length;number++){
                  for(int array=0;array<newGen[number].length;array++){
                     if(random.nextInt(100)>=mutationRate){
                        newGen[number][array]=random.nextInt(nodes);
                     }
                     currentGeneration[number][array]=newGen[number][array];
                  }
               }   
               
               
               
               curGen++;
               
            }
            writer.write("Current Generation is "+curGen+"\n");
            //for(int i=0;i<popSize;i++){
               //if(checkAnswer(currentGeneration[i],graph,k)) System.out.print("Correct: ");
               //else System.out.print("Incorrect: ");
              // for(int j=0;j<k;j++){
              //    System.out.print(currentGeneration[i][j]);
              // }
             //  System.out.println();
          //  }
            
            
            //start of brute force
            
          writer.write("Start of Brute Force: \n");
          int[] combos=new int[k];//set up k spaces
          //generate first combination
          for(int setUp=0;setUp<combos.length;setUp++) combos[setUp]=setUp;
          //gets new combos until none left, tests for validity of combo
          while(combos!=null){
             if(checkAnswer(combos,graph,k, writer)) break;
             combos=getNextCombo(combos,nodes);
          }//to do permutations just shift k times
            
              
       
         scan.close();
         writer.close();
      }catch(Exception e){
         e.printStackTrace();
      }

   }
   /**
    * Calculates the frequency of each fitness score and outputs it
    * @param currentGeneration chromomes for population
    * @param fitnessScores asociated fitness scores
    * @param writer Writer object to write to file
    * @param k input k
    * @throws IOException 
    */
   public static void makeFrequencies(int[][] currentGeneration,
         int[] fitnessScores, Writer writer, int k) throws IOException {
      int [] scores=new int[k+1];
      writer.write("Total fitness possible: "+(k)*10+"\n" );
      /*
       * make frequency of eaxh score
       */
      for(int count=0;count<fitnessScores.length;count++){
         scores[fitnessScores[count]/10]++;
      }
      //output frequency if greater than zero
      for(int count=0;count<scores.length;count++){
         if(scores[count]!=0) writer.write("Frequency of fitness score "+((count)*10)+ ": "+scores[count]+"\n");
      }
      
      
   }
   /**
    * Used to exit program if fitness score matches threshold
    * @param currentGeneration chromosomes of generation
    * @param percentFit percentage of fitness
    * @param threshold threshold fitness
    * @param writer writer object
    * @return boolean whether it matches or not
    * @throws IOException
    */
   public static boolean criteriaMet(int[][] currentGeneration,double[] percentFit,int threshold,Writer writer) throws IOException{
      //go one individual at a time
      for(int count=0;count<percentFit.length;count++){
         if(percentFit[count]>=threshold) {
            //System.out.println(i+"  "+ percentFit[i]);
            writer.write("Individual "+count+" fits criteria for threshold! \n");
            //go through each chromosome if it fits 
            for(int innerCount=0;innerCount<currentGeneration[count].length;innerCount++){
               writer.write(currentGeneration[count][innerCount]+" ");
            }
            writer.write("\n");
            return true;
         }
      }
      return false;
   }
   /**
    * fitness function o determine how good the chromosome matches what criteria are set. 
    * How many nodes are adjacent to each other
    * @param testing the chromosome
    * @param graph the actual input graph of nodes
    * @return fitness value
    */
   public static int fitness(int[] testing, boolean[][] graph){
      int fit=0;
      //check for multiples
      //Sets cannot contain duplicate
      Set set=new HashSet();
      //add to Set to determine if their are duplicates
      for(int count=0;count<testing.length;count++) set.add(testing[count]);
      if(set.size()!=testing.length) return fit;
      //outer loop to go through chromosome and compare
      for(int count=0;count<testing.length;count++){
         //inner loop to use to compare to value in outer loop. Goes sequentially up. 
         for(int innerCount=count;innerCount<testing.length;innerCount++){
            if(graph[testing[count]][testing[innerCount]]) fit+=10; 
         }
         //System.out.print(testing[i]+" ");
      }
      //System.out.println(fit);
      return fit;
      
   }
   /**
    * Retrieves the next combination based on the previous combination and addin to get the next one
    * @param combos
    * @param rows
    * @return
    */
   public static int[] getNextCombo(int[] combos, int rows){
      combos[combos.length-1]++;//add 1 to end of array
      int place=combos.length-1;//placeholder
      if( combos[combos.length-1]==rows){//if needs to carry 
         //go through to determine if need to use the carry
         for(place=combos.length-1; combos[place]>=rows-combos.length+place+1;place--){//go through 1 by 1
            if(place==0) return null;//if its at zero no more combos
            combos[place-1]++;//add carry
            //resets values in the array for further combinations properly
            for(int count=place-1;count<combos.length-1;count++) combos[count+1]=combos[count]+1;//ensure next combination restarts appropriately
         }
      }
     
      
      return combos;
   }
   /**
    * checks the answer for accuracy for brute force
    * @param testing combination testing
    * @param graph input graph 
    * @param k how many nodes are needed
    * @param writer writer object
    * @return 
    * @throws IOException
    */
   static public boolean checkAnswer(int[] testing, boolean[][] graph, int k,Writer writer) throws IOException{
     //outputs the combinations
      for(int count=0;count<testing.length;count++){
         writer.write(testing[count]+" ");
      }
      writer.write("\n");
      //outer loop to use for comparison
      for(int count=0;count<testing.length;count++){
         //inner loop for comparison
         for(int innerCount=count;innerCount<testing.length;innerCount++){
            //now compare 
            if(count==innerCount) continue;//not need hit itself
            if(!graph[testing[count]][testing[innerCount]]){
               return false;
            }
         }
      }
      writer.write("The answer above is correct");
      //if passes all conditions
      return true;
         
      
   }
}
