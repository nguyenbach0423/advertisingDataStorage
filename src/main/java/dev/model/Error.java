package dev.model;

public class Error {
    private long ErrorID;
    private String SheetName;
    private String HeaderName;
    private int RowNumber;
    private String ErrorMessage;

    public Error () {}

    public Error (String SheetName, String HeaderName, int RowNumber, String ErrorMessage) {
        super();
        this.SheetName = SheetName;
        this.HeaderName = HeaderName;
        this.RowNumber = RowNumber;
        this.ErrorMessage = ErrorMessage;
    }

    public long getErrorID() {
        return ErrorID;
    }

    public void setErrorID(long errorID) {
        ErrorID = errorID;
    }

    public String getSheetName() {
        return SheetName;
    }

    public void setSheetName(String sheetName) {
        SheetName = sheetName;
    }

    public String getHeaderName() {
        return HeaderName;
    }

    public void setHeaderName(String headerName) {
        HeaderName = headerName;
    }

    public int getRowNumber() {
        return RowNumber;
    }

    public void setRowNumber(int rowNumber) {
        RowNumber = rowNumber;
    }

    public String getErrorMessage() {
        return ErrorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        ErrorMessage = errorMessage;
    }
}
