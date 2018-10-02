import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import model.Project;
import model.Ranking;
import model.user.Student;
import util.ProjectAssignment;

public class BruteForceAlgorithm {

	// Map of Assignment ID with <Map of Project with assigned list of students>
	HashMap<Integer, HashMap<Project, ArrayList<Student> > >assignmentsMap;
	//Map of Assignment ID and the total sat score for it
	HashMap<Integer, Integer> totalSatScoreMap;
	
	public static ArrayList<Project> projects = new ArrayList<>();
	public static ArrayList<Student> students = new ArrayList<>();
	public static List<Ranking> rankings = new ArrayList<>();
	public static Set<Integer[]> setArrays = new HashSet<Integer[]>();

	public static void main(String[] args) {
		// populate projects, students, rankings
		ProjectAssignment currentRun = new ProjectAssignment(projects, students,rankings);
		currentRun.importDataLocallyAndPopulateDatabase();
		projects = currentRun.getProjects();
		students = currentRun.getStudents();
		rankings = currentRun.getAllRankings();
		
		//start algorithm
		bruteforce();
	}
/**
 * Solution Sets = 3D Array [][][] number solution, number of student, number of project
 * Solution: Student Index with Project Value
 * 
 * @param projects
 */
	public static void backtracking(Integer[][][] projects, Integer[][] solutionSet, int solutionIndex, Integer[] newSolution,
			int index1, int index2, int index3, Integer[] lastlastElement) {
		// if (index1 == 2 && index2 == 10 && index3 == 2) {
		// 	System.out.println("At least got here");
		// 	for (int i = 0; i < solutionSet.length; i++) {
		// 		System.out.println(i  + "is at " + solutionSet[i]);
		// 	}
		// 	return;
		// }
		//System.out.println("Sol index" + solutionIndex);
//		if (solutionIndex == 2) {
//			System.out.println("                     Index1 " + index1 + "  Index2   " + index2 + "  Index3    " + index3);
//			System.out.println("                     Last1 " + lastlastElement[0] + "  LAst2 " + lastlastElement[1] + "  last3 " + lastlastElement[2]);
//			return;
//		}
		// if at least one students is not assigned a project, did not find solution
		boolean foundSolution = true;
		for (int st = 1; st <= students.size(); st++) {
			if (newSolution[st] == -1) {
				foundSolution = false;
				break;
			}
		}
		
		// if number of students is equal to students.size() found a solution
		if (foundSolution == true) {
			//System.out.println("Solution Index is " + solutionIndex);
			//System.out.println("Solution set size is: " + solutionSet.length);
			//System.out.println("The value in SolutionSet at Index solutionIndex is" + solutionSet[solutionIndex][0]);
			for (int i = 0; i < newSolution.length; i++) {
				solutionSet[solutionIndex][i] = newSolution[i];
			}
			solutionIndex++;
			System.out.println("************Printing Solution*************");
			for (int sol = 1; sol < newSolution.length; sol++) {
				System.out.println("For Student " + sol + "   Assigned Project " + newSolution[sol]);
			}
			System.out.println("***************End Solution***************");
			for (int b = 1; b < newSolution.length; b++) {
				newSolution[b] = -1; // clean tmpSolution
			}
			
//			if (index2 < projects[index1].length-1 && (projects[index1][index2+1][0] != -1)) {
//				index2++;
//				index3 = 0;
//			}
			index1=0; index2=0; index3=0;
			System.out.println("I found a solution, can I find another one..all indexes are back to 0 0 0");
			if (index1 == 3) {
				System.out.println("before back");
				return;
			}
			backtracking(projects, solutionSet, solutionIndex, newSolution, index1, index2, index3, lastlastElement);
		} 
		
		// solution not found
		else 
		{
			int lastIndex1 = projects.length-1;
			int lastIndex2 = projects[lastIndex1].length-1;
			boolean isLastElement = false;
			System.out.println("Before increment: index1   " + index1 + "  index2  " + index2  + "  index3  " + index3);
			System.out.println("Before Increment: lastlasIndex1   " + lastlastElement[0] + "  lastindex2  " + lastlastElement[1]  + "  lastindex3   " + lastlastElement[2]);
			if (index1 == lastIndex1 && index2 == lastIndex2 ) {
				System.out.println("This is last element at " + index1 + "  " + index2);
				isLastElement = true;
			}
			
			boolean isAssigned = false;
			int studentIndex = projects[index1][index2][index3];
			if (newSolution[studentIndex] != -1) {
				isAssigned = true;
			}
			
			if (isAssigned == true) {
				// if not the first student in set clear the previous one in set
				if (index3 > 0) {
					for (int p = index3-1; p >= 0; p--) {
						newSolution[projects[index1][index2][p]] = -1;
					}
				}
				//if cannot assign and is last element and lastlastelement is last no more solutions exist
				if (isLastElement == true && lastlastElement[0]==lastIndex1 && lastlastElement[1]==lastIndex2) {
					System.out.println("NO MORE SOLUTIONS");
					getFinalSolution(solutionSet);
					return;
				} 
				//if last element but not lastlastelement could be more solutions, start from lastlastelement
				else if (isLastElement == true) {
					for (int b = 1; b < newSolution.length; b++) {
						newSolution[b] = -1; //clean solution
					}
					// if combo set is less than last set in projects
					if (lastlastElement[1] < projects[lastlastElement[0]].length-1) {
						lastlastElement[1]++;
						index1 = lastlastElement[0]; index2 = lastlastElement[1]; index3 = 0;
					} else if (lastlastElement[0] < projects.length-1) { //if project is less than all
						lastlastElement[0]++; lastlastElement[1] = 0; lastlastElement[2]=0;
						index1 = lastlastElement[0]; index2 = 0; index3 = 0;
					} else { //no more sols
						System.out.println("NO MORE SOLUTIONS WERE FOUND");
						getFinalSolution(solutionSet);
						return;
					}
					//print Index after increment
					System.out.println("After increment: index1  " + index1 + "  index2  " + index2  + "  index3  " + index3);
					System.out.println("After Increment: lastlasIndex1  " + lastlastElement[0] + "   lastindex2   " + lastlastElement[1]  + "  lastindex3  " + lastlastElement[2]);
					
				}
				// is not last element
				else {
					if (index2 < projects[index1].length-1 && (projects[index1][index2+1][0] != -1)) {
						index2++; index3 = 0;
					}
					else if (index1 < projects.length-1) {
						index1++; index2 = 0; index3 = 0;
					} 
					else {
						System.out.println("SHOULD NEVER GET HERE");
						return;
					}
				}
			}
			// if it is not assigned, assign student for the project
			// and increment index1 to next project, index2 = 0
			else {
				// create new tmp solution 
				Integer[] tmpNewSolution = new Integer[newSolution.length];
				for (int i = 0; i < tmpNewSolution.length; i++) {
					tmpNewSolution[i] = newSolution[i];
				}
				//assign value to the temp array
				tmpNewSolution[studentIndex] = index1;
				
//				for (int t = 0; t < tmpNewSolution.length; t++) {
//					System.out.println("Temp New Solution at " + t+ " is :  "  + tmpNewSolution[t]);
//					System.out.println(" New Solution at " + t+ " is :  "  + newSolution[t]);
//				}
				
				int stIndex = studentIndex;
				for (int i = 0; i < projects[index1][index2].length; i++) {
					if (index3 < projects[index1][index2].length-1 && projects[index1][index2][index3+1] !=-1) {
						stIndex = projects[index1][index2][i];
						tmpNewSolution[stIndex] = index1;
					}
				}
				//System.out.println("**********************************************************");
				for (int i = 0; i < solutionSet.length; i++) {
					if (tmpNewSolution[1] != -1 && solutionSet[i][1]!=-1) {
						//System.out.println("Solution set i" + solutionSet[i]);
						if (Arrays.equals(solutionSet[i], tmpNewSolution)) {
							System.out.println("**************************SAME SOL********************************");
							if (index2 < projects[index1].length-1 && (projects[index1][index2+1][0] != -1)) {
								//skip this combo
								index2++;
								System.out.println("**************************INDEX2++*******************************");
								System.out.println("                     Index1 " + index1 + "  Index2 " + index2 + "  Index3 " + index3);
								System.out.println("                     Last1 " + lastlastElement[0] + "  LAst2 " + lastlastElement[1] + "  last3 " + lastlastElement[2]);
							} else if (index1 < projects.length-1) {
								index1++;
								index2=0;
								index3=0;
								System.out.println("**************************INDEX1++*******************************");
								System.out.println("                     Index1 " + index1 + "  Index2 " + index2 + "  Index3 " + index3);
								System.out.println("                     Last1 " + lastlastElement[0] + "  LAst2 " + lastlastElement[1] + "  last3 " + lastlastElement[2]);	
							} else {
								System.out.println("**************************NOT SURE WHAT TO DO*******************************");
								return;
							}
						break;
						}
					}		
				}
				//used to be here
				newSolution[studentIndex] = index1;	
				// if index3 is not last one increment
				if (index3 < projects[index1][index2].length-1 && projects[index1][index2][index3+1] !=-1) {
					//System.out.println("Move to next student index3++" + index3);
					index3++;
				}
				// if index1 not last project increment
				else if (index1 < projects.length-1){
					index1++; index2 = 0; index3 = 0;
				} 
				// all projects assigned
				else {
					//Does get in here
					System.out.println("                     Index1 " + index1 + "  Index2 " + index2 + "  Index3 " + index3);
					System.out.println("                     Last1 " + lastlastElement[0] + "  LAst2 " + lastlastElement[1] + "  last3 " + lastlastElement[2]);
				}
			}
			backtracking(projects, solutionSet, solutionIndex, newSolution, index1, index2, index3, lastlastElement);
		}
	}

