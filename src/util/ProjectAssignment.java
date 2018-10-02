package util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Vector;
import model.Project;
import model.Ranking;
import model.user.Student;

public class ProjectAssignment {
	
	private ArrayList<Project> projects;
	private ArrayList<Student> students;
	private List<Ranking> rankings;
	private ArrayList<Student> unassignedStudents;
	
	private static int NUM_RANKED = 5;
	private static String folder_name = "src/data";
	public double algoSatScore = 0; // overall satisfaction of this matching
	
	public static int getStudentSatScore(int i) { // i = project's rank
		return fibonacci(i);
		//return ( ( (NUM_RANKED-i+1) * (NUM_RANKED-i)) / 2 ) + 1;
	}

	public static int fibonacci(int i) {
		if (i==0 || i==1) {
			return 1;
		} else {
			return fibonacci(i-1)*fibonacci(i-2);
		}
	}
	
	// Imports data from local text files, populates the database tables for Projects, Users, and Project Rankings, and terminates the program.
	public void importDataLocallyAndPopulateDatabase() {
				
		// import projects from text file
        String line = null;
        try {
            BufferedReader projectsBR = new BufferedReader(new FileReader(folder_name + "/projects_9.txt"));

            while((line = projectsBR.readLine()) != null) {                
                String[] elements = line.split(" ");
                
                Project newProject = new Project(getStudentSatScore(1));
                newProject.setProjectName(elements[0]);
                newProject.setProjectId(projects.size()+1); // TODO: MAKE THIS DYNAMIC WITH AUTOINCREMENT
                newProject.setMinSize(Integer.parseInt(elements[1]));
                newProject.setMaxSize(Integer.parseInt(elements[2]));
                projects.add(newProject);
            }
            
            projectsBR.close();         
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        
        // import users and rankings from text file
        try {
            BufferedReader studentsBR = new BufferedReader(new FileReader(folder_name + "/rankings_9.txt"));
            int idCount = 1;
            while((line = studentsBR.readLine()) != null) {      
                String[] elements = line.split(" ");
                List<String> orderedRanks = new ArrayList<String>();
                Student newStudent = new Student();
                newStudent.setFirstName(elements[0]);
                newStudent.setUserId(idCount);
                HashMap<String,Integer> ranks = new HashMap<String,Integer>();
                for(int i = 1; i < elements.length; i++) {
                //	orderedRanks.add(elements[i]);
                	ranks.put(elements[i],i);
                	rankings.add(new Ranking(newStudent.getUserId(),Integer.parseInt(elements[i]),i));
                }
               // newStudent.setOrderedRankings(orderedRanks);
                students.add(newStudent);
                idCount++;
            }
            
            studentsBR.close();         
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        
	}
	
	public ProjectAssignment(ArrayList<Project> projects, ArrayList<Student> students, List<Ranking> rankings) {
		this.projects = projects;
		this.students = students;
		this.rankings = rankings;
		this.unassignedStudents = new ArrayList<Student>();
	}
	
	
	public void run(int iteration, int _NUM_RANKED, String _folder_name) {
		
		NUM_RANKED = _NUM_RANKED;
		if (projects.size() < NUM_RANKED) {
			NUM_RANKED = projects.size();
		}
		folder_name = _folder_name;
		
		// sort projects by popularity in descending order
		Collections.sort(projects, new Project.popularityComparator());
		
		AssignInitial();
		EliminateProjects();
		Bump();
		PlaceUnassignedStudents();
		
		algoSatScore = computeAlgoSatScore();
	}
	
	private double computeAlgoSatScore() {
		double satWeight = 0.0;
		
		// calculate this iteration's overall sat score:
		double totalProjSatScores = 0;
		
		for (Project p : projects) {
			if(!p.getProjectName().equals("Unassigned")) {
			  for(Student s : p.members) {	
				if(s.getOrderedRankings().indexOf(p.getProjectName()) <= 1) {
					//6-1
					satWeight += 5.0;
				}
				
				else if(s.getOrderedRankings().indexOf(p.getProjectName()) == 2){
					//6-2
					satWeight += 4.0;
				}
				else if(s.getOrderedRankings().indexOf(p.getProjectName()) == 3){
					//6-3
					satWeight += 3.0;
				}
				else if(s.getOrderedRankings().indexOf(p.getProjectName()) == 4){
					//6-5
					satWeight += 1.0;
				}
			  }
			  totalProjSatScores += p.returnProjSatScore();
			}
		}

		for(Student s : unassignedStudents) {
			satWeight -= 2.0;
		}
		
		return satWeight * totalProjSatScores / (projects.size()-1);
	}

	public void outputProjectDecisions() {
		for (int i=0; i<projects.size(); i++) {
			System.out.print(projects.get(i).getProjectId() + " - " + projects.get(i).getProjectName() + ", ");
			for(Student s : projects.get(i).members)
				System.out.print(s.getFirstName() + ", ");
			System.out.println("");
		}
	}
	
//	public String JSONOutputWeb() {
//		//String jsonStr = "[";
//		String jsonStr = "[";
//		ObjectMapper mapper = new ObjectMapper();
//		for (int i=0; i<projects.size(); i++) {
//		    try {  
//		        // Writing to a file  
//		    	   if (i != 0) {
//		    		   jsonStr += ",";
//		    	   }
//		    	   jsonStr += mapper.writeValueAsString(projects.get(i));
//	           // System.out.println(jsonStr);
//		    } catch (IOException e) {  
//		        e.printStackTrace();  
//		    }
//	    }
//		jsonStr += "]";
//		return jsonStr;
//	}
	
	void AssignInitial() {
		for (Student s: students)
			unassignedStudents.add(s);
		Collections.shuffle(unassignedStudents);
		
		for (int choice = 0; choice < NUM_RANKED; choice++) {
			
			for (Iterator<Student> it = unassignedStudents.iterator(); it.hasNext();) {
				Student s = it.next();
				if (s.orderedRankings.size() > choice) {
					String projname = s.orderedRankings.get(choice);
					Project p = GetProjectWithName(projname);
					if (p.members.size() < p.getMaxSize()) {
						(p.members).add(s);
						it.remove();
					}
				}
			}
		}
	}
	
	void EliminateProjects() {
		for (int i=projects.size()-1; i>0; i--) {
			Project p = projects.get(i);
			if (p.members.size() < p.getMinSize() && (GetTotalMaxSpots()-p.getMaxSize()) >= students.size()) {
				//writer.println("Eliminated " + p.getProjectName());
				for (Student s: p.members) {
					if (!unassignedStudents.contains(s)) {
						unassignedStudents.add(s);
					}
				}
				projects.remove(i);
			}
		}
		//writer.println("");
	}

	void Bump() {
		Collections.shuffle(unassignedStudents);
		for (Iterator<Student> it = unassignedStudents.iterator(); it.hasNext();) {
			Student s = it.next();
			if (BumpHelper(s, 0))
				it.remove();
		}
	}
	
	boolean BumpHelper(Student s, int level) {
		if (level>3)
			return false;
		for (int i=0; i<s.orderedRankings.size(); i++) {
			Project p = GetProjectWithName(s.orderedRankings.get(i));
			if (p!=null && p.members.size() < p.getMaxSize()) { //found a spot for them
				p.members.add(s);
				return true;
			}
		}
		
		Project p = GetProjectWithName(s.orderedRankings.get(0));
		
		if (p == null) 
		{
			return false;
		}
		
		Random rand = new Random();
		int index = rand.nextInt(p.members.size());
		Student displaced = (p.members).get(index);
		if (BumpHelper(displaced, level+1)) {
			p.members.remove(displaced);
			p.members.add(s);
		}
		
		return true;
	}
	
	Project GetProjectWithName(String projname) {
		for (int j=0; j<projects.size(); j++) {
			if (projects.get(j).getProjectName().equals(projname))
				return projects.get(j);
		}
		return null;
	}
	
	int GetTotalMaxSpots() {
		int maxspots = 0;
		for (Project p: projects)
			maxspots += p.getMaxSize();
		return maxspots;
	}
	
	boolean CanStop() { // assignment is satisfactory
		int numstudents = 0;
		for (Project p: projects) {
			if (!p.members.isEmpty() && 
				(p.members.size() < p.getMinSize() || p.members.size() > p.getMaxSize()))
				return false;
			numstudents += p.members.size();
		}
		if (numstudents != students.size())
			return false;
		return true;
	}
	
	void PlaceUnassignedStudents() {
		if (!unassignedStudents.isEmpty()) {
			Project unassignedProj = new Project();
			unassignedProj.setProjectName("Unassigned");
			unassignedProj.members = new Vector<Student>();
			for (Student s: unassignedStudents) {
				unassignedProj.members.add(s);
			}
			projects.add(unassignedProj);
		}		
	}
	
	public ArrayList<Project> getProjects() {
		return projects;
	}
	
	public ArrayList<Student> getStudents() {
		return students;
	}
	
	public List<Project> assignedProjects() {
		return projects;
	}
	
	public List<Ranking> getAllRankings() {
		return rankings;
	}
}