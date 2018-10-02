package model;

public class Ranking {
	Long rankingId;
	int studentId;
	int projectId;
	int rank;
	
	public Ranking() {
	}
	public Ranking(int studentId, int projectId, int rank) {
		this.studentId = studentId;
		this.projectId = projectId;
		this.rank = rank;
	}
	
	public Long getRankingId() {
		return rankingId;
	}

	public void setRankingId(Long rankingId) {
		this.rankingId = rankingId;
	}

	public int getStudentId() {
		return studentId;
	}

	public void setStudentId(int studentId) {
		this.studentId = studentId;
	}

	public int getProjectId() {
		return projectId;
	}

	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}
}
