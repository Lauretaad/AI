
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class TabuSearch {

    private int[][] processingTimes; // Processing times for each job on each machine
    private int[] machineLimits; // Maximum job capacity for each machine
    private int numMachines; // Number of machines
    private int numJobs; // Number of jobs
    private int tabuListSize; // Size of the tabu list
    private int maxIterations; // Maximum number of iterations
    
    public TabuSearch(int[][] processingTimes, int[] machineLimits, int tabuListSize, int maxIterations) {
        this.processingTimes = processingTimes;
        this.machineLimits = machineLimits;
        this.numMachines = machineLimits.length;
        this.numJobs = processingTimes.length;
        this.tabuListSize = tabuListSize;
        this.maxIterations = maxIterations;
    }
    
    public int[] scheduleJobs() {
        int[] bestSolution = generateInitialSolution();
        int[] currentSolution = bestSolution.clone();
        int[] bestNeighbor = new int[numJobs];
        int bestNeighborCost;
        int bestCost = calculateCost(bestSolution);
        int iteration = 0;
        List<Integer> tabuList = new ArrayList<>();
        
        while (iteration < maxIterations) {
            bestNeighborCost = Integer.MAX_VALUE;
            
            for (int i = 0; i < numJobs; i++) {
                for (int j = i + 1; j < numJobs; j++) {
                    int[] neighbor = currentSolution.clone();
                    swapJobs(neighbor, i, j);
                    int neighborCost = calculateCost(neighbor);
                    
                    if (!tabuList.contains(neighborCost) && neighborCost < bestNeighborCost) {
                        bestNeighbor = neighbor.clone();
                        bestNeighborCost = neighborCost;
                    }
                }
            }
            
            if (bestNeighborCost < bestCost) {
                currentSolution = bestNeighbor.clone();
                bestCost = bestNeighborCost;
                
                if (tabuList.size() >= tabuListSize) {
                    tabuList.remove(0);
                }
                
                tabuList.add(bestNeighborCost);
            }
            
            iteration++;
        }
        
        return bestSolution;
    }
    
    private int[] generateInitialSolution() {
        int[] solution = new int[numJobs];
        Random random = new Random();
        
        for (int i = 0; i < numJobs; i++) {
            solution[i] = random.nextInt(numMachines);
        }
        
        return solution;
    }
    
    private void swapJobs(int[] solution, int job1, int job2) {
        int temp = solution[job1];
        solution[job1] = solution[job2];
        solution[job2] = temp;
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
        Scanner scanner = new Scanner(System.in);
        
        System.out.print("Enter the number of machines: ");
        int numMachines = scanner.nextInt();
        
        System.out.print("Enter the number of jobs: ");
        int numJobs = scanner.nextInt();
        
        int[][] processingTimes = new int[numJobs][numMachines];
        int[] machineLimits = new int[numMachines];
        
        for (int i = 0; i < numJobs; i++) {
            System.out.println("Enter the processing times for job " + (i + 1) + " on each machine:");
            for (int j = 0; j < numMachines; j++) {
                processingTimes[i][j] = scanner.nextInt();
            }
        }
        
        System.out.println("Enter the machine limits:");
        for (int i = 0; i < numMachines; i++) {
            machineLimits[i] = scanner.nextInt();
        }
        
        System.out.print("Enter the tabu list size: ");
        int tabuListSize = scanner.nextInt();
        
        System.out.print("Enter the maximum number of iterations: ");
        int maxIterations = scanner.nextInt();
        
        TabuSearch tabuSearch = new TabuSearch(processingTimes, machineLimits, tabuListSize, maxIterations);
        int[] schedule = tabuSearch.scheduleJobs();
        
        System.out.println("Best job schedule: ");
        for (int i = 0; i < schedule.length; i++) {
            System.out.println("Job " + (i + 1) + " -> Machine " + (schedule[i] + 1));
        }
        
        scanner.close();
    }
}




