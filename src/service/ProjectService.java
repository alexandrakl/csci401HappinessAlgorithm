package service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.Project;
import model.Ranking;
import model.user.Student;
import util.ProjectAssignment;

public class ProjectService {
	
	private static ProjectAssignment maxAlgorithm;
	private static String folder_name = "src/data";
	private static int NUM_RANKED = 5; // number of projects that each student can rank
	public static Map<Double, ProjectAssignment> algorithms = new HashMap<>();
	public static Map<Double, Integer> iterations = new HashMap<>();
	private static List<Project> savedProjects = new ArrayList<Project>();
	
	public static List<Project> runAlgorithm() {
		
		for (int iteration = 0; iteration < 10000; iteration++) {
			ArrayList<Project> projects = new ArrayList<>();
			ArrayList<Student> students = new ArrayList<>();
			List<Ranking> rankings = new ArrayList<>();
			ProjectAssignment currentRun = new ProjectAssignment(projects, students,rankings);
			currentRun.importDataLocallyAndPopulateDatabase();
			projects = currentRun.getProjects();
			students = currentRun.getStudents();
			rankings = currentRun.getAllRankings();
			for (Ranking rank : rankings) {
				Student student = null;
				for (Student s : students) {
					if (s.getUserId() == rank.getStudentId()) {
						student = s;
					}
				}
				
				Project project = null;
				for (Project p : projects) {
					if (p.getProjectId() == rank.getProjectId()) {
						project = p;
					}
				}
				
				if (project != null && student != null) {
					String projectName = project.getProjectName();
		            student.rankings.put(projectName, rank.getRank());
		            student.orderedRankings.add(projectName);
					
					Integer p = ProjectAssignment.getStudentSatScore(rank.getRank());
		            project.incSum_p(p);
		            project.incN();
				}
			}

				ProjectAssignment algorithm = new ProjectAssignment(projects, students,rankings);
				algorithm.run(iteration, NUM_RANKED, folder_name);
				double groupSatScore = algorithm.algoSatScore;
				algorithms.put(groupSatScore, algorithm);
				iterations.put(groupSatScore, iteration);
		}

		Double maxScore = Collections.max(algorithms.keySet());
		
		maxAlgorithm = algorithms.get(maxScore);
		Integer maxIteration = iterations.get(maxScore);
		System.out.println("maxScore: " + maxScore + ". maxIteration: " + maxIteration);
		
		maxAlgorithm.outputProjectDecisions();
		savedProjects = maxAlgorithm.assignedProjects();
		return savedProjects;
	}
	
	public static void main(String[] args) {
		runAlgorithm();
	}
}
