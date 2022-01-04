package challenges;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day19 {
    public static void main(String[] args) {
        Map<Integer, Set<BeaconPosition>> sensorsAndBeacons = readFile();

        NormalizedSensorsAndBeacons normalizedTools = normalizeBeaconsAndSensors(sensorsAndBeacons);
        // System.out.println(normalizedTools.getNormalizedBeacons().size());
        System.out.println(getMaxSensorDistance(normalizedTools.getNormalizedSensors()));
    }

    private static Map<Integer, Set<BeaconPosition>> readFile() {
        Map<Integer, Set<BeaconPosition>> sensorsAndBeacons = new HashMap<>();

        int beaconNumber = -1;
        String fileName = "resources/day19.txt";
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank())
                    continue;

                if (line.contains("scanner")) {
                    Matcher matcher = Pattern.compile("scanner ([0-9]+)").matcher(line);
                    matcher.find();
                    beaconNumber = Integer.parseInt(matcher.group(1));
                    sensorsAndBeacons.put(beaconNumber, new HashSet<>());
                    continue;
                }

                Matcher positionMatcher = Pattern.compile("([-0-9]+),([-0-9]+),([-0-9]+)").matcher(line);
                positionMatcher.find();
                BeaconPosition beaconForLine = new BeaconPosition(Integer.parseInt(positionMatcher.group(1)),
                    Integer.parseInt(positionMatcher.group(2)), Integer.parseInt(positionMatcher.group(3)));
                sensorsAndBeacons.get(beaconNumber).add(beaconForLine);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sensorsAndBeacons;
    }

    private static NormalizedSensorsAndBeacons normalizeBeaconsAndSensors(Map<Integer, Set<BeaconPosition>> sensorsAndBeacons) {
        Set<BeaconPosition> normalizedBeaconPositions = sensorsAndBeacons.get(0);
        Set<BeaconPosition> normalizedSensorPositions = new HashSet<>(Set.of(new BeaconPosition(0, 0, 0)));
        sensorsAndBeacons.remove(0);

        // try match more scanners to the normalized set
        while (sensorsAndBeacons.size() > 0) {
            OffsetAndNormalizedBeacons normalization = normalizeFirstOverlappingSensorBeacons(
                normalizedBeaconPositions, sensorsAndBeacons);
            normalizedBeaconPositions.addAll(normalization.getNormalizedBeacons());

            Offset sensorNormalization = normalization.getNormalizationOffset();
            normalizedSensorPositions.add(new BeaconPosition(sensorNormalization.getXOffset(),
                sensorNormalization.getYOffset(), sensorNormalization.getZOffset()));
        }

        return new NormalizedSensorsAndBeacons(normalizedBeaconPositions, normalizedSensorPositions);
    }

    private static OffsetAndNormalizedBeacons normalizeFirstOverlappingSensorBeacons(Set<BeaconPosition> normalizedBeacons,
        Map<Integer, Set<BeaconPosition>> sensorsAndBeacons
    ) {
        Map<BeaconPosition, Set<Double>> normalizedDistances = getDistancesBetweenBeacons(normalizedBeacons);

        for (int i : sensorsAndBeacons.keySet()) {
            Set<BeaconPosition> jBeacons = sensorsAndBeacons.get(i);
            Map<BeaconPosition, Set<Double>> jDistances = getDistancesBetweenBeacons(jBeacons);

            CorrespondingOverlap overlap = overlappingBeaconDistances(normalizedDistances, jDistances);
            if (overlap.getNormalizedSet().size() > 0) {
                sensorsAndBeacons.remove(i); // remove handled sensor's beacons
                return normalizeBToA(normalizedBeacons, jBeacons, overlap);
            }
        }

        return new OffsetAndNormalizedBeacons(null, normalizedBeacons);
    }

    private static Map<BeaconPosition, Set<Double>> getDistancesBetweenBeacons(Set<BeaconPosition> beacons) {
        Map<BeaconPosition, Set<Double>> distancesBetweenBeacons = new HashMap<>();

        for (BeaconPosition beacon : beacons)
            distancesBetweenBeacons.put(beacon, getDistancesBetweenBeacons(beacon, beacons));

        return distancesBetweenBeacons;
    }

    private static Set<Double> getDistancesBetweenBeacons(BeaconPosition beacon, Set<BeaconPosition> beacons) {
        Set<Double> distances = new HashSet<>();

        for (BeaconPosition otherBeacon : beacons) {
            if (beacon.equals(otherBeacon))
                continue;

            distances.add(getDistanceBetweenBeacons(beacon, otherBeacon));
        }

        return distances;
    }

    private static double getDistanceBetweenBeacons(BeaconPosition beacon, BeaconPosition otherBeacon) {
        return Math.sqrt(Math.pow(beacon.getX() - otherBeacon.getX(), 2)
            + Math.pow(beacon.getY() - otherBeacon.getY(), 2)
            + Math.pow(beacon.getZ() - otherBeacon.getZ(), 2)
        );
    }

    private static int getManhattanDistanceBetweenBeacons(BeaconPosition beacon, BeaconPosition otherBeacon) {
        return Math.abs(beacon.getX() - otherBeacon.getX())
            + Math.abs(beacon.getY() - otherBeacon.getY())
            + Math.abs(beacon.getZ() - otherBeacon.getZ());
    }

    private static CorrespondingOverlap overlappingBeaconDistances(Map<BeaconPosition, Set<Double>> aPositions,
        Map<BeaconPosition, Set<Double>> bPositions
    ) {
        int numOverlapping = 0;

        for (Set<Double> aDistance : aPositions.values()) {
            Set<Double> bSetThatOverlaps = bSetThatOverlaps(aDistance, bPositions);
            if (bSetThatOverlaps.size() > 0) {
                numOverlapping++;

                if (numOverlapping >= 12)
                    return new CorrespondingOverlap(aDistance, bSetThatOverlaps);
            }
        }

        return new CorrespondingOverlap(new HashSet<>(), new HashSet<>());
    }

    private static Set<Double> bSetThatOverlaps(Set<Double> aDistances, Map<BeaconPosition, Set<Double>> bPositions) {
        for (Set<Double> bDistance : bPositions.values()) {
            Set<Double> overlap = new HashSet<>(bDistance);

            overlap.retainAll(aDistances);

            if (overlap.size() >= 11)
                return bDistance;
        }

        return new HashSet<>();
    }

    private static OffsetAndNormalizedBeacons normalizeBToA(Set<BeaconPosition> aBeacons, Set<BeaconPosition> bBeacons, CorrespondingOverlap overlapSet) {
        // find aBeacon that has overlap equal to overlapSet's normalized set
        BeaconPosition aBeaconToMatchTo = aBeacons.stream()
            .filter(ab -> getDistancesBetweenBeacons(ab, aBeacons).equals(overlapSet.getNormalizedSet()))
            .toList().get(0);
        // find bBeacon that has overlap equal to overlapSet
        BeaconPosition bBeaconToMatch = bBeacons.stream()
            .filter(bb -> getDistancesBetweenBeacons(bb, bBeacons).equals(overlapSet.getOverlappingDenormalizedSet()))
            .toList().get(0);

        // rotate and shift bBeaconToMatch until it's in same position as aBeaconToMatchTo
        for (Rotation r : Rotation.allPossibleRotations()) {
            // transform bBeaconToMatch to be in same position as aBeaconToMatchTo
            BeaconPosition rotatedBBeacon = bBeaconToMatch.rotated(r);
            int xChange = aBeaconToMatchTo.getX() - rotatedBBeacon.getX();
            int yChange = aBeaconToMatchTo.getY() - rotatedBBeacon.getY();
            int zChange = aBeaconToMatchTo.getZ() - rotatedBBeacon.getZ();

            // transform all other bBeacons the same way, check if 12+ changed bBeacons at same positions as aBeacons
            Set<BeaconPosition> rotatedBBeaconPositions = Set.copyOf(bBeacons.stream()
                .map(b -> b.rotated(r))
                .map(b -> b.shifted(xChange, yChange, zChange)).toList());

            Set<BeaconPosition> overlap = new HashSet<>(aBeacons);
            overlap.retainAll(rotatedBBeaconPositions);

            if (overlap.size() >= 12)
                return new OffsetAndNormalizedBeacons(new Offset(r, xChange, yChange, zChange), rotatedBBeaconPositions);
        }

        return new OffsetAndNormalizedBeacons(null, new HashSet<>());
    }

    private static int getMaxSensorDistance(Set<BeaconPosition> sensorPositions) {
        int maxDistance = 0;

        for (BeaconPosition sensor : sensorPositions) {
            for (BeaconPosition otherSensor : sensorPositions) {
                if (sensor.equals(otherSensor))
                    continue;;

                int distanceBetweenSensors = getManhattanDistanceBetweenBeacons(sensor, otherSensor);
                if (distanceBetweenSensors > maxDistance)
                    maxDistance = distanceBetweenSensors;
            }
        }

        return maxDistance;
    }
}

