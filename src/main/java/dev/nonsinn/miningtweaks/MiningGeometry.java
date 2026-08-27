package dev.nonsinn.miningtweaks;

import java.util.ArrayList;
import java.util.List;
import org.joml.Vector3d;
import org.joml.Vector3i;

public final class MiningGeometry {
    private MiningGeometry() {
    }

    public static int depthAxis(Vector3d direction) {
        double x = Math.abs(direction.x());
        double y = Math.abs(direction.y());
        double z = Math.abs(direction.z());
        if (x >= y && x >= z) {
            return 0;
        }
        if (z >= y) {
            return 2;
        }
        return 1;
    }

    /**
     * Resolves the face through which the player's view ray enters the target block.
     * This stays stable at steep angles where the largest look-direction component
     * is not necessarily the face the player actually selected.
     */
    public static int depthAxis(Vector3i target, Vector3d rayOrigin, Vector3d rayDirection) {
        double[] origin = {rayOrigin.x(), rayOrigin.y(), rayOrigin.z()};
        double[] direction = {rayDirection.x(), rayDirection.y(), rayDirection.z()};
        double[] minimum = {target.x(), target.y(), target.z()};
        double[] maximum = {target.x() + 1.0, target.y() + 1.0, target.z() + 1.0};
        double entry = Double.NEGATIVE_INFINITY;
        double exit = Double.POSITIVE_INFINITY;
        int entryAxis = -1;

        for (int axis = 0; axis < 3; axis++) {
            double component = direction[axis];
            if (Math.abs(component) < 1.0e-9) {
                if (origin[axis] < minimum[axis] || origin[axis] > maximum[axis]) {
                    return depthAxis(rayDirection);
                }
                continue;
            }
            double first = (minimum[axis] - origin[axis]) / component;
            double second = (maximum[axis] - origin[axis]) / component;
            double near = Math.min(first, second);
            double far = Math.max(first, second);
            if (near > entry) {
                entry = near;
                entryAxis = axis;
            }
            exit = Math.min(exit, far);
        }
        if (entryAxis < 0 || exit + 1.0e-7 < entry || exit < 0.0) {
            return depthAxis(rayDirection);
        }
        return entryAxis;
    }

    public static List<Vector3i> area(Vector3i center, int depthAxis, boolean includeCenter) {
        List<Vector3i> result = new ArrayList<>(includeCenter ? 9 : 8);
        for (int first = -1; first <= 1; first++) {
            for (int second = -1; second <= 1; second++) {
                if (!includeCenter && first == 0 && second == 0) {
                    continue;
                }
                int x = center.x();
                int y = center.y();
                int z = center.z();
                switch (depthAxis) {
                    case 0 -> {
                        y += first;
                        z += second;
                    }
                    case 1 -> {
                        x += first;
                        z += second;
                    }
                    default -> {
                        x += first;
                        y += second;
                    }
                }
                result.add(new Vector3i(x, y, z));
            }
        }
        return result;
    }
}
