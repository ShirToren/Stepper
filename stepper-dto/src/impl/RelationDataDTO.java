package impl;

import api.DTO;
import java.util.*;

public class RelationDataDTO implements DTO {
    private List<String> columns;
    private List<SingleRowDTO> rows;
    public RelationDataDTO(String ... columns) {
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

    public static class SingleRowDTO {
        private Map<String, String> data;

        public SingleRowDTO() {
            data = new HashMap<>();
        }

        public String getDataByColumnName(String column) { return data.get(column); }
    }
}