class BeaconPosition {
    private int x;
    public int getX() { return x; }

    private int y;
    public int getY() { return y; }

    private int z;
    public int getZ() { return z; }

    public int hashCode() {
        return x * 7919 + y * 5897 + z * 4931;
    }

    public boolean equals(Object other) {
        return other instanceof BeaconPosition
            && x == ((BeaconPosition)other).getX()
            && y == ((BeaconPosition)other).getY()
            && z == ((BeaconPosition)other).getZ();
    }

    // assumes normal +x is forward, +y is to the right, +z is up
    // returns a new BeaconPosition with the current object rotated to be equal to the rotation of rotation
    public BeaconPosition rotated(Rotation rotation) {
        int newX = 0;
        int newY = 0;
        int newZ = 0;

        switch (rotation.getNewFacing()) {
            case "+x" -> {
                newX = x;
                switch (rotation.getUpDirection()) {
                    case "+y" -> {
                        newY = -z;
                        newZ = y;
                    }
                    case "-y" -> {
                        newY = z;
                        newZ = -y;
                    }
                    case "+z" -> {
                        newY = y;
                        newZ = z;
                    }
                    case "-z" -> {
                        newY = -y;
                        newZ = -z;
                    }
                }
            }
            case "-x" -> {
                newX = -x;
                switch (rotation.getUpDirection()) {
                    case "+y" -> {
                        newY = z;
                        newZ = y;
                    }
                    case "-y" -> {
                        newY = -z;
                        newZ = -y;
                    }
                    case "+z" -> {
                        newY = -y;
                        newZ = z;
                    }
                    case "-z" -> {
                        newY = y;
                        newZ = -z;
                    }
                }
            }
            case "+y" -> {
                newX = y;
                switch (rotation.getUpDirection()) {
                    case "+x" -> {
                        newY = z;
                        newZ = x;
                    }
                    case "-x" -> {
                        newY = -z;
                        newZ = -x;
                    }
                    case "+z" -> {
                        newY = -x;
                        newZ = z;
                    }
                    case "-z" -> {
                        newY = x;
                        newZ = -z;
                    }
                }
            }
            case "-y" -> {
                newX = -y;
                switch (rotation.getUpDirection()) {
                    case "+x" -> {
                        newY = -z;
                        newZ = x;
                    }
                    case "-x" -> {
                        newY = z;
                        newZ = -x;
                    }
                    case "+z" -> {
                        newY = x;
                        newZ = z;
                    }
                    case "-z" -> {
                        newY = -x;
                        newZ = -z;
                    }
                }
            }
            case "+z" -> {
                newX = z;
                switch (rotation.getUpDirection()) {
                    case "+x" -> {
                        newY = -y;
                        newZ = x;
                    }
                    case "-x" -> {
                        newY = y;
                        newZ = -x;
                    }
                    case "+y" -> {
                        newY = x;
                        newZ = y;
                    }
                    case "-y" -> {
                        newY = -x;
                        newZ = -y;
                    }
                }
            }
            case "-z" -> {
                newX = -z;
                switch (rotation.getUpDirection()) {
                    case "+x" -> {
                        newY = y;
                        newZ = x;
                    }
                    case "-x" -> {
                        newY = -y;
                        newZ = -x;
                    }
                    case "+y" -> {
                        newY = -x;
                        newZ = y;
                    }
                    case "-y" -> {
                        newY = x;
                        newZ = -y;
                    }
                }
            }
        }

        return new BeaconPosition(newX, newY, newZ);
    }

