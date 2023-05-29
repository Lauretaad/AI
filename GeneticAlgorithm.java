package AI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class GeneticAlgorithm{

    private int[][] processingTimes; // Processing times for each job on each machine
    private int[] machineLimits; // Maximum job capacity for each machine
    private int numMachines; // Number of machines
    private int numJobs; // Number of jobs
    private int populationSize; // Size of the population
    private double crossoverRate; // Crossover rate
    private double mutationRate; // Mutation rate
    private int maxIterations; // Maximum number of iterations
    
    public GeneticAlgorithm(int[][] processingTimes, int[] machineLimits, int populationSize, double crossoverRate,
                            double mutationRate, int maxIterations) {
        this.processingTimes = processingTimes;
        this.machineLimits = machineLimits;
        this.numMachines = machineLimits.length;
        this.numJobs = processingTimes.length;
        this.populationSize = populationSize;
        this.crossoverRate = crossoverRate;
        this.mutationRate = mutationRate;
        this.maxIterations = maxIterations;
    }
    
    public int[] scheduleJobs() {
        List<int[]> population = generateInitialPopulation();
        int iteration = 0;
        
        while (iteration < maxIterations) {
            List<int[]> newPopulation = new ArrayList<>();
            
            while (newPopulation.size() < populationSize) {
                int[] parent1 = selectParent(population);
                int[] parent2 = selectParent(population);
                int[] offspring = crossover(parent1, parent2);
                mutate(offspring);
                newPopulation.add(offspring);
            }
            
            population = newPopulation;
            iteration++;
        }
        
        return getBestSolution(population);
    }
    
    private List<int[]> generateInitialPopulation() {
        List<int[]> population = new ArrayList<>();
        Random random = new Random();
        
        for (int i = 0; i < populationSize; i++) {
            int[] solution = new int[numJobs];
            
            for (int j = 0; j < numJobs; j++) {
                solution[j] = random.nextInt(numMachines);
            }
            
            population.add(solution);
        }
        
        return population;
    }
    
    private int[] selectParent(List<int[]> population) {
        Random random = new Random();
        int tournamentSize = 5;
        List<int[]> tournament = new ArrayList<>();
        
        for (int i = 0; i < tournamentSize; i++) {
            int index = random.nextInt(population.size());
            tournament.add(population.get(index));
        }
        
        return getBestSolution(tournament);
    }
    
    private int[] crossover(int[] parent1, int[] parent2) {
        Random random = new Random();
        int[] offspring = new int[numJobs];
        
        if (random.nextDouble() < crossoverRate) {
            int crossoverPoint = random.nextInt(numJobs);
            
            for (int i = 0; i < crossoverPoint; i++) {
                offspring[i] = parent1[i];
            }
            
            for (int i = crossoverPoint; i < numJobs; i++) {
                offspring[i] = parent2[i];
            }
        } else {
            offspring = parent1.clone();
        }
        
        return offspring;
    }
    
    private void mutate(int[] solution) {
        Random random = new Random();
        
        for (int i = 0; i < numJobs; i++) {
            if (random.nextDouble() < mutationRate) {
                int newMachine = random.nextInt(numMachines);
                solution[i] = newMachine;
            }
        }
    }
    
    private int[] getBestSolution(List<int[]> population) {
        int bestCost = Integer.MAX_VALUE;
        int[] bestSolution = null;
        
        for (int[] solution : population) {
            int cost = calculateCost(solution);
            
            if (cost < bestCost) {
                bestCost = cost;
                bestSolution = solution;
            }
        }
        
        return bestSolution;
    }
    
    private int calculateCost(int[] solution) {
        int[] machineLoads = new int[numMachines];
        
        for (int i = 0; i < numJobs; i++) {
            int machine = solution[i];
            machineLoads[machine] += processingTimes[i][machine];
        }
        
        int maxLoad = 0;
        
        for (int i = 0; i < numMachines; i++) {
            int loadDiff = Math.abs(machineLoads[i] - machineLimits[i]);
            if (loadDiff > maxLoad) {
                maxLoad = loadDiff;
            }
        }
        
        return maxLoad;
    }
    
    public static void main(String[] args) {
        JFrame frame = new JFrame("Genetic Algorithm Input");
        
        int numMachines = Integer.parseInt(JOptionPane.showInputDialog(frame, "Enter the number of machines:"));
        int numJobs = Integer.parseInt(JOptionPane.showInputDialog(frame, "Enter the number of jobs:"));
        
        int[][] processingTimes = new int[numJobs][numMachines];
        int[] machineLimits = new int[numMachines];
        
        for (int i = 0; i < numJobs; i++) {
            for (int j = 0; j < numMachines; j++) {
                int processingTime = Integer.parseInt(JOptionPane.showInputDialog(frame,
                        "Enter the processing time for job " + (i + 1) + " on machine " + (j + 1) + ":"));
                processingTimes[i][j] = processingTime;
            }
        }
        
        for (int i = 0; i < numMachines; i++) {
            int machineLimit = Integer.parseInt(JOptionPane.showInputDialog(frame,
                    "Enter the machine limit for machine " + (i + 1) + ":"));
            machineLimits[i] = machineLimit;
        }
        
        int populationSize = Integer.parseInt(JOptionPane.showInputDialog(frame, "Enter the population size:"));
        double crossoverRate = Double.parseDouble(JOptionPane.showInputDialog(frame, "Enter the crossover rate:"));
        double mutationRate = Double.parseDouble(JOptionPane.showInputDialog(frame, "Enter the mutation rate:"));
        int maxIterations = Integer.parseInt(JOptionPane.showInputDialog(frame, "Enter the maximum number of iterations:"));
        
        GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm(processingTimes, machineLimits, populationSize,
                crossoverRate, mutationRate, maxIterations);
        int[] schedule = geneticAlgorithm.scheduleJobs();
        
        System.out.println("Best job schedule: ");
        for (int i = 0; i < schedule.length; i++) {
            System.out.println("Job " + (i + 1) + " -> Machine " + (schedule[i] + 1));
        }
        
        frame.dispose();
    }
}
