package com.jojo.leetcode;

public class Solution {

	public int pathSum(TreeNode root, int sum) {
		if (root == null) {
			return 0;
		}
		int count = depth(root, sum);
		int rightCount = pathSum(root.right, sum);
		int leftCount = pathSum(root.left, sum);
		
		return count + leftCount + rightCount;
	}

	private int depth(TreeNode root, int sum) {
		if (root == null && sum == 0) {
			return 1;
		} else if (root == null && sum != 0) {
			return 0;
		}
		int left = depth(root.left, sum - root.val);
		int right = depth(root.right, sum - root.val);
		return left + right;
	}

	
	/**
	 * leetcode 189
	 * @param nums
	 * @param k
	 */
	public void rotate(int[] nums, int k) {
       
    }
	
	
	
	public static void main(String[] args) {
		Solution solution = new Solution();
		
		TreeNode root = new TreeNode(10);

		root.left = new TreeNode(5);
		root.right = new TreeNode(-3);

		root.left.left = new TreeNode(3);
		root.left.right = new TreeNode(2);
		root.right.right = new TreeNode(11);

		root.left.left.left = new TreeNode(3);
		root.left.left.right = new TreeNode(-2);
		root.left.right.right = new TreeNode(1);
		System.out.println(solution.pathSum(root, 8));
		
	}
}