	/**
	 * Method that returns maximum size of students per project
	 * @return
	 */
	public static int getMaxSize() {
		int maxSize = 0;
		for (Project p : projects) {
			if (maxSize < p.getMaxSize()) {
				maxSize = p.getMaxSize();
			}
		}
		return maxSize;
	}
	
	public static void bruteforce() {
		HashMap<Integer, Set<Integer> > initialSets = new HashMap<Integer, Set<Integer>>();
		for (Ranking rank : rankings) {
			Set<Integer> tmpS = new HashSet<Integer>();
			if (rank.getStudentId() != -1) {
				tmpS.add(rank.getStudentId());
				if (initialSets.size() > 0) {
					if (initialSets.containsKey(rank.getProjectId())) {
						tmpS.addAll(initialSets.get(rank.getProjectId()));
					}
				}
				initialSets.put(rank.getProjectId(), tmpS);
			}
		}
		generateCombinations(initialSets);	
	}
	
	public static void generateCombinations(HashMap<Integer, Set<Integer> > initialSets) {
		
		for (int i = 1; i <= projects.size(); i++) {
			System.out.println("Project's " +  projects.get(i-1).getProjectId() + "insitial set is:  " + initialSets.get(i));
		}
		
		Integer[][][] projectsArray = new Integer[projects.size()][100][getMaxSize()];
		//Integer[][][] projectsArray = new Integer[projects.size()][comboArrays.length][r];
		int maxSolutionSize = 0;
		
		//DO I NEED <= here?
		for (int i = 1; i <= initialSets.size(); i++) {

			Set<Integer> tmpSet = initialSets.get(i);
			//create combos
			int r = getMaxSize();
			System.out.println("Project's" + projects.get(i-1).getProjectId() + "size of set is" + tmpSet.size());
			
			Integer[] array = new Integer[tmpSet.size()];
			int count = 0;
			for (Integer s : tmpSet) {
				array[count] = s;
				count++;
			}	
			Integer[] currSet = new Integer[r];
			
			
			combinationUtil(array, currSet, 0, initialSets.get(i).size()-1, 0, r);
			
			Integer[][] comboArrays = new Integer[setArrays.size()][r];
			int a = 0;
			for (Integer[] l : setArrays) {
				comboArrays[a] = l;
				a++;
			}
			setArrays.clear();
			projectsArray[projects.get(i-1).getProjectId()-1] = comboArrays;
			if (maxSolutionSize < comboArrays.length) {
				maxSolutionSize = comboArrays.length;
			}
			
		}
		System.out.println("*******************ProjectArray**************");
		
		for (int x=0; x<projectsArray.length;x++) {
			for (int y = 0; y < projectsArray[x].length; y++) {
				System.out.print(" [");
				for (int z = 0; z < projectsArray[x][y].length; z++) {
					if (projectsArray[x][y][z] == null) {
						projectsArray[x][y][z] = -1;
					}
					System.out.print(" " + projectsArray[x][y][z] + " " );
				}
				System.out.print("] ");
			}
			System.out.println();
		}
		System.out.println();
		System.out.println("*******************ProjectArrayEnd**************");
		//backtrackingPrint(projectsArray);
		Integer[][] solutionSet = new Integer[100][students.size()+1];
		for (int i = 0; i < solutionSet.length; i++) {
			for (int j = 0; j < solutionSet[i].length; j++) {
				solutionSet[i][j]=-1;
			}
		}
		
		// Solution is 0[-1] 1[-1] 2[-1] ... student#[project#]
		Integer[] newSolution = new Integer[students.size()+1];
		for (int st = 0; st <= students.size(); st++) {
			newSolution[st] = -1;
		}
		
		Integer[] lastlastElement = new Integer[3];
		lastlastElement[0] = 0;
		lastlastElement[1] = 0;
		lastlastElement[2] = 0;
		//System.out.println("ProjectsArray Size" + projectsArray.length );
		backtracking(projectsArray, solutionSet, 0, newSolution, 0, 0, 0, lastlastElement);
	}
	
