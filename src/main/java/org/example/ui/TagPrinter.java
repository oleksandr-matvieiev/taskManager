package org.example.ui;

import org.example.model.Tag;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TagPrinter {
    public static Map<Integer, Integer> printTags(List<Tag> tags) {
        Map<Integer, Integer> rowToId = new HashMap<>();

        String separator = "+----+--------------------------+\n";
        String header = String.format("| %-2s | %-24s |\n",
                "№", "Tag Name");

        System.out.print(separator);
        System.out.print(header);
        System.out.print(separator);

        int rowNumber = 1;
        for (Tag tag : tags) {
            String row = String.format("| %-2d | %-24s |\n", rowNumber, tag.getName());
            System.out.print(row);
            rowToId.put(rowNumber, tag.getId());
            rowNumber++;
        }
        System.out.print(separator);

        return rowToId;
    }
}
