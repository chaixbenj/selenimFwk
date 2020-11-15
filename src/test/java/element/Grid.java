package element;

import driver.Driver;
import org.openqa.selenium.By;

import org.openqa.selenium.WebElement;
import util.TestProperties;


import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

public class Grid {
    Element gridContainer = new Element("gridContainer", By.xpath("to be defined in constructor"));
    Element rowDataHeaders = new Element("headers des lignes de données", By.xpath("//table/thead/tr/th")).setContainer(gridContainer);
    Element rowDataAll = new Element("liste des lignes de données", By.xpath("//tr[td]")).setContainer(gridContainer);
    Element rowDataByContent = new Element("ligne de données contenant VALUE_IN_ROW", By.xpath("//tr[td][contains(., \"VALUE_IN_ROW\")]")).setContainer(gridContainer);
    Element cellDataByRowNumByColNum = new Element("cellule COL_NUMBER de la ligne ROW_NUMBER", By.xpath("//tr[td][ROW_NUMBER]/td[COL_NUMBER]")).setContainer(gridContainer);
    Element cellDataOneColAll = new Element("liste des cellules de la colonne COL_NUMBER", By.xpath("(//tr[td])/td[COL_NUMBER]")).setContainer(gridContainer);
    Element cellActionByRowNum = new Element("action ATTR_ACTION sur la ligne ROW_NUMBER", By.xpath("//tr[td][ROW_NUMBER]//*[attribute::*[contains(., \"ATTR_ACTION\")]]")).setContainer(gridContainer);


    private static HashMap<String, LocalDateTime> dateStartSearch = new HashMap<String, LocalDateTime>();


