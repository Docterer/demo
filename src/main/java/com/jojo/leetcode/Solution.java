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
	 * 1 -> A 
	 * 2 -> B 
	 * 3 -> C 
	 * ... 
	 * 26 -> Z 
	 * 27 -> AA 
	 * 28 -> AB
	 * 
	 * @param n
	 * @return
	 */
	public static String convertToTitle(int n) {
		return null;
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
		
		
		System.out.println(convertToTitle(1));
		
	}
}