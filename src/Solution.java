/********************Starter Code
 * 
 * This represents the solution for search algorithms (path_found, path, path_string, path_cost, n_explored)
 *
 */

public class Solution {

	private boolean path_found;
	private String path;
	private String path_string;
	private double path_cost;
	private int n_explored;

	public Solution(boolean path_found, String path, String path_string, double path_cost, int n_explored) {
		this.path_found=path_found;
		this.path=path;
		this.path_string=path_string;
		this.path_cost=path_cost;
		this.n_explored=n_explored;
	}

	public boolean getPathFound() {
		return path_found;
	}
	public String getPath() {
		return path;
	}
	public String getPathString() {
		return path_string;
	}
	public double getPathCost() {
		return path_cost;
	}
	public int getNExplored() {
		return n_explored;
	}

}