    private static void startSearch(String from) {
        if (dateStartSearch.containsKey(from)) {
            dateStartSearch.remove(from);
        }
        dateStartSearch.put(from, LocalDateTime.now());
    }
    private static boolean stopSearch(int timeout, String from) {
        if (dateStartSearch.get(from).plusSeconds(timeout).isAfter(LocalDateTime.now())) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Constructor
     * @param gridName name for the report
     * @param locator locator of the element containing the table
     */
    public Grid(String gridName, By locator) {
        gridContainer.setName(gridName);
        gridContainer.setLocator(locator);
    }

    /**
     * replace some strings in gridContainer locator by other values. 
     * @param params
     * @return
     */
    public Grid setParameter(String[] params) {
        gridContainer.setParameter(params);
        return this;
    }

    /**
     * replace one string in the gridContainer locator by other values.
     * @param key
     * @param value
     * @return
     */
    public Grid setParameter(String key, String value) {
        gridContainer.setParameter(key, value);
        return this;
    }

    /**
     * return gridContainer element
     * @return
     */
    public Element getGridContainer() {
        return 	gridContainer;
    }

    /**
     * return col number corresponding to the header
     * @param headerName
     * @return
     */
    public int getColNumberByHeaderName(String headerName) {
        int correspondingColumn = 0;
        boolean foundCol = false;
        startSearch("getColNumberByHeaderName");
        while (!foundCol && !stopSearch(60, "getColNumberByHeaderName")) {
            try {
                List<WebElement> allHeaders = rowDataHeaders.findAllElements();
                correspondingColumn = 0;
                for (WebElement header : allHeaders) {
                    correspondingColumn += 1;
                    if (header.getAttribute("outerHTML").contains(headerName)) {
                        System.out.println("getColNumberByHeaderName => " + correspondingColumn);
                        foundCol = true;
                        break;
                    }
                }
            } catch (Exception e) {
            }
        }
        Driver.getReport().log("info", "getColNumberByHeaderName " + headerName + " => " + String.valueOf(correspondingColumn),null,null,null,null);
        return (foundCol?correspondingColumn:0);
    }

    /**
     * return row number containing a string
     * @param subStringInRow
     * @return
     */
    public int getRowNumberBySubstringInRow(String subStringInRow) {
        int correspondingRow = 0;
        boolean foundRow = false;
        startSearch("getRowNumberBySubstringInRow");
        while (!foundRow && !stopSearch(60, "getRowNumberBySubstringInRow")) {
            try {
                List<WebElement> allRows = rowDataAll.findAllElements();
                correspondingRow = 0;
                for (WebElement row : allRows) {
                    correspondingRow += 1;
                    if (row.getAttribute("outerHTML").contains(subStringInRow)) {
                        foundRow = true;
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Driver.getReport().log("info", "getRowNumberBySubstringInRow " + subStringInRow + " => " + String.valueOf(correspondingRow),null,null,null,null);
        return (foundRow?correspondingRow:0);
    }

    /**
     * return row number containing a string in a column
     * @param subStringInCell
     * @param headerName
     * @return
     */
    public int getRowNumberBySubstringInColumn(String subStringInCell, String headerName) {
        return getRowNumberBySubstringInColumn(subStringInCell, getColNumberByHeaderName(headerName));
    }

    /**
     * return row number containing a string in a column
     * @param subStringInCell
     * @param colNumber
     * @return
     */
    public int getRowNumberBySubstringInColumn(String subStringInCell, int colNumber) {
        int correspondingRow = 0;
        boolean foundRow = false;
        if (colNumber>0) {
            cellDataOneColAll.setParameter("COL_NUMBER", String.valueOf(colNumber));
            startSearch("getRowNumberBySubstringInColumn");
            while (!foundRow && !stopSearch(60, "getRowNumberBySubstringInColumn")) {
                try {
                    List<WebElement> allRows = cellDataOneColAll.findAllElements();
                    for (WebElement row : allRows) {
                        correspondingRow += 1;
                        if (row.getAttribute("outerHTML").contains(subStringInCell)) {
                            System.out.println("getRowNumberBySubstringInColumn row found index : " + correspondingRow);
                            foundRow = true;
                            break;
                        }
                    }
                } catch (Exception e) {
                }
            }
        }
        Driver.getReport().log("info", "getRowNumberBySubstringInColumn " + subStringInCell + " colonne " + String.valueOf(colNumber) + " => " + String.valueOf(correspondingRow),null,null,null,null);
        return (foundRow?correspondingRow:0);
    }

    /**
     * return row number containing a string and column number by the header
     * @param subStringInRow
     * @param headerName
     * @return
     */
    public int[] getCellRowAndColumn(String subStringInRow, String headerName) {
        Driver.getReport().log("info", "getCellRowAndColumn " + subStringInRow + " colonne " + headerName ,null,null,null,null);
        int correspondingColumn = getColNumberByHeaderName(headerName);
        int correspondingRow = getRowNumberBySubstringInRow(subStringInRow);
        return new int[] {correspondingRow, correspondingColumn};
    }

    /**
     * return row count in the table
     * @return 
     */
    public int getRowCount() {
        return rowDataAll.getElementsNumber(0);
    }

    /**
     * return if the table is displayed
     * @return 
     */
    public boolean exists() {
        return rowDataHeaders.exists(TestProperties.timeout);
    }

    /**
     * return if the table contains a string in one row (or more)
     * 
     * @param value
     * @param timeout
     * @return
     */
    public boolean contains(String value, int timeout) {
        rowDataByContent.setParameter("VALUE_IN_ROW", value);
        return rowDataByContent.exists(timeout);
    }

    /**
     * return if the table contains a string in one row (or more)
     * @param value
     * @return
     */
    public boolean contains(String value) {
        rowDataByContent.setParameter("VALUE_IN_ROW", value);
        return rowDataByContent.exists(TestProperties.timeout);
    }

    /**
     * return if a column contains a string
     * @param subStringInCell 
     * @param subStringHeaderName 
     * @return 
     */
    public boolean columnContains(String subStringInCell, String subStringHeaderName) {
        return columnContains(subStringInCell, getColNumberByHeaderName(subStringHeaderName));
    }

    /**
     * return if a column contains a string
     * @param subStringInCell
     * @param columnNumber
     * @return
     */
    public boolean columnContains(String subStringInCell, int columnNumber) {
        boolean foundRow = false;
        try {
            if (columnNumber>0) {
                cellDataOneColAll.setParameter("COL_NUMBER", String.valueOf(columnNumber));
                List<WebElement> allRows = cellDataOneColAll.findAllElements();
                for (WebElement row : allRows) {
                    Driver.JSExecutor().executeScript("arguments[0].scrollIntoView(true);", row);
                    if (row.getText().contains(subStringInCell)) {
                        foundRow = true;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return foundRow;
    }
    /**
     * return value in a column from a row containing a string
     * @param subStringInRow 
     * @param headerName 
     * @return 
     */
    public String getCellsValue(String subStringInRow, String headerName) {
        return getCellsValue(subStringInRow, getColNumberByHeaderName(headerName));
    }

    /**
     * return value in a column from a row containing a string
     * @param subStringInRow
     * @param columnNumber
     * @return
     */
    public String getCellsValue(String subStringInRow, int columnNumber) {
        String value;
        try {
            int rowNumber = getRowNumberBySubstringInRow(subStringInRow);
            if (columnNumber>0) {
                if (rowNumber>0) {
                    cellDataByRowNumByColNum.setParameter(new String[] {"ROW_NUMBER", String.valueOf(rowNumber) , "COL_NUMBER", String.valueOf(columnNumber)});
                    value = cellDataByRowNumByColNum.getValue();
                } else {
                    value = "row not found";
                }
            } else {
                value = "col not found";
            }
        } catch (Exception e) {
            value = e.getMessage();
        }
        Driver.getReport().log("info", "getCellsValue => " + String.valueOf(value),null,null,null,null);
        return value;
    }

    /**
     * return if the value in a column from a row containing a string is equals to a value
     * @param subStringInRow
     * @param headerName
     * @param cellValue
     * @return
     */
    public boolean cellsValueEquals(String subStringInRow, String headerName, String cellValue) {
        String value = this.getCellsValue(subStringInRow, headerName);
        if (value.equals(cellValue)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * return if the value in a column from a row containing a string is equals to a value
     * @param subStringInRow
     * @param colNumber
     * @param cellValue
     * @return
     */
    public boolean cellsValueEquals(String subStringInRow, int colNumber, String cellValue) {
        String value = this.getCellsValue(subStringInRow, colNumber);
        if (value.equals(cellValue)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * return if the value in a column from a row containing a string contains to a value
     * @param subStringInRow
     * @param headerName
     * @param cellValue
     * @return
     */
    public boolean cellsValueContains(String subStringInRow, String headerName, String cellValue) {
        String value = this.getCellsValue(subStringInRow, headerName);
        if (value.trim().contains(cellValue.trim())) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * return if the value in a column from a row containing a string contains to a value
     * @param subStringInRow
     * @param colNumber
     * @param cellValue
     * @return
     */
    public boolean cellsValueContains(String subStringInRow, int colNumber, String cellValue) {
        String value = this.getCellsValue(subStringInRow, colNumber);
        if (value.trim().contains(cellValue.trim())) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * find a row containing a value in a column and return the value of another column
     * @param subStringInCell
     * @param subStringHeaderName
     * @param cellToReadHeaderName
     * @return
     */
    public String getCellsValue(String subStringInCell, String subStringHeaderName, String cellToReadHeaderName) {
        return getCellsValue(subStringInCell, getColNumberByHeaderName(subStringHeaderName), getColNumberByHeaderName(cellToReadHeaderName));
    }

    /**
     * find a row containing a value in a column and return the value of another column
     * @param subStringInCell
     * @param colNumber
     * @param colNumberCellToRead
     * @return
     */
    public String getCellsValue(String subStringInCell, int colNumber, int colNumberCellToRead) {
        String value;
        try {
            int correspondingRow = getRowNumberBySubstringInColumn(subStringInCell, colNumber);

            if (colNumberCellToRead>0) {
                if (correspondingRow>0 ) {
                    cellDataByRowNumByColNum.setParameter(new String[] {"ROW_NUMBER", String.valueOf(correspondingRow) , "COL_NUMBER", String.valueOf(colNumberCellToRead)});
                    value = cellDataByRowNumByColNum.getValue();
                } else {
                    value = "row not found";
                }
            } else {
                value = "col not found";
            }
        } catch (Exception e) {
            value = e.getMessage();
        }
        Driver.getReport().log("info", "getCellsValue => " + String.valueOf(value),null,null,null,null);
        return value;
    }


    /**
     * find a row containing a value in a column and return true if the value of another column equals the value in arg
     * @param subStringInCell
     * @param subStringHeaderName
     * @param cellToReadHeaderName
     * @param cellValue
     * @return
     */
    public boolean cellsValueEquals(String subStringInCell, String subStringHeaderName, String cellToReadHeaderName, String cellValue) {
        String value = this.getCellsValue(subStringInCell, subStringHeaderName, cellToReadHeaderName);
        if (value.equals(cellValue)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * find a row containing a value in a column and return true if the value of another column equals the value in arg
     * @param subStringInCell
     * @param colNumberSubString
     * @param colNumberCellToRead
     * @param cellValue
     * @return
     */
    public boolean cellsValueEquals(String subStringInCell, int colNumberSubString, int colNumberCellToRead, String cellValue) {
        String value = this.getCellsValue(subStringInCell, colNumberSubString, colNumberCellToRead);
        if (value.equals(cellValue)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     *  find a row containing a value in a column and return the TD attibute value of another column
     * @param subStringInCell
     * @param subStringHeaderName
     * @param cellToReadHeaderName
     * @param attr
     * @return
     */
    public String getCellsAttribute(String subStringInCell, String subStringHeaderName, String cellToReadHeaderName, String attr) {
        return getCellsAttribute(subStringInCell, getColNumberByHeaderName(subStringHeaderName), getColNumberByHeaderName(cellToReadHeaderName), attr);
    }

    /**
     * find a row containing a value in a column and return the TD attibute value of another column
     * @param subStringInCell
     * @param colNumberSubString
     * @param colNumberCellToRead
     * @param attr
     * @return
     */
    public String getCellsAttribute(String subStringInCell, int colNumberSubString, int colNumberCellToRead, String attr) {
        String value;
        try {
            if (colNumberCellToRead>0) {
                if (colNumberSubString>0 ) {
                    cellDataByRowNumByColNum.setParameter(new String[] {"ROW_NUMBER", String.valueOf(colNumberSubString) , "COL_NUMBER", String.valueOf(colNumberCellToRead)});
                    value = cellDataByRowNumByColNum.getAttribute(attr);
                } else {
                    value = "row not found";
                }
            } else {
                value = "col not found";
            }
        } catch (Exception e) {
            value = e.getMessage();
        }
        Driver.getReport().log("info", "getCellsAttribute => " + String.valueOf(value),null,null,null,null);
        return value;
    }


    /////////////////// ACTIONS

    /**
     * find a row continaing a string and click in cell of column in argument until one page is loaded
     * @param subStringInRow
     * @param headerName
     * @param pageObjectToBeLoadedAfterClick
     * @param assertLoadedMethod
     */
    public void clickCell(String subStringInRow, String headerName, Class pageObjectToBeLoadedAfterClick, String assertLoadedMethod) {
        clickCell(getRowNumberBySubstringInRow(subStringInRow), getColNumberByHeaderName(headerName), pageObjectToBeLoadedAfterClick, assertLoadedMethod);
    }

    /**
     * find a row continaing a string and click in cell of column in argument until one page is loaded
     * @param subStringInRow
     * @param colNumber
     * @param pageObjectToBeLoadedAfterClick
     * @param assertLoadedMethod
     */
    public void clickCell(String subStringInRow, int colNumber, Class pageObjectToBeLoadedAfterClick, String assertLoadedMethod) {
        clickCell(getRowNumberBySubstringInRow(subStringInRow), colNumber, pageObjectToBeLoadedAfterClick, assertLoadedMethod);
    }

    /**
     * find a row continaing a string and click in cell of column in argument
     * @param subStringInRow
     * @param headerName
     */
    public void clickCell(String subStringInRow, String headerName) {
        clickCell(subStringInRow, headerName, null, null);
    }

    /**
     * find a row continaing a string and click in cell of column in argument
     * @param subStringInRow
     * @param colNumber
     */
    public void clickCell(String subStringInRow, int colNumber) {
        clickCell(subStringInRow, colNumber, null, null);
    }

    /**
     * click in cell of column in argument and row number in argument until one page is loaded
     * @param rowNumber
     * @param headerName
     * @param pageObjectToBeLoadedAfterClick
     * @param assertLoadedMethod
     */
    public void clickCell(int rowNumber, String headerName, Class pageObjectToBeLoadedAfterClick, String assertLoadedMethod) {
        clickCell(rowNumber, getColNumberByHeaderName(headerName), pageObjectToBeLoadedAfterClick, assertLoadedMethod);
    }

    /**
     * click in cell of column in argument and row number in argument until one page is loaded
     * @param rowNumber
     * @param colNumber
     * @param pageObjectToBeLoadedAfterClick
     * @param assertLoadedMethod
     */
    public void clickCell(int rowNumber, int colNumber, Class pageObjectToBeLoadedAfterClick, String assertLoadedMethod) {
        System.out.println("Table.clickCell ");
        String result = "error";
        String errorMessage ;
        try {
            if (colNumber>0) {
                cellDataByRowNumByColNum.setParameter(new String[] {"ROW_NUMBER", String.valueOf(rowNumber) , "COL_NUMBER", String.valueOf(colNumber)});
                if (pageObjectToBeLoadedAfterClick!=null) {
                    cellDataByRowNumByColNum.click(pageObjectToBeLoadedAfterClick, assertLoadedMethod);
                } else {
                    cellDataByRowNumByColNum.click();
                }
                errorMessage = null;
                result = "pass";
            } else {
                errorMessage = "col not found";
            }
        } catch (Exception e) {
            errorMessage = e.getMessage();
        }
        Driver.getReport().log(result, "clickCell " + colNumber + " " + rowNumber, cellDataByRowNumByColNum.getName(), null , null, errorMessage);
    }

    /**
     * click in cell of column in argument and row number in argument
     * @param rowNumber
     * @param headerName
     */
    public void clickCell(int rowNumber, String headerName) {
        clickCell(rowNumber, headerName, null, null);
    }

    /**
     * click on an element of row in argument that contains text or attribute "action" until one page is loaded
     * @param correspondingRow
     * @param action
     * @param pageObjectToBeLoadedAfterClick
     * @param assertLoadedMethod
     */
    public void actionOnRow(int correspondingRow, String action, Class pageObjectToBeLoadedAfterClick, String assertLoadedMethod) {
        System.out.println("Table.actionOnRow ");
        String result = "error";
        String errorMessage = "ligne " + correspondingRow + " non trouvée";
        try {
            if (correspondingRow>0) {
                cellActionByRowNum.setParameter(new String[] {"ROW_NUMBER", String.valueOf(correspondingRow), "ATTR_ACTION", action});
                if (pageObjectToBeLoadedAfterClick!=null) {
                    cellActionByRowNum.click(pageObjectToBeLoadedAfterClick, assertLoadedMethod);
                } else {
                    cellActionByRowNum.click();
                }
                result = "pass";
                errorMessage = null;
            }
        } catch (Exception e) {
            errorMessage = e.getMessage();
            System.out.println(errorMessage);
        }
        Driver.getReport().log(result, "actionOnRow " + action + " " + correspondingRow, cellActionByRowNum.getName(), null , null, errorMessage);
    }

    /**
     * click on an element of row in argument that contains text or attribute "action"
     * @param correspondingRow
     * @param action
     */
    public void actionOnRow(int correspondingRow, String action) {
        actionOnRow(correspondingRow, action, null, null);
    }

    /**
     * click on an element of a row containing a string in argument that contains text or attribute "action" until one page is loaded
     * @param subStringInRow
     * @param action
     * @param pageObjectToBeLoadedAfterClick
     * @param assertLoadedMethod
     */
    public void actionOnRow(String subStringInRow, String action, Class pageObjectToBeLoadedAfterClick, String assertLoadedMethod) {
        System.out.println("Table.actionOnRow ");
        String result = "error";
        String errorMessage = "ligne " + subStringInRow + " non trouvée";
        try {
            int correspondingRow = getRowNumberBySubstringInRow(subStringInRow);
            if (correspondingRow>0) {
                cellActionByRowNum.setParameter(new String[] {"ROW_NUMBER", String.valueOf(correspondingRow), "ATTR_ACTION", action});
                if (pageObjectToBeLoadedAfterClick!=null) {
                    cellActionByRowNum.click(pageObjectToBeLoadedAfterClick, assertLoadedMethod);
                } else {
                    cellActionByRowNum.click();
                }
                result = "pass";
                errorMessage = null;
            }
        } catch (Exception e) {
            errorMessage = e.getMessage();
            System.out.println(errorMessage);
        }
        Driver.getReport().log(result, "actionOnRow " + action + " " + subStringInRow, cellActionByRowNum.getName(), null , null, errorMessage);
    }

    /**
     * click on an element of a row containing a string in argument that contains text or attribute "action"
     * @param subStringInRow
     * @param action
     */
    public void actionOnRow(String subStringInRow, String action) {
        actionOnRow(subStringInRow, action, null, null);
    }

    /**
     * click on an element of a row containing a string in a column in argument that contains text or attribute "action" until one page is loaded
     * @param subStringInCell
     * @param colNumberSubString
     * @param action
     * @param pageObjectToBeLoadedAfterClick
     * @param assertLoadedMethod
     */
    public void actionOnRow(String subStringInCell, int colNumberSubString, String action, Class pageObjectToBeLoadedAfterClick, String assertLoadedMethod) {
        System.out.println("Table.actionOnRow ");
        String result = "error";
        String errorMessage = "ligne " + subStringInCell + " non trouvée";
        try {
            int correspondingRow = getRowNumberBySubstringInColumn(subStringInCell, colNumberSubString);
            if (correspondingRow>0) {
                cellActionByRowNum.setParameter(new String[] {"ROW_NUMBER", String.valueOf(correspondingRow), "ATTR_ACTION", action});
                if (pageObjectToBeLoadedAfterClick!=null) {
                    cellActionByRowNum.click(pageObjectToBeLoadedAfterClick, assertLoadedMethod);
                } else {
                    cellActionByRowNum.click();
                }
                result = "pass";
                errorMessage = null;
            }
        } catch (Exception e) {
            errorMessage = e.getMessage();
            System.out.println(errorMessage);
        }
        Driver.getReport().log(result, "actionOnRow " + action + " " + subStringInCell, cellActionByRowNum.getName(), null , null, errorMessage);
    }

    /**
     * click on an element of a row containing a string in argument that contains text or attribute "action" until one page is loaded
     * @param subStringInCell
     * @param subStringHeaderName
     * @param action
     * @param pageObjectToBeLoadedAfterClick
     * @param assertLoadedMethod
     */
    public void actionOnRow(String subStringInCell, String subStringHeaderName, String action, Class pageObjectToBeLoadedAfterClick, String assertLoadedMethod) {
        actionOnRow(subStringInCell, getColNumberByHeaderName(subStringHeaderName), action, pageObjectToBeLoadedAfterClick, assertLoadedMethod);
    }

    /**
     * click on an element of a row containing a string in argument that contains text or attribute "action"
     * @param subStringInCell
     * @param subStringHeaderName
     * @param action
     */
    public void actionOnRow(String subStringInCell, String subStringHeaderName, String action) {
        actionOnRow(subStringInCell, subStringHeaderName, action, null, null);
    }

    //////////////////////////////// ASSERTIONS

    /**
     * assert a row contains a string
     * @param subStringInRow
     */
    public void assertContains(String subStringInRow) {
        startSearch("assertContains");
        boolean contains = contains(subStringInRow);
        while (!contains && !stopSearch(30,"assertContains")) {
            contains = contains(subStringInRow);
        }
        Driver.getReport().log((contains?"pass":"fail"), "assertContains " + subStringInRow , null, null , null, null);
    }

    /**
     * assert none row contains a string
     * @param subStringInRow
     */
    public void assertNotContains(String subStringInRow) {
        startSearch("asserNotContains");
        boolean contains = contains(subStringInRow);
        while (contains && !stopSearch(30,"asserNotContains")) {
            contains = contains(subStringInRow);
        }
        Driver.getReport().log((!contains?"pass":"fail"), "asserNotContains " + subStringInRow , null, null , null, null);
    }

    /**
     * find a row that contains a string in one column and assert that another column contains an element
     * @param subStringInCell
     * @param colNumberSubString
     * @param colNumberCellToRead
     * @param element
     */
    public void assertCellsContainsElement(String subStringInCell, int colNumberSubString, int colNumberCellToRead, Element element) {
        assertCellsContainsElement( subStringInCell,  colNumberSubString,  colNumberCellToRead,  element, false);
    }

    /**
     * find a row that contains a string in one column and assert that another column contains an element
     * @param subStringInCell
     * @param subStringHeaderName
     * @param cellToReadHeaderName
     * @param element
     */
    public void assertCellsContainsElement(String subStringInCell, String subStringHeaderName, String cellToReadHeaderName, Element element) {
        assertCellsContainsElement( subStringInCell,  subStringHeaderName,  cellToReadHeaderName,  element, false);
    }

    /**
     * find a row that contains a string in one column and assert that another column contains an element
     * @param subStringInCell
     * @param subStringHeaderName
     * @param cellToReadHeaderName
     * @param element
     * @param justWarning
     */
    public void assertCellsContainsElement(String subStringInCell, String subStringHeaderName, String cellToReadHeaderName, Element element, boolean justWarning) {
        assertCellsContainsElement(subStringInCell, getColNumberByHeaderName(subStringHeaderName), getColNumberByHeaderName(cellToReadHeaderName), element, justWarning);
    }

    /**
     * find a row that contains a string in one column and assert that another column contains an element
     * @param subStringInCell
     * @param colNumberSubString
     * @param colNumberCellToRead
     * @param element
     * @param justWarning
     */
    public void assertCellsContainsElement(String subStringInCell, int colNumberSubString, int colNumberCellToRead, Element element, boolean justWarning) {
        System.out.println("Table.assertCellsContainsElement ");
        String status = (justWarning ? "warning" : "failnext");
        try {
            int row = getRowNumberBySubstringInColumn(subStringInCell, colNumberSubString);
            Element cell = cellDataByRowNumByColNum.setParameter(new String[] {"ROW_NUMBER", String.valueOf(row) , "COL_NUMBER", String.valueOf(colNumberCellToRead)});
            element.setContainer(cell);
            if (element.exists(TestProperties.timeout)) {
                status = "pass";
            }
        } catch (Exception e) {
        }
        Driver.getReport().log(status, "assertCellsContainsElement " + colNumberCellToRead + " " + subStringInCell, cellDataByRowNumByColNum.getName(), element.getName(), "", null);
    }

    /**
     * assert a row containing a string contains a value in another column
     * @param subStringInRow
     * @param colNumber
     * @param cellValue
     */
    public void assertCellsValueEquals(String subStringInRow, int colNumber, String cellValue) {
        assertCellsValueEquals(subStringInRow, colNumber, cellValue, false);
    }

    /**
     * assert a row containing a string contains a value in another column
     * @param subStringInRow
     * @param headerName
     * @param cellValue
     */
    public void assertCellsValueEquals(String subStringInRow, String headerName, String cellValue) {
        assertCellsValueEquals(subStringInRow, headerName, cellValue, false);
    }

    /**
     * assert a row containing a string contains a value in another column
     * @param subStringInRow
     * @param headerName
     * @param cellValue
     * @param justWarning
     */
    public void assertCellsValueEquals(String subStringInRow, String headerName, String cellValue, boolean justWarning) {
        assertCellsValueEquals(subStringInRow, getColNumberByHeaderName(headerName), cellValue, justWarning);
    }

    /**
     * assert a row containing a string contains a value in another column
     * @param subStringInRow
     * @param colNumber
     * @param cellValue
     * @param justWarning
     */
    public void assertCellsValueEquals(String subStringInRow, int colNumber, String cellValue, boolean justWarning) {
        System.out.println("Table.assertCellsValueEquals ");
        String status = "pass";
        startSearch("assertCellsValueEquals");
        String value = getCellsValue(subStringInRow, colNumber);
        while (!value.trim().equals(cellValue.trim()) && !stopSearch(30,"assertCellsValueEquals")) {
            value = getCellsValue(subStringInRow, colNumber);
        }
        if (!value.trim().equals(cellValue.trim())) {
            status = (justWarning?"warning":"failnext");
        }
        Driver.getReport().log(status, "assertCellsValueEquals " + colNumber + " " + subStringInRow, cellDataByRowNumByColNum.getName(), cellValue, value, null);
    }

    /**
     * assert a row containing a string in a column contains a value in another column
     * @param subStringInCell
     * @param colNumberSubString
     * @param colNumberCellToRead
     * @param cellValue
     */
    public void assertCellsValueEquals(String subStringInCell, int colNumberSubString, int colNumberCellToRead, String cellValue) {
        assertCellsValueEquals(subStringInCell, colNumberSubString, colNumberCellToRead, cellValue, false);
    }

    /**
     * assert a row containing a string in a column contains a value in another column
     * @param subStringInCell
     * @param subStringHeaderName
     * @param cellToReadHeaderName
     * @param cellValue
     */
    public void assertCellsValueEquals(String subStringInCell, String subStringHeaderName, String cellToReadHeaderName, String cellValue) {
        assertCellsValueEquals(subStringInCell, subStringHeaderName, cellToReadHeaderName, cellValue, false);
    }

    /**
     * assert a row containing a string in a column contains a value in another column
     * @param subStringInCell
     * @param subStringHeaderName
     * @param cellToReadHeaderName
     * @param cellValue
     * @param justWarning
     */
    public void assertCellsValueEquals(String subStringInCell, String subStringHeaderName, String cellToReadHeaderName, String cellValue, boolean justWarning) {
        assertCellsValueEquals(subStringInCell, getColNumberByHeaderName(subStringHeaderName), getColNumberByHeaderName(cellToReadHeaderName), cellValue, justWarning);
    }

    /**
     * assert a row containing a string in a column contains a value in another column
     * @param subStringInCell
     * @param colNumberSubString
     * @param colNumberCellToRead
     * @param cellValue
     * @param justWarning
     */
    public void assertCellsValueEquals(String subStringInCell, int colNumberSubString, int colNumberCellToRead, String cellValue, boolean justWarning) {
        System.out.println("Table.assertCellsValueEquals ");
        String status = "pass";
        startSearch("assertCellsValueEquals");
        String value = getCellsValue(subStringInCell, colNumberSubString, colNumberCellToRead);
        while (!value.trim().equals(cellValue.trim()) && !stopSearch(30,"assertCellsValueEquals")) {
            value = getCellsValue(subStringInCell, colNumberSubString, colNumberCellToRead);
        }
        if (!value.trim().equals(cellValue.trim())) {
            status = (justWarning?"warning":"failnext");
        }
        Driver.getReport().log(status, "assertCellsValueEquals " + colNumberCellToRead + " " + subStringInCell, cellDataByRowNumByColNum.getName(), cellValue, value, null);
    }

    /**
     * assert the cell in row and column number in argument contains a value
     * @param subStringInRow
     * @param colNumber
     * @param cellValue
     */
    public void assertCellsValueContains(String subStringInRow, int colNumber, String cellValue) {
        assertCellsValueContains( subStringInRow,  colNumber,  cellValue, false);
    }

    /**
     *  assert the cell in row and column number in argument contains a value
     * @param subStringInRow
     * @param headerName
     * @param cellValue
     */
    public void assertCellsValueContains(String subStringInRow, String headerName, String cellValue) {
        assertCellsValueContains( subStringInRow,  headerName,  cellValue, false);
    }

    /**
     * assert a row containing a string contains a value in another column
     * @param subStringInRow
     * @param headerName
     * @param cellValue
     * @param justWarning
     */
    public void assertCellsValueContains(String subStringInRow, String headerName, String cellValue, boolean justWarning) {
        assertCellsValueContains(subStringInRow, getColNumberByHeaderName(headerName), cellValue, justWarning);
    }

    /**
     * assert a row containing a string contains a value in another column
     * @param subStringInRow
     * @param colNumber
     * @param cellValue
     * @param justWarning
     */
    public void assertCellsValueContains(String subStringInRow, int colNumber, String cellValue, boolean justWarning) {
        System.out.println("Table.assertCellsValueContains ");
        String status = "pass";
        startSearch("assertCellsValueContains");
        String value = getCellsValue(subStringInRow, colNumber);
        while (!value.contains(cellValue) && !stopSearch(30,"assertCellsValueContains")) {
            value = getCellsValue(subStringInRow, colNumber);
        }
        if (!value.contains(cellValue)) {
            status = (justWarning?"warning":"failnext");
        }
        Driver.getReport().log(status, "assertCellsValueContains " + colNumber + " " + subStringInRow, cellDataByRowNumByColNum.getName(),  cellValue, value, null);
    }

    /**
     * assert a row containing a string in a column contains a value in another column
     * @param subStringInCell
     * @param subStringHeaderName
     * @param cellToReadHeaderName
     * @param cellValue
     */
    public void assertCellsValueContains(String subStringInCell, String subStringHeaderName, String cellToReadHeaderName, String cellValue) {
        assertCellsValueContains( subStringInCell,  subStringHeaderName,  cellToReadHeaderName,  cellValue, false);
    }

    /**
     * assert a row containing a string in a column contains a value in another column
     * @param subStringInCell
     * @param colNumberSubString
     * @param colNumberCellToRead
     * @param cellValue
     */
    public void assertCellsValueContains(String subStringInCell, int colNumberSubString, int colNumberCellToRead, String cellValue) {
        assertCellsValueContains( subStringInCell,  colNumberSubString,  colNumberCellToRead,  cellValue, false);
    }


    /**
     * assert a row containing a string in a column contains a value in another column
     * @param subStringInCell
     * @param subStringHeaderName
     * @param cellToReadHeaderName
     * @param cellValue
     * @param justWarning
     */
    public void assertCellsValueContains(String subStringInCell, String subStringHeaderName, String cellToReadHeaderName, String cellValue, boolean justWarning) {
        assertCellsValueContains(subStringInCell, getColNumberByHeaderName(subStringHeaderName), getColNumberByHeaderName(cellToReadHeaderName), cellValue, justWarning);
    }

    /**
     * assert a row containing a string in a column contains a value in another column
     * @param subStringInCell
     * @param colNumberSubString
     * @param colNumberCellToRead
     * @param cellValue
     * @param justWarning
     */
    public void assertCellsValueContains(String subStringInCell, int colNumberSubString, int colNumberCellToRead, String cellValue, boolean justWarning) {
        System.out.println("Table.assertCellsValueContains ");
        String status = "pass";
        startSearch("assertCellsValueContains");
        String value = getCellsValue(subStringInCell, colNumberSubString, colNumberCellToRead);
        while (!value.contains(cellValue) && !stopSearch(30,"assertCellsValueContains")) {
            value = getCellsValue(subStringInCell, colNumberSubString, colNumberCellToRead);
        }
        if (!value.contains(cellValue)) {
            status = (justWarning?"warning":"failnext");
        }
        Driver.getReport().log(status, "assertCellsValueContains " + colNumberCellToRead + " " + subStringInCell, cellDataByRowNumByColNum.getName(),  cellValue, value, null);
    }

    /**
     * assert a row containing a string does not contain a value in another column
     * @param subStringInRow
     * @param colNumber
     * @param cellValue
     */
    public void assertCellsValueNotContains(String subStringInRow, int colNumber, String cellValue) {
        assertCellsValueNotContains( subStringInRow,  colNumber,  cellValue, false);
    }

    /**
     * assert a row containing a string does not contain a value in another column
     * @param subStringInRow
     * @param headerName
     * @param cellValue
     */
    public void assertCellsValueNotContains(String subStringInRow, String headerName, String cellValue) {
        assertCellsValueNotContains( subStringInRow,  headerName,  cellValue, false);
    }

    /**
     * assert a row containing a string does not contain a value in another column
     * @param subStringInRow
     * @param headerName
     * @param cellValue
     * @param justWarning
     */
    public void assertCellsValueNotContains(String subStringInRow, String headerName, String cellValue, boolean justWarning) {
        assertCellsValueNotContains(subStringInRow, getColNumberByHeaderName(headerName), cellValue, justWarning);
    }

    /**
     * assert a row containing a string does not contain a value in another column
     * @param subStringInRow
     * @param colNumber
     * @param cellValue
     * @param justWarning
     */
    public void assertCellsValueNotContains(String subStringInRow, int colNumber, String cellValue, boolean justWarning) {
        System.out.println("Table.assertCellsValueNotContains ");
        String status = "pass";
        startSearch("assertCellsValueNotContains");
        String value = getCellsValue(subStringInRow, colNumber);
        while (value.contains(cellValue) && !stopSearch(30,"assertCellsValueNotContains")) {
            value = getCellsValue(subStringInRow, colNumber);
        }
        if (value.contains(cellValue)) {
            status = (justWarning?"warning":"failnext");
        }
        Driver.getReport().log(status, "assertCellsValueNotEquals " + colNumber+ " " + subStringInRow, cellDataByRowNumByColNum.getName(),  cellValue, value, null);
    }

    /**
     * assert a column contains a value
     * @param subStringInCell
     * @param subStringHeaderName
     */
    public void assertContains(String subStringInCell, String subStringHeaderName) {
        assertContains(subStringInCell, getColNumberByHeaderName(subStringHeaderName));
    }

    /**
     * assert a column contains a value
     * @param subStringInCell
     * @param colNumberSubString
     */
    public void assertContains(String subStringInCell, int colNumberSubString) {
        startSearch("assertContains");
        boolean contains = columnContains(subStringInCell, colNumberSubString);
        while (!contains && !stopSearch(30,"assertContains")) {
            contains = columnContains(subStringInCell, colNumberSubString);
        }
        Driver.getReport().log((contains?"pass":"fail"), "assertContains " + subStringInCell , String.valueOf(colNumberSubString), null , null, null);
    }

    /**
     * assert a column does not contain a value
     * @param subStringInCell
     * @param subStringHeaderName
     */
    public void assertNotContains(String subStringInCell, String subStringHeaderName) {
        assertNotContains(subStringInCell, getColNumberByHeaderName(subStringHeaderName));
    }

    /**
     * assert a column does not contain a value
     * @param subStringInCell
     * @param colNumberSubString
     */
    public void assertNotContains(String subStringInCell, int colNumberSubString) {
        startSearch("asserNotContains");
        boolean contains = columnContains(subStringInCell, colNumberSubString);
        while (contains && !stopSearch(30,"asserNotContains")) {
            contains = columnContains(subStringInCell, colNumberSubString);
        }
        Driver.getReport().log((!contains?"pass":"fail"), "asserNotContains " + subStringInCell , String.valueOf(colNumberSubString), null , null, null);
    }

    /**
     * assert the attribute of TD in the column in argument of a row containing a string in a column contains a value
     * @param subStringInCell
     * @param colNumberSubString
     * @param colNumberCellToRead
     * @param attr
     * @param attributeValue
     */
    public void assertCellsAttributeContains(String subStringInCell, int colNumberSubString, int colNumberCellToRead, String attr, String attributeValue) {
        assertCellsAttributeContains( subStringInCell,  colNumberSubString,  colNumberCellToRead,  attr,  attributeValue, false);
    }

    /**
     * assert the attribute of TD in the column in argument of a row containing a string in a column contains a value
     * @param subStringInCell
     * @param subStringHeaderName
     * @param cellToReadHeaderName
     * @param attr
     * @param attributeValue
     */
    public void assertCellsAttributeContains(String subStringInCell, String subStringHeaderName, String cellToReadHeaderName, String attr, String attributeValue) {
        assertCellsAttributeContains( subStringInCell,  subStringHeaderName,  cellToReadHeaderName,  attr,  attributeValue, false);
    }

    /**
     * assert the attribute of TD in the column in argument of a row containing a string in a column contains a value
     * @param subStringInCell
     * @param subStringHeaderName
     * @param cellToReadHeaderName
     * @param attr
     * @param attributeValue
     * @param justWarning
     */
    public void assertCellsAttributeContains(String subStringInCell, String subStringHeaderName, String cellToReadHeaderName, String attr, String attributeValue, boolean justWarning) {
        assertCellsAttributeContains(subStringInCell, getColNumberByHeaderName(subStringHeaderName), getColNumberByHeaderName(cellToReadHeaderName), attr, attributeValue, justWarning);
    }

    /**
     * assert the attribute of TD in the column in argument of a row containing a string in a column contains a value
     * @param subStringInCell
     * @param colNumberSubString
     * @param colNumberCellToRead
     * @param attr
     * @param attributeValue
     * @param justWarning
     */
    public void assertCellsAttributeContains(String subStringInCell, int colNumberSubString, int colNumberCellToRead, String attr, String attributeValue, boolean justWarning) {
        System.out.println("Table.assertCellsAttributeContains ");
        String status = "pass";
        startSearch("assertCellsAttributeContains");
        String value = getCellsAttribute(subStringInCell, colNumberSubString, colNumberCellToRead, attr);
        while (!value.contains(attributeValue) && !stopSearch(30,"assertCellsAttributeContains")) {
            value = getCellsAttribute(subStringInCell, colNumberSubString, colNumberCellToRead, attr);
        }
        if (!value.contains(attributeValue)) {
            status = (justWarning?"warning":"failnext");
        }
        Driver.getReport().log(status, "assertCellsAttributeContains " + colNumberCellToRead + " " + subStringInCell, cellDataByRowNumByColNum.getName(),  attributeValue, value, null);
    }

    /**
     * assert the attribute of TD in the column in argument of a row containing a string in a column does not contain a value
     * @param subStringInCell
     * @param colNumberSubString
     * @param colNumberCellToRead
     * @param attr
     * @param attributeValue
     */
    public void assertCellsAttributeNotContains(String subStringInCell, int colNumberSubString, int colNumberCellToRead, String attr, String attributeValue) {
        assertCellsAttributeNotContains( subStringInCell,  colNumberSubString,  colNumberCellToRead,  attr,  attributeValue, false);
    }

    /**
     * assert the attribute of TD in the column in argument of a row containing a string in a column does not contain a value
     * @param subStringInCell
     * @param subStringHeaderName
     * @param cellToReadHeaderName
     * @param attr
     * @param attributeValue
     */
    public void assertCellsAttributeNotContains(String subStringInCell, String subStringHeaderName, String cellToReadHeaderName, String attr, String attributeValue) {
        assertCellsAttributeNotContains( subStringInCell,  subStringHeaderName,  cellToReadHeaderName,  attr,  attributeValue, false);
    }

    /**
     * assert the attribute of TD in the column in argument of a row containing a string in a column does not contain a value
     * @param subStringInCell
     * @param subStringHeaderName
     * @param cellToReadHeaderName
     * @param attr
     * @param attributeValue
     * @param justWarning
     */
    public void assertCellsAttributeNotContains(String subStringInCell, String subStringHeaderName, String cellToReadHeaderName, String attr, String attributeValue, boolean justWarning) {
        assertCellsAttributeNotContains(subStringInCell, getColNumberByHeaderName(subStringHeaderName), getColNumberByHeaderName(cellToReadHeaderName), attr, attributeValue, justWarning);
    }

    /**
     * assert the attribute of TD in the column in argument of a row containing a string in a column does not contain a value
     * @param subStringInCell
     * @param colNumberSubString
     * @param colNumberCellToRead
     * @param attr
     * @param attributeValue
     * @param justWarning
     */
    public void assertCellsAttributeNotContains(String subStringInCell, int colNumberSubString, int colNumberCellToRead, String attr, String attributeValue, boolean justWarning) {
        System.out.println("Table.assertCellsAttributeNotContains ");
        String status = "pass";
        startSearch("assertCellsAttributeNotContains");
        String value = getCellsAttribute(subStringInCell,colNumberSubString, colNumberCellToRead, attr);
        while (value.contains(attributeValue) && !stopSearch(30,"assertCellsAttributeNotContains")) {
            value = getCellsAttribute(subStringInCell,colNumberSubString, colNumberCellToRead, attr);
        }
        if (value.contains(attributeValue)) {
            status = (justWarning?"warning":"failnext");
        }
        Driver.getReport().log(status, "assertCellsAttributeNotContains " + colNumberCellToRead+ " " + subStringInCell, cellDataByRowNumByColNum.getName(),  attributeValue, value, null);
    }

    /**
     * assert row count in the table
     */
    public void assertRowCount(int nbRow) {
        int i = 0;
        int rowCount = getRowCount();
        while (i<10 && nbRow!=rowCount) {rowCount = getRowCount();i++;}
        Driver.getReport().assertEquals("vérification nombre de ligne dans la table ", nbRow, rowCount);
    }
}