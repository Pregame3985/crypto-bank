package com.tokimi;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * @author william
 */
@Getter
@Setter
@RequiredArgsConstructor
@ToString
class TreeNode {

    @NonNull
    private Integer val;

    public TreeNode left;

    public TreeNode right;
}

@Slf4j
public class Solution {

    public static void main(String[] args) {
        Solution s = new Solution();

        List<TreeNode> treeNodes = s.generateTrees(5);

        log.info("{}", treeNodes);
    }

    public List<TreeNode> generateTrees(int n) {
        if (n < 1) {
            return new ArrayList<>();
        }
        return helper(1, n);
    }

    public List<TreeNode> helper(int start, int end) {
        List<TreeNode> list = new ArrayList<>();

        if (start > end) {
            // 如果当前子树为空，不加null行吗？
            list.add(null);
            return list;
        }

        for (int i = start; i <= end; i++) {
            // 想想为什么这行不能放在这里，而放在下面？
            // TreeNode root = new TreeNode(i);
            List<TreeNode> left = helper(start, i - 1);
            List<TreeNode> right = helper(i + 1, end);

            // 固定左孩子，遍历右孩子
            for (TreeNode l : left) {
                for (TreeNode r : right) {
                    TreeNode root = new TreeNode(i);
                    root.left = l;
                    root.right = r;
                    list.add(root);
                }
            }
        }
        return list;
    }
}
