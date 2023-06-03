package dd.impl.relation;

import java.util.*;

public class RelationData {
    private List<String> columns;
    private List<SingleRow> rows;
    public RelationData(String ... columns) {
        this.columns = Arrays.asList(columns);
        rows = new ArrayList<>();
    }

    public List<String> getRowDataByColumnsOrder(int rowId) {
        List<String> result = new ArrayList<>();
        for (String col : columns) {
            result.add(rows.get(rowId).getDataByColumnName(col));
        }
        return result;
    }

    public List<String> getColumns() {
        return columns;
    }

    public int getNumOfRows() {
        return rows.size();
    }

    public void addRowByColumnsOrder(String ... values) {
        List<String> valuesList = Arrays.asList(values);
        SingleRow newRow = new SingleRow();
        for(int i = 0; i < valuesList.size(); i++){
            newRow.addData(columns.get(i) , valuesList.get(i));
        }
        rows.add(newRow);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("Columns names: ");
        result.append("\n");
        for (String col: columns) {
            result.append(col);
            result.append(", ");

        }
        result.delete(result.length() - 2, result.length());
        result.append("\n");
        result.append("Number of rows: ");
        result.append(rows.size());
        return result.toString();
    }

    private static class SingleRow {
        private Map<String, String> data;

        public SingleRow() {
            data = new HashMap<>();
        }

        public void addData(String columnName, String value) {
            data.put(columnName, value);
        }

        public String getDataByColumnName(String column) { return data.get(column); }

        @Override
        public String toString() {
            return "SingleRow{" +
                    "data=" + data +
                    '}';
        }
    }
}
