package project.spreadsheet.sheet;

import project.spreadsheet.content.Content;
import project.spreadsheet.content.ContentParser;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class Spreadsheet {

    private final Map<Address, Cell> cells = new HashMap<>();

    private Cell ensureCell(Address addr) {
        return cells.computeIfAbsent(addr, a -> new Cell());
    }

    public Optional<Cell> findCell(Address addr) {
        return Optional.ofNullable(cells.get(addr));
    }

    public Set<Address> getUsedAddresses() {
        return cells.keySet();
    }

    public void setRawContent(Address addr, String raw) {
        Content content = ContentParser.parse(raw);
        ensureCell(addr).setContent(content);
    }

    public String getRawContent(Address addr) {
        return findCell(addr)
                .map(Cell::getContent)
                .map(Content::raw)
                .orElse("");
    }
}