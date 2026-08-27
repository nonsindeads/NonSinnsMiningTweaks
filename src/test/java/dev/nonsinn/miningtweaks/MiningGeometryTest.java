package dev.nonsinn.miningtweaks;

import java.util.HashSet;
import java.util.List;
import org.joml.Vector3d;
import org.joml.Vector3i;

public final class MiningGeometryTest {
    public static void main(String[] args) {
        require(MiningGeometry.depthAxis(new Vector3d(1, 0.2, 0.1)) == 0, "X look must produce a YZ plane");
        require(MiningGeometry.depthAxis(new Vector3d(0.1, -1, 0.2)) == 1, "Y look must produce an XZ plane");
        require(MiningGeometry.depthAxis(new Vector3d(0.1, 0.2, -1)) == 2, "Z look must produce an XY plane");

        Vector3i unitTarget = new Vector3i(0, 0, 0);
        require(MiningGeometry.depthAxis(unitTarget, new Vector3d(-3, 0.7, 0.4), new Vector3d(1, -0.05, 0.02)) == 0,
            "Ray entering the west face must use the X depth axis");
        require(MiningGeometry.depthAxis(unitTarget, new Vector3d(0.4, 3, 0.7), new Vector3d(0.02, -1, -0.05)) == 1,
            "Ray entering the top face must use the Y depth axis");
        require(MiningGeometry.depthAxis(unitTarget, new Vector3d(0.7, 0.4, 3), new Vector3d(-0.05, 0.02, -1)) == 2,
            "Ray entering the south face must use the Z depth axis");

        Vector3i center = new Vector3i(10, 20, 30);
        for (int axis = 0; axis < 3; axis++) {
            List<Vector3i> nine = MiningGeometry.area(center, axis, true);
            List<Vector3i> eight = MiningGeometry.area(center, axis, false);
            require(nine.size() == 9, "Preview area must contain nine positions");
            require(eight.size() == 8, "Break area must contain eight extra positions");
            require(new HashSet<>(nine).size() == 9, "Area positions must be unique");
            require(nine.contains(center), "Preview must include the target block");
            require(!eight.contains(center), "Extra break list must exclude the target block");
            for (Vector3i point : nine) {
                if (axis == 0) require(point.x() == center.x(), "YZ plane changed X depth");
                if (axis == 1) require(point.y() == center.y(), "XZ plane changed Y depth");
                if (axis == 2) require(point.z() == center.z(), "XY plane changed Z depth");
            }
        }
        System.out.println("MiningGeometryTest passed");
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
