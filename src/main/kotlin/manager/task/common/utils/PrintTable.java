//package manager.task.common.utils;
//
//
//import java.util.*;
//import java.util.function.Function;
//import java.util.function.Predicate;
//import java.util.stream.Collectors;
//import java.util.stream.Stream;
//import java.util.stream.StreamSupport;
//
///**
// * @version 3.0
// */
//public class PrintTable<T> {
//    private static final String LS = System.lineSeparator();
//    private static final String SL = "*SL*";
//    private static final String AUTO_FIT = "%AUTO_FIT%";
//
//
//    private String name = "PrintTable<Object>";
//    private Iterable<T> content;
//    private int size;
//    private final List<Column> columns = new ArrayList<>();
//
//
//    public static <T> PrintTable<T> of(Iterable<T> content) {
//        var rsl = new PrintTable<T>();
//        rsl.content = content;
//        rsl.size = (int) StreamSupport.stream(content.spliterator(), false).count();
//        return rsl;
//    }
//
//    public static <T> PrintTable<T> of(List<T> content) {
//        var rsl = new PrintTable<T>();
//        rsl.content = content;
//        rsl.size = content.size();
//        return rsl;
//    }
//
//    public static <T> PrintTable<T> of(Stream<T> content) {
//        var rsl = new PrintTable<T>();
//        rsl.content = content.collect(Collectors.toList());
//        rsl.size = ((List<T>) rsl.content).size();
//        return rsl;
//    }
//
//    public PrintTable<T> name(String tableName) {
//        this.name = tableName;
//        return this;
//    }
//
//    /** @see PrintTable#addColumn(String name, Function getValue, List cellColours) */
//    public PrintTable<T> addColumn(String name, Function<T, Object> getValue) {
//        columns.add(new Column(name, AUTO_FIT, getValue, size, new ArrayList<>()));
//        return this;
//    }
//    /** @see PrintTable#addColumn(String name, Function getValue, List cellColours) */
//    public PrintTable<T> addColumn(String name, Function<T, Object> getValue, String pattern) {
//        columns.add(new Column(name, pattern, getValue, size, new ArrayList<>()));
//        return this;
//    }
//    /** @see PrintTable#addColumn(String name, Function getValue, List cellColours) */
//    public PrintTable<T> addColumn(String name, Function<T, Object> getValue, String pattern, List<CellColour<T>> cellColours) {
//        columns.add(new Column(name, pattern, getValue, size, cellColours));
//        return this;
//    }
//    /** @see PrintTable#addColumn(String name, Function getValue, List cellColours) */
//    public PrintTable<T> addColumn(String name, Function<T, Object> getValue, CellColour<T> cellColour) {
//        columns.add(new Column(name, AUTO_FIT, getValue, size, Collections.singletonList(cellColour)));
//        return this;
//    }
//
//    /**
//     * @param name - имя колонки.
//     * @param getValue - ссылка на Getter для каждого объекта,
//     *                 что бы получить нужное значения для каждой ячейки в колонке.
//     * @param cellColours - условия для покраски значения в ячейки
//     *                    (применяется каждый {@link CellColour} к каждой ячейке).
//     */
//    public PrintTable<T> addColumn(String name, Function<T, Object> getValue, List<CellColour<T>> cellColours) {
//        columns.add(new Column(name, AUTO_FIT, getValue, size, cellColours));
//        return this;
//    }
//
//    private String buildHeader() {
//        var rsl = new StringBuilder();
//
//        for (var column : columns) {
//            rsl.append("| ");
//
//            var alignSpaceNum = (column.pattern.equals(AUTO_FIT))
//                    ? column.maxCellWidth - column.name.length()
//                    : Integer.parseInt(column.pattern) - column.name.length();
//
//            if (alignSpaceNum > 0) rsl.append(repeatChar(' ', alignSpaceNum));
//            rsl.append(column.name).append(' ');
//        }
//        rsl.append('|');
//        return rsl.toString();
//    }
//
//    private String buildLine(int lineIndex) {
//        var rsl = new StringBuilder();
//
//        for (var column : columns) {
//            var cellContent = column.content[lineIndex];
//            rsl.append("| ");
//
//            var cellContentLength = (column.isCellColorized[lineIndex])
//                    ? cellContent.length() - 9
//                    : cellContent.length();
//            var alignSpaceNum = (column.pattern.equals(AUTO_FIT))
//                    ? column.maxCellWidth - cellContentLength
//                    : Integer.parseInt(column.pattern) - cellContentLength;
//
//            if (alignSpaceNum > 0) rsl.append(repeatChar(' ', alignSpaceNum));
//            rsl.append(cellContent).append(' ');
//        }
//        rsl.append('|');
//        return rsl.toString();
//    }
//
//
//    @Override
//    public String toString() {
//        var rsl = new StringBuilder();
//
//        columns.forEach(it -> it.buildAllCellsContent(content));
//
//        var header = buildHeader();
//        rsl.append(SL).append(LS);
//        rsl.append(header).append(LS);
//        rsl.append(SL).append(LS);
//
//        for (int i = 0; i < size; i++) {
//            rsl.append(buildLine(i)).append(LS);
//        }
//        rsl.append(SL).append(LS);
//
//        var splitLine = "+" + repeatChar('-', header.length() -2) + "+";
//        return rsl.toString().replace(SL, splitLine);
//    }
//
//    public void print() {
//        var size = (content instanceof Collection) ? "" + ((Collection<T>) content).size() : "";
//        var sizeLabel = (size.isEmpty()) ? "" : " (table size: " + size + ')';
//        var rsl = new StringJoiner(System.lineSeparator())
//                .add(name + sizeLabel)
//                .add(this.toString());
//        System.out.println(rsl);
//    }
//
//    private String repeatChar(char c, int times) {
//        var rsl = new StringBuilder();
//        for (int i = 0; i < times ; i++) {
//            rsl.append(c);
//        }
//        return rsl.toString();
//    }
//
//
//    class Column {
//        /* all final fields - init in constructor */
//        final String name;
//        final String pattern;
//        final Function<T, Object> getValue;
//        final List<CellColour<T>> cellColours;
//        final String[] content;
//        final boolean[] isCellColorized;
//
//        int maxCellWidth = 0;
//
//
//        public Column(String name, String pattern, Function<T, Object> getValue, int contentSize,
//                      List<CellColour<T>> cellColours) {
//            this.name = name;
//            this.pattern = pattern;
//            this.getValue = getValue;
//            this.cellColours = cellColours;
//            this.content = new String[contentSize];
//            this.isCellColorized = new boolean[contentSize];
//        }
//
//        public void buildAllCellsContent(Iterable<T> tableContent) {
//            int i = 0;
//            for (var each : tableContent) {
//                var cellValue = "" + getValue.apply(each);
//                var cellValueWidth = cellValue.length();
//
//                for (var cc : cellColours) {
//                    if (cc.check.test(each)) {
//                        cellValue = Colour.colour(cellValue, cc.colour);
//                        isCellColorized[i] = true;
//                    }
//                }
//                content[i] = cellValue;
//                if (cellValueWidth > maxCellWidth) maxCellWidth = cellValueWidth;
//                i++;
//            }
//        }
//    }
//
//    public static class CellColour<T> {
//        private final Colour colour;
//        private final Predicate<T> check;
//
//        public CellColour(Colour colour, Predicate<T> check) {
//            this.colour = colour;
//            this.check = check;
//        }
//    }
//
//    /**
//     * смещение текста за 1 colour(...) -> 9 chars
//     * все цвета дают одинаковое смещение.
//     */
//    public enum Colour {
//        BLACK(TEXT_BLACK),
//        RED(TEXT_RED),
//        GREEN(TEXT_GREEN),
//        YELLOW(TEXT_YELLOW),
//        BLUE(TEXT_BLUE),
//        PURPLE(TEXT_PURPLE),
//        CYAN(TEXT_CYAN),
//        WHITE(TEXT_WHITE),
//        DEFAULT(TEXT_RESET);
//
//
//        private final String txtColour;
//
//        Colour(String txtColour) {
//            this.txtColour = txtColour;
//        }
//
//        public static String colour(Object txt, Colour colour) {
//            return colour.txtColour + txt + TEXT_RESET;
//        }
//    }
//
//
//    /* Colours for text */
//    public static final String TEXT_RESET = "\u001B[0m";
//    public static final String TEXT_BLACK = "\u001B[30m";
//    public static final String TEXT_RED = "\u001B[31m";
//    public static final String TEXT_GREEN = "\u001B[32m";
//    public static final String TEXT_YELLOW = "\u001B[33m";
//    public static final String TEXT_BLUE = "\u001B[34m";
//    public static final String TEXT_PURPLE = "\u001B[35m";
//    public static final String TEXT_CYAN = "\u001B[36m";
//    public static final String TEXT_WHITE = "\u001B[37m";
//
//    private static final String BACKGROUND_BLACK = "\u001B[40m";
//    private static final String BACKGROUND_RED = "\u001B[41m";
//    private static final String BACKGROUND_GREEN = "\u001B[42m";
//    private static final String BACKGROUND_YELLOW = "\u001B[43m";
//    private static final String BACKGROUND_BLUE = "\u001B[44m";
//    private static final String BACKGROUND_MAGENTA = "\u001B[45m";
//    private static final String BACKGROUND_CYAN = "\u001B[46m";
//    private static final String BACKGROUND_WHITE = "\u001B[47m";
//}
//
//
