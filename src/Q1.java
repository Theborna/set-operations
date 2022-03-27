import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;

public class Q1 {
    public static Map<String, HashSet<Integer>> data = new HashMap<String, HashSet<Integer>>();

    private static void validate(String key) throws Exception {
        if (!data.containsKey(key))
            throw new IllegalArgumentException("undefined key");
    }

    private static void printSet(String key) {
        try {
            validate(key);
            int[] arr = data.get(key).stream().mapToInt(i -> i).toArray();
            Arrays.sort(arr);
            System.out.println(
                    key + " = {" + Arrays.toString(arr).replace("]", "").replace(" ", "").replace("[", "") + "}");
        } catch (Exception e) {
            System.out.println(key + " is not defined");
        }
    }

    public static void addToSet(String key, int input) {
        try {
            validate(key);
            if (!data.get(key).add(input))
                System.out.println(input + " is already in " + key);
            else
                System.out.println("added successfully");
        } catch (Exception e) {
            System.out.println(key + " is not defined");
        }
    }

    private static HashSet<Integer> basicOperations(HashSet<Integer> set1, HashSet<Integer> set2, char operation) {
        HashSet<Integer> result = new HashSet<>(set1);
        if (operation == '-')
            result.removeAll(set2);
        else if (operation == '*')
            result.retainAll(set2);
        else
            result.addAll(set2);
        return result;
    }

    private static HashSet<Integer> rightSide(String entry) {
        HashSet<Integer> result = new HashSet<>();
        if (entry.length() == 0)
            return result;
        if (entry.charAt(0) == '{') { // getting string to a set
            String[] entries = entry.substring(1, entry.indexOf('}', 0)).split(",");
            for (String s : entries)
                result.add(Integer.parseInt(s));
        } else if (entry.contains("(")) {
            int size = entry.length(), start = entry.indexOf('(', 0), control = 0, end;
            for (end = start; end < size; end++) { // finds the end of the first parenthesis
                if (entry.charAt(end) == '(')
                    control++;
                else if (entry.charAt(end) == ')')
                    control--;
                if (control == 0)
                    break;
            }
            String[] parts = { entry.substring(0, Math.max(start - 1, 0)), entry.substring(start + 1, end),
                    entry.substring(Math.min(end + 2, size - 1), size) };
            char[] operations = { entry.charAt(Math.max(start - 1, 0)), entry.charAt(Math.min(end + 1, size - 1)) };
            if (start < 0)
                return basicOperations(rightSide(parts[1]), rightSide(parts[2]), operations[1]);
            if (end + 2 > size - 1)
                return basicOperations(rightSide(parts[0]), rightSide(parts[1]), operations[0]);
            return basicOperations(basicOperations(rightSide(parts[0]), rightSide(parts[1]), operations[0]),
                    rightSide(parts[2]), operations[1]);
        } else {
            String[] containedSets = entry.split("\\W"), operators = entry.split("\\w");
            result = data.get(containedSets[0]);
            for (int i = 1; i < operators.length; i++)
                result = basicOperations(result, data.get(containedSets[i]), operators[i].charAt(0));
        }
        return result;
    }

    private static void defineSet(String entry) {
        char[] containedSets = entry.substring(2).replaceAll("\\W|\\d", "").toCharArray();
        for (char c : containedSets) {
            if (!data.containsKey(String.valueOf(c))) {
                System.out.println("some sets are not defined");
                return;
            }
        }
        if (containedSets.length == 0) {
            if (data.containsKey(String.valueOf(entry.charAt(0))))
                System.out.println("set updated successfully");
            else
                System.out.println("set created successfully");
        }
        data.put(String.valueOf(entry.charAt(0)), rightSide(entry.substring(2)));
    }

    private static void subsets(String key) {
        try {
            validate(key);
            int[] arr = data.get(key).stream().mapToInt(i -> i).toArray();
            Arrays.sort(arr);
            int size = arr.length;
            for (int j = 0; j < (1 << size); j++) {
                System.out.print("{");
                boolean first = true;
                for (int j2 = 0; j2 < size; j2++)
                    if ((j & (1 << j2)) > 0) {
                        System.out.print(((first) ? "" : ",") + arr[j2]);
                        first = false;
                    }
                System.out.println("}");
            }
        } catch (Exception e) {
            System.out.println(key + " is not defined");
        }
    }

    public static void parse(String entry) {
        if (entry.contains("print"))
            printSet(String.valueOf(entry.charAt(entry.length() - 1)));
        if (entry.contains("add"))
            addToSet(String.valueOf(entry.charAt(entry.length() - 1)), Integer.parseInt(entry.replaceAll("\\D", "")));
        if (entry.contains("="))
            defineSet(entry);
        if (entry.contains("subsets"))
            subsets(String.valueOf(entry.charAt(entry.length() - 1)));
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String entry;
        do {
            entry = sc.nextLine().replaceAll(" ", "");
            parse(entry);
        } while (!entry.equals("end"));
        sc.close();
    }
}
