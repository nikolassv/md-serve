package de.nikolassv.mdserve.render;

import java.util.List;

public record TreeNode(
        String name,
        String path,
        boolean directory,
        boolean active,
        List<TreeNode> children
) {}