	public static void getFinalSolution(Integer[][] solutionSet) {
		System.out.println("HERE IS MY FINAL SOLUTION");
		for (int i = 0; i < solutionSet.length; i++) {
			if (solutionSet[i][1] == -1) {
				return;
			}
			System.out.println("Solution " + i + "\n");
			int total = 0;
			for (int j = 1; j < solutionSet[i].length; j++) {
//				System.out.println(" for student: " + j + "   project assigned is: " + solutionSet[i][j]+1);
//				System.out.println("               for student: " + j + "   ranking is: " + findRanking(students.get(j-1).getId(), solutionSet[i][j]+1));
				total+=getScore(findRanking(students.get(j-1).getId(), solutionSet[i][j]+1));
			}
			System.out.println("***************TOTAL: " + total);
			System.out.println();
		}
	}
	
	public static int findRanking(int studentID, int projectID) {
		System.out.println("  ST id   " + studentID + "   proj id   " + projectID);
		int rank = -1;
		for (int i = 0; i < rankings.size(); i++) {
			if (rankings.get(i).getStudentId() == studentID && rankings.get(i).getProjectId() == projectID) {
				rank = rankings.get(i).getRank();
			}
		}
		return rank;
	}
	
	public static int getScore(int i) { // i = project's rank
		return fibonacci(i);
	}

	public static int fibonacci(int i) {
		if (i==1 || i==2) {
			return 1;
		} else {
			return fibonacci(i-1)*fibonacci(i-2);
		}
	}

	public static void combinationUtil(Integer[] initalSet, Integer[] currentSet, int start, int end, 
			int index, int r) {
		
		if (index == r) {
			Integer[] tmp = new Integer[r];
			for (int j = 0; j < r; j++) {
				//System.out.print(currentSet[j] + " ");
				tmp[j] = currentSet[j];
			}
			//System.out.println();
			setArrays.add(tmp);
			
			return;
		}
		
		for (int i = start; i <= end & end-i+1 >= r-index; i++) {
			currentSet[index] = initalSet[i];
			combinationUtil(initalSet, currentSet, i+1, end, index+1, r);
		}
	}
	
	public static void backtrackingPrint(Integer[][][] projects) {
		for (int i = 0; i < projects.length; i++) {
			for (int j = 0; j < projects[i].length; j++) {	
				if (projects[i][j] != null) {
					for (int k = 0; k < 2; k++) {
						if (projects[i][j][k] != null) {
							System.out.print(projects[i][j][k] + " , ");
						}	
					}
				}
				
			}
			System.out.println();
		}
	}

}