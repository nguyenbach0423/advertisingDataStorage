package dev.helper;

import dev.model.Error;

import java.lang.reflect.Field;
import java.util.ArrayList;

public abstract class ObjectHelper {
    public ArrayList<Error> errors = new ArrayList<>();

    public ArrayList<Error> getErrors() {
        return errors;
    }

    protected boolean isErrorHeader(Field[] fields, ArrayList<String> columnNameList, String sheetName) {
        boolean errorExists = false;
        for (String columnName : columnNameList)
        {
            int columnNameExists = 0;
            String columnNameDuplicate = columnName.replaceAll("\\s", "");
            columnNameDuplicate = columnNameDuplicate.toLowerCase();
            for (Field field : fields)
            {
                String fieldName = field.getName().toLowerCase();
                if (columnNameDuplicate.equals(fieldName)) {
                    columnNameExists = 1;
                    break;
                }
            }
            if (columnNameExists == 1)
            {
                continue;
            }
            else {
                errorExists = true;
                Error error = new Error();
                error.setSheetName(sheetName);
                error.setHeaderName(columnName);
                error.setErrorMessage("Header Name invalid.");
                errors.add(error);
            }
        }
        return errorExists;
    }

    protected int[] getIndex(Field[] fields, ArrayList<String> columnNameList) {
        int cells = columnNameList.size();
        int[] indexes = new int[cells];

        for (int i = 0; i < cells; i++)
        {
            String columnName = columnNameList.get(i);
            columnName = columnName.replaceAll("\\s", "");
            for (int j = 0; j < fields.length; j++)
            {
                if (columnName.toLowerCase().equals(fields[j].getName().toLowerCase()))
                {
                    indexes[i] = j;
                    break;
                }
            }
        }
        return indexes;
    }
}