    public BeaconPosition shifted(int xChange, int yChange, int zChange) {
        return new BeaconPosition(x + xChange, y + yChange, z + zChange);
    }

    public BeaconPosition(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
}

class Rotation {
    private String newFacing;
    public String getNewFacing() { return newFacing; }

    private String upDirection;
    public String getUpDirection() { return upDirection; }

    public static List<Rotation> allPossibleRotations() {
        return List.of(
            new Rotation("+x", "+y"),
            new Rotation("+x", "-y"),
            new Rotation("+x", "+z"),
            new Rotation("+x", "-z"),
            new Rotation("-x", "+y"),
            new Rotation("-x", "-y"),
            new Rotation("-x", "+z"),
            new Rotation("-x", "-z"),
            new Rotation("+y", "+x"),
            new Rotation("+y", "-x"),
            new Rotation("+y", "+z"),
            new Rotation("+y", "-z"),
            new Rotation("-y", "+x"),
            new Rotation("-y", "-x"),
            new Rotation("-y", "+z"),
            new Rotation("-y", "-z"),
            new Rotation("+z", "+x"),
            new Rotation("+z", "-x"),
            new Rotation("+z", "+y"),
            new Rotation("+z", "-y"),
            new Rotation("-z", "+x"),
            new Rotation("-z", "-x"),
            new Rotation("-z", "+y"),
            new Rotation("-z", "-y")
        );
    }

