package com.jojo.leetcode;

import java.util.HashMap;

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
	 * 771. Jewels and Stones
	 * 
	 * @param J
	 * @param S
	 * @return
	 */
	public int numJewelsInStones(String J, String S) {
		if (J == null || "".equals(J) || S == null || "".equals(S)) {
			return 0;
		}
		HashMap<Character, Integer> map = new HashMap<Character, Integer>();
		for (Character c : J.toCharArray()) {
			map.put(c, 1);
		}
		int result = 0;
		for (Character c : S.toCharArray()) {
			if (map.get(c) != null) {
				++result;
			}
		}
		return result;
	}


	
	/**
	 * 665. Non-decreasing Array 
	 * 
	 * 对于 i 与 i+1，如果 i+1 大于 i 之前的值，那么就另 i = i+1，
	 * 如果 i+1 小于 i 之前的某个数字，那么令 i+1 = i
	 * 
	 * @param nums
	 * @return
	 */
	public static boolean checkPossibility(int[] nums) {
		if (nums == null || nums.length == 0) {
			return false;
		}
		int count = 0;
		for (int i = 1; i < nums.length; i++) {
			if (nums[i - 1] > nums[i]) {
				count++;
			}
		}
		if (count >= 2) {
			return false;
		} else {
			return true;
		}
	}
	
	
	
	
	
	public static void main(String[] args) {
//		Solution solution = new Solution();
//
//		TreeNode root = new TreeNode(10);
//
//		root.left = new TreeNode(5);
//		root.right = new TreeNode(-3);
//
//		root.left.left = new TreeNode(3);
//		root.left.right = new TreeNode(2);
//		root.right.right = new TreeNode(11);
//
//		root.left.left.left = new TreeNode(3);
//		root.left.left.right = new TreeNode(-2);
//		root.left.right.right = new TreeNode(1);
//		System.out.println(solution.pathSum(root, 8));

		System.out.println(checkPossibility(new int[] {3,4,2,3}));
	}
}