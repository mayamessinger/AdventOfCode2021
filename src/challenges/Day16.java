package challenges;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class Day16 {
    public static void main(String[] args) {
        Packet packet = readFile();

        System.out.println(packet.getValue());
    }

    private static Packet readFile() {
        char[] chars = null;

        String fileName = "resources/day16.txt";
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.startsWith("--")) // put the many test cases into one file, by commenting
                    chars = line.toCharArray();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Queue<String> binaryQueue = new ArrayDeque<>();
        for (char c : chars) {
            String binaryRepresentation = Integer.toBinaryString(Integer.parseInt(String.valueOf(c), 16));
            String paddedBinary = String.format("%4s", binaryRepresentation);
            binaryQueue.add(paddedBinary.replace(" ", "0"));
        }

        return convertBinaryToPacket(binaryQueue);
    }

    private static Packet convertBinaryToPacket(Queue<String> fourBitBinaryStrings) {
        return convertBinaryToPackets(fourBitBinaryStrings, new ArrayDeque<>(), LimitType.none, -1).get(0);
    }

    private static List<Packet> convertBinaryToPackets(Queue<String> fourBitBinaryStrings,
        Queue<Character> packetBinary, LimitType limitType, int limit
    ) {
        List<Packet> packets = new ArrayList<>();

        int startingBitsSize = unusedBits(fourBitBinaryStrings.size(), packetBinary.size());
        int packetsFound = 0;
        while (enoughForFullPacket(fourBitBinaryStrings.size(), packetBinary.size())) {
            PacketHeader header = getHeader(fourBitBinaryStrings, packetBinary);
            List<Packet> packetAndSubPackets = getPacketAndPossibleSubpackets(fourBitBinaryStrings, packetBinary,
                header);
            packets.addAll(packetAndSubPackets);

            packetsFound++;

            if (limitType == LimitType.bitLength &&
                startingBitsSize - unusedBits(fourBitBinaryStrings.size(), packetBinary.size()) == limit)
                return packets;

            if (limitType == LimitType.packetCount && packetsFound == limit)
                return packets;
        }

        return packets;
    }

    // encountered an example with 5 0-bits trailing, apparently we're supposed to not process them
    // so just don't process anything unless an actual packet can be made from it (minimum 11 chars needed for that -
    // a literal value packet with 6 bits for header, 5 for at least 1 value group)
    private static boolean enoughForFullPacket(int fourBitBinarySize, int singleBitBinarySize) {
        return fourBitBinarySize * 4 + singleBitBinarySize >= 11;
    }

    private static PacketHeader getHeader(Queue<String> fourBitBinaryStrings, Queue<Character> packetBinary) {
        String versionBits = getPacketSection(fourBitBinaryStrings, packetBinary, 3);
        int version = Integer.parseInt(versionBits, 2);

        String typeBits = getPacketSection(fourBitBinaryStrings, packetBinary, 3);
        int type = Integer.parseInt(typeBits, 2);

        return new PacketHeader(version, type);
    }

    private static String getPacketSection(Queue<String> fourBitBinaryStrings, Queue<Character> binaryToParse,
        int sectionSize
    ) {
        seedCharQueue(fourBitBinaryStrings, binaryToParse, sectionSize);
        return getBitSection(binaryToParse, sectionSize);
    }

    // removes the first sectionSize elements from binaryChars and puts them into a String that is returned
    private static String getBitSection(Queue<Character> binaryChars, int sectionSize) {
        StringBuilder bits = new StringBuilder();
        for (int i = 0; i < sectionSize; i++)
            bits.append(binaryChars.remove());

        return bits.toString();
    }

    // removes enough elements from fourBitBinaryStrings and adds then to binaryToParse, such that binaryToParse ends with
    // enough contents to be pulled from numBitsNeeded times
    private static void seedCharQueue(Queue<String> fourBitBinaryStrings, Queue<Character> binaryToParse,
        int numBitsNeeded
    ) {
        while (binaryToParse.size() < numBitsNeeded) {
            char[] toAdd = fourBitBinaryStrings.remove().toCharArray();
            for (char c : toAdd)
                binaryToParse.add(c);
        }
    }

    private static int unusedBits(int fourBitBinarySize, int singleBitBinarySize) {
        return fourBitBinarySize * 4 + singleBitBinarySize;
    }

    private static List<Packet> getPacketAndPossibleSubpackets(Queue<String> fourBitBinaryStrings, Queue<Character> packetBinary,
        PacketHeader header
    ) {
        List<Packet> packetsForHeader = new ArrayList<>();

        if (header.getType() == 4) {
            seedCharQueue(fourBitBinaryStrings, packetBinary, 5);
            packetsForHeader.add(getLiteralPacket(fourBitBinaryStrings, packetBinary, header));
        }
        else {
            seedCharQueue(fourBitBinaryStrings, packetBinary, 1);
            char lengthTypeId = packetBinary.remove();

            if (lengthTypeId == '0') {
                int subPacketLengths = Integer.parseInt(getPacketSection(fourBitBinaryStrings, packetBinary, 15),
                    2);

                List<Packet> subpackets = convertBinaryToPackets(fourBitBinaryStrings, packetBinary,
                    LimitType.bitLength, subPacketLengths);
                var packet = new Packet(header, getPacketValue(header.getType(), subpackets), subpackets);
                packetsForHeader.add(packet); // add parent packet last, to easily access
            }
            if (lengthTypeId == '1') {
                int numSubPackets = Integer.parseInt(getPacketSection(fourBitBinaryStrings, packetBinary, 11), 2);
                List<Packet> subpackets = convertBinaryToPackets(fourBitBinaryStrings, packetBinary,
                    LimitType.packetCount, numSubPackets);
                var packet = new Packet(header, getPacketValue(header.getType(), subpackets), subpackets);
                packetsForHeader.add(packet); // add parent packet last, to easily access
            }
        }

        return packetsForHeader;
    }

    private static Packet getLiteralPacket(Queue<String> fourBitBinaryStrings, Queue<Character> binaryToParse,
        PacketHeader header
    ) {
        StringBuilder valueAsString = new StringBuilder();
        while (!binaryToParse.peek().equals('0')) {
            valueAsString.append(getPacketSection(fourBitBinaryStrings, binaryToParse, 5).substring(1));
            if (binaryToParse.isEmpty())
                seedCharQueue(fourBitBinaryStrings, binaryToParse, 5);
        }
        valueAsString.append(getPacketSection(fourBitBinaryStrings, binaryToParse, 5).substring(1));
        long value = Long.parseLong(valueAsString.toString(), 2);

        return new Packet(header, value, new ArrayList<>());
    }

    private static long getPacketValue(int parentType, List<Packet> subpackets) {
        switch (parentType) {
            case 0:
                return subpackets.stream().mapToLong(sp -> sp.getValue()).sum();
            case 1:
                return subpackets.stream().mapToLong(sp -> sp.getValue()).reduce(1, (a, b) -> a * b);
            case 2:
                return subpackets.stream().mapToLong(sp -> sp.getValue()).min().getAsLong();
            case 3:
                return subpackets.stream().mapToLong(sp -> sp.getValue()).max().getAsLong();
            case 5:
                return subpackets.get(0).getValue() > subpackets.get(1).getValue()
                        ? 1
                        : 0;
            case 6:
                return subpackets.get(0).getValue() < subpackets.get(1).getValue()
                        ? 1
                        : 0;
            case 7:
                return subpackets.get(0).getValue() == subpackets.get(1).getValue()
                        ? 1
                        : 0;
            default:
                return 0;
        }
    }
}

enum LimitType {
    none, bitLength, packetCount
}

class PacketHeader {
    private int version;

    private int type;
    public int getType() { return type; }

    public PacketHeader(int version, int type) {
        this.version = version;
        this.type = type;
    }
}

class Packet {
    private PacketHeader header;

    private long value;
    public long getValue() { return value; }

    private List<Packet> subpackets;

    public Packet(PacketHeader header, long value, List<Packet> subpackets) {
        this.header = header;
        this.value = value;
        this.subpackets = subpackets;
    }
}