    public Rotation(String newFacing, String upDirection) {
        this.newFacing = newFacing;
        this.upDirection = upDirection;
    }
}

class CorrespondingOverlap {
    private Set<Double> normalizedSet;
    Set<Double> getNormalizedSet() { return normalizedSet; }

    private Set<Double> overlappingDenormalizedSet;
    Set<Double> getOverlappingDenormalizedSet() { return overlappingDenormalizedSet; }

    public CorrespondingOverlap(Set<Double> normalizedSet, Set<Double> overlappingDenormalizedSet) {
        this.normalizedSet = normalizedSet;
        this.overlappingDenormalizedSet = overlappingDenormalizedSet;
    }
}

class Offset {
    private Rotation rotationOffset;
    private int xOffset;
    int getXOffset() { return xOffset; }
    private int yOffset;
    int getYOffset() { return yOffset; }
    private int zOffset;
    int getZOffset() { return zOffset; }

    public Offset(Rotation rOffset, int xOffset, int yOffset, int zOffset) {
        this.rotationOffset = rOffset;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.zOffset = zOffset;
    }
}

class OffsetAndNormalizedBeacons {
    private Offset normalizationOffset;
    Offset getNormalizationOffset() { return normalizationOffset; }

    private Set<BeaconPosition> normalizedBeacons;
    Set<BeaconPosition> getNormalizedBeacons() { return normalizedBeacons; }

    public OffsetAndNormalizedBeacons(Offset offset, Set<BeaconPosition> beacons) {
        this.normalizationOffset = offset;
        this.normalizedBeacons = beacons;
    }
}

class NormalizedSensorsAndBeacons {
    private Set<BeaconPosition> normalizedBeacons;
    Set<BeaconPosition> getNormalizedBeacons() { return normalizedBeacons; }

    private Set<BeaconPosition> normalizedSensors;
    Set<BeaconPosition> getNormalizedSensors() { return normalizedSensors; }

    public NormalizedSensorsAndBeacons(Set<BeaconPosition> beaconPositions, Set<BeaconPosition> sensorPositions) {
        this.normalizedBeacons = beaconPositions;
        this.normalizedSensors = sensorPositions;
    }
